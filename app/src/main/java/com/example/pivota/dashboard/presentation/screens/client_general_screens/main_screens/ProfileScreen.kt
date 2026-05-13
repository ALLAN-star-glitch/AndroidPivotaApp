package com.example.pivota.dashboard.presentation.screens.client_general_screens.main_screens

import JobSeekerTabContent
import android.annotation.SuppressLint
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.window.core.layout.WindowWidthSizeClass
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.pivota.R
import com.example.pivota.dashboard.domain.model.profile_models.AccountStatus
import com.example.pivota.dashboard.domain.model.profile_models.AccountType
import com.example.pivota.dashboard.domain.model.profile_models.AgentProfile
import com.example.pivota.dashboard.domain.model.profile_models.BeneficiaryProfile
import com.example.pivota.dashboard.domain.model.profile_models.CompleteProfile
import com.example.pivota.dashboard.domain.model.profile_models.CompletionLevel
import com.example.pivota.dashboard.domain.model.profile_models.EmployerProfile
import com.example.pivota.dashboard.domain.model.profile_models.HousingSeekerProfile
import com.example.pivota.dashboard.domain.model.profile_models.OrganizationProfile
import com.example.pivota.dashboard.domain.model.profile_models.ProfileAccount
import com.example.pivota.dashboard.domain.model.profile_models.ProfileCompletion
import com.example.pivota.dashboard.domain.model.profile_models.ProfileType
import com.example.pivota.dashboard.domain.model.profile_models.ProfileUser
import com.example.pivota.dashboard.domain.model.profile_models.IndividualProfile
import com.example.pivota.dashboard.domain.model.profile_models.JobSeekerProfile
import com.example.pivota.dashboard.domain.model.profile_models.ProfessionalProfile
import com.example.pivota.dashboard.domain.model.profile_models.PropertyOwnerProfile
import com.example.pivota.dashboard.domain.model.profile_models.UserStatus
import com.example.pivota.dashboard.presentation.composables.client_general_composables.general.ReusableHeader
import com.example.pivota.dashboard.presentation.composables.client_general_composables.profile_composables.AccountTabContent
import com.example.pivota.dashboard.presentation.composables.client_general_composables.profile_composables.AgentTabContent
import com.example.pivota.dashboard.presentation.composables.client_general_composables.profile_composables.BeneficiaryTabContent
import com.example.pivota.dashboard.presentation.composables.client_general_composables.profile_composables.EmployerTabContent
import com.example.pivota.dashboard.presentation.composables.client_general_composables.profile_composables.ProfessionalTabContent
import com.example.pivota.dashboard.presentation.composables.client_general_composables.profile_composables.PropertyOwnerTabContent
import com.example.pivota.dashboard.presentation.composables.client_general_composables.profile_composables.PropertySeekerTabContent
import com.example.pivota.dashboard.presentation.viewmodels.client_general_viewmodels.DashboardSharedViewModel
import com.example.pivota.dashboard.presentation.viewmodels.client_general_viewmodels.ProfileLoadState
import com.example.pivota.ui.theme.*



