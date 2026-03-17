package com.example.pivota.admin.presentation.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.window.core.layout.WindowSizeClass
import coil3.compose.AsyncImage
import com.example.pivota.R
import com.example.pivota.core.presentations.composables.TopBar
import com.example.pivota.dashboard.domain.ListingStatus
import com.example.pivota.dashboard.presentation.state.HousingListingUiModel
import com.example.pivota.listings.domain.models.HousingPost
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AdminHouseDetailsScreen(
    housingListing: HousingListingUiModel,
    onBack: () -> Unit = {},
    onApprove: (String) -> Unit = {},
    onReject: (String) -> Unit = {},
    onEdit: (String) -> Unit = {},
    onDelete: (String) -> Unit = {}
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    // Convert to HousingPost if needed, or use HousingListingUiModel directly
    val priceValue = extractPriceValue(housingListing.price)
    val formattedPrice = NumberFormat.getInstance(Locale.US).format(priceValue)

    val windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val isWide = windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) ||
            windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)

    Scaffold(
        topBar = {
            TopBar(
                icon = Icons.Default.Edit,
                title = "House Details (Admin)",
                onBack = onBack
            )
        }
    ) { paddingValues ->
        if (isWide) {
            // TWO PANE LAYOUT (Tablet/Desktop)
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Left Pane - Image and Key Details
                LeftDetailsPane(
                    housingListing = housingListing,
                    formattedPrice = formattedPrice,
                    colorScheme = colorScheme,
                    typography = typography,
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                )

                // Right Pane - Scrollable Content
                RightDetailsPane(
                    housingListing = housingListing,
                    formattedPrice = formattedPrice,
                    colorScheme = colorScheme,
                    typography = typography,
                    onApprove = onApprove,
                    onReject = onReject,
                    onEdit = onEdit,
                    onDelete = onDelete,
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                )
            }
        } else {
            // SINGLE PANE LAYOUT (Mobile)
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 1. Image Banner
                item {
                    ImageBanner(
                        housingListing = housingListing,
                        colorScheme = colorScheme
                    )
                }

                // 2. Header Information
                item {
                    HeaderInfo(
                        housingListing = housingListing,
                        colorScheme = colorScheme,
                        typography = typography
                    )
                }

                // 3. Owner Information
                item {
                    OwnerInfo(
                        housingListing = housingListing,
                        colorScheme = colorScheme,
                        typography = typography
                    )
                }

                // 4. Property Details Card
                item {
                    PropertyDetailsCard(
                        housingListing = housingListing,
                        formattedPrice = formattedPrice,
                        colorScheme = colorScheme,
                        typography = typography
                    )
                }

                // 5. Facilities Card
                item {
                    FacilitiesCard(
                        colorScheme = colorScheme,
                        typography = typography
                    )
                }

                // 6. Description Card
                item {
                    DescriptionCard(
                        housingListing = housingListing,
                        colorScheme = colorScheme,
                        typography = typography
                    )
                }

                // 7. Activity Logs Card
                item {
                    ActivityLogsCard(
                        housingListing = housingListing,
                        colorScheme = colorScheme,
                        typography = typography
                    )
                }

                // 8. Action Buttons
                item {
                    ActionButtons(
                        housingListing = housingListing,
                        onApprove = onApprove,
                        onReject = onReject,
                        onEdit = onEdit,
                        onDelete = onDelete,
                        colorScheme = colorScheme,
                        typography = typography
                    )
                }
            }
        }
    }
}

/* ───────── TWO PANE COMPONENTS ───────── */

