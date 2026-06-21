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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ChevronRight
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
import com.example.pivota.dashboard.presentation.composables.client_general_composables.general.shimmer
import com.example.pivota.ui.theme.PivotaConnectTheme

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun ModernJobCardV2(
    modifier: Modifier = Modifier,
    imageUrl: Any? = null,
    jobTitle: String,
    companyName: String,
    location: String,
    postedTime: String,
    employmentType: String,
    jobType: String,
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
                // Large screens - Full featured
                DesktopJobCardContent(
                    imageUrl = imageUrl,
                    jobTitle = jobTitle,
                    companyName = companyName,
                    location = location,
                    postedTime = postedTime,
                    employmentType = employmentType,
                    jobType = jobType,
                    onViewDetailsClick = onViewDetailsClick,
                    colorScheme = colorScheme
                )
            }
            isMedium -> {
                // Medium screens - Full but slightly compact
                MediumJobCardContent(
                    imageUrl = imageUrl,
                    jobTitle = jobTitle,
                    companyName = companyName,
                    location = location,
                    postedTime = postedTime,
                    employmentType = employmentType,
                    jobType = jobType,
                    onViewDetailsClick = onViewDetailsClick,
                    colorScheme = colorScheme
                )
            }
            isTwoColumnCompact -> {
                // Compact screens with 2 columns - Very compact
                MobileJobCardContent(
                    imageUrl = imageUrl,
                    jobTitle = jobTitle,
                    companyName = companyName,
                    location = location,
                    postedTime = postedTime,
                    employmentType = employmentType,
                    jobType = jobType,
                    onViewDetailsClick = onViewDetailsClick,
                    colorScheme = colorScheme,
                    isTwoColumn = true
                )
            }
            else -> {
                // Compact screens with 1 column - Regular compact
                MobileJobCardContent(
                    imageUrl = imageUrl,
                    jobTitle = jobTitle,
                    companyName = companyName,
                    location = location,
                    postedTime = postedTime,
                    employmentType = employmentType,
                    jobType = jobType,
                    onViewDetailsClick = onViewDetailsClick,
                    colorScheme = colorScheme,
                    isTwoColumn = false
                )
            }
        }
    }
}

@Composable
private fun MediumJobCardContent(
    imageUrl: Any?,
    jobTitle: String,
    companyName: String,
    location: String,
    postedTime: String,
    employmentType: String,
    jobType: String,
    onViewDetailsClick: () -> Unit,
    colorScheme: ColorScheme
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Left: Company logo
        Surface(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp)),
            color = colorScheme.primary.copy(alpha = 0.1f)
        ) {
            OptimizedJobImage(
                imageUrl = imageUrl,
                companyName = companyName,
                primaryColor = colorScheme.primary
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
                    color = colorScheme.primary.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = employmentType.uppercase(),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium,
                        color = colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = colorScheme.secondary.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = jobType,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium,
                        color = colorScheme.secondary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Text(
                text = jobTitle,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = companyName,
                    fontSize = 11.sp,
                    color = colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "•",
                    fontSize = 11.sp,
                    color = colorScheme.onSurfaceVariant
                )
                Icon(
                    imageVector = Icons.Outlined.LocationOn,
                    contentDescription = "Location",
                    tint = colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(10.dp)
                )
                Text(
                    text = location,
                    fontSize = 10.sp,
                    color = colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // Right: Time and arrow
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.AccessTime,
                    contentDescription = "Posted time",
                    tint = colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.size(10.dp)
                )
                Text(
                    text = postedTime,
                    fontSize = 9.sp,
                    color = colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "View details",
                tint = colorScheme.tertiary,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onViewDetailsClick() }
            )
        }
    }
}

