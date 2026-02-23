package com.example.pivota.dashboard.presentation.composables

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pivota.R

data class HeaderConfig(
    val title: String,
    val subtitle: String,
    val backgroundColor: Color,
    val accentColor: Color,
    val backgroundImageRes: Int = R.drawable.nairobi_city,
    val showSmartMatchBadge: Boolean = true,
    val showUpgradeButton: Boolean = true,
    val showAvatar: Boolean = true,
    val maxHeight: Dp = 220.dp,
    val minHeight: Dp = 90.dp,
    val maxFontSize: TextUnit = 34.sp,
    val minFontSize: TextUnit = 24.sp
)

@Composable
fun CollapsibleHeroHeader(
    config: HeaderConfig,
    height: Dp,
    collapseFraction: Float,
    onMailClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onUpgradeClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val collapsed = collapseFraction > 0.85f

    // Animate font size based on collapseFraction
    val titleFontSize = ((config.maxFontSize.value - collapseFraction *
            (config.maxFontSize.value - config.minFontSize.value))).sp

    // Animate background color based on collapse state
    val backgroundColor by animateColorAsState(
        targetValue = if (collapsed) config.backgroundColor.copy(alpha = 0.95f) else Color.Transparent,
        animationSpec = tween(durationMillis = 300)
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(height),
        shadowElevation = if (collapsed) 6.dp else 0.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Background Image - fades out when collapsed
            AnimatedVisibility(
                visible = !collapsed,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Image(
                    painter = painterResource(id = config.backgroundImageRes),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Solid color background for collapsed state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor)
            )

            // Gradient overlay - only visible when not collapsed
            AnimatedVisibility(
                visible = !collapsed,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    config.backgroundColor.copy(0.95f),
                                    config.backgroundColor.copy(0.75f),
                                    Color.Transparent
                                )
                            )
                        )
                )
            }

            // Golden Accent (Horizontal Glow) - only visible when not collapsed
            AnimatedVisibility(
                visible = !collapsed,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(config.accentColor.copy(0.15f), Color.Transparent),
                                endX = 400f
                            )
                        )
                )
            }

            // Main content container
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
            ) {
                // TOP ROW - Only visible when NOT collapsed
                if (config.showAvatar) {
                    AnimatedVisibility(
                        visible = !collapsed,
                        enter = fadeIn() + slideInVertically(),
                        exit = fadeOut() + slideOutVertically()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp, start = 20.dp, end = 20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // 👤 AVATAR SECTION
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box {
                                    HeaderAvatar(
                                        backgroundColor = config.backgroundColor
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(12.dp)
                                            .background(config.accentColor, CircleShape)
                                            .border(2.dp, config.backgroundColor, CircleShape)
                                            .align(Alignment.BottomEnd)
                                    )
                                }

                                Spacer(Modifier.width(12.dp))

                                Column {
                                    Text(
                                        "Hi, Guest",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        "Welcome to Pivota",
                                        color = Color.White.copy(0.85f),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }

                            // 🔔 ICON ROW
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                HeaderActionIcon(
                                    icon = Icons.Default.Mail,
                                    iconTint = Color.White,
                                    backgroundTint = Color.White.copy(alpha = 0.2f),
                                    onClick = onMailClick
                                )
                                HeaderActionIcon(
                                    icon = Icons.Default.Notifications,
                                    iconTint = Color.White,
                                    backgroundTint = Color.White.copy(alpha = 0.2f),
                                    onClick = onNotificationsClick
                                )
                            }
                        }
                    }
                } else {
                    // If avatar is hidden, just show icons on the right
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, end = 20.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            HeaderActionIcon(
                                icon = Icons.Default.Mail,
                                iconTint = Color.White,
                                backgroundTint = Color.White.copy(alpha = 0.2f),
                                onClick = onMailClick
                            )
                            HeaderActionIcon(
                                icon = Icons.Default.Notifications,
                                iconTint = Color.White,
                                backgroundTint = Color.White.copy(alpha = 0.2f),
                                onClick = onNotificationsClick
                            )
                        }
                    }
                }

                // Spacer to push content down when not collapsed
                if (!collapsed) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }

            // Main content area - Changes based on collapse state
            if (collapsed) {
                // COLLAPSED STATE - All elements in one horizontal row, lowered
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterStart)
                        .padding(start = 20.dp, end = 20.dp)
                        .offset(y = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Left side - Title with badge and button
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = config.title,
                            style = MaterialTheme.typography.headlineLarge.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                letterSpacing = (-1.5).sp,
                                fontSize = titleFontSize
                            )
                        )

                        if (config.showSmartMatchBadge) {
                            // 🤖 SmartMatch™ Badge
                            Surface(
                                color = config.accentColor,
                                shape = RoundedCornerShape(4.dp),
                                modifier = Modifier.graphicsLayer { translationY = 2f }
                            ) {
                                Text(
                                    text = "SmartMatch™",
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = config.backgroundColor,
                                        fontSize = 9.sp
                                    )
                                )
                            }
                        }

                        if (config.showUpgradeButton) {
                            // ⬆️ Upgrade Button
                            Surface(
                                color = Color.White.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(4.dp),
                                border = BorderStroke(1.dp, config.accentColor.copy(alpha = 0.5f)),
                                modifier = Modifier.clickable { onUpgradeClick() }
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Icon(
                                        Icons.Default.ArrowUpward,
                                        contentDescription = null,
                                        tint = config.accentColor,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = "UPGRADE",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.ExtraBold,
                                            color = config.accentColor,
                                            fontSize = 9.sp
                                        )
                                    )
                                }
                            }
                        }
                    }

                    // Right side - Icons (only show if avatar was hidden or we need them)
                    if (!config.showAvatar) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            HeaderActionIcon(
                                icon = Icons.Default.Mail,
                                iconTint = Color.White,
                                backgroundTint = Color.White.copy(alpha = 0.2f),
                                onClick = onMailClick
                            )
                            HeaderActionIcon(
                                icon = Icons.Default.Notifications,
                                iconTint = Color.White,
                                backgroundTint = Color.White.copy(alpha = 0.2f),
                                onClick = onNotificationsClick
                            )
                        }
                    }
                }
            } else {
                // EXPANDED STATE
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 20.dp, bottom = 32.dp)
                        .statusBarsPadding()
                ) {
                    // Row containing title, badge, and button
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 4.dp)
                    ) {
                        Text(
                            text = config.title,
                            style = MaterialTheme.typography.headlineLarge.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                letterSpacing = (-1.5).sp,
                                fontSize = titleFontSize
                            )
                        )

                        if (config.showSmartMatchBadge) {
                            Spacer(modifier = Modifier.width(8.dp))

                            // 🤖 SmartMatch™ Badge
                            Surface(
                                color = config.accentColor,
                                shape = RoundedCornerShape(4.dp),
                                modifier = Modifier.graphicsLayer { translationY = 4f }
                            ) {
                                Text(
                                    text = "SmartMatch™",
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = config.backgroundColor,
                                        fontSize = 9.sp
                                    )
                                )
                            }
                        }

                        if (config.showUpgradeButton) {
                            Spacer(modifier = Modifier.width(8.dp))

                            // ⬆️ Upgrade Button
                            Surface(
                                color = Color.White.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(4.dp),
                                border = BorderStroke(1.dp, config.accentColor.copy(alpha = 0.5f)),
                                modifier = Modifier.clickable { onUpgradeClick() }
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Icon(
                                        Icons.Default.ArrowUpward,
                                        contentDescription = null,
                                        tint = config.accentColor,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = "UPGRADE",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.ExtraBold,
                                            color = config.accentColor,
                                            fontSize = 9.sp
                                        )
                                    )
                                }
                            }
                        }
                    }

                    // 📝 Subtitle Text
                    Text(
                        text = config.subtitle,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White.copy(0.9f)
                        ),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun HeaderAvatar(
    backgroundColor: Color
) {
    Box(
        modifier = Modifier
            .size(45.dp)
            .background(Color.White.copy(0.2f), CircleShape)
            .border(1.5.dp, Color.White.copy(0.5f), CircleShape)
            .clip(CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.PersonOutline,
            contentDescription = "User Avatar",
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun HeaderActionIcon(
    icon: ImageVector,
    iconTint: Color = Color.White,
    backgroundTint: Color = Color.White.copy(alpha = 0.2f),
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(48.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(backgroundTint, CircleShape)
                .clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}