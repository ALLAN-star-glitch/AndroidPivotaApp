package com.example.pivota.core.presentation.composables

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pivota.admin.presentation.screens.JobStatus
import com.example.pivota.ui.theme.PivotaConnectTheme


/**
 * Status Badge Styles
 */
enum class StatusBadgeStyle {
    PILL,      // Light background with dot/icon (default)
    SOLID,     // Solid color background with white text
    OUTLINE,   // Outlined with colored text
    DOT_ONLY   // Just a colored dot (compact)
}

/**
 * Status Badge Sizes
 */
enum class StatusBadgeSize {
    SMALL,
    MEDIUM,
    LARGE;

    fun height(): Dp = when (this) {
        SMALL -> 20.dp
        MEDIUM -> 24.dp
        LARGE -> 32.dp
    }

    fun fontSize(): TextUnit = when (this) {
        SMALL -> 10.sp
        MEDIUM -> 12.sp
        LARGE -> 14.sp
    }

    fun iconSize(): Dp = when (this) {
        SMALL -> 12.dp
        MEDIUM -> 14.dp
        LARGE -> 16.dp
    }

    fun dotSize(): Dp = when (this) {
        SMALL -> 8.dp
        MEDIUM -> 10.dp
        LARGE -> 12.dp
    }

    fun paddingHorizontal(): Dp = when (this) {
        SMALL -> 6.dp
        MEDIUM -> 8.dp
        LARGE -> 12.dp
    }
}

/**
 * Status Badge Composable using PivotaConnect theme colors
 *
 * @param status The JobStatus to display
 * @param style Visual style of the badge (PILL, SOLID, OUTLINE, DOT_ONLY)
 * @param size Size of the badge (SMALL, MEDIUM, LARGE)
 * @param showIcon Whether to show the status icon
 * @param showLabel Whether to show the status label
 * @param animated Whether to add a subtle pulse animation (great for PENDING_REVIEW)
 */
@Composable
fun StatusBadge(
    status: JobStatus,
    style: StatusBadgeStyle = StatusBadgeStyle.PILL,
    size: StatusBadgeSize = StatusBadgeSize.MEDIUM,
    showIcon: Boolean = true,
    showLabel: Boolean = true,
    animated: Boolean = false,
    modifier: Modifier = Modifier
) {
    val color = status.color()
    val icon = if (showIcon) status.icon() else null
    val label = if (showLabel) status.displayName() else null

    // Animation for pulsing effect (only for PENDING_REVIEW or when animated=true)
    val shouldAnimate = animated || status == JobStatus.PENDING_REVIEW

    val infiniteTransition = rememberInfiniteTransition()
    val pulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        )
    )

    val finalModifier = if (shouldAnimate && style != StatusBadgeStyle.DOT_ONLY) {
        modifier.scale(pulse)
    } else {
        modifier
    }

    when (style) {
        StatusBadgeStyle.PILL -> PillStatusBadge(
            color = color,
            icon = icon,
            label = label,
            size = size,
            shouldAnimate = shouldAnimate,
            alpha = alpha,
            isPendingReview = status == JobStatus.PENDING_REVIEW,
            modifier = finalModifier
        )

        StatusBadgeStyle.SOLID -> SolidStatusBadge(
            color = color,
            icon = icon,
            label = label,
            size = size,
            modifier = finalModifier
        )

        StatusBadgeStyle.OUTLINE -> OutlineStatusBadge(
            color = color,
            icon = icon,
            label = label,
            size = size,
            modifier = modifier
        )

        StatusBadgeStyle.DOT_ONLY -> DotStatusBadge(
            color = color,
            size = size,
            shouldAnimate = shouldAnimate,
            alpha = alpha,
            modifier = modifier
        )
    }
}

/**
 * Pill Style - Light background with colored dot/icon
 * Uses theme surface colors for background
 */
