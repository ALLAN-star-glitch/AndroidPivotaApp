package com.example.pivota.dashboard.presentation.composables.client_general_composables.listings_composables.categories

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ServiceGridSkeleton(
    columnsPerRow: Int = 4,
    rowsToShow: Int = 2,
    horizontalPadding: Dp = 16.dp
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding)
    ) {
        repeat(rowsToShow) { rowIndex ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                repeat(columnsPerRow) { colIndex ->
                    val itemIndex = rowIndex * columnsPerRow + colIndex
                    ServiceCardCircleSkeleton(
                        modifier = Modifier.weight(1f),
                        index = itemIndex
                    )
                }
            }
        }
    }
}

@Composable
fun ServiceCardCircleSkeleton(
    modifier: Modifier = Modifier,
    index: Int = 0
) {
    val colorScheme = MaterialTheme.colorScheme

    // Match the color scheme of the skeleton with the actual cards
    val skeletonColors = listOf(
        colorScheme.primary.copy(alpha = 0.15f),
        colorScheme.secondary.copy(alpha = 0.15f),
        colorScheme.tertiary.copy(alpha = 0.15f)
    )
    val skeletonColor = skeletonColors[index % skeletonColors.size]

    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmerAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmerAlpha"
    )

    Column(
        modifier = modifier
            .padding(horizontal = 4.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Circular skeleton with matching color
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(skeletonColor)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Text skeleton
        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(11.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(skeletonColor.copy(alpha = shimmerAlpha))
        )
    }
}