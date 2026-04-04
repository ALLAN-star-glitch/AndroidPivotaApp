package com.example.pivota.auth.domain.repository

import com.example.pivota.auth.data.remote.dto.BaseOtpResponseDto
import com.example.pivota.auth.data.remote.dto.BaseResponseDto
import com.example.pivota.auth.data.remote.dto.LoginResponseDto
import com.example.pivota.auth.data.remote.dto.RefreshTokenResponseDto
import com.example.pivota.auth.data.remote.dto.SignupResponseDto
import com.example.pivota.auth.data.remote.dto.VerifyOtpResponseDto
import com.example.pivota.auth.domain.model.User

interface AuthRepository {

    /**
     * Stage 1: Request OTP
     * Backend returns: BaseOtpResponseDto (BaseResponseDto<Nothing>)
     */
    suspend fun requestOtp(email: String, purpose: String): BaseOtpResponseDto

    /**
     * Stage 2: Verify OTP
     * Backend returns: VerifyOtpResponseDto (BaseResponseDto<VerifyOtpDataDto>)
     */
    suspend fun verifyOtp(email: String, code: String, purpose: String): VerifyOtpResponseDto

    /**
     * Stage 3: Individual Signup
     * Backend returns: SignupResponseDto (BaseResponseDto<SignupSuccessDataDto>)
     */
    suspend fun signupIndividual(user: User, code: String, password: String): SignupResponseDto

    /**
     * Stage 1: User Login
     * Backend returns: LoginResponseDto (BaseResponseDto<LoginDataDto>)
     */
    suspend fun login(email: String, password: String): LoginResponseDto

    /**
     * Stage 2: Verify MFA and Complete Login
     * Backend returns: LoginResponseDto (BaseResponseDto<LoginDataDto>)
     */
    suspend fun verifyMfaLogin(email: String, code: String): LoginResponseDto

    /**
     * Refresh expired access token
     * Backend returns: RefreshTokenResponseDto (BaseResponseDto<RefreshTokenDataDto>)
     */
    suspend fun refreshToken(refreshToken: String): RefreshTokenResponseDto

    /**
     * Request password reset OTP
     * Backend returns: BaseOtpResponseDto (BaseResponseDto<Nothing>)
     */
    suspend fun requestPasswordReset(email: String): BaseOtpResponseDto

    /**
     * Reset password using OTP
     * Backend returns: BaseResponseDto<Nothing>
     */
    suspend fun resetPassword(email: String, code: String, newPassword: String): BaseResponseDto<Nothing>

    /**
     * Logout user and invalidate tokens
     * Backend returns: BaseResponseDto<Nothing>
     */
    suspend fun logout(refreshToken: String): BaseResponseDto<Nothing>

    /**
     * Persistence & Navigation
     */
    suspend fun saveAuthenticatedUser(user: User)
    suspend fun setWelcomeScreenSeen()
    suspend fun hasSeenWelcomeScreen(): Boolean


}