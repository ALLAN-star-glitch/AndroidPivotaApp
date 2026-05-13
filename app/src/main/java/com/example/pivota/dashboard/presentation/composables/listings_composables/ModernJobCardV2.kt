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

@Composable
fun ModernJobCardV2(
    modifier: Modifier = Modifier,
    imageUrl: Any? = null,
    jobTitle: String,
    companyName: String,
    location: String,
    postedTime: String,
    employmentType: String,  // e.g., "Formal" or "Informal"
    jobType: String,         // e.g., "Remote", "Full-time", "Contract", "Gig", "Part-time", "Hybrid", "On-site"
    onViewDetailsClick: () -> Unit = {},
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariantColor = MaterialTheme.colorScheme.onSurfaceVariant
    val surfaceColor = MaterialTheme.colorScheme.surface
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val tertiaryColor = MaterialTheme.colorScheme.tertiary

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
            // Left side: Image with rounded corners
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
                        contentDescription = "$companyName logo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        error = painterResource(id = R.drawable.job_placeholder1)
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }

            // Right side: Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Top row: Two badges - Employment Type + Job Type
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // First badge - Employment Type (Formal/Informal)
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = primaryColor.copy(alpha = 0.1f),
                        modifier = Modifier
                    ) {
                        Text(
                            text = employmentType.uppercase(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = primaryColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            maxLines = 1
                        )
                    }

                    // Second badge - Job Type (Remote, Full-time, Contract, Gig, etc.)
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = secondaryColor.copy(alpha = 0.1f),
                        modifier = Modifier
                    ) {
                        Text(
                            text = jobType,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = secondaryColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Job Title
                Text(
                    text = jobTitle,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = onSurfaceColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleSmall
                )

                // Company Name
                Text(
                    text = companyName,
                    fontSize = 11.sp,
                    color = onSurfaceVariantColor.copy(alpha = 0.8f),
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

                Spacer(modifier = Modifier.height(8.dp))

                // View details link only (no button)
                Text(
                    text = "View details →",
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
private const val SAMPLE_IMAGE_URL = "https://images.unsplash.com/photo-1573164713988-8665fc963095?w=100&h=100&fit=crop"

// Light Theme Preview - Multiple Cards showing different combinations
@Preview(
    name = "Light Theme - Multiple Cards",
    showBackground = true,
    backgroundColor = 0xFFF7F9FE,
    heightDp = 750,
    widthDp = 400
)
@Composable
private fun PreviewModernJobCardV2Light() {
    PivotaConnectTheme(darkTheme = false) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Formal + Full-time
            ModernJobCardV2(
                imageUrl = SAMPLE_IMAGE_URL,
                jobTitle = "Senior Software Developer",
                companyName = "Safaricom PLC",
                location = "Nairobi, Westlands",
                postedTime = "2h ago",
                employmentType = "Formal",
                jobType = "Full-time",
                onViewDetailsClick = {}
            )

            // Informal + Gig
            ModernJobCardV2(
                imageUrl = null,
                jobTitle = "Welder & Fabricator",
                companyName = "Joseph's Welding Services",
                location = "Nairobi, Industrial Area",
                postedTime = "1d ago",
                employmentType = "Informal",
                jobType = "Gig",
                onViewDetailsClick = {}
            )

            // Formal + Remote
            ModernJobCardV2(
                imageUrl = SAMPLE_IMAGE_URL,
                jobTitle = "Mobile App Developer - Flutter",
                companyName = "Pivota Labs",
                location = "Remote",
                postedTime = "5h ago",
                employmentType = "Formal",
                jobType = "Remote",
                onViewDetailsClick = {}
            )

            // Informal + Contract
            ModernJobCardV2(
                imageUrl = null,
                jobTitle = "Electrician - Residential",
                companyName = "John's Electrical Services",
                location = "Nairobi, Eastlands",
                postedTime = "3d ago",
                employmentType = "Informal",
                jobType = "Contract",
                onViewDetailsClick = {}
            )

            // Formal + Part-time
            ModernJobCardV2(
                imageUrl = SAMPLE_IMAGE_URL,
                jobTitle = "Customer Service Representative",
                companyName = "Equity Bank",
                location = "Nairobi, CBD",
                postedTime = "1d ago",
                employmentType = "Formal",
                jobType = "Part-time",
                onViewDetailsClick = {}
            )

            // Informal + On-site
            ModernJobCardV2(
                imageUrl = null,
                jobTitle = "Plumber",
                companyName = "Rapid Repairs",
                location = "Nairobi, All areas",
                postedTime = "6h ago",
                employmentType = "Informal",
                jobType = "On-site",
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
private fun PreviewModernJobCardV2Dark() {
    PivotaConnectTheme(darkTheme = true) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            ModernJobCardV2(
                imageUrl = SAMPLE_IMAGE_URL,
                jobTitle = "Product Manager",
                companyName = "M-KOPA Solar",
                location = "Nairobi, Kilimani",
                postedTime = "Just now",
                employmentType = "Formal",
                jobType = "Hybrid",
                onViewDetailsClick = {}
            )
        }
    }
}

// Preview showing all job type combinations
@Preview(
    name = "All Job Type Combinations",
    showBackground = true,
    heightDp = 600,
    widthDp = 400
)
@Composable
private fun PreviewAllJobCombinations() {
    PivotaConnectTheme(darkTheme = false) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Formal combinations
            ModernJobCardV2(
                jobTitle = "Formal + Remote",
                companyName = "Tech Corp",
                location = "Remote",
                postedTime = "2h ago",
                employmentType = "Formal",
                jobType = "Remote",
                onViewDetailsClick = {}
            )
            ModernJobCardV2(
                jobTitle = "Formal + Full-time",
                companyName = "Finance Ltd",
                location = "Nairobi",
                postedTime = "3h ago",
                employmentType = "Formal",
                jobType = "Full-time",
                onViewDetailsClick = {}
            )
            ModernJobCardV2(
                jobTitle = "Formal + Contract",
                companyName = "Consulting Group",
                location = "Nairobi",
                postedTime = "1d ago",
                employmentType = "Formal",
                jobType = "Contract",
                onViewDetailsClick = {}
            )
            ModernJobCardV2(
                jobTitle = "Formal + Part-time",
                companyName = "Retail Store",
                location = "Nairobi",
                postedTime = "2d ago",
                employmentType = "Formal",
                jobType = "Part-time",
                onViewDetailsClick = {}
            )
            ModernJobCardV2(
                jobTitle = "Formal + Hybrid",
                companyName = "Tech Startup",
                location = "Nairobi",
                postedTime = "5h ago",
                employmentType = "Formal",
                jobType = "Hybrid",
                onViewDetailsClick = {}
            )

            // Informal combinations
            ModernJobCardV2(
                jobTitle = "Informal + Gig",
                companyName = "Freelance Hub",
                location = "Nairobi",
                postedTime = "1h ago",
                employmentType = "Informal",
                jobType = "Gig",
                onViewDetailsClick = {}
            )
            ModernJobCardV2(
                jobTitle = "Informal + Contract",
                companyName = "Construction Co",
                location = "Nairobi",
                postedTime = "4h ago",
                employmentType = "Informal",
                jobType = "Contract",
                onViewDetailsClick = {}
            )
            ModernJobCardV2(
                jobTitle = "Informal + On-site",
                companyName = "Maintenance Services",
                location = "Nairobi",
                postedTime = "6h ago",
                employmentType = "Informal",
                jobType = "On-site",
                onViewDetailsClick = {}
            )
            ModernJobCardV2(
                jobTitle = "Informal + Flexible",
                companyName = "Task Network",
                location = "Nairobi",
                postedTime = "1d ago",
                employmentType = "Informal",
                jobType = "Flexible",
                onViewDetailsClick = {}
            )
        }
    }
}