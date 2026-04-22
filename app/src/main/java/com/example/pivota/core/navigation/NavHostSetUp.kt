package com.example.pivota.core.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.pivota.auth.presentation.screens.AdaptiveResetPasswordScreen
import com.example.pivota.auth.presentation.screens.LoginScreen
import com.example.pivota.auth.presentation.screens.SplashScreen
import com.example.pivota.auth.presentation.viewModel.LoginViewModel
import com.example.pivota.auth.presentation.viewModel.SharedAuthViewModel
import com.example.pivota.dashboard.presentation.bookservice.ReviewAndPayScreen
import com.example.pivota.dashboard.presentation.bookservice.ScheduleServiceScreen
import com.example.pivota.dashboard.presentation.bookservice.SelectServiceScreen
import com.example.pivota.dashboard.presentation.bookservice.ServiceDetailsScreen
import com.example.pivota.core.preferences.PivotaDataStore
import com.example.pivota.dashboard.presentation.screens.DashboardScaffold
import com.example.pivota.welcome.presentation.screens.OnboardingPager
import com.example.pivota.welcome.presentation.screens.WelcomeScreen
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.launch

@Composable
fun NavHostSetup(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val sharedAuthViewModel: SharedAuthViewModel = hiltViewModel()
    val onboardingDataStore = rememberOnboardingDataStore()
    val coroutineScope = rememberCoroutineScope()

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
                    coroutineScope.launch {
                        val hasValidTokens = onboardingDataStore.getAccessToken() != null
                        val isGuestMode = onboardingDataStore.isGuestModeEnabled()

                        val targetDestination = when {
                            hasValidTokens -> Dashboard
                            isGuestMode -> GuestDashboard
                            else -> Welcome
                        }

                        navController.navigate(targetDestination) {
                            popUpTo(Splash) { inclusive = true }
                        }
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
                    coroutineScope.launch {
                        onboardingDataStore.setOnboardingComplete(true)
                        // Save guest mode flag
                        onboardingDataStore.saveGuestModeEnabled(true)
                    }
                    navController.navigate(GuestDashboard) {
                        popUpTo(Welcome) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(AuthFlow)
                }
            )
        }
        /* ───────── ONBOARDING FLOW ───────── */
        composable<OnboardingFlow> {
            OnboardingPager(
                datastore = onboardingDataStore,
                onOnboardingComplete = {
                    // User skipped onboarding - mark complete AND save guest mode
                    coroutineScope.launch {
                        onboardingDataStore.setOnboardingComplete(true)
                        // ✅ IMPORTANT: Save guest mode flag
                        onboardingDataStore.saveGuestModeEnabled(true)
                    }
                    // Navigate to GuestDashboard, not Dashboard
                    navController.navigate(GuestDashboard) {
                        popUpTo(OnboardingFlow) { inclusive = true }
                    }
                },
                onSignupSuccess = { message, accessToken, refreshToken, user ->
                    coroutineScope.launch {
                        onboardingDataStore.setOnboardingComplete(true)
                        onboardingDataStore.saveTokens(accessToken, refreshToken)
                        onboardingDataStore.saveUserEmail(user?.email ?: "")
                        onboardingDataStore.clearOnboardingCache()
                        // ✅ Clear guest mode if it was set
                        onboardingDataStore.saveGuestModeEnabled(false)
                    }
                    sharedAuthViewModel.setSignupSuccessMessage(message)
                    if (user != null) {
                        sharedAuthViewModel.setUser(user)
                    }

                    navController.navigate(Dashboard) {
                        popUpTo(OnboardingFlow) { inclusive = true }
                    }
                },
                onLoginClick = {
                    navController.navigate(AuthFlow)
                }
            )
        }

        /* ───────── AUTH FLOW (NESTED) ───────── */
        navigation<AuthFlow>(startDestination = Login) {

            /* ───────── LOGIN SCREEN ───────── */
            composable<Login> {
                val resetSuccessMessage = sharedAuthViewModel.peekResetSuccessMessage()

                LoginScreen(
                    onLoginSuccess = { user, message, accessToken, refreshToken ->
                        sharedAuthViewModel.setLoginSuccessMessage(message)
                        sharedAuthViewModel.setUser(user)
                        sharedAuthViewModel.setUserTokens(accessToken, refreshToken)
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
                        sharedAuthViewModel.setResetSuccessMessage(successMessage)
                        navController.popBackStack(Login, inclusive = false)
                    }
                )
            }
        }

        /* ───────── GUEST DASHBOARD ───────── */
        composable<GuestDashboard> {
            DashboardScaffold(
                isGuestMode = true
            )
        }
        /* ───────── AUTHENTICATED DASHBOARD ───────── */
        composable<Dashboard> {
            val loginViewModel: LoginViewModel = hiltViewModel()

            val loginSuccessMessage = sharedAuthViewModel.peekLoginSuccessMessage()
            val signupSuccessMessage = sharedAuthViewModel.peekSignupSuccessMessage()
            var user by remember { mutableStateOf(sharedAuthViewModel.peekUser()) }
            var accessToken by remember { mutableStateOf(sharedAuthViewModel.peekAccessToken()) }
            var refreshToken by remember { mutableStateOf(sharedAuthViewModel.peekRefreshToken()) }

            // If user is null (app was killed), restore from database
            LaunchedEffect(Unit) {
                if (user == null) {
                    println("🔍 [NavHostSetup] No user in memory, restoring from database...")
                    val restoredUser = loginViewModel.getStoredUser()
                    val restoredAccessToken = loginViewModel.getAccessToken()
                    val restoredRefreshToken = loginViewModel.getRefreshToken()

                    if (restoredUser != null && restoredAccessToken != null && restoredRefreshToken != null) {
                        user = restoredUser
                        accessToken = restoredAccessToken
                        refreshToken = restoredRefreshToken
                        sharedAuthViewModel.setUser(restoredUser)
                        sharedAuthViewModel.setUserTokens(restoredAccessToken, restoredRefreshToken)
                        println("✅ [NavHostSetup] Session restored for: ${restoredUser.email}")
                    }
                }
            }

            val successMessage = signupSuccessMessage ?: loginSuccessMessage

            DashboardScaffold(
                isGuestMode = false,
                successMessage = successMessage,
                user = user,
                accessToken = accessToken,
                refreshToken = refreshToken,
                onMessageConsumed = {
                    sharedAuthViewModel.clearSignupSuccessMessage()
                    sharedAuthViewModel.clearLoginSuccessMessage()
                }
            )
        }

        /* ───────── BOOK SERVICE FLOW (NESTED) ───────── */
        navigation<BookServiceFlow>(startDestination = SelectService) {
            composable<SelectService> {
                SelectServiceScreen(
                    onBack = { navController.popBackStack() },
                    onContinue = { navController.navigate(ScheduleService) }
                )
            }
            composable<ScheduleService> {
                ScheduleServiceScreen(
                    onBack = { navController.popBackStack() },
                    onContinue = { navController.navigate(ServiceDetails) }
                )
            }
            composable<ServiceDetails> {
                ServiceDetailsScreen(
                    onBack = { navController.popBackStack() },
                    onContinue = { navController.navigate(ReviewAndPay) }
                )
            }
            composable<ReviewAndPay> {
                ReviewAndPayScreen(
                    onBack = { navController.popBackStack() },
                    onConfirm = {
                        navController.navigate(Dashboard) {
                            popUpTo(BookServiceFlow) { inclusive = true }
                        }
                    }
                )
            }
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