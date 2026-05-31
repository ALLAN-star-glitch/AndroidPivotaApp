package com.example.pivota.dashboard.presentation.composables.client_general_composables.general

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.request.crossfade
import com.example.pivota.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MarketingCarouselBanner(
    modifier: Modifier = Modifier,
    horizontalPadding: Dp = 16.dp,
    displayName: String = "Guest",
    isGuestMode: Boolean = false,
    onCtaClick: (bannerType: BannerType) -> Unit = {}
) {
    val context = LocalContext.current
    val colorScheme = MaterialTheme.colorScheme

    val banners = remember {
        listOf(
            BannerItem(
                type = BannerType.WELCOME_BACK,
                imageRes = R.drawable.welcome_pivota,
                title = { name, isGuest ->
                    if (isGuest) "Start Your Journey" else "Welcome Back, $name"
                },
                subtitle = { isGuest ->
                    if (isGuest) "Create an account to access all features"
                    else "Upgrade to Pro plan and unlock premium listings"
                },
                ctaText = { isGuest -> if (isGuest) "Sign Up →" else "Upgrade" }
            ),
            BannerItem(
                type = BannerType.JOBS,
                imageRes = R.drawable.find_job,
                title = { _, _ -> "Find Your Dream Job" },
                subtitle = { _ -> "1000+ jobs available in your area" },
                ctaText = { _ -> "Browse Jobs →" }
            ),
            BannerItem(
                type = BannerType.HOUSING,
                imageRes = R.drawable.found_property,
                title = { _, _ -> "Find Your Perfect Home" },
                subtitle = { _ -> "Rent or buy - Great deals available" },
                ctaText = { _ -> "View Properties →" }
            ),
            BannerItem(
                type = BannerType.PROFESSIONALS,
                imageRes = R.drawable.trusted_professional,
                title = { _, _ -> "Hire Trusted Professionals" },
                subtitle = { _ -> "Verified experts ready to help" },
                ctaText = { _ -> "Find Professionals →" }
            ),
            BannerItem(
                type = BannerType.SOCIAL_SUPPORT,
                imageRes = R.drawable.find_support,
                title = { _, _ -> "Find Social Support" },
                subtitle = { _ -> "Food aid, counseling, grants & more - Verified NGOs" },
                ctaText = { _ -> "Get Support →" }
            )
        )
    }

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { banners.size }
    )

    val scope = rememberCoroutineScope()

    // Safe auto-scroll with lifecycle awareness
    var isAutoScrollingEnabled by remember { mutableStateOf(true) }

    LaunchedEffect(pagerState, isAutoScrollingEnabled) {
        while (isAutoScrollingEnabled) {
            delay(5000)
            if (isAutoScrollingEnabled && pagerState.pageCount > 0) {
                val next = (pagerState.currentPage + 1) % banners.size
                scope.launch {
                    pagerState.animateScrollToPage(next)
                }
            }
        }
    }

    // Pause auto-scroll when user interacts
    LaunchedEffect(pagerState.currentPage) {
        isAutoScrollingEnabled = false
        delay(10000) // Resume after 10 seconds of inactivity
        isAutoScrollingEnabled = true
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding, vertical = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                pageSpacing = 12.dp,
                key = { page -> banners[page].type.name } // Add key for better recycling
            ) { page ->
                val banner = banners[page]

                // Memoize banner content to prevent recomposition
                val bannerTitle = remember(banner.type, displayName, isGuestMode) {
                    banner.title(displayName, isGuestMode)
                }
                val bannerSubtitle = remember(banner.type, isGuestMode) {
                    banner.subtitle(isGuestMode)
                }
                val bannerCtaText = remember(banner.type, isGuestMode) {
                    banner.ctaText(isGuestMode)
                }

                Card(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { onCtaClick(banner.type) },
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Box(Modifier.fillMaxSize()) {
                        // Optimized image loading
                        OptimizedBannerImage(
                            imageRes = banner.imageRes,
                            context = context
                        )

                        // Gradient overlay
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(
                                            colorScheme.primary.copy(alpha = 0.8f),
                                            colorScheme.primary.copy(alpha = 0.6f),
                                            colorScheme.primary.copy(alpha = 0.4f),
                                            colorScheme.primary.copy(alpha = 0.15f),
                                            Color.Transparent
                                        )
                                    )
                                )
                        )

                        // Content
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(0.6f)) {
                                Text(
                                    text = bannerTitle,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    maxLines = 2,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = bannerSubtitle,
                                    fontSize = 13.sp,
                                    color = Color.White.copy(alpha = 0.9f),
                                    maxLines = 2,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                )
                            }

                            Button(
                                onClick = { onCtaClick(banner.type) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorScheme.tertiary,
                                    contentColor = colorScheme.onTertiary
                                ),
                                shape = RoundedCornerShape(24.dp),
                                modifier = Modifier.height(40.dp)
                            ) {
                                Text(
                                    text = bannerCtaText,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            // Page indicators
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(banners.size) { index ->
                    val selected = index == pagerState.currentPage

                    Box(
                        modifier = Modifier
                            .size(width = if (selected) 24.dp else 8.dp, height = 4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(
                                if (selected) Color.White
                                else Color.White.copy(alpha = 0.5f)
                            )
                            .animateContentSize(
                                animationSpec = tween(
                                    durationMillis = 300,
                                    easing = FastOutSlowInEasing
                                )
                            )
                            .clickable {
                                scope.launch {
                                    isAutoScrollingEnabled = false
                                    pagerState.animateScrollToPage(index)
                                    delay(10000)
                                    isAutoScrollingEnabled = true
                                }
                            }
                    )
                }
            }
        }
    }
}

// OPTIMIZED BANNER IMAGE COMPOSABLE
@Composable
fun OptimizedBannerImage(
    imageRes: Int,
    context: android.content.Context
) {
    val imageRequest = remember(imageRes) {
        ImageRequest.Builder(context)
            .data(imageRes)
            .size(800, 400)
            .allowHardware(true) // Enable hardware acceleration for banners
            .crossfade(true)
            .build()
    }

    AsyncImage(
        model = imageRequest,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        alignment = Alignment.TopCenter,
        modifier = Modifier.fillMaxSize(),
        error = painterResource(R.drawable.property_placeholder1),
        fallback = painterResource(R.drawable.property_placeholder1)
    )
}

// ---------------- DATA ----------------

data class BannerItem(
    val type: BannerType,
    val imageRes: Int,
    val title: (String, Boolean) -> String,
    val subtitle: (Boolean) -> String,
    val ctaText: (Boolean) -> String
)

enum class BannerType {
    WELCOME_BACK,
    JOBS,
    HOUSING,
    PROFESSIONALS,
    SOCIAL_SUPPORT
}