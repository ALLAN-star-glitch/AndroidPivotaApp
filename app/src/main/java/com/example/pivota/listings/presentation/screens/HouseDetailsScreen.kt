package com.example.pivota.listings.presentation.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Help
import androidx.compose.material.icons.outlined.Bathtub
import androidx.compose.material.icons.outlined.Bed
import androidx.compose.material.icons.outlined.Chair
import androidx.compose.material.icons.outlined.ChatBubble
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.LocalParking
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.SquareFoot
import androidx.compose.material.icons.outlined.Shower
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.window.core.layout.WindowSizeClass
import com.example.pivota.R
import com.example.pivota.core.presentations.composables.TopBar
import com.example.pivota.dashboard.presentation.state.HousingListingUiModel
import java.text.NumberFormat
import java.util.Locale
import androidx.compose.material3.Typography

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HouseDetailsScreen(
    housingListing: HousingListingUiModel,
    onNavigateBack: () -> Unit,
    onBookClick: (HousingListingUiModel) -> Unit = {}
) {
    // Add null check at the beginning
    if (housingListing == null) {
        LaunchedEffect(Unit) {
            onNavigateBack()
        }
        return
    }

    // Extract price value for display
    val priceValue = extractPriceValue(housingListing.price)
    val formattedPrice = NumberFormat.getInstance(Locale.US).format(priceValue)

    val windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val isWide = windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) ||
            windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)

    Scaffold(
        topBar = {
            TopBar(
                title = "House Details",
                onBack = onNavigateBack,
                icon = Icons.AutoMirrored.Outlined.Help
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surfaceContainer
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            // TODO handle contact button click
                        }
                    ) {
                        Text("Contact")
                    }

                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            onBookClick(housingListing)
                        }
                    ) {
                        Text("Book Viewing", color = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            }
        }
    ) { innerPadding ->
        if (isWide) {
            // TWO PANE LAYOUT (Tablet/Desktop)
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Left Pane - Image and Key Details
                HouseDetailsLeftPane(
                    housingListing = housingListing,
                    formattedPrice = formattedPrice,
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                )

                // Right Pane - Scrollable Details
                HouseDetailsRightPane(
                    housingListing = housingListing,
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                )
            }
        } else {
            // SINGLE PANE LAYOUT (Mobile) - EXACTLY as before
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Property Image
                if (housingListing.imageRes != null) {
                    Image(
                        painter = painterResource(id = housingListing.imageRes),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Fallback image
                    Image(
                        painter = painterResource(id = R.drawable.property_placeholder1),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Price
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "KES ",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = formattedPrice,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (housingListing.isForSale) "" else "/month",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Title
                Text(
                    text = housingListing.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Location
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = housingListing.location,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                // Verified badge if applicable
                if (housingListing.isVerified) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Icon(
                            Icons.Outlined.Verified,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            "Verified Property",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))

                // Room Info Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    RoomInfo(
                        icon = Icons.Outlined.Bed,
                        data = housingListing.bedrooms.toString(),
                        category = "Bedrooms",
                        modifier = Modifier.weight(1f)
                    )
                    RoomInfo(
                        icon = Icons.Outlined.Bathtub,
                        data = housingListing.bathrooms.toString(),
                        category = "Bathrooms",
                        modifier = Modifier.weight(1f)
                    )
                    RoomInfo(
                        icon = Icons.Outlined.Home,
                        data = housingListing.propertyType,
                        category = "Type",
                        modifier = Modifier.weight(1f)
                    )
                    RoomInfo(
                        icon = Icons.Outlined.Chair,
                        data = "Yes",
                        category = "Furnished",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))

                // Description
                Text(
                    text = "Description",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = housingListing.description,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))

                // Property Details
                Text(
                    text = "Property Details",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                DetailRow("Property Type", housingListing.propertyType)
                DetailRow("Square Meters", "${housingListing.squareMeters} m²")
                DetailRow("Status", if (housingListing.isForSale) "For Sale" else "For Rent")
                DetailRow("Rating", "${housingListing.rating} ★")

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))

                // Location Details
                Text(
                    text = "Location",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = housingListing.location,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Owner Section
                Owner(
                    name = "Property Owner",
                    imageLink = null
                )
            }
        }
    }
}