@SuppressLint("FrequentlyChangingValue")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToEditProfile: (ProfileType) -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToHelpCenter: () -> Unit = {},
    onNavigateToTeamManagement: () -> Unit = {},
    onNavigateToVerification: () -> Unit = {},
    onNavigateToSubscription: () -> Unit = {},
    onNavigateToPaymentMethods: () -> Unit = {},
    onNavigateToBillingHistory: () -> Unit = {},
    onSignOut: () -> Unit = {},
    isGuestMode: Boolean = false,
    sharedViewModel: DashboardSharedViewModel = hiltViewModel()
) {

    val profileState by sharedViewModel.profileState.collectAsState()
    val colorScheme = MaterialTheme.colorScheme

    if (isGuestMode) {
        GuestProfileScreenContent(
            onNavigateToEditProfile = onNavigateToEditProfile,
            onNavigateToSettings = onNavigateToSettings,
            onNavigateToHelpCenter = onNavigateToHelpCenter,
            onNavigateToTeamManagement = onNavigateToTeamManagement,
            onNavigateToVerification = onNavigateToVerification,
            onNavigateToSubscription = onNavigateToSubscription,
            onNavigateToPaymentMethods = onNavigateToPaymentMethods,
            onNavigateToBillingHistory = onNavigateToBillingHistory,
            onSignOut = onSignOut,
            colorScheme = colorScheme,
            sharedViewModel = sharedViewModel
        )
        return
    }

    when (profileState) {
        is ProfileLoadState.Loading -> {
            ProfileLoadingSkeleton(colorScheme = colorScheme)
        }
        is ProfileLoadState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.ErrorOutline,
                        contentDescription = null,
                        tint = colorScheme.error,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Error loading profile",
                        style = MaterialTheme.typography.titleMedium,
                        color = colorScheme.error
                    )
                    Text(
                        text = (profileState as ProfileLoadState.Error).message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { sharedViewModel.refreshProfile() },
                        colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary)
                    ) {
                        Text("Retry")
                    }
                }
            }
        }
        is ProfileLoadState.Success -> {
            val profile = (profileState as ProfileLoadState.Success).profile
            AuthenticatedProfileContent(
                profile = profile,
                onNavigateToEditProfile = onNavigateToEditProfile,
                onNavigateToSettings = onNavigateToSettings,
                onNavigateToHelpCenter = onNavigateToHelpCenter,
                onNavigateToTeamManagement = onNavigateToTeamManagement,
                onNavigateToVerification = onNavigateToVerification,
                onNavigateToSubscription = onNavigateToSubscription,
                onNavigateToPaymentMethods = onNavigateToPaymentMethods,
                onNavigateToBillingHistory = onNavigateToBillingHistory,
                onSignOut = onSignOut,
                colorScheme = colorScheme,
                sharedViewModel = sharedViewModel
            )
        }

        else -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Something went wrong")
            }
        }
    }
}

// ==================== GUEST PROFILE CONTENT ====================

@Composable
fun GuestProfileScreenContent(
    onNavigateToEditProfile: (ProfileType) -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToHelpCenter: () -> Unit,
    onNavigateToTeamManagement: () -> Unit,
    onNavigateToVerification: () -> Unit,
    onNavigateToSubscription: () -> Unit,
    onNavigateToPaymentMethods: () -> Unit,
    onNavigateToBillingHistory: () -> Unit,
    onSignOut: () -> Unit,
    colorScheme: ColorScheme,
    sharedViewModel: DashboardSharedViewModel = hiltViewModel()
) {
    val guestProfile = mockGuestProfile()
    val primaryTeal = colorScheme.primary
    val goldenAccent = colorScheme.tertiary
    val softBackground = colorScheme.background
    val purpleAccent = PurpleAccent

    val listState = rememberLazyListState()
    val scrollOffset by remember {
        derivedStateOf {
            if (listState.firstVisibleItemIndex == 0) {
                listState.firstVisibleItemScrollOffset.toFloat()
            } else {
                Float.MAX_VALUE
            }
        }
    }
    val isScrolled = scrollOffset > 20f

    Scaffold(
        containerColor = softBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 0.dp)
            ) {
                item {
                    ReusableHeader(
                        colorScheme = colorScheme,
                        pageTitle = "Profile",
                        pageSubtitle = "Sign in to access your account",
                        isGuestMode = true,
                        isSticky = false,
                        sharedViewModel = sharedViewModel,
                        scrollOffset = scrollOffset,
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    ) {
                        GuestAccountTabContent(
                            profile = guestProfile,
                            primaryColor = primaryTeal,
                            goldenAccent = goldenAccent,
                            purpleAccent = purpleAccent,
                            warningColor = WarningAmber,
                            successColor = SuccessGreen,
                            colorScheme = colorScheme,
                            onNavigateToSettings = onNavigateToSettings,
                            onNavigateToTeamManagement = onNavigateToTeamManagement,
                            onNavigateToVerification = onNavigateToVerification,
                            onNavigateToSubscription = onNavigateToSubscription,
                            onNavigateToPaymentMethods = onNavigateToPaymentMethods,
                            onNavigateToBillingHistory = onNavigateToBillingHistory,
                            onNavigateToHelpCenter = onNavigateToHelpCenter,
                            onSignOut = onSignOut,
                            onEditPersonalInfo = { onNavigateToEditProfile(ProfileType.ACCOUNT) },
                            onEditVerification = { onNavigateToEditProfile(ProfileType.ACCOUNT) }
                        )
                        Spacer(modifier = Modifier.height(40.dp))
                    }
                }
            }
        }
    }
}

