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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
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
import com.example.pivota.auth.domain.model.User
import com.example.pivota.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

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

data class EscrowTransaction(
    val id: String,
    val title: String,
    val amount: String,
    val status: EscrowStatus,
    val counterparty: String,
    val date: String,
    val progress: Float,
    val type: TransactionType
)

enum class EscrowStatus {
    ACTIVE, PENDING, COMPLETED, DISPUTED
}

enum class TransactionType {
    LISTING, SERVICE, VIEWING_FEE
}

data class WalletInfo(
    val availableBalance: String,
    val pendingPayout: String,
    val totalEarned: String,
    val currency: String = "KES"
)

data class ProfessionalMetrics(
    val activeServices: Int,
    val completedJobs: Int,
    val averageRating: Float,
    val totalReviews: Int
)

data class ListerMetrics(
    val activeListings: Int,
    val totalViews: Int,
    val inquiries: Int,
    val conversionRate: String
)

data class BusinessMetric(
    val title: String,
    val value: String,
    val change: String,
    val icon: ImageVector,
    val color: Color
)

data class UpcomingBooking(
    val id: String,
    val title: String,
    val client: String,
    val date: String,
    val time: String,
    val status: String
)

data class Message(
    val id: String,
    val sender: String,
    val preview: String,
    val time: String,
    val unread: Boolean,
    val avatar: String?
)

data class ListingSummary(
    val id: String,
    val title: String,
    val type: String,
    val views: Int,
    val inquiries: Int,
    val status: String
)

enum class UserType {
    PROFESSIONAL, LISTER, BOTH
}

