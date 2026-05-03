package com.example.pivota.dashboard.presentation.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import com.example.pivota.R

import com.example.pivota.dashboard.presentation.composables.ModernHousingCardV2
import com.example.pivota.dashboard.presentation.state.HousingListingUiModel
import com.example.pivota.dashboard.presentation.viewmodels.HouseListingsViewModel
import kotlinx.coroutines.delay
import androidx.compose.foundation.rememberScrollState
import com.example.pivota.dashboard.domain.ListingStatus

// Category type for housing filtering
enum class HousingCategoryType {
    ALL,
    APARTMENT,
    HOUSE,
    STUDIO,
    BEDSITTER
}

// Status filter state for bottom sheet
data class HousingStatusFilterState(
    val selectedStatuses: Set<ListingStatus> = emptySet()
)

// Price range filter
data class HousePriceRange(
    val min: Int? = null,
    val max: Int? = null
)

/* ────────────── SCREEN ────────────── */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HouseListingsScreen(
    viewModel: HouseListingsViewModel = hiltViewModel(),
    onListingClick: (HousingListingUiModel) -> Unit,
    onBookClick: (HousingListingUiModel) -> Unit,
    onPostListingClick: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val listings by viewModel.filteredListings.collectAsStateWithLifecycle()
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp

    // State for search and filters
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(HousingCategoryType.ALL) }
    var statusFilterState by remember { mutableStateOf(HousingStatusFilterState()) }
    var priceRange by remember { mutableStateOf(HousePriceRange()) }
    var showFilterModal by remember { mutableStateOf(false) }
    var activeFilterCount by remember { mutableStateOf(0) }
    var isSearching by remember { mutableStateOf(false) }
    var isRecording by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    // Track if search bar should be pinned (triggers when any scrolling occurs)
    val gridState = rememberLazyGridState()
    val isSearchBarPinned by remember {
        derivedStateOf {
            gridState.firstVisibleItemIndex > 0 || gridState.firstVisibleItemScrollOffset > 0
        }
    }

    // Debounce search
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

    // Filter listings based on category, status, price, and search
    val filteredListings = remember(debouncedQuery.value, selectedCategory, statusFilterState, priceRange, listings) {
        listings.filter { listing ->
            var matches = true

            // Apply category filter
            when (selectedCategory) {
                HousingCategoryType.ALL -> matches = true
                HousingCategoryType.APARTMENT -> matches = listing.propertyType.equals("Apartment", ignoreCase = true)
                HousingCategoryType.HOUSE -> matches = listing.propertyType.equals("House", ignoreCase = true)
                HousingCategoryType.STUDIO -> matches = listing.propertyType.equals("Studio", ignoreCase = true)
                HousingCategoryType.BEDSITTER -> matches = listing.propertyType.equals("Bedsitter", ignoreCase = true)
            }

            // Apply status filter
            if (statusFilterState.selectedStatuses.isNotEmpty() && matches) {
                matches = statusFilterState.selectedStatuses.contains(listing.status)
            }

            // Apply price range filter
            if (matches) {
                val price = extractPriceValue(listing.price)
                priceRange.min?.let {
                    if (price < it) matches = false
                }
                priceRange.max?.let {
                    if (price > it) matches = false
                }
            }

            // Apply search filter
            if (debouncedQuery.value.isNotEmpty() && matches) {
                matches = listing.title.lowercase().contains(debouncedQuery.value) ||
                        listing.location.lowercase().contains(debouncedQuery.value) ||
                        listing.description.lowercase().contains(debouncedQuery.value)
            }

            matches
        }
    }

    // Update active filter count
    LaunchedEffect(statusFilterState, priceRange) {
        var count = statusFilterState.selectedStatuses.size
        if (priceRange.min != null || priceRange.max != null) count++
        activeFilterCount = count
    }

    // ────────────── ADAPTIVE BREAKPOINTS ──────────────
    val windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val windowWidthClass = windowSizeClass.windowWidthSizeClass

    // Determine grid columns based on screen width
    val gridColumns = when {
        windowWidthClass == WindowWidthSizeClass.EXPANDED -> 3  // Large tablets and desktops (840dp+)
        windowWidthClass == WindowWidthSizeClass.MEDIUM -> 2   // Medium tablets (600-840dp)
        screenWidth >= 900 -> 3  // Extra large phones in landscape or large tablets
        screenWidth >= 600 -> 2  // Tablets or large phones in landscape
        else -> 1  // Regular phones
    }

    // Adaptive content padding
    val horizontalPadding = when {
        windowWidthClass == WindowWidthSizeClass.EXPANDED -> 32.dp
        windowWidthClass == WindowWidthSizeClass.MEDIUM -> 24.dp
        else -> 20.dp
    }

    // Adaptive grid spacing
    val gridSpacing = when {
        windowWidthClass == WindowWidthSizeClass.EXPANDED -> 20.dp
        windowWidthClass == WindowWidthSizeClass.MEDIUM -> 16.dp
        else -> 12.dp
    }

    Scaffold(
        containerColor = colorScheme.background
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // SCROLLABLE CONTENT
            LazyVerticalGrid(
                columns = GridCells.Fixed(gridColumns),
                state = gridState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = horizontalPadding,
                    end = horizontalPadding,
                    top = 0.dp,
                    bottom = 100.dp
                ),
                horizontalArrangement = Arrangement.spacedBy(gridSpacing),
                verticalArrangement = Arrangement.spacedBy(gridSpacing)
            ) {
                // Header section (back button, title, subtitle) - scrolls away
                item(span = { GridItemSpan(maxLineSpan) }) {
                    HouseListingsHeader(
                        onNavigateBack = onNavigateBack,
                        colorScheme = colorScheme,
                        isExpanded = windowWidthClass == WindowWidthSizeClass.EXPANDED
                    )
                }

                // Search Bar and Category Pills - ONLY show when NOT pinned
                // When pinned, the sticky version takes over to prevent duplication
                if (!isSearchBarPinned) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        HousingSearchAndPillsSection(
                            searchQuery = searchQuery,
                            onSearchQueryChange = { searchQuery = it },
                            isSearching = isSearching,
                            isRecording = isRecording,
                            onAudioClick = { isRecording = !isRecording },
                            onFilterClick = { showFilterModal = true },
                            activeFilterCount = activeFilterCount,
                            selectedCategory = selectedCategory,
                            onCategorySelected = { selectedCategory = it },
                            colorScheme = colorScheme,
                            isExpanded = windowWidthClass == WindowWidthSizeClass.EXPANDED,
                            isSticky = false
                        )
                    }
                }

                // Empty state or listings
                if (filteredListings.isEmpty()) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        if (searchQuery.isNotEmpty() || selectedCategory != HousingCategoryType.ALL ||
                            statusFilterState.selectedStatuses.isNotEmpty() || priceRange.min != null ||
                            priceRange.max != null) {
                            // No results for current filters
                            HousingNoResultsEmptyState(
                                onClearFilters = {
                                    searchQuery = ""
                                    selectedCategory = HousingCategoryType.ALL
                                    statusFilterState = HousingStatusFilterState()
                                    priceRange = HousePriceRange()
                                    activeFilterCount = 0
                                    focusManager.clearFocus()
                                }
                            )
                        } else {
                            // No listings at all
                            HousingEmptyState(
                                onPostListingClick = onPostListingClick,
                                colorScheme = colorScheme
                            )
                        }
                    }
                } else {
                    items(filteredListings, key = { it.id }) { listing ->
                        ModernHousingCardV2(
                            imageUrl = listing.imageRes ?: R.drawable.property_placeholder1,
                            title = listing.title,
                            price = listing.price,
                            location = listing.location,
                            postedTime = listing.postedTime ?: "Recently",
                            propertyType = listing.propertyType,
                            listingType = if (listing.isForSale) "For Sale" else "For Rent",
                            bedrooms = listing.bedrooms,
                            bathrooms = listing.bathrooms,
                            squareMeters = listing.squareMeters,
                            isVerified = listing.isVerified,
                            onViewDetailsClick = { onListingClick(listing) }
                        )
                    }
                }
            }

            // STICKY SEARCH + CATEGORY PILLS SECTION (appears when scrolled)
            if (isSearchBarPinned) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .zIndex(10f),
                    shape = RoundedCornerShape(
                        topStart = 0.dp,
                        topEnd = 0.dp,
                        bottomStart = 20.dp,
                        bottomEnd = 20.dp
                    ),
                    color = colorScheme.surface,
                    tonalElevation = 0.dp,
                    shadowElevation = 0.dp
                ) {
                    Column {
                        HousingSearchAndPillsSection(
                            searchQuery = searchQuery,
                            onSearchQueryChange = { searchQuery = it },
                            isSearching = isSearching,
                            isRecording = isRecording,
                            onAudioClick = { isRecording = !isRecording },
                            onFilterClick = { showFilterModal = true },
                            activeFilterCount = activeFilterCount,
                            selectedCategory = selectedCategory,
                            onCategorySelected = { selectedCategory = it },
                            colorScheme = colorScheme,
                            isExpanded = windowWidthClass == WindowWidthSizeClass.EXPANDED,
                            isSticky = true
                        )

                        // Bottom divider line for visual separation
                        Divider(
                            color = colorScheme.outlineVariant.copy(alpha = 0.5f),
                            thickness = 1.dp,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }
        }
    }

    // Filter Modal - Adaptive presentation based on screen size
    if (showFilterModal) {
        if (windowWidthClass == WindowWidthSizeClass.COMPACT) {
            // Use bottom sheet for phones
            HousingFilterBottomSheet(
                statusFilterState = statusFilterState,
                priceRange = priceRange,
                onStatusFilterChange = { statusFilterState = it },
                onPriceRangeChange = { priceRange = it },
                onDismiss = { showFilterModal = false },
                onApply = { showFilterModal = false },
                onReset = {
                    statusFilterState = HousingStatusFilterState()
                    priceRange = HousePriceRange()
                    activeFilterCount = 0
                    showFilterModal = false
                },
                colorScheme = colorScheme,
                isExpanded = false
            )
        } else {
            // Use dialog for tablets and larger screens
            AlertDialog(
                onDismissRequest = { showFilterModal = false },
                title = {
                    Text(
                        "Filter Properties",
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    HousingFilterDialogContent(
                        statusFilterState = statusFilterState,
                        priceRange = priceRange,
                        onStatusFilterChange = { statusFilterState = it },
                        onPriceRangeChange = { priceRange = it },
                        colorScheme = colorScheme
                    )
                },
                confirmButton = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TextButton(
                            onClick = {
                                statusFilterState = HousingStatusFilterState()
                                priceRange = HousePriceRange()
                                activeFilterCount = 0
                                showFilterModal = false
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Reset")
                        }
                        Button(
                            onClick = { showFilterModal = false },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Apply")
                        }
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showFilterModal = false }) {
                        Text("Cancel")
                    }
                },
                shape = RoundedCornerShape(24.dp)
            )
        }
    }
}

