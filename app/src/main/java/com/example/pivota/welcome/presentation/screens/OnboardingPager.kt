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
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.example.pivota.R
import com.example.pivota.auth.domain.model.User
import com.example.pivota.auth.presentation.composables.AdaptiveAuthLayout
import com.example.pivota.auth.presentation.viewModel.SignupViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pivota.core.preferences.PivotaDataStore
import kotlinx.coroutines.delay

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingPager(
    modifier: Modifier = Modifier,
    onOnboardingComplete: () -> Unit,
    onSignupSuccess: (String, String, String, User?) -> Unit,
    onLoginClick: () -> Unit,
    signupViewModel: SignupViewModel = hiltViewModel(),
    datastore: PivotaDataStore,
) {
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { 3 }
    )

    val coroutineScope = rememberCoroutineScope()
    val currentPage by remember { derivedStateOf { pagerState.currentPage } }
    var showContent by remember { mutableStateOf(false) }

    // Animate content entrance
    LaunchedEffect(Unit) {
        delay(200)
        showContent = true
    }

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
            userScrollEnabled = false,
            pageContent = { page ->
                AnimatedContent(
                    targetState = page,
                    transitionSpec = {
                        // Smoother page transition animations
                        fadeIn(
                            animationSpec = tween(
                                durationMillis = 500,
                                easing = FastOutSlowInEasing
                            )
                        ) + slideInHorizontally(
                            initialOffsetX = { if (targetState > initialState) 200 else -200 },
                            animationSpec = tween(
                                durationMillis = 500,
                                easing = FastOutSlowInEasing
                            )
                        ) togetherWith
                                fadeOut(
                                    animationSpec = tween(
                                        durationMillis = 300,
                                        easing = FastOutSlowInEasing
                                    )
                                ) + slideOutHorizontally(
                            targetOffsetX = { if (targetState > initialState) -200 else 200 },
                            animationSpec = tween(
                                durationMillis = 300,
                                easing = FastOutSlowInEasing
                            )
                        )
                    },
                    label = "page_transition"
                ) { currentPageValue ->
                    when (currentPageValue) {
                        0 -> AdaptiveJoiningAsScreenContent(
                            onContinue = { goToNextPage() },
                            onLoginClick = onLoginClick,
                            onSkipToDashboard = {
                                onOnboardingComplete()
                            },
                            currentStep = currentPageValue,
                            totalSteps = 3
                        )
                        1 -> AdaptivePurposeSelectionScreenContent(
                            onContinue = { goToNextPage() },
                            onSkipToDashboard = {
                                onOnboardingComplete()
                            },
                            onContinueWithGoogle = {
                                onOnboardingComplete()
                            },
                            onJustExploring = {},
                            currentStep = currentPageValue,
                            totalSteps = 3
                        )
                        2 -> AdaptiveAuthLayout(
                            viewModel = signupViewModel,
                            desc1 = "After registering, you can upgrade your account...",
                            desc2 = "It's free to join. Upgrade when you're ready!",
                            isLoginScreen = false,
                            onRegisterSuccess = { message, accessToken, refreshToken, user ->
                                coroutineScope.launch {
                                    datastore.clear()
                                }
                                onSignupSuccess(message, accessToken, refreshToken, user)
                            },
                            onLoginClick = onLoginClick
                        )
                    }
                }
            }
        )

        // Progress Indicator with smooth animations
        AnimatedVisibility(
            visible = showContent,
            enter = fadeIn(
                animationSpec = tween(
                    durationMillis = 600,
                    easing = FastOutSlowInEasing
                )
            ) + slideInVertically(
                initialOffsetY = { -50 },
                animationSpec = tween(
                    durationMillis = 600,
                    easing = FastOutSlowInEasing
                )
            ),
            exit = fadeOut(animationSpec = tween(300)) +
                    slideOutVertically(
                        targetOffsetY = { -50 },
                        animationSpec = tween(300)
                    )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back Button with smooth animation
                    AnimatedVisibility(
                        visible = currentPage > 0,
                        enter = fadeIn(
                            animationSpec = tween(
                                durationMillis = 400,
                                easing = FastOutSlowInEasing
                            )
                        ) + scaleIn(
                            initialScale = 0.6f,
                            animationSpec = tween(
                                durationMillis = 400,
                                easing = FastOutSlowInEasing
                            )
                        ),
                        exit = fadeOut(animationSpec = tween(300)) +
                                scaleOut(
                                    targetScale = 0.6f,
                                    animationSpec = tween(300)
                                )
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
                                .animateContentSize(
                                    animationSpec = tween(
                                        durationMillis = 300,
                                        easing = FastOutSlowInEasing
                                    )
                                ),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    if (currentPage == 0) {
                        Spacer(modifier = Modifier.size(32.dp))
                    }

                    // Step Text with animation
                    AnimatedContent(
                        targetState = currentPage,
                        transitionSpec = {
                            fadeIn(
                                animationSpec = tween(
                                    durationMillis = 300,
                                    easing = FastOutSlowInEasing
                                )
                            ) togetherWith
                                    fadeOut(
                                        animationSpec = tween(
                                            durationMillis = 200,
                                            easing = FastOutSlowInEasing
                                        )
                                    )
                        },
                        label = "step_text"
                    ) { page ->
                        Text(
                            text = "Step ${page + 1} of 3",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }

                    // Progress Indicators with smooth animations
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.animateContentSize(
                            animationSpec = tween(
                                durationMillis = 300,
                                easing = FastOutSlowInEasing
                            )
                        )
                    ) {
                        repeat(3) { index ->
                            val isActive = index <= currentPage
                            val isCurrent = index == currentPage

                            AnimatedContent(
                                targetState = isCurrent,
                                transitionSpec = {
                                    fadeIn(
                                        animationSpec = tween(
                                            durationMillis = 200,
                                            easing = FastOutSlowInEasing
                                        )
                                    ) + scaleIn(
                                        initialScale = 0.5f,
                                        animationSpec = tween(
                                            durationMillis = 200,
                                            easing = FastOutSlowInEasing
                                        )
                                    ) togetherWith
                                            fadeOut(
                                                animationSpec = tween(
                                                    durationMillis = 150,
                                                    easing = FastOutSlowInEasing
                                                )
                                            ) + scaleOut(
                                        targetScale = 0.5f,
                                        animationSpec = tween(
                                            durationMillis = 150,
                                            easing = FastOutSlowInEasing
                                        )
                                    )
                                },
                                label = "indicator_$index"
                            ) { isCurrentIndicator ->
                                Box(
                                    modifier = Modifier
                                        .size(
                                            if (isCurrentIndicator) 24.dp else 8.dp,
                                            8.dp
                                        )
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
                                                durationMillis = 400,
                                                easing = FastOutSlowInEasing
                                            )
                                        )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}