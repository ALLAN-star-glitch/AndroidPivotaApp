package com.example.pivota.welcome.presentation.composables.welcome_content

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.pivota.R
import com.example.pivota.core.presentations.composables.buttons.PivotaPrimaryButton
import com.example.pivota.ui.theme.InfoBlue
import kotlinx.coroutines.delay

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun EnhancedWelcomeContent(
    header: String,
    welcomeText: String,
    onNavigateToContinueSetup: () -> Unit,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier,
    isCompact: Boolean = true,
    hasWhiteBackground: Boolean = false
) {
    var showContent by remember { mutableStateOf(false) }

    // Slower entrance animation
    LaunchedEffect(Unit) {
        delay(300)
        showContent = true
    }

    // Use appropriate colors based on background
    val textColor = if (hasWhiteBackground) {
        MaterialTheme.colorScheme.onSurface
    } else {
        Color.White
    }

    val secondaryTextColor = if (hasWhiteBackground) {
        MaterialTheme.colorScheme.onSurfaceVariant
    } else {
        Color.White.copy(alpha = 0.9f)
    }

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(
                horizontal = if (isCompact) 24.dp else 32.dp,
                vertical = if (isCompact) 32.dp else 48.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo animation - fade and scale
        AnimatedVisibility(
            visible = showContent,
            enter = fadeIn(
                animationSpec = tween(
                    durationMillis = 600,
                    easing = FastOutSlowInEasing
                )
            ) + scaleIn(
                initialScale = 0.9f,
                animationSpec = tween(
                    durationMillis = 600,
                    easing = FastOutSlowInEasing
                )
            )
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                AnimatedLogo()
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // Header animation - fade only (no vertical movement to avoid compression)
        AnimatedVisibility(
            visible = showContent,
            enter = fadeIn(
                animationSpec = tween(
                    durationMillis = 600,
                    delayMillis = 150,
                    easing = FastOutSlowInEasing
                )
            )
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = header,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = if (isCompact) 24.sp else 28.sp,
                        color = if (hasWhiteBackground) MaterialTheme.colorScheme.primary else Color.White
                    ),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        // Welcome text animation - fade only
        AnimatedVisibility(
            visible = showContent,
            enter = fadeIn(
                animationSpec = tween(
                    durationMillis = 600,
                    delayMillis = 300,
                    easing = FastOutSlowInEasing
                )
            )
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = welcomeText,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = if (isCompact) 14.sp else 16.sp,
                        color = secondaryTextColor,
                        lineHeight = 22.sp
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Button animation - fade only
        AnimatedVisibility(
            visible = showContent,
            enter = fadeIn(
                animationSpec = tween(
                    durationMillis = 600,
                    delayMillis = 500,
                    easing = FastOutSlowInEasing
                )
            )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                PivotaPrimaryButton(
                    text = "Get Started",
                    onClick = onNavigateToContinueSetup,
                    modifier = Modifier.fillMaxWidth(),
                    icon = ImageVector.vectorResource(R.drawable.ic_person)
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // Login link animation
        AnimatedVisibility(
            visible = showContent,
            enter = fadeIn(
                animationSpec = tween(
                    durationMillis = 600,
                    delayMillis = 650,
                    easing = FastOutSlowInEasing
                )
            )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Already have an account? ",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = secondaryTextColor
                        )
                    )
                    Text(
                        text = "Log in",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = InfoBlue
                        ),
                        modifier = Modifier.clickable { onNavigateToLogin() }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Footer links animation
        AnimatedVisibility(
            visible = showContent,
            enter = fadeIn(
                animationSpec = tween(
                    durationMillis = 600,
                    delayMillis = 800,
                    easing = FastOutSlowInEasing
                )
            )
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Terms of Service",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = secondaryTextColor.copy(alpha = 0.7f)
                    ),
                    modifier = Modifier.clickable { /* Navigate to Terms */ }
                )
                Text(
                    text = " • ",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = secondaryTextColor.copy(alpha = 0.4f)
                    )
                )
                Text(
                    text = "Privacy Policy",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = secondaryTextColor.copy(alpha = 0.7f)
                    ),
                    modifier = Modifier.clickable { /* Navigate to Privacy */ }
                )
            }
        }
    }
}

@Composable
fun AnimatedLogo() {
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 3000,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier
            .size(120.dp * scale)
            .clip(RoundedCornerShape(60.dp))
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
    ) {
        AsyncImage(
            model = R.drawable.transparentpivlogo,
            contentDescription = "PivotaConnect Logo",
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        )
    }
}