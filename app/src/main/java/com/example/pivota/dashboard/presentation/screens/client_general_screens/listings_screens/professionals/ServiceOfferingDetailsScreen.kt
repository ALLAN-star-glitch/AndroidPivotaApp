package com.example.pivota.dashboard.presentation.screens.client_general_screens.listings_screens.professionals

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.ChatBubble
import androidx.compose.material.icons.outlined.CurrencyExchange
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material.icons.outlined.Work
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.window.core.layout.WindowSizeClass
import com.example.pivota.core.presentations.composables.TopBar
import com.example.pivota.dashboard.domain.model.listings_models.professionals.DayAvailability
import com.example.pivota.dashboard.domain.model.listings_models.professionals.ServiceOffering
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

    val formattedPrice = formatPrice(serviceOffering.basePrice, serviceOffering.currency)
    val priceUnitLabel = formatPriceUnitLabel(serviceOffering.priceUnit)
    val location = if (serviceOffering.locationNeighborhood != null) {
        "${serviceOffering.locationCity}, ${serviceOffering.locationNeighborhood}"
    } else {
        serviceOffering.locationCity
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
                        onClick = onContactProvider
                    ) {
                        Text("Contact")
                    }

                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = onBookService
                    ) {
                        Text("Book Service", color = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            }
        }
    ) { innerPadding ->
        if (isWide) {
            // Two pane layout for tablets
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                ServiceDetailsLeftPane(
                    serviceOffering = serviceOffering,
                    formattedPrice = formattedPrice,
                    priceUnitLabel = priceUnitLabel,
                    location = location,
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                )

                ServiceDetailsRightPane(
                    serviceOffering = serviceOffering,
                    location = location,
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                )
            }
        } else {
            // Single pane layout for mobile
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {

                Spacer(modifier = Modifier.height(16.dp))

                // Price
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formattedPrice,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = priceUnitLabel,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Title
                Text(
                    text = serviceOffering.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Category
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Badge,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = serviceOffering.categoryName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))

                // Key Info Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ServiceInfoItem(
                        icon = Icons.Outlined.CurrencyExchange,
                        value = priceUnitLabel,
                        label = "Pricing",
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
                    text = serviceOffering.description,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))

                // Service Details
                Text(
                    text = "Service Details",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                DetailRow("Category", serviceOffering.categoryName)
                DetailRow("Service Areas", serviceOffering.serviceAreas.joinToString(", "))
                DetailRow("Status", serviceOffering.status)

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))

                // Location
                Text(
                    text = "Service Location",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = location,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))

                // Availability
                Text(
                    text = "Availability",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (serviceOffering.availability.isNotEmpty()) {
                    serviceOffering.availability.filter { !it.isClosed }.forEach { availability ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = availability.day,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = formatTimeTo12Hour(availability.open) + " - " + formatTimeTo12Hour(availability.close),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    Text(
                        text = "Contact provider for availability",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Provider Section with Experience
                ServiceProviderCard(
                    name = serviceOffering.professionalName,
                    yearsExperience = serviceOffering.yearsExperience,
                    isVerified = serviceOffering.isVerified,
                    onMessageClick = onContactProvider
                )
            }
        }
    }
}

// Two Pane Components
@Composable
private fun ServiceDetailsLeftPane(
    serviceOffering: ServiceOffering,
    formattedPrice: String,
    priceUnitLabel: String,
    location: String,
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

        // Price
        Column {
            Text(
                text = "Pricing",
                style = typography.labelLarge,
                color = colorScheme.onSurfaceVariant
            )
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formattedPrice,
                    color = colorScheme.primary,
                    style = typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = priceUnitLabel,
                    style = typography.bodyLarge,
                    color = colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }

        // Title
        Text(
            text = serviceOffering.title,
            style = typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = colorScheme.onSurface
        )

        // Category
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Badge,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = colorScheme.onSurfaceVariant
            )
            Text(
                text = serviceOffering.categoryName,
                style = typography.bodyLarge,
                color = colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        // Location
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Outlined.LocationOn,
                contentDescription = null,
                tint = colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = location,
                style = typography.bodyLarge,
                color = colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 4.dp),
                maxLines = 2
            )
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
                    text = "Service Information",
                    style = typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(16.dp))

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
                        label = "Rating",
                        colorScheme = colorScheme
                    )
                }
            }
        }
    }
}

