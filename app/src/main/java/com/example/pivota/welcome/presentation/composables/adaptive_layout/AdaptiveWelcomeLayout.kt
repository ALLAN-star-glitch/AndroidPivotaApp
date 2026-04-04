package com.example.pivota.welcome.presentation.composables.adaptive_layout


import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.pivota.R
import com.example.pivota.core.presentations.composables.background_image_and_overlay.BackgroundImageAndOverlay
import com.example.pivota.core.presentations.composables.buttons.AuthGoogleButton
import com.example.pivota.core.presentations.composables.buttons.PivotaPrimaryButton
import com.example.pivota.ui.theme.InfoBlue
import com.example.pivota.welcome.presentation.composables.welcome_content.WelcomeContent

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun AdaptiveWelcomeLayout(
    header: String,
    welcomeText: String,
    onNavigateToContinueSetup: () -> Unit,
    onNavigateToContinueWithGoogle: () -> Unit,
    onNavigateToLogin: () -> Unit,
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

                    // Right pane with all four buttons
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f)
                            .background(Color.White)
                    ) {
                        TwoPaneWelcomeContent(
                            header = header,
                            welcomeText = welcomeText,
                            onNavigateToContinueSetup = onNavigateToContinueSetup,
                            onNavigateToLogin = onNavigateToLogin
                        )
                    }
                }
            }

            /* SINGLE-PANE LAYOUT FOR MOBILE */
            else -> {
                val configuration = androidx.compose.ui.platform.LocalConfiguration.current
                val screenHeight = configuration.screenHeightDp.dp
                val dynamicTopPadding = screenHeight * 0.42f

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
                        onNavigateToContinueSetup = onNavigateToContinueSetup,
                        onNavigateToLogin = onNavigateToLogin
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
    onNavigateToContinueSetup: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 48.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo
        coil3.compose.AsyncImage(
            model = R.drawable.transparentpivlogo,
            contentDescription = "PivotaConnect Logo",
            modifier = Modifier.size(120.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Header
        Text(
            text = header,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                color = MaterialTheme.colorScheme.primary,
                lineHeight = 36.sp
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Welcome text
        Text(
            text = welcomeText,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 22.sp
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // All Buttons
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // Get Started Button
            PivotaPrimaryButton(
                text = "Get Started",
                onClick = onNavigateToContinueSetup,
                modifier = Modifier.fillMaxWidth(),
                icon = ImageVector.vectorResource(R.drawable.ic_person)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Login Text (Clickable)
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Already have an account? ",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            Text(
                text = "Log in",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = InfoBlue
                ),
                modifier = Modifier.clickable { onNavigateToLogin() }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Footer Links
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Terms of Service",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.clickable { /* Navigate to Terms */ }
            )
            Text(
                text = " • ",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.outlineVariant
                )
            )
            Text(
                text = "Privacy Policy",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.clickable { /* Navigate to Privacy */ }
            )
        }
    }
}