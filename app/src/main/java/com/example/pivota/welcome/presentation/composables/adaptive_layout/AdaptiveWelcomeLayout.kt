package com.example.pivota.welcome.presentation.composables.adaptive_layout

import WelcomeContent
import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.window.core.layout.WindowSizeClass
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import com.example.pivota.R
import com.example.pivota.core.presentations.composables.background_image_and_overlay.BackgroundImageAndOverlay
import com.example.pivota.core.presentations.composables.buttons.PivotaPrimaryButton
import com.example.pivota.core.presentations.composables.buttons.PivotaSecondaryButton

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

        val carouselImages = listOf(
            R.drawable.happypeople,
            R.drawable.nairobi_city,
            R.drawable.mama_mboga,
            R.drawable.organizationpic
        )

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
                    // Left pane with image carousel
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f)
                            .background(Color.White)
                    ) {
                        BackgroundImageAndOverlay(
                            isWideScreen = true,
                            showUpgradeButton = false,
                            enableCarousel = true,
                            images = carouselImages,
                            messages = carouselMessages
                        )
                    }

                    // Right pane - completely redesigned for proper centering
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f)
                            .background(Color.White)
                    ) {
                        TwoPaneWelcomeContent(
                            header = header,
                            welcomeText = welcomeText,
                            onNavigateToRegistrationScreen = onNavigateToGetStarted,
                            onNavigateToLoginScreen = onNavigateToLoginScreen
                        )
                    }
                }
            }

            /* SINGLE-PANE LAYOUT FOR MOBILE */
            else -> {
                val configuration = androidx.compose.ui.platform.LocalConfiguration.current
                val screenHeight = configuration.screenHeightDp.dp
                val dynamicTopPadding = screenHeight * 0.45f

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                ) {
                    BackgroundImageAndOverlay(
                        isWideScreen = false,
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

@Composable
fun TwoPaneWelcomeContent(
    header: String,
    welcomeText: String,
    onNavigateToRegistrationScreen: () -> Unit,
    onNavigateToLoginScreen: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo
        coil3.compose.AsyncImage(
            model = com.example.pivota.R.drawable.logofinale,
            contentDescription = "Pivota Logo",
            modifier = Modifier.size(150.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Header
        Text(
            text = header,
            style = MaterialTheme.typography.headlineMedium.copy(
                color = MaterialTheme.colorScheme.primary,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Welcome text
        Text(
            text = welcomeText,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Get Started button
        PivotaPrimaryButton(
            text = "Get Started",
            onClick = onNavigateToRegistrationScreen,
            modifier = Modifier.fillMaxWidth(0.7f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Divider with OR
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(0.6f)
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f), thickness = 1.dp, color = Color.LightGray)
            Text(
                text = " OR ",
                modifier = Modifier.padding(horizontal = 8.dp),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            HorizontalDivider(modifier = Modifier.weight(1f), thickness = 1.dp, color = Color.LightGray)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Login button
        PivotaSecondaryButton(
            text = "Login",
            onclick = onNavigateToLoginScreen,
            modifier = Modifier.fillMaxWidth(0.7f)
        )
    }
}