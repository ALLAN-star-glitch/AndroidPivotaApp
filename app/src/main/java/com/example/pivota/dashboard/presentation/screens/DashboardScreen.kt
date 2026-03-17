package com.example.pivota.dashboard.presentation.screens

import android.annotation.SuppressLint
import android.content.res.Configuration
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
import androidx.compose.ui.platform.LocalContext
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
import androidx.window.core.layout.WindowWidthSizeClass
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.pivota.R
import com.example.pivota.ui.theme.*

// Data classes
data class KPI(
    val title: String,
    val value: String,
    val trend: String,
    val icon: ImageVector
)

data class Activity(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val color: Color
)

@SuppressLint("FrequentlyChangingValue")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DashboardScreen(onNavigateToListings: () -> Unit) {
    val colorScheme = MaterialTheme.colorScheme

    // 📱 Orientation & Window Size Logic
    val configuration = LocalConfiguration.current
    val windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    val isTabletWidth = windowSizeClass.windowWidthSizeClass != WindowWidthSizeClass.COMPACT
    val isWide = windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) ||
            windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)

    // Header is ALWAYS sticky - use the same listState for both layouts
    val listState = rememberLazyListState()

    // 📏 Header sizes
    val maxHeight = if (isWide) 280.dp else 220.dp
    val minHeight = if (isWide) 120.dp else 90.dp

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
        containerColor = colorScheme.background,
        topBar = {
            DashboardHeroHeader(
                primaryColor = colorScheme.primary,
                accentColor = colorScheme.tertiaryContainer,
                height = animatedHeight,
                collapseFraction = collapseFraction,
                onSurfaceColor = colorScheme.onSurface,
                isWide = isWide
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = padding.calculateBottomPadding())
        ) {
            // Use LazyColumn for BOTH layouts to ensure header collapses properly
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    top = maxHeight + 12.dp
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                if (isWide) {
                    // TWO PANE LAYOUT (Tablet/Desktop)
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp),
                            horizontalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            // Left Pane
                            Column(
                                modifier = Modifier.weight(1.5f),
                                verticalArrangement = Arrangement.spacedBy(20.dp)
                            ) {
                                KpiCardsSection(
                                    kpis = kpiData,
                                    primaryColor = colorScheme.primary,
                                    textPrimary = colorScheme.onSurface,
                                    textSecondary = colorScheme.onSurfaceVariant,
                                    isWide = true
                                )

                                ProfessionalAnalyticsSection(
                                    primaryColor = colorScheme.primary,
                                    accentColor = colorScheme.tertiary,
                                    textPrimary = colorScheme.onSurface,
                                    textSecondary = colorScheme.onSurfaceVariant,
                                    borderColor = colorScheme.outlineVariant,
                                    isWide = true
                                )

                                WalletTrustSection(
                                    primaryColor = colorScheme.primary,
                                    textPrimary = colorScheme.onSurface,
                                    textSecondary = colorScheme.onSurfaceVariant,
                                    borderColor = colorScheme.outlineVariant,
                                    isWide = true
                                )
                            }

                            // Right Pane
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(20.dp)
                            ) {
                                BusinessManagementSection(
                                    primaryColor = colorScheme.primary,
                                    textPrimary = colorScheme.onSurface,
                                    textSecondary = colorScheme.onSurfaceVariant,
                                    borderColor = colorScheme.outlineVariant,
                                    onMyListingsClick = onNavigateToListings,
                                    isWide = true
                                )

                                RecentActivitySection(
                                    primaryColor = colorScheme.primary,
                                    accentColor = colorScheme.tertiary,
                                    textPrimary = colorScheme.onSurface,
                                    textSecondary = colorScheme.onSurfaceVariant,
                                    borderColor = colorScheme.outlineVariant,
                                    isWide = true
                                )
                            }
                        }
                    }
                } else {
                    // SINGLE PANE LAYOUT (Mobile) - Optimized for small screens
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // KPI Cards - First row (2 cards)
                            KpiCardsSection(
                                kpis = kpiData,
                                primaryColor = colorScheme.primary,
                                textPrimary = colorScheme.onSurface,
                                textSecondary = colorScheme.onSurfaceVariant,
                                isWide = false
                            )

                            // Analytics Section
                            ProfessionalAnalyticsSection(
                                primaryColor = colorScheme.primary,
                                accentColor = colorScheme.tertiary,
                                textPrimary = colorScheme.onSurface,
                                textSecondary = colorScheme.onSurfaceVariant,
                                borderColor = colorScheme.outlineVariant,
                                isWide = false
                            )

                            // Business Management
                            BusinessManagementSection(
                                primaryColor = colorScheme.primary,
                                textPrimary = colorScheme.onSurface,
                                textSecondary = colorScheme.onSurfaceVariant,
                                borderColor = colorScheme.outlineVariant,
                                onMyListingsClick = onNavigateToListings,
                                isWide = false
                            )

                            // Recent Activity
                            RecentActivitySection(
                                primaryColor = colorScheme.primary,
                                accentColor = colorScheme.tertiary,
                                textPrimary = colorScheme.onSurface,
                                textSecondary = colorScheme.onSurfaceVariant,
                                borderColor = colorScheme.outlineVariant,
                                isWide = false
                            )

                            // Wallet & Trust
                            WalletTrustSection(
                                primaryColor = colorScheme.primary,
                                textPrimary = colorScheme.onSurface,
                                textSecondary = colorScheme.onSurfaceVariant,
                                borderColor = colorScheme.outlineVariant,
                                isWide = false
                            )
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(60.dp)) }
            }
        }
    }
}

