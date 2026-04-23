package com.example.pivota.dashboard.presentation.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.pivota.R
import com.example.pivota.dashboard.domain.model.ProfessionalType

// Add this enum to distinguish between individuals and organizations
enum class ProviderType {
    INDIVIDUAL,
    ORGANIZATION
}

@Composable
fun ModernProfessionalCard(
    modifier: Modifier = Modifier,
    name: String,
    specialty: String,
    rating: Float,
    jobs: Int,
    isVerified: Boolean,
    professionalType: ProfessionalType = ProfessionalType.INDIVIDUAL, // New parameter
    description: String = "",
    coverImageRes: Any? = null, // New cover image parameter
    profileImageRes: Any? = null, // Profile image
    onCardClick: () -> Unit = {},
    onViewClick: () -> Unit = {},
    onBookClick: () -> Unit = {},
    onHireClick: () -> Unit,

    ) {
    // Using MaterialTheme colors from your theme
    val primaryColor = MaterialTheme.colorScheme.primary      // African Sapphire
    val secondaryColor = MaterialTheme.colorScheme.secondary  // Warm Terracotta
    val tertiaryColor = MaterialTheme.colorScheme.tertiary    // Baobab Gold
    val surfaceColor = MaterialTheme.colorScheme.surface
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariantColor = MaterialTheme.colorScheme.onSurfaceVariant
    val onPrimaryColor = MaterialTheme.colorScheme.onPrimary
    val onSecondaryColor = MaterialTheme.colorScheme.onSecondary

    // Determine badge properties based on provider type
    val (badgeIcon, badgeText, badgeColor) = when (professionalType) {
        ProfessionalType.INDIVIDUAL -> Triple(Icons.Default.Person, "Individual", secondaryColor)
        ProfessionalType.ORGANIZATION -> Triple(Icons.Default.Business, "Company", primaryColor)
    }

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
        ) {
            // Cover Image Section (NEW)
            if (coverImageRes != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .background(primaryColor.copy(0.1f))
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(coverImageRes)
                            .crossfade(true)
                            .build(),
                        contentDescription = "$name cover image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        error = painterResource(id = R.drawable.property_placeholder1)
                    )

                    // Gradient overlay for better text visibility
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        surfaceColor.copy(alpha = 0.9f)
                                    ),
                                    startY = 50f
                                )
                            )
                    )
                }
            }

            // Content Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Header with profile image, name, and badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Profile Image or Avatar with first letter
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(primaryColor.copy(0.08f))
                            .border(2.dp, surfaceColor, CircleShape), // Add border to separate from cover
                        contentAlignment = Alignment.Center
                    ) {
                        if (profileImageRes != null) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(profileImageRes)
                                    .crossfade(true)
                                    .size(56)
                                    .build(),
                                contentDescription = "$name profile picture",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize(),
                                error = painterResource(id = R.drawable.job_placeholder1)
                            )
                        } else {
                            // Fallback to first letter avatar
                            Text(
                                text = name.take(1).uppercase(),
                                color = primaryColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Name and specialty
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
                                fontSize = 16.sp,
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
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Text(
                            text = specialty,
                            fontSize = 13.sp,
                            color = onSurfaceVariantColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Provider Type Badge (NEW)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    // Badge
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = badgeColor.copy(0.1f),
                        border = BorderStroke(1.dp, badgeColor.copy(0.3f))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                badgeIcon,
                                contentDescription = null,
                                tint = badgeColor,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = badgeText,
                                fontSize = 10.sp,
                                color = badgeColor,
                                fontWeight = FontWeight.Medium,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Response time indicator
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = tertiaryColor.copy(0.1f)
                    ) {
                        Text(
                            text = "⭐ SmartMatch",
                            fontSize = 10.sp,
                            color = tertiaryColor,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                // Short Description
                if (description.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = description,
                        fontSize = 12.sp,
                        color = onSurfaceVariantColor,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                // Stats Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
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

                Spacer(modifier = Modifier.height(8.dp))

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

                    // Hire Button
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
                                text = "Hire",
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