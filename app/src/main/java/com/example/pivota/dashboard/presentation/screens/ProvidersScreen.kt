package com.example.pivota.dashboard.presentation.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/* ─────────────────────────────────────────────
   Providers Screen
   Purpose: Discover & connect with verified service providers
   Tone: Trustworthy, calm, professional
   ───────────────────────────────────────────── */

@Composable
fun ProvidersScreen() {

    val primaryTeal = Color(0xFF006565)
    val goldenAccent = Color(0xFFE9C16C)
    val softBackground = Color(0xFFF6FAF9)

    Scaffold(
        containerColor = softBackground,
        contentWindowInsets = WindowInsets(0,0,0,0)

    ) { padding ->

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 340.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = padding.calculateBottomPadding()),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            /* ── HERO HEADER ─────────────────────── */

            item(span = { GridItemSpan(maxLineSpan) }) {
                PivotaProvidersHeroHeader(primaryTeal, goldenAccent)
            }

            /* ── SEARCH + FILTER ───────────────── */

            item(span = { GridItemSpan(maxLineSpan) }) {
                ProvidersSearchSection(primaryTeal)
            }

            /* ── SMART MATCH CALLOUT ───────────── */

            item(span = { GridItemSpan(maxLineSpan) }) {
                SmartMatchProviderHighlight(primaryTeal, goldenAccent)
            }

            /* ── PROVIDER LIST ─────────────────── */

            val providers = listOf(
                ProviderData(
                    name = "Musa Jallow",
                    category = "Electrician • Solar Specialist",
                    location = "Nairobi",
                    isVerified = true,
                    isSmartMatch = true
                ),
                ProviderData(
                    name = "Sarah Wanjiku",
                    category = "Interior Designer",
                    location = "Kiambu",
                    isVerified = true,
                    isSmartMatch = false
                ),
                ProviderData(
                    name = "Pivota Housing Ltd",
                    category = "Property & Facility Management",
                    location = "Mombasa",
                    isVerified = true,
                    isSmartMatch = true
                ),
                ProviderData(
                    name = "Legal Aid Kenya",
                    category = "Help & Support • NGO Partner",
                    location = "Nakuru",
                    isVerified = true,
                    isSmartMatch = false
                )
            )

            items(providers) { provider ->
                ProviderCard(
                    teal = primaryTeal,
                    gold = goldenAccent,
                    data = provider
                )
            }
        }
    }
}

/* ─────────────────────────────────────────────
   HERO HEADER
   ───────────────────────────────────────────── */

@Composable
fun PivotaProvidersHeroHeader(teal: Color, gold: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
    ) {
        // 🖼️ Background Image
        Image(
            painter = painterResource(id = com.example.pivota.R.drawable.nairobi_city),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // 🎨 Branded Teal Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            teal.copy(alpha = 0.92f),
                            teal.copy(alpha = 0.70f)
                        )
                    )
                )
        )

        // ✍️ Content (Text & Icons)
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
                    Text(
                        text = "Discover",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-1).sp
                        )
                    )
                    Text(
                        text = "Life opportunities, tailored for you",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White.copy(0.9f)
                        )
                    )
                }

                // 🔔 Action Icons
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Message Icon
                    HeaderActionIconButton(Icons.Default.Mail) { /* Navigate to Messages */ }
                    // Notification Icon
                    HeaderActionIconButton(Icons.Default.Notifications) { /* Navigate to Notifications */ }
                }
            }
        }
    }
}

/**
 * Marked 'private' so it cannot conflict with other files in the same package
 */
@Composable
private fun HeaderActionIconButton(icon: ImageVector, onClick: () -> Unit) {
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

/* ─────────────────────────────────────────────
   SEARCH + FILTER
   ───────────────────────────────────────────── */

@Composable
private fun ProvidersSearchSection(teal: Color) {

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
    ) {

        OutlinedTextField(
            value = "",
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text("Search by skill, service, or location", fontSize = 14.sp)
            },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null, tint = teal)
            },
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = teal,
                unfocusedBorderColor = teal.copy(0.3f),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            listOf(
                "All",
                "Verified",
                "Housing",
                "Jobs",
                "Help & Support"
            ).forEach { label ->

                FilterChip(
                    selected = label == "All",
                    onClick = {},
                    label = { Text(label) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = teal,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }
    }
}

/* ─────────────────────────────────────────────
   PROVIDER CARD
   ───────────────────────────────────────────── */

@Composable
private fun ProviderCard(
    teal: Color,
    gold: Color,
    data: ProviderData
) {

    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {

        Column {

            /* Header */

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(96.dp)
                    .background(teal.copy(alpha = 0.1f))
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                listOf(teal.copy(0.45f), Color.Transparent)
                            )
                        )
                )

                Box(
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .align(Alignment.BottomStart)
                        .offset(y = 28.dp)
                ) {

                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .border(4.dp, Color.White, CircleShape)
                            .background(Color.LightGray, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = data.name.take(1),
                            fontWeight = FontWeight.Bold,
                            color = teal,
                            fontSize = 22.sp
                        )
                    }

                    if (data.isVerified) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = gold,
                            modifier = Modifier
                                .size(20.dp)
                                .align(Alignment.TopEnd)
                                .background(Color.White, CircleShape)
                        )
                    }
                }
            }

            Spacer(Modifier.height(36.dp))

            /* Body */

            Column(Modifier.padding(horizontal = 16.dp)) {

                Text(
                    text = data.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = data.category,
                    color = teal,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 6.dp)
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = data.location,
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }

                if (data.isSmartMatch) {
                    Surface(
                        color = gold.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.padding(top = 12.dp)
                    ) {
                        Text(
                            text = "Recommended for you",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = teal
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    OutlinedButton(
                        onClick = {},
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, teal)
                    ) {
                        Text("View Profile", color = teal)
                    }

                    Button(
                        onClick = {},
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = teal)
                    ) {
                        Text("Contact")
                    }
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

/* ─────────────────────────────────────────────
   SMART MATCH CALLOUT
   ───────────────────────────────────────────── */

@Composable
private fun SmartMatchProviderHighlight(
    teal: Color,
    gold: Color
) {

    Card(
        modifier = Modifier.padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = teal.copy(0.05f))
    ) {

        Row(
            modifier = Modifier
                .padding(16.dp)
                .drawWithContent {
                    drawContent()
                    drawRect(
                        color = gold,
                        size = size.copy(width = 4.dp.toPx())
                    )
                }
                .padding(start = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(modifier = Modifier.weight(1f)) {

                Text(
                    text = "SmartMatch™",
                    color = gold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )

                Text(
                    text = "Providers suggested based on your activity",
                    fontSize = 13.sp
                )
            }

            TextButton(onClick = {}) {
                Text("View", color = teal, fontWeight = FontWeight.Bold)
            }
        }
    }
}

/* ─────────────────────────────────────────────
   DATA MODEL
   ───────────────────────────────────────────── */

data class ProviderData(
    val name: String,
    val category: String,
    val location: String,
    val isVerified: Boolean,
    val isSmartMatch: Boolean
)
