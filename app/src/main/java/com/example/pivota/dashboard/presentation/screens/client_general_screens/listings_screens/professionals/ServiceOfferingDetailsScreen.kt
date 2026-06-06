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
import androidx.compose.material.icons.filled.ChevronRight
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
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
    // ✅ Updated: Use coverageAreas instead of location fields
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
                        onClick = onContactProvider,
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
        if (isWide) {
            // Two pane layout for tablets - BOTH panes scrollable
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Left Pane - Scrollable
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

                // Right Pane - Scrollable
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
            // Single pane layout for mobile
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 80.dp)
            ) {
                // Price Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "Starting Price",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text(
                                text = formattedPrice,
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                fontSize = 32.sp
                            )
                            Text(
                                text = priceUnitLabel,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(start = 4.dp, bottom = 4.dp),
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

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
                        // ✅ Updated: Use coverageAreas instead of serviceAreas
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
        // Price Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = colorScheme.primaryContainer.copy(alpha = 0.15f)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Starting Price",
                    style = MaterialTheme.typography.labelMedium,
                    color = colorScheme.onSurfaceVariant,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
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

        // Category & Location - IMPROVED VISIBILITY
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            // Category row
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

            // Location row
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
                    // ✅ Updated: Use coverageAreas instead of serviceAreas
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
    // ✅ Updated: Use coverageAreas instead of location fields
    coverageAreas = listOf("Westlands", "Kilimani", "Lavington", "Karen"),
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
    status = "ACTIVE",
    averageRating = 4.8,
    reviewCount = 124,
    createdAt = "2024-01-01T00:00:00Z",
    updatedAt = "2024-01-01T00:00:00Z"
)