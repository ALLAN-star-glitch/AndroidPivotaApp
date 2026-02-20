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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pivota.R

/* ─────────────────────────────────────────────
   Providers Screen
   Purpose: Discover & connect with verified service providers
   Tone: Trustworthy, calm, professional
   ───────────────────────────────────────────── */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProvidersScreen() {
    val primaryTeal = Color(0xFF006565)
    val goldenAccent = Color(0xFFE9C16C)
    val softBackground = Color(0xFFF6FAF9)

    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val windowSizeClass = androidx.compose.material3.adaptive.currentWindowAdaptiveInfo().windowSizeClass

    val isPortrait = configuration.orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT
    val isTabletWidth = windowSizeClass.windowWidthSizeClass != androidx.window.core.layout.WindowWidthSizeClass.COMPACT
    val shouldScrollHeader = isTabletWidth && !isPortrait

    Scaffold(
        containerColor = softBackground,
        topBar = {
            if (!shouldScrollHeader) {
                PivotaProvidersHeroHeader(primaryTeal, goldenAccent)
            }
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 340.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = padding.calculateBottomPadding()),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Adaptive Header Placement
            if (shouldScrollHeader) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    PivotaProvidersHeroHeader(primaryTeal, goldenAccent)
                }
            } else {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Spacer(modifier = Modifier.height(padding.calculateTopPadding()))
                }
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                ProvidersSearchSection(primaryTeal)
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                SmartMatchProviderHighlight(primaryTeal, goldenAccent)
            }

            // Providers Data...
            val providers = listOf(
                ProviderData("Musa Jallow", "Electrician • Solar Specialist", "Nairobi", true, true),
                ProviderData("Sarah Wanjiku", "Interior Designer", "Kiambu", true, false),
                ProviderData("Pivota Housing Ltd", "Property & Facility Management", "Mombasa", true, true),
                ProviderData("Legal Aid Kenya", "Help & Support • NGO Partner", "Nakuru", true, false)
            )

            items(providers) { provider ->
                ProviderCard(teal = primaryTeal, gold = goldenAccent, data = provider)
            }

            item(span = { GridItemSpan(maxLineSpan) }) { Spacer(Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun PivotaProvidersHeroHeader(teal: Color, gold: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp) // Matched to DiscoverScreen height
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
            // --- TOP ROW ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box {
                    HeaderAvatar(teal)
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
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "Welcome to Pivota",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White.copy(0.8f)
                        )
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    HeaderActionIcon(Icons.Default.Mail) { /* Open Messages */ }
                    HeaderActionIcon(Icons.Default.Notifications) { /* Open Notifications */ }
                }
            }

            // --- BOTTOM ROW ---
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Providers",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-1.5).sp
                        )
                    )
                    Spacer(Modifier.width(8.dp))

                    Surface(
                        color = gold,
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.graphicsLayer { translationY = 4f }
                    ) {
                        Text(
                            text = "SmartMatch™",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = teal,
                                fontSize = 9.sp
                            )
                        )
                    }
                }
                Text(
                    text = "Professional partners you can trust",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White.copy(0.9f)
                    )
                )
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
