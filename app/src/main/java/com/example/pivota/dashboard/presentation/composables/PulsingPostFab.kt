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
import androidx.compose.ui.unit.dp
import com.example.pivota.ui.theme.*

@Composable
fun PulsingPostFab(
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.12f, // Subtle pulse
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(contentAlignment = Alignment.Center) {
        // Pulse Effect (The "Glow" behind) - Using primary color with opacity
        Surface(
            modifier = Modifier
                .size(60.dp)
                .scale(scale),
            shape = CircleShape,
            color = colorScheme.primary.copy(alpha = 0.3f)
        ) {}

        // Main FAB with Border via Surface wrapper
        Surface(
            modifier = Modifier.size(64.dp),
            shape = CircleShape,
            color = colorScheme.primary, // Primary color for background
            border = BorderStroke(3.dp, colorScheme.tertiary), // Tertiary for gold/yellow outline
            tonalElevation = 4.dp,
            shadowElevation = 6.dp,
            onClick = onClick
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Post",
                    tint = colorScheme.onPrimary, // onPrimary for white/contrasting plus
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}