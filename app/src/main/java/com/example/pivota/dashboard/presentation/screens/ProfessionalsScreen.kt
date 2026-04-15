package com.example.pivota.dashboard.presentation.screens

import android.annotation.SuppressLint
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
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

    // Get window size class for responsive design
    val windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val isExpanded = windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.EXPANDED
    val isMedium = windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.MEDIUM
    val isTablet = isExpanded || isMedium

    // Adaptive grid columns based on screen size
    val professionalGridColumns = when {
        isExpanded -> 3
        isMedium -> 2
        else -> 1
    }

    // State for search and filters
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

    val primaryColor = colorScheme.primary
    val softBackground = colorScheme.background

    val horizontalPadding = when {
        isExpanded -> 24.dp
        isMedium -> 20.dp
        else -> 16.dp
    }

    // Track if search bar should be pinned (only search and pills are sticky)
    val isSearchBarPinned by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 1 ||
                    (listState.firstVisibleItemIndex == 1 && listState.firstVisibleItemScrollOffset > 0)
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
    val filteredProfessionals = remember(debouncedQuery.value, selectedPill, filterState, priceRange, professionals) {
        professionals.filter { professional ->
            var matches = true

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
        containerColor = softBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
        ) {
            // SCROLL CONTENT - Header is inside LazyColumn (scrolls away)
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp),
                horizontalAlignment = Alignment.Start
            ) {
                // HEADER IS NOW INSIDE LAZYCOLUMN - IT SCROLLS AWAY
                item {
                    NonStickyHeaderProfessionals(
                        colorScheme = colorScheme,
                        user = user,
                        isGuestMode = isGuestMode,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Search and Pills Section (scrolls with content)
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = horizontalPadding, vertical = 12.dp)
                    ) {
                        ProfessionalsSearchBarWithAudio(
                            query = searchQuery,
                            onQueryChange = { searchQuery = it },
                            onAudioClick = { isRecording = !isRecording },
                            isRecording = isRecording,
                            accentColor = primaryColor,
                            colorScheme = colorScheme,
                            activeFilterCount = activeFilterCount,
                            onFilterClick = { showFilterModal = true }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        ProfessionalsFilterPillsRow(
                            selectedPill = selectedPill,
                            onPillSelected = { pill ->
                                selectedPill = pill
                            },
                            accentColor = primaryColor,
                            colorScheme = colorScheme
                        )
                    }
                }

                // Search results info
                if (debouncedQuery.value.isNotEmpty()) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = horizontalPadding, vertical = 8.dp),
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
                                    text = "${filteredProfessionals.size} professionals found",
                                    fontSize = 13.sp,
                                    color = primaryColor,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                // Empty state
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
                                            containerColor = primaryColor
                                        ),
                                        shape = RoundedCornerShape(16.dp)
                                    ) {
                                        Text("Clear Filters")
                                    }
                                } else {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_work),
                                        contentDescription = null,
                                        tint = primaryColor.copy(0.5f),
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

                // Professional Cards Grid with proper spacing
                if (filteredProfessionals.isNotEmpty()) {
                    items((filteredProfessionals.size + professionalGridColumns - 1) / professionalGridColumns) { rowIndex ->
                        val startIndex = rowIndex * professionalGridColumns

                        if (professionalGridColumns == 1) {
                            // Single column - vertical layout with 16dp spacing between cards
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = horizontalPadding),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                for (i in 0 until professionalGridColumns) {
                                    val index = startIndex + i
                                    if (index < filteredProfessionals.size) {
                                        val professional = filteredProfessionals[index]
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
                        } else {
                            // Multiple columns - use Row with 16dp spacing
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = horizontalPadding),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    for (i in 0 until professionalGridColumns) {
                                        val index = startIndex + i
                                        if (index < filteredProfessionals.size) {
                                            val professional = filteredProfessionals[index]
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .fillMaxWidth()
                                            ) {
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
                                                    onViewDetailsClick = { onProfessionalClick(professional) }
                                                )
                                            }
                                        } else {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // STICKY SEARCH + PILLS SECTION (appears when scrolled past the original search)
            if (isSearchBarPinned) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
                            ambientColor = Color.Black.copy(0.08f)
                        )
                        .zIndex(10f),
                    shape = RoundedCornerShape(
                        topStart = 0.dp,
                        topEnd = 0.dp,
                        bottomStart = 20.dp,
                        bottomEnd = 20.dp
                    ),
                    color = colorScheme.surface,
                    tonalElevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = horizontalPadding, vertical = 12.dp)
                    ) {
                        ProfessionalsSearchBarWithAudio(
                            query = searchQuery,
                            onQueryChange = { searchQuery = it },
                            onAudioClick = { isRecording = !isRecording },
                            isRecording = isRecording,
                            accentColor = primaryColor,
                            colorScheme = colorScheme,
                            activeFilterCount = activeFilterCount,
                            onFilterClick = { showFilterModal = true }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        ProfessionalsFilterPillsRow(
                            selectedPill = selectedPill,
                            onPillSelected = { pill ->
                                selectedPill = pill
                            },
                            accentColor = primaryColor,
                            colorScheme = colorScheme
                        )
                    }
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
            accentColor = primaryColor,
            colorScheme = colorScheme
        )
    }
}