@Composable
private fun MobileJobCardContent(
    imageUrl: Any?,
    jobTitle: String,
    companyName: String,
    location: String,
    postedTime: String,
    employmentType: String,
    jobType: String,
    onViewDetailsClick: () -> Unit,
    colorScheme: ColorScheme,
    isTwoColumn: Boolean = false
) {
    val paddingSize = if (isTwoColumn) 8.dp else 12.dp
    val logoSize = if (isTwoColumn) 40.dp else 48.dp
    val titleFontSize = if (isTwoColumn) 12.sp else 14.sp
    val subtitleFontSize = if (isTwoColumn) 9.sp else 11.sp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(paddingSize),
        horizontalArrangement = Arrangement.spacedBy(if (isTwoColumn) 6.dp else 10.dp)
    ) {
        // Left: Small company logo
        Surface(
            modifier = Modifier
                .size(logoSize)
                .clip(RoundedCornerShape(if (isTwoColumn) 6.dp else 8.dp)),
            color = colorScheme.primary.copy(alpha = 0.1f)
        ) {
            OptimizedJobImage(
                imageUrl = imageUrl,
                companyName = companyName,
                primaryColor = colorScheme.primary
            )
        }

        // Middle: Job info - condensed
        Column(
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            // Job Title
            Text(
                text = jobTitle,
                fontSize = titleFontSize,
                fontWeight = FontWeight.SemiBold,
                color = colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Show company + location in 1-column, just company in 2-column
            if (isTwoColumn) {
                Text(
                    text = companyName,
                    fontSize = subtitleFontSize,
                    color = colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            } else {
                Text(
                    text = "$companyName • $location",
                    fontSize = subtitleFontSize,
                    color = colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Show badge only in 1-column mode
            if (!isTwoColumn) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = colorScheme.primary.copy(alpha = 0.08f)
                    ) {
                        Text(
                            text = "$employmentType • $jobType",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Medium,
                            color = colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            maxLines = 1
                        )
                    }
                }
            }
        }

        // Right: Time and arrow
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(if (isTwoColumn) 2.dp else 4.dp)
        ) {
            // Show time only in 1-column mode
            if (!isTwoColumn) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.AccessTime,
                        contentDescription = "Posted time",
                        tint = colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(10.dp)
                    )
                    Text(
                        text = postedTime,
                        fontSize = 9.sp,
                        color = colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        maxLines = 1
                    )
                }
            }

            // View chevron
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
private fun DesktopJobCardContent(
    imageUrl: Any?,
    jobTitle: String,
    companyName: String,
    location: String,
    postedTime: String,
    employmentType: String,
    jobType: String,
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
        // Decorative pattern - subtle dots in corner (desktop only)
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
            // Left side: Company logo with gradient ring
            Box(
                modifier = Modifier.size(56.dp),
                contentAlignment = Alignment.Center
            ) {
                // Gradient ring for verified/featured jobs
                Surface(
                    modifier = Modifier.size(56.dp),
                    shape = RoundedCornerShape(12.dp),
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
                    shape = RoundedCornerShape(10.dp),
                    color = primaryColor.copy(alpha = 0.1f)
                ) {
                    OptimizedJobImage(
                        imageUrl = imageUrl,
                        companyName = companyName,
                        primaryColor = primaryColor
                    )
                }
            }

            // Right side: Content - Full version
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Top row: Two badges - Employment Type + Job Type
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
                            text = employmentType.uppercase(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = primaryColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            maxLines = 1
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = secondaryColor.copy(alpha = 0.1f),
                        modifier = Modifier
                    ) {
                        Text(
                            text = jobType,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = secondaryColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = jobTitle,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = onSurfaceColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = companyName,
                    fontSize = 12.sp,
                    color = onSurfaceVariantColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))

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

                Spacer(modifier = Modifier.height(8.dp))

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
                            text = "View details",
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

// OPTIMIZED IMAGE COMPOSABLE FOR JOB CARDS
@Composable
fun OptimizedJobImage(
    imageUrl: Any?,
    companyName: String,
    primaryColor: Color
) {
    val context = LocalContext.current

    if (imageUrl != null && imageUrl.toString().isNotBlank()) {
        // Cache the image request to prevent recreation
        val imageRequest = remember(imageUrl) {
            ImageRequest.Builder(context)
                .data(imageUrl)
                .crossfade(true)
                .allowHardware(true) // Enable hardware bitmaps for better performance
                .size(360, 360) // Limit size to reduce memory
                .diskCacheKey(imageUrl.toString())
                .memoryCacheKey(imageUrl.toString())
                .build()
        }

        AsyncImage(
            model = imageRequest,
            contentDescription = "$companyName logo",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            error = painterResource(id = R.drawable.job_placeholder1),
            fallback = painterResource(id = R.drawable.job_placeholder1)
        )
    } else {
        // Lightweight placeholder - text-based initials
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
                text = companyName
                    .split(" ")
                    .take(2)
                    .map { it.firstOrNull()?.toString() ?: "" }
                    .joinToString("")
                    .uppercase()
                    .take(2),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = primaryColor
            )
        }
    }
}

