package com.example.pivota.auth.presentation.screens



import androidx.compose.runtime.Composable
import com.example.pivota.core.presentations.screens.AdaptiveAuthLayout


@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLoginScreen: ()-> Unit
) {

    //Adaptive Layout for Register Screen
    AdaptiveAuthLayout(
        header = "REGISTER",
        welcomeText = "Welcome to Pivota",
        desc1 = "After registering, you can upgrade your account to post unlimited jobs, rentals, or services",
        desc2 = "It's free to join. Upgrade when you're ready!",
        isLoginScreen = false,
        onRegisterSuccess = onRegisterSuccess,
        onNavigateToLogin = onNavigateToLoginScreen,
    )
}






