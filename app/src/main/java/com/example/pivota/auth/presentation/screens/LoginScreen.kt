package com.example.pivota.auth.presentation.screens


import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.pivota.core.presentations.screens.AdaptiveAuthLayout

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,             // <- renamed to match NavHost
    onNavigateToRegisterScreen: () -> Unit
) {
    AdaptiveAuthLayout(
        header = "LOGIN",
        welcomeText = "Welcome Back",
        desc1 = "After login, you can unlock full SmartMatch™ recommendations and access more opportunities.",
        desc2 = "Upgrade when you're ready!",
        isLoginScreen = true,
        onNavigateToDashboard = onLoginSuccess,   // <- use this internally
        onNavigateToRegisterScreen = onNavigateToRegisterScreen
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    LoginScreen(onLoginSuccess = {}, onNavigateToRegisterScreen = {})
}