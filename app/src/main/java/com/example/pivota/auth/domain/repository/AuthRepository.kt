package com.example.pivota.auth.domain.repository

import com.example.pivota.auth.domain.model.User

interface AuthRepository {

    /**
     * Stage 1: Request OTP
     * Used for SIGNUP, LOGIN (2FA), or FORGOT_PASSWORD.
     * @param email User's email address
     * @param purpose "SIGNUP", "LOGIN", or "FORGOT_PASSWORD"
     * @return Result<Unit> - Success or failure
     */
    suspend fun requestOtp(email: String, purpose: String): Result<Unit>

    /**
     * Stage 2: Verify OTP
     * Verifies the OTP code before proceeding with signup or password reset
     * @param email User's email address
     * @param code 6-digit OTP code
     * @param purpose "SIGNUP", "LOGIN", or "FORGOT_PASSWORD"
     * @return Result<Boolean> - True if verified, false otherwise
     */
    suspend fun verifyOtp(email: String, code: String, purpose: String): Result<Boolean>

    /**
     * Stage 3: Individual Signup
     * Completes the registration process after OTP verification.
     * Backend returns only success message, no tokens.
     * User must login separately after signup.
     * @param user User object containing registration data
     * @param code 6-digit OTP code
     * @param password User's password
     * @return Result<Unit> - Success or failure (no data returned)
     */
    suspend fun signupIndividual(user: User, code: String, password: String): Result<Unit>

    /**
     * Stage 1: User Login
     * First step of login - may return MFA_REQUIRED or tokens directly
     * @param email User's email
     * @param password User's password
     * @return Result<LoginResponse> - Contains MFA status or authenticated user with tokens
     */
    suspend fun login(email: String, password: String): Result<com.example.pivota.auth.domain.model.LoginResponse>

    /**
     * Stage 2: Verify MFA and Complete Login
     * Second step of login - verifies MFA code and returns tokens
     * @param email User's email
     * @param code MFA/2FA code
     * @return Result<User> - Authenticated user with tokens
     */
    suspend fun verifyMfaLogin(email: String, code: String): Result<User>

    /**
     * Refresh expired access token
     * @param refreshToken The refresh token
     * @return Result<Pair<String, String>> - New access and refresh tokens
     */
    suspend fun refreshToken(refreshToken: String): Result<Pair<String, String>>

    /**
     * Request password reset OTP
     * @param email User's email address
     * @return Result<Unit> - Success or failure
     */
    suspend fun requestPasswordReset(email: String): Result<Unit>

    /**
     * Reset password using OTP
     * @param email User's email
     * @param code OTP code
     * @param newPassword New password
     * @return Result<Unit> - Success or failure
     */
    suspend fun resetPassword(email: String, code: String, newPassword: String): Result<Unit>

    /**
     * Logout user and invalidate tokens
     * @param refreshToken The refresh token to invalidate
     * @return Result<Unit> - Success or failure
     */
    suspend fun logout(refreshToken: String): Result<Unit>

    /**
     * Persistence & Navigation
     */
    suspend fun saveAuthenticatedUser(user: User)
    suspend fun setWelcomeScreenSeen()
    suspend fun hasSeenWelcomeScreen(): Boolean
}

/**
 * Login response that can be either MFA required or authenticated
 */
sealed class LoginResponse {
    data class MfaRequired(val email: String, val uuid: String) : LoginResponse()
    data class Authenticated(val user: User, val accessToken: String, val refreshToken: String) : LoginResponse()
}