package com.example.pivota.dashboard.presentation.composables

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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.pivota.R
import com.example.pivota.core.presentations.viewmodel.ThemeViewModel
import com.example.pivota.dashboard.presentation.viewmodels.DashboardSharedViewModel
import com.example.pivota.dashboard.presentation.screens.ProfileMenuBottomSheet
import com.example.pivota.dashboard.presentation.viewmodels.HeaderState

@SuppressLint("Range")
@OptIn(ExperimentalMaterial3Api::class)
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
    messageCount: Int = 0,
    notificationCount: Int = 0,
) {
    val context = LocalContext.current
    var showMenuBottomSheet by remember { mutableStateOf(false) }


    // Force recomposition by adding a key that changes when headerState updates
    val headerState by sharedViewModel.headerState.collectAsState()

    // Add a derived state that forces recomposition
    val headerUser = remember(headerState) {
        (headerState as? HeaderState.Success)?.headerUser
    }
    val isLoading = headerState is HeaderState.Loading

    // Add a LaunchedEffect that triggers recomposition when headerState changes
    LaunchedEffect(headerState) {
        println("🔍 [ReusableHeader] headerState type: ${headerState::class.simpleName}")
        when (headerState) {
            is HeaderState.Success -> {
                val user = (headerState as HeaderState.Success).headerUser
                println("🔍 [ReusableHeader] SUCCESS - name: ${user.name}, shortName: ${user.shortName}")
            }
            is HeaderState.Loading -> println("🔍 [ReusableHeader] LOADING")
            is HeaderState.Error -> println("🔍 [ReusableHeader] ERROR: ${(headerState as HeaderState.Error).message}")
            is HeaderState.AuthError -> println("🔍 [ReusableHeader] AUTH_ERROR: ${(headerState as HeaderState.AuthError).message}")
        }
    }

    // Animated rotation for dropdown icon
    val rotateAngle by animateFloatAsState(
        targetValue = if (showMenuBottomSheet) 180f else 0f,
        animationSpec = tween(durationMillis = 200),
        label = "dropdown_rotation"
    )

    val themeViewModel: ThemeViewModel = hiltViewModel()
    val isDarkTheme by themeViewModel.isDarkTheme
    val isScrolled = scrollOffset > 20f

    // Use headerUser for all data
    val firstName = when {
        isGuestMode -> "Guest"
        headerUser != null -> headerUser.shortName
        else -> "User"
    }

    val userRole = when {
        isGuestMode -> "Guest"
        headerUser != null -> headerUser.role
        else -> "Member"
    }

    val profileImageUrl = when {
        isGuestMode -> null
        headerUser != null -> headerUser.avatarUrl
        else -> null
    }

    val isVerified = !isGuestMode && (headerUser?.isVerified == true)

    // Log current values
    LaunchedEffect(headerUser, isLoading) {
        println("🔍 [ReusableHeader] Current values - firstName: $firstName, userRole: $userRole, isLoading: $isLoading, headerUser: ${headerUser != null}")
    }

    Column(
        modifier = modifier
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = if (isSticky) 8.dp else 4.dp,
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
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Profile Avatar
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .clickable { showMenuBottomSheet = true },
                        contentAlignment = Alignment.Center
                    ) {
                        when {
                            isGuestMode -> {
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
                                            if (isVerified) 2.dp else 0.dp,
                                            if (isVerified) colorScheme.tertiary else Color.Transparent,
                                            CircleShape
                                        ),
                                    placeholder = painterResource(R.drawable.job_placeholder3),
                                    error = painterResource(R.drawable.job_placeholder3)
                                )
                            }
                        }
                    }

                    Column(
                        modifier = Modifier
                            .clickable { showMenuBottomSheet = true }
                            .weight(1f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Hi, $firstName",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = colorScheme.onSurface,
                                letterSpacing = 0.2.sp,
                                maxLines = 1,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
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

                        Text(
                            text = userRole,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Normal,
                            color = colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            letterSpacing = 0.1.sp,
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HeaderActionIcon(
                        icon = if (isDarkTheme) Icons.Outlined.LightMode else Icons.Outlined.DarkMode,
                        colorScheme = colorScheme,
                        onClick = { themeViewModel.toggleTheme() }
                    )

                    Box {
                        HeaderActionIcon(
                            icon = Icons.Outlined.MailOutline,
                            colorScheme = colorScheme
                        )
                        if (notificationCount > 0) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = 4.dp, y = 4.dp)
                                    .background(Color.Red, CircleShape)
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                                    .defaultMinSize(minWidth = 16.dp, minHeight = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (notificationCount > 99) "99+" else notificationCount.toString(),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = !isScrolled,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically()
        ) {
            Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = pageTitle,
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onSurface,
                        letterSpacing = (-0.5).sp,
                        fontSize = 28.sp
                    )
                )
                if (pageSubtitle != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = pageSubtitle,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            fontSize = 14.sp
                        )
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }

    if (showMenuBottomSheet) {
        ProfileMenuBottomSheet(
            onDismiss = { showMenuBottomSheet = false },
            colorScheme = colorScheme,
            onMyAccountClick = { showMenuBottomSheet = false },
            onMyListingsClick = { showMenuBottomSheet = false },
            onMyFavoritesClick = { showMenuBottomSheet = false },
            onPostClick = { showMenuBottomSheet = false },
            onLogoutClick = { showMenuBottomSheet = false }
        )
    }
}

@Composable
fun HeaderActionIcon(
    icon: ImageVector,
    colorScheme: ColorScheme,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(
                color = colorScheme.surfaceVariant.copy(alpha = 0.7f),
                shape = CircleShape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
    }
}