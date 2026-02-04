package com.example.pivota.auth.domain.useCase

data class AuthUseCases(
    val requestOtp: RequestOtpUseCase,
    val registerUser: RegisterUserUseCase,
    val loginWithMfa: LoginWithMfaUseCase,
    val setWelcomeSeen: SetWelcomeSeenUseCase,
    val hasSeenWelcome: HasSeenWelcomeUseCase,
    val loginUser: LoginUserUserUseCase
)