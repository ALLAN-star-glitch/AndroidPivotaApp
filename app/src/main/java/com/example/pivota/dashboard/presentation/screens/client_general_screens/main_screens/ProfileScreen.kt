package com.example.pivota.dashboard.presentation.screens.client_general_screens.main_screens

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

// ==================== TAB 1: ACCOUNT ====================

@Composable
fun AccountTabContent(
    account: ProfileAccount,
    user: ProfileUser,
    individualProfile: IndividualProfile?,
    organizationProfile: OrganizationProfile?,
    completion: ProfileCompletion,
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
        // Account Overview
        ProfileSection(
            title = "Account Overview",
            action = {
                IconButton(onClick = onEditPersonalInfo) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = primaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        ) {
            ProfileItem(
                icon = Icons.Default.AccountCircle,
                label = "Account Code",
                subtitle = account.code,
                onClick = {},
                iconColor = primaryColor,
                showDivider = true
            )
            ProfileItem(
                icon = Icons.Default.Category,
                label = "Account Type",
                subtitle = account.type.name,
                onClick = {},
                iconColor = primaryColor,
                showDivider = true
            )
            ProfileItem(
                icon = Icons.Default.Info,
                label = "Status",
                subtitle = account.status.name,
                onClick = {},
                iconColor = when (account.status) {
                    AccountStatus.ACTIVE -> successColor
                    AccountStatus.PENDING_PAYMENT -> warningColor
                    else -> errorLight
                },
                showDivider = false
            )
            ProfileItem(
                icon = Icons.Default.CalendarToday,
                label = "Member Since",
                subtitle = account.createdAt.take(10),
                onClick = {},
                iconColor = primaryColor,
                showDivider = false
            )
        }

        // Profile Completion
        ProfileSection(
            title = "Profile Completion",
            action = {
                IconButton(onClick = onEditPersonalInfo) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = primaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                LinearProgressIndicator(
                    progress = completion.overallCompletion / 100f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = primaryColor,
                    trackColor = colorScheme.surfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${completion.overallCompletion}% Complete",
                    fontSize = 13.sp,
                    color = colorScheme.onSurfaceVariant
                )
                Text(
                    text = when (completion.completionLevel) {
                        CompletionLevel.INCOMPLETE -> "Getting started"
                        CompletionLevel.BASIC -> "Basic profile"
                        CompletionLevel.PARTIAL -> "Almost there"
                        CompletionLevel.COMPLETE -> "Complete profile"
                    },
                    fontSize = 12.sp,
                    color = colorScheme.onSurfaceVariant
                )
            }
        }

        // Personal Information
        ProfileSection(
            title = "Personal Information",
            action = {
                IconButton(onClick = onEditPersonalInfo) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = primaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        ) {
            ProfileItem(
                icon = Icons.Default.Person,
                label = "Full Name",
                subtitle = user.displayName,
                onClick = {},
                iconColor = primaryColor
            )
            ProfileItem(
                icon = Icons.Default.Email,
                label = "Email",
                subtitle = user.email,
                onClick = {},
                iconColor = primaryColor
            )
            user.phoneNumber?.let {
                ProfileItem(
                    icon = Icons.Default.Phone,
                    label = "Phone",
                    subtitle = it,
                    onClick = {},
                    iconColor = primaryColor
                )
            }
            individualProfile?.dateOfBirth?.let {
                ProfileItem(
                    icon = Icons.Default.Cake,
                    label = "Date of Birth",
                    subtitle = it,
                    onClick = {},
                    iconColor = primaryColor
                )
            }
            individualProfile?.gender?.let {
                ProfileItem(
                    icon = Icons.Default.Wc,
                    label = "Gender",
                    subtitle = it,
                    onClick = {},
                    iconColor = primaryColor
                )
            }
            individualProfile?.nationalId?.let {
                ProfileItem(
                    icon = Icons.Default.Badge,
                    label = "National ID",
                    subtitle = it,
                    onClick = {},
                    iconColor = primaryColor,
                    showDivider = false
                )
            }
        }

        if (!individualProfile?.bio.isNullOrBlank()) {
            ProfileSection(
                title = "About Me",
                action = {
                    IconButton(onClick = onEditPersonalInfo) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = primaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = individualProfile?.bio ?: "",
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = colorScheme.onSurface
                    )
                }
            }
        }

        // Organization Profile Section (if user is part of an organization)
        organizationProfile?.let { orgData ->
            ProfileSection(
                title = "Organization Information",
                action = {
                    IconButton(onClick = onEditPersonalInfo) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = primaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            ) {
                ProfileItem(
                    icon = Icons.Default.Business,
                    label = "Organization Name",
                    subtitle = orgData.name,
                    onClick = {},
                    iconColor = primaryColor
                )
                orgData.officialEmail?.let {
                    ProfileItem(
                        icon = Icons.Default.Email,
                        label = "Official Email",
                        subtitle = it,
                        onClick = {},
                        iconColor = primaryColor
                    )
                }
                orgData.officialPhone?.let {
                    ProfileItem(
                        icon = Icons.Default.Phone,
                        label = "Official Phone",
                        subtitle = it,
                        onClick = {},
                        iconColor = primaryColor
                    )
                }
                orgData.registrationNumber?.let {
                    ProfileItem(
                        icon = Icons.Default.Description,
                        label = "Registration No",
                        subtitle = it,
                        onClick = {},
                        iconColor = primaryColor
                    )
                }
                orgData.kraPin?.let {
                    ProfileItem(
                        icon = Icons.Default.Receipt,
                        label = "KRA PIN",
                        subtitle = it,
                        onClick = {},
                        iconColor = primaryColor
                    )
                }
                orgData.type?.let {
                    ProfileItem(
                        icon = Icons.Default.Category,
                        label = "Organization Type",
                        subtitle = it,
                        onClick = {},
                        iconColor = primaryColor
                    )
                }
                orgData.website?.let {
                    ProfileItem(
                        icon = Icons.Default.Language,
                        label = "Website",
                        subtitle = it,
                        onClick = {},
                        iconColor = primaryColor
                    )
                }
                orgData.physicalAddress?.let {
                    ProfileItem(
                        icon = Icons.Default.LocationOn,
                        label = "Address",
                        subtitle = it,
                        onClick = {},
                        iconColor = primaryColor,
                        showDivider = false
                    )
                }
            }

            if (!orgData.about.isNullOrBlank()) {
                ProfileSection(
                    title = "About",
                    action = {
                        IconButton(onClick = onEditPersonalInfo) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Edit",
                                tint = primaryColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = orgData.about ?: "",
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            color = colorScheme.onSurface
                        )
                    }
                }
            }

            // Team Snapshot (count only)
            if (orgData.members.isNotEmpty() || orgData.pendingInvitations.isNotEmpty()) {
                ProfileSection(
                    title = "Team",
                    action = {
                        Row {
                            IconButton(onClick = { /* Edit team */ }) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = "Edit",
                                    tint = primaryColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            TextButton(onClick = onNavigateToTeamManagement) {
                                Text("Manage", color = primaryColor)
                            }
                        }
                    }
                ) {
                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        if (orgData.members.isNotEmpty()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(primaryColor.copy(alpha = 0.1f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Group,
                                        contentDescription = null,
                                        tint = primaryColor,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(
                                        "Team Members",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = colorScheme.onSurface
                                    )
                                    Text(
                                        "${orgData.members.size} members",
                                        fontSize = 13.sp,
                                        color = colorScheme.onSurfaceVariant
                                    )
                                }
                                Spacer(modifier = Modifier.weight(1f))
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = primaryColor.copy(alpha = 0.1f)
                                ) {
                                    Text(
                                        "View",
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        fontSize = 12.sp,
                                        color = primaryColor
                                    )
                                }
                            }
                        }

                        if (orgData.pendingInvitations.isNotEmpty()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(warningColor.copy(alpha = 0.1f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.HourglassEmpty,
                                        contentDescription = null,
                                        tint = warningColor,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(
                                        "Pending Invitations",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = colorScheme.onSurface
                                    )
                                    Text(
                                        "${orgData.pendingInvitations.size} awaiting response",
                                        fontSize = 13.sp,
                                        color = colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Preferences & Settings
        ProfileSection(
            title = "Preferences & Settings",
            action = {
                IconButton(onClick = onNavigateToSettings) {
                    Icon(
                        Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = primaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        ) {
            ProfileItem(
                icon = Icons.Default.Notifications,
                label = "Notifications",
                subtitle = "Email, Push, SMS",
                onClick = onNavigateToSettings,
                iconColor = primaryColor
            )
            ProfileItem(
                icon = Icons.Default.Language,
                label = "Language",
                subtitle = "English",
                onClick = onNavigateToSettings,
                iconColor = primaryColor
            )
            ProfileItem(
                icon = Icons.Default.Palette,
                label = "Theme",
                subtitle = "System default",
                onClick = onNavigateToSettings,
                iconColor = primaryColor
            )
            ProfileItem(
                icon = Icons.Default.Lock,
                label = "Privacy & Security",
                subtitle = "2FA enabled",
                onClick = onNavigateToSettings,
                iconColor = primaryColor,
                showDivider = false
            )
        }

        // Subscription & Billing
        ProfileSection(
            title = "Subscription & Billing",
            action = {
                IconButton(onClick = onNavigateToSubscription) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = primaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        ) {
            ProfileItem(
                icon = Icons.Default.Payment,
                label = "Subscription Plan",
                subtitle = "Pro Plan • 1,500 KES/month",
                onClick = onNavigateToSubscription,
                iconColor = primaryColor
            )
            ProfileItem(
                icon = Icons.Default.CreditCard,
                label = "Payment Methods",
                subtitle = "M-Pesa • 2 cards",
                onClick = onNavigateToPaymentMethods,
                iconColor = primaryColor
            )
            ProfileItem(
                icon = Icons.Default.History,
                label = "Billing History",
                subtitle = "View past invoices",
                onClick = onNavigateToBillingHistory,
                iconColor = primaryColor,
                showDivider = false
            )
        }

        // Support & Info
        ProfileSection(title = "Support") {
            ProfileItem(
                icon = Icons.Default.HelpOutline,
                label = "Help Center",
                subtitle = "FAQs, Guides, Tutorials",
                onClick = onNavigateToHelpCenter,
                iconColor = primaryColor
            )
            ProfileItem(
                icon = Icons.Default.Info,
                label = "About PivotaConnect",
                subtitle = "Version 1.0.0 • MVP1",
                onClick = {},
                iconColor = primaryColor
            )
            ProfileItem(
                icon = Icons.Default.Description,
                label = "Terms of Service",
                subtitle = "Read our terms",
                onClick = {},
                iconColor = primaryColor
            )
            ProfileItem(
                icon = Icons.Default.PrivacyTip,
                label = "Privacy Policy",
                subtitle = "How we handle your data",
                onClick = {},
                iconColor = primaryColor,
                showDivider = false
            )
        }

        // Sign Out
        ProfileSection(title = "Account") {
            ProfileItem(
                icon = Icons.AutoMirrored.Filled.Logout,
                label = "Sign Out",
                subtitle = "",
                onClick = onSignOut,
                iconColor = errorLight,
                textColor = errorLight,
                showDivider = false
            )
        }
    }
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

// ==================== TAB 2: PROFESSIONAL ====================

@Composable
fun ProfessionalTabContent(
    professionalProfile: ProfessionalProfile,
    primaryColor: Color,
    goldenAccent: Color,
    colorScheme: ColorScheme,
    onEditOverview: () -> Unit,
    onEditSpecialties: () -> Unit,
    onEditServiceAreas: () -> Unit,
    onEditBusinessDetails: () -> Unit,
    onEditPortfolio: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Overview
        ProfileSection(
            title = "Professional Overview",
            action = {
                IconButton(onClick = onEditOverview) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = primaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                if (!professionalProfile.title.isNullOrBlank()) {
                    Text(
                        text = professionalProfile.title!!,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            "Experience",
                            fontSize = 12.sp,
                            color = colorScheme.onSurfaceVariant
                        )
                        Text(
                            "${professionalProfile.yearsExperience ?: 0}+ years",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onSurface
                        )
                    }
                    Column {
                        Text(
                            "Rating",
                            fontSize = 12.sp,
                            color = colorScheme.onSurfaceVariant
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                tint = goldenAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                String.format("%.1f", professionalProfile.averageRating),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.onSurface,
                                modifier = Modifier.padding(start = 2.dp)
                            )
                            Text(
                                " (${professionalProfile.totalReviews})",
                                fontSize = 12.sp,
                                color = colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Column {
                        Text(
                            "Jobs",
                            fontSize = 12.sp,
                            color = colorScheme.onSurfaceVariant
                        )
                        Text(
                            "${professionalProfile.completedJobs}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onSurface
                        )
                    }
                }

                if (professionalProfile.isVerified) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = SuccessGreen.copy(alpha = 0.1f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                Icons.Default.Verified,
                                contentDescription = null,
                                tint = SuccessGreen,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "Verified Professional",
                                fontSize = 12.sp,
                                color = SuccessGreen,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // Specialties
        if (professionalProfile.specialties.isNotEmpty()) {
            ProfileSection(
                title = "Specialties",
                action = {
                    IconButton(onClick = onEditSpecialties) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = primaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(professionalProfile.specialties) { specialty ->
                            AssistChip(
                                onClick = {},
                                label = { Text(specialty) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = primaryColor.copy(alpha = 0.1f)
                                )
                            )
                        }
                    }
                }
            }
        }

        // Service Areas
        if (professionalProfile.serviceAreas.isNotEmpty()) {
            ProfileSection(
                title = "Service Areas",
                action = {
                    IconButton(onClick = onEditServiceAreas) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = primaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(professionalProfile.serviceAreas) { area ->
                            AssistChip(
                                onClick = {},
                                label = { Text(area) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = goldenAccent.copy(alpha = 0.1f)
                                )
                            )
                        }
                    }
                }
            }
        }

        // Business Details
        ProfileSection(
            title = "Business Details",
            action = {
                IconButton(onClick = onEditBusinessDetails) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = primaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        ) {
            if (!professionalProfile.licenseNumber.isNullOrBlank()) {
                ProfileItem(
                    icon = Icons.Default.Badge,
                    label = "License Number",
                    subtitle = professionalProfile.licenseNumber!!,
                    onClick = {},
                    iconColor = if (professionalProfile.isVerified) SuccessGreen else primaryColor
                )
            }
            if (!professionalProfile.insuranceInfo.isNullOrBlank()) {
                ProfileItem(
                    icon = Icons.Default.Security,
                    label = "Insurance",
                    subtitle = professionalProfile.insuranceInfo!!,
                    onClick = {},
                    iconColor = primaryColor
                )
            }
            if (professionalProfile.hourlyRate != null) {
                ProfileItem(
                    icon = Icons.Default.AttachMoney,
                    label = "Hourly Rate",
                    subtitle = "KES ${professionalProfile.hourlyRate}",
                    onClick = {},
                    iconColor = primaryColor
                )
            }
            if (!professionalProfile.paymentTerms.isNullOrBlank()) {
                ProfileItem(
                    icon = Icons.Default.Payment,
                    label = "Payment Terms",
                    subtitle = professionalProfile.paymentTerms!!,
                    onClick = {},
                    iconColor = primaryColor,
                    showDivider = false
                )
            }
        }

        // Portfolio
        if (professionalProfile.portfolioImages.isNotEmpty()) {
            ProfileSection(
                title = "Portfolio",
                action = {
                    IconButton(onClick = onEditPortfolio) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = primaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            ) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    items(professionalProfile.portfolioImages.take(5)) { imageUrl ->
                        Card(
                            modifier = Modifier
                                .size(100.dp)
                                .clickable { },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(imageUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize(),
                                error = painterResource(id = R.drawable.ic_launcher_background)
                            )
                        }
                    }
                }
            }
        }
    }
}

// ==================== TAB 3: AGENT ====================

@Composable
fun AgentTabContent(
    agentProfile: AgentProfile,
    primaryColor: Color,
    goldenAccent: Color,
    colorScheme: ColorScheme,
    onEditOverview: () -> Unit,
    onEditSpecializations: () -> Unit,
    onEditLicense: () -> Unit,
    onEditServiceAreas: () -> Unit,
    onEditCommission: () -> Unit,
    onEditContact: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Overview
        ProfileSection(
            title = "Agent Overview",
            action = {
                IconButton(onClick = onEditOverview) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = primaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = agentProfile.agentType.displayName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            "Experience",
                            fontSize = 12.sp,
                            color = colorScheme.onSurfaceVariant
                        )
                        Text(
                            "${agentProfile.yearsExperience ?: 0}+ years",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onSurface
                        )
                    }
                    Column {
                        Text(
                            "Rating",
                            fontSize = 12.sp,
                            color = colorScheme.onSurfaceVariant
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                tint = goldenAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                String.format("%.1f", agentProfile.averageRating),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.onSurface,
                                modifier = Modifier.padding(start = 2.dp)
                            )
                            Text(
                                " (${agentProfile.totalReviews})",
                                fontSize = 12.sp,
                                color = colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Column {
                        Text(
                            "Deals",
                            fontSize = 12.sp,
                            color = colorScheme.onSurfaceVariant
                        )
                        Text(
                            "${agentProfile.completedDeals}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onSurface
                        )
                    }
                }

                if (agentProfile.isVerified) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = SuccessGreen.copy(alpha = 0.1f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                Icons.Default.Verified,
                                contentDescription = null,
                                tint = SuccessGreen,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "Verified Agent",
                                fontSize = 12.sp,
                                color = SuccessGreen,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // Specializations
        if (agentProfile.specializations.isNotEmpty()) {
            ProfileSection(
                title = "Specializations",
                action = {
                    IconButton(onClick = onEditSpecializations) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = primaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(agentProfile.specializations) { spec ->
                            AssistChip(
                                onClick = {},
                                label = { Text(spec) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = primaryColor.copy(alpha = 0.1f)
                                )
                            )
                        }
                    }
                }
            }
        }

        // License & Credentials
        if (!agentProfile.licenseNumber.isNullOrBlank()) {
            ProfileSection(
                title = "License & Credentials",
                action = {
                    IconButton(onClick = onEditLicense) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = primaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            ) {
                ProfileItem(
                    icon = Icons.Default.Badge,
                    label = "License Number",
                    subtitle = agentProfile.licenseNumber!!,
                    onClick = {},
                    iconColor = primaryColor
                )
                if (!agentProfile.licenseBody.isNullOrBlank()) {
                    ProfileItem(
                        icon = Icons.Default.Business,
                        label = "Issuing Body",
                        subtitle = agentProfile.licenseBody!!,
                        onClick = {},
                        iconColor = primaryColor
                    )
                }
                if (!agentProfile.agencyName.isNullOrBlank()) {
                    ProfileItem(
                        icon = Icons.Default.Apartment,
                        label = "Agency",
                        subtitle = agentProfile.agencyName!!,
                        onClick = {},
                        iconColor = primaryColor,
                        showDivider = false
                    )
                }
            }
        }

        // Service Areas
        if (agentProfile.serviceAreas.isNotEmpty()) {
            ProfileSection(
                title = "Service Areas",
                action = {
                    IconButton(onClick = onEditServiceAreas) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = primaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(agentProfile.serviceAreas) { area ->
                            AssistChip(
                                onClick = {},
                                label = { Text(area) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = goldenAccent.copy(alpha = 0.1f)
                                )
                            )
                        }
                    }
                }
            }
        }

        // Commission & Fees
        ProfileSection(
            title = "Commission & Fees",
            action = {
                IconButton(onClick = onEditCommission) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = primaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        ) {
            if (agentProfile.commissionRate != null) {
                ProfileItem(
                    icon = Icons.Default.Percent,
                    label = "Commission Rate",
                    subtitle = "${agentProfile.commissionRate}%",
                    onClick = {},
                    iconColor = primaryColor
                )
            }
            if (!agentProfile.feeStructure.isNullOrBlank()) {
                ProfileItem(
                    icon = Icons.Default.AttachMoney,
                    label = "Fee Structure",
                    subtitle = agentProfile.feeStructure!!,
                    onClick = {},
                    iconColor = primaryColor
                )
            }
            if (agentProfile.minimumFee != null) {
                ProfileItem(
                    icon = Icons.Default.Payment,
                    label = "Minimum Fee",
                    subtitle = "KES ${agentProfile.minimumFee}",
                    onClick = {},
                    iconColor = primaryColor,
                    showDivider = false
                )
            }
        }

        // Contact Info
        ProfileSection(
            title = "Contact Information",
            action = {
                IconButton(onClick = onEditContact) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = primaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        ) {
            if (!agentProfile.contactEmail.isNullOrBlank()) {
                ProfileItem(
                    icon = Icons.Default.Email,
                    label = "Email",
                    subtitle = agentProfile.contactEmail!!,
                    onClick = {},
                    iconColor = primaryColor
                )
            }
            if (!agentProfile.contactPhone.isNullOrBlank()) {
                ProfileItem(
                    icon = Icons.Default.Phone,
                    label = "Phone",
                    subtitle = agentProfile.contactPhone!!,
                    onClick = {},
                    iconColor = primaryColor
                )
            }
            if (!agentProfile.website.isNullOrBlank()) {
                ProfileItem(
                    icon = Icons.Default.Language,
                    label = "Website",
                    subtitle = agentProfile.website!!,
                    onClick = {},
                    iconColor = primaryColor,
                    showDivider = false
                )
            }
        }

        // About
        if (!agentProfile.about.isNullOrBlank()) {
            ProfileSection(
                title = "About",
                action = {
                    IconButton(onClick = onEditContact) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = primaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = agentProfile.about!!,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = colorScheme.onSurface
                    )
                }
            }
        }
    }
}

// ==================== TAB 4: EMPLOYER ====================

@Composable
fun EmployerTabContent(
    employerProfile: EmployerProfile,
    primaryColor: Color,
    goldenAccent: Color,
    colorScheme: ColorScheme,
    onEditOverview: () -> Unit,
    onEditDescription: () -> Unit,
    onEditPreferences: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Company Overview
        ProfileSection(
            title = "Company Overview",
            action = {
                IconButton(onClick = onEditOverview) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = primaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = employerProfile.displayName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (!employerProfile.industry.isNullOrBlank()) {
                        Column {
                            Text(
                                "Industry",
                                fontSize = 12.sp,
                                color = colorScheme.onSurfaceVariant
                            )
                            Text(
                                employerProfile.industry!!,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = colorScheme.onSurface
                            )
                        }
                    }
                    if (!employerProfile.companySize.isNullOrBlank()) {
                        Column {
                            Text(
                                "Size",
                                fontSize = 12.sp,
                                color = colorScheme.onSurfaceVariant
                            )
                            Text(
                                employerProfile.companySize!!,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = colorScheme.onSurface
                            )
                        }
                    }
                    if (employerProfile.foundedYear != null) {
                        Column {
                            Text(
                                "Founded",
                                fontSize = 12.sp,
                                color = colorScheme.onSurfaceVariant
                            )
                            Text(
                                "${employerProfile.foundedYear}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = colorScheme.onSurface
                            )
                        }
                    }
                }

                if (employerProfile.isVerified) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = SuccessGreen.copy(alpha = 0.1f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                Icons.Default.Verified,
                                contentDescription = null,
                                tint = SuccessGreen,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "Verified Employer",
                                fontSize = 12.sp,
                                color = SuccessGreen,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // Description
        if (!employerProfile.description.isNullOrBlank()) {
            ProfileSection(
                title = "About",
                action = {
                    IconButton(onClick = onEditDescription) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = primaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = employerProfile.description!!,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = colorScheme.onSurface
                    )
                }
            }
        }

        // Hiring Preferences
        if (employerProfile.preferredSkills.isNotEmpty()) {
            ProfileSection(
                title = "Preferred Skills",
                action = {
                    IconButton(onClick = onEditPreferences) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = primaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(employerProfile.preferredSkills) { skill ->
                            AssistChip(
                                onClick = {},
                                label = { Text(skill) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = primaryColor.copy(alpha = 0.1f)
                                )
                            )
                        }
                    }
                }
            }
        }

        // Remote Policy
        if (!employerProfile.remotePolicy.isNullOrBlank()) {
            ProfileSection(
                title = "Work Policy",
                action = {
                    IconButton(onClick = onEditPreferences) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = primaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            ) {
                ProfileItem(
                    icon = Icons.Default.Work,
                    label = "Remote Policy",
                    subtitle = employerProfile.remotePolicy!!,
                    onClick = {},
                    iconColor = primaryColor,
                    showDivider = false
                )
            }
        }
    }
}

// ==================== TAB 5: JOB SEEKER ====================

@Composable
fun JobSeekerTabContent(
    jobSeekerProfileData: JobSeekerProfile,
    primaryColor: Color,
    goldenAccent: Color,
    colorScheme: ColorScheme,
    onEditBranding: () -> Unit,
    onEditSkills: () -> Unit,
    onEditIndustries: () -> Unit,
    onEditPreferences: () -> Unit,
    onEditWorkAuth: () -> Unit,
    onEditCV: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Professional Branding
        ProfileSection(
            title = "Professional Profile",
            action = {
                IconButton(onClick = onEditBranding) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = primaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                if (!jobSeekerProfileData.headline.isNullOrBlank()) {
                    Text(
                        text = jobSeekerProfileData.headline,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (jobSeekerProfileData.isActivelySeeking)
                            SuccessGreen.copy(alpha = 0.1f)
                        else
                            colorScheme.surfaceVariant
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                Icons.Default.Accessibility,
                                contentDescription = null,
                                tint = if (jobSeekerProfileData.isActivelySeeking) SuccessGreen else colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                if (jobSeekerProfileData.isActivelySeeking) "Actively Seeking" else "Open to Opportunities",
                                fontSize = 12.sp,
                                color = if (jobSeekerProfileData.isActivelySeeking) SuccessGreen else colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // Skills
        if (jobSeekerProfileData.skills.isNotEmpty()) {
            ProfileSection(
                title = "Skills",
                action = {
                    IconButton(onClick = onEditSkills) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = primaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(jobSeekerProfileData.skills) { skill ->
                            AssistChip(
                                onClick = {},
                                label = { Text(skill) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = primaryColor.copy(alpha = 0.1f)
                                )
                            )
                        }
                    }
                }
            }
        }

        // Industries
        if (jobSeekerProfileData.industries.isNotEmpty()) {
            ProfileSection(
                title = "Industries",
                action = {
                    IconButton(onClick = onEditIndustries) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = primaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(jobSeekerProfileData.industries) { industry ->
                            AssistChip(
                                onClick = {},
                                label = { Text(industry) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = goldenAccent.copy(alpha = 0.1f)
                                )
                            )
                        }
                    }
                }
            }
        }

        // Job Preferences
        ProfileSection(
            title = "Job Preferences",
            action = {
                IconButton(onClick = onEditPreferences) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = primaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        ) {
            if (jobSeekerProfileData.jobTypes.isNotEmpty()) {
                ProfileItem(
                    icon = Icons.Default.Work,
                    label = "Job Types",
                    subtitle = jobSeekerProfileData.jobTypes.joinToString(", "),
                    onClick = {},
                    iconColor = primaryColor
                )
            }
            if (!jobSeekerProfileData.seniorityLevel.isNullOrBlank()) {
                ProfileItem(
                    icon = Icons.Default.TrendingUp,
                    label = "Seniority Level",
                    subtitle = jobSeekerProfileData.seniorityLevel,
                    onClick = {},
                    iconColor = primaryColor
                )
            }
            if (!jobSeekerProfileData.noticePeriod.isNullOrBlank()) {
                ProfileItem(
                    icon = Icons.Default.Schedule,
                    label = "Notice Period",
                    subtitle = jobSeekerProfileData.noticePeriod,
                    onClick = {},
                    iconColor = primaryColor
                )
            }
            if (jobSeekerProfileData.expectedSalary != null) {
                ProfileItem(
                    icon = Icons.Default.AttachMoney,
                    label = "Expected Salary",
                    subtitle = "KES ${jobSeekerProfileData.expectedSalary}",
                    onClick = {},
                    iconColor = primaryColor,
                    showDivider = false
                )
            }
        }

        // Work Authorization
        if (jobSeekerProfileData.workAuthorization.isNotEmpty()) {
            ProfileSection(
                title = "Work Authorization",
                action = {
                    IconButton(onClick = onEditWorkAuth) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = primaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(jobSeekerProfileData.workAuthorization) { auth ->
                            AssistChip(
                                onClick = {},
                                label = { Text(auth) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = primaryColor.copy(alpha = 0.1f)
                                )
                            )
                        }
                    }
                }
            }
        }

        // CV & Links
        ProfileSection(
            title = "CV & Professional Links",
            action = {
                IconButton(onClick = onEditCV) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = primaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        ) {
            if (!jobSeekerProfileData.cvUrl.isNullOrBlank()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Description,
                        contentDescription = null,
                        tint = primaryColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "CV/Resume",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = colorScheme.onSurface
                        )
                        if (jobSeekerProfileData.cvLastUpdated != null) {
                            Text(
                                "Updated ${jobSeekerProfileData.cvLastUpdated}",
                                fontSize = 12.sp,
                                color = colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Icon(
                        Icons.Rounded.Download,
                        contentDescription = null,
                        tint = primaryColor
                    )
                }
            }

            if (!jobSeekerProfileData.linkedInUrl.isNullOrBlank()) {
                ProfileItem(
                    icon = Icons.Default.Info,
                    label = "LinkedIn",
                    subtitle = "View Profile",
                    onClick = {},
                    iconColor = Color(0xFF0077B5)
                )
            }
            if (!jobSeekerProfileData.portfolioUrl.isNullOrBlank()) {
                ProfileItem(
                    icon = Icons.Default.Link,
                    label = "Portfolio",
                    subtitle = "View Website",
                    onClick = {},
                    iconColor = primaryColor
                )
            }
            if (!jobSeekerProfileData.githubUrl.isNullOrBlank()) {
                ProfileItem(
                    icon = Icons.Default.Code,
                    label = "GitHub",
                    subtitle = "View Profile",
                    onClick = {},
                    iconColor = Color.Black,
                    showDivider = false
                )
            }
        }
    }
}

// ==================== TAB 6: PROPERTY OWNER ====================

@Composable
fun PropertyOwnerTabContent(
    propertyOwnerProfile: PropertyOwnerProfile,
    primaryColor: Color,
    goldenAccent: Color,
    colorScheme: ColorScheme,
    onEditOverview: () -> Unit,
    onEditProfessionalDetails: () -> Unit,
    onEditPreferences: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Overview
        ProfileSection(
            title = "Property Owner Overview",
            action = {
                IconButton(onClick = onEditOverview) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = primaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            "Type",
                            fontSize = 12.sp,
                            color = colorScheme.onSurfaceVariant
                        )
                        Text(
                            if (propertyOwnerProfile.isProfessional) "Professional" else "Individual Owner",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onSurface
                        )
                    }
                    if (propertyOwnerProfile.yearsInBusiness != null) {
                        Column {
                            Text(
                                "Years in Business",
                                fontSize = 12.sp,
                                color = colorScheme.onSurfaceVariant
                            )
                            Text(
                                "${propertyOwnerProfile.yearsInBusiness}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.onSurface
                            )
                        }
                    }
                }

                if (propertyOwnerProfile.isVerified) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = SuccessGreen.copy(alpha = 0.1f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                Icons.Default.Verified,
                                contentDescription = null,
                                tint = SuccessGreen,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "Verified Owner",
                                fontSize = 12.sp,
                                color = SuccessGreen,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // License & Company
        if (propertyOwnerProfile.isProfessional) {
            ProfileSection(
                title = "Professional Details",
                action = {
                    IconButton(onClick = onEditProfessionalDetails) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = primaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            ) {
                if (!propertyOwnerProfile.licenseNumber.isNullOrBlank()) {
                    ProfileItem(
                        icon = Icons.Default.Badge,
                        label = "License Number",
                        subtitle = propertyOwnerProfile.licenseNumber!!,
                        onClick = {},
                        iconColor = primaryColor
                    )
                }
                if (!propertyOwnerProfile.companyName.isNullOrBlank()) {
                    ProfileItem(
                        icon = Icons.Default.Business,
                        label = "Company",
                        subtitle = propertyOwnerProfile.companyName!!,
                        onClick = {},
                        iconColor = primaryColor,
                        showDivider = false
                    )
                }
            }
        }

        // Property Preferences - using preferredPropertyTypes (List<PropertyType>)
        if (propertyOwnerProfile.preferredPropertyTypes.isNotEmpty()) {
            ProfileSection(
                title = "Property Types",
                action = {
                    IconButton(onClick = onEditPreferences) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = primaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(propertyOwnerProfile.preferredPropertyTypes) { propertyType ->
                            AssistChip(
                                onClick = {},
                                label = { Text(propertyType.name) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = primaryColor.copy(alpha = 0.1f)
                                )
                            )
                        }
                    }
                }
            }
        }

        // Service Areas
        if (propertyOwnerProfile.serviceAreas.isNotEmpty()) {
            ProfileSection(
                title = "Service Areas",
                action = {
                    IconButton(onClick = onEditPreferences) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = primaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(propertyOwnerProfile.serviceAreas) { area ->
                            AssistChip(
                                onClick = {},
                                label = { Text(area) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = goldenAccent.copy(alpha = 0.1f)
                                )
                            )
                        }
                    }
                }
            }
        }

        // Optional: Show listing type info
        if (propertyOwnerProfile.isListingForRent || propertyOwnerProfile.isListingForSale) {
            ProfileSection(
                title = "Listing Information",
                action = {
                    IconButton(onClick = onEditPreferences) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = primaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            ) {
                ProfileItem(
                    icon = Icons.Default.Home,
                    label = "Listing Type",
                    subtitle = propertyOwnerProfile.listingTypeLabel,
                    onClick = {},
                    iconColor = primaryColor,
                    showDivider = true
                )
                if (propertyOwnerProfile.propertyCount != null && propertyOwnerProfile.propertyCount > 0) {
                    ProfileItem(
                        icon = Icons.Default.Store,
                        label = "Properties",
                        subtitle = "${propertyOwnerProfile.propertyCount} properties",
                        onClick = {},
                        iconColor = primaryColor,
                        showDivider = false
                    )
                }
            }
        }
    }
}

// ==================== TAB 7: PROPERTY SEEKER ====================

@Composable
fun PropertySeekerTabContent(
    propertySeekerProfileData: HousingSeekerProfile,
    primaryColor: Color,
    goldenAccent: Color,
    colorScheme: ColorScheme,
    onEditHousing: () -> Unit,
    onEditLocation: () -> Unit,
    onEditMoveIn: () -> Unit,
    onEditHousehold: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Housing Preferences
        ProfileSection(
            title = "Housing Preferences",
            action = {
                IconButton(onClick = onEditHousing) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = primaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        ) {
            ProfileItem(
                icon = Icons.Default.Bed,
                label = "Bedrooms",
                subtitle = "${propertySeekerProfileData.minBedrooms} - ${propertySeekerProfileData.maxBedrooms}",
                onClick = {},
                iconColor = primaryColor
            )
            ProfileItem(
                icon = Icons.Default.AttachMoney,
                label = "Budget",
                subtitle = buildString {
                    if (propertySeekerProfileData.minBudget != null) {
                        append("KES ${propertySeekerProfileData.minBudget}")
                    }
                    if (propertySeekerProfileData.maxBudget != null) {
                        if (propertySeekerProfileData.minBudget != null) append(" - ")
                        append("KES ${propertySeekerProfileData.maxBudget}")
                    }
                },
                onClick = {},
                iconColor = primaryColor
            )
            if (propertySeekerProfileData.preferredTypes.isNotEmpty()) {
                ProfileItem(
                    icon = Icons.Default.Category,
                    label = "Property Types",
                    subtitle = propertySeekerProfileData.preferredTypes.joinToString(", "),
                    onClick = {},
                    iconColor = primaryColor,
                    showDivider = false
                )
            }
        }

        // Location Preferences
        ProfileSection(
            title = "Location Preferences",
            action = {
                IconButton(onClick = onEditLocation) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = primaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        ) {
            if (propertySeekerProfileData.preferredCities.isNotEmpty()) {
                ProfileItem(
                    icon = Icons.Default.LocationCity,
                    label = "Cities",
                    subtitle = propertySeekerProfileData.preferredCities.joinToString(", "),
                    onClick = {},
                    iconColor = primaryColor
                )
            }
            if (propertySeekerProfileData.preferredNeighborhoods.isNotEmpty()) {
                ProfileItem(
                    icon = Icons.Default.LocationOn,
                    label = "Neighborhoods",
                    subtitle = propertySeekerProfileData.preferredNeighborhoods.joinToString(", "),
                    onClick = {},
                    iconColor = primaryColor,
                    showDivider = false
                )
            }
        }

        // Move-in Details
        ProfileSection(
            title = "Move-in Details",
            action = {
                IconButton(onClick = onEditMoveIn) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = primaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        ) {
            if (!propertySeekerProfileData.moveInDate.isNullOrBlank()) {
                ProfileItem(
                    icon = Icons.Default.CalendarToday,
                    label = "Move-in Date",
                    subtitle = propertySeekerProfileData.moveInDate,
                    onClick = {},
                    iconColor = primaryColor
                )
            }
            if (!propertySeekerProfileData.leaseDuration.isNullOrBlank()) {
                ProfileItem(
                    icon = Icons.Default.Schedule,
                    label = "Lease Duration",
                    subtitle = propertySeekerProfileData.leaseDuration,
                    onClick = {},
                    iconColor = primaryColor,
                    showDivider = false
                )
            }
        }

        // Household
        ProfileSection(
            title = "Household",
            action = {
                IconButton(onClick = onEditHousehold) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = primaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        ) {
            ProfileItem(
                icon = Icons.Default.People,
                label = "Household Size",
                subtitle = "${propertySeekerProfileData.householdSize}",
                onClick = {},
                iconColor = primaryColor
            )
            if (propertySeekerProfileData.hasPets != null) {
                ProfileItem(
                    icon = Icons.Default.Pets,
                    label = "Pets",
                    subtitle = if (propertySeekerProfileData.hasPets == true)
                        propertySeekerProfileData.petDetails ?: "Yes"
                    else
                        "No",
                    onClick = {},
                    iconColor = primaryColor,
                    showDivider = false
                )
            }
        }
    }
}

// ==================== TAB 8: SERVICE PROVIDER ====================

@Composable
fun ServiceProviderTabContent(
    serviceProviderProfile: ProfessionalProfile,
    primaryColor: Color,
    goldenAccent: Color,
    colorScheme: ColorScheme,
    onEditOverview: () -> Unit,
    onEditServices: () -> Unit,
    onEditBeneficiaries: () -> Unit,
    onEditAreas: () -> Unit,
    onEditContact: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Overview
        ProfileSection(
            title = "Service Provider Overview",
            action = {
                IconButton(onClick = onEditOverview) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = primaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = serviceProviderProfile.displayTitle,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            "Experience",
                            fontSize = 12.sp,
                            color = colorScheme.onSurfaceVariant
                        )
                        Text(
                            "${serviceProviderProfile.yearsExperience ?: 0}+ years",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onSurface
                        )
                    }
                    Column {
                        Text(
                            "Rating",
                            fontSize = 12.sp,
                            color = colorScheme.onSurfaceVariant
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                tint = goldenAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                String.format("%.1f", serviceProviderProfile.averageRating),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.onSurface,
                                modifier = Modifier.padding(start = 2.dp)
                            )
                            Text(
                                " (${serviceProviderProfile.totalReviews})",
                                fontSize = 12.sp,
                                color = colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                if (serviceProviderProfile.isVerified) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = SuccessGreen.copy(alpha = 0.1f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                Icons.Default.Verified,
                                contentDescription = null,
                                tint = SuccessGreen,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "Verified Provider",
                                fontSize = 12.sp,
                                color = SuccessGreen,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // Services Offered
        if (serviceProviderProfile.specialties.isNotEmpty()) {
            ProfileSection(
                title = "Services Offered",
                action = {
                    IconButton(onClick = onEditServices) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = primaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(serviceProviderProfile.specialties) { service ->
                            AssistChip(
                                onClick = {},
                                label = { Text(service) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = primaryColor.copy(alpha = 0.1f)
                                )
                            )
                        }
                    }
                }
            }
        }

        // Service Areas
        if (serviceProviderProfile.serviceAreas.isNotEmpty()) {
            ProfileSection(
                title = "Service Areas",
                action = {
                    IconButton(onClick = onEditAreas) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = primaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(serviceProviderProfile.serviceAreas) { area ->
                            AssistChip(
                                onClick = {},
                                label = { Text(area) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = primaryColor.copy(alpha = 0.1f)
                                )
                            )
                        }
                    }
                }
            }
        }

        // Business Details
        ProfileSection(
            title = "Business Details",
            action = {
                IconButton(onClick = onEditContact) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = primaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        ) {
            if (!serviceProviderProfile.licenseNumber.isNullOrBlank()) {
                ProfileItem(
                    icon = Icons.Default.Badge,
                    label = "License Number",
                    subtitle = serviceProviderProfile.licenseNumber!!,
                    onClick = {},
                    iconColor = if (serviceProviderProfile.isVerified) SuccessGreen else primaryColor
                )
            }
            if (serviceProviderProfile.hourlyRate != null) {
                ProfileItem(
                    icon = Icons.Default.AttachMoney,
                    label = "Hourly Rate",
                    subtitle = "KES ${serviceProviderProfile.hourlyRate}",
                    onClick = {},
                    iconColor = primaryColor
                )
            }
        }

        // About
        if (!serviceProviderProfile.title.isNullOrBlank()) {
            ProfileSection(
                title = "About",
                action = {
                    IconButton(onClick = onEditContact) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = primaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = serviceProviderProfile.title!!,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = colorScheme.onSurface
                    )
                }
            }
        }

        // Contact Info
        ProfileSection(
            title = "Contact Information",
            action = {
                IconButton(onClick = onEditContact) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = primaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        ) {
            // These fields don't exist in ProfessionalProfile - they would come from ServiceProviderProfile
            // For now, they are commented out or shown as "Not provided"
            ProfileItem(
                icon = Icons.Default.Email,
                label = "Email",
                subtitle = "Not provided",
                onClick = {},
                iconColor = primaryColor
            )
            ProfileItem(
                icon = Icons.Default.Phone,
                label = "Phone",
                subtitle = "Not provided",
                onClick = {},
                iconColor = primaryColor,
                showDivider = false
            )
        }

        // Support Needs
        ProfileSection(
            title = "Support Needs",
            action = {
                IconButton(onClick = onEditContact) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = primaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        ) {
            ProfileItem(
                icon = Icons.Default.Favorite,
                label = "Accepts Donations",
                subtitle = "Not specified",
                onClick = {},
                iconColor = Color(0xFFE91E63)
            )
            ProfileItem(
                icon = Icons.Default.People,
                label = "Needs Volunteers",
                subtitle = "Not specified",
                onClick = {},
                iconColor = primaryColor,
                showDivider = false
            )
        }
    }
}

// ==================== TAB 9: BENEFICIARY ====================

@Composable
fun BeneficiaryTabContent(
    beneficiaryProfile: BeneficiaryProfile,
    primaryColor: Color,
    goldenAccent: Color,
    colorScheme: ColorScheme,
    onEditNeeds: () -> Unit,
    onEditHousehold: () -> Unit,
    onEditLocation: () -> Unit,
    onEditPrivacy: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Needs
        ProfileSection(
            title = "Needs",
            action = {
                IconButton(onClick = onEditNeeds) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = primaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        ) {
            if (beneficiaryProfile.needs.isNotEmpty()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(beneficiaryProfile.needs) { need ->
                            AssistChip(
                                onClick = {},
                                label = { Text(need) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = primaryColor.copy(alpha = 0.1f)
                                )
                            )
                        }
                    }
                }
            }

            if (beneficiaryProfile.urgentNeeds.isNotEmpty()) {
                Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {
                    Text(
                        "Urgent Needs",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = errorLight,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(beneficiaryProfile.urgentNeeds) { need ->
                            AssistChip(
                                onClick = {},
                                label = { Text(need) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = errorLight.copy(alpha = 0.1f),
                                    labelColor = errorLight
                                )
                            )
                        }
                    }
                }
            }
        }

        // Household
        ProfileSection(
            title = "Household",
            action = {
                IconButton(onClick = onEditHousehold) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = primaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        ) {
            if (beneficiaryProfile.familySize != null) {
                ProfileItem(
                    icon = Icons.Default.People,
                    label = "Family Size",
                    subtitle = "${beneficiaryProfile.familySize}",
                    onClick = {},
                    iconColor = primaryColor
                )
            }
            if (beneficiaryProfile.dependents != null) {
                ProfileItem(
                    icon = Icons.Default.ChildCare,
                    label = "Dependents",
                    subtitle = "${beneficiaryProfile.dependents}",
                    onClick = {},
                    iconColor = primaryColor,
                    showDivider = false
                )
            }
            if (!beneficiaryProfile.householdComposition.isNullOrBlank()) {
                ProfileItem(
                    icon = Icons.Default.FamilyRestroom,
                    label = "Household Composition",
                    subtitle = beneficiaryProfile.householdComposition!!,
                    onClick = {},
                    iconColor = primaryColor,
                    showDivider = false
                )
            }
        }

        // Vulnerability Factors
        if (beneficiaryProfile.vulnerabilityFactors.isNotEmpty()) {
            ProfileSection(
                title = "Vulnerability Factors",
                action = {
                    IconButton(onClick = onEditNeeds) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = primaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(beneficiaryProfile.vulnerabilityFactors) { factor ->
                            AssistChip(
                                onClick = {},
                                label = { Text(factor) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = errorLight.copy(alpha = 0.1f)
                                )
                            )
                        }
                    }
                }
            }
        }

        // Location
        if (!beneficiaryProfile.city.isNullOrBlank() || !beneficiaryProfile.neighborhood.isNullOrBlank()) {
            ProfileSection(
                title = "Location",
                action = {
                    IconButton(onClick = onEditLocation) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = primaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            ) {
                if (!beneficiaryProfile.city.isNullOrBlank()) {
                    ProfileItem(
                        icon = Icons.Default.LocationCity,
                        label = "City",
                        subtitle = beneficiaryProfile.city!!,
                        onClick = {},
                        iconColor = primaryColor
                    )
                }
                if (!beneficiaryProfile.neighborhood.isNullOrBlank()) {
                    ProfileItem(
                        icon = Icons.Default.LocationOn,
                        label = "Neighborhood",
                        subtitle = beneficiaryProfile.neighborhood!!,
                        onClick = {},
                        iconColor = primaryColor,
                        showDivider = false
                    )
                }
                if (!beneficiaryProfile.landmark.isNullOrBlank()) {
                    ProfileItem(
                        icon = Icons.Default.Place,
                        label = "Landmark",
                        subtitle = beneficiaryProfile.landmark!!,
                        onClick = {},
                        iconColor = primaryColor,
                        showDivider = false
                    )
                }
            }
        }

        // Privacy Settings
        ProfileSection(
            title = "Privacy Settings",
            action = {
                IconButton(onClick = onEditPrivacy) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = primaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        ) {
            ProfileItem(
                icon = Icons.Default.PrivacyTip,
                label = "Anonymous Profile",
                subtitle = if (beneficiaryProfile.prefersAnonymity) "Yes" else "No",
                onClick = {},
                iconColor = primaryColor
            )
            if (beneficiaryProfile.languagePreferences.isNotEmpty()) {
                ProfileItem(
                    icon = Icons.Default.Language,
                    label = "Language Preference",
                    subtitle = beneficiaryProfile.languagePreferences.joinToString(", "),
                    onClick = {},
                    iconColor = primaryColor,
                    showDivider = false
                )
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