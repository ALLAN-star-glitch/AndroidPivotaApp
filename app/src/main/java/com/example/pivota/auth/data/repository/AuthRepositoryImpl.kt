package com.example.pivota.auth.data.repository

import android.util.Base64
import com.example.pivota.auth.data.mapper.AuthDataMapper
import com.example.pivota.auth.data.remote.api.AuthApiService
import com.example.pivota.auth.data.remote.dto.*
import com.example.pivota.auth.domain.model.User
import com.example.pivota.auth.domain.repository.AuthRepository
import com.example.pivota.core.database.dao.UserDao
import com.example.pivota.core.database.entity.UserEntity
import com.example.pivota.core.network.ApiResult
import com.example.pivota.core.network.safeApiCall
import com.example.pivota.core.preferences.PivotaDataStore
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import javax.inject.Inject
import com.example.pivota.auth.domain.model.LoginResponse
import com.example.pivota.core.network.NetworkError

class AuthRepositoryImpl @Inject constructor(
    private val apiService: AuthApiService,
    private val preferences: PivotaDataStore,
    private val userDao: UserDao,
    private val mapper: AuthDataMapper
) : AuthRepository {

    override suspend fun requestOtp(email: String, purpose: String, phone: String?): ApiResult<BaseOtpResponseDto> {
        val request = RequestOtpRequestDto(
            email = email,
            purpose = purpose,
            phone = phone
        )
        return safeApiCall {
            apiService.requestOtp(request)
        }
    }

    override suspend fun verifyOtp(email: String, code: String, purpose: String): ApiResult<VerifyOtpResponseDto> {
        val request = VerifyOtpRequestDto(
            email = email,
            code = code,
            purpose = purpose
        )
        return safeApiCall {
            apiService.verifyOtp(request)
        }
    }

    override suspend fun signupIndividual(
        user: User,
        code: String,
        password: String
    ): ApiResult<SignupResponseDto> {
        val request = mapper.toSignupRequestDto(user, code, password)

        // ========== LOG THE FULL REQUEST ==========
        println("🔍 ==================== SIGNUP REQUEST ====================")
        println("🔍 firstName: ${request.firstName}")
        println("🔍 lastName: ${request.lastName}")
        println("🔍 email: ${request.email}")
        println("🔍 phone: ${request.phone}")
        println("🔍 code: ${request.code}")
        println("🔍 planSlug: ${request.planSlug}")
        println("🔍 primaryPurpose: ${request.primaryPurpose}")
        println("🔍 jobSeekerData: ${request.jobSeekerData}")
        println("🔍 skilledProfessionalData: ${request.skilledProfessionalData}")
        println("🔍 intermediaryAgentData: ${request.intermediaryAgentData}")
        println("🔍 housingSeekerData: ${request.housingSeekerData}")
        println("🔍 supportBeneficiaryData: ${request.supportBeneficiaryData}")
        println("🔍 employerData: ${request.employerData}")
        println("🔍 propertyOwnerData: ${request.propertyOwnerData}")
        println("🔍 =========================================================")

        return safeApiCall {
            apiService.signup(request)
        }.let { apiResult ->
            when (apiResult) {
                is ApiResult.Success -> {
                    val response = apiResult.data
                    println("🔍 RESPONSE: success=${response.success}, message=${response.message}")

                    // Handle auto-login if tokens are present
                    if (response.success && response.data != null) {
                        val signupData = response.data

                        // Check if we have tokens (auto-login for free plan)
                        if (!signupData.accessToken.isNullOrEmpty() && !signupData.refreshToken.isNullOrEmpty()) {
                            println("🔍 Auto-login: Tokens received, saving user session")

                            // Extract user from token and save authenticated session
                            val authenticatedUser = extractUserFromToken(
                                email = request.email,
                                accessToken = signupData.accessToken,
                                refreshToken = signupData.refreshToken
                            )

                            // Also save additional fields from signup
                            val finalUser = authenticatedUser.copy(
                                firstName = request.firstName,
                                lastName = request.lastName,
                                personalPhone = request.phone,
                                primaryPurpose = request.primaryPurpose
                            )

                            saveAuthenticatedUser(finalUser)

                            println("🔍 User auto-logged in successfully: ${finalUser.email}")
                        }
                        // Check if payment is required (premium plan)
                        else if (!signupData.redirectUrl.isNullOrEmpty()) {
                            println("🔍 Payment required: Redirect to ${signupData.redirectUrl}")
                            // Don't save user yet - they need to complete payment first
                            // Save only basic info for later
                            val basicUser = user.copy(isAuthenticated = false)
                            saveBasicUserInfo(basicUser)
                        }
                        // Fallback - just success message (should go to login)
                        else {
                            println("🔍 Signup successful, no tokens. User should login manually")
                            val basicUser = user.copy(isAuthenticated = false)
                            saveBasicUserInfo(basicUser)
                        }
                    }
                    apiResult
                }
                is ApiResult.Error -> {
                    println("❌ Signup failed: ${apiResult.networkError.userFriendlyMessage}")
                    apiResult
                }
                ApiResult.Loading -> {
                    // Loading state - do nothing, just return
                    apiResult
                }
            }
        }
    }

    override suspend fun login(email: String, password: String): ApiResult<LoginResponseDto> {
        val request = LoginRequestDto(
            email = email,
            password = password
        )
        return safeApiCall {
            apiService.login(request)
        }
    }

    override suspend fun verifyMfaLogin(email: String, code: String): ApiResult<LoginResponseDto> {
        val request = VerifyMfaLoginRequestDto(
            email = email,
            code = code
        )
        return safeApiCall {
            apiService.verifyMfaLogin(request)
        }.let { apiResult ->
            when (apiResult) {
                is ApiResult.Success -> {
                    val response = apiResult.data
                    // Save authenticated user if login successful
                    if (response.success && response.data?.accessToken != null) {
                        val data = response.data
                        // Decode JWT token to extract user information
                        val user = extractUserFromToken(
                            email = email,
                            accessToken = data.accessToken!!,
                            refreshToken = data.refreshToken
                        )
                        saveAuthenticatedUser(user)
                    }
                    apiResult
                }
                is ApiResult.Error -> {
                    println("❌ MFA verification failed: ${apiResult.networkError.userFriendlyMessage}")
                    apiResult
                }
                ApiResult.Loading -> {
                    // Loading state - do nothing, just return
                    apiResult
                }
            }
        }
    }

    // ======================================================
    // GOOGLE SIGN-IN
    // ======================================================

    override suspend fun googleSignIn(
        idToken: String,
        onboardingData: Map<String, Any?>?
    ): ApiResult<LoginResponse> {
        val request = GoogleSignInRequestDto(
            token = idToken,
            onboardingData = onboardingData?.let { data ->
                GoogleOnboardingDataDto(
                    primaryPurpose = data["primaryPurpose"] as? String,
                    jobSeekerData = data["jobSeekerData"] as? JobSeekerProfileDataDto,
                    housingSeekerData = data["housingSeekerData"] as? HousingSeekerProfileDataDto,
                    skilledProfessionalData = data["skilledProfessionalData"] as? SkilledProfessionalProfileDataDto,
                    intermediaryAgentData = data["intermediaryAgentData"] as? IntermediaryAgentProfileDataDto,
                    supportBeneficiaryData = data["supportBeneficiaryData"] as? SupportBeneficiaryProfileDataDto,
                    employerData = data["employerData"] as? EmployerProfileDataDto,
                    propertyOwnerData = data["propertyOwnerData"] as? PropertyOwnerProfileDataDto
                )
            }
        )

        return safeApiCall {
            apiService.googleSignIn(request)
        }.let { apiResult ->
            when (apiResult) {
                is ApiResult.Success -> {
                    val response = apiResult.data
                    println("🔍 [Google Sign-In] Response: success=${response.success}, message=${response.message}")

                    try {
                        // Use the existing mapper to convert DTO to domain model
                        val loginResponse = mapper.toLoginResponse(response)

                        // Save authenticated user if login successful
                        if (loginResponse is LoginResponse.Authenticated) {
                            saveAuthenticatedUser(loginResponse.user)
                            println("🔍 [Google Sign-In] User authenticated: ${loginResponse.user.email}")
                        }

                        ApiResult.Success(loginResponse)
                    } catch (e: Exception) {
                        println("❌ [Google Sign-In] Parse error: ${e.message}")
                        ApiResult.Error(NetworkError.ParsingError)
                    }
                }
                is ApiResult.Error -> {
                    println("❌ [Google Sign-In] Failed: ${apiResult.networkError.userFriendlyMessage}")
                    ApiResult.Error(apiResult.networkError)
                }
                ApiResult.Loading -> {
                    ApiResult.Loading
                }
            }
        }
    }

    override suspend fun refreshToken(refreshToken: String): ApiResult<RefreshTokenResponseDto> {
        return safeApiCall {
            apiService.refreshToken(refreshToken)
        }.let { apiResult ->
            when (apiResult) {
                is ApiResult.Success -> {
                    val response = apiResult.data
                    // Update stored tokens if refresh successful
                    if (response.success && response.data != null) {
                        preferences.saveAccessToken(response.data.accessToken)
                        preferences.saveRefreshToken(response.data.refreshToken)
                    }
                    apiResult
                }
                is ApiResult.Error -> apiResult
                ApiResult.Loading -> apiResult
            }
        }
    }

    override suspend fun requestPasswordReset(email: String): ApiResult<BaseOtpResponseDto> {
        return safeApiCall {
            apiService.requestPasswordReset(email)
        }
    }

    override suspend fun resetPassword(email: String, code: String, newPassword: String): ApiResult<BaseResponseDto<Nothing>> {
        return safeApiCall {
            apiService.resetPassword(email, code, newPassword)
        }
    }

    override suspend fun logout(refreshToken: String): ApiResult<BaseResponseDto<Nothing>> {
        return safeApiCall {
            apiService.logout(refreshToken)
        }.let { apiResult ->
            when (apiResult) {
                is ApiResult.Success -> {
                    val response = apiResult.data
                    if (response.success) {
                        // Clear local user data
                        preferences.clearUserData()
                        userDao.deleteAll()
                    }
                    apiResult
                }
                is ApiResult.Error -> apiResult
                ApiResult.Loading -> apiResult
            }
        }
    }

    /**
     * Extract user information from JWT token
     * JWT format: header.payload.signature
     * Payload is Base64Url encoded JSON
     */
    private fun extractUserFromToken(
        email: String,
        accessToken: String,
        refreshToken: String?
    ): User {
        return try {
            // Split JWT token
            val parts = accessToken.split(".")
            if (parts.size != 3) {
                // Invalid JWT format, return basic user
                return User(
                    email = email,
                    isAuthenticated = true,
                    accessToken = accessToken,
                    refreshToken = refreshToken
                )
            }

            // Decode payload (second part)
            val payloadJson = decodeBase64Url(parts[1])

            // Parse JSON
            val jsonElement = Json.parseToJsonElement(payloadJson)
            val jsonObject = jsonElement.jsonObject

            // Extract fields from JWT payload (matching backend JwtPayload interface)
            val userUuid = jsonObject["userUuid"]?.jsonPrimitive?.contentOrNull ?: ""
            val userName = jsonObject["userName"]?.jsonPrimitive?.contentOrNull ?: ""
            val accountId = jsonObject["accountId"]?.jsonPrimitive?.contentOrNull ?: ""
            val accountName = jsonObject["accountName"]?.jsonPrimitive?.contentOrNull ?: ""
            val accountType = jsonObject["accountType"]?.jsonPrimitive?.contentOrNull
            val tokenId = jsonObject["tokenId"]?.jsonPrimitive?.contentOrNull ?: ""
            val role = jsonObject["role"]?.jsonPrimitive?.contentOrNull
            val organizationUuid = jsonObject["organizationUuid"]?.jsonPrimitive?.contentOrNull
            val planSlug = jsonObject["planSlug"]?.jsonPrimitive?.contentOrNull

            // Extract first name and last name from userName if available
            val firstName = jsonObject["firstName"]?.jsonPrimitive?.contentOrNull
                ?: userName.split(" ").firstOrNull()
                ?: ""
            val lastName = jsonObject["lastName"]?.jsonPrimitive?.contentOrNull
                ?: userName.split(" ").drop(1).joinToString(" ")

            println("🔍 [JWT Decoded]")
            println("   - userUuid: $userUuid")
            println("   - userName: $userName")
            println("   - accountId: $accountId")
            println("   - accountName: $accountName")
            println("   - accountType: $accountType")
            println("   - tokenId: $tokenId")
            println("   - role: $role")
            println("   - organizationUuid: $organizationUuid")
            println("   - planSlug: $planSlug")

            User(
                uuid = userUuid,
                email = email,
                firstName = firstName,
                lastName = lastName,
                userName = userName,
                accessToken = accessToken,
                refreshToken = refreshToken,
                isAuthenticated = true,
                // JWT payload fields
                userUuid = userUuid,
                accountId = accountId,
                accountName = accountName,
                accountType = accountType,
                tokenId = tokenId,
                role = role,
                organizationUuid = organizationUuid,
                planSlug = planSlug
            )
        } catch (e: Exception) {
            println("🔍 [JWT Decode Error] ${e.message}")
            e.printStackTrace()
            // Fallback to basic user
            User(
                email = email,
                isAuthenticated = true,
                accessToken = accessToken,
                refreshToken = refreshToken
            )
        }
    }

    /**
     * Decode Base64Url string to JSON string
     */
    private fun decodeBase64Url(base64Url: String): String {
        // Convert Base64Url to standard Base64
        var base64 = base64Url.replace('-', '+').replace('_', '/')
        // Add padding if needed
        when (base64.length % 4) {
            2 -> base64 += "=="
            3 -> base64 += "="
        }
        val decodedBytes = Base64.decode(base64, Base64.DEFAULT)
        return String(decodedBytes, Charsets.UTF_8)
    }

    override suspend fun saveAuthenticatedUser(user: User) {
        // Save tokens to DataStore (simple key-value, more secure)
        user.accessToken?.let { preferences.saveAccessToken(it) }
        user.refreshToken?.let { preferences.saveRefreshToken(it) }
        preferences.saveUserEmail(user.email)
        preferences.markOnboardingComplete(true)

        // Save user profile to Room Database (complex data, no tokens)
        val userEntity = UserEntity(
            uuid = user.uuid,
            email = user.email,
            firstName = user.firstName,
            lastName = user.lastName,
            userName = user.userName,
            phone = user.personalPhone,
            profileImage = user.profileImage,
            isAuthenticated = user.isAuthenticated,
            isOnboardingComplete = true,
            hasSeenWelcomeScreen = true,
            primaryPurpose = user.primaryPurpose,
            role = user.role,
            accountType = user.accountType,
            accountId = user.accountId,
            accountName = user.accountName,
            organizationUuid = user.organizationUuid,
            planSlug = user.planSlug,
            tokenId = user.tokenId,
            updatedAt = System.currentTimeMillis()
        )
        userDao.insertUser(userEntity)

        // Log user info for debugging
        println("🔍 [AuthRepositoryImpl] User saved successfully:")
        println("   - UUID: ${user.uuid}")
        println("   - Email: ${user.email}")
        println("   - Name: ${user.userName}")
        println("   - Role: ${user.role}")
        println("   - Account Type: ${user.accountType}")
    }

    private suspend fun saveBasicUserInfo(user: User) {
        // Save basic user info without tokens (for pre-fill after signup)
        preferences.saveUserEmail(user.email)

        // Optionally save to Room as non-authenticated user
        val userEntity = UserEntity(
            uuid = user.uuid,
            email = user.email,
            firstName = user.firstName,
            lastName = user.lastName,
            phone = user.personalPhone,
            isAuthenticated = false
        )
        userDao.insertUser(userEntity)
    }

    override suspend fun setWelcomeScreenSeen() {
        preferences.markWelcomeScreenSeen(true)
    }

    override suspend fun hasSeenWelcomeScreen(): Boolean {
        return preferences.isWelcomeScreenSeen()
    }
}