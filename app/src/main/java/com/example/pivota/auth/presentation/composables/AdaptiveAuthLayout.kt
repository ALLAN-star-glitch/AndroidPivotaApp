package com.example.pivota.auth.presentation.composables

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.window.core.layout.WindowSizeClass
import com.example.pivota.R
import com.example.pivota.auth.domain.model.User
import com.example.pivota.auth.presentation.viewModel.SignupViewModel
import com.example.pivota.core.presentations.composables.background_image_and_overlay.BackgroundImageAndOverlay

@Composable
fun AdaptiveAuthLayout(
    desc1: String,
    desc2: String,
    isLoginScreen: Boolean,
    viewModel: SignupViewModel? = null,
    onRegisterClick: () -> Unit = {},
    onLoginClick: () -> Unit = {},
    onLoginSuccess: (User, String, String, String) -> Unit = { _, _, _, _ -> }, // New: 4 parameters for login
    onRegisterSuccess: ( String) -> Unit = {},
    onForgotPasswordClick: () -> Unit = {},
    onGoogleLoginClick: () -> Unit = {},
    successMessage: String? = null
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
                        enableCarousel = false,
                        image = staticAuthImage
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
                        onRegisterSuccess = onRegisterSuccess,
                        onForgotPasswordClick = onForgotPasswordClick,
                        onGoogleLoginClick = onGoogleLoginClick,
                        successMessage = successMessage,
                        viewModel = viewModel
                    )
                }
            }
        } else {
            /* ───────── SINGLE PANE LAYOUT (Mobile Overlay) ───────── */
            Box(modifier = Modifier
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
                    viewModel = viewModel,
                    successMessage = successMessage
                )
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
    onLoginSuccess: (User, String, String, String) -> Unit, // 4 parameters for login
    onRegisterSuccess: (String) -> Unit,
    onForgotPasswordClick: () -> Unit,
    onGoogleLoginClick: () -> Unit,
    successMessage: String? = null
) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (isLoginScreen) {
            LoginFormContent(
                onLoginSuccess = onLoginSuccess, // Now passes 4 parameters
                onGoogleLoginClick = onGoogleLoginClick,
                onRegisterLinkClick = onRegisterClick,
                onForgotPasswordClick = onForgotPasswordClick,
                successMessage = successMessage
            )
        } else {
            // Only call if viewModel is not null - Registration uses 2 parameters
            viewModel?.let { vm ->
                RegistrationFormContent(
                    viewModel = vm,
                    onRegisterSuccess = onRegisterSuccess, // Still 2 parameters
                    onLoginLinkClick = onLoginClick
                )
            }
        }
    }
}