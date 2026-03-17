import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Responsive SmartMatch badge that adapts to different screen sizes
 * Shows AI-powered match count with animated gradient background
 *
 * @param matchCount The number of matches to display
 * @param modifier Modifier to be applied to the badge
 * @param isTablet Whether the device is a tablet (affects sizing)
 * @param showLabel Whether to show the "SmartMatch" text label
 * @param onClick Optional click handler for the badge
 * @param animate Whether to animate the gradient and icon
 * @param backgroundColor Optional custom background color (defaults to gradient)
 * @param contentColor Optional custom text/icon color (defaults to White)
 * @param shape Optional custom shape (defaults to rounded shape based on tablet)
 * @param icon Optional custom icon (defaults to AutoAwesome)
 * @param iconSize Optional custom icon size
 * @param fontSize Optional custom font size
 * @param horizontalPadding Optional custom horizontal padding
 * @param verticalPadding Optional custom vertical padding
 * @param maxCount Optional maximum count before showing "+" (defaults to 99)
 * @param animationDuration Optional animation duration in ms (defaults to 3000)
 */
@Composable
fun SmartMatchBadge(
    matchCount: Int,
    modifier: Modifier = Modifier,
    isTablet: Boolean = false,
    showLabel: Boolean = true,
    onClick: (() -> Unit)? = null,
    animate: Boolean = true,
    backgroundColor: Color? = null,
    contentColor: Color = Color.White,
    shape: RoundedCornerShape? = null,
    icon: ImageVector = Icons.Outlined.AutoAwesome,
    iconSize: Dp? = null,
    fontSize: TextUnit? = null,
    horizontalPadding: Dp? = null,
    verticalPadding: Dp? = null,
    maxCount: Int = 99,
    animationDuration: Int = 3000
) {
    val colorScheme = MaterialTheme.colorScheme
    val primaryColor = colorScheme.primary
    val tertiaryColor = colorScheme.tertiary

    // Responsive sizing based on screen size
    val defaultIconSize = if (isTablet) 22.dp else 18.dp
    val defaultFontSize = if (isTablet) 13.sp else 11.sp

    // Only apply horizontal padding if showLabel is true, otherwise 0.dp
    val effectiveHorizontalPadding = when {
        horizontalPadding != null -> horizontalPadding
        showLabel -> (if (isTablet) 16.dp else 12.dp)
        else -> 0.dp // Changed from 8.dp to 0.dp when showLabel = false
    }

    val defaultVerticalPadding = if (isTablet) 10.dp else 6.dp
    val effectiveVerticalPadding = if (showLabel) (verticalPadding ?: defaultVerticalPadding) else 0.dp
    val defaultShape = shape ?: RoundedCornerShape(if (isTablet) 24.dp else 20.dp)

    // Format count with max limit
    val displayCount = if (matchCount > maxCount) "$maxCount+" else matchCount.toString()

    // Animation for gradient shift
    val infiniteTransition = rememberInfiniteTransition()
    val gradientOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(animationDuration, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    // State for press animation
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed && onClick != null) 0.95f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 300f),
        label = "press_scale"
    )

    val badgeModifier = if (onClick != null) {
        Modifier
            .scale(scale)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        try {
                            awaitRelease()
                        } finally {
                            isPressed = false
                        }
                    },
                    onTap = {
                        onClick()
                    }
                )
            }
    } else {
        Modifier
    }

    Row(
        modifier = modifier
            .then(badgeModifier)
            .clip(defaultShape)
            .then(
                if (backgroundColor != null) {
                    Modifier.background(backgroundColor, defaultShape)
                } else {
                    Modifier.background(
                        if (animate) {
                            Brush.linearGradient(
                                colors = listOf(
                                    primaryColor,
                                    tertiaryColor,
                                    primaryColor.copy(0.8f),
                                    tertiaryColor.copy(0.8f)
                                ),
                                start = Offset(gradientOffset, 0f),
                                end = Offset(gradientOffset + 100f, 100f),
                                tileMode = TileMode.Mirror
                            )
                        } else {
                            Brush.linearGradient(
                                colors = listOf(primaryColor, tertiaryColor)
                            )
                        },
                        shape = defaultShape
                    )
                }
            )
            .padding(
                horizontal = effectiveHorizontalPadding,
                vertical = effectiveVerticalPadding
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        // Icon with micro-animation
        Box(
            modifier = Modifier
                .size(iconSize ?: defaultIconSize)
                .then(
                    if (animate && onClick != null) {
                        Modifier.graphicsLayer {
                            rotationZ = if (isPressed) 0f else gradientOffset / 3
                        }
                    } else Modifier
                )
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "SmartMatch",
                tint = contentColor,
                modifier = Modifier.fillMaxSize()
            )
        }

        if (showLabel) {
            Spacer(modifier = Modifier.width(if (isTablet) 8.dp else 6.dp))

            // Match count with micro-animation
            Text(
                text = "$displayCount SmartMatch${if (matchCount != 1) "es" else ""}",
                color = contentColor,
                fontSize = fontSize ?: defaultFontSize,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = if (animate) {
                    Modifier.graphicsLayer {
                        alpha = 1f - (gradientOffset / 200f).coerceIn(0.8f, 1f)
                    }
                } else Modifier
            )
        } else {
            // Just show count without label (for compact spaces)
            Text(
                text = displayCount,
                color = contentColor,
                fontSize = fontSize ?: defaultFontSize,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 2.dp)
            )
        }
    }
}