// Add to your ModernJobCardV2 file

// ======================================================
// SKELETON LOADING CARDS
// ======================================================



// ======================================================
// SKELETON LOADING CARDS - Updated with shimmer from general package
// ======================================================

@Composable
fun JobCardSkeleton(
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val isWide = windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)
    val isMedium = windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.MEDIUM
    val isCompact = windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val isTwoColumnCompact = isCompact && (isLandscape || configuration.screenWidthDp >= 480)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(
                when {
                    isWide -> 180.dp
                    isMedium -> 140.dp
                    isTwoColumnCompact -> 120.dp
                    else -> 140.dp
                }
            ),
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
            isWide -> DesktopJobCardSkeleton(colorScheme)
            isMedium -> MediumJobCardSkeleton(colorScheme)
            isTwoColumnCompact -> MobileJobCardSkeleton(colorScheme, isTwoColumn = true)
            else -> MobileJobCardSkeleton(colorScheme, isTwoColumn = false)
        }
    }
}

@Composable
private fun DesktopJobCardSkeleton(
    colorScheme: ColorScheme
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        colorScheme.surface,
                        colorScheme.surface.copy(alpha = 0.95f)
                    )
                )
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Left side: Logo skeleton with gradient ring
            Box(
                modifier = Modifier.size(56.dp),
                contentAlignment = Alignment.Center
            ) {
                // Gradient ring skeleton
                Surface(
                    modifier = Modifier.size(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color.Transparent
                ) {}

                // ✅ Logo placeholder with shimmer - using Color.Transparent
                Surface(
                    modifier = Modifier
                        .size(48.dp)
                        .shimmer(shape = RoundedCornerShape(10.dp)),
                    shape = RoundedCornerShape(10.dp),
                    color = Color.Transparent
                ) {}
            }

            // Right side: Content skeletons
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Badges skeleton
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .width(70.dp)
                            .height(20.dp)
                            .shimmer(shape = RoundedCornerShape(4.dp)),
                        shape = RoundedCornerShape(4.dp),
                        color = Color.Transparent
                    ) {}
                    Surface(
                        modifier = Modifier
                            .width(60.dp)
                            .height(20.dp)
                            .shimmer(shape = RoundedCornerShape(4.dp)),
                        shape = RoundedCornerShape(4.dp),
                        color = Color.Transparent
                    ) {}
                }

                // Title skeleton
                Surface(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(18.dp)
                        .shimmer(shape = RoundedCornerShape(4.dp)),
                    shape = RoundedCornerShape(4.dp),
                    color = Color.Transparent
                ) {}

                // Company name skeleton
                Surface(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(14.dp)
                        .shimmer(shape = RoundedCornerShape(4.dp)),
                    shape = RoundedCornerShape(4.dp),
                    color = Color.Transparent
                ) {}

                // Location skeleton
                Surface(
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .height(14.dp)
                        .shimmer(shape = RoundedCornerShape(4.dp)),
                    shape = RoundedCornerShape(4.dp),
                    color = Color.Transparent
                ) {}

                Spacer(modifier = Modifier.weight(1f))

                // Divider skeleton
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    colorScheme.surfaceVariant,
                                    colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                )
                            )
                        )
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Footer skeletons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Surface(
                        modifier = Modifier
                            .width(80.dp)
                            .height(14.dp)
                            .shimmer(shape = RoundedCornerShape(4.dp)),
                        shape = RoundedCornerShape(4.dp),
                        color = Color.Transparent
                    ) {}
                    Surface(
                        modifier = Modifier
                            .width(60.dp)
                            .height(14.dp)
                            .shimmer(shape = RoundedCornerShape(4.dp)),
                        shape = RoundedCornerShape(4.dp),
                        color = Color.Transparent
                    ) {}
                }
            }
        }
    }
}

