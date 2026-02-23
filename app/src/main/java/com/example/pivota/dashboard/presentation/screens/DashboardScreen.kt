package com.example.pivota.dashboard.presentation.screens

import android.annotation.SuppressLint
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import androidx.window.core.layout.WindowSizeClass
import com.example.pivota.R
import com.example.pivota.ui.theme.*

// Additional color definitions
val successLight = Color(0xFF2E7D32)      // Success green
val errorLight = Color(0xFFD32F2F)        // Error red
val warningLight = Color(0xFFED6C02)      // Warning orange
val infoLight = Color(0xFF0288D1)         // Info blue

// Data classes
data class KPI(
    val title: String,
    val value: String,
    val trend: String,
    val icon: ImageVector
)

data class QuickAction(
    val icon: ImageVector,
    val title: String,
    val subtitle: String,
    val color: Color
)

data class Activity(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val color: Color
)

@SuppressLint("FrequentlyChangingValue")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(onNavigateToListings: () -> Unit) {
    // 🎨 Brand Palette using theme colors
    val primaryColor = primaryLight
    val accentColor = tertiaryLight
    val backgroundColor = backgroundLight
    val surfaceColor = surfaceLight
    val textPrimary = onSurfaceLight
    val textSecondary = onSurfaceVariantLight
    val borderColor = outlineVariantLight

    // 📱 Orientation & Window Size Logic
    val configuration = LocalConfiguration.current
    val windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    val isPortrait = configuration.orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT
    val isTabletWidth = windowSizeClass.windowWidthSizeClass != androidx.window.core.layout.WindowWidthSizeClass.COMPACT

    // Header is ALWAYS sticky
    val listState = rememberLazyListState()

    // 📏 Header sizes
    val maxHeight = 240.dp
    val minHeight = 100.dp

    val density = LocalDensity.current
    val collapseRangePx = with(density) {
        (maxHeight - minHeight).toPx()
    }

    // 🔥 Collapse logic
    val scrollY = when (listState.firstVisibleItemIndex) {
        0 -> listState.firstVisibleItemScrollOffset.toFloat()
        else -> collapseRangePx
    }

    val collapseFraction =
        (scrollY / collapseRangePx).coerceIn(0f, 1f)

    val animatedHeight =
        lerp(maxHeight, minHeight, collapseFraction)

    // Mock data for KPIs
    val kpiData = listOf(
        KPI("Active Listings", "24", "+12%", Icons.Outlined.List),
        KPI("Total Views", "3,842", "+28%", Icons.Outlined.Visibility),
        KPI("SmartMatches", "156", "+43%", Icons.Outlined.AutoAwesome),
        KPI("Revenue", "KES 45.2K", "+18%", Icons.Outlined.TrendingUp)
    )

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            DashboardHeroHeader(
                primaryColor = primaryColor,
                accentColor = accentColor,
                height = animatedHeight,
                collapseFraction = collapseFraction,
                onSurfaceColor = onSurfaceLight
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = padding.calculateBottomPadding())
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    top = maxHeight + 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                // 📊 1. KPI Cards Section
                item {
                    DashboardSectionWrapper(isTabletWidth) {
                        KpiCardsSection(kpiData, primaryColor, textPrimary, textSecondary)
                    }
                }

                // 📈 2. Analytics & Graph Section
                item {
                    DashboardSectionWrapper(isTabletWidth) {
                        AnalyticsGraphSection(
                            primaryColor = primaryColor,
                            accentColor = accentColor,
                            textPrimary = textPrimary,
                            textSecondary = textSecondary,
                            borderColor = borderColor
                        )
                    }
                }

                // ⚡ 3. Quick Actions
                item {
                    DashboardSectionWrapper(isTabletWidth) {
                        QuickActionsSection(
                            primaryColor = primaryColor,
                            textPrimary = textPrimary,
                            textSecondary = textSecondary
                        )
                    }
                }

                // 🤖 4. SmartMatch Insights
                item {
                    DashboardSectionWrapper(isTabletWidth) {
                        SmartMatchInsightSection(
                            primaryColor = primaryColor,
                            accentColor = accentColor,
                            textPrimary = textPrimary,
                            textSecondary = textSecondary
                        )
                    }
                }

                // 💼 5. Business Management
                item {
                    DashboardSectionWrapper(isTabletWidth) {
                        BusinessManagementSection(
                            primaryColor = primaryColor,
                            textPrimary = textPrimary,
                            textSecondary = textSecondary,
                            borderColor = borderColor,
                            onMyListingsClick = onNavigateToListings
                        )
                    }
                }

                // 🔔 6. Recent Activity
                item {
                    DashboardSectionWrapper(isTabletWidth) {
                        RecentActivitySection(
                            primaryColor = primaryColor,
                            accentColor = accentColor,
                            textPrimary = textPrimary,
                            textSecondary = textSecondary,
                            borderColor = borderColor
                        )
                    }
                }

                // 🏦 7. Wallet & Trust Summary
                item {
                    DashboardSectionWrapper(isTabletWidth) {
                        WalletTrustSection(
                            primaryColor = primaryColor,
                            textPrimary = textPrimary,
                            textSecondary = textSecondary,
                            borderColor = borderColor
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
fun DashboardSectionWrapper(isWide: Boolean, content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = if (isWide) 48.dp else 20.dp)
    ) {
        content()
    }
}

/* ────────────── KPI CARDS SECTION (REDESIGNED) ────────────── */
@Composable
fun KpiCardsSection(
    kpis: List<KPI>,
    primaryColor: Color,
    textPrimary: Color,
    textSecondary: Color
) {
    Column {
        SectionHeader("Performance Overview", "Last 30 days", textPrimary, textSecondary)
        Spacer(modifier = Modifier.height(16.dp))

        // Using rows instead of LazyVerticalGrid to avoid nesting scrollables
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // First row (first 2 KPIs)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                kpis.take(2).forEach { kpi ->
                    Box(modifier = Modifier.weight(1f)) {
                        CompactKpiCard(kpi, primaryColor, textPrimary, textSecondary)
                    }
                }
            }

            // Second row (remaining KPIs)
            if (kpis.size > 2) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    kpis.drop(2).forEach { kpi ->
                        Box(modifier = Modifier.weight(1f)) {
                            CompactKpiCard(kpi, primaryColor, textPrimary, textSecondary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CompactKpiCard(
    kpi: KPI,
    primaryColor: Color,
    textPrimary: Color,
    textSecondary: Color
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = surfaceContainerLowLight),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Smaller icon
            Surface(
                shape = CircleShape,
                color = primaryColor.copy(0.1f),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        kpi.icon,
                        contentDescription = null,
                        tint = primaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Text content with better proportions
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = kpi.title,
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = textSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = kpi.value,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = textPrimary,
                        fontSize = 16.sp
                    ),
                    maxLines = 1
                )
            }

            // Trend indicator - more compact
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (kpi.trend.startsWith("+")) successLight.copy(0.1f) else errorLight.copy(0.1f)
            ) {
                Text(
                    text = kpi.trend,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = if (kpi.trend.startsWith("+")) successLight else errorLight,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                )
            }
        }
    }
}