/* ────────────── KPI CARDS SECTION (MOBILE OPTIMIZED) ────────────── *//* ────────────── KPI CARDS SECTION (MOBILE OPTIMIZED - FIXED) ────────────── */
@Composable
fun KpiCardsSection(
    kpis: List<KPI>,
    primaryColor: Color,
    textPrimary: Color,
    textSecondary: Color,
    isWide: Boolean
) {
    val colorScheme = MaterialTheme.colorScheme

    Column {
        SectionHeader("Performance Overview", "Last 30 days", textPrimary, textSecondary, isWide)
        Spacer(modifier = Modifier.height(12.dp))

        if (isWide) {
            // Desktop/Tablet: 4 in a row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                kpis.forEach { kpi ->
                    Box(modifier = Modifier.weight(1f)) {
                        CompactKpiCard(
                            kpi = kpi,
                            primaryColor = primaryColor,
                            textPrimary = textPrimary,
                            textSecondary = textSecondary,
                            colorScheme = colorScheme
                        )
                    }
                }
            }
        } else {
            // Mobile: 2 rows of 2 with improved visibility
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // First row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    kpis.take(2).forEach { kpi ->
                        Box(modifier = Modifier.weight(1f)) {
                            MobileKpiCard(
                                kpi = kpi,
                                primaryColor = primaryColor,
                                textPrimary = textPrimary,
                                textSecondary = textSecondary,
                                colorScheme = colorScheme
                            )
                        }
                    }
                }
                // Second row
                if (kpis.size > 2) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        kpis.drop(2).forEach { kpi ->
                            Box(modifier = Modifier.weight(1f)) {
                                MobileKpiCard(
                                    kpi = kpi,
                                    primaryColor = primaryColor,
                                    textPrimary = textPrimary,
                                    textSecondary = textSecondary,
                                    colorScheme = colorScheme
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MobileKpiCard(
    kpi: KPI,
    primaryColor: Color,
    textPrimary: Color,
    textSecondary: Color,
    colorScheme: ColorScheme
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceContainerLow),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
        ) {
            // First row: Icon and trend
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Icon with background
                Surface(
                    shape = CircleShape,
                    color = primaryColor.copy(0.1f),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            kpi.icon,
                            contentDescription = null,
                            tint = primaryColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Trend badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (kpi.trend.startsWith("+")) colorScheme.primary.copy(0.1f) else colorScheme.error.copy(0.1f)
                ) {
                    Text(
                        text = kpi.trend,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (kpi.trend.startsWith("+")) colorScheme.primary else colorScheme.error,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Value with appropriate font size
            Text(
                text = kpi.value,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = textPrimary,
                    fontSize = 18.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Title with proper spacing
            Text(
                text = kpi.title,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = textSecondary,
                    fontSize = 12.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun CompactKpiCard(
    kpi: KPI,
    primaryColor: Color,
    textPrimary: Color,
    textSecondary: Color,
    colorScheme: ColorScheme
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceContainerLow),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Surface(
                shape = CircleShape,
                color = primaryColor.copy(0.1f),
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        kpi.icon,
                        contentDescription = null,
                        tint = primaryColor,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = kpi.title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = textSecondary,
                        fontSize = 13.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = kpi.value,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = textPrimary,
                        fontSize = 20.sp
                    ),
                    maxLines = 1
                )
            }

            // Trend
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (kpi.trend.startsWith("+")) colorScheme.primary.copy(0.1f) else colorScheme.error.copy(0.1f)
            ) {
                Text(
                    text = kpi.trend,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = if (kpi.trend.startsWith("+")) colorScheme.primary else colorScheme.error,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                )
            }
        }
    }
}

/* ────────────── PROFESSIONAL ANALYTICS SECTION (MOBILE OPTIMIZED) ────────────── */
@Composable
fun ProfessionalAnalyticsSection(
    primaryColor: Color,
    accentColor: Color,
    textPrimary: Color,
    textSecondary: Color,
    borderColor: Color,
    isWide: Boolean
) {
    val colorScheme = MaterialTheme.colorScheme
    var selectedPeriod by remember { mutableStateOf("Week") }
    var selectedChartType by remember { mutableStateOf("Views") }
    val periods = listOf("Day", "Week", "Month", "Year")
    val chartTypes = listOf("Views", "Leads", "CTR")

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceContainerLow),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(if (isWide) 20.dp else 16.dp)) {
            // Header
            if (isWide) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HeaderText(textPrimary, textSecondary, isWide)
                    PeriodSelector(
                        periods = periods,
                        selectedPeriod = selectedPeriod,
                        onPeriodSelected = { selectedPeriod = it },
                        primaryColor = primaryColor,
                        textSecondary = textSecondary,
                        colorScheme = colorScheme,
                        isWide = isWide
                    )
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HeaderText(textPrimary, textSecondary, isWide)
                    PeriodSelector(
                        periods = periods,
                        selectedPeriod = selectedPeriod,
                        onPeriodSelected = { selectedPeriod = it },
                        primaryColor = primaryColor,
                        textSecondary = textSecondary,
                        colorScheme = colorScheme,
                        isWide = isWide
                    )
                }
            }

            Spacer(modifier = Modifier.height(if (isWide) 20.dp else 16.dp))

            // Chart Type Selector - Horizontal scrollable on mobile
            if (isWide) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    ChartTypeSelector(
                        chartTypes = chartTypes,
                        selectedChartType = selectedChartType,
                        onChartTypeSelected = { selectedChartType = it },
                        primaryColor = primaryColor,
                        textSecondary = textSecondary,
                        colorScheme = colorScheme
                    )
                }
            } else {
                // Horizontal scrollable for mobile
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        ChartTypeSelector(
                            chartTypes = chartTypes,
                            selectedChartType = selectedChartType,
                            onChartTypeSelected = { selectedChartType = it },
                            primaryColor = primaryColor,
                            textSecondary = textSecondary,
                            colorScheme = colorScheme
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(if (isWide) 20.dp else 16.dp))

            // Chart Card
            ProfessionalChartCard(
                primaryColor = primaryColor,
                accentColor = accentColor,
                textPrimary = textPrimary,
                textSecondary = textSecondary,
                borderColor = borderColor,
                isWide = isWide,
                colorScheme = colorScheme,
                selectedChartType = selectedChartType
            )

            // Stats Grid - Simplified for mobile
            if (isWide) {
                Spacer(modifier = Modifier.height(16.dp))
                ProfessionalStatsGrid(
                    primaryColor = primaryColor,
                    accentColor = accentColor,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary,
                    colorScheme = colorScheme,
                    isWide = true
                )
            }
        }
    }
}

