package com.example.pivota.auth.presentation.composables

import LoginFormContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.window.core.layout.WindowSizeClass
import com.example.pivota.R
import com.example.pivota.core.presentations.composables.background_image_and_overlay.BackgroundImageAndOverlay

@Composable
fun AdaptiveAuthLayout(
    welcomeText: String,
    desc1: String,
    desc2: String,
    isLoginScreen: Boolean,
    onRegisterClick: () -> Unit = {},
    onLoginClick: () -> Unit = {},
    onDashboardNavigate: () -> Unit = {},
    onSuccess: (String) -> Unit = {}
) {
    Box {
        val windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
        val isMediumScreen = windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)
        val isExpandedScreen = windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)
        val isWide = isMediumScreen || isExpandedScreen

        val authCarouselImages = listOf(
            R.drawable.nairobi_city, R.drawable.happy_people,
            R.drawable.mama_mboga, R.drawable.organization
        )

        val authCarouselMessages = listOf(
            "Access Opportunities in Kenya", "Your Trusted Life Partner",
            "Empowering the Community", "Verified & Secure"
        )

        if (isWide) {
            /* ───────── TWO PANE LAYOUT (Tablet/Desktop) ───────── */
            Row(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.fillMaxHeight().weight(1f).background(Color.White)) {
                    BackgroundImageAndOverlay(
                        isWideScreen = true,
                        welcomeText = welcomeText,
                        desc1 = desc1,
                        desc2 = desc2,
                        offset = 200.dp,
                        showUpgradeButton = false,
                        imageHeight = 300.dp,
                        enableCarousel = true,
                        images = authCarouselImages,
                        messages = authCarouselMessages
                    )
                }

                Box(modifier = Modifier.fillMaxHeight().weight(1f).background(Color.White)) {
                    AuthFormSwitcher(
                        isLoginScreen = isLoginScreen,
                        isWideScreen = true,
                        onRegisterClick = onRegisterClick,
                        onLoginClick = onLoginClick,
                        onDashboardNavigate = onDashboardNavigate,
                        onSuccess = onSuccess
                    )
                }
            }
        } else {
            /* ───────── SINGLE PANE LAYOUT (Mobile) ───────── */
            Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
                BackgroundImageAndOverlay(
                    isWideScreen = false,
                    header = header,
                    offset = if (isLoginScreen) 350.dp else 180.dp,
                    showUpgradeButton = false,
                    imageHeight = if (isLoginScreen) 600.dp else 300.dp,
                    enableCarousel = true,
                    images = authCarouselImages,
                    messages = authCarouselMessages
                )

                Box(modifier = Modifier.fillMaxSize().zIndex(2f)) {
                    AuthFormSwitcher(
                        isLoginScreen = isLoginScreen,
                        isWideScreen = false,
                        onRegisterClick = onRegisterClick,
                        onLoginClick = onLoginClick,
                        onDashboardNavigate = onDashboardNavigate,
                        onSuccess = onSuccess
                    )
                }
            }
        }
    }
}

@Composable
private fun AuthFormSwitcher(
    isLoginScreen: Boolean,
    isWideScreen: Boolean,
    onRegisterClick: () -> Unit,
    onLoginClick: () -> Unit,
    onDashboardNavigate: () -> Unit,
    onSuccess: (String) -> Unit
) {
    if (isLoginScreen) {
        LoginFormContent(
            topPadding = if (isWideScreen) 64.dp else 450.dp,
            showHeader = true,
            isWideScreen = isWideScreen,
            onRegisterClick = onRegisterClick,   // 👈 Corrected mapping
            onLoginSuccess = onDashboardNavigate // 👈 Corrected mapping
        )
    } else {
        RegistrationFormContent(
            topPadding = if (isWideScreen) 64.dp else 240.dp,
            showHeader = true,
            isWideScreen = isWideScreen,
            onSuccess = onSuccess,
            onLoginClick = onLoginClick
        )
    }
}