@Composable
private fun MediumJobCardSkeleton(
    colorScheme: ColorScheme
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Logo skeleton
        Surface(
            modifier = Modifier
                .size(48.dp)
                .shimmer(shape = RoundedCornerShape(8.dp)),
            shape = RoundedCornerShape(8.dp),
            color = Color.Transparent
        ) {}

        // Content skeletons
        Column(
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Badges skeleton
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .width(60.dp)
                        .height(16.dp)
                        .shimmer(shape = RoundedCornerShape(4.dp)),
                    shape = RoundedCornerShape(4.dp),
                    color = Color.Transparent
                ) {}
                Surface(
                    modifier = Modifier
                        .width(50.dp)
                        .height(16.dp)
                        .shimmer(shape = RoundedCornerShape(4.dp)),
                    shape = RoundedCornerShape(4.dp),
                    color = Color.Transparent
                ) {}
            }

            // Title skeleton
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(16.dp)
                    .shimmer(shape = RoundedCornerShape(4.dp)),
                shape = RoundedCornerShape(4.dp),
                color = Color.Transparent
            ) {}

            // Company + Location skeleton
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .width(80.dp)
                        .height(12.dp)
                        .shimmer(shape = RoundedCornerShape(4.dp)),
                    shape = RoundedCornerShape(4.dp),
                    color = Color.Transparent
                ) {}
                Surface(
                    modifier = Modifier
                        .width(60.dp)
                        .height(12.dp)
                        .shimmer(shape = RoundedCornerShape(4.dp)),
                    shape = RoundedCornerShape(4.dp),
                    color = Color.Transparent
                ) {}
            }
        }

        // Right side skeletons
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Surface(
                modifier = Modifier
                    .width(50.dp)
                    .height(12.dp)
                    .shimmer(shape = RoundedCornerShape(4.dp)),
                shape = RoundedCornerShape(4.dp),
                color = Color.Transparent
            ) {}
            Surface(
                modifier = Modifier
                    .size(24.dp)
                    .shimmer(shape = CircleShape),
                shape = CircleShape,
                color = Color.Transparent
            ) {}
        }
    }
}

@Composable
private fun MobileJobCardSkeleton(
    colorScheme: ColorScheme,
    isTwoColumn: Boolean = false
) {
    val paddingSize = if (isTwoColumn) 8.dp else 12.dp
    val logoSize = if (isTwoColumn) 40.dp else 48.dp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(paddingSize),
        horizontalArrangement = Arrangement.spacedBy(if (isTwoColumn) 6.dp else 10.dp)
    ) {
        // Logo skeleton
        Surface(
            modifier = Modifier
                .size(logoSize)
                .shimmer(shape = RoundedCornerShape(if (isTwoColumn) 6.dp else 8.dp)),
            shape = RoundedCornerShape(if (isTwoColumn) 6.dp else 8.dp),
            color = Color.Transparent
        ) {}

        // Content skeletons
        Column(
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            // Title skeleton
            Surface(
                modifier = Modifier
                    .fillMaxWidth(if (isTwoColumn) 0.9f else 0.7f)
                    .height(if (isTwoColumn) 14.dp else 16.dp)
                    .shimmer(shape = RoundedCornerShape(4.dp)),
                shape = RoundedCornerShape(4.dp),
                color = Color.Transparent
            ) {}

            // Company skeleton
            Surface(
                modifier = Modifier
                    .fillMaxWidth(if (isTwoColumn) 0.7f else 0.5f)
                    .height(if (isTwoColumn) 10.dp else 12.dp)
                    .shimmer(shape = RoundedCornerShape(4.dp)),
                shape = RoundedCornerShape(4.dp),
                color = Color.Transparent
            ) {}

            // Badge skeleton (only in 1-column mode)
            if (!isTwoColumn) {
                Surface(
                    modifier = Modifier
                        .width(80.dp)
                        .height(14.dp)
                        .shimmer(shape = RoundedCornerShape(4.dp)),
                    shape = RoundedCornerShape(4.dp),
                    color = Color.Transparent
                ) {}
            }
        }

        // Right side skeletons
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(if (isTwoColumn) 2.dp else 4.dp)
        ) {
            // Time skeleton (only in 1-column mode)
            if (!isTwoColumn) {
                Surface(
                    modifier = Modifier
                        .width(40.dp)
                        .height(10.dp)
                        .shimmer(shape = RoundedCornerShape(4.dp)),
                    shape = RoundedCornerShape(4.dp),
                    color = Color.Transparent
                ) {}
            }

            // Chevron skeleton
            Surface(
                modifier = Modifier
                    .size(if (isTwoColumn) 20.dp else 24.dp)
                    .shimmer(shape = CircleShape),
                shape = CircleShape,
                color = Color.Transparent
            ) {}
        }
    }
}

