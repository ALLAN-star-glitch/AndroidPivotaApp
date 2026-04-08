package com.example.pivota.dashboard.presentation.screens

import com.example.pivota.dashboard.presentation.viewmodels.MyListingsViewModel
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
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.PostAdd
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass
import com.example.pivota.admin.presentation.screens.AdminJobListingUiModel
import com.example.pivota.admin.presentation.screens.ApplicationFunnel
import com.example.pivota.admin.presentation.screens.JobStatus
import com.example.pivota.dashboard.domain.*
import com.example.pivota.dashboard.presentation.model.*
import com.example.pivota.dashboard.presentation.state.HousingListingUiModel
import kotlinx.coroutines.delay
import java.util.*

// Category type for filtering
enum class CategoryType {
    ALL,
    JOB,
    HOUSING,
    SERVICE
}

// Status filter state for bottom sheet
data class StatusFilterState(
    val selectedStatuses: Set<ListingStatus> = emptySet()
)

/* ────────────── SCREEN ────────────── */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyListingsScreen(
    viewModel: MyListingsViewModel = hiltViewModel(),
    onListingClick: (ListingUiModel) -> Unit,
    onJobClick: (AdminJobListingUiModel) -> Unit, // NEW: Admin job click handler
    onPostListingClick: () -> Unit,
    onNavigateBack: () -> Unit,
    onHousingViewClick: (HousingListingUiModel) -> Unit,
) {
    val colorScheme = MaterialTheme.colorScheme
    val listings by viewModel.filteredListings.collectAsStateWithLifecycle()

    // State for search and filters
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(CategoryType.ALL) }
    var statusFilterState by remember { mutableStateOf(StatusFilterState()) }
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

    // Filter listings based on category, status, and search
    val filteredListings = remember(debouncedQuery.value, selectedCategory, statusFilterState, listings) {
        listings.filter { listing ->
            var matches = true

            // Apply category filter
            when (selectedCategory) {
                CategoryType.ALL -> matches = true
                CategoryType.JOB -> matches = listing.category.label.equals("Job", ignoreCase = true)
                CategoryType.HOUSING -> matches = listing.category.label.equals("House", ignoreCase = true)
                CategoryType.SERVICE -> matches = listing.category.label.equals("Service", ignoreCase = true)
            }

            // Apply status filter
            if (statusFilterState.selectedStatuses.isNotEmpty() && matches) {
                matches = statusFilterState.selectedStatuses.contains(listing.status)
            }

            // Apply search filter
            if (debouncedQuery.value.isNotEmpty() && matches) {
                matches = listing.title.lowercase().contains(debouncedQuery.value) ||
                        listing.descriptionPreview.lowercase().contains(debouncedQuery.value)
            }

            matches
        }
    }

    // Update active filter count
    LaunchedEffect(statusFilterState) {
        activeFilterCount = statusFilterState.selectedStatuses.size
    }

    // ────────────── ADAPTIVE BREAKPOINTS ──────────────
    val windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val isWide = windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) ||
            windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)

    Scaffold(
        containerColor = colorScheme.background,
        topBar = {
            MyListingsHeader(
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
            // Category Filter Pills (sticky) - Only category filters remain
            CategoryFilterPills(
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = it },
                colorScheme = colorScheme
            )

            if (filteredListings.isEmpty()) {
                if (searchQuery.isNotEmpty() || selectedCategory != CategoryType.ALL || statusFilterState.selectedStatuses.isNotEmpty()) {
                    // No results for current filters
                    NoResultsEmptyState(
                        onClearFilters = {
                            searchQuery = ""
                            selectedCategory = CategoryType.ALL
                            statusFilterState = StatusFilterState()
                            activeFilterCount = 0
                            focusManager.clearFocus()
                        }
                    )
                } else {
                    // No listings at all
                    ElegantEmptyState(
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
                        ListingCard(
                            listing = listing,
                            onClick = {
                                // Check if it's a housing listing using the label
                                if (listing.category.label.equals("House", ignoreCase = true)) {
                                    // Since we don't have full housing data, we'll create a basic HousingListingUiModel
                                    val housingListing = HousingListingUiModel(
                                        id = listing.id,
                                        title = listing.title,
                                        price = "KES 0", // Default price
                                        location = "Nairobi", // Default location
                                        propertyType = "Apartment", // Default type
                                        description = listing.descriptionPreview,
                                        isVerified = false,
                                        isForSale = false,
                                        rating = 0.0,
                                        bedrooms = 0,
                                        bathrooms = 0,
                                        squareMeters = 0,
                                        imageRes = null,
                                        status = listing.status,
                                        views = listing.views,
                                        messages = listing.messages,
                                        requests = listing.requests
                                    )
                                    onHousingViewClick(housingListing)
                                }
                                // NEW: Handle job clicks
                                else if (listing.category.label.equals("Job", ignoreCase = true)) {
                                    // Convert the generic listing to AdminJobListingUiModel
                                    val adminJobListing = convertToAdminJobListing(listing)
                                    onJobClick(adminJobListing)
                                }
                                else {
                                    onListingClick(listing)
                                }
                            },
                            colorScheme = colorScheme
                        )
                    }
                }
            }
        }
    }

    // Status Filter Bottom Sheet
    if (showFilterModal) {
        StatusFilterBottomSheet(
            filterState = statusFilterState,
            onFilterChange = { statusFilterState = it },
            onDismiss = { showFilterModal = false },
            onApply = { showFilterModal = false },
            onReset = {
                statusFilterState = StatusFilterState()
                activeFilterCount = 0
                showFilterModal = false
            },
            colorScheme = colorScheme
        )
    }
}

