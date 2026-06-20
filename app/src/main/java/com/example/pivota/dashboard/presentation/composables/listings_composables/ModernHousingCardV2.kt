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
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Shower
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.SquareFoot
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun ElegantHousingCard(
    imageUrl: Any? = null,
    title: String,
    price: String,
    location: String,
    postedTime: String,
    propertyType: String,
    listingType: String,
    bedrooms: Int,
    bathrooms: Int,
    squareMeters: Int,
    isVerified: Boolean = false,
    isFavorite: Boolean = false,
    onFavoriteClick: () -> Unit = {},
    onViewDetailsClick: () -> Unit = {},
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
            .clickable { onViewDetailsClick() }
            .drawBehind {
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
                DesktopHousingCardContent(
                    imageUrl = imageUrl,
                    title = title,
                    price = price,
                    location = location,
                    postedTime = postedTime,
                    propertyType = propertyType,
                    listingType = listingType,
                    bedrooms = bedrooms,
                    bathrooms = bathrooms,
                    squareMeters = squareMeters,
                    isVerified = isVerified,
                    isFavorite = isFavorite,
                    onFavoriteClick = onFavoriteClick,
                    onViewDetailsClick = onViewDetailsClick,
                    colorScheme = colorScheme
                )
            }
            isMedium -> {
                MediumHousingCardContent(
                    imageUrl = imageUrl,
                    title = title,
                    price = price,
                    location = location,
                    postedTime = postedTime,
                    propertyType = propertyType,
                    listingType = listingType,
                    bedrooms = bedrooms,
                    bathrooms = bathrooms,
                    squareMeters = squareMeters,
                    isVerified = isVerified,
                    isFavorite = isFavorite,
                    onFavoriteClick = onFavoriteClick,
                    onViewDetailsClick = onViewDetailsClick,
                    colorScheme = colorScheme
                )
            }
            isTwoColumnCompact -> {
                MobileHousingCardContent(
                    imageUrl = imageUrl,
                    title = title,
                    price = price,
                    location = location,
                    postedTime = postedTime,
                    propertyType = propertyType,
                    listingType = listingType,
                    bedrooms = bedrooms,
                    bathrooms = bathrooms,
                    squareMeters = squareMeters,
                    isVerified = isVerified,
                    isFavorite = isFavorite,
                    onFavoriteClick = onFavoriteClick,
                    onViewDetailsClick = onViewDetailsClick,
                    colorScheme = colorScheme,
                    isTwoColumn = true
                )
            }
            else -> {
                MobileHousingCardContent(
                    imageUrl = imageUrl,
                    title = title,
                    price = price,
                    location = location,
                    postedTime = postedTime,
                    propertyType = propertyType,
                    listingType = listingType,
                    bedrooms = bedrooms,
                    bathrooms = bathrooms,
                    squareMeters = squareMeters,
                    isVerified = isVerified,
                    isFavorite = isFavorite,
                    onFavoriteClick = onFavoriteClick,
                    onViewDetailsClick = onViewDetailsClick,
                    colorScheme = colorScheme,
                    isTwoColumn = false
                )
            }
        }
    }
}

