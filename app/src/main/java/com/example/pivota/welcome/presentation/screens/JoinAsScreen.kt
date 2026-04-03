package com.example.pivota.welcome.presentation.screens

import android.annotation.SuppressLint
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.window.core.layout.WindowSizeClass
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import com.airbnb.lottie.compose.*
import com.example.pivota.R
import com.example.pivota.core.presentations.composables.buttons.PivotaPrimaryButton
import com.example.pivota.core.presentations.composables.buttons.PivotaSkipButton
import com.example.pivota.welcome.presentation.viewmodel.JoiningAsViewModel
import androidx.hilt.navigation.compose.hiltViewModel

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun AdaptiveJoiningAsScreenContent(
    modifier: Modifier = Modifier,
    onContinue: () -> Unit,  // Changed: no parameter needed
    onLoginClick: () -> Unit,
    onSkipToDashboard: () -> Unit,
    currentStep: Int = 0,
    totalSteps: Int = 3,
    viewModel: JoiningAsViewModel = hiltViewModel()  // Inject ViewModel
) {
    val windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val isMediumScreen = windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)
    val isExpandedScreen = windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)

    when {
        /* TWO-PANE LAYOUT FOR TABLETS/DESKTOP */
        isMediumScreen || isExpandedScreen -> {
            Row(modifier = modifier.fillMaxSize()) {
                // Left pane with illustration
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
                ) {
                    TwoPaneJoiningAsLeftContent()
                }

                // Right pane with content
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    TwoPaneJoiningAsRightContent(
                        viewModel = viewModel,
                        onContinue = onContinue,
                        onLoginClick = onLoginClick,
                        onSkipToDashboard = onSkipToDashboard,
                        currentStep = currentStep,
                        totalSteps = totalSteps
                    )
                }
            }
        }

        /* SINGLE-PANE LAYOUT FOR MOBILE */
        else -> {
            JoiningAsScreenContent(
                viewModel = viewModel,
                onContinue = onContinue,
                onLoginClick = onLoginClick,
                onSkipToDashboard = onSkipToDashboard,
                currentStep = currentStep,
                totalSteps = totalSteps,
                modifier = modifier
            )
        }
    }
}

@Composable
fun TwoPaneJoiningAsLeftContent() {
    // Lottie animation for tablet/desktop
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.illustration_two_paths)
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        isPlaying = true
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.size(300.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Choose Your Path",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.primary
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Join as an individual or organization to access tailored opportunities",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun TwoPaneJoiningAsRightContent(
    viewModel: JoiningAsViewModel,
    onContinue: () -> Unit,
    onLoginClick: () -> Unit,
    onSkipToDashboard: () -> Unit,
    currentStep: Int = 0,
    totalSteps: Int = 3
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 48.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Step indicator
        Text(
            text = "Step ${currentStep + 1} of $totalSteps",
            style = MaterialTheme.typography.labelMedium.copy(
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Headline
        Text(
            text = "Joining as?",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                color = MaterialTheme.colorScheme.primary
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Join us as an organization or an individual with just 3 steps",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Cards Container
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // Individual Card
            OnboardingCard(
                isSelected = uiState.selectedAccountType == "individual",
                onClick = {
                    if (!uiState.isLoading) {
                        viewModel.selectAccountType("individual")
                    }
                },
                animationDelay = 0,
                isEnabled = true
            ) {
                IndividualCardContent(isEnabled = true)
            }

            // Organization Card
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                OnboardingCard(
                    isSelected = uiState.selectedAccountType == "organization",
                    onClick = { /* Disabled */ },
                    animationDelay = 100,
                    isEnabled = false
                ) {
                    OrganizationCardContent(isEnabled = false)
                }

                // Badge overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    contentAlignment = Alignment.TopEnd
                ) {
                    Surface(
                        modifier = Modifier.wrapContentSize(),
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFE0E0E0).copy(alpha = 0.9f),
                        shadowElevation = 2.dp
                    ) {
                        Text(
                            text = "COMING SOON",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                letterSpacing = 0.5.sp,
                                color = Color(0xFF666666)
                            ),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Continue Button
        PivotaPrimaryButton(
            text = "Continue",
            onClick = {
                if (uiState.selectedAccountType != null && !uiState.isLoading) {
                    viewModel.confirmAccountType()  // Cache the selection
                    onContinue()  // Navigate to next screen
                }
            },
            enabled = uiState.selectedAccountType != null && !uiState.isLoading,
            modifier = Modifier.fillMaxWidth(),
            icon = ImageVector.vectorResource(R.drawable.ic_skip)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Skip to Dashboard Button
        PivotaSkipButton(
            text = "Skip to Dashboard",
            onClick = onSkipToDashboard,
            modifier = Modifier.fillMaxWidth(),
            icon = ImageVector.vectorResource(R.drawable.ic_skip)
        )

        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
                    .clickable(enabled = false) { },
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Login Link
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Already have an account? ",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            Text(
                text = "Log in",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.secondary
                ),
                modifier = Modifier.clickable { onLoginClick() }
            )
        }
    }
}