@Composable
fun ChartTypeSelector(
    chartTypes: List<String>,
    selectedChartType: String,
    onChartTypeSelected: (String) -> Unit,
    primaryColor: Color,
    textSecondary: Color,
    colorScheme: ColorScheme
) {
    Surface(
        shape = RoundedCornerShape(50.dp),
        color = colorScheme.surfaceContainerHighest.copy(alpha = 0.5f),
    ) {
        Row(
            modifier = Modifier.padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            chartTypes.forEach { type ->
                val isSelected = selectedChartType == type
                Surface(
                    shape = RoundedCornerShape(30.dp),
                    color = if (isSelected) primaryColor else Color.Transparent,
                    modifier = Modifier
                        .clickable { onChartTypeSelected(type) }
                        .animateContentSize()
                ) {
                    Text(
                        text = type,
                        modifier = Modifier.padding(horizontal = if (isSelected) 16.dp else 12.dp, vertical = 6.dp),
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) colorScheme.onPrimary else textSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun ProfessionalChartCard(
    primaryColor: Color,
    accentColor: Color,
    textPrimary: Color,
    textSecondary: Color,
    borderColor: Color,
    isWide: Boolean,
    colorScheme: ColorScheme,
    selectedChartType: String
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceContainer),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(if (isWide) 16.dp else 12.dp)) {
            // Chart Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    if (isWide) "Performance Trend" else "Trend",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = textPrimary
                    )
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(if (isWide) 16.dp else 8.dp)
                ) {
                    LegendItem("Current", primaryColor, textSecondary, isWide)
                    LegendItem("Previous", accentColor, textSecondary, isWide)
                }
            }

            Spacer(modifier = Modifier.height(if (isWide) 16.dp else 12.dp))

            // Chart
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (isWide) 180.dp else 120.dp)
            ) {
                // Background grid lines
                Canvas(modifier = Modifier.matchParentSize()) {
                    val stepY = size.height / 4
                    for (i in 1..3) {
                        drawLine(
                            color = borderColor.copy(alpha = 0.2f),
                            start = Offset(0f, stepY * i),
                            end = Offset(size.width, stepY * i),
                            strokeWidth = 1f
                        )
                    }
                }

                val multiplier = when (selectedChartType) {
                    "Views" -> 1.0
                    "Leads" -> 0.6
                    "CTR" -> 0.3
                    else -> 1.0
                }

                val data = if (isWide) {
                    listOf(
                        Triple("Mon", (40 * multiplier).toInt(), (25 * multiplier).toInt()),
                        Triple("Tue", (65 * multiplier).toInt(), (35 * multiplier).toInt()),
                        Triple("Wed", (45 * multiplier).toInt(), (30 * multiplier).toInt()),
                        Triple("Thu", (80 * multiplier).toInt(), (45 * multiplier).toInt()),
                        Triple("Fri", (55 * multiplier).toInt(), (40 * multiplier).toInt()),
                        Triple("Sat", (70 * multiplier).toInt(), (50 * multiplier).toInt()),
                        Triple("Sun", (90 * multiplier).toInt(), (60 * multiplier).toInt())
                    )
                } else {
                    listOf(
                        Triple("M", (40 * multiplier).toInt(), (25 * multiplier).toInt()),
                        Triple("T", (65 * multiplier).toInt(), (35 * multiplier).toInt()),
                        Triple("W", (45 * multiplier).toInt(), (30 * multiplier).toInt()),
                        Triple("T", (80 * multiplier).toInt(), (45 * multiplier).toInt()),
                        Triple("F", (55 * multiplier).toInt(), (40 * multiplier).toInt()),
                        Triple("S", (70 * multiplier).toInt(), (50 * multiplier).toInt()),
                        Triple("S", (90 * multiplier).toInt(), (60 * multiplier).toInt())
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    data.forEach { (day, currentHeight, previousHeight) ->
                        ProfessionalChartBar(
                            day = day,
                            currentHeight = currentHeight,
                            previousHeight = previousHeight,
                            primaryColor = primaryColor,
                            accentColor = accentColor,
                            textSecondary = textSecondary,
                            isWide = isWide,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            if (isWide) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = borderColor, modifier = Modifier.padding(vertical = 4.dp))

                // Chart Footer
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = colorScheme.primary.copy(0.1f),
                            modifier = Modifier.size(20.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Outlined.TrendingUp,
                                    contentDescription = null,
                                    tint = colorScheme.primary,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "↑ 23.5%",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = primaryColor.copy(0.08f),
                        modifier = Modifier.clickable { }
                    ) {
                        Text(
                            "Details",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = primaryColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProfessionalChartBar(
    day: String,
    currentHeight: Int,
    previousHeight: Int,
    primaryColor: Color,
    accentColor: Color,
    textSecondary: Color,
    isWide: Boolean,
    modifier: Modifier = Modifier
) {
    var currentAnimation by remember { mutableStateOf(0f) }
    var previousAnimation by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        currentAnimation = 1f
        previousAnimation = 1f
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = modifier
    ) {
        // Previous period bar (accent)
        Box(
            modifier = Modifier
                .fillMaxWidth(if (isWide) 0.5f else 0.4f)
                .height((previousHeight * previousAnimation).dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(accentColor.copy(0.7f), accentColor.copy(0.3f))
                    ),
                    RoundedCornerShape(3.dp)
                )
        )

        Spacer(modifier = Modifier.height(3.dp))

        // Current period bar (primary)
        Box(
            modifier = Modifier
                .fillMaxWidth(if (isWide) 0.8f else 0.7f)
                .height((currentHeight * currentAnimation).dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(primaryColor, primaryColor.copy(0.5f))
                    ),
                    RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                )
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = day,
            style = MaterialTheme.typography.labelSmall.copy(
                color = textSecondary,
                fontSize = if (isWide) 10.sp else 9.sp
            ),
            maxLines = 1
        )
    }
}

@Composable
fun PeriodSelector(
    periods: List<String>,
    selectedPeriod: String,
    onPeriodSelected: (String) -> Unit,
    primaryColor: Color,
    textSecondary: Color,
    colorScheme: ColorScheme,
    isWide: Boolean
) {
    Surface(
        shape = RoundedCornerShape(50.dp),
        color = colorScheme.surfaceContainerHighest.copy(alpha = 0.5f),
    ) {
        Row(
            modifier = Modifier.padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            periods.forEach { period ->
                val isSelected = selectedPeriod == period
                Surface(
                    shape = RoundedCornerShape(30.dp),
                    color = if (isSelected) primaryColor else Color.Transparent,
                    modifier = Modifier
                        .clickable { onPeriodSelected(period) }
                        .animateContentSize()
                ) {
                    Text(
                        text = period,
                        modifier = Modifier.padding(
                            horizontal = if (isWide) 12.dp else 8.dp,
                            vertical = if (isWide) 6.dp else 4.dp
                        ),
                        fontSize = if (isWide) 12.sp else 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) colorScheme.onPrimary else textSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun HeaderText(
    textPrimary: Color,
    textSecondary: Color,
    isWide: Boolean
) {
    Column {
        Text(
            if (isWide) "Analytics Dashboard" else "Analytics",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = textPrimary,
                fontSize = if (isWide) 18.sp else 16.sp
            )
        )
        if (isWide) {
            Text(
                "Real-time performance metrics",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = textSecondary
                )
            )
        }
    }
}

@Composable
fun ProfessionalStatsGrid(
    primaryColor: Color,
    accentColor: Color,
    textPrimary: Color,
    textSecondary: Color,
    colorScheme: ColorScheme,
    isWide: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ProfessionalStatCard(
            title = "Views",
            value = "12.4K",
            change = "+18.2%",
            icon = Icons.Outlined.Visibility,
            color = primaryColor,
            textPrimary = textPrimary,
            textSecondary = textSecondary,
            colorScheme = colorScheme,
            modifier = Modifier.weight(1f)
        )
        ProfessionalStatCard(
            title = "Visitors",
            value = "8.2K",
            change = "+12.5%",
            icon = Icons.Outlined.Person,
            color = accentColor,
            textPrimary = textPrimary,
            textSecondary = textSecondary,
            colorScheme = colorScheme,
            modifier = Modifier.weight(1f)
        )
        ProfessionalStatCard(
            title = "Conv.",
            value = "3.8%",
            change = "+2.1%",
            icon = Icons.Outlined.TrendingUp,
            color = colorScheme.secondary,
            textPrimary = textPrimary,
            textSecondary = textSecondary,
            colorScheme = colorScheme,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun ProfessionalStatCard(
    title: String,
    value: String,
    change: String,
    icon: ImageVector,
    color: Color,
    textPrimary: Color,
    textSecondary: Color,
    colorScheme: ColorScheme,
    modifier: Modifier = Modifier
) {
    val isPositive = change.startsWith("+")
    val changeColor = if (isPositive) colorScheme.primary else colorScheme.error

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceContainer),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    shape = CircleShape,
                    color = color.copy(0.1f),
                    modifier = Modifier.size(28.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            icon,
                            contentDescription = null,
                            tint = color,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = changeColor.copy(0.1f)
                ) {
                    Text(
                        text = change,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = changeColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = textPrimary,
                    fontSize = 14.sp
                )
            )

            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = textSecondary,
                    fontSize = 10.sp
                )
            )
        }
    }
}

@Composable
fun LegendItem(label: String, color: Color, textSecondary: Color, isWide: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(if (isWide) 8.dp else 6.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                color = textSecondary,
                fontSize = if (isWide) 10.sp else 9.sp
            )
        )
    }
}

// Business Management Section (Mobile Optimized)
@Composable
fun BusinessManagementSection(
    primaryColor: Color,
    textPrimary: Color,
    textSecondary: Color,
    borderColor: Color,
    onMyListingsClick: () -> Unit,
    isWide: Boolean
) {
    val colorScheme = MaterialTheme.colorScheme

    Column {
        SectionHeader("Business", "3 active", textPrimary, textSecondary, isWide)
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceContainerLow),
            elevation = CardDefaults.cardElevation(1.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                MobileBusinessItem(
                    icon = Icons.Outlined.List,
                    title = "My Listings",
                    value = "3",
                    color = primaryColor,
                    onClick = onMyListingsClick
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = borderColor)
                MobileBusinessItem(
                    icon = Icons.Outlined.EventAvailable,
                    title = "Bookings",
                    value = "12",
                    color = primaryColor
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = borderColor)
                MobileBusinessItem(
                    icon = Icons.Outlined.Chat,
                    title = "Messages",
                    value = "8",
                    color = primaryColor
                )
            }
        }
    }
}

