package com.example.pivota.dashboard.presentation.composables.listings_composables

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.request.crossfade
import com.example.pivota.R
import com.example.pivota.ui.theme.PivotaConnectTheme

enum class ProfessionalType {
    INDIVIDUAL,
    ORGANIZATION
}

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun ModernProfessionalCardV2(
    modifier: Modifier = Modifier,
    imageUrl: Any? = null,
    name: String,
    profession: String,
    location: String,
    postedTime: String,
    professionalType: ProfessionalType,
    rating: Float,
    jobsCompleted: Int,
    onViewDetailsClick: () -> Unit = {},
) {
    val colorScheme = MaterialTheme.colorScheme
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val isWide = windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)
    val isMedium = windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.MEDIUM
    val isCompact = windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val isTwoColumnCompact = isCompact && (isLandscape || configuration.screenWidthDp >= 480)

    // Memoize badge properties to prevent recalculation
    val (secondBadgeIcon, secondBadgeText, secondBadgeColor) = remember(professionalType) {
        when (professionalType) {
            ProfessionalType.INDIVIDUAL -> Triple(
                Icons.Filled.Person,
                "Individual",
                colorScheme.secondary
            )
            ProfessionalType.ORGANIZATION -> Triple(
                Icons.Filled.Business,
                "Company",
                colorScheme.primary
            )
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onViewDetailsClick() }
            .drawBehind {
                // Only show dashed border on wide screens
                if (isWide) {
                    val strokeWidth = 1f
                    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
                    drawRoundRect(
                        color = colorScheme.primary.copy(alpha = 0.15f),
                        style = Stroke(width = strokeWidth, pathEffect = pathEffect),
                        cornerRadius = CornerRadius(12.dp.toPx(), 12.dp.toPx())
                    )
                }
            },
        shape = RoundedCornerShape(
            when {
                isWide -> 12.dp
                isMedium -> 12.dp
                isTwoColumnCompact -> 10.dp
                else -> 12.dp
            }
        ),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = when {
                isWide -> 3.dp
                isTwoColumnCompact -> 1.dp
                else -> 2.dp
            }
        )
    ) {
        when {
            isWide -> {
                DesktopProfessionalCardContent(
                    imageUrl = imageUrl,
                    name = name,
                    profession = profession,
                    location = location,
                    postedTime = postedTime,
                    professionalType = professionalType,
                    rating = rating,
                    jobsCompleted = jobsCompleted,
                    secondBadgeIcon = secondBadgeIcon,
                    secondBadgeText = secondBadgeText,
                    secondBadgeColor = secondBadgeColor,
                    onViewDetailsClick = onViewDetailsClick,
                    colorScheme = colorScheme
                )
            }
            isMedium -> {
                MediumProfessionalCardContent(
                    imageUrl = imageUrl,
                    name = name,
                    profession = profession,
                    location = location,
                    postedTime = postedTime,
                    professionalType = professionalType,
                    rating = rating,
                    jobsCompleted = jobsCompleted,
                    secondBadgeIcon = secondBadgeIcon,
                    secondBadgeText = secondBadgeText,
                    secondBadgeColor = secondBadgeColor,
                    onViewDetailsClick = onViewDetailsClick,
                    colorScheme = colorScheme
                )
            }
            isTwoColumnCompact -> {
                MobileProfessionalCardContent(
                    imageUrl = imageUrl,
                    name = name,
                    profession = profession,
                    location = location,
                    postedTime = postedTime,
                    professionalType = professionalType,
                    rating = rating,
                    jobsCompleted = jobsCompleted,
                    secondBadgeIcon = secondBadgeIcon,
                    secondBadgeText = secondBadgeText,
                    secondBadgeColor = secondBadgeColor,
                    onViewDetailsClick = onViewDetailsClick,
                    colorScheme = colorScheme,
                    isTwoColumn = true
                )
            }
            else -> {
                MobileProfessionalCardContent(
                    imageUrl = imageUrl,
                    name = name,
                    profession = profession,
                    location = location,
                    postedTime = postedTime,
                    professionalType = professionalType,
                    rating = rating,
                    jobsCompleted = jobsCompleted,
                    secondBadgeIcon = secondBadgeIcon,
                    secondBadgeText = secondBadgeText,
                    secondBadgeColor = secondBadgeColor,
                    onViewDetailsClick = onViewDetailsClick,
                    colorScheme = colorScheme,
                    isTwoColumn = false
                )
            }
        }
    }
}

