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

    override suspend fun requestOtp(email: String, purpose: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.requestOtp(RequestOtpRequest(email, purpose))
                if (response.success) Result.success(Unit)
                else Result.failure(Exception(response.message))
            } catch (e: Exception) {
                Result.failure(handleException(e))
            }
        }

    override suspend fun signupIndividual(
        user: User,
        code: String,
        password: String
    ): Result<User> =
        executeAuth {
            apiService.signupIndividual(user.toIndividualRequest(code, password))
        }

    override suspend fun signupOrganization(
        user: User,
        code: String,
        password: String
    ): Result<User> =
        executeAuth {
            apiService.signupOrganization(user.toOrganisationRequest(code, password))
        }

    override suspend fun loginWithMfa(
        email: String,
        code: String,
        purpose: String
    ): Result<User> =
        executeAuth {
            apiService.verifyLoginOtp(VerifyLoginOtpDto(email, code, purpose))
        }

    override suspend fun loginUser(
        email: String,
        password: String
    ): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.login(LoginRequestDto(email, password))
                if (response.success && response.data != null) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception(response.message))
                }
            } catch (e: Exception) {
                Result.failure(handleException(e))
            }
        }

    override suspend fun saveAuthenticatedUser(user: User) =
        withContext(Dispatchers.IO) {
            userDao.insertUser(user.toEntity())
            preferences.setOnboardingComplete(true)
        }

    override suspend fun hasSeenWelcomeScreen(): Boolean =
        preferences.isOnboardingComplete()

    override suspend fun setWelcomeScreenSeen() {
        preferences.setOnboardingComplete(true)
    }

    /* ------------------ Helpers ------------------ */

    private suspend fun executeAuth(
        call: suspend () -> BaseResponseDto<UserResponseDto>
    ): Result<User> =
        withContext(Dispatchers.IO) {
            try {
                val response = call()
                val data = response.data

                if (response.success && data != null) {
                    val domainUser = data.user.toDomain(
                        data.account,
                        data.completion
                    )
                    saveAuthenticatedUser(domainUser)
                    preferences.saveUserEmail(domainUser.email)
                    Result.success(domainUser)
                } else {
                    Result.failure(Exception(response.message))
                }
            } catch (e: Exception) {
                Result.failure(handleException(e))
            }
        }

    private suspend fun handleException(e: Exception): Throwable =
        when (e) {
            is io.ktor.client.plugins.ResponseException -> {
                try {
                    val error =
                        e.response.body<BaseResponseDto<Unit>>()
                    Exception(error.message)
                } catch (_: Exception) {
                    Exception("An unexpected error occurred. Please try again.")
                }
            }
            is java.io.IOException ->
                Exception("No internet connection. Please check your network.")
            else -> e
        }
}
