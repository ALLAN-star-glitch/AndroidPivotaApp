package com.example.pivota.dashboard.presentation.screens

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.pivota.R
import com.example.pivota.auth.domain.model.User
import com.example.pivota.dashboard.domain.EmployerType
import com.example.pivota.dashboard.domain.ListingStatus
import com.example.pivota.dashboard.presentation.state.HousingListingUiModel
import kotlinx.coroutines.delay

// Favorite item data class
data class FavoriteListing(
    val id: String,
    val type: FavoriteListingType,
    val title: String,
    val subtitle: String,
    val price: String,
    val location: String,
    val rating: Float,
    val isVerified: Boolean,
    val imageRes: Int? = null,
    val imageUrl: String? = null,
    val dateSaved: String,
    // Job specific
    val employerType: EmployerType? = null,
    val jobType: String? = null,
    // Housing specific
    val bedrooms: Int? = null,
    val bathrooms: Int? = null,
    val squareMeters: Int? = null,
    val isForSale: Boolean = false,
    // Professional specific
    val specialty: String? = null,
    val completedJobs: Int? = null
)

enum class FavoriteListingType {
    JOB, HOUSING, PROFESSIONAL
}

// Filter state for favorites
data class FavoriteFilterState(
    val selectedTypes: Set<FavoriteListingType> = setOf(
        FavoriteListingType.JOB,
        FavoriteListingType.HOUSING,
        FavoriteListingType.PROFESSIONAL
    ),
    val minRating: Double = 0.0,
    val isVerifiedOnly: Boolean = false,
    val showSmartMatchOnly: Boolean = false
)

