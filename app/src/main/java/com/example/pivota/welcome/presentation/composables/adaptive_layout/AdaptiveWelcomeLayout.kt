package com.example.pivota.welcome.presentation.composables.adaptive_layout

import WelcomeContent
import android.annotation.SuppressLint
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

@SuppressLint("ConfigurationScreenWidthHeight")
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
            R.drawable.happypeople,
            R.drawable.nairobi_city,
            R.drawable.mama_mboga,         // Local mwananchi / market vendor
            R.drawable.organizationpic // Professional corporate setting
        )

        // Unified list of messages that update with each image
        val carouselMessages = listOf(
            "Connect, Discover, Grow",
            "Built for Kenya. Ready for Africa",
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
                            topPadding = 0.dp,
                            onNavigateToRegistrationScreen = onNavigateToGetStarted,
                            onNavigateToLoginScreen = onNavigateToLoginScreen
                        )
                    }
                }
            }

            /* SINGLE-PANE LAYOUT FOR MOBILE */
            else -> {
                // Get the screen height
                val configuration = androidx.compose.ui.platform.LocalConfiguration.current
                val screenHeight = configuration.screenHeightDp.dp

                // Calculate padding (e.g., 40% of the screen height)
                val dynamicTopPadding = screenHeight * 0.45f
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


                    WelcomeContent(
                        header = header,
                        welcomeText = welcomeText,
                        topPadding = dynamicTopPadding,
                        onNavigateToRegistrationScreen = onNavigateToGetStarted,
                        onNavigateToLoginScreen = onNavigateToLoginScreen
                    )

                }
            }
        }
    }
}