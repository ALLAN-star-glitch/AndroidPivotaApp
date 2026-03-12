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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import androidx.compose.ui.platform.LocalContext
import com.example.pivota.R
import com.example.pivota.ui.theme.*

// ==================== DATA CLASSES ====================

enum class ListingType {
    PROPERTY,
    JOB,
    SERVICE_PROVIDER
}

enum class EntityType {
    INDIVIDUAL,
    ORGANIZATION
}

data class FavoriteListing(
    val id: String,
    val title: String,
    val subtitle: String,
    val price: String,
    val location: String,
    val rating: Double,
    val mainImageUrl: String,
    val type: ListingType,
    val entityType: EntityType,
    val entityName: String,
    val entityImageUrl: String
)

// ==================== MAIN PROFILE SCREEN ====================

@SuppressLint("FrequentlyChangingValue")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToFavorites: () -> Unit = {},
    onNavigateToListingDetail: (String, ListingType) -> Unit = { _, _ -> }
) {
    val colorScheme = MaterialTheme.colorScheme

    // 🎨 Brand Palette - Using theme colors
    val primaryTeal = colorScheme.primary
    val goldenAccent = colorScheme.tertiary
    val softBackground = colorScheme.background
    val deepNavy = colorScheme.onSurface

    // Mock Subscription Data
    val activePlanModules = listOf("houses", "jobs", "service-offerings")
    val isContractorEnabled = activePlanModules.contains("service-offerings")
    val isVerified = true
    val userName = "Allan Mathenge"
    val userEmail = "allan.mathenge@example.com"

    // Mock Favorites Data
    val favoriteListings = remember {
        listOf(
            FavoriteListing(
                id = "1",
                title = "Modern Apartment",
                subtitle = "3 beds • 2 baths",
                price = "$4,500/mo",
                location = "Nairobi",
                rating = 4.5,
                mainImageUrl = "",
                type = ListingType.PROPERTY,
                entityType = EntityType.ORGANIZATION,
                entityName = "Urban Living Ltd",
                entityImageUrl = ""
            ),
            FavoriteListing(
                id = "2",
                title = "Senior Software Engineer",
                subtitle = "Remote • Full-time",
                price = "$8,000/mo",
                location = "Remote",
                rating = 4.8,
                mainImageUrl = "",
                type = ListingType.JOB,
                entityType = EntityType.ORGANIZATION,
                entityName = "Tech Corp Inc",
                entityImageUrl = ""
            ),
            FavoriteListing(
                id = "3",
                title = "Professional Plumbing",
                subtitle = "5+ years experience",
                price = "$50/hr",
                location = "Nairobi",
                rating = 4.9,
                mainImageUrl = "",
                type = ListingType.SERVICE_PROVIDER,
                entityType = EntityType.INDIVIDUAL,
                entityName = "John Doe",
                entityImageUrl = ""
            )
        )
    }

    val listState = rememberLazyListState()

    // Header sizes
    val maxHeight = 260.dp
    val minHeight = 100.dp

    val density = LocalDensity.current
    val collapseRangePx = with(density) {
        (maxHeight - minHeight).toPx()
    }

    val scrollY = when (listState.firstVisibleItemIndex) {
        0 -> listState.firstVisibleItemScrollOffset.toFloat()
        else -> collapseRangePx
    }

    val collapseFraction =
        (scrollY / collapseRangePx).coerceIn(0f, 1f)

    val animatedHeight =
        lerp(maxHeight, minHeight, collapseFraction)

    // Dynamically filter titles based on Plan capabilities
    val titleOptions = remember(activePlanModules) {
        mutableListOf<String>().apply {
            if (activePlanModules.contains("houses")) add("Property Owner")
            if (activePlanModules.contains("jobs")) add("Recruiter / Employer")
            if (activePlanModules.contains("help-and-support")) add("NGO Partner")
            if (activePlanModules.contains("service-offerings")) add("Professional Contractor")
        }.ifEmpty { listOf("Individual Member") }
    }

    var expanded by remember { mutableStateOf(false) }
    var selectedTitle by remember { mutableStateOf(titleOptions.first()) }

    Scaffold(
        containerColor = softBackground,
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
                    top = maxHeight
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 🛠 1. PROFESSIONAL SERVICE CONSOLE
                if (isContractorEnabled) {
                    item {
                        ProfileSection(
                            title = "Professional Service Console",
                            colorScheme = colorScheme,
                            goldenAccent = goldenAccent
                        ) {
                            ProfileItem(
                                Icons.Default.Build,
                                "Manage Specialties",
                                "Electrical, Plumbing, Solar",
                                colorScheme = colorScheme,
                                goldenAccent = goldenAccent
                            )
                            ProfileItem(
                                Icons.Default.History,
                                "Work Experience",
                                "5+ years • 12 completed jobs",
                                colorScheme = colorScheme,
                                goldenAccent = goldenAccent
                            )
                            ProfileItem(
                                Icons.Default.Map,
                                "Service Coverage Areas",
                                "Nairobi, Kiambu, Machakos",
                                colorScheme = colorScheme,
                                goldenAccent = goldenAccent
                            )
                            ProfileItem(
                                Icons.Default.Star,
                                "SmartMatch™ Insights",
                                "87% match rate • Top 10%",
                                colorScheme = colorScheme,
                                goldenAccent = goldenAccent
                            )
                        }
                    }
                }



                // 👤 3. Account Management
                item {
                    ProfileSection(
                        title = "Account Management",
                        colorScheme = colorScheme,
                        goldenAccent = goldenAccent
                    ) {
                        ProfileItem(
                            Icons.Default.Person,
                            "Personal Information",
                            "$userName • $userEmail",
                            colorScheme = colorScheme,
                            goldenAccent = goldenAccent
                        )
                        ProfileItem(
                            Icons.Default.Badge,
                            "Professional CV / Documents",
                            "Last updated: 2 weeks ago",
                            colorScheme = colorScheme,
                            goldenAccent = goldenAccent
                        )
                        ProfileItem(
                            Icons.Default.VerifiedUser,
                            "Identity Verification Status",
                            "Verified • Level 2",
                            colorScheme = colorScheme,
                            goldenAccent = goldenAccent
                        )
                        ProfileItem(
                            Icons.Default.GroupAdd,
                            "Manage Organization Team",
                            "3 team members",
                            colorScheme = colorScheme,
                            goldenAccent = goldenAccent
                        )
                    }
                }

                // ⚙️ 4. Financials & Settings
                item {
                    ProfileSection(
                        title = "Preferences & Billing",
                        colorScheme = colorScheme,
                        goldenAccent = goldenAccent
                    ) {
                        ProfileItem(
                            Icons.Default.Payment,
                            "Subscription Plan",
                            "Pro Plan • 1,500 KES/month",
                            colorScheme = colorScheme,
                            goldenAccent = goldenAccent
                        )
                        ProfileItem(
                            Icons.Default.Notifications,
                            "Notification Settings",
                            "Email, Push, SMS",
                            colorScheme = colorScheme,
                            goldenAccent = goldenAccent
                        )
                        ProfileItem(
                            Icons.Default.Lock,
                            "Privacy & Security",
                            "2FA enabled • Last login: Today",
                            colorScheme = colorScheme,
                            goldenAccent = goldenAccent
                        )
                    }
                }

                // 💬 5. Support
                item {
                    ProfileSection(
                        title = "Support",
                        colorScheme = colorScheme,
                        goldenAccent = goldenAccent
                    ) {
                        ProfileItem(
                            Icons.Default.HelpOutline,
                            "Help Center",
                            "FAQs, Guides, Tutorials",
                            colorScheme = colorScheme,
                            goldenAccent = goldenAccent
                        )
                        ProfileItem(
                            Icons.Default.Info,
                            "About PivotaConnect",
                            "Version 1.0.0 • MVP1",
                            colorScheme = colorScheme,
                            goldenAccent = goldenAccent
                        )
                    }
                }

                // 🚪 Sign Out
                item {
                    TextButton(
                        onClick = { /* Sign Out Logic */ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 16.dp),
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = colorScheme.error
                        )
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Logout, null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Sign Out of Account", fontWeight = FontWeight.Bold)
                    }
                }

                item { Spacer(modifier = Modifier.height(40.dp)) }
            }

            // 🏆 UNIQUE COLLAPSING HEADER
            ProfileHeroHeader(
                teal = primaryTeal,
                gold = goldenAccent,
                navy = deepNavy,
                height = animatedHeight,
                collapseFraction = collapseFraction,
                userName = userName,
                userEmail = userEmail,
                isVerified = isVerified,
                titleOptions = titleOptions,
                selectedTitle = selectedTitle,
                expanded = expanded,
                onExpandedChange = { expanded = it },
                onTitleSelected = { selectedTitle = it },
                onEditProfileClick = { /* Navigate to edit profile */ },
                colorScheme = colorScheme,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}


