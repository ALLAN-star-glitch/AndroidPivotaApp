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
import com.example.pivota.auth.presentation.screens.RegisterScreen
import com.example.pivota.auth.presentation.screens.SplashScreen
import com.example.pivota.auth.presentation.screens.VerifyOtpScreen
import com.example.pivota.auth.presentation.viewModel.LoginViewModel
import com.example.pivota.auth.presentation.viewModel.SignupViewModel
import com.example.pivota.dashboard.presentation.screens.DashboardScaffold

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

        /* ───────── WELCOME ───────── */
        composable<Welcome> {
            WelcomeScreen(
                onNavigateToGetStarted = { navController.navigate(Discovery) },
                onNavigateToLoginScreen = { navController.navigate(AuthFlow) }
            )
        }

        /* ───────── DISCOVERY / INTERESTS ───────── */
        composable<Discovery> {
            InterestsScreen(
                onBack = { navController.popBackStack() },
                onSave = {
                    navController.navigate(GuestDashboard) {
                        popUpTo(Welcome) { inclusive = true }
                    }
                }
            )
        }

        /* ───────── GUEST DASHBOARD ───────── */
        composable<GuestDashboard> {
            DashboardScaffold(
                isGuest = true,
                onLockedAction = { navController.navigate(AuthFlow) }
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
                        navController.navigate(Register) {
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

            /* ───────── REGISTER SCREEN ───────── */
            composable<Register> { backStackEntry ->
                // Shared ViewModel scoped to the AuthFlow graph
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(AuthFlow)
                }
                val signupViewModel: SignupViewModel = hiltViewModel(parentEntry)

                RegisterScreen(
                    viewModel = signupViewModel,
                    onSuccess = { email ->
                        // After Step 1 (OTP Sent), navigate to Verification
                        navController.navigate(VerifyOtp(email = email, isLogin = false))
                    },
                    onLoginClick = {
                        navController.navigate(Login) {
                            popUpTo(Register) { inclusive = true }
                        }
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
                        // Final registration success: clear auth graph and go to Dashboard
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
            DashboardScaffold(isGuest = false)
        }
    }
}