@Composable
private fun DesktopHousingCardContent(
    imageUrl: Any?,
    title: String,
    price: String,
    location: String,
    postedTime: String,
    propertyType: String,
    listingType: String,
    bedrooms: Int,
    bathrooms: Int,
    squareMeters: Int,
    isVerified: Boolean,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
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
            // Left side: Property image with gradient ring
            Box(
                modifier = Modifier.size(80.dp),
                contentAlignment = Alignment.Center
            ) {
                // Gradient ring
                Surface(
                    modifier = Modifier.size(80.dp),
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
                    modifier = Modifier.size(72.dp),
                    shape = RoundedCornerShape(10.dp),
                    color = primaryColor.copy(alpha = 0.1f)
                ) {
                    OptimizedHousingImage(
                        imageUrl = imageUrl,
                        title = title,
                        primaryColor = primaryColor
                    )
                }

                // Status Badge (SALE/RENT) on image
                Surface(
                    shape = RoundedCornerShape(
                        topStart = 0.dp,
                        topEnd = 0.dp,
                        bottomStart = 8.dp,
                        bottomEnd = 0.dp
                    ),
                    color = if (listingType == "For Sale") tertiaryColor else secondaryColor,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(0.dp)
                ) {
                    Text(
                        text = if (listingType == "For Sale") "SALE" else "RENT",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 4.dp
                        )
                    )
                }

                // Verified Badge
                if (isVerified) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(6.dp)
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF4CAF50)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Verified",
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }

                // Favorite Button
                IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(6.dp)
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.95f),
                                    Color.White.copy(alpha = 0.85f)
                                )
                            )
                        )
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color(0xFFE74C3C) else onSurfaceVariantColor,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            // Right side: Content - Full version
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Top row: Property Type + Listing Type badges
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = primaryColor.copy(alpha = 0.1f),
                    ) {
                        Text(
                            text = propertyType.uppercase(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = primaryColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            maxLines = 1
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = if (listingType == "For Sale")
                            tertiaryColor.copy(alpha = 0.1f)
                        else
                            secondaryColor.copy(alpha = 0.1f),
                    ) {
                        Text(
                            text = if (listingType == "For Sale") "FOR SALE" else "FOR RENT",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (listingType == "For Sale") tertiaryColor else secondaryColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            maxLines = 1
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = onSurfaceColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Price
                Text(
                    text = price,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Location
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

                // Features: Bedrooms, Bathrooms, Square Meters
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    HousingFeatureCompact(
                        icon = Icons.Filled.Bed,
                        label = "$bedrooms",
                        color = onSurfaceVariantColor
                    )
                    HousingFeatureCompact(
                        icon = Icons.Filled.Shower,
                        label = "$bathrooms",
                        color = onSurfaceVariantColor
                    )
                    HousingFeatureCompact(
                        icon = Icons.Outlined.SquareFoot,
                        label = "$squareMeters m²",
                        color = onSurfaceVariantColor
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

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

                Spacer(modifier = Modifier.height(6.dp))

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

@Composable
private fun MediumHousingCardContent(
    imageUrl: Any?,
    title: String,
    price: String,
    location: String,
    postedTime: String,
    propertyType: String,
    listingType: String,
    bedrooms: Int,
    bathrooms: Int,
    squareMeters: Int,
    isVerified: Boolean,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onViewDetailsClick: () -> Unit,
    colorScheme: ColorScheme
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Image
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(colorScheme.primary.copy(alpha = 0.1f))
        ) {
            OptimizedHousingImage(
                imageUrl = imageUrl,
                title = title,
                primaryColor = colorScheme.primary
            )

            if (isVerified) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF4CAF50)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Verified",
                        tint = Color.White,
                        modifier = Modifier.size(10.dp)
                    )
                }
            }
        }

        // Content
        Column(
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = colorScheme.primary.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = propertyType.uppercase(),
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Medium,
                        color = colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = if (listingType == "For Sale")
                        colorScheme.tertiary.copy(alpha = 0.1f)
                    else
                        colorScheme.secondary.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = if (listingType == "For Sale") "SALE" else "RENT",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (listingType == "For Sale") colorScheme.tertiary else colorScheme.secondary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = price,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = colorScheme.primary,
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

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                HousingFeatureCompact(
                    icon = Icons.Filled.Bed,
                    label = "$bedrooms",
                    color = colorScheme.onSurfaceVariant,
                    small = true
                )
                HousingFeatureCompact(
                    icon = Icons.Filled.Shower,
                    label = "$bathrooms",
                    color = colorScheme.onSurfaceVariant,
                    small = true
                )
                HousingFeatureCompact(
                    icon = Icons.Outlined.SquareFoot,
                    label = "$squareMeters",
                    color = colorScheme.onSurfaceVariant,
                    small = true
                )
            }
        }

        // Right Column
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            IconButton(
                onClick = onFavoriteClick,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (isFavorite) Color(0xFFE74C3C) else colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
            }

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
private fun MobileHousingCardContent(
    imageUrl: Any?,
    title: String,
    price: String,
    location: String,
    postedTime: String,
    propertyType: String,
    listingType: String,
    bedrooms: Int,
    bathrooms: Int,
    squareMeters: Int,
    isVerified: Boolean,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onViewDetailsClick: () -> Unit,
    colorScheme: ColorScheme,
    isTwoColumn: Boolean = false
) {
    val paddingSize = if (isTwoColumn) 8.dp else 12.dp
    val imageSize = if (isTwoColumn) 56.dp else 64.dp
    val titleFontSize = if (isTwoColumn) 12.sp else 14.sp
    val priceFontSize = if (isTwoColumn) 13.sp else 15.sp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(paddingSize),
        horizontalArrangement = Arrangement.spacedBy(if (isTwoColumn) 6.dp else 10.dp)
    ) {
        // Image
        Box(
            modifier = Modifier
                .size(imageSize)
                .clip(RoundedCornerShape(if (isTwoColumn) 6.dp else 8.dp))
                .background(colorScheme.primary.copy(alpha = 0.1f))
        ) {
            OptimizedHousingImage(
                imageUrl = imageUrl,
                title = title,
                primaryColor = colorScheme.primary
            )

            if (isVerified) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(3.dp)
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF4CAF50)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Verified",
                        tint = Color.White,
                        modifier = Modifier.size(8.dp)
                    )
                }
            }
        }

        // Content
        Column(
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            if (!isTwoColumn) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = colorScheme.primary.copy(alpha = 0.08f)
                    ) {
                        Text(
                            text = propertyType.uppercase().take(6),
                            fontSize = 7.sp,
                            fontWeight = FontWeight.Medium,
                            color = colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = if (listingType == "For Sale")
                            colorScheme.tertiary.copy(alpha = 0.08f)
                        else
                            colorScheme.secondary.copy(alpha = 0.08f)
                    ) {
                        Text(
                            text = if (listingType == "For Sale") "SALE" else "RENT",
                            fontSize = 7.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (listingType == "For Sale") colorScheme.tertiary else colorScheme.secondary,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }
            }

            Text(
                text = title,
                fontSize = titleFontSize,
                fontWeight = FontWeight.SemiBold,
                color = colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = price,
                fontSize = priceFontSize,
                fontWeight = FontWeight.Bold,
                color = colorScheme.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            if (!isTwoColumn) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = "Location",
                        tint = colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(8.dp)
                    )
                    Text(
                        text = location,
                        fontSize = 8.sp,
                        color = colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            if (!isTwoColumn) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    HousingFeatureCompact(
                        icon = Icons.Filled.Bed,
                        label = "$bedrooms",
                        color = colorScheme.onSurfaceVariant,
                        small = true,
                        tiny = true
                    )
                    HousingFeatureCompact(
                        icon = Icons.Filled.Shower,
                        label = "$bathrooms",
                        color = colorScheme.onSurfaceVariant,
                        small = true,
                        tiny = true
                    )
                    HousingFeatureCompact(
                        icon = Icons.Outlined.SquareFoot,
                        label = "$squareMeters",
                        color = colorScheme.onSurfaceVariant,
                        small = true,
                        tiny = true
                    )
                }
            }
        }

        // Right Column
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(if (isTwoColumn) 2.dp else 4.dp)
        ) {
            IconButton(
                onClick = onFavoriteClick,
                modifier = Modifier.size(if (isTwoColumn) 24.dp else 28.dp)
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (isFavorite) Color(0xFFE74C3C) else colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(if (isTwoColumn) 14.dp else 16.dp)
                )
            }

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
private fun HousingFeatureCompact(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: Color,
    small: Boolean = false,
    tiny: Boolean = false
) {
    val iconSize = when {
        tiny -> 10.dp
        small -> 12.dp
        else -> 14.dp
    }
    val textSize = when {
        tiny -> 8.sp
        small -> 9.sp
        else -> 11.sp
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(if (tiny) 2.dp else if (small) 3.dp else 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(iconSize)
        )
        Text(
            text = label,
            fontSize = textSize,
            fontWeight = FontWeight.Medium,
            color = color
        )
    }
}

