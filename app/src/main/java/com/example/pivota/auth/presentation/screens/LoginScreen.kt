package com.example.pivota.auth.presentation.screens
import androidx.activity.compose.BackHandler // Fixes the unresolved reference
import androidx.compose.runtime.Composable
import com.example.pivota.auth.presentation.composables.AdaptiveAuthLayout

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onRegisterClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onBack: () -> Unit
) {
    // This intercepts the system back button and triggers your navigation logic
    BackHandler {
        onBack()
    }

    AdaptiveAuthLayout(
        desc1 = "Access Opportunities in Kenya",
        desc2 = "Your Trusted Life Partner",
        isLoginScreen = true,
        onLoginSuccess = onLoginSuccess,
        onRegisterClick = onRegisterClick,
        onForgotPasswordClick = onForgotPasswordClick,
        onLoginClick = { /* Already on login screen */ },
        onSuccess = { /* Not used in login mode */ }
    )
}