package com.example.pivota.dashboard.presentation.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.window.core.layout.WindowSizeClass
import com.example.pivota.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(onNavigateToListings: () -> Unit) {
    // 🎨 Unified Brand Palette
    val primaryTeal = Color(0xFF006565)
    val goldenAccent = Color(0xFFE9C16C)
    val softBackground = Color(0xFFF8FAF9)
    val slateHeader = Color(0xFF1A1C1E)

    // 📱 1. Orientation & Window Size Logic
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    val isPortrait = configuration.orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT
    val isTabletWidth = windowSizeClass.windowWidthSizeClass != androidx.window.core.layout.WindowWidthSizeClass.COMPACT

    // 🎯 2. Logic: Header is Sticky in Portrait (Mobile/Tablet), Scrolling in Landscape
    val shouldScrollHeader = isTabletWidth && !isPortrait

    Scaffold(
        containerColor = softBackground,
        topBar = {
            // Pinned TopBar for Portrait mode
            if (!shouldScrollHeader) {
                SimpleDashboardTopBar(primaryTeal, goldenAccent)
            }
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = padding.calculateBottomPadding()),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // 🔝 3. Header Placement Logic
            if (shouldScrollHeader) {
                item { SimpleDashboardTopBar(primaryTeal, goldenAccent) }
            } else {
                item { Spacer(modifier = Modifier.height(padding.calculateTopPadding())) }
            }

            // 📊 1. Analytics & Graph Section
            item {
                DashboardSectionWrapper(isTabletWidth) {
                    AnalyticsGraphSection(primaryTeal, slateHeader)
                }
            }

            // ⚡ 2. Quick Actions
            item {
                DashboardSectionWrapper(isTabletWidth) {
                    QuickActionsSection(primaryTeal, slateHeader)
                }
            }

            // 🤖 3. SmartMatch Insight
            item {
                DashboardSectionWrapper(isTabletWidth) {
                    SmartMatchInsightSection(primaryTeal, goldenAccent)
                }
            }

            // 💼 4. Business/Org Management
            item {
                DashboardSectionWrapper(isTabletWidth) {
                    BusinessManagementSection(primaryTeal, slateHeader, onNavigateToListings)
                }
            }

            // 🔔 5. Recent Activity
            item {
                DashboardSectionWrapper(isTabletWidth) {
                    RecentActivitySection(primaryTeal, slateHeader)
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}
@Composable
fun DashboardSectionWrapper(isWide: Boolean, content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = if (isWide) 64.dp else 20.dp)
    ) {
        content()
    }
}

/* ────────────── TOP BAR ────────────── */
@Composable
fun SimpleDashboardTopBar(teal: Color, gold: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        // 1. Background Image
        Image(
            painter = painterResource(id = R.drawable.nairobi_city),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // 2. Teal Overlay (Vertical) - Professional depth
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            teal.copy(alpha = 0.98f),
                            teal.copy(alpha = 0.7f),
                            Color.Transparent
                        )
                    )
                )
        )

        // 3. Golden Accent (Left-side horizontal glow)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(gold.copy(alpha = 0.15f), Color.Transparent),
                        endX = 400f
                    )
                )
        )

        // 4. Content Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column {
                Text(
                    text = "Good morning 👋",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Dashboard Center",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = (-1).sp
                    )
                )
            }

            // Action Icons with Glassmorphism
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                HeaderActionIcon(Icons.Default.Mail)
                HeaderActionIcon(Icons.Default.Notifications)
            }
        }
    }
}

@Composable
private fun HeaderActionIcon(icon: ImageVector) {
    Box(
        modifier = Modifier
            .size(42.dp)
            .background(Color.White.copy(alpha = 0.2f), CircleShape)
            .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
            .clickable { /* Action */ },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )
    }
}
/* ────────────── ANALYTICS GRAPH SECTION ────────────── */
@Composable
fun AnalyticsGraphSection(teal: Color, slate: Color) {
    Column {
        Text("Performance Overview", fontWeight = FontWeight.Bold, color = slate, fontSize = 17.sp)
        Spacer(modifier = Modifier.height(16.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            border = BorderStroke(1.dp, Color.Black.copy(0.04f)),
            shadowElevation = 0.5.dp
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Views & Leads", fontSize = 12.sp, color = Color.Gray)
                        Text("2,480", fontWeight = FontWeight.Black, fontSize = 20.sp, color = slate)
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF0F4F4),
                        modifier = Modifier.clickable { }
                    ) {
                        Text(
                            "This Week",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = teal
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Graph Visualization Placeholder
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .background(teal.copy(0.03f), RoundedCornerShape(16.dp))
                        .border(1.dp, teal.copy(0.08f), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Chart Visualization Container",
                        style = MaterialTheme.typography.labelSmall.copy(color = Color.LightGray)
                    )
                }
            }
        }
    }
}