// NEW: Helper function to convert generic ListingUiModel to AdminJobListingUiModel
private fun convertToAdminJobListing(listing: ListingUiModel): AdminJobListingUiModel {
    // Map ListingStatus to JobStatus
    val jobStatus = when (listing.status) {
        ListingStatus.ACTIVE -> JobStatus.ACTIVE
        ListingStatus.PAUSED -> JobStatus.PAUSED
        ListingStatus.CLOSED -> JobStatus.CLOSED
        ListingStatus.PENDING -> JobStatus.PENDING_REVIEW
        ListingStatus.REJECTED -> JobStatus.CLOSED
        ListingStatus.EXPIRED -> JobStatus.CLOSED
        ListingStatus.ARCHIVED -> JobStatus.CLOSED
        ListingStatus.DRAFT -> JobStatus.PENDING_REVIEW
        ListingStatus.AVAILABLE -> JobStatus.ACTIVE
        ListingStatus.RENTED -> JobStatus.CLOSED
        ListingStatus.SOLD -> JobStatus.CLOSED
        ListingStatus.INACTIVE -> JobStatus.CLOSED
    }

    // Create sample dates (you should get these from your data source)
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_YEAR, -5)
    val postedDate = calendar.time

    calendar.add(Calendar.DAY_OF_YEAR, 25)
    val expiryDate = calendar.time

    return AdminJobListingUiModel(
        id = listing.id,
        title = listing.title,
        companyName = "Your Company", // This should come from your data
        companyLogoUrl = null,
        location = "Nairobi, Kenya", // This should come from your data
        exactLocation = null,
        jobType = "Full-time", // This should come from your data
        status = jobStatus,
        postedDate = postedDate,
        expiryDate = expiryDate,
        views = listing.views,
        applications = listing.requests, // Map requests to applications
        newApplications = 0, // This needs to come from your data
        reviewedApplications = 0, // This needs to come from your data
        description = listing.descriptionPreview,
        requirements = emptyList(),
        skills = emptyList(),
        benefits = emptyList(),
        isVerified = true,
        employerName = "Employer Name", // This should come from your data
        employerVerified = true,
        averageTimeToApply = 0.0,
        applicationFunnel = ApplicationFunnel(
            viewed = listing.views,
            applied = listing.requests,
            reviewed = 0
        )
    )
}

