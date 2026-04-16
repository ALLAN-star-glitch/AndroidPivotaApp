package com.example.pivota.dashboard.presentation.screens

import android.annotation.SuppressLint
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.request.crossfade
import com.example.pivota.R
import com.example.pivota.auth.domain.model.User
import com.example.pivota.dashboard.presentation.composables.*

@SuppressLint("FrequentlyChangingValue")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverScreen(
    onNavigateToHouseListings: () -> Unit = {},
    onNavigateToJobListings: () -> Unit = {},
    onNavigateToAllJobs: () -> Unit = {},
    onNavigateToAllHousing: () -> Unit = {},
    onNavigateToAllProviders: () -> Unit = {},
    onNavigateToAllServices: () -> Unit = {},
    onNavigateToAllSupport: () -> Unit = {},
    user: User? = null,
    isGuestMode: Boolean = false
) {
    val colorScheme = MaterialTheme.colorScheme

    val windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val isExpanded = windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.EXPANDED
    val isMedium = windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.MEDIUM
    val isTablet = isExpanded || isMedium

    // Adaptive grid columns based on screen size
    val jobGridColumns = when {
        isExpanded -> 2
        isMedium -> 2
        else -> 1
    }

    val housingGridColumns = when {
        isExpanded -> 2
        isMedium -> 2
        else -> 1
    }

    val professionalGridColumns = when {
        isExpanded -> 2
        isMedium -> 2
        else -> 1
    }

    val primaryColor = colorScheme.primary
    val secondaryColor = colorScheme.secondary
    val tertiaryColor = colorScheme.tertiary
    val softBackground = colorScheme.background

    val listState = rememberLazyListState()

    var searchQuery by remember { mutableStateOf("") }
    var isRecording by remember { mutableStateOf(false) }
    var selectedFilters by remember { mutableStateOf(setOf<String>()) }

    val horizontalPadding = when {
        isExpanded -> 24.dp
        isMedium -> 20.dp
        else -> 16.dp
    }

    // Track if search bar should be pinned
    val isSearchBarPinned by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 3 ||
                    (listState.firstVisibleItemIndex == 3 && listState.firstVisibleItemScrollOffset > 0)
        }
    }

    LaunchedEffect(selectedFilters) {
        when {
            selectedFilters.contains("Properties") -> {
                onNavigateToHouseListings()
                selectedFilters = selectedFilters - "Properties"
            }
            selectedFilters.contains("Jobs") -> {
                onNavigateToJobListings()
                selectedFilters = selectedFilters - "Jobs"
            }
            selectedFilters.contains("Professionals") -> {
                onNavigateToAllProviders()
                selectedFilters = selectedFilters - "Professionals"
            }
        }
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
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 100.dp),
                horizontalAlignment = Alignment.Start
            ) {
                // Reusable Header (replaces NonStickyHeader)
                item {
                    ReusableHeader(
                        colorScheme = colorScheme,
                        pageTitle = "PivotaConnect",
                        pageSubtitle = "Connect to opportunities near you",
                        user = user,
                        isGuestMode = isGuestMode,
                        isSticky = false,
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                // Marketing Carousel Banner
                item {
                    val displayName = remember(user, isGuestMode) {
                        when {
                            user == null || isGuestMode -> "Guest"
                            user.userName.isNotBlank() -> user.userName.split(" ").firstOrNull() ?: "Guest"
                            user.firstName.isNotBlank() -> user.firstName
                            user.email.isNotBlank() -> user.email.split("@").firstOrNull() ?: "Guest"
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

                // Spacer before search bar
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Search Bar + Pills
                item {
                    SearchAndPillsSection(
                        searchQuery = searchQuery,
                        onSearchQueryChange = { searchQuery = it },
                        isRecording = isRecording,
                        onAudioClick = { isRecording = !isRecording },
                        selectedFilters = selectedFilters,
                        onFilterSelected = { filter ->
                            selectedFilters = if (selectedFilters.contains(filter)) {
                                selectedFilters - filter
                            } else {
                                selectedFilters + filter
                            }
                        },
                        primaryColor = primaryColor,
                        secondaryColor = secondaryColor,
                        colorScheme = colorScheme,
                        isTablet = isTablet,
                        horizontalPadding = horizontalPadding
                    )
                }

                // COMMON SERVICES SECTION
                item {
                    ModernSectionHeader(
                        title = "Common Services",
                        actionText = "Browse all →",
                        onActionClick = onNavigateToAllServices,
                        horizontalPadding = horizontalPadding,
                        colorScheme = colorScheme
                    )
                }
                item {
                    ModernServiceGrid(
                        colorScheme = colorScheme,
                        horizontalPadding = horizontalPadding,
                        isTablet = isTablet
                    )
                }

                // Jobs Section
                item {
                    ModernSectionHeader(
                        title = "Jobs Near You",
                        actionText = "View all →",
                        onActionClick = onNavigateToAllJobs,
                        horizontalPadding = horizontalPadding,
                        colorScheme = colorScheme
                    )
                }

                // Jobs Grid
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = horizontalPadding),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val rows = jobItems.chunked(jobGridColumns)
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
                                repeat(jobGridColumns - rowItems.size) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }

                // Housing Section
                item {
                    ModernSectionHeader(
                        title = "Housing Opportunities",
                        actionText = "Browse all →",
                        onActionClick = onNavigateToAllHousing,
                        horizontalPadding = horizontalPadding,
                        colorScheme = colorScheme
                    )
                }

                // Housing Grid
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = horizontalPadding),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val rows = housingItems.chunked(housingGridColumns)
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
                                repeat(housingGridColumns - rowItems.size) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }

                // Professionals Section
                item {
                    ModernSectionHeader(
                        title = "Trusted Professionals",
                        actionText = "See all →",
                        onActionClick = onNavigateToAllProviders,
                        horizontalPadding = horizontalPadding,
                        colorScheme = colorScheme
                    )
                }

                // Professionals Grid
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = horizontalPadding),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val rows = professionalItems.chunked(professionalGridColumns)
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
                                repeat(professionalGridColumns - rowItems.size) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }

                // Social Support Section
                item {
                    ModernSectionHeader(
                        title = "Social Support & Services",
                        actionText = "Get help →",
                        onActionClick = onNavigateToAllSupport,
                        horizontalPadding = horizontalPadding,
                        colorScheme = colorScheme
                    )
                }
                items(supportItems.size) { index ->
                    val item = supportItems[index]
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

            // STICKY SEARCH + PILLS SECTION
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
                            .statusBarsPadding()
                            .padding(horizontal = horizontalPadding, vertical = 12.dp)
                    ) {
                        SearchBarWithAudio(
                            query = searchQuery,
                            onQueryChange = { searchQuery = it },
                            onAudioClick = { isRecording = !isRecording },
                            isRecording = isRecording,
                            primaryColor = primaryColor,
                            colorScheme = colorScheme,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        FilterPillsRow(
                            selectedFilters = selectedFilters,
                            onFilterSelected = { filter ->
                                selectedFilters = if (selectedFilters.contains(filter)) {
                                    selectedFilters - filter
                                } else {
                                    selectedFilters + filter
                                }
                            },
                            primaryColor = primaryColor,
                            colorScheme = colorScheme,
                            isTablet = isTablet
                        )
                    }
                }
            }
        }
    }
}


