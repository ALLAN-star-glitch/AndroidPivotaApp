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
import com.example.pivota.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmartMatchScreen() {
    val primaryTeal = Color(0xFF006565)
    val goldenAccent = Color(0xFFE9C16C)
    val softBackground = Color(0xFFF6FAF9)

    // 📱 Orientation & Adaptive Logic
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val windowSizeClass = androidx.compose.material3.adaptive.currentWindowAdaptiveInfo().windowSizeClass

    val isPortrait = configuration.orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT
    val isTabletWidth = windowSizeClass.windowWidthSizeClass != androidx.window.core.layout.WindowWidthSizeClass.COMPACT

    // 🎯 Logic: Sticky in Portrait, Scrolling in Landscape
    val shouldScrollHeader = isTabletWidth && !isPortrait

    Scaffold(
        containerColor = softBackground,
        topBar = {
            if (!shouldScrollHeader) {
                SmartMatchHeroHeader(primaryTeal, goldenAccent)
            }
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 340.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = padding.calculateBottomPadding()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // Adaptive Header Placement
            if (shouldScrollHeader) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    SmartMatchHeroHeader(primaryTeal, goldenAccent)
                }
            } else {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Spacer(modifier = Modifier.height(padding.calculateTopPadding()))
                }
            }

            // 🧠 Context Banner
            item(span = { GridItemSpan(maxLineSpan) }) {
                SmartMatchContextCard(primaryTeal, goldenAccent)
            }

            // 📋 SmartMatch Listings
            val smartMatchListings = listOf(
                SmartMatchListing("2-Bedroom Apartment – Westlands", "Housing", "Nairobi", "Matches your saved housing searches", true),
                SmartMatchListing("Electrician Needed – Solar Install", "Jobs", "Kiambu", "Based on your recent job views", true),
                SmartMatchListing("NGO Legal Support Program", "Help & Support", "Nakuru", "Relevant to your community interests", false)
            )

            items(smartMatchListings) { listing ->
                SmartMatchListingCard(primaryTeal, goldenAccent, listing)
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun SmartMatchHeroHeader(teal: Color, gold: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp) // Matched to Discover & Providers Screen
    ) {
        // 1. Background Image
        Image(
            painter = painterResource(id = R.drawable.nairobi_city),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // 2. Teal Overlay (Vertical)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(teal.copy(0.98f), teal.copy(0.7f), Color.Transparent)
                    )
                )
        )

        // 3. Golden Accent (Horizontal Glow)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(gold.copy(0.15f), Color.Transparent),
                        endX = 400f
                    )
                )
        )

        // 4. Content (Avatar, Greeting, Icons, and Branding)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // --- TOP ROW (Consistency with Discover) ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box {
                    // Reusing avatar logic
                    Box(
                        modifier = Modifier
                            .size(45.dp)
                            .background(Color.White.copy(0.2f), CircleShape)
                            .border(1.5.dp, Color.White.copy(0.5f), CircleShape)
                            .clip(CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.PersonOutline, null, tint = Color.White, modifier = Modifier.size(24.dp))
                    }
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(gold, CircleShape)
                            .border(2.dp, teal, CircleShape)
                            .align(Alignment.BottomEnd)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Hi, Guest",
                        style = MaterialTheme.typography.titleMedium.copy(color = Color.White, fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Your AI-Picks",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(0.8f))
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    SmartMatchHeaderIcon(Icons.Default.Mail) { }
                    SmartMatchHeaderIcon(Icons.Default.Notifications) { }
                }
            }

            // --- BOTTOM ROW ---
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = gold,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "SmartMatch",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-1.5).sp
                        )
                    )
                }
                Text(
                    text = "Opportunities tailored for you",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.White.copy(0.9f))
                )
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
