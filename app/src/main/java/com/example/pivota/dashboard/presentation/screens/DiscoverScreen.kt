package com.example.pivota.dashboard.presentation.screens

import android.annotation.SuppressLint
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.pivota.R
import kotlin.math.max

// Modern, elegant color palette - teal used sparingly as accent
val DeepNavy = Color(0xFF0A1A2F)      // Professional dark blue
val WarmGray = Color(0xFFF8F9FA)      // Soft background
val SlateGray = Color(0xFF4A5568)     // Secondary text
val SoftGold = Color(0xFFD4AF37)      // Premium accent
val ForestGreen = Color(0xFF2E7D32)   // Success/Verified
val CleanWhite = Color(0xFFFFFFFF)    // Pure white
val LightBorder = Color(0xFFE2E8F0)   // Subtle borders
val MutedTeal = Color(0xFF2C6E6E)     // Muted teal for accents

@SuppressLint("FrequentlyChangingValue")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverScreen() {
    // 🎨 Brand Palette - Using teal sparingly as accent
    val primaryTeal = MutedTeal
    val goldenAccent = SoftGold
    val softBackground = WarmGray

    val listState = rememberLazyListState()

    // State for search
    var searchQuery by remember { mutableStateOf("") }

    // State for audio recording
    var isRecording by remember { mutableStateOf(false) }

    // 📏 Header sizes
    val maxHeight = 220.dp
    val minHeight = 90.dp

    val density = LocalDensity.current
    val collapseRangePx = with(density) {
        (maxHeight - minHeight).toPx()
    }

    // 🔥 Correct collapse logic
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

    Scaffold(
        containerColor = softBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
        ) {
            // 📜 SCROLL CONTENT
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize()
            ) {
                // Spacer for the fixed header and search bar
                item {
                    Spacer(
                        modifier = Modifier.height(
                            maxHeight + 120.dp // Height for header + search section
                        )
                    )
                }

                // 📋 1. Jobs Near You - Enhanced
                item {
                    ModernSectionHeader("Jobs Near You", "View all jobs →")
                }
                item {
                    ModernHorizontalList(
                        items = listOf(
                            {
                                ModernJobCard(
                                    title = "Construction Foreman",
                                    company = "BuildWell Ltd",
                                    location = "Upper Hill",
                                    salary = "KSh 3,500/day",
                                    type = "Contract",
                                    isVerified = true,
                                    accentColor = primaryTeal
                                )
                            },
                            {
                                ModernJobCard(
                                    title = "Junior Accountant",
                                    company = "FinCorp",
                                    location = "Westlands",
                                    salary = "KSh 55,000/month",
                                    type = "Full-time",
                                    isVerified = true,
                                    accentColor = primaryTeal
                                )
                            },
                            {
                                ModernJobCard(
                                    title = "Store Keeper",
                                    company = "Retail Solutions",
                                    location = "Mombasa Rd",
                                    salary = "KSh 25,000/month",
                                    type = "Full-time",
                                    isVerified = false,
                                    accentColor = primaryTeal
                                )
                            },
                            {
                                ModernJobCard(
                                    title = "Solar Installer",
                                    company = "Green Energy",
                                    location = "Karen",
                                    salary = "KSh 40,000/month",
                                    type = "Contract",
                                    isVerified = true,
                                    accentColor = primaryTeal
                                )
                            }
                        )
                    )
                }

                // 📋 2. Housing - Enhanced
                item {
                    ModernSectionHeader("Housing Opportunities", "Browse all →")
                }
                item {
                    ModernHorizontalList(
                        items = listOf(
                            {
                                ModernHousingCard(
                                    price = "KSh 22,000",
                                    title = "Modern Bedsitter",
                                    location = "Ruiru",
                                    type = "Apartment",
                                    rating = 4.5,
                                    isVerified = true,
                                    isForSale = false,
                                    imageRes = R.drawable.nairobi_city,
                                    accentColor = primaryTeal
                                )
                            },
                            {
                                ModernHousingCard(
                                    price = "KSh 45,000",
                                    title = "2 Bedroom Apartment",
                                    location = "Syokimau",
                                    type = "Apartment",
                                    rating = 4.8,
                                    isVerified = true,
                                    isForSale = false,
                                    imageRes = R.drawable.nairobi_city,
                                    accentColor = primaryTeal
                                )
                            },
                            {
                                ModernHousingCard(
                                    price = "KSh 4.5M",
                                    title = "Luxury Villa",
                                    location = "Karen",
                                    type = "House",
                                    rating = 4.9,
                                    isVerified = true,
                                    isForSale = true,
                                    imageRes = R.drawable.nairobi_city,
                                    accentColor = primaryTeal
                                )
                            },
                            {
                                ModernHousingCard(
                                    price = "KSh 35,000",
                                    title = "Studio Apartment",
                                    location = "Kilimani",
                                    type = "Studio",
                                    rating = 4.3,
                                    isVerified = true,
                                    isForSale = false,
                                    imageRes = R.drawable.nairobi_city,
                                    accentColor = primaryTeal
                                )
                            }
                        )
                    )
                }

                // 🛠️ 3. Trusted Service Providers
                item {
                    ModernSectionHeader("Trusted Service Providers", "See all →")
                }
                item {
                    ModernHorizontalList(
                        items = listOf(
                            {
                                ModernProviderCard(
                                    name = "QuickMovers Kenya",
                                    specialty = "Moving & Logistics",
                                    rating = 4.9f,
                                    jobs = 342,
                                    isVerified = true,
                                    accentColor = primaryTeal
                                )
                            },
                            {
                                ModernProviderCard(
                                    name = "Fundi Digital",
                                    specialty = "Electrical & Plumbing",
                                    rating = 4.7f,
                                    jobs = 256,
                                    isVerified = true,
                                    accentColor = primaryTeal
                                )
                            },
                            {
                                ModernProviderCard(
                                    name = "CleanPro Services",
                                    specialty = "Cleaning & Maintenance",
                                    rating = 4.8f,
                                    jobs = 189,
                                    isVerified = true,
                                    accentColor = primaryTeal
                                )
                            },
                            {
                                ModernProviderCard(
                                    name = "SolarTech",
                                    specialty = "Solar Installation",
                                    rating = 4.6f,
                                    jobs = 112,
                                    isVerified = false,
                                    accentColor = primaryTeal
                                )
                            }
                        )
                    )
                }

                // ⚡ 4. Quick Services Grid - Enhanced
                item {
                    ModernSectionHeader("Common Services", "Browse categories →")
                }
                item {
                    ModernServiceGrid(primaryTeal, goldenAccent)
                }

                // 🤝 5. Social Support - Enhanced
                item {
                    ModernSectionHeader("Social Support & Services", "Get help →")
                }
                items(3) { index ->
                    val supports = listOf(
                        Triple("Red Cross Kenya", "Emergency Relief & Disaster Response", "Nationwide"),
                        Triple("Legal Aid Kenya", "Free Legal Advice & Representation", "Regional Offices"),
                        Triple("Food for All", "Community Food Programs", "Nairobi & Kiambu")
                    )
                    ModernSupportCard(
                        name = supports[index].first,
                        service = supports[index].second,
                        location = supports[index].third,
                        isUrgent = index == 0,
                        accentColor = primaryTeal
                    )
                }

                item { Spacer(modifier = Modifier.height(100.dp)) }
            }

            // 🏆 FIXED HEADER (always on top)
            DiscoverHeroHeader(
                teal = primaryTeal,
                gold = goldenAccent,
                height = animatedHeight,
                collapseFraction = collapseFraction,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .zIndex(2f) // Higher z-index to stay on top
            )

            // 📌 FIXED SEARCH SECTION (always visible below header)
            FixedSearchSection(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                onAudioClick = {
                    isRecording = !isRecording
                    // Handle audio recording start/stop
                    if (isRecording) {
                        // Start recording
                    } else {
                        // Stop recording and process audio
                    }
                },
                isRecording = isRecording,
                accentColor = primaryTeal,
                headerHeight = animatedHeight,
                showShadow = isPastThreshold.value,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .zIndex(1f) // Below header but above content
            )
        }
    }
}

