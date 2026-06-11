package com.example.pivota.dashboard.presentation.screens.client_general_screens.listings_screens.professionals

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import com.example.pivota.dashboard.presentation.composables.listings_composables.ServiceOfferingCard
import com.example.pivota.dashboard.presentation.state.ServiceOfferingsUiState
import com.example.pivota.dashboard.presentation.viewmodels.client_general_viewmodels.ServiceOfferingsViewModel
import kotlinx.coroutines.delay

// Filter options for service offerings
enum class ServiceOfferingFilter {
    ALL,
    LOWEST_PRICE,
    HIGHEST_PRICE,
    HIGHEST_RATED,
    MOST_EXPERIENCED,
    VERIFIED_ONLY
}

// Sort options
enum class SortOption(val displayName: String) {
    RECENT("Most Recent"),
    LOWEST_PRICE("Price: Low to High"),
    HIGHEST_PRICE("Price: High to Low"),
    HIGHEST_RATED("Highest Rated"),
    MOST_EXPERIENCED("Most Experienced")
}

// Filter state for bottom sheet
data class ServiceFilterState(
    val selectedSort: SortOption = SortOption.RECENT,
    val minPrice: Int? = null,
    val maxPrice: Int? = null,
    val verifiedOnly: Boolean = false,
    val minRating: Int = 0
)

// Custom modifier for shimmer effect
fun Modifier.shimmerEffect(): Modifier = composed {
    var size by remember { mutableStateOf(0f) }
    val density = LocalDensity.current
    val transition = rememberInfiniteTransition(label = "shimmer")
    val animationValue = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_animation"
    )

    val brush = Brush.linearGradient(
        colors = listOf(
            Color.Transparent,
            Color.White.copy(alpha = 0.7f),
            Color.White.copy(alpha = 0.3f),
            Color.Transparent
        ),
        start = Offset(animationValue.value - size, 0f),
        end = Offset(animationValue.value, size)
    )

    this
        .onGloballyPositioned {
            size = it.size.width.toFloat()
        }
        .background(brush)
}