/* ────────────── ANALYTICS GRAPH SECTION ────────────── */
@Composable
fun AnalyticsGraphSection(
    primaryColor: Color,
    accentColor: Color,
    textPrimary: Color,
    textSecondary: Color,
    borderColor: Color
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = surfaceContainerLowLight),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Views & Engagement",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = textPrimary
                        )
                    )
                    Text(
                        "Track your listing performance",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = textSecondary
                        )
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = primaryColor.copy(0.08f),
                    modifier = Modifier.clickable { }
                ) {
                    Text(
                        "This Week",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = primaryColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem("Views", "1,284", "+18%", textPrimary, textSecondary)
                StatItem("Leads", "342", "+24%", textPrimary, textSecondary)
                StatItem("CTR", "4.2%", "-2%", textPrimary, textSecondary)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(primaryColor.copy(0.05f), accentColor.copy(0.05f)),
                            start = Offset(0f, 0f),
                            end = Offset(1000f, 0f)
                        ),
                        RoundedCornerShape(16.dp)
                    )
                    .border(1.dp, borderColor, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    ChartBar(40, primaryColor)
                    ChartBar(65, primaryColor)
                    ChartBar(45, primaryColor)
                    ChartBar(80, primaryColor)
                    ChartBar(55, primaryColor)
                    ChartBar(70, primaryColor)
                    ChartBar(90, primaryColor)
                }
            }
        }
    }
}