@SuppressLint("FrequentlyChangingValue")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DashboardScreen(
    onNavigateToListings: () -> Unit,
    onNavigateToBookings: () -> Unit = {},
    onNavigateToMessages: () -> Unit = {},
    onNavigateToEarnings: () -> Unit = {},
    onNavigateToEscrow: () -> Unit = {},
    userType: UserType = UserType.BOTH,
    isGuestMode: Boolean = false,
    user: User? = null,
    accessToken: String? = null
) {
    val colorScheme = MaterialTheme.colorScheme

    // Extract user information for display
    val displayName = remember(user) {
        when {
            user == null -> "Guest"
            user.userName.isNotBlank() -> user.userName.split(" ").firstOrNull() ?: "Guest"
            user.firstName.isNotBlank() -> user.firstName
            else -> user.email.split("@").firstOrNull() ?: "Guest"
        }
    }

    val fullName = remember(user) {
        when {
            user == null -> "Guest"
            user.userName.isNotBlank() -> user.userName
            user.firstName.isNotBlank() && user.lastName.isNotBlank() -> "${user.firstName} ${user.lastName}"
            user.firstName.isNotBlank() -> user.firstName
            else -> user.email.split("@").firstOrNull() ?: "Guest"
        }
    }

    val userRole = user?.role
    val accountType = user?.accountType

    // Log user info for debugging
    LaunchedEffect(user, accessToken) {
        if (user != null) {
            println("🔍 [DashboardScreen] User loaded:")
            println("   - Email: ${user.email}")
            println("   - Name: ${user.userName}")
            println("   - First: ${user.firstName}")
            println("   - Last: ${user.lastName}")
            println("   - Role: ${user.role}")
            println("   - Account Type: ${user.accountType}")
            println("   - Access Token: ${if (accessToken != null) "Present" else "Missing"}")
        }
    }

    // 📱 Orientation & Window Size Logic
    val configuration = LocalConfiguration.current
    val windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    val isTabletWidth = windowSizeClass.windowWidthSizeClass != WindowWidthSizeClass.COMPACT
    val isWide = windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) ||
            windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)

    val listState = rememberLazyListState()

    // 📏 Header sizes
    val maxHeight = if (isWide) 280.dp else 220.dp
    val minHeight = if (isWide) 120.dp else 90.dp

    val density = LocalDensity.current
    val collapseRangePx = with(density) {
        (maxHeight - minHeight).toPx()
    }

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
        KPI("Total Views", "3.8K", "+28%", Icons.Outlined.Visibility),
        KPI("SmartMatches", "156", "+43%", Icons.Outlined.AutoAwesome),
        KPI("Revenue", "KES 45.2K", "+18%", Icons.Outlined.TrendingUp)
    )

    // Mock wallet data
    val walletInfo = WalletInfo(
        availableBalance = "KES 45,200",
        pendingPayout = "KES 12,500",
        totalEarned = "KES 157,800"
    )

    // Mock professional metrics
    val professionalMetrics = ProfessionalMetrics(
        activeServices = 8,
        completedJobs = 47,
        averageRating = 4.8f,
        totalReviews = 89
    )

    // Mock lister metrics
    val listerMetrics = ListerMetrics(
        activeListings = 12,
        totalViews = 3842,
        inquiries = 156,
        conversionRate = "23%"
    )

    // Mock business metrics using theme colors
    val businessMetrics = listOf(
        BusinessMetric("Conversion Rate", "23%", "+5%", Icons.Rounded.TrendingUp, SuccessGreen),
        BusinessMetric("Avg. Response", "2.4h", "-12%", Icons.Rounded.Schedule, InfoBlue),
        BusinessMetric("Completion Rate", "94%", "+3%", Icons.Rounded.CheckCircle, SuccessGreen),
        BusinessMetric("Customer Rating", "4.8★", "+0.2", Icons.Rounded.Star, tertiaryLight)
    )

    // Mock escrow transactions
    val escrowTransactions = listOf(
        EscrowTransaction(
            id = "1",
            title = "Property Deposit - Kilimani",
            amount = "KES 150,000",
            status = EscrowStatus.ACTIVE,
            counterparty = "John Mwangi (Tenant)",
            date = "Release in 3 days",
            progress = 0.65f,
            type = TransactionType.LISTING
        ),
        EscrowTransaction(
            id = "2",
            title = "Plumbing Services",
            amount = "KES 45,000",
            status = EscrowStatus.ACTIVE,
            counterparty = "Sarah Kimani (Client)",
            date = "Awaiting completion",
            progress = 0.7f,
            type = TransactionType.SERVICE
        ),
        EscrowTransaction(
            id = "3",
            title = "Electrical Repairs",
            amount = "KES 12,500",
            status = EscrowStatus.PENDING,
            counterparty = "Peter Otieno",
            date = "Pending approval",
            progress = 0.3f,
            type = TransactionType.SERVICE
        ),
        EscrowTransaction(
            id = "4",
            title = "House Viewing - Lavington",
            amount = "KES 500",
            status = EscrowStatus.COMPLETED,
            counterparty = "Mary Wanjiku",
            date = "Completed yesterday",
            progress = 1f,
            type = TransactionType.VIEWING_FEE
        ),
        EscrowTransaction(
            id = "5",
            title = "Moving Services",
            amount = "KES 25,000",
            status = EscrowStatus.ACTIVE,
            counterparty = "James Mburu",
            date = "Release in 5 days",
            progress = 0.4f,
            type = TransactionType.SERVICE
        ),
        EscrowTransaction(
            id = "6",
            title = "Rental Deposit - Westlands",
            amount = "KES 85,000",
            status = EscrowStatus.PENDING,
            counterparty = "Alice Njeri",
            date = "Awaiting viewing",
            progress = 0.1f,
            type = TransactionType.LISTING
        )
    )

    // Mock upcoming bookings
    val upcomingBookings = listOf(
        UpcomingBooking("1", "Property Viewing", "James K.", "Today", "2:00 PM", "Confirmed"),
        UpcomingBooking("2", "Plumbing Service", "Mary W.", "Tomorrow", "10:00 AM", "Pending"),
        UpcomingBooking("3", "Electrical Repair", "Peter O.", "Wed", "3:30 PM", "Confirmed")
    )

    // Mock messages
    val messages = listOf(
        Message("1", "Sarah Kimani", "Is the apartment still available?", "5m", true, null),
        Message("2", "John Mburu", "When can you come for inspection?", "2h", true, null),
        Message("3", "Mary Wanjiku", "Thank you for the excellent service!", "1d", false, null),
        Message("4", "Peter Otieno", "I'd like to book your services", "2d", false, null)
    )

    // Mock listings summary
    val listingsSummary = listOf(
        ListingSummary("1", "Modern Apartment - Kilimani", "Property", 234, 12, "Active"),
        ListingSummary("2", "Senior Plumber Needed", "Job", 189, 8, "Active"),
        ListingSummary("3", "Electrical Services", "Service", 156, 5, "Active"),
        ListingSummary("4", "Commercial Space - Westlands", "Property", 98, 3, "Pending")
    )



    Scaffold(
        containerColor = colorScheme.background,
        topBar = {
            DashboardHeroHeader(
                primaryColor = colorScheme.primary,
                accentColor = tertiaryLight,
                height = animatedHeight,
                collapseFraction = collapseFraction,
                onSurfaceColor = colorScheme.onSurface,
                isWide = isWide,
                isGuestMode = isGuestMode,
                userName = displayName,  // Use first name for greeting
                fullName = fullName,     // Use full name for welcome message
                isLoggedIn = user != null,
                userRole = userRole,
                accountType = accountType
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
                    top = maxHeight + 12.dp
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // 🏦 UNIFIED FINANCIAL HUB (Always visible)
                item {
                    UnifiedFinancialHub(
                        walletInfo = walletInfo,
                        escrowTransactions = escrowTransactions,
                        professionalMetrics = professionalMetrics,
                        listerMetrics = listerMetrics,
                        userType = userType,
                        primaryColor = colorScheme.primary,
                        secondaryColor = colorScheme.secondary,
                        tertiaryColor = tertiaryLight,
                        textPrimary = colorScheme.onSurface,
                        textSecondary = colorScheme.onSurfaceVariant,
                        borderColor = colorScheme.outlineVariant,
                        surfaceColor = colorScheme.surfaceContainerLow,
                        errorColor = colorScheme.error,
                        successColor = SuccessGreen,
                        onViewAllEscrow = onNavigateToEscrow,
                        isWide = isWide
                    )
                }

                // Quick Actions Row
                item {
                    QuickActionsRow(
                        primaryColor = colorScheme.primary,
                        accentColor = tertiaryLight,
                        textSecondary = colorScheme.onSurfaceVariant,
                        borderColor = colorScheme.outlineVariant,
                        surfaceColor = colorScheme.surfaceContainerLow,
                        onNavigateToListings = onNavigateToListings,
                        onNavigateToBookings = onNavigateToBookings,
                        onNavigateToMessages = onNavigateToMessages,
                        onNavigateToEarnings = onNavigateToEarnings,
                        isWide = isWide
                    )
                }

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
                                EnhancedKpiCardsSection(
                                    kpis = kpiData,
                                    primaryColor = colorScheme.primary,
                                    textPrimary = colorScheme.onSurface,
                                    textSecondary = colorScheme.onSurfaceVariant,
                                    surfaceColor = colorScheme.surfaceContainerLow,
                                    borderColor = colorScheme.outlineVariant,
                                    isWide = true
                                )

                                ProfessionalAnalyticsSection(
                                    primaryColor = colorScheme.primary,
                                    accentColor = tertiaryLight,
                                    textPrimary = colorScheme.onSurface,
                                    textSecondary = colorScheme.onSurfaceVariant,
                                    borderColor = colorScheme.outlineVariant,
                                    isWide = true
                                )

                                BusinessMetricsGrid(
                                    metrics = businessMetrics,
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
                                    listingsSummary = listingsSummary,
                                    isWide = true
                                )

                                UpcomingBookingsSection(
                                    bookings = upcomingBookings,
                                    primaryColor = colorScheme.primary,
                                    accentColor = tertiaryLight,
                                    successColor = SuccessGreen,
                                    warningColor = WarningAmber,
                                    textPrimary = colorScheme.onSurface,
                                    textSecondary = colorScheme.onSurfaceVariant,
                                    borderColor = colorScheme.outlineVariant,
                                    onViewAllClick = onNavigateToBookings,
                                    isWide = true
                                )

                                MessagesSection(
                                    messages = messages,
                                    primaryColor = colorScheme.primary,
                                    textPrimary = colorScheme.onSurface,
                                    textSecondary = colorScheme.onSurfaceVariant,
                                    borderColor = colorScheme.outlineVariant,
                                    unreadColor = InfoBlue,
                                    onViewAllClick = onNavigateToMessages,
                                    isWide = true
                                )
                            }
                        }
                    }
                } else {
                    // SINGLE PANE LAYOUT (Mobile)
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            KpiCardsSection(
                                kpis = kpiData,
                                primaryColor = colorScheme.primary,
                                textPrimary = colorScheme.onSurface,
                                textSecondary = colorScheme.onSurfaceVariant,
                                isWide = false
                            )

                            BusinessMetricsGrid(
                                metrics = businessMetrics,
                                isWide = false
                            )

                            ProfessionalAnalyticsSection(
                                primaryColor = colorScheme.primary,
                                accentColor = tertiaryLight,
                                textPrimary = colorScheme.onSurface,
                                textSecondary = colorScheme.onSurfaceVariant,
                                borderColor = colorScheme.outlineVariant,
                                isWide = false
                            )

                            BusinessManagementSection(
                                primaryColor = colorScheme.primary,
                                textPrimary = colorScheme.onSurface,
                                textSecondary = colorScheme.onSurfaceVariant,
                                borderColor = colorScheme.outlineVariant,
                                onMyListingsClick = onNavigateToListings,
                                listingsSummary = listingsSummary,
                                isWide = false
                            )

                            UpcomingBookingsSection(
                                bookings = upcomingBookings,
                                primaryColor = colorScheme.primary,
                                accentColor = tertiaryLight,
                                successColor = SuccessGreen,
                                warningColor = WarningAmber,
                                textPrimary = colorScheme.onSurface,
                                textSecondary = colorScheme.onSurfaceVariant,
                                borderColor = colorScheme.outlineVariant,
                                onViewAllClick = onNavigateToBookings,
                                isWide = false
                            )

                            MessagesSection(
                                messages = messages,
                                primaryColor = colorScheme.primary,
                                textPrimary = colorScheme.onSurface,
                                textSecondary = colorScheme.onSurfaceVariant,
                                borderColor = colorScheme.outlineVariant,
                                unreadColor = InfoBlue,
                                onViewAllClick = onNavigateToMessages,
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

// Quick Actions Row
@Composable
fun QuickActionsRow(
    primaryColor: Color,
    accentColor: Color,
    textSecondary: Color,
    borderColor: Color,
    surfaceColor: Color,
    onNavigateToListings: () -> Unit,
    onNavigateToBookings: () -> Unit,
    onNavigateToMessages: () -> Unit,
    onNavigateToEarnings: () -> Unit,
    isWide: Boolean
) {
    val horizontalPadding = if (isWide) 24.dp else 16.dp

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = surfaceColor),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            QuickActionButton(
                icon = Icons.Rounded.AddCircle,
                label = "New Listing",
                color = primaryColor,
                onClick = onNavigateToListings
            )
            QuickActionButton(
                icon = Icons.Rounded.CalendarMonth,
                label = "Bookings",
                color = accentColor,
                onClick = onNavigateToBookings
            )
            QuickActionButton(
                icon = Icons.Rounded.Message,
                label = "Messages",
                color = InfoBlue,
                onClick = onNavigateToMessages
            )
            QuickActionButton(
                icon = Icons.Rounded.AccountBalanceWallet,
                label = "Earnings",
                color = SuccessGreen,
                onClick = onNavigateToEarnings
            )
        }
    }
}

