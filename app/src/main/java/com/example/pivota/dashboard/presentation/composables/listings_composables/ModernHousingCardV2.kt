package com.example.pivota.dashboard.presentation.composables.listings_composables

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.Shower
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.SquareFoot
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.size.Size
import com.example.pivota.R
import com.example.pivota.ui.theme.PivotaConnectTheme

@Composable
fun ModernHousingCardV2(
    imageUrl: Any? = null,
    title: String,
    price: String,
    location: String,
    postedTime: String,
    propertyType: String,      // e.g., "Apartment", "House", "Bedsitter", "Room"
    listingType: String,       // e.g., "For Rent" or "For Sale" only
    bedrooms: Int,
    bathrooms: Int,
    squareMeters: Int,
    isVerified: Boolean = false,
    onViewDetailsClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val primaryColor = colorScheme.primary
    val onSurfaceColor = colorScheme.onSurface
    val onSurfaceVariantColor = colorScheme.onSurfaceVariant
    val surfaceColor = colorScheme.surface
    val secondaryColor = colorScheme.secondary
    val tertiaryColor = colorScheme.tertiary

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onViewDetailsClick() }
            .drawBehind {
                // Subtle dotted border pattern
                val strokeWidth = 1f
                val pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
                drawRoundRect(
                    color = primaryColor.copy(alpha = 0.15f),
                    style = Stroke(width = strokeWidth, pathEffect = pathEffect),
                    cornerRadius = CornerRadius(12.dp.toPx(), 12.dp.toPx())
                )
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = surfaceColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
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
                // Left side: Image with gradient ring
                Box(
                    modifier = Modifier.size(90.dp, 90.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Gradient ring for featured/verified listings
                    Surface(
                        modifier = Modifier.size(90.dp, 90.dp),
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

                    Box(
                        modifier = Modifier
                            .size(84.dp, 84.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        if (imageUrl != null && imageUrl.toString().isNotBlank()) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(imageUrl)
                                    .crossfade(true)
                                    .size(Size(360, 360))
                                    .build(),
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
                                Icon(
                                    imageVector = Icons.Outlined.LocationOn,
                                    contentDescription = null,
                                    tint = primaryColor,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
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
                            color = if (listingType == "For Sale") tertiaryColor.copy(alpha = 0.1f) else secondaryColor.copy(alpha = 0.1f),
                            modifier = Modifier
                        ) {
                            Text(
                                text = listingType.uppercase(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (listingType == "For Sale") tertiaryColor else secondaryColor,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                maxLines = 1
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Title
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
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryColor,
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

                    // Property Features Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Bed,
                                contentDescription = "Bedrooms",
                                tint = onSurfaceVariantColor,
                                modifier = Modifier.size(11.dp)
                            )
                            Text(
                                text = bedrooms.toString(),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = onSurfaceColor
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Shower,
                                contentDescription = "Bathrooms",
                                tint = onSurfaceVariantColor,
                                modifier = Modifier.size(11.dp)
                            )
                            Text(
                                text = bathrooms.toString(),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = onSurfaceColor
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.SquareFoot,
                                contentDescription = "Square Meters",
                                tint = onSurfaceVariantColor,
                                modifier = Modifier.size(11.dp)
                            )
                            Text(
                                text = "$squareMeters m²",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = onSurfaceColor
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

                    // View details link and posted time row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // View details link
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

                        // Posted time
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
}

// Sample image URL for testing
private const val SAMPLE_IMAGE_URL = "https://images.unsplash.com/photo-1560448204-e02f11c3d0e2?w=120&h=120&fit=crop"

// Light Theme Preview - Multiple Cards
@Preview(
    name = "Light Theme - Multiple Cards",
    showBackground = true,
    backgroundColor = 0xFFF7F9FE,
    heightDp = 750,
    widthDp = 400
)
@Composable
private fun PreviewModernHousingCardV2Light() {
    PivotaConnectTheme(darkTheme = false) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // For Rent - Apartment
            ModernHousingCardV2(
                imageUrl = SAMPLE_IMAGE_URL,
                title = "Modern 2BR Apartment",
                price = "KES 45,000",
                location = "Nairobi, Westlands",
                postedTime = "2h ago",
                propertyType = "Apartment",
                listingType = "For Rent",
                bedrooms = 2,
                bathrooms = 2,
                squareMeters = 85,
                isVerified = true,
                onViewDetailsClick = {}
            )

            // For Sale - House
            ModernHousingCardV2(
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
                onViewDetailsClick = {}
            )

            // For Rent - Bedsitter
            ModernHousingCardV2(
                imageUrl = SAMPLE_IMAGE_URL,
                title = "Cozy Bedsitter",
                price = "KES 8,500",
                location = "Nairobi, Umoja",
                postedTime = "3d ago",
                propertyType = "Bedsitter",
                listingType = "For Rent",
                bedrooms = 1,
                bathrooms = 1,
                squareMeters = 25,
                isVerified = false,
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
    heightDp = 240,
    widthDp = 400
)
@Composable
private fun PreviewModernHousingCardV2Dark() {
    PivotaConnectTheme(darkTheme = true) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            ModernHousingCardV2(
                imageUrl = SAMPLE_IMAGE_URL,
                title = "Luxury Penthouse",
                price = "KES 150,000",
                location = "Nairobi, Kilimani",
                postedTime = "Just now",
                propertyType = "Penthouse",
                listingType = "For Rent",
                bedrooms = 3,
                bathrooms = 3,
                squareMeters = 180,
                isVerified = true,
                onViewDetailsClick = {}
            )
        }
    }
}