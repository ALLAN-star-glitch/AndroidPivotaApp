package com.example.pivota.dashboard.presentation.screens

import HorizontalSmartMatchBadge
import android.annotation.SuppressLint
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.pivota.R
import com.example.pivota.auth.domain.model.User
import com.example.pivota.dashboard.domain.EmployerType
import com.example.pivota.dashboard.domain.ListingStatus
import com.example.pivota.dashboard.presentation.composables.ModernHousingCard
import com.example.pivota.dashboard.presentation.composables.ModernJobCard
import com.example.pivota.dashboard.presentation.composables.ModernProfessionalCard
import com.example.pivota.dashboard.presentation.state.HousingListingUiModel


// ========== END OF SMARTMATCH BADGE COMPOSABLES ==========

@SuppressLint("FrequentlyChangingValue")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverScreen(
    onNavigateToHouseListings: () -> Unit = {},
    onNavigateToJobListings: () -> Unit = {},
    onNavigateToAllJobs: () -> Unit = {},
    onNavigateToAllHousing: () -> Unit = {},
    onNavigateToAllProviders: () -> Unit = {},
    onNavigateToAllServices: () -> Unit = {},
    onNavigateToAllSupport: () -> Unit = {},
    onNavigateToSmartMatch: () -> Unit = {}, // Navigation for SmartMatch
    onBookHousingClick: (HousingListingUiModel) -> Unit = {},
    onViewHousingClick: (HousingListingUiModel) -> Unit = {},
    user: User? = null,
    isGuestMode: Boolean = false
) {
    val colorScheme = MaterialTheme.colorScheme

    // 🎯 Get window size class for responsive design
    val windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val isExpanded = windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.EXPANDED
    val isMedium = windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.MEDIUM
    val isTablet = isExpanded || isMedium

    // 🎨 Brand Palette - Using theme colors
    val primaryColor = colorScheme.primary
    val secondaryColor = colorScheme.secondary
    val tertiaryColor = colorScheme.tertiaryContainer
    val softBackground = colorScheme.background

    val listState = rememberLazyListState()

    // State for search
    var searchQuery by remember { mutableStateOf("") }
    var isRecording by remember { mutableStateOf(false) }

    // State for selected filters
    var selectedFilters by remember { mutableStateOf(setOf<String>()) }

    // Responsive card width based on window size class
    val cardWidth = when {
        isExpanded -> 360.dp
        isMedium -> 320.dp
        else -> 280.dp
    }

    // Responsive horizontal padding
    val horizontalPadding = when {
        isExpanded -> 32.dp
        isMedium -> 24.dp
        else -> 16.dp
    }

    // 📏 Header sizes - slightly larger on tablets
    val maxHeight = if (isTablet) 260.dp else 220.dp
    val minHeight = if (isTablet) 100.dp else 90.dp

    val density = LocalDensity.current
    val collapseRangePx = with(density) {
        (maxHeight - minHeight).toPx()
    }

    // 🔥 Correct collapse logic
    val scrollY = when (listState.firstVisibleItemIndex) {
        0 -> listState.firstVisibleItemScrollOffset.toFloat()
        else -> collapseRangePx
    }

    val collapseFraction =
        (scrollY / collapseRangePx).coerceIn(0f, 1f)

    val animatedHeight =
        lerp(maxHeight, minHeight, collapseFraction)

    // Track if we're past the threshold to show shadow
    val isPastThreshold = remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 100
        }
    }

    // Watch for filter selection to trigger navigation
    LaunchedEffect(selectedFilters) {
        when {
            selectedFilters.contains("Houses") -> {
                onNavigateToHouseListings()
                selectedFilters = selectedFilters - "Houses"
            }
            selectedFilters.contains("Jobs") -> {
                onNavigateToJobListings()
                selectedFilters = selectedFilters - "Jobs"
            }
        }
    }

    Scaffold(
        containerColor = softBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
        ) {
            // 📜 SCROLL CONTENT
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    top = maxHeight + 130.dp
                ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 🤖 SmartMatch Highlight Section - Using the version with isTablet
                item {
                    HorizontalSmartMatchBadge(
                        matchCount = 156,
                        isTablet = isTablet,
                        onClick = onNavigateToSmartMatch,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = horizontalPadding, vertical = 8.dp)
                    )
                }

                // 📋 1. Jobs Near You
                item {
                    ModernSectionHeader(
                        title = "Jobs Near You",
                        actionText = "View all jobs →",
                        onActionClick = onNavigateToAllJobs,
                        horizontalPadding = horizontalPadding
                    )
                }
                item {
                    ModernHorizontalList(
                        items = listOf(
                            {
                                ModernJobCard(
                                    title = "Construction Foreman",
                                    company = "BuildWell Ltd",
                                    location = "Upper Hill",
                                    salary = "KSh 3,500/day",
                                    type = "Contract",
                                    description = "Experienced foreman needed for high-rise construction project.",
                                    isVerified = true,
                                    employerType = EmployerType.ORGANIZATION,
                                    profileImageRes = R.drawable.job_placeholder3,
                                    onViewClick = { /* Navigate to job details */ },
                                    onApplyClick = { /* Handle apply */ },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            },
                            {
                                ModernJobCard(
                                    title = "Junior Accountant",
                                    company = "FinCorp",
                                    location = "Westlands",
                                    salary = "KSh 55,000/month",
                                    type = "Full-time",
                                    description = "CPA qualified accountant with 2+ years experience.",
                                    isVerified = true,
                                    employerType = EmployerType.ORGANIZATION,
                                    profileImageRes = R.drawable.job_placeholder2,
                                    onViewClick = { /* Navigate to job details */ },
                                    onApplyClick = { /* Handle apply */ },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            },
                            {
                                ModernJobCard(
                                    title = "Store Keeper",
                                    company = "Retail Solutions",
                                    location = "Mombasa Rd",
                                    salary = "KSh 25,000/month",
                                    type = "Full-time",
                                    description = "Inventory management and stock coordination.",
                                    isVerified = false,
                                    employerType = EmployerType.ORGANIZATION,
                                    profileImageRes = R.drawable.job_placeholder4,
                                    onViewClick = { /* Navigate to job details */ },
                                    onApplyClick = { /* Handle apply */ },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            },
                            {
                                ModernJobCard(
                                    title = "Solar Installer",
                                    company = "Green Energy",
                                    location = "Karen",
                                    salary = "KSh 40,000/month",
                                    type = "Contract",
                                    description = "Solar panel installation and maintenance.",
                                    isVerified = true,
                                    employerType = EmployerType.ORGANIZATION,
                                    profileImageRes = R.drawable.job_placeholder5,
                                    onViewClick = { /* Navigate to job details */ },
                                    onApplyClick = { /* Handle apply */ },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        ),
                        cardWidth = cardWidth,
                        horizontalPadding = horizontalPadding
                    )
                }

                // 📋 2. Housing Opportunities
                item {
                    ModernSectionHeader(
                        title = "Housing Opportunities",
                        actionText = "Browse all →",
                        onActionClick = onNavigateToAllHousing,
                        horizontalPadding = horizontalPadding
                    )
                }
                item {
                    ModernHorizontalList(
                        items = listOf(
                            {
                                ModernHousingCard(
                                    price = "KSh 22,000",
                                    title = "Modern Bedsitter",
                                    location = "Ruiru",
                                    type = "Apartment",
                                    rating = 4.5,
                                    isVerified = true,
                                    description = "Self-contained bedsitter with parking",
                                    isForSale = false,
                                    imageRes = R.drawable.property_placeholder1,
                                    bedrooms = 1,
                                    bathrooms = 1,
                                    squareMeters = 70,
                                    onViewClick = {
                                        val listing = HousingListingUiModel(
                                            id = "1",
                                            title = "Modern Bedsitter",
                                            price = "KSh 22,000",
                                            location = "Ruiru",
                                            propertyType = "Apartment",
                                            description = "Self-contained bedsitter with parking",
                                            isVerified = true,
                                            isForSale = false,
                                            rating = 4.5,
                                            bedrooms = 1,
                                            bathrooms = 1,
                                            squareMeters = 70,
                                            imageRes = R.drawable.property_placeholder1,
                                            status = ListingStatus.AVAILABLE,
                                            views = 0,
                                            messages = 0,
                                            requests = 0
                                        )
                                        onViewHousingClick(listing)
                                    },
                                    onBookClick = {
                                        val listing = HousingListingUiModel(
                                            id = "1",
                                            title = "Modern Bedsitter",
                                            price = "KSh 22,000",
                                            location = "Ruiru",
                                            propertyType = "Apartment",
                                            description = "Self-contained bedsitter with parking",
                                            isVerified = true,
                                            isForSale = false,
                                            rating = 4.5,
                                            bedrooms = 1,
                                            bathrooms = 1,
                                            squareMeters = 70,
                                            imageRes = R.drawable.property_placeholder1,
                                            status = ListingStatus.AVAILABLE,
                                            views = 0,
                                            messages = 0,
                                            requests = 0
                                        )
                                        onBookHousingClick(listing)
                                    },
                                    onClick = { /* Handle click */ },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            },
                            {
                                ModernHousingCard(
                                    price = "KSh 45,000",
                                    title = "2 Bedroom Apartment",
                                    location = "Syokimau",
                                    type = "Apartment",
                                    rating = 4.8,
                                    isVerified = true,
                                    description = "Spacious 2BR with balcony",
                                    isForSale = false,
                                    imageRes = R.drawable.property_placeholder2,
                                    bedrooms = 2,
                                    bathrooms = 2,
                                    squareMeters = 85,
                                    onViewClick = {
                                        val listing = HousingListingUiModel(
                                            id = "2",
                                            title = "2 Bedroom Apartment",
                                            price = "KSh 45,000",
                                            location = "Syokimau",
                                            propertyType = "Apartment",
                                            description = "Spacious 2BR with balcony",
                                            isVerified = true,
                                            isForSale = false,
                                            rating = 4.8,
                                            bedrooms = 2,
                                            bathrooms = 2,
                                            squareMeters = 85,
                                            imageRes = R.drawable.property_placeholder2,
                                            status = ListingStatus.AVAILABLE,
                                            views = 0,
                                            messages = 0,
                                            requests = 0
                                        )
                                        onViewHousingClick(listing)
                                    },
                                    onBookClick = {
                                        val listing = HousingListingUiModel(
                                            id = "2",
                                            title = "2 Bedroom Apartment",
                                            price = "KSh 45,000",
                                            location = "Syokimau",
                                            propertyType = "Apartment",
                                            description = "Spacious 2BR with balcony",
                                            isVerified = true,
                                            isForSale = false,
                                            rating = 4.8,
                                            bedrooms = 2,
                                            bathrooms = 2,
                                            squareMeters = 85,
                                            imageRes = R.drawable.property_placeholder2,
                                            status = ListingStatus.AVAILABLE,
                                            views = 0,
                                            messages = 0,
                                            requests = 0
                                        )
                                        onBookHousingClick(listing)
                                    },
                                    onClick = {},
                                    modifier = Modifier.fillMaxWidth()
                                )
                            },
                            {
                                ModernHousingCard(
                                    price = "KSh 4.5M",
                                    title = "Luxury Villa",
                                    location = "Karen",
                                    type = "House",
                                    rating = 4.9,
                                    isVerified = true,
                                    description = "4BR villa with garden and pool",
                                    isForSale = true,
                                    imageRes = R.drawable.property_placeholder3,
                                    bedrooms = 4,
                                    bathrooms = 4,
                                    squareMeters = 350,
                                    onViewClick = {
                                        val listing = HousingListingUiModel(
                                            id = "3",
                                            title = "Luxury Villa",
                                            price = "KSh 4.5M",
                                            location = "Karen",
                                            propertyType = "House",
                                            description = "4BR villa with garden and pool",
                                            isVerified = true,
                                            isForSale = true,
                                            rating = 4.9,
                                            bedrooms = 4,
                                            bathrooms = 4,
                                            squareMeters = 350,
                                            imageRes = R.drawable.property_placeholder3,
                                            status = ListingStatus.AVAILABLE,
                                            views = 0,
                                            messages = 0,
                                            requests = 0
                                        )
                                        onViewHousingClick(listing)
                                    },
                                    onBookClick = {
                                        val listing = HousingListingUiModel(
                                            id = "3",
                                            title = "Luxury Villa",
                                            price = "KSh 4.5M",
                                            location = "Karen",
                                            propertyType = "House",
                                            description = "4BR villa with garden and pool",
                                            isVerified = true,
                                            isForSale = true,
                                            rating = 4.9,
                                            bedrooms = 4,
                                            bathrooms = 4,
                                            squareMeters = 350,
                                            imageRes = R.drawable.property_placeholder3,
                                            status = ListingStatus.AVAILABLE,
                                            views = 0,
                                            messages = 0,
                                            requests = 0
                                        )
                                        onBookHousingClick(listing)
                                    },
                                    onClick = {},
                                    modifier = Modifier.fillMaxWidth()
                                )
                            },
                            {
                                ModernHousingCard(
                                    price = "KSh 35,000",
                                    title = "Studio Apartment",
                                    location = "Kilimani",
                                    type = "Studio",
                                    rating = 4.3,
                                    isVerified = true,
                                    description = "Modern studio near Yaya Centre",
                                    isForSale = false,
                                    imageRes = R.drawable.property_placeholder4,
                                    bedrooms = 1,
                                    bathrooms = 1,
                                    squareMeters = 45,
                                    onViewClick = {
                                        val listing = HousingListingUiModel(
                                            id = "4",
                                            title = "Studio Apartment",
                                            price = "KSh 35,000",
                                            location = "Kilimani",
                                            propertyType = "Studio",
                                            description = "Modern studio near Yaya Centre",
                                            isVerified = true,
                                            isForSale = false,
                                            rating = 4.3,
                                            bedrooms = 1,
                                            bathrooms = 1,
                                            squareMeters = 45,
                                            imageRes = R.drawable.property_placeholder4,
                                            status = ListingStatus.AVAILABLE,
                                            views = 0,
                                            messages = 0,
                                            requests = 0
                                        )
                                        onViewHousingClick(listing)
                                    },
                                    onBookClick = {
                                        val listing = HousingListingUiModel(
                                            id = "4",
                                            title = "Studio Apartment",
                                            price = "KSh 35,000",
                                            location = "Kilimani",
                                            propertyType = "Studio",
                                            description = "Modern studio near Yaya Centre",
                                            isVerified = true,
                                            isForSale = false,
                                            rating = 4.3,
                                            bedrooms = 1,
                                            bathrooms = 1,
                                            squareMeters = 45,
                                            imageRes = R.drawable.property_placeholder4,
                                            status = ListingStatus.AVAILABLE,
                                            views = 0,
                                            messages = 0,
                                            requests = 0
                                        )
                                        onBookHousingClick(listing)
                                    },
                                    onClick = {},
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        ),
                        cardWidth = cardWidth,
                        horizontalPadding = horizontalPadding
                    )
                }

                // 🛠️ 3. Trusted Service Providers
                item {
                    ModernSectionHeader(
                        title = "Trusted Professionals",
                        actionText = "See all →",
                        onActionClick = onNavigateToAllProviders,
                        horizontalPadding = horizontalPadding
                    )
                }
                item {
                    ModernHorizontalList(
                        items = listOf(
                            {
                                ModernProfessionalCard(
                                    name = "QuickMovers Kenya",
                                    specialty = "Moving & Logistics",
                                    rating = 4.9f,
                                    jobs = 342,
                                    isVerified = true,
                                    description = "Professional moving services across Nairobi",
                                    onCardClick = { /* Navigate to provider details */ },
                                    onViewClick = { /* Handle view */ },
                                    onHireClick = { /* Handle hire */ },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            },
                            {
                                ModernProfessionalCard(
                                    name = "Fundi Digital",
                                    specialty = "Electrical & Plumbing",
                                    rating = 4.7f,
                                    jobs = 256,
                                    isVerified = true,
                                    description = "24/7 emergency electrical services",
                                    onCardClick = { /* Navigate to provider details */ },
                                    onViewClick = { /* Handle view */ },
                                    onHireClick = { /* Handle hire */ },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            },
                            {
                                ModernProfessionalCard(
                                    name = "CleanPro Services",
                                    specialty = "Cleaning & Maintenance",
                                    rating = 4.8f,
                                    jobs = 189,
                                    isVerified = true,
                                    description = "Professional cleaning for homes and offices",
                                    onCardClick = { /* Navigate to provider details */ },
                                    onViewClick = { /* Handle view */ },
                                    onHireClick = { /* Handle hire */ },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            },
                            {
                                ModernProfessionalCard(
                                    name = "SolarTech",
                                    specialty = "Solar Installation",
                                    rating = 4.6f,
                                    jobs = 112,
                                    isVerified = false,
                                    description = "Solar panel installation and maintenance",
                                    onCardClick = { /* Navigate to provider details */ },
                                    onViewClick = { /* Handle view */ },
                                    onHireClick = { /* Handle hire */ },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        ),
                        cardWidth = cardWidth,
                        horizontalPadding = horizontalPadding
                    )
                }

                // ⚡ 4. Quick Services Grid
                item {
                    ModernSectionHeader(
                        title = "Common Services",
                        actionText = "Browse categories →",
                        onActionClick = onNavigateToAllServices,
                        horizontalPadding = horizontalPadding
                    )
                }
                item {
                    ModernServiceGrid(
                        colorScheme = colorScheme,
                        horizontalPadding = horizontalPadding,
                        isTablet = isTablet
                    )
                }

                // 🤝 5. Social Support
                item {
                    ModernSectionHeader(
                        title = "Social Support & Services",
                        actionText = "Get help →",
                        onActionClick = onNavigateToAllSupport,
                        horizontalPadding = horizontalPadding
                    )
                }
                items(3) { index ->
                    val supports = listOf(
                        Triple("Red Cross Kenya", "Emergency Relief & Disaster Response", "Nationwide"),
                        Triple("Legal Aid Kenya", "Free Legal Advice & Representation", "Regional Offices"),
                        Triple("Food for All", "Community Food Programs", "Nairobi & Kiambu")
                    )
                    ModernSupportCard(
                        name = supports[index].first,
                        service = supports[index].second,
                        location = supports[index].third,
                        isUrgent = index == 0,
                        colorScheme = colorScheme,
                        horizontalPadding = horizontalPadding
                    )
                }

                item { Spacer(modifier = Modifier.height(100.dp)) }
            }

            // 🏆 FIXED HEADER
            DiscoverHeroHeader(
                teal = primaryColor,
                gold = tertiaryColor,
                height = animatedHeight,
                collapseFraction = collapseFraction,
                colorScheme = colorScheme,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .zIndex(3f),
                user = user,  // Pass the user
                isGuestMode = isGuestMode  // Pass guest mode
            )

            // 📌 FIXED SEARCH + PILLS SECTION
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(top = animatedHeight)
                    .shadow(
                        elevation = if (isPastThreshold.value) 8.dp else 0.dp,
                        shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
                        ambientColor = Color.Black.copy(0.08f)
                    )
                    .zIndex(2f),
                shape = RoundedCornerShape(
                    topStart = 0.dp,
                    topEnd = 0.dp,
                    bottomStart = 20.dp,
                    bottomEnd = 20.dp
                ),
                color = colorScheme.surface,
                tonalElevation = if (isPastThreshold.value) 4.dp else 0.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = horizontalPadding, vertical = 12.dp)
                ) {
                    // Search Bar with Audio Icon
                    SearchBarWithAudio(
                        query = searchQuery,
                        onQueryChange = { searchQuery = it },
                        onAudioClick = {
                            isRecording = !isRecording
                        },
                        isRecording = isRecording,
                        accentColor = primaryColor,
                        colorScheme = colorScheme
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Filter Pills with SmartMatch badge at the beginning
                    FilterPillsRow(
                        selectedFilters = selectedFilters,
                        onFilterSelected = { filter ->
                            selectedFilters = if (selectedFilters.contains(filter)) {
                                selectedFilters - filter
                            } else {
                                selectedFilters + filter
                            }
                        },
                        accentColor = primaryColor,
                        colorScheme = colorScheme,
                        isTablet = isTablet, // Pass isTablet instead of windowSizeClass
                        onSmartMatchClick = onNavigateToSmartMatch
                    )
                }
            }
        }
    }
}

/* ─────────────────────────────────────────────
   FILTER PILLS ROW (SmartMatch badge removed)
   ───────────────────────────────────────────── */

@Composable
fun FilterPillsRow(
    selectedFilters: Set<String>,
    onFilterSelected: (String) -> Unit,
    accentColor: Color,
    colorScheme: ColorScheme,
    isTablet: Boolean,
    onSmartMatchClick: () -> Unit // Keep parameter but don't use it
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        // SmartMatch badge REMOVED from here

        val filters = listOf(
            "All" to null,
            "Jobs" to Icons.Outlined.Work,
            "Houses" to Icons.Outlined.Home,
            "Professionals" to Icons.Outlined.Build,
            "Support" to Icons.Outlined.VolunteerActivism,
            "Verified" to Icons.Outlined.Verified
        )

        items(filters.size) { index ->
            val (filter, icon) = filters[index]
            val isSelected = selectedFilters.contains(filter) || (filter == "All" && selectedFilters.isEmpty())

            Surface(
                shape = RoundedCornerShape(30.dp),
                color = if (isSelected) accentColor else colorScheme.surface,
                border = if (!isSelected) BorderStroke(1.dp, colorScheme.outlineVariant) else null,
                modifier = Modifier
                    .clickable { onFilterSelected(filter) }
                    .shadow(
                        elevation = if (isSelected) 2.dp else 0.dp,
                        shape = RoundedCornerShape(30.dp),
                        ambientColor = Color.Black.copy(0.05f)
                    )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (icon != null) {
                        Icon(
                            icon,
                            contentDescription = null,
                            tint = if (isSelected) colorScheme.onPrimary else colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    Text(
                        text = filter,
                        color = if (isSelected) colorScheme.onPrimary else colorScheme.onSurfaceVariant,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

/* ─────────────────────────────────────────────
   SEARCH BAR WITH AUDIO ICON (SmartMatch badge removed)
   ───────────────────────────────────────────── */

@Composable
fun SearchBarWithAudio(
    query: String,
    onQueryChange: (String) -> Unit,
    onAudioClick: () -> Unit,
    isRecording: Boolean,
    accentColor: Color,
    colorScheme: ColorScheme
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Black.copy(0.05f)
            ),
        shape = RoundedCornerShape(16.dp),
        color = colorScheme.surface,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Outlined.Search,
                contentDescription = null,
                tint = colorScheme.onSurfaceVariant.copy(0.6f),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))

            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.weight(1f),
                decorationBox = { innerTextField ->
                    Box {
                        if (query.isEmpty()) {
                            Text(
                                "Search...",
                                color = colorScheme.onSurfaceVariant.copy(0.5f),
                                fontSize = 14.sp
                            )
                        }
                        innerTextField()
                    }
                },
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 14.sp,
                    color = colorScheme.onSurface
                )
            )

            if (query.isNotEmpty()) {
                IconButton(
                    onClick = { onQueryChange("") },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Outlined.Close,
                        contentDescription = "Clear",
                        tint = colorScheme.onSurfaceVariant.copy(0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            } else {
                // Audio Icon only - SmartMatch badge removed
                IconButton(
                    onClick = onAudioClick,
                    modifier = Modifier
                        .size(40.dp)
                        .then(
                            if (isRecording) {
                                Modifier.background(
                                    color = accentColor.copy(0.1f),
                                    shape = CircleShape
                                )
                            } else {
                                Modifier
                            }
                        )
                ) {
                    Icon(
                        imageVector = if (isRecording)
                            Icons.Filled.Mic
                        else
                            Icons.Outlined.Mic,
                        contentDescription = if (isRecording) "Stop recording" else "Start voice search",
                        tint = if (isRecording) accentColor else colorScheme.onSurfaceVariant.copy(0.6f),
                        modifier = Modifier.size(20.dp)
                    )
                }

                if (isRecording) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                color = colorScheme.error,
                                shape = CircleShape
                            )
                    )
                }
            }
        }
    }
}


/* ────────────── UPDATED COMPONENTS WITH HORIZONTAL PADDING ────────────── */

@Composable
fun ModernSectionHeader(
    title: String,
    actionText: String,
    onActionClick: () -> Unit = {},
    horizontalPadding: Dp = 16.dp
) {
    val colorScheme = MaterialTheme.colorScheme

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = horizontalPadding, end = horizontalPadding, top = 24.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = colorScheme.onSurface,
                fontSize = 16.sp,
                letterSpacing = 0.5.sp
            )
        )
        Text(
            text = actionText,
            color = colorScheme.primary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.clickable { onActionClick() }
        )
    }
}

@Composable
fun ModernHorizontalList(
    items: List<@Composable () -> Unit>,
    cardWidth: Dp,
    horizontalPadding: Dp = 16.dp
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items.size) { index ->
            Box(
                modifier = Modifier
                    .width(cardWidth)
                    .animateItem()
            ) {
                items[index]()
            }
        }
    }
}