@Composable
fun QuickActionButton(
    icon: ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(color.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
        }
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

// Business Metrics Grid
@Composable
fun BusinessMetricsGrid(
    metrics: List<BusinessMetric>,
    isWide: Boolean
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                "Business Metrics",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (isWide) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    metrics.chunked(2).forEach { chunk ->
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            chunk.forEach { metric ->
                                BusinessMetricItem(metric)
                            }
                        }
                    }
                }
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    metrics.forEach { metric ->
                        BusinessMetricItem(metric)
                    }
                }
            }
        }
    }
}

@Composable
fun BusinessMetricItem(
    metric: BusinessMetric
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(metric.color.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    metric.icon,
                    contentDescription = null,
                    tint = metric.color,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    metric.title,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    metric.value,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (metric.change.startsWith("+")) SuccessGreen.copy(alpha = 0.1f) else errorLight.copy(alpha = 0.1f)
        ) {
            Text(
                metric.change,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (metric.change.startsWith("+")) SuccessGreen else errorLight
            )
        }
    }
}

// Upcoming Bookings Section
@Composable
fun UpcomingBookingsSection(
    bookings: List<UpcomingBooking>,
    primaryColor: Color,
    accentColor: Color,
    successColor: Color,
    warningColor: Color,
    textPrimary: Color,
    textSecondary: Color,
    borderColor: Color,
    onViewAllClick: () -> Unit,
    isWide: Boolean
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Upcoming Bookings",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
                TextButton(onClick = onViewAllClick) {
                    Text("View All", color = primaryColor)
                }
            }

            bookings.forEach { booking ->
                BookingItem(
                    booking = booking,
                    primaryColor = primaryColor,
                    accentColor = accentColor,
                    successColor = successColor,
                    warningColor = warningColor,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary
                )
                HorizontalDivider(
                    color = borderColor,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
fun BookingItem(
    booking: UpcomingBooking,
    primaryColor: Color,
    accentColor: Color,
    successColor: Color,
    warningColor: Color,
    textPrimary: Color,
    textSecondary: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(primaryColor.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Rounded.CalendarToday,
                contentDescription = null,
                tint = primaryColor,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                booking.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = textPrimary
            )
            Text(
                "${booking.client} • ${booking.date} at ${booking.time}",
                fontSize = 12.sp,
                color = textSecondary
            )
        }

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = when (booking.status) {
                "Confirmed" -> successColor.copy(alpha = 0.1f)
                "Pending" -> warningColor.copy(alpha = 0.1f)
                else -> accentColor.copy(alpha = 0.1f)
            }
        ) {
            Text(
                booking.status,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = when (booking.status) {
                    "Confirmed" -> successColor
                    "Pending" -> warningColor
                    else -> accentColor
                }
            )
        }
    }
}

