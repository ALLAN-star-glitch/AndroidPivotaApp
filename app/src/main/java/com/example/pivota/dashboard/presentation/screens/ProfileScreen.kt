package com.example.pivota.dashboard.presentation.screens

import android.annotation.SuppressLint
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
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
import com.example.pivota.R

/**
 * COMPLETE PROFILE SCREEN
 * Aligned with PivotaConnect MVP1: Capability-Based Identity
 * Elegant design with unique collapsible header
 */
@SuppressLint("FrequentlyChangingValue")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen() {
    val primaryTeal = Color(0xFF006565)
    val goldenAccent = Color(0xFFE9C16C)
    val softBackground = Color(0xFFF6FAF9)
    val deepNavy = Color(0xFF13294B) // New elegant color for profile

    // Mock Subscription Data: In production, these come from your Auth/Subscription State
    val activePlanModules = listOf("houses", "jobs", "service-offerings")
    val isContractorEnabled = activePlanModules.contains("service-offerings")
    val isVerified = true
    val userName = "Allan Mathenge"
    val userEmail = "allan.mathenge@example.com"

    val listState = rememberLazyListState()

    // 📏 Header sizes - Unique to Profile (slightly taller for elegance)
    val maxHeight = 260.dp
    val minHeight = 100.dp

    val density = LocalDensity.current
    val collapseRangePx = with(density) {
        (maxHeight - minHeight).toPx()
    }

    // 🔥 Correct collapse logic (works even after index > 0)
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
                        ProfileSection(title = "Professional Service Console") {
                            ProfileItem(Icons.Default.Build, "Manage Specialties", "Electrical, Plumbing, Solar")
                            ProfileItem(Icons.Default.History, "Work Experience", "5+ years • 12 completed jobs")
                            ProfileItem(Icons.Default.Map, "Service Coverage Areas", "Nairobi, Kiambu, Machakos")
                            ProfileItem(Icons.Default.Star, "SmartMatch™ Insights", "87% match rate • Top 10%")
                        }
                    }
                }

                // 👤 2. Account Management
                item {
                    ProfileSection(title = "Account Management") {
                        ProfileItem(Icons.Default.Person, "Personal Information", "Allan Mathenge • allan@email.com")
                        ProfileItem(Icons.Default.Badge, "Professional CV / Documents", "Last updated: 2 weeks ago")
                        ProfileItem(Icons.Default.VerifiedUser, "Identity Verification Status", "Verified • Level 2")
                        ProfileItem(Icons.Default.GroupAdd, "Manage Organization Team", "3 team members")
                    }
                }

                // ⚙️ 3. Financials & Settings
                item {
                    ProfileSection(title = "Preferences & Billing") {
                        ProfileItem(Icons.Default.Payment, "Subscription Plan", "Pro Plan • 1,500 KES/month")
                        ProfileItem(Icons.Default.Notifications, "Notification Settings", "Email, Push, SMS")
                        ProfileItem(Icons.Default.Lock, "Privacy & Security", "2FA enabled • Last login: Today")
                    }
                }

                // 💬 4. Support
                item {
                    ProfileSection(title = "Support") {
                        ProfileItem(Icons.Default.HelpOutline, "Help Center", "FAQs, Guides, Tutorials")
                        ProfileItem(Icons.Default.Info, "About PivotaConnect", "Version 1.0.0 • MVP1")
                    }
                }

                // 🚪 Sign Out
                item {
                    TextButton(
                        onClick = { /* Sign Out Logic */ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 16.dp),
                        colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFD32F2F))
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Logout, null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Sign Out of Account", fontWeight = FontWeight.Bold)
                    }
                }

                item { Spacer(modifier = Modifier.height(40.dp)) }
            }

            // 🏆 UNIQUE COLLAPSING HEADER - Elegant profile design
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
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

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
    modifier: Modifier = Modifier
) {
    val collapsed = collapseFraction > 0.7f // Different threshold for profile

    // Animated properties
    val avatarSize by animateDpAsState(
        targetValue = if (collapsed) 48.dp else 90.dp,
        animationSpec = tween(300)
    )

    val nameFontSize by animateDpAsState(
        targetValue = if (collapsed) 16.dp else 20.dp,
        animationSpec = tween(300)
    )

    // Format multiple titles as a pipe-separated string
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
            // Teal-only gradient background for better contrast
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                teal,                          // Deep teal
                                teal.copy(alpha = 0.85f),      // Slightly lighter
                                teal.copy(alpha = 0.7f),       // Even lighter
                                teal.copy(alpha = 0.55f)       // Lightest teal
                            ),
                            start = Offset(0f, 0f),
                            end = Offset(1000f, 1000f),
                            tileMode = TileMode.Clamp
                        )
                    )
            )

            // Abstract pattern overlay for elegance
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color.White.copy(0.1f), Color.Transparent),
                            radius = 800f
                        )
                    )
            )

            // Main content container
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
            ) {
                if (!collapsed) {
                    // EXPANDED STATE - Full elegant profile
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Avatar with Verification Status Ring
                        Box(
                            modifier = Modifier
                                .size(avatarSize)
                                .background(
                                    if (isVerified)
                                        Brush.linearGradient(listOf(gold, Color.White))
                                    else
                                        Brush.linearGradient(listOf(Color.White.copy(0.5f), Color.White.copy(0.3f))),
                                    CircleShape
                                )
                                .padding(3.dp)
                                .clip(CircleShape)
                                .background(Color.White),
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
                            color = Color.White,
                            fontSize = nameFontSize.value.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = userEmail,
                            color = Color.White.copy(0.8f),
                            fontSize = 13.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Multiple Titles as pipe-separated text
                        Text(
                            text = titlesDisplay,
                            color = Color.White.copy(0.9f),
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

            // Edit Profile Button with Text - Always visible in top right
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 16.dp, end = 16.dp)
                    .clickable { onEditProfileClick() }
                    .clip(RoundedCornerShape(30.dp))
                    .background(Color.White.copy(alpha = 0.2f))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "Edit Profile",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // COLLAPSED STATE
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
                    // Left side - Avatar and name with pipe-separated titles
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        // Avatar with verification indicator
                        Box(
                            modifier = Modifier
                                .size(avatarSize)
                                .background(
                                    if (isVerified) gold else Color.White.copy(0.3f),
                                    CircleShape
                                )
                                .padding(2.dp)
                                .clip(CircleShape)
                                .background(Color.White),
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

                        // Name and titles column
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = userName,
                                color = Color.White,
                                fontSize = nameFontSize.value.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )

                            // Pipe-separated titles in collapsed state
                            Text(
                                text = titlesDisplay,
                                color = Color.White.copy(0.8f),
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }
                    }

                    // Edit button is already at the top, so no need for additional button here
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

@Composable
fun ProfileSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelLarge.copy(
                color = Color(0xFF006565).copy(0.7f),
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                fontSize = 12.sp
            ),
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column { content() }
        }
    }
}

@Composable
fun ProfileItem(icon: ImageVector, label: String, subtitle: String = "") {
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
                        colors = listOf(Color(0xFF006565).copy(0.1f), Color(0xFFE9C16C).copy(0.1f))
                    ),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF006565),
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
                color = Color(0xFF1A1C1E)
            )
            if (subtitle.isNotEmpty()) {
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }

        Icon(
            imageVector = Icons.Rounded.ChevronRight,
            contentDescription = null,
            tint = Color(0xFF006565).copy(0.5f),
            modifier = Modifier.size(20.dp)
        )
    }

    if (subtitle.isNotEmpty()) {
        HorizontalDivider(
            modifier = Modifier.padding(start = 76.dp, end = 16.dp),
            thickness = 0.5.dp,
            color = Color.LightGray.copy(0.3f)
        )
    }
}