// Data classes for items (same as before)
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

// NON-STICKY HEADER - With appropriate top spacing
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NonStickyHeader(
    colorScheme: ColorScheme,
    user: User? = null,
    isGuestMode: Boolean = false,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val profileUrl = user?.profileImageUrl?.takeIf { it.isNotBlank() }
    var showMenuBottomSheet by remember { mutableStateOf(false) }

    // Get user info
    val firstName = remember(user, isGuestMode) {
        when {
            user == null || isGuestMode -> "Guest"
            user.firstName.isNotBlank() -> user.firstName
            user.userName.isNotBlank() -> user.userName.split(" ").firstOrNull() ?: "Guest"
            else -> "Guest"
        }
    }

    // Get user role
    val userRole = remember(user, isGuestMode) {
        when {
            isGuestMode -> "Guest User"
            user?.role?.equals("admin", ignoreCase = true) == true -> "Administrator"
            user?.role?.equals("professional", ignoreCase = true) == true -> "Professional"
            else -> "General User"
        }
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()  // Add status bar padding back for non-sticky header
            .padding(horizontal = 16.dp)
            .padding(top = 8.dp, bottom = 8.dp)  // Small top padding
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = Color.Black.copy(alpha = 0.08f),
                spotColor = Color.Black.copy(alpha = 0.06f)
            ),
        shape = RoundedCornerShape(24.dp),
        color = colorScheme.surface.copy(alpha = 0.98f),
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left side - Profile Avatar and User Info
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Profile Avatar (Clickable)
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .clickable { showMenuBottomSheet = true },
                    contentAlignment = Alignment.Center
                ) {
                    if (isGuestMode || profileUrl.isNullOrBlank()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    color = colorScheme.primary.copy(alpha = 0.1f),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Outlined.AccountCircle,
                                contentDescription = "Profile",
                                tint = colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    } else {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(profileUrl)
                                .size(128)
                                .allowHardware(false)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Profile",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .border(
                                    2.dp,
                                    colorScheme.tertiary.copy(alpha = 0.5f),
                                    CircleShape
                                ),
                            placeholder = painterResource(R.drawable.job_placeholder3),
                            error = painterResource(R.drawable.job_placeholder3)
                        )
                    }
                }

                // User Info Column
                Column(
                    modifier = Modifier.clickable { showMenuBottomSheet = true }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Hi, $firstName",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colorScheme.onSurface,
                            letterSpacing = 0.2.sp
                        )
                        // Dropdown Icon - Now next to the name
                        Icon(
                            Icons.Outlined.KeyboardArrowDown,
                            contentDescription = "Menu",
                            tint = colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Text(
                        text = userRole,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Normal,
                        color = colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        letterSpacing = 0.1.sp
                    )
                }
            }

            // Right side - Action icons
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Message Icon
                HeaderIcon(Icons.Outlined.MailOutline, colorScheme)

                // Notifications Icon with badge
                Box {
                    HeaderIcon(Icons.Outlined.NotificationsNone, colorScheme)
                    // Notification badge
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 4.dp, y = 4.dp)
                            .size(10.dp)
                            .background(
                                color = colorScheme.tertiary,
                                shape = CircleShape
                            )
                            .border(
                                width = 1.5.dp,
                                color = colorScheme.error,
                                shape = CircleShape
                            )
                    )
                }
            }
        }
    }

    // Profile Menu Bottom Sheet
    if (showMenuBottomSheet) {
        ProfileMenuBottomSheet(
            onDismiss = { showMenuBottomSheet = false },
            colorScheme = colorScheme,
            onMyAccountClick = {
                showMenuBottomSheet = false
                // Navigate to My Account
            },
            onMyListingsClick = {
                showMenuBottomSheet = false
                // Navigate to My Listings
            },
            onMyFavoritesClick = {
                showMenuBottomSheet = false
                // Navigate to My Favorites
            },
            onPostClick = {
                showMenuBottomSheet = false
                // Navigate to Post
            },
            onLogoutClick = {
                showMenuBottomSheet = false
                // Handle logout
            }
        )
    }
}