@Composable
fun JoiningAsScreenContent(
    viewModel: JoiningAsViewModel,
    onContinue: () -> Unit,
    onLoginClick: () -> Unit,
    onSkipToDashboard: () -> Unit,
    currentStep: Int = 0,
    totalSteps: Int = 3,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    // Lottie animation
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.illustration_two_paths)
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        isPlaying = true
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
            .padding(top = 48.dp, bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(56.dp))

        // Lottie Illustration
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(animationSpec = tween(300))
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .padding(8.dp)
            ) {
                LottieAnimation(
                    composition = composition,
                    progress = { progress },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Headline
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(animationSpec = tween(300, delayMillis = 100))
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Joining as?",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 26.sp,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Join us as an organization or an individual with just 3 steps",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Cards Container
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // Individual Card
            OnboardingCard(
                isSelected = uiState.selectedAccountType == "individual",
                onClick = {
                    if (!uiState.isLoading) {
                        viewModel.selectAccountType("individual")
                    }
                },
                animationDelay = 0,
                isEnabled = true
            ) {
                IndividualCardContent(isEnabled = true)
            }

            // Organization Card
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                OnboardingCard(
                    isSelected = uiState.selectedAccountType == "organization",
                    onClick = { /* Disabled */ },
                    animationDelay = 100,
                    isEnabled = false
                ) {
                    OrganizationCardContent(isEnabled = false)
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    contentAlignment = Alignment.TopEnd
                ) {
                    Surface(
                        modifier = Modifier.wrapContentSize(),
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFE0E0E0).copy(alpha = 0.9f),
                        shadowElevation = 2.dp
                    ) {
                        Text(
                            text = "COMING SOON",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                letterSpacing = 0.5.sp,
                                color = Color(0xFF666666)
                            ),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Continue Button
        PivotaPrimaryButton(
            text = "Continue",
            onClick = {
                if (uiState.selectedAccountType != null && !uiState.isLoading) {
                    viewModel.confirmAccountType()
                    onContinue()
                }
            },
            enabled = uiState.selectedAccountType != null && !uiState.isLoading,
            modifier = Modifier.fillMaxWidth(),
            icon = ImageVector.vectorResource(R.drawable.ic_skip)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Skip to Dashboard Button
        PivotaSkipButton(
            text = "Skip to Dashboard",
            onClick = onSkipToDashboard,
            modifier = Modifier.fillMaxWidth(),
            icon = ImageVector.vectorResource(R.drawable.ic_skip)
        )

        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
                    .clickable(enabled = false) { },
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Login Link
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Already have an account? ",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            Text(
                text = "Log in",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.secondary
                ),
                modifier = Modifier.clickable { onLoginClick() }
            )
        }
    }
}

@Composable
fun OnboardingCard(
    isSelected: Boolean,
    onClick: () -> Unit,
    animationDelay: Int,
    isEnabled: Boolean = true,
    content: @Composable () -> Unit
) {
    val offset by animateDpAsState(
        targetValue = 0.dp,
        animationSpec = tween(400, delayMillis = animationDelay, easing = FastOutSlowInEasing),
        label = "offset"
    )

    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(150, easing = FastOutSlowInEasing),
        label = "scale"
    )

    Card(
        modifier = Modifier
            .offset(y = offset)
            .scale(scale)
            .then(
                if (isEnabled) {
                    Modifier.clickable { onClick() }
                } else {
                    Modifier
                }
            )
            .shadow(
                elevation = if (isSelected && isEnabled) 8.dp else 4.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = if (isSelected && isEnabled) MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
                else Color.Black.copy(alpha = 0.08f),
                spotColor = if (isSelected && isEnabled) MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
                else Color.Black.copy(alpha = 0.08f)
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected && isEnabled) MaterialTheme.colorScheme.secondary.copy(alpha = 0.03f)
            else MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            width = if (isSelected && isEnabled) 2.dp else 1.5.dp,
            color = if (isSelected && isEnabled) MaterialTheme.colorScheme.secondary
            else MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        content()
    }
}

@Composable
fun IndividualCardContent(isEnabled: Boolean = true) {
    val alpha = if (isEnabled) 1f else 0.6f

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.ic_person),
            contentDescription = "Individual",
            tint = MaterialTheme.colorScheme.primary.copy(alpha = alpha),
            modifier = Modifier.size(32.dp)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "INDIVIDUAL",
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = alpha)
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "For personal use — job seeking, offering services, finding housing, or accessing support",
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = alpha)
            ),
            textAlign = TextAlign.Center,
            maxLines = 2
        )
    }
}

@Composable
fun OrganizationCardContent(isEnabled: Boolean = false) {
    val alpha = if (isEnabled) 1f else 0.2f

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.ic_work),
            contentDescription = "Organization",
            tint = MaterialTheme.colorScheme.primary.copy(alpha = alpha),
            modifier = Modifier.size(32.dp)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "ORGANIZATION",
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = alpha)
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "For companies, NGOs, government agencies, and institutions",
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = alpha)
            ),
            textAlign = TextAlign.Center,
            maxLines = 2
        )
    }
}