/* ─────────────────────────────────────────────
   FIXED SEARCH SECTION (always visible)
   ───────────────────────────────────────────── */

@Composable
fun FixedSearchSection(
    query: String,
    onQueryChange: (String) -> Unit,
    onAudioClick: () -> Unit,
    isRecording: Boolean,
    accentColor: Color,
    headerHeight: Dp,
    showShadow: Boolean,
    modifier: Modifier = Modifier
) {
    // Animate shadow based on scroll position
    val elevation by animateDpAsState(
        targetValue = if (showShadow) 8.dp else 0.dp,
        animationSpec = tween(durationMillis = 200)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = headerHeight) // Position right below header
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = elevation,
                    shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
                    ambientColor = Color.Black.copy(0.08f),
                    spotColor = Color.Black.copy(0.08f)
                ),
            shape = RoundedCornerShape(
                topStart = 0.dp,
                topEnd = 0.dp,
                bottomStart = 20.dp,
                bottomEnd = 20.dp
            ),
            color = CleanWhite,
            tonalElevation = if (showShadow) 4.dp else 0.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // Search Bar with Audio Icon
                SearchBarWithAudio(
                    query = query,
                    onQueryChange = onQueryChange,
                    onAudioClick = onAudioClick,
                    isRecording = isRecording,
                    accentColor = accentColor
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Filter Pills
                FilterPillsRow(
                    selectedFilters = emptySet(),
                    onFilterSelected = { /* Handle pill selection */ },
                    accentColor = accentColor
                )
            }
        }
    }
}

