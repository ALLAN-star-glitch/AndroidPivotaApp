package com.example.pivota.core.presentations.composables.background_image_and_overlay

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EnhancedBackgroundImage(
    images: List<Int>,
    carouselMessages: List<String> = emptyList(),
    enableCarousel: Boolean = true,
    overlayOpacity: Float = 0.6f,
    carouselInterval: Long = 4000
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val pagerState = rememberPagerState(initialPage = 0) { images.size }

    // Fixed height that doesn't change based on device
    val fixedOverlayHeight = 180.dp

    LaunchedEffect(enableCarousel, images.size) {
        if (enableCarousel && images.size > 1) {
            while (isActive) {
                delay(carouselInterval)
                val nextPage = (pagerState.currentPage + 1) % images.size
                pagerState.animateScrollToPage(nextPage)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Background Images
        if (enableCarousel && images.isNotEmpty()) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                key = { it }
            ) { page ->
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(images[page])
                        .crossfade(true)
                        .build(),
                    contentDescription = "Welcome background ${page + 1}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        } else if (images.isNotEmpty()) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(images[0])
                    .crossfade(true)
                    .build(),
                contentDescription = "Welcome background",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Curved overlay at the bottom for carousel content - FIXED HEIGHT
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(fixedOverlayHeight)
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = overlayOpacity),
                            MaterialTheme.colorScheme.primary.copy(alpha = overlayOpacity * 1.3f)
                        )
                    )
                )
        ) {
            // Carousel Content inside the curved overlay
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                // Animated Carousel Message
                if (enableCarousel && carouselMessages.isNotEmpty()) {
                    AnimatedContent(
                        targetState = pagerState.currentPage,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(300)) +
                                    slideInVertically { it } togetherWith
                                    fadeOut(animationSpec = tween(300)) +
                                    slideOutVertically { -it }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { currentPage ->
                        Text(
                            text = carouselMessages.getOrElse(currentPage) { "" },
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Medium,
                                fontSize = 16.sp,
                                color = Color.White,
                                lineHeight = 24.sp
                            ),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Carousel Indicators
                if (enableCarousel && images.size > 1) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        repeat(images.size) { index ->
                            val isSelected = index == pagerState.currentPage
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 6.dp)
                                    .size(if (isSelected) 28.dp else 8.dp, 8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(
                                        color = if (isSelected)
                                            Color.White
                                        else
                                            Color.White.copy(alpha = 0.5f)
                                    )
                            )
                        }
                    }
                }
            }
        }
    }
}