package com.example.pivota.dashboard.presentation.screens.client_general_screens.main_screens

import android.annotation.SuppressLint
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.pivota.R
import com.example.pivota.dashboard.domain.model.listings_models.general.DiscoveryCategory
import com.example.pivota.dashboard.presentation.composables.client_general_composables.general.BannerType
import com.example.pivota.dashboard.presentation.composables.client_general_composables.general.MarketingCarouselBanner
import com.example.pivota.dashboard.presentation.composables.client_general_composables.general.ReusableHeader
import com.example.pivota.dashboard.presentation.composables.client_general_composables.listings_composables.categories.EmptyServicesState
import com.example.pivota.dashboard.presentation.composables.client_general_composables.listings_composables.categories.ErrorServicesState
import com.example.pivota.dashboard.presentation.composables.client_general_composables.listings_composables.categories.ServiceGridSkeleton
import com.example.pivota.dashboard.presentation.composables.listings_composables.ModernHousingCardV2
import com.example.pivota.dashboard.presentation.composables.listings_composables.ModernJobCardV2
import com.example.pivota.dashboard.presentation.composables.listings_composables.ModernProfessionalCardV2
import com.example.pivota.dashboard.presentation.composables.listings_composables.ProfessionalType
import com.example.pivota.dashboard.presentation.state.CommonServicesUiState
import com.example.pivota.dashboard.presentation.viewmodels.client_general_viewmodels.CommonServicesViewModel
import com.example.pivota.dashboard.presentation.viewmodels.client_general_viewmodels.DashboardSharedViewModel
import com.example.pivota.dashboard.presentation.viewmodels.client_general_viewmodels.HeaderState
import com.example.pivota.dashboard.presentation.composables.client_general_composables.listings_composables.categories.getIconForService
import com.example.pivota.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Filter categories using theme colors
@Composable
private fun getFilterCategories(colorScheme: ColorScheme): List<FilterCategory> {
    return listOf(
        FilterCategory("Jobs", Icons.Rounded.Work, colorScheme.primary),
        FilterCategory("Properties", Icons.Rounded.Home, colorScheme.secondary),
        FilterCategory("Professionals", Icons.Rounded.Build, colorScheme.tertiary),
        FilterCategory("Social Support", Icons.Rounded.VolunteerActivism, PurpleAccent),
        FilterCategory("Verified", Icons.Rounded.Verified, InfoBlue)
    )
}

