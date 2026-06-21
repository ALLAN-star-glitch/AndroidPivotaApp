package com.example.pivota.dashboard.presentation.composables.client_general_composables.general

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

/**
 * Brush-based shimmer that creates a sliding light streak effect.
 * The shimmer moves across the component like a wave.
 *
 * @param shimmerColors Colors for the shimmer gradient
 * @return Animated Brush that slides across
 */
@Composable
fun shimmerBrush(
    shimmerColors: List<Color> = listOf(
        Color.LightGray.copy(alpha = 0.4f),
        Color.White.copy(alpha = 0.7f),
        Color.LightGray.copy(alpha = 0.4f)
    )
): Brush {
    val transition = rememberInfiniteTransition(label = "shimmer_brush")
    val translateAnimation by transition.animateFloat(
        initialValue = -400f,
        targetValue = 1400f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "translateAnimation"
    )

    return Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnimation, 0f),
        end = Offset(translateAnimation + 500f, 0f)
    )
}

/**
 * Modifier that applies a sliding shimmer effect to a component.
 * Use this with Surface(color = Color.Transparent) to see the shimmer.
 *
 * @param shape Shape of the shimmer background
 * @param shimmerColors Colors for the shimmer gradient
 */
fun Modifier.shimmer(
    shape: Shape = RoundedCornerShape(4.dp),
    shimmerColors: List<Color> = listOf(
        Color.LightGray.copy(alpha = 0.4f),
        Color.White.copy(alpha = 0.7f),
        Color.LightGray.copy(alpha = 0.4f)
    )
): Modifier = composed {
    val brush = shimmerBrush(shimmerColors)
    this.background(
        brush = brush,
        shape = shape
    )
}

/**
 * Helper function to get a shimmer color for a given color scheme.
 * This creates the pulsing/breathing effect (not sliding).
 * Use this if you want the alpha-based pulsing effect instead of sliding.
 *
 * @param colorScheme The MaterialTheme color scheme
 * @param durationMillis Duration of one complete animation cycle in milliseconds
 * @param minAlpha Minimum alpha value (dimmer state)
 * @param maxAlpha Maximum alpha value (brighter state)
 * @return Animated Color that pulses between alpha values
 */
@Composable
fun getShimmerColor(
    colorScheme: ColorScheme,
    durationMillis: Int = 1000,
    minAlpha: Float = 0.3f,
    maxAlpha: Float = 0.8f
): Color {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer_alpha")
    val alpha by infiniteTransition.animateFloat(
        initialValue = minAlpha,
        targetValue = maxAlpha,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmerAlpha"
    )
    return colorScheme.surfaceVariant.copy(alpha = alpha)
}