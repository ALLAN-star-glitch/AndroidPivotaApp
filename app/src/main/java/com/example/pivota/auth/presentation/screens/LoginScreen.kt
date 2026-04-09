package com.example.pivota.auth.presentation.screens

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pivota.auth.domain.model.User
import com.example.pivota.auth.presentation.composables.AdaptiveAuthLayout
import com.example.pivota.auth.presentation.viewModel.SharedAuthViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: (User, String, String, String) -> Unit, // Updated to accept all 4 parameters
    onRegisterClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onBack: () -> Unit,
    successMessage: String? = null,
    sharedAuthViewModel: SharedAuthViewModel = hiltViewModel()
) {
    println("🔍 [LoginScreen] Received successMessage: $successMessage")

    // Clear the shared message after it's been received
    LaunchedEffect(successMessage) {
        if (!successMessage.isNullOrBlank()) {
            // The message has been delivered, clear it from shared ViewModel
            sharedAuthViewModel.clearResetSuccessMessage()
        }
    }

    BackHandler { onBack() }

    AdaptiveAuthLayout(
        desc1 = "Access Opportunities in Kenya",
        desc2 = "Your Trusted Life Partner",
        isLoginScreen = true,
        onRegisterClick = onRegisterClick,
        onForgotPasswordClick = onForgotPasswordClick,
        onLoginClick = { /* Already on login screen */ },
        onLoginSuccess = onLoginSuccess, // This now matches the expected signature
        successMessage = successMessage
    )
}