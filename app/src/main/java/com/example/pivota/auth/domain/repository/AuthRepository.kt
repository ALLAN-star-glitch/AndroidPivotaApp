package com.example.pivota.auth.domain.repository

import com.example.pivota.auth.domain.model.User

interface AuthRepository {

    /**
     * Stage 1: Request OTP
     * Used for SIGNUP, 2FA (Login), or RESET_PASSWORD.
     */
    suspend fun requestOtp(email: String, purpose: String): Result<Unit>

    /**
     * Stage 2: Registration
     */
    suspend fun signupIndividual(user: User, code: String, password: String): Result<User>
    suspend fun signupOrganization(user: User, code: String, password: String): Result<User>

    /**
     * Stage 2: Login (MFA)
     * This is the single API call that validates the 2FA code and
     * returns the User with Access/Refresh tokens.
     */
    suspend fun loginWithMfa(email: String, code: String): Result<User>

    /**
     * Persistence & Navigation
     */
    suspend fun saveAuthenticatedUser(user: User)
    suspend fun setWelcomeScreenSeen()
    suspend fun hasSeenWelcomeScreen(): Boolean
}