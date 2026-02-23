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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import com.example.pivota.R
import kotlinx.coroutines.delay

// Modern, elegant color palette - teal used sparingly as accent
val DeepNavySmart = Color(0xFF0A1A2F)      // Professional dark blue
val WarmGraySmart = Color(0xFFF8F9FA)      // Soft background
val SlateGraySmart = Color(0xFF4A5568)     // Secondary text
val SoftGoldSmart = Color(0xFFD4AF37)      // Premium accent
val ForestGreenSmart = Color(0xFF2E7D32)   // Success/Verified
val CleanWhiteSmart = Color(0xFFFFFFFF)    // Pure white
val LightBorderSmart = Color(0xFFE2E8F0)   // Subtle borders
val MutedTealSmart = Color(0xFF2C6E6E)     // Muted teal for accents

// Updated data classes for different content types
sealed class SmartMatchItem {
    data class Provider(
        val id: String,
        val name: String,
        val businessName: String?,
        val profileImageUrl: String?,
        val rating: Double,
        val reviewCount: Int,
        val description: String,
        val experienceYears: Int,
        val location: String,
        val startingPrice: Int,
        val isVerified: Boolean,
        val responseTime: String,
        val category: String,
        val matchReason: String,
        val matchScore: Int
    ) : SmartMatchItem()

    data class Job(
        val id: String,
        val title: String,
        val company: String,
        val location: String,
        val salary: String,
        val jobType: String,
        val postedTime: String,
        val matchScore: Int,
        val matchReason: String,
        val isRemote: Boolean
    ) : SmartMatchItem()

    data class Housing(
        val id: String,
        val title: String,
        val propertyType: String,
        val location: String,
        val price: Int,
        val bedrooms: Int,
        val bathrooms: Int,
        val squareMeters: Int,
        val imageUrl: String?,
        val matchScore: Int,
        val matchReason: String,
        val isFurnished: Boolean
    ) : SmartMatchItem()

    data class Support(
        val id: String,
        val serviceName: String,
        val providerName: String,
        val category: String,
        val location: String,
        val distance: String,
        val rating: Double,
        val reviewCount: Int,
        val isAvailable247: Boolean,
        val matchScore: Int,
        val matchReason: String
    ) : SmartMatchItem()
}

data class TraySection(
    val title: String,
    val subtitle: String?,
    val icon: ImageVector?,
    val items: List<SmartMatchItem>,
    val viewAllAction: (() -> Unit)? = null
)

