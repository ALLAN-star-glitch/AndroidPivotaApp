package com.example.pivota.dashboard.presentation.screens

import android.annotation.SuppressLint
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.layout.Layout
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
import androidx.compose.ui.zIndex
import androidx.window.core.layout.WindowSizeClass
import com.example.pivota.R
import com.example.pivota.ui.theme.*
import com.example.pivota.dashboard.presentation.composables.ContactIcon
import com.example.pivota.dashboard.presentation.composables.ModernProviderCard
import kotlinx.coroutines.delay

// Enhanced data model
data class EnhancedProviderData(
    val id: String,
    val name: String,
    val businessName: String?,
    val profileImageRes: Int? = null,
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
    val completedJobs: Int,
    val isFavorite: Boolean = false
)

// Simplified filter state - minimal filters only
data class FilterState(
    val selectedCategories: Set<String> = emptySet(),
    val minRating: Double = 0.0,
    val isVerifiedOnly: Boolean = false
)

@SuppressLint("FrequentlyChangingValue")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProvidersScreen() {
    val colorScheme = MaterialTheme.colorScheme

    // 🎨 Brand Palette - Using theme colors
    val primaryColor = colorScheme.primary      // African Sapphire
    val secondaryColor = colorScheme.secondary  // Warm Terracotta
    val tertiaryColor = colorScheme.tertiary    // Baobab Gold
    val softBackground = colorScheme.background

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

    // Get screen width for adaptive layout
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val isTablet = screenWidth > 600.dp
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    // Determine grid columns based on screen size
    val gridColumns = when {
        isTablet && isLandscape -> 3
        isTablet -> 2
        screenWidth > 480.dp -> 2
        else -> 1
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
            delay(300)
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
                profileImageRes = null,
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
                profileImageRes = null,
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
                profileImageRes = null,
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
                profileImageRes = null,
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
                profileImageRes = null,
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

            // Apply verified filter
            if (filterState.isVerifiedOnly) {
                matches = matches && provider.isVerified
            }

            matches
        }
    }

    Scaffold(
        containerColor = softBackground,
        topBar = {
            ProvidersHeroHeader(
                primaryColor = primaryColor,
                tertiaryColor = tertiaryColor,
                height = animatedHeight,
                collapseFraction = collapseFraction,
                colorScheme = colorScheme
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
                            maxHeight + 72.dp
                        )
                    )
                }

                // Search results info
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
                                color = colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium
                            )
                            if (isSearching) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = primaryColor
                                )
                            } else {
                                Text(
                                    text = "${filteredProviders.size} providers found",
                                    fontSize = 13.sp,
                                    color = primaryColor,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
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
                                    tint = colorScheme.onSurfaceVariant.copy(0.5f),
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "No providers found",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Try different keywords or clear filters",
                                    fontSize = 14.sp,
                                    color = colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                // Provider Cards - Use LazyVerticalGrid as the main layout for content
                // Instead of nesting, we'll use the LazyColumn to hold the grid
                if (filteredProviders.isNotEmpty()) {
                    // Since we're already in a LazyColumn, we need to add items individually
                    // Split the providers into chunks for grid display
                    val chunkedProviders = filteredProviders.chunked(gridColumns)

                    items(chunkedProviders) { rowProviders ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            rowProviders.forEach { provider ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth()
                                ) {
                                    ModernProviderCard(
                                        name = provider.name,
                                        specialty = provider.category,
                                        rating = provider.rating.toFloat(),
                                        jobs = provider.completedJobs,
                                        isVerified = provider.isVerified,
                                        description = provider.description,
                                        onCardClick = { /* Navigate to provider details */ },
                                        onViewClick = { /* View provider profile */ },
                                        onBookClick = { /* Book provider */ },
                                        onMessageClick = { /* Send message */ },
                                        onWhatsAppClick = { /* WhatsApp */ },
                                        onPhoneClick = { /* Call */ },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .animateItem()
                                    )
                                }
                            }

                            // Add empty boxes to fill remaining space if row has fewer items than columns
                            repeat(gridColumns - rowProviders.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                item { Spacer(Modifier.height(80.dp)) }
            }

            // 🏆 FIXED HEADER (always on top)
            ProvidersHeroHeader(
                primaryColor = primaryColor,
                tertiaryColor = tertiaryColor,
                height = animatedHeight,
                collapseFraction = collapseFraction,
                colorScheme = colorScheme,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .zIndex(2f)
            )

            // 📌 Sticky Search Bar
            ProvidersStickySearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                onFilterClick = { showFilterModal = true },
                onAudioClick = {
                    isRecording = !isRecording
                },
                isRecording = isRecording,
                activeFilterCount = activeFilterCount,
                accentColor = primaryColor,
                headerHeight = animatedHeight,
                showShadow = isPastThreshold.value,
                colorScheme = colorScheme,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .zIndex(1f)
            )
        }
    }

    // Simplified Filter Modal Bottom Sheet - minimal filters
    if (showFilterModal) {
        ProvidersMinimalFilterBottomSheet(
            filterState = filterState,
            onFilterChange = { filterState = it },
            onDismiss = { showFilterModal = false },
            onApply = {
                activeFilterCount = listOfNotNull(
                    if (filterState.selectedCategories.isNotEmpty()) 1 else null,
                    if (filterState.minRating > 0) 1 else null,
                    if (filterState.isVerifiedOnly) 1 else null
                ).size
                showFilterModal = false
            },
            onReset = {
                filterState = FilterState()
                activeFilterCount = 0
                showFilterModal = false
            },
            accentColor = primaryColor,
            colorScheme = colorScheme
        )
    }
}

