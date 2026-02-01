package com.example.pivota.auth.presentation.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pivota.R
import com.example.pivota.auth.presentation.viewModel.SplashViewModel
import kotlinx.coroutines.delay

/**
 * 1. STATEFUL: Use this in your NavHost.
 * This handles the ViewModel and Navigation logic.
 */
@Composable
fun SplashScreen(
    viewModel: SplashViewModel,
    onNavigate: (Any) -> Unit
) {
    val startDestination by viewModel.startDestination.collectAsState()

    LaunchedEffect(startDestination) {
        if (startDestination != null) {
            delay(2200) // Ensure the brand animation finishes
            onNavigate(startDestination!!)
        }
    }

    SplashContent()
}

/**
 * 2. STATELESS: Use this for Previews and clean UI code.
 * This contains purely the animation and layout logic.
 */
@Composable
fun SplashContent() {
    val foundationGrey = Color(0xFFF6FAF9)
    val pivotaTeal = Color(0xFF006565)
    val opportunityGold = Color(0xFFE9C16C)

    var startAnimation by remember { mutableStateOf(false) }
    var showTagline by remember { mutableStateOf(false) }

    val logoAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(600, easing = EaseOut), label = "logo_alpha"
    )
    val logoScale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.95f,
        animationSpec = tween(600, easing = EaseOut), label = "logo_scale"
    )

    val pulseAnim = rememberInfiniteTransition(label = "connection_pulse")
    val pulseScale by pulseAnim.animateFloat(
        initialValue = 1f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ), label = "pulse_scale"
    )
    val pulseAlpha by pulseAnim.animateFloat(
        initialValue = 0.2f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ), label = "pulse_alpha"
    )

    val taglineAlpha by animateFloatAsState(
        targetValue = if (showTagline) 1f else 0f,
        animationSpec = tween(600), label = "tagline_fade"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(800)
        showTagline = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(foundationGrey),
        contentAlignment = Alignment.Center
    ) {
        if (startAnimation) {
            Canvas(modifier = Modifier.size(150.dp)) {
                drawCircle(
                    color = opportunityGold,
                    radius = (size.minDimension / 2) * pulseScale,
                    alpha = pulseAlpha,
                    style = Stroke(width = 1.5.dp.toPx())
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "PivotaConnect Logo",
                modifier = Modifier
                    .size(140.dp)
                    .scale(logoScale)
                    .alpha(logoAlpha)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Connecting life opportunities",
                color = pivotaTeal,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.5.sp,
                modifier = Modifier.alpha(taglineAlpha)
            )
        }
    }
}

/**
 * 3. PREVIEW: Top-level declaration.
 */
@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    SplashContent()
}