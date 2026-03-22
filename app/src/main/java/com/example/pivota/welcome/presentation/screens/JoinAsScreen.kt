package com.example.pivota.welcome.presentation.screens

import android.annotation.SuppressLint
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
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

        // Lottie Illustration
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(animationSpec = tween(300))
        ) {
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .padding(16.dp)
            ) {
                LottieAnimation(
                    composition = composition,
                    progress = { progress },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Headline
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(animationSpec = tween(300, delayMillis = 100))
        ) {
            Text(
                text = "Joining as?",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                    color = MaterialTheme.colorScheme.primary
                ),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Cards Container
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // Individual Card
            OnboardingCard(
                isSelected = selectedType == "individual",
                onClick = {
                    if (!isLoading) {
                        selectedType = "individual"
                    }
                },
                animationDelay = 0
            ) {
                IndividualCardContent()
            }

            // Organization Card
            OnboardingCard(
                isSelected = selectedType == "organization",
                onClick = {
                    if (!isLoading) {
                        selectedType = "organization"
                    }
                },
                animationDelay = 100
            ) {
                OrganizationCardContent()
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Continue Button
        Button(
            onClick = {
                if (selectedType != null && !isLoading) {
                    isLoading = true
                    onContinue(selectedType!!)
                }
            },
            enabled = selectedType != null && !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selectedType != null) MaterialTheme.colorScheme.tertiary
                else MaterialTheme.colorScheme.outlineVariant,
                contentColor = if (selectedType != null) MaterialTheme.colorScheme.onTertiary
                else MaterialTheme.colorScheme.onSurfaceVariant
            ),
            shape = RoundedCornerShape(48.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onTertiary,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "Continue",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
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
            .clickable { onClick() }
            .shadow(
                elevation = if (isSelected) 8.dp else 4.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = if (isSelected) MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
                else Color.Black.copy(alpha = 0.08f),
                spotColor = if (isSelected) MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
                else Color.Black.copy(alpha = 0.08f)
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.secondary.copy(alpha = 0.03f)
            else MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.5.dp,
            color = if (isSelected) MaterialTheme.colorScheme.secondary
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
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.ic_person),
            contentDescription = "Individual",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(48.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "INDIVIDUAL",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.primary
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "For personal use — job seeking, offering services, finding housing, or accessing support",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun OrganizationCardContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.ic_work),
            contentDescription = "Organization",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(48.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "ORGANIZATION",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.primary
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "For companies, NGOs, government agencies, and institutions — hiring, listing properties, or providing services",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            textAlign = TextAlign.Center
        )
    }
}