// Messages Section
@Composable
fun MessagesSection(
    messages: List<Message>,
    primaryColor: Color,
    textPrimary: Color,
    textSecondary: Color,
    borderColor: Color,
    unreadColor: Color,
    onViewAllClick: () -> Unit,
    isWide: Boolean
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Recent Messages",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
                TextButton(onClick = onViewAllClick) {
                    Text("View All", color = primaryColor)
                }
            }

            messages.take(3).forEach { message ->
                MessageItem(
                    message = message,
                    primaryColor = primaryColor,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary,
                    unreadColor = unreadColor
                )
                HorizontalDivider(
                    color = borderColor,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
fun MessageItem(
    message: Message,
    primaryColor: Color,
    textPrimary: Color,
    textSecondary: Color,
    unreadColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(primaryColor.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            if (message.avatar != null) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(message.avatar)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    error = painterResource(id = R.drawable.ic_launcher_background)
                )
            } else {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = primaryColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    message.sender,
                    fontSize = 15.sp,
                    fontWeight = if (message.unread) FontWeight.Bold else FontWeight.Medium,
                    color = textPrimary
                )
                if (message.unread) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(unreadColor, CircleShape)
                    )
                }
            }
            Text(
                message.preview,
                fontSize = 12.sp,
                color = textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Text(
            message.time,
            fontSize = 11.sp,
            color = textSecondary
        )
    }
}

/* ────────────── UNIFIED FINANCIAL HUB ────────────── */
@Composable
fun UnifiedFinancialHub(
    walletInfo: WalletInfo,
    escrowTransactions: List<EscrowTransaction>,
    professionalMetrics: ProfessionalMetrics,
    listerMetrics: ListerMetrics,
    userType: UserType,
    primaryColor: Color,
    secondaryColor: Color,
    tertiaryColor: Color,
    textPrimary: Color,
    textSecondary: Color,
    borderColor: Color,
    surfaceColor: Color,
    errorColor: Color,
    successColor: Color,
    onViewAllEscrow: () -> Unit,
    isWide: Boolean
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Overview", "Escrow", "History")

    Card(
        shape = RoundedCornerShape(if (isWide) 24.dp else 20.dp),
        colors = CardDefaults.cardColors(
            containerColor = surfaceColor
        ),
        elevation = CardDefaults.cardElevation(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = if (isWide) 24.dp else 16.dp)
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(if (isWide) 24.dp else 20.dp),
                spotColor = primaryColor.copy(alpha = 0.15f)
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(if (isWide) 20.dp else 16.dp)
        ) {
            // Header with user type indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Animated icon based on user type
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = primaryColor.copy(alpha = 0.15f),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                when (userType) {
                                    UserType.PROFESSIONAL -> Icons.Rounded.Build
                                    UserType.LISTER -> Icons.Rounded.Home
                                    UserType.BOTH -> Icons.Rounded.AccountBalanceWallet
                                },
                                contentDescription = null,
                                tint = primaryColor,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Column {
                        Text(
                            when (userType) {
                                UserType.PROFESSIONAL -> "Professional Dashboard"
                                UserType.LISTER -> "Lister Dashboard"
                                UserType.BOTH -> "Your Financial Hub"
                            },
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = textPrimary
                            )
                        )
                        Text(
                            when (userType) {
                                UserType.PROFESSIONAL -> "Manage your services & earnings"
                                UserType.LISTER -> "Manage your listings & income"
                                UserType.BOTH -> "Manage all your earnings & transactions"
                            },
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = textSecondary
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Wallet Balance Card (Prominent)
            WalletBalanceCard(
                walletInfo = walletInfo,
                primaryColor = primaryColor,
                textPrimary = textPrimary,
                textSecondary = textSecondary,
                onWithdrawClick = { /* Navigate to withdraw */ },
                isWide = isWide
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Role-specific metrics
            if (userType == UserType.PROFESSIONAL || userType == UserType.BOTH) {
                ProfessionalMetricsRow(
                    metrics = professionalMetrics,
                    primaryColor = primaryColor,
                    secondaryColor = secondaryColor,
                    successColor = successColor,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary,
                    isWide = isWide
                )
            }

            if (userType == UserType.LISTER || userType == UserType.BOTH) {
                ListerMetricsRow(
                    metrics = listerMetrics,
                    primaryColor = primaryColor,
                    secondaryColor = secondaryColor,
                    tertiaryColor = tertiaryColor,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary,
                    isWide = isWide
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tab Row
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                edgePadding = 0.dp,
                divider = {}
            ) {
                tabs.forEachIndexed { index, tab ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(tab, fontSize = if (isWide) 14.sp else 12.sp) },
                        selectedContentColor = primaryColor,
                        unselectedContentColor = textSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tab Content
            when (selectedTab) {
                0 -> OverviewTab(
                    transactions = escrowTransactions,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary,
                    primaryColor = primaryColor,
                    successColor = successColor,
                    onViewAllClick = onViewAllEscrow,
                    isWide = isWide
                )
                1 -> EscrowTab(
                    transactions = escrowTransactions,
                    primaryColor = primaryColor,
                    secondaryColor = secondaryColor,
                    errorColor = errorColor,
                    successColor = successColor,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary,
                    borderColor = borderColor,
                    isWide = isWide
                )
                2 -> HistoryTab(
                    transactions = escrowTransactions,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary,
                    isWide = isWide
                )
            }
        }
    }
}

@Composable
fun WalletBalanceCard(
    walletInfo: WalletInfo,
    primaryColor: Color,
    textPrimary: Color,
    textSecondary: Color,
    onWithdrawClick: () -> Unit,
    isWide: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        primaryColor.copy(alpha = 0.1f),
                        Color.Transparent
                    )
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                "Available Balance",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = textSecondary
                )
            )
            Text(
                walletInfo.availableBalance,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = textPrimary,
                    fontSize = if (isWide) 32.sp else 28.sp
                )
            )
            Text(
                "Pending: ${walletInfo.pendingPayout}",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = textSecondary
                )
            )
            Text(
                "Total Earned: ${walletInfo.totalEarned}",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = textSecondary
                )
            )
        }

        Button(
            onClick = onWithdrawClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = primaryColor
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Rounded.CallMade, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Withdraw")
        }
    }
}