/* ────────────── QUICK ACTIONS ────────────── */
@Composable
fun QuickActionsSection(teal: Color, slate: Color) {
    Column {
        Text("Quick Actions", fontWeight = FontWeight.Bold, color = slate, fontSize = 17.sp)
        Spacer(modifier = Modifier.height(16.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            val actions = listOf(
                Icons.Rounded.BusinessCenter to "Post Job",
                Icons.Rounded.HomeWork to "Post House",
                Icons.Rounded.Handshake to "Offer Help",
                Icons.Rounded.Groups to "List Service"
            )
            items(actions.size) { index ->
                Surface(
                    onClick = { },
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color.Black.copy(0.04f)),
                    shadowElevation = 0.5.dp
                ) {
                    Column(
                        modifier = Modifier.width(115.dp).padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier.size(40.dp).background(teal.copy(0.06f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(actions[index].first, null, tint = teal, modifier = Modifier.size(22.dp))
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(actions[index].second, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = slate)
                    }
                }
            }
        }
    }
}

/* ────────────── BUSINESS MANAGEMENT ────────────── */
@Composable
fun BusinessManagementSection(teal: Color, slate: Color, onMyListingsClick: () -> Unit) {
    Column {
        Text("Management", fontWeight = FontWeight.Bold, color = slate, fontSize = 17.sp)
        Spacer(modifier = Modifier.height(12.dp))
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            border = BorderStroke(1.dp, Color.Black.copy(0.04f)),
            shadowElevation = 0.5.dp
        ) {
            Column {
                BusinessItem(Icons.Default.Dashboard, "My Listings", "Manage your posts", teal, onClick = onMyListingsClick)
                HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp), thickness = 0.5.dp, color = Color.LightGray.copy(0.2f))
                BusinessItem(Icons.Default.EventAvailable, "Bookings", "Appointments & requests", teal)
                HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp), thickness = 0.5.dp, color = Color.LightGray.copy(0.2f))
                BusinessItem(Icons.AutoMirrored.Filled.Chat, "Messages", "Client conversations", teal)
                HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp), thickness = 0.5.dp, color = Color.LightGray.copy(0.2f))
                BusinessItem(Icons.Default.Groups, "Team", "Collaborators", teal, badge = "Admin")
            }
        }
    }
}

@Composable
fun BusinessItem(icon: ImageVector, title: String, subtitle: String, teal: Color, badge: String? = null, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(44.dp).background(Color(0xFFF0F4F4), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = teal, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF1A1C1E))
                if (badge != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(color = teal.copy(0.1f), shape = RoundedCornerShape(8.dp)) {
                        Text(badge, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = teal, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                    }
                }
            }
            Text(subtitle, fontSize = 13.sp, color = Color.Gray)
        }
        Icon(Icons.Rounded.ChevronRight, null, tint = Color.LightGray, modifier = Modifier.size(20.dp))
    }
}

/* ────────────── SMARTMATCH & RECENT ACTIVITY ────────────── */
@Composable
fun SmartMatchInsightSection(teal: Color, gold: Color) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFFE0F2F1).copy(0.2f),
        border = BorderStroke(1.dp, teal.copy(0.08f))
    ) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.AutoAwesome, null, tint = gold, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                "3 providers match your recent housing post",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, color = Color.DarkGray)
            )
            Icon(Icons.Rounded.ArrowForward, null, tint = teal, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
fun RecentActivitySection(teal: Color, slate: Color) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Live Status", fontWeight = FontWeight.Bold, color = slate, fontSize = 17.sp)
            TextButton(onClick = {}) {
                Text("Manage All", color = teal, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }

        val activities = listOf(
            Triple(Icons.Rounded.AssignmentTurnedIn, "Job Active", "Electrician needed • 12 Applied"),
            Triple(Icons.AutoMirrored.Filled.Chat, "New Message", "Sarah responded to your housing inquiry")
        )

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            activities.forEach { activity ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color.Black.copy(0.04f)),
                    onClick = {}
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(36.dp).background(teal.copy(0.06f), CircleShape), contentAlignment = Alignment.Center) {
                            Icon(activity.first, null, tint = teal, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(activity.second, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = slate)
                            Text(activity.third, fontSize = 12.sp, color = Color.Gray)
                        }
                        Icon(Icons.Rounded.ChevronRight, null, tint = Color.LightGray, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}