@SuppressLint("FrequentlyChangingValue")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DiscoverScreen(
    onNavigateToHouseListings: () -> Unit = {},
    onNavigateToJobListings: () -> Unit = {},
    onNavigateToAllJobs: () -> Unit = {},
    onNavigateToAllHousing: () -> Unit = {},
    onNavigateToAllProviders: () -> Unit = {},
    onNavigateToAllServices: () -> Unit = {},
    onNavigateToAllSupport: () -> Unit = {},
    onServiceClick: (String, String, String) -> Unit = { _, _, _ -> },
    onSubcategoriesClick: (String, String, String) -> Unit = { _, _, _ -> },
    onSearchClick: () -> Unit = {},
    isGuestMode: Boolean = false,
    sharedViewModel: DashboardSharedViewModel = hiltViewModel(),
    commonServicesViewModel: CommonServicesViewModel = hiltViewModel()
) {
    val colorScheme = MaterialTheme.colorScheme
    val headerState by sharedViewModel.headerState.collectAsState()
    val headerUser = (headerState as? HeaderState.Success)?.headerUser
    val scope = rememberCoroutineScope()

    var stickySearchQuery by remember { mutableStateOf("") }

    val commonServicesState by commonServicesViewModel.uiState.collectAsState()

    val windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val isExpanded = windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.EXPANDED
    val isMedium = windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.MEDIUM
    val isTablet = isExpanded || isMedium

    val filterCategories = getFilterCategories(colorScheme)

    val jobGridColumns = if (isExpanded || isMedium) 2 else 1
    val housingGridColumns = if (isExpanded || isMedium) 2 else 1
    val professionalGridColumns = if (isExpanded || isMedium) 2 else 1
    val serviceGridColumns = if (isTablet) 6 else 4

    val horizontalPadding = when {
        isExpanded -> 24.dp
        isMedium -> 20.dp
        else -> 16.dp
    }

    val listState = rememberLazyListState()
    var selectedFilter by remember { mutableStateOf<String?>(null) }

    val scrollOffset by remember {
        derivedStateOf {
            if (listState.firstVisibleItemIndex == 0) {
                listState.firstVisibleItemScrollOffset.toFloat()
            } else {
                Float.MAX_VALUE
            }
        }
    }

    // Track if the filter section should be sticky
    val isFilterSectionVisible by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex >= 1
        }
    }

    LaunchedEffect(selectedFilter) {
        scope.launch {
            listState.animateScrollToItem(0)
        }
    }

    val jobItemsMemo = remember { jobItems }
    val housingItemsMemo = remember { housingItems }
    val professionalItemsMemo = remember { professionalItems }
    val supportItemsMemo = remember { supportItems }

    LaunchedEffect(Unit) {
        delay(100)
        commonServicesViewModel.loadCommonServices(isTablet)
    }

    LaunchedEffect(isTablet) {
        commonServicesViewModel.loadCommonServices(isTablet)
    }

    LaunchedEffect(selectedFilter) {
        when (selectedFilter) {
            "Jobs" -> {
                onNavigateToJobListings()
                selectedFilter = null
            }
            "Properties" -> {
                onNavigateToHouseListings()
                selectedFilter = null
            }
            "Professionals" -> {
                onNavigateToAllProviders()
                selectedFilter = null
            }
        }
    }

    Scaffold(
        containerColor = colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 100.dp),
                horizontalAlignment = Alignment.Start
            ) {
                item(key = "header") {
                    ReusableHeader(
                        colorScheme = colorScheme,
                        pageTitle = "PivotaConnect",
                        pageSubtitle = "Connect to opportunities near you",
                        isGuestMode = isGuestMode,
                        isSticky = false,
                        sharedViewModel = sharedViewModel,
                        showSearchIcon = true,
                        onSearchClick = onSearchClick,
                        scrollOffset = scrollOffset,
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                item(key = "marketing_carousel") {
                    val displayName = remember(headerUser, isGuestMode) {
                        when {
                            isGuestMode -> "Guest"
                            headerUser != null -> headerUser.shortName
                            else -> "Guest"
                        }
                    }

                    MarketingCarouselBanner(
                        horizontalPadding = horizontalPadding,
                        displayName = displayName,
                        isGuestMode = isGuestMode,
                        onCtaClick = { bannerType ->
                            when (bannerType) {
                                BannerType.WELCOME_BACK -> {
                                    if (isGuestMode) {
                                        // Navigate to signup
                                    } else {
                                        // Navigate to upgrade
                                    }
                                }
                                BannerType.JOBS -> onNavigateToAllJobs()
                                BannerType.HOUSING -> onNavigateToAllHousing()
                                BannerType.PROFESSIONALS -> onNavigateToAllProviders()
                                BannerType.SOCIAL_SUPPORT -> {}
                            }
                        }
                    )
                }

                // Sticky filter section
                stickyHeader(key = "filter_section") {
                    StickyFilterSection(
                        selectedFilter = selectedFilter,
                        onFilterSelected = { filter ->
                            selectedFilter = if (selectedFilter == filter) null else filter
                        },
                        colorScheme = colorScheme,
                        filterCategories = filterCategories,
                        horizontalPadding = horizontalPadding,
                        isTablet = isTablet,
                        searchQuery = stickySearchQuery,
                        onSearchQueryChanged = { query ->
                            stickySearchQuery = query
                            // Handle search logic here - filter results, call API, etc.
                        },
                        isSticky = isFilterSectionVisible
                    )
                }

                // Common Services Section
                item(key = "common_services_header") {
                    ModernSectionHeader(
                        title = "Common Services",
                        actionText = "Browse all →",
                        onActionClick = onNavigateToAllServices,
                        horizontalPadding = horizontalPadding,
                        colorScheme = colorScheme
                    )
                }

                val currentState = commonServicesState
                when (currentState) {
                    is CommonServicesUiState.Loading -> {
                        item(key = "common_services_loading") {
                            ServiceGridSkeleton(
                                columnsPerRow = serviceGridColumns,
                                rowsToShow = 2,
                                horizontalPadding = horizontalPadding,
                            )
                        }
                    }
                    is CommonServicesUiState.Success -> {
                        val services = currentState.services
                        if (services.isNotEmpty()) {
                            item(key = "common_services_grid") {
                                DynamicServiceGrid(
                                    services = services,
                                    colorScheme = colorScheme,
                                    horizontalPadding = horizontalPadding,
                                    columnsPerRow = serviceGridColumns,
                                    onServiceClick = { category ->
                                        onServiceClick(category.id, category.name, category.vertical)
                                    },
                                    onSubcategoriesClick = { category ->
                                        onSubcategoriesClick(category.id, category.name, category.vertical)
                                    }
                                )
                            }
                        } else {
                            item(key = "common_services_empty") {
                                EmptyServicesState(
                                    colorScheme = colorScheme,
                                    horizontalPadding = horizontalPadding
                                )
                            }
                        }
                    }
                    is CommonServicesUiState.Error -> {
                        item(key = "common_services_error") {
                            ErrorServicesState(
                                message = currentState.message,
                                colorScheme = colorScheme,
                                horizontalPadding = horizontalPadding,
                                onRetry = { commonServicesViewModel.refresh() }
                            )
                        }
                    }
                }

                item(key = "jobs_header") {
                    ModernSectionHeader(
                        title = "Jobs Near You",
                        actionText = "View all →",
                        onActionClick = onNavigateToAllJobs,
                        horizontalPadding = horizontalPadding,
                        colorScheme = colorScheme
                    )
                }

                item(key = "jobs_content") {
                    JobsContent(
                        items = jobItemsMemo,
                        gridColumns = jobGridColumns,
                        horizontalPadding = horizontalPadding
                    )
                }

                item(key = "housing_header") {
                    ModernSectionHeader(
                        title = "Housing Opportunities",
                        actionText = "Browse all →",
                        onActionClick = onNavigateToAllHousing,
                        horizontalPadding = horizontalPadding,
                        colorScheme = colorScheme
                    )
                }

                item(key = "housing_content") {
                    HousingContent(
                        items = housingItemsMemo,
                        gridColumns = housingGridColumns,
                        horizontalPadding = horizontalPadding
                    )
                }

                item(key = "professionals_header") {
                    ModernSectionHeader(
                        title = "Trusted Professionals",
                        actionText = "See all →",
                        onActionClick = onNavigateToAllProviders,
                        horizontalPadding = horizontalPadding,
                        colorScheme = colorScheme
                    )
                }

                item(key = "professionals_content") {
                    ProfessionalsContent(
                        items = professionalItemsMemo,
                        gridColumns = professionalGridColumns,
                        horizontalPadding = horizontalPadding
                    )
                }

                item(key = "support_header") {
                    ModernSectionHeader(
                        title = "Social Support & Services",
                        actionText = "Get help →",
                        onActionClick = onNavigateToAllSupport,
                        horizontalPadding = horizontalPadding,
                        colorScheme = colorScheme
                    )
                }

                items(
                    count = supportItemsMemo.size,
                    key = { index -> "support_item_$index" }
                ) { index ->
                    val item = supportItemsMemo[index]
                    ModernSupportCard(
                        name = item.name,
                        service = item.service,
                        location = item.location,
                        isUrgent = item.isUrgent,
                        colorScheme = colorScheme,
                        horizontalPadding = horizontalPadding
                    )
                }
            }
        }
    }
}