/**
 * Compact version for use in tight spaces (like inside cards)
 *
 * @param matchCount The number of matches to display
 * @param modifier Modifier to be applied to the badge
 * @param isTablet Whether the device is a tablet
 * @param onClick Optional click handler
 * @param backgroundColor Optional custom background color
 * @param contentColor Optional custom text/icon color
 * @param size Optional custom size for the badge
 */
@Composable
fun CompactSmartMatchBadge(
    matchCount: Int,
    modifier: Modifier = Modifier,
    isTablet: Boolean = false,
    onClick: (() -> Unit)? = null,
    backgroundColor: Color? = null,
    contentColor: Color = Color.White,
    size: Dp? = null
) {
    val badgeSize = size ?: if (isTablet) 48.dp else 40.dp
    val iconSize = if (isTablet) 22.dp else 18.dp
    val fontSize = if (isTablet) 12.sp else 10.sp

    Box(
        modifier = modifier
            .size(badgeSize)
            .then(
                if (onClick != null) {
                    Modifier.clickable { onClick() }
                } else Modifier
            )
            .clip(CircleShape)
            .background(
                backgroundColor ?: MaterialTheme.colorScheme.primary,
                CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.AutoAwesome,
                contentDescription = "SmartMatch",
                tint = contentColor,
                modifier = Modifier.size(iconSize)
            )
            if (matchCount > 0) {
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = matchCount.toString(),
                    color = contentColor,
                    fontSize = fontSize,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * Horizontal version with label for wider spaces
 *
 * @param matchCount The number of matches to display
 * @param modifier Modifier to be applied to the card
 * @param isTablet Whether the device is a tablet
 * @param onClick Optional click handler
 * @param backgroundColor Optional custom background color
 * @param borderColor Optional custom border color
 * @param iconColor Optional custom icon color
 * @param titleColor Optional custom title color
 * @param subtitleColor Optional custom subtitle color
 * @param showIcon Whether to show the icon
 * @param titleText Custom title text (defaults to "AI SmartMatch™")
 * @param subtitleText Custom subtitle text (defaults to "X potential matches available")
 * @param iconSize Optional custom icon size
 * @param horizontalPadding Optional custom horizontal padding
 * @param verticalPadding Optional custom vertical padding
 */
@Composable
fun HorizontalSmartMatchBadge(
    matchCount: Int,
    modifier: Modifier = Modifier,
    isTablet: Boolean = false,
    onClick: (() -> Unit)? = null,
    backgroundColor: Color? = null,
    borderColor: Color? = null,
    iconColor: Color? = null,
    titleColor: Color? = null,
    subtitleColor: Color? = null,
    showIcon: Boolean = true,
    titleText: String = "AI SmartMatch™",
    subtitleText: String = "$matchCount potential matches available",
    iconSize: Dp? = null,
    horizontalPadding: Dp = 16.dp,
    verticalPadding: Dp = 12.dp
) {
    val colorScheme = MaterialTheme.colorScheme
    val primaryColor = colorScheme.tertiary
    val tertiaryColor = colorScheme.tertiaryContainer

    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor ?: primaryColor.copy(0.1f)
        ),
        border = BorderStroke(
            1.dp,
            borderColor ?: tertiaryColor.copy(0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = horizontalPadding, vertical = verticalPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (showIcon) {
                    Surface(
                        shape = CircleShape,
                        color = (iconColor ?: tertiaryColor).copy(0.15f),
                        modifier = Modifier.size(if (isTablet) 44.dp else 36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Outlined.AutoAwesome,
                                contentDescription = null,
                                tint = iconColor ?: tertiaryColor,
                                modifier = Modifier.size(iconSize ?: (if (isTablet) 24.dp else 20.dp))
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))
                }

                Column {
                    Text(
                        text = titleText,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = titleColor ?: colorScheme.onSurface
                        )
                    )
                    Text(
                        text = subtitleText,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = subtitleColor ?: colorScheme.onSurfaceVariant
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            if (onClick != null) {
                Icon(
                    Icons.Rounded.ChevronRight,
                    contentDescription = "View matches",
                    tint = tertiaryColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}