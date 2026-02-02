package com.example.pivota.auth.data.repository

import com.example.pivota.auth.data.mapper.*
import com.example.pivota.auth.data.remote.AuthApiService
import com.example.pivota.auth.data.remote.dto.*
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

    override suspend fun requestOtp(email: String, purpose: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.requestOtp(RequestOtpRequest(email, purpose))
            if (response.success) Result.success(Unit)
            else Result.failure(Exception(response.message))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signupIndividual(user: User, code: String, password: String): Result<User> =
        executeAuth {
            apiService.signupIndividual(user.toIndividualRequest(code, password))
        }

    override suspend fun signupOrganization(user: User, code: String, password: String): Result<User> =
        executeAuth {
            apiService.signupOrganization(user.toOrganisationRequest(code, password))
        }

    override suspend fun loginWithMfa(email: String, code: String): Result<User> =
        executeAuth {
            apiService.verifyLoginOtp(VerifyLoginOtpDto(email, code))
        }

    /**
     * Centralized Authentication Orchestrator
     * Logic: Maps the complex UserResponseDto (Account + User + Completion)
     * into the Domain User model and persists it.
     */
    private suspend fun executeAuth(
        call: suspend () -> BaseResponseDto<UserResponseDto>
    ): Result<User> = withContext(Dispatchers.IO) {
        try {
            val response = call()

            // Handle the nested data structure from your backend response
            val data = response.data
            if (response.success && data != null) {

                // 1. Map DTO components to Domain Model
                // We pass 'data.account' and 'data.completion' into the mapper
                // to populate fields like accountType and completionPercentage
                val domainUser = data.user.toDomain(
                    account = data.account,
                    completion = data.completion
                )

                // 2. Local Database Sync
                saveAuthenticatedUser(domainUser)

                // 3. Mark account-specific flags in Preferences
                preferences.saveUserEmail(domainUser.email)

                Result.success(domainUser)
            } else {
                // Return server-side error message (e.g., "Signup successful" or errors)
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            // Handle network or parsing exceptions
            Result.failure(e)
        }
    }

    override suspend fun saveAuthenticatedUser(user: User) {
        withContext(Dispatchers.IO) {
            // Persist the domain user as an Entity for offline access
            userDao.insertUser(user.toEntity())

            // Mark onboarding/welcome flow as done since user is registered
            preferences.setOnboardingComplete(true)
        }
    }

    override suspend fun hasSeenWelcomeScreen(): Boolean = preferences.isOnboardingComplete()

    override suspend fun setWelcomeScreenSeen() {
        preferences.setOnboardingComplete(true)
    }
}