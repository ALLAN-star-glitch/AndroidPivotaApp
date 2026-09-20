package com.example.pivota.auth.data.repository

import android.util.Base64
import com.example.pivota.auth.data.mapper.AuthDataMapper
import com.example.pivota.auth.data.remote.api.AuthApiService
import com.example.pivota.auth.data.remote.api.AuthAuthenticatedApiService
import com.example.pivota.auth.data.remote.dto.*
import com.example.pivota.auth.domain.model.CompleteProfileResult
import com.example.pivota.auth.domain.model.LoginResponse
import com.example.pivota.auth.domain.model.OtpRequestResult
import com.example.pivota.auth.domain.model.OtpVerificationResult
import com.example.pivota.auth.domain.model.PasswordResetResult
import com.example.pivota.auth.domain.model.SignupResult
import com.example.pivota.auth.domain.model.TokenRefreshResult
import com.example.pivota.auth.domain.model.User
import com.example.pivota.auth.domain.repository.AuthRepository
import com.example.pivota.core.database.dao.UserDao
import com.example.pivota.core.database.entity.UserEntity
import com.example.pivota.core.network.ApiResult
import com.example.pivota.core.network.NetworkError
import com.example.pivota.core.network.safeApiCall
import com.example.pivota.core.preferences.PivotaDataStore
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val apiService: AuthApiService,
    private val authenticatedApiService: AuthAuthenticatedApiService,
    private val preferences: PivotaDataStore,
    private val userDao: UserDao,
    private val mapper: AuthDataMapper
) : AuthRepository {

    // ======================================================
    // OTP / SIGNUP FLOW
    // ======================================================

    override suspend fun requestOtp(
        email: String,
        purpose: String,
        phone: String?
    ): ApiResult<OtpRequestResult> {
        val request = RequestOtpRequestDto(
            email = email,
            purpose = purpose,
            phone = phone
        )
        return safeApiCall {
            val dto = apiService.requestOtp(request)
            mapper.toOtpRequestResult(dto)
        }
    }

    override suspend fun verifyOtp(
        email: String,
        code: String,
        purpose: String
    ): ApiResult<OtpVerificationResult> {
        val request = VerifyOtpRequestDto(
            email = email,
            code = code,
            purpose = purpose
        )
        return safeApiCall {
            val dto = apiService.verifyOtp(request)
            mapper.toOtpVerificationResult(dto, email)
        }
    }

    override suspend fun signupIndividual(
        user: User,
        code: String,
        password: String
    ): ApiResult<SignupResult> {
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
            val dto = apiService.signup(request)
            val result = mapper.toSignupResult(dto)

            // Side-effect: handle auto-login / payment-required / manual-login
            println("🔍 RESPONSE: success=${dto.success}, message=${dto.message}")

            if (dto.success) {
                when {
                    // Auto-login (free plan): tokens present
                    !result.accessToken.isNullOrEmpty() &&
                            !result.refreshToken.isNullOrEmpty() -> {
                        println("🔍 Auto-login: Tokens received, saving user session")

                        val authenticatedUser = extractUserFromToken(
                            email = request.email,
                            accessToken = result.accessToken,
                            refreshToken = result.refreshToken
                        )

                        val finalUser = authenticatedUser.copy(
                            firstName = request.firstName,
                            lastName = request.lastName,
                            personalPhone = request.phone,
                            primaryPurpose = request.primaryPurpose
                        )

                        saveAuthenticatedUser(finalUser)
                        println("🔍 User auto-logged in successfully: ${finalUser.email}")
                    }

                    // Payment required (premium plan)
                    !result.redirectUrl.isNullOrEmpty() -> {
                        println("🔍 Payment required: Redirect to ${result.redirectUrl}")
                        val basicUser = user.copy(isAuthenticated = false)
                        saveBasicUserInfo(basicUser)
                    }

                    // Fallback: success but no tokens, no redirect
                    else -> {
                        println("🔍 Signup successful, no tokens. User should login manually")
                        val basicUser = user.copy(isAuthenticated = false)
                        saveBasicUserInfo(basicUser)
                    }
                }
            }

            result
        }
    }

    // ======================================================
    // LOGIN FLOW
    // ======================================================

    override suspend fun login(
        email: String,
        password: String
    ): ApiResult<LoginResponse> {
        val request = LoginRequestDto(
            email = email,
            password = password
        )
        return safeApiCall {
            val dto = apiService.login(request)
            val loginResponse = mapper.toLoginResponse(dto)

            // Persist user only if authenticated (not MFA-required)
            if (loginResponse is LoginResponse.Authenticated) {
                saveAuthenticatedUser(loginResponse.user)
            }

            loginResponse
        }
    }

    override suspend fun verifyMfaLogin(
        email: String,
        code: String
    ): ApiResult<LoginResponse> {
        val request = VerifyMfaLoginRequestDto(
            email = email,
            code = code
        )
        return safeApiCall {
            val dto = apiService.verifyMfaLogin(request)
            val loginResponse = mapper.toLoginResponse(dto)

            if (loginResponse is LoginResponse.Authenticated) {
                saveAuthenticatedUser(loginResponse.user)
            }

            loginResponse
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
            val dto = apiService.googleSignIn(request)
            println("🔍 [Google Sign-In] Response: success=${dto.success}, message=${dto.message}")

            val loginResponse = mapper.toLoginResponse(dto)

            if (loginResponse is LoginResponse.Authenticated) {
                saveAuthenticatedUser(loginResponse.user)
                println("🔍 [Google Sign-In] User authenticated: ${loginResponse.user.email}")
            }

            loginResponse
        }
    }

    // ======================================================
    // TOKEN LIFECYCLE
    // ======================================================

    override suspend fun refreshToken(
        refreshToken: String
    ): ApiResult<TokenRefreshResult> {
        return safeApiCall {
            val dto = apiService.refreshToken(refreshToken)
            val result = mapper.toTokenRefreshResult(dto)

            // Persist new tokens
            preferences.saveAccessToken(result.accessToken)
            preferences.saveRefreshToken(result.refreshToken)

            result
        }
    }

    // ======================================================
    // PASSWORD RESET FLOW
    // ======================================================

    override suspend fun requestPasswordReset(
        email: String
    ): ApiResult<OtpRequestResult> {
        return safeApiCall {
            val dto = apiService.requestPasswordReset(email)
            mapper.toOtpRequestResult(dto)
        }
    }

    override suspend fun resetPassword(
        email: String,
        code: String,
        newPassword: String
    ): ApiResult<PasswordResetResult> {
        return safeApiCall {
            val dto = apiService.resetPassword(email, code, newPassword)
            mapper.toPasswordResetResult(dto)
        }
    }

    // ======================================================
    // SESSION
    // ======================================================

    override suspend fun logout(refreshToken: String): ApiResult<Unit> {
        return safeApiCall {
            val dto = authenticatedApiService.logout()
            if (!dto.success) {
                throw IllegalStateException(dto.message.ifBlank { "Logout failed" })
            }

            // Clear local data on success
            preferences.clearUserData()
            userDao.deleteAll()
            println("✅ Logout successful, local data cleared")
        }.also { apiResult ->
            // Even on error, clear local data so the user isn't stuck
            if (apiResult is ApiResult.Error) {
                println("❌ Logout API failed: ${apiResult.networkError.userFriendlyMessage}")
                preferences.clearUserData()
                userDao.deleteAll()
            }
        }
    }

    // ======================================================
    // PERSISTENCE
    // ======================================================

    override suspend fun saveAuthenticatedUser(user: User) {
        user.accessToken?.let { preferences.saveAccessToken(it) }
        user.refreshToken?.let { preferences.saveRefreshToken(it) }
        preferences.saveUserEmail(user.email)
        preferences.markOnboardingComplete(true)

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

        println("🔍 [AuthRepositoryImpl] User saved successfully:")
        println("   - UUID: ${user.uuid}")
        println("   - Email: ${user.email}")
        println("   - Name: ${user.userName}")
        println("   - Role: ${user.role}")
        println("   - Account Type: ${user.accountType}")
        println("   - Account ID: ${user.accountId}")
        println("   - Token ID: ${user.tokenId}")
        user.organizationUuid?.let { println("   - Organization UUID: $it") }
        user.planSlug?.let { println("   - Plan Slug: $it") }
    }

    override suspend fun setWelcomeScreenSeen() {
        preferences.markWelcomeScreenSeen(true)
    }

    override suspend fun hasSeenWelcomeScreen(): Boolean {
        return preferences.isWelcomeScreenSeen()
    }

    // ======================================================
    // HELPERS (unchanged from your original, private to impl)
    // ======================================================

    private suspend fun saveBasicUserInfo(user: User) {
        preferences.saveUserEmail(user.email)

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

    private suspend fun updateLocalUserFromProfile(profile: CompleteProfileResult) {
        try {
            val existingUser = userDao.getUserByEmail(profile.user?.email ?: return)

            val jobSeekerJson = profile.jobSeekerProfile?.let { Json.encodeToString(it) }
            val skilledProfessionalJson = profile.skilledProfessionalProfile?.let { Json.encodeToString(it) }
            val intermediaryAgentJson = profile.intermediaryAgentProfile?.let { Json.encodeToString(it) }
            val housingSeekerJson = profile.housingSeekerProfile?.let { Json.encodeToString(it) }
            val supportBeneficiaryJson = profile.supportBeneficiaryProfile?.let { Json.encodeToString(it) }
            val employerJson = profile.employerProfile?.let { Json.encodeToString(it) }
            val propertyOwnerJson = profile.propertyOwnerProfile?.let { Json.encodeToString(it) }
            val organizationJson = profile.organizationProfile?.let { Json.encodeToString(it) }
            val individualJson = profile.individualProfile?.let { Json.encodeToString(it) }
            val verificationsJson = Json.encodeToString(profile.verifications)
            val completionJson = profile.completion?.let { Json.encodeToString(it) }

            val updatedUser = UserEntity(
                uuid = profile.user?.uuid ?: existingUser?.uuid ?: "",
                email = profile.user?.email ?: existingUser?.email ?: return,
                firstName = profile.user?.firstName ?: existingUser?.firstName ?: "",
                lastName = profile.user?.lastName ?: existingUser?.lastName ?: "",
                userName = profile.displayName,
                phone = profile.user?.personalPhone ?: existingUser?.phone,
                profileImage = profile.profileImageUrl ?: existingUser?.profileImage,
                isAuthenticated = existingUser?.isAuthenticated ?: true,
                isOnboardingComplete = existingUser?.isOnboardingComplete ?: true,
                hasSeenWelcomeScreen = existingUser?.hasSeenWelcomeScreen ?: true,
                primaryPurpose = profile.user?.primaryPurpose ?: existingUser?.primaryPurpose,
                role = profile.user?.role ?: existingUser?.role,
                accountType = profile.accountType,
                accountId = profile.account.uuid,
                accountName = profile.account.name,
                organizationUuid = existingUser?.organizationUuid,
                planSlug = existingUser?.planSlug,
                tokenId = existingUser?.tokenId,
                jobSeekerPreferences = jobSeekerJson,
                skilledProfessionalProfile = skilledProfessionalJson,
                intermediaryAgentProfile = intermediaryAgentJson,
                housingSeekerPreferences = housingSeekerJson,
                supportBeneficiaryNeeds = supportBeneficiaryJson,
                employerRequirements = employerJson,
                propertyOwnerPortfolio = propertyOwnerJson,
                organizationProfile = organizationJson,
                individualProfile = individualJson,
                verifications = verificationsJson,
                profileCompletion = completionJson,
                updatedAt = System.currentTimeMillis()
            )

            userDao.updateUser(updatedUser)
            println("✅ Local user data updated from profile")
        } catch (e: Exception) {
            println("⚠️ Failed to update local user from profile: ${e.message}")
            e.printStackTrace()
        }
    }

    private suspend fun extractUserFromToken(
        email: String,
        accessToken: String,
        refreshToken: String?
    ): User {
        return try {
            val parts = accessToken.split(".")
            if (parts.size != 3) {
                return User(
                    email = email,
                    isAuthenticated = true,
                    accessToken = accessToken,
                    refreshToken = refreshToken
                )
            }

            val payloadJson = decodeBase64Url(parts[1])
            val jsonElement = Json.parseToJsonElement(payloadJson)
            val jsonObject = jsonElement.jsonObject

            val userUuid = jsonObject["sub"]?.jsonPrimitive?.contentOrNull ?: ""
            val tokenId = jsonObject["jti"]?.jsonPrimitive?.contentOrNull ?: ""
            val emailFromToken = jsonObject["email"]?.jsonPrimitive?.contentOrNull ?: email
            val accountId = jsonObject["accountId"]?.jsonPrimitive?.contentOrNull ?: ""
            val role = jsonObject["role"]?.jsonPrimitive?.contentOrNull ?: "Individual"
            val accountType = jsonObject["accountType"]?.jsonPrimitive?.contentOrNull
            val organizationUuid = jsonObject["organizationUuid"]?.jsonPrimitive?.contentOrNull
            val planSlug = jsonObject["planSlug"]?.jsonPrimitive?.contentOrNull

            println("🔍 [JWT Decoded - Backend Structure]")
            println("   - sub (userUuid): $userUuid")
            println("   - jti (tokenId): $tokenId")
            println("   - email: $emailFromToken")
            println("   - accountId: $accountId")
            println("   - role: $role")
            println("   - accountType: $accountType")
            println("   - organizationUuid: $organizationUuid")
            println("   - planSlug: $planSlug")

            val existingUser = userDao.getUserByEmail(emailFromToken)
            val firstName = existingUser?.firstName ?: emailFromToken.substringBefore("@")
            val lastName = existingUser?.lastName ?: ""
            val userName = existingUser?.userName ?: firstName
            val profileImage = existingUser?.profileImage
            val phone = existingUser?.phone

            User(
                uuid = userUuid,
                email = emailFromToken,
                firstName = firstName,
                lastName = lastName,
                userName = userName,
                personalPhone = phone,
                profileImage = profileImage,
                accessToken = accessToken,
                refreshToken = refreshToken,
                isAuthenticated = true,
                userUuid = userUuid,
                accountId = accountId,
                accountType = accountType,
                tokenId = tokenId,
                role = role,
                organizationUuid = organizationUuid,
                planSlug = planSlug,
                accountName = existingUser?.accountName ?: "",
                primaryPurpose = existingUser?.primaryPurpose
            )
        } catch (e: Exception) {
            println("🔍 [JWT Decode Error] ${e.message}")
            e.printStackTrace()
            User(
                email = email,
                isAuthenticated = true,
                accessToken = accessToken,
                refreshToken = refreshToken
            )
        }
    }

    private fun decodeBase64Url(base64Url: String): String {
        var base64 = base64Url.replace('-', '+').replace('_', '/')
        when (base64.length % 4) {
            2 -> base64 += "=="
            3 -> base64 += "="
        }
        val decodedBytes = Base64.decode(base64, Base64.DEFAULT)
        return String(decodedBytes, Charsets.UTF_8)
    }
}