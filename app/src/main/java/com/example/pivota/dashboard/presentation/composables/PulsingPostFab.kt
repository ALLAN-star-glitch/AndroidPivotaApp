package com.example.pivota.dashboard.presentation.composables

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.example.pivota.ui.theme.*

@Composable
fun PulsingPostFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    pulseColor: Color = MaterialTheme.colorScheme.primary,
    iconTint: Color = MaterialTheme.colorScheme.onPrimary
) {
    val colorScheme = MaterialTheme.colorScheme

    // More sophisticated pulse animation with easing
    val infiniteTransition = rememberInfiniteTransition(label = "fab_pulse")

    // Main pulse scale with smoother curve
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1500,
                easing = EaseInOutQuad // Smoother easing curve
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    // Subtle alpha animation for the glow effect
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1500,
                easing = EaseInOutQuad
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        // Elegant glow effect with varying opacity
        Surface(
            modifier = Modifier
                .size(68.dp)
                .scale(pulseScale)
                .graphicsLayer {
                    // Add a subtle blur effect for softer glow
                    alpha = glowAlpha
                },
            shape = CircleShape,
            color = pulseColor.copy(alpha = 0.2f),
            shadowElevation = 8.dp
        ) {}

        // Main FAB with refined styling
        Surface(
            modifier = Modifier.size(60.dp),
            shape = CircleShape,
            color = colorScheme.primary,
            contentColor = iconTint,
            border = BorderStroke(
                width = 2.dp,
                color = colorScheme.tertiary.copy(alpha = 0.7f) // Slightly transparent border
            ),
            tonalElevation = 6.dp,
            shadowElevation = 8.dp,
            onClick = onClick
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create new post",
                    modifier = Modifier.size(28.dp) // Slightly smaller icon
                )
            }
        }
    }
}

// Alternative minimal version for cleaner UI
@Composable
fun MinimalPulsingFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme

    val infiniteTransition = rememberInfiniteTransition(label = "minimal_pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f, // More subtle pulse
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    FloatingActionButton(
        onClick = onClick,
        modifier = modifier
            .size(56.dp)
            .scale(scale),
        containerColor = colorScheme.primary,
        contentColor = colorScheme.onPrimary,
        shape = CircleShape,
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = 6.dp,
            pressedElevation = 8.dp
        )
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Create post",
            modifier = Modifier.size(24.dp)
        )
    }
}