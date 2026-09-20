package com.example.pivota.auth.domain.repository

import com.example.pivota.auth.domain.model.LoginResponse
import com.example.pivota.auth.domain.model.OtpRequestResult
import com.example.pivota.auth.domain.model.OtpVerificationResult
import com.example.pivota.auth.domain.model.PasswordResetResult
import com.example.pivota.auth.domain.model.SignupResult
import com.example.pivota.auth.domain.model.TokenRefreshResult
import com.example.pivota.auth.domain.model.User
import com.example.pivota.core.network.ApiResult

interface AuthRepository {

    // ─────────────────────────────────────────────────────────
    // OTP / Signup flow
    // ─────────────────────────────────────────────────────────

    /**
     * Stage 1: Request OTP for signup / verification.
     */
    suspend fun requestOtp(
        email: String,
        purpose: String,
        phone: String? = null
    ): ApiResult<OtpRequestResult>

    /**
     * Stage 2: Verify OTP code.
     */
    suspend fun verifyOtp(
        email: String,
        code: String,
        purpose: String
    ): ApiResult<OtpVerificationResult>

    /**
     * Stage 3: Complete individual signup after OTP verification.
     */
    suspend fun signupIndividual(
        user: User,
        code: String,
        password: String
    ): ApiResult<SignupResult>

    // ─────────────────────────────────────────────────────────
    // Login flow
    // ─────────────────────────────────────────────────────────

    /**
     * Stage 1: Login with email + password.
     * Returns either MfaRequired or Authenticated.
     */
    suspend fun login(
        email: String,
        password: String
    ): ApiResult<LoginResponse>

    /**
     * Stage 2: Verify MFA code and complete login.
     */
    suspend fun verifyMfaLogin(
        email: String,
        code: String
    ): ApiResult<LoginResponse>

    /**
     * Google Sign-In — login or register using a Google ID token.
     *
     * @param idToken       Google ID token from the client SDK.
     * @param onboardingData Optional onboarding data (primaryPurpose, etc.).
     */
    suspend fun googleSignIn(
        idToken: String,
        onboardingData: Map<String, Any?>? = null
    ): ApiResult<LoginResponse>

    // ─────────────────────────────────────────────────────────
    // Token lifecycle
    // ─────────────────────────────────────────────────────────

    /**
     * Refresh an expired access token.
     */
    suspend fun refreshToken(
        refreshToken: String
    ): ApiResult<TokenRefreshResult>

    // ─────────────────────────────────────────────────────────
    // Password reset flow
    // ─────────────────────────────────────────────────────────

    /**
     * Request a password reset OTP for the given email.
     */
    suspend fun requestPasswordReset(
        email: String
    ): ApiResult<OtpRequestResult>

    /**
     * Reset password using the OTP code.
     */
    suspend fun resetPassword(
        email: String,
        code: String,
        newPassword: String
    ): ApiResult<PasswordResetResult>

    // ─────────────────────────────────────────────────────────
    // Session
    // ─────────────────────────────────────────────────────────

    /**
     * Logout — invalidate tokens on the backend.
     */
    suspend fun logout(
        refreshToken: String
    ): ApiResult<Unit>

    // ─────────────────────────────────────────────────────────
    // Persistence & Navigation
    // ─────────────────────────────────────────────────────────

    suspend fun saveAuthenticatedUser(user: User)
    suspend fun setWelcomeScreenSeen()
    suspend fun hasSeenWelcomeScreen(): Boolean
}