/* ────────────── SEARCH AND PILLS SECTION ────────────── */

@Composable
private fun HousingSearchAndPillsSection(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    isSearching: Boolean,
    isRecording: Boolean,
    onAudioClick: () -> Unit,
    onFilterClick: () -> Unit,
    activeFilterCount: Int,
    selectedCategory: HousingCategoryType,
    onCategorySelected: (HousingCategoryType) -> Unit,
    colorScheme: ColorScheme,
    isExpanded: Boolean = false,
    isSticky: Boolean = false
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorScheme.background)
            .padding(
                horizontal = if (isExpanded) 32.dp else 16.dp,
                vertical = if (isSticky) if (isExpanded) 16.dp else 12.dp else 0.dp
            )
    ) {
        // Search Bar
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = if (isSticky) 4.dp else 0.dp,
                    shape = RoundedCornerShape(16.dp),
                    ambientColor = colorScheme.scrim.copy(0.08f)
                ),
            shape = RoundedCornerShape(16.dp),
            color = colorScheme.surface,
            tonalElevation = if (isSticky) 2.dp else 0.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = if (isExpanded) 20.dp else 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Outlined.Search,
                    contentDescription = "Search",
                    tint = colorScheme.onSurfaceVariant.copy(0.6f),
                    modifier = Modifier.size(if (isExpanded) 24.dp else 20.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                BasicTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier.weight(1f),
                    decorationBox = { innerTextField ->
                        Box {
                            if (searchQuery.isEmpty()) {
                                Text(
                                    text = "Search by location, title...",
                                    color = colorScheme.onSurfaceVariant.copy(0.5f),
                                    fontSize = if (isExpanded) 16.sp else 14.sp,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            innerTextField()
                        }
                    },
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = if (isExpanded) 16.sp else 14.sp,
                        color = colorScheme.onSurface
                    )
                )

                if (searchQuery.isNotEmpty()) {
                    IconButton(
                        onClick = { onSearchQueryChange("") },
                        modifier = Modifier.size(if (isExpanded) 36.dp else 32.dp)
                    ) {
                        Icon(
                            Icons.Outlined.Close,
                            contentDescription = "Clear",
                            tint = colorScheme.onSurfaceVariant.copy(0.6f),
                            modifier = Modifier.size(if (isExpanded) 20.dp else 16.dp)
                        )
                    }
                } else {
                    // Loading indicator while searching
                    if (isSearching) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(if (isExpanded) 24.dp else 20.dp),
                            strokeWidth = 2.dp,
                            color = colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    // Audio Icon
                    IconButton(
                        onClick = onAudioClick,
                        modifier = Modifier
                            .size(if (isExpanded) 44.dp else 40.dp)
                            .then(
                                if (isRecording) {
                                    Modifier.background(
                                        color = colorScheme.primary.copy(0.1f),
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
                            tint = if (isRecording) colorScheme.primary else colorScheme.onSurfaceVariant.copy(0.6f),
                            modifier = Modifier.size(if (isExpanded) 24.dp else 20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    if (isRecording) {
                        Box(
                            modifier = Modifier
                                .size(if (isExpanded) 10.dp else 8.dp)
                                .background(
                                    color = colorScheme.error,
                                    shape = CircleShape
                                )
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }

                    // Filter button with badge
                    Box {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = colorScheme.primary.copy(0.08f),
                            modifier = Modifier
                                .clickable { onFilterClick() }
                                .padding(horizontal = if (isExpanded) 14.dp else 10.dp, vertical = if (isExpanded) 8.dp else 6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Outlined.Tune,
                                    contentDescription = "Filter",
                                    tint = colorScheme.primary,
                                    modifier = Modifier.size(if (isExpanded) 18.dp else 14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    "Filters",
                                    fontSize = if (isExpanded) 14.sp else 12.sp,
                                    color = colorScheme.primary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        // Badge
                        if (activeFilterCount > 0) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = 8.dp, y = (-4).dp)
                            ) {
                                Surface(
                                    color = colorScheme.primary,
                                    shape = CircleShape,
                                    modifier = Modifier.size(if (isExpanded) 20.dp else 16.dp)
                                ) {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = activeFilterCount.toString(),
                                            color = colorScheme.onPrimary,
                                            fontSize = if (isExpanded) 11.sp else 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (!isSticky) {
            Spacer(modifier = Modifier.height(if (isExpanded) 16.dp else 12.dp))
        }

        // Category Filter Pills
        HousingCategoryFilterPills(
            selectedCategory = selectedCategory,
            onCategorySelected = onCategorySelected,
            colorScheme = colorScheme,
            isExpanded = isExpanded
        )
    }
}

/* ────────────── HEADER (NON-STICKY, SCROLLS AWAY) ────────────── */

@Composable
private fun HouseListingsHeader(
    onNavigateBack: () -> Unit,
    colorScheme: ColorScheme,
    isExpanded: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colorScheme.background)
            .padding(
                horizontal = if (isExpanded) 32.dp else 16.dp,
                vertical = if (isExpanded) 20.dp else 12.dp
            )
    ) {
        // Back button row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier.size(if (isExpanded) 48.dp else 40.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = colorScheme.primary.copy(0.08f)
                ) {
                    Box(
                        modifier = Modifier.size(if (isExpanded) 48.dp else 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = colorScheme.primary,
                            modifier = Modifier.size(if (isExpanded) 28.dp else 20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(if (isExpanded) 12.dp else 8.dp))

        // Title and subtitle
        Text(
            text = "Properties",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.ExtraBold,
                color = colorScheme.onSurface,
                letterSpacing = (-0.5).sp,
                fontSize = if (isExpanded) 36.sp else 28.sp
            )
        )

        Spacer(modifier = Modifier.height(if (isExpanded) 8.dp else 4.dp))

        Text(
            text = "Find your dream home",
            style = MaterialTheme.typography.bodySmall.copy(
                color = colorScheme.onSurfaceVariant,
                fontSize = if (isExpanded) 16.sp else 14.sp
            )
        )
    }
}

/* ────────────── CATEGORY FILTER PILLS ────────────── */

@Composable
private fun HousingCategoryFilterPills(
    selectedCategory: HousingCategoryType,
    onCategorySelected: (HousingCategoryType) -> Unit,
    colorScheme: ColorScheme,
    isExpanded: Boolean = false
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 0.dp, vertical = if (isExpanded) 12.dp else 8.dp),
        horizontalArrangement = Arrangement.spacedBy(if (isExpanded) 12.dp else 8.dp)
    ) {
        val categories = listOf(
            HousingCategoryType.ALL to "All",
            HousingCategoryType.APARTMENT to "Apartments",
            HousingCategoryType.HOUSE to "Houses",
            HousingCategoryType.STUDIO to "Studios",
            HousingCategoryType.BEDSITTER to "Bedsitters"
        )

        items(categories) { (category, displayName) ->
            val isSelected = selectedCategory == category

            Surface(
                onClick = { onCategorySelected(category) },
                shape = RoundedCornerShape(20.dp),
                color = if (isSelected) colorScheme.secondary else colorScheme.surface,
                border = if (isSelected) null else BorderStroke(1.dp, colorScheme.outlineVariant),
                modifier = Modifier.height(if (isExpanded) 44.dp else 36.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = if (isExpanded) 16.dp else 12.dp)
                ) {
                    Text(
                        text = displayName,
                        color = if (isSelected) colorScheme.onSecondary else colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = if (isExpanded) 15.sp else 13.sp
                        )
                    )
                }
            }
        }
    }
}

/* ────────────── HELPER FUNCTION ────────────── */

private fun extractPriceValue(priceString: String): Int {
    return try {
        val cleaned = priceString
            .replace("KES", "")
            .replace("KSh", "")
            .replace(",", "")
            .replace(" ", "")
            .trim()

        when {
            cleaned.endsWith("M", ignoreCase = true) -> {
                val number = cleaned.dropLast(1).toDouble()
                (number * 1_000_000).toInt()
            }
            cleaned.endsWith("K", ignoreCase = true) -> {
                val number = cleaned.dropLast(1).toDouble()
                (number * 1_000).toInt()
            }
            else -> cleaned.toIntOrNull() ?: 0
        }
    } catch (e: Exception) {
        0
    }
}

/* ────────────── FILTER DIALOG CONTENT (for tablets) ────────────── */

@Composable
private fun HousingFilterDialogContent(
    statusFilterState: HousingStatusFilterState,
    priceRange: HousePriceRange,
    onStatusFilterChange: (HousingStatusFilterState) -> Unit,
    onPriceRangeChange: (HousePriceRange) -> Unit,
    colorScheme: ColorScheme
) {
    var localStatusState by remember { mutableStateOf(statusFilterState) }
    var localMinPrice by remember { mutableStateOf(priceRange.min?.toString() ?: "") }
    var localMaxPrice by remember { mutableStateOf(priceRange.max?.toString() ?: "") }

    // Update parent state when local state changes
    LaunchedEffect(localStatusState, localMinPrice, localMaxPrice) {
        onStatusFilterChange(localStatusState)
        onPriceRangeChange(HousePriceRange(min = localMinPrice.toIntOrNull(), max = localMaxPrice.toIntOrNull()))
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Price Range Section
        Column {
            Text(
                text = "Price Range (KES)",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = localMinPrice,
                    onValueChange = { localMinPrice = it.filter { char -> char.isDigit() } },
                    label = { Text("Min") },
                    placeholder = { Text("Any") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true
                )

                OutlinedTextField(
                    value = localMaxPrice,
                    onValueChange = { localMaxPrice = it.filter { char -> char.isDigit() } },
                    label = { Text("Max") },
                    placeholder = { Text("Any") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true
                )
            }
        }

        // Status Section
        Column {
            Text(
                text = "Listing Status",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            val statuses = ListingStatus.entries.toTypedArray()

            androidx.compose.foundation.layout.FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                statuses.forEach { status ->
                    val isSelected = localStatusState.selectedStatuses.contains(status)
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            localStatusState = localStatusState.copy(
                                selectedStatuses = if (isSelected) {
                                    localStatusState.selectedStatuses - status
                                } else {
                                    localStatusState.selectedStatuses + status
                                }
                            )
                        },
                        label = {
                            Text(
                                status.name.lowercase().replaceFirstChar { it.uppercase() },
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = colorScheme.primary,
                            selectedLabelColor = colorScheme.onPrimary,
                            containerColor = colorScheme.surface,
                            labelColor = colorScheme.onSurfaceVariant
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) colorScheme.primary else colorScheme.outlineVariant
                        ),
                        shape = RoundedCornerShape(30.dp)
                    )
                }
            }
        }
    }
}

/* ────────────── FILTER BOTTOM SHEET (for phones) ────────────── */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HousingFilterBottomSheet(
    statusFilterState: HousingStatusFilterState,
    priceRange: HousePriceRange,
    onStatusFilterChange: (HousingStatusFilterState) -> Unit,
    onPriceRangeChange: (HousePriceRange) -> Unit,
    onDismiss: () -> Unit,
    onApply: () -> Unit,
    onReset: () -> Unit,
    colorScheme: ColorScheme,
    isExpanded: Boolean = false
) {
    var localStatusState by remember { mutableStateOf(statusFilterState) }
    var localMinPrice by remember { mutableStateOf(priceRange.min?.toString() ?: "") }
    var localMaxPrice by remember { mutableStateOf(priceRange.max?.toString() ?: "") }

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
                .padding(horizontal = if (isExpanded) 32.dp else 20.dp)
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
                    text = "Filter Properties",
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

            // Price Range Section
            Text(
                text = "Price Range (KES)",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = localMinPrice,
                    onValueChange = { localMinPrice = it.filter { char -> char.isDigit() } },
                    label = { Text("Min") },
                    placeholder = { Text("Any") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colorScheme.primary,
                        unfocusedBorderColor = colorScheme.outlineVariant
                    ),
                    singleLine = true
                )

                OutlinedTextField(
                    value = localMaxPrice,
                    onValueChange = { localMaxPrice = it.filter { char -> char.isDigit() } },
                    label = { Text("Max") },
                    placeholder = { Text("Any") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colorScheme.primary,
                        unfocusedBorderColor = colorScheme.outlineVariant
                    ),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Status Section
            Text(
                text = "Listing Status",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            val statuses = ListingStatus.entries.toTypedArray()

            androidx.compose.foundation.layout.FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                statuses.forEach { status ->
                    val isSelected = localStatusState.selectedStatuses.contains(status)
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            localStatusState = localStatusState.copy(
                                selectedStatuses = if (isSelected) {
                                    localStatusState.selectedStatuses - status
                                } else {
                                    localStatusState.selectedStatuses + status
                                }
                            )
                        },
                        label = {
                            Text(
                                status.name.lowercase().replaceFirstChar { it.uppercase() },
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = colorScheme.primary,
                            selectedLabelColor = colorScheme.onPrimary,
                            containerColor = colorScheme.surface,
                            labelColor = colorScheme.onSurfaceVariant
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) colorScheme.primary else colorScheme.outlineVariant
                        ),
                        shape = RoundedCornerShape(30.dp)
                    )
                }
            }

            if (localStatusState.selectedStatuses.isNotEmpty() || localMinPrice.isNotEmpty() ||
                localMaxPrice.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    color = colorScheme.primary.copy(0.1f),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = "${localStatusState.selectedStatuses.size} status${if (localStatusState.selectedStatuses.size != 1) "es" else ""} selected",
                        fontSize = 12.sp,
                        color = colorScheme.primary,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            } else {
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
                        localStatusState = HousingStatusFilterState()
                        localMinPrice = ""
                        localMaxPrice = ""
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
                        onStatusFilterChange(localStatusState)
                        onPriceRangeChange(
                            HousePriceRange(
                                min = localMinPrice.toIntOrNull(),
                                max = localMaxPrice.toIntOrNull()
                            )
                        )
                        onApply()
                    },
                    modifier = Modifier
                        .weight(2f)
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorScheme.primary,
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

/* ────────────── NO RESULTS EMPTY STATE ────────────── */

@Composable
private fun HousingNoResultsEmptyState(
    onClearFilters: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size(80.dp),
            color = colorScheme.primary.copy(0.05f),
            shape = CircleShape
        ) {
            Icon(
                Icons.Outlined.SearchOff,
                null,
                tint = colorScheme.primary,
                modifier = Modifier.padding(20.dp)
            )
        }
        Spacer(Modifier.height(24.dp))
        Text(
            "No matching properties",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface
            )
        )
        Text(
            "Try adjusting your filters or search terms",
            style = MaterialTheme.typography.bodySmall.copy(
                color = colorScheme.onSurfaceVariant
            ),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(32.dp))
        Button(
            onClick = onClearFilters,
            colors = ButtonDefaults.buttonColors(
                containerColor = colorScheme.primary
            ),
            shape = RoundedCornerShape(16.dp),
            contentPadding = PaddingValues(horizontal = 32.dp, vertical = 12.dp)
        ) {
            Text(
                "Clear Filters",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/* ────────────── EMPTY STATE ────────────── */

@Composable
private fun HousingEmptyState(
    onPostListingClick: () -> Unit,
    colorScheme: ColorScheme
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size(80.dp),
            color = colorScheme.primary.copy(0.05f),
            shape = CircleShape
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_home),
                contentDescription = null,
                tint = colorScheme.primary,
                modifier = Modifier.padding(20.dp)
            )
        }
        Spacer(Modifier.height(24.dp))
        Text(
            "No properties listed yet",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface
            )
        )
        Text(
            "Be the first to list a property in your area.",
            style = MaterialTheme.typography.bodySmall.copy(
                color = colorScheme.onSurfaceVariant
            ),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(32.dp))
        Button(
            onClick = onPostListingClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = colorScheme.primary
            ),
            shape = RoundedCornerShape(16.dp),
            contentPadding = PaddingValues(horizontal = 32.dp, vertical = 12.dp)
        ) {
            Text(
                "Post Listing",
                fontWeight = FontWeight.Bold
            )
        }
    }
}