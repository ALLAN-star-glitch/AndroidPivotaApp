package com.example.pivota.core.presentations.navigation

import DiscoveryScreen
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

        /* ───────── WELCOME ───────── */
        composable<Welcome> {
            WelcomeScreen(
                onNavigateToGetStarted = { navController.navigate(Discovery) },
                onNavigateToLoginScreen = { navController.navigate(Login) }
            )
        }

        /* ───────── DISCOVERY / PREFERENCES ───────── */
        composable<Discovery> {
            DiscoveryScreen(
                onContinue = { navController.navigate(GuestDashboard) }
            )
        }

        /* ───────── GUEST DASHBOARD ───────── */
        composable<GuestDashboard> {
            DashboardScaffold(
                isGuest = true,
                onLockedAction = {
                    // Trigger on-demand auth for guest users
                    navController.navigate(AuthFlow)
                }
            )
        }

        /* ───────── AUTH FLOW (ON-DEMAND) ───────── */
        navigation<AuthFlow>(startDestination = Register) {

            composable<Register> {
                RegisterScreen(
                    onRegisterSuccess = {
                        navController.navigate(Dashboard) {
                            popUpTo(Welcome) { inclusive = true }
                        }
                    },
                    onNavigateToLoginScreen = { navController.navigate(Login) }
                )
            }

            composable<Login> {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(Dashboard) {
                            popUpTo(Welcome) { inclusive = true }
                        }
                    },
                    onNavigateToRegisterScreen = { navController.navigate(Register) }
                )
            }
        }

        /* ───────── FULL DASHBOARD ───────── */
        composable<Dashboard> {
            DashboardScaffold(isGuest = false)
        }
    }
}