/* ─────────────────────────────────────────────
   SEARCH BAR WITH AUDIO ICON
   ───────────────────────────────────────────── */

@Composable
fun SearchBarWithAudio(
    query: String,
    onQueryChange: (String) -> Unit,
    onAudioClick: () -> Unit,
    isRecording: Boolean,
    accentColor: Color
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Black.copy(0.05f),
                spotColor = Color.Black.copy(0.05f)
            ),
        shape = RoundedCornerShape(16.dp),
        color = CleanWhite,
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
                tint = SlateGray.copy(0.6f),
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
                                "Search jobs, houses, services...",
                                color = SlateGray.copy(0.5f),
                                fontSize = 14.sp
                            )
                        }
                        innerTextField()
                    }
                },
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 14.sp,
                    color = DeepNavy
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
                        tint = SlateGray.copy(0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            } else {
                // Audio Icon with recording animation
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
                        tint = if (isRecording) accentColor else SlateGray.copy(0.6f),
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Recording indicator (pulsing dot)
                if (isRecording) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                color = Color.Red,
                                shape = CircleShape
                            )
                    )
                }
            }
        }
    }
}

/* ─────────────────────────────────────────────
   FILTER PILLS ROW
   ───────────────────────────────────────────── */

@Composable
fun FilterPillsRow(
    selectedFilters: Set<String>,
    onFilterSelected: (String) -> Unit,
    accentColor: Color
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        val filters = listOf(
            "All" to null,
            "Jobs" to Icons.Outlined.Work,
            "Houses" to Icons.Outlined.Home,
            "Service providers" to Icons.Outlined.Build,
            "Support" to Icons.Outlined.VolunteerActivism,
            "Verified" to Icons.Outlined.Verified
        )

        items(filters.size) { index ->
            val (filter, icon) = filters[index]
            val isSelected = selectedFilters.contains(filter) || (filter == "All" && selectedFilters.isEmpty())

            Surface(
                shape = RoundedCornerShape(30.dp),
                color = if (isSelected) accentColor else CleanWhite,
                border = if (!isSelected) BorderStroke(1.dp, LightBorder) else null,
                modifier = Modifier
                    .clickable { onFilterSelected(filter) }
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
                            tint = if (isSelected) CleanWhite else SlateGray,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    Text(
                        text = filter,
                        color = if (isSelected) CleanWhite else SlateGray,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

/* ────────────── EXISTING COMPONENTS (unchanged) ────────────── */

@Composable
fun ModernSectionHeader(title: String, actionText: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = DeepNavy,
                fontSize = 16.sp,
                letterSpacing = 0.5.sp
            )
        )
        Text(
            text = actionText,
            color = MutedTeal,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun ModernHorizontalList(items: List<@Composable () -> Unit>) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items.size) { index ->
            items[index]()
        }
    }
}

@Composable
fun ModernJobCard(
    title: String,
    company: String,
    location: String,
    salary: String,
    type: String,
    isVerified: Boolean,
    accentColor: Color
) {
    Card(
        modifier = Modifier
            .width(260.dp)
            .clickable { /* Navigate to job details */ },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CleanWhite),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp,
            pressedElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header with company and verification
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Company initial avatar
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(accentColor.copy(0.08f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = company.take(1),
                        color = accentColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = DeepNavy,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = company,
                        fontSize = 12.sp,
                        color = SlateGray,
                        maxLines = 1
                    )
                }

                if (isVerified) {
                    Icon(
                        Icons.Outlined.Verified,
                        contentDescription = "Verified",
                        tint = ForestGreen,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Location
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Outlined.LocationOn,
                    contentDescription = null,
                    tint = SlateGray.copy(0.5f),
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = location,
                    fontSize = 12.sp,
                    color = SlateGray,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Salary and type
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = salary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = DeepNavy
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = accentColor.copy(0.08f)
                ) {
                    Text(
                        text = type,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontSize = 10.sp,
                        color = accentColor,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Apply button - Minimal
            OutlinedButton(
                onClick = { /* Apply */ },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, accentColor.copy(0.3f)),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = accentColor
                )
            ) {
                Text(
                    "Quick Apply",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun ModernHousingCard(
    price: String,
    title: String,
    location: String,
    type: String,
    rating: Double,
    isVerified: Boolean,
    isForSale: Boolean = false,
    imageRes: Int,
    accentColor: Color
) {
    Card(
        modifier = Modifier
            .width(300.dp)
            .clickable { /* Navigate to details */ },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CleanWhite),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 4.dp
        )
    ) {
        Column {
            // Image with overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(0.4f)
                                )
                            )
                        )
                )

                // Status Badge (For Sale / For Rent)
                Surface(
                    color = if (isForSale) SoftGold else accentColor,
                    shape = RoundedCornerShape(topEnd = 0.dp, bottomEnd = 12.dp),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                ) {
                    Text(
                        text = if (isForSale) "FOR SALE" else "FOR RENT",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isForSale) DeepNavy else CleanWhite
                    )
                }

                // Verification badge
                if (isVerified) {
                    Surface(
                        color = CleanWhite,
                        shape = CircleShape,
                        modifier = Modifier
                            .padding(12.dp)
                            .align(Alignment.TopEnd)
                            .size(32.dp)
                    ) {
                        Icon(
                            Icons.Outlined.Verified,
                            contentDescription = "Verified",
                            tint = ForestGreen,
                            modifier = Modifier.padding(6.dp)
                        )
                    }
                }

                // Type badge (Apartment, House, etc.)
                Surface(
                    color = CleanWhite.copy(0.95f),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.BottomStart)
                ) {
                    Text(
                        text = type,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        fontSize = 11.sp,
                        color = DeepNavy,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Content
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = price,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = accentColor
                )

                Text(
                    text = title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = DeepNavy,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 6.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 6.dp)
                ) {
                    Icon(
                        Icons.Outlined.LocationOn,
                        contentDescription = null,
                        tint = SlateGray.copy(0.5f),
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = location,
                        fontSize = 13.sp,
                        color = SlateGray,
                        modifier = Modifier.padding(start = 4.dp)
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Outlined.Star,
                            contentDescription = null,
                            tint = SoftGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = String.format("%.1f", rating),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = SlateGray,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // View House Button
                Button(
                    onClick = { /* View house details */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = accentColor,
                        contentColor = CleanWhite
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 2.dp
                    )
                ) {
                    Icon(
                        Icons.Outlined.Visibility,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "View House",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun ModernProviderCard(
    name: String,
    specialty: String,
    rating: Float,
    jobs: Int,
    isVerified: Boolean,
    accentColor: Color
) {
    Card(
        modifier = Modifier
            .width(240.dp)
            .clickable { /* View provider */ },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CleanWhite),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp,
            pressedElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header with avatar
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(accentColor.copy(0.08f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = name.take(1),
                        color = accentColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = name,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = DeepNavy,
                            maxLines = 1
                        )
                        if (isVerified) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                Icons.Outlined.Verified,
                                contentDescription = null,
                                tint = ForestGreen,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                    Text(
                        text = specialty,
                        fontSize = 12.sp,
                        color = SlateGray,
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Rating
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Outlined.Star,
                            contentDescription = null,
                            tint = SoftGold,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = rating.toString(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = DeepNavy,
                            modifier = Modifier.padding(start = 2.dp)
                        )
                    }
                    Text(
                        text = "Rating",
                        fontSize = 10.sp,
                        color = SlateGray
                    )
                }

                // Jobs completed
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "$jobs+",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = DeepNavy
                    )
                    Text(
                        text = "Jobs",
                        fontSize = 10.sp,
                        color = SlateGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Contact button
            Button(
                onClick = { /* Contact */ },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = accentColor.copy(0.08f),
                    contentColor = accentColor
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 0.dp
                )
            ) {
                Text(
                    "Contact",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun ModernSupportCard(
    name: String,
    service: String,
    location: String,
    isUrgent: Boolean,
    accentColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { /* View details */ },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CleanWhite),
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
            // Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        if (isUrgent) SoftGold.copy(0.1f) else accentColor.copy(0.08f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isUrgent) Icons.Outlined.Emergency else Icons.Outlined.VolunteerActivism,
                    contentDescription = null,
                    tint = if (isUrgent) SoftGold else accentColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = name,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = DeepNavy
                )
                Text(
                    text = service,
                    fontSize = 13.sp,
                    color = SlateGray,
                    modifier = Modifier.padding(top = 2.dp)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(
                        Icons.Outlined.LocationOn,
                        contentDescription = null,
                        tint = SlateGray.copy(0.5f),
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = location,
                        fontSize = 11.sp,
                        color = SlateGray.copy(0.7f),
                        modifier = Modifier.padding(start = 2.dp)
                    )
                }
            }

            // Action
            Icon(
                Icons.Outlined.ChevronRight,
                contentDescription = "View",
                tint = SlateGray.copy(0.5f)
            )
        }
    }
}

@Composable
fun ModernServiceGrid(teal: Color, gold: Color) {
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

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Split into rows of 4
        services.chunked(4).forEach { rowItems ->
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
                        color = CleanWhite,
                        border = BorderStroke(1.dp, LightBorder),
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
                                tint = SlateGray,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = name,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = DeepNavy
                            )
                        }
                    }
                }
            }
        }
    }
}

