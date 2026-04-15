// ElegantBottomNavBar.kt
package com.example.pivota.dashboard.presentation.composables

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ElegantBottomNavBar(
    items: List<TopLevelRoute>,
    selectedRoute: Any?,
    onItemClick: (TopLevelRoute) -> Unit,
    modifier: Modifier = Modifier
) {
    // Animation for center item pulse and pop-out effect
    val infiniteTransition = rememberInfiniteTransition()
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .shadow(
                elevation = 4.dp,  // Reduced shadow for normal appearance
                shape = RoundedCornerShape(32.dp),
                clip = false,
                ambientColor = Color.Black.copy(alpha = 0.08f),
                spotColor = Color.Black.copy(alpha = 0.06f)
            )
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(32.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            items.forEachIndexed { index, item ->
                val isSelected = selectedRoute?.javaClass == item.route.javaClass
                val isCenterItem = index == items.size / 2

                if (isCenterItem) {
                    ExclusiveCenterNavItem(
                        item = item,
                        isSelected = isSelected,
                        onClick = { onItemClick(item) },
                        pulseScale = pulseScale
                    )
                } else {
                    ElegantNavItem(
                        item = item,
                        isSelected = isSelected,
                        onClick = { onItemClick(item) }
                    )
                }
            }
        }
    }
}

@Composable
fun ElegantNavItem(
    item: TopLevelRoute,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val selectedColor = MaterialTheme.colorScheme.tertiary
    val unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant

    val animatedColor by animateColorAsState(
        targetValue = if (isSelected) selectedColor else unselectedColor,
        animationSpec = tween(300),
        label = "color"
    )

    val animatedScale by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0.92f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .scale(animatedScale)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp)
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.contentDescription,
            tint = animatedColor,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = item.label,
            fontSize = 11.sp,
            color = animatedColor,
            maxLines = 1
        )

        if (isSelected) {
            Spacer(modifier = Modifier.height(2.dp))
            Box(
                modifier = Modifier
                    .size(3.dp)
                    .background(
                        color = selectedColor,
                        shape = CircleShape
                    )
            )
        }
    }
}

@Composable
fun ExclusiveCenterNavItem(
    item: TopLevelRoute,
    isSelected: Boolean,
    onClick: () -> Unit,
    pulseScale: Float
) {
    val surfaceColor = MaterialTheme.colorScheme.surface
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val tertiaryColor = MaterialTheme.colorScheme.tertiary
    val outlineVariant = MaterialTheme.colorScheme.outlineVariant
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant

    // Gradient using theme colors (60-30-10 rule)
    val gradient = Brush.linearGradient(
        colors = listOf(
            tertiaryColor,   // Baobab Gold - 10%
            secondaryColor,  // Warm Terracotta - 30%
            primaryColor     // African Sapphire - 60%
        ),
        start = Offset(0f, 0f),
        end = Offset(1f, 1f)
    )

    // No offset - normal position
    val elevation = if (isSelected) 8.dp else 4.dp
    val buttonSize = if (isSelected) 56.dp else 48.dp

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Animated glow ring when selected
        if (isSelected) {
            AnimatedGlowRing(glowColor = tertiaryColor)
        }

        // Main exclusive button
        Box(
            modifier = Modifier
                .size(buttonSize)
                .scale(if (isSelected) pulseScale else 1f)
                .shadow(
                    elevation = elevation,
                    shape = CircleShape,
                    clip = false,
                    ambientColor = tertiaryColor.copy(alpha = 0.3f),
                    spotColor = tertiaryColor.copy(alpha = 0.2f)
                )
                .background(
                    color = if (isSelected) Color.Transparent else surfaceColor,
                    shape = CircleShape
                )
                .then(
                    if (isSelected) Modifier
                        .background(brush = gradient, shape = CircleShape)
                    else Modifier
                        .border(
                            width = 1.5.dp,
                            color = outlineVariant,
                            shape = CircleShape
                        )
                        .background(color = surfaceColor, shape = CircleShape)
                )
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                EnhancedPulsingRings(pulseColor = tertiaryColor)
            }

            Icon(
                imageVector = item.icon,
                contentDescription = item.contentDescription,
                tint = if (isSelected) Color.White else primaryColor,
                modifier = Modifier.size(if (isSelected) 28.dp else 24.dp)
            )
        }

        // Label with enhanced styling when selected
        Text(
            text = item.label,
            fontSize = if (isSelected) 11.sp else 10.sp,
            color = if (isSelected) tertiaryColor else onSurfaceVariant,
            modifier = Modifier.padding(top = if (isSelected) 8.dp else 6.dp)
        )
    }
}

@Composable
fun AnimatedGlowRing(glowColor: Color) {
    val infiniteTransition = rememberInfiniteTransition()
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier
            .size(76.dp)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        glowColor.copy(alpha = alpha),
                        glowColor.copy(alpha = 0f)
                    ),
                    radius = 55f
                ),
                shape = CircleShape
            )
    )
}

@Composable
fun EnhancedPulsingRings(pulseColor: Color) {
    val infiniteTransition = rememberInfiniteTransition()
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val ring1Scale = 0.9f + (progress * 0.4f)
    val ring1Alpha = (1f - progress) * 0.4f

    val progress2 = (progress + 0.5f) % 1f
    val ring2Scale = 0.9f + (progress2 * 0.4f)
    val ring2Alpha = (1f - progress2) * 0.3f

    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                scaleX = ring1Scale
                scaleY = ring1Scale
                alpha = ring1Alpha
            }
            .drawBehind {
                drawCircle(
                    color = pulseColor,
                    radius = size.minDimension / 1.3f,
                    style = Stroke(width = 1.5.dp.toPx())
                )
            }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                scaleX = ring2Scale
                scaleY = ring2Scale
                alpha = ring2Alpha
            }
            .drawBehind {
                drawCircle(
                    color = pulseColor,
                    radius = size.minDimension / 1.3f,
                    style = Stroke(width = 1.5.dp.toPx())
                )
            }
    )
}