@Composable
fun ProfessionalMetricsRow(
    metrics: ProfessionalMetrics,
    primaryColor: Color,
    secondaryColor: Color,
    successColor: Color,
    textPrimary: Color,
    textSecondary: Color,
    isWide: Boolean
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = primaryColor.copy(alpha = 0.05f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            MetricItem(
                value = metrics.activeServices.toString(),
                label = "Active Jobs",
                icon = Icons.Rounded.Work,
                color = primaryColor,
                textPrimary = textPrimary,
                textSecondary = textSecondary
            )

            VerticalDivider(
                modifier = Modifier.height(30.dp),
                color = textSecondary.copy(alpha = 0.3f)
            )

            MetricItem(
                value = metrics.completedJobs.toString(),
                label = "Completed",
                icon = Icons.Rounded.CheckCircle,
                color = successColor,
                textPrimary = textPrimary,
                textSecondary = textSecondary
            )

            VerticalDivider(
                modifier = Modifier.height(30.dp),
                color = textSecondary.copy(alpha = 0.3f)
            )

            MetricItem(
                value = String.format("%.1f", metrics.averageRating),
                label = "Rating",
                icon = Icons.Rounded.Star,
                color = secondaryColor,
                textPrimary = textPrimary,
                textSecondary = textSecondary
            )

            VerticalDivider(
                modifier = Modifier.height(30.dp),
                color = textSecondary.copy(alpha = 0.3f)
            )

            MetricItem(
                value = metrics.totalReviews.toString(),
                label = "Reviews",
                icon = Icons.Rounded.RateReview,
                color = PurpleAccent,
                textPrimary = textPrimary,
                textSecondary = textSecondary
            )
        }
    }
}

@Composable
fun ListerMetricsRow(
    metrics: ListerMetrics,
    primaryColor: Color,
    secondaryColor: Color,
    tertiaryColor: Color,
    textPrimary: Color,
    textSecondary: Color,
    isWide: Boolean
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = primaryColor.copy(alpha = 0.05f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            MetricItem(
                value = metrics.activeListings.toString(),
                label = "Listings",
                icon = Icons.Rounded.List,
                color = primaryColor,
                textPrimary = textPrimary,
                textSecondary = textSecondary
            )

            VerticalDivider(
                modifier = Modifier.height(30.dp),
                color = textSecondary.copy(alpha = 0.3f)
            )

            MetricItem(
                value = metrics.totalViews.toString(),
                label = "Views",
                icon = Icons.Rounded.Visibility,
                color = secondaryColor,
                textPrimary = textPrimary,
                textSecondary = textSecondary
            )

            VerticalDivider(
                modifier = Modifier.height(30.dp),
                color = textSecondary.copy(alpha = 0.3f)
            )

            MetricItem(
                value = metrics.inquiries.toString(),
                label = "Inquiries",
                icon = Icons.Rounded.Chat,
                color = tertiaryColor,
                textPrimary = textPrimary,
                textSecondary = textSecondary
            )

            VerticalDivider(
                modifier = Modifier.height(30.dp),
                color = textSecondary.copy(alpha = 0.3f)
            )

            MetricItem(
                value = metrics.conversionRate,
                label = "Conv. Rate",
                icon = Icons.Rounded.TrendingUp,
                color = SuccessGreen,
                textPrimary = textPrimary,
                textSecondary = textSecondary
            )
        }
    }
}

@Composable
fun MetricItem(
    value: String,
    label: String,
    icon: ImageVector,
    color: Color,
    textPrimary: Color,
    textSecondary: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(16.dp)
            )
            Text(
                value,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = textPrimary
                )
            )
        }
        Text(
            label,
            style = MaterialTheme.typography.labelSmall.copy(
                color = textSecondary
            )
        )
    }
}

@Composable
fun OverviewTab(
    transactions: List<EscrowTransaction>,
    textPrimary: Color,
    textSecondary: Color,
    primaryColor: Color,
    successColor: Color,
    onViewAllClick: () -> Unit,
    isWide: Boolean
) {
    Column {
        // Summary cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Active escrows
            val activeCount = transactions.count { it.status == EscrowStatus.ACTIVE }
            val activeValue = transactions
                .filter { it.status == EscrowStatus.ACTIVE }
                .sumOf { it.amount.filter { c -> c.isDigit() || c == ',' }.replace(",", "").toIntOrNull() ?: 0 }
                .let { "KES ${String.format("%,d", it)}" }

            OverviewCard(
                title = "Active Escrow",
                value = activeCount.toString(),
                subValue = activeValue,
                icon = Icons.Rounded.Lock,
                color = primaryColor,
                textPrimary = textPrimary,
                textSecondary = textSecondary,
                modifier = Modifier.weight(1f)
            )

            // Pending approvals
            val pendingCount = transactions.count { it.status == EscrowStatus.PENDING }
            val pendingValue = transactions
                .filter { it.status == EscrowStatus.PENDING }
                .sumOf { it.amount.filter { c -> c.isDigit() || c == ',' }.replace(",", "").toIntOrNull() ?: 0 }
                .let { "KES ${String.format("%,d", it)}" }

            OverviewCard(
                title = "Pending",
                value = pendingCount.toString(),
                subValue = pendingValue,
                icon = Icons.Rounded.Schedule,
                color = WarningAmber,
                textPrimary = textPrimary,
                textSecondary = textSecondary,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Completed
            val completedCount = transactions.count { it.status == EscrowStatus.COMPLETED }
            OverviewCard(
                title = "Completed",
                value = completedCount.toString(),
                subValue = "Last 30 days",
                icon = Icons.Rounded.CheckCircle,
                color = successColor,
                textPrimary = textPrimary,
                textSecondary = textSecondary,
                modifier = Modifier.weight(1f)
            )

            // Total Value
            val totalValue = transactions
                .sumOf { it.amount.filter { c -> c.isDigit() || c == ',' }.replace(",", "").toIntOrNull() ?: 0 }
                .let { "KES ${String.format("%,d", it)}" }

            OverviewCard(
                title = "Total Volume",
                value = totalValue,
                subValue = "All time",
                icon = Icons.Rounded.TrendingUp,
                color = PurpleAccent,
                textPrimary = textPrimary,
                textSecondary = textSecondary,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Recent transactions preview
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Recent Transactions",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = textPrimary
                )
            )
            TextButton(onClick = onViewAllClick) {
                Text("View All", color = primaryColor)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        transactions.take(3).forEach { transaction ->
            TransactionPreviewItem(
                transaction = transaction,
                textPrimary = textPrimary,
                textSecondary = textSecondary
            )
        }
    }
}

@Composable
fun OverviewCard(
    title: String,
    value: String,
    subValue: String,
    icon: ImageVector,
    color: Color,
    textPrimary: Color,
    textSecondary: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )

            Column {
                Text(
                    value,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = textPrimary
                    )
                )
                Text(
                    title,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = textSecondary
                    )
                )
                Text(
                    subValue,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = textSecondary,
                        fontSize = 10.sp
                    )
                )
            }
        }
    }
}