@Composable
private fun LeftDetailsPane(
    housingListing: HousingListingUiModel,
    formattedPrice: String,
    colorScheme: ColorScheme,
    typography: Typography,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colorScheme.surface)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Image
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(colorScheme.surfaceVariant)
        ) {
            if (housingListing.imageRes != null) {
                AsyncImage(
                    model = housingListing.imageRes,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(64.dp),
                    tint = colorScheme.onSurfaceVariant
                )
            }

            // Status Badge
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp),
                color = when (housingListing.status) {
                    ListingStatus.AVAILABLE -> colorScheme.primary
                    ListingStatus.PENDING -> colorScheme.tertiary
                    ListingStatus.RENTED, ListingStatus.SOLD -> colorScheme.error
                    ListingStatus.INACTIVE -> colorScheme.onSurfaceVariant
                    else -> colorScheme.secondary
                },
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = housingListing.status.name.lowercase().replaceFirstChar { it.uppercase() },
                    color = colorScheme.onPrimary,
                    style = typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
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
                Icons.Outlined.LocationOn,
                contentDescription = "Location",
                modifier = Modifier.size(18.dp),
                tint = colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = housingListing.location,
                style = typography.bodyLarge,
                color = colorScheme.onSurfaceVariant
            )
        }

        // Price
        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = colorScheme.primary,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 32.sp
                    )
                ) {
                    append("KES $formattedPrice")
                }
                withStyle(
                    style = SpanStyle(
                        color = colorScheme.onSurfaceVariant,
                        fontSize = 16.sp
                    )
                ) {
                    append(if (housingListing.isForSale) "" else " /month")
                }
            }
        )

        // Key Specs Grid
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
                        value = "${housingListing.bedrooms}",
                        label = "Beds",
                        colorScheme = colorScheme
                    )
                    SpecItem(
                        icon = Icons.Outlined.Shower,
                        value = "${housingListing.bathrooms}",
                        label = "Baths",
                        colorScheme = colorScheme
                    )
                    SpecItem(
                        icon = Icons.Outlined.SquareFoot,
                        value = "${housingListing.squareMeters}",
                        label = "m²",
                        colorScheme = colorScheme
                    )
                }
            }
        }
    }
}

