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
import com.airbnb.lottie.compose.*
import com.example.pivota.R
import com.example.pivota.core.presentations.composables.buttons.PivotaPrimaryButton

@Composable
fun JoiningAsScreenContent(
    onContinue: (accountType: String) -> Unit,
    onLoginClick: () -> Unit,
    currentStep: Int = 0,
    totalSteps: Int = 2,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
) {
    var selectedType by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

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
        // Spacer for header (since header is in parent pager)
        Spacer(modifier = Modifier.height(56.dp))

        // Lottie Illustration - Reduced size
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(animationSpec = tween(300))
        ) {
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .padding(12.dp)
            ) {
                LottieAnimation(
                    composition = composition,
                    progress = { progress },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Headline
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(animationSpec = tween(300, delayMillis = 100))
        ) {
            Text(
                text = "Joining as?",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    color = MaterialTheme.colorScheme.primary
                ),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Cards Container
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // Individual Card - Reduced size
            OnboardingCard(
                isSelected = selectedType == "individual",
                onClick = {
                    if (!isLoading) {
                        selectedType = "individual"
                    }
                },
                animationDelay = 0,
                isEnabled = true
            ) {
                IndividualCardContent()
            }

            // Organization Card with "Coming Soon" overlay
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                OnboardingCard(
                    isSelected = selectedType == "organization",
                    onClick = {
                        // Disabled - organization coming soon
                    },
                    animationDelay = 100,
                    isEnabled = false
                ) {
                    OrganizationCardContent()
                }

                // Overlay for coming soon - Light gray with opacity for better text visibility
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            color = Color(0xFFE0E0E0).copy(alpha = 0.85f),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .clickable(enabled = false) { },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier
                            .padding(16.dp)
                            .background(
                                color = Color(0xFFF5F5F5).copy(alpha = 0.95f),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "🚀 COMING SOON",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.primary
                            ),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Organization features in next release:",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Medium,
                                fontSize = 11.sp,
                                color = Color(0xFF666666)
                            ),
                            textAlign = TextAlign.Center
                        )

                        Column(
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            FeatureItem("• Post job listings and hire talent")
                            FeatureItem("• List properties and manage rentals")
                            FeatureItem("• Access organization analytics dashboard")
                            FeatureItem("• Team management and roles")
                            FeatureItem("• Company profile and branding")
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Get notified when available",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.primary
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Continue Button using custom PivotaPrimaryButton
        PivotaPrimaryButton(
            text = "Continue",
            onClick = {
                if (selectedType != null && !isLoading) {
                    isLoading = true
                    onContinue(selectedType!!)
                }
            },
            enabled = selectedType != null && !isLoading,
            modifier = Modifier.fillMaxWidth(),
            icon = androidx.compose.ui.graphics.vector.ImageVector.vectorResource(R.drawable.ic_skip)
        )

        // Show loading overlay if needed
        if (isLoading) {
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
fun FeatureItem(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall.copy(
            fontSize = 10.sp,
            color = Color(0xFF444444),
            lineHeight = 14.sp
        ),
        textAlign = TextAlign.Start
    )
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
                shape = RoundedCornerShape(20.dp),
                ambientColor = if (isSelected && isEnabled) MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
                else Color.Black.copy(alpha = 0.08f),
                spotColor = if (isSelected && isEnabled) MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
                else Color.Black.copy(alpha = 0.08f)
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected && isEnabled) MaterialTheme.colorScheme.secondary.copy(alpha = 0.03f)
            else MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(20.dp),
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
fun IndividualCardContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.ic_person),
            contentDescription = "Individual",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(40.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "INDIVIDUAL",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.primary
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "For personal use — job seeking, offering services, finding housing, or accessing support",
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            textAlign = TextAlign.Center,
            maxLines = 2
        )
    }
}

@Composable
fun OrganizationCardContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.ic_work),
            contentDescription = "Organization",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(40.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "ORGANIZATION",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.primary
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "For companies, NGOs, government agencies, and institutions",
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            textAlign = TextAlign.Center,
            maxLines = 2
        )
    }
}