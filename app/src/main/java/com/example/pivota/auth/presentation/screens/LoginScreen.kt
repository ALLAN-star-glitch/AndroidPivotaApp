package com.example.pivota.auth.presentation.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.pivota.auth.presentation.composables.AdaptiveAuthLayout

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,       // Triggered after successful API call
    onRegisterClick: () -> Unit       // Triggered when user wants to switch to Register
) {
    // Adaptive Layout for Login Screen
    AdaptiveAuthLayout(
        header = "LOGIN",
        welcomeText = "Welcome Back",
        desc1 = "After login, you can unlock full SmartMatch™ recommendations and access more opportunities.",
        desc2 = "Upgrade when you're ready!",
        isLoginScreen = true,
        onDashboardNavigate = onLoginSuccess, // Maps to the internal Dashboard trigger
        onRegisterClick = onRegisterClick     // Maps to the internal switch trigger
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    LoginScreen(
        onLoginSuccess = {},
        onRegisterClick = {}
    )
}