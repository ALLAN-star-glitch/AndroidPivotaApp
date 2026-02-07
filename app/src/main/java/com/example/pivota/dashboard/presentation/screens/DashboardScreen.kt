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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pivota.R

@Composable
fun DashboardScreen() {
    val primaryTeal = Color(0xFF006565)
    val goldenAccent = Color(0xFFE9C16C)
    val softBackground = Color(0xFFF6FAF9)

    Scaffold(
        containerColor = softBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = padding.calculateBottomPadding() + 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item { PivotaHomeHeroHeader() }
            item { QuickActionsSection(primaryTeal) }
            item { SmartMatchInsightSection(primaryTeal, goldenAccent) }
            item { AnalyticsSection(primaryTeal) }
            item { BusinessManagementSection(primaryTeal) }
            item { RecentActivitySection(primaryTeal) }
        }
    }
}

/* ────────────── HERO HEADER ────────────── */
@Composable
fun PivotaHomeHeroHeader() {
    Box(modifier = Modifier
        .fillMaxWidth()
        .height(200.dp)) {
        Image(
            painter = painterResource(id = R.drawable.nairobi_city),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary.copy(0.95f),
                            MaterialTheme.colorScheme.primary.copy(0.75f)
                        )
                    )
                )
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.End
        ) {
            DashboardHeaderIcon(Icons.Default.Mail)
            Spacer(modifier = Modifier.width(10.dp))
            DashboardHeaderIcon(Icons.Default.Notifications)
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            Text("Good morning 👋", color = Color.White.copy(0.8f), fontSize = 14.sp)
            Text(
                "What do you need today?",
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.5).sp
                )
            )
        }
    }
}

/* ────────────── QUICK ACTIONS ────────────── */
@Composable
fun QuickActionsSection(teal: Color) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Text("Manage Your Work", fontWeight = FontWeight.Bold, color = Color.DarkGray, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(end = 20.dp)
        ) {
            val actions = listOf(
                Pair(Icons.Rounded.BusinessCenter, "Post Job"),
                Pair(Icons.Rounded.HomeWork, "Post House"),
                Pair(Icons.Rounded.Handshake, "Offer Help"),
                Pair(Icons.Rounded.Groups, "List Service")
            )
            items(actions.size) { index ->
                ActionCard(actions[index].first, actions[index].second, teal)
            }
        }
    }
}

@Composable
fun ActionCard(icon: ImageVector, label: String, teal: Color) {
    Surface(
        onClick = { },
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .size(width = 110.dp, height = 100.dp)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, null, tint = teal, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(label, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = teal)
        }
    }
}

/* ────────────── SMARTMATCH INSIGHT ────────────── */
@Composable
fun SmartMatchInsightSection(teal: Color, gold: Color) {
    Card(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = teal.copy(0.04f)),
        border = BorderStroke(1.dp, teal.copy(0.1f))
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(gold.copy(0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.AutoAwesome, null, tint = teal, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("SmartMatch™ Insight", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("3 providers match your recent housing post", fontSize = 13.sp, color = Color.Gray)
            }
            Icon(Icons.Rounded.ChevronRight, null, tint = teal)
        }
    }
}

/* ────────────── ANALYTICS ────────────── */
@Composable
fun AnalyticsSection(teal: Color) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Text("Analytics Overview", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.DarkGray)
        Spacer(modifier = Modifier.height(12.dp))
        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(1.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("📊 Graph / Chart goes here", color = Color.Gray)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard("Active Jobs", "12", teal)
            StatCard("Messages", "8", Color(0xFFE9C16C))
            StatCard("Bookings", "5", Color(0xFF8BC34A))
        }
    }
}

@Composable
fun StatCard(title: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.5.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = color)
            Spacer(modifier = Modifier.height(4.dp))
            Text(title, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

/* ────────────── BUSINESS / ORGANIZATION MANAGEMENT ────────────── */
@Composable
fun BusinessManagementSection(teal: Color) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Spacer(modifier = Modifier.height(12.dp))
        Text("Business / Organization", fontWeight = FontWeight.Bold, color = Color.DarkGray, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(12.dp))
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            shadowElevation = 1.dp
        ) {
            Column {
                BusinessItem(Icons.Default.Dashboard, "My Listings", "Jobs, housing & services you posted", teal)
                Divider(modifier = Modifier.padding(horizontal = 16.dp))
                BusinessItem(Icons.Default.EventAvailable, "Bookings & Requests", "Appointments, requests & responses", teal)
                Divider(modifier = Modifier.padding(horizontal = 16.dp))
                BusinessItem(Icons.AutoMirrored.Filled.Chat, "Messages & Leads", "Conversations with clients & providers", teal)
                Divider(modifier = Modifier.padding(horizontal = 16.dp))
                BusinessItem(Icons.Default.Groups, "Organization & Team", "Manage staff and collaborators", teal, badge = "Admin")
            }
        }
    }
}

@Composable
fun BusinessItem(icon: ImageVector, title: String, subtitle: String, teal: Color, badge: String? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(teal.copy(0.08f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = teal, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                if (badge != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = teal.copy(0.12f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            badge,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = teal,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            }
            Text(subtitle, fontSize = 12.sp, color = Color.Gray)
        }
        Icon(Icons.Rounded.ChevronRight, null, tint = Color.LightGray, modifier = Modifier.size(18.dp))
    }
}

/* ────────────── RECENT ACTIVITY ────────────── */
@Composable
fun RecentActivitySection(teal: Color) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Live Status", fontWeight = FontWeight.Bold, color = Color.DarkGray, fontSize = 16.sp)
            TextButton(onClick = {}) {
                Text("Manage All", color = teal, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        val activities = listOf(
            Triple(Icons.Rounded.AssignmentTurnedIn, "Job Active", "Electrician needed • 12 Applied"),
            Triple(Icons.AutoMirrored.Filled.Chat, "New Message", "Sarah responded to your housing inquiry")
        )
        activities.forEach { activity ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                shadowElevation = 0.5.dp,
                onClick = {}
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(activity.first, null, tint = teal, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(activity.second, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(activity.third, fontSize = 12.sp, color = Color.Gray)
                    }
                    Icon(Icons.Rounded.ChevronRight, null, tint = Color.LightGray, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

/* ────────────── HEADER ICON ────────────── */
@Composable
private fun DashboardHeaderIcon(icon: ImageVector) {
    Box(
        modifier = Modifier
            .size(42.dp)
            .background(Color.White.copy(0.2f), CircleShape)
            .clip(CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, null, tint = Color.White, modifier = Modifier.size(20.dp))
    }
}