@Composable
private fun RightDetailsPane(
    housingListing: HousingListingUiModel,
    formattedPrice: String,
    colorScheme: ColorScheme,
    typography: Typography,
    onApprove: (String) -> Unit,
    onReject: (String) -> Unit,
    onEdit: (String) -> Unit,
    onDelete: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Owner Information
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = colorScheme.surface
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Owner Information",
                        style = typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Avatar
                        Surface(
                            modifier = Modifier.size(56.dp),
                            shape = CircleShape,
                            color = colorScheme.primaryContainer
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = null,
                                    tint = colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    "Property Owner",
                                    fontWeight = FontWeight.Bold,
                                    style = typography.titleMedium,
                                    color = colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                if (housingListing.isVerified) {
                                    Icon(
                                        Icons.Default.Verified,
                                        contentDescription = "Verified",
                                        tint = colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            Text(
                                if (housingListing.isVerified) "Verified Owner" else "Unverified Owner",
                                style = typography.bodyMedium,
                                color = colorScheme.onSurfaceVariant
                            )
                        }

                        Row {
                            IconButton(
                                onClick = { },
                                modifier = Modifier
                                    .background(colorScheme.surfaceVariant, CircleShape)
                                    .size(40.dp)
                            ) {
                                Icon(
                                    Icons.Default.Phone,
                                    contentDescription = "Call",
                                    tint = colorScheme.onSurface,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            IconButton(
                                onClick = { },
                                modifier = Modifier
                                    .background(colorScheme.surfaceVariant, CircleShape)
                                    .size(40.dp)
                            ) {
                                Icon(
                                    Icons.Outlined.Email,
                                    contentDescription = "Message",
                                    tint = colorScheme.onSurface,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Property Details
        item {
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

                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
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
                                label = "Listing Type",
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
                                label = "Bedrooms",
                                valueText = "${housingListing.bedrooms}",
                                colorScheme = colorScheme,
                                typography = typography
                            )
                            DetailItem(
                                modifier = Modifier.weight(1f),
                                label = "Bathrooms",
                                valueText = "${housingListing.bathrooms}",
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
                                label = "Size",
                                valueText = "${housingListing.squareMeters} m²",
                                colorScheme = colorScheme,
                                typography = typography
                            )
                            DetailItem(
                                modifier = Modifier.weight(1f),
                                label = "Rating",
                                valueText = String.format("%.1f", housingListing.rating),
                                colorScheme = colorScheme,
                                typography = typography
                            )
                        }
                    }
                }
            }
        }

        // Description
        item {
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
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = housingListing.description.ifEmpty { "No description provided" },
                        style = typography.bodyMedium,
                        color = colorScheme.onSurfaceVariant,
                        lineHeight = 20.sp
                    )
                }
            }
        }

        // Facilities
        item {
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
        }

        // Activity Logs
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = colorScheme.surface
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Activity Logs",
                        style = typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        ActivityLogItem(
                            icon = Icons.Outlined.Visibility,
                            iconTint = colorScheme.primary,
                            title = "${housingListing.views} views",
                            time = "Total views",
                            colorScheme = colorScheme,
                            typography = typography
                        )
                        ActivityLogItem(
                            icon = Icons.Outlined.ChatBubbleOutline,
                            iconTint = colorScheme.secondary,
                            title = "${housingListing.messages} messages",
                            time = "Total messages",
                            colorScheme = colorScheme,
                            typography = typography
                        )
                        ActivityLogItem(
                            icon = Icons.Outlined.PendingActions,
                            iconTint = colorScheme.tertiary,
                            title = "${housingListing.requests} requests",
                            time = "Total requests",
                            colorScheme = colorScheme,
                            typography = typography
                        )
                    }
                }
            }
        }

        // Action Buttons
        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { onApprove(housingListing.id) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorScheme.primary,
                        contentColor = colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Approve / Mark Available", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { onReject(housingListing.id) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorScheme.error,
                        contentColor = colorScheme.onError
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Reject / Mark Unavailable", fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = { onEdit(housingListing.id) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, colorScheme.outline),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = colorScheme.onSurface
                    )
                ) {
                    Text("Edit Listing", fontWeight = FontWeight.SemiBold)
                }

                TextButton(
                    onClick = { onDelete(housingListing.id) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = colorScheme.error
                    )
                ) {
                    Text("Delete Listing", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

/* ───────── SINGLE PANE COMPONENTS (Mobile) ───────── */

@Composable
private fun ImageBanner(
    housingListing: HousingListingUiModel,
    colorScheme: ColorScheme
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(colorScheme.surfaceVariant)
    ) {
        if (housingListing.imageRes != null) {
            AsyncImage(
                model = housingListing.imageRes,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(64.dp),
                tint = colorScheme.onSurfaceVariant
            )
        }

        // Image counter badge
        Surface(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(12.dp),
            shape = RoundedCornerShape(16.dp),
            color = colorScheme.scrim.copy(alpha = 0.6f),
            contentColor = colorScheme.onPrimary
        ) {
            Text(
                text = "1/1",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
private fun HeaderInfo(
    housingListing: HousingListingUiModel,
    colorScheme: ColorScheme,
    typography: Typography
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(
            text = housingListing.title,
            style = typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Location row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                Icons.Outlined.LocationOn,
                contentDescription = "Location",
                modifier = Modifier.size(16.dp),
                tint = colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = housingListing.location,
                style = typography.bodyMedium,
                color = colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f)
            )

            // Rating
            if (housingListing.rating > 0) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(colorScheme.primary.copy(alpha = 0.1f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        Icons.Filled.Star,
                        contentDescription = "Rating",
                        modifier = Modifier.size(14.dp),
                        tint = colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = String.format("%.1f", housingListing.rating),
                        style = typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Property type badge
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = colorScheme.secondary.copy(alpha = 0.1f)
            ) {
                Text(
                    text = housingListing.propertyType,
                    style = typography.bodySmall,
                    color = colorScheme.secondary,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }

            // Status Badge
            Surface(
                color = when (housingListing.status) {
                    ListingStatus.AVAILABLE -> colorScheme.primary.copy(alpha = 0.1f)
                    ListingStatus.PENDING -> colorScheme.tertiary.copy(alpha = 0.1f)
                    ListingStatus.RENTED, ListingStatus.SOLD -> colorScheme.error.copy(alpha = 0.1f)
                    ListingStatus.INACTIVE -> colorScheme.onSurfaceVariant.copy(alpha = 0.1f)
                    else -> colorScheme.secondary.copy(alpha = 0.1f)
                },
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = housingListing.status.name.lowercase().replaceFirstChar { it.uppercase() },
                    color = when (housingListing.status) {
                        ListingStatus.AVAILABLE -> colorScheme.primary
                        ListingStatus.PENDING -> colorScheme.tertiary
                        ListingStatus.RENTED, ListingStatus.SOLD -> colorScheme.error
                        ListingStatus.INACTIVE -> colorScheme.onSurfaceVariant
                        else -> colorScheme.secondary
                    },
                    style = typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }

    HorizontalDivider(
        modifier = Modifier.padding(vertical = 8.dp),
        color = colorScheme.outlineVariant
    )
}

@Composable
private fun OwnerInfo(
    housingListing: HousingListingUiModel,
    colorScheme: ColorScheme,
    typography: Typography
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar
        Surface(
            modifier = Modifier.size(48.dp),
            shape = CircleShape,
            color = colorScheme.primaryContainer
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Property Owner",
                    fontWeight = FontWeight.Bold,
                    style = typography.titleMedium,
                    color = colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(4.dp))
                if (housingListing.isVerified) {
                    Icon(
                        Icons.Default.Verified,
                        contentDescription = "Verified",
                        tint = colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Text(
                if (housingListing.isVerified) "Verified Owner" else "Unverified Owner",
                style = typography.bodySmall,
                color = colorScheme.onSurfaceVariant
            )
        }

        // Contact buttons
        IconButton(
            onClick = { },
            modifier = Modifier
                .background(colorScheme.surfaceVariant, CircleShape)
                .size(40.dp)
        ) {
            Icon(
                Icons.Default.Phone,
                contentDescription = "Call",
                tint = colorScheme.onSurface,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(
            onClick = { },
            modifier = Modifier
                .background(colorScheme.surfaceVariant, CircleShape)
                .size(40.dp)
        ) {
            Icon(
                Icons.Outlined.Email,
                contentDescription = "Message",
                tint = colorScheme.onSurface,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun PropertyDetailsCard(
    housingListing: HousingListingUiModel,
    formattedPrice: String,
    colorScheme: ColorScheme,
    typography: Typography
) {
    DetailCard(title = "Property Details") {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DetailItem(
                    modifier = Modifier.weight(1f),
                    label = "Price / Rent",
                    value = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append("KES $formattedPrice")
                        }
                        withStyle(
                            style = SpanStyle(
                                color = colorScheme.onSurfaceVariant,
                                fontSize = 12.sp
                            )
                        ) {
                            append(if (housingListing.isForSale) "" else "/month")
                        }
                    },
                    colorScheme = colorScheme,
                    typography = typography
                )
                DetailItem(
                    modifier = Modifier.weight(1f),
                    label = "Rooms",
                    valueText = "${housingListing.bedrooms} Beds, ${housingListing.bathrooms} Baths",
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
                    label = "Size",
                    valueText = "${housingListing.squareMeters} m²",
                    colorScheme = colorScheme,
                    typography = typography
                )
                DetailItem(
                    modifier = Modifier.weight(1f),
                    label = "Type",
                    valueText = housingListing.propertyType,
                    colorScheme = colorScheme,
                    typography = typography
                )
            }
        }
    }
}

@Composable
private fun FacilitiesCard(
    colorScheme: ColorScheme,
    typography: Typography
) {
    DetailCard(title = "Facilities") {
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

@Composable
private fun DescriptionCard(
    housingListing: HousingListingUiModel,
    colorScheme: ColorScheme,
    typography: Typography
) {
    DetailCard(title = "Description") {
        Text(
            text = housingListing.description.ifEmpty { "No description provided" },
            style = typography.bodyMedium,
            color = colorScheme.onSurfaceVariant,
            lineHeight = 20.sp
        )
    }
}

@Composable
private fun ActivityLogsCard(
    housingListing: HousingListingUiModel,
    colorScheme: ColorScheme,
    typography: Typography
) {
    DetailCard(title = "Activity Logs") {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            ActivityLogItem(
                icon = Icons.Outlined.Visibility,
                iconTint = colorScheme.primary,
                title = "${housingListing.views} views",
                time = "Total views",
                colorScheme = colorScheme,
                typography = typography
            )
            ActivityLogItem(
                icon = Icons.Outlined.ChatBubbleOutline,
                iconTint = colorScheme.secondary,
                title = "${housingListing.messages} messages",
                time = "Total messages",
                colorScheme = colorScheme,
                typography = typography
            )
            ActivityLogItem(
                icon = Icons.Outlined.PendingActions,
                iconTint = colorScheme.tertiary,
                title = "${housingListing.requests} requests",
                time = "Total requests",
                colorScheme = colorScheme,
                typography = typography
            )
        }
    }
}

@Composable
private fun ActionButtons(
    housingListing: HousingListingUiModel,
    onApprove: (String) -> Unit,
    onReject: (String) -> Unit,
    onEdit: (String) -> Unit,
    onDelete: (String) -> Unit,
    colorScheme: ColorScheme,
    typography: Typography
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = { onApprove(housingListing.id) },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorScheme.primary,
                contentColor = colorScheme.onPrimary
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Approve / Mark Available", fontWeight = FontWeight.Bold)
        }

        Button(
            onClick = { onReject(housingListing.id) },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorScheme.error,
                contentColor = colorScheme.onError
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Reject / Mark Unavailable", fontWeight = FontWeight.Bold)
        }

        OutlinedButton(
            onClick = { onEdit(housingListing.id) },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, colorScheme.outline),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = colorScheme.onSurface
            )
        ) {
            Text("Edit Listing", fontWeight = FontWeight.SemiBold)
        }

        TextButton(
            onClick = { onDelete(housingListing.id) },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.textButtonColors(
                contentColor = colorScheme.error
            )
        ) {
            Text("Delete Listing", fontWeight = FontWeight.SemiBold)
        }
    }
}

/* ───────── UTILITY COMPONENTS ───────── */

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

// Helper function to extract price value
private fun extractPriceValue(priceString: String): Int {
    return try {
        val cleaned = priceString
            .replace("KES", "")
            .replace("KSh", "")
            .replace("Ksh", "")
            .replace(",", "")
            .replace(" ", "")
            .trim()

        when {
            cleaned.endsWith("M", ignoreCase = true) -> {
                val number = cleaned.dropLast(1).toDoubleOrNull() ?: 0.0
                (number * 1_000_000).toInt()
            }
            cleaned.endsWith("K", ignoreCase = true) -> {
                val number = cleaned.dropLast(1).toDoubleOrNull() ?: 0.0
                (number * 1_000).toInt()
            }
            else -> cleaned.toIntOrNull() ?: 0
        }
    } catch (e: Exception) {
        0
    }
}

// Reusable Custom Card Component
@Composable
fun DetailCard(
    title: String,
    content: @Composable () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surfaceContainer
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}

// Reusable Detail Item
@Composable
fun DetailItem(
    modifier: Modifier = Modifier,
    label: String,
    valueText: String? = null,
    value: androidx.compose.ui.text.AnnotatedString? = null,
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
        if (value != null) {
            Text(
                text = value,
                style = typography.titleSmall,
                color = colorScheme.onSurface
            )
        } else if (valueText != null) {
            Text(
                text = valueText,
                style = typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface
            )
        }
    }
}

// Reusable Activity Log Item
@Composable
fun ActivityLogItem(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    time: String,
    colorScheme: ColorScheme,
    typography: Typography
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Surface(
            modifier = Modifier.size(36.dp),
            shape = CircleShape,
            color = colorScheme.surfaceVariant,
            tonalElevation = 0.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = typography.bodyMedium,
                color = colorScheme.onSurface
            )
            Text(
                text = time,
                style = typography.labelSmall,
                color = colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAdminHouseDetailsScreen() {
    MaterialTheme {
        val sampleListing = HousingListingUiModel(
            id = "1",
            title = "Modern 2-Bedroom Apartment in Westlands",
            price = "KES 45,000",
            location = "Westlands, Nairobi",
            propertyType = "Apartment",
            description = "Spacious and well-lit apartment located in the heart of Westlands. Features include modern finishes, ample parking, and 24/7 security.",
            isVerified = true,
            isForSale = false,
            rating = 4.5,
            bedrooms = 2,
            bathrooms = 2,
            squareMeters = 85,
            imageRes = R.drawable.property_placeholder4,
            status = ListingStatus.PENDING,
            views = 120,
            messages = 5,
            requests = 3
        )

        AdminHouseDetailsScreen(
            housingListing = sampleListing,
            onBack = {}
        )
    }
}