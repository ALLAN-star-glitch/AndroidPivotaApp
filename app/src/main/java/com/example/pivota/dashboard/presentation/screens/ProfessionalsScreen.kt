package com.example.pivota.dashboard.presentation.screens

import android.annotation.SuppressLint
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
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
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.example.pivota.auth.domain.model.User
import com.example.pivota.dashboard.presentation.composables.*
import com.example.pivota.dashboard.presentation.viewmodels.ProfessionalsViewModel
import kotlinx.coroutines.delay

// Category type for professional filtering
enum class ProfessionalCategoryType {
    ALL,
    ELECTRICIAN,
    PLUMBER,
    DESIGNER,
    LEGAL,
    PROPERTY,
    CARPENTER
}

// Price range filter
data class PriceRange(
    val min: Int? = null,
    val max: Int? = null
)

// Filter state for bottom sheet
data class ProfessionalFilterState(
    val minRating: Double = 0.0,
    val isVerifiedOnly: Boolean = false,
    val isSmartMatchOnly: Boolean = false
)

data class EnhancedProfessionalData(
    val id: String,
    val name: String,
    val businessName: String?,
    val profileImageUrl: String? = null,
    val coverImageUrl: String? = null,
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

@SuppressLint("FrequentlyChangingValue")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfessionalsScreen(
    viewModel: ProfessionalsViewModel = hiltViewModel(),
    onProfessionalClick: (EnhancedProfessionalData) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    user: User? = null,
    isGuestMode: Boolean = false
) {
    val colorScheme = MaterialTheme.colorScheme
    val professionals by viewModel.filteredProfessionals.collectAsStateWithLifecycle()
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp

    // Get window size class for responsive design
    val windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val windowWidthClass = windowSizeClass.windowWidthSizeClass
    val isExpanded = windowWidthClass == WindowWidthSizeClass.EXPANDED
    val isMedium = windowWidthClass == WindowWidthSizeClass.MEDIUM

    // Adaptive grid columns based on screen size
    val gridColumns = when {
        windowWidthClass == WindowWidthSizeClass.EXPANDED -> 3
        windowWidthClass == WindowWidthSizeClass.MEDIUM -> 2
        screenWidth >= 900 -> 3
        screenWidth >= 600 -> 2
        else -> 1
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

    // State for search and filters
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(ProfessionalCategoryType.ALL) }
    var filterState by remember { mutableStateOf(ProfessionalFilterState()) }
    var priceRange by remember { mutableStateOf(PriceRange()) }
    var showFilterModal by remember { mutableStateOf(false) }
    var activeFilterCount by remember { mutableIntStateOf(0) }
    var isSearching by remember { mutableStateOf(false) }
    var isRecording by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    // Track if search bar should be pinned
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

    // Filter professionals
    val filteredProfessionals = remember(debouncedQuery.value, selectedCategory, filterState, priceRange, professionals) {
        professionals.filter { professional ->
            var matches = true

            when (selectedCategory) {
                ProfessionalCategoryType.ALL -> matches = true
                ProfessionalCategoryType.ELECTRICIAN -> matches = professional.category == "Electrician"
                ProfessionalCategoryType.PLUMBER -> matches = professional.category == "Plumber"
                ProfessionalCategoryType.DESIGNER -> matches = professional.category == "Designer"
                ProfessionalCategoryType.LEGAL -> matches = professional.category == "Legal Services"
                ProfessionalCategoryType.PROPERTY -> matches = professional.category == "Property Management"
                ProfessionalCategoryType.CARPENTER -> matches = professional.category == "Carpenter"
            }

            if (filterState.minRating > 0 && matches) {
                matches = professional.rating >= filterState.minRating
            }

            if (filterState.isVerifiedOnly && matches) {
                matches = professional.isVerified
            }

            if (filterState.isSmartMatchOnly && matches) {
                matches = professional.isSmartMatch
            }

            if (matches) {
                priceRange.min?.let {
                    if (professional.startingPrice < it) matches = false
                }
                priceRange.max?.let {
                    if (professional.startingPrice > it) matches = false
                }
            }

            if (debouncedQuery.value.isNotEmpty() && matches) {
                matches = professional.name.lowercase().contains(debouncedQuery.value) ||
                        professional.businessName?.lowercase()?.contains(debouncedQuery.value) == true ||
                        professional.category.lowercase().contains(debouncedQuery.value) ||
                        professional.location.lowercase().contains(debouncedQuery.value) ||
                        professional.specialties.any { it.lowercase().contains(debouncedQuery.value) }
            }

            matches
        }
    }

    // Update active filter count
    LaunchedEffect(filterState, priceRange) {
        var count = 0
        if (filterState.minRating > 0) count++
        if (filterState.isVerifiedOnly) count++
        if (filterState.isSmartMatchOnly) count++
        if (priceRange.min != null || priceRange.max != null) count++
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
                    ProfessionalsHeader(
                        onNavigateBack = onNavigateBack,
                        colorScheme = colorScheme,
                        isExpanded = isExpanded
                    )
                }

                // Search Bar and Category Pills - ONLY show when NOT pinned
                if (!isSearchBarPinned) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        ProfessionalsSearchAndPillsSection(
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
                            isExpanded = isExpanded,
                            isSticky = false
                        )
                    }
                }

                // Search results info
                if (debouncedQuery.value.isNotEmpty() && filteredProfessionals.isNotEmpty()) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Search results for \"$searchQuery\"",
                                fontSize = if (isExpanded) 15.sp else 13.sp,
                                color = colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium
                            )
                            if (isSearching) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(if (isExpanded) 20.dp else 16.dp),
                                    strokeWidth = 2.dp,
                                    color = colorScheme.primary
                                )
                            } else {
                                Text(
                                    text = "${filteredProfessionals.size} professionals found",
                                    fontSize = if (isExpanded) 14.sp else 12.sp,
                                    color = colorScheme.primary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                // Empty state or listings
                if (filteredProfessionals.isEmpty()) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        if (searchQuery.isNotEmpty() || selectedCategory != ProfessionalCategoryType.ALL ||
                            filterState.minRating > 0 || filterState.isVerifiedOnly || filterState.isSmartMatchOnly ||
                            priceRange.min != null || priceRange.max != null) {
                            ProfessionalsNoResultsEmptyState(
                                onClearFilters = {
                                    searchQuery = ""
                                    selectedCategory = ProfessionalCategoryType.ALL
                                    filterState = ProfessionalFilterState()
                                    priceRange = PriceRange()
                                    activeFilterCount = 0
                                    focusManager.clearFocus()
                                },
                                colorScheme = colorScheme,
                                isExpanded = isExpanded
                            )
                        } else {
                            ProfessionalsEmptyState(
                                colorScheme = colorScheme,
                                isExpanded = isExpanded
                            )
                        }
                    }
                } else {
                    items(filteredProfessionals, key = { it.id }) { professional ->
                        ModernProfessionalCardV2(
                            imageUrl = professional.profileImageUrl,
                            name = professional.name,
                            profession = professional.category,
                            location = professional.location,
                            postedTime = "Active today",
                            professionalType = if (professional.businessName != null)
                                ProfessionalType.ORGANIZATION else ProfessionalType.INDIVIDUAL,
                            rating = professional.rating.toFloat(),
                            jobsCompleted = professional.completedJobs,
                            onViewDetailsClick = { onProfessionalClick(professional) },
                            modifier = Modifier.fillMaxWidth()
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
                        ProfessionalsSearchAndPillsSection(
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
                            isExpanded = isExpanded,
                            isSticky = true
                        )

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

    // Filter Modal - Adaptive presentation
    if (showFilterModal) {
        if (windowWidthClass == WindowWidthSizeClass.COMPACT) {
            ProfessionalsFilterBottomSheet(
                filterState = filterState,
                priceRange = priceRange,
                onFilterChange = { filterState = it },
                onPriceRangeChange = { priceRange = it },
                onDismiss = { showFilterModal = false },
                onApply = { showFilterModal = false },
                onReset = {
                    filterState = ProfessionalFilterState()
                    priceRange = PriceRange()
                    activeFilterCount = 0
                    showFilterModal = false
                },
                colorScheme = colorScheme,
                isExpanded = isExpanded
            )
        } else {
            AlertDialog(
                onDismissRequest = { showFilterModal = false },
                title = {
                    Text(
                        "Filter Professionals",
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    ProfessionalsFilterDialogContent(
                        filterState = filterState,
                        priceRange = priceRange,
                        onFilterChange = { filterState = it },
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
                                filterState = ProfessionalFilterState()
                                priceRange = PriceRange()
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

/* ────────────── HEADER WITH BACK BUTTON, TITLE, SUBTITLE ────────────── */

@Composable
private fun ProfessionalsHeader(
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

        // Title
        Text(
            text = "Professionals",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.ExtraBold,
                color = colorScheme.onSurface,
                letterSpacing = (-0.5).sp,
                fontSize = if (isExpanded) 36.sp else 28.sp
            )
        )

        Spacer(modifier = Modifier.height(if (isExpanded) 8.dp else 4.dp))

        // Subtitle
        Text(
            text = "Find trusted professionals near you",
            style = MaterialTheme.typography.bodySmall.copy(
                color = colorScheme.onSurfaceVariant,
                fontSize = if (isExpanded) 16.sp else 14.sp
            )
        )
    }
}

/* ────────────── SEARCH AND PILLS SECTION ────────────── */

@Composable
private fun ProfessionalsSearchAndPillsSection(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    isSearching: Boolean,
    isRecording: Boolean,
    onAudioClick: () -> Unit,
    onFilterClick: () -> Unit,
    activeFilterCount: Int,
    selectedCategory: ProfessionalCategoryType,
    onCategorySelected: (ProfessionalCategoryType) -> Unit,
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
                                    text = "Search professionals...",
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
                    if (isSearching) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(if (isExpanded) 24.dp else 20.dp),
                            strokeWidth = 2.dp,
                            color = colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }

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

        ProfessionalsCategoryFilterPills(
            selectedCategory = selectedCategory,
            onCategorySelected = onCategorySelected,
            colorScheme = colorScheme,
            isExpanded = isExpanded
        )
    }
}

/* ────────────── CATEGORY FILTER PILLS ────────────── */

@Composable
private fun ProfessionalsCategoryFilterPills(
    selectedCategory: ProfessionalCategoryType,
    onCategorySelected: (ProfessionalCategoryType) -> Unit,
    colorScheme: ColorScheme,
    isExpanded: Boolean = false
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 0.dp, vertical = if (isExpanded) 12.dp else 8.dp),
        horizontalArrangement = Arrangement.spacedBy(if (isExpanded) 12.dp else 8.dp)
    ) {
        val categories = listOf(
            ProfessionalCategoryType.ALL to "All",
            ProfessionalCategoryType.ELECTRICIAN to "Electrician",
            ProfessionalCategoryType.PLUMBER to "Plumber",
            ProfessionalCategoryType.DESIGNER to "Designer",
            ProfessionalCategoryType.LEGAL to "Legal",
            ProfessionalCategoryType.PROPERTY to "Property",
            ProfessionalCategoryType.CARPENTER to "Carpenter"
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

/* ────────────── FILTER DIALOG CONTENT (for tablets) ────────────── */

@Composable
private fun ProfessionalsFilterDialogContent(
    filterState: ProfessionalFilterState,
    priceRange: PriceRange,
    onFilterChange: (ProfessionalFilterState) -> Unit,
    onPriceRangeChange: (PriceRange) -> Unit,
    colorScheme: ColorScheme
) {
    var localFilterState by remember { mutableStateOf(filterState) }
    var localMinPrice by remember { mutableStateOf(priceRange.min?.toString() ?: "") }
    var localMaxPrice by remember { mutableStateOf(priceRange.max?.toString() ?: "") }

    LaunchedEffect(localFilterState, localMinPrice, localMaxPrice) {
        onFilterChange(localFilterState)
        onPriceRangeChange(PriceRange(min = localMinPrice.toIntOrNull(), max = localMaxPrice.toIntOrNull()))
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
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

        Column {
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
                                fontSize = 13.sp,
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
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    localFilterState = localFilterState.copy(
                        isVerifiedOnly = !localFilterState.isVerifiedOnly
                    )
                }
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Outlined.Verified,
                    contentDescription = null,
                    tint = colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    "Verified professionals only",
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
                    checkedThumbColor = colorScheme.primary,
                    checkedTrackColor = colorScheme.primary.copy(0.5f),
                    uncheckedThumbColor = colorScheme.onSurfaceVariant,
                    uncheckedTrackColor = colorScheme.outlineVariant
                )
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    localFilterState = localFilterState.copy(
                        isSmartMatchOnly = !localFilterState.isSmartMatchOnly
                    )
                }
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Outlined.AutoAwesome,
                    contentDescription = null,
                    tint = colorScheme.tertiary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    "SmartMatch professionals only",
                    fontSize = 14.sp,
                    color = colorScheme.onSurface
                )
            }
            Switch(
                checked = localFilterState.isSmartMatchOnly,
                onCheckedChange = {
                    localFilterState = localFilterState.copy(isSmartMatchOnly = it)
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = colorScheme.tertiary,
                    checkedTrackColor = colorScheme.tertiary.copy(0.5f),
                    uncheckedThumbColor = colorScheme.onSurfaceVariant,
                    uncheckedTrackColor = colorScheme.outlineVariant
                )
            )
        }
    }
}

/* ────────────── FILTER BOTTOM SHEET (for phones) ────────────── */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfessionalsFilterBottomSheet(
    filterState: ProfessionalFilterState,
    priceRange: PriceRange,
    onFilterChange: (ProfessionalFilterState) -> Unit,
    onPriceRangeChange: (PriceRange) -> Unit,
    onDismiss: () -> Unit,
    onApply: () -> Unit,
    onReset: () -> Unit,
    colorScheme: ColorScheme,
    isExpanded: Boolean = false
) {
    var localFilterState by remember { mutableStateOf(filterState) }
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filter Professionals",
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
                                fontSize = 13.sp,
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
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        localFilterState = localFilterState.copy(
                            isVerifiedOnly = !localFilterState.isVerifiedOnly
                        )
                    }
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Outlined.Verified,
                        contentDescription = null,
                        tint = colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "Verified professionals only",
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
                        checkedThumbColor = colorScheme.primary,
                        checkedTrackColor = colorScheme.primary.copy(0.5f),
                        uncheckedThumbColor = colorScheme.onSurfaceVariant,
                        uncheckedTrackColor = colorScheme.outlineVariant
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        localFilterState = localFilterState.copy(
                            isSmartMatchOnly = !localFilterState.isSmartMatchOnly
                        )
                    }
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Outlined.AutoAwesome,
                        contentDescription = null,
                        tint = colorScheme.tertiary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "SmartMatch professionals only",
                        fontSize = 14.sp,
                        color = colorScheme.onSurface
                    )
                }
                Switch(
                    checked = localFilterState.isSmartMatchOnly,
                    onCheckedChange = {
                        localFilterState = localFilterState.copy(isSmartMatchOnly = it)
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = colorScheme.tertiary,
                        checkedTrackColor = colorScheme.tertiary.copy(0.5f),
                        uncheckedThumbColor = colorScheme.onSurfaceVariant,
                        uncheckedTrackColor = colorScheme.outlineVariant
                    )
                )
            }

            val selectedCount = listOfNotNull(
                if (localFilterState.minRating > 0) 1 else null,
                if (localFilterState.isVerifiedOnly) 1 else null,
                if (localFilterState.isSmartMatchOnly) 1 else null,
                if (localMinPrice.isNotEmpty() || localMaxPrice.isNotEmpty()) 1 else null
            ).size

            if (selectedCount > 0) {
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    color = colorScheme.primary.copy(0.1f),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = "$selectedCount filter${if (selectedCount > 1) "s" else ""} selected",
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

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        localFilterState = ProfessionalFilterState()
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
                        onFilterChange(localFilterState)
                        onPriceRangeChange(
                            PriceRange(
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
private fun ProfessionalsNoResultsEmptyState(
    onClearFilters: () -> Unit,
    colorScheme: ColorScheme,
    isExpanded: Boolean = false
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size(if (isExpanded) 100.dp else 80.dp),
            color = colorScheme.primary.copy(0.05f),
            shape = CircleShape
        ) {
            Icon(
                Icons.Outlined.SearchOff,
                null,
                tint = colorScheme.primary,
                modifier = Modifier.padding(if (isExpanded) 24.dp else 20.dp)
            )
        }
        Spacer(Modifier.height(if (isExpanded) 28.dp else 24.dp))
        Text(
            "No matching professionals",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface,
                fontSize = if (isExpanded) 20.sp else 18.sp
            )
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Try adjusting your filters or search terms",
            style = MaterialTheme.typography.bodySmall.copy(
                color = colorScheme.onSurfaceVariant,
                fontSize = if (isExpanded) 15.sp else 14.sp
            ),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(if (isExpanded) 36.dp else 32.dp))
        Button(
            onClick = onClearFilters,
            colors = ButtonDefaults.buttonColors(
                containerColor = colorScheme.primary
            ),
            shape = RoundedCornerShape(16.dp),
            contentPadding = PaddingValues(horizontal = if (isExpanded) 40.dp else 32.dp, vertical = if (isExpanded) 16.dp else 12.dp)
        ) {
            Text(
                "Clear Filters",
                fontWeight = FontWeight.Bold,
                fontSize = if (isExpanded) 16.sp else 14.sp
            )
        }
    }
}

/* ────────────── EMPTY STATE ────────────── */

@Composable
private fun ProfessionalsEmptyState(
    colorScheme: ColorScheme,
    isExpanded: Boolean = false
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size(if (isExpanded) 100.dp else 80.dp),
            color = colorScheme.primary.copy(0.05f),
            shape = CircleShape
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_work),
                contentDescription = null,
                tint = colorScheme.primary,
                modifier = Modifier.padding(if (isExpanded) 24.dp else 20.dp)
            )
        }
        Spacer(Modifier.height(if (isExpanded) 28.dp else 24.dp))
        Text(
            "No professionals yet",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface,
                fontSize = if (isExpanded) 20.sp else 18.sp
            )
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Check back later for professionals in your area",
            style = MaterialTheme.typography.bodySmall.copy(
                color = colorScheme.onSurfaceVariant,
                fontSize = if (isExpanded) 15.sp else 14.sp
            ),
            textAlign = TextAlign.Center
        )
    }
}