@Composable
fun MobileBusinessItem(
    icon: ImageVector,
    title: String,
    value: String,
    color: Color,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = color.copy(0.08f),
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            )
            Icon(
                Icons.Rounded.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

// Recent Activity Section (Mobile Optimized)
@Composable
fun RecentActivitySection(
    primaryColor: Color,
    accentColor: Color,
    textPrimary: Color,
    textSecondary: Color,
    borderColor: Color,
    isWide: Boolean
) {
    val colorScheme = MaterialTheme.colorScheme

    Column {
        SectionHeader("Activity", "View all", textPrimary, textSecondary, isWide)
        Spacer(modifier = Modifier.height(8.dp))

        val activities = listOf(
            Activity("New application", "Electrician needed", Icons.Outlined.Description, primaryColor),
            Activity("Message received", "Sarah responded", Icons.Outlined.Chat, accentColor),
            Activity("Booking confirmed", "Moving service", Icons.Outlined.Event, primaryColor)
        )

        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceContainerLow),
            elevation = CardDefaults.cardElevation(1.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                activities.forEachIndexed { index, activity ->
                    MobileActivityItem(activity, textPrimary, textSecondary)
                    if (index < activities.size - 1) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = borderColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MobileActivityItem(
    activity: Activity,
    textPrimary: Color,
    textSecondary: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            color = activity.color.copy(0.1f),
            modifier = Modifier.size(32.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    activity.icon,
                    contentDescription = null,
                    tint = activity.color,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = activity.title,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = textPrimary
                )
            )
            Text(
                text = activity.subtitle,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = textSecondary
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Text(
            text = "2h",
            style = MaterialTheme.typography.labelSmall.copy(
                color = textSecondary
            )
        )
    }
}

// Wallet & Trust Section (Mobile Optimized)
@Composable
fun WalletTrustSection(
    primaryColor: Color,
    textPrimary: Color,
    textSecondary: Color,
    borderColor: Color,
    isWide: Boolean
) {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceContainerLow),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Wallet",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = textSecondary
                        )
                    )
                    Text(
                        "KES 12.4K",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = textPrimary
                        )
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.Verified,
                        contentDescription = null,
                        tint = colorScheme.primary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        "98",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryColor,
                        contentColor = colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Deposit", fontSize = 12.sp)
                }
                OutlinedButton(
                    onClick = { },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = primaryColor
                    ),
                    border = BorderStroke(1.dp, primaryColor),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Withdraw", fontSize = 12.sp)
                }
            }
        }
    }
}