@Composable
fun StickyFilterSection(
    selectedFilter: String?,
    onFilterSelected: (String) -> Unit,
    colorScheme: ColorScheme,
    filterCategories: List<FilterCategory>,
    horizontalPadding: Dp,
    isTablet: Boolean,
    isSticky: Boolean,
    onSearchQueryChanged: (String) -> Unit = {},
    searchQuery: String = ""
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .zIndex(10f),
        color = colorScheme.background,
        shadowElevation = if (isSticky) 4.dp else 0.dp,
        tonalElevation = if (isSticky) 1.dp else 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = horizontalPadding)
                .padding(
                    top = if (isSticky) 48.dp else 0.dp,
                    bottom = if (isSticky) 16.dp else 12.dp
                )
        ) {
            // Show search bar only when sticky
            if (isSticky) {
                StickySearchBar(
                    searchQuery = searchQuery,
                    onSearchQueryChanged = onSearchQueryChanged,
                    colorScheme = colorScheme
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Only show the text when NOT sticky
            if (!isSticky) {
                Text(
                    text = "Explore by Category",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = colorScheme.onSurface
                    ),
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(
                    start = 0.dp,
                    end = 0.dp
                )
            ) {
                items(filterCategories.size) { index ->
                    val category = filterCategories[index]
                    val isSelected = selectedFilter == category.name

                    FilterPillEnhanced(
                        category = category,
                        isSelected = isSelected,
                        onClick = { onFilterSelected(category.name) },
                        colorScheme = colorScheme
                    )
                }
            }
        }
    }
}

