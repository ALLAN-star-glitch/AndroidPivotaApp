package com.example.pivota.dashboard.presentation.screens

import android.annotation.SuppressLint
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.pivota.R
import kotlinx.coroutines.delay

// Modern, elegant color palette - teal used sparingly as accent
val DeepNavyProviders = Color(0xFF0A1A2F)      // Professional dark blue
val WarmGraySmartProviders = Color(0xFFF8F9FA)      // Soft background
val SlateGrayProviders = Color(0xFF4A5568)     // Secondary text
val SoftGoldProviders = Color(0xFFD4AF37)      // Premium accent
val ForestGreenProviders = Color(0xFF2E7D32)   // Success/Verified
val CleanWhiteProviders = Color(0xFFFFFFFF)    // Pure white
val LightBorderProviders = Color(0xFFE2E8F0)   // Subtle borders
val MutedTealProviders = Color(0xFF2C6E6E)     // Muted teal for accents

// Enhanced data model
data class EnhancedProviderData(
    val id: String,
    val name: String,
    val businessName: String?,
    val profileImageUrl: String?,
    val category: String,
    val specialties: List<String>,
    val rating: Double,
    val reviewCount: Int,
    val description: String,
    val experienceYears: Int,
    val location: String,
    val serviceRadius: String,
    val startingPrice: Int,
    val isVerified: Boolean,
    val isSmartMatch: Boolean,
    val responseTime: String,
    val availability: List<String>,
    val completedJobs: Int
)

// Filter state
data class FilterState(
    val selectedCategories: Set<String> = emptySet(),
    val minRating: Double = 0.0,
    val maxPrice: Int? = null,
    val selectedLocations: Set<String> = emptySet(),
    val isVerifiedOnly: Boolean = false,
    val isAvailableToday: Boolean = false,
    val minExperience: Int = 0
)

