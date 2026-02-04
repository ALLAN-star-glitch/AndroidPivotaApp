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

    /**
     * Uses [toIndividualRequest] extension function from AuthMapper
     */
    override suspend fun signupIndividual(user: User, code: String, password: String): Result<User> =
        executeAuth {
            apiService.signupIndividual(user.toIndividualRequest(code, password))
        }

    /**
     * Uses [toOrganisationRequest] extension function from AuthMapper
     */
    override suspend fun signupOrganization(user: User, code: String, password: String): Result<User> =
        executeAuth {
            apiService.signupOrganization(user.toOrganisationRequest(code, password))
        }

    override suspend fun loginWithMfa(email: String, code: String, purpose: String): Result<User> =
        executeAuth {
            apiService.verifyLoginOtp(VerifyLoginOtpDto(email, code, purpose))
        }

    override suspend fun loginUser(email: String, password: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.login(LoginRequestDto(email, password))
            if (response.success && response.data != null) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Centralized Authentication Orchestrator
     * Handles: Token persistence, Domain mapping, and Local DB sync.
     */
    private suspend fun executeAuth(
        call: suspend () -> BaseResponseDto<UserResponseDto>
    ): Result<User> = withContext(Dispatchers.IO) {
        try {
            val response = call()
            if (response.success && response.data != null) {
                // 1. Persist Session Tokens
                response.data.accessToken?.let { preferences.saveAccessToken(it) }
                response.data.refreshToken?.let { preferences.saveRefreshToken(it) }

                // 2. Map to Domain
                val domainUser = response.data.toDomain()

                // 3. Persist Profile to Room
                saveAuthenticatedUser(domainUser)

                Result.success(domainUser)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveAuthenticatedUser(user: User) {
        withContext(Dispatchers.IO) {
            // Uses [toEntity] extension function from AuthMapper
            userDao.insertUser(user.toEntity())

            // Marks session as active/onboarded
            preferences.setOnboardingComplete(true)
        }
    }

    override suspend fun hasSeenWelcomeScreen(): Boolean = preferences.isOnboardingComplete()

    override suspend fun setWelcomeScreenSeen() {
        preferences.setOnboardingComplete(true)
    }
}