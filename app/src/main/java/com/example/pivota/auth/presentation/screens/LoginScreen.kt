package com.example.pivota.auth.presentation.screens

import AdaptiveAuthLayout
import androidx.compose.runtime.Composable


@Composable
fun LoginScreen(
    onNavigateToDashboardScreen: ()-> Unit,
    onNavigateToRegisterScreen: ()->Unit
                ) {

    //Adaptive Layout for Login Screen
    AdaptiveAuthLayout(
        header = "LOGIN",
        welcomeText = "Welcome Back",
        desc1 = "After login, you can upgrade your account to post unlimited jobs, rentals, or services",
        desc2 = "Upgrade when you're ready!",
        isLoginScreen = true,
        onNavigateToDashboard = onNavigateToDashboardScreen,
        onNavigateToRegisterScreen = onNavigateToRegisterScreen,
    )
}




