package com.example.pivota.dashboard.presentation.composables

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
            )
        )
    }

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { banners.size }
    )

    val scope = rememberCoroutineScope()

    // Safe auto-scroll (prevents runaway coroutine issues)
    LaunchedEffect(pagerState) {
        while (true) {
            delay(5000)
            val next = (pagerState.currentPage + 1) % banners.size
            scope.launch {
                pagerState.animateScrollToPage(next)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding, vertical = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp) // Slightly increased height for better visibility
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                pageSpacing = 12.dp
            ) { page ->

                val banner = banners[page]

                Card(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { onCtaClick(banner.type) },
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Box(Modifier.fillMaxSize()) {

                        // ✅ FIXED: Lowered image with Alignment.TopCenter to show head/face
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(banner.imageRes)
                                .size(800, 400)
                                .allowHardware(false)
                                .crossfade(true)
                                .build(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            alignment = Alignment.TopCenter, // 👈 KEY CHANGE: aligns image to top
                            modifier = Modifier.fillMaxSize(),
                            error = painterResource(R.drawable.property_placeholder1)
                        )

                        // Gradient overlay (adjusted to work better with lower image)
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

                        // Content (slightly adjusted padding)
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {

                            Column(modifier = Modifier.weight(0.6f)) {
                                Text(
                                    text = banner.title(displayName, isGuestMode),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = banner.subtitle(isGuestMode),
                                    fontSize = 13.sp,
                                    color = Color.White.copy(alpha = 0.9f),
                                    maxLines = 2
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
                                    text = banner.ctaText(isGuestMode),
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
                                    pagerState.animateScrollToPage(index)
                                }
                            }
                    )
                }
            }
        }
    }
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
    PROFESSIONALS
}