/* ────────────── SEARCH HEADER with SmartMatch style ────────────── */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MyListingsHeader(
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
                            text = "My Listings",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = colorScheme.onSurface,
                                letterSpacing = (-0.5).sp
                            )
                        )
                        Text(
                            text = "Everything you've posted",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.width(40.dp))
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Search Bar (SmartMatch style with mic and filter)
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
                                        text = "Search...",
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
                        // Audio Icon (SmartMatch style)
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

                        // Filter button with badge (SmartMatch style)
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
private fun CategoryFilterPills(
    selectedCategory: CategoryType,
    onCategorySelected: (CategoryType) -> Unit,
    colorScheme: ColorScheme
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorScheme.surface),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Map category types to display info
        val categories = listOf(
            CategoryType.ALL to null to "All",
            CategoryType.JOB to Icons.Outlined.Work to "Jobs",
            CategoryType.HOUSING to Icons.Outlined.Home to "Housing",
            CategoryType.SERVICE to Icons.Outlined.Build to "Services"
        )

        items(categories) { (typeInfo) ->
            val (category, icon) = typeInfo
            val displayName = when (category) {
                CategoryType.ALL -> "All"
                CategoryType.JOB -> "Jobs"
                CategoryType.HOUSING -> "Housing"
                CategoryType.SERVICE -> "Services"
            }

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

/* ────────────── STATUS FILTER BOTTOM SHEET ────────────── */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StatusFilterBottomSheet(
    filterState: StatusFilterState,
    onFilterChange: (StatusFilterState) -> Unit,
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
                    text = "Filter by Status",
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
                text = "Listing Status",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Status chips - Using simple grid layout without chunked
            val statuses = ListingStatus.entries.toTypedArray()

            // Manual grid layout - first row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (i in 0..2) {
                    if (i < statuses.size) {
                        val status = statuses[i]
                        val isSelected = localFilterState.selectedStatuses.contains(status)
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                localFilterState = localFilterState.copy(
                                    selectedStatuses = if (isSelected) {
                                        localFilterState.selectedStatuses - status
                                    } else {
                                        localFilterState.selectedStatuses + status
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

            // Second row (if there are more than 3 statuses)
            if (statuses.size > 3) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (i in 3 until statuses.size) {
                        val status = statuses[i]
                        val isSelected = localFilterState.selectedStatuses.contains(status)
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                localFilterState = localFilterState.copy(
                                    selectedStatuses = if (isSelected) {
                                        localFilterState.selectedStatuses - status
                                    } else {
                                        localFilterState.selectedStatuses + status
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
                    // Fill remaining slots if needed
                    for (j in 0 until (3 - (statuses.size - 3))) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            if (localFilterState.selectedStatuses.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    color = colorScheme.primary.copy(0.1f),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = "${localFilterState.selectedStatuses.size} status${if (localFilterState.selectedStatuses.size > 1) "es" else ""} selected",
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
                        localFilterState = StatusFilterState()
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

/* ────────────── LISTING CARD ────────────── */

@Composable
private fun ListingCard(
    listing: ListingUiModel,
    onClick: () -> Unit,
    colorScheme: ColorScheme
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        color = colorScheme.surface,
        shadowElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxSize()
        ) {
            // Top Content
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        listing.category.label.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )
                    Spacer(Modifier.width(8.dp))
                    Box(
                        Modifier
                            .size(3.dp)
                            .background(colorScheme.outlineVariant, CircleShape)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        listing.status.name.lowercase().replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = colorScheme.onSurfaceVariant
                        )
                    )
                }

                Spacer(Modifier.height(8.dp))
                Text(
                    text = listing.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = colorScheme.onSurface
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = listing.descriptionPreview,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = colorScheme.onSurfaceVariant
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                listing.performanceHint?.let { hint ->
                    Spacer(Modifier.height(14.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(colorScheme.tertiary.copy(0.1f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.AutoAwesome,
                            null,
                            tint = colorScheme.tertiary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = when(hint) {
                                PerformanceHint.HighInterest -> "High interest this week"
                                PerformanceHint.NewResponses -> "New responses today"
                                is PerformanceHint.Custom -> hint.message
                            },
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = colorScheme.tertiary,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
            }

            // Bottom Content
            Column {
                Spacer(Modifier.height(16.dp))
                HorizontalDivider(
                    thickness = 0.5.dp,
                    color = colorScheme.outlineVariant
                )
                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        MetricItem(
                            icon = Icons.Default.Visibility,
                            count = listing.views.toString(),
                            colorScheme = colorScheme
                        )
                        MetricItem(
                            icon = Icons.Default.ChatBubbleOutline,
                            count = listing.messages.toString(),
                            colorScheme = colorScheme
                        )
                        MetricItem(
                            icon = Icons.Default.PendingActions,
                            count = listing.requests.toString(),
                            colorScheme = colorScheme
                        )
                    }
                    Icon(
                        Icons.Rounded.ChevronRight,
                        null,
                        tint = colorScheme.onSurfaceVariant.copy(0.5f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun MetricItem(
    icon: ImageVector,
    count: String,
    colorScheme: ColorScheme
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            icon,
            null,
            modifier = Modifier.size(14.dp),
            tint = colorScheme.onSurfaceVariant.copy(0.6f)
        )
        Spacer(Modifier.width(6.dp))
        Text(
            count,
            fontSize = 12.sp,
            color = colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Bold
        )
    }
}

/* ────────────── NO RESULTS EMPTY STATE ────────────── */

@Composable
private fun NoResultsEmptyState(
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
            "No matching listings",
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

/* ────────────── ELEGANT EMPTY STATE ────────────── */

@Composable
private fun ElegantEmptyState(
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
                Icons.Rounded.PostAdd,
                null,
                tint = colorScheme.primary,
                modifier = Modifier.padding(20.dp)
            )
        }
        Spacer(Modifier.height(24.dp))
        Text(
            "No listings yet",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface
            )
        )
        Text(
            "Post your first job, service, or request to get started.",
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