package com.example.pivota.dashboard.presentation.screens.client_general_screens.listings_screens.professionals

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
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
import com.example.pivota.dashboard.domain.model.listings_models.general.DiscoveryCategory
import com.example.pivota.dashboard.domain.model.listings_models.professionals.GetAllOfferingsParams
import com.example.pivota.dashboard.domain.model.listings_models.professionals.ServiceOffering
import com.example.pivota.dashboard.presentation.composables.client_general_composables.listings_composables.categories.getIconForService
import com.example.pivota.dashboard.presentation.composables.listings_composables.ServiceOfferingCard
import com.example.pivota.dashboard.presentation.state.ServiceOfferingsUiState
import com.example.pivota.dashboard.presentation.viewmodels.client_general_viewmodels.AllServicesViewModel
import com.example.pivota.dashboard.presentation.state.CommonServicesUiState
import com.example.pivota.dashboard.presentation.viewmodels.client_general_viewmodels.ServiceOfferingsViewModel
import kotlinx.coroutines.delay

enum class ServiceFilterPill {
    ALL,
    PROPERTY_SERVICES,
    CAREER_SERVICES,
    COMMUNITY_SUPPORT
}

// Sort options for service offerings
enum class ServiceSortOption(val displayName: String) {
    RECENT("Most Recent"),
    LOWEST_PRICE("Price: Low to High"),
    HIGHEST_PRICE("Price: High to Low"),
    HIGHEST_RATED("Highest Rated")
}

// Filter state for service offerings
data class ServiceOfferingFilterState(
    val selectedSort: ServiceSortOption = ServiceSortOption.RECENT,
    val minPrice: Int? = null,
    val maxPrice: Int? = null,
    val verifiedOnly: Boolean = false,
    val minRating: Int = 0
)

