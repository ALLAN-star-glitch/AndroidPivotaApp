package com.example.pivota.dashboard.presentation.composables

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Whatsapp
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.pivota.dashboard.presentation.composables.ContactIcon

@SuppressLint("DefaultLocale")
@Composable
fun ModernHousingCard(
    price: String,
    title: String,
    location: String,
    type: String,
    rating: Double,
    isVerified: Boolean,
    description: String = "", // Added description parameter
    isForSale: Boolean = false,
    imageRes: Int,
    onViewClick: () -> Unit = {},
    onBookClick: () -> Unit = {},
    onMessageClick: () -> Unit = {},
    onWhatsAppClick: () -> Unit = {},
    onPhoneClick: () -> Unit = {},
    onClick: () -> Unit = {}
) {
    var isFavorite by remember { mutableStateOf(false) }

    // Using MaterialTheme colors from your theme
    val primaryColor = MaterialTheme.colorScheme.primary      // African Sapphire (#2B638B)
    val secondaryColor = MaterialTheme.colorScheme.secondary  // Warm Terracotta (#8F4C36)
    val tertiaryColor = MaterialTheme.colorScheme.tertiary    // Baobab Gold (#755B0B)
    val surfaceColor = MaterialTheme.colorScheme.surface
    val surfaceVariantColor = MaterialTheme.colorScheme.surfaceVariant
    val onSurfaceVariantColor = MaterialTheme.colorScheme.onSurfaceVariant
    val outlineVariantColor = MaterialTheme.colorScheme.outlineVariant
    val scrimColor = MaterialTheme.colorScheme.scrim
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val onPrimaryColor = MaterialTheme.colorScheme.onPrimary

    Card(
        modifier = Modifier
            .width(300.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = surfaceColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Image Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                AsyncImage(
                    model = imageRes,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Gradient overlay for text visibility
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    scrimColor.copy(alpha = 0.4f)
                                ),
                                startY = 300f
                            )
                        )
                )

                // Sale/Rent Badge
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            if (isForSale) tertiaryColor
                            else primaryColor
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (isForSale) "FOR SALE" else "FOR RENT",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = onPrimaryColor,
                        letterSpacing = 0.3.sp,
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                // Favorite Button
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(surfaceColor.copy(alpha = 0.9f))
                        .clickable { isFavorite = !isFavorite }
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite
                        else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) tertiaryColor else onSurfaceVariantColor,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(18.dp)
                    )
                }

                // Rating on image
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(scrimColor.copy(alpha = 0.7f))
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    repeat(5) { index ->
                        Icon(
                            imageVector = if (index < rating.toInt()) Icons.Filled.Star else Icons.Outlined.Star,
                            contentDescription = null,
                            tint = if (index < rating.toInt()) tertiaryColor else onSurfaceColor.copy(alpha = 0.5f),
                            modifier = Modifier.size(10.dp)
                        )
                    }
                    Text(
                        text = String.format("%.1f", rating),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = onSurfaceColor,
                        modifier = Modifier.padding(start = 4.dp),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            // Content Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Title and Property Type Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Title with verification
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = title,
                            fontWeight = FontWeight.Bold,
                            color = onSurfaceColor,
                            fontSize = 16.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.titleSmall
                        )

                        if (isVerified) {
                            Icon(
                                imageVector = Icons.Filled.Verified,
                                contentDescription = "Verified",
                                tint = primaryColor,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // Property Type
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(secondaryColor.copy(alpha = 0.1f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = type,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = secondaryColor,
                            maxLines = 1,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }

                // Short Description (new)
                if (description.isNotEmpty()) {
                    Text(
                        text = description,
                        fontSize = 12.sp,
                        color = onSurfaceVariantColor,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }

                // Location
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = null,
                        tint = onSurfaceVariantColor,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = location,
                        fontSize = 12.sp,
                        color = onSurfaceVariantColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                // Price Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = price,
                            fontWeight = FontWeight.ExtraBold,
                            color = primaryColor,
                            fontSize = 20.sp,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = if (isForSale) "" else "/month",
                            color = onSurfaceVariantColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    // Premium indicator
                    if (rating >= 4.5) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(tertiaryColor.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "PREMIUM",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = tertiaryColor,
                                letterSpacing = 0.5.sp,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }

                // Divider
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(outlineVariantColor)
                )

                // Contact Icons Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Message Icon
                    ContactIcon(
                        icon = Icons.Filled.Message,
                        contentDescription = "Send message",
                        onClick = onMessageClick,
                        tint = secondaryColor,
                        backgroundColor = secondaryColor.copy(alpha = 0.1f)
                    )

                    // WhatsApp Icon
                    ContactIcon(
                        icon = Icons.Filled.Whatsapp,
                        contentDescription = "Chat on WhatsApp",
                        onClick = onWhatsAppClick,
                        tint = primaryColor,
                        backgroundColor = primaryColor.copy(alpha = 0.1f)
                    )

                    // Phone Icon
                    ContactIcon(
                        icon = Icons.Filled.Call,
                        contentDescription = "Call now",
                        onClick = onPhoneClick,
                        tint = secondaryColor,
                        backgroundColor = secondaryColor.copy(alpha = 0.1f)
                    )
                }

                // Action Buttons Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // View Button
                    Button(
                        onClick = onViewClick,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primaryColor
                        ),
                        shape = RoundedCornerShape(50.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Visibility,
                                contentDescription = null,
                                tint = onPrimaryColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "View",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = onPrimaryColor,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }

                    // Book Button
                    OutlinedButton(
                        onClick = onBookClick,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = secondaryColor
                        ),
                        border = BorderStroke(1.dp, secondaryColor),
                        shape = RoundedCornerShape(50.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CalendarMonth,
                                contentDescription = null,
                                tint = secondaryColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Book",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = secondaryColor,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }
        }
    }
}