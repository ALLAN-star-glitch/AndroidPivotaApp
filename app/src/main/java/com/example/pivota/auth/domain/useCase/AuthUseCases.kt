// Update: com.example.pivota.auth.domain.useCase.AuthUseCases

package com.example.pivota.auth.domain.useCase

data class AuthUseCases(
    val requestOtp: RequestOtpUseCase,
    val registerUser: RegisterUserUseCase,
    val loginUser: LoginUserUseCase,
    val verifyMfaLogin: VerifyMfaLoginUseCase,
    val refreshToken: RefreshTokenUseCase,
    val requestPasswordReset: RequestPasswordResetUseCase,
    val resetPassword: ResetPasswordUseCase,
    val logout: LogoutUseCase,
    val hasSeenWelcome: HasSeenWelcomeUseCase,
    val setWelcomeSeen: SetWelcomeSeenUseCase
)