@Composable
fun PropertyFavoriteCard(
    listing: FavoriteListing,
    goldenAccent: Color,
    colorScheme: androidx.compose.material3.ColorScheme,
    onClick: () -> Unit
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .width(160.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface)
    ) {
        Column {
            // Property Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(colorScheme.surfaceVariant)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(listing.mainImageUrl.ifEmpty { "https://example.com/property-placeholder.jpg" })
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    error = painterResource(id = R.drawable.ic_launcher_background),
                    placeholder = painterResource(id = R.drawable.ic_launcher_background)
                )

                // Type badge
                Surface(
                    color = colorScheme.primary,
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(6.dp)
                ) {
                    Text(
                        text = "HOUSE",
                        fontSize = 8.sp,
                        color = colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                // Rating badge
                Surface(
                    color = goldenAccent,
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = colorScheme.onPrimary,
                            modifier = Modifier.size(8.dp)
                        )
                        Text(
                            text = String.format("%.1f", listing.rating),
                            fontSize = 8.sp,
                            color = colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 2.dp)
                        )
                    }
                }
            }

            // Content
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = listing.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = colorScheme.onSurface
                )

                Text(
                    text = listing.subtitle,
                    fontSize = 9.sp,
                    color = colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp)
                )

                // Agent/Company info
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(colorScheme.surfaceVariant)
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(listing.entityImageUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                            error = painterResource(id = R.drawable.ic_launcher_background),
                            placeholder = painterResource(id = R.drawable.ic_launcher_background)
                        )
                    }

                    Text(
                        text = listing.entityName,
                        fontSize = 8.sp,
                        color = colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .weight(1f)
                    )
                }

                // Price and location
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = listing.price,
                        fontSize = 11.sp,
                        color = colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(8.dp)
                        )
                        Text(
                            text = listing.location.take(3),
                            fontSize = 8.sp,
                            color = colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(start = 1.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun JobFavoriteCard(
    listing: FavoriteListing,
    goldenAccent: Color,
    colorScheme: androidx.compose.material3.ColorScheme,
    onClick: () -> Unit
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .width(160.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface)
    ) {
        Column {
            // Header with entity image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                colorScheme.primary.copy(0.8f),
                                colorScheme.primary.copy(0.4f)
                            )
                        )
                    )
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(colorScheme.surface)
                        .border(2.dp, colorScheme.surface, CircleShape)
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(listing.entityImageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        error = painterResource(id = R.drawable.ic_launcher_background),
                        placeholder = painterResource(id = R.drawable.ic_launcher_background)
                    )
                }

                Surface(
                    color = colorScheme.primary,
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(6.dp)
                ) {
                    Text(
                        text = "JOB",
                        fontSize = 8.sp,
                        color = colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = listing.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = colorScheme.onSurface
                )

                Text(
                    text = listing.subtitle,
                    fontSize = 9.sp,
                    color = colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Icon(
                        imageVector = if (listing.entityType == EntityType.ORGANIZATION)
                            Icons.Default.Business else Icons.Default.Person,
                        contentDescription = null,
                        tint = colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(8.dp)
                    )
                    Text(
                        text = listing.entityName,
                        fontSize = 8.sp,
                        color = colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .padding(start = 2.dp)
                            .weight(1f)
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = goldenAccent,
                            modifier = Modifier.size(8.dp)
                        )
                        Text(
                            text = String.format("%.1f", listing.rating),
                            fontSize = 8.sp,
                            color = colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(start = 1.dp)
                        )
                    }

                    Text(
                        text = listing.price,
                        fontSize = 11.sp,
                        color = colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun ServiceProviderFavoriteCard(
    listing: FavoriteListing,
    goldenAccent: Color,
    colorScheme: androidx.compose.material3.ColorScheme,
    onClick: () -> Unit
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .width(160.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(if (listing.entityType == EntityType.INDIVIDUAL) CircleShape else RoundedCornerShape(8.dp))
                        .background(colorScheme.surfaceVariant)
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(listing.entityImageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        error = painterResource(id = R.drawable.ic_launcher_background),
                        placeholder = painterResource(id = R.drawable.ic_launcher_background)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        color = goldenAccent,
                        shape = RoundedCornerShape(4.dp),
                    ) {
                        Text(
                            text = "PROVIDER",
                            fontSize = 7.sp,
                            color = colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }

                    Text(
                        text = listing.entityName,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = colorScheme.onSurface,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            Text(
                text = listing.title,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Text(
                text = listing.subtitle,
                fontSize = 9.sp,
                color = colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .padding(top = 2.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = goldenAccent,
                        modifier = Modifier.size(10.dp)
                    )
                    Text(
                        text = String.format("%.1f", listing.rating),
                        fontSize = 9.sp,
                        color = colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(start = 2.dp)
                    )
                }

                Text(
                    text = listing.price,
                    fontSize = 11.sp,
                    color = goldenAccent,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, bottom = 8.dp)
            ) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.size(8.dp)
                )
                Text(
                    text = listing.location,
                    fontSize = 8.sp,
                    color = colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(start = 2.dp)
                        .weight(1f)
                )
            }
        }
    }
}

@Composable
fun ViewAllCard(
    count: Int,
    goldenAccent: Color,
    colorScheme: androidx.compose.material3.ColorScheme,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(100.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = goldenAccent.copy(alpha = 0.1f))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            Icon(
                Icons.Default.Favorite,
                contentDescription = null,
                tint = goldenAccent,
                modifier = Modifier.size(24.dp)
            )

            Text(
                text = "View All",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = goldenAccent,
                modifier = Modifier.padding(top = 4.dp)
            )

            Text(
                text = "$count saved",
                fontSize = 9.sp,
                color = colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Composable
fun EmptyFavoritesState(
    goldenAccent: Color,
    colorScheme: androidx.compose.material3.ColorScheme
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                Icons.Outlined.FavoriteBorder,
                contentDescription = null,
                tint = goldenAccent.copy(alpha = 0.5f),
                modifier = Modifier.size(40.dp)
            )
            Text(
                text = "No saved items yet",
                fontSize = 14.sp,
                color = colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Tap the ♡ on properties, jobs, or service providers you love",
                fontSize = 12.sp,
                color = colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}

// ==================== PROFILE SECTION COMPOSABLES ====================

@Composable
fun ProfileSection(
    title: String,
    action: @Composable (() -> Unit)? = null,
    colorScheme: androidx.compose.material3.ColorScheme,
    goldenAccent: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title.uppercase(),
                style = MaterialTheme.typography.labelLarge.copy(
                    color = colorScheme.primary.copy(0.7f),
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    fontSize = 12.sp
                ),
                modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
            )

            if (action != null) {
                action()
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column { content() }
        }
    }
}

@Composable
fun ProfileItem(
    icon: ImageVector,
    label: String,
    subtitle: String = "",
    colorScheme: androidx.compose.material3.ColorScheme,
    goldenAccent: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Navigate to detail screen */ }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(
                    Brush.linearGradient(
                        colors = listOf(colorScheme.primary.copy(0.1f), goldenAccent.copy(0.1f))
                    ),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = colorScheme.primary,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = label,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = colorScheme.onSurface
            )
            if (subtitle.isNotEmpty()) {
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }

        Icon(
            imageVector = Icons.Rounded.ChevronRight,
            contentDescription = null,
            tint = colorScheme.primary.copy(0.5f),
            modifier = Modifier.size(20.dp)
        )
    }

    if (subtitle.isNotEmpty()) {
        HorizontalDivider(
            modifier = Modifier.padding(start = 76.dp, end = 16.dp),
            thickness = 0.5.dp,
            color = colorScheme.outlineVariant
        )
    }
}