// NON-STICKY HEADER - Same as DiscoverScreen (scrolls away)
@Composable
fun NonStickyHeaderProfessionals(
    colorScheme: ColorScheme,
    user: User? = null,
    isGuestMode: Boolean = false,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val profileUrl = user?.profileImageUrl?.takeIf { it.isNotBlank() }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding(),
        color = colorScheme.surface,
        shadowElevation = 0.dp
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Logo
                AsyncImage(
                    model = R.drawable.logofinale,
                    contentDescription = "Logo",
                    modifier = Modifier
                        .height(34.dp)
                        .width(120.dp),
                    error = painterResource(R.drawable.ic_launcher_foreground)
                )

                // Right icons + profile
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HeaderIconProfessionals(Icons.Outlined.MailOutline, colorScheme)
                    HeaderIconProfessionals(Icons.Outlined.NotificationsNone, colorScheme)

                    // Profile
                    Box(
                        modifier = Modifier.size(42.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(profileUrl)
                                .size(128)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Profile",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .matchParentSize()
                                .clip(CircleShape)
                                .border(
                                    1.dp,
                                    colorScheme.outlineVariant.copy(alpha = 0.3f),
                                    CircleShape
                                ),
                            placeholder = painterResource(R.drawable.job_placeholder3),
                            error = painterResource(R.drawable.job_placeholder3),
                            fallback = painterResource(R.drawable.job_placeholder3)
                        )
                    }
                }
            }

            // Subtle divider
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(colorScheme.outlineVariant.copy(alpha = 0.15f))
            )
        }
    }
}

@Composable
fun HeaderIconProfessionals(
    icon: ImageVector,
    colorScheme: ColorScheme
) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(colorScheme.surfaceVariant.copy(alpha = 0.4f))
            .clickable { },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
    }
}

/* ─────────────────────────────────────────────
   SEARCH BAR WITH AUDIO ICON
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
                                "Search professionals...",
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
                                    modifier = Modifier.align(Alignment.Center as Alignment.Vertical)
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
   FILTER PILLS ROW
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
            val isSelected = selectedPill == filter

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
                OutlinedTextField(
                    value = localMinPrice,
                    onValueChange = { localMinPrice = it.filter { char -> char.isDigit() } },
                    label = { Text("Min") },
                    placeholder = { Text("Any") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = accentColor,
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
                        focusedBorderColor = accentColor,
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

@Composable
fun BadgedBox(
    badge: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    Box {
        content()
        Box(
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            badge()
        }
    }
}