@Composable
fun StickySearchBar(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    colorScheme: ColorScheme
) {
    // Removed FocusRequester and keyboardController - no more autofocus
    val keyboardController = LocalSoftwareKeyboardController.current

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(12.dp),
                ambientColor = Color.Black.copy(alpha = 0.05f)
            ),
        shape = RoundedCornerShape(12.dp),
        color = colorScheme.surfaceContainerHighest,
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                Icons.Outlined.Search,
                contentDescription = "Search",
                tint = colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )

            BasicTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChanged,
                modifier = Modifier.weight(1f),  // Removed .focusRequester(focusRequester)
                decorationBox = { innerTextField ->
                    Box {
                        if (searchQuery.isEmpty()) {
                            Text(
                                "Search services, jobs, housing...",
                                color = colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                fontSize = 14.sp
                            )
                        }
                        innerTextField()
                    }
                },
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 14.sp,
                    color = colorScheme.onSurface
                ),
                singleLine = true
            )

            if (searchQuery.isNotEmpty()) {
                IconButton(
                    onClick = {
                        onSearchQueryChanged("")
                        keyboardController?.hide()
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Outlined.Close,
                        contentDescription = "Clear",
                        tint = colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            } else {
                IconButton(
                    onClick = {
                        // For voice search - you can implement this later
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Outlined.Mic,
                        contentDescription = "Voice search",
                        tint = colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }

    // REMOVED: Auto-focus LaunchedEffect block
}

@Composable
fun FilterPillEnhanced(
    category: FilterCategory,
    isSelected: Boolean,
    onClick: () -> Unit,
    colorScheme: ColorScheme
) {
    Surface(
        modifier = Modifier
            .clickable(onClick = onClick)
            .animateContentSize(),
        shape = RoundedCornerShape(40.dp),
        color = if (isSelected) {
            category.color
        } else {
            colorScheme.surfaceContainerLow
        },
        shadowElevation = if (isSelected) 4.dp else 1.dp,
        border = if (isSelected) null else BorderStroke(1.dp, category.color.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = category.icon,
                contentDescription = category.name,
                tint = if (isSelected) Color.White else category.color,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = category.name,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else colorScheme.onSurface
            )
            if (isSelected) {
                Icon(
                    Icons.Rounded.Close,
                    contentDescription = "Clear",
                    tint = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

data class FilterCategory(
    val name: String,
    val icon: ImageVector,
    val color: Color
)

@Composable
fun JobsContent(
    items: List<JobItem>,
    gridColumns: Int,
    horizontalPadding: Dp
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        val rows = items.chunked(gridColumns)
        rows.forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowItems.forEach { item ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        ModernJobCardV2(
                            imageUrl = item.imageUrl,
                            jobTitle = item.jobTitle,
                            companyName = item.companyName,
                            location = item.location,
                            postedTime = item.postedTime,
                            employmentType = item.employmentType,
                            jobType = item.jobType,
                            onViewDetailsClick = {}
                        )
                    }
                }
                repeat(gridColumns - rowItems.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun HousingContent(
    items: List<HousingItem>,
    gridColumns: Int,
    horizontalPadding: Dp
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        val rows = items.chunked(gridColumns)
        rows.forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowItems.forEach { item ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        ModernHousingCardV2(
                            imageUrl = item.imageUrl,
                            title = item.title,
                            price = item.price,
                            location = item.location,
                            postedTime = item.postedTime,
                            propertyType = item.propertyType,
                            listingType = item.listingType,
                            bedrooms = item.bedrooms,
                            bathrooms = item.bathrooms,
                            squareMeters = item.squareMeters,
                            isVerified = item.isVerified,
                            onViewDetailsClick = {}
                        )
                    }
                }
                repeat(gridColumns - rowItems.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun ProfessionalsContent(
    items: List<ProfessionalItem>,
    gridColumns: Int,
    horizontalPadding: Dp
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        val rows = items.chunked(gridColumns)
        rows.forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowItems.forEach { item ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        ModernProfessionalCardV2(
                            imageUrl = item.imageUrl,
                            name = item.name,
                            profession = item.profession,
                            location = item.location,
                            postedTime = item.postedTime,
                            professionalType = item.professionalType,
                            rating = item.rating,
                            jobsCompleted = item.jobsCompleted,
                            onViewDetailsClick = {}
                        )
                    }
                }
                repeat(gridColumns - rowItems.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun DynamicServiceGrid(
    services: List<DiscoveryCategory>,
    colorScheme: ColorScheme,
    horizontalPadding: Dp,
    columnsPerRow: Int,
    onServiceClick: (DiscoveryCategory) -> Unit,
    onSubcategoriesClick: (DiscoveryCategory) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding)
    ) {
        services.chunked(columnsPerRow).forEachIndexed { rowIndex, rowItems ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                rowItems.forEachIndexed { itemIndex, service ->
                    ServiceCardCircle(
                        service = service,
                        icon = getIconForService(service.name, service.vertical),
                        colorScheme = colorScheme,
                        onClick = {
                            if (service.hasSubcategories) {
                                onSubcategoriesClick(service)
                            } else {
                                onServiceClick(service)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        index = itemIndex
                    )
                }
                repeat(columnsPerRow - rowItems.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun ServiceCardCircle(
    service: DiscoveryCategory,
    icon: ImageVector,
    colorScheme: ColorScheme,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    index: Int = 0
) {
    val lightColors = listOf(
        colorScheme.primary.copy(alpha = 0.08f),
        colorScheme.secondary.copy(alpha = 0.08f),
        colorScheme.tertiary.copy(alpha = 0.08f),
        colorScheme.primary.copy(alpha = 0.12f),
        colorScheme.secondary.copy(alpha = 0.12f),
        colorScheme.tertiary.copy(alpha = 0.12f)
    )

    val iconTintColors = listOf(
        colorScheme.primary,
        colorScheme.secondary,
        colorScheme.tertiary,
        colorScheme.primary,
        colorScheme.secondary,
        colorScheme.tertiary
    )

    val backgroundColor = lightColors[index % lightColors.size]
    val iconColor = iconTintColors[index % iconTintColors.size]

    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(56.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(backgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = service.name,
                    tint = iconColor,
                    modifier = Modifier.size(28.dp)
                )
            }

            if (service.hasSubcategories) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(iconColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "📁",
                        fontSize = 8.sp,
                        color = colorScheme.onPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = service.name,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = colorScheme.onSurface,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            softWrap = true,
            modifier = Modifier.fillMaxWidth()
        )

        if (service.hasSubcategories) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Browse",
                fontSize = 8.sp,
                color = iconColor,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun ModernSectionHeader(
    title: String,
    actionText: String,
    onActionClick: () -> Unit = {},
    horizontalPadding: Dp = 16.dp,
    colorScheme: ColorScheme
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = horizontalPadding, end = horizontalPadding, top = 24.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = colorScheme.onSurface,
                fontSize = 18.sp,
                letterSpacing = 0.5.sp
            )
        )
        Text(
            text = actionText,
            color = colorScheme.tertiary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.clickable { onActionClick() }
        )
    }
}

@Composable
fun ModernSupportCard(
    name: String,
    service: String,
    location: String,
    isUrgent: Boolean,
    colorScheme: ColorScheme,
    horizontalPadding: Dp = 16.dp
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding, vertical = 6.dp)
            .clickable { /* View details */ },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp,
            pressedElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = if (isUrgent) colorScheme.error.copy(alpha = 0.1f) else colorScheme.primary.copy(alpha = 0.08f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isUrgent) Icons.Outlined.Emergency else Icons.Outlined.VolunteerActivism,
                    contentDescription = null,
                    tint = if (isUrgent) colorScheme.error else colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = name,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = colorScheme.onSurface
                )
                Text(
                    text = service,
                    fontSize = 13.sp,
                    color = colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(
                        Icons.Outlined.LocationOn,
                        contentDescription = null,
                        tint = colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = location,
                        fontSize = 11.sp,
                        color = colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        modifier = Modifier.padding(start = 2.dp)
                    )
                }
            }

            Text(
                text = "View →",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = colorScheme.tertiary
            )
        }
    }
}

// Data classes
data class JobItem(
    val imageUrl: Any?,
    val jobTitle: String,
    val companyName: String,
    val location: String,
    val postedTime: String,
    val employmentType: String,
    val jobType: String
)

data class HousingItem(
    val imageUrl: Any?,
    val title: String,
    val price: String,
    val location: String,
    val postedTime: String,
    val propertyType: String,
    val listingType: String,
    val bedrooms: Int,
    val bathrooms: Int,
    val squareMeters: Int,
    val isVerified: Boolean
)

data class ProfessionalItem(
    val imageUrl: Any?,
    val name: String,
    val profession: String,
    val location: String,
    val postedTime: String,
    val professionalType: ProfessionalType,
    val rating: Float,
    val jobsCompleted: Int
)

data class SupportItem(
    val name: String,
    val service: String,
    val location: String,
    val isUrgent: Boolean
)

// Sample data
private val jobItems = listOf(
    JobItem(R.drawable.job_placeholder3, "Construction Foreman", "BuildWell Ltd", "Upper Hill, Nairobi", "2h ago", "Formal", "Contract"),
    JobItem(R.drawable.job_placeholder2, "Junior Accountant", "FinCorp", "Westlands, Nairobi", "1d ago", "Formal", "Full-time"),
    JobItem(null, "Welder & Fabricator", "Joseph's Welding", "Industrial Area, Nairobi", "3h ago", "Informal", "Gig"),
    JobItem(R.drawable.job_placeholder4, "Store Keeper", "Retail Solutions", "Mombasa Rd, Nairobi", "5h ago", "Formal", "Full-time"),
    JobItem(R.drawable.job_placeholder5, "Solar Installer", "Green Energy", "Karen, Nairobi", "1d ago", "Formal", "Contract"),
    JobItem(R.drawable.job_placeholder3, "Delivery Rider", "Bolt Food", "CBD, Nairobi", "2h ago", "Informal", "Gig")
)

private val housingItems = listOf(
    HousingItem(R.drawable.property_placeholder1, "Modern 2BR Apartment", "KES 45,000", "Westlands, Nairobi", "2h ago", "Apartment", "For Rent", 2, 2, 85, true),
    HousingItem(R.drawable.property_placeholder2, "Spacious Family Home", "KES 12,500,000", "Karen, Nairobi", "1d ago", "House", "For Sale", 4, 3, 220, true),
    HousingItem(null, "Cozy Bedsitter", "KES 8,500", "Umoja, Nairobi", "3d ago", "Bedsitter", "For Rent", 1, 1, 25, false),
    HousingItem(R.drawable.property_placeholder4, "Studio Apartment", "KES 35,000", "Kilimani, Nairobi", "5h ago", "Studio", "For Rent", 1, 1, 45, true),
    HousingItem(R.drawable.property_placeholder3, "Luxury Villa", "KES 4.5M", "Karen, Nairobi", "2d ago", "Villa", "For Sale", 4, 4, 350, true),
    HousingItem(R.drawable.property_placeholder1, "2BR Apartment", "KES 28,000", "Ruiru, Nairobi", "1d ago", "Apartment", "For Rent", 2, 2, 75, false)
)

private val professionalItems = listOf(
    ProfessionalItem(null, "QuickMovers Kenya", "Moving & Logistics", "Nairobi", "2h ago", ProfessionalType.ORGANIZATION, 4.9f, 342),
    ProfessionalItem(null, "John Mwangi", "Electrician", "Eastlands, Nairobi", "5h ago", ProfessionalType.INDIVIDUAL, 4.8f, 127),
    ProfessionalItem(null, "CleanPro Services", "Cleaning & Maintenance", "Nairobi", "1d ago", ProfessionalType.ORGANIZATION, 4.7f, 189),
    ProfessionalItem(null, "Fundi Digital", "Electrical & Plumbing", "Nairobi", "3h ago", ProfessionalType.ORGANIZATION, 4.6f, 256),
    ProfessionalItem(null, "Sarah Wanjiku", "House Cleaner", "Westlands, Nairobi", "2h ago", ProfessionalType.INDIVIDUAL, 4.9f, 89),
    ProfessionalItem(null, "SolarTech", "Solar Installation", "Nairobi", "1d ago", ProfessionalType.ORGANIZATION, 4.5f, 112)
)

private val supportItems = listOf(
    SupportItem("Red Cross Kenya", "Emergency Relief & Disaster Response", "Nationwide", true),
    SupportItem("Legal Aid Kenya", "Free Legal Advice & Representation", "Regional Offices", false),
    SupportItem("Food for All", "Community Food Programs", "Nairobi & Kiambu", false)
)

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    colorScheme: ColorScheme,
    isDestructive: Boolean = false
) {
    val textColor = if (isDestructive) colorScheme.error else colorScheme.onSurface
    val iconColor = if (isDestructive) colorScheme.error else colorScheme.primary

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = if (isDestructive) colorScheme.error.copy(alpha = 0.1f)
                    else colorScheme.primary.copy(alpha = 0.08f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = title,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
        }

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = textColor
            )
            Text(
                text = subtitle,
                fontSize = 13.sp,
                color = colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        Icon(
            Icons.Outlined.ChevronRight,
            contentDescription = null,
            tint = colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun SearchAndPillsSection(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    isRecording: Boolean,
    onAudioClick: () -> Unit,
    selectedFilters: Set<String>,
    onFilterSelected: (String) -> Unit,
    primaryColor: Color,
    secondaryColor: Color,
    colorScheme: ColorScheme,
    isTablet: Boolean,
    horizontalPadding: Dp
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding, vertical = 8.dp)
    ) {
        SearchBarWithAudio(
            query = searchQuery,
            onQueryChange = onSearchQueryChange,
            onAudioClick = onAudioClick,
            isRecording = isRecording,
            primaryColor = primaryColor,
            colorScheme = colorScheme,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        FilterPillsRow(
            selectedFilters = selectedFilters,
            onFilterSelected = onFilterSelected,
            primaryColor = primaryColor,
            colorScheme = colorScheme,
            isTablet = isTablet
        )
    }
}

@Composable
fun FilterPillsRow(
    selectedFilters: Set<String>,
    onFilterSelected: (String) -> Unit,
    primaryColor: Color,
    colorScheme: ColorScheme,
    isTablet: Boolean
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        val navItems = listOf(
            "Jobs" to Icons.Outlined.Work,
            "Properties" to Icons.Outlined.Home,
            "Professionals" to Icons.Outlined.Build,
            "Support" to Icons.Outlined.VolunteerActivism,
            "Verified" to Icons.Outlined.Verified
        )

        items(navItems.size) { index ->
            val (label, icon) = navItems[index]

            Row(
                modifier = Modifier
                    .clickable { onFilterSelected(label) }
                    .padding(horizontal = 4.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    icon,
                    contentDescription = label,
                    tint = colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = label,
                    color = colorScheme.onSurfaceVariant,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun SearchBarWithAudio(
    query: String,
    onQueryChange: (String) -> Unit,
    onAudioClick: () -> Unit,
    isRecording: Boolean,
    primaryColor: Color,
    colorScheme: ColorScheme,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Black.copy(alpha = 0.05f)
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
                tint = colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
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
                                color = colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
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
                        tint = colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            } else {
                IconButton(
                    onClick = onAudioClick,
                    modifier = Modifier
                        .size(40.dp)
                        .then(
                            if (isRecording) {
                                Modifier.background(
                                    color = primaryColor.copy(alpha = 0.1f),
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
                        tint = if (isRecording) primaryColor else colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.size(20.dp)
                    )
                }

                if (isRecording) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                color = colorScheme.error,
                                shape = CircleShape
                            )
                    )
                }
            }
        }
    }
}