@SuppressLint("FrequentlyChangingValue")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmartMatchScreen() {
    // 🎨 Brand Palette - Using teal as accent
    val primaryTeal = MutedTealProviders
    val goldenAccent = SoftGoldProviders
    val softBackground = WarmGraySmartProviders

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

    // State for filters and search
    var searchQuery by remember { mutableStateOf("") }
    var showFilterModal by remember { mutableStateOf(false) }
    var selectedFilterChips by remember { mutableStateOf(setOf<String>()) }
    var activeFilterCount by remember { mutableStateOf(0) }
    var isSearching by remember { mutableStateOf(false) }

    // Debounce search to avoid too many updates
    val debouncedQuery = remember { mutableStateOf("") }
    LaunchedEffect(searchQuery) {
        if (searchQuery.length >= 2) {
            isSearching = true
            delay(300) // Debounce for 300ms
            debouncedQuery.value = searchQuery.lowercase()
            isSearching = false
        } else if (searchQuery.isEmpty()) {
            debouncedQuery.value = ""
            isSearching = false
        }
    }

    // Mock data for different trays
    val allTraySections = remember {
        listOf(
            // "Top Picks for You" - Mixed (Jobs + Houses)
            TraySection(
                title = "✨ Top Picks for You",
                subtitle = "Based on your activity",
                icon = Icons.Outlined.AutoAwesome,
                items = listOf(
                    SmartMatchItem.Job(
                        id = "job1",
                        title = "Senior Social Worker",
                        company = "Nairobi Children's Foundation",
                        location = "Nairobi",
                        salary = "KES 65K - 85K",
                        jobType = "Full-time",
                        postedTime = "2h ago",
                        matchScore = 98,
                        matchReason = "Matches your social work experience",
                        isRemote = false
                    ),
                    SmartMatchItem.Housing(
                        id = "house1",
                        title = "Modern 2BR Apartment",
                        propertyType = "Apartment",
                        location = "Westlands, Nairobi",
                        price = 45000,
                        bedrooms = 2,
                        bathrooms = 2,
                        squareMeters = 85,
                        imageUrl = null,
                        matchScore = 95,
                        matchReason = "In your preferred area",
                        isFurnished = true
                    ),
                    SmartMatchItem.Provider(
                        id = "provider1",
                        name = "Musa Jallow",
                        businessName = "Musa Electrical Services",
                        profileImageUrl = null,
                        rating = 4.8,
                        reviewCount = 32,
                        description = "Expert electrical installations",
                        experienceYears = 8,
                        location = "Westlands",
                        startingPrice = 1500,
                        isVerified = true,
                        responseTime = "10 mins",
                        category = "Electrician",
                        matchReason = "Top-rated in your area",
                        matchScore = 94
                    )
                ),
                viewAllAction = {}
            ),

            // "Work Opportunities" - Job Listings
            TraySection(
                title = "Work Opportunities",
                subtitle = "Jobs matched to your skills",
                icon = Icons.Outlined.Work,
                items = List(5) { index ->
                    SmartMatchItem.Job(
                        id = "job${index + 2}",
                        title = when (index) {
                            0 -> "Community Health Worker"
                            1 -> "Project Manager - NGO"
                            2 -> "Customer Support Lead"
                            3 -> "Sales Representative"
                            else -> "Graphic Designer"
                        },
                        company = listOf("Amref", "Save the Children", "Safaricom", "KCB", "UNICEF")[index],
                        location = listOf("Nairobi", "Mombasa", "Kisumu", "Nakuru", "Remote")[index],
                        salary = listOf("KES 45K", "KES 120K", "KES 55K", "KES 40K + Commission", "KES 60K")[index],
                        jobType = listOf("Full-time", "Contract", "Full-time", "Part-time", "Remote")[index],
                        postedTime = listOf("1d ago", "3h ago", "Just now", "2d ago", "5h ago")[index],
                        matchScore = listOf(92, 88, 85, 82, 79)[index],
                        matchReason = listOf(
                            "Healthcare background match",
                            "Leadership experience",
                            "Customer service skills",
                            "Sales experience",
                            "Portfolio review"
                        )[index],
                        isRemote = index == 4
                    )
                },
                viewAllAction = {}
            ),

            // "Near Your Location" - Support & Services
            TraySection(
                title = "Near Your Location",
                subtitle = "Services in Kilimani area",
                icon = Icons.Outlined.LocationOn,
                items = List(4) { index ->
                    SmartMatchItem.Support(
                        id = "support${index + 1}",
                        serviceName = listOf("Emergency Plumbing", "24/7 Electrician", "Cleaning Service", "Security Installer")[index],
                        providerName = listOf("Pipemasters Ltd", "Musa Electricals", "Sparkle Clean", "SecureTech")[index],
                        category = listOf("Plumbing", "Electrical", "Cleaning", "Security")[index],
                        location = "Kilimani",
                        distance = listOf("0.8 km", "1.2 km", "2.1 km", "1.5 km")[index],
                        rating = listOf(4.8, 4.9, 4.7, 4.6)[index],
                        reviewCount = listOf(45, 89, 67, 23)[index],
                        isAvailable247 = index % 2 == 0,
                        matchScore = listOf(94, 92, 88, 85)[index],
                        matchReason = listOf(
                            "Nearby & available now",
                            "High-rated in your area",
                            "Popular in your building",
                            "Emergency response nearby"
                        )[index]
                    )
                },
                viewAllAction = {}
            ),

            // "House Opportunities" - Housing & Decor
            TraySection(
                title = "House Opportunities",
                subtitle = "Based on your current housing",
                icon = Icons.Outlined.Home,
                items = List(4) { index ->
                    SmartMatchItem.Housing(
                        id = "upgrade${index + 1}",
                        title = listOf(
                            "Interior Design Consultation",
                            "Modern Furniture Package",
                            "Smart Home Installation",
                            "Paint & Renovation Services"
                        )[index],
                        propertyType = listOf("Service", "Package", "Installation", "Service")[index],
                        location = "Nairobi",
                        price = listOf(5000, 75000, 35000, 25000)[index],
                        bedrooms = 0,
                        bathrooms = 0,
                        squareMeters = 0,
                        imageUrl = null,
                        matchScore = listOf(96, 91, 89, 87)[index],
                        matchReason = listOf(
                            "Perfect for your apartment",
                            "Matches your style preferences",
                            "Security upgrade",
                            "Quick transformation"
                        )[index],
                        isFurnished = index == 1
                    )
                },
                viewAllAction = {}
            )
        )
    }

    // Filter sections based on selected categories
    val categoryFilteredSections = remember(selectedFilterChips) {
        if (selectedFilterChips.isEmpty() || selectedFilterChips.contains("All")) {
            allTraySections
        } else {
            allTraySections.filter { section ->
                when {
                    selectedFilterChips.contains("Jobs") && section.title.contains("Work") -> true
                    selectedFilterChips.contains("Housing") && (section.title.contains("House") || section.title.contains("Picks") && section.items.any { it is SmartMatchItem.Housing }) -> true
                    selectedFilterChips.contains("Services") && (section.title.contains("Location") || section.title.contains("Picks") && section.items.any { it is SmartMatchItem.Support }) -> true
                    selectedFilterChips.contains("Support") && section.title.contains("Location") -> true
                    else -> false
                }
            }
        }
    }

    // Apply search filter to items within sections
    val filteredTraySections = remember(debouncedQuery.value, categoryFilteredSections) {
        if (debouncedQuery.value.isEmpty()) {
            categoryFilteredSections
        } else {
            categoryFilteredSections.map { section ->
                val filteredItems = section.items.filter { item ->
                    when (item) {
                        is SmartMatchItem.Job ->
                            item.title.lowercase().contains(debouncedQuery.value) ||
                                    item.company.lowercase().contains(debouncedQuery.value) ||
                                    item.location.lowercase().contains(debouncedQuery.value)
                        is SmartMatchItem.Housing ->
                            item.title.lowercase().contains(debouncedQuery.value) ||
                                    item.location.lowercase().contains(debouncedQuery.value) ||
                                    item.propertyType.lowercase().contains(debouncedQuery.value)
                        is SmartMatchItem.Provider ->
                            item.name.lowercase().contains(debouncedQuery.value) ||
                                    item.businessName?.lowercase()?.contains(debouncedQuery.value) == true ||
                                    item.category.lowercase().contains(debouncedQuery.value) ||
                                    item.location.lowercase().contains(debouncedQuery.value)
                        is SmartMatchItem.Support ->
                            item.serviceName.lowercase().contains(debouncedQuery.value) ||
                                    item.providerName.lowercase().contains(debouncedQuery.value) ||
                                    item.category.lowercase().contains(debouncedQuery.value)
                    }
                }
                section.copy(items = filteredItems)
            }.filter { it.items.isNotEmpty() }
        }
    }

    Scaffold(
        containerColor = softBackground,
        topBar = {
            // Collapsible Header (unchanged)
            SmartMatchHeroHeader(
                teal = primaryTeal,
                gold = goldenAccent,
                height = animatedHeight,
                collapseFraction = collapseFraction
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = padding.calculateBottomPadding())
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    top = maxHeight + 72.dp // Add space for sticky search bar
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
                                color = SlateGrayProviders,
                                fontWeight = FontWeight.Medium
                            )
                            if (isSearching) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = primaryTeal
                                )
                            } else {
                                Text(
                                    text = "${filteredTraySections.sumOf { it.items.size }} items found",
                                    fontSize = 13.sp,
                                    color = primaryTeal,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                // No results message
                if (filteredTraySections.isEmpty() && debouncedQuery.value.isNotEmpty() && !isSearching) {
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
                                Icon(
                                    Icons.Outlined.SearchOff,
                                    contentDescription = null,
                                    tint = SlateGrayProviders.copy(0.5f),
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "No results found",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepNavyProviders
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Try different keywords or clear filters",
                                    fontSize = 14.sp,
                                    color = SlateGrayProviders,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                // 🎯 Horizontal Tray Sections
                items(filteredTraySections.size) { index ->
                    val section = filteredTraySections[index]
                    SmartMatchTraySection(
                        section = section,
                        accentColor = primaryTeal,
                        goldAccent = goldenAccent,
                        onItemClick = { item ->
                            // Handle item click based on type
                        },
                        onViewAllClick = section.viewAllAction
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item { Spacer(Modifier.height(80.dp)) }
            }

            // 📌 Sticky Search Bar - Positioned just below header
            StickySearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                onFilterClick = { showFilterModal = true },
                onVoiceClick = { /* Handle voice search */ },
                activeFilterCount = activeFilterCount,
                accentColor = primaryTeal,
                headerHeight = animatedHeight,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }

    // Filter Modal Bottom Sheet - Simplified with just Content Type
    if (showFilterModal) {
        FilterBottomSheet(
            onDismiss = { showFilterModal = false },
            onApply = { filters ->
                val selectedTypes = filters["categories"] as? Set<String> ?: emptySet()
                selectedFilterChips = if (selectedTypes.isEmpty()) emptySet() else selectedTypes
                activeFilterCount = selectedTypes.size
                showFilterModal = false
            },
            onReset = {
                selectedFilterChips = emptySet()
                activeFilterCount = 0
                showFilterModal = false
            },
            accentColor = primaryTeal
        )
    }
}

/* ─────────────────────────────────────────────
   STICKY SEARCH BAR (just below header)
   ───────────────────────────────────────────── */

@Composable
fun StickySearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onFilterClick: () -> Unit,
    onVoiceClick: () -> Unit,
    activeFilterCount: Int,
    accentColor: Color,
    headerHeight: Dp,
    modifier: Modifier = Modifier
) {
    // Calculate the top position based on header height
    Box(
        modifier = modifier
            .fillMaxWidth()
            .offset(y = headerHeight)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(16.dp),
                    ambientColor = Color.Black.copy(0.08f),
                    spotColor = Color.Black.copy(0.08f)
                ),
            shape = RoundedCornerShape(16.dp),
            color = CleanWhiteProviders,
            tonalElevation = 2.dp
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
                    tint = SlateGrayProviders.copy(0.6f),
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
                                    text = "Search listings ... ",
                                    color = SlateGrayProviders.copy(0.5f),
                                    fontSize = 14.sp
                                )
                            }
                            innerTextField()
                        }
                    },
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 14.sp,
                        color = DeepNavyProviders
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
                            tint = SlateGrayProviders.copy(0.6f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                } else {
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
                                        color = Color.White,
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

                    Spacer(modifier = Modifier.width(8.dp))

                    // Voice search icon
                    IconButton(
                        onClick = onVoiceClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Outlined.Mic,
                            contentDescription = "Voice search",
                            tint = SlateGrayProviders.copy(0.6f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

/* ─────────────────────────────────────────────
   SMART MATCH TRAY SECTION
   ───────────────────────────────────────────── */

@Composable
fun SmartMatchTraySection(
    section: TraySection,
    accentColor: Color,
    goldAccent: Color,
    onItemClick: (SmartMatchItem) -> Unit,
    onViewAllClick: (() -> Unit)?
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Section Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                section.icon?.let {
                    Icon(
                        it,
                        contentDescription = null,
                        tint = goldAccent,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column {
                    Text(
                        text = section.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = DeepNavyProviders
                    )
                    section.subtitle?.let {
                        Text(
                            text = it,
                            fontSize = 12.sp,
                            color = SlateGrayProviders
                        )
                    }
                }
            }

            if (onViewAllClick != null && section.items.size > 3) {
                TextButton(
                    onClick = onViewAllClick,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        "View All",
                        fontSize = 12.sp,
                        color = accentColor,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Horizontal Scrollable Tray
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(section.items.size) { index ->
                val item = section.items[index]
                when (item) {
                    is SmartMatchItem.Provider -> {
                        ProviderTrayCard(
                            provider = item,
                            accentColor = accentColor,
                            goldAccent = goldAccent,
                            onClick = { onItemClick(item) }
                        )
                    }
                    is SmartMatchItem.Job -> {
                        JobTrayCard(
                            job = item,
                            accentColor = accentColor,
                            goldAccent = goldAccent,
                            onClick = { onItemClick(item) }
                        )
                    }
                    is SmartMatchItem.Housing -> {
                        HousingTrayCard(
                            housing = item,
                            accentColor = accentColor,
                            goldAccent = goldAccent,
                            onClick = { onItemClick(item) }
                        )
                    }
                    is SmartMatchItem.Support -> {
                        SupportTrayCard(
                            support = item,
                            accentColor = accentColor,
                            goldAccent = goldAccent,
                            onClick = { onItemClick(item) }
                        )
                    }
                }
            }
        }
    }
}

/* ─────────────────────────────────────────────
   TRAY CARD COMPONENTS
   ───────────────────────────────────────────── */

@Composable
fun ProviderTrayCard(
    provider: SmartMatchItem.Provider,
    accentColor: Color,
    goldAccent: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(240.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CleanWhiteProviders),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp,
            pressedElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // Match Score Badge
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = goldAccent.copy(0.15f),
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(
                    text = "${provider.matchScore}% Match",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = goldAccent,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Provider Info
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = CircleShape,
                    color = accentColor.copy(0.1f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            provider.name.take(1),
                            color = accentColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            provider.name,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = DeepNavyProviders,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (provider.isVerified) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                Icons.Outlined.Verified,
                                contentDescription = "Verified",
                                tint = ForestGreenProviders,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                    provider.businessName?.let {
                        Text(
                            it,
                            fontSize = 10.sp,
                            color = SlateGrayProviders,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Category and Price
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = accentColor.copy(0.1f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        provider.category,
                        fontSize = 9.sp,
                        color = accentColor,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }

                Text(
                    "KES ${provider.startingPrice}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = DeepNavyProviders
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Match Reason
            Text(
                provider.matchReason,
                fontSize = 10.sp,
                color = SlateGrayProviders,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Rating
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Outlined.Star,
                    contentDescription = null,
                    tint = goldAccent,
                    modifier = Modifier.size(12.dp)
                )
                Text(
                    " ${provider.rating} (${provider.reviewCount})",
                    fontSize = 10.sp,
                    color = SlateGrayProviders
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    "⚡ ${provider.responseTime}",
                    fontSize = 9.sp,
                    color = SlateGrayProviders.copy(0.7f)
                )
            }
        }
    }
}

@Composable
fun JobTrayCard(
    job: SmartMatchItem.Job,
    accentColor: Color,
    goldAccent: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(240.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CleanWhiteProviders),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp,
            pressedElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // Header with Match Score and Remote Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = goldAccent.copy(0.15f)
                ) {
                    Text(
                        text = "${job.matchScore}% Match",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = goldAccent,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                if (job.isRemote) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = accentColor.copy(0.1f)
                    ) {
                        Text(
                            "Remote",
                            fontSize = 9.sp,
                            color = accentColor,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Job Title and Company
            Text(
                job.title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = DeepNavyProviders,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                job.company,
                fontSize = 12.sp,
                color = SlateGrayProviders
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Location and Salary
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Outlined.LocationOn,
                    contentDescription = null,
                    tint = SlateGrayProviders.copy(0.5f),
                    modifier = Modifier.size(12.dp)
                )
                Text(
                    " ${job.location}",
                    fontSize = 10.sp,
                    color = SlateGrayProviders,
                    modifier = Modifier.weight(1f)
                )
            }

            Text(
                job.salary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = DeepNavyProviders,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Match Reason
            Text(
                job.matchReason,
                fontSize = 10.sp,
                color = SlateGrayProviders,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Posted Time
            Text(
                "⏱ ${job.postedTime}",
                fontSize = 9.sp,
                color = SlateGrayProviders.copy(0.7f)
            )
        }
    }
}

@Composable
fun HousingTrayCard(
    housing: SmartMatchItem.Housing,
    accentColor: Color,
    goldAccent: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(240.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CleanWhiteProviders),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp,
            pressedElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // Match Score
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = goldAccent.copy(0.15f),
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(
                    text = "${housing.matchScore}% Match",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = goldAccent,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Property Image Placeholder
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                color = accentColor.copy(0.1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Outlined.Home,
                        contentDescription = null,
                        tint = accentColor.copy(0.3f),
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Title and Type
            Text(
                housing.title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = DeepNavyProviders,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = accentColor.copy(0.1f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        housing.propertyType,
                        fontSize = 9.sp,
                        color = accentColor,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                if (housing.isFurnished) {
                    Surface(
                        color = goldAccent.copy(0.15f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            "Furnished",
                            fontSize = 9.sp,
                            color = goldAccent,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Price
            Text(
                "KES ${housing.price}/mo",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = DeepNavyProviders
            )

            // Location
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Icon(
                    Icons.Outlined.LocationOn,
                    contentDescription = null,
                    tint = SlateGrayProviders.copy(0.5f),
                    modifier = Modifier.size(10.dp)
                )
                Text(
                    " ${housing.location}",
                    fontSize = 10.sp,
                    color = SlateGrayProviders
                )
            }

            // Match Reason
            Text(
                housing.matchReason,
                fontSize = 10.sp,
                color = SlateGrayProviders,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun SupportTrayCard(
    support: SmartMatchItem.Support,
    accentColor: Color,
    goldAccent: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(220.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CleanWhiteProviders),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp,
            pressedElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // Header with Match and Availability
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = goldAccent.copy(0.15f)
                ) {
                    Text(
                        text = "${support.matchScore}% Match",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = goldAccent,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                if (support.isAvailable247) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = accentColor.copy(0.1f)
                    ) {
                        Text(
                            "24/7",
                            fontSize = 9.sp,
                            color = accentColor,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Service and Provider
            Text(
                support.serviceName,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = DeepNavyProviders,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                support.providerName,
                fontSize = 12.sp,
                color = SlateGrayProviders
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Distance and Rating
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Outlined.LocationOn,
                        contentDescription = null,
                        tint = SlateGrayProviders.copy(0.5f),
                        modifier = Modifier.size(10.dp)
                    )
                    Text(
                        " ${support.distance}",
                        fontSize = 10.sp,
                        color = SlateGrayProviders
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Outlined.Star,
                        contentDescription = null,
                        tint = goldAccent,
                        modifier = Modifier.size(10.dp)
                    )
                    Text(
                        " ${support.rating}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = DeepNavyProviders
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Match Reason
            Text(
                support.matchReason,
                fontSize = 10.sp,
                color = SlateGrayProviders,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/* ─────────────────────────────────────────────
   SIMPLIFIED FILTER BOTTOM SHEET - Improved Design
   ───────────────────────────────────────────── */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    onDismiss: () -> Unit,
    onApply: (Map<String, Any>) -> Unit,
    onReset: () -> Unit,
    accentColor: Color
) {
    // Filter states - only Content Type
    var selectedCategories by remember { mutableStateOf(setOf<String>()) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = CleanWhiteProviders,
        tonalElevation = 8.dp,
        dragHandle = { BottomSheetDefaults.DragHandle(color = LightBorderProviders) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            // Header with title and close button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filter Content",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = DeepNavyProviders
                    )
                )

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(40.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = LightBorderProviders.copy(0.5f),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Outlined.Close,
                                contentDescription = "Close",
                                tint = SlateGrayProviders,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // Content Type Label
            Text(
                text = "Show me:",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = DeepNavyProviders,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Filter Chips - Compact and well-spaced
            val contentTypes = listOf("Jobs", "Services", "Housing", "Support")

            // First row of chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                contentTypes.take(2).forEach { type ->
                    val isSelected = selectedCategories.contains(type)
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            selectedCategories = if (isSelected) {
                                selectedCategories - type
                            } else {
                                selectedCategories + type
                            }
                        },
                        label = {
                            Text(
                                type,
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        modifier = Modifier.weight(1f),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = accentColor,
                            selectedLabelColor = CleanWhiteProviders,
                            containerColor = CleanWhiteProviders,
                            labelColor = SlateGrayProviders
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) accentColor else LightBorderProviders
                        ),
                        shape = RoundedCornerShape(30.dp)
                    )
                }
            }

            // Second row of chips
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                contentTypes.drop(2).forEach { type ->
                    val isSelected = selectedCategories.contains(type)
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            selectedCategories = if (isSelected) {
                                selectedCategories - type
                            } else {
                                selectedCategories + type
                            }
                        },
                        label = {
                            Text(
                                type,
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        modifier = Modifier.weight(1f),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = accentColor,
                            selectedLabelColor = CleanWhiteProviders,
                            containerColor = CleanWhiteProviders,
                            labelColor = SlateGrayProviders
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) accentColor else LightBorderProviders
                        ),
                        shape = RoundedCornerShape(30.dp)
                    )
                }
            }

            // Selected count indicator (compact)
            if (selectedCategories.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    color = accentColor.copy(0.1f),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.wrapContentSize()
                ) {
                    Text(
                        text = "${selectedCategories.size} filter${if (selectedCategories.size > 1) "s" else ""} selected",
                        fontSize = 12.sp,
                        color = accentColor,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(16.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Bottom Action Buttons - Fixed spacing
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Clear Button
                OutlinedButton(
                    onClick = {
                        selectedCategories = setOf()
                        onReset()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, LightBorderProviders),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = SlateGrayProviders
                    )
                ) {
                    Text(
                        "Clear",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Apply Button - Prominent
                Button(
                    onClick = {
                        val filters = mapOf("categories" to selectedCategories)
                        onApply(filters)
                    },
                    modifier = Modifier
                        .weight(2f)
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = accentColor,
                        contentColor = CleanWhiteProviders
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

// Keep the original header (unchanged)
@Composable
fun SmartMatchHeroHeader(
    teal: Color,
    gold: Color,
    height: Dp,
    collapseFraction: Float,
    modifier: Modifier = Modifier
) {
    val collapsed = collapseFraction > 0.85f

    val maxFontSize = 34.sp
    val minFontSize = 24.sp

    // Animate background color based on collapse state
    val backgroundColor by animateColorAsState(
        targetValue = if (collapsed) teal.copy(alpha = 0.95f) else Color.Transparent,
        animationSpec = tween(durationMillis = 300)
    )

    // Animate font size based on collapseFraction
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
            // Background Image - fades out when collapsed
            AnimatedVisibility(
                visible = !collapsed,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.happy_clients),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Solid color background for collapsed state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor)
            )

            // Gradient overlay - only visible when not collapsed
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
                                    teal.copy(0.95f),
                                    teal.copy(0.75f),
                                    Color.Transparent
                                )
                            )
                        )
                )
            }

            // Golden Accent (Horizontal Glow) - only visible when not collapsed
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
                                colors = listOf(gold.copy(0.15f), Color.Transparent),
                                endX = 400f
                            )
                        )
                )
            }

            // Main content container
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
            ) {
                // TOP ROW - Only visible when NOT collapsed
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
                        // 👤 AVATAR SECTION
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box {
                                HeaderAvatarSmartMatch(teal)
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .background(gold, CircleShape)
                                        .border(2.dp, teal, CircleShape)
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
                                    "Your AI-Picks",
                                    color = Color.White.copy(0.85f),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }

                        // 🔔 ICON ROW
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            HeaderActionIconSmartMatch(
                                icon = Icons.Default.Mail,
                                iconTint = Color.White,
                                backgroundTint = Color.White.copy(alpha = 0.2f),
                                onClick = {}
                            )
                            HeaderActionIconSmartMatch(
                                icon = Icons.Default.Notifications,
                                iconTint = Color.White,
                                backgroundTint = Color.White.copy(alpha = 0.2f),
                                onClick = {}
                            )
                        }
                    }
                }

                // Spacer to push content down when not collapsed
                if (!collapsed) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }

            // Main content area - Changes based on collapse state
            if (collapsed) {
                // COLLAPSED STATE - All elements in one horizontal row, lowered
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterStart)
                        .padding(start = 20.dp, end = 20.dp)
                        .offset(y = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Left side - SmartMatch text with icon
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = gold,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "SmartMatch",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                letterSpacing = (-1.5).sp,
                                fontSize = titleFontSize
                            )
                        )
                    }

                    // Right side - Icons
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        HeaderActionIconSmartMatch(
                            icon = Icons.Default.Mail,
                            iconTint = Color.White,
                            backgroundTint = Color.White.copy(alpha = 0.2f),
                            onClick = {}
                        )
                        HeaderActionIconSmartMatch(
                            icon = Icons.Default.Notifications,
                            iconTint = Color.White,
                            backgroundTint = Color.White.copy(alpha = 0.2f),
                            onClick = {}
                        )
                    }
                }
            } else {
                // EXPANDED STATE
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 20.dp, bottom = 32.dp)
                        .statusBarsPadding()
                ) {
                    // SmartMatch text with icon
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(bottom = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = gold,
                            modifier = Modifier.size(28.dp)
                        )
                        Text(
                            text = "SmartMatch",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                letterSpacing = (-1.5).sp,
                                fontSize = titleFontSize
                            )
                        )
                    }

                    // 📝 Subtitle Text
                    Text(
                        text = "Opportunities tailored for you",
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
fun HeaderAvatarSmartMatch(teal: Color) {
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
fun HeaderActionIconSmartMatch(
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

// Fixed FlowRow utility with proper constraints
@Composable
fun FlowRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: @Composable () -> Unit
) {
    Layout(
        content = content,
        modifier = modifier
    ) { measurables, constraints ->
        // Ensure we don't get infinite constraints
        val maxWidth = if (constraints.maxWidth == Constraints.Infinity) {
            // If infinite, use a reasonable max (screen width)
            constraints.maxWidth
        } else {
            constraints.maxWidth
        }

        // Cap the max height to prevent overflow
        val maxHeight = if (constraints.maxHeight == Constraints.Infinity) {
            Int.MAX_VALUE.coerceAtMost(10000) // Reasonable max
        } else {
            constraints.maxHeight
        }

        val looseConstraints = constraints.copy(
            maxWidth = maxWidth,
            maxHeight = maxHeight
        )

        val placeables = measurables.map { it.measure(looseConstraints) }

        layout(maxWidth, maxHeight) {
            var xPosition = 0
            var yPosition = 0
            var rowMaxHeight = 0

            placeables.forEach { placeable ->
                if (xPosition + placeable.width > maxWidth) {
                    xPosition = 0
                    yPosition += rowMaxHeight
                    rowMaxHeight = 0
                }

                placeable.placeRelative(x = xPosition, y = yPosition)
                xPosition += placeable.width
                rowMaxHeight = maxOf(rowMaxHeight, placeable.height)
            }
        }
    }
}