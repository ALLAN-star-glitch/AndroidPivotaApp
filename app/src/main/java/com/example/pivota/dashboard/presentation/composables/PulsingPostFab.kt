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

@Composable
fun PulsingPostFab(
    onClick: () -> Unit
) {
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
        // Pulse Effect (The "Glow" behind)
        Surface(
            modifier = Modifier
                .size(60.dp)
                .scale(scale),
            shape = CircleShape,
            color = Color(0xFF008080).copy(alpha = 0.3f)
        ) {}

        // Main FAB with Border via Surface wrapper
        Surface(
            modifier = Modifier.size(64.dp),
            shape = CircleShape,
            color = Color(0xFF008080), // Teal background
            border = BorderStroke(3.dp, Color(0xFFE9C16C)), // Golden yellow outline
            tonalElevation = 4.dp,
            shadowElevation = 6.dp,
            onClick = onClick
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Post",
                    tint = Color.White, // White plus
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}