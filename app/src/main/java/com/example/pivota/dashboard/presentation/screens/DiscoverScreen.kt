package com.example.pivota.dashboard.presentation.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverScreen() {
    // Brand Palette
    val primaryTeal = MaterialTheme.colorScheme.primary
    val goldenAccent = Color(0xFFE9C16C)
    val softBackground = Color(0xFFF6FAF9)

    // 📱 Adaptive Logic (consistent with Dashboard)
    val windowSizeClass = androidx.compose.material3.adaptive.currentWindowAdaptiveInfo().windowSizeClass
    val isWide = windowSizeClass.windowWidthSizeClass != androidx.window.core.layout.WindowWidthSizeClass.COMPACT

    Scaffold(
        containerColor = softBackground,
        // Sticky Header only on mobile
        topBar = {
            if (!isWide) {
                DiscoverHeroHeader(primaryTeal, goldenAccent)
            }
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
        ) {
            // 🔝 Header Placement: Scrolling for Tablet, Spacer for Mobile (to account for topBar)
            if (isWide) {
                item { DiscoverHeroHeader(primaryTeal, goldenAccent) }
            } else {
                item { Spacer(modifier = Modifier.height(paddingValues.calculateTopPadding())) }
            }

            // --- ALL DISCOVER CONTENT ---
            item { UpgradePremiumBanner(primaryTeal, goldenAccent) }
            item { SmartSearchAndFilter(primaryTeal) }
            item { SmartMatchHighlightCard(primaryTeal, goldenAccent) }

            // 📋 4.1 Jobs Near You
            item { SectionHeader("Jobs Near You") }
            item {
                HorizontalListingRow {
                    JobCard(primaryTeal, "Construction Foreman", "Upper Hill", "KSh 3,500/day", "Casual")
                    JobCard(primaryTeal, "Junior Accountant", "Westlands", "KSh 55,000", "Formal")
                    JobCard(primaryTeal, "Store Keeper", "Mombasa Rd", "KSh 25,000", "Formal")
                }
            }

            // 📋 4.2 Housing Opportunities
            item { SectionHeader("Housing Opportunities") }
            item {
                HorizontalListingRow {
                    HousingCard(primaryTeal, "KSh 22,000", "Bedsitter Units", "Ruiru")
                    HousingCard(primaryTeal, "KSh 45,000", "2 Bedroom Apt", "Syokimau")
                    HousingCard(
                        teal = primaryTeal,
                        price = "KSh 22,000",
                        type = "Modern Bedsitter",
                        loc = "Ruiru, Bypass",
                        status = "For Rent",
                        amenities = listOf("Wifi", "CCTV", "Water")
                    )
                }
            }

            // 🛠️ 4.3 Verified Service Providers
            item { SectionHeader("Service Providers") }
            item {
                HorizontalListingRow {
                    ServiceProviderCard(primaryTeal, goldenAccent, "QuickMovers Kenya", "Housing Support", "Professional Moving", 4.9f, true)
                    ServiceProviderCard(primaryTeal, goldenAccent, "Fundi Digital", "Maintenance", "Electrical & Plumbing", 4.7f, false)
                }
            }

            // ⚡ 4.4 Quick Service Grid
            item { SectionHeader("Common Services") }
            item { ServiceGrid(primaryTeal, goldenAccent) }

            // 📋 Social Support
            item { SectionHeader("Social Support & Services") }
            items(2) { index ->
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                    SupportCard(
                        primaryTeal,
                        name = if(index == 0) "Red Cross Support" else "Legal Aid Kenya",
                        service = if(index == 0) "Emergency Relief" else "Family Law Advice",
                        loc = "Regional Office"
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(100.dp)) }
        }
    }
}

@Composable
fun DiscoverHeroHeader(teal: Color, gold: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
    ) {
        // 1.  Background Image - Absolute Edge-to-Edge
        Image(
            painter = painterResource(id = com.example.pivota.R.drawable.nairobi_city),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // 2.  Teal Overlay (Vertical)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(teal.copy(0.98f), teal.copy(0.7f), Color.Transparent)
                    )
                )
        )

        // 3.  Golden Accent (Left-side horizontal glow)
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

        // 4.  Content (Avatar, Greeting, Icons, and Discover + Badge)
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
                // Avatar with Golden status dot
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

                // Action Icons (Mail & Notifications restored)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    HeaderActionIcon(Icons.Default.Mail) { /* Open Messages */ }
                    HeaderActionIcon(Icons.Default.Notifications) { /* Open Notifications */ }
                }
            }

            // --- BOTTOM ROW ---
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Discover",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-1.5).sp
                        )
                    )
                    Spacer(Modifier.width(8.dp))

                    // 🤖 SmartMatch™ Badge
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
                    text = "Life opportunities, tailored for you",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White.copy(0.9f)
                    )
                )
            }
        }
    }
}

