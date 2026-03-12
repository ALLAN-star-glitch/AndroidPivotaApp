package com.example.pivota.admin.presentation.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pivota.core.presentations.composables.TopBar
import com.example.pivota.listings.domain.models.HousingPost

val sampleHousingPost = HousingPost(
    images = listOf(
        "https://example.com/image1.jpg",
        "https://example.com/image2.jpg",
        "https://example.com/image3.jpg"
    ),
    currency = "KES",
    price = 85000,
    priceRate = "/mo",
    title = "Modern 3-Bedroom Apartment",
    description = "Spacious 3-bedroom apartment located in the heart of Westlands. Features modern finishing, ample parking, and 24/7 security. Proximity to major shopping malls, schools, and hospitals. Well-lit naturally and freshly painted. Ready for immediate occupation.",
    country = "Kenya",
    city = "Nairobi",
    neighbourhood = "Westlands",
    address = null, // Or provide a mock address like "123 Rapta Road"
    bedrooms = 3,
    bathrooms = 2,
    furnished = false,
    type = "Apartment",
    amenities = listOf("Water", "Electricity", "Parking", "Wi-Fi")
)

@Preview(showBackground = true)
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HouseDetailsScreen(
    post: HousingPost = sampleHousingPost,
    onBack: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopBar (
                icon = Icons.Default.Edit,
                title = "House Details",
                onBack = onBack
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // 1. Image Banner
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.LightGray) // Placeholder for Image
                ) {
                    // Replace with AsyncImage in real implementation
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = null,
                        modifier = Modifier.align(Alignment.Center).size(64.dp),
                        tint = Color.Gray
                    )

                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(12.dp)
                            .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "1/${post.images.size.coerceAtLeast(1)}",
                            color = Color.White,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // 2. Header Information
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Text(
                        text = post.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Outlined.LocationOn,
                            contentDescription = "Location",
                            modifier = Modifier.size(16.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${post.neighbourhood}, ${post.city}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = post.type,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )

                        // Available Badge
                        Surface(
                            color = Color(0xFFE0F2F1), // Light teal background
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = "Available",
                                color = Color(0xFF00796B), // Dark teal text
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.surfaceVariant)
            }

            // 3. Owner Information
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Mock Avatar
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = Color.White)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("David K.", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.CheckCircle, contentDescription = "Verified", tint = Color(0xFFFFB300), modifier = Modifier.size(16.dp))
                        }
                        Text("Verified Owner", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }

                    IconButton(onClick = { }, modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant, CircleShape).size(40.dp)) {
                        Icon(Icons.Default.Phone, contentDescription = "Call", tint = Color.DarkGray, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = { }, modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant, CircleShape).size(40.dp)) {
                        Icon(Icons.Outlined.Email, contentDescription = "Message", tint = Color.DarkGray, modifier = Modifier.size(20.dp))
                    }
                }
            }

            // 4. Property Details Card
            item {
                DetailCard(title = "Property Details") {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(Modifier.fillMaxWidth()) {
                            DetailItem(
                                modifier = Modifier.weight(1f),
                                label = "Price / Rent",
                                value = buildAnnotatedString {
                                    withStyle(style = SpanStyle(color = Color(0xFF00796B), fontWeight = FontWeight.Bold)) { // Green price
                                        append("${post.currency} ${post.price}")
                                    }
                                    withStyle(style = SpanStyle(color = Color.Gray, fontSize = 12.sp)) {
                                        append(post.priceRate)
                                    }
                                }
                            )
                            DetailItem(
                                modifier = Modifier.weight(1f),
                                label = "Rooms",
                                valueText = "${post.bedrooms} Beds, ${post.bathrooms} Baths"
                            )
                        }
                        Row(Modifier.fillMaxWidth()) {
                            DetailItem(
                                modifier = Modifier.weight(1f),
                                label = "Size",
                                valueText = "120 sq.m" // Hardcoded to match UI as it's missing in HousingPost
                            )
                            DetailItem(
                                modifier = Modifier.weight(1f),
                                label = "Type",
                                valueText = post.type
                            )
                        }
                    }
                }
            }

            // 5. Facilities Card
            item {
                DetailCard(title = "Facilities") {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Using provided amenities or fallback to mock data to match screenshot
                        val displayAmenities =
                            post.amenities.ifEmpty { listOf("Water", "Electricity", "Parking", "Wi-Fi") }

                        displayAmenities.forEach { amenity ->
                            Surface(
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Simplified icon mapping
                                    val icon = when(amenity.lowercase()) {
                                        "water" -> Icons.Outlined.WaterDrop
                                        "electricity" -> Icons.Outlined.Bolt
                                        "parking" -> Icons.Outlined.LocalParking
                                        "wi-fi" -> Icons.Outlined.Wifi
                                        else -> Icons.Outlined.CheckCircle
                                    }
                                    Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(amenity, style = MaterialTheme.typography.bodyMedium, color = Color.DarkGray)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(
                        onClick = { },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("View full amenities", color = Color(0xFF00796B), fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // 6. Description Card
            item {
                DetailCard(title = "Description") {
                    Text(
                        text = post.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        lineHeight = 20.sp
                    )
                }
            }

            // 7. Activity Logs Card
            item {
                DetailCard(title = "Activity Logs") {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        ActivityLogItem(icon = Icons.Outlined.CheckCircle, iconTint = Color(0xFF00796B), title = "Listing approved by Admin", time = "20 Feb, 10:30 AM")
                        ActivityLogItem(icon = Icons.Outlined.PhotoLibrary, iconTint = Color.Gray, title = "Gallery updated (3 new photos)", time = "19 Feb, 02:15 PM")
                        ActivityLogItem(icon = Icons.Outlined.Description, iconTint = Color.Gray, title = "Listing submitted by David K.", time = "18 Feb, 09:00 AM")
                    }
                }
            }

            // 8. Action Buttons
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00695C)), // Dark Teal
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Approve / Mark Available", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)), // Red
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Reject / Mark Unavailable", fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Text("Edit Listing", color = Color.DarkGray, fontWeight = FontWeight.SemiBold)
                    }

                    TextButton(
                        onClick = { },
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Text("Delete Listing", color = Color(0xFFD32F2F), fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

// Reusable Custom Card Component
@Composable
fun DetailCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray
            )
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}

// Reusable Detail Item
@Composable
fun DetailItem(
    modifier: Modifier = Modifier,
    label: String,
    valueText: String? = null,
    value: androidx.compose.ui.text.AnnotatedString? = null
) {
    Column(modifier = modifier) {
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
        Spacer(modifier = Modifier.height(4.dp))
        if (value != null) {
            Text(text = value)
        } else if (valueText != null) {
            Text(text = valueText, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color.DarkGray)
        }
    }
}

// Reusable Activity Log Item
@Composable
fun ActivityLogItem(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    time: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(title, style = MaterialTheme.typography.bodyMedium, color = Color.DarkGray)
            Text(time, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        }
    }
}