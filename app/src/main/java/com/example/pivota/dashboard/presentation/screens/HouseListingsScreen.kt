package com.example.pivota.dashboard.presentation.screens


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass
import com.example.pivota.dashboard.domain.ListingStatus
import com.example.pivota.dashboard.presentation.composables.ModernHousingCard
import com.example.pivota.dashboard.presentation.state.HousingListingUiModel
import com.example.pivota.dashboard.presentation.viewmodels.HouseListingsViewModel
import com.example.pivota.R
import kotlinx.coroutines.delay

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
    onPostListingClick: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val listings by viewModel.filteredListings.collectAsStateWithLifecycle()

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
    val isWide = windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) ||
            windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)

    Scaffold(
        containerColor = colorScheme.background,
        topBar = {
            HouseListingsHeader(
                onNavigateBack = onNavigateBack,
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                isSearching = isSearching,
                isRecording = isRecording,
                onAudioClick = { isRecording = !isRecording },
                onFilterClick = { showFilterModal = true },
                activeFilterCount = activeFilterCount,
                colorScheme = colorScheme
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Category Filter Pills
            HousingCategoryFilterPills(
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = it },
                colorScheme = colorScheme
            )

            if (filteredListings.isEmpty()) {
                if (searchQuery.isNotEmpty() || selectedCategory != HousingCategoryType.ALL ||
                    statusFilterState.selectedStatuses.isNotEmpty() || priceRange.min != null || priceRange.max != null) {
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
            } else {
                // Adaptive Layout: Grid for Medium/Expanded, List for Compact
                LazyVerticalGrid(
                    columns = if (isWide) GridCells.Fixed(2) else GridCells.Fixed(1),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 20.dp,
                        end = 20.dp,
                        top = 12.dp,
                        bottom = 32.dp
                    ),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredListings, key = { it.id }) { listing ->
                        ModernHousingCard(
                            price = listing.price,
                            title = listing.title,
                            location = listing.location,
                            type = listing.propertyType,
                            rating = listing.rating,
                            isVerified = listing.isVerified,
                            description = listing.description,
                            isForSale = listing.isForSale,
                            imageRes = listing.imageRes ?: R.drawable.property_placeholder1,
                            bedrooms = listing.bedrooms,
                            bathrooms = listing.bathrooms,
                            squareMeters = listing.squareMeters,
                            onViewClick = { onListingClick(listing) },
                            onBookClick = { /* Handle book action */ },
                            onClick = { onListingClick(listing) }
                        )
                    }
                }
            }
        }
    }

    // Filter Bottom Sheet
    if (showFilterModal) {
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
            colorScheme = colorScheme
        )
    }
}

/* ────────────── HELPER FUNCTION ────────────── */

private fun extractPriceValue(priceString: String): Int {
    return try {
        // Remove "KES ", "KSh ", commas, and handle "M" for millions
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

/* ────────────── SEARCH HEADER ────────────── */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HouseListingsHeader(
    onNavigateBack: () -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    isSearching: Boolean,
    isRecording: Boolean,
    onAudioClick: () -> Unit,
    onFilterClick: () -> Unit,
    activeFilterCount: Int,
    colorScheme: ColorScheme,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
                ambientColor = colorScheme.scrim.copy(0.08f)
            ),
        color = colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Top row with back button and title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Back Arrow
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = colorScheme.primary.copy(0.08f)
                        ) {
                            Box(
                                modifier = Modifier.size(40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    Column {
                        Text(
                            text = "House Listings",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = colorScheme.onSurface,
                                letterSpacing = (-0.5).sp
                            )
                        )
                        Text(
                            text = "Find your dream home",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.width(40.dp))
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Search Bar
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 2.dp,
                        shape = RoundedCornerShape(16.dp),
                        ambientColor = colorScheme.scrim.copy(0.05f)
                    ),
                shape = RoundedCornerShape(16.dp),
                color = colorScheme.surface,
                tonalElevation = 1.dp
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
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        modifier = Modifier.weight(1f),
                        decorationBox = { innerTextField ->
                            Box {
                                if (searchQuery.isEmpty()) {
                                    Text(
                                        text = "Search by location, title...",
                                        color = colorScheme.onSurfaceVariant.copy(0.5f),
                                        fontSize = 14.sp,
                                        style = MaterialTheme.typography.bodyMedium
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

                    if (searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = { onSearchQueryChange("") },
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
                                        color = colorScheme.primary,
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
                                color = colorScheme.primary.copy(0.08f),
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
                                        tint = colorScheme.primary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        "Filters",
                                        fontSize = 12.sp,
                                        color = colorScheme.primary,
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
}

/* ────────────── CATEGORY FILTER PILLS ────────────── */



@Composable
private fun HousingCategoryFilterPills(
    selectedCategory: HousingCategoryType,
    onCategorySelected: (HousingCategoryType) -> Unit,
    colorScheme: ColorScheme
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorScheme.surface),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Create a list of category items with proper typing using available icons
        val categories = listOf(
            Triple(HousingCategoryType.ALL, null, "All"),
            Triple(HousingCategoryType.APARTMENT, Icons.Outlined.Apartment, "Apartments"),
            Triple(HousingCategoryType.HOUSE, Icons.Outlined.House, "Houses"),
            Triple(HousingCategoryType.STUDIO, Icons.Outlined.HomeWork, "Studios"), // Using Meal icon as alternative
            Triple(HousingCategoryType.BEDSITTER, Icons.Outlined.Bed, "Bedsitters")
        )

        items(categories) { (category, icon, displayName) ->
            val isSelected = selectedCategory == category

            Surface(
                onClick = { onCategorySelected(category) },
                shape = RoundedCornerShape(20.dp),
                color = if (isSelected) colorScheme.secondary else colorScheme.surface,
                border = if (isSelected) null else BorderStroke(1.dp, colorScheme.outlineVariant),
                modifier = Modifier.height(36.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp)
                ) {
                    if (icon != null) {
                        Icon(
                            icon,
                            contentDescription = null,
                            tint = if (isSelected) colorScheme.onSecondary else colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Text(
                        text = displayName,
                        color = if (isSelected) colorScheme.onSecondary else colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    )
                }
            }
        }
    }
}

/* ────────────── FILTER BOTTOM SHEET ────────────── */

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
    colorScheme: ColorScheme
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
                // Min Price
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

                // Max Price
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

            // Status chips
            val statuses = ListingStatus.entries.toTypedArray()

            // First row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (i in 0..2) {
                    if (i < statuses.size) {
                        val status = statuses[i]
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
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            modifier = Modifier.weight(1f),
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
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Second row
            if (statuses.size > 3) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (i in 3 until statuses.size) {
                        val status = statuses[i]
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
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            modifier = Modifier.weight(1f),
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
                    // Fill remaining slots
                    for (j in 0 until (3 - (statuses.size - 3))) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            if (localStatusState.selectedStatuses.isNotEmpty() || localMinPrice.isNotEmpty() || localMaxPrice.isNotEmpty()) {
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