/* ───────── TWO PANE COMPONENTS (Added for larger screens) ───────── */

@Composable
private fun HouseDetailsLeftPane(
    housingListing: HousingListingUiModel,
    formattedPrice: String,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Property Image
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(colorScheme.surfaceVariant)
        ) {
            if (housingListing.imageRes != null) {
                Image(
                    painter = painterResource(id = housingListing.imageRes),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.property_placeholder1),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            // Verified Badge
            if (housingListing.isVerified) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp),
                    shape = CircleShape,
                    color = colorScheme.primary
                ) {
                    Icon(
                        Icons.Outlined.Verified,
                        contentDescription = "Verified",
                        tint = colorScheme.onPrimary,
                        modifier = Modifier
                            .padding(8.dp)
                            .size(20.dp)
                    )
                }
            }
        }

        // Price
        Column {
            Text(
                text = "Price",
                style = typography.labelLarge,
                color = colorScheme.onSurfaceVariant
            )
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "KES ",
                    color = colorScheme.primary,
                    style = typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = formattedPrice,
                    color = colorScheme.primary,
                    style = typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = if (housingListing.isForSale) "" else "/month",
                    style = typography.bodyLarge,
                    color = colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }

        // Title
        Text(
            text = housingListing.title,
            style = typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = colorScheme.onSurface
        )

        // Location
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.LocationOn,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = colorScheme.onSurfaceVariant
            )
            Text(
                text = housingListing.location,
                style = typography.bodyLarge,
                color = colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        // Rating if available
        if (housingListing.rating > 0) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Outlined.Star,
                    contentDescription = null,
                    tint = colorScheme.tertiary,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = String.format("%.1f", housingListing.rating),
                    style = typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Key Specs Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = colorScheme.surfaceContainer
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Key Specifications",
                    style = typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    SpecItem(
                        icon = Icons.Outlined.Bed,
                        value = housingListing.bedrooms.toString(),
                        label = "Beds",
                        colorScheme = colorScheme
                    )
                    SpecItem(
                        icon = Icons.Outlined.Shower,
                        value = housingListing.bathrooms.toString(),
                        label = "Baths",
                        colorScheme = colorScheme
                    )
                    SpecItem(
                        icon = Icons.Outlined.SquareFoot,
                        value = housingListing.squareMeters.toString(),
                        label = "m²",
                        colorScheme = colorScheme
                    )
                    SpecItem(
                        icon = Icons.Outlined.Chair,
                        value = "Yes",
                        label = "Furnished",
                        colorScheme = colorScheme
                    )
                }
            }
        }
    }
}