@Composable
fun StatItem(
    label: String,
    value: String,
    change: String,
    textPrimary: Color,
    textSecondary: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = textPrimary,
                fontSize = 16.sp
            )
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                color = textSecondary,
                fontSize = 11.sp
            )
        )
        Text(
            text = change,
            style = MaterialTheme.typography.labelSmall.copy(
                color = if (change.startsWith("+")) successLight else errorLight,
                fontWeight = FontWeight.SemiBold,
                fontSize = 10.sp
            )
        )
    }
}

@Composable
fun ChartBar(heightPercent: Int, color: Color) {
    Box(
        modifier = Modifier
            .width(20.dp)
            .height(heightPercent.dp)
            .background(
                Brush.verticalGradient(
                    colors = listOf(color, color.copy(0.5f))
                ),
                RoundedCornerShape(4.dp)
            )
    )
}

/* ────────────── QUICK ACTIONS ────────────── */
@Composable
fun QuickActionsSection(
    primaryColor: Color,
    textPrimary: Color,
    textSecondary: Color
) {
    Column {
        SectionHeader("Quick Actions", "Post new", textPrimary, textSecondary)
        Spacer(modifier = Modifier.height(16.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            val actions = listOf(
                QuickAction(Icons.Rounded.BusinessCenter, "Post Job", "Employment", primaryColor),
                QuickAction(Icons.Rounded.HomeWork, "Post House", "Housing", primaryColor),
                QuickAction(Icons.Rounded.VolunteerActivism, "Offer Help", "Support", primaryColor),
                QuickAction(Icons.Rounded.Handshake, "List Service", "Contractor", primaryColor)
            )
            items(actions.size) { index ->
                QuickActionCard(actions[index], textPrimary, textSecondary)
            }
        }
    }
}

@Composable
fun QuickActionCard(
    action: QuickAction,
    textPrimary: Color,
    textSecondary: Color
) {
    Card(
        onClick = { /* Navigate to post form */ },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = surfaceContainerLowLight),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.width(140.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = CircleShape,
                color = action.color.copy(0.1f),
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        action.icon,
                        contentDescription = null,
                        tint = action.color,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = action.title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = textPrimary
                ),
                maxLines = 1
            )

            Text(
                text = action.subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = textSecondary
                ),
                fontSize = 11.sp
            )
        }
    }
}

/* ────────────── SMARTMATCH INSIGHTS ────────────── */
@Composable
fun SmartMatchInsightSection(
    primaryColor: Color,
    accentColor: Color,
    textPrimary: Color,
    textSecondary: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = accentColor.copy(0.08f)),
        border = BorderStroke(1.dp, accentColor.copy(0.3f))
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = accentColor.copy(0.15f),
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Outlined.AutoAwesome,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "AI SmartMatch™ Active",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = textPrimary
                    )
                )
                Text(
                    text = "3 providers match your recent housing post",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = textSecondary
                    )
                )
            }

            Button(
                onClick = { /* Navigate to SmartMatch */ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = accentColor,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("View Matches", fontWeight = FontWeight.Bold)
            }
        }
    }
}

/* ────────────── BUSINESS MANAGEMENT ────────────── */
@Composable
fun BusinessManagementSection(
    primaryColor: Color,
    textPrimary: Color,
    textSecondary: Color,
    borderColor: Color,
    onMyListingsClick: () -> Unit
) {
    Column {
        SectionHeader("Business Management", "3 active listings", textPrimary, textSecondary)
        Spacer(modifier = Modifier.height(12.dp))

        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = surfaceContainerLowLight),
            elevation = CardDefaults.cardElevation(2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                BusinessItem(
                    icon = Icons.Outlined.List,
                    title = "My Listings",
                    subtitle = "Manage your jobs, houses & services",
                    value = "3 active",
                    color = primaryColor,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary,
                    onClick = onMyListingsClick
                )

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    color = borderColor
                )

                BusinessItem(
                    icon = Icons.Outlined.EventAvailable,
                    title = "Bookings",
                    subtitle = "Appointments & service requests",
                    value = "12 pending",
                    color = primaryColor,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary
                )

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    color = borderColor
                )

                BusinessItem(
                    icon = Icons.Outlined.Chat,
                    title = "Messages",
                    subtitle = "Client conversations",
                    value = "8 unread",
                    color = primaryColor,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary
                )

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    color = borderColor
                )

                BusinessItem(
                    icon = Icons.Outlined.AccountBalance,
                    title = "Wallet & Escrow",
                    subtitle = "KES 12,450 available",
                    value = "2 escrow",
                    color = primaryColor,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary
                )
            }
        }
    }
}

