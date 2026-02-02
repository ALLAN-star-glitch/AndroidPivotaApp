package com.example.pivota.auth.presentation.screens

import androidx.compose.runtime.Composable
import com.example.pivota.auth.presentation.composables.AdaptiveAuthLayout
import com.example.pivota.auth.presentation.viewModel.SignupViewModel

@Composable
fun RegisterScreen(
    viewModel: SignupViewModel, // Scoped to AuthFlow in NavHost
    onSuccess: (String) -> Unit, // Navigate to OTP screen
    onLoginClick: () -> Unit      // Navigate back to Login
) {
    // Pass the ViewModel explicitly to AdaptiveAuthLayout
    AdaptiveAuthLayout(
        viewModel = viewModel,
        desc1 = "After registering, you can upgrade your account to post unlimited jobs, rentals, or services",
        desc2 = "It's free to join. Upgrade when you're ready!",
        isLoginScreen = false,
        onSuccess = onSuccess,
        onLoginClick = onLoginClick
    )
}
