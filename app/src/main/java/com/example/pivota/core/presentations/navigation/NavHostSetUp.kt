package com.example.pivota.core.presentations.navigation

import WelcomeScreen
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pivota.auth.presentation.screens.LoginScreen
import com.example.pivota.auth.presentation.screens.RegisterScreen
import com.example.pivota.dashboard.presentation.EmployerDashboardScreen
import kotlinx.coroutines.launch

@Composable
fun NavHostSetup(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "welcome",
        modifier = modifier // comes from MainActivity Scaffold padding
    ) {
        composable("welcome") {
            WelcomeScreen(
                onNavigateToRegisterScreen = { navController.navigate("register") },
                onNavigateToLoginScreen = { navController.navigate("loginScreen") }
            )
        }

        composable("register") {
            RegisterScreen(
                onRegisterSuccess = { navController.navigate("loginScreen") },
                onNavigateToLoginScreen = { navController.navigate("loginScreen") }
            )
        }

        composable("loginScreen") {
            LoginScreen(
                onNavigateToDashboardScreen = { navController.navigate("dashboardScreen") },
                onNavigateToRegisterScreen = { navController.navigate("register") }
            )
        }

        composable("dashboardScreen") {
            EmployerDashboardScreen() // apply padding here
        }
    }
}
