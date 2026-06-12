package com.example.pivota.dashboard.presentation.screens.client_general_screens.listings_screens.professionals

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ContactSupport
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.ChatBubble
import androidx.compose.material.icons.outlined.CurrencyExchange
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material.icons.outlined.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.window.core.layout.WindowSizeClass
import coil3.compose.AsyncImage
import com.example.pivota.core.presentations.composables.TopBar
import com.example.pivota.dashboard.domain.model.listings_models.professionals.DayAvailability
import com.example.pivota.dashboard.domain.model.listings_models.professionals.ServiceOffering
import com.example.pivota.dashboard.presentation.composables.client_general_composables.listings_composables.professionals.ContactBottomSheet
import com.example.pivota.dashboard.presentation.composables.client_general_composables.listings_composables.professionals.ContactInfo
import com.example.pivota.ui.theme.PivotaConnectTheme
import java.text.NumberFormat
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ServiceOfferingDetailsScreen(
    serviceOffering: ServiceOffering,
    onNavigateBack: () -> Unit,
    onContactProvider: () -> Unit = {},
    onBookService: () -> Unit = {}
) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val isWide = windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) ||
            windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)

    // State for bottom sheet visibility
    var showContactBottomSheet by remember { mutableStateOf(false) }

    // State for custom contact dialog (since backend doesn't have phone/email yet)
    var showContactOptionsDialog by remember { mutableStateOf(false) }

    // Since backend doesn't have phone/email fields yet, we'll use a better approach
    // Instead of showing "Contact Info Unavailable", we'll provide alternative options
    val hasContactInfo = false // Set to true when backend provides phone/email

    val formattedPrice = formatPrice(serviceOffering.basePrice, serviceOffering.currency)
    val priceUnitLabel = formatPriceUnitLabel(serviceOffering.priceUnit)
    val location = if (serviceOffering.coverageAreas.isNotEmpty()) {
        if (serviceOffering.coverageAreas.size == 1) {
            serviceOffering.coverageAreas.first()
        } else {
            "${serviceOffering.coverageAreas.first()} +${serviceOffering.coverageAreas.size - 1}"
        }
    } else {
        "Location not specified"
    }

    Scaffold(
        topBar = {
            TopBar(
                title = "Service Details",
                onBack = onNavigateBack,
                icon = Icons.AutoMirrored.Outlined.Help
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp,
                tonalElevation = 3.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 8.dp, top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        onClick = {
                            // Show contact options dialog instead of bottom sheet
                            // since backend doesn't have phone/email yet
                            showContactOptionsDialog = true
                            onContactProvider()
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            Icons.Outlined.ChatBubble,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Contact", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }

                    Button(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        onClick = onBookService,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Book Service", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    ) { innerPadding ->
        // Your existing content (all the existing UI code remains exactly the same)
        if (isWide) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    ServiceDetailsLeftPane(
                        serviceOffering = serviceOffering,
                        formattedPrice = formattedPrice,
                        priceUnitLabel = priceUnitLabel,
                        location = location,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    ServiceDetailsRightPane(
                        serviceOffering = serviceOffering,
                        location = location,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 80.dp)
            ) {
                // Price Section
                PriceSection(
                    serviceOffering = serviceOffering,
                    formattedPrice = formattedPrice,
                    priceUnitLabel = priceUnitLabel
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Negotiable Pricing Range Card
                if (serviceOffering.isNegotiable) {
                    NegotiablePricingCard(
                        serviceOffering = serviceOffering,
                        priceUnitLabel = priceUnitLabel
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Title and Category
                Text(
                    text = serviceOffering.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    lineHeight = 32.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Badge,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = serviceOffering.categoryName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 6.dp),
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Stats Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ServiceInfoItem(
                        icon = Icons.Outlined.CurrencyExchange,
                        value = priceUnitLabel,
                        label = "Pricing Model",
                        modifier = Modifier.weight(1f)
                    )
                    ServiceInfoItem(
                        icon = Icons.Outlined.AccessTime,
                        value = getAvailabilitySummary(serviceOffering.availability),
                        label = "Availability",
                        modifier = Modifier.weight(1f)
                    )
                    ServiceInfoItem(
                        icon = Icons.Outlined.Star,
                        value = String.format("%.1f", serviceOffering.averageRating),
                        label = "Rating (${serviceOffering.reviewCount})",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(24.dp))

                // Description
                Text(
                    text = "About This Service",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = serviceOffering.description,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp),
                        fontSize = 14.sp,
                        lineHeight = 22.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(24.dp))

                // Service Details
                Text(
                    text = "Service Details",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        DetailRow("Category", serviceOffering.categoryName)
                        DetailRow("Service Areas", serviceOffering.coverageAreas.joinToString(", "))
                        DetailRow("Status", serviceOffering.status)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(24.dp))

                // Location
                Text(
                    text = "Service Location",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Icon(
                            Icons.Outlined.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = location,
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(24.dp))

                // Availability
                Text(
                    text = "Weekly Availability",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (serviceOffering.availability.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            serviceOffering.availability.filter { !it.isClosed }.forEach { availability ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = availability.day,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = formatTimeTo12Hour(availability.open) + " - " + formatTimeTo12Hour(availability.close),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 13.sp
                                    )
                                }
                                if (availability != serviceOffering.availability.filter { !it.isClosed }.last()) {
                                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                                }
                            }
                        }
                    }
                } else {
                    Text(
                        text = "Contact provider for availability",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Provider Section
                ServiceProviderCard(
                    name = serviceOffering.professionalName,
                    yearsExperience = serviceOffering.yearsExperience ?: 0,
                    isVerified = serviceOffering.isVerified,
                    avatarUrl = serviceOffering.professionalAvatar,
                    onClick = { },
                    showTitle = true
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    // Show contact options dialog when backend doesn't have phone/email yet
    if (showContactOptionsDialog) {
        ContactOptionsDialog(
            professionalName = serviceOffering.professionalName,
            onBookService = {
                showContactOptionsDialog = false
                onBookService()
            },
            onDismiss = { showContactOptionsDialog = false }
        )
    }
}

// New dialog component for when contact info isn't available yet
@Composable
fun ContactOptionsDialog(
    professionalName: String,
    onBookService: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Contact $professionalName",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    "You can connect with the professional through the following options:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Option 1: Book Service
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onDismiss()
                            onBookService()
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            Icons.Filled.Bookmark,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Column {
                            Text(
                                "Book Their Service",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleSmall
                            )
                            Text(
                                "Send a booking request directly to the professional",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Option 2: Info note
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            Icons.Filled.ContactSupport,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(24.dp)
                        )
                        Column {
                            Text(
                                "Contact Info Coming Soon",
                                fontWeight = FontWeight.Medium,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                "Direct contact options will be available after booking",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Close", fontWeight = FontWeight.Medium)
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}


@Composable
private fun PriceSection(
    serviceOffering: ServiceOffering,
    formattedPrice: String,
    priceUnitLabel: String
) {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.primaryContainer.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Starting Price",
                    style = MaterialTheme.typography.labelMedium,
                    color = colorScheme.onSurfaceVariant,
                    fontSize = 13.sp
                )
                if (serviceOffering.isNegotiable) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = colorScheme.tertiaryContainer.copy(alpha = 0.3f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Outlined.StarBorder,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = colorScheme.tertiary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Negotiable",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = colorScheme.tertiary
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = formattedPrice,
                color = colorScheme.primary,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp
            )

            Text(
                text = priceUnitLabel,
                style = MaterialTheme.typography.bodyMedium,
                color = colorScheme.onSurfaceVariant,
                fontSize = 14.sp
            )

            // Booking Fee Display
            val hasBookingFee = serviceOffering.useCustomBookingFee &&
                    serviceOffering.customBookingFeeEnabled == true &&
                    serviceOffering.customBookingFeeAmount != null &&
                    serviceOffering.customBookingFeeAmount > 0

            if (hasBookingFee) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Outlined.Receipt,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = colorScheme.tertiary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Booking fee: ${serviceOffering.customBookingFeeCurrency} ${serviceOffering.customBookingFeeAmount}",
                        fontSize = 12.sp,
                        color = colorScheme.tertiary
                    )
                    if (serviceOffering.customBookingFeeRefundable == true) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(2.dp),
                            color = colorScheme.primaryContainer.copy(alpha = 0.5f)
                        ) {
                            Text(
                                text = "Refundable",
                                fontSize = 9.sp,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                                color = colorScheme.primary
                            )
                        }
                    }
                }
                if (serviceOffering.customBookingFeeDescription != null) {
                    Text(
                        text = serviceOffering.customBookingFeeDescription,
                        fontSize = 11.sp,
                        color = colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 20.dp, top = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun NegotiablePricingCard(
    serviceOffering: ServiceOffering,
    priceUnitLabel: String
) {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.tertiaryContainer.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Outlined.CurrencyExchange,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = colorScheme.tertiary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Price Negotiable",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = colorScheme.tertiary
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "The provider is open to negotiation. You can propose a fair price during booking.",
                fontSize = 13.sp,
                color = colorScheme.onSurfaceVariant
            )
            if (serviceOffering.minNegotiablePrice != null || serviceOffering.maxNegotiablePrice != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = buildAnnotatedString {
                        append("Acceptable range: ")
                        if (serviceOffering.minNegotiablePrice != null) {
                            append("${serviceOffering.currency} ${serviceOffering.minNegotiablePrice}")
                        }
                        if (serviceOffering.minNegotiablePrice != null && serviceOffering.maxNegotiablePrice != null) {
                            append(" - ")
                        }
                        if (serviceOffering.maxNegotiablePrice != null) {
                            append("${serviceOffering.currency} ${serviceOffering.maxNegotiablePrice}")
                        }
                        append(priceUnitLabel)
                    },
                    fontSize = 12.sp,
                    color = colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ServiceDetailsLeftPane(
    serviceOffering: ServiceOffering,
    formattedPrice: String,
    priceUnitLabel: String,
    location: String,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = modifier
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Price Card with Negotiable Badge and Booking Fee
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = colorScheme.primaryContainer.copy(alpha = 0.15f)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Starting Price",
                        style = MaterialTheme.typography.labelMedium,
                        color = colorScheme.onSurfaceVariant,
                        fontSize = 13.sp
                    )
                    if (serviceOffering.isNegotiable) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = colorScheme.tertiaryContainer.copy(alpha = 0.3f)
                        ) {
                            Text(
                                text = "Negotiable",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = colorScheme.tertiary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = formattedPrice,
                    color = colorScheme.primary,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = 36.sp
                )
                Text(
                    text = priceUnitLabel,
                    style = MaterialTheme.typography.bodyLarge,
                    color = colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 4.dp, bottom = 6.dp),
                    fontSize = 14.sp
                )

                val hasBookingFee = serviceOffering.useCustomBookingFee &&
                        serviceOffering.customBookingFeeEnabled == true &&
                        serviceOffering.customBookingFeeAmount != null &&
                        serviceOffering.customBookingFeeAmount > 0

                if (hasBookingFee) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Outlined.Receipt,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = colorScheme.tertiary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Booking fee: ${serviceOffering.customBookingFeeCurrency} ${serviceOffering.customBookingFeeAmount}",
                            fontSize = 12.sp,
                            color = colorScheme.tertiary
                        )
                        if (serviceOffering.customBookingFeeRefundable == true) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(2.dp),
                                color = colorScheme.primaryContainer.copy(alpha = 0.5f)
                            ) {
                                Text(
                                    text = "Refundable",
                                    fontSize = 9.sp,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                                    color = colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }

        // Title
        Text(
            text = serviceOffering.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = colorScheme.onSurface,
            fontSize = 28.sp,
            lineHeight = 36.sp
        )

        // Category & Location
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = colorScheme.surfaceContainerHighest
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Badge,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "CATEGORY",
                            style = MaterialTheme.typography.labelSmall,
                            color = colorScheme.primary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = serviceOffering.categoryName,
                            style = MaterialTheme.typography.bodyLarge,
                            color = colorScheme.onSurface,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = colorScheme.surfaceContainerHighest
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(
                        Icons.Outlined.LocationOn,
                        contentDescription = null,
                        tint = colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "SERVICE LOCATION",
                            style = MaterialTheme.typography.labelSmall,
                            color = colorScheme.primary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = location,
                            style = MaterialTheme.typography.bodyLarge,
                            color = colorScheme.onSurface,
                            fontSize = 15.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        // Key Specs Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = colorScheme.surfaceContainerHighest
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Quick Info",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = colorScheme.onSurface,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ServiceSpecItem(
                        icon = Icons.Outlined.Description,
                        value = serviceOffering.status,
                        label = "Status",
                        colorScheme = colorScheme
                    )
                    ServiceSpecItem(
                        icon = Icons.Outlined.Schedule,
                        value = getAvailabilitySummary(serviceOffering.availability),
                        label = "Schedule",
                        colorScheme = colorScheme
                    )
                    ServiceSpecItem(
                        icon = Icons.Outlined.Star,
                        value = String.format("%.1f", serviceOffering.averageRating),
                        label = "Rating (${serviceOffering.reviewCount})",
                        colorScheme = colorScheme
                    )
                }
            }
        }

        // Negotiable Pricing Range in Left Pane
        if (serviceOffering.isNegotiable && (serviceOffering.minNegotiablePrice != null || serviceOffering.maxNegotiablePrice != null)) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = colorScheme.tertiaryContainer.copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Outlined.CurrencyExchange,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = colorScheme.tertiary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Negotiable Range",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = colorScheme.tertiary
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = buildAnnotatedString {
                            if (serviceOffering.minNegotiablePrice != null) {
                                append("Min: ${serviceOffering.currency} ${serviceOffering.minNegotiablePrice}")
                            }
                            if (serviceOffering.minNegotiablePrice != null && serviceOffering.maxNegotiablePrice != null) {
                                append(" • ")
                            }
                            if (serviceOffering.maxNegotiablePrice != null) {
                                append("Max: ${serviceOffering.currency} ${serviceOffering.maxNegotiablePrice}")
                            }
                        },
                        fontSize = 13.sp,
                        color = colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun ServiceDetailsRightPane(
    serviceOffering: ServiceOffering,
    location: String,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = modifier
            .background(colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Description Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = colorScheme.surface
            ),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Description",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = colorScheme.onSurface,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = serviceOffering.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.onSurfaceVariant,
                    fontSize = 14.sp,
                    lineHeight = 22.sp
                )
            }
        }

        // Negotiable Pricing Card in Right Pane
        if (serviceOffering.isNegotiable) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = colorScheme.surface
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Outlined.CurrencyExchange,
                            contentDescription = null,
                            modifier = Modifier.size(22.dp),
                            tint = colorScheme.tertiary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Negotiable Pricing",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = colorScheme.onSurface,
                            fontSize = 16.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "The provider is open to negotiation on the price.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorScheme.onSurfaceVariant,
                        fontSize = 14.sp
                    )
                    if (serviceOffering.minNegotiablePrice != null || serviceOffering.maxNegotiablePrice != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = buildAnnotatedString {
                                append("Price range: ")
                                if (serviceOffering.minNegotiablePrice != null) {
                                    append("${serviceOffering.currency} ${serviceOffering.minNegotiablePrice}")
                                }
                                if (serviceOffering.minNegotiablePrice != null && serviceOffering.maxNegotiablePrice != null) {
                                    append(" - ")
                                }
                                if (serviceOffering.maxNegotiablePrice != null) {
                                    append("${serviceOffering.currency} ${serviceOffering.maxNegotiablePrice}")
                                }
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = colorScheme.primary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Service Details Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = colorScheme.surface
            ),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Service Details",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = colorScheme.onSurface,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(20.dp))

                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    DetailItem(
                        modifier = Modifier.fillMaxWidth(),
                        label = "Category",
                        valueText = serviceOffering.categoryName,
                        colorScheme = colorScheme
                    )
                    DetailItem(
                        modifier = Modifier.fillMaxWidth(),
                        label = "Service Areas",
                        valueText = serviceOffering.coverageAreas.joinToString(", "),
                        colorScheme = colorScheme
                    )
                    DetailItem(
                        modifier = Modifier.fillMaxWidth(),
                        label = "Status",
                        valueText = serviceOffering.status,
                        colorScheme = colorScheme
                    )
                }
            }
        }

        // Location Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = colorScheme.surface
            ),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Service Location",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = colorScheme.onSurface,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Outlined.LocationOn,
                        contentDescription = null,
                        tint = colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = location,
                        style = MaterialTheme.typography.bodyLarge,
                        color = colorScheme.onSurface,
                        fontSize = 14.sp
                    )
                }
            }
        }

        // Availability Card
        if (serviceOffering.availability.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = colorScheme.surface
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Weekly Availability",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = colorScheme.onSurface,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    serviceOffering.availability.filter { !it.isClosed }.forEach { availability ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = availability.day,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = colorScheme.onSurface,
                                fontSize = 14.sp
                            )
                            Text(
                                text = formatTimeTo12Hour(availability.open) + " - " + formatTimeTo12Hour(availability.close),
                                style = MaterialTheme.typography.bodyMedium,
                                color = colorScheme.onSurfaceVariant,
                                fontSize = 13.sp
                            )
                        }
                        if (availability != serviceOffering.availability.filter { !it.isClosed }.last()) {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                        }
                    }
                }
            }
        }

        // Provider Card
        ServiceProviderCard(
            name = serviceOffering.professionalName,
            yearsExperience = serviceOffering.yearsExperience ?: 0,
            isVerified = serviceOffering.isVerified,
            avatarUrl = serviceOffering.professionalAvatar,
            onClick = {},
            showTitle = true
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun ServiceInfoItem(
    icon: ImageVector,
    value: String,
    label: String,
    modifier: Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline,
            fontSize = 11.sp
        )
    }
}

