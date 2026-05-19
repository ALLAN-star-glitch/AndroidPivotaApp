package com.example.pivota.auth.presentation.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pivota.R
import com.example.pivota.auth.presentation.viewModel.SplashViewModel
import com.example.pivota.ui.theme.PivotaConnectTheme
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

/**
 * 1. STATEFUL: Use this in your NavHost.
 */
@Composable
fun SplashScreen(
    viewModel: SplashViewModel,
    onNavigate: (Any) -> Unit
) {
    val startDestination by viewModel.startDestination.collectAsState()

    LaunchedEffect(startDestination) {
        if (startDestination != null) {
            delay(2200)
            onNavigate(startDestination!!)
        }
    }

    SplashContent()
}

/**
 * 2. STATELESS: Nodes orbit AROUND the logo with NO OVERLAP
 */
@Composable
fun SplashContent() {
    val primaryColor = MaterialTheme.colorScheme.primary      // African Sapphire #1B4B6C
    val secondaryColor = MaterialTheme.colorScheme.secondary  // Warm Terracotta #C95D3A
    val tertiaryColor = MaterialTheme.colorScheme.tertiary    // Baobab Gold #E6B422
    val surfaceColor = MaterialTheme.colorScheme.surface
    val taglineColor = MaterialTheme.colorScheme.onSurfaceVariant

    var startAnimation by remember { mutableStateOf(true) }
    var showTagline by remember { mutableStateOf(false) }

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

    val taglineAlpha by animateFloatAsState(
        targetValue = if (showTagline) 1f else 0f,
        animationSpec = tween(600),
        label = "tagline_alpha"
    )

    LaunchedEffect(Unit) {
        delay(600)
        showTagline = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(Color.White, surfaceColor),
                    radius = 900f
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo + Nodes container (nodes orbit OUTSIDE the logo)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(280.dp) // Increased to give nodes room
                    .wrapContentSize(Alignment.Center)
            ) {
                // Nodes orbiting around the logo (larger canvas, wider orbit)
                OrbitingNodes(
                    primaryColor = primaryColor,
                    secondaryColor = secondaryColor,
                    tertiaryColor = tertiaryColor,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                )

                // Logo in center (protected from overlap by orbit radius)
                Image(
                    painter = painterResource(id = R.drawable.pivota_logo_transparent),
                    contentDescription = "PivotaConnect Logo",
                    modifier = Modifier
                        .size(120.dp) // Slightly smaller to ensure clearance
                        .scale(logoScale)
                        .alpha(logoAlpha)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Tagline - well below nodes
            Text(
                text = "Find your way forward.",
                color = taglineColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.5.sp,
                modifier = Modifier.alpha(taglineAlpha)
            )
        }
    }
}

/**
 * ORBITING NODES: Three nodes positioned around the logo in a triangle formation
 * Uses WIDER orbit radius to prevent overlap with logo
 */
@Composable
fun OrbitingNodes(
    primaryColor: Color,
    secondaryColor: Color,
    tertiaryColor: Color,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "orbiting_nodes")

    // Slow rotation of the entire node group
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    // Individual node pulses (staggered for rhythmic effect)
    val pulse1 by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing, delayMillis = 0),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse1"
    )

    val pulse2 by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing, delayMillis = 467),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse2"
    )

    val pulse3 by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing, delayMillis = 933),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse3"
    )

    // Glow pulse for connection lines
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_pulse"
    )

    Canvas(modifier = modifier) {
        val centerX = size.width / 2
        val centerY = size.height / 2

        // WIDE orbit radius to ensure nodes are OUTSIDE the logo area
        // Logo is 120dp, so orbit radius of ~110-120dp places nodes safely outside
        val orbitRadius = size.minDimension / 2.1f // Approximately 130-140dp for a 280dp container

        // Three node positions (equilateral triangle around the logo)
        // Angles: Top (-90°), Bottom-Right (30°), Bottom-Left (150°)
        val angles = listOf(-90f, 30f, 150f)
        val positions = angles.map { angle ->
            val rad = Math.toRadians((angle + rotation).toDouble())
            Offset(
                centerX + orbitRadius * cos(rad).toFloat(),
                centerY + orbitRadius * sin(rad).toFloat()
            )
        }

        val colors = listOf(primaryColor, secondaryColor, tertiaryColor)
        val pulses = listOf(pulse1, pulse2, pulse3)

        // Draw subtle connection lines (network effect) - these stay behind nodes
        drawLine(
            color = primaryColor.copy(alpha = 0.15f + glowPulse * 0.1f),
            start = positions[0],
            end = positions[1],
            strokeWidth = 1.5.dp.toPx(),
            cap = StrokeCap.Round
        )
        drawLine(
            color = secondaryColor.copy(alpha = 0.15f + glowPulse * 0.1f),
            start = positions[1],
            end = positions[2],
            strokeWidth = 1.5.dp.toPx(),
            cap = StrokeCap.Round
        )
        drawLine(
            color = tertiaryColor.copy(alpha = 0.15f + glowPulse * 0.1f),
            start = positions[2],
            end = positions[0],
            strokeWidth = 1.5.dp.toPx(),
            cap = StrokeCap.Round
        )

        // Draw nodes with pulsing effect
        positions.forEachIndexed { index, position ->
            val pulseScale = pulses[index]
            val baseRadius = 12.dp.toPx()
            val nodeColor = colors[index]

            // Outer glow ring (expands with pulse)
            drawCircle(
                color = nodeColor.copy(alpha = 0.1f * (1 + glowPulse)),
                radius = baseRadius * 2.4f * pulseScale,
                center = position
            )

            // Secondary glow
            drawCircle(
                color = nodeColor.copy(alpha = 0.15f * pulseScale),
                radius = baseRadius * 1.7f * pulseScale,
                center = position
            )

            // Inner ring
            drawCircle(
                color = nodeColor.copy(alpha = 0.5f),
                radius = baseRadius * 1.15f * pulseScale,
                center = position,
                style = Stroke(width = 2.dp.toPx())
            )

            // Core node
            drawCircle(
                color = nodeColor,
                radius = baseRadius * pulseScale,
                center = position
            )

            // Tiny highlight for depth
            drawCircle(
                color = Color.White.copy(alpha = 0.4f),
                radius = baseRadius * 0.3f * pulseScale,
                center = Offset(position.x - baseRadius * 0.25f, position.y - baseRadius * 0.25f)
            )
        }

        // Optional: Very subtle ring around center (barely visible, adds connection)
        drawCircle(
            color = tertiaryColor.copy(alpha = 0.08f + glowPulse * 0.03f),
            radius = orbitRadius * 0.98f,
            center = Offset(centerX, centerY),
            style = Stroke(width = 0.8.dp.toPx())
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    PivotaConnectTheme {
        SplashContent()
    }
}