// Profile Menu Bottom Sheet
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileMenuBottomSheet(
    onDismiss: () -> Unit,
    colorScheme: ColorScheme,
    onMyAccountClick: () -> Unit,
    onMyListingsClick: () -> Unit,
    onMyFavoritesClick: () -> Unit,
    onPostClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colorScheme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .background(
                        color = colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(2.dp)
                    )
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Menu Items
            ProfileMenuItem(
                icon = Icons.Outlined.Person,
                title = "My Account",
                subtitle = "Manage your profile and settings",
                onClick = onMyAccountClick,
                colorScheme = colorScheme
            )

            Divider(
                color = colorScheme.outlineVariant.copy(alpha = 0.3f),
                modifier = Modifier.padding(vertical = 4.dp)
            )

            ProfileMenuItem(
                icon = Icons.Outlined.List,
                title = "My Listings",
                subtitle = "View and manage your listings",
                onClick = onMyListingsClick,
                colorScheme = colorScheme
            )

            Divider(
                color = colorScheme.outlineVariant.copy(alpha = 0.3f),
                modifier = Modifier.padding(vertical = 4.dp)
            )

            ProfileMenuItem(
                icon = Icons.Outlined.FavoriteBorder,
                title = "My Favorites",
                subtitle = "Saved opportunities",
                onClick = onMyFavoritesClick,
                colorScheme = colorScheme
            )

            Divider(
                color = colorScheme.outlineVariant.copy(alpha = 0.3f),
                modifier = Modifier.padding(vertical = 4.dp)
            )

            ProfileMenuItem(
                icon = Icons.Outlined.AddCircle,
                title = "Post",
                subtitle = "Create a new listing",
                onClick = onPostClick,
                colorScheme = colorScheme
            )

            Divider(
                color = colorScheme.outlineVariant.copy(alpha = 0.3f),
                modifier = Modifier.padding(vertical = 4.dp)
            )

            ProfileMenuItem(
                icon = Icons.Outlined.Logout,
                title = "Logout",
                subtitle = "Sign out from your account",
                onClick = onLogoutClick,
                colorScheme = colorScheme,
                isDestructive = true
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

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
        // Icon Container
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

        // Text Content
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

        // Chevron Icon
        Icon(
            Icons.Outlined.ChevronRight,
            contentDescription = null,
            tint = colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun HeaderIcon(
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

// SEARCH AND PILLS SECTION
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

// NAVIGATION ROW - Simple navigation items without filtering/active states
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

// SEARCH BAR WITH AUDIO - Fixed with proper width
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
                IconButton(
                    onClick = onAudioClick,
                    modifier = Modifier
                        .size(40.dp)
                        .then(
                            if (isRecording) {
                                Modifier.background(
                                    color = primaryColor.copy(0.1f),
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
                        tint = if (isRecording) primaryColor else colorScheme.onSurfaceVariant.copy(0.6f),
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

// SECTION HEADER - Using brand colors
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
            color = colorScheme.tertiary,  // Baobab Gold for action text
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.clickable { onActionClick() }
        )
    }
}

// SUPPORT CARD
@Composable
fun ModernSupportCard(
    name: String,
    service: String,
    location: String,
    isUrgent: Boolean,
    colorScheme: ColorScheme,
    horizontalPadding: Dp = 16.dp
) {
    val primaryColor = colorScheme.primary
    val tertiaryColor = colorScheme.tertiary

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
                        if (isUrgent) colorScheme.error.copy(0.1f) else primaryColor.copy(0.08f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isUrgent) Icons.Outlined.Emergency else Icons.Outlined.VolunteerActivism,
                    contentDescription = null,
                    tint = if (isUrgent) colorScheme.error else primaryColor,
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
                        tint = colorScheme.onSurfaceVariant.copy(0.5f),
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = location,
                        fontSize = 11.sp,
                        color = colorScheme.onSurfaceVariant.copy(0.7f),
                        modifier = Modifier.padding(start = 2.dp)
                    )
                }
            }

            Text(
                text = "View →",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = tertiaryColor
            )
        }
    }
}

