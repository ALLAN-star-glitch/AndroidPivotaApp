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
import androidx.navigation.toRoute
import com.example.pivota.auth.presentation.screens.InterestsScreen
import com.example.pivota.auth.presentation.screens.LoginScreen
import com.example.pivota.auth.presentation.screens.SplashScreen
import com.example.pivota.auth.presentation.screens.VerifyOtpScreen
import com.example.pivota.auth.presentation.viewModel.LoginViewModel
import com.example.pivota.auth.presentation.viewModel.SignupViewModel
import com.example.pivota.dashboard.presentation.screens.DashboardScaffold
import com.example.pivota.welcome.presentation.screens.OnboardingPager

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
                    // Navigate to full onboarding flow
                    navController.navigate(OnboardingFlow)
                },
                onNavigateToContinueWithGoogle = {
                    // TODO: Implement Google Sign-In when ready
                    navController.navigate(GuestDashboard)
                },
                onNavigateToLogin = {
                    // Navigate to login screen
                    navController.navigate(AuthFlow)
                },
                onNavigateToSkipToDashboard = {
                    // Skip to minimal account (guest dashboard)
                    navController.navigate(GuestDashboard)
                }
            )
        }

        /* ───────── ONBOARDING FLOW ───────── */
        composable<OnboardingFlow> {
            OnboardingPager(
                onOnboardingComplete = { purpose, purposeData ->
                    // After onboarding complete (registration success), navigate to dashboard
                    navController.navigate(Dashboard) {
                        popUpTo(Welcome) { inclusive = true }
                    }
                },
                onLoginClick = {
                    // Navigate to login screen
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
                    onSuccess = { email ->
                        navController.navigate(VerifyOtp(email = email, isLogin = true))
                    },
                    onRegisterClick = {
                        // Navigate to onboarding flow instead of separate register screen
                        navController.navigate(OnboardingFlow) {
                            popUpTo(Login) { inclusive = true }
                        }
                    },
                    onForgotPasswordClick = {
                        // TODO: navController.navigate(ForgotPassword)
                    },
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }

            /* ───────── VERIFY OTP SCREEN ───────── */
            composable<VerifyOtp> { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(AuthFlow)
                }
                val signupViewModel: SignupViewModel = hiltViewModel(parentEntry)
                val loginViewModel: LoginViewModel = hiltViewModel(backStackEntry)
                val args = backStackEntry.toRoute<VerifyOtp>()

                VerifyOtpScreen(
                    email = args.email,
                    isLoginFlow = args.isLogin,
                    loginViewModel = loginViewModel,
                    signupViewModel = signupViewModel,
                    onVerificationSuccess = {
                        navController.navigate(Dashboard) {
                            popUpTo(AuthFlow) { inclusive = true }
                        }
                    },
                    onNavigateBack = {
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