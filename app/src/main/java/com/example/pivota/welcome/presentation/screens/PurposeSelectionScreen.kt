package com.example.pivota.welcome.presentation.screens

import android.annotation.SuppressLint
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.window.core.layout.WindowSizeClass
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.pivota.R
import com.example.pivota.core.presentations.composables.buttons.AuthGoogleButton
import com.example.pivota.core.presentations.composables.buttons.PivotaPrimaryButton
import com.example.pivota.core.presentations.composables.buttons.PivotaSkipButton
import com.example.pivota.welcome.presentation.composables.purpose_selection.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pivota.welcome.presentation.viewmodel.PurposeSelectionViewModel

@SuppressLint("ConfigurationScreenWidthHeight")
@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AdaptivePurposeSelectionScreenContent(
    onContinue: () -> Unit,  // Changed: no parameters, ViewModel handles caching
    onSkipToDashboard: () -> Unit,
    onContinueWithGoogle: () -> Unit,
    onJustExploring: () -> Unit,
    currentStep: Int = 2,
    totalSteps: Int = 6,
    modifier: Modifier = Modifier,
    viewModel: PurposeSelectionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val isMediumScreen = windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)
    val isExpandedScreen = windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)

    when {
        /* TWO-PANE LAYOUT FOR TABLETS/DESKTOP */
        isMediumScreen || isExpandedScreen -> {
            Row(modifier = modifier.fillMaxSize()) {
                // Left pane with selection card and skip button
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    TwoPanePurposeSelectionLeftContent(
                        viewModel = viewModel,
                        onSkipToDashboard = onSkipToDashboard
                    )
                }

                // Right pane with dynamic fields and other buttons
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                ) {
                    TwoPanePurposeSelectionRightContent(
                        viewModel = viewModel,
                        onContinue = onContinue,
                        onContinueWithGoogle = onContinueWithGoogle,
                        onJustExploring = onJustExploring
                    )
                }
            }
        }

        /* SINGLE-PANE LAYOUT FOR MOBILE */
        else -> {
            PurposeSelectionScreenContent(
                viewModel = viewModel,
                onContinue = onContinue,
                onSkipToDashboard = onSkipToDashboard,
                onContinueWithGoogle = onContinueWithGoogle,
                onJustExploring = onJustExploring,
                currentStep = currentStep,
                totalSteps = totalSteps,
                modifier = modifier
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TwoPanePurposeSelectionLeftContent(
    viewModel: PurposeSelectionViewModel,
    onSkipToDashboard: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val colorScheme = MaterialTheme.colorScheme
    val scrollState = rememberScrollState()

    // Purpose options
    val purposeOptions = listOf(
        PurposeOption("Just Exploring", Icons.Default.Explore, "✨", "Explore what Pivota has to offer before deciding"),
        PurposeOption("Find a Job", Icons.Default.Work, "🔍", "Search and apply for jobs that match your skills"),
        PurposeOption("Find Housing", Icons.Default.Home, "🏠", "Discover rental properties, apartments, and houses"),
        PurposeOption("Get Social Support", Icons.Default.Favorite, "❤️", "Access community support services and resources"),
        PurposeOption("List Properties", Icons.Default.House, "📋", "Rent out your properties to qualified tenants"),
        PurposeOption("Hire Employees", Icons.Default.Business, "👔", "Find talented professionals for your business"),
        PurposeOption("Offer Skilled Services", Icons.Default.Build, "🔧", "Showcase your expertise and get hired by clients"),
        PurposeOption("Work as Agent", Icons.Default.Person, "🤝", "Help others find opportunities and earn commissions"),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(48.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Choose Your Main Goal",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                color = MaterialTheme.colorScheme.primary
            ),
            textAlign = TextAlign.Start
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Select your primary focus to get personalized recommendations",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            textAlign = TextAlign.Start
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Professional Selection Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { viewModel.showBottomSheet() },
            shape = RoundedCornerShape(16.dp),
            color = colorScheme.surface,
            tonalElevation = 2.dp,
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 18.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (uiState.selectedPurpose != null)
                                    colorScheme.primary.copy(alpha = 0.1f)
                                else
                                    colorScheme.surfaceVariant
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (uiState.selectedPurpose != null) {
                                purposeOptions.find { it.label == uiState.selectedPurpose }?.icon ?: Icons.Default.Info
                            } else Icons.Default.Info,
                            contentDescription = null,
                            tint = if (uiState.selectedPurpose != null)
                                colorScheme.primary
                            else
                                colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Column {
                        Text(
                            text = if (uiState.selectedPurpose != null) "Selected Purpose" else "Select Your Purpose",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 12.sp,
                                color = colorScheme.onSurfaceVariant,
                                letterSpacing = 0.5.sp
                            )
                        )
                        Text(
                            text = uiState.selectedPurpose ?: "Choose your primary focus",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = if (uiState.selectedPurpose != null) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (uiState.selectedPurpose != null)
                                    colorScheme.primary
                                else
                                    colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }

                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "Select purpose",
                    tint = colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Text(
            text = "You can add more roles from your dashboard later",
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            textAlign = TextAlign.End
        )

        Spacer(modifier = Modifier.height(32.dp))

        PivotaSkipButton(
            text = "Skip to Dashboard",
            onClick = onSkipToDashboard,
            modifier = Modifier.fillMaxWidth(),
            icon = ImageVector.vectorResource(R.drawable.ic_skip)
        )
    }

    // Bottom Sheet
    if (uiState.showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.hideBottomSheet() },
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            containerColor = colorScheme.surface,
            tonalElevation = 8.dp,
            dragHandle = { BottomSheetDefaults.DragHandle(color = colorScheme.outlineVariant) }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Select Your Purpose",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onSurface
                        )
                    )

                    IconButton(
                        onClick = { viewModel.hideBottomSheet() },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = colorScheme.outlineVariant.copy(0.5f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Outlined.Close,
                                    contentDescription = "Close",
                                    tint = colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                Text(
                    text = "Choose the primary way you'll use Pivota",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                purposeOptions.forEach { option ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.selectPurpose(option.label)
                            }
                            .padding(vertical = 4.dp),
                        color = if (uiState.selectedPurpose == option.label)
                            colorScheme.primary.copy(alpha = 0.08f)
                        else
                            Color.Transparent,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(
                                        if (uiState.selectedPurpose == option.label)
                                            colorScheme.primary.copy(alpha = 0.15f)
                                        else
                                            colorScheme.surfaceVariant
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = option.icon,
                                    contentDescription = null,
                                    tint = if (uiState.selectedPurpose == option.label)
                                        colorScheme.primary
                                    else
                                        colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = option.label,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = if (uiState.selectedPurpose == option.label)
                                            FontWeight.Bold
                                        else
                                            FontWeight.SemiBold,
                                        color = if (uiState.selectedPurpose == option.label)
                                            colorScheme.primary
                                        else
                                            colorScheme.onSurface
                                    )
                                )
                                Text(
                                    text = option.description,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = colorScheme.onSurfaceVariant
                                    ),
                                    maxLines = 2
                                )
                            }

                            if (uiState.selectedPurpose == option.label) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Selected",
                                    tint = colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun TwoPanePurposeSelectionRightContent(
    viewModel: PurposeSelectionViewModel,
    onContinue: () -> Unit,
    onContinueWithGoogle: () -> Unit,
    onJustExploring: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // Lottie animation for empty state
    val emptyStateComposition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.purpose_empty_state)
    )
    val emptyStateProgress by animateLottieCompositionAsState(
        composition = emptyStateComposition,
        iterations = LottieConstants.IterateForever,
        isPlaying = uiState.selectedPurpose == null
    )

    val scrollState = rememberScrollState()
    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(48.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (uiState.selectedPurpose == null) {
            // Empty State
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                LottieAnimation(
                    composition = emptyStateComposition,
                    progress = { emptyStateProgress },
                    modifier = Modifier.size(200.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No purpose selected",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Select a purpose from the left panel\nto see the details here",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    textAlign = TextAlign.Center
                )
            }
        } else {
            // Dynamic Fields based on selection
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AnimatedContent(
                    targetState = uiState.selectedPurpose,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(200)) +
                                slideInVertically(initialOffsetY = { 20 }) togetherWith
                                fadeOut(animationSpec = tween(150)) +
                                slideOutVertically(targetOffsetY = { -20 })
                    }
                ) { purpose ->
                    when (purpose) {
                        "Find a Job" -> JobSeekerFields(
                            data = uiState.jobSeekerData,
                            onDataChange = { viewModel.updateJobSeekerData(it) }
                        )
                        "Offer Skilled Services" -> SkilledProfessionalFields(
                            data = uiState.skilledProfessionalData,
                            onDataChange = { viewModel.updateSkilledProfessionalData(it) }
                        )
                        "Work as Agent" -> AgentFields(
                            data = uiState.agentData,
                            onDataChange = { viewModel.updateAgentData(it) }
                        )
                        "Find Housing" -> HousingSeekerFields(
                            data = uiState.housingSeekerData,
                            onDataChange = { viewModel.updateHousingSeekerData(it) }
                        )
                        "Get Social Support" -> SupportBeneficiaryFields(
                            data = uiState.supportBeneficiaryData,
                            onDataChange = { viewModel.updateSupportBeneficiaryData(it) }
                        )
                        "Hire Employees" -> EmployerFields(
                            data = uiState.employerData,
                            onDataChange = { viewModel.updateEmployerData(it) }
                        )
                        "List Properties" -> PropertyOwnerFields(
                            data = uiState.propertyOwnerData,
                            onDataChange = { viewModel.updatePropertyOwnerData(it) }
                        )
                        "Just Exploring" -> JustExploringMessage()
                        else -> Spacer(modifier = Modifier.height(0.dp))
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                PivotaPrimaryButton(
                    text = if (uiState.selectedPurpose == "Just Exploring") "Start Exploring" else "Continue",
                    onClick = {
                        if (!uiState.isLoading) {
                            viewModel.confirmSelection()
                            // ALWAYS navigate to the next screen
                            onContinue()
                        }
                    },
                    enabled = viewModel.canProceed() && !uiState.isLoading,
                    modifier = Modifier.fillMaxWidth(),
                    icon = if (uiState.selectedPurpose == "Just Exploring")
                        ImageVector.vectorResource(R.drawable.ic_explore)
                    else
                        ImageVector.vectorResource(R.drawable.ic_person)
                )

                Spacer(modifier = Modifier.height(16.dp))

                AuthGoogleButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onContinueWithGoogle
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.surfaceVariant
                    )
                    Text(
                        text = " OR ",
                        modifier = Modifier.padding(horizontal = 12.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.surfaceVariant
                    )
                }

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

                Text(
                    text = "You can always add more roles from dashboard",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PurposeSelectionScreenContent(
    viewModel: PurposeSelectionViewModel,
    onContinue: () -> Unit,
    onSkipToDashboard: () -> Unit,
    onContinueWithGoogle: () -> Unit,
    onJustExploring: () -> Unit,
    currentStep: Int = 2,
    totalSteps: Int = 6,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    // Lottie animation for empty state
    val emptyStateComposition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.purpose_empty_state)
    )
    val emptyStateProgress by animateLottieCompositionAsState(
        composition = emptyStateComposition,
        iterations = LottieConstants.IterateForever,
        isPlaying = uiState.selectedPurpose == null
    )

    val scrollState = rememberScrollState()
    val colorScheme = MaterialTheme.colorScheme

    val purposeOptions = listOf(
        PurposeOption("Just Exploring", Icons.Default.Explore, "✨", "Explore what Pivota has to offer before deciding"),
        PurposeOption("Find a Job", Icons.Default.Work, "🔍", "Search and apply for jobs that match your skills"),
        PurposeOption("Find Housing", Icons.Default.Home, "🏠", "Discover rental properties, apartments, and houses"),
        PurposeOption("Get Social Support", Icons.Default.Favorite, "❤️", "Access community support services and resources"),
        PurposeOption("List Properties", Icons.Default.House, "📋", "Rent out your properties to qualified tenants"),
        PurposeOption("Hire Employees", Icons.Default.Business, "👔", "Find talented professionals for your business"),
        PurposeOption("Offer Skilled Services", Icons.Default.Build, "🔧", "Showcase your expertise and get hired by clients"),
        PurposeOption("Work as Agent", Icons.Default.Person, "🤝", "Help others find opportunities and earn commissions"),
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp)
                .padding(top = 56.dp, bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "What's your main goal?",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    color = MaterialTheme.colorScheme.primary
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Choose your primary focus—you can add more roles later",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Professional Selection Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.showBottomSheet() },
                shape = RoundedCornerShape(16.dp),
                color = colorScheme.surface,
                tonalElevation = 2.dp,
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (uiState.selectedPurpose != null)
                                        colorScheme.primary.copy(alpha = 0.1f)
                                    else
                                        colorScheme.surfaceVariant
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (uiState.selectedPurpose != null) {
                                    purposeOptions.find { it.label == uiState.selectedPurpose }?.icon ?: Icons.Default.Info
                                } else Icons.Default.Info,
                                contentDescription = null,
                                tint = if (uiState.selectedPurpose != null)
                                    colorScheme.primary
                                else
                                    colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Column {
                            Text(
                                text = if (uiState.selectedPurpose != null) "Selected Purpose" else "Select Your Purpose",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 12.sp,
                                    color = colorScheme.onSurfaceVariant,
                                    letterSpacing = 0.5.sp
                                )
                            )
                            Text(
                                text = uiState.selectedPurpose ?: "Choose your primary focus",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = if (uiState.selectedPurpose != null) FontWeight.SemiBold else FontWeight.Normal,
                                    color = if (uiState.selectedPurpose != null)
                                        colorScheme.primary
                                    else
                                        colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "Select purpose",
                        tint = colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Text(
                text = "You can add more roles from your dashboard later",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                textAlign = TextAlign.End
            )

            // Lottie Animation for empty state
            AnimatedVisibility(
                visible = uiState.selectedPurpose == null,
                enter = fadeIn(animationSpec = tween(300)) + scaleIn(initialScale = 0.8f),
                exit = fadeOut(animationSpec = tween(200)) + scaleOut(targetScale = 0.8f)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    LottieAnimation(
                        composition = emptyStateComposition,
                        progress = { emptyStateProgress },
                        modifier = Modifier.size(100.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Select a purpose to see details",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Dynamic Fields
            AnimatedContent(
                targetState = uiState.selectedPurpose,
                transitionSpec = {
                    fadeIn(animationSpec = tween(200)) +
                            slideInVertically(initialOffsetY = { 20 }) togetherWith
                            fadeOut(animationSpec = tween(150)) +
                            slideOutVertically(targetOffsetY = { -20 })
                }
            ) { purpose ->
                when (purpose) {
                    "Find a Job" -> JobSeekerFields(
                        data = uiState.jobSeekerData,
                        onDataChange = { viewModel.updateJobSeekerData(it) }
                    )
                    "Offer Skilled Services" -> SkilledProfessionalFields(
                        data = uiState.skilledProfessionalData,
                        onDataChange = { viewModel.updateSkilledProfessionalData(it) }
                    )
                    "Work as Agent" -> AgentFields(
                        data = uiState.agentData,
                        onDataChange = { viewModel.updateAgentData(it) }
                    )
                    "Find Housing" -> HousingSeekerFields(
                        data = uiState.housingSeekerData,
                        onDataChange = { viewModel.updateHousingSeekerData(it) }
                    )
                    "Get Social Support" -> SupportBeneficiaryFields(
                        data = uiState.supportBeneficiaryData,
                        onDataChange = { viewModel.updateSupportBeneficiaryData(it) }
                    )
                    "Hire Employees" -> EmployerFields(
                        data = uiState.employerData,
                        onDataChange = { viewModel.updateEmployerData(it) }
                    )
                    "List Properties" -> PropertyOwnerFields(
                        data = uiState.propertyOwnerData,
                        onDataChange = { viewModel.updatePropertyOwnerData(it) }
                    )
                    "Just Exploring" -> JustExploringMessage()
                    else -> Spacer(modifier = Modifier.height(0.dp))
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            PivotaPrimaryButton(
                text = if (uiState.selectedPurpose == "Just Exploring") "Start Exploring" else "Continue",
                onClick = {
                    if (uiState.selectedPurpose != null && !uiState.isLoading) {
                        viewModel.confirmSelection()
                        // ALWAYS navigate to the next screen
                        onContinue()
                    }
                },
                enabled = viewModel.canProceed() && !uiState.isLoading,
                modifier = Modifier.fillMaxWidth(),
                icon = if (uiState.selectedPurpose == "Just Exploring")
                    ImageVector.vectorResource(R.drawable.ic_explore)
                else
                    ImageVector.vectorResource(R.drawable.ic_person)
            )

            Spacer(modifier = Modifier.height(16.dp))

            AuthGoogleButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = onContinueWithGoogle
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.surfaceVariant
                )
                Text(
                    text = " OR ",
                    modifier = Modifier.padding(horizontal = 12.dp),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.surfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

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

            Text(
                text = "You can always add more roles from dashboard",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                textAlign = TextAlign.Center
            )
        }
    }

    // Bottom Sheet
    if (uiState.showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.hideBottomSheet() },
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            containerColor = colorScheme.surface,
            tonalElevation = 8.dp,
            dragHandle = { BottomSheetDefaults.DragHandle(color = colorScheme.outlineVariant) }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Select Your Purpose",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onSurface
                        )
                    )

                    IconButton(
                        onClick = { viewModel.hideBottomSheet() },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = colorScheme.outlineVariant.copy(0.5f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Outlined.Close,
                                    contentDescription = "Close",
                                    tint = colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                Text(
                    text = "Choose the primary way you'll use Pivota",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                purposeOptions.forEach { option ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.selectPurpose(option.label)
                            }
                            .padding(vertical = 4.dp),
                        color = if (uiState.selectedPurpose == option.label)
                            colorScheme.primary.copy(alpha = 0.08f)
                        else
                            Color.Transparent,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(
                                        if (uiState.selectedPurpose == option.label)
                                            colorScheme.primary.copy(alpha = 0.15f)
                                        else
                                            colorScheme.surfaceVariant
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = option.icon,
                                    contentDescription = null,
                                    tint = if (uiState.selectedPurpose == option.label)
                                        colorScheme.primary
                                    else
                                        colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = option.label,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = if (uiState.selectedPurpose == option.label)
                                            FontWeight.Bold
                                        else
                                            FontWeight.SemiBold,
                                        color = if (uiState.selectedPurpose == option.label)
                                            colorScheme.primary
                                        else
                                            colorScheme.onSurface
                                    )
                                )
                                Text(
                                    text = option.description,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = colorScheme.onSurfaceVariant
                                    ),
                                    maxLines = 2
                                )
                            }

                            if (uiState.selectedPurpose == option.label) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Selected",
                                    tint = colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// Updated PurposeOption with description
data class PurposeOption(
    val label: String,
    val icon: ImageVector,
    val emoji: String,
    val description: String
)

// Data classes for dynamic fields
data class JobSeekerData(
    var headline: String = "",
    var isActivelySeeking: Boolean = true,
    var skills: String = "",
    var industries: String = "",
    var jobTypes: String = "",
    var seniorityLevel: String = "",
    var expectedSalary: String = "",
    var noticePeriod: String = "",
    var workAuthorization: String = ""
)

data class SkilledProfessionalData(
    var profession: String = "",
    var otherProfession: String = "",
    var specialties: String = "",
    var yearsExperience: String = "",
    var serviceAreas: String = "",
    var hourlyRate: String = "",
    var licenseNumber: String = ""
)

data class AgentData(
    var agentType: String = "",
    var specializations: String = "",
    var serviceAreas: String = "",
    var commissionRate: String = "",
    var licenseNumber: String = ""
)

data class HousingSeekerData(
    var propertyType: String = "",
    var minBedrooms: String = "",
    var maxBedrooms: String = "",
    var minBudget: String = "",
    var maxBudget: String = "",
    var preferredAreas: String = "",
    val listingTypes: List<String> = emptyList(),
)

data class SupportBeneficiaryData(
    var supportTypes: List<String> = emptyList(),
    var urgentNeeds: String = "",
    var location: String = "",
    var familySize: String = ""
)

data class EmployerData(
    var businessName: String = "",
    var industrySector: String = "",
    var companySize: String = "",
    var preferredSkills: String = "",
    var otherIndustry: String = ""
)

data class PropertyOwnerData(
    var professionalStatus: String = "",
    var propertyCount: String = "",
    var propertyTypes: String = "",
    var serviceAreas: String = ""
)

@Composable
fun JustExploringMessage() {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.exploring_animation)
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        isPlaying = true
    )

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "No additional details needed!",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "You can complete your profile later from your dashboard. Start browsing to discover opportunities that matter to you.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

// Extension functions to convert data to Map
fun JobSeekerData.toMap() = mapOf(
    "skills" to skills,
    "expectedSalary" to expectedSalary,
)

fun SkilledProfessionalData.toMap() = mapOf(
    "profession" to if (profession == "Other") otherProfession else profession,
    "specialties" to specialties,
    "yearsExperience" to yearsExperience,
    "serviceAreas" to serviceAreas,
    "hourlyRate" to hourlyRate,
    "licenseNumber" to licenseNumber
)

fun AgentData.toMap() = mapOf(
    "agentType" to agentType,
    "specializations" to specializations,
    "serviceAreas" to serviceAreas,
    "commissionRate" to commissionRate,
    "licenseNumber" to licenseNumber
)

fun HousingSeekerData.toMap() = mapOf(
    "propertyType" to propertyType,
    "minBedrooms" to minBedrooms,
    "maxBedrooms" to maxBedrooms,
    "minBudget" to minBudget,
    "maxBudget" to maxBudget,
    "preferredAreas" to preferredAreas
)

fun SupportBeneficiaryData.toMap() = mapOf(
    "supportTypes" to supportTypes,
    "urgentNeeds" to urgentNeeds,
    "location" to location,
    "familySize" to familySize
)

fun EmployerData.toMap() = mapOf(
    "businessName" to businessName,
    "industrySector" to industrySector,
    "companySize" to companySize,
    "preferredSkills" to preferredSkills
)

fun PropertyOwnerData.toMap() = mapOf(
    "professionalStatus" to professionalStatus,
    "propertyCount" to propertyCount,
    "propertyTypes" to propertyTypes,
    "serviceAreas" to serviceAreas
)