// Switch enum for Services vs Categories
enum class AllServicesView {
    SERVICES,
    CATEGORIES
}

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("FrequentlyChangingValue")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllServicesScreen(
    viewModel: AllServicesViewModel = hiltViewModel(),
    serviceOfferingsViewModel: ServiceOfferingsViewModel = hiltViewModel(),
    onServiceClick: (String, String, String) -> Unit = { _, _, _ -> },
    onSubcategoriesClick: (String, String, String) -> Unit = { _, _, _ -> },
    onOfferingClick: (String) -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    val colorScheme = MaterialTheme.colorScheme
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val allOfferingsState by serviceOfferingsViewModel.allOfferingsState.collectAsStateWithLifecycle()

    val windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val windowWidthClass = windowSizeClass.windowWidthSizeClass
    val isExpanded = windowWidthClass == WindowWidthSizeClass.EXPANDED
    val isMedium = windowWidthClass == WindowWidthSizeClass.MEDIUM
    val isTablet = isExpanded || isMedium

    val gridColumns = when {
        isExpanded -> 6
        isMedium -> 4
        else -> 3
    }

    val horizontalPadding = when {
        isExpanded -> 32.dp
        isMedium -> 24.dp
        else -> 16.dp
    }

    val gridSpacing = when {
        isExpanded -> 20.dp
        isMedium -> 16.dp
        else -> 12.dp
    }

    // Use pager state for swipe functionality
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { 2 } // 2 tabs: SERVICES (0) and CATEGORIES (1)
    )

    var currentView by remember { mutableStateOf(AllServicesView.SERVICES) }

    // When pager page changes, update the currentView
    LaunchedEffect(pagerState.currentPage) {
        currentView = if (pagerState.currentPage == 0) {
            AllServicesView.SERVICES
        } else {
            AllServicesView.CATEGORIES
        }
    }

    // When currentView changes externally (e.g., from the segmented switch), update the pager
    LaunchedEffect(currentView) {
        val targetPage = if (currentView == AllServicesView.SERVICES) 0 else 1
        if (pagerState.currentPage != targetPage) {
            pagerState.animateScrollToPage(targetPage)
        }
    }

    // ==================== CATEGORIES TAB STATE ====================
    var categoriesSearchQuery by remember { mutableStateOf("") }
    var categoriesSelectedFilter by remember { mutableStateOf(ServiceFilterPill.ALL) }
    var isCategoriesSearching by remember { mutableStateOf(false) }
    var showCategoriesFilterModal by remember { mutableStateOf(false) }
    var categoriesActiveFilterCount by remember { mutableIntStateOf(0) }
    val categoriesFocusManager = LocalFocusManager.current

    // ==================== SERVICES TAB STATE ====================
    var servicesSearchQuery by remember { mutableStateOf("") }
    var isServicesSearching by remember { mutableStateOf(false) }
    var showServicesFilterModal by remember { mutableStateOf(false) }
    var servicesFilterState by remember { mutableStateOf(ServiceOfferingFilterState()) }
    var servicesActiveFilterCount by remember { mutableIntStateOf(0) }

    val categoriesGridState = rememberLazyGridState()

    // Load all service offerings
    LaunchedEffect(Unit) {
        serviceOfferingsViewModel.loadAllOfferings(GetAllOfferingsParams(limit = 20))
    }

    // Debounced search for categories
    val debouncedCategoriesQuery = remember { mutableStateOf("") }
    LaunchedEffect(categoriesSearchQuery) {
        if (categoriesSearchQuery.isNotEmpty()) {
            isCategoriesSearching = true
            delay(300)
            debouncedCategoriesQuery.value = categoriesSearchQuery.lowercase()
            isCategoriesSearching = false
        } else {
            debouncedCategoriesQuery.value = ""
            isCategoriesSearching = false
        }
    }

    // Debounced search for services
    val debouncedServicesQuery = remember { mutableStateOf("") }
    LaunchedEffect(servicesSearchQuery) {
        if (servicesSearchQuery.isNotEmpty()) {
            isServicesSearching = true
            delay(300)
            debouncedServicesQuery.value = servicesSearchQuery.lowercase()
            isServicesSearching = false
        } else {
            debouncedServicesQuery.value = ""
            isServicesSearching = false
        }
    }

    val getVerticalFilter = { filter: ServiceFilterPill ->
        when (filter) {
            ServiceFilterPill.PROPERTY_SERVICES -> "HOUSING"
            ServiceFilterPill.CAREER_SERVICES -> "JOBS"
            ServiceFilterPill.COMMUNITY_SUPPORT -> "SOCIAL_SUPPORT"
            else -> null
        }
    }

    val filteredServices = remember(uiState, debouncedCategoriesQuery.value, categoriesSelectedFilter) {
        when (uiState) {
            is CommonServicesUiState.Success -> {
                val allServices = (uiState as CommonServicesUiState.Success).services
                allServices.filter { service ->
                    var matches = true
                    val verticalFilter = getVerticalFilter(categoriesSelectedFilter)
                    if (verticalFilter != null && matches) {
                        matches = service.vertical == verticalFilter
                    }
                    if (matches && debouncedCategoriesQuery.value.isNotEmpty()) {
                        matches = service.name.lowercase().contains(debouncedCategoriesQuery.value)
                    }
                    matches
                }.sortedBy { it.name }
            }
            else -> emptyList()
        }
    }

    val filteredOfferings = remember(allOfferingsState, debouncedServicesQuery.value, servicesFilterState) {
        if (allOfferingsState is ServiceOfferingsUiState.Success) {
            var offerings = (allOfferingsState as ServiceOfferingsUiState.Success).offerings
            if (debouncedServicesQuery.value.isNotEmpty()) {
                offerings = offerings.filter {
                    it.title.lowercase().contains(debouncedServicesQuery.value) ||
                            it.professionalName.lowercase().contains(debouncedServicesQuery.value) ||
                            it.categoryName.lowercase().contains(debouncedServicesQuery.value)
                }
            }
            if (servicesFilterState.minPrice != null) {
                offerings = offerings.filter { it.basePrice >= servicesFilterState.minPrice!! }
            }
            if (servicesFilterState.maxPrice != null) {
                offerings = offerings.filter { it.basePrice <= servicesFilterState.maxPrice!! }
            }
            if (servicesFilterState.verifiedOnly) {
                offerings = offerings.filter { it.isVerified }
            }
            if (servicesFilterState.minRating > 0) {
                offerings = offerings.filter { it.averageRating >= servicesFilterState.minRating }
            }
            offerings = when (servicesFilterState.selectedSort) {
                ServiceSortOption.RECENT -> offerings
                ServiceSortOption.LOWEST_PRICE -> offerings.sortedBy { it.basePrice }
                ServiceSortOption.HIGHEST_PRICE -> offerings.sortedByDescending { it.basePrice }
                ServiceSortOption.HIGHEST_RATED -> offerings.sortedByDescending { it.averageRating }
            }
            offerings
        } else {
            emptyList()
        }
    }

    LaunchedEffect(categoriesSelectedFilter, categoriesSearchQuery) {
        var count = 0
        if (categoriesSelectedFilter != ServiceFilterPill.ALL) count++
        if (categoriesSearchQuery.isNotEmpty()) count++
        categoriesActiveFilterCount = count
    }

    LaunchedEffect(servicesFilterState, servicesSearchQuery) {
        var count = 0
        if (servicesFilterState.selectedSort != ServiceSortOption.RECENT) count++
        if (servicesFilterState.minPrice != null || servicesFilterState.maxPrice != null) count++
        if (servicesFilterState.verifiedOnly) count++
        if (servicesFilterState.minRating > 0) count++
        if (servicesSearchQuery.isNotEmpty()) count++
        servicesActiveFilterCount = count
    }

    Scaffold(
        containerColor = colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Header with back button and title
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = colorScheme.background,
                shadowElevation = 0.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = horizontalPadding, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.size(if (isTablet) 40.dp else 36.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = colorScheme.primary.copy(0.08f)
                        ) {
                            Box(
                                modifier = Modifier.size(if (isTablet) 40.dp else 36.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = colorScheme.primary,
                                    modifier = Modifier.size(if (isTablet) 24.dp else 20.dp)
                                )
                            }
                        }
                    }
                    Text(
                        text = "All Services",
                        fontSize = if (isTablet) 24.sp else 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onSurface
                    )
                }
            }

            // Animated Segmented Switch
            AnimatedSegmentedSwitch(
                currentView = currentView,
                onViewSelected = { view ->
                    currentView = view
                },
                colorScheme = colorScheme,
                isTablet = isTablet,
                modifier = Modifier.padding(horizontal = horizontalPadding, vertical = 8.dp)
            )

            // Horizontal Pager for swiping between tabs
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                userScrollEnabled = true
            ) { page ->
                when (page) {
                    0 -> {
                        // SERVICES Tab
                        ServicesContent(
                            allOfferingsState = allOfferingsState,
                            filteredOfferings = filteredOfferings,
                            searchQuery = servicesSearchQuery,
                            onSearchQueryChange = { servicesSearchQuery = it },
                            isSearching = isServicesSearching,
                            onFilterClick = { showServicesFilterModal = true },
                            activeFilterCount = servicesActiveFilterCount,
                            isTablet = isTablet,
                            onOfferingClick = onOfferingClick,
                            onRefresh = { serviceOfferingsViewModel.refreshAllOfferings() },
                            onLoadMore = { serviceOfferingsViewModel.loadMoreAllOfferings() },
                            hasMoreData = (allOfferingsState as? ServiceOfferingsUiState.Success)?.hasMore ?: false,
                            colorScheme = colorScheme,
                            horizontalPadding = horizontalPadding
                        )
                    }
                    1 -> {
                        // CATEGORIES Tab
                        CategoriesContent(
                            uiState = uiState,
                            filteredServices = filteredServices,
                            searchQuery = categoriesSearchQuery,
                            onSearchQueryChange = { categoriesSearchQuery = it },
                            isSearching = isCategoriesSearching,
                            onFilterClick = { showCategoriesFilterModal = true },
                            activeFilterCount = categoriesActiveFilterCount,
                            selectedFilter = categoriesSelectedFilter,
                            onFilterSelected = { categoriesSelectedFilter = it },
                            gridColumns = gridColumns,
                            horizontalPadding = horizontalPadding,
                            gridSpacing = gridSpacing,
                            gridState = categoriesGridState,
                            onServiceClick = onServiceClick,
                            onSubcategoriesClick = onSubcategoriesClick,
                            onRefresh = { viewModel.refresh() },
                            onClearFilters = {
                                categoriesSearchQuery = ""
                                categoriesSelectedFilter = ServiceFilterPill.ALL
                                categoriesFocusManager.clearFocus()
                            },
                            colorScheme = colorScheme,
                            isTablet = isTablet
                        )
                    }
                }
            }
        }
    }

    if (showCategoriesFilterModal) {
        AllServicesFilterBottomSheet(
            selectedFilter = categoriesSelectedFilter,
            onFilterSelected = { categoriesSelectedFilter = it },
            onDismiss = { showCategoriesFilterModal = false },
            onApply = { showCategoriesFilterModal = false },
            onReset = {
                categoriesSelectedFilter = ServiceFilterPill.ALL
                categoriesSearchQuery = ""
                categoriesActiveFilterCount = 0
                showCategoriesFilterModal = false
            },
            colorScheme = colorScheme,
            isTablet = isTablet
        )
    }

    if (showServicesFilterModal) {
        ServicesFilterBottomSheet(
            filterState = servicesFilterState,
            onFilterChange = { servicesFilterState = it },
            onDismiss = { showServicesFilterModal = false },
            onApply = { showServicesFilterModal = false },
            onReset = {
                servicesFilterState = ServiceOfferingFilterState()
                servicesSearchQuery = ""
                showServicesFilterModal = false
            },
            colorScheme = colorScheme,
            isTablet = isTablet
        )
    }
}


