package com.example.pivota.core.presentations.composables.background_image_and_overlay

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.pivota.core.presentations.composables.buttons.PivotaUpgradeButton
import kotlinx.coroutines.delay
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BackgroundImageAndOverlay(
    isWideScreen: Boolean,
    welcomeText: String = "",
    desc1: String = "",
    desc2: String = "",
    header: String = "",
    offset: Dp = 0.dp,
    imageHeight: Dp,
    showUpgradeButton: Boolean,
    image: Int = 0, // fallback image
    enableCarousel: Boolean = false,
    images: List<Int> = emptyList(),
    messages: List<String> = emptyList()
) {
    val context = LocalContext.current
    val pagerState = rememberPagerState(
        initialPage = 0
    ) { images.size }

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
            modifier = if (isWideScreen) Modifier.fillMaxSize()
            else Modifier.fillMaxWidth().height(imageHeight)
        ) { page ->
            // Replaced Image with AsyncImage to fix memory crashes
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
        // Fallback image using AsyncImage
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(image)
                .crossfade(true)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = if (isWideScreen) Modifier.fillMaxSize()
            else Modifier.fillMaxWidth().height(imageHeight)
        )
    }

    // Overlay
    Box(
        modifier = Modifier
            .offset(y = offset)
            .fillMaxWidth()
            .fillMaxHeight()
            .clip(RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp))
            .background(Color(0xAA008080))
            .zIndex(1f)
    ) {
        if (isWideScreen) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (enableCarousel && messages.isNotEmpty()) {
                    Text(
                        text = messages[pagerState.currentPage],
                        style = MaterialTheme.typography.headlineSmall.copy(color = Color.White),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.Center) {
                        repeat(images.size) { index ->
                            Box(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .size(if (pagerState.currentPage == index) 12.dp else 8.dp)
                                    .background(
                                        color = if (pagerState.currentPage == index) Color.White else Color.Gray,
                                        shape = RoundedCornerShape(50)
                                    )
                            )
                        }
                    }
                } else {
                    Text(
                        text = welcomeText,
                        style = MaterialTheme.typography.headlineSmall.copy(color = Color.White)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    if (desc1.isNotBlank()) {
                        Text(
                            text = desc1,
                            style = MaterialTheme.typography.bodyLarge.copy(color = Color.White),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    if (desc2.isNotBlank()) {
                        Text(
                            text = desc2,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color(
                                    0xFFFFC107
                                )
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                if (showUpgradeButton) {
                    PivotaUpgradeButton(modifier = Modifier.padding(top = 15.dp))
                }
            }
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 24.dp)
            ) {
                if (enableCarousel && messages.isNotEmpty()) {
                    Text(
                        text = messages[pagerState.currentPage],
                        style = MaterialTheme.typography.headlineSmall.copy(color = Color.White),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.Center) {
                        repeat(images.size) { index ->
                            Box(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .size(if (pagerState.currentPage == index) 12.dp else 8.dp)
                                    .background(
                                        color = if (pagerState.currentPage == index) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.outline,
                                        shape = RoundedCornerShape(50)
                                    )
                            )
                        }
                    }
                } else {
                    HorizontalDivider(
                        modifier = Modifier
                            .width(100.dp)
                            .padding(bottom = 8.dp),
                        thickness = 2.dp,
                        color = Color(0xFFFFC107)
                    )

                    Text(
                        text = header,
                        style = MaterialTheme.typography.headlineMedium.copy(color = Color.White),
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                }
            }
        }
    }
}