@Composable
fun TransactionPreviewItem(
    transaction: EscrowTransaction,
    textPrimary: Color,
    textSecondary: Color
) {
    val statusColor = when (transaction.status) {
        EscrowStatus.ACTIVE -> primaryLight
        EscrowStatus.PENDING -> WarningAmber
        EscrowStatus.COMPLETED -> SuccessGreen
        EscrowStatus.DISPUTED -> errorLight
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Type indicator
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = when (transaction.type) {
                TransactionType.LISTING -> primaryLight.copy(alpha = 0.1f)
                TransactionType.SERVICE -> SuccessGreen.copy(alpha = 0.1f)
                TransactionType.VIEWING_FEE -> WarningAmber.copy(alpha = 0.1f)
            },
            modifier = Modifier.size(36.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    when (transaction.type) {
                        TransactionType.LISTING -> Icons.Rounded.Home
                        TransactionType.SERVICE -> Icons.Rounded.Build
                        TransactionType.VIEWING_FEE -> Icons.Rounded.Visibility
                    },
                    contentDescription = null,
                    tint = when (transaction.type) {
                        TransactionType.LISTING -> primaryLight
                        TransactionType.SERVICE -> SuccessGreen
                        TransactionType.VIEWING_FEE -> WarningAmber
                    },
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                transaction.title,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Medium,
                    color = textPrimary
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                transaction.counterparty,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = textSecondary
                )
            )
        }

        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                transaction.amount,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = textPrimary
                )
            )
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = statusColor.copy(alpha = 0.1f)
            ) {
                Text(
                    transaction.status.name,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = statusColor,
                        fontSize = 9.sp
                    )
                )
            }
        }
    }
}

@Composable
fun EscrowTab(
    transactions: List<EscrowTransaction>,
    primaryColor: Color,
    secondaryColor: Color,
    errorColor: Color,
    successColor: Color,
    textPrimary: Color,
    textSecondary: Color,
    borderColor: Color,
    isWide: Boolean
) {
    var shimmerProgress by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(16)
            shimmerProgress = (shimmerProgress + 0.02f) % 1f
        }
    }

    Column {
        val activeTransactions = transactions.filter {
            it.status == EscrowStatus.ACTIVE || it.status == EscrowStatus.PENDING
        }

        if (activeTransactions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Rounded.Inbox,
                        contentDescription = null,
                        tint = textSecondary,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "No active escrow transactions",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = textSecondary
                        )
                    )
                }
            }
        } else {
            activeTransactions.forEach { transaction ->
                EnhancedEscrowItem(
                    transaction = transaction,
                    primaryColor = primaryColor,
                    secondaryColor = secondaryColor,
                    errorColor = errorColor,
                    successColor = successColor,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary,
                    borderColor = borderColor,
                    shimmerProgress = shimmerProgress
                )
                HorizontalDivider(color = borderColor, modifier = Modifier.padding(vertical = 8.dp))
            }
        }
    }
}

@Composable
fun EnhancedEscrowItem(
    transaction: EscrowTransaction,
    primaryColor: Color,
    secondaryColor: Color,
    errorColor: Color,
    successColor: Color,
    textPrimary: Color,
    textSecondary: Color,
    borderColor: Color,
    shimmerProgress: Float
) {
    val statusColor = when (transaction.status) {
        EscrowStatus.ACTIVE -> primaryColor
        EscrowStatus.PENDING -> secondaryColor
        EscrowStatus.COMPLETED -> successColor
        EscrowStatus.DISPUTED -> errorColor
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left side - Transaction info
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Type indicator with shimmer for active
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                brush = if (transaction.status == EscrowStatus.ACTIVE) {
                                    Brush.linearGradient(
                                        colors = listOf(
                                            statusColor.copy(alpha = 0.3f),
                                            statusColor.copy(alpha = 0.6f),
                                            statusColor.copy(alpha = 0.3f)
                                        ),
                                        start = Offset(shimmerProgress * 100f, 0f),
                                        end = Offset(shimmerProgress * 100f + 100f, 100f)
                                    )
                                } else {
                                    Brush.linearGradient(
                                        colors = listOf(
                                            when (transaction.type) {
                                                TransactionType.LISTING -> primaryLight.copy(alpha = 0.2f)
                                                TransactionType.SERVICE -> SuccessGreen.copy(alpha = 0.2f)
                                                TransactionType.VIEWING_FEE -> WarningAmber.copy(alpha = 0.2f)
                                            },
                                            when (transaction.type) {
                                                TransactionType.LISTING -> primaryLight.copy(alpha = 0.1f)
                                                TransactionType.SERVICE -> SuccessGreen.copy(alpha = 0.1f)
                                                TransactionType.VIEWING_FEE -> WarningAmber.copy(alpha = 0.1f)
                                            }
                                        )
                                    )
                                }
                            )
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                when (transaction.type) {
                                    TransactionType.LISTING -> Icons.Rounded.Home
                                    TransactionType.SERVICE -> Icons.Rounded.Build
                                    TransactionType.VIEWING_FEE -> Icons.Rounded.Visibility
                                },
                                contentDescription = null,
                                tint = when (transaction.type) {
                                    TransactionType.LISTING -> primaryLight
                                    TransactionType.SERVICE -> SuccessGreen
                                    TransactionType.VIEWING_FEE -> WarningAmber
                                },
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Column {
                        Text(
                            transaction.title,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = textPrimary
                            )
                        )
                        Text(
                            transaction.counterparty,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = textSecondary
                            )
                        )
                    }
                }

                // Amount and status
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        transaction.amount,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = textPrimary
                        )
                    )
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = statusColor.copy(alpha = 0.1f),
                        border = BorderStroke(1.dp, statusColor.copy(alpha = 0.3f))
                    ) {
                        Text(
                            transaction.status.name,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = statusColor,
                                fontWeight = FontWeight.Medium,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }

            // Progress bar for active escrows
            if (transaction.status == EscrowStatus.ACTIVE) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        transaction.date,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = textSecondary
                        )
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(borderColor)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(transaction.progress)
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(
                                            primaryColor,
                                            primaryColor.copy(alpha = 0.7f)
                                        )
                                    )
                                )
                        )
                    }

                    Text(
                        "${(transaction.progress * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = primaryColor
                        )
                    )
                }
            }

            // Action buttons for pending/active
            if (transaction.status == EscrowStatus.PENDING || transaction.status == EscrowStatus.ACTIVE) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = textPrimary
                        )
                    ) {
                        Text("Contact")
                    }

                    Button(
                        onClick = { },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primaryColor
                        )
                    ) {
                        Text(if (transaction.status == EscrowStatus.PENDING) "Approve" else "Release")
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryTab(
    transactions: List<EscrowTransaction>,
    textPrimary: Color,
    textSecondary: Color,
    isWide: Boolean
) {
    Column {
        val historyTransactions = transactions.filter {
            it.status == EscrowStatus.COMPLETED || it.status == EscrowStatus.DISPUTED
        }

        if (historyTransactions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Rounded.History,
                        contentDescription = null,
                        tint = textSecondary,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "No transaction history",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = textSecondary
                        )
                    )
                }
            }
        } else {
            historyTransactions.forEach { transaction ->
                HistoryItem(
                    transaction = transaction,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary
                )
                HorizontalDivider(color = textSecondary.copy(alpha = 0.2f))
            }
        }
    }
}

