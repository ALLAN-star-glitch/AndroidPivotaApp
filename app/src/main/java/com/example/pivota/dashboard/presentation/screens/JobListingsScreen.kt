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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass
import com.example.pivota.R
import com.example.pivota.dashboard.domain.EmployerType
import com.example.pivota.dashboard.domain.ListingStatus
import com.example.pivota.dashboard.presentation.composables.ModernJobCard
import com.example.pivota.dashboard.presentation.state.JobListingUiModel
import com.example.pivota.dashboard.presentation.viewmodels.JobListingsViewModel
import kotlinx.coroutines.delay

// Category type for job filtering
enum class JobCategoryType {
    ALL,
    FULL_TIME,
    PART_TIME,
    CONTRACT,
    INTERNSHIP
}

// Status filter state for bottom sheet
data class JobStatusFilterState(
    val selectedStatuses: Set<ListingStatus> = emptySet()
)

// Salary range filter
data class SalaryRange(
    val min: Int? = null,
    val max: Int? = null
)

// Employer type filter
enum class EmployerFilterType {
    ALL,
    ORGANIZATION,
    INDIVIDUAL
}

/* ────────────── SCREEN ────────────── */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobListingsScreen(
    viewModel: JobListingsViewModel = hiltViewModel(),
    onListingClick: (JobListingUiModel) -> Unit,
    onPostListingClick: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val listings by viewModel.filteredListings.collectAsStateWithLifecycle()

    // State for search and filters
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(JobCategoryType.ALL) }
    var statusFilterState by remember { mutableStateOf(JobStatusFilterState()) }
    var salaryRange by remember { mutableStateOf(SalaryRange()) }
    var employerFilter by remember { mutableStateOf(EmployerFilterType.ALL) }
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

    // Filter listings based on category, status, salary, employer, and search
    val filteredListings = remember(debouncedQuery.value, selectedCategory, statusFilterState, salaryRange, employerFilter, listings) {
        listings.filter { listing ->
            var matches = true

            // Apply job type category filter
            when (selectedCategory) {
                JobCategoryType.ALL -> matches = true
                JobCategoryType.FULL_TIME -> matches = listing.jobType.equals("Full-time", ignoreCase = true)
                JobCategoryType.PART_TIME -> matches = listing.jobType.equals("Part-time", ignoreCase = true)
                JobCategoryType.CONTRACT -> matches = listing.jobType.equals("Contract", ignoreCase = true)
                JobCategoryType.INTERNSHIP -> matches = listing.jobType.equals("Internship", ignoreCase = true)
            }

            // Apply status filter
            if (statusFilterState.selectedStatuses.isNotEmpty() && matches) {
                matches = statusFilterState.selectedStatuses.contains(listing.status)
            }

            // Apply employer type filter
            if (employerFilter != EmployerFilterType.ALL && matches) {
                matches = when (employerFilter) {
                    EmployerFilterType.ORGANIZATION -> listing.employerType == EmployerType.ORGANIZATION
                    EmployerFilterType.INDIVIDUAL -> listing.employerType == EmployerType.INDIVIDUAL
                    else -> true
                }
            }

            // Apply salary range filter
            if (matches) {
                val salary = extractSalaryValue(listing.salary)
                salaryRange.min?.let {
                    if (salary < it) matches = false
                }
                salaryRange.max?.let {
                    if (salary > it) matches = false
                }
            }

            // Apply search filter
            if (debouncedQuery.value.isNotEmpty() && matches) {
                matches = listing.title.lowercase().contains(debouncedQuery.value) ||
                        listing.company.lowercase().contains(debouncedQuery.value) ||
                        listing.location.lowercase().contains(debouncedQuery.value) ||
                        listing.description.lowercase().contains(debouncedQuery.value)
            }

            matches
        }
    }

    // Update active filter count
    LaunchedEffect(statusFilterState, salaryRange, employerFilter) {
        var count = statusFilterState.selectedStatuses.size
        if (salaryRange.min != null || salaryRange.max != null) count++
        if (employerFilter != EmployerFilterType.ALL) count++
        activeFilterCount = count
    }

    // ────────────── ADAPTIVE BREAKPOINTS ──────────────
    val windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val isWide = windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) ||
            windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)

    Scaffold(
        containerColor = colorScheme.background,
        topBar = {
            JobListingsHeader(
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
            JobCategoryFilterPills(
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = it },
                colorScheme = colorScheme
            )

            if (filteredListings.isEmpty()) {
                if (searchQuery.isNotEmpty() || selectedCategory != JobCategoryType.ALL ||
                    statusFilterState.selectedStatuses.isNotEmpty() || salaryRange.min != null ||
                    salaryRange.max != null || employerFilter != EmployerFilterType.ALL) {
                    // No results for current filters
                    JobNoResultsEmptyState(
                        onClearFilters = {
                            searchQuery = ""
                            selectedCategory = JobCategoryType.ALL
                            statusFilterState = JobStatusFilterState()
                            salaryRange = SalaryRange()
                            employerFilter = EmployerFilterType.ALL
                            activeFilterCount = 0
                            focusManager.clearFocus()
                        }
                    )
                } else {
                    // No listings at all
                    JobEmptyState(
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
                        ModernJobCard(
                            title = listing.title,
                            company = listing.company,
                            location = listing.location,
                            salary = listing.salary,
                            type = listing.jobType,
                            isVerified = listing.isVerified,
                            employerType = listing.employerType,
                            profileImageRes = if (listing.employerType == EmployerType.INDIVIDUAL)
                                listing.imageRes else null,  // For individuals, use profile image
                            companyLogoRes = if (listing.employerType == EmployerType.ORGANIZATION)
                                listing.imageRes else null,  // For companies, use logo
                            onViewClick = { onListingClick(listing) },
                            onApplyClick = { /* Handle apply action */ },
                            modifier = Modifier.fillMaxWidth(),
                            description = listing.description,
                            isFavorite = listing.isVerified,
                            onFavoriteClick = {},
                        )
                    }
                }
            }
        }
    }

    // Filter Bottom Sheet
    if (showFilterModal) {
        JobFilterBottomSheet(
            statusFilterState = statusFilterState,
            salaryRange = salaryRange,
            employerFilter = employerFilter,
            onStatusFilterChange = { statusFilterState = it },
            onSalaryRangeChange = { salaryRange = it },
            onEmployerFilterChange = { employerFilter = it },
            onDismiss = { showFilterModal = false },
            onApply = { showFilterModal = false },
            onReset = {
                statusFilterState = JobStatusFilterState()
                salaryRange = SalaryRange()
                employerFilter = EmployerFilterType.ALL
                activeFilterCount = 0
                showFilterModal = false
            },
            colorScheme = colorScheme
        )
    }
}