/* ─────────────────────────────────────────────
   SIMPLIFIED MINIMAL FILTER BOTTOM SHEET - FIXED VERSION
   ───────────────────────────────────────────── */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProvidersMinimalFilterBottomSheet(
    filterState: FilterState,
    onFilterChange: (FilterState) -> Unit,
    onDismiss: () -> Unit,
    onApply: () -> Unit,
    onReset: () -> Unit,
    accentColor: Color,
    colorScheme: androidx.compose.material3.ColorScheme
) {
    var localFilterState by remember { mutableStateOf(filterState) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = colorScheme.surface,
        tonalElevation = 8.dp,
        dragHandle = { BottomSheetDefaults.DragHandle(color = colorScheme.outlineVariant) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
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
                        color = colorScheme.onSurface
                    )
                )

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(40.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = colorScheme.outlineVariant.copy(0.5f),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Outlined.Close,
                                contentDescription = "Close",
                                tint = colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // Categories Section - SINGLE VERSION (FIXED)
            Text(
                text = "Categories",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            val categories = listOf("Electrician", "Plumber", "Designer", "Legal", "Property")

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // First row - first 3 categories
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.take(3).forEach { category ->
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
                                selectedLabelColor = colorScheme.onPrimary,
                                containerColor = colorScheme.surface,
                                labelColor = colorScheme.onSurfaceVariant
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) accentColor else colorScheme.outlineVariant
                            ),
                            shape = RoundedCornerShape(30.dp)
                        )
                    }
                }

                // Second row - remaining categories
                if (categories.size > 3) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        categories.drop(3).forEach { category ->
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
                                    selectedLabelColor = colorScheme.onPrimary,
                                    containerColor = colorScheme.surface,
                                    labelColor = colorScheme.onSurfaceVariant
                                ),
                                border = BorderStroke(
                                    1.dp,
                                    if (isSelected) accentColor else colorScheme.outlineVariant
                                ),
                                shape = RoundedCornerShape(30.dp)
                            )
                        }
                        // Fill empty slots (for 3-column grid)
                        repeat(3 - (categories.size - 3)) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Rating Section
            Text(
                text = "Minimum Rating",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = colorScheme.onSurface,
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
                            selectedLabelColor = colorScheme.onPrimary,
                            containerColor = colorScheme.surface,
                            labelColor = colorScheme.onSurfaceVariant
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) accentColor else colorScheme.outlineVariant
                        ),
                        shape = RoundedCornerShape(30.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Verified Only Toggle
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
                        color = colorScheme.onSurface
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
                        uncheckedThumbColor = colorScheme.onSurfaceVariant,
                        uncheckedTrackColor = colorScheme.outlineVariant
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Selected count indicator
            val selectedCount = listOf(
                localFilterState.selectedCategories.size,
                if (localFilterState.minRating > 0) 1 else 0,
                if (localFilterState.isVerifiedOnly) 1 else 0
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
                OutlinedButton(
                    onClick = {
                        localFilterState = FilterState()
                        onReset()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, colorScheme.outlineVariant),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = colorScheme.onSurfaceVariant
                    )
                ) {
                    Text(
                        "Reset",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

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
                        contentColor = colorScheme.onPrimary
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
    colorScheme: androidx.compose.material3.ColorScheme,
    modifier: Modifier = Modifier
) {
    val elevation by animateDpAsState(
        targetValue = if (showShadow) 8.dp else 0.dp,
        animationSpec = tween(durationMillis = 200)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = headerHeight)
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
            color = colorScheme.surface,
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
                                    text = "Search provider...",
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
                    // Audio Icon
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

                    Spacer(modifier = Modifier.width(4.dp))

                    if (isRecording) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(
                                    color = colorScheme.error,
                                    shape = CircleShape
                                )
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }

                    // Filter button with badge
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
                                        color = colorScheme.onPrimary,
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
   PROVIDERS HERO HEADER
   ───────────────────────────────────────────── */

@Composable
fun ProvidersHeroHeader(
    primaryColor: Color,
    tertiaryColor: Color,
    height: Dp,
    collapseFraction: Float,
    colorScheme: androidx.compose.material3.ColorScheme,
    modifier: Modifier = Modifier
) {
    val collapsed = collapseFraction > 0.85f

    val maxFontSize = 34.sp
    val minFontSize = 24.sp

    val backgroundColor by animateColorAsState(
        targetValue = if (collapsed) colorScheme.primary.copy(alpha = 0.95f) else Color.Transparent,
        animationSpec = tween(durationMillis = 300)
    )

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
                                colors = listOf(colorScheme.tertiary.copy(0.15f), Color.Transparent),
                                endX = 400f
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
                                HeaderAvatarProviders(colorScheme)
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .background(colorScheme.tertiary, CircleShape)
                                        .border(2.dp, colorScheme.primary, CircleShape)
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
                        text = "Providers",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-1.5).sp,
                            fontSize = titleFontSize
                        )
                    )

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
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 20.dp, bottom = 32.dp)
                        .statusBarsPadding()
                ) {
                    Text(
                        text = "Providers",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-1.5).sp,
                            fontSize = titleFontSize
                        )
                    )

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
fun HeaderAvatarProviders(colorScheme: androidx.compose.material3.ColorScheme) {
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

// FlowRow utility for wrapping chips - KEPT BUT NOT USED
@Composable
fun FlowRowProviders(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: @Composable () -> Unit
) {
    Layout(
        content = content,
        modifier = modifier
    ) { measurables, constraints ->
        val placeables = measurables.map { it.measure(constraints) }

        var xPosition = 0
        var yPosition = 0
        var rowMaxHeight = 0

        layout(constraints.maxWidth, constraints.maxHeight) {
            placeables.forEach { placeable ->
                if (xPosition + placeable.width > constraints.maxWidth) {
                    xPosition = 0
                    yPosition += rowMaxHeight
                    rowMaxHeight = 0
                }
                placeable.placeRelative(x = xPosition, y = yPosition)
                xPosition += placeable.width
                rowMaxHeight = maxOf(rowMaxHeight, placeable.height)
            }
        }
    }
}