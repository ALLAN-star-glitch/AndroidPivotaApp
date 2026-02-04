package com.example.pivota.core.presentations.composables.background_image_and_overlay

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.pivota.core.presentations.composables.buttons.PivotaUpgradeButton
import kotlinx.coroutines.delay

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BackgroundImageAndOverlay(
    isWideScreen: Boolean,
    desc1: String = "",
    desc2: String = "",
    header: String = "",
    showUpgradeButton: Boolean,
    image: Int = 0,
    enableCarousel: Boolean = false,
    images: List<Int> = emptyList(),
    messages: List<String> = emptyList()
) {
    val context = LocalContext.current
    val pagerState = rememberPagerState(initialPage = 0) { images.size }

    // Proportions for layout consistency
    val topSpacerWeight = if (isWideScreen) 0.5f else 0.3f
    val overlayWeight = if (isWideScreen) 0.5f else 0.7f

    Box(modifier = Modifier.fillMaxSize()) {

        /* 1. TUNED BACKGROUND LAYER (Image or Carousel) */
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    // Wide screen fills the whole pane (1f)
                    // Mobile takes top area + slight overlap (0.2f) for smooth transition
                    .weight(if (isWideScreen) 1f else topSpacerWeight + 0.2f)
            ) {
                if (enableCarousel && images.isNotEmpty()) {
                    LaunchedEffect(Unit) {
                        while (true) {
                            delay(3000)
                            val next = (pagerState.currentPage + 1) % images.size
                            pagerState.animateScrollToPage(next)
                        }
                    }
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(images[page])
                                .crossfade(true)
                                .build(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                } else {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(image)
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            // Only add a bottom spacer on mobile to stop the image rendering behind content
            if (!isWideScreen) {
                Spacer(modifier = Modifier.weight(0.2f))
            }
        }

        /* 2. OVERLAY LAYER */
        Column(modifier = Modifier.fillMaxSize()) {

            // Pushes the teal box down
            Spacer(modifier = Modifier.weight(topSpacerWeight))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(overlayWeight)
                    .clip(RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(0.5f)) // Teal overlay with transparency
                    .zIndex(1f)
            ) {
                OverlayContent(
                    isWideScreen = isWideScreen,
                    enableCarousel = enableCarousel,
                    messages = messages,
                    pagerState = pagerState,
                    imagesSize = images.size,
                    desc1 = desc1,
                    desc2 = desc2,
                    header = header,
                    showUpgradeButton = showUpgradeButton
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun OverlayContent(
    isWideScreen: Boolean,
    enableCarousel: Boolean,
    messages: List<String>,
    pagerState: PagerState,
    imagesSize: Int,
    desc1: String,
    desc2: String,
    header: String,
    showUpgradeButton: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        /* ───────── ALWAYS VISIBLE GOLDEN DIVIDER ───────── */
        Box(
            modifier = Modifier
                .width(45.dp)               // Short, professional width
                .height(4.dp)                // Professional thickness
                .clip(RoundedCornerShape(2.dp))
                .background(Color(0xFFFFC107))   // Golden Yellow
        )

        Spacer(modifier = Modifier.height(18.dp))

        if (enableCarousel && messages.isNotEmpty()) {
            /* ───────── CAROUSEL CONTENT ───────── */
            Text(
                text = messages[pagerState.currentPage],
                style = if (isWideScreen) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.titleMedium,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Carousel Indicators (Dots)
            Row(horizontalArrangement = Arrangement.Center) {
                repeat(imagesSize) { index ->
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .size(if (pagerState.currentPage == index) 10.dp else 6.dp)
                            .background(
                                color = if (pagerState.currentPage == index) Color.White else Color.Gray,
                                shape = RoundedCornerShape(50)
                            )
                    )
                }
            }
        } else {
            /* ───────── HEADER & DESCRIPTION CONTENT ───────── */
            if (header.isNotBlank()) {
                Text(
                    text = header,
                    style = MaterialTheme.typography.headlineSmall, // Professional Header
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (desc1.isNotBlank()) {
                Text(
                    text = desc1,
                    style = MaterialTheme.typography.bodyLarge, // Supporting Description
                    color = Color.White.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center
                )
            }

            if (desc2.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = desc2,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFFFC107), // Secondary accent text
                    textAlign = TextAlign.Center
                )
            }
        }

        if (showUpgradeButton) {
            PivotaUpgradeButton(modifier = Modifier.padding(top = 15.dp))
        }
    }
}