/* ────────────── HELPER FUNCTION ────────────── */

private fun extractSalaryValue(salaryString: String): Int {
    return try {
        // Remove "KSh ", "KES ", commas, and handle "/day", "/month", etc.
        val cleaned = salaryString
            .replace("KSh", "")
            .replace("KES", "")
            .replace(",", "")
            .replace("/day", "")
            .replace("/month", "")
            .replace("/hr", "")
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
private fun JobListingsHeader(
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
                            text = "Job Listings",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = colorScheme.onSurface,
                                letterSpacing = (-0.5).sp
                            )
                        )
                        Text(
                            text = "Find your next opportunity",
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
                                        text = "Search by title, company...",
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
                                            modifier = Modifier.align(Alignment.Center as Alignment.Vertical)
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
private fun JobCategoryFilterPills(
    selectedCategory: JobCategoryType,
    onCategorySelected: (JobCategoryType) -> Unit,
    colorScheme: ColorScheme
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorScheme.surface),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Create a list of category items with proper typing
        val categories = listOf(
            Triple(JobCategoryType.ALL, null, "All"),
            Triple(JobCategoryType.FULL_TIME, Icons.Outlined.Work, "Full-time"),
            Triple(JobCategoryType.PART_TIME, Icons.Outlined.AccessTime, "Part-time"),
            Triple(JobCategoryType.CONTRACT, Icons.Outlined.Description, "Contract"),
            Triple(JobCategoryType.INTERNSHIP, Icons.Outlined.School, "Internship")
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
private fun JobFilterBottomSheet(
    statusFilterState: JobStatusFilterState,
    salaryRange: SalaryRange,
    employerFilter: EmployerFilterType,
    onStatusFilterChange: (JobStatusFilterState) -> Unit,
    onSalaryRangeChange: (SalaryRange) -> Unit,
    onEmployerFilterChange: (EmployerFilterType) -> Unit,
    onDismiss: () -> Unit,
    onApply: () -> Unit,
    onReset: () -> Unit,
    colorScheme: ColorScheme
) {
    var localStatusState by remember { mutableStateOf(statusFilterState) }
    var localMinSalary by remember { mutableStateOf(salaryRange.min?.toString() ?: "") }
    var localMaxSalary by remember { mutableStateOf(salaryRange.max?.toString() ?: "") }
    var localEmployerFilter by remember { mutableStateOf(employerFilter) }

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
                    text = "Filter Jobs",
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

            // Salary Range Section
            Text(
                text = "Salary Range (KES)",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Min Salary
                OutlinedTextField(
                    value = localMinSalary,
                    onValueChange = { localMinSalary = it.filter { char -> char.isDigit() } },
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

                // Max Salary
                OutlinedTextField(
                    value = localMaxSalary,
                    onValueChange = { localMaxSalary = it.filter { char -> char.isDigit() } },
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

            // Employer Type Section
            Text(
                text = "Employer Type",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val employerTypes = listOf(
                    EmployerFilterType.ALL to "All",
                    EmployerFilterType.ORGANIZATION to "Companies",
                    EmployerFilterType.INDIVIDUAL to "Individuals"
                )

                employerTypes.forEach { (type, label) ->
                    val isSelected = localEmployerFilter == type
                    FilterChip(
                        selected = isSelected,
                        onClick = { localEmployerFilter = type },
                        label = {
                            Text(
                                label,
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
                    // Fill remaining slots
                    for (j in 0 until (3 - (statuses.size - 3))) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            if (localStatusState.selectedStatuses.isNotEmpty() || localMinSalary.isNotEmpty() ||
                localMaxSalary.isNotEmpty() || localEmployerFilter != EmployerFilterType.ALL) {
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
                        localStatusState = JobStatusFilterState()
                        localMinSalary = ""
                        localMaxSalary = ""
                        localEmployerFilter = EmployerFilterType.ALL
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
                        onSalaryRangeChange(
                            SalaryRange(
                                min = localMinSalary.toIntOrNull(),
                                max = localMaxSalary.toIntOrNull()
                            )
                        )
                        onEmployerFilterChange(localEmployerFilter)
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
private fun JobNoResultsEmptyState(
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
            "No matching jobs",
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
private fun JobEmptyState(
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
                painter = painterResource(id = R.drawable.ic_work),
                contentDescription = null,
                tint = colorScheme.primary,
                modifier = Modifier.padding(20.dp)
            )
        }
        Spacer(Modifier.height(24.dp))
        Text(
            "No jobs listed yet",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface
            )
        )
        Text(
            "Be the first to post a job opportunity.",
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
                "Post Job",
                fontWeight = FontWeight.Bold
            )
        }
    }
}