// Keep existing header and helper composables
@Composable
fun DiscoverHeroHeader(
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
    val discoverFontSize = ((maxFontSize.value - collapseFraction * (maxFontSize.value - minFontSize.value))).sp

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
                    painter = painterResource(
                        id = com.example.pivota.R.drawable.nairobi_city
                    ),
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

            // Golden Accent - only visible when not collapsed
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
                                HeaderAvatar(teal)
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
                                    "Welcome to Pivota",
                                    color = Color.White.copy(0.85f),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }

                        // 🔔 ICON ROW
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            HeaderActionIcon(
                                icon = Icons.Default.Mail,
                                iconTint = Color.White,
                                backgroundTint = Color.White.copy(alpha = 0.2f)
                            ) {}
                            HeaderActionIcon(
                                icon = Icons.Default.Notifications,
                                iconTint = Color.White,
                                backgroundTint = Color.White.copy(alpha = 0.2f)
                            ) {}
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
                    // Left side - Discover text only (no button)
                    Text(
                        text = "Discover",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-1.5).sp,
                            fontSize = discoverFontSize
                        )
                    )

                    // Right side - Icons
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        HeaderActionIcon(
                            icon = Icons.Default.Mail,
                            iconTint = Color.White,
                            backgroundTint = Color.White.copy(alpha = 0.2f)
                        ) {}
                        HeaderActionIcon(
                            icon = Icons.Default.Notifications,
                            iconTint = Color.White,
                            backgroundTint = Color.White.copy(alpha = 0.2f)
                        ) {}
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
                    // Discover text only (no button)
                    Text(
                        text = "Discover",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-1.5).sp,
                            fontSize = discoverFontSize
                        )
                    )

                    // 📝 Subtitle Text
                    Text(
                        text = "Life opportunities, tailored for you",
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
fun HeaderActionIcon(
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

@Composable
fun HeaderAvatar(teal: Color) {
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