@Composable
private fun MediumProfessionalCardContent(
    imageUrl: Any?,
    name: String,
    profession: String,
    location: String,
    postedTime: String,
    professionalType: ProfessionalType,
    rating: Float,
    jobsCompleted: Int,
    secondBadgeIcon: androidx.compose.ui.graphics.vector.ImageVector,
    secondBadgeText: String,
    secondBadgeColor: Color,
    onViewDetailsClick: () -> Unit,
    colorScheme: ColorScheme
) {
    val primaryColor = colorScheme.primary
    val tertiaryColor = colorScheme.tertiary
    val onSurfaceColor = colorScheme.onSurface
    val onSurfaceVariantColor = colorScheme.onSurfaceVariant

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Left: Profile image
        Surface(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape),
            color = primaryColor.copy(alpha = 0.1f)
        ) {
            OptimizedProfessionalImage(
                imageUrl = imageUrl,
                name = name,
                primaryColor = primaryColor
            )
        }

        // Middle: Content
        Column(
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Badges
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = primaryColor.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = "SERVICE",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium,
                        color = primaryColor,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = secondBadgeColor.copy(alpha = 0.1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Icon(
                            imageVector = secondBadgeIcon,
                            contentDescription = null,
                            tint = secondBadgeColor,
                            modifier = Modifier.size(8.dp)
                        )
                        Text(
                            text = secondBadgeText,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Medium,
                            color = secondBadgeColor
                        )
                    }
                }
            }

            Text(
                text = name,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = onSurfaceColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = profession,
                fontSize = 11.sp,
                color = onSurfaceVariantColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.LocationOn,
                    contentDescription = "Location",
                    tint = onSurfaceVariantColor,
                    modifier = Modifier.size(10.dp)
                )
                Text(
                    text = location,
                    fontSize = 10.sp,
                    color = onSurfaceVariantColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // Right: Rating and chevron
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Rating
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = "Rating",
                    tint = tertiaryColor,
                    modifier = Modifier.size(10.dp)
                )
                Text(
                    text = String.format("%.1f", rating),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = onSurfaceColor
                )
                Text(
                    text = "($jobsCompleted)",
                    fontSize = 9.sp,
                    color = onSurfaceVariantColor
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.AccessTime,
                    contentDescription = "Posted time",
                    tint = onSurfaceVariantColor.copy(alpha = 0.5f),
                    modifier = Modifier.size(10.dp)
                )
                Text(
                    text = postedTime,
                    fontSize = 9.sp,
                    color = onSurfaceVariantColor.copy(alpha = 0.6f)
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "View details",
                tint = tertiaryColor,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onViewDetailsClick() }
            )
        }
    }
}

@Composable
private fun MobileProfessionalCardContent(
    imageUrl: Any?,
    name: String,
    profession: String,
    location: String,
    postedTime: String,
    professionalType: ProfessionalType,
    rating: Float,
    jobsCompleted: Int,
    secondBadgeIcon: androidx.compose.ui.graphics.vector.ImageVector,
    secondBadgeText: String,
    secondBadgeColor: Color,
    onViewDetailsClick: () -> Unit,
    colorScheme: ColorScheme,
    isTwoColumn: Boolean = false
) {
    val paddingSize = if (isTwoColumn) 8.dp else 12.dp
    val imageSize = if (isTwoColumn) 40.dp else 48.dp
    val titleFontSize = if (isTwoColumn) 12.sp else 14.sp
    val subtitleFontSize = if (isTwoColumn) 9.sp else 11.sp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(paddingSize),
        horizontalArrangement = Arrangement.spacedBy(if (isTwoColumn) 6.dp else 10.dp)
    ) {
        // Left: Profile image
        Surface(
            modifier = Modifier
                .size(imageSize)
                .clip(CircleShape),
            color = colorScheme.primary.copy(alpha = 0.1f)
        ) {
            OptimizedProfessionalImage(
                imageUrl = imageUrl,
                name = name,
                primaryColor = colorScheme.primary
            )
        }

        // Middle: Content
        Column(
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            // Badges - only in 1-column mode
            if (!isTwoColumn) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = colorScheme.primary.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = "SERVICE",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Medium,
                            color = colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = secondBadgeColor.copy(alpha = 0.1f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Icon(
                                imageVector = secondBadgeIcon,
                                contentDescription = null,
                                tint = secondBadgeColor,
                                modifier = Modifier.size(6.dp)
                            )
                            Text(
                                text = if (professionalType == ProfessionalType.INDIVIDUAL) "IND" else "CO",
                                fontSize = 7.sp,
                                fontWeight = FontWeight.Medium,
                                color = secondBadgeColor
                            )
                        }
                    }
                }
            }

            Text(
                text = name,
                fontSize = titleFontSize,
                fontWeight = FontWeight.SemiBold,
                color = colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Profession - only in 1-column mode
            if (!isTwoColumn) {
                Text(
                    text = profession,
                    fontSize = subtitleFontSize,
                    color = colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Rating - only in 1-column mode
            if (!isTwoColumn) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Rating",
                        tint = colorScheme.tertiary,
                        modifier = Modifier.size(8.dp)
                    )
                    Text(
                        text = String.format("%.1f", rating),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium,
                        color = colorScheme.onSurface
                    )
                    Text(
                        text = "($jobsCompleted)",
                        fontSize = 8.sp,
                        color = colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Right: Time and chevron
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(if (isTwoColumn) 2.dp else 4.dp)
        ) {
            // Show time only in 1-column mode
            if (!isTwoColumn) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.AccessTime,
                        contentDescription = "Posted time",
                        tint = colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(8.dp)
                    )
                    Text(
                        text = postedTime,
                        fontSize = 8.sp,
                        color = colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "View details",
                tint = colorScheme.tertiary,
                modifier = Modifier
                    .size(if (isTwoColumn) 20.dp else 24.dp)
                    .clickable { onViewDetailsClick() }
            )
        }
    }
}

