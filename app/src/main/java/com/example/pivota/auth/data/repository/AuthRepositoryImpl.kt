package com.example.pivota.auth.data.repository

import com.example.pivota.auth.data.mapper.AuthDataMapper
import com.example.pivota.auth.data.remote.api.AuthApiService
import com.example.pivota.auth.data.remote.dto.*
import com.example.pivota.auth.domain.model.LoginResponse
import com.example.pivota.auth.domain.model.User
import com.example.pivota.auth.domain.repository.AuthRepository
import com.example.pivota.core.database.dao.UserDao
import com.example.pivota.core.database.entity.UserEntity
import com.example.pivota.core.preferences.PivotaDataStore
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val apiService: AuthApiService,
    private val preferences: PivotaDataStore,
    private val userDao: UserDao,
    private val mapper: AuthDataMapper
) : AuthRepository {

    override suspend fun requestOtp(email: String, purpose: String): Result<Unit> {
        return try {
            val request = RequestOtpRequestDto(
                email = email,
                purpose = purpose
            )
            val response: BaseOtpResponseDto = apiService.requestOtp(request)

            if (response.success) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun verifyOtp(email: String, code: String, purpose: String): Result<Boolean> {
        return try {
            val request = VerifyOtpRequestDto(
                email = email,
                code = code,
                purpose = purpose
            )
            val response: VerifyOtpResponseDto = apiService.verifyOtp(request)

            if (response.success && response.data != null) {
                Result.success(response.data.verified)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signupIndividual(
        user: User,
        code: String,
        password: String
    ): Result<Unit> {
        return try {
            val request = mapper.toSignupRequestDto(user, code, password)

            // ========== LOG THE FULL REQUEST ==========
            println("🔍 ==================== SIGNUP REQUEST ====================")
            println("🔍 firstName: ${request.firstName}")
            println("🔍 lastName: ${request.lastName}")
            println("🔍 email: ${request.email}")
            println("🔍 phone: ${request.phone}")
            println("🔍 password: ${request.password}")
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

            val response = apiService.signup(request)

            println("🔍 RESPONSE: success=${response.success}, message=${response.message}")

            if (response.success) {
                val basicUser = user.copy(isAuthenticated = false)
                saveBasicUserInfo(basicUser)
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            println("🔍 ERROR: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun login(email: String, password: String): Result<LoginResponse> {
        return try {
            val request = LoginRequestDto(
                email = email,
                password = password
            )
            val response: LoginResponseDto = apiService.login(request)

            if (response.success && response.data != null) {
                val data = response.data

                // Check if MFA is required (Stage 1)
                if (data.message == "MFA_REQUIRED" && data.email != null && data.uuid != null) {
                    Result.success(LoginResponse.MfaRequired(data.email, data.uuid))
                } else if (data.accessToken != null && data.refreshToken != null) {
                    // Direct login without MFA
                    val user = User(
                        email = email,
                        isAuthenticated = true,
                        accessToken = data.accessToken,
                        refreshToken = data.refreshToken
                    )
                    saveAuthenticatedUser(user)
                    Result.success(LoginResponse.Authenticated(user, data.accessToken, data.refreshToken))
                } else {
                    Result.failure(Exception("Invalid login response"))
                }
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun verifyMfaLogin(email: String, code: String): Result<User> {
        return try {
            val request = VerifyMfaLoginRequestDto(
                email = email,
                code = code
            )
            val response: LoginResponseDto = apiService.verifyMfaLogin(request)

            if (response.success && response.data != null) {
                val data = response.data
                if (data.accessToken != null && data.refreshToken != null) {
                    val user = User(
                        email = email,
                        isAuthenticated = true,
                        accessToken = data.accessToken,
                        refreshToken = data.refreshToken
                    )
                    saveAuthenticatedUser(user)
                    Result.success(user)
                } else {
                    Result.failure(Exception("Invalid MFA verification response"))
                }
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun refreshToken(refreshToken: String): Result<Pair<String, String>> {
        return try {
            val response: RefreshTokenResponseDto = apiService.refreshToken(refreshToken)

            if (response.success && response.data != null) {
                val tokens = response.data
                // Update stored tokens
                preferences.saveAccessToken(tokens.accessToken)
                preferences.saveRefreshToken(tokens.refreshToken)
                Result.success(Pair(tokens.accessToken, tokens.refreshToken))
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun requestPasswordReset(email: String): Result<Unit> {
        return try {
            val response: BaseOtpResponseDto = apiService.requestPasswordReset(email)

            if (response.success) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun resetPassword(email: String, code: String, newPassword: String): Result<Unit> {
        return try {
            val response: BaseResponseDto<Nothing> = apiService.resetPassword(email, code, newPassword)

            if (response.success) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout(refreshToken: String): Result<Unit> {
        return try {
            val response: BaseResponseDto<Nothing> = apiService.logout(refreshToken)

            if (response.success) {
                // Clear local user data
                preferences.run { clearUserData() }
                userDao.deleteAll()
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveAuthenticatedUser(user: User) {
        // Save to DataStore
        user.accessToken?.let { preferences.saveAccessToken(it) }
        user.refreshToken?.let { preferences.saveRefreshToken(it) }
        preferences.saveUserEmail(user.email)
        preferences.markOnboardingComplete(true)

        // Save to Room Database
        val userEntity = UserEntity(
            uuid = user.uuid,
            email = user.email,
            firstName = user.firstName,
            lastName = user.lastName,
            phone = user.personalPhone,
            isAuthenticated = user.isAuthenticated
        )
        userDao.insertUser(userEntity)
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