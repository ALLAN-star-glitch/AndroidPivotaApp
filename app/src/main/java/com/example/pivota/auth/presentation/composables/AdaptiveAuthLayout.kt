package com.example.pivota.auth.presentation.composables

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
    onNavigateToRegisterScreen: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {},
    onNavigateToDashboard: () -> Unit = {},
    onRegisterSuccess: () -> Unit = {}
) {
    Box {
        val windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
        val isMediumScreen = windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)
        val isExpandedScreen = windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)

        // Localized Images for Kenya / Pan-African context
        val authCarouselImages = listOf(
            R.drawable.nairobi_city,
            R.drawable.happy_people,
            R.drawable.mama_mboga,
            R.drawable.organization
        )

        // Contextual messages for the auth carousel
        val authCarouselMessages = listOf(
            "Access Opportunities in Kenya",
            "Your Trusted Life Partner",
            "Empowering the Community",
            "Verified & Secure"
        )

        when {
            /* TWO PANE LAYOUT (Tablet/Desktop) */
            isMediumScreen || isExpandedScreen -> {
                Row(modifier = Modifier.fillMaxSize()) {
                    // Left Pane: Image/Carousel
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f)
                            .background(Color.White)
                    ) {
                        BackgroundImageAndOverlay(
                            isWideScreen = true,
                            welcomeText = welcomeText,
                            desc1 = desc1,
                            desc2 = desc2,
                            offset = 200.dp,
                            showUpgradeButton = false,
                            imageHeight = 300.dp,
                            enableCarousel = true, // Enables the auto-scroll
                            images = authCarouselImages,
                            messages = authCarouselMessages
                        )
                    }

                    // Right Pane: Form
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f)
                            .background(Color.White)
                    ) {
                        if (isLoginScreen) {
                            LoginFormContent(
                                onNavigateToRegisterScreen = onNavigateToRegisterScreen,
                                onNavigateToDashboardScreen = onNavigateToDashboard
                            )
                        } else {
                            RegistrationFormContent(
                                onRegisterSuccess = onRegisterSuccess,
                                onNavigateToLoginScreen = onNavigateToLogin
                            )
                        }
                    }
                }
            }

            /* SINGLE PANE LAYOUT (Mobile) */
            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .zIndex(2f)
                    ) {
                        if (isLoginScreen) {
                            LoginFormContent(
                                onNavigateToRegisterScreen = onNavigateToRegisterScreen,
                                onNavigateToDashboardScreen = onNavigateToDashboard
                            )
                        } else {
                            RegistrationFormContent(
                                onRegisterSuccess = onRegisterSuccess,
                                onNavigateToLoginScreen = onNavigateToLogin
                            )
                        }
                    }
                }
            }
        }
    }
}