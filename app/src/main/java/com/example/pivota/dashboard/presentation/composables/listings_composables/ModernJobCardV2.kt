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
import androidx.compose.foundation.shape.CircleShape
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
    val colorScheme = MaterialTheme.colorScheme
    val primaryColor = colorScheme.primary
    val onSurfaceColor = colorScheme.onSurface
    val onSurfaceVariantColor = colorScheme.onSurfaceVariant
    val surfaceColor = colorScheme.surface
    val secondaryColor = colorScheme.secondary
    val tertiaryColor = colorScheme.tertiary

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onViewDetailsClick() }
            .drawBehind {
                // Subtle dotted border pattern
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
                // Left side: Company logo with gradient ring
                Box(
                    modifier = Modifier.size(56.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Gradient ring for verified/featured jobs
                    Surface(
                        modifier = Modifier.size(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = Color.Transparent,
                        border = androidx.compose.foundation.BorderStroke(
                            1.5.dp,
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

                    Surface(
                        modifier = Modifier.size(48.dp),
                        shape = RoundedCornerShape(10.dp),
                        color = primaryColor.copy(alpha = 0.1f)
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
                                    text = companyName
                                        .split(" ")
                                        .take(2)
                                        .map { it.firstOrNull()?.toString() ?: "" }
                                        .joinToString("")
                                        .uppercase()
                                        .take(2),
                                    fontSize = 14.sp,
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

                        // Second badge - Job Type
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
                        overflow = TextOverflow.Ellipsis
                    )

                    // Company Name
                    Text(
                        text = companyName,
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
                            modifier = Modifier.size(11.dp)
                        )
                        Text(
                            text = location,
                            fontSize = 11.sp,
                            color = onSurfaceVariantColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Divider line with gradient
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

                    // View details link and posted time row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // View details link
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.clickable { onViewDetailsClick() }
                        ) {
                            Text(
                                text = "View details",
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

                        // Posted time
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AccessTime,
                                contentDescription = "Posted time",
                                tint = onSurfaceVariantColor.copy(alpha = 0.6f),
                                modifier = Modifier.size(11.dp)
                            )
                            Text(
                                text = postedTime,
                                fontSize = 10.sp,
                                color = onSurfaceVariantColor.copy(alpha = 0.7f),
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}

// Sample image URL for testing
private const val SAMPLE_IMAGE_URL = "https://images.unsplash.com/photo-1573164713988-8665fc963095?w=100&h=100&fit=crop"

// Light Theme Preview - Multiple Cards
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