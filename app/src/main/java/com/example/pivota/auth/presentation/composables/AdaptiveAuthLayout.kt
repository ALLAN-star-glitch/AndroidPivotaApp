package com.example.pivota.auth.presentation.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.zIndex
import androidx.window.core.layout.WindowSizeClass
import com.example.pivota.R
import com.example.pivota.core.presentations.composables.background_image_and_overlay.BackgroundImageAndOverlay

@Composable
fun AdaptiveAuthLayout(
    desc1: String,
    desc2: String,
    isLoginScreen: Boolean,
    onRegisterClick: () -> Unit = {},
    onLoginClick: () -> Unit = {},
    onLoginSuccess: () -> Unit = {}, // Dashboard navigation
    onSuccess: (String) -> Unit = {}, // Registration/OTP navigation
    onForgotPasswordClick: () -> Unit = {},
    onGoogleLoginClick: () -> Unit = {}
) {
    Box(modifier = Modifier.fillMaxSize()) {
        val windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
        val isWide = windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) ||
                windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)

        /* ───────── STATIC IMAGE SELECTION ───────── */
        val staticAuthImage = if (isLoginScreen) R.drawable.nairobi_city else R.drawable.organization
        val authHeader = if (isLoginScreen) "Welcome Back" else "Join Pivota"

        if (isWide) {
            /* ───────── TWO PANE LAYOUT (Tablet/Desktop) ───────── */
            Row(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)) {
                    BackgroundImageAndOverlay(
                        isWideScreen = true,
                        header = authHeader,
                        desc1 = desc1,
                        desc2 = desc2,
                        showUpgradeButton = false,
                        enableCarousel = false, // Carousel disabled
                        image = staticAuthImage // Static image
                    )
                }

                Box(modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .background(Color.White)) {
                    AuthFormSwitcher(
                        isLoginScreen = isLoginScreen,
                        onRegisterClick = onRegisterClick,
                        onLoginClick = onLoginClick,
                        onLoginSuccess = onLoginSuccess,
                        onSuccess = onSuccess,
                        onForgotPasswordClick = onForgotPasswordClick,
                        onGoogleLoginClick = onGoogleLoginClick
                    )
                }
            }
        } else {
            /* ───────── SINGLE PANE LAYOUT (Mobile Overlay) ───────── */
            Box(modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface) // Solid professional background
                .statusBarsPadding()
                .navigationBarsPadding()
            ) {
                AuthFormSwitcher(
                    isLoginScreen = isLoginScreen,
                    onRegisterClick = onRegisterClick,
                    onLoginClick = onLoginClick,
                    onLoginSuccess = onLoginSuccess,
                    onSuccess = onSuccess,
                    onForgotPasswordClick = onForgotPasswordClick,
                    onGoogleLoginClick = onGoogleLoginClick
                )
            }
        }
    }
}

@Composable
private fun AuthFormSwitcher(
    isLoginScreen: Boolean,
    onRegisterClick: () -> Unit,
    onLoginClick: () -> Unit,
    onLoginSuccess: () -> Unit,
    onSuccess: (String) -> Unit,
    onForgotPasswordClick: () -> Unit,
    onGoogleLoginClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (isLoginScreen) {
            LoginFormContent(
                onLoginClick = { email, password ->
                    // Logic to perform login with email/password goes here
                    onLoginSuccess()
                },
                onGoogleLoginClick = onGoogleLoginClick,
                onRegisterLinkClick = onRegisterClick,
                onForgotPasswordClick = onForgotPasswordClick
            )
        } else {
            RegistrationFormContent(
                onRegisterSuccess = onSuccess,
                onLoginLinkClick = onLoginClick
            )
        }
    }
}