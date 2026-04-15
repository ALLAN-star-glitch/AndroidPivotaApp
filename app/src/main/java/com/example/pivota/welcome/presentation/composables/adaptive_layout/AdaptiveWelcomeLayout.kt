package com.example.pivota.welcome.presentation.composables.adaptive_layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.example.pivota.core.presentations.composables.background_image_and_overlay.EnhancedBackgroundImage
import com.example.pivota.welcome.presentation.composables.welcome_content.EnhancedWelcomeContent

@Composable
fun AdaptiveWelcomeLayout(
    header: String,
    welcomeText: String,
    onNavigateToContinueSetup: () -> Unit,
    onNavigateToContinueWithGoogle: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600
    val isLandscape =
        configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    // Carousel messages for each image
    val carouselMessages = listOf(
        "Find your dream job with verified employers across Africa",
        "Discover quality housing and accommodation options",
        "Find Trusted Professionals",
        "Access essential services and community support"
    )

    val backgroundImages = listOf(
        com.example.pivota.R.drawable.find_job,
        com.example.pivota.R.drawable.found_property,
        com.example.pivota.R.drawable.trusted_professional,
        com.example.pivota.R.drawable.find_support
    )

    when {
        isTablet && !isLandscape -> TabletPortraitWelcomeLayout(
            header = header,
            welcomeText = welcomeText,
            onNavigateToContinueSetup = onNavigateToContinueSetup,
            onNavigateToContinueWithGoogle = onNavigateToContinueWithGoogle,
            onNavigateToLogin = onNavigateToLogin,
            backgroundImages = backgroundImages,
            carouselMessages = carouselMessages
        )

        isLandscape -> LandscapeWelcomeLayout(
            header = header,
            welcomeText = welcomeText,
            onNavigateToContinueSetup = onNavigateToContinueSetup,
            onNavigateToContinueWithGoogle = onNavigateToContinueWithGoogle,
            onNavigateToLogin = onNavigateToLogin,
            backgroundImages = backgroundImages,
            carouselMessages = carouselMessages
        )

        else -> MobilePortraitWelcomeLayout(
            header = header,
            welcomeText = welcomeText,
            onNavigateToContinueSetup = onNavigateToContinueSetup,
            onNavigateToContinueWithGoogle = onNavigateToContinueWithGoogle,
            onNavigateToLogin = onNavigateToLogin,
            backgroundImages = backgroundImages,
            carouselMessages = carouselMessages
        )
    }
}

// MOBILE PORTRAIT - White card CENTERED with opacity
@Composable
private fun MobilePortraitWelcomeLayout(
    header: String,
    welcomeText: String,
    onNavigateToContinueSetup: () -> Unit,
    onNavigateToContinueWithGoogle: () -> Unit,
    onNavigateToLogin: () -> Unit,
    backgroundImages: List<Int>,
    carouselMessages: List<String>
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Full screen background with carousel
        EnhancedBackgroundImage(
            images = backgroundImages,
            carouselMessages = carouselMessages,
            enableCarousel = true,
            overlayOpacity = 0.55f
        )

        // Centered white content card with opacity
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(32.dp))
                    .background(
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.92f) // 92% opacity (slightly transparent)
                    )
            ) {
                EnhancedWelcomeContent(
                    header = header,
                    welcomeText = welcomeText,
                    onNavigateToContinueSetup = onNavigateToContinueSetup,
                    onNavigateToLogin = onNavigateToLogin,
                    modifier = Modifier.fillMaxWidth(),
                    isCompact = true,
                    hasWhiteBackground = true
                )
            }
        }
    }
}

// TABLET PORTRAIT - White card CENTERED with opacity
@Composable
private fun TabletPortraitWelcomeLayout(
    header: String,
    welcomeText: String,
    onNavigateToContinueSetup: () -> Unit,
    onNavigateToContinueWithGoogle: () -> Unit,
    onNavigateToLogin: () -> Unit,
    backgroundImages: List<Int>,
    carouselMessages: List<String>
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Full screen background with carousel
        EnhancedBackgroundImage(
            images = backgroundImages,
            carouselMessages = carouselMessages,
            enableCarousel = true,
            overlayOpacity = 0.55f
        )

        // Centered white content card with opacity
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(48.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .clip(RoundedCornerShape(32.dp))
                    .background(
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.92f) // 92% opacity (slightly transparent)
                    )
            ) {
                EnhancedWelcomeContent(
                    header = header,
                    welcomeText = welcomeText,
                    onNavigateToContinueSetup = onNavigateToContinueSetup,
                    onNavigateToLogin = onNavigateToLogin,
                    modifier = Modifier.fillMaxWidth(),
                    isCompact = false,
                    hasWhiteBackground = true
                )
            }
        }
    }
}

// LANDSCAPE - Split screen with white card on right (with opacity)
@Composable
private fun LandscapeWelcomeLayout(
    header: String,
    welcomeText: String,
    onNavigateToContinueSetup: () -> Unit,
    onNavigateToContinueWithGoogle: () -> Unit,
    onNavigateToLogin: () -> Unit,
    backgroundImages: List<Int>,
    carouselMessages: List<String>
) {
    Row(modifier = Modifier.fillMaxSize()) {
        // Left side - Full image with carousel
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            EnhancedBackgroundImage(
                images = backgroundImages,
                carouselMessages = carouselMessages,
                enableCarousel = true,
                overlayOpacity = 0.6f
            )
        }

        // Right side - White content card with opacity
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(
                    MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
                )
        ) {
            EnhancedWelcomeContent(
                header = header,
                welcomeText = welcomeText,
                onNavigateToContinueSetup = onNavigateToContinueSetup,
                onNavigateToLogin = onNavigateToLogin,
                modifier = Modifier.fillMaxSize(),
                isCompact = false,
                hasWhiteBackground = true
            )
        }
    }
}