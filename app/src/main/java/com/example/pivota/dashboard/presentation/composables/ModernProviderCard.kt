package com.example.pivota.dashboard.presentation.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Whatsapp
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Import the shared ContactIcon
import com.example.pivota.dashboard.presentation.composables.ContactIcon

@Composable
fun ModernProviderCard(
    name: String,
    specialty: String,
    rating: Float,
    jobs: Int,
    isVerified: Boolean,
    description: String = "", // Added description parameter
    onCardClick: () -> Unit = {},
    onViewClick: () -> Unit = {},
    onBookClick: () -> Unit = {},
    onMessageClick: () -> Unit = {},
    onWhatsAppClick: () -> Unit = {},
    onPhoneClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Using MaterialTheme colors from your theme
    val primaryColor = MaterialTheme.colorScheme.primary      // African Sapphire
    val secondaryColor = MaterialTheme.colorScheme.secondary  // Warm Terracotta
    val tertiaryColor = MaterialTheme.colorScheme.tertiary    // Baobab Gold
    val surfaceColor = MaterialTheme.colorScheme.surface
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariantColor = MaterialTheme.colorScheme.onSurfaceVariant
    val onPrimaryColor = MaterialTheme.colorScheme.onPrimary

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCardClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = surfaceColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header with avatar and info
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Avatar with first letter
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(primaryColor.copy(0.08f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = name.take(1),
                        color = primaryColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Name and specialty - Takes remaining space
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = name,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = onSurfaceColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.weight(1f)
                        )
                        if (isVerified) {
                            Icon(
                                Icons.Outlined.Verified,
                                contentDescription = "Verified",
                                tint = primaryColor,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                    Text(
                        text = specialty,
                        fontSize = 12.sp,
                        color = onSurfaceVariantColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Short Description (new)
            if (description.isNotEmpty()) {
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = onSurfaceVariantColor,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            // Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Rating
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Icon(
                            Icons.Outlined.Star,
                            contentDescription = null,
                            tint = tertiaryColor,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = String.format("%.1f", rating),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = onSurfaceColor,
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                    Text(
                        text = "Rating",
                        fontSize = 10.sp,
                        color = onSurfaceVariantColor,
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                // Jobs completed
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "$jobs+",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = primaryColor,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = "Jobs",
                        fontSize = 10.sp,
                        color = onSurfaceVariantColor,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Contact Icons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Message Icon
                ContactIcon(
                    onClick = onMessageClick,
                    icon = Icons.Filled.Message,
                    contentDescription = "Send message",
                    tint = secondaryColor,
                    backgroundColor = secondaryColor.copy(alpha = 0.1f)
                )

                // WhatsApp Icon
                ContactIcon(
                    onClick = onWhatsAppClick,
                    icon = Icons.Filled.Whatsapp,
                    contentDescription = "Chat on WhatsApp",
                    tint = primaryColor,
                    backgroundColor = primaryColor.copy(alpha = 0.1f)
                )

                // Phone Icon
                ContactIcon(
                    onClick = onPhoneClick,
                    icon = Icons.Filled.Call,
                    contentDescription = "Call now",
                    tint = secondaryColor,
                    backgroundColor = secondaryColor.copy(alpha = 0.1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

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
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
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
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
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