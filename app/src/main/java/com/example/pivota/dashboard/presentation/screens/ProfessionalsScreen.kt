package com.example.pivota.dashboard.presentation.screens

import android.annotation.SuppressLint
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.pivota.R
import com.example.pivota.dashboard.presentation.composables.ModernProfessionalCard
import com.example.pivota.dashboard.domain.ProfessionalType
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
    val profileImageRes: Int? = null,
    val coverImageRes: Int? = null,
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
    onNavigateBack: () -> Unit = {}
) {
    val colorScheme = MaterialTheme.colorScheme
    val professionals by viewModel.filteredProfessionals.collectAsStateWithLifecycle()

    // State for search and filters - exactly like DiscoverScreen
    var searchQuery by remember { mutableStateOf("") }
    var selectedPill by remember { mutableStateOf("All") }
    var filterState by remember { mutableStateOf(ProfessionalFilterState()) }
    var priceRange by remember { mutableStateOf(PriceRange()) }
    var showFilterModal by remember { mutableStateOf(false) }
    var activeFilterCount by remember { mutableIntStateOf(0) }
    var isSearching by remember { mutableStateOf(false) }
    var isRecording by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

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

    // Filter professionals based on category, filters, price, and search
    val filteredProfessionals = remember(debouncedQuery.value, selectedPill, filterState, priceRange, professionals) {
        professionals.filter { professional ->
            var matches = true

            // Apply category filter based on selected pill
            when (selectedPill) {
                "All" -> matches = true
                "Electrician" -> matches = professional.category == "Electrician"
                "Plumber" -> matches = professional.category == "Plumber"
                "Designer" -> matches = professional.category == "Designer"
                "Legal" -> matches = professional.category == "Legal Services"
                "Property" -> matches = professional.category == "Property Management"
                "Carpenter" -> matches = professional.category == "Carpenter"
                else -> matches = true
            }

            // Apply rating filter
            if (filterState.minRating > 0 && matches) {
                matches = professional.rating >= filterState.minRating
            }

            // Apply verified filter
            if (filterState.isVerifiedOnly && matches) {
                matches = professional.isVerified
            }

            // Apply SmartMatch filter
            if (filterState.isSmartMatchOnly && matches) {
                matches = professional.isSmartMatch
            }

            // Apply price range filter
            if (matches) {
                priceRange.min?.let {
                    if (professional.startingPrice < it) matches = false
                }
                priceRange.max?.let {
                    if (professional.startingPrice > it) matches = false
                }
            }

            // Apply search filter
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
        containerColor = colorScheme.background,
        topBar = {
            ProfessionalsHeroHeader(
                primaryColor = colorScheme.primary,
                tertiaryColor = colorScheme.tertiary,
                height = animatedHeight,
                collapseFraction = collapseFraction,
                colorScheme = colorScheme
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
        ) {
            // 📜 SCROLL CONTENT - Using contentPadding like DiscoverScreen
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    top = maxHeight + 180.dp // Combined height of header + search + pills
                )
            ) {
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
                                    color = colorScheme.primary
                                )
                            } else {
                                Text(
                                    text = "${filteredProfessionals.size} professionals found",
                                    fontSize = 13.sp,
                                    color = colorScheme.primary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                // Empty state when no professionals match search/filters
                if (filteredProfessionals.isEmpty() && !isSearching) {
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
                                if (searchQuery.isNotEmpty() || selectedPill != "All" ||
                                    filterState.minRating > 0 || filterState.isVerifiedOnly || filterState.isSmartMatchOnly ||
                                    priceRange.min != null || priceRange.max != null) {
                                    // No results for current filters
                                    Icon(
                                        Icons.Outlined.SearchOff,
                                        contentDescription = null,
                                        tint = colorScheme.onSurfaceVariant.copy(0.5f),
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "No professionals found",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Try adjusting your filters or search terms",
                                        fontSize = 14.sp,
                                        color = colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Button(
                                        onClick = {
                                            searchQuery = ""
                                            selectedPill = "All"
                                            filterState = ProfessionalFilterState()
                                            priceRange = PriceRange()
                                            activeFilterCount = 0
                                            focusManager.clearFocus()
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = colorScheme.primary
                                        ),
                                        shape = RoundedCornerShape(16.dp)
                                    ) {
                                        Text("Clear Filters")
                                    }
                                } else {
                                    // No professionals at all
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_work),
                                        contentDescription = null,
                                        tint = colorScheme.primary.copy(0.5f),
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "No professionals yet",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Check back later for professionals in your area",
                                        fontSize = 14.sp,
                                        color = colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }

                // Professional Cards Grid
                if (filteredProfessionals.isNotEmpty()) {
                    // Split the professionals into chunks for grid display
                    val chunkedProfessionals = filteredProfessionals.chunked(gridColumns)

                    items(chunkedProfessionals) { rowProfessionals ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            rowProfessionals.forEach { professional ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth()
                                ) {
                                    ModernProfessionalCard(
                                        name = professional.name,
                                        specialty = professional.category,
                                        rating = professional.rating.toFloat(),
                                        jobs = professional.completedJobs,
                                        isVerified = professional.isVerified,
                                        professionalType = if (professional.businessName != null)
                                            ProfessionalType.ORGANIZATION else ProfessionalType.INDIVIDUAL,
                                        description = professional.description,
                                        coverImageRes = professional.coverImageRes,
                                        profileImageRes = professional.profileImageRes,
                                        onCardClick = { onProfessionalClick(professional) },
                                        onViewClick = { onProfessionalClick(professional) },
                                        onHireClick = { /* Handle hire */ },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .animateItem()
                                    )
                                }
                            }

                            // Add empty boxes to fill remaining space if row has fewer items than columns
                            repeat(gridColumns - rowProfessionals.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                item { Spacer(Modifier.height(80.dp)) }
            }

            // 🏆 FIXED HEADER - Like DiscoverScreen
            ProfessionalsHeroHeader(
                primaryColor = colorScheme.primary,
                tertiaryColor = colorScheme.tertiary,
                height = animatedHeight,
                collapseFraction = collapseFraction,
                colorScheme = colorScheme,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .zIndex(3f)
            )

            // 📌 FIXED SEARCH + PILLS SECTION - Exactly like DiscoverScreen
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(top = animatedHeight)
                    .shadow(
                        elevation = if (isPastThreshold.value) 8.dp else 0.dp,
                        shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
                        ambientColor = Color.Black.copy(0.08f)
                    )
                    .zIndex(2f),
                shape = RoundedCornerShape(
                    topStart = 0.dp,
                    topEnd = 0.dp,
                    bottomStart = 20.dp,
                    bottomEnd = 20.dp
                ),
                color = colorScheme.surface,
                tonalElevation = if (isPastThreshold.value) 4.dp else 0.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    // Search Bar with Audio Icon - Like DiscoverScreen
                    ProfessionalsSearchBarWithAudio(
                        query = searchQuery,
                        onQueryChange = { searchQuery = it },
                        onAudioClick = {
                            isRecording = !isRecording
                        },
                        isRecording = isRecording,
                        accentColor = colorScheme.primary,
                        colorScheme = colorScheme,
                        activeFilterCount = activeFilterCount,
                        onFilterClick = { showFilterModal = true }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Filter Pills - Like DiscoverScreen
                    ProfessionalsFilterPillsRow(
                        selectedPill = selectedPill,
                        onPillSelected = { pill ->
                            selectedPill = pill
                        },
                        accentColor = colorScheme.primary,
                        colorScheme = colorScheme
                    )
                }
            }
        }
    }

    // Filter Modal Bottom Sheet
    if (showFilterModal) {
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
            accentColor = colorScheme.primary,
            colorScheme = colorScheme
        )
    }
}

/* ─────────────────────────────────────────────
   SEARCH BAR WITH AUDIO ICON - Like DiscoverScreen
   ───────────────────────────────────────────── */

@Composable
fun ProfessionalsSearchBarWithAudio(
    query: String,
    onQueryChange: (String) -> Unit,
    onAudioClick: () -> Unit,
    isRecording: Boolean,
    accentColor: Color,
    colorScheme: ColorScheme,
    activeFilterCount: Int,
    onFilterClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Black.copy(0.05f)
            ),
        shape = RoundedCornerShape(16.dp),
        color = colorScheme.surface,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Outlined.Search,
                contentDescription = null,
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
                                "Search...",
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

                // Filter button with badge - Like DiscoverScreen
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

/* ─────────────────────────────────────────────
   FILTER PILLS ROW - Like DiscoverScreen
   ───────────────────────────────────────────── */

@Composable
fun ProfessionalsFilterPillsRow(
    selectedPill: String,
    onPillSelected: (String) -> Unit,
    accentColor: Color,
    colorScheme: ColorScheme
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        val filters = listOf(
            "All" to null,
            "Electrician" to Icons.Outlined.Bolt,
            "Plumber" to Icons.Outlined.Plumbing,
            "Designer" to Icons.Outlined.Brush,
            "Legal" to Icons.Outlined.Gavel,
            "Property" to Icons.Outlined.Apartment,
            "Carpenter" to Icons.Outlined.Handyman
        )

        items(filters.size) { index ->
            val (filter, icon) = filters[index]
            val isSelected = selectedPill == filter || (filter == "All" && selectedPill == "All")

            Surface(
                shape = RoundedCornerShape(30.dp),
                color = if (isSelected) accentColor else colorScheme.surface,
                border = if (!isSelected) BorderStroke(1.dp, colorScheme.outlineVariant) else null,
                modifier = Modifier
                    .clickable { onPillSelected(filter) }
                    .shadow(
                        elevation = if (isSelected) 2.dp else 0.dp,
                        shape = RoundedCornerShape(30.dp),
                        ambientColor = Color.Black.copy(0.05f)
                    )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (icon != null) {
                        Icon(
                            icon,
                            contentDescription = null,
                            tint = if (isSelected) colorScheme.onPrimary else colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    Text(
                        text = filter,
                        color = if (isSelected) colorScheme.onPrimary else colorScheme.onSurfaceVariant,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

/* ────────────── FILTER BOTTOM SHEET ────────────── */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfessionalsFilterBottomSheet(
    filterState: ProfessionalFilterState,
    priceRange: PriceRange,
    onFilterChange: (ProfessionalFilterState) -> Unit,
    onPriceRangeChange: (PriceRange) -> Unit,
    onDismiss: () -> Unit,
    onApply: () -> Unit,
    onReset: () -> Unit,
    accentColor: Color,
    colorScheme: ColorScheme
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
                        tint = accentColor,
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
                        checkedThumbColor = accentColor,
                        checkedTrackColor = accentColor.copy(0.5f),
                        uncheckedThumbColor = colorScheme.onSurfaceVariant,
                        uncheckedTrackColor = colorScheme.outlineVariant
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // SmartMatch Only Toggle
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

            Spacer(modifier = Modifier.height(16.dp))

            // Selected count indicator
            val selectedCount = listOfNotNull(
                if (localFilterState.minRating > 0) 1 else null,
                if (localFilterState.isVerifiedOnly) 1 else null,
                if (localFilterState.isSmartMatchOnly) 1 else null,
                if (localMinPrice.isNotEmpty() || localMaxPrice.isNotEmpty()) 1 else null
            ).size

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
   PROFESSIONALS HERO HEADER
   ───────────────────────────────────────────── */

@Composable
fun ProfessionalsHeroHeader(
    primaryColor: Color,
    tertiaryColor: Color,
    height: Dp,
    collapseFraction: Float,
    colorScheme: ColorScheme,
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
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(R.drawable.happypeople)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    onLoading = {},
                    onSuccess = {},
                    onError = {}
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
                                HeaderAvatarProfessionals(colorScheme)
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .background(colorScheme.tertiaryContainer, CircleShape)
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
                                    "Find trusted professionals",
                                    color = Color.White.copy(0.85f),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            HeaderActionIconProfessionals(
                                icon = Icons.Default.Mail,
                                iconTint = Color.White,
                                backgroundTint = Color.White.copy(alpha = 0.2f),
                                onClick = {}
                            )
                            HeaderActionIconProfessionals(
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
                        text = "Professionals",
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
                        HeaderActionIconProfessionals(
                            icon = Icons.Default.Mail,
                            iconTint = Color.White,
                            backgroundTint = Color.White.copy(alpha = 0.2f),
                            onClick = {}
                        )
                        HeaderActionIconProfessionals(
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
                        text = "Professionals",
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
fun HeaderAvatarProfessionals(colorScheme: ColorScheme) {
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
fun HeaderActionIconProfessionals(
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