@Composable
fun ServiceSpecItem(
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
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = colorScheme.onSurface
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun ServiceProviderCard(
    name: String,
    yearsExperience: Int,
    isVerified: Boolean,
    onClick: () -> Unit,
    avatarUrl: String? = null,
    showTitle: Boolean = true
) {
    val colorScheme = MaterialTheme.colorScheme
    val formattedName = formatName(name)

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (showTitle) {
            Text(
                text = "Professional/Contract",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = colorScheme.onSurface,
                fontSize = 18.sp,
                letterSpacing = 0.5.sp
            )
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() },
            colors = CardDefaults.cardColors(
                containerColor = colorScheme.surface
            ),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp,
                pressedElevation = 4.dp
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                if (avatarUrl != null && avatarUrl.isNotEmpty()) {
                    AsyncImage(
                        model = avatarUrl,
                        contentDescription = "Provider avatar",
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape),
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = getInitials(formattedName),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.primary
                        )
                    }
                }

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = formattedName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = colorScheme.onSurface,
                        fontSize = 16.sp
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Outlined.Work,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "$yearsExperience years",
                                style = MaterialTheme.typography.bodySmall,
                                color = colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(start = 4.dp),
                                fontSize = 12.sp
                            )
                        }

                        if (isVerified) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Outlined.Verified,
                                    tint = colorScheme.primary,
                                    modifier = Modifier.size(14.dp),
                                    contentDescription = null
                                )
                                Text(
                                    "Verified",
                                    color = colorScheme.primary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }
                        }
                    }
                }

                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "View Provider Details",
                    modifier = Modifier.size(24.dp),
                    tint = colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun formatName(name: String): String {
    return name.split(" ")
        .joinToString(" ") { part ->
            if (part.isNotEmpty()) {
                part.lowercase().replaceFirstChar { it.uppercase() }
            } else {
                part
            }
        }
}

