package com.example.pivota.dashboard.presentation.composables.listings_composables

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material.icons.outlined.WorkOutline
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
import com.example.pivota.dashboard.domain.model.listings_models.professionals.ServiceOffering
import com.example.pivota.ui.theme.PivotaConnectTheme

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ServiceOfferingCard(
    modifier: Modifier = Modifier,
    offering: ServiceOffering,
    onClick: () -> Unit = {},
) {
    val colorScheme = MaterialTheme.colorScheme
    val primaryColor = colorScheme.primary
    val onSurfaceColor = colorScheme.onSurface
    val onSurfaceVariantColor = colorScheme.onSurfaceVariant
    val surfaceColor = colorScheme.surface
    val tertiaryColor = colorScheme.tertiary
    val secondaryColor = colorScheme.secondary

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .drawBehind {
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
                // Left side: Avatar with gradient ring
                Box(
                    modifier = Modifier.size(56.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Gradient ring for verified professionals
                    if (offering.isVerified) {
                        Surface(
                            modifier = Modifier.size(56.dp),
                            shape = CircleShape,
                            color = Color.Transparent,
                            border = androidx.compose.foundation.BorderStroke(
                                2.dp,
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
                    } else {
                        // Subtle dotted ring for unverified
                        Surface(
                            modifier = Modifier.size(56.dp),
                            shape = CircleShape,
                            color = Color.Transparent,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                primaryColor.copy(alpha = 0.2f)
                            )
                        ) {}
                    }

                    Surface(
                        modifier = Modifier.size(48.dp),
                        shape = CircleShape,
                        color = primaryColor.copy(alpha = 0.1f)
                    ) {
                        if (!offering.professionalAvatar.isNullOrEmpty()) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(offering.professionalAvatar)
                                    .crossfade(true)
                                    .size(Size(200, 200))
                                    .build(),
                                contentDescription = "${offering.professionalName} avatar",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize(),
                                error = painterResource(id = R.drawable.ic_launcher_foreground)
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
                                    text = offering.professionalName
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
                }

                // Right side: Content
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    // Top row: Category badge
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = primaryColor.copy(alpha = 0.1f),
                        modifier = Modifier
                    ) {
                        Text(
                            text = offering.categoryName.uppercase(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = primaryColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            maxLines = 1
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Title with verification badge
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = offering.title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = onSurfaceColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        if (offering.isVerified) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .background(
                                        brush = Brush.radialGradient(
                                            colors = listOf(primaryColor, primaryColor.copy(alpha = 0.7f))
                                        ),
                                        shape = CircleShape
                                    )
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Verified,
                                    contentDescription = "Verified",
                                    tint = Color.White,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }

                    // Professional name
                    Text(
                        text = offering.professionalName,
                        fontSize = 12.sp,
                        color = onSurfaceVariantColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 2.dp)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Rating row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (offering.averageRating > 0) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = "Rating",
                                    tint = tertiaryColor,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = String.format("%.1f", offering.averageRating),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = onSurfaceVariantColor
                                )
                                Text(
                                    text = "(${offering.reviewCount} reviews)",
                                    fontSize = 10.sp,
                                    color = onSurfaceVariantColor.copy(alpha = 0.7f)
                                )
                            }
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.StarBorder,
                                    contentDescription = "No reviews",
                                    tint = onSurfaceVariantColor.copy(alpha = 0.5f),
                                    modifier = Modifier.size(11.dp)
                                )
                                Text(
                                    text = "New on Pivota",
                                    fontSize = 10.sp,
                                    color = onSurfaceVariantColor.copy(alpha = 0.7f),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Location and experience row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Location
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.LocationOn,
                                contentDescription = "Location",
                                tint = onSurfaceVariantColor,
                                modifier = Modifier.size(11.dp)
                            )
                            Text(
                                text = if (offering.coverageAreas.isNotEmpty())
                                    offering.coverageAreas.first()
                                else
                                    "Location not specified",
                                fontSize = 11.sp,
                                color = onSurfaceVariantColor,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            // Show badge for multiple areas
                            if (offering.coverageAreas.size > 1) {
                                Text(
                                    text = "+${offering.coverageAreas.size - 1}",
                                    fontSize = 10.sp,
                                    color = onSurfaceVariantColor.copy(alpha = 0.6f),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        // Separator dot
                        if (offering.yearsExperience != null && offering.yearsExperience > 0) {
                            Box(
                                modifier = Modifier
                                    .size(3.dp)
                                    .background(
                                        color = onSurfaceVariantColor.copy(alpha = 0.5f),
                                        shape = CircleShape
                                    )
                            )
                        }

                        // Experience
                        if (offering.yearsExperience != null && offering.yearsExperience > 0) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.WorkOutline,
                                    contentDescription = "Experience",
                                    tint = onSurfaceVariantColor,
                                    modifier = Modifier.size(11.dp)
                                )
                                Text(
                                    text = "${offering.yearsExperience}+ yrs",
                                    fontSize = 11.sp,
                                    color = onSurfaceVariantColor
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Divider line
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

                    // Price and View service link
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Price
                        Column {
                            Text(
                                text = formatPrice(offering.basePrice, offering.currency),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = primaryColor
                            )
                            Text(
                                text = formatPriceUnit(offering.priceUnit),
                                fontSize = 10.sp,
                                color = onSurfaceVariantColor.copy(alpha = 0.7f)
                            )
                        }

                        // View service link
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.clickable { onClick() }
                        ) {
                            Text(
                                text = "View service",
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
                    }
                }
            }
        }
    }
}

private fun formatPrice(price: Double, currency: String): String {
    return when (currency) {
        "KES" -> "KES ${String.format("%,.0f", price)}"
        else -> "${String.format("%,.0f", price)} $currency"
    }
}

private fun formatPriceUnit(unit: String): String {
    return when (unit) {
        "PER_HOUR" -> "/hour"
        "PER_DAY" -> "/day"
        "PER_VISIT" -> "/visit"
        "PER_SESSION" -> "/session"
        "PER_MONTH" -> "/month"
        "FIXED" -> "fixed price"
        else -> ""
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
private fun PreviewServiceOfferingCard() {
    PivotaConnectTheme(darkTheme = false) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val sampleOffering1 = ServiceOffering(
                id = "1",
                externalId = "ext1",
                professionalName = "John Mwangi",
                professionalAvatar = "https://example.com/avatar.jpg",
                isVerified = true,
                title = "Professional Electrical Installation",
                description = "",
                categoryId = "cat1",
                categoryName = "Electricians",
                basePrice = 800.0,
                priceUnit = "PER_HOUR",
                currency = "KES",
                coverageAreas = listOf("Nairobi CBD", "Westlands", "Kilimani"),
                availability = emptyList(),
                yearsExperience = 8,
                hourlyRate = 800.0,
                status = "ACTIVE",
                averageRating = 4.8,
                reviewCount = 24,
                createdAt = "2026-05-22T10:30:00Z",
                updatedAt = "2026-05-22T10:30:00Z"
            )

            ServiceOfferingCard(offering = sampleOffering1, onClick = {})

            val sampleOffering2 = ServiceOffering(
                id = "2",
                externalId = "ext2",
                professionalName = "Jane Smith",
                professionalAvatar = null,
                isVerified = false,
                title = "Professional CV Writing Service",
                description = "",
                categoryId = "cat2",
                categoryName = "CV Writing",
                basePrice = 5000.0,
                priceUnit = "FIXED",
                currency = "KES",
                coverageAreas = listOf("Kiambu", "Thika", "Ruiru"),
                availability = emptyList(),
                yearsExperience = 5,
                hourlyRate = 0.0,
                status = "ACTIVE",
                averageRating = 0.0,
                reviewCount = 0,
                createdAt = "2026-05-22T10:30:00Z",
                updatedAt = "2026-05-22T10:30:00Z"
            )

            ServiceOfferingCard(offering = sampleOffering2, onClick = {})
        }
    }
}