@Composable
fun OptimizedHousingImage(
    imageUrl: Any?,
    title: String,
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
            contentDescription = "$title image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            error = painterResource(id = R.drawable.houses),
            fallback = painterResource(id = R.drawable.houses)
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
                text = title
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

// ======================================================
// SKELETON LOADING
// ======================================================

@Composable
fun ElegantHousingCardSkeleton(
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
            isWide -> DesktopHousingCardSkeleton(colorScheme)
            isMedium -> MediumHousingCardSkeleton(colorScheme)
            isTwoColumnCompact -> MobileHousingCardSkeleton(colorScheme, isTwoColumn = true)
            else -> MobileHousingCardSkeleton(colorScheme, isTwoColumn = false)
        }
    }
}

@Composable
private fun DesktopHousingCardSkeleton(colorScheme: ColorScheme) {
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
            // Left side: Image skeleton with gradient ring
            Box(
                modifier = Modifier.size(80.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier.size(80.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color.Transparent
                ) {}

                Surface(
                    modifier = Modifier.size(72.dp),
                    shape = RoundedCornerShape(10.dp),
                    color = colorScheme.surfaceVariant
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .shimmerEffectHousing()
                    )
                }
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
                            .shimmerEffectHousing(),
                        shape = RoundedCornerShape(4.dp),
                        color = colorScheme.surfaceVariant
                    ) {}
                    Surface(
                        modifier = Modifier
                            .width(60.dp)
                            .height(20.dp)
                            .shimmerEffectHousing(),
                        shape = RoundedCornerShape(4.dp),
                        color = colorScheme.surfaceVariant
                    ) {}
                }

                // Title skeleton
                Surface(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(18.dp)
                        .shimmerEffectHousing(),
                    shape = RoundedCornerShape(4.dp),
                    color = colorScheme.surfaceVariant
                ) {}

                // Price skeleton
                Surface(
                    modifier = Modifier
                        .fillMaxWidth(0.3f)
                        .height(20.dp)
                        .shimmerEffectHousing(),
                    shape = RoundedCornerShape(4.dp),
                    color = colorScheme.surfaceVariant
                ) {}

                // Location skeleton
                Surface(
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .height(14.dp)
                        .shimmerEffectHousing(),
                    shape = RoundedCornerShape(4.dp),
                    color = colorScheme.surfaceVariant
                ) {}

                // Features skeleton
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    repeat(3) {
                        Surface(
                            modifier = Modifier
                                .width(50.dp)
                                .height(14.dp)
                                .shimmerEffectHousing(),
                            shape = RoundedCornerShape(4.dp),
                            color = colorScheme.surfaceVariant
                        ) {}
                    }
                }

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
                            .shimmerEffectHousing(),
                        shape = RoundedCornerShape(4.dp),
                        color = colorScheme.surfaceVariant
                    ) {}
                    Surface(
                        modifier = Modifier
                            .width(60.dp)
                            .height(14.dp)
                            .shimmerEffectHousing(),
                        shape = RoundedCornerShape(4.dp),
                        color = colorScheme.surfaceVariant
                    ) {}
                }
            }
        }
    }
}