@Composable
private fun ServiceDetailsRightPane(
    serviceOffering: ServiceOffering,
    location: String,
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
                    text = serviceOffering.description,
                    style = typography.bodyMedium,
                    color = colorScheme.onSurfaceVariant,
                    lineHeight = 20.sp
                )
            }
        }

        // Service Details Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = colorScheme.surface
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Service Details",
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
                            label = "Category",
                            valueText = serviceOffering.categoryName,
                            colorScheme = colorScheme,
                            typography = typography
                        )
                        DetailItem(
                            modifier = Modifier.weight(1f),
                            label = "Status",
                            valueText = serviceOffering.status,
                            colorScheme = colorScheme,
                            typography = typography
                        )
                    }
                    DetailItem(
                        modifier = Modifier.fillMaxWidth(),
                        label = "Service Areas",
                        valueText = serviceOffering.serviceAreas.joinToString(", "),
                        colorScheme = colorScheme,
                        typography = typography
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
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Service Location",
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
                        text = location,
                        style = typography.bodyLarge,
                        color = colorScheme.onSurface
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
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Availability",
                        style = typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    serviceOffering.availability.filter { !it.isClosed }.forEach { availability ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = availability.day,
                                style = typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = colorScheme.onSurface
                            )
                            Text(
                                text = formatTimeTo12Hour(availability.open) + " - " + formatTimeTo12Hour(availability.close),
                                style = typography.bodyMedium,
                                color = colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Provider Card with Experience
        ServiceProviderCard(
            name = serviceOffering.professionalName,
            yearsExperience = serviceOffering.yearsExperience,
            isVerified = serviceOffering.isVerified,
            onMessageClick = {}
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

// Reusable Components
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
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline
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
        Text(
            text = value,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = colorScheme.onSurface
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ServiceProviderCard(
    name: String,
    yearsExperience: Int,
    isVerified: Boolean,
    onMessageClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

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
                Icon(
                    Icons.Outlined.Person,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = colorScheme.primary
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = name,
                    style = typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = colorScheme.onSurface
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Icon(
                        Icons.Outlined.Work,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$yearsExperience years experience",
                        style = typography.bodySmall,
                        color = colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
                if (isVerified) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Icon(
                            Icons.Outlined.Verified,
                            tint = colorScheme.primary,
                            modifier = Modifier.size(14.dp),
                            contentDescription = null
                        )
                        Text(
                            "Verified Professional",
                            color = colorScheme.primary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
            }

            IconButton(
                onClick = onMessageClick
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
fun DetailItem(
    modifier: Modifier = Modifier,
    label: String,
    valueText: String,
    colorScheme: ColorScheme,
    typography: androidx.compose.material3.Typography
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

// Helper functions
private fun formatPrice(price: Double, currency: String): String {
    val formatter = NumberFormat.getNumberInstance(Locale.US).apply {
        minimumFractionDigits = 0
        maximumFractionDigits = 0
    }
    return "$currency ${formatter.format(price)}"
}

private fun formatPriceUnitLabel(unit: String): String {
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

// Preview
@RequiresApi(Build.VERSION_CODES.O)
@Preview(
    name = "Service Offering Details - Mobile",
    showBackground = true,
    backgroundColor = 0xFFF7F9FE,
    heightDp = 800,
    widthDp = 400
)
@Composable
private fun PreviewServiceOfferingDetailsMobile() {
    PivotaConnectTheme(darkTheme = false) {
        ServiceOfferingDetailsScreen(
            serviceOffering = sampleServiceOffering,
            onNavigateBack = {},
            onContactProvider = {},
            onBookService = {}
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(
    name = "Service Offering Details - Tablet",
    device = "spec:width=800dp,height=1280dp,dpi=240",
    showBackground = true,
    backgroundColor = 0xFFF7F9FE
)
@Composable
private fun PreviewServiceOfferingDetailsTablet() {
    PivotaConnectTheme(darkTheme = false) {
        ServiceOfferingDetailsScreen(
            serviceOffering = sampleServiceOffering,
            onNavigateBack = {},
            onContactProvider = {},
            onBookService = {}
        )
    }
}

// Sample data for preview
private val sampleServiceOffering = ServiceOffering(
    id = "1",
    externalId = "EXT123",
    professionalName = "John Doe",
    professionalAvatar = null,
    isVerified = true,
    title = "Professional House Painting Service",
    description = "Expert house painting services with high-quality materials. We offer interior and exterior painting for residential and commercial properties. 10+ years of experience with guaranteed satisfaction.",
    categoryId = "cat123",
    categoryName = "Painting",
    basePrice = 15000.0,
    priceUnit = "PER_DAY",
    currency = "KES",
    locationCity = "Nairobi",
    locationNeighborhood = "Westlands",
    availability = listOf(
        DayAvailability("Monday", "09:00", "17:00", false),
        DayAvailability("Tuesday", "09:00", "17:00", false),
        DayAvailability("Wednesday", "09:00", "17:00", false),
        DayAvailability("Thursday", "09:00", "17:00", false),
        DayAvailability("Friday", "09:00", "17:00", false),
        DayAvailability("Saturday", "10:00", "14:00", false),
        DayAvailability("Sunday", "00:00", "00:00", true)
    ),
    yearsExperience = 10,
    hourlyRate = 2000.0,
    serviceAreas = listOf("Westlands", "Kilimani", "Lavington", "Karen"),
    status = "ACTIVE",
    averageRating = 4.8,
    reviewCount = 124,
    createdAt = "2024-01-01T00:00:00Z",
    updatedAt = "2024-01-01T00:00:00Z"
)