@Composable
private fun PillStatusBadge(
    color: Color,
    icon: ImageVector?,
    label: String?,
    size: StatusBadgeSize,
    shouldAnimate: Boolean,
    alpha: Float,
    isPendingReview: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(40),
        color = color.copy(alpha = 0.12f),
        // FIXED: In Material3, use shadowElevation instead of elevation
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .height(size.height())
                .padding(horizontal = size.paddingHorizontal()),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Animated dot for pending review
            if (shouldAnimate && isPendingReview) {
                Box(
                    modifier = Modifier
                        .size(size.dotSize())
                        .background(color.copy(alpha = alpha), CircleShape)
                )
            } else {
                // Regular dot
                Box(
                    modifier = Modifier
                        .size(size.dotSize())
                        .background(color, CircleShape)
                )
            }

            // Icon (if provided and not showing dot)
            if (icon != null && !isPendingReview) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(size.iconSize())
                )
            }

            // Label
            if (label != null) {
                if (icon != null || isPendingReview) {
                    Spacer(modifier = Modifier.width(4.dp))
                }
                Text(
                    text = label,
                    color = color,
                    fontSize = size.fontSize(),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

/**
 * Solid Style - Full color background with white text/icons
 */
@Composable
private fun SolidStatusBadge(
    color: Color,
    icon: ImageVector?,
    label: String?,
    size: StatusBadgeSize,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(40),
        color = color,
        // FIXED: In Material3, use shadowElevation instead of elevation
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .height(size.height())
                .padding(horizontal = size.paddingHorizontal()),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(size.iconSize())
                )
            }

            if (label != null) {
                if (icon != null) Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = label,
                    color = Color.White,
                    fontSize = size.fontSize(),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * Outline Style - Border with colored text/icons
 */
@Composable
private fun OutlineStatusBadge(
    color: Color,
    icon: ImageVector?,
    label: String?,
    size: StatusBadgeSize,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(40),
        color = Color.Transparent,
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f)),
        // FIXED: In Material3, use shadowElevation instead of elevation
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .height(size.height())
                .padding(horizontal = size.paddingHorizontal()),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Outlined dot
            Box(
                modifier = Modifier
                    .size(size.dotSize())
                    .background(Color.Transparent, CircleShape)
                    .border(1.5.dp, color, CircleShape)
            )

            if (icon != null) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(size.iconSize())
                )
            }

            if (label != null) {
                if (icon != null) Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = label,
                    color = color,
                    fontSize = size.fontSize(),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

/**
 * Dot Only Style - Most compact, just a colored dot
 */
@Composable
private fun DotStatusBadge(
    color: Color,
    size: StatusBadgeSize,
    shouldAnimate: Boolean,
    alpha: Float,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val dotModifier = if (shouldAnimate) {
        modifier.scale(scale)
    } else {
        modifier
    }

    Box(
        modifier = dotModifier
            .size(size.dotSize())
            .background(
                color = if (shouldAnimate) color.copy(alpha = alpha) else color,
                shape = CircleShape
            )
    )
}

/**
 * Simplified StatusBadge for quick use in lists
 */
@Composable
fun SimpleStatusBadge(
    status: JobStatus,
    modifier: Modifier = Modifier
) {
    StatusBadge(
        status = status,
        style = StatusBadgeStyle.PILL,
        size = StatusBadgeSize.SMALL,
        showIcon = false,
        animated = false,
        modifier = modifier
    )
}

/**
 * Preview of all status badges
 */
@Preview(showBackground = true)
@Composable
fun StatusBadgePreview() {
    PivotaConnectTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // All statuses in Pill style
            Text(
                text = "Pill Style (Default)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                JobStatus.values().forEach { status ->
                    StatusBadge(
                        status = status,
                        style = StatusBadgeStyle.PILL
                    )
                }
            }

            // All statuses in Solid style
            Text(
                text = "Solid Style",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                JobStatus.values().forEach { status ->
                    StatusBadge(
                        status = status,
                        style = StatusBadgeStyle.SOLID
                    )
                }
            }

            // All statuses in Outline style
            Text(
                text = "Outline Style",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                JobStatus.values().forEach { status ->
                    StatusBadge(
                        status = status,
                        style = StatusBadgeStyle.OUTLINE
                    )
                }
            }

            // Dot only style
            Text(
                text = "Dot Only (Compact)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                JobStatus.values().forEach { status ->
                    StatusBadge(
                        status = status,
                        style = StatusBadgeStyle.DOT_ONLY
                    )
                }
            }

            // Size variants
            Text(
                text = "Size Variants",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                StatusBadgeSize.values().forEach { size ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatusBadge(
                            status = JobStatus.ACTIVE,
                            size = size,
                            style = StatusBadgeStyle.PILL
                        )
                        StatusBadge(
                            status = JobStatus.PENDING_REVIEW,
                            size = size,
                            style = StatusBadgeStyle.PILL
                        )
                    }
                }
            }

            // Animated examples
            Text(
                text = "Animated (Pending Review)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatusBadge(
                    status = JobStatus.PENDING_REVIEW,
                    style = StatusBadgeStyle.PILL
                )
                StatusBadge(
                    status = JobStatus.PENDING_REVIEW,
                    style = StatusBadgeStyle.DOT_ONLY
                )
            }
        }
    }
}