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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingPager(
    modifier: Modifier = Modifier,
    onOnboardingComplete: () -> Unit,  // For just exploring / skip
    onSignupSuccess: (String, String, String, User?) -> Unit,  // (message, accessToken, refreshToken, user)
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
                        0 -> AdaptiveJoiningAsScreenContent(
                            onContinue = { goToNextPage() },
                            onLoginClick = onLoginClick,
                            onSkipToDashboard = {
                                onOnboardingComplete()
                            },
                            currentStep = currentPage,
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
                            currentStep = currentPage,
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

        // Progress Indicator - Moved OUTSIDE of HorizontalPager
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
                AnimatedVisibility(
                    visible = currentPage > 0,
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
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                if (currentPage == 0) {
                    Spacer(modifier = Modifier.size(32.dp))
                }

                Text(
                    text = "Step ${currentPage + 1} of 3",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.animateContentSize()
                ) {
                    repeat(3) { index ->
                        val isActive = index <= currentPage
                        Box(
                            modifier = Modifier
                                .size(if (isActive && index == currentPage) 24.dp else 8.dp, 8.dp)
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
}