@SuppressLint("FrequentlyChangingValue")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProvidersScreen() {
    // 🎨 Brand Palette - Using teal as accent
    val primaryTeal = MutedTealProviders
    val goldenAccent = SoftGoldProviders
    val softBackground = WarmGraySmartProviders

    val listState = rememberLazyListState()

    // 📏 Header sizes
    val maxHeight = 220.dp
    val minHeight = 90.dp

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

    // Track if we're past the threshold to show shadow
    val isPastThreshold = remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 100
        }
    }

    // State for filters and search
    var searchQuery by remember { mutableStateOf("") }
    var showFilterModal by remember { mutableStateOf(false) }
    var filterState by remember { mutableStateOf(FilterState()) }
    var activeFilterCount by remember { mutableStateOf(0) }
    var isSearching by remember { mutableStateOf(false) }

    // State for audio recording
    var isRecording by remember { mutableStateOf(false) }

    // Debounce search to avoid too many updates
    val debouncedQuery = remember { mutableStateOf("") }
    LaunchedEffect(searchQuery) {
        if (searchQuery.length >= 2) {
            isSearching = true
            delay(300) // Debounce for 300ms
            debouncedQuery.value = searchQuery.lowercase()
            isSearching = false
        } else if (searchQuery.isEmpty()) {
            debouncedQuery.value = ""
            isSearching = false
        }
    }

    // Enhanced providers data
    val allProviders = remember {
        listOf(
            EnhancedProviderData(
                id = "1",
                name = "Musa Jallow",
                businessName = "Musa Electrical Services",
                profileImageUrl = null,
                category = "Electrician",
                specialties = listOf("Solar Installation", "Wiring", "Emergency Repairs"),
                rating = 4.8,
                reviewCount = 32,
                description = "Licensed electrician with expertise in solar panel installation and emergency electrical repairs.",
                experienceYears = 8,
                location = "Westlands, Nairobi",
                serviceRadius = "15 km",
                startingPrice = 1500,
                isVerified = true,
                isSmartMatch = true,
                responseTime = "< 10 mins",
                availability = listOf("Today", "Weekends"),
                completedJobs = 156
            ),
            EnhancedProviderData(
                id = "2",
                name = "Sarah Wanjiku",
                businessName = "Sarah Interior Designs",
                profileImageUrl = null,
                category = "Interior Designer",
                specialties = listOf("Space Planning", "Furniture Selection", "Color Consulting"),
                rating = 4.9,
                reviewCount = 47,
                description = "Award-winning interior designer specializing in modern African aesthetics.",
                experienceYears = 6,
                location = "Kilimani, Nairobi",
                serviceRadius = "10 km",
                startingPrice = 2500,
                isVerified = true,
                isSmartMatch = false,
                responseTime = "< 30 mins",
                availability = listOf("Weekdays", "Weekends"),
                completedJobs = 89
            ),
            EnhancedProviderData(
                id = "3",
                name = "James Omondi",
                businessName = "Pipemasters Plumbing",
                profileImageUrl = null,
                category = "Plumber",
                specialties = listOf("Pipe Repair", "Water Heaters", "Bathroom Installation"),
                rating = 4.7,
                reviewCount = 28,
                description = "Professional plumber with 5+ years experience in residential and commercial plumbing.",
                experienceYears = 5,
                location = "Kilimani, Nairobi",
                serviceRadius = "20 km",
                startingPrice = 1200,
                isVerified = true,
                isSmartMatch = true,
                responseTime = "< 15 mins",
                availability = listOf("Today", "24/7"),
                completedJobs = 203
            ),
            EnhancedProviderData(
                id = "4",
                name = "Pivota Housing Ltd",
                businessName = null,
                profileImageUrl = null,
                category = "Property Management",
                specialties = listOf("Maintenance", "Tenant Management", "Inspections"),
                rating = 4.6,
                reviewCount = 89,
                description = "Full-service property management company serving Mombasa and surrounding areas.",
                experienceYears = 10,
                location = "Mombasa",
                serviceRadius = "30 km",
                startingPrice = 5000,
                isVerified = true,
                isSmartMatch = true,
                responseTime = "< 1 hour",
                availability = listOf("Weekdays"),
                completedJobs = 412
            ),
            EnhancedProviderData(
                id = "5",
                name = "Legal Aid Kenya",
                businessName = null,
                profileImageUrl = null,
                category = "Legal Services",
                specialties = listOf("Family Law", "Land Disputes", "Documentation"),
                rating = 4.9,
                reviewCount = 112,
                description = "NGO providing free legal services to underserved communities.",
                experienceYears = 15,
                location = "Nakuru",
                serviceRadius = "50 km",
                startingPrice = 0,
                isVerified = true,
                isSmartMatch = false,
                responseTime = "< 2 hours",
                availability = listOf("Weekdays", "Weekends"),
                completedJobs = 678
            )
        )
    }

    // Filter providers based on filter state
    val filteredProviders = remember(debouncedQuery.value, filterState) {
        allProviders.filter { provider ->
            var matches = true

            // Apply search filter
            if (debouncedQuery.value.isNotEmpty()) {
                matches = matches && (
                        provider.name.lowercase().contains(debouncedQuery.value) ||
                                provider.businessName?.lowercase()?.contains(debouncedQuery.value) == true ||
                                provider.category.lowercase().contains(debouncedQuery.value) ||
                                provider.location.lowercase().contains(debouncedQuery.value) ||
                                provider.specialties.any { it.lowercase().contains(debouncedQuery.value) }
                        )
            }

            // Apply category filter
            if (filterState.selectedCategories.isNotEmpty()) {
                matches = matches && filterState.selectedCategories.contains(provider.category)
            }

            // Apply rating filter
            if (filterState.minRating > 0) {
                matches = matches && provider.rating >= filterState.minRating
            }

            // Apply price filter
            if (filterState.maxPrice != null) {
                matches = matches && provider.startingPrice <= filterState.maxPrice!!
            }

            // Apply location filter
            if (filterState.selectedLocations.isNotEmpty()) {
                matches = matches && filterState.selectedLocations.any { location ->
                    provider.location.contains(location)
                }
            }

            // Apply verified filter
            if (filterState.isVerifiedOnly) {
                matches = matches && provider.isVerified
            }

            // Apply experience filter
            if (filterState.minExperience > 0) {
                matches = matches && provider.experienceYears >= filterState.minExperience
            }

            matches
        }
    }

    Scaffold(
        containerColor = softBackground,
        topBar = {
            // Collapsible Header (unchanged)
            ProvidersHeroHeader(
                teal = primaryTeal,
                gold = goldenAccent,
                height = animatedHeight,
                collapseFraction = collapseFraction
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
                modifier = Modifier.fillMaxSize()
            ) {
                // Spacer for the fixed header and search bar
                item {
                    Spacer(
                        modifier = Modifier.height(
                            maxHeight + 72.dp // Height for header + search bar
                        )
                    )
                }

                // Search results info (like SmartMatch)
                if (debouncedQuery.value.isNotEmpty()) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Search results for \"${searchQuery}\"",
                                fontSize = 14.sp,
                                color = SlateGrayProviders,
                                fontWeight = FontWeight.Medium
                            )
                            if (isSearching) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = primaryTeal
                                )
                            } else {
                                Text(
                                    text = "${filteredProviders.size} providers found",
                                    fontSize = 13.sp,
                                    color = primaryTeal,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                // SmartMatch Highlight (kept from original)
                item {
                    SmartMatchProviderHighlight(primaryTeal, goldenAccent)
                }

                // Empty state when no providers match search/filters
                if (filteredProviders.isEmpty() && debouncedQuery.value.isNotEmpty() && !isSearching) {
                    item {
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
                                    Icons.Outlined.SearchOff,
                                    contentDescription = null,
                                    tint = SlateGrayProviders.copy(0.5f),
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "No providers found",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepNavyProviders
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Try different keywords or clear filters",
                                    fontSize = 14.sp,
                                    color = SlateGrayProviders,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                // Empty state when no providers exist at all (and no active search)
                if (filteredProviders.isEmpty() && debouncedQuery.value.isEmpty() && !isSearching) {
                    item {
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
                                    Icons.Outlined.Info,
                                    contentDescription = null,
                                    tint = SlateGrayProviders.copy(0.5f),
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "No providers available",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepNavyProviders
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Check back later for new providers",
                                    fontSize = 14.sp,
                                    color = SlateGrayProviders,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                // Provider Cards
                items(filteredProviders) { provider ->
                    ElegantProviderCard(
                        provider = provider,
                        accentColor = primaryTeal,
                        goldAccent = goldenAccent,
                        onViewProfileClick = { /* Navigate to profile */ },
                        onContactClick = { /* Open contact */ }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                item { Spacer(Modifier.height(80.dp)) }
            }

            // 🏆 FIXED HEADER (always on top)
            ProvidersHeroHeader(
                teal = primaryTeal,
                gold = goldenAccent,
                height = animatedHeight,
                collapseFraction = collapseFraction,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .zIndex(2f) // Higher z-index to stay on top
            )

            // 📌 Sticky Search Bar - Positioned just below header (like SmartMatch)
            ProvidersStickySearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                onFilterClick = { showFilterModal = true },
                onAudioClick = {
                    isRecording = !isRecording
                    // Handle audio recording start/stop
                    if (isRecording) {
                        // Start recording
                    } else {
                        // Stop recording and process audio
                    }
                },
                isRecording = isRecording,
                activeFilterCount = activeFilterCount,
                accentColor = primaryTeal,
                headerHeight = animatedHeight,
                showShadow = isPastThreshold.value,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .zIndex(1f) // Below header but above content
            )
        }
    }

    // Filter Modal Bottom Sheet
    if (showFilterModal) {
        ProvidersFilterBottomSheet(
            filterState = filterState,
            onFilterChange = { filterState = it },
            onDismiss = { showFilterModal = false },
            onApply = {
                // Calculate active filter count
                activeFilterCount = listOfNotNull(
                    if (filterState.selectedCategories.isNotEmpty()) 1 else null,
                    if (filterState.minRating > 0) 1 else null,
                    if (filterState.maxPrice != null) 1 else null,
                    if (filterState.selectedLocations.isNotEmpty()) 1 else null,
                    if (filterState.isVerifiedOnly) 1 else null,
                    if (filterState.minExperience > 0) 1 else null
                ).size
                showFilterModal = false
            },
            onReset = {
                filterState = FilterState()
                activeFilterCount = 0
                showFilterModal = false
            },
            accentColor = primaryTeal
        )
    }
}

/* ─────────────────────────────────────────────
   STICKY SEARCH BAR (with filter and audio icons)
   ───────────────────────────────────────────── */

@Composable
fun ProvidersStickySearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onFilterClick: () -> Unit,
    onAudioClick: () -> Unit,
    isRecording: Boolean,
    activeFilterCount: Int,
    accentColor: Color,
    headerHeight: Dp,
    showShadow: Boolean,
    modifier: Modifier = Modifier
) {
    // Animate shadow based on scroll position
    val elevation by animateDpAsState(
        targetValue = if (showShadow) 8.dp else 0.dp,
        animationSpec = tween(durationMillis = 200)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = headerHeight) // Position right below header
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .shadow(
                    elevation = elevation,
                    shape = RoundedCornerShape(16.dp),
                    ambientColor = Color.Black.copy(0.08f),
                    spotColor = Color.Black.copy(0.08f)
                ),
            shape = RoundedCornerShape(16.dp),
            color = CleanWhiteProviders,
            tonalElevation = if (showShadow) 4.dp else 0.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Outlined.Search,
                    contentDescription = "Search",
                    tint = SlateGrayProviders.copy(0.6f),
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
                                    text = "Search provider...",
                                    color = SlateGrayProviders.copy(0.5f),
                                    fontSize = 14.sp
                                )
                            }
                            innerTextField()
                        }
                    },
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 14.sp,
                        color = DeepNavyProviders
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
                            tint = SlateGrayProviders.copy(0.6f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                } else {
                    // Audio Icon with recording animation
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
                            tint = if (isRecording) accentColor else SlateGrayProviders.copy(0.6f),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // Recording indicator (pulsing dot)
                    if (isRecording) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(
                                    color = Color.Red,
                                    shape = CircleShape
                                )
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }

                    // Filter button with badge (now after audio icon)
                    BadgedBox(
                        badge = {
                            if (activeFilterCount > 0) {
                                Surface(
                                    color = accentColor,
                                    shape = CircleShape,
                                    modifier = Modifier
                                        .size(16.dp)
                                        .offset(x = (-4).dp, y = (4).dp)
                                ) {
                                    Text(
                                        text = activeFilterCount.toString(),
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                }
                            }
                        }
                    ) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = accentColor.copy(0.08f),
                            modifier = Modifier
                                .clickable { onFilterClick() }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Outlined.Tune,
                                    contentDescription = "Filter",
                                    tint = accentColor,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    "Filters",
                                    fontSize = 12.sp,
                                    color = accentColor,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/* ─────────────────────────────────────────────
   ELEGANT PROVIDER CARD
   ───────────────────────────────────────────── */

@Composable
fun ElegantProviderCard(
    provider: EnhancedProviderData,
    accentColor: Color,
    goldAccent: Color,
    onViewProfileClick: () -> Unit,
    onContactClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .animateContentSize(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CleanWhiteProviders),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header Row: Avatar, Name, and SmartMatch Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar with Initial
                Surface(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape),
                    color = accentColor.copy(0.1f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = provider.name.take(1),
                            color = accentColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Name and Business
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = provider.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = DeepNavyProviders
                            )
                        )
                        if (provider.isVerified) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                Icons.Outlined.Verified,
                                contentDescription = "Verified",
                                tint = goldAccent,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    provider.businessName?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = SlateGrayProviders
                            )
                        )
                    }
                }

                // SmartMatch Badge
                if (provider.isSmartMatch) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = goldAccent.copy(0.15f)
                    ) {
                        Text(
                            text = "AI Match",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = goldAccent
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Rating and Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Rating
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Outlined.Star,
                        contentDescription = null,
                        tint = goldAccent,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = String.format("%.1f", provider.rating),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = DeepNavyProviders,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                    Text(
                        text = "(${provider.reviewCount} reviews)",
                        fontSize = 12.sp,
                        color = SlateGrayProviders,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                // Experience Badge
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = accentColor.copy(0.1f)
                ) {
                    Text(
                        text = "${provider.experienceYears}+ years",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontSize = 11.sp,
                        color = accentColor,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Description
            Text(
                text = provider.description,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = SlateGrayProviders
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Specialties Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                provider.specialties.take(3).forEach { specialty ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = accentColor.copy(0.08f)
                    ) {
                        Text(
                            text = specialty,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 10.sp,
                            color = accentColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Location and Service Radius
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Outlined.LocationOn,
                    contentDescription = null,
                    tint = SlateGrayProviders,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = provider.location,
                    fontSize = 12.sp,
                    color = SlateGrayProviders,
                    modifier = Modifier.padding(start = 4.dp)
                )
                Text(
                    text = " • ",
                    fontSize = 12.sp,
                    color = SlateGrayProviders
                )
                Icon(
                    Icons.Outlined.Radar,
                    contentDescription = null,
                    tint = SlateGrayProviders,
                    modifier = Modifier.size(12.dp)
                )
                Text(
                    text = provider.serviceRadius,
                    fontSize = 12.sp,
                    color = SlateGrayProviders,
                    modifier = Modifier.padding(start = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Price and Availability Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Price
                Column {
                    Text(
                        text = "Starting at",
                        fontSize = 11.sp,
                        color = SlateGrayProviders
                    )
                    Text(
                        text = "KES ${provider.startingPrice}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = accentColor
                        )
                    )
                }

                // Availability and Response Time
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "⚡ ${provider.responseTime}",
                            fontSize = 11.sp,
                            color = SlateGrayProviders
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.horizontalScroll(rememberScrollState())
                    ) {
                        provider.availability.take(2).forEach { time ->
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = accentColor.copy(0.08f),
                                modifier = Modifier.padding(end = if (time != provider.availability.last()) 4.dp else 0.dp)
                            ) {
                                Text(
                                    text = time,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                    fontSize = 9.sp,
                                    color = accentColor
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // View Profile Button (Outlined)
                OutlinedButton(
                    onClick = onViewProfileClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, accentColor.copy(0.5f)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = accentColor
                    )
                ) {
                    Text(
                        "View Profile",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Contact Button (Filled)
                Button(
                    onClick = onContactClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = accentColor
                    )
                ) {
                    Text(
                        "Contact",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = CleanWhiteProviders
                    )
                }
            }
        }
    }
}

/* ─────────────────────────────────────────────
   FILTER BOTTOM SHEET (like SmartMatch)
   ───────────────────────────────────────────── */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProvidersFilterBottomSheet(
    filterState: FilterState,
    onFilterChange: (FilterState) -> Unit,
    onDismiss: () -> Unit,
    onApply: () -> Unit,
    onReset: () -> Unit,
    accentColor: Color
) {
    var localFilterState by remember { mutableStateOf(filterState) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = CleanWhiteProviders,
        tonalElevation = 8.dp,
        dragHandle = { BottomSheetDefaults.DragHandle(color = LightBorderProviders) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header with title and close button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filter Providers",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = DeepNavyProviders
                    )
                )

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(40.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = LightBorderProviders.copy(0.5f),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Outlined.Close,
                                contentDescription = "Close",
                                tint = SlateGrayProviders,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // Categories Section
            Text(
                text = "Categories",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = DeepNavyProviders,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Category chips - First row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val categories = listOf("Electrician", "Plumber")
                categories.forEach { category ->
                    val isSelected = localFilterState.selectedCategories.contains(category)
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            localFilterState = localFilterState.copy(
                                selectedCategories = if (isSelected) {
                                    localFilterState.selectedCategories - category
                                } else {
                                    localFilterState.selectedCategories + category
                                }
                            )
                        },
                        label = {
                            Text(
                                category,
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        modifier = Modifier.weight(1f),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = accentColor,
                            selectedLabelColor = CleanWhiteProviders,
                            containerColor = CleanWhiteProviders,
                            labelColor = SlateGrayProviders
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) accentColor else LightBorderProviders
                        ),
                        shape = RoundedCornerShape(30.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Category chips - Second row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val categories = listOf("Designer", "Legal", "Property")
                categories.forEach { category ->
                    val isSelected = localFilterState.selectedCategories.contains(category)
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            localFilterState = localFilterState.copy(
                                selectedCategories = if (isSelected) {
                                    localFilterState.selectedCategories - category
                                } else {
                                    localFilterState.selectedCategories + category
                                }
                            )
                        },
                        label = {
                            Text(
                                category,
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        modifier = Modifier.weight(1f),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = accentColor,
                            selectedLabelColor = CleanWhiteProviders,
                            containerColor = CleanWhiteProviders,
                            labelColor = SlateGrayProviders
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) accentColor else LightBorderProviders
                        ),
                        shape = RoundedCornerShape(30.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Rating Section
            Text(
                text = "Minimum Rating",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = DeepNavyProviders,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val ratings = listOf(0.0, 3.5, 4.0, 4.5)
                ratings.forEach { rating ->
                    val isSelected = localFilterState.minRating == rating
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            localFilterState = localFilterState.copy(minRating = rating)
                        },
                        label = {
                            Text(
                                if (rating == 0.0) "Any" else "$rating+",
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        modifier = Modifier.weight(1f),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = accentColor,
                            selectedLabelColor = CleanWhiteProviders,
                            containerColor = CleanWhiteProviders,
                            labelColor = SlateGrayProviders
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) accentColor else LightBorderProviders
                        ),
                        shape = RoundedCornerShape(30.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Price Range Section
            Text(
                text = "Max Price (KES)",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = DeepNavyProviders,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val priceOptions = listOf(null, 1000, 2500, 5000)
                priceOptions.forEach { price ->
                    val isSelected = localFilterState.maxPrice == price
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            localFilterState = localFilterState.copy(maxPrice = price)
                        },
                        label = {
                            Text(
                                when (price) {
                                    null -> "Any"
                                    1000 -> "< 1K"
                                    2500 -> "< 2.5K"
                                    else -> "< 5K"
                                },
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        modifier = Modifier.weight(1f),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = accentColor,
                            selectedLabelColor = CleanWhiteProviders,
                            containerColor = CleanWhiteProviders,
                            labelColor = SlateGrayProviders
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) accentColor else LightBorderProviders
                        ),
                        shape = RoundedCornerShape(30.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val priceOptions = listOf(10000)
                priceOptions.forEach { price ->
                    val isSelected = localFilterState.maxPrice == price
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            localFilterState = localFilterState.copy(maxPrice = price)
                        },
                        label = {
                            Text(
                                "< 10K",
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        modifier = Modifier.weight(1f),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = accentColor,
                            selectedLabelColor = CleanWhiteProviders,
                            containerColor = CleanWhiteProviders,
                            labelColor = SlateGrayProviders
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) accentColor else LightBorderProviders
                        ),
                        shape = RoundedCornerShape(30.dp)
                    )
                }
                // Empty space for balance
                Spacer(modifier = Modifier.weight(2f))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Experience Section
            Text(
                text = "Experience",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = DeepNavyProviders,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val experienceOptions = listOf(0, 3, 5, 10)
                experienceOptions.forEach { years ->
                    val isSelected = localFilterState.minExperience == years
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            localFilterState = localFilterState.copy(minExperience = years)
                        },
                        label = {
                            Text(
                                if (years == 0) "Any" else "$years+ years",
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        modifier = Modifier.weight(1f),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = accentColor,
                            selectedLabelColor = CleanWhiteProviders,
                            containerColor = CleanWhiteProviders,
                            labelColor = SlateGrayProviders
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) accentColor else LightBorderProviders
                        ),
                        shape = RoundedCornerShape(30.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Toggle Options
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Verified Only
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            localFilterState = localFilterState.copy(
                                isVerifiedOnly = !localFilterState.isVerifiedOnly
                            )
                        }
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Outlined.Verified,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Verified providers only",
                            fontSize = 14.sp,
                            color = DeepNavyProviders
                        )
                    }
                    Switch(
                        checked = localFilterState.isVerifiedOnly,
                        onCheckedChange = {
                            localFilterState = localFilterState.copy(isVerifiedOnly = it)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = accentColor,
                            checkedTrackColor = accentColor.copy(0.5f),
                            uncheckedThumbColor = SlateGrayProviders,
                            uncheckedTrackColor = LightBorderProviders
                        )
                    )
                }

                // Available Today
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            localFilterState = localFilterState.copy(
                                isAvailableToday = !localFilterState.isAvailableToday
                            )
                        }
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Outlined.Today,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Available today",
                            fontSize = 14.sp,
                            color = DeepNavyProviders
                        )
                    }
                    Switch(
                        checked = localFilterState.isAvailableToday,
                        onCheckedChange = {
                            localFilterState = localFilterState.copy(isAvailableToday = it)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = accentColor,
                            checkedTrackColor = accentColor.copy(0.5f),
                            uncheckedThumbColor = SlateGrayProviders,
                            uncheckedTrackColor = LightBorderProviders
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Selected count indicator
            val selectedCount = listOf(
                localFilterState.selectedCategories.size,
                if (localFilterState.minRating > 0) 1 else 0,
                if (localFilterState.maxPrice != null) 1 else 0,
                localFilterState.selectedLocations.size,
                if (localFilterState.isVerifiedOnly) 1 else 0,
                if (localFilterState.minExperience > 0) 1 else 0
            ).sum()

            if (selectedCount > 0) {
                Surface(
                    color = accentColor.copy(0.1f),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = "$selectedCount filter${if (selectedCount > 1) "s" else ""} selected",
                        fontSize = 12.sp,
                        color = accentColor,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Action Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Reset Button
                OutlinedButton(
                    onClick = {
                        localFilterState = FilterState()
                        onReset()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, LightBorderProviders),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = SlateGrayProviders
                    )
                ) {
                    Text(
                        "Reset",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Apply Button
                Button(
                    onClick = {
                        onFilterChange(localFilterState)
                        onApply()
                    },
                    modifier = Modifier
                        .weight(2f)
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = accentColor,
                        contentColor = CleanWhiteProviders
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 2.dp,
                        pressedElevation = 4.dp
                    )
                ) {
                    Text(
                        "Apply Filters",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/* ─────────────────────────────────────────────
   SMART MATCH HIGHLIGHT
   ───────────────────────────────────────────── */

@Composable
private fun SmartMatchProviderHighlight(
    teal: Color,
    gold: Color
) {
    Card(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = teal.copy(0.05f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.AutoAwesome,
                contentDescription = null,
                tint = gold,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "SmartMatch™",
                    color = gold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                Text(
                    text = "Providers suggested based on your activity",
                    fontSize = 12.sp,
                    color = SlateGrayProviders
                )
            }
            TextButton(onClick = {}) {
                Text("View", color = teal, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// Keep the original header and helper composables exactly as they were
@Composable
fun ProvidersHeroHeader(
    teal: Color,
    gold: Color,
    height: Dp,
    collapseFraction: Float,
    modifier: Modifier = Modifier
) {
    val collapsed = collapseFraction > 0.85f

    val maxFontSize = 34.sp
    val minFontSize = 24.sp

    // Animate background color based on collapse state
    val backgroundColor by animateColorAsState(
        targetValue = if (collapsed) teal.copy(alpha = 0.95f) else Color.Transparent,
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
                    painter = painterResource(id = R.drawable.happypeople),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Solid color background for collapsed state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor)
            )

            // Gradient overlay - only visible when not collapsed
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
                                    teal.copy(0.95f),
                                    teal.copy(0.75f),
                                    Color.Transparent
                                )
                            )
                        )
                )
            }

            // Golden Accent (Horizontal Glow) - only visible when not collapsed
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
                                colors = listOf(gold.copy(0.15f), Color.Transparent),
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
                                HeaderAvatarProviders(teal)
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .background(gold, CircleShape)
                                        .border(2.dp, teal, CircleShape)
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
                            HeaderActionIconProviders(
                                icon = Icons.Default.Mail,
                                iconTint = Color.White,
                                backgroundTint = Color.White.copy(alpha = 0.2f),
                                onClick = {}
                            )
                            HeaderActionIconProviders(
                                icon = Icons.Default.Notifications,
                                iconTint = Color.White,
                                backgroundTint = Color.White.copy(alpha = 0.2f),
                                onClick = {}
                            )
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
                    // Left side - Providers text only (no button)
                    Text(
                        text = "Providers",
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
                        HeaderActionIconProviders(
                            icon = Icons.Default.Mail,
                            iconTint = Color.White,
                            backgroundTint = Color.White.copy(alpha = 0.2f),
                            onClick = {}
                        )
                        HeaderActionIconProviders(
                            icon = Icons.Default.Notifications,
                            iconTint = Color.White,
                            backgroundTint = Color.White.copy(alpha = 0.2f),
                            onClick = {}
                        )
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
                    // Providers text only (no button)
                    Text(
                        text = "Providers",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-1.5).sp,
                            fontSize = titleFontSize
                        )
                    )

                    // 📝 Subtitle Text
                    Text(
                        text = "Professional partners you can trust",
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
fun HeaderAvatarProviders(teal: Color) {
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

@Composable
fun HeaderActionIconProviders(
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