package com.example.pivota.auth.presentation.screens

import androidx.compose.runtime.Composable
import com.example.pivota.auth.presentation.composables.AdaptiveAuthLayout
import com.example.pivota.auth.presentation.viewModel.SignupViewModel

@Composable
fun RegisterScreen(
    viewModel: SignupViewModel, // Passed from NavHost (scoped to AuthFlow)
    onSuccess: (String) -> Unit, // Renamed and passes email to OTP screen
    onLoginClick: () -> Unit      // Renamed for consistency
) {
    // Adaptive Layout for Register Screen
    AdaptiveAuthLayout(
        welcomeText = "Welcome to Pivota",
        desc1 = "After registering, you can upgrade your account to post unlimited jobs, rentals, or services",
        desc2 = "It's free to join. Upgrade when you're ready!",
        isLoginScreen = false,
        onSuccess = onSuccess,
        onLoginClick = onLoginClick
    )
}