package com.example.pivota.welcome.presentation.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import kotlinx.coroutines.launch
import com.example.pivota.R



@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingPager(
    onOnboardingComplete: () -> Unit,
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { 2 }
    )

    val coroutineScope = rememberCoroutineScope()
    val currentPage by remember { derivedStateOf { pagerState.currentPage } }

    // Handle page navigation
    fun goToNextPage() {
        coroutineScope.launch {
            pagerState.animateScrollToPage(currentPage + 1)
        }
    }

    fun goToPreviousPage() {
        coroutineScope.launch {
            pagerState.animateScrollToPage(currentPage - 1)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            pageSpacing = 16.dp
        ) { page ->
            when (page) {
                0 -> JoiningAsScreenContent(
                    onContinue = { accountType -> goToNextPage() },
                    onLoginClick = onLoginClick,
                    currentStep = currentPage,
                    totalSteps = 2
                )
                1 -> PurposeSelectionScreenContent(
                    onContinue = { purpose, purposeData ->
                        // Store purposeData if needed (e.g., in ViewModel)
                        goToNextPage()
                    },
                    onBack = { goToPreviousPage() },
                    currentStep = currentPage,
                    totalSteps = 2
                )
            }
        }

        // Progress Indicator
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp)
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = R.drawable.transparentpivlogo,
                contentDescription = "PivotaConnect Logo",
                modifier = Modifier.size(32.dp)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(2) { index ->
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                when {
                                    index < currentPage + 1 -> MaterialTheme.colorScheme.primary
                                    else -> MaterialTheme.colorScheme.outlineVariant
                                }
                            )
                    )
                }
            }
        }
    }
}