fun Modifier.prominentShimmerEffect(): Modifier = composed {
    var size by remember { mutableStateOf(0f) }
    val transition = rememberInfiniteTransition(label = "shimmer")
    val animationValue = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_animation"
    )

    val brush = Brush.linearGradient(
        colors = listOf(
            Color.Transparent,
            Color(0xFFE0E0E0).copy(alpha = 0.8f),
            Color(0xFFF5F5F5).copy(alpha = 0.5f),
            Color.Transparent
        ),
        start = Offset(animationValue.value - size, 0f),
        end = Offset(animationValue.value + (size * 0.5f), size)
    )

    this
        .onGloballyPositioned { size = it.size.width.toFloat() }
        .background(brush)
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceOfferingsScreen(
    categoryId: String,
    categoryName: String,
    viewModel: ServiceOfferingsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onOfferingClick: (String) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val offeringsState by viewModel.offeringsState.collectAsStateWithLifecycle()

    // Get window size for adaptive layout
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val isExpanded = windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.EXPANDED
    val isMedium = windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.MEDIUM
    val isTablet = isExpanded || isMedium

    // Determine grid columns based on screen size
    // Phone: 1 column, Tablet: 2 columns, Large Tablet/Desktop: 3 columns
    val gridColumns = when {
        isExpanded -> 3  // Large tablets / Desktop
        isMedium -> 2    // Medium tablets (2 per row)
        else -> 1        // Phones
    }

    val listState = rememberLazyGridState()

    // Search and filter state
    var searchQuery by remember { mutableStateOf("") }
    var filterState by remember { mutableStateOf(ServiceFilterState()) }
    var showFilterModal by remember { mutableStateOf(false) }
    var activeFilterCount by remember { mutableIntStateOf(0) }
    var isSearching by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    // Load offerings when screen opens
    LaunchedEffect(categoryId) {
        viewModel.loadOfferings(categoryId)
    }

    // Debounce search
    val debouncedQuery = remember { mutableStateOf("") }
    LaunchedEffect(searchQuery) {
        if (searchQuery.isNotEmpty()) {
            isSearching = true
            delay(300)
            debouncedQuery.value = searchQuery.lowercase()
            isSearching = false
        } else {
            debouncedQuery.value = ""
            isSearching = false
        }
    }

    // Update filter count
    LaunchedEffect(filterState) {
        var count = 0
        if (filterState.selectedSort != SortOption.RECENT) count++
        if (filterState.minPrice != null || filterState.maxPrice != null) count++
        if (filterState.verifiedOnly) count++
        if (filterState.minRating > 0) count++
        activeFilterCount = count
    }

    // Filter and sort offerings
    val filteredOfferings = remember(offeringsState, debouncedQuery.value, filterState) {
        if (offeringsState is ServiceOfferingsUiState.Success) {
            val offerings = (offeringsState as ServiceOfferingsUiState.Success).offerings
            var filtered = offerings

            if (debouncedQuery.value.isNotEmpty()) {
                filtered = filtered.filter {
                    it.title.lowercase().contains(debouncedQuery.value) ||
                            it.professionalName.lowercase().contains(debouncedQuery.value) ||
                            it.categoryName.lowercase().contains(debouncedQuery.value)
                }
            }

            if (filterState.minPrice != null) {
                filtered = filtered.filter { it.basePrice >= filterState.minPrice!! }
            }
            if (filterState.maxPrice != null) {
                filtered = filtered.filter { it.basePrice <= filterState.maxPrice!! }
            }
            if (filterState.verifiedOnly) {
                filtered = filtered.filter { it.isVerified }
            }
            if (filterState.minRating > 0) {
                filtered = filtered.filter { it.averageRating >= filterState.minRating }
            }

            filtered = when (filterState.selectedSort) {
                SortOption.RECENT -> filtered
                SortOption.LOWEST_PRICE -> filtered.sortedBy { it.basePrice }
                SortOption.HIGHEST_PRICE -> filtered.sortedByDescending { it.basePrice }
                SortOption.HIGHEST_RATED -> filtered.sortedByDescending { it.averageRating }
                SortOption.MOST_EXPERIENCED -> filtered.sortedByDescending { it.yearsExperience }
            }

            filtered
        } else {
            emptyList()
        }
    }

    val hasMoreData = offeringsState is ServiceOfferingsUiState.Success &&
            (offeringsState as ServiceOfferingsUiState.Success).hasMore

    // Load more when scrolling near the end
    LaunchedEffect(listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index) {
        val lastVisibleIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
        if (lastVisibleIndex >= filteredOfferings.size - 3 &&
            filteredOfferings.isNotEmpty() &&
            hasMoreData) {
            viewModel.loadMore()
        }
    }

    Scaffold(
        containerColor = colorScheme.background,
        topBar = {
            ServiceOfferingsHeader(
                categoryName = categoryName,
                onNavigateBack = onNavigateBack,
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                isSearching = isSearching,
                onFilterClick = { showFilterModal = true },
                activeFilterCount = activeFilterCount,
                colorScheme = colorScheme
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (offeringsState) {
                is ServiceOfferingsUiState.Loading -> {
                    ServiceOfferingsLoadingSkeleton(gridColumns = gridColumns)
                }

                is ServiceOfferingsUiState.Success -> {
                    if (filteredOfferings.isEmpty()) {
                        ServiceOfferingsEmptyState(
                            hasSearchOrFilter = searchQuery.isNotEmpty() || activeFilterCount > 0,
                            onClearFilters = {
                                searchQuery = ""
                                filterState = ServiceFilterState()
                                focusManager.clearFocus()
                            },
                            colorScheme = colorScheme
                        )
                    } else {
                        if (gridColumns == 1) {
                            // Phone layout - single column (list)
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(
                                    horizontal = 16.dp,
                                    vertical = 12.dp
                                ),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(
                                    items = filteredOfferings,
                                    key = { it.id }
                                ) { offering ->
                                    ServiceOfferingCard(
                                        offering = offering,
                                        onClick = { onOfferingClick(offering.id) }
                                    )
                                }

                                if (hasMoreData) {
                                    item {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(32.dp),
                                                strokeWidth = 2.dp
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            // Tablet/Desktop layout - grid with 2 or 3 columns
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(gridColumns),
                                state = listState,
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(
                                    horizontal = 16.dp,
                                    vertical = 12.dp
                                ),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(
                                    items = filteredOfferings,
                                    key = { it.id }
                                ) { offering ->
                                    ServiceOfferingCard(
                                        offering = offering,
                                        onClick = { onOfferingClick(offering.id) },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }

                                if (hasMoreData) {
                                    item {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(32.dp),
                                                strokeWidth = 2.dp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                is ServiceOfferingsUiState.Error -> {
                    val state = offeringsState as ServiceOfferingsUiState.Error
                    ServiceOfferingsErrorState(
                        message = state.message,
                        onRetry = { viewModel.loadOfferings(categoryId) },
                        colorScheme = colorScheme
                    )
                }
            }
        }
    }

    if (showFilterModal) {
        ServiceOfferingsFilterBottomSheet(
            filterState = filterState,
            onFilterChange = { filterState = it },
            onDismiss = { showFilterModal = false },
            onApply = { showFilterModal = false },
            onReset = {
                filterState = ServiceFilterState()
                showFilterModal = false
            },
            colorScheme = colorScheme
        )
    }
}

@Composable
private fun ServiceOfferingsLoadingSkeleton(gridColumns: Int) {
    if (gridColumns == 1) {
        // Phone layout - single column
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                horizontal = 16.dp,
                vertical = 12.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(5) {
                ServiceOfferingCardSkeleton()
            }
        }
    } else {
        // Tablet/Desktop layout - grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(gridColumns),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                horizontal = 16.dp,
                vertical = 12.dp
            ),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Show 6 skeletons for grid (2 rows of 3 or 3 rows of 2)
            items(gridColumns * 2) {
                ServiceOfferingCardSkeleton()
            }
        }
    }
}

@Composable
private fun ServiceOfferingCardSkeleton() {
    val colorScheme = MaterialTheme.colorScheme
    val shimmerModifier = Modifier.prominentShimmerEffect()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Avatar skeleton
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(colorScheme.surfaceVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .then(shimmerModifier)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Title skeleton
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(20.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(colorScheme.surfaceVariant)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .then(shimmerModifier)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Category skeleton
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(16.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(colorScheme.surfaceVariant)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .then(shimmerModifier)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Price skeleton
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .height(24.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(colorScheme.surfaceVariant)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .then(shimmerModifier)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Rating skeleton
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(colorScheme.surfaceVariant)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .then(shimmerModifier)
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.3f)
                            .height(14.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(colorScheme.surfaceVariant)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .then(shimmerModifier)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ServiceOfferingsHeader(
    categoryName: String,
    onNavigateBack: () -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    isSearching: Boolean,
    onFilterClick: () -> Unit,
    activeFilterCount: Int,
    colorScheme: ColorScheme
) {
    Surface(
        modifier = Modifier
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
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
                            text = categoryName,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = colorScheme.onSurface,
                                letterSpacing = (-0.5).sp
                            )
                        )
                        Text(
                            text = "Service Offerings",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.width(40.dp))
            }

            Spacer(modifier = Modifier.height(12.dp))

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
                                        text = "Search services...",
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
                    } else if (isSearching) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

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
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = activeFilterCount.toString(),
                                            color = colorScheme.onPrimary,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ServiceOfferingsFilterBottomSheet(
    filterState: ServiceFilterState,
    onFilterChange: (ServiceFilterState) -> Unit,
    onDismiss: () -> Unit,
    onApply: () -> Unit,
    onReset: () -> Unit,
    colorScheme: ColorScheme
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
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filter Services",
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

            Text(
                text = "Sort By",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SortOption.values().forEach { sortOption ->
                    val isSelected = localFilterState.selectedSort == sortOption
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            localFilterState = localFilterState.copy(selectedSort = sortOption)
                        },
                        label = {
                            Text(
                                sortOption.displayName,
                                fontSize = 13.sp,
                                maxLines = 1
                            )
                        },
                        modifier = Modifier.weight(1f),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = colorScheme.primary,
                            selectedLabelColor = colorScheme.onPrimary,
                            containerColor = colorScheme.surface,
                            labelColor = colorScheme.onSurfaceVariant
                        ),
                        border = if (isSelected) null else BorderStroke(1.dp, colorScheme.outlineVariant)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

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
                    value = localFilterState.minPrice?.toString() ?: "",
                    onValueChange = {
                        localFilterState = localFilterState.copy(
                            minPrice = it.toIntOrNull()
                        )
                    },
                    placeholder = { Text("Min", fontSize = 13.sp) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
                )

                OutlinedTextField(
                    value = localFilterState.maxPrice?.toString() ?: "",
                    onValueChange = {
                        localFilterState = localFilterState.copy(
                            maxPrice = it.toIntOrNull()
                        )
                    },
                    placeholder = { Text("Max", fontSize = 13.sp) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        localFilterState = localFilterState.copy(
                            verifiedOnly = !localFilterState.verifiedOnly
                        )
                    }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Verified Professionals Only",
                    fontSize = 14.sp,
                    color = colorScheme.onSurface
                )
                Checkbox(
                    checked = localFilterState.verifiedOnly,
                    onCheckedChange = {
                        localFilterState = localFilterState.copy(verifiedOnly = it)
                    },
                    colors = CheckboxDefaults.colors(
                        checkedColor = colorScheme.primary
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Minimum Rating",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(0, 3, 4, 5).forEach { rating ->
                    val isSelected = localFilterState.minRating == rating
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            localFilterState = localFilterState.copy(
                                minRating = if (isSelected) 0 else rating
                            )
                        },
                        label = {
                            Text(
                                if (rating == 0) "Any" else "$rating★+",
                                fontSize = 13.sp
                            )
                        },
                        modifier = Modifier.weight(1f),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = colorScheme.primary,
                            selectedLabelColor = colorScheme.onPrimary,
                            containerColor = colorScheme.surface,
                            labelColor = colorScheme.onSurfaceVariant
                        ),
                        border = if (isSelected) null else BorderStroke(1.dp, colorScheme.outlineVariant)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            val activeFilters = mutableListOf<String>()
            if (localFilterState.selectedSort != SortOption.RECENT) {
                activeFilters.add(localFilterState.selectedSort.displayName)
            }
            if (localFilterState.minPrice != null || localFilterState.maxPrice != null) {
                val min = localFilterState.minPrice?.toString() ?: "Any"
                val max = localFilterState.maxPrice?.toString() ?: "Any"
                activeFilters.add("KES $min - $max")
            }
            if (localFilterState.verifiedOnly) activeFilters.add("Verified only")
            if (localFilterState.minRating > 0) activeFilters.add("${localFilterState.minRating}★+")

            if (activeFilters.isNotEmpty()) {
                Surface(
                    color = colorScheme.primary.copy(0.1f),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = activeFilters.joinToString(" • "),
                        fontSize = 11.sp,
                        color = colorScheme.primary,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            } else {
                Spacer(modifier = Modifier.height(24.dp))
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        localFilterState = ServiceFilterState()
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

@Composable
private fun ServiceOfferingsEmptyState(
    hasSearchOrFilter: Boolean,
    onClearFilters: () -> Unit,
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
                Icons.Outlined.SearchOff,
                null,
                tint = colorScheme.primary,
                modifier = Modifier.padding(20.dp)
            )
        }
        Spacer(Modifier.height(24.dp))
        Text(
            if (hasSearchOrFilter) "No matching services" else "No services available",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface
            )
        )
        Spacer(Modifier.height(8.dp))
        Text(
            if (hasSearchOrFilter)
                "Try adjusting your search or filter criteria"
            else
                "Check back later for more services",
            style = MaterialTheme.typography.bodySmall.copy(
                color = colorScheme.onSurfaceVariant
            ),
            textAlign = TextAlign.Center
        )
        if (hasSearchOrFilter) {
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
}

@Composable
private fun ServiceOfferingsErrorState(
    message: String,
    onRetry: () -> Unit,
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
            color = colorScheme.error.copy(0.05f),
            shape = CircleShape
        ) {
            Icon(
                Icons.Outlined.Error,
                null,
                tint = colorScheme.error,
                modifier = Modifier.padding(20.dp)
            )
        }
        Spacer(Modifier.height(24.dp))
        Text(
            "Something went wrong",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface
            )
        )
        Spacer(Modifier.height(8.dp))
        Text(
            message,
            style = MaterialTheme.typography.bodySmall.copy(
                color = colorScheme.onSurfaceVariant
            ),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(32.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = colorScheme.primary
            ),
            shape = RoundedCornerShape(16.dp),
            contentPadding = PaddingValues(horizontal = 32.dp, vertical = 12.dp)
        ) {
            Text(
                "Try Again",
                fontWeight = FontWeight.Bold
            )
        }
    }
}