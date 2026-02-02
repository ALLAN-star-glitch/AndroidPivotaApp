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

    // Set startAnimation to TRUE immediately to avoid the "first-frame lag"
    var startAnimation by remember { mutableStateOf(true) }
    var showTagline by remember { mutableStateOf(false) }

    // Logo animation: Entrance starts instantly on composition
    val logoAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(600, easing = EaseOut),
        label = "logo_alpha"
    )

    val logoScale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.92f,
        animationSpec = tween(700, easing = EaseOut),
        label = "logo_scale"
    )

    // Infinite pulse logic stays the same...
    val infiniteTransition = rememberInfiniteTransition(label = "wave_pulse")
    val waveScale by infiniteTransition.animateFloat(
        initialValue = 0.85f, targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ), label = "wave_scale"
    )
    val waveAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f, targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "wave_alpha"
    )

    val taglineAlpha by animateFloatAsState(
        targetValue = if (showTagline) 1f else 0f,
        animationSpec = tween(600),
        label = "tagline_alpha"
    )

    // Only handle the delayed appearance of the tagline here
    LaunchedEffect(Unit) {
        delay(600) // Wait for logo entrance to nearly finish
        showTagline = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = androidx.compose.ui.graphics.Brush.radialGradient(
                    colors = listOf(Color.White, foundationGrey),
                    radius = 900f
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // ===== WAVES =====
        Canvas(modifier = Modifier.size(220.dp)) {
            val baseRadius = size.minDimension / 2
            drawCircle(
                color = opportunityGold,
                radius = baseRadius * waveScale,
                alpha = waveAlpha,
                style = Stroke(width = 4.dp.toPx())
            )
            drawCircle(
                color = pivotaTeal,
                radius = baseRadius * (waveScale * 0.75f),
                alpha = waveAlpha * 0.6f,
                style = Stroke(width = 3.dp.toPx())
            )
        }

        // ===== LOGO + TEXT =====
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "PivotaConnect Logo",
                modifier = Modifier
                    .size(140.dp)
                    .scale(logoScale)
                    .alpha(logoAlpha) // This will now start animating from frame 1
            )

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Connecting life opportunities",
                color = pivotaTeal,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.6.sp,
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