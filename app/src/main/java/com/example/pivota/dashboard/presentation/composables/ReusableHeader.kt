package com.example.pivota.dashboard.presentation.composables

import android.annotation.SuppressLint
import androidx.compose.animation.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.pivota.R
import com.example.pivota.auth.domain.model.User
import com.example.pivota.dashboard.presentation.screens.ProfileMenuBottomSheet

@SuppressLint("Range")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReusableHeader(
    modifier: Modifier = Modifier,
    colorScheme: ColorScheme,
    pageTitle: String,
    user: User? = null,
    isGuestMode: Boolean = false,
    isSticky: Boolean = false,
    pageSubtitle: String? = null,
    scrollOffset: Float = 0f,
) {
    val context = LocalContext.current
    val profileUrl = user?.profileImageUrl?.takeIf { it.isNotBlank() }
    var showMenuBottomSheet by remember { mutableStateOf(false) }

    // Simple logic: hide title and subtitle when scrolled more than 20px
    val isScrolled = scrollOffset > 20f

    // Get user info
    val firstName = remember(user, isGuestMode) {
        when {
            user == null || isGuestMode -> "Guest"
            user.firstName.isNotBlank() -> user.firstName
            user.userName.isNotBlank() -> user.userName.split(" ").firstOrNull() ?: "Guest"
            else -> "Guest"
        }
    }

    val userRole = remember(user, isGuestMode) {
        when {
            isGuestMode -> "Guest User"
            user?.role?.equals("admin", ignoreCase = true) == true -> "Administrator"
            user?.role?.equals("professional", ignoreCase = true) == true -> "Professional"
            else -> "General User"
        }
    }

    Column(
        modifier = modifier
    ) {
        // Profile Header Card (always visible)
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
                // Left side - Profile Avatar and User Info
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

                    // User Info - always show full info
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
                    HeaderActionIcon(
                        icon = Icons.Outlined.MailOutline,
                        colorScheme = colorScheme
                    )

                    Box {
                        HeaderActionIcon(
                            icon = Icons.Outlined.NotificationsNone,
                            colorScheme = colorScheme
                        )
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
                                    color = colorScheme.surface,
                                    shape = CircleShape
                                )
                        )
                    }
                }
            }
        }

        // Page Title and Subtitle (hide when scrolling)
        AnimatedVisibility(
            visible = !isScrolled,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically()
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
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

    // Profile Menu Bottom Sheet
    if (showMenuBottomSheet) {
        ProfileMenuBottomSheet(
            onDismiss = { showMenuBottomSheet = false },
            colorScheme = colorScheme,
            onMyAccountClick = {
                showMenuBottomSheet = false
            },
            onMyListingsClick = {
                showMenuBottomSheet = false
            },
            onMyFavoritesClick = {
                showMenuBottomSheet = false
            },
            onPostClick = {
                showMenuBottomSheet = false
            },
            onLogoutClick = {
                showMenuBottomSheet = false
            }
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
            .background(colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .clickable(onClick = onClick),
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