// ======================================================
// REMOVE the old shimmerEffect() function - no longer needed
// ======================================================
// Delete this function:
// fun Modifier.shimmerEffect(): Modifier = this.drawBehind { ... }

// Shimmer Effect Modifier
fun Modifier.shimmerEffect(): Modifier = this.drawBehind {
    val shimmerWidth = size.width * 0.3f
    val gradient = Brush.horizontalGradient(
        colors = listOf(
            Color.Transparent,
            Color.White.copy(alpha = 0.3f),
            Color.Transparent
        ),
        startX = -shimmerWidth,
        endX = -shimmerWidth + size.width
    )
    // This is a placeholder - you'll need to animate this
    drawRect(brush = gradient)
}

// Sample image URL for testing
private const val SAMPLE_IMAGE_URL = "https://images.unsplash.com/photo-1573164713988-8665fc963095?w=100&h=100&fit=crop"

// Light Theme Preview - Multiple Cards
@Preview(
    name = "Light Theme - Multiple Cards",
    showBackground = true,
    backgroundColor = 0xFFF7F9FE,
    heightDp = 750,
    widthDp = 400
)
@Composable
private fun PreviewModernJobCardV2Light() {
    PivotaConnectTheme(darkTheme = false) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Formal + Full-time
            ModernJobCardV2(
                imageUrl = SAMPLE_IMAGE_URL,
                jobTitle = "Senior Software Developer",
                companyName = "Safaricom PLC",
                location = "Nairobi, Westlands",
                postedTime = "2h ago",
                employmentType = "Formal",
                jobType = "Full-time",
                onViewDetailsClick = {}
            )

            // Informal + Gig
            ModernJobCardV2(
                imageUrl = null,
                jobTitle = "Welder & Fabricator",
                companyName = "Joseph's Welding Services",
                location = "Nairobi, Industrial Area",
                postedTime = "1d ago",
                employmentType = "Informal",
                jobType = "Gig",
                onViewDetailsClick = {}
            )

            // Formal + Remote
            ModernJobCardV2(
                imageUrl = SAMPLE_IMAGE_URL,
                jobTitle = "Mobile App Developer - Flutter",
                companyName = "Pivota Labs",
                location = "Remote",
                postedTime = "5h ago",
                employmentType = "Formal",
                jobType = "Remote",
                onViewDetailsClick = {}
            )

            // Informal + Contract
            ModernJobCardV2(
                imageUrl = null,
                jobTitle = "Electrician - Residential",
                companyName = "John's Electrical Services",
                location = "Nairobi, Eastlands",
                postedTime = "3d ago",
                employmentType = "Informal",
                jobType = "Contract",
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
private fun PreviewModernJobCardV2Dark() {
    PivotaConnectTheme(darkTheme = true) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            ModernJobCardV2(
                imageUrl = SAMPLE_IMAGE_URL,
                jobTitle = "Product Manager",
                companyName = "M-KOPA Solar",
                location = "Nairobi, Kilimani",
                postedTime = "Just now",
                employmentType = "Formal",
                jobType = "Hybrid",
                onViewDetailsClick = {}
            )
        }
    }
}