@Composable
fun HeaderAvatar(teal: Color) {
    Box(
        modifier = Modifier
            .size(45.dp)
            .background(Color.White.copy(0.2f), CircleShape)
            .border(1.5.dp, Color.White.copy(0.5f), CircleShape)
            .clip(CircleShape),
        contentAlignment = Alignment.Center
    ) {
        // Placeholder Icon for unlogged user
        Icon(
            imageVector = Icons.Default.PersonOutline,
            contentDescription = "User Avatar",
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun HeaderActionIcon(icon: ImageVector, onClick: () -> Unit) {
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
fun SmartSearchAndFilter(teal: Color) {
    val gold = Color(0xFFE9C16C)

    Column(modifier = Modifier.padding(top = 20.dp, bottom = 10.dp)) {
        // Search Bar
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(28.dp),
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            TextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("Search Jobs, Houses, Services...", fontSize = 14.sp, color = Color.Gray) },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = teal) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Filter Chips - Updated for MVP1 Pillars
        Row(
            modifier = Modifier
                .padding(top = 16.dp)
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Updated List based on MVP1 Concept Note (Jobs, Houses, Contractors, Help & Support)
            val filters = listOf("All", "Jobs", "Housing", "Service Providers", "Help & Support", "Verified")

            filters.forEach { filter ->
                val isActive = filter == "All"
                val isVerified = filter == "Verified"

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    // "Verified" gets a subtle golden border/glow to match the trust objective
                    color = when {
                        isActive -> teal
                        isVerified -> gold.copy(alpha = 0.15f)
                        else -> Color(0xFFE5ECEA).copy(0.5f)
                    },
                    border = if (isVerified) BorderStroke(1.dp, gold) else null,
                    modifier = Modifier.clickable { /* Handle filter */ }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        if (isVerified) {
                            Icon(
                                Icons.Default.Verified,
                                contentDescription = null,
                                tint = gold,
                                modifier = Modifier.size(14.dp).padding(end = 4.dp)
                            )
                        }
                        Text(
                            text = filter,
                            color = when {
                                isActive -> Color.White
                                isVerified -> teal // Contrast for the gold background
                                else -> Color.DarkGray
                            },
                            fontSize = 13.sp,
                            fontWeight = if (isActive || isVerified) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
@Composable
fun SmartMatchHighlightCard(teal: Color, gold: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = teal.copy(alpha = 0.05f)),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, teal.copy(0.1f))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .drawWithContent {
                    drawContent()
                    // Left accent line in golden yellow
                    drawRect(color = gold, size = size.copy(width = 4.dp.toPx()))
                }
                .padding(start = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Surface(
                    color = gold.copy(0.2f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        "SmartMatch™ Picks",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = teal
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "SmartMatch™ recommends 3 providers to make these opportunities actionable",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray
                )
            }
            OutlinedButton(
                onClick = {},
                border = BorderStroke(1.dp, teal),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 12.dp)
            ) {
                Text("View Matches", fontSize = 12.sp, color = teal)
            }
        }
    }
}

@Composable
fun JobCard(teal: Color, title: String, loc: String, pay: String, type: String) {
    Card(
        modifier = Modifier.width(220.dp),
        colors = CardDefaults.cardColors(Color.White),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFFE5ECEA))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = type.uppercase(),
                color = if(type == "Casual") Color(0xFFE9C16C) else teal,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(loc, color = Color.Gray, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(pay, color = teal, fontWeight = FontWeight.ExtraBold, modifier = Modifier.weight(1f))
                Surface(color = teal, shape = RoundedCornerShape(8.dp), modifier = Modifier.clickable {  }) {
                    Text("Apply", color = Color.White, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun HousingCard(
    teal: Color,
    price: String,
    type: String,
    loc: String,
    imageRes: Int = com.example.pivota.R.drawable.nairobi_city,
    status: String = "For Rent",
    rating: Float = 4.5f,
    amenities: List<String> = listOf("Water", "Security")
) {
    val gold = Color(0xFFE9C16C) // Your brand gold

    Card(
        modifier = Modifier
            .width(280.dp)
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(Color.White),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(160.dp)) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // 🏷️ Status Badge with Golden/Teal Touch
                Surface(
                    color = if (status.contains("Sale")) gold else teal,
                    shape = RoundedCornerShape(topStart = 0.dp, bottomEnd = 12.dp, topEnd = 0.dp, bottomStart = 0.dp),
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Text(
                        text = status.uppercase(),
                        color = if (status.contains("Sale")) teal else Color.White,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                // 🤖 SmartMatch Verified Icon (Gold Border)
                Surface(
                    color = Color.White,
                    shape = CircleShape,
                    border = BorderStroke(2.dp, gold),
                    modifier = Modifier.padding(12.dp).size(34.dp).align(Alignment.TopEnd)
                ) {
                    Icon(
                        Icons.Default.Verified,
                        contentDescription = "Verified",
                        tint = gold,
                        modifier = Modifier.padding(6.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = price,
                        color = teal,
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(Icons.Default.Star, null, tint = gold, modifier = Modifier.size(16.dp))
                    Text(text = rating.toString(), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                Text(text = type, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1A1C1E))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                    Text(text = loc, color = Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(start = 2.dp))
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    amenities.forEach { amenity ->
                        Surface(
                            color = gold.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(0.5.dp, gold.copy(0.3f))
                        ) {
                            Text(
                                text = amenity,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontSize = 10.sp,
                                color = teal,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { /* Navigate to details */ },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = teal),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Visibility, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("View Details", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.DarkGray))
        Text("See all", color = Color(0xFF006565), fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun SupportCard(teal: Color, name: String, service: String, loc: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFFE5ECEA)) // Divider Grey
    ) {
        ListItem(
            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
            headlineContent = {
                Text(name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            },
            supportingContent = {
                Column {
                    Text(service, fontSize = 13.sp, color = Color.Gray)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(loc, fontSize = 12.sp, color = Color.Gray)
                    }
                }
            },
            leadingContent = {
                // Provider Avatar/Logo Placeholder
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(teal.copy(0.08f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Groups, contentDescription = null, tint = teal)
                }
            },
            trailingContent = {
                Button(
                    onClick = { /* Request Support */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    border = BorderStroke(1.dp, teal.copy(0.3f)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text("Request", color = teal, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
fun ServiceProviderCard(
    teal: Color,
    gold: Color,
    name: String,
    specialty: String,
    service: String,
    rating: Float,
    isCompany: Boolean
) {
    Card(
        modifier = Modifier.width(260.dp),
        colors = CardDefaults.cardColors(Color.White),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, Color(0xFFE5ECEA))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Provider Avatar
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(teal.copy(0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isCompany) Icons.Default.Business else Icons.Default.Engineering,
                        contentDescription = null,
                        tint = teal
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(name, fontWeight = FontWeight.Bold, fontSize = 15.sp, maxLines = 1)
                    Surface(color = gold.copy(0.2f), shape = RoundedCornerShape(4.dp)) {
                        Text(
                            text = specialty.uppercase(),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = teal
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
            Text(service, fontSize = 13.sp, color = Color.DarkGray, minLines = 2)

            Divider(Modifier.padding(vertical = 8.dp), thickness = 0.5.dp, color = Color.LightGray)

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, null, tint = gold, modifier = Modifier.size(14.dp))
                Text(rating.toString(), fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 4.dp))
                Spacer(Modifier.weight(1f))
                TextButton(onClick = { }) {
                    Text("Hire Now", color = teal, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Icon(Icons.Default.ArrowForwardIos, null, modifier = Modifier.size(10.dp), tint = teal)
                }
            }
        }
    }
}

@Composable
fun ServiceGrid(teal: Color, gold: Color) {
    val services = listOf(
        "Movers" to Icons.Default.LocalShipping,
        "Plumbers" to Icons.Default.WaterDrop,
        "Trainers" to Icons.Default.School,
        "Cleaners" to Icons.Default.CleaningServices,
        "Electricians" to Icons.Default.Bolt,
        "Counselors" to Icons.Default.Psychology
    )

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        services.chunked(3).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowItems.forEach { item ->
                    Surface(
                        modifier = Modifier.weight(1f).clickable { },
                        color = Color.White,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFFE5ECEA))
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(item.second, null, tint = teal, modifier = Modifier.size(24.dp))
                            Spacer(Modifier.height(4.dp))
                            Text(item.first, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UpgradePremiumBanner(teal: Color, gold: Color) {
    // We use a very soft teal or even a white-smoke background to reduce the "shout"
    val subtleTeal = Color(0xFFF0F4F4)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        shape = RoundedCornerShape(24.dp),
        color = Color.White, // Clean base
        border = BorderStroke(1.dp, teal.copy(alpha = 0.1f)), // Breathable border
        shadowElevation = 2.dp
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.horizontalGradient(
                        listOf(Color.White, subtleTeal)
                    )
                )
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.WorkspacePremium,
                            contentDescription = null,
                            tint = gold,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "PIVOTA PREMIUM",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp,
                                color = teal
                            )
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Get priority access to verified listings",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }

                Button(
                    onClick = { /* Upgrade logic */ },
                    colors = ButtonDefaults.buttonColors(containerColor = teal),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        "Upgrade",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
@Composable
fun HorizontalListingRow(content: @Composable () -> Unit) {
    Row(
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        content()
    }
}