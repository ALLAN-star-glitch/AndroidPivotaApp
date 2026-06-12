package com.example.pivota.dashboard.presentation.composables.client_general_composables.listings_composables.professionals

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

/**
 * Contact information data class
 */
data class ContactInfo(
    val phoneNumber: String,
    val email: String,
    val name: String? = null,
    val businessName: String? = null,
    val responseTime: String? = null
)

/**
 * Elegant bottom sheet for displaying contact details
 * @param contactInfo The contact information to display
 * @param onDismiss Callback when bottom sheet is dismissed
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactBottomSheet(
    contactInfo: ContactInfo,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val colorScheme = MaterialTheme.colorScheme

    // Animation states
    var isVisible by remember { mutableStateOf(false) }

    // Animate entrance
    LaunchedEffect(Unit) {
        isVisible = true
    }

    ModalBottomSheet(
        onDismissRequest = {
            isVisible = false
            onDismiss()
        },
        sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = false,
            confirmValueChange = { true }
        ),
        containerColor = colorScheme.surface,
        tonalElevation = 0.dp,
        shape = RoundedCornerShape(
            topStart = 24.dp,
            topEnd = 24.dp,
            bottomStart = 0.dp,
            bottomEnd = 0.dp
        ),
        dragHandle = {
            // Custom drag handle with animation
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(300)) + slideInVertically(
                    initialOffsetY = { -50 },
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                )
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .width(40.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    ) {}
                }
            }
        }
    ) {
        AnimatedContent(
            targetState = isVisible,
            transitionSpec = {
                fadeIn(animationSpec = tween(300)) + slideInVertically(
                    initialOffsetY = { 100 },
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                ) togetherWith
                        fadeOut(animationSpec = tween(200)) + slideOutVertically(
                    targetOffsetY = { 100 }
                )
            }
        ) { visible ->
            if (visible) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 32.dp, top = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header Section with Icon
                    HeaderSection(colorScheme = colorScheme)

                    Spacer(modifier = Modifier.height(8.dp))

                    // Business/Professional Name
                    if (contactInfo.name != null || contactInfo.businessName != null) {
                        Text(
                            text = contactInfo.businessName ?: contactInfo.name ?: "",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp
                            ),
                            color = colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    // Response time badge
                    if (contactInfo.responseTime != null) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = colorScheme.primaryContainer.copy(alpha = 0.3f),
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    Icons.Outlined.AccessTime,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = colorScheme.primary
                                )
                                Text(
                                    "Typically responds in ${contactInfo.responseTime}",
                                    fontSize = 12.sp,
                                    color = colorScheme.primary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Contact Options Title
                    Text(
                        text = "Contact Options",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.5.sp
                        ),
                        color = colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Phone Contact Card
                    ContactActionCard(
                        icon = Icons.Filled.Phone,
                        iconBackgroundColor = Color(0xFF34C759),
                        title = "Call",
                        subtitle = contactInfo.phoneNumber,
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:${contactInfo.phoneNumber}")
                            }
                            context.startActivity(intent)
                        },
                        colorScheme = colorScheme,
                        delayAnimation = 100
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Email Contact Card
                    ContactActionCard(
                        icon = Icons.Filled.Email,
                        iconBackgroundColor = Color(0xFF007AFF),
                        title = "Email",
                        subtitle = contactInfo.email,
                        onClick = {
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:${contactInfo.email}")
                                putExtra(Intent.EXTRA_EMAIL, arrayOf(contactInfo.email))
                            }
                            context.startActivity(intent)
                        },
                        colorScheme = colorScheme,
                        delayAnimation = 200
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Note about response time
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(animationSpec = tween(500, delayMillis = 300))
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Outlined.Info,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Your contact request helps us connect you faster. The professional will get back to you shortly.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = colorScheme.onSurfaceVariant,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Close Button
                    OutlinedButton(
                        onClick = {
                            isVisible = false
                            onDismiss()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = colorScheme.onSurfaceVariant
                        )
                    ) {
                        Text("Close", fontWeight = FontWeight.Medium, fontSize = 15.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun HeaderSection(colorScheme: ColorScheme) {
    Box(
        modifier = Modifier
            .size(80.dp)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        colorScheme.primary.copy(alpha = 0.15f),
                        colorScheme.primary.copy(alpha = 0.05f)
                    ),
                    radius = 100f
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier.size(56.dp),
            shape = CircleShape,
            color = colorScheme.primary,
            shadowElevation = 4.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Filled.ContactSupport,
                    contentDescription = "Contact",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(12.dp))

    Text(
        text = "Contact Professional",
        style = MaterialTheme.typography.headlineSmall.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        ),
        color = colorScheme.onSurface,
        textAlign = TextAlign.Center
    )
}

@Composable
private fun ContactActionCard(
    icon: ImageVector,
    iconBackgroundColor: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    colorScheme: ColorScheme,
    delayAnimation: Int = 0
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "card_scale"
    )

    val alpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(300, delayMillis = delayAnimation),
        label = "card_alpha"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable(
                onClick = {
                    isPressed = true
                    onClick()
                }
            )
            .animateContentSize(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp,
            pressedElevation = 4.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Animated Icon
            AnimatedIcon(
                icon = icon,
                backgroundColor = iconBackgroundColor,
                colorScheme = colorScheme
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                    color = colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }

            // Animated Arrow
            AnimatedArrow(colorScheme = colorScheme)
        }
    }

    // Reset press state
    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(150)
            isPressed = false
        }
    }
}

@Composable
private fun AnimatedIcon(
    icon: ImageVector,
    backgroundColor: Color,
    colorScheme: ColorScheme
) {
    var isHovered by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isHovered) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "icon_scale"
    )

    Surface(
        modifier = Modifier
            .size(48.dp)
            .scale(scale),
        shape = CircleShape,
        color = backgroundColor,
        shadowElevation = 2.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun AnimatedArrow(colorScheme: ColorScheme) {
    var isAnimating by remember { mutableStateOf(false) }
    val translationX by animateFloatAsState(
        targetValue = if (isAnimating) 8f else 0f,
        animationSpec = repeatable(
            iterations = 1,
            animation = tween(300, easing = EaseOutCubic)
        ),
        label = "arrow_translation"
    )

    LaunchedEffect(Unit) {
        isAnimating = true
        delay(300)
        isAnimating = false
    }

    Icon(
        Icons.Default.ArrowForward,
        contentDescription = null,
        modifier = Modifier
            .size(20.dp)
            .offset(x = translationX.dp),
        tint = colorScheme.primary
    )
}

/**
 * Preview composable for ContactBottomSheet
 */
@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun PreviewContactBottomSheet() {
    MaterialTheme {
        ContactBottomSheet(
            contactInfo = ContactInfo(
                phoneNumber = "+254 712 345 678",
                email = "contact@professional.com",
                name = "John Doe",
                businessName = "Premium Services Ltd",
                responseTime = "within 1 hour"
            ),
            onDismiss = {}
        )
    }
}