@Composable
fun BusinessItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    value: String,
    color: Color,
    textPrimary: Color,
    textSecondary: Color,
    badge: String? = null,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            color = color.copy(0.08f),
            modifier = Modifier.size(44.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = textPrimary
                    )
                )
                if (badge != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = color.copy(0.1f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = badge,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = color
                        )
                    }
                }
            }
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = textSecondary
                )
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = value,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Icon(
            Icons.Rounded.ChevronRight,
            contentDescription = null,
            tint = textSecondary,
            modifier = Modifier.size(20.dp)
        )
    }
}

/* ────────────── WALLET & TRUST SECTION ────────────── */
@Composable
fun WalletTrustSection(
    primaryColor: Color,
    textPrimary: Color,
    textSecondary: Color,
    borderColor: Color
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = surfaceContainerLowLight),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "Wallet Balance",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = textSecondary
                    )
                )
                Text(
                    "KES 12,450",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = textPrimary
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.Verified,
                        contentDescription = null,
                        tint = successLight,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "Trust Score: 98",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = successLight,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Button(
                    onClick = { /* Deposit */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryColor
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.width(100.dp)
                ) {
                    Text("Deposit", fontSize = 12.sp, color = onPrimaryLight)
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { /* Withdraw */ },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = primaryColor
                    ),
                    border = BorderStroke(1.dp, primaryColor),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.width(100.dp)
                ) {
                    Text("Withdraw", fontSize = 12.sp)
                }
            }
        }
    }
}

/* ────────────── RECENT ACTIVITY ────────────── */
@Composable
fun RecentActivitySection(
    primaryColor: Color,
    accentColor: Color,
    textPrimary: Color,
    textSecondary: Color,
    borderColor: Color
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SectionHeader("Recent Activity", "View all", textPrimary, textSecondary)
        }

        Spacer(modifier = Modifier.height(12.dp))

        val activities = listOf(
            Activity("New application", "Electrician needed • 12 Applied", Icons.Outlined.Description, primaryColor),
            Activity("Message received", "Sarah responded to your inquiry", Icons.Outlined.Chat, accentColor),
            Activity("Booking confirmed", "Moving service • Tomorrow 10AM", Icons.Outlined.Event, primaryColor),
            Activity("Payment received", "KES 3,500 • Job completed", Icons.Outlined.Payment, successLight)
        )

        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = surfaceContainerLowLight),
            elevation = CardDefaults.cardElevation(2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                activities.forEachIndexed { index, activity ->
                    ActivityItem(activity, textPrimary, textSecondary)
                    if (index < activities.size - 1) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 20.dp),
                            color = borderColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ActivityItem(
    activity: Activity,
    textPrimary: Color,
    textSecondary: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* View details */ }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            color = activity.color.copy(0.1f),
            modifier = Modifier.size(40.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    activity.icon,
                    contentDescription = null,
                    tint = activity.color,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = activity.title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = textPrimary
                )
            )
            Text(
                text = activity.subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = textSecondary
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Text(
            text = "2h ago",
            style = MaterialTheme.typography.labelSmall.copy(
                color = textSecondary
            )
        )
    }
}

@Composable
fun SectionHeader(
    title: String,
    subtitle: String,
    textPrimary: Color,
    textSecondary: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = textPrimary
            )
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall.copy(
                color = textSecondary
            ),
            modifier = Modifier.clickable { /* Navigate to full view */ }
        )
    }
}

