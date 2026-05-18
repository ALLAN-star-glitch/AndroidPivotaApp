package com.example.pivota.dashboard.presentation.screens.client_general_screens.listings_screens.professionals

import android.annotation.SuppressLint
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import com.example.pivota.dashboard.domain.model.listings_models.general.DiscoveryCategory
import com.example.pivota.dashboard.presentation.composables.client_general_composables.listings_composables.categories.getIconForService
import com.example.pivota.dashboard.presentation.viewmodels.client_general_viewmodels.AllServicesViewModel
import com.example.pivota.dashboard.presentation.state.CommonServicesUiState
import kotlinx.coroutines.delay

enum class ServiceFilterPill {
    ALL,
    PROPERTY_SERVICES,
    CAREER_SERVICES,
    COMMUNITY_SUPPORT
}

@SuppressLint("FrequentlyChangingValue")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllServicesScreen(
    viewModel: AllServicesViewModel = hiltViewModel(),
    onServiceClick: (String, String, String) -> Unit = { _, _, _ -> },
    onNavigateBack: () -> Unit = {}
) {
    val colorScheme = MaterialTheme.colorScheme
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

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

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf(ServiceFilterPill.ALL) }
    var isSearching by remember { mutableStateOf(false) }
    var showFilterModal by remember { mutableStateOf(false) }
    var activeFilterCount by remember { mutableIntStateOf(0) }
    val focusManager = LocalFocusManager.current

    val gridState = rememberLazyGridState()
    val isSearchBarPinned by remember {
        derivedStateOf {
            gridState.firstVisibleItemIndex > 1 || gridState.firstVisibleItemScrollOffset > 0
        }
    }

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

    val getVerticalFilter = { filter: ServiceFilterPill ->
        when (filter) {
            ServiceFilterPill.PROPERTY_SERVICES -> "HOUSING"
            ServiceFilterPill.CAREER_SERVICES -> "JOBS"
            ServiceFilterPill.COMMUNITY_SUPPORT -> "SOCIAL_SUPPORT"
            else -> null
        }
    }

    val filteredServices = remember(uiState, debouncedQuery.value, selectedFilter) {
        when (uiState) {
            is CommonServicesUiState.Success -> {
                val allServices = (uiState as CommonServicesUiState.Success).services
                allServices.filter { service ->
                    var matches = true

                    val verticalFilter = getVerticalFilter(selectedFilter)
                    if (verticalFilter != null && matches) {
                        matches = service.vertical == verticalFilter
                    }

                    if (matches && debouncedQuery.value.isNotEmpty()) {
                        matches = service.name.lowercase().contains(debouncedQuery.value)
                    }

                    matches
                }.sortedBy { it.name }
            }
            else -> emptyList()
        }
    }

    LaunchedEffect(selectedFilter, searchQuery) {
        var count = 0
        if (selectedFilter != ServiceFilterPill.ALL) count++
        if (searchQuery.isNotEmpty()) count++
        activeFilterCount = count
    }

    Scaffold(
        containerColor = colorScheme.background
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
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
                item(span = { GridItemSpan(maxLineSpan) }) {
                    AllServicesHeader(
                        onNavigateBack = onNavigateBack,
                        colorScheme = colorScheme,
                        isTablet = isTablet
                    )
                }

                if (!isSearchBarPinned) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        AllServicesSearchAndFilters(
                            searchQuery = searchQuery,
                            onSearchQueryChange = { searchQuery = it },
                            isSearching = isSearching,
                            onFilterClick = { showFilterModal = true },
                            activeFilterCount = activeFilterCount,
                            selectedFilter = selectedFilter,
                            onFilterSelected = { selectedFilter = it },
                            colorScheme = colorScheme,
                            isTablet = isTablet,
                            isSticky = false
                        )
                    }
                }

                if (debouncedQuery.value.isNotEmpty() && filteredServices.isNotEmpty()) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Results for \"$searchQuery\"",
                                fontSize = if (isTablet) 15.sp else 13.sp,
                                color = colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium
                            )
                            if (isSearching) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(if (isTablet) 20.dp else 16.dp),
                                    strokeWidth = 2.dp,
                                    color = colorScheme.primary
                                )
                            } else {
                                Text(
                                    text = "${filteredServices.size} services found",
                                    fontSize = if (isTablet) 14.sp else 12.sp,
                                    color = colorScheme.primary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                when (uiState) {
                    is CommonServicesUiState.Loading -> {
                        items(gridColumns * 3) {
                            AllServicesSkeletonItem(
                                colorScheme = colorScheme,
                                index = it,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    is CommonServicesUiState.Success -> {
                        if (filteredServices.isEmpty()) {
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                AllServicesEmptyState(
                                    hasSearchOrFilter = searchQuery.isNotEmpty() || selectedFilter != ServiceFilterPill.ALL,
                                    onClearFilters = {
                                        searchQuery = ""
                                        selectedFilter = ServiceFilterPill.ALL
                                        focusManager.clearFocus()
                                    },
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
                                        onServiceClick(category.id, category.name, category.vertical)
                                    }
                                )
                            }
                        }
                    }

                    is CommonServicesUiState.Error -> {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            AllServicesErrorState(
                                message = (uiState as CommonServicesUiState.Error).message,
                                onRetry = { viewModel.refresh() },
                                colorScheme = colorScheme,
                                isTablet = isTablet
                            )
                        }
                    }
                }
            }

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
                    shadowElevation = 4.dp
                ) {
                    Column {
                        AllServicesSearchAndFilters(
                            searchQuery = searchQuery,
                            onSearchQueryChange = { searchQuery = it },
                            isSearching = isSearching,
                            onFilterClick = { showFilterModal = true },
                            activeFilterCount = activeFilterCount,
                            selectedFilter = selectedFilter,
                            onFilterSelected = { selectedFilter = it },
                            colorScheme = colorScheme,
                            isTablet = isTablet,
                            isSticky = true
                        )

                        Divider(
                            color = colorScheme.outlineVariant.copy(alpha = 0.5f),
                            thickness = 1.dp,
                            modifier = Modifier.padding(horizontal = horizontalPadding)
                        )
                    }
                }
            }
        }
    }

    if (showFilterModal) {
        AllServicesFilterBottomSheet(
            selectedFilter = selectedFilter,
            onFilterSelected = { selectedFilter = it },
            onDismiss = { showFilterModal = false },
            onApply = { showFilterModal = false },
            onReset = {
                selectedFilter = ServiceFilterPill.ALL
                searchQuery = ""
                activeFilterCount = 0
                showFilterModal = false
            },
            colorScheme = colorScheme,
            isTablet = isTablet
        )
    }
}

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
private fun AllServicesSearchAndFilters(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    isSearching: Boolean,
    onFilterClick: () -> Unit,
    activeFilterCount: Int,
    selectedFilter: ServiceFilterPill,
    onFilterSelected: (ServiceFilterPill) -> Unit,
    colorScheme: ColorScheme,
    isTablet: Boolean,
    isSticky: Boolean
) {
    val horizontalPadding = if (isTablet) 32.dp else 16.dp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorScheme.surface)
            .padding(
                horizontal = horizontalPadding,
                vertical = if (isSticky) if (isTablet) 16.dp else 12.dp else 0.dp
            )
    ) {
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
                                    text = "Search services...",
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
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

        if (!isSticky) {
            Spacer(modifier = Modifier.height(if (isTablet) 16.dp else 12.dp))
        }

        AllServicesFilterPills(
            selectedFilter = selectedFilter,
            onFilterSelected = onFilterSelected,
            colorScheme = colorScheme,
            isTablet = isTablet
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { localSelectedFilter = filter },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = filterColor
                                )
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
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = colorScheme.onSurfaceVariant
                    )
                ) {
                    Text(
                        "Reset All Filters",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
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
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(pillarColor.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = category.name,
                tint = pillarColor,
                modifier = Modifier.size(32.dp)
            )
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

    Column(
        modifier = modifier.padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(skeletonColor)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(12.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(skeletonColor)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(12.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(skeletonColor.copy(alpha = 0.5f))
        )
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
            Icon(
                Icons.Outlined.SearchOff,
                null,
                tint = colorScheme.primary,
                modifier = Modifier.padding(if (isTablet) 24.dp else 20.dp)
            )
        }
        Spacer(Modifier.height(if (isTablet) 28.dp else 24.dp))

        Text(
            if (hasSearchOrFilter) "No matching services" else "No services available",
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
                "Check back later for more services",
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
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.primary
                ),
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(
                    horizontal = if (isTablet) 40.dp else 32.dp,
                    vertical = if (isTablet) 16.dp else 12.dp
                )
            ) {
                Text(
                    "Clear Filters",
                    fontWeight = FontWeight.Bold,
                    fontSize = if (isTablet) 16.sp else 14.sp
                )
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
            colors = ButtonDefaults.buttonColors(
                containerColor = colorScheme.primary
            ),
            shape = RoundedCornerShape(16.dp),
            contentPadding = PaddingValues(
                horizontal = if (isTablet) 40.dp else 32.dp,
                vertical = if (isTablet) 16.dp else 12.dp
            )
        ) {
            Text(
                "Try Again",
                fontWeight = FontWeight.Bold,
                fontSize = if (isTablet) 16.sp else 14.sp
            )
        }
    }
}