// ==================== LOADING SKELETON ====================

@Composable
fun ProfileLoadingSkeleton(colorScheme: ColorScheme) {
    val softBackground = colorScheme.background
    val skeletonColor = colorScheme.surfaceVariant.copy(alpha = 0.5f)

    Scaffold(
        containerColor = softBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(skeletonColor)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .width(200.dp)
                            .height(24.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(skeletonColor)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .width(150.dp)
                            .height(16.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(skeletonColor)
                    )
                }
            }

            item {
                ProfileSectionSkeleton(skeletonColor, colorScheme)
            }

            item {
                VerificationSkeleton(skeletonColor, colorScheme)
            }

            item {
                SettingsSkeleton(skeletonColor, colorScheme)
            }
        }
    }
}

@Composable
fun ProfileSectionSkeleton(skeletonColor: Color, colorScheme: ColorScheme) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .width(120.dp)
                .height(20.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(skeletonColor)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                repeat(4) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(skeletonColor)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .width(100.dp)
                                    .height(16.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(skeletonColor)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .width(150.dp)
                                    .height(14.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(skeletonColor)
                            )
                        }
                    }
                    if (it < 3) {
                        HorizontalDivider(
                            color = colorScheme.outlineVariant,
                            modifier = Modifier.padding(start = 56.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun VerificationSkeleton(skeletonColor: Color, colorScheme: ColorScheme) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .width(150.dp)
                .height(20.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(skeletonColor)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                repeat(3) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(skeletonColor)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .width(60.dp)
                                .height(12.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(skeletonColor)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsSkeleton(skeletonColor: Color, colorScheme: ColorScheme) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .width(140.dp)
                .height(20.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(skeletonColor)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                repeat(4) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(skeletonColor)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Box(
                            modifier = Modifier
                                .width(120.dp)
                                .height(16.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(skeletonColor)
                        )
                    }
                    if (it < 3) {
                        HorizontalDivider(
                            color = colorScheme.outlineVariant,
                            modifier = Modifier.padding(start = 56.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthenticatedProfileContent(
    profile: CompleteProfile,
    onNavigateToEditProfile: (ProfileType) -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToHelpCenter: () -> Unit,
    onNavigateToTeamManagement: () -> Unit,
    onNavigateToVerification: () -> Unit,
    onNavigateToSubscription: () -> Unit,
    onNavigateToPaymentMethods: () -> Unit,
    onNavigateToBillingHistory: () -> Unit,
    onSignOut: () -> Unit,
    colorScheme: ColorScheme,
    sharedViewModel: DashboardSharedViewModel = hiltViewModel()
) {
    val configuration = LocalConfiguration.current
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val isWide = windowSizeClass.windowWidthSizeClass != WindowWidthSizeClass.COMPACT

    val primaryTeal = colorScheme.primary
    val goldenAccent = colorScheme.tertiary
    val softBackground = colorScheme.background
    val purpleAccent = PurpleAccent

    val listState = rememberLazyListState()
    val scrollOffset by remember {
        derivedStateOf {
            if (listState.firstVisibleItemIndex == 0) {
                listState.firstVisibleItemScrollOffset.toFloat()
            } else {
                Float.MAX_VALUE
            }
        }
    }
    val isScrolled = scrollOffset > 20f

    val allTabs = listOf(
        Triple(ProfileType.ACCOUNT, "Account", Icons.Default.Person),
        Triple(ProfileType.JOB_SEEKER, "Looking for Job?", Icons.Default.Search),
        Triple(ProfileType.PROFESSIONAL, "Offer Your Services?", Icons.Default.Work),
        Triple(ProfileType.AGENT, "Work as Agent?", Icons.Default.Handshake),
        Triple(ProfileType.EMPLOYER, "Hiring?", Icons.Default.Business),
        Triple(ProfileType.PROPERTY_OWNER, "List Properties?", Icons.Default.Home),
        Triple(ProfileType.HOUSING_SEEKER, "Looking for House?", Icons.Default.LocationOn),
        Triple(ProfileType.BENEFICIARY, "Need Support?", Icons.Default.Favorite)
    )

    var selectedTabIndex by remember { mutableStateOf(0) }

    Scaffold(
        containerColor = softBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 0.dp)
            ) {
                item {
                    ReusableHeader(
                        colorScheme = colorScheme,
                        pageTitle = "Profile",
                        pageSubtitle = "Manage your identity",
                        isGuestMode = false,
                        isSticky = false,
                        sharedViewModel = sharedViewModel,
                        scrollOffset = scrollOffset,
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                stickyHeader(key = "tabRow") {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(elevation = 4.dp, shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)),
                            color = softBackground,
                            tonalElevation = 2.dp
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .then(
                                        if (isScrolled) Modifier.statusBarsPadding() else Modifier
                                    )
                            ) {
                                ScrollableTabRow(
                                    selectedTabIndex = selectedTabIndex,
                                    containerColor = Color.Transparent,
                                    edgePadding = 16.dp,
                                    divider = {},
                                    indicator = { tabPositions ->
                                        TabRowDefaults.SecondaryIndicator(
                                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                                            color = primaryTeal,
                                            height = 3.dp
                                        )
                                    }
                                ) {
                                    allTabs.forEachIndexed { index, (_, title, icon) ->
                                        val selected = selectedTabIndex == index
                                        Tab(
                                            selected = selected,
                                            onClick = { selectedTabIndex = index },
                                            text = {
                                                Text(
                                                    text = title,
                                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                                    fontSize = 12.sp,
                                                    color = if (selected) primaryTeal else colorScheme.onSurfaceVariant
                                                )
                                            },
                                            icon = {
                                                Icon(
                                                    imageVector = icon,
                                                    contentDescription = title,
                                                    modifier = Modifier.size(20.dp),
                                                    tint = if (selected) primaryTeal else colorScheme.onSurfaceVariant
                                                )
                                            },
                                            selectedContentColor = primaryTeal,
                                            unselectedContentColor = colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    ) {
                        when (allTabs[selectedTabIndex].first) {
                            ProfileType.ACCOUNT -> AccountTabContent(
                                account = profile.account,
                                user = profile.user,
                                individualProfile = profile.individualProfile,
                                organizationProfile = profile.organizationProfile,
                                completion = profile.completion,
                                primaryColor = primaryTeal,
                                goldenAccent = goldenAccent,
                                purpleAccent = purpleAccent,
                                warningColor = WarningAmber,
                                successColor = SuccessGreen,
                                colorScheme = colorScheme,
                                onNavigateToSettings = onNavigateToSettings,
                                onNavigateToTeamManagement = onNavigateToTeamManagement,
                                onNavigateToVerification = onNavigateToVerification,
                                onNavigateToSubscription = onNavigateToSubscription,
                                onNavigateToPaymentMethods = onNavigateToPaymentMethods,
                                onNavigateToBillingHistory = onNavigateToBillingHistory,
                                onNavigateToHelpCenter = onNavigateToHelpCenter,
                                onSignOut = onSignOut,
                                onEditPersonalInfo = { onNavigateToEditProfile(ProfileType.ACCOUNT) },
                                onEditVerification = { onNavigateToEditProfile(ProfileType.ACCOUNT) }
                            )

                            ProfileType.JOB_SEEKER -> {
                                if (profile.jobSeekerProfile != null) {
                                    JobSeekerTabContent(
                                        jobSeekerProfileData = profile.jobSeekerProfile,
                                        primaryColor = primaryTeal,
                                        goldenAccent = goldenAccent,
                                        colorScheme = colorScheme,
                                        onEditBranding = { onNavigateToEditProfile(ProfileType.JOB_SEEKER) },
                                        onEditSkills = { onNavigateToEditProfile(ProfileType.JOB_SEEKER) },
                                        onEditIndustries = { onNavigateToEditProfile(ProfileType.JOB_SEEKER) },
                                        onEditPreferences = { onNavigateToEditProfile(ProfileType.JOB_SEEKER) },
                                        onEditWorkAuth = { onNavigateToEditProfile(ProfileType.JOB_SEEKER) },
                                        onEditCV = { onNavigateToEditProfile(ProfileType.JOB_SEEKER) },
                                    )
                                } else {
                                    EmptyProfileState(
                                        title = "Looking for Job?",
                                        description = "Create a job seeker profile to find your dream job.",
                                        lottieAsset = "lottie/job_search.json",
                                        primaryColor = primaryTeal,
                                        onCreateClick = { onNavigateToEditProfile(ProfileType.JOB_SEEKER) }
                                    )
                                }
                            }

                            ProfileType.PROFESSIONAL -> {
                                if (profile.professionalProfile != null) {
                                    ProfessionalTabContent(
                                        professionalProfile = profile.professionalProfile,
                                        primaryColor = primaryTeal,
                                        goldenAccent = goldenAccent,
                                        colorScheme = colorScheme,
                                        onEditOverview = { onNavigateToEditProfile(ProfileType.PROFESSIONAL) },
                                        onEditSpecialties = { onNavigateToEditProfile(ProfileType.PROFESSIONAL) },
                                        onEditServiceAreas = { onNavigateToEditProfile(ProfileType.PROFESSIONAL) },
                                        onEditBusinessDetails = { onNavigateToEditProfile(ProfileType.PROFESSIONAL) },
                                        onEditPortfolio = { onNavigateToEditProfile(ProfileType.PROFESSIONAL) }
                                    )
                                } else {
                                    EmptyProfileState(
                                        title = "Offer Your Services?",
                                        description = "Showcase your skills and start offering your professional services.",
                                        lottieAsset = "lottie/professional.json",
                                        primaryColor = primaryTeal,
                                        onCreateClick = { onNavigateToEditProfile(ProfileType.PROFESSIONAL) }
                                    )
                                }
                            }

                            ProfileType.AGENT -> {
                                if (profile.agentProfile != null) {
                                    AgentTabContent(
                                        agentProfile = profile.agentProfile,
                                        primaryColor = primaryTeal,
                                        goldenAccent = goldenAccent,
                                        colorScheme = colorScheme,
                                        onEditOverview = { onNavigateToEditProfile(ProfileType.AGENT) },
                                        onEditSpecializations = { onNavigateToEditProfile(ProfileType.AGENT) },
                                        onEditLicense = { onNavigateToEditProfile(ProfileType.AGENT) },
                                        onEditServiceAreas = { onNavigateToEditProfile(ProfileType.AGENT) },
                                        onEditCommission = { onNavigateToEditProfile(ProfileType.AGENT) },
                                        onEditContact = { onNavigateToEditProfile(ProfileType.AGENT) }
                                    )
                                } else {
                                    EmptyProfileState(
                                        title = "Work as Agent?",
                                        description = "Create an agent profile to help people find properties, jobs, or other services.",
                                        lottieAsset = "lottie/agent.json",
                                        primaryColor = primaryTeal,
                                        onCreateClick = { onNavigateToEditProfile(ProfileType.AGENT) }
                                    )
                                }
                            }

                            ProfileType.EMPLOYER -> {
                                if (profile.employerProfile != null) {
                                    EmployerTabContent(
                                        employerProfile = profile.employerProfile,
                                        primaryColor = primaryTeal,
                                        goldenAccent = goldenAccent,
                                        colorScheme = colorScheme,
                                        onEditOverview = { onNavigateToEditProfile(ProfileType.EMPLOYER) },
                                        onEditDescription = { onNavigateToEditProfile(ProfileType.EMPLOYER) },
                                        onEditPreferences = { onNavigateToEditProfile(ProfileType.EMPLOYER) }
                                    )
                                } else {
                                    EmptyProfileState(
                                        title = "Hiring?",
                                        description = "Post jobs and find the best talent for your company.",
                                        lottieAsset = "lottie/employer.json",
                                        primaryColor = primaryTeal,
                                        onCreateClick = { onNavigateToEditProfile(ProfileType.EMPLOYER) }
                                    )
                                }
                            }

                            ProfileType.PROPERTY_OWNER -> {
                                if (profile.propertyOwnerProfile != null) {
                                    PropertyOwnerTabContent(
                                        propertyOwnerProfile = profile.propertyOwnerProfile,
                                        primaryColor = primaryTeal,
                                        goldenAccent = goldenAccent,
                                        colorScheme = colorScheme,
                                        onEditOverview = { onNavigateToEditProfile(ProfileType.PROPERTY_OWNER) },
                                        onEditProfessionalDetails = { onNavigateToEditProfile(ProfileType.PROPERTY_OWNER) },
                                        onEditPreferences = { onNavigateToEditProfile(ProfileType.PROPERTY_OWNER) }
                                    )
                                } else {
                                    EmptyProfileState(
                                        title = "List Properties?",
                                        description = "List your properties for rent or sale.",
                                        lottieAsset = "lottie/property_owner.json",
                                        primaryColor = primaryTeal,
                                        onCreateClick = { onNavigateToEditProfile(ProfileType.PROPERTY_OWNER) }
                                    )
                                }
                            }

                            ProfileType.HOUSING_SEEKER -> {
                                if (profile.housingSeekerProfile != null) {
                                    PropertySeekerTabContent(
                                        propertySeekerProfileData = profile.housingSeekerProfile,
                                        primaryColor = primaryTeal,
                                        goldenAccent = goldenAccent,
                                        colorScheme = colorScheme,
                                        onEditHousing = { onNavigateToEditProfile(ProfileType.HOUSING_SEEKER) },
                                        onEditLocation = { onNavigateToEditProfile(ProfileType.HOUSING_SEEKER) },
                                        onEditMoveIn = { onNavigateToEditProfile(ProfileType.HOUSING_SEEKER) },
                                        onEditHousehold = { onNavigateToEditProfile(ProfileType.HOUSING_SEEKER) }
                                    )
                                } else {
                                    EmptyProfileState(
                                        title = "Looking for House?",
                                        description = "Tell us what you're looking for and find your perfect home.",
                                        lottieAsset = "lottie/housing_seeker.json",
                                        primaryColor = primaryTeal,
                                        onCreateClick = { onNavigateToEditProfile(ProfileType.HOUSING_SEEKER) }
                                    )
                                }
                            }

                            ProfileType.BENEFICIARY -> {
                                if (profile.beneficiaryProfile != null) {
                                    BeneficiaryTabContent(
                                        beneficiaryProfile = profile.beneficiaryProfile,
                                        primaryColor = primaryTeal,
                                        goldenAccent = goldenAccent,
                                        colorScheme = colorScheme,
                                        onEditNeeds = { onNavigateToEditProfile(ProfileType.BENEFICIARY) },
                                        onEditHousehold = { onNavigateToEditProfile(ProfileType.BENEFICIARY) },
                                        onEditLocation = { onNavigateToEditProfile(ProfileType.BENEFICIARY) },
                                        onEditPrivacy = { onNavigateToEditProfile(ProfileType.BENEFICIARY) }
                                    )
                                } else {
                                    EmptyProfileState(
                                        title = "Need Support?",
                                        description = "Create a profile to receive support and assistance.",
                                        lottieAsset = "lottie/beneficiary.json",
                                        primaryColor = primaryTeal,
                                        onCreateClick = { onNavigateToEditProfile(ProfileType.BENEFICIARY) }
                                    )
                                }
                            }

                            else -> {}
                        }
                        Spacer(modifier = Modifier.height(40.dp))
                    }
                }
            }
        }
    }
}


fun mockGuestProfile(): CompleteProfile {
    return CompleteProfile(
        user = ProfileUser(
            id = "",
            userCode = "",
            email = "",
            firstName = "Guest",
            lastName = "",
            fullName = "Guest User",
            phoneNumber = null,
            profileImageUrl = null,
            status = UserStatus.ACTIVE,
            role = "Guest"
        ),
        account = ProfileAccount(
            id = "",
            code = "",
            name = "Guest User",
            type = AccountType.INDIVIDUAL,
            status = AccountStatus.ACTIVE,
            isVerified = false,
            verifiedFeatures = emptyList(),
            createdAt = "",
            updatedAt = ""
        ),
        individualProfile = null,
        organizationProfile = null,
        professionalProfile = null,
        jobSeekerProfile = null,
        agentProfile = null,
        housingSeekerProfile = null,
        propertyOwnerProfile = null,
        beneficiaryProfile = null,
        employerProfile = null,
        verifications = emptyList(),
        completion = ProfileCompletion(
            accountCompleted = false,
            profileCompleted = 0,
            documentsCompleted = 0
        ),
        createdAt = "",
        updatedAt = ""
    )
}


@Composable
fun GuestAccountTabContent(
    profile: CompleteProfile,
    primaryColor: Color,
    goldenAccent: Color,
    purpleAccent: Color,
    warningColor: Color,
    successColor: Color,
    colorScheme: ColorScheme,
    onNavigateToSettings: () -> Unit,
    onNavigateToTeamManagement: () -> Unit,
    onNavigateToVerification: () -> Unit,
    onNavigateToSubscription: () -> Unit,
    onNavigateToPaymentMethods: () -> Unit,
    onNavigateToBillingHistory: () -> Unit,
    onNavigateToHelpCenter: () -> Unit,
    onSignOut: () -> Unit,
    onEditPersonalInfo: () -> Unit,
    onEditVerification: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.PersonOutline,
                contentDescription = null,
                tint = colorScheme.onSurfaceVariant,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Sign in to your account",
                style = MaterialTheme.typography.titleLarge,
                color = colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Sign in to view and manage your profile, listings, and account settings.",
                style = MaterialTheme.typography.bodyMedium,
                color = colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onSignOut,
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Sign In", color = Color.White)
            }
        }
    }
}


// ==================== HELPER COMPOSABLES ====================

@Composable
fun ProfileSection(
    title: String,
    action: @Composable (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp, start = 4.dp, end = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
            if (action != null) {
                action()
            }
        }

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column { content() }
        }
    }
}

@Composable
fun ProfileItem(
    icon: ImageVector,
    label: String,
    subtitle: String = "",
    onClick: () -> Unit,
    iconColor: Color = MaterialTheme.colorScheme.primary,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    showDivider: Boolean = true
) {
    val colorScheme = MaterialTheme.colorScheme

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(iconColor.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = label,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = textColor
                )
                if (subtitle.isNotEmpty()) {
                    Text(
                        text = subtitle,
                        fontSize = 13.sp,
                        color = colorScheme.onSurfaceVariant
                    )
                }
            }

            Icon(
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = null,
                tint = colorScheme.onSurfaceVariant
            )
        }

        if (showDivider) {
            HorizontalDivider(
                color = colorScheme.outlineVariant,
                modifier = Modifier.padding(start = 72.dp, end = 16.dp)
            )
        }
    }
}


// ==================== EMPTY PROFILE STATE ====================

@Composable
fun EmptyProfileState(
    title: String,
    description: String,
    lottieAsset: String,
    primaryColor: Color,
    onCreateClick: () -> Unit
) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.Asset(lottieAsset)
    )

    // Track if composition is ready
    val isCompositionReady = composition != null

    // Track if file is missing (after composition is determined)
    var isFileMissing by remember { mutableStateOf(false) }

    LaunchedEffect(composition) {
        if (composition == null) {
            isFileMissing = true
            println("⚠️ Lottie file not found: $lottieAsset")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Only show content when composition is ready
        if (isCompositionReady) {
            LottieAnimation(
                composition = composition,
                iterations = LottieConstants.IterateForever,
                modifier = Modifier.size(200.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onCreateClick,
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Create Profile", color = Color.White)
            }
        } else if (isFileMissing) {
            // Only show fallback if file is confirmed missing (not while loading)
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
                    .background(primaryColor.copy(alpha = 0.05f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Animation,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = primaryColor.copy(alpha = 0.5f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onCreateClick,
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Create Profile", color = Color.White)
            }
        }
        // else: Show NOTHING (empty Column) while loading
    }
}