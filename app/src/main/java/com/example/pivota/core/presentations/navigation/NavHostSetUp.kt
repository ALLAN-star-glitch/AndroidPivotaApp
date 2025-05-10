package com.example.pivota.core.presentations.navigation


import PrefrenceScreen
import WelcomeScreen
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.pivota.auth.presentation.screens.LoginScreen
import com.example.pivota.auth.presentation.screens.RegisterScreen
import com.example.pivota.dashboard.presentation.screens.DashboardScaffold



@Composable
fun NavHostSetup(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Welcome,
        modifier = modifier
    ) {
        composable<Welcome> {
            WelcomeScreen(
                onNavigateToRegisterScreen = {
                    navController.navigate(Register)
                },
                onNavigateToLoginScreen = {
                    navController.navigate(Login)
                }
            )
        }

        composable<Register> {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(MainFlow) {
                        popUpTo(Register) { inclusive = true }
                    }
                },
                onNavigateToLoginScreen = {
                    navController.navigate(Login)
                }
            )
        }

        composable<Login> {
            LoginScreen(
                onNavigateToDashboardScreen = {
                    navController.navigate(Dashboard) {
                        popUpTo(Welcome) { inclusive = true }
                    }
                },
                onNavigateToRegisterScreen = {
                    navController.navigate(Register)
                }
            )
        }

        navigation<MainFlow>(startDestination = Preference) {
            composable<Preference> {
                PrefrenceScreen(
                    onNavigateToDashboardScreen = {
                        navController.navigate(Dashboard) {
                            popUpTo(Welcome) { inclusive = true }
                        }
                    }
                )
            }

            composable<Dashboard> {
                DashboardScaffold()
            }
        }
    }
}