@Composable
private fun DesktopProfessionalCardContent(
    imageUrl: Any?,
    name: String,
    profession: String,
    location: String,
    postedTime: String,
    professionalType: ProfessionalType,
    rating: Float,
    jobsCompleted: Int,
    secondBadgeIcon: androidx.compose.ui.graphics.vector.ImageVector,
    secondBadgeText: String,
    secondBadgeColor: Color,
    onViewDetailsClick: () -> Unit,
    colorScheme: ColorScheme
) {
    val primaryColor = colorScheme.primary
    val onSurfaceColor = colorScheme.onSurface
    val onSurfaceVariantColor = colorScheme.onSurfaceVariant
    val surfaceColor = colorScheme.surface
    val secondaryColor = colorScheme.secondary
    val tertiaryColor = colorScheme.tertiary

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        surfaceColor,
                        surfaceColor.copy(alpha = 0.95f)
                    )
                )
            )
    ) {
        // Decorative pattern - subtle dots in corner
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(80.dp)
                .drawBehind {
                    val dotSize = 2.dp.toPx()
                    val spacing = 8.dp.toPx()
                    repeat(6) { row ->
                        repeat(6) { col ->
                            if (row * col % 2 == 0) {
                                drawCircle(
                                    color = primaryColor.copy(alpha = 0.06f),
                                    radius = dotSize,
                                    center = Offset(
                                        x = size.width - (col * spacing) - spacing,
                                        y = row * spacing
                                    )
                                )
                            }
                        }
                    }
                }
        ) {}

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Left side: Profile image with gradient ring
            Box(
                modifier = Modifier.size(56.dp),
                contentAlignment = Alignment.Center
            ) {
                // Gradient ring for featured professionals
                Surface(
                    modifier = Modifier.size(56.dp),
                    shape = CircleShape,
                    color = Color.Transparent,
                    border = androidx.compose.foundation.BorderStroke(
                        1.5.dp,
                        Brush.sweepGradient(
                            colors = listOf(
                                primaryColor,
                                secondaryColor,
                                tertiaryColor,
                                primaryColor
                            )
                        )
                    )
                ) {}

                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = CircleShape,
                    color = primaryColor.copy(alpha = 0.1f)
                ) {
                    OptimizedProfessionalImage(
                        imageUrl = imageUrl,
                        name = name,
                        primaryColor = primaryColor
                    )
                }
            }

            // Right side: Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Top row: Two badges
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = primaryColor.copy(alpha = 0.1f),
                        modifier = Modifier
                    ) {
                        Text(
                            text = "SERVICE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = primaryColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            maxLines = 1
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = secondBadgeColor.copy(alpha = 0.1f),
                        modifier = Modifier
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Icon(
                                imageVector = secondBadgeIcon,
                                contentDescription = null,
                                tint = secondBadgeColor,
                                modifier = Modifier.size(10.dp)
                            )
                            Text(
                                text = secondBadgeText,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = secondBadgeColor,
                                maxLines = 1
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Name
                Text(
                    text = name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = onSurfaceColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Profession
                Text(
                    text = profession,
                    fontSize = 12.sp,
                    color = onSurfaceVariantColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Location with icon
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = "Location",
                        tint = onSurfaceVariantColor,
                        modifier = Modifier.size(11.dp)
                    )
                    Text(
                        text = location,
                        fontSize = 11.sp,
                        color = onSurfaceVariantColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Rating and Jobs row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "Rating",
                            tint = tertiaryColor,
                            modifier = Modifier.size(11.dp)
                        )
                        Text(
                            text = String.format("%.1f", rating),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = onSurfaceColor
                        )
                        Text(
                            text = "($jobsCompleted jobs)",
                            fontSize = 10.sp,
                            color = onSurfaceVariantColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Divider line with gradient
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    primaryColor.copy(alpha = 0.1f),
                                    primaryColor.copy(alpha = 0.3f),
                                    primaryColor.copy(alpha = 0.1f)
                                )
                            )
                        )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // View profile link and posted time row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.clickable { onViewDetailsClick() }
                    ) {
                        Text(
                            text = "View profile",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = tertiaryColor
                        )
                        Text(
                            text = "→",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = tertiaryColor
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AccessTime,
                            contentDescription = "Posted time",
                            tint = onSurfaceVariantColor.copy(alpha = 0.6f),
                            modifier = Modifier.size(11.dp)
                        )
                        Text(
                            text = postedTime,
                            fontSize = 10.sp,
                            color = onSurfaceVariantColor.copy(alpha = 0.7f),
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

// OPTIMIZED IMAGE COMPOSABLE FOR PROFESSIONAL CARDS
@Composable
fun OptimizedProfessionalImage(
    imageUrl: Any?,
    name: String,
    primaryColor: Color
) {
    val context = LocalContext.current

    if (imageUrl != null && imageUrl.toString().isNotBlank()) {
        val imageRequest = remember(imageUrl) {
            ImageRequest.Builder(context)
                .data(imageUrl)
                .crossfade(true)
                .allowHardware(true)
                .size(360, 360)
                .diskCacheKey(imageUrl.toString())
                .memoryCacheKey(imageUrl.toString())
                .build()
        }

        AsyncImage(
            model = imageRequest,
            contentDescription = "$name profile",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            error = painterResource(id = R.drawable.job_placeholder1),
            fallback = painterResource(id = R.drawable.job_placeholder1)
        )
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            primaryColor.copy(alpha = 0.2f),
                            primaryColor.copy(alpha = 0.05f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = name
                    .split(" ")
                    .take(2)
                    .map { it.firstOrNull()?.toString() ?: "" }
                    .joinToString("")
                    .uppercase()
                    .take(2),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = primaryColor
            )
        }
    }
}

// Sample image URL for testing
private const val SAMPLE_IMAGE_URL = "https://images.unsplash.com/photo-1573497019940-1c28c88b4f3e?w=120&h=120&fit=crop"

// Light Theme Preview - Multiple Cards
@Preview(
    name = "Light Theme - Multiple Cards",
    showBackground = true,
    backgroundColor = 0xFFF7F9FE,
    heightDp = 650,
    widthDp = 400
)
@Composable
private fun PreviewModernProfessionalCardV2Light() {
    PivotaConnectTheme(darkTheme = false) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ModernProfessionalCardV2(
                imageUrl = SAMPLE_IMAGE_URL,
                name = "John Mwangi",
                profession = "Electrician",
                location = "Nairobi, Eastlands",
                postedTime = "2h ago",
                professionalType = ProfessionalType.INDIVIDUAL,
                rating = 4.8f,
                jobsCompleted = 127,
                onViewDetailsClick = {}
            )

            ModernProfessionalCardV2(
                imageUrl = null,
                name = "Nairobi Plumbers Ltd",
                profession = "Plumbing Services",
                location = "Nairobi, CBD",
                postedTime = "1d ago",
                professionalType = ProfessionalType.ORGANIZATION,
                rating = 4.5f,
                jobsCompleted = 342,
                onViewDetailsClick = {}
            )

            ModernProfessionalCardV2(
                imageUrl = SAMPLE_IMAGE_URL,
                name = "Sarah Wanjiku",
                profession = "House Cleaner",
                location = "Nairobi, Westlands",
                postedTime = "5h ago",
                professionalType = ProfessionalType.INDIVIDUAL,
                rating = 4.2f,
                jobsCompleted = 89,
                onViewDetailsClick = {}
            )
        }
    }
}

// Dark Theme Preview
@Preview(
    name = "Dark Theme - Single Card",
    showBackground = true,
    backgroundColor = 0xFF101418,
    heightDp = 220,
    widthDp = 400
)
@Composable
private fun PreviewModernProfessionalCardV2Dark() {
    PivotaConnectTheme(darkTheme = true) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            ModernProfessionalCardV2(
                imageUrl = SAMPLE_IMAGE_URL,
                name = "Peter Ochieng",
                profession = "Carpenter",
                location = "Nairobi, Industrial Area",
                postedTime = "Just now",
                professionalType = ProfessionalType.INDIVIDUAL,
                rating = 4.7f,
                jobsCompleted = 45,
                onViewDetailsClick = {}
            )
        }
    }
}