package com.example.pivota.dashboard.presentation.composables.client_general_composables.general

import com.example.pivota.dashboard.presentation.composables.client_general_composables.profile_composables.ProfileMenuBottomSheet
import android.annotation.SuppressLint
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.pivota.R
import com.example.pivota.core.presentations.viewmodel.ThemeViewModel
import com.example.pivota.dashboard.presentation.composables.client_general_composables.profile_composables.LogoutConfirmationDialog
import com.example.pivota.dashboard.presentation.viewmodels.client_general_viewmodels.DashboardSharedViewModel
import com.example.pivota.dashboard.presentation.viewmodels.client_general_viewmodels.HeaderState
import kotlinx.coroutines.delay
import kotlin.math.absoluteValue

// Plan configuration data class
data class PlanConfig(
    val name: String,
    val icon: ImageVector,
    val color: Color
)

// Plan configurations using theme colors
val getPlanConfig: @Composable (String?) -> PlanConfig = { planName ->
    val colorScheme = MaterialTheme.colorScheme

    when (planName) {
        "Free Forever" -> PlanConfig(
            name = "Free",
            icon = Icons.Outlined.EmojiEvents,
            color = colorScheme.tertiary
        )
        "Starter" -> PlanConfig(
            name = "Starter",
            icon = Icons.Outlined.Whatshot,
            color = colorScheme.secondary
        )
        "Pro" -> PlanConfig(
            name = "Pro",
            icon = Icons.Outlined.WorkspacePremium,
            color = colorScheme.primary
        )
        "Enterprise" -> PlanConfig(
            name = "Enterprise",
            icon = Icons.Outlined.Business,
            color = colorScheme.primary.copy(alpha = 0.8f)
        )
        else -> PlanConfig(
            name = "Member",
            icon = Icons.Outlined.Person,
            color = colorScheme.onSurfaceVariant
        )
    }
}

// Helper function to truncate text professionally
private fun truncateText(text: String, maxLength: Int = 20): String {
    return if (text.length > maxLength) {
        text.substring(0, maxLength - 3) + "..."
    } else {
        text
    }
}


private fun getBorderColor(colorScheme: ColorScheme): Color {
    return colorScheme.surfaceVariant
}

