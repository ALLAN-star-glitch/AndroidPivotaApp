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
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.LocationOn
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
import com.example.pivota.R
import com.example.pivota.ui.theme.PivotaConnectTheme

enum class ProfessionalType {
    INDIVIDUAL,
    ORGANIZATION
}

@Composable
fun ModernProfessionalCardV2(
    modifier: Modifier = Modifier,
    imageUrl: Any? = null,
    name: String,
    profession: String,          // e.g., "Electrician", "Plumber", "Software Developer"
    location: String,
    postedTime: String,
    professionalType: ProfessionalType,  // INDIVIDUAL or ORGANIZATION
    rating: Float,
    jobsCompleted: Int,
    onViewDetailsClick: () -> Unit = {},

) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariantColor = MaterialTheme.colorScheme.onSurfaceVariant
    val surfaceColor = MaterialTheme.colorScheme.surface
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val tertiaryColor = MaterialTheme.colorScheme.tertiary

    // Second badge properties based on professional type
    val (secondBadgeIcon, secondBadgeText, secondBadgeColor) = when (professionalType) {
        ProfessionalType.INDIVIDUAL -> Triple(
            Icons.Filled.Person,
            "Individual",
            secondaryColor
        )
        ProfessionalType.ORGANIZATION -> Triple(
            Icons.Filled.Business,
            "Company",
            primaryColor
        )
    }

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
            // Left side: Profile image with rounded corners
            Box(
                modifier = Modifier
                    .size(70.dp, 70.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
                if (imageUrl != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = "$name profile",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        error = painterResource(id = R.drawable.job_placeholder1)
                    )
                } else {
                    // Fallback - show first letter
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = name.take(1).uppercase(),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = primaryColor
                        )
                    }
                }
            }

            // Right side: Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Top row: Two badges - First always "SERVICE", Second is Individual/Company
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // First badge - Always "SERVICE"
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = primaryColor.copy(alpha = 0.1f),
                        modifier = Modifier
                    ) {
                        Text(
                            text = "SERVICE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = primaryColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            maxLines = 1
                        )
                    }

                    // Second badge - Individual or Company
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = secondBadgeColor.copy(alpha = 0.1f),
                        modifier = Modifier
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Icon(
                                imageVector = secondBadgeIcon,
                                contentDescription = null,
                                tint = secondBadgeColor,
                                modifier = Modifier.size(10.dp)
                            )
                            Text(
                                text = secondBadgeText,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = secondBadgeColor,
                                maxLines = 1
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Name
                Text(
                    text = name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = onSurfaceColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleSmall
                )

                // Profession
                Text(
                    text = profession,
                    fontSize = 12.sp,
                    color = onSurfaceVariantColor,
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

                Spacer(modifier = Modifier.height(6.dp))

                // Rating and Jobs row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Rating
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "Rating",
                            tint = tertiaryColor,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = String.format("%.1f", rating),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = onSurfaceColor
                        )
                        Text(
                            text = "($jobsCompleted jobs)",
                            fontSize = 10.sp,
                            color = onSurfaceVariantColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // View details link only
                Text(
                    text = "View profile →",
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
private const val SAMPLE_IMAGE_URL = "https://images.unsplash.com/photo-1573497019940-1c28c88b4f3e?w=120&h=120&fit=crop"

// Light Theme Preview - Multiple Cards
@Preview(
    name = "Light Theme - Multiple Cards",
    showBackground = true,
    backgroundColor = 0xFFF7F9FE,
    heightDp = 650,
    widthDp = 400
)
@Composable
private fun PreviewModernProfessionalCardV2Light() {
    PivotaConnectTheme(darkTheme = false) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Individual Professional
            ModernProfessionalCardV2(
                imageUrl = SAMPLE_IMAGE_URL,
                name = "John Mwangi",
                profession = "Electrician",
                location = "Nairobi, Eastlands",
                postedTime = "2h ago",
                professionalType = ProfessionalType.INDIVIDUAL,
                rating = 4.8f,
                jobsCompleted = 127,
                onViewDetailsClick = {}
            )

            // Organization/Company
            ModernProfessionalCardV2(
                imageUrl = null,
                name = "Nairobi Plumbers Ltd",
                profession = "Plumbing Services",
                location = "Nairobi, CBD",
                postedTime = "1d ago",
                professionalType = ProfessionalType.ORGANIZATION,
                rating = 4.5f,
                jobsCompleted = 342,
                onViewDetailsClick = {}
            )

            // Individual Professional
            ModernProfessionalCardV2(
                imageUrl = SAMPLE_IMAGE_URL,
                name = "Sarah Wanjiku",
                profession = "House Cleaner",
                location = "Nairobi, Westlands",
                postedTime = "5h ago",
                professionalType = ProfessionalType.INDIVIDUAL,
                rating = 4.2f,
                jobsCompleted = 89,
                onViewDetailsClick = {}
            )

            // Organization/Company
            ModernProfessionalCardV2(
                imageUrl = null,
                name = "Digital Solutions KE",
                profession = "Software Development",
                location = "Remote",
                postedTime = "3d ago",
                professionalType = ProfessionalType.ORGANIZATION,
                rating = 4.9f,
                jobsCompleted = 56,
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
    heightDp = 220,
    widthDp = 400
)
@Composable
private fun PreviewModernProfessionalCardV2Dark() {
    PivotaConnectTheme(darkTheme = true) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            ModernProfessionalCardV2(
                imageUrl = SAMPLE_IMAGE_URL,
                name = "Peter Ochieng",
                profession = "Carpenter",
                location = "Nairobi, Industrial Area",
                postedTime = "Just now",
                professionalType = ProfessionalType.INDIVIDUAL,
                rating = 4.7f,
                jobsCompleted = 45,
                onViewDetailsClick = {}
            )
        }
    }
}

// Preview showing both badge types
@Preview(
    name = "Badge Variations",
    showBackground = true,
    heightDp = 400,
    widthDp = 400
)
@Composable
private fun PreviewBadgeVariations() {
    PivotaConnectTheme(darkTheme = false) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Individual badge
            ModernProfessionalCardV2(
                name = "Jane Akinyi",
                profession = "Tailor",
                location = "Nairobi, Kibera",
                postedTime = "1h ago",
                professionalType = ProfessionalType.INDIVIDUAL,
                rating = 4.6f,
                jobsCompleted = 34,
                onViewDetailsClick = {}
            )

            // Company badge
            ModernProfessionalCardV2(
                name = "Elite Security Services",
                profession = "Security Guards",
                location = "Nairobi, Westlands",
                postedTime = "4h ago",
                professionalType = ProfessionalType.ORGANIZATION,
                rating = 4.3f,
                jobsCompleted = 89,
                onViewDetailsClick = {}
            )
        }
    }
}