@Composable
fun HistoryItem(
    transaction: EscrowTransaction,
    textPrimary: Color,
    textSecondary: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Status icon
        Icon(
            when (transaction.status) {
                EscrowStatus.COMPLETED -> Icons.Rounded.CheckCircle
                EscrowStatus.DISPUTED -> Icons.Rounded.Warning
                else -> Icons.Rounded.History
            },
            contentDescription = null,
            tint = when (transaction.status) {
                EscrowStatus.COMPLETED -> SuccessGreen
                EscrowStatus.DISPUTED -> errorLight
                else -> textSecondary
            },
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                transaction.title,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Medium,
                    color = textPrimary
                )
            )
            Text(
                "${transaction.date} • ${transaction.counterparty}",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = textSecondary
                )
            )
        }

        Text(
            transaction.amount,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Bold,
                color = textPrimary
            )
        )
    }
}

/* ────────────── ENHANCED KPI CARDS ────────────── */
@Composable
fun EnhancedKpiCardsSection(
    kpis: List<KPI>,
    primaryColor: Color,
    textPrimary: Color,
    textSecondary: Color,
    surfaceColor: Color,
    borderColor: Color,
    isWide: Boolean
) {
    Column {
        SectionHeader("Performance Overview", "Last 30 days", textPrimary, textSecondary, isWide)
        Spacer(modifier = Modifier.height(16.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                kpis.take(2).forEach { kpi ->
                    EnhancedKpiCard(
                        kpi = kpi,
                        primaryColor = primaryColor,
                        textPrimary = textPrimary,
                        textSecondary = textSecondary,
                        surfaceColor = surfaceColor,
                        borderColor = borderColor,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                kpis.drop(2).forEach { kpi ->
                    EnhancedKpiCard(
                        kpi = kpi,
                        primaryColor = primaryColor,
                        textPrimary = textPrimary,
                        textSecondary = textSecondary,
                        surfaceColor = surfaceColor,
                        borderColor = borderColor,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun EnhancedKpiCard(
    kpi: KPI,
    primaryColor: Color,
    textPrimary: Color,
    textSecondary: Color,
    surfaceColor: Color,
    borderColor: Color,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val isPositive = kpi.trend.startsWith("+")

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = surfaceColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 4.dp
        ),
        border = BorderStroke(1.dp, borderColor.copy(alpha = 0.3f)),
        modifier = modifier
            .fillMaxWidth()
            .height(140.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            surfaceColor,
                            surfaceColor.copy(alpha = 0.95f)
                        ),
                        start = Offset.Zero,
                        end = Offset.Infinite
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = primaryColor.copy(alpha = 0.12f),
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = kpi.icon,
                                contentDescription = null,
                                tint = primaryColor,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(30.dp),
                        color = if (isPositive)
                            colorScheme.primary.copy(alpha = 0.1f)
                        else
                            colorScheme.error.copy(alpha = 0.1f),
                        border = BorderStroke(
                            1.dp,
                            if (isPositive)
                                primaryColor.copy(alpha = 0.3f)
                            else
                                colorScheme.error.copy(alpha = 0.3f)
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = if (isPositive)
                                    Icons.Rounded.TrendingUp
                                else
                                    Icons.Rounded.TrendingDown,
                                contentDescription = null,
                                tint = if (isPositive) primaryColor else colorScheme.error,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = kpi.trend,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isPositive) primaryColor else colorScheme.error,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = kpi.value,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = textPrimary,
                            fontSize = 28.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = kpi.title,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = textSecondary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .align(Alignment.TopCenter)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                primaryColor.copy(alpha = 0.5f),
                                primaryColor,
                                primaryColor.copy(alpha = 0.5f)
                            )
                        )
                    )
            )
        }
    }
}

/* ────────────── KPI CARDS SECTION ────────────── */
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
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
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

/* ────────────── PROFESSIONAL ANALYTICS SECTION ────────────── */
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

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (isWide) 180.dp else 120.dp)
            ) {
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

// Business Management Section
@Composable
fun BusinessManagementSection(
    primaryColor: Color,
    textPrimary: Color,
    textSecondary: Color,
    borderColor: Color,
    onMyListingsClick: () -> Unit,
    listingsSummary: List<ListingSummary>,
    isWide: Boolean
) {
    val colorScheme = MaterialTheme.colorScheme

    Column {
        SectionHeader("Business", "${listingsSummary.count { it.status == "Active" }} active", textPrimary, textSecondary, isWide)
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
                    value = "${listingsSummary.size}",
                    color = primaryColor,
                    onClick = onMyListingsClick
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = borderColor)

                // Show top 3 listings
                listingsSummary.take(3).forEach { listing ->
                    ListingSummaryItem(
                        listing = listing,
                        primaryColor = primaryColor,
                        textPrimary = textPrimary,
                        textSecondary = textSecondary
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = borderColor)
                }

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
fun ListingSummaryItem(
    listing: ListingSummary,
    primaryColor: Color,
    textPrimary: Color,
    textSecondary: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(
                    when (listing.type) {
                        "Property" -> primaryColor.copy(alpha = 0.1f)
                        "Job" -> SuccessGreen.copy(alpha = 0.1f)
                        else -> tertiaryLight.copy(alpha = 0.1f)
                    },
                    RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                when (listing.type) {
                    "Property" -> Icons.Rounded.Home
                    "Job" -> Icons.Rounded.Work
                    else -> Icons.Rounded.Build
                },
                contentDescription = null,
                tint = when (listing.type) {
                    "Property" -> primaryColor
                    "Job" -> SuccessGreen
                    else -> tertiaryLight
                },
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                listing.title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "${listing.views} views",
                    fontSize = 11.sp,
                    color = textSecondary
                )
                Text(
                    "•",
                    fontSize = 11.sp,
                    color = textSecondary
                )
                Text(
                    "${listing.inquiries} inquiries",
                    fontSize = 11.sp,
                    color = textSecondary
                )
            }
        }

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (listing.status == "Active") SuccessGreen.copy(alpha = 0.1f) else WarningAmber.copy(alpha = 0.1f)
        ) {
            Text(
                listing.status,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                fontSize = 10.sp,
                color = if (listing.status == "Active") SuccessGreen else WarningAmber,
                fontWeight = FontWeight.Medium
            )
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
                    color = MaterialTheme.colorScheme.onSurface
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
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

// Recent Activity Section
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
            Activity("Booking confirmed", "Moving service", Icons.Outlined.Event, primaryColor),
            Activity("Payment received", "KES 12,500", Icons.Outlined.Payment, SuccessGreen),
            Activity("Review received", "★★★★★ 5 stars", Icons.Outlined.Star, tertiaryLight)
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
    isGuestMode: Boolean = false,
    userName: String = "Guest",
    fullName: String = "Guest",  // Add this parameter
    isLoggedIn: Boolean = false,
    userRole: String? = null,    // Add this parameter
    accountType: String? = null, // Add this parameter
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val collapsed = collapseFraction > 0.85f

    val maxFontSize = if (isWide) 40.sp else 32.sp
    val minFontSize = if (isWide) 28.sp else 22.sp

    val backgroundColor by animateColorAsState(
        targetValue = if (collapsed) primaryColor.copy(alpha = 0.95f) else Color.Transparent,
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
            // Background image (only for non-guest mode)
            if (!isGuestMode) {
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
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor)
            )

            // Gradient overlay (only for non-guest mode)
            if (!isGuestMode) {
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
                                        primaryColor.copy(0.9f),
                                        primaryColor.copy(0.6f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
            ) {
                // Header content (different for guest mode)
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
                                        .border(1.5.dp, primaryColor, CircleShape)
                                        .align(Alignment.BottomEnd)
                                )
                            }

                            Spacer(Modifier.width(8.dp))

                            Column {
                                Text(
                                    text = if (isGuestMode) "Hi, Guest" else "Hi, $userName",
                                    color = if (isGuestMode) primaryColor else Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = when {
                                        isGuestMode -> "Sign in for full access"
                                        userRole != null && accountType != null -> "$userRole • $accountType"
                                        userRole != null -> userRole
                                        accountType != null -> accountType
                                        isLoggedIn -> "Welcome back!"
                                        else -> "Welcome"
                                    },
                                    color = if (isGuestMode) primaryColor.copy(0.7f) else Color.White.copy(0.85f),
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            HeaderActionIconDashboard(
                                icon = Icons.Outlined.Mail,
                                iconTint = if (isGuestMode) primaryColor else Color.White,
                                backgroundTint = if (isGuestMode) primaryColor.copy(alpha = 0.1f) else Color.White.copy(alpha = 0.2f),
                                size = if (isWide) 44.dp else 36.dp
                            ) {}
                            HeaderActionIconDashboard(
                                icon = Icons.Outlined.Notifications,
                                iconTint = if (isGuestMode) primaryColor else Color.White,
                                backgroundTint = if (isGuestMode) primaryColor.copy(alpha = 0.1f) else Color.White.copy(alpha = 0.2f),
                                size = if (isWide) 44.dp else 36.dp
                            ) {}
                        }
                    }
                }

                if (!collapsed) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }

            // Collapsed header content
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
                            color = if (isGuestMode) primaryColor else Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = titleFontSize
                        )
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        HeaderActionIconDashboard(
                            icon = Icons.Outlined.Mail,
                            iconTint = if (isGuestMode) primaryColor else Color.White,
                            backgroundTint = if (isGuestMode) primaryColor.copy(alpha = 0.1f) else Color.White.copy(alpha = 0.2f),
                            size = 36.dp
                        ) {}
                        HeaderActionIconDashboard(
                            icon = Icons.Outlined.Notifications,
                            iconTint = if (isGuestMode) primaryColor else Color.White,
                            backgroundTint = if (isGuestMode) primaryColor.copy(alpha = 0.1f) else Color.White.copy(alpha = 0.2f),
                            size = 36.dp
                        ) {}
                    }
                }
            } else {
                // Expanded header content
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 16.dp, bottom = 24.dp)
                        .statusBarsPadding()
                ) {
                    Text(
                        text = "Dashboard",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = if (isGuestMode) primaryColor else Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = titleFontSize
                        )
                    )

                    Text(
                        text = if (isGuestMode) {
                            "Explore opportunities in your area"
                        } else {
                            "Welcome back, $fullName!"
                        },
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = if (isGuestMode) primaryColor.copy(0.8f) else Color.White.copy(0.9f),
                            fontSize = 14.sp
                        ),
                        modifier = Modifier.padding(top = 2.dp)
                    )

                    // Show role and account type in expanded header for logged-in users
                    if (!isGuestMode && (userRole != null || accountType != null)) {
                        Text(
                            text = listOfNotNull(userRole, accountType).joinToString(" • "),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (isGuestMode) primaryColor.copy(0.6f) else Color.White.copy(0.7f),
                                fontSize = 11.sp
                            ),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
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