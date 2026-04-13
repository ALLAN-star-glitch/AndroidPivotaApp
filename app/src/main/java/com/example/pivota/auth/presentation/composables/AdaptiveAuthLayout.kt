package com.example.pivota.auth.presentation.composables

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.window.core.layout.WindowSizeClass
import com.example.pivota.R
import com.example.pivota.auth.domain.model.User
import com.example.pivota.auth.presentation.viewModel.SignupViewModel
import com.example.pivota.core.presentations.composables.background_image_and_overlay.BackgroundImageAndOverlay
import kotlinx.coroutines.delay

@Composable
fun AdaptiveAuthLayout(
    desc1: String,
    desc2: String,
    isLoginScreen: Boolean,
    viewModel: SignupViewModel? = null,
    onRegisterClick: () -> Unit = {},
    onLoginClick: () -> Unit = {},
    onLoginSuccess: (User, String, String, String) -> Unit = { _, _, _, _ -> },
    onRegisterSuccess: (String, String, String, User?) -> Unit = { _, _, _, _ -> },
    onForgotPasswordClick: () -> Unit = {},
    onGoogleLoginClick: () -> Unit = {},
    onGoogleSignUpClick: () -> Unit = {},
    successMessage: String? = null
) {
    var showContent by remember { mutableStateOf(false) }

    // Animate content entrance for mobile only
    LaunchedEffect(Unit) {
        delay(300)
        showContent = true
    }

    Box(modifier = Modifier.fillMaxSize()) {
        val windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
        val isWide = windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) ||
                windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)

        /* ───────── STATIC IMAGE SELECTION ───────── */
        val staticAuthImage = if (isLoginScreen) R.drawable.nairobi_city else R.drawable.organization
        val authHeader = if (isLoginScreen) "Welcome Back" else "Join Pivota"

        if (isWide) {
            /* ───────── TWO PANE LAYOUT (Tablet/Desktop) - NO ANIMATIONS ───────── */
            Row(modifier = Modifier.fillMaxSize()) {
                // Left pane - Image
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                ) {
                    BackgroundImageAndOverlay(
                        isWideScreen = true,
                        header = authHeader,
                        desc1 = desc1,
                        desc2 = desc2,
                        showUpgradeButton = false,
                        enableCarousel = false,
                        image = staticAuthImage
                    )
                }

                // Right pane - Form
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    AuthFormSwitcher(
                        isLoginScreen = isLoginScreen,
                        onRegisterClick = onRegisterClick,
                        onLoginClick = onLoginClick,
                        onLoginSuccess = onLoginSuccess,
                        onRegisterSuccess = onRegisterSuccess,
                        onForgotPasswordClick = onForgotPasswordClick,
                        onGoogleLoginClick = onGoogleLoginClick,
                        onGoogleSignUpClick = onGoogleSignUpClick,
                        successMessage = successMessage,
                        viewModel = viewModel
                    )
                }
            }
        } else {
            /* ───────── SINGLE PANE LAYOUT (Mobile Overlay) - WITH ANIMATIONS ───────── */
            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn(animationSpec = tween(600, easing = FastOutSlowInEasing)) +
                        slideInVertically(
                            initialOffsetY = { 50 },
                            animationSpec = tween(600, easing = FastOutSlowInEasing)
                        )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface)
                        .statusBarsPadding()
                        .navigationBarsPadding()
                        .imePadding()
                ) {
                    AuthFormSwitcher(
                        isLoginScreen = isLoginScreen,
                        onRegisterClick = onRegisterClick,
                        onLoginClick = onLoginClick,
                        onLoginSuccess = onLoginSuccess,
                        onRegisterSuccess = onRegisterSuccess,
                        onForgotPasswordClick = onForgotPasswordClick,
                        onGoogleLoginClick = onGoogleLoginClick,
                        onGoogleSignUpClick = onGoogleSignUpClick,
                        viewModel = viewModel,
                        successMessage = successMessage
                    )
                }
            }
        }
    }
}

@Composable
private fun AuthFormSwitcher(
    isLoginScreen: Boolean,
    viewModel: SignupViewModel?,
    onRegisterClick: () -> Unit,
    onLoginClick: () -> Unit,
    onLoginSuccess: (User, String, String, String) -> Unit,
    onRegisterSuccess: (String, String, String, User?) -> Unit,
    onForgotPasswordClick: () -> Unit,
    onGoogleLoginClick: () -> Unit,
    onGoogleSignUpClick: () -> Unit = {},
    successMessage: String? = null
) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (isLoginScreen) {
            LoginFormContent(
                onLoginSuccess = onLoginSuccess,
                onRegisterLinkClick = onRegisterClick,
                onForgotPasswordClick = onForgotPasswordClick,
                successMessage = successMessage
            )
        } else {
            // Only call if viewModel is not null
            viewModel?.let { vm ->
                RegistrationFormContent(
                    viewModel = vm,
                    onRegisterSuccess = onRegisterSuccess,
                    onLoginLinkClick = onLoginClick
                )
            }
        }
    }
}