@Composable
private fun HouseDetailsRightPane(
    housingListing: HousingListingUiModel,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Description Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = colorScheme.surface
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Description",
                    style = typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = housingListing.description,
                    style = typography.bodyMedium,
                    color = colorScheme.onSurfaceVariant,
                    lineHeight = 20.sp
                )
            }
        }

        // Property Details Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = colorScheme.surface
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Property Details",
                    style = typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(16.dp))

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        DetailItem(
                            modifier = Modifier.weight(1f),
                            label = "Property Type",
                            valueText = housingListing.propertyType,
                            colorScheme = colorScheme,
                            typography = typography
                        )
                        DetailItem(
                            modifier = Modifier.weight(1f),
                            label = "Status",
                            valueText = if (housingListing.isForSale) "For Sale" else "For Rent",
                            colorScheme = colorScheme,
                            typography = typography
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        DetailItem(
                            modifier = Modifier.weight(1f),
                            label = "Square Meters",
                            valueText = "${housingListing.squareMeters} m²",
                            colorScheme = colorScheme,
                            typography = typography
                        )
                        DetailItem(
                            modifier = Modifier.weight(1f),
                            label = "Rating",
                            valueText = "${housingListing.rating} ★",
                            colorScheme = colorScheme,
                            typography = typography
                        )
                    }
                }
            }
        }

        // Facilities Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = colorScheme.surface
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Facilities",
                    style = typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(12.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Water", "Electricity", "Parking", "Wi-Fi", "Security", "Gym").forEach { amenity ->
                        Surface(
                            color = colorScheme.surfaceContainerLow,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, colorScheme.outlineVariant),
                            tonalElevation = 0.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val icon = when(amenity.lowercase()) {
                                    "water" -> Icons.Outlined.WaterDrop
                                    "electricity" -> Icons.Outlined.Bolt
                                    "parking" -> Icons.Outlined.LocalParking
                                    "wi-fi" -> Icons.Outlined.Wifi
                                    "security" -> Icons.Outlined.Security
                                    "gym" -> Icons.Outlined.FitnessCenter
                                    else -> Icons.Outlined.CheckCircle
                                }
                                Icon(
                                    icon,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    amenity,
                                    style = typography.bodyMedium,
                                    color = colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }

        // Location Details Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = colorScheme.surface
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Location",
                    style = typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Outlined.LocationOn,
                        contentDescription = null,
                        tint = colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = housingListing.location,
                        style = typography.bodyLarge,
                        color = colorScheme.onSurface
                    )
                }
            }
        }

        // Owner Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = colorScheme.surface
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.happyman),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .aspectRatio(1f),
                        contentScale = ContentScale.Crop,
                    )
                }

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Property Owner",
                        style = typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = colorScheme.onSurface
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Outlined.Verified,
                            tint = colorScheme.primary,
                            modifier = Modifier.size(16.dp),
                            contentDescription = null
                        )
                        Text(
                            "Verified Owner",
                            color = colorScheme.primary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }

                IconButton(
                    onClick = {
                        // TODO handle messaging
                    },
                ) {
                    Icon(
                        Icons.Outlined.ChatBubble,
                        contentDescription = "Message"
                    )
                }
            }
        }

        // Spacer at bottom for scroll
        Spacer(modifier = Modifier.height(32.dp))
    }
}

/* ───────── EXISTING COMPONENTS (Kept exactly as before) ───────── */

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun Owner(name: String, imageLink: String?) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.happyman),
                    contentDescription = null,
                    modifier = Modifier
                        .aspectRatio(1f),
                    contentScale = ContentScale.Crop,
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Outlined.Verified,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp),
                        contentDescription = null
                    )
                    Text(
                        "Verified Owner",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }

            IconButton(
                onClick = {
                    // TODO handle messaging
                },
            ) {
                Icon(
                    Icons.Outlined.ChatBubble,
                    contentDescription = "Message"
                )
            }
        }
    }
}

@Composable
fun RoomInfo(
    icon: ImageVector,
    data: String,
    category: String,
    modifier: Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = data,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = category,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
fun SpecItem(
    icon: ImageVector,
    value: String,
    label: String,
    colorScheme: ColorScheme
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = value,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = colorScheme.onSurface
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun DetailItem(
    modifier: Modifier = Modifier,
    label: String,
    valueText: String,
    colorScheme: ColorScheme,
    typography: Typography
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = typography.labelMedium,
            color = colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = valueText,
            style = typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = colorScheme.onSurface
        )
    }
}

// Helper function to extract price value
private fun extractPriceValue(priceString: String): Int {
    return try {
        val cleaned = priceString
            .replace("KES", "")
            .replace("KSh", "")
            .replace(",", "")
            .replace(" ", "")
            .trim()

        when {
            cleaned.endsWith("M", ignoreCase = true) -> {
                val number = cleaned.dropLast(1).toDouble()
                (number * 1_000_000).toInt()
            }
            cleaned.endsWith("K", ignoreCase = true) -> {
                val number = cleaned.dropLast(1).toDouble()
                (number * 1_000).toInt()
            }
            else -> cleaned.toIntOrNull() ?: 0
        }
    } catch (e: Exception) {
        0
    }
}