@Composable
private fun AnimatedSegmentedSwitch(
    currentView: AllServicesView,
    onViewSelected: (AllServicesView) -> Unit,
    colorScheme: ColorScheme,
    isTablet: Boolean,
    modifier: Modifier = Modifier
) {
    val indicatorWidth = if (isTablet) 80.dp else 60.dp

    // Calculate the bias for BiasAlignment or use an animated float fraction for position.
    // 0f represents the exact center of the first half, 1f represents the center of the second half.
    val targetFraction = if (currentView == AllServicesView.SERVICES) 0f else 1f
    val indicatorPositionFraction = animateFloatAsState(
        targetValue = targetFraction,
        animationSpec = tween(
            durationMillis = 250,
            easing = FastOutSlowInEasing
        ),
        label = "indicatorPosition"
    )

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // Services Tab
            val isServicesSelected = currentView == AllServicesView.SERVICES
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { onViewSelected(AllServicesView.SERVICES) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Services",
                    fontSize = if (isTablet) 16.sp else 14.sp,
                    fontWeight = if (isServicesSelected) FontWeight.SemiBold else FontWeight.Medium,
                    color = if (isServicesSelected) colorScheme.primary else colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }

            // Categories Tab
            val isCategoriesSelected = currentView == AllServicesView.CATEGORIES
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { onViewSelected(AllServicesView.CATEGORIES) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Categories",
                    fontSize = if (isTablet) 16.sp else 14.sp,
                    fontWeight = if (isCategoriesSelected) FontWeight.SemiBold else FontWeight.Medium,
                    color = if (isCategoriesSelected) colorScheme.primary else colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }
        }

        // Animated underline track and indicator
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(colorScheme.surfaceVariant.copy(alpha = 0.3f))
        ) {
            // LayoutBox to dynamically position the indicator within its track
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .layout { measurable, constraints ->
                        val placeable = measurable.measure(constraints)
                        layout(constraints.maxWidth, constraints.maxHeight) {
                            // Total width allocated for one full tab segment
                            val segmentWidth = constraints.maxWidth / 2

                            // Center the fixed indicator width within the active segment
                            val indicatorLeftOffset = (segmentWidth - indicatorWidth.roundToPx()) / 2

                            // Interpolate the exact X coordinate based on animation fraction
                            val xPosition = (indicatorPositionFraction.value * segmentWidth).toInt() + indicatorLeftOffset

                            placeable.placeRelative(xPosition, 0)
                        }
                    }
            ) {
                Box(
                    modifier = Modifier
                        .width(indicatorWidth)
                        .fillMaxHeight()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    colorScheme.primary,
                                    colorScheme.primary.copy(alpha = 0.7f)
                                )
                            )
                        )
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun ServicesContent(
    allOfferingsState: ServiceOfferingsUiState,
    filteredOfferings: List<ServiceOffering>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    isSearching: Boolean,
    onFilterClick: () -> Unit,
    activeFilterCount: Int,
    isTablet: Boolean,
    onOfferingClick: (String) -> Unit,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    hasMoreData: Boolean,
    colorScheme: ColorScheme,
    horizontalPadding: androidx.compose.ui.unit.Dp
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Sticky Search bar
        ServicesSearchBarWithFilter(
            searchQuery = searchQuery,
            onSearchQueryChange = onSearchQueryChange,
            isSearching = isSearching,
            onFilterClick = onFilterClick,
            activeFilterCount = activeFilterCount,
            colorScheme = colorScheme,
            isTablet = isTablet
        )

        // Offerings content
        when (allOfferingsState) {
            is ServiceOfferingsUiState.Loading -> {
                // Reuse the existing ServiceOfferingsLoadingSkeleton from your ServiceOfferingsScreen
                // We need to determine grid columns based on tablet/phone
                val gridColumns = if (isTablet) 2 else 1
                ServiceOfferingsLoadingSkeleton(gridColumns = gridColumns)
            }

            is ServiceOfferingsUiState.Success -> {
                if (filteredOfferings.isEmpty()) {
                    ServicesEmptyState(
                        hasSearchOrFilter = searchQuery.isNotEmpty() || activeFilterCount > 0,
                        onClearFilters = {
                            onSearchQueryChange("")
                            onFilterClick()
                        },
                        colorScheme = colorScheme,
                        isTablet = isTablet,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(if (isTablet) 2 else 1),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            horizontal = horizontalPadding,
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
                                    Button(
                                        onClick = onLoadMore,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = colorScheme.primary.copy(alpha = 0.1f),
                                            contentColor = colorScheme.primary
                                        )
                                    ) {
                                        Text("Load More")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            is ServiceOfferingsUiState.Error -> {
                val state = allOfferingsState as ServiceOfferingsUiState.Error
                ServicesErrorState(
                    message = state.message,
                    onRetry = onRefresh,
                    colorScheme = colorScheme,
                    isTablet = isTablet,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}


@Composable
private fun ServicesSearchBarWithFilter(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    isSearching: Boolean,
    onFilterClick: () -> Unit,
    activeFilterCount: Int,
    colorScheme: ColorScheme,
    isTablet: Boolean
) {
    val horizontalPadding = if (isTablet) 32.dp else 16.dp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorScheme.surface)
            .padding(horizontal = horizontalPadding, vertical = if (isTablet) 12.dp else 8.dp)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp), ambientColor = colorScheme.scrim.copy(0.08f)),
            shape = RoundedCornerShape(16.dp),
            color = colorScheme.surface,
            tonalElevation = 1.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = if (isTablet) 20.dp else 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Outlined.Search,
                    contentDescription = "Search",
                    tint = colorScheme.onSurfaceVariant.copy(0.6f),
                    modifier = Modifier.size(if (isTablet) 24.dp else 20.dp)
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
                                    text = "Search service offerings...",
                                    color = colorScheme.onSurfaceVariant.copy(0.5f),
                                    fontSize = if (isTablet) 16.sp else 14.sp,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            innerTextField()
                        }
                    },
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = if (isTablet) 16.sp else 14.sp,
                        color = colorScheme.onSurface
                    )
                )
                if (searchQuery.isNotEmpty()) {
                    IconButton(
                        onClick = { onSearchQueryChange("") },
                        modifier = Modifier.size(if (isTablet) 36.dp else 32.dp)
                    ) {
                        Icon(
                            Icons.Outlined.Close,
                            contentDescription = "Clear",
                            tint = colorScheme.onSurfaceVariant.copy(0.6f),
                            modifier = Modifier.size(if (isTablet) 20.dp else 16.dp)
                        )
                    }
                } else if (isSearching) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(if (isTablet) 24.dp else 20.dp),
                        strokeWidth = 2.dp,
                        color = colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Box {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (activeFilterCount > 0) colorScheme.primary.copy(0.1f) else colorScheme.surfaceVariant.copy(0.3f),
                        modifier = Modifier
                            .clickable { onFilterClick() }
                            .padding(horizontal = if (isTablet) 14.dp else 10.dp, vertical = if (isTablet) 8.dp else 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Outlined.Tune,
                                contentDescription = "Filter",
                                tint = if (activeFilterCount > 0) colorScheme.primary else colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(if (isTablet) 18.dp else 14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "Filters",
                                fontSize = if (isTablet) 14.sp else 12.sp,
                                color = if (activeFilterCount > 0) colorScheme.primary else colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    if (activeFilterCount > 0) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = 8.dp, y = (-4).dp)
                        ) {
                            Surface(
                                color = colorScheme.primary,
                                shape = CircleShape,
                                modifier = Modifier.size(if (isTablet) 20.dp else 16.dp)
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = activeFilterCount.toString(),
                                        color = colorScheme.onPrimary,
                                        fontSize = if (isTablet) 11.sp else 10.sp,
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
}

@Composable
private fun CategoriesContent(
    uiState: CommonServicesUiState,
    filteredServices: List<DiscoveryCategory>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    isSearching: Boolean,
    onFilterClick: () -> Unit,
    activeFilterCount: Int,
    selectedFilter: ServiceFilterPill,
    onFilterSelected: (ServiceFilterPill) -> Unit,
    gridColumns: Int,
    horizontalPadding: androidx.compose.ui.unit.Dp,
    gridSpacing: androidx.compose.ui.unit.Dp,
    gridState: androidx.compose.foundation.lazy.grid.LazyGridState,
    onServiceClick: (String, String, String) -> Unit,
    onSubcategoriesClick: (String, String, String) -> Unit,
    onRefresh: () -> Unit,
    onClearFilters: () -> Unit,
    colorScheme: ColorScheme,
    isTablet: Boolean
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Search bar with filter button (same design as Services tab)
        CategoriesSearchBarWithFilter(
            searchQuery = searchQuery,
            onSearchQueryChange = onSearchQueryChange,
            isSearching = isSearching,
            onFilterClick = onFilterClick,
            activeFilterCount = activeFilterCount,
            colorScheme = colorScheme,
            isTablet = isTablet
        )

        // Filter pills row
        CategoriesFilterPills(
            selectedFilter = selectedFilter,
            onFilterSelected = onFilterSelected,
            colorScheme = colorScheme,
            isTablet = isTablet
        )

        // Categories grid
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
            when (uiState) {
                is CommonServicesUiState.Loading -> {
                    items(gridColumns * 3) { index ->
                        AllServicesSkeletonItem(
                            colorScheme = colorScheme,
                            index = index,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                is CommonServicesUiState.Success -> {
                    if (filteredServices.isEmpty()) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            AllServicesEmptyState(
                                hasSearchOrFilter = searchQuery.isNotEmpty() || selectedFilter != ServiceFilterPill.ALL,
                                onClearFilters = onClearFilters,
                                colorScheme = colorScheme,
                                isTablet = isTablet
                            )
                        }
                    } else {
                        items(filteredServices, key = { it.id }) { category ->
                            AllServicesGridItem(
                                category = category,
                                colorScheme = colorScheme,
                                onClick = {
                                    if (category.hasSubcategories) {
                                        onSubcategoriesClick(category.id, category.name, category.vertical)
                                    } else {
                                        onServiceClick(category.id, category.name, category.vertical)
                                    }
                                }
                            )
                        }
                    }
                }

                is CommonServicesUiState.Error -> {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        AllServicesErrorState(
                            message = (uiState as CommonServicesUiState.Error).message,
                            onRetry = onRefresh,
                            colorScheme = colorScheme,
                            isTablet = isTablet
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoriesSearchBarWithFilter(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    isSearching: Boolean,
    onFilterClick: () -> Unit,
    activeFilterCount: Int,
    colorScheme: ColorScheme,
    isTablet: Boolean
) {
    val horizontalPadding = if (isTablet) 32.dp else 16.dp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorScheme.surface)
            .padding(horizontal = horizontalPadding, vertical = if (isTablet) 12.dp else 8.dp)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp), ambientColor = colorScheme.scrim.copy(0.08f)),
            shape = RoundedCornerShape(16.dp),
            color = colorScheme.surface,
            tonalElevation = 1.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = if (isTablet) 20.dp else 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Outlined.Search,
                    contentDescription = "Search",
                    tint = colorScheme.onSurfaceVariant.copy(0.6f),
                    modifier = Modifier.size(if (isTablet) 24.dp else 20.dp)
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
                                    text = "Search categories...",
                                    color = colorScheme.onSurfaceVariant.copy(0.5f),
                                    fontSize = if (isTablet) 16.sp else 14.sp,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            innerTextField()
                        }
                    },
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = if (isTablet) 16.sp else 14.sp,
                        color = colorScheme.onSurface
                    )
                )
                if (searchQuery.isNotEmpty()) {
                    IconButton(
                        onClick = { onSearchQueryChange("") },
                        modifier = Modifier.size(if (isTablet) 36.dp else 32.dp)
                    ) {
                        Icon(
                            Icons.Outlined.Close,
                            contentDescription = "Clear",
                            tint = colorScheme.onSurfaceVariant.copy(0.6f),
                            modifier = Modifier.size(if (isTablet) 20.dp else 16.dp)
                        )
                    }
                } else if (isSearching) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(if (isTablet) 24.dp else 20.dp),
                        strokeWidth = 2.dp,
                        color = colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Box {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (activeFilterCount > 0) colorScheme.primary.copy(0.1f) else colorScheme.surfaceVariant.copy(0.3f),
                        modifier = Modifier
                            .clickable { onFilterClick() }
                            .padding(horizontal = if (isTablet) 14.dp else 10.dp, vertical = if (isTablet) 8.dp else 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Outlined.Tune,
                                contentDescription = "Filter",
                                tint = if (activeFilterCount > 0) colorScheme.primary else colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(if (isTablet) 18.dp else 14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "Filters",
                                fontSize = if (isTablet) 14.sp else 12.sp,
                                color = if (activeFilterCount > 0) colorScheme.primary else colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    if (activeFilterCount > 0) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = 8.dp, y = (-4).dp)
                        ) {
                            Surface(
                                color = colorScheme.primary,
                                shape = CircleShape,
                                modifier = Modifier.size(if (isTablet) 20.dp else 16.dp)
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = activeFilterCount.toString(),
                                        color = colorScheme.onPrimary,
                                        fontSize = if (isTablet) 11.sp else 10.sp,
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
}

@Composable
private fun CategoriesFilterPills(
    selectedFilter: ServiceFilterPill,
    onFilterSelected: (ServiceFilterPill) -> Unit,
    colorScheme: ColorScheme,
    isTablet: Boolean
) {
    val horizontalPadding = if (isTablet) 32.dp else 16.dp

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorScheme.surface)
            .padding(horizontal = horizontalPadding, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val filters = listOf(
            ServiceFilterPill.ALL to "All Services",
            ServiceFilterPill.PROPERTY_SERVICES to "Property Services",
            ServiceFilterPill.CAREER_SERVICES to "Career Services",
            ServiceFilterPill.COMMUNITY_SUPPORT to "Community Support"
        )

        items(filters.size) { index ->
            val (filter, displayName) = filters[index]
            val isSelected = selectedFilter == filter
            val filterColor = when (filter) {
                ServiceFilterPill.PROPERTY_SERVICES -> colorScheme.primary
                ServiceFilterPill.CAREER_SERVICES -> colorScheme.secondary
                ServiceFilterPill.COMMUNITY_SUPPORT -> colorScheme.tertiary
                else -> colorScheme.primary
            }

            FilterChip(
                selected = isSelected,
                onClick = { onFilterSelected(filter) },
                label = {
                    Text(
                        displayName,
                        fontSize = if (isTablet) 14.sp else 13.sp,
                        fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = filterColor,
                    selectedLabelColor = colorScheme.onPrimary,
                    containerColor = colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    labelColor = filterColor
                ),
                border = if (isSelected) null else BorderStroke(1.dp, colorScheme.outlineVariant.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(32.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ServicesFilterBottomSheet(
    filterState: ServiceOfferingFilterState,
    onFilterChange: (ServiceOfferingFilterState) -> Unit,
    onDismiss: () -> Unit,
    onApply: () -> Unit,
    onReset: () -> Unit,
    colorScheme: ColorScheme,
    isTablet: Boolean
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
                .padding(horizontal = if (isTablet) 32.dp else 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filter Service Offerings",
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

            // Sort By Section
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
                ServiceSortOption.values().forEach { sortOption ->
                    val isSelected = localFilterState.selectedSort == sortOption
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            localFilterState = localFilterState.copy(selectedSort = sortOption)
                        },
                        label = {
                            Text(
                                sortOption.displayName,
                                fontSize = if (isTablet) 13.sp else 12.sp,
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
                    value = localFilterState.minPrice?.toString() ?: "",
                    onValueChange = {
                        localFilterState = localFilterState.copy(
                            minPrice = it.toIntOrNull()
                        )
                    },
                    placeholder = { Text("Min", fontSize = if (isTablet) 14.sp else 13.sp) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(fontSize = if (isTablet) 14.sp else 13.sp)
                )

                OutlinedTextField(
                    value = localFilterState.maxPrice?.toString() ?: "",
                    onValueChange = {
                        localFilterState = localFilterState.copy(
                            maxPrice = it.toIntOrNull()
                        )
                    },
                    placeholder = { Text("Max", fontSize = if (isTablet) 14.sp else 13.sp) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(fontSize = if (isTablet) 14.sp else 13.sp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Verified Only Section
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
                    fontSize = if (isTablet) 14.sp else 13.sp,
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

            // Minimum Rating Section
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
                                fontSize = if (isTablet) 13.sp else 12.sp
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

            // Active Filters Summary
            val activeFilters = mutableListOf<String>()
            if (localFilterState.selectedSort != ServiceSortOption.RECENT) {
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
                        fontSize = if (isTablet) 11.sp else 10.sp,
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

            // Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        localFilterState = ServiceOfferingFilterState()
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
                    Text("Reset", fontSize = if (isTablet) 15.sp else 14.sp, fontWeight = FontWeight.Medium)
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
                    Text("Apply Filters", fontSize = if (isTablet) 16.sp else 15.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ==================== SKELETON & EMPTY STATE COMPOSABLES ====================

@Composable
private fun ServicesSkeletonItem(colorScheme: ColorScheme) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmerAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmerAlpha"
    )
    val skeletonColor = colorScheme.surfaceVariant.copy(alpha = shimmerAlpha)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(skeletonColor)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(16.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(skeletonColor)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(12.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(skeletonColor)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.3f)
                        .height(20.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(skeletonColor)
                )
            }
        }
    }
}

@Composable
private fun ServicesEmptyState(
    hasSearchOrFilter: Boolean,
    onClearFilters: () -> Unit,
    colorScheme: ColorScheme,
    isTablet: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size(if (isTablet) 100.dp else 80.dp),
            color = colorScheme.primary.copy(0.05f),
            shape = CircleShape
        ) {
            Icon(
                Icons.Outlined.SearchOff,
                null,
                tint = colorScheme.primary,
                modifier = Modifier.padding(if (isTablet) 24.dp else 20.dp)
            )
        }
        Spacer(Modifier.height(if (isTablet) 28.dp else 24.dp))
        Text(
            if (hasSearchOrFilter) "No matching service offerings" else "No service offerings available",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface,
                fontSize = if (isTablet) 20.sp else 18.sp
            )
        )
        Spacer(Modifier.height(8.dp))
        Text(
            if (hasSearchOrFilter)
                "Try adjusting your search or filter criteria"
            else
                "Check back later for service offerings",
            style = MaterialTheme.typography.bodySmall.copy(
                color = colorScheme.onSurfaceVariant,
                fontSize = if (isTablet) 15.sp else 14.sp
            ),
            textAlign = TextAlign.Center
        )
        if (hasSearchOrFilter) {
            Spacer(Modifier.height(if (isTablet) 36.dp else 32.dp))
            Button(
                onClick = onClearFilters,
                colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Clear Filters", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun ServicesErrorState(
    message: String,
    onRetry: () -> Unit,
    colorScheme: ColorScheme,
    isTablet: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size(if (isTablet) 100.dp else 80.dp),
            color = colorScheme.error.copy(0.05f),
            shape = CircleShape
        ) {
            Icon(
                Icons.Outlined.Error,
                null,
                tint = colorScheme.error,
                modifier = Modifier.padding(if (isTablet) 24.dp else 20.dp)
            )
        }
        Spacer(Modifier.height(if (isTablet) 28.dp else 24.dp))
        Text(
            "Something went wrong",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface,
                fontSize = if (isTablet) 20.sp else 18.sp
            )
        )
        Spacer(Modifier.height(8.dp))
        Text(
            message,
            style = MaterialTheme.typography.bodySmall.copy(
                color = colorScheme.onSurfaceVariant,
                fontSize = if (isTablet) 15.sp else 14.sp
            ),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(if (isTablet) 36.dp else 32.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Try Again", fontWeight = FontWeight.Bold)
        }
    }
}

// ==================== EXISTING COMPOSABLES (KEPT AS-IS) ====================

@Composable
private fun AllServicesHeader(
    onNavigateBack: () -> Unit,
    colorScheme: ColorScheme,
    isTablet: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorScheme.background)
            .padding(
                horizontal = if (isTablet) 32.dp else 16.dp,
                vertical = if (isTablet) 20.dp else 12.dp
            )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier.size(if (isTablet) 48.dp else 40.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = colorScheme.primary.copy(0.08f)
                ) {
                    Box(
                        modifier = Modifier.size(if (isTablet) 48.dp else 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = colorScheme.primary,
                            modifier = Modifier.size(if (isTablet) 28.dp else 20.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(if (isTablet) 12.dp else 8.dp))
        Text(
            text = "All Services",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.ExtraBold,
                color = colorScheme.onSurface,
                letterSpacing = (-0.5).sp,
                fontSize = if (isTablet) 36.sp else 28.sp
            )
        )
        Spacer(modifier = Modifier.height(if (isTablet) 8.dp else 4.dp))
        Text(
            text = "Browse all professional services across categories",
            style = MaterialTheme.typography.bodySmall.copy(
                color = colorScheme.onSurfaceVariant,
                fontSize = if (isTablet) 16.sp else 14.sp
            )
        )
    }
}



@Composable
private fun AllServicesFilterPills(
    selectedFilter: ServiceFilterPill,
    onFilterSelected: (ServiceFilterPill) -> Unit,
    colorScheme: ColorScheme,
    isTablet: Boolean
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 0.dp, vertical = if (isTablet) 12.dp else 8.dp),
        horizontalArrangement = Arrangement.spacedBy(if (isTablet) 12.dp else 8.dp)
    ) {
        val filters = listOf(
            ServiceFilterPill.ALL to "All Services",
            ServiceFilterPill.PROPERTY_SERVICES to "Property Services",
            ServiceFilterPill.CAREER_SERVICES to "Career Services",
            ServiceFilterPill.COMMUNITY_SUPPORT to "Community Support"
        )
        items(filters.size) { index ->
            val (filter, displayName) = filters[index]
            val isSelected = selectedFilter == filter
            val filterColor = when (filter) {
                ServiceFilterPill.PROPERTY_SERVICES -> colorScheme.primary
                ServiceFilterPill.CAREER_SERVICES -> colorScheme.secondary
                ServiceFilterPill.COMMUNITY_SUPPORT -> colorScheme.tertiary
                else -> colorScheme.primary
            }
            FilterChip(
                selected = isSelected,
                onClick = { onFilterSelected(filter) },
                label = {
                    Text(
                        displayName,
                        fontSize = if (isTablet) 14.sp else 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = filterColor,
                    selectedLabelColor = colorScheme.onPrimary,
                    containerColor = if (isSelected) filterColor.copy(alpha = 0.1f) else colorScheme.surface,
                    labelColor = filterColor
                ),
                border = if (isSelected) null else BorderStroke(1.dp, colorScheme.outlineVariant),
                shape = RoundedCornerShape(30.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AllServicesFilterBottomSheet(
    selectedFilter: ServiceFilterPill,
    onFilterSelected: (ServiceFilterPill) -> Unit,
    onDismiss: () -> Unit,
    onApply: () -> Unit,
    onReset: () -> Unit,
    colorScheme: ColorScheme,
    isTablet: Boolean
) {
    var localSelectedFilter by remember { mutableStateOf(selectedFilter) }
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
                .padding(horizontal = if (isTablet) 32.dp else 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filter Categories",
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
                text = "Service Category",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            val filterOptions = listOf(
                ServiceFilterPill.ALL to "All Services",
                ServiceFilterPill.PROPERTY_SERVICES to "Property Services",
                ServiceFilterPill.CAREER_SERVICES to "Career Services",
                ServiceFilterPill.COMMUNITY_SUPPORT to "Community Support"
            )
            filterOptions.forEach { (filter, displayName) ->
                val isSelected = localSelectedFilter == filter
                val filterColor = when (filter) {
                    ServiceFilterPill.PROPERTY_SERVICES -> colorScheme.primary
                    ServiceFilterPill.CAREER_SERVICES -> colorScheme.secondary
                    ServiceFilterPill.COMMUNITY_SUPPORT -> colorScheme.tertiary
                    else -> colorScheme.primary
                }
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { localSelectedFilter = filter },
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) filterColor.copy(alpha = 0.08f) else Color.Transparent
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { localSelectedFilter = filter },
                                colors = RadioButtonDefaults.colors(selectedColor = filterColor)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = displayName,
                                fontSize = 14.sp,
                                color = colorScheme.onSurface,
                                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
                            )
                        }
                        if (isSelected) {
                            Icon(
                                Icons.Outlined.Check,
                                contentDescription = null,
                                tint = filterColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        localSelectedFilter = ServiceFilterPill.ALL
                        onReset()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, colorScheme.outlineVariant),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = colorScheme.onSurfaceVariant)
                ) {
                    Text("Reset All Filters", fontSize = 15.sp, fontWeight = FontWeight.Medium)
                }
                Button(
                    onClick = {
                        onFilterSelected(localSelectedFilter)
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
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp, pressedElevation = 4.dp)
                ) {
                    Text("Apply Filters", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun AllServicesGridItem(
    category: DiscoveryCategory,
    colorScheme: ColorScheme,
    onClick: () -> Unit
) {
    val icon = getIconForService(category.name, category.vertical)
    val pillarColor = when (category.vertical) {
        "HOUSING" -> colorScheme.primary
        "JOBS" -> colorScheme.secondary
        "SOCIAL_SUPPORT" -> colorScheme.tertiary
        else -> colorScheme.primary
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.size(64.dp), contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(pillarColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = category.name, tint = pillarColor, modifier = Modifier.size(32.dp))
            }
            if (category.hasSubcategories) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(pillarColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "📁", fontSize = 10.sp, color = colorScheme.onPrimary)
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = category.name,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = colorScheme.onSurface,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
        )
        if (category.hasSubcategories) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Browse ↓", fontSize = 9.sp, color = pillarColor, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun AllServicesSkeletonItem(
    colorScheme: ColorScheme,
    index: Int,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmerAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmerAlpha"
    )
    val skeletonColor = colorScheme.surfaceVariant.copy(alpha = shimmerAlpha)
    Column(modifier = modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.size(64.dp).clip(CircleShape).background(skeletonColor))
        Spacer(modifier = Modifier.height(8.dp))
        Box(modifier = Modifier.fillMaxWidth(0.8f).height(12.dp).clip(RoundedCornerShape(4.dp)).background(skeletonColor))
        Spacer(modifier = Modifier.height(4.dp))
        Box(modifier = Modifier.fillMaxWidth(0.6f).height(12.dp).clip(RoundedCornerShape(4.dp)).background(skeletonColor.copy(alpha = 0.5f)))
    }
}

@Composable
private fun AllServicesEmptyState(
    hasSearchOrFilter: Boolean,
    onClearFilters: () -> Unit,
    colorScheme: ColorScheme,
    isTablet: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size(if (isTablet) 100.dp else 80.dp),
            color = colorScheme.primary.copy(0.05f),
            shape = CircleShape
        ) {
            Icon(Icons.Outlined.SearchOff, null, tint = colorScheme.primary, modifier = Modifier.padding(if (isTablet) 24.dp else 20.dp))
        }
        Spacer(Modifier.height(if (isTablet) 28.dp else 24.dp))
        Text(
            if (hasSearchOrFilter) "No matching categories" else "No categories available",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface,
                fontSize = if (isTablet) 20.sp else 18.sp
            )
        )
        Spacer(Modifier.height(8.dp))
        Text(
            if (hasSearchOrFilter)
                "Try adjusting your search or filter criteria"
            else
                "Check back later for more categories",
            style = MaterialTheme.typography.bodySmall.copy(
                color = colorScheme.onSurfaceVariant,
                fontSize = if (isTablet) 15.sp else 14.sp
            ),
            textAlign = TextAlign.Center
        )
        if (hasSearchOrFilter) {
            Spacer(Modifier.height(if (isTablet) 36.dp else 32.dp))
            Button(
                onClick = onClearFilters,
                colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary),
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(horizontal = if (isTablet) 40.dp else 32.dp, vertical = if (isTablet) 16.dp else 12.dp)
            ) {
                Text("Clear Filters", fontWeight = FontWeight.Bold, fontSize = if (isTablet) 16.sp else 14.sp)
            }
        }
    }
}

@Composable
private fun AllServicesErrorState(
    message: String,
    onRetry: () -> Unit,
    colorScheme: ColorScheme,
    isTablet: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size(if (isTablet) 100.dp else 80.dp),
            color = colorScheme.error.copy(0.05f),
            shape = CircleShape
        ) {
            Icon(Icons.Outlined.Error, null, tint = colorScheme.error, modifier = Modifier.padding(if (isTablet) 24.dp else 20.dp))
        }
        Spacer(Modifier.height(if (isTablet) 28.dp else 24.dp))
        Text(
            "Something went wrong",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface,
                fontSize = if (isTablet) 20.sp else 18.sp
            )
        )
        Spacer(Modifier.height(8.dp))
        Text(
            message,
            style = MaterialTheme.typography.bodySmall.copy(
                color = colorScheme.onSurfaceVariant,
                fontSize = if (isTablet) 15.sp else 14.sp
            ),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(if (isTablet) 36.dp else 32.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary),
            shape = RoundedCornerShape(16.dp),
            contentPadding = PaddingValues(horizontal = if (isTablet) 40.dp else 32.dp, vertical = if (isTablet) 16.dp else 12.dp)
        ) {
            Text("Try Again", fontWeight = FontWeight.Bold, fontSize = if (isTablet) 16.sp else 14.sp)
        }
    }
}