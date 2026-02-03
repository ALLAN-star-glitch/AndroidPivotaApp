package com.example.pivota.auth.data.repository

import com.example.pivota.auth.data.mapper.*
import com.example.pivota.auth.data.remote.AuthApiService
import com.example.pivota.auth.data.remote.dto.*
import com.example.pivota.auth.domain.model.User
import com.example.pivota.auth.domain.repository.AuthRepository
import com.example.pivota.core.database.dao.UserDao
import com.example.pivota.core.preferences.PivotaPreferences
import io.ktor.client.call.body
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


    private suspend fun handleException(e: Exception): Throwable {
        return when (e) {
            is io.ktor.client.plugins.ResponseException -> {
                try {
                    // Ktor allows us to peek into the body of the error response
                    val errorResponse = e.response.body<BaseResponseDto<Unit>>()
                    Exception(errorResponse.message)
                } catch (parseException: Exception) {
                    Exception("An unexpected error occurred. Please try again.")
                }
            }
            is java.io.IOException -> {
                Exception("No internet connection. Please check your network.")
            }
            else -> e
        }
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
            val data = response.data

            // Even if status is 200, check the 'success' flag
            if (response.success && data != null) {
                val domainUser = data.user.toDomain(data.account, data.completion)
                saveAuthenticatedUser(domainUser)
                preferences.saveUserEmail(domainUser.email)
                Result.success(domainUser)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            // This is where Ktor's 400/401/500 errors land
            Result.failure(handleException(e))
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