package com.example.pivota.auth.presentation.screens

import androidx.compose.runtime.Composable
import com.example.pivota.auth.domain.model.User
import com.example.pivota.auth.presentation.composables.AdaptiveAuthLayout
import com.example.pivota.auth.presentation.viewModel.SignupViewModel

@Composable
fun RegisterScreen(
    viewModel: SignupViewModel, // Scoped to AuthFlow in NavHost
    onSuccess: (String, String, String, User?) -> Unit, // (message, accessToken, refreshToken, user)
    onLoginClick: () -> Unit      // Navigate back to Login
) {
    // Pass the ViewModel explicitly to AdaptiveAuthLayout
    AdaptiveAuthLayout(
        viewModel = viewModel,
        desc1 = "After registering, you can upgrade your account to post unlimited jobs, rentals, or services",
        desc2 = "It's free to join. Upgrade when you're ready!",
        isLoginScreen = false,
        onRegisterSuccess = { message, accessToken, refreshToken, user ->
            // Pass the message, tokens, and user to the callback
            onSuccess(message, accessToken, refreshToken, user)
        },
        onLoginClick = onLoginClick
    )
}