// Section Header
@Composable
fun SectionHeader(
    title: String,
    subtitle: String,
    textPrimary: Color,
    textSecondary: Color,
    isWide: Boolean
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
                color = textPrimary,
                fontSize = if (isWide) 18.sp else 16.sp
            )
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall.copy(
                color = textSecondary,
                fontSize = if (isWide) 13.sp else 12.sp
            ),
            modifier = Modifier.clickable { }
        )
    }
}

/* ────────────── COLLAPSIBLE HEADER ────────────── */
@Composable
fun DashboardHeroHeader(
    primaryColor: Color,
    accentColor: Color,
    height: Dp,
    collapseFraction: Float,
    onSurfaceColor: Color,
    isWide: Boolean,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val collapsed = collapseFraction > 0.85f

    val maxFontSize = if (isWide) 40.sp else 32.sp
    val minFontSize = if (isWide) 28.sp else 22.sp

    val backgroundColor by animateColorAsState(
        targetValue = if (collapsed) colorScheme.primary.copy(alpha = 0.95f) else Color.Transparent,
        animationSpec = tween(durationMillis = 300)
    )

    val titleFontSize = ((maxFontSize.value - collapseFraction * (maxFontSize.value - minFontSize.value))).sp

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(height),
        shadowElevation = if (collapsed) 4.dp else 0.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Background Image
            AnimatedVisibility(
                visible = !collapsed,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(R.drawable.dashbaordd)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor)
            )

            // Gradient overlay
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
                                    colorScheme.primary.copy(0.9f),
                                    colorScheme.primary.copy(0.6f),
                                    Color.Transparent
                                )
                            )
                        )
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
            ) {
                // Top Row
                AnimatedVisibility(
                    visible = !collapsed,
                    enter = fadeIn() + slideInVertically(),
                    exit = fadeOut() + slideOutVertically()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp, start = 16.dp, end = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box {
                                HeaderAvatarDashboard(colorScheme)
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .background(colorScheme.tertiary, CircleShape)
                                        .border(1.5.dp, colorScheme.primary, CircleShape)
                                        .align(Alignment.BottomEnd)
                                )
                            }

                            Spacer(Modifier.width(8.dp))

                            Column {
                                Text(
                                    "Hi, Guest",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    "Welcome",
                                    color = Color.White.copy(0.85f),
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            HeaderActionIconDashboard(
                                icon = Icons.Outlined.Mail,
                                iconTint = Color.White,
                                backgroundTint = Color.White.copy(alpha = 0.2f),
                                size = if (isWide) 44.dp else 36.dp
                            ) {}
                            HeaderActionIconDashboard(
                                icon = Icons.Outlined.Notifications,
                                iconTint = Color.White,
                                backgroundTint = Color.White.copy(alpha = 0.2f),
                                size = if (isWide) 44.dp else 36.dp
                            ) {}
                        }
                    }
                }

                if (!collapsed) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }

            if (collapsed) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterStart)
                        .padding(start = 16.dp, end = 16.dp)
                        .offset(y = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Dashboard",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = titleFontSize
                        )
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        HeaderActionIconDashboard(
                            icon = Icons.Outlined.Mail,
                            iconTint = Color.White,
                            backgroundTint = Color.White.copy(alpha = 0.2f),
                            size = 36.dp
                        ) {}
                        HeaderActionIconDashboard(
                            icon = Icons.Outlined.Notifications,
                            iconTint = Color.White,
                            backgroundTint = Color.White.copy(alpha = 0.2f),
                            size = 36.dp
                        ) {}
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 16.dp, bottom = 24.dp)
                        .statusBarsPadding()
                ) {
                    Text(
                        text = "Dashboard",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = titleFontSize
                        )
                    )

                    Text(
                        text = "Manage your business",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White.copy(0.9f),
                            fontSize = 14.sp
                        ),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun HeaderActionIconDashboard(
    icon: ImageVector,
    iconTint: Color = MaterialTheme.colorScheme.onSurface,
    backgroundTint: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
    size: Dp = 40.dp,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(size)
    ) {
        Box(
            modifier = Modifier
                .size(size * 0.8f)
                .background(backgroundTint, CircleShape)
                .clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(size * 0.4f)
            )
        }
    }
}

@Composable
fun HeaderAvatarDashboard(colorScheme: ColorScheme) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .background(colorScheme.onSurface.copy(0.2f), CircleShape)
            .border(1.5.dp, colorScheme.onSurface.copy(0.5f), CircleShape)
            .clip(CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.PersonOutline,
            contentDescription = "User Avatar",
            tint = colorScheme.onSurface,
            modifier = Modifier.size(18.dp)
        )
    }
}