/* ────────────── COLLAPSIBLE HEADER ────────────── */
/* ────────────── COLLAPSIBLE HEADER ────────────── */
@Composable
fun DashboardHeroHeader(
    primaryColor: Color,
    accentColor: Color,
    height: Dp,
    collapseFraction: Float,
    onSurfaceColor: Color,
    modifier: Modifier = Modifier
) {
    val collapsed = collapseFraction > 0.85f

    val maxFontSize = 34.sp
    val minFontSize = 24.sp

    // Animate background color based on collapse state - using the same teal color
    val backgroundColor by animateColorAsState(
        targetValue = if (collapsed) primaryColor.copy(alpha = 0.95f) else Color.Transparent,
        animationSpec = tween(durationMillis = 300)
    )

    // Animate font size based on collapseFraction
    val titleFontSize = ((maxFontSize.value - collapseFraction * (maxFontSize.value - minFontSize.value))).sp

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(height),
        shadowElevation = if (collapsed) 8.dp else 0.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Background Image - fades out when collapsed
            AnimatedVisibility(
                visible = !collapsed,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.dashbaordd),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Solid color background for collapsed state - using primaryColor (teal)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor)
            )

            // Gradient overlay - only visible when not collapsed - using teal gradient
            AnimatedVisibility(
                visible = !collapsed,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    primaryColor.copy(0.95f),
                                    primaryColor.copy(0.75f),
                                    Color.Transparent
                                )
                            )
                        )
                )
            }

            // Golden Accent - only visible when not collapsed - using accentColor (gold)
            AnimatedVisibility(
                visible = !collapsed,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(accentColor.copy(0.15f), Color.Transparent),
                                endX = 400f
                            )
                        )
                )
            }

            // Main content container
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
            ) {
                // TOP ROW - Only visible when NOT collapsed
                AnimatedVisibility(
                    visible = !collapsed,
                    enter = fadeIn() + slideInVertically(),
                    exit = fadeOut() + slideOutVertically()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, start = 20.dp, end = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 👤 AVATAR SECTION
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box {
                                HeaderAvatarDashboard(primaryColor)
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .background(accentColor, CircleShape)
                                        .border(2.dp, primaryColor, CircleShape)
                                        .align(Alignment.BottomEnd)
                                )
                            }

                            Spacer(Modifier.width(12.dp))

                            Column {
                                Text(
                                    "Hi, Guest",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "Welcome to Pivota",
                                    color = Color.White.copy(0.85f),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }

                        // 🔔 ICON ROW
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            HeaderActionIconDashboard(
                                icon = Icons.Outlined.Mail,
                                iconTint = Color.White,
                                backgroundTint = Color.White.copy(alpha = 0.2f)
                            ) {}
                            HeaderActionIconDashboard(
                                icon = Icons.Outlined.Notifications,
                                iconTint = Color.White,
                                backgroundTint = Color.White.copy(alpha = 0.2f)
                            ) {}
                        }
                    }
                }

                // Spacer to push content down when not collapsed
                if (!collapsed) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }

            // Main content area - Changes based on collapse state
            if (collapsed) {
                // COLLAPSED STATE - All elements in one horizontal row, lowered
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterStart)
                        .padding(start = 20.dp, end = 20.dp)
                        .offset(y = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Left side - Dashboard text
                    Text(
                        text = "Dashboard",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-1.5).sp,
                            fontSize = titleFontSize
                        )
                    )

                    // Right side - Icons
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        HeaderActionIconDashboard(
                            icon = Icons.Outlined.Mail,
                            iconTint = Color.White,
                            backgroundTint = Color.White.copy(alpha = 0.2f)
                        ) {}
                        HeaderActionIconDashboard(
                            icon = Icons.Outlined.Notifications,
                            iconTint = Color.White,
                            backgroundTint = Color.White.copy(alpha = 0.2f)
                        ) {}
                    }
                }
            } else {
                // EXPANDED STATE
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 20.dp, bottom = 32.dp)
                        .statusBarsPadding()
                ) {
                    // Dashboard text
                    Text(
                        text = "Dashboard",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-1.5).sp,
                            fontSize = titleFontSize
                        )
                    )

                    // 📝 Subtitle Text
                    Text(
                        text = "Manage your business at a glance",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White.copy(0.9f)
                        ),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun HeaderActionIconDashboard(
    icon: ImageVector,
    iconTint: Color = onSurfaceLight,
    backgroundTint: Color = onSurfaceLight.copy(alpha = 0.2f),
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(44.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(backgroundTint, CircleShape)
                .clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun HeaderAvatarDashboard(primaryColor: Color) {
    Box(
        modifier = Modifier
            .size(45.dp)
            .background(onSurfaceLight.copy(0.2f), CircleShape)
            .border(1.5.dp, onSurfaceLight.copy(0.5f), CircleShape)
            .clip(CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.PersonOutline,
            contentDescription = "User Avatar",
            tint = onSurfaceLight,
            modifier = Modifier.size(24.dp)
        )
    }
}