// ==================== PROFILE HEADER COMPOSABLES ====================

@Composable
fun ProfileHeroHeader(
    teal: Color,
    gold: Color,
    navy: Color,
    height: Dp,
    collapseFraction: Float,
    userName: String,
    userEmail: String,
    isVerified: Boolean,
    titleOptions: List<String>,
    selectedTitle: String,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onTitleSelected: (String) -> Unit,
    onEditProfileClick: () -> Unit,
    colorScheme: androidx.compose.material3.ColorScheme,
    modifier: Modifier = Modifier
) {
    val collapsed = collapseFraction > 0.7f

    val avatarSize by animateDpAsState(
        targetValue = if (collapsed) 48.dp else 90.dp,
        animationSpec = tween(300)
    )

    val nameFontSize by animateDpAsState(
        targetValue = if (collapsed) 16.dp else 20.dp,
        animationSpec = tween(300)
    )

    val titlesDisplay = remember(titleOptions) {
        titleOptions.joinToString(" | ")
    }

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
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                teal,
                                teal.copy(alpha = 0.85f),
                                teal.copy(alpha = 0.7f),
                                teal.copy(alpha = 0.55f)
                            ),
                            start = Offset(0f, 0f),
                            end = Offset(1000f, 1000f),
                            tileMode = TileMode.Clamp
                        )
                    )
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(colorScheme.surface.copy(0.1f), Color.Transparent),
                            radius = 800f
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
            ) {
                if (!collapsed) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(avatarSize)
                                .background(
                                    if (isVerified)
                                        Brush.linearGradient(listOf(gold, colorScheme.surface))
                                    else
                                        Brush.linearGradient(listOf(colorScheme.surface.copy(0.5f), colorScheme.surface.copy(0.3f))),
                                    CircleShape
                                )
                                .padding(3.dp)
                                .clip(CircleShape)
                                .background(colorScheme.surface),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Person,
                                null,
                                tint = navy,
                                modifier = Modifier.size(avatarSize * 0.5f)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = userName,
                            color = colorScheme.onPrimary,
                            fontSize = nameFontSize.value.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = userEmail,
                            color = colorScheme.onPrimary.copy(0.8f),
                            fontSize = 13.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = titlesDisplay,
                            color = colorScheme.onPrimary.copy(0.9f),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .padding(bottom = 12.dp)
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 16.dp, end = 16.dp)
                    .clickable { onEditProfileClick() }
                    .clip(RoundedCornerShape(30.dp))
                    .background(colorScheme.surface.copy(alpha = 0.2f))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = null,
                    tint = colorScheme.onPrimary,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "Edit Profile",
                    color = colorScheme.onPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            if (collapsed) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopStart)
                        .padding(start = 20.dp, end = 20.dp, top = 16.dp)
                        .offset(y = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(avatarSize)
                                .background(
                                    if (isVerified) gold else colorScheme.surface.copy(0.3f),
                                    CircleShape
                                )
                                .padding(2.dp)
                                .clip(CircleShape)
                                .background(colorScheme.surface),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Person,
                                null,
                                tint = navy,
                                modifier = Modifier.size(avatarSize * 0.5f)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = userName,
                                color = colorScheme.onPrimary,
                                fontSize = nameFontSize.value.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )

                            Text(
                                text = titlesDisplay,
                                color = colorScheme.onPrimary.copy(0.8f),
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(48.dp))
                }
            }
        }
    }
}

@Composable
fun ProfileHeaderIcon(
    icon: ImageVector,
    iconTint: Color = Color.White,
    backgroundTint: Color = Color.White.copy(alpha = 0.2f),
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.size(44.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(backgroundTint, CircleShape)
                .clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}