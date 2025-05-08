package com.example.pivota.core.presentations.navigation

import PrefrenceScreen
import WelcomeScreen
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pivota.auth.presentation.screens.LoginScreen
import com.example.pivota.auth.presentation.screens.RegisterScreen
import com.example.pivota.dashboard.presentation.EmployerDashboardScreen

@Composable
fun NavHostSetup(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "welcome",
        modifier = modifier
    ) {
        // Welcome Screen
        composable("welcome") {
            WelcomeScreen(
                onNavigateToRegisterScreen = {
                    // No popUpTo here – we want the user to be able to go back to welcome
                    navController.navigate("register")
                },
                onNavigateToLoginScreen = {
                    // Same here – preserve welcome on back stack
                    navController.navigate("loginScreen")
                }
            )
        }

        // Register Screen
        composable("register") {
            RegisterScreen(
                onRegisterSuccess = {
                    // Clear register screen from back stack after successful registration
                    navController.navigate("loginScreen") {
                        popUpTo("register") { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onNavigateToLoginScreen = {
                    // Don't clear back stack if just navigating between forms
                    navController.navigate("loginScreen")
                }
            )
        }

        // Login Screen
        composable("loginScreen") {
            LoginScreen(
                onNavigateToDashboardScreen = {
                    // Clear login screen after successful login
                    navController.navigate("preferenceScreen") {
                        popUpTo("loginScreen") { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onNavigateToRegisterScreen = {
                    navController.navigate("register")
                }
            )
        }

        // Preference Screen
        composable("preferenceScreen") {
            PrefrenceScreen(
                onNavigateToDashboardScreen = {
                    // Clear preference screen once dashboard is reached
                    navController.navigate("dashboard") {
                        popUpTo("preferenceScreen") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        // Final Dashboard
        composable("dashboard") {
            EmployerDashboardScreen()
        }
    }
}
