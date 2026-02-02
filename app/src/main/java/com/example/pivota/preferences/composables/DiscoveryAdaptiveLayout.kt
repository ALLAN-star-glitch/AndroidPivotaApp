package com.example.pivota.discovery.presentation.composables

import DiscoveryContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
fun DiscoveryAdaptiveLayout(
    header: String = "Discover Life Opportunities",
    welcomeText: String = "What are you looking for today?",
    onNavigateToDashboard: () -> Unit
) {
    Box {
        val windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
        val isMediumScreen = windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)
        val isExpandedScreen = windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)

        when {
            // TWO-PANE LAYOUT
            isMediumScreen || isExpandedScreen -> {
                Row(modifier = Modifier.fillMaxSize()) {

                    // Left Pane: Image
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f)
                            .background(Color.White)
                    ) {
                        BackgroundImageAndOverlay(
                            header = header,
                            isWideScreen = true,
                            showUpgradeButton = true, // Kept true as per your original
                            enableCarousel = true,
                            image = R.drawable.happy_client
                        )
                    }

                    // Right Pane: Form
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f)
                            .background(Color.White)
                    ) {
                        DiscoveryContent(
                            topPadding = 24.dp,
                            onContinue = onNavigateToDashboard
                        )
                    }
                }
            }

            // SINGLE-PANE LAYOUT
            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                ) {

                    BackgroundImageAndOverlay(
                        header = header,
                        isWideScreen = false,
                        showUpgradeButton = false,
                        enableCarousel = true,
                        image = R.drawable.happy_client
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .zIndex(2f)
                    ) {
                        DiscoveryContent(
                            // Pushes the discovery selection UI below the teal overlay area
                            topPadding = 450.dp,
                            onContinue = onNavigateToDashboard
                        )
                    }
                }
            }
        }
    }
}