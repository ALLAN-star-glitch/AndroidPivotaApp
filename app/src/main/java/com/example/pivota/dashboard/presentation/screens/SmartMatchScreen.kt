package com.example.pivota.dashboard.presentation.screens


import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SmartMatchScreen() {
    val primaryTeal = Color(0xFF006565)
    val goldenAccent = Color(0xFFE9C16C)
    val softBackground = Color(0xFFF6FAF9)

    Scaffold(containerColor = softBackground, contentWindowInsets = WindowInsets(0,0,0,0)) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 340.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = padding.calculateBottomPadding()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {

            // 🔝 Hero Header
            item(span = { GridItemSpan(maxLineSpan) }) {
                SmartMatchHeroHeader(primaryTeal, goldenAccent)
            }

            // 🧠 Context Banner
            item(span = { GridItemSpan(maxLineSpan) }) {
                SmartMatchContextCard(primaryTeal, goldenAccent)
            }

            // 📋 SmartMatch Listings
            val smartMatchListings = listOf(
                SmartMatchListing(
                    title = "2-Bedroom Apartment – Westlands",
                    type = "Housing",
                    location = "Nairobi",
                    reason = "Matches your saved housing searches",
                    isVerified = true
                ),
                SmartMatchListing(
                    title = "Electrician Needed – Solar Install",
                    type = "Jobs",
                    location = "Kiambu",
                    reason = "Based on your recent job views",
                    isVerified = true
                ),
                SmartMatchListing(
                    title = "NGO Legal Support Program",
                    type = "Help & Support",
                    location = "Nakuru",
                    reason = "Relevant to your community interests",
                    isVerified = false
                )
            )

            items(smartMatchListings) { listing ->
                SmartMatchListingCard(primaryTeal, goldenAccent, listing)
            }
        }
    }
}

/* ────────────── UI COMPONENTS ────────────── */

@Composable
fun SmartMatchHeroHeader(teal: Color, gold: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
    ) {
        // 🖼️ Background Image (Same as Discover/Providers for consistency)
        Image(
            painter = painterResource(id = com.example.pivota.R.drawable.nairobi_city),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // 🎨 Branded Teal Overlay (Slightly darker to make the gold SmartMatch icon pop)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            teal.copy(alpha = 0.95f),
                            teal.copy(alpha = 0.80f)
                        )
                    )
                )
        )

        // ✍️ Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = gold,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "SmartMatch™",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = (-1).sp
                            )
                        )
                    }
                    Text(
                        text = "Curated opportunities for your profile",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White.copy(0.9f)
                        )
                    )
                }

                // 🔔 Action Icons (Consistency across all dashboard tabs)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    SmartMatchHeaderIcon(Icons.Default.Mail) { /* Open Messages */ }
                    SmartMatchHeaderIcon(Icons.Default.Notifications) { /* Open Notifications */ }
                }
            }
        }
    }
}

/**
 * Helper component marked private to avoid 'Conflicting Overload' errors
 * when copying between screens.
 */
@Composable
private fun SmartMatchHeaderIcon(icon: ImageVector, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(42.dp)
            .background(Color.White.copy(0.2f), CircleShape)
            .clip(CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(22.dp)
        )
    }
}
@Composable
fun SmartMatchContextCard(teal: Color, gold: Color) {
    Card(
        modifier = Modifier.padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .drawWithContent {
                    drawContent()
                    drawRect(gold, size = size.copy(width = 4.dp.toPx()))
                }
                .padding(start = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Why you’re seeing these",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = teal
                )
                Text(
                    "SmartMatch™ uses your activity, interests, and saved actions to recommend relevant listings.",
                    fontSize = 13.sp
                )
            }
            Icon(Icons.Default.Info, contentDescription = null, tint = gold)
        }
    }
}

@Composable
fun SmartMatchListingCard(
    teal: Color,
    gold: Color,
    data: SmartMatchListing
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column {

            // 🖼️ Header Image Placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .background(teal.copy(0.1f))
            ) {
                Box(
                    modifier = Modifier.fillMaxSize().background(
                        Brush.horizontalGradient(
                            listOf(teal.copy(0.45f), Color.Transparent)
                        )
                    )
                )

                if (data.isVerified) {
                    Surface(
                        color = gold,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .padding(12.dp)
                            .align(Alignment.TopEnd)
                    ) {
                        Text(
                            "Verified",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {

                Text(
                    data.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    data.type,
                    color = teal,
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        null,
                        modifier = Modifier.size(14.dp),
                        tint = Color.Gray
                    )
                    Text(data.location, fontSize = 12.sp, color = Color.Gray)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    color = gold.copy(0.15f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        data.reason,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = teal
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {},
                        modifier = Modifier.weight(1f),
                        border = BorderStroke(1.dp, teal),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("View Details", color = teal)
                    }
                    Button(
                        onClick = {},
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(teal),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Take Action")
                    }
                }
            }
        }
    }
}

/* ────────────── DATA MODEL ────────────── */

data class SmartMatchListing(
    val title: String,
    val type: String,
    val location: String,
    val reason: String,
    val isVerified: Boolean
)
