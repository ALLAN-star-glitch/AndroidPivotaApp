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
    val primaryColor = MaterialTheme.colorScheme.primary
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariantColor = MaterialTheme.colorScheme.onSurfaceVariant
    val surfaceColor = MaterialTheme.colorScheme.surface
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val tertiaryColor = MaterialTheme.colorScheme.tertiary

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onViewDetailsClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = surfaceColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Left side: Image with rounded corners
            Box(
                modifier = Modifier
                    .size(90.dp, 90.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
                if (imageUrl != null && imageUrl.toString().isNotBlank()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(imageUrl)
                            .crossfade(true)
                            .size(Size(360, 360)) // Limit image size to prevent memory issues
                            .build(),
                        contentDescription = "$title image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        error = painterResource(id = R.drawable.houses),
                        fallback = painterResource(id = R.drawable.houses)
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(40.dp)
                        )
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
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleSmall
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
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = location,
                        fontSize = 11.sp,
                        color = onSurfaceVariantColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodySmall
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
                            modifier = Modifier.size(12.dp)
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
                            modifier = Modifier.size(12.dp)
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
                            modifier = Modifier.size(12.dp)
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

                // View details link only (no button)
                Text(
                    text = "View details →",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = tertiaryColor,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.clickable { onViewDetailsClick() }
                )
            }

            // Top right: Posted time
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.padding(top = 0.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.AccessTime,
                        contentDescription = "Posted time",
                        tint = onSurfaceVariantColor.copy(alpha = 0.6f),
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = postedTime,
                        fontSize = 10.sp,
                        color = onSurfaceVariantColor.copy(alpha = 0.7f),
                        maxLines = 1,
                        style = MaterialTheme.typography.labelSmall
                    )
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

            // For Rent - Room
            ModernHousingCardV2(
                imageUrl = null,
                title = "Single Room",
                price = "KES 3,500",
                location = "Nairobi, Eastlands",
                postedTime = "5h ago",
                propertyType = "Room",
                listingType = "For Rent",
                bedrooms = 1,
                bathrooms = 1,
                squareMeters = 15,
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

// Preview showing all property types
@Preview(
    name = "All Property Types",
    showBackground = true,
    heightDp = 650,
    widthDp = 400
)
@Composable
private fun PreviewAllPropertyTypes() {
    PivotaConnectTheme(darkTheme = false) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            listOf(
                Triple("Apartment", "For Rent", "KES 45,000"),
                Triple("House", "For Sale", "KES 8,500,000"),
                Triple("Bedsitter", "For Rent", "KES 8,000"),
                Triple("Room", "For Rent", "KES 3,500"),
                Triple("Penthouse", "For Rent", "KES 120,000"),
                Triple("Townhouse", "For Sale", "KES 6,200,000"),
                Triple("Studio", "For Rent", "KES 2,500/day"),
                Triple("Commercial", "For Sale", "KES 12,000,000")
            ).forEach { (type, listing, price) ->
                ModernHousingCardV2(
                    title = "$type in Nairobi",
                    price = price,
                    location = "Nairobi, Kenya",
                    postedTime = "Today",
                    propertyType = type,
                    listingType = listing,
                    bedrooms = if (type == "Bedsitter" || type == "Room" || type == "Studio") 1 else 2,
                    bathrooms = 1,
                    squareMeters = if (type == "Bedsitter") 25 else 80,
                    onViewDetailsClick = {}
                )
            }
        }
    }
}