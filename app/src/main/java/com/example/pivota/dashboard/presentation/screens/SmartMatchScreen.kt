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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.pivota.R
import com.example.pivota.dashboard.domain.EmployerType
import com.example.pivota.dashboard.presentation.composables.ModernJobCard
import com.example.pivota.dashboard.presentation.composables.ModernHousingCard
import com.example.pivota.dashboard.presentation.composables.ModernProfessionalCard
import kotlinx.coroutines.delay

// Updated data classes for different content types
sealed class SmartMatchItem {
    data class Provider(
        val id: String,
        val name: String,
        val businessName: String?,
        val profileImageUrl: String?,
        val profileImageRes: Int? = null, // Added for drawable resources
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
        val description: String, // Added description field
        val postedTime: String,
        val matchScore: Int,
        val matchReason: String,
        val isRemote: Boolean,
        val imageRes: Int? = null // Added for job images
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
    val viewAllAction: (() -> Unit)? = null,
)

@SuppressLint("FrequentlyChangingValue")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmartMatchScreen() {
    val colorScheme = MaterialTheme.colorScheme

    // 🎨 Brand Palette - Using theme colors
    val primaryColor = colorScheme.primary          // African Sapphire
    val secondaryColor = colorScheme.secondary      // Warm Terracotta
    val tertiaryColor = colorScheme.tertiary        // Baobab Gold (Golden Yellow)
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

    // Categorized listings - REMOVED "Top Picks" section
    val allTraySections = remember {
        listOf(
            // Jobs Section
            TraySection(
                title = "Jobs for You",
                subtitle = "Matched to your skills",
                icon = Icons.Outlined.Work,
                items = List(5) { index ->
                    SmartMatchItem.Job(
                        id = "job${index + 1}",
                        title = when (index) {
                            0 -> "Community Health Worker"
                            1 -> "Project Manager - NGO"
                            2 -> "Senior Social Worker"
                            3 -> "Customer Support Lead"
                            else -> "Graphic Designer"
                        },
                        company = listOf("Amref", "Save the Children", "Nairobi Children's Foundation", "Safaricom", "UNICEF")[index],
                        location = listOf("Nairobi", "Mombasa", "Nairobi", "Kisumu", "Remote")[index],
                        salary = listOf("KES 45K", "KES 120K", "KES 65K - 85K", "KES 55K", "KES 60K")[index],
                        jobType = listOf("Full-time", "Contract", "Full-time", "Full-time", "Remote")[index],
                        description = listOf(
                            "Community health worker needed for outreach programs in informal settlements. Must speak Swahili and English.",
                            "Experienced project manager needed for humanitarian programs. 5+ years experience required.",
                            "Senior social worker to handle child protection cases. Must have counseling certification.",
                            "Lead customer support team for mobile money services. Tech-savvy with leadership skills.",
                            "Creative graphic designer for UNICEF Kenya communications team. Portfolio required."
                        )[index],
                        postedTime = listOf("1d ago", "3h ago", "2h ago", "Just now", "5h ago")[index],
                        matchScore = listOf(92, 88, 98, 85, 79)[index],
                        matchReason = listOf(
                            "Healthcare background match",
                            "Leadership experience",
                            "Social work experience",
                            "Customer service skills",
                            "Portfolio review"
                        )[index],
                        isRemote = index == 4,
                        imageRes = when(index) {
                            0 -> R.drawable.job_placeholder2
                            1 -> R.drawable.job_placeholder5
                            2 -> R.drawable.job_placeholder5
                            3 -> R.drawable.job_placeholder3
                            else -> R.drawable.job_placeholder1
                        }
                    )
                },
                viewAllAction = {}
            ),

            // Housing Section
            TraySection(
                title = "Housing Matches",
                subtitle = "Based on your preferences",
                icon = Icons.Outlined.Home,
                items = List(4) { index ->
                    SmartMatchItem.Housing(
                        id = "house${index + 1}",
                        title = listOf(
                            "Modern 2BR Apartment",
                            "Studio Apartment",
                            "Luxury 3BR Villa",
                            "1BR Bedsitter"
                        )[index],
                        propertyType = listOf("Apartment", "Studio", "House", "Bedsitter")[index],
                        location = listOf("Westlands", "Kilimani", "Karen", "Ruiru")[index],
                        price = listOf(45000, 35000, 120000, 22000)[index],
                        bedrooms = listOf(2, 0, 3, 1)[index],
                        bathrooms = listOf(2, 1, 3, 1)[index],
                        squareMeters = listOf(85, 40, 200, 50)[index],
                        imageRes = when(index) {
                            0 -> R.drawable.property_placeholder1
                            1 -> R.drawable.property_placeholder2
                            2 -> R.drawable.property_placeholder3
                            else -> R.drawable.property_placeholder4
                        },
                        matchScore = listOf(95, 93, 90, 88)[index],
                        matchReason = listOf(
                            "In your preferred area",
                            "Great location",
                            "Perfect for families",
                            "Affordable option"
                        )[index],
                        isFurnished = index == 0
                    )
                },
                viewAllAction = {}
            ),

            // Providers Section
            TraySection(
                title = "Recommended Providers",
                subtitle = "Trusted partners near you",
                icon = Icons.Outlined.Build,
                items = List(4) { index ->
                    SmartMatchItem.Provider(
                        id = "provider${index + 1}",
                        name = listOf("Musa Jallow", "Sarah Wanjiku", "James Omondi", "Pipemasters Ltd")[index],
                        businessName = listOf("Musa Electrical Services", "Sarah Interior Designs", "Pipemasters Plumbing", null)[index],
                        profileImageUrl = null,
                        profileImageRes = when(index) {
                            0 -> R.drawable.job_placeholder1
                            1 -> R.drawable.job_placeholder3
                            2 -> R.drawable.job_placeholder2
                            else -> R.drawable.job_placeholder5
                        },
                        rating = listOf(4.8, 4.9, 4.7, 4.6)[index],
                        reviewCount = listOf(32, 47, 28, 89)[index],
                        description = listOf(
                            "Expert electrical installations and repairs. Available 24/7 for emergencies.",
                            "Award-winning interior design for homes and offices. Free consultation.",
                            "Professional plumbing services - leaks, installations, and maintenance.",
                            "Full-service property management for landlords and tenants."
                        )[index],
                        experienceYears = listOf(8, 6, 5, 10)[index],
                        location = listOf("Westlands", "Kilimani", "Kilimani", "Mombasa")[index],
                        startingPrice = listOf(1500, 2500, 1200, 5000)[index],
                        isVerified = listOf(true, true, true, true)[index],
                        responseTime = listOf("10 mins", "30 mins", "15 mins", "1 hour")[index],
                        category = listOf("Electrician", "Designer", "Plumber", "Management")[index],
                        matchReason = listOf(
                            "Top-rated in your area",
                            "Highly recommended",
                            "Emergency services available",
                            "Trusted partner"
                        )[index],
                        matchScore = listOf(94, 92, 90, 88)[index]
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
                    selectedFilterChips.contains("Jobs") && section.title.contains("Jobs") -> true
                    selectedFilterChips.contains("Housing") && section.title.contains("Housing") -> true
                    selectedFilterChips.contains("Providers") && section.title.contains("Providers") -> true
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
                                    item.jobType.lowercase().contains(debouncedQuery.value) ||
                                    item.description.lowercase().contains(debouncedQuery.value)
                        is SmartMatchItem.Housing ->
                            item.title.lowercase().contains(debouncedQuery.value) ||
                                    item.location.lowercase().contains(debouncedQuery.value) ||
                                    item.propertyType.lowercase().contains(debouncedQuery.value)
                        is SmartMatchItem.Provider ->
                            item.name.lowercase().contains(debouncedQuery.value) ||
                                    item.businessName?.lowercase()?.contains(debouncedQuery.value) == true ||
                                    item.category.lowercase().contains(debouncedQuery.value) ||
                                    item.location.lowercase().contains(debouncedQuery.value) ||
                                    item.description.lowercase().contains(debouncedQuery.value)
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
                primaryColor = primaryColor,
                tertiaryColor = tertiaryColor,
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
                // SmartMatch Highlight - Using tertiary (golden yellow)
                item {
                    SmartMatchHighlight(
                        tertiaryColor = tertiaryColor,
                        primaryColor = primaryColor,
                        colorScheme = colorScheme
                    )
                }

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
   SMART MATCH HIGHLIGHT - Using tertiary (golden yellow)
   ───────────────────────────────────────────── */

@Composable
fun SmartMatchHighlight(
    tertiaryColor: Color,
    primaryColor: Color,
    colorScheme: ColorScheme
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = tertiaryColor.copy(0.08f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Golden yellow icon
            Icon(
                imageVector = Icons.Outlined.AutoAwesome,
                contentDescription = null,
                tint = tertiaryColor,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "SmartMatch™ Recommendations",
                    color = tertiaryColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = "Personalized for you based on your activity",
                    fontSize = 12.sp,
                    color = colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            TextButton(
                onClick = { /* View all recommendations */ },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = primaryColor
                )
            ) {
                Text(
                    text = "View All",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

/* ─────────────────────────────────────────────
   STICKY SEARCH BAR
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
    colorScheme: ColorScheme,
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
                                    text = "Search listings...",
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
    colorScheme: ColorScheme,
    onItemClick: (SmartMatchItem) -> Unit,
    onViewAllClick: (() -> Unit)?
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Section Header - Using tertiary (golden yellow) for icons
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
                        tint = tertiaryColor, // Golden yellow icons
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
                        ModernProfessionalCard(
                            name = item.name,
                            specialty = item.category,
                            rating = item.rating.toFloat(),
                            jobs = item.reviewCount,
                            isVerified = item.isVerified,
                            description = item.description,
                            profileImageRes = item.profileImageRes, // Pass the image resource
                            onCardClick = { onItemClick(item) },
                            onViewClick = { onItemClick(item) },
                            onBookClick = { /* Book provider */ },
                        )
                    }
                    is SmartMatchItem.Job -> {
                        ModernJobCard(
                            title = item.title,
                            company = item.company,
                            location = item.location,
                            salary = item.salary,
                            type = item.jobType,
                            description = item.description, // Added description
                            isVerified = true,
                            employerType = EmployerType.ORGANIZATION,
                            profileImageRes = item.imageRes, // Added image if available
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
                            description = item.matchReason,
                            bedrooms = item.bedrooms,
                            bathrooms = item.bathrooms,
                            squareMeters = item.squareMeters,
                            onViewClick = { onItemClick(item) },
                            onBookClick = { /* Handle book */ },
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
    colorScheme: ColorScheme
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
                                colors = listOf(tertiaryColor.copy(0.15f), Color.Transparent),
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
                                HeaderAvatarSmartMatch(
                                    colorScheme = colorScheme,
                                    tertiaryColor = tertiaryColor
                                )
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .background(tertiaryColor, CircleShape)
                                        .border(2.dp, primaryColor, CircleShape)
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
                            tint = tertiaryColor,
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
                            tint = tertiaryColor,
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
fun HeaderAvatarSmartMatch(
    colorScheme: ColorScheme,
    tertiaryColor: Color
) {
    Box(
        modifier = Modifier
            .size(45.dp)
    ) {
        // Avatar background
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

        // Online indicator - Golden yellow
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(tertiaryColor, CircleShape)
                .border(2.dp, colorScheme.primary, CircleShape)
                .align(Alignment.BottomEnd)
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