package com.example.pivota.core.navigation

import WelcomeScreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.pivota.auth.presentation.screens.LoginScreen
import com.example.pivota.auth.presentation.screens.SplashScreen
import com.example.pivota.core.preferences.PivotaDataStore
import com.example.pivota.dashboard.presentation.screens.DashboardScaffold
import com.example.pivota.welcome.presentation.screens.OnboardingPager
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.EntryPointAccessors

@Composable
fun NavHostSetup(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Splash,
        modifier = modifier
    ) {

        /* ───────── SPLASH SCREEN ───────── */
        composable<Splash> {
            SplashScreen(
                viewModel = hiltViewModel(),
                onNavigate = { destinationRoute ->
                    navController.navigate(destinationRoute) {
                        popUpTo(Splash) { inclusive = true }
                    }
                }
            )
        }

        /* ───────── WELCOME SCREEN ───────── */
        composable<Welcome> {
            WelcomeScreen(
                onNavigateToContinueSetup = {
                    navController.navigate(OnboardingFlow)
                },
                onNavigateToContinueWithGoogle = {
                    navController.navigate(GuestDashboard)
                },
                onNavigateToLogin = {
                    navController.navigate(AuthFlow)
                },
                onNavigateToSkipToDashboard = {
                    navController.navigate(GuestDashboard)
                }
            )
        }

        /* ───────── ONBOARDING FLOW ───────── */
        composable<OnboardingFlow> {
            val onboardingDataStore = rememberOnboardingDataStore()

            OnboardingPager(
                datastore = onboardingDataStore,
                onOnboardingComplete = { purpose, purposeData ->
                    navController.navigate(Dashboard) {
                        popUpTo(Welcome) { inclusive = true }
                    }
                },
                onLoginClick = {
                    navController.navigate(AuthFlow)
                }
            )
        }

        /* ───────── GUEST DASHBOARD ───────── */
        composable<GuestDashboard> {
            DashboardScaffold(
                isGuestMode = true
            )
        }

        /* ───────── AUTH FLOW (NESTED) ───────── */
        navigation<AuthFlow>(startDestination = Login) {

            /* ───────── LOGIN SCREEN ───────── */
            composable<Login> {
                LoginScreen(
                    onSuccess = { user ->
                        navController.navigate(Dashboard) {
                            popUpTo(AuthFlow) { inclusive = true }
                        }
                    },
                    onRegisterClick = {
                        navController.navigate(OnboardingFlow) {
                            popUpTo(Login) { inclusive = true }
                        }
                    },
                    onForgotPasswordClick = {
                        // TODO: Navigate to ForgotPassword screen when implemented
                    },
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
        }

        /* ───────── AUTHENTICATED DASHBOARD ───────── */
        composable<Dashboard> {
            DashboardScaffold(
                isGuestMode = false
            )
        }
    }
}

@Composable
fun rememberOnboardingDataStore(): PivotaDataStore {
    val context = androidx.compose.ui.platform.LocalContext.current

    val entryPoint = remember {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            OnboardingDataStoreEntryPoint::class.java
        )
    }

    return entryPoint.onboardingDataStore()
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface OnboardingDataStoreEntryPoint {
    fun onboardingDataStore(): PivotaDataStore
}