@Composable
private fun MediumHousingCardSkeleton(colorScheme: ColorScheme) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Image skeleton
        Surface(
            modifier = Modifier
                .size(72.dp)
                .shimmerEffectHousing(),
            shape = RoundedCornerShape(8.dp),
            color = colorScheme.surfaceVariant
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
                        .shimmerEffectHousing(),
                    shape = RoundedCornerShape(4.dp),
                    color = colorScheme.surfaceVariant
                ) {}
                Surface(
                    modifier = Modifier
                        .width(50.dp)
                        .height(16.dp)
                        .shimmerEffectHousing(),
                    shape = RoundedCornerShape(4.dp),
                    color = colorScheme.surfaceVariant
                ) {}
            }

            // Title skeleton
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(16.dp)
                    .shimmerEffectHousing(),
                shape = RoundedCornerShape(4.dp),
                color = colorScheme.surfaceVariant
            ) {}

            // Price skeleton
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.3f)
                    .height(18.dp)
                    .shimmerEffectHousing(),
                shape = RoundedCornerShape(4.dp),
                color = colorScheme.surfaceVariant
            ) {}

            // Location skeleton
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .height(12.dp)
                    .shimmerEffectHousing(),
                shape = RoundedCornerShape(4.dp),
                color = colorScheme.surfaceVariant
            ) {}

            // Features skeleton
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(3) {
                    Surface(
                        modifier = Modifier
                            .width(40.dp)
                            .height(12.dp)
                            .shimmerEffectHousing(),
                        shape = RoundedCornerShape(4.dp),
                        color = colorScheme.surfaceVariant
                    ) {}
                }
            }
        }

        // Right side skeletons
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Surface(
                modifier = Modifier
                    .size(28.dp)
                    .shimmerEffectHousing(),
                shape = CircleShape,
                color = colorScheme.surfaceVariant
            ) {}
            Surface(
                modifier = Modifier
                    .width(50.dp)
                    .height(12.dp)
                    .shimmerEffectHousing(),
                shape = RoundedCornerShape(4.dp),
                color = colorScheme.surfaceVariant
            ) {}
            Surface(
                modifier = Modifier
                    .size(24.dp)
                    .shimmerEffectHousing(),
                shape = CircleShape,
                color = colorScheme.surfaceVariant
            ) {}
        }
    }
}