private fun getInitials(name: String): String {
    return name.split(" ")
        .mapNotNull { it.firstOrNull()?.toString() }
        .take(2)
        .joinToString("")
        .uppercase()
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}

@Composable
fun DetailItem(
    modifier: Modifier = Modifier,
    label: String,
    valueText: String,
    colorScheme: ColorScheme
) {
    Column(modifier = modifier) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = colorScheme.primary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = valueText,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = colorScheme.onSurface,
            fontSize = 14.sp
        )
    }
}

private fun formatPrice(price: Double, currency: String): String {
    val formatter = NumberFormat.getNumberInstance(Locale.US).apply {
        minimumFractionDigits = 0
        maximumFractionDigits = 0
    }
    return "$currency ${formatter.format(price)}"
}

fun formatPriceUnitLabel(unit: String): String {
    return when (unit) {
        "PER_HOUR" -> "/hour"
        "PER_DAY" -> "/day"
        "PER_WEEK" -> "/week"
        "PER_MONTH" -> "/month"
        "PER_YEAR" -> "/year"
        "PER_VISIT" -> "/visit"
        "PER_SESSION" -> "/session"
        "FIXED" -> " fixed price"
        "PACKAGE" -> " package"
        else -> "/${unit.lowercase().replace("_", " ")}"
    }
}

private fun getAvailabilitySummary(availability: List<DayAvailability>): String {
    val availableDays = availability.filter { !it.isClosed }.map { it.day.take(3) }
    return if (availableDays.isNotEmpty()) {
        when (availableDays.size) {
            7 -> "Daily"
            5 -> "Weekdays"
            2 -> "Weekends"
            else -> availableDays.joinToString(", ")
        }
    } else {
        "Contact"
    }
}

private fun formatTimeTo12Hour(time24: String): String {
    val parts = time24.split(":")
    if (parts.size != 2) return time24
    val hour = parts[0].toIntOrNull() ?: 9
    val minute = parts[1]
    val displayHour = when {
        hour == 0 -> 12
        hour > 12 -> hour - 12
        else -> hour
    }
    val ampm = if (hour < 12) "AM" else "PM"
    return "$displayHour:$minute $ampm"
}