@SuppressLint("Range")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun ReusableHeader(
    modifier: Modifier = Modifier,
    colorScheme: ColorScheme,
    pageTitle: String,
    isGuestMode: Boolean = false,
    isSticky: Boolean = false,
    pageSubtitle: String? = null,
    scrollOffset: Float = 0f,
    sharedViewModel: DashboardSharedViewModel,
    showSearchIcon: Boolean = false,
    onSearchClick: () -> Unit = {},
    messageCount: Int = 0,
    notificationCount: Int = 0,
    onLogoutComplete: () -> Unit = {},
    onMessageClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {}
) {
    val context = LocalContext.current
    var showMenuBottomSheet by remember { mutableStateOf(false) }
    val showLogoutDialog by sharedViewModel.showLogoutDialog.collectAsState()

    // Animation state for elevation
    var targetElevation by remember { mutableStateOf(4.dp) }

    LaunchedEffect(isSticky, scrollOffset) {
        targetElevation = when {
            isSticky -> 12.dp
            scrollOffset > 10f -> 8.dp
            else -> 4.dp
        }
    }

    val headerState by sharedViewModel.headerState.collectAsState()

    val headerUser = remember(headerState) {
        (headerState as? HeaderState.Success)?.headerUser
    }
    val isLoading = headerState is HeaderState.Loading

    // Combine message and notification counts for the badge
    val totalUnread = messageCount + notificationCount

    LaunchedEffect(headerState) {
        println("🔍 [ReusableHeader] headerState type: ${headerState::class.simpleName}")
        when (headerState) {
            is HeaderState.Success -> {
                val user = (headerState as HeaderState.Success).headerUser
                println("🔍 [ReusableHeader] SUCCESS - name: ${user.name}, shortName: ${user.shortName}, scope: ${user.scope}, planName: ${user.planName}, role: ${user.role}")
            }
            is HeaderState.Loading -> println("🔍 [ReusableHeader] LOADING")
            is HeaderState.Error -> println("🔍 [ReusableHeader] ERROR: ${(headerState as HeaderState.Error).message}")
            is HeaderState.AuthError -> println("🔍 [ReusableHeader] AUTH_ERROR: ${(headerState as HeaderState.AuthError).message}")
        }
    }

    val rotateAngle by animateFloatAsState(
        targetValue = if (showMenuBottomSheet) 180f else 0f,
        animationSpec = tween(durationMillis = 200),
        label = "dropdown_rotation"
    )

    val themeViewModel: ThemeViewModel = hiltViewModel()
    val isDarkTheme by themeViewModel.isDarkTheme
    val isScrolled = scrollOffset > 20f

    // Get user data with truncation
    val fullFirstName = when {
        isGuestMode -> "Guest"
        headerUser != null -> headerUser.shortName
        else -> "User"
    }
    val firstName = truncateText(fullFirstName, 15)

    // Determine display text based on scope with truncation
    val userScope = headerUser?.scope ?: "BUSINESS"
    val planName = headerUser?.planName
    val userRole = headerUser?.role ?: "Member"

    // Scope-specific display logic
    val isSystemScope = userScope == "SYSTEM"
    val isBusinessScope = userScope == "BUSINESS"

    // Get plan config with theme awareness
    val planConfig = if (isBusinessScope) getPlanConfig(planName) else null
    val truncatedPlanName = truncateText(planConfig?.name ?: "Member", 10)

    // Display text format with NO truncation for SYSTEM scope
    val displayText = when {
        isGuestMode -> "Guest"
        isSystemScope -> "Admin"  // ← FIXED: Show "Admin" instead of truncated role
        isBusinessScope -> truncatedPlanName
        else -> "Member"  // ← FIXED: Fallback to "Member" instead of truncated role
    }

    val profileImageUrl = when {
        isGuestMode -> null
        headerUser != null -> headerUser.avatarUrl
        else -> null
    }
    val isVerified = !isGuestMode && true  // Temporarily always true for testing
// TODO: Change back to: val isVerified = !isGuestMode && (headerUser?.isVerified == true)

    LaunchedEffect(headerUser, isLoading) {
        println("🔍 [ReusableHeader] Current values - firstName: $firstName, displayText: $displayText, scope: $userScope, planName: $planName, isLoading: $isLoading")
    }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // Professional Card Header with Rounded Corners on ALL sides and White Background
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = targetElevation,
                    shape = RoundedCornerShape(28.dp),
                    ambientColor = Color.Black.copy(alpha = 0.08f),
                    spotColor = Color.Black.copy(alpha = 0.06f)
                ),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = targetElevation
            )
        ) {
            Column {
                // Main Header Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left Section - Profile & User Info
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Profile Avatar with Professional Border
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .clickable { showMenuBottomSheet = true }
                                .shadow(
                                    elevation = 2.dp,
                                    shape = CircleShape,
                                    ambientColor = Color.Black.copy(alpha = 0.15f),
                                    spotColor = Color.Black.copy(alpha = 0.1f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            when {
                                isGuestMode -> {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                brush = Brush.linearGradient(
                                                    colors = listOf(
                                                        colorScheme.primary.copy(alpha = 0.15f),
                                                        colorScheme.primary.copy(alpha = 0.05f)
                                                    )
                                                ),
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
                                }
                                isLoading && headerUser == null -> {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                color = colorScheme.surfaceVariant,
                                                shape = CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Outlined.AccountCircle,
                                            contentDescription = "Profile",
                                            tint = colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                }
                                else -> {
                                    // Get alternating border color based on user ID (border for everyone)
                                    val borderColor = getBorderColor(colorScheme)

                                    AsyncImage(
                                        model = ImageRequest.Builder(context)
                                            .data(profileImageUrl)
                                            .size(128)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = "Profile",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape)
                                            .border(
                                                width = 2.5.dp,
                                                color = borderColor,
                                                shape = CircleShape
                                            ),
                                        placeholder = painterResource(R.drawable.job_placeholder3),
                                        error = painterResource(R.drawable.job_placeholder3)
                                    )
                                }
                            }
                        }

                        // User Info Column
                        Column(
                            modifier = Modifier
                                .clickable { showMenuBottomSheet = true }
                                .weight(1f)
                        ) {
                            // Name Row with Verified Badge
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = firstName,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = colorScheme.onSurface,
                                    letterSpacing = 0.2.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f, fill = false),
                                    softWrap = false
                                )

                                if (isVerified && !isGuestMode) {
                                    Icon(
                                        Icons.Outlined.Verified,
                                        contentDescription = "Verified",
                                        tint = colorScheme.tertiary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }

                                Icon(
                                    Icons.Outlined.KeyboardArrowDown,
                                    contentDescription = "Menu",
                                    tint = colorScheme.onSurfaceVariant,
                                    modifier = Modifier
                                        .size(18.dp)
                                        .graphicsLayer {
                                            rotationZ = rotateAngle
                                        }
                                )
                            }

                            // Display Role/Plan with Professional Styling
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.padding(top = 2.dp)
                            ) {
                                when {
                                    isBusinessScope && planConfig != null -> {
                                        // Business scope: Show pill with Plan
                                        Surface(
                                            shape = RoundedCornerShape(20.dp),
                                            color = planConfig.color.copy(alpha = 0.12f),
                                            modifier = Modifier
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                            ) {
                                                Icon(
                                                    planConfig.icon,
                                                    contentDescription = null,
                                                    tint = planConfig.color,
                                                    modifier = Modifier.size(12.dp)
                                                )
                                                Text(
                                                    text = displayText,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    color = planConfig.color,
                                                    letterSpacing = 0.2.sp,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                        }
                                    }
                                    isSystemScope -> {
                                        // System scope: Show "Admin" pill
                                        Surface(
                                            shape = RoundedCornerShape(20.dp),
                                            color = colorScheme.primary.copy(alpha = 0.12f),
                                            modifier = Modifier
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                            ) {
                                                Icon(
                                                    Icons.Outlined.AdminPanelSettings,
                                                    contentDescription = null,
                                                    tint = colorScheme.primary,
                                                    modifier = Modifier.size(12.dp)
                                                )
                                                Text(
                                                    text = displayText,  // Now shows "Admin"
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    color = colorScheme.primary,
                                                    letterSpacing = 0.2.sp,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                        }
                                    }
                                    else -> {
                                        // Fallback - just text (shows "Member")
                                        Text(
                                            text = displayText,  // Now shows "Member"
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = colorScheme.onSurfaceVariant,
                                            letterSpacing = 0.1.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Right Section - Action Icons
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Search Icon (conditionally shown)
                        if (showSearchIcon) {
                            HeaderActionIcon(
                                icon = Icons.Rounded.Search,
                                colorScheme = colorScheme,
                                onClick = onSearchClick
                            )
                        }

                        // Theme Toggle Icon
                        HeaderActionIcon(
                            icon = if (isDarkTheme) Icons.Outlined.LightMode else Icons.Outlined.DarkMode,
                            colorScheme = colorScheme,
                            onClick = { themeViewModel.toggleTheme() }
                        )

                        // Notifications Icon with Badge
                        Box {
                            HeaderActionIcon(
                                icon = Icons.Outlined.NotificationsActive,
                                colorScheme = colorScheme,
                                onClick = {
                                    onNotificationClick()
                                    onMessageClick()
                                }
                            )
                            if (totalUnread > 0) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .offset(x = 2.dp, y = 2.dp)
                                        .background(
                                            brush = Brush.linearGradient(
                                                colors = listOf(
                                                    Color.Red,
                                                    Color.Red.copy(alpha = 0.8f)
                                                )
                                            ),
                                            shape = CircleShape
                                        )
                                        .padding(horizontal = 5.dp, vertical = 2.dp)
                                        .defaultMinSize(minWidth = 18.dp, minHeight = 18.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (totalUnread > 99) "99+" else totalUnread.toString(),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }

                // Animated Page Title Section - Hides on scroll
                AnimatedVisibility(
                    visible = !isScrolled,
                    enter = fadeIn(animationSpec = tween(300)) +
                            slideInVertically(initialOffsetY = { -it / 2 }, animationSpec = tween(300)),
                    exit = fadeOut(animationSpec = tween(200)) +
                            slideOutVertically(targetOffsetY = { -it / 2 }, animationSpec = tween(200))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, bottom = 20.dp)
                    ) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = pageTitle,
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.onSurface,
                                letterSpacing = (-0.5).sp,
                                fontSize = 28.sp
                            ),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (pageSubtitle != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = pageSubtitle,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                    fontSize = 14.sp
                                ),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        // Bottom shadow line for professional separation
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            colorScheme.outline.copy(alpha = 0.1f),
                            colorScheme.outline.copy(alpha = 0.1f),
                            Color.Transparent
                        )
                    )
                )
        )
    }

    // Bottom Sheet for Profile Menu
    if (showMenuBottomSheet) {
        ProfileMenuBottomSheet(
            onDismiss = { showMenuBottomSheet = false },
            colorScheme = colorScheme,
            onMyAccountClick = { showMenuBottomSheet = false },
            onMyListingsClick = { showMenuBottomSheet = false },
            onMyFavoritesClick = { showMenuBottomSheet = false },
            onPostClick = { showMenuBottomSheet = false },
            onLogoutClick = {
                showMenuBottomSheet = false
                sharedViewModel.onLogoutClicked()
            }
        )
    }

    // Logout Confirmation Dialog
    if (showLogoutDialog) {
        LogoutConfirmationDialog(
            onConfirm = {
                sharedViewModel.onLogoutConfirmed()
                onLogoutComplete()
            },
            onDismiss = { sharedViewModel.onLogoutCancelled() }
        )
    }
}

@Composable
fun HeaderActionIcon(
    icon: ImageVector,
    colorScheme: ColorScheme,
    onClick: () -> Unit = {}
) {
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = tween(durationMillis = 100),
        label = "icon_scale"
    )

    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .shadow(
                elevation = if (isPressed) 0.dp else 2.dp,
                shape = CircleShape,
                ambientColor = Color.Black.copy(alpha = 0.1f),
                spotColor = Color.Black.copy(alpha = 0.08f)
            )
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        colorScheme.surfaceVariant.copy(alpha = 0.8f),
                        colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    )
                ),
                shape = CircleShape
            )
            .clickable(
                onClick = {
                    isPressed = true
                    onClick()
                }
            )
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
    }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(100)
            isPressed = false
        }
    }
}