// SERVICE GRID
@Composable
fun ModernServiceGrid(
    colorScheme: ColorScheme,
    horizontalPadding: Dp = 16.dp,
    isTablet: Boolean = false
) {
    val services = listOf(
        "Movers" to Icons.Outlined.LocalShipping,
        "Plumbers" to Icons.Outlined.Plumbing,
        "Electricians" to Icons.Outlined.Bolt,
        "Cleaners" to Icons.Outlined.CleaningServices,
        "Trainers" to Icons.Outlined.FitnessCenter,
        "Counselors" to Icons.Outlined.Psychology,
        "Security" to Icons.Outlined.Security,
        "Painters" to Icons.Outlined.FormatPaint
    )

    val columnsPerRow = if (isTablet) 6 else 4

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding)
    ) {
        services.chunked(columnsPerRow).forEach { rowItems ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowItems.forEach { (name, icon) ->
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { /* Navigate to service */ },
                        shape = RoundedCornerShape(12.dp),
                        color = colorScheme.surface,
                        border = BorderStroke(1.dp, colorScheme.outlineVariant),
                        shadowElevation = 0.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                icon,
                                contentDescription = null,
                                tint = colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = name,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = colorScheme.onSurface
                            )
                        }
                    }
                }
                repeat(columnsPerRow - rowItems.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}