/**
 * Implementation of [AuthRepository] handling the multi-stage authentication lifecycle.
 * * This repository orchestrates:
 * - **Identity Verification**: Two-stage OTP requests and validation for logins and signups.
 * - **Account Flexibility**: Distinct registration flows for Individuals and
 * Organizations (where the creator is automatically assigned the Admin role).
 * - **Local State Synchronization**: Persisting session tokens to [PivotaPreferences]
 * and user profiles to [UserDao] to manage the app's onboarding and auth state.
 */

package com.example.pivota.auth.data.repository

import com.example.pivota.auth.data.mapper.toDomain
import com.example.pivota.auth.data.mapper.toEntity
import com.example.pivota.auth.data.remote.AuthApiService
import com.example.pivota.auth.data.remote.dto.*
import com.example.pivota.auth.domain.model.AccountType
import com.example.pivota.auth.domain.model.User
import com.example.pivota.auth.domain.repository.AuthRepository
import com.example.pivota.core.database.dao.UserDao
import com.example.pivota.core.preferences.PivotaPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val apiService: AuthApiService,
    private val userDao: UserDao,
    private val preferences: PivotaPreferences
) : AuthRepository {



    /**
     * Stage 1: Request OTP
     * Triggers the backend email engine.
     * Use "SIGNUP" for new users or "2FA" for logging in.
     */
    override suspend fun requestOtp(email: String, purpose: String): Result<Unit> {
        return try {
            val response = apiService.requestOtp(email, purpose)
            if (response.success) Result.success(Unit)
            else Result.failure(Exception(response.message))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Stage 2: Individual Registration
     * Sends held user details + the verified OTP code to the server.
     */
    override suspend fun signupIndividual(user: User, code: String, password: String): Result<User> {
        return try {
            val request = UserSignupRequestDto(
                email = user.email,
                password = password,
                firstName = user.firstName,
                lastName = user.lastName,
                phone = user.personalPhone,
                code = code
            )
            val response = apiService.signupIndividual(request)
            if (response.success && response.data != null) {
                Result.success(response.data.toDomain())
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Stage 2: Organization Registration
     * Sends org + admin details + the verified OTP code to the server.
     */
    override suspend fun signupOrganization(user: User, code: String, password: String): Result<User> {
        return try {
            val org = user.accountType as? AccountType.Organization
                ?: return Result.failure(Exception("Invalid Organization Data"))

            val request = OrganisationSignupRequestDto(
                name = org.orgName,
                officialEmail = user.email,
                officialPhone = user.personalPhone,
                physicalAddress = "",
                email = user.email,
                phone = user.personalPhone,
                adminFirstName = user.firstName,
                adminLastName = user.lastName,
                password = password,
                code = code
            )
            val response = apiService.signupOrganization(request)
            if (response.success && response.data != null) {
                Result.success(response.data.toDomain())
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Stage 2: Login (MFA)
     * Matches the backend 'verifyMfaLogin' logic.
     * Validates the 2FA code and returns User with Access/Refresh tokens.
     */
    override suspend fun loginWithMfa(email: String, code: String): Result<User> {
        // Moves execution to the IO thread pool
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.verifyLoginOtp(VerifyLoginOtpDto(email, code))
                if (response.success && response.data != null) {
                    Result.success(response.data.toDomain())
                } else {
                    Result.failure(Exception(response.message))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Local Persistence
     * Saves the authenticated user and their tokens to Room.
     */
    override suspend fun saveAuthenticatedUser(user: User) {
        userDao.insertUser(user.toEntity())
        // Once logged in, onboarding/welcome screen is naturally bypassed
        preferences.setOnboardingComplete(true)
    }

    /**
     * Optional Onboarding: Marks the "Get Started" bypass
     */
    override suspend fun setWelcomeScreenSeen() {
        preferences.setOnboardingComplete(true)
    }

    /**
     * Optional Onboarding: Checks if the user should skip the Welcome Screen
     */
    override suspend fun hasSeenWelcomeScreen(): Boolean {
        return preferences.isOnboardingComplete()
    }
}