@Composable
private fun MobileHousingCardSkeleton(
    colorScheme: ColorScheme,
    isTwoColumn: Boolean = false
) {
    val paddingSize = if (isTwoColumn) 8.dp else 12.dp
    val imageSize = if (isTwoColumn) 56.dp else 64.dp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(paddingSize),
        horizontalArrangement = Arrangement.spacedBy(if (isTwoColumn) 6.dp else 10.dp)
    ) {
        // Image skeleton
        Surface(
            modifier = Modifier
                .size(imageSize)
                .shimmerEffectHousing(),
            shape = RoundedCornerShape(if (isTwoColumn) 6.dp else 8.dp),
            color = colorScheme.surfaceVariant
        ) {}

        // Content skeletons
        Column(
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            if (!isTwoColumn) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .width(40.dp)
                            .height(12.dp)
                            .shimmerEffectHousing(),
                        shape = RoundedCornerShape(4.dp),
                        color = colorScheme.surfaceVariant
                    ) {}
                    Surface(
                        modifier = Modifier
                            .width(35.dp)
                            .height(12.dp)
                            .shimmerEffectHousing(),
                        shape = RoundedCornerShape(4.dp),
                        color = colorScheme.surfaceVariant
                    ) {}
                }
            }

            // Title skeleton
            Surface(
                modifier = Modifier
                    .fillMaxWidth(if (isTwoColumn) 0.9f else 0.7f)
                    .height(if (isTwoColumn) 14.dp else 16.dp)
                    .shimmerEffectHousing(),
                shape = RoundedCornerShape(4.dp),
                color = colorScheme.surfaceVariant
            ) {}

            // Price skeleton
            Surface(
                modifier = Modifier
                    .fillMaxWidth(if (isTwoColumn) 0.5f else 0.3f)
                    .height(if (isTwoColumn) 14.dp else 16.dp)
                    .shimmerEffectHousing(),
                shape = RoundedCornerShape(4.dp),
                color = colorScheme.surfaceVariant
            ) {}

            if (!isTwoColumn) {
                // Features skeleton
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    repeat(3) {
                        Surface(
                            modifier = Modifier
                                .width(30.dp)
                                .height(10.dp)
                                .shimmerEffectHousing(),
                            shape = RoundedCornerShape(4.dp),
                            color = colorScheme.surfaceVariant
                        ) {}
                    }
                }
            }
        }

        // Right side skeletons
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(if (isTwoColumn) 2.dp else 4.dp)
        ) {
            Surface(
                modifier = Modifier
                    .size(if (isTwoColumn) 24.dp else 28.dp)
                    .shimmerEffectHousing(),
                shape = CircleShape,
                color = colorScheme.surfaceVariant
            ) {}

            if (!isTwoColumn) {
                Surface(
                    modifier = Modifier
                        .width(40.dp)
                        .height(10.dp)
                        .shimmerEffectHousing(),
                    shape = RoundedCornerShape(4.dp),
                    color = colorScheme.surfaceVariant
                ) {}
            }

            Surface(
                modifier = Modifier
                    .size(if (isTwoColumn) 20.dp else 24.dp)
                    .shimmerEffectHousing(),
                shape = CircleShape,
                color = colorScheme.surfaceVariant
            ) {}
        }
    }
}