@SuppressLint("FrequentlyChangingValue")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    onNavigateBack: () -> Unit = {},
    onJobClick: (String) -> Unit = {},
    onHousingClick: (HousingListingUiModel) -> Unit = {},
    onProfessionalClick: (String) -> Unit = {},
    onRemoveFavorite: (String, FavoriteListingType) -> Unit = { _, _ -> },
    user: User? = null,
    isGuestMode: Boolean = false
) {
    val colorScheme = MaterialTheme.colorScheme

    // 🎯 Get window size class for responsive design
    val windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val isExpanded = windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.EXPANDED
    val isMedium = windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.MEDIUM
    val isTablet = isExpanded || isMedium

    // Responsive horizontal padding
    val horizontalPadding = when {
        isExpanded -> 32.dp
        isMedium -> 24.dp
        else -> 16.dp
    }

    // Mock favorite data - In real app, this would come from ViewModel
    val favoriteListings = remember {
        mutableStateListOf(
            // Jobs
            FavoriteListing(
                id = "job_1",
                type = FavoriteListingType.JOB,
                title = "Construction Foreman",
                subtitle = "BuildWell Ltd",
                price = "KSh 3,500/day",
                location = "Upper Hill",
                rating = 4.8f,
                isVerified = true,
                imageRes = R.drawable.job_placeholder3,
                dateSaved = "Saved 2 days ago",
                employerType = EmployerType.ORGANIZATION,
                jobType = "Contract"
            ),
            FavoriteListing(
                id = "job_2",
                type = FavoriteListingType.JOB,
                title = "Junior Accountant",
                subtitle = "FinCorp",
                price = "KSh 55,000/month",
                location = "Westlands",
                rating = 4.5f,
                isVerified = true,
                imageRes = R.drawable.job_placeholder2,
                dateSaved = "Saved 5 days ago",
                employerType = EmployerType.ORGANIZATION,
                jobType = "Full-time"
            ),
            // Housing
            FavoriteListing(
                id = "housing_1",
                type = FavoriteListingType.HOUSING,
                title = "Modern Bedsitter",
                subtitle = "Self-contained with parking",
                price = "KSh 22,000",
                location = "Ruiru",
                rating = 4.5f,
                isVerified = true,
                imageRes = R.drawable.property_placeholder1,
                dateSaved = "Saved 1 day ago",
                bedrooms = 1,
                bathrooms = 1,
                squareMeters = 70,
                isForSale = false
            ),
            FavoriteListing(
                id = "housing_2",
                type = FavoriteListingType.HOUSING,
                title = "2 Bedroom Apartment",
                subtitle = "Spacious with balcony",
                price = "KSh 45,000",
                location = "Syokimau",
                rating = 4.8f,
                isVerified = true,
                imageRes = R.drawable.property_placeholder2,
                dateSaved = "Saved 3 days ago",
                bedrooms = 2,
                bathrooms = 2,
                squareMeters = 85,
                isForSale = false
            ),
            // Professionals
            FavoriteListing(
                id = "pro_1",
                type = FavoriteListingType.PROFESSIONAL,
                title = "QuickMovers Kenya",
                subtitle = "Moving & Logistics",
                price = "From KSh 5,000",
                location = "Nairobi",
                rating = 4.9f,
                isVerified = true,
                imageRes = R.drawable.manphone,
                dateSaved = "Saved 1 week ago",
                specialty = "Moving & Logistics",
                completedJobs = 342
            ),
            FavoriteListing(
                id = "pro_2",
                type = FavoriteListingType.PROFESSIONAL,
                title = "Fundi Digital",
                subtitle = "Electrical & Plumbing",
                price = "From KSh 2,500",
                location = "Nairobi",
                rating = 4.7f,
                isVerified = true,
                imageRes = R.drawable.mama_mboga,
                dateSaved = "Saved 3 days ago",
                specialty = "Electrical & Plumbing",
                completedJobs = 256
            )
        )
    }

    // Extract user name for display
    val displayName = remember(user) {
        when {
            user == null || isGuestMode -> "Guest"
            user.userName.isNotBlank() -> user.userName.split(" ").firstOrNull() ?: "Guest"
            user.firstName.isNotBlank() -> user.firstName
            else -> user.email.split("@").firstOrNull() ?: "Guest"
        }
    }

    val welcomeMessage = remember(user, isGuestMode) {
        when {
            isGuestMode -> "Welcome to Pivota"
            user?.userName?.isNotBlank() == true -> "Welcome back, ${user.userName.split(" ")
                .firstOrNull() ?: "User"}!"
            user?.firstName?.isNotBlank() == true -> "Welcome back, ${user.firstName}!"
            else -> "Welcome to Pivota"
        }
    }

    // State for search and filters
    var searchQuery by remember { mutableStateOf("") }
    var selectedTypePill by remember { mutableStateOf("All") }
    var filterState by remember { mutableStateOf(FavoriteFilterState()) }
    var showFilterModal by remember { mutableStateOf(false) }
    var activeFilterCount by remember { mutableIntStateOf(0) }
    var isSearching by remember { mutableStateOf(false) }
    var isRecording by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    val listState = rememberLazyListState()

    // 📏 Header sizes - larger on tablets
    val maxHeight = if (isTablet) 260.dp else 220.dp
    val minHeight = if (isTablet) 100.dp else 90.dp

    val density = LocalDensity.current
    val collapseRangePx = with(density) {
        (maxHeight - minHeight).toPx()
    }

    val scrollY = when (listState.firstVisibleItemIndex) {
        0 -> listState.firstVisibleItemScrollOffset.toFloat()
        else -> collapseRangePx
    }

    val collapseFraction = (scrollY / collapseRangePx).coerceIn(0f, 1f)
    val animatedHeight = lerp(maxHeight, minHeight, collapseFraction)

    val isPastThreshold = remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 100
        }
    }

    // Get screen width for adaptive layout
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

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

    // Filter favorites
    val filteredFavorites = remember(debouncedQuery.value, selectedTypePill, filterState, favoriteListings) {
        favoriteListings.filter { favorite ->
            var matches = true

            when (selectedTypePill) {
                "All" -> matches = true
                "Jobs" -> matches = favorite.type == FavoriteListingType.JOB
                "Housing" -> matches = favorite.type == FavoriteListingType.HOUSING
                "Professionals" -> matches = favorite.type == FavoriteListingType.PROFESSIONAL
                else -> matches = true
            }

            if (matches && !filterState.selectedTypes.contains(favorite.type)) {
                matches = false
            }

            if (filterState.minRating > 0 && matches) {
                matches = favorite.rating >= filterState.minRating
            }

            if (filterState.isVerifiedOnly && matches) {
                matches = favorite.isVerified
            }

            if (debouncedQuery.value.isNotEmpty() && matches) {
                matches = favorite.title.lowercase().contains(debouncedQuery.value) ||
                        favorite.subtitle.lowercase().contains(debouncedQuery.value) ||
                        favorite.location.lowercase().contains(debouncedQuery.value)
            }

            matches
        }
    }

    // Update active filter count
    LaunchedEffect(filterState) {
        var count = 0
        if (filterState.selectedTypes.size < 3) count++
        if (filterState.minRating > 0) count++
        if (filterState.isVerifiedOnly) count++
        activeFilterCount = count
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
                contentPadding = PaddingValues(
                    top = maxHeight + 130.dp
                ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
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
                                text = "Search results for \"$searchQuery\"",
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
                                    text = "${filteredFavorites.size} favorites",
                                    fontSize = 13.sp,
                                    color = colorScheme.primary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                // Empty state
                if (filteredFavorites.isEmpty() && !isSearching) {
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
                                if (searchQuery.isNotEmpty() || selectedTypePill != "All" ||
                                    filterState.minRating > 0 || filterState.isVerifiedOnly) {
                                    Icon(
                                        Icons.Outlined.FavoriteBorder,
                                        contentDescription = null,
                                        tint = colorScheme.onSurfaceVariant.copy(0.5f),
                                        modifier = Modifier.size(64.dp)
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "No matching favorites",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Try adjusting your filters",
                                        fontSize = 14.sp,
                                        color = colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Button(
                                        onClick = {
                                            searchQuery = ""
                                            selectedTypePill = "All"
                                            filterState = FavoriteFilterState()
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
                                    Icon(
                                        Icons.Outlined.FavoriteBorder,
                                        contentDescription = null,
                                        tint = colorScheme.primary.copy(0.5f),
                                        modifier = Modifier.size(64.dp)
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "No favorites yet",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Start saving jobs, housing, and professionals you love",
                                        fontSize = 14.sp,
                                        color = colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }

                // Favorite items grid
                if (filteredFavorites.isNotEmpty()) {
                    val chunkedFavorites = filteredFavorites.chunked(gridColumns)

                    items(chunkedFavorites) { rowFavorites ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = horizontalPadding),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            rowFavorites.forEach { favorite ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth()
                                        .animateItem()
                                ) {
                                    when (favorite.type) {
                                        FavoriteListingType.JOB -> {
                                            FavoriteJobCard(
                                                favorite = favorite,
                                                onCardClick = { onJobClick(favorite.id) },
                                                onRemoveFavorite = {
                                                    onRemoveFavorite(favorite.id, favorite.type)
                                                    favoriteListings.remove(favorite)
                                                },
                                                colorScheme = colorScheme
                                            )
                                        }
                                        FavoriteListingType.HOUSING -> {
                                            FavoriteHousingCard(
                                                favorite = favorite,
                                                onCardClick = {
                                                    val listing = HousingListingUiModel(
                                                        id = favorite.id,
                                                        title = favorite.title,
                                                        price = favorite.price,
                                                        location = favorite.location,
                                                        propertyType = favorite.subtitle,
                                                        description = favorite.subtitle,
                                                        isVerified = favorite.isVerified,
                                                        isForSale = favorite.isForSale,
                                                        rating = favorite.rating.toDouble(),
                                                        bedrooms = favorite.bedrooms ?: 1,
                                                        bathrooms = favorite.bathrooms ?: 1,
                                                        squareMeters = favorite.squareMeters ?: 50,
                                                        imageRes = favorite.imageRes ?: R.drawable.property_placeholder1,
                                                        status = ListingStatus.AVAILABLE,
                                                        views = 0,
                                                        messages = 0,
                                                        requests = 0
                                                    )
                                                    onHousingClick(listing)
                                                },
                                                onRemoveFavorite = {
                                                    onRemoveFavorite(favorite.id, favorite.type)
                                                    favoriteListings.remove(favorite)
                                                },
                                                colorScheme = colorScheme
                                            )
                                        }
                                        FavoriteListingType.PROFESSIONAL -> {
                                            FavoriteProfessionalCard(
                                                favorite = favorite,
                                                onCardClick = { onProfessionalClick(favorite.id) },
                                                onRemoveFavorite = {
                                                    onRemoveFavorite(favorite.id, favorite.type)
                                                    favoriteListings.remove(favorite)
                                                },
                                                colorScheme = colorScheme
                                            )
                                        }
                                    }
                                }
                            }

                            repeat(gridColumns - rowFavorites.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                item { Spacer(Modifier.height(100.dp)) }
            }

            // 🏆 FIXED HEADER - Like DiscoverScreen
            FavoritesHeroHeader(
                primaryColor = colorScheme.primary,
                tertiaryColor = colorScheme.tertiary,
                height = animatedHeight,
                collapseFraction = collapseFraction,
                colorScheme = colorScheme,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .zIndex(3f),
                user = user,
                isGuestMode = isGuestMode,
                displayName = displayName,
                welcomeMessage = welcomeMessage,
                onNavigateBack = onNavigateBack
            )

            // 📌 FIXED SEARCH + PILLS SECTION - Like DiscoverScreen
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
                        .padding(horizontal = horizontalPadding, vertical = 12.dp)
                ) {
                    // Search Bar with Filter
                    FavoritesSearchBar(
                        query = searchQuery,
                        onQueryChange = { searchQuery = it },
                        onAudioClick = { isRecording = !isRecording },
                        isRecording = isRecording,
                        accentColor = colorScheme.primary,
                        colorScheme = colorScheme,
                        activeFilterCount = activeFilterCount,
                        onFilterClick = { showFilterModal = true }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Filter Pills
                    FavoritesFilterPillsRow(
                        selectedPill = selectedTypePill,
                        onPillSelected = { selectedTypePill = it },
                        accentColor = colorScheme.primary,
                        colorScheme = colorScheme
                    )
                }
            }
        }
    }

    // Filter Bottom Sheet
    if (showFilterModal) {
        FavoritesFilterBottomSheet(
            filterState = filterState,
            onFilterChange = { filterState = it },
            onDismiss = { showFilterModal = false },
            onApply = { showFilterModal = false },
            onReset = {
                filterState = FavoriteFilterState()
                showFilterModal = false
            },
            accentColor = colorScheme.primary,
            colorScheme = colorScheme
        )
    }
}

// Favorite Job Card
@Composable
fun FavoriteJobCard(
    favorite: FavoriteListing,
    onCardClick: () -> Unit,
    onRemoveFavorite: () -> Unit,
    colorScheme: ColorScheme
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onCardClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Image Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(favorite.imageRes ?: R.drawable.nairobi_city)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Favorite button overlay
                IconButton(
                    onClick = onRemoveFavorite,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(32.dp)
                        .background(
                            color = Color.Black.copy(alpha = 0.5f),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        Icons.Filled.Favorite,
                        contentDescription = "Remove from favorites",
                        tint = colorScheme.secondary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Verified badge
                if (favorite.isVerified) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Filled.Verified,
                                contentDescription = null,
                                tint = colorScheme.primary,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "Verified",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = colorScheme.primary
                            )
                        }
                    }
                }
            }

            // Content Section
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    favorite.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    favorite.subtitle,
                    fontSize = 12.sp,
                    color = colorScheme.onSurfaceVariant,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Outlined.LocationOn,
                        contentDescription = null,
                        tint = colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        favorite.location,
                        fontSize = 11.sp,
                        color = colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 2.dp)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        favorite.price,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFB800),
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            "${favorite.rating}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = colorScheme.onSurface,
                            modifier = Modifier.padding(start = 2.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = colorScheme.primary.copy(alpha = 0.1f)
                    ) {
                        Text(
                            favorite.jobType ?: "Full-time",
                            fontSize = 10.sp,
                            color = colorScheme.primary,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Text(
                        favorite.dateSaved,
                        fontSize = 9.sp,
                        color = colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

// Favorite Housing Card
@Composable
fun FavoriteHousingCard(
    favorite: FavoriteListing,
    onCardClick: () -> Unit,
    onRemoveFavorite: () -> Unit,
    colorScheme: ColorScheme
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onCardClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Image Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(favorite.imageRes ?: R.drawable.happypeople)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                IconButton(
                    onClick = onRemoveFavorite,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(32.dp)
                        .background(
                            color = Color.Black.copy(alpha = 0.5f),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        Icons.Filled.Favorite,
                        contentDescription = "Remove from favorites",
                        tint = colorScheme.secondary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                if (favorite.isVerified) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Filled.Verified,
                                contentDescription = null,
                                tint = colorScheme.primary,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "Verified",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = colorScheme.primary
                            )
                        }
                    }
                }

                // For Sale/Rent tag
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (favorite.isForSale) Color(0xFFFF6B6B) else colorScheme.primary,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp)
                ) {
                    Text(
                        if (favorite.isForSale) "For Sale" else "For Rent",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            // Content Section
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    favorite.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Outlined.LocationOn,
                        contentDescription = null,
                        tint = colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        favorite.location,
                        fontSize = 11.sp,
                        color = colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    favorite.bedrooms?.let {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Outlined.Bed,
                                contentDescription = null,
                                tint = colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                "$it bed",
                                fontSize = 11.sp,
                                color = colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(start = 2.dp)
                            )
                        }
                    }

                    favorite.bathrooms?.let {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Outlined.Bathtub,
                                contentDescription = null,
                                tint = colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                "$it bath",
                                fontSize = 11.sp,
                                color = colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(start = 2.dp)
                            )
                        }
                    }

                    favorite.squareMeters?.let {
                        Text(
                            "$it m²",
                            fontSize = 11.sp,
                            color = colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFB800),
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            "${favorite.rating}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = colorScheme.onSurface,
                            modifier = Modifier.padding(start = 2.dp)
                        )
                    }

                    Text(
                        favorite.price,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.primary
                    )
                }

                Text(
                    favorite.dateSaved,
                    fontSize = 9.sp,
                    color = colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

// Favorite Professional Card
@Composable
fun FavoriteProfessionalCard(
    favorite: FavoriteListing,
    onCardClick: () -> Unit,
    onRemoveFavorite: () -> Unit,
    colorScheme: ColorScheme
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onCardClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(colorScheme.primary.copy(alpha = 0.1f))
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(favorite.imageRes ?: R.drawable.happy_clients)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                if (favorite.isVerified) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .align(Alignment.BottomEnd)
                            .background(colorScheme.primary, CircleShape)
                            .border(2.dp, colorScheme.surface, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(10.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    favorite.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    favorite.specialty ?: favorite.subtitle,
                    fontSize = 12.sp,
                    color = colorScheme.onSurfaceVariant,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFB800),
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            "${favorite.rating}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = colorScheme.onSurface,
                            modifier = Modifier.padding(start = 2.dp)
                        )
                    }

                    Text(
                        "•",
                        fontSize = 11.sp,
                        color = colorScheme.onSurfaceVariant
                    )

                    Text(
                        "${favorite.completedJobs ?: 0} jobs",
                        fontSize = 11.sp,
                        color = colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    favorite.price,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = colorScheme.primary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Remove favorite button
            IconButton(
                onClick = onRemoveFavorite,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    Icons.Filled.Favorite,
                    contentDescription = "Remove from favorites",
                    tint = colorScheme.secondary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // Date saved footer
        Divider(
            color = colorScheme.outlineVariant,
            modifier = Modifier.padding(horizontal = 12.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                favorite.dateSaved,
                fontSize = 10.sp,
                color = colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        }
    }
}

// Search Bar
@Composable
fun FavoritesSearchBar(
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
                                "Search favorites...",
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
                        imageVector = if (isRecording) Icons.Filled.Mic else Icons.Outlined.Mic,
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
                            .background(color = colorScheme.error, shape = CircleShape)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }

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
                        modifier = Modifier.clickable { onFilterClick() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
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

// Filter Pills Row
@Composable
fun FavoritesFilterPillsRow(
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
            "Jobs" to Icons.Outlined.Work,
            "Housing" to Icons.Outlined.Home,
            "Professionals" to Icons.Outlined.Build
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

// Filter Bottom Sheet
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesFilterBottomSheet(
    filterState: FavoriteFilterState,
    onFilterChange: (FavoriteFilterState) -> Unit,
    onDismiss: () -> Unit,
    onApply: () -> Unit,
    onReset: () -> Unit,
    accentColor: Color,
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
                    text = "Filter Favorites",
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

            // Listing Type Selection
            Text(
                text = "Listing Type",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FavoriteTypeChip(
                    label = "Jobs",
                    isSelected = localFilterState.selectedTypes.contains(FavoriteListingType.JOB),
                    onClick = {
                        val newSet = localFilterState.selectedTypes.toMutableSet()
                        if (newSet.contains(FavoriteListingType.JOB)) {
                            newSet.remove(FavoriteListingType.JOB)
                        } else {
                            newSet.add(FavoriteListingType.JOB)
                        }
                        localFilterState = localFilterState.copy(selectedTypes = newSet)
                    },
                    accentColor = accentColor,
                    colorScheme = colorScheme,
                    modifier = Modifier.weight(1f)
                )

                FavoriteTypeChip(
                    label = "Housing",
                    isSelected = localFilterState.selectedTypes.contains(FavoriteListingType.HOUSING),
                    onClick = {
                        val newSet = localFilterState.selectedTypes.toMutableSet()
                        if (newSet.contains(FavoriteListingType.HOUSING)) {
                            newSet.remove(FavoriteListingType.HOUSING)
                        } else {
                            newSet.add(FavoriteListingType.HOUSING)
                        }
                        localFilterState = localFilterState.copy(selectedTypes = newSet)
                    },
                    accentColor = accentColor,
                    colorScheme = colorScheme,
                    modifier = Modifier.weight(1f)
                )

                FavoriteTypeChip(
                    label = "Professionals",
                    isSelected = localFilterState.selectedTypes.contains(FavoriteListingType.PROFESSIONAL),
                    onClick = {
                        val newSet = localFilterState.selectedTypes.toMutableSet()
                        if (newSet.contains(FavoriteListingType.PROFESSIONAL)) {
                            newSet.remove(FavoriteListingType.PROFESSIONAL)
                        } else {
                            newSet.add(FavoriteListingType.PROFESSIONAL)
                        }
                        localFilterState = localFilterState.copy(selectedTypes = newSet)
                    },
                    accentColor = accentColor,
                    colorScheme = colorScheme,
                    modifier = Modifier.weight(1f)
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
                        "Verified listings only",
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

            Spacer(modifier = Modifier.height(16.dp))

            // Selected count indicator
            val selectedCount = listOfNotNull(
                if (localFilterState.selectedTypes.size < 3) 1 else null,
                if (localFilterState.minRating > 0) 1 else null,
                if (localFilterState.isVerifiedOnly) 1 else null
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
                    onClick = onReset,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, colorScheme.outlineVariant),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = colorScheme.onSurfaceVariant
                    )
                ) {
                    Text("Reset", fontSize = 15.sp, fontWeight = FontWeight.Medium)
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
                        containerColor = accentColor,
                        contentColor = colorScheme.onPrimary
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 2.dp,
                        pressedElevation = 4.dp
                    )
                ) {
                    Text("Apply Filters", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun FavoriteTypeChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    accentColor: Color,
    colorScheme: ColorScheme,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) accentColor else colorScheme.surface,
        border = if (!isSelected) BorderStroke(1.dp, colorScheme.outlineVariant) else null,
        modifier = modifier.clickable { onClick() }
    ) {
        Text(
            text = label,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            textAlign = TextAlign.Center,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) colorScheme.onPrimary else colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun FavoritesHeroHeader(
    primaryColor: Color,
    tertiaryColor: Color,
    height: Dp,
    collapseFraction: Float,
    colorScheme: ColorScheme,
    modifier: Modifier = Modifier,
    user: User? = null,
    isGuestMode: Boolean = false,
    displayName: String = "Guest",
    welcomeMessage: String = "Welcome to Pivota",
    onNavigateBack: () -> Unit = {} // Keep parameter but don't use it in header (topBar handles back)
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
            modifier = Modifier.fillMaxSize()
        ) {
            // Background Image
            AnimatedVisibility(
                visible = !collapsed,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(R.drawable.nairobi_city)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    placeholder = painterResource(R.drawable.nairobi_city),
                    error = painterResource(R.drawable.nairobi_city)
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor)
            )

            // Gradient Overlay
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

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
            ) {
                // Top bar with user info (NO back button here - it's in Scaffold's topBar)
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
                                HeaderAvatarFavorites(colorScheme)
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
                                    text = if (isGuestMode) "Hi, Guest" else "Hi, $displayName",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = welcomeMessage,
                                    color = Color.White.copy(0.85f),
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            HeaderActionIconFavorites(
                                icon = Icons.Default.Mail,
                                iconTint = Color.White,
                                backgroundTint = Color.White.copy(alpha = 0.2f),
                                onClick = {}
                            )
                            HeaderActionIconFavorites(
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

            // Title
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
                        text = "Favorites",
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
                        HeaderActionIconFavorites(
                            icon = Icons.Default.Mail,
                            iconTint = Color.White,
                            backgroundTint = Color.White.copy(alpha = 0.2f),
                            onClick = {}
                        )
                        HeaderActionIconFavorites(
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
                        text = "Favorites",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-1.5).sp,
                            fontSize = titleFontSize
                        )
                    )
                    Text(
                        text = "Your saved listings",
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
fun HeaderAvatarFavorites(colorScheme: ColorScheme) {
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
fun HeaderActionIconFavorites(
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