@Composable
fun ModernSupportCard(
    name: String,
    service: String,
    location: String,
    isUrgent: Boolean,
    colorScheme: ColorScheme,
    horizontalPadding: Dp = 16.dp
) {
    val primaryColor = colorScheme.primary
    val tertiaryColor = colorScheme.tertiary

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding, vertical = 6.dp)
            .clickable { /* View details */ },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp,
            pressedElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        if (isUrgent) tertiaryColor.copy(0.1f) else primaryColor.copy(0.08f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isUrgent) Icons.Outlined.Emergency else Icons.Outlined.VolunteerActivism,
                    contentDescription = null,
                    tint = if (isUrgent) tertiaryColor else primaryColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = name,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = colorScheme.onSurface,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = service,
                    fontSize = 13.sp,
                    color = colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 2.dp)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(
                        Icons.Outlined.LocationOn,
                        contentDescription = null,
                        tint = colorScheme.onSurfaceVariant.copy(0.5f),
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = location,
                        fontSize = 11.sp,
                        color = colorScheme.onSurfaceVariant.copy(0.7f),
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(start = 2.dp)
                    )
                }
            }

            // Action
            Icon(
                Icons.Outlined.ChevronRight,
                contentDescription = "View",
                tint = colorScheme.onSurfaceVariant.copy(0.5f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun ModernServiceGrid(
    colorScheme: ColorScheme,
    horizontalPadding: Dp = 16.dp,
    isTablet: Boolean = false
) {
    val services = listOf(
        "Movers" to Icons.Outlined.LocalShipping,
        "Plumbers" to Icons.Outlined.Plumbing,
        "Electricians" to Icons.Outlined.Bolt,
        "Cleaners" to Icons.Outlined.CleaningServices,
        "Trainers" to Icons.Outlined.FitnessCenter,
        "Counselors" to Icons.Outlined.Psychology,
        "Security" to Icons.Outlined.Security,
        "Painters" to Icons.Outlined.FormatPaint
    )

    // Adjust grid columns based on tablet
    val columnsPerRow = if (isTablet) 6 else 4

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding)
    ) {
        services.chunked(columnsPerRow).forEach { rowItems ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowItems.forEach { (name, icon) ->
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { /* Navigate to service */ },
                        shape = RoundedCornerShape(12.dp),
                        color = colorScheme.surface,
                        border = BorderStroke(1.dp, colorScheme.outlineVariant),
                        shadowElevation = 0.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                icon,
                                contentDescription = null,
                                tint = colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = name,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = colorScheme.onSurface,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DiscoverHeroHeader(
    teal: Color,
    gold: Color,
    height: Dp,
    collapseFraction: Float,
    colorScheme: ColorScheme,
    modifier: Modifier = Modifier,
    user: User? = null,  // Add this parameter
    isGuestMode: Boolean = false  // Add this parameter
) {
    val collapsed = collapseFraction > 0.85f

    val maxFontSize = 34.sp
    val minFontSize = 24.sp

    // Extract user name for display
    val displayName = remember(user) {
        when {
            user == null || isGuestMode -> "Guest"
            user.userName.isNotBlank() -> user.userName.split(" ").firstOrNull() ?: "Guest"
            user.firstName.isNotBlank() -> user.firstName
            else -> user.email.split("@").firstOrNull() ?: "Guest"
        }
    }

    val welcomeMessage = remember(user, isGuestMode) {
        when {
            isGuestMode -> "Welcome to Pivota"
            user?.userName?.isNotBlank() == true -> "Welcome back, ${user.userName.split(" ")
                .firstOrNull() ?: "User"}!"
            user?.firstName?.isNotBlank() == true -> "Welcome back, ${user.firstName}!"
            else -> "Welcome to Pivota"
        }
    }

    val backgroundColor by animateColorAsState(
        targetValue = if (collapsed) colorScheme.primary.copy(alpha = 0.95f) else Color.Transparent,
        animationSpec = tween(durationMillis = 300)
    )

    val discoverFontSize = ((maxFontSize.value - collapseFraction * (maxFontSize.value - minFontSize.value))).sp

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
            AnimatedVisibility(
                visible = !collapsed,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(R.drawable.nairobi_city)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    placeholder = painterResource(R.drawable.nairobi_city),
                    error = painterResource(R.drawable.nairobi_city)
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor)
            )

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
                                    colorScheme.primary.copy(0.95f),
                                    colorScheme.primary.copy(0.75f),
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box {
                                HeaderAvatar(colorScheme)
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .background(colorScheme.tertiaryContainer, CircleShape)
                                        .border(2.dp, colorScheme.primary, CircleShape)
                                        .align(Alignment.BottomEnd)
                                )
                            }

                            Spacer(Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = if (isGuestMode) "Hi, Guest" else "Hi, $displayName",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = welcomeMessage,
                                    color = Color.White.copy(0.85f),
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            HeaderActionIcon(
                                icon = Icons.Default.Mail,
                                iconTint = Color.White,
                                backgroundTint = Color.White.copy(alpha = 0.2f)
                            ) {}
                            HeaderActionIcon(
                                icon = Icons.Default.Notifications,
                                iconTint = Color.White,
                                backgroundTint = Color.White.copy(alpha = 0.2f)
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
                        .padding(start = 20.dp, end = 20.dp)
                        .offset(y = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Discover",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-1.5).sp,
                            fontSize = discoverFontSize
                        )
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        HeaderActionIcon(
                            icon = Icons.Default.Mail,
                            iconTint = Color.White,
                            backgroundTint = Color.White.copy(alpha = 0.2f)
                        ) {}
                        HeaderActionIcon(
                            icon = Icons.Default.Notifications,
                            iconTint = Color.White,
                            backgroundTint = Color.White.copy(alpha = 0.2f)
                        ) {}
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 20.dp, bottom = 32.dp)
                        .statusBarsPadding()
                ) {
                    Text(
                        text = "Discover",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-1.5).sp,
                            fontSize = discoverFontSize
                        )
                    )

                    Text(
                        text = if (isGuestMode) {
                            "Life opportunities, tailored for you"
                        } else {
                            "Find opportunities ${if (displayName != "Guest") "for $displayName" else "for you"}"
                        },
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
fun HeaderActionIcon(
    icon: ImageVector,
    iconTint: Color = Color.White,
    backgroundTint: Color = Color.White.copy(alpha = 0.2f),
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(48.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(backgroundTint, CircleShape)
                .clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun HeaderAvatar(colorScheme: ColorScheme) {
    Box(
        modifier = Modifier
            .size(45.dp)
            .background(Color.White.copy(0.2f), CircleShape)
            .border(1.5.dp, Color.White.copy(0.5f), CircleShape)
            .clip(CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.PersonOutline,
            contentDescription = "User Avatar",
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}