// ======================================================
// SHIMMER EFFECT EXTENSION
// ======================================================

@Composable
fun Modifier.shimmerEffectHousing(): Modifier {
    return this.drawBehind {
        val shimmerWidth = size.width * 0.5f
        val startX = -shimmerWidth
        val endX = size.width + shimmerWidth

        val brush = Brush.linearGradient(
            colors = listOf(
                Color.Transparent,
                Color.White.copy(alpha = 0.1f),
                Color.White.copy(alpha = 0.2f),
                Color.White.copy(alpha = 0.1f),
                Color.Transparent
            ),
            start = Offset(startX, 0f),
            end = Offset(endX, 0f)
        )

        drawRect(
            brush = brush,
            topLeft = Offset(0f, 0f),
            size = size
        )
    }
}

// ======================================================
// PREVIEWS
// ======================================================

private const val SAMPLE_IMAGE_URL =
    "https://images.unsplash.com/photo-1560448204-e02f11c3d0e2?w=200&h=200&fit=crop"

@Preview(
    name = "Light - List",
    showBackground = true,
    backgroundColor = 0xFFF5F7FA,
    heightDp = 800,
    widthDp = 400
)
@Composable
private fun PreviewElegantHousingCardLight() {
    PivotaConnectTheme(darkTheme = false) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ElegantHousingCard(
                imageUrl = SAMPLE_IMAGE_URL,
                title = "Luxury 2BR Apartment",
                price = "KES 45,000/month",
                location = "Nairobi, Westlands",
                postedTime = "2h ago",
                propertyType = "Apartment",
                listingType = "For Rent",
                bedrooms = 2,
                bathrooms = 2,
                squareMeters = 85,
                isVerified = true,
                isFavorite = false
            )

            ElegantHousingCard(
                imageUrl = null,
                title = "Spacious Family Home",
                price = "KES 12,500,000",
                location = "Nairobi, Karen",
                postedTime = "1d ago",
                propertyType = "House",
                listingType = "For Sale",
                bedrooms = 4,
                bathrooms = 3,
                squareMeters = 220,
                isVerified = true,
                isFavorite = true
            )

            ElegantHousingCard(
                imageUrl = SAMPLE_IMAGE_URL,
                title = "Cozy Bedsitter",
                price = "KES 8,500/month",
                location = "Nairobi, Umoja",
                postedTime = "3d ago",
                propertyType = "Bedsitter",
                listingType = "For Rent",
                bedrooms = 1,
                bathrooms = 1,
                squareMeters = 25,
                isVerified = false,
                isFavorite = false
            )
        }
    }
}

@Preview(
    name = "Dark - Single",
    showBackground = true,
    backgroundColor = 0xFF0D1117,
    heightDp = 220,
    widthDp = 400
)
@Composable
private fun PreviewElegantHousingCardDark() {
    PivotaConnectTheme(darkTheme = true) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            ElegantHousingCard(
                imageUrl = SAMPLE_IMAGE_URL,
                title = "Penthouse Suite",
                price = "KES 150,000/month",
                location = "Nairobi, Kilimani",
                postedTime = "Just now",
                propertyType = "Penthouse",
                listingType = "For Rent",
                bedrooms = 3,
                bathrooms = 3,
                squareMeters = 180,
                isVerified = true,
                isFavorite = false
            )
        }
    }
}

@Preview(
    name = "Skeleton Loading",
    showBackground = true,
    backgroundColor = 0xFFF5F7FA,
    heightDp = 600,
    widthDp = 400
)
@Composable
private fun PreviewSkeleton() {
    PivotaConnectTheme(darkTheme = false) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            repeat(3) {
                ElegantHousingCardSkeleton()
            }
        }
    }
}