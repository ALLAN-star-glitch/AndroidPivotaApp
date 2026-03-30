package com.example.pivota.welcome.presentation.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import com.example.pivota.R

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingPager(
    onOnboardingComplete: (purpose: String, purposeData: Map<String, Any>) -> Unit,
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
            pageSpacing = 16.dp,
            userScrollEnabled = false, // Disable hand swiping
            pageContent = { page ->
                // Apply crossfade animation when changing pages
                AnimatedContent(
                    targetState = page,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(300)) +
                                slideInHorizontally(
                                    initialOffsetX = { if (targetState > initialState) 300 else -300 },
                                    animationSpec = tween(300)
                                ) togetherWith
                                fadeOut(animationSpec = tween(200)) +
                                slideOutHorizontally(
                                    targetOffsetX = { if (targetState > initialState) -300 else 300 },
                                    animationSpec = tween(200)
                                )
                    }
                ) { currentPage ->
                    when (currentPage) {
                        0 -> JoiningAsScreenContent(
                            onContinue = { accountType -> goToNextPage() },
                            onLoginClick = onLoginClick,
                            currentStep = currentPage,
                            totalSteps = 2
                        )
                        1 -> PurposeSelectionScreenContent(
                            onContinue = { purpose, purposeData ->
                                // Pass the purpose data to the completion callback
                                onOnboardingComplete(purpose, purposeData)
                            },
                            currentStep = currentPage,
                            totalSteps = 2,
                            onSkipToDashboard = {
                                // Skip to dashboard with empty purpose
                                onOnboardingComplete("just_exploring", emptyMap())
                            },
                            onContinueWithGoogle = {
                                // Handle Google sign-in
                                onOnboardingComplete("google", emptyMap())
                            },
                            onJustExploring = {
                                // Navigate directly to registration for Just Exploring
                                onOnboardingComplete("just_exploring", emptyMap())
                            },

                        )
                    }
                }
            }
        )

        // Progress Indicator with animation
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp)
                .padding(horizontal = 24.dp)
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back Arrow Button (visible on all pages)
            AnimatedVisibility(
                visible = true,
                enter = fadeIn() + scaleIn(initialScale = 0.8f),
                exit = fadeOut() + scaleOut(targetScale = 0.8f)
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_back_arrow),
                    contentDescription = "Back",
                    modifier = Modifier
                        .size(32.dp)
                        .clickable {
                            if (currentPage > 0) {
                                goToPreviousPage()
                            }
                        }
                        .animateContentSize(),
                    tint = if (currentPage > 0)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                )
            }

            // Progress Indicators with animation
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.animateContentSize()
            ) {
                repeat(2) { index ->
                    val isActive = index < currentPage + 1
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                color = if (isActive)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.outlineVariant,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .animateContentSize(
                                animationSpec = tween(
                                    durationMillis = 300,
                                    easing = FastOutSlowInEasing
                                )
                            )
                    )
                }
            }
        }
    }
}