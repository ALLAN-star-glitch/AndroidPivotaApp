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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.pivota.R
import com.example.pivota.dashboard.domain.EmployerType
import com.example.pivota.dashboard.presentation.composables.ModernJobCard
import com.example.pivota.dashboard.presentation.composables.ModernHousingCard
import com.example.pivota.dashboard.presentation.composables.ModernProviderCard
import kotlinx.coroutines.delay

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
        val imageRes: Int? = null,
        val matchScore: Int,
        val matchReason: String,
        val isFurnished: Boolean
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
    val colorScheme = MaterialTheme.colorScheme

    // 🎨 Brand Palette - Using theme colors with better contrast
    val primaryColor = colorScheme.primary          // African Sapphire
    val secondaryColor = colorScheme.secondary      // Warm Terracotta
    val tertiaryColor = colorScheme.tertiary        // Baobab Gold
    val softBackground = colorScheme.background
    val surfaceColor = colorScheme.surface
    val onSurfaceColor = colorScheme.onSurface
    val onSurfaceVariantColor = colorScheme.onSurfaceVariant
    val outlineVariantColor = colorScheme.outlineVariant

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

    // State for filters and search
    var searchQuery by remember { mutableStateOf("") }
    var showFilterModal by remember { mutableStateOf(false) }
    var selectedFilterChips by remember { mutableStateOf(setOf<String>()) }
    var activeFilterCount by remember { mutableStateOf(0) }
    var isSearching by remember { mutableStateOf(false) }
    var isRecording by remember { mutableStateOf(false) }

    // Debounce search to avoid too many updates
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

    // Mock data for different trays - REMOVED "Near Your Location" section
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
                        imageRes = R.drawable.property_placeholder1,
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
                        imageRes = R.drawable.property_placeholder1,
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
                    selectedFilterChips.contains("Providers") && section.title.contains("Picks") && section.items.any { it is SmartMatchItem.Provider } -> true
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
                                    item.location.lowercase().contains(debouncedQuery.value) ||
                                    item.jobType.lowercase().contains(debouncedQuery.value)
                        is SmartMatchItem.Housing ->
                            item.title.lowercase().contains(debouncedQuery.value) ||
                                    item.location.lowercase().contains(debouncedQuery.value) ||
                                    item.propertyType.lowercase().contains(debouncedQuery.value)
                        is SmartMatchItem.Provider ->
                            item.name.lowercase().contains(debouncedQuery.value) ||
                                    item.businessName?.lowercase()?.contains(debouncedQuery.value) == true ||
                                    item.category.lowercase().contains(debouncedQuery.value) ||
                                    item.location.lowercase().contains(debouncedQuery.value)
                    }
                }
                section.copy(items = filteredItems)
            }.filter { it.items.isNotEmpty() }
        }
    }

    Scaffold(
        containerColor = softBackground,
        topBar = {
            SmartMatchHeroHeader(
                teal = primaryColor,
                gold = tertiaryColor,
                height = animatedHeight,
                collapseFraction = collapseFraction,
                colorScheme = colorScheme
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
                    top = maxHeight + 72.dp
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
                                color = onSurfaceVariantColor,
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
                                    text = "${filteredTraySections.sumOf { it.items.size }} items found",
                                    fontSize = 13.sp,
                                    color = primaryColor,
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
                                    tint = onSurfaceVariantColor.copy(0.5f),
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "No results found",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = onSurfaceColor
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Try different keywords or clear filters",
                                    fontSize = 14.sp,
                                    color = onSurfaceVariantColor,
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
                        primaryColor = primaryColor,
                        secondaryColor = secondaryColor,
                        tertiaryColor = tertiaryColor,
                        colorScheme = colorScheme,
                        onItemClick = { item ->
                            // Handle item click based on type
                        },
                        onViewAllClick = section.viewAllAction
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item { Spacer(Modifier.height(80.dp)) }
            }

            // 📌 Sticky Search Bar (Providers style)
            StickySearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                onFilterClick = { showFilterModal = true },
                onAudioClick = {
                    isRecording = !isRecording
                },
                isRecording = isRecording,
                activeFilterCount = activeFilterCount,
                accentColor = primaryColor,
                headerHeight = animatedHeight,
                showShadow = isPastThreshold.value,
                colorScheme = colorScheme,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .zIndex(1f)
            )
        }
    }

    // Filter Modal Bottom Sheet
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
            accentColor = primaryColor,
            colorScheme = colorScheme
        )
    }
}

/* ─────────────────────────────────────────────
   STICKY SEARCH BAR (Providers style with audio and shadow)
   ───────────────────────────────────────────── */

@Composable
fun StickySearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onFilterClick: () -> Unit,
    onAudioClick: () -> Unit,
    isRecording: Boolean,
    activeFilterCount: Int,
    accentColor: Color,
    headerHeight: Dp,
    showShadow: Boolean,
    colorScheme: androidx.compose.material3.ColorScheme,
    modifier: Modifier = Modifier
) {
    val elevation by animateDpAsState(
        targetValue = if (showShadow) 8.dp else 0.dp,
        animationSpec = tween(durationMillis = 200)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = headerHeight)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .shadow(
                    elevation = elevation,
                    shape = RoundedCornerShape(16.dp),
                    ambientColor = Color.Black.copy(0.08f),
                    spotColor = Color.Black.copy(0.08f)
                ),
            shape = RoundedCornerShape(16.dp),
            color = colorScheme.surface,
            tonalElevation = if (showShadow) 4.dp else 0.dp
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
                    value = query,
                    onValueChange = onQueryChange,
                    modifier = Modifier.weight(1f),
                    decorationBox = { innerTextField ->
                        Box {
                            if (query.isEmpty()) {
                                Text(
                                    text = "Search jobs, houses, services...",
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
}

/* ─────────────────────────────────────────────
   SMART MATCH TRAY SECTION - Using reusable cards
   ───────────────────────────────────────────── */

@Composable
fun SmartMatchTraySection(
    section: TraySection,
    primaryColor: Color,
    secondaryColor: Color,
    tertiaryColor: Color,
    colorScheme: androidx.compose.material3.ColorScheme,
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
                        tint = tertiaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column {
                    Text(
                        text = section.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = colorScheme.onSurface,
                        style = MaterialTheme.typography.titleMedium
                    )
                    section.subtitle?.let {
                        Text(
                            text = it,
                            fontSize = 12.sp,
                            color = colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodySmall
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
                        color = primaryColor,
                        fontWeight = FontWeight.Medium,
                        style = MaterialTheme.typography.labelLarge
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
                        ModernProviderCard(
                            name = item.name,
                            specialty = item.category,
                            rating = item.rating.toFloat(),
                            jobs = item.reviewCount,
                            isVerified = item.isVerified,
                            onCardClick = { onItemClick(item) },
                            onViewClick = { /* View provider */ },
                            onBookClick = { /* Book provider */ },
                            onMessageClick = { /* Send message */ },
                            onWhatsAppClick = { /* WhatsApp */ },
                            onPhoneClick = { /* Call */ }
                        )
                    }
                    is SmartMatchItem.Job -> {
                        ModernJobCard(
                            title = item.title,
                            company = item.company,
                            location = item.location,
                            salary = item.salary,
                            type = item.jobType,
                            isVerified = true,
                            employerType = EmployerType.ORGANIZATION,
                            isFavorite = false,
                            onFavoriteClick = {},
                            onViewClick = { onItemClick(item) },
                            onApplyClick = { /* Handle apply */ }
                        )
                    }
                    is SmartMatchItem.Housing -> {
                        ModernHousingCard(
                            price = "KES ${item.price}",
                            title = item.title,
                            location = item.location,
                            type = item.propertyType,
                            rating = item.matchScore.toDouble(),
                            isVerified = true,
                            isForSale = false,
                            imageRes = item.imageRes ?: R.drawable.property_placeholder1,
                            onViewClick = { onItemClick(item) },
                            onBookClick = { /* Handle book */ },
                            onMessageClick = { /* Handle message */ },
                            onWhatsAppClick = { /* Handle WhatsApp */ },
                            onPhoneClick = { /* Handle phone */ },
                            onClick = { onItemClick(item) }
                        )
                    }
                }
            }
        }
    }
}

/* ─────────────────────────────────────────────
   SIMPLIFIED FILTER BOTTOM SHEET
   ───────────────────────────────────────────── */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    onDismiss: () -> Unit,
    onApply: (Map<String, Any>) -> Unit,
    onReset: () -> Unit,
    accentColor: Color,
    colorScheme: androidx.compose.material3.ColorScheme
) {
    var selectedCategories by remember { mutableStateOf(setOf<String>()) }

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
                    text = "Filter Content",
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
                text = "Show me:",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Filter Chips
            val contentTypes = listOf("Jobs", "Housing", "Providers")

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                contentTypes.forEach { type ->
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

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        selectedCategories = setOf()
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
                        "Clear",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

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

// Keep the original header (updated with theme)
@Composable
fun SmartMatchHeroHeader(
    teal: Color,
    gold: Color,
    height: Dp,
    collapseFraction: Float,
    colorScheme: androidx.compose.material3.ColorScheme,
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
                Image(
                    painter = painterResource(id = R.drawable.smart),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
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
                                HeaderAvatarSmartMatch(colorScheme)
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .background(colorScheme.tertiary, CircleShape)
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
                                    "Your AI-Picks",
                                    color = Color.White.copy(0.85f),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }

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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = colorScheme.tertiary,
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
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 20.dp, bottom = 32.dp)
                        .statusBarsPadding()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(bottom = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = colorScheme.tertiary,
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
fun HeaderAvatarSmartMatch(colorScheme: androidx.compose.material3.ColorScheme) {
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

// FlowRow utility for wrapping chips
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
        val placeables = measurables.map { it.measure(constraints) }

        var xPosition = 0
        var yPosition = 0
        var rowMaxHeight = 0

        layout(constraints.maxWidth, constraints.maxHeight) {
            placeables.forEach { placeable ->
                if (xPosition + placeable.width > constraints.maxWidth) {
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