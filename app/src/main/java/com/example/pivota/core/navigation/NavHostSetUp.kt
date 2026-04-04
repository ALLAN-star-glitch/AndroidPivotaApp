package com.example.pivota.core.navigation

import WelcomeScreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pivota.auth.domain.model.User
import com.example.pivota.auth.presentation.screens.AdaptiveResetPasswordScreen
import com.example.pivota.auth.presentation.screens.LoginScreen
import com.example.pivota.auth.presentation.screens.SplashScreen
import com.example.pivota.auth.presentation.viewModel.SharedAuthViewModel
import com.example.pivota.core.preferences.PivotaDataStore
import com.example.pivota.dashboard.presentation.screens.DashboardScaffold
import com.example.pivota.welcome.presentation.screens.OnboardingPager
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.EntryPointAccessors
import kotlinx.serialization.json.Json

@Composable
fun NavHostSetup(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val sharedAuthViewModel: SharedAuthViewModel = hiltViewModel()

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
                // Only get reset password message for login screen
                val resetSuccessMessage = sharedAuthViewModel.peekResetSuccessMessage()

                println("🔍 [NavHostSetup] Login screen received resetSuccessMessage: $resetSuccessMessage")

                LoginScreen(
                    onLoginSuccess = { user, message, accessToken, refreshToken ->
                        // Set login success message and tokens for dashboard
                        sharedAuthViewModel.setLoginSuccessMessage(message)
                        sharedAuthViewModel.setUserTokens(accessToken, refreshToken)
                        sharedAuthViewModel.setUser(user)
                        // Clear reset message since it's been shown
                        sharedAuthViewModel.clearResetSuccessMessage()
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
                        navController.navigate(ResetPassword)
                    },
                    onBack = {
                        navController.popBackStack()
                    },
                    successMessage = resetSuccessMessage
                )
            }

            /* ───────── RESET PASSWORD SCREEN ───────── */
            composable<ResetPassword> {
                AdaptiveResetPasswordScreen(
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onPasswordReset = { successMessage ->
                        println("🔍 [NavHostSetup] ResetPasswordScreen onPasswordReset called with message: $successMessage")
                        sharedAuthViewModel.setResetSuccessMessage(successMessage)
                        navController.popBackStack(Login, inclusive = false)
                    }
                )
            }
        }

        /* ───────── AUTHENTICATED DASHBOARD ───────── */
        composable<Dashboard> {
            // Get login success message and user data from shared view model
            val loginSuccessMessage = sharedAuthViewModel.peekLoginSuccessMessage()
            val user = sharedAuthViewModel.peekUser()
            val accessToken = sharedAuthViewModel.peekAccessToken()
            val refreshToken = sharedAuthViewModel.peekRefreshToken()

            println("🔍 [NavHostSetup] Dashboard received loginSuccessMessage: $loginSuccessMessage")
            println("🔍 [NavHostSetup] Dashboard received user: ${user?.email}")

            DashboardScaffold(
                isGuestMode = false,
                successMessage = loginSuccessMessage,
                user = user,
                accessToken = accessToken,
                refreshToken = refreshToken,
                onMessageConsumed = {
                    sharedAuthViewModel.clearLoginSuccessMessage()
                }
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