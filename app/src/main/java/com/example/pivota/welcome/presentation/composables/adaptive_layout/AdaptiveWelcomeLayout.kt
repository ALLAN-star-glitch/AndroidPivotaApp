package com.example.pivota.welcome.presentation.composables.adaptive_layout

import WelcomeContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.window.core.layout.WindowSizeClass
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import com.example.pivota.R
import com.example.pivota.core.presentations.composables.background_image_and_overlay.BackgroundImageAndOverlay

@Composable
fun AdaptiveWelcomeLayout(
    header: String,
    welcomeText: String,
    onNavigateToGetStarted: () -> Unit,
    onNavigateToLoginScreen: () -> Unit
) {
    Box {
        val windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
        val isMediumScreen = windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)
        val isExpandedScreen = windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)

        // Unified list of localized images for the carousel
        val carouselImages = listOf(
            R.drawable.nairobi_city,    // Image of Nairobi City / CBD
            R.drawable.happy_people,       // Diverse happy Kenyans
            R.drawable.mama_mboga,         // Local mwananchi / market vendor
            R.drawable.organization // Professional corporate setting
        )

        // Unified list of messages that update with each image
        val carouselMessages = listOf(
            "Built for Kenya, Ready for Africa",
            "Connecting People with Purpose",
            "Empowering Every Mwananchi",
            "Trusted by Leading Organizations"
        )

        when {
            /* TWO-PANE LAYOUT FOR TABLETS/DESKTOP */
            isMediumScreen || isExpandedScreen -> {
                Row(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f)
                            .background(Color.White)
                    ) {
                        BackgroundImageAndOverlay(
                            isWideScreen = true,
                            // Removed fixed offset and imageHeight
                            showUpgradeButton = false,
                            enableCarousel = true,
                            images = carouselImages,
                            messages = carouselMessages
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f)
                            .background(Color.White)
                    ) {
                        WelcomeContent(
                            header = header,
                            welcomeText = welcomeText,
                            topPadding = 24.dp,
                            onNavigateToRegistrationScreen = onNavigateToGetStarted,
                            onNavigateToLoginScreen = onNavigateToLoginScreen
                        )
                    }
                }
            }

            /* SINGLE-PANE LAYOUT FOR MOBILE */
            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                ) {
                    BackgroundImageAndOverlay(
                        isWideScreen = false,
                        // Removed fixed offset and imageHeight
                        showUpgradeButton = false,
                        enableCarousel = true,
                        images = carouselImages,
                        messages = carouselMessages
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .zIndex(2f)
                    ) {
                        WelcomeContent(
                            header = header,
                            welcomeText = welcomeText,
                            topPadding = 350.dp,
                            onNavigateToRegistrationScreen = onNavigateToGetStarted,
                            onNavigateToLoginScreen = onNavigateToLoginScreen
                        )
                    }
                }
            }
        }
    }
}