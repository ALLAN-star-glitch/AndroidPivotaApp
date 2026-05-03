package com.example.pivota.dashboard.presentation.screens

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import androidx.window.core.layout.WindowWidthSizeClass
import com.example.pivota.admin.presentation.screens.AdminHouseDetailsScreen
import com.example.pivota.admin.presentation.screens.AdminJobDetailsScreen
import com.example.pivota.admin.presentation.screens.AdminJobListingUiModel
import com.example.pivota.admin.presentation.screens.ApplicationFunnel
import com.example.pivota.admin.presentation.screens.JobStatus
import com.example.pivota.core.presentations.composables.PivotaSnackbar
import com.example.pivota.core.presentations.composables.SnackbarType
import com.example.pivota.dashboard.presentation.composables.*
import com.example.pivota.dashboard.presentation.state.HousingListingUiModel
import com.example.pivota.dashboard.presentation.state.JobListingUiModel as DashboardJobListingUiModel
import com.example.pivota.dashboard.presentation.viewmodels.DashboardSharedViewModel
import com.example.pivota.dashboard.presentation.viewmodels.DashboardViewModel
import com.example.pivota.dashboard.presentation.viewmodels.HeaderState
import com.example.pivota.dashboard.presentation.viewmodels.HouseListingsViewModel
import com.example.pivota.dashboard.presentation.viewmodels.MyListingsViewModel
import com.example.pivota.listings.presentation.screens.BookViewingScreen
import com.example.pivota.listings.presentation.screens.HouseDetailsScreen
import com.example.pivota.listings.presentation.screens.HousingPostScreen
import com.example.pivota.listings.presentation.screens.JobPostScreen
import com.example.pivota.listings.presentation.screens.PostServiceScreen
import com.example.pivota.listings.presentation.screens.jobs.JobDetailsScreen
import com.example.pivota.listings.presentation.screens.jobs.JobListingUiModel as DetailsJobListingUiModel
import com.example.pivota.listings.presentation.screens.jobs.JobType
import com.example.pivota.listings.presentation.screens.jobs.SalaryPeriod
import com.example.pivota.listings.presentation.screens.jobs.Benefit
import com.example.pivota.listings.presentation.screens.jobs.BenefitIcon
import topLevelRoutes
import java.util.Date
import java.util.concurrent.TimeUnit
import androidx.compose.material3.SheetState
import androidx.compose.ui.Alignment
import androidx.compose.ui.zIndex
import kotlinx.coroutines.launch

// Quick conversion functions (keep as is)
private fun quickConvertToDetailsJob(dashboardJob: DashboardJobListingUiModel): DetailsJobListingUiModel {
    // Parse salary to get a number (simplified)
    val salaryValue = try {
        dashboardJob.salary.replace("KES", "")
            .replace("KSh", "")
            .replace(",", "")
            .replace(" ", "")
            .trim()
            .toIntOrNull() ?: 50000
    } catch (e: Exception) {
        50000
    }

    // Determine job type from string
    val jobType = when (dashboardJob.jobType.lowercase()) {
        "full-time" -> JobType.FULL_TIME
        "part-time" -> JobType.PART_TIME
        "freelance" -> JobType.FREELANCE
        "contract" -> JobType.CONTRACT
        "internship" -> JobType.INTERNSHIP
        "casual" -> JobType.CASUAL
        "remote" -> JobType.REMOTE
        "hybrid" -> JobType.HYBRID
        else -> JobType.FULL_TIME
    }

    return DetailsJobListingUiModel(
        id = dashboardJob.id,
        title = dashboardJob.title,
        companyName = dashboardJob.company,
        companyLogoUrl = null,
        companyRating = 4.5,
        reviewCount = 42,
        location = dashboardJob.location,
        exactLocation = dashboardJob.location,
        jobType = jobType,
        salaryMin = salaryValue,
        salaryMax = salaryValue + 20000,
        salaryPeriod = SalaryPeriod.PER_MONTH,
        currency = "KES",
        description = dashboardJob.description.ifEmpty {
            "This is a great opportunity at ${dashboardJob.company}. Apply now!"
        },
        responsibilities = listOf(
            "Work with the team to achieve goals",
            "Deliver high quality results",
            "Collaborate with stakeholders"
        ),
        requirements = listOf(
            "Relevant experience",
            "Good communication skills",
            "Team player"
        ),
        preferredQualifications = emptyList(),
        benefits = listOf(
            Benefit(BenefitIcon.HEALTH, "Health Insurance"),
            Benefit(BenefitIcon.TRANSPORT, "Transport Allowance")
        ),
        postedDate = Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(3)),
        applicationDeadline = Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(14)),
        numberOfPositions = 2,
        employerName = dashboardJob.company,
        employerAvatarUrl = null,
        employerVerified = dashboardJob.isVerified,
        isSaved = false,
        isVerified = dashboardJob.isVerified,
        aiMatchScore = 85,
        commuteDistance = "15 min",
        tags = listOf(dashboardJob.jobType, "Urgent"),
        applicationUrl = null,
        hasQuickApply = true
    )
}

// NEW: Conversion function for admin job details
private fun convertToAdminJobListing(dashboardJob: DashboardJobListingUiModel): AdminJobListingUiModel {
    // Map to admin job status
    val jobStatus = JobStatus.ACTIVE // Default, you can map based on your data

    return AdminJobListingUiModel(
        id = dashboardJob.id,
        title = dashboardJob.title,
        companyName = dashboardJob.company,
        companyLogoUrl = null,
        location = dashboardJob.location,
        exactLocation = dashboardJob.location,
        jobType = dashboardJob.jobType,
        status = jobStatus,
        postedDate = Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(3)),
        expiryDate = Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(25)),
        views = 0,
        applications = 0,
        newApplications = 0,
        reviewedApplications = 0,
        description = dashboardJob.description,
        requirements = listOf(
            "Bachelor's degree in Computer Science or related field",
            "5+ years of experience"
        ),
        skills = listOf("Kotlin", "Android", "REST APIs"),
        benefits = listOf("Health Insurance", "Transport allowance"),
        isVerified = dashboardJob.isVerified,
        employerName = dashboardJob.company,
        employerVerified = true,
        averageTimeToApply = 3.2,
        applicationFunnel = com.example.pivota.admin.presentation.screens.ApplicationFunnel(
            viewed = 100,
            applied = 24,
            reviewed = 12
        )
    )
}

@SuppressLint("ViewModelConstructorInComposable")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScaffold(
    isGuestMode: Boolean = false,
    successMessage: String? = null,
    accessToken: String? = null,
    refreshToken: String? = null,
    onMessageConsumed: (() -> Unit)? = null
) {
    val navController = rememberNavController()
    val sheetState = rememberModalBottomSheetState()
    var showSheet by remember { mutableStateOf(false) }
    val dashboardViewModel: DashboardViewModel = hiltViewModel()
    val sharedViewModel: DashboardSharedViewModel = hiltViewModel()
    val instanceId = System.identityHashCode(sharedViewModel)
    println("🔍 [DashboardScaffold/ReusableHeader] ViewModel instance: $instanceId")

    // Helper functions to get state synchronously
    val isLoading = sharedViewModel.isLoading()
    val profileError = sharedViewModel.getErrorMessage()
    val profile = sharedViewModel.getCurrentProfile()

    val headerState by sharedViewModel.headerState.collectAsState()

    var showWelcomeSnackbar by remember { mutableStateOf(false) }
    var welcomeMessage by remember { mutableStateOf("") }
    var snackbarType by remember { mutableStateOf(SnackbarType.SUCCESS) }


    LaunchedEffect(Unit) {
        if (!isGuestMode) {
            sharedViewModel.debugRoomCache()
        }
    }


    LaunchedEffect(successMessage) {
        if (!successMessage.isNullOrBlank()) {
            welcomeMessage = successMessage
            snackbarType = SnackbarType.SUCCESS
            showWelcomeSnackbar = true
            onMessageConsumed?.invoke()
        }
    }

    //  Only start token refresh and auto-logout monitoring, NOT profile loading
    // Profile loading is now handled by ViewModel's init block
    LaunchedEffect(Unit) {
        if (!isGuestMode && accessToken != null) {
            dashboardViewModel.startTokenRefresh()
            println("🔄 Token auto-refresh started")

            // ✅ Check token validity and refresh if needed
            val isValid = dashboardViewModel.isTokenValid()
            if (!isValid) {
                println("⚠️ Token may be invalid, attempting refresh...")
                dashboardViewModel.refreshTokenNow()
            }
        }
    }

    LaunchedEffect(Unit) {
        dashboardViewModel.logoutEvent.collect {
            println("🚨 Logout event received, navigating to login...")
            sharedViewModel.reset()
        }
    }

    LaunchedEffect(Unit) {
        dashboardViewModel.networkErrorEvent.collect { errorMessage ->
            println("⚠️ Network error: $errorMessage")
        }
    }

    LaunchedEffect(headerState) {
        if (headerState is HeaderState.Success) {
            val headerUser = (headerState as HeaderState.Success).headerUser
            println("🔍 [DashboardScaffold] User loaded: ${headerUser.name}")
            println("🔍 [DashboardScaffold] Account type: ${headerUser.accountType}")
        }
    }

    // Separate state for different navigation flows
    var selectedListingForBooking by remember { mutableStateOf<HousingListingUiModel?>(null) }
    var selectedListingForViewing by remember { mutableStateOf<HousingListingUiModel?>(null) }
    var selectedListingForAdminView by remember { mutableStateOf<HousingListingUiModel?>(null) }
    var selectedJobForViewing by remember { mutableStateOf<DetailsJobListingUiModel?>(null) }
    var selectedAdminJobForViewing by remember { mutableStateOf<AdminJobListingUiModel?>(null) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val isTablet = windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.EXPANDED ||
            windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.MEDIUM

    val visibleRoutes = topLevelRoutes

    if (isGuestMode) {
        GuestContent(
            navController = navController,
            sheetState = sheetState,
            showSheet = showSheet,
            onShowSheetChange = { showSheet = it },
            showWelcomeSnackbar = showWelcomeSnackbar,
            welcomeMessage = welcomeMessage,
            snackbarType = snackbarType,
            onSnackbarDismiss = {
                showWelcomeSnackbar = false
                welcomeMessage = ""
            }
        )
        return
    }

    // ============================================================
// HANDLE AUTH ERRORS FIRST (Critical - must logout)
// ============================================================
    val isAuthError = sharedViewModel.isAuthError()
    val offlineMessage by sharedViewModel.offlineMessage.collectAsState()
    val isOffline by sharedViewModel.isOffline.collectAsState()

    if (isAuthError) {
        // Auth error - session expired
        // The TokenManager will automatically emit logoutEvent
        // which is already being collected below
        LaunchedEffect(Unit) {
            sharedViewModel.reset()
        }

        // Show user-friendly message while waiting for logout
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.Lock,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Session Expired",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Please login again",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            }
        }
        return
    }

// ============================================================
// ALWAYS SHOW DASHBOARD CONTENT (with skeleton only on first-ever load)
// ============================================================
// Only show skeleton if:
// 1. Still loading AND
// 2. No profile data available yet (first launch) AND
// 3. Not offline (offline means we have cache)
    val hasProfileData = sharedViewModel.hasProfileData()
    val shouldShowSkeleton = isLoading && !hasProfileData && !isOffline

    if (shouldShowSkeleton) {
        DashboardLoadingSkeleton()
    } else {
        // Always show dashboard content - either from cache or fresh
        // Show offline/warning banner if needed
        Box {
            if (isTablet) {
                TabletDashboardContent(
                    navController = navController,
                    currentDestination = currentDestination,
                    visibleRoutes = visibleRoutes,
                    selectedListingForBooking = selectedListingForBooking,
                    selectedListingForViewing = selectedListingForViewing,
                    selectedListingForAdminView = selectedListingForAdminView,
                    selectedJobForViewing = selectedJobForViewing,
                    selectedAdminJobForViewing = selectedAdminJobForViewing,
                    sharedViewModel = sharedViewModel,
                    accessToken = accessToken,
                    showSheet = showSheet,
                    onShowSheetChange = { showSheet = it },
                    sheetState = sheetState,
                    onBookingSelected = { selectedListingForBooking = it },
                    onViewingSelected = { selectedListingForViewing = it },
                    onAdminViewSelected = { selectedListingForAdminView = it },
                    onJobViewingSelected = { selectedJobForViewing = it },
                    onAdminJobViewingSelected = { selectedAdminJobForViewing = it },
                    showWelcomeSnackbar = showWelcomeSnackbar,
                    welcomeMessage = welcomeMessage,
                    snackbarType = snackbarType,
                    onSnackbarDismiss = {
                        showWelcomeSnackbar = false
                        welcomeMessage = ""
                    }
                )
            } else {
                MobileDashboardContent(
                    navController = navController,
                    currentDestination = currentDestination,
                    visibleRoutes = visibleRoutes,
                    selectedListingForBooking = selectedListingForBooking,
                    selectedListingForViewing = selectedListingForViewing,
                    selectedListingForAdminView = selectedListingForAdminView,
                    selectedJobForViewing = selectedJobForViewing,
                    selectedAdminJobForViewing = selectedAdminJobForViewing,
                    sharedViewModel = sharedViewModel,
                    accessToken = accessToken,
                    showSheet = showSheet,
                    onShowSheetChange = { showSheet = it },
                    sheetState = sheetState,
                    onBookingSelected = { selectedListingForBooking = it },
                    onViewingSelected = { selectedListingForViewing = it },
                    onAdminViewSelected = { selectedListingForAdminView = it },
                    onJobViewingSelected = { selectedJobForViewing = it },
                    onAdminJobViewingSelected = { selectedAdminJobForViewing = it },
                    showWelcomeSnackbar = showWelcomeSnackbar,
                    welcomeMessage = welcomeMessage,
                    snackbarType = snackbarType,
                    onSnackbarDismiss = {
                        showWelcomeSnackbar = false
                        welcomeMessage = ""
                    }
                )
            }

            // Show offline/warning banner if needed
            if (isOffline && offlineMessage != null) {
                OfflineWarningBanner(
                    message = offlineMessage!!,
                    onDismiss = { sharedViewModel.dismissOfflineMessage() }
                )
            }

            // Show retry button if offline and user wants to retry
            if (isOffline && hasProfileData) {
                FloatingActionButton(
                    onClick = { sharedViewModel.refreshProfile() },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp),
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Retry")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TabletDashboardContent(
    navController: NavHostController,
    currentDestination: NavDestination?,
    visibleRoutes: List<TopLevelRoute>,
    selectedListingForBooking: HousingListingUiModel?,
    selectedListingForViewing: HousingListingUiModel?,
    selectedListingForAdminView: HousingListingUiModel?,
    selectedJobForViewing: DetailsJobListingUiModel?,
    selectedAdminJobForViewing: AdminJobListingUiModel?,
    sharedViewModel: DashboardSharedViewModel,
    accessToken: String?,
    showSheet: Boolean,
    onShowSheetChange: (Boolean) -> Unit,
    sheetState: SheetState,
    onBookingSelected: (HousingListingUiModel?) -> Unit,
    onViewingSelected: (HousingListingUiModel?) -> Unit,
    onAdminViewSelected: (HousingListingUiModel?) -> Unit,
    onJobViewingSelected: (DetailsJobListingUiModel?) -> Unit,
    onAdminJobViewingSelected: (AdminJobListingUiModel?) -> Unit,
    showWelcomeSnackbar: Boolean,
    welcomeMessage: String,
    snackbarType: SnackbarType,
    onSnackbarDismiss: () -> Unit
) {
    // Use a Box with proper constraints to prevent snackbar from stretching
    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.TopStart) // This prevents stretching
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            NavigationRail(
                modifier = Modifier
                    .fillMaxHeight()
                    .navigationBarsPadding(),
                containerColor = MaterialTheme.colorScheme.surface,
            ) {
                Column(
                    modifier = Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    visibleRoutes.forEach { route ->
                        val isSelected = currentDestination?.hierarchy?.any { it.route == route.route::class.qualifiedName } == true
                        NavigationRailItem(
                            icon = { Icon(route.icon, contentDescription = route.contentDescription, modifier = Modifier.size(24.dp)) },
                            label = { Text(route.label, fontSize = 11.sp) },
                            selected = isSelected,
                            onClick = {
                                if (currentDestination?.route != route.route) {
                                    navController.navigate(route.route) {
                                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.tertiary,
                                selectedTextColor = MaterialTheme.colorScheme.tertiary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }

            // Content area
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 8.dp)
            ) {
                TabletNavHost(
                    navController = navController,
                    selectedListingForBooking = selectedListingForBooking,
                    selectedListingForViewing = selectedListingForViewing,
                    selectedListingForAdminView = selectedListingForAdminView,
                    selectedJobForViewing = selectedJobForViewing,
                    selectedAdminJobForViewing = selectedAdminJobForViewing,
                    isGuestMode = false,
                    sharedViewModel = sharedViewModel,
                    accessToken = accessToken,
                    showSheet = showSheet,
                    onShowSheetChange = onShowSheetChange,
                    sheetState = sheetState,
                    onBookingSelected = onBookingSelected,
                    onViewingSelected = onViewingSelected,
                    onAdminViewSelected = onAdminViewSelected,
                    onJobViewingSelected = onJobViewingSelected,
                    onAdminJobViewingSelected = onAdminJobViewingSelected
                )
            }
        }

        // Snackbar - now properly constrained and won't stretch across whole screen
        if (showWelcomeSnackbar && welcomeMessage.isNotBlank()) {
            androidx.compose.animation.AnimatedVisibility(
                visible = true,
                enter = androidx.compose.animation.slideInVertically(
                    initialOffsetY = { -it } // Slide in from top
                ) + androidx.compose.animation.fadeIn(),
                exit = androidx.compose.animation.slideOutVertically(
                    targetOffsetY = { -it } // Slide out to top
                ) + androidx.compose.animation.fadeOut(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp) // Add some padding from the top edge
                    .wrapContentWidth() // Only take needed width, not full width
                    .zIndex(100f)
            ) {
                PivotaSnackbar(
                    message = welcomeMessage,
                    type = snackbarType,
                    duration = 3000L,
                    onDismiss = onSnackbarDismiss
                )
            }
        }
    }

    if (showSheet) {
        PostOptionsBottomSheet(
            sheetState = sheetState,
            onDismiss = { onShowSheetChange(false) },
            onOptionSelected = { category ->
                onShowSheetChange(false)
                when (category) {
                    "jobs" -> navController.navigate(PostJob)
                    "housing" -> navController.navigate(PostHousing)
                    "support" -> navController.navigate(PostSupport)
                    "service" -> navController.navigate(PostService)
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MobileDashboardContent(
    navController: NavHostController,
    currentDestination: NavDestination?,
    visibleRoutes: List<TopLevelRoute>,
    selectedListingForBooking: HousingListingUiModel?,
    selectedListingForViewing: HousingListingUiModel?,
    selectedListingForAdminView: HousingListingUiModel?,
    selectedJobForViewing: DetailsJobListingUiModel?,
    selectedAdminJobForViewing: AdminJobListingUiModel?,
    sharedViewModel: DashboardSharedViewModel,
    accessToken: String?,
    showSheet: Boolean,
    onShowSheetChange: (Boolean) -> Unit,
    sheetState: SheetState,
    onBookingSelected: (HousingListingUiModel?) -> Unit,
    onViewingSelected: (HousingListingUiModel?) -> Unit,
    onAdminViewSelected: (HousingListingUiModel?) -> Unit,
    onJobViewingSelected: (DetailsJobListingUiModel?) -> Unit,
    onAdminJobViewingSelected: (AdminJobListingUiModel?) -> Unit,
    showWelcomeSnackbar: Boolean,
    welcomeMessage: String,
    snackbarType: SnackbarType,
    onSnackbarDismiss: () -> Unit
) {
    MobileNavHost(
        navController = navController,
        selectedListingForBooking = selectedListingForBooking,
        selectedListingForViewing = selectedListingForViewing,
        selectedListingForAdminView = selectedListingForAdminView,
        selectedJobForViewing = selectedJobForViewing,
        selectedAdminJobForViewing = selectedAdminJobForViewing,
        isGuestMode = false,
        sharedViewModel = sharedViewModel,
        accessToken = accessToken,
        visibleRoutes = visibleRoutes,
        currentDestination = currentDestination,
        showSheet = showSheet,
        onShowSheetChange = onShowSheetChange,
        sheetState = sheetState,
        onBookingSelected = onBookingSelected,
        onViewingSelected = onViewingSelected,
        onAdminViewSelected = onAdminViewSelected,
        onJobViewingSelected = onJobViewingSelected,
        onAdminJobViewingSelected = onAdminJobViewingSelected,
        showWelcomeSnackbar = showWelcomeSnackbar,
        welcomeMessage = welcomeMessage,
        snackbarType = snackbarType,
        onSnackbarDismiss = onSnackbarDismiss
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GuestContent(
    navController: NavHostController,
    sheetState: SheetState,
    showSheet: Boolean,
    onShowSheetChange: (Boolean) -> Unit,
    showWelcomeSnackbar: Boolean,
    welcomeMessage: String,
    snackbarType: SnackbarType,
    onSnackbarDismiss: () -> Unit
) {
    var selectedListingForBooking by remember { mutableStateOf<HousingListingUiModel?>(null) }
    var selectedListingForViewing by remember { mutableStateOf<HousingListingUiModel?>(null) }
    var selectedListingForAdminView by remember { mutableStateOf<HousingListingUiModel?>(null) }
    var selectedJobForViewing by remember { mutableStateOf<DetailsJobListingUiModel?>(null) }
    var selectedAdminJobForViewing by remember { mutableStateOf<AdminJobListingUiModel?>(null) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val isTablet = windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.EXPANDED ||
            windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.MEDIUM
    val visibleRoutes = topLevelRoutes

    if (isTablet) {
        Row(modifier = Modifier.fillMaxSize()) {
            NavigationRail(
                modifier = Modifier.fillMaxHeight().navigationBarsPadding(),
                containerColor = MaterialTheme.colorScheme.surface,
            ) {
                Column(
                    modifier = Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    visibleRoutes.forEach { route ->
                        val isSelected = currentDestination?.hierarchy?.any { it.route == route.route::class.qualifiedName } == true
                        NavigationRailItem(
                            icon = { Icon(route.icon, contentDescription = route.contentDescription, modifier = Modifier.size(24.dp)) },
                            label = { Text(route.label, fontSize = 11.sp) },
                            selected = isSelected,
                            onClick = {
                                if (currentDestination?.route != route.route) {
                                    navController.navigate(route.route) {
                                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.tertiary,
                                selectedTextColor = MaterialTheme.colorScheme.tertiary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
            Box(modifier = Modifier.fillMaxSize().padding(start = 8.dp)) {
                TabletNavHost(
                    navController = navController,
                    selectedListingForBooking = selectedListingForBooking,
                    selectedListingForViewing = selectedListingForViewing,
                    selectedListingForAdminView = selectedListingForAdminView,
                    selectedJobForViewing = selectedJobForViewing,
                    selectedAdminJobForViewing = selectedAdminJobForViewing,
                    isGuestMode = true,
                    sharedViewModel = hiltViewModel(),
                    accessToken = null,
                    showSheet = showSheet,
                    onShowSheetChange = onShowSheetChange,
                    sheetState = sheetState,
                    onBookingSelected = { selectedListingForBooking = it },
                    onViewingSelected = { selectedListingForViewing = it },
                    onAdminViewSelected = { selectedListingForAdminView = it },
                    onJobViewingSelected = { selectedJobForViewing = it },
                    onAdminJobViewingSelected = { selectedAdminJobForViewing = it }
                )
            }
        }
    } else {
        MobileNavHost(
            navController = navController,
            selectedListingForBooking = selectedListingForBooking,
            selectedListingForViewing = selectedListingForViewing,
            selectedListingForAdminView = selectedListingForAdminView,
            selectedJobForViewing = selectedJobForViewing,
            selectedAdminJobForViewing = selectedAdminJobForViewing,
            isGuestMode = true,
            sharedViewModel = hiltViewModel(),
            accessToken = null,
            visibleRoutes = visibleRoutes,
            currentDestination = currentDestination,
            showSheet = showSheet,
            onShowSheetChange = onShowSheetChange,
            sheetState = sheetState,
            onBookingSelected = { selectedListingForBooking = it },
            onViewingSelected = { selectedListingForViewing = it },
            onAdminViewSelected = { selectedListingForAdminView = it },
            onJobViewingSelected = { selectedJobForViewing = it },
            onAdminJobViewingSelected = { selectedAdminJobForViewing = it },
            showWelcomeSnackbar = showWelcomeSnackbar,
            welcomeMessage = welcomeMessage,
            snackbarType = snackbarType,
            onSnackbarDismiss = onSnackbarDismiss
        )
    }

    if (showSheet) {
        PostOptionsBottomSheet(
            sheetState = sheetState,
            onDismiss = { onShowSheetChange(false) },
            onOptionSelected = { category ->
                onShowSheetChange(false)
                when (category) {
                    "jobs" -> navController.navigate(PostJob)
                    "housing" -> navController.navigate(PostHousing)
                    "support" -> navController.navigate(PostSupport)
                    "service" -> navController.navigate(PostService)
                }
            }
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AuthenticatedContent(
    navController: NavHostController,
    sheetState: SheetState,
    showSheet: Boolean,
    onShowSheetChange: (Boolean) -> Unit,
    showWelcomeSnackbar: Boolean,
    welcomeMessage: String,
    snackbarType: SnackbarType,
    onSnackbarDismiss: () -> Unit,
    accessToken: String?,
    sharedViewModel: DashboardSharedViewModel
) {
    var selectedListingForBooking by remember { mutableStateOf<HousingListingUiModel?>(null) }
    var selectedListingForViewing by remember { mutableStateOf<HousingListingUiModel?>(null) }
    var selectedListingForAdminView by remember { mutableStateOf<HousingListingUiModel?>(null) }
    var selectedJobForViewing by remember { mutableStateOf<DetailsJobListingUiModel?>(null) }
    var selectedAdminJobForViewing by remember { mutableStateOf<AdminJobListingUiModel?>(null) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val isTablet = windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.EXPANDED ||
            windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.MEDIUM
    val visibleRoutes = topLevelRoutes

    if (isTablet) {
        Row(modifier = Modifier.fillMaxSize()) {
            NavigationRail(
                modifier = Modifier.fillMaxHeight().navigationBarsPadding(),
                containerColor = MaterialTheme.colorScheme.surface,
            ) {
                Column(
                    modifier = Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    visibleRoutes.forEach { route ->
                        val isSelected = currentDestination?.hierarchy?.any { it.route == route.route::class.qualifiedName } == true
                        NavigationRailItem(
                            icon = { Icon(route.icon, contentDescription = route.contentDescription, modifier = Modifier.size(24.dp)) },
                            label = { Text(route.label, fontSize = 11.sp) },
                            selected = isSelected,
                            onClick = {
                                if (currentDestination?.route != route.route) {
                                    navController.navigate(route.route) {
                                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.tertiary,
                                selectedTextColor = MaterialTheme.colorScheme.tertiary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
            Box(modifier = Modifier.fillMaxSize().padding(start = 8.dp)) {
                TabletNavHost(
                    navController = navController,
                    selectedListingForBooking = selectedListingForBooking,
                    selectedListingForViewing = selectedListingForViewing,
                    selectedListingForAdminView = selectedListingForAdminView,
                    selectedJobForViewing = selectedJobForViewing,
                    selectedAdminJobForViewing = selectedAdminJobForViewing,
                    isGuestMode = false,
                    sharedViewModel = sharedViewModel,
                    accessToken = accessToken,
                    showSheet = showSheet,
                    onShowSheetChange = onShowSheetChange,
                    sheetState = sheetState,
                    onBookingSelected = { selectedListingForBooking = it },
                    onViewingSelected = { selectedListingForViewing = it },
                    onAdminViewSelected = { selectedListingForAdminView = it },
                    onJobViewingSelected = { selectedJobForViewing = it },
                    onAdminJobViewingSelected = { selectedAdminJobForViewing = it }
                )
            }
        }
    } else {
        MobileNavHost(
            navController = navController,
            selectedListingForBooking = selectedListingForBooking,
            selectedListingForViewing = selectedListingForViewing,
            selectedListingForAdminView = selectedListingForAdminView,
            selectedJobForViewing = selectedJobForViewing,
            selectedAdminJobForViewing = selectedAdminJobForViewing,
            isGuestMode = false,
            sharedViewModel = sharedViewModel,
            accessToken = accessToken,
            visibleRoutes = visibleRoutes,
            currentDestination = currentDestination,
            showSheet = showSheet,
            onShowSheetChange = onShowSheetChange,
            sheetState = sheetState,
            onBookingSelected = { selectedListingForBooking = it },
            onViewingSelected = { selectedListingForViewing = it },
            onAdminViewSelected = { selectedListingForAdminView = it },
            onJobViewingSelected = { selectedJobForViewing = it },
            onAdminJobViewingSelected = { selectedAdminJobForViewing = it },
            showWelcomeSnackbar = showWelcomeSnackbar,
            welcomeMessage = welcomeMessage,
            snackbarType = snackbarType,
            onSnackbarDismiss = onSnackbarDismiss
        )
    }

    if (showSheet) {
        PostOptionsBottomSheet(
            sheetState = sheetState,
            onDismiss = { onShowSheetChange(false) },
            onOptionSelected = { category ->
                onShowSheetChange(false)
                when (category) {
                    "jobs" -> navController.navigate(PostJob)
                    "housing" -> navController.navigate(PostHousing)
                    "support" -> navController.navigate(PostSupport)
                    "service" -> navController.navigate(PostService)
                }
            }
        )
    }
}

// Mobile NavHost with proper bottom navigation
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MobileNavHost(
    navController: NavHostController,
    selectedListingForBooking: HousingListingUiModel?,
    selectedListingForViewing: HousingListingUiModel?,
    selectedListingForAdminView: HousingListingUiModel?,
    selectedJobForViewing: DetailsJobListingUiModel?,
    selectedAdminJobForViewing: AdminJobListingUiModel?,
    isGuestMode: Boolean,
    sharedViewModel: DashboardSharedViewModel,
    accessToken: String?,
    visibleRoutes: List<TopLevelRoute>,
    currentDestination: NavDestination?,
    showSheet: Boolean,
    onShowSheetChange: (Boolean) -> Unit,
    sheetState: SheetState,
    onBookingSelected: (HousingListingUiModel?) -> Unit,
    onViewingSelected: (HousingListingUiModel?) -> Unit,
    onAdminViewSelected: (HousingListingUiModel?) -> Unit,
    onJobViewingSelected: (DetailsJobListingUiModel?) -> Unit,
    onAdminJobViewingSelected: (AdminJobListingUiModel?) -> Unit,
    showWelcomeSnackbar: Boolean,
    welcomeMessage: String,
    snackbarType: SnackbarType,
    onSnackbarDismiss: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Connect,
        modifier = Modifier.fillMaxSize()
    ) {
        // ========== MAIN SCREENS WITH BOTTOM NAVIGATION ==========

        // Dashboard Screen
        composable<Dashboard> {
            MainScreenScaffold(
                navController = navController,
                currentDestination = currentDestination,
                visibleRoutes = visibleRoutes,
                isGuestMode = isGuestMode,
                showSheet = showSheet,
                onShowSheetChange = onShowSheetChange,
                sheetState = sheetState,
                showWelcomeSnackbar = showWelcomeSnackbar,
                welcomeMessage = welcomeMessage,
                snackbarType = snackbarType,
                onSnackbarDismiss = onSnackbarDismiss,
                sharedViewModel = sharedViewModel
            ) {
                DashboardScreen(
                    onNavigateToListings = {
                        navController.navigate(MyListings)
                    },
                    isGuestMode = isGuestMode,
                    accessToken = accessToken,
                    sharedViewModel = sharedViewModel
                )
            }
        }

        // Discover (Connect) Screen
        composable<Connect> {
            MainScreenScaffold(
                navController = navController,
                currentDestination = currentDestination,
                visibleRoutes = visibleRoutes,
                isGuestMode = isGuestMode,
                showSheet = showSheet,
                onShowSheetChange = onShowSheetChange,
                sheetState = sheetState,
                showWelcomeSnackbar = showWelcomeSnackbar,
                welcomeMessage = welcomeMessage,
                snackbarType = snackbarType,
                onSnackbarDismiss = onSnackbarDismiss,
                sharedViewModel = sharedViewModel

            ) {
                DiscoverScreen(
                    onNavigateToHouseListings = {
                        navController.navigate(HouseListings)
                    },
                    onNavigateToJobListings = {
                        navController.navigate(JobListings)
                    },
                    onNavigateToAllJobs = {
                        navController.navigate(JobListings)
                    },
                    onNavigateToAllHousing = {
                        navController.navigate(HouseListings)
                    },
                    onNavigateToAllProviders = {
                        navController.navigate(Professionals)
                    },
                    onNavigateToAllServices = {},
                    onNavigateToAllSupport = {},
                    isGuestMode = isGuestMode,
                    sharedViewModel = sharedViewModel
                )
            }
        }

        // Profile Screen
        composable<Profile> {
            MainScreenScaffold(
                navController = navController,
                currentDestination = currentDestination,
                visibleRoutes = visibleRoutes,
                isGuestMode = isGuestMode,
                showSheet = showSheet,
                onShowSheetChange = onShowSheetChange,
                sheetState = sheetState,
                showWelcomeSnackbar = showWelcomeSnackbar,
                welcomeMessage = welcomeMessage,
                snackbarType = snackbarType,
                onSnackbarDismiss = onSnackbarDismiss,
                sharedViewModel = sharedViewModel
            ) {
                ProfileScreen(
                    isGuestMode = isGuestMode,
                    sharedViewModel = sharedViewModel,
                )
            }
        }

        // ========== DETAIL SCREENS - NO BOTTOM NAVIGATION ==========

        composable<Professionals> {
            NoBottomNavScaffold {
                ProfessionalsScreen()
            }
        }

        // House Details
        composable<HouseDetails> {
            NoBottomNavScaffold {
                val listing = selectedListingForViewing
                if (listing != null) {
                    HouseDetailsScreen(
                        housingListing = listing,
                        onNavigateBack = {
                            navController.popBackStack()
                            onViewingSelected(null)
                        },
                        onBookClick = { housingListing ->
                            onBookingSelected(housingListing)
                            navController.navigate(BookViewing)
                        }
                    )
                } else {
                    LaunchedEffect(Unit) {
                        navController.popBackStack()
                    }
                }
            }
        }

        // Admin House Details
        composable<AdminHouseDetails> {
            NoBottomNavScaffold {
                val listing = selectedListingForAdminView
                if (listing != null) {
                    AdminHouseDetailsScreen(
                        housingListing = listing,
                        onNavigateBack = {
                            navController.popBackStack()
                            onAdminViewSelected(null)
                        },
                        onEditListing = { id -> println("Edit listing: $id") },
                        onDuplicateListing = { id -> println("Duplicate listing: $id") },
                        onArchiveListing = { id -> println("Archive listing: $id") },
                        onDeleteListing = { id ->
                            println("Delete listing: $id")
                            navController.popBackStack()
                            onAdminViewSelected(null)
                        },
                        onPauseListing = { id -> println("Pause listing: $id") },
                        onResumeListing = { id -> println("Resume listing: $id") },
                        onMarkAvailable = { id -> println("Mark available: $id") },
                        onMarkRented = { id -> println("Mark rented: $id") },
                        onMarkSold = { id -> println("Mark sold: $id") },
                        onViewInquiries = { id -> println("View inquiries: $id") },
                        onShareListing = { id -> println("Share listing: $id") },
                        onViewLogs = { id -> println("View logs: $id") },
                        onCopyListingLink = { id -> println("Copy link: $id") }
                    )
                } else {
                    LaunchedEffect(Unit) {
                        navController.popBackStack()
                    }
                }
            }
        }

        // Book Viewing
        composable<BookViewing> {
            NoBottomNavScaffold {
                selectedListingForBooking?.let { listing ->
                    BookViewingScreen(
                        housingListing = listing,
                        onNavigateBack = {
                            navController.popBackStack()
                            onBookingSelected(null)
                        }
                    )
                } ?: run {
                    LaunchedEffect(Unit) {
                        navController.popBackStack()
                    }
                }
            }
        }

        // House Listings
        composable<HouseListings> {
            NoBottomNavScaffold {
                val viewModel: HouseListingsViewModel = hiltViewModel()
                HouseListingsScreen(
                    viewModel = viewModel,
                    onListingClick = { housingListing ->
                        onViewingSelected(housingListing)
                        navController.navigate(HouseDetails)
                    },
                    onBookClick = { housingListing ->
                        onBookingSelected(housingListing)
                        navController.navigate(BookViewing)
                    },
                    onPostListingClick = {
                        if (!isGuestMode) onShowSheetChange(true)
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }

        // Job Listings
        composable<JobListings> {
            NoBottomNavScaffold {
                JobListingsScreen(
                    onListingClick = { dashboardJob ->
                        val detailsJob = quickConvertToDetailsJob(dashboardJob)
                        onJobViewingSelected(detailsJob)
                        navController.navigate(JobDetails)
                    },
                    onPostListingClick = {
                        if (!isGuestMode) onShowSheetChange(true)
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }

        // Job Details
        composable<JobDetails> {
            NoBottomNavScaffold {
                val jobListing = selectedJobForViewing
                if (jobListing != null) {
                    JobDetailsScreen(
                        jobListing = jobListing,
                        onNavigateBack = {
                            navController.popBackStack()
                            onJobViewingSelected(null)
                        },
                        onApplyClick = { job -> println("Apply for job: ${job.title}") },
                        onMessageEmployerClick = { job -> println("Message employer for: ${job.title}") },
                        onSaveToggle = { jobId, isSaved -> println("Job $jobId saved: $isSaved") },
                        onBookmarkClick = { job -> println("Bookmark clicked for: ${job.title}") }
                    )
                } else {
                    LaunchedEffect(Unit) {
                        navController.popBackStack()
                    }
                }
            }
        }

        // Admin Job Details
        composable<AdminJobDetails> {
            NoBottomNavScaffold {
                val jobListing = selectedAdminJobForViewing
                if (jobListing != null) {
                    AdminJobDetailsScreen(
                        jobListing = jobListing,
                        onNavigateBack = {
                            navController.popBackStack()
                            onAdminJobViewingSelected(null)
                        },
                        onEditJob = { jobId -> println("Edit job: $jobId") },
                        onDuplicateJob = { jobId -> println("Duplicate job: $jobId") },
                        onArchiveJob = { jobId -> println("Archive job: $jobId") },
                        onDeleteJob = { jobId -> println("Delete job: $jobId") },
                        onPauseJob = { jobId -> println("Pause job: $jobId") },
                        onResumeJob = { jobId -> println("Resume job: $jobId") },
                        onCloseJob = { jobId -> println("Close job: $jobId") },
                        onViewApplicants = { jobId -> println("View applicants for: $jobId") },
                        onShareJob = { jobId -> println("Share job: $jobId") },
                        onViewLogs = { jobId -> println("View logs for: $jobId") },
                        onCopyJobLink = { jobId -> println("Copy link for: $jobId") }
                    )
                } else {
                    LaunchedEffect(Unit) {
                        navController.popBackStack()
                    }
                }
            }
        }

        // Post Job
        composable<PostJob> {
            NoBottomNavScaffold {
                JobPostScreen.Content(onBack = { navController.popBackStack() })
            }
        }

        // Post Service
        composable<PostService> {
            NoBottomNavScaffold {
                PostServiceScreen(onBack = { navController.popBackStack() })
            }
        }

        // Post Housing
        composable<PostHousing> {
            NoBottomNavScaffold {
                HousingPostScreen.Content(onBack = { navController.popBackStack() })
            }
        }

        // My Listings
        composable<MyListings> {
            NoBottomNavScaffold {
                val myListingsViewModel: MyListingsViewModel = hiltViewModel()
                MyListingsScreen(
                    onListingClick = { listingUiModel ->
                        println("Generic listing clicked: ${listingUiModel.title}")
                    },
                    onJobClick = { adminJobListing ->
                        onAdminJobViewingSelected(adminJobListing)
                        navController.navigate(AdminJobDetails)
                    },
                    onHousingViewClick = { housingListing ->
                        onAdminViewSelected(housingListing)
                        navController.navigate(AdminHouseDetails)
                    },
                    onPostListingClick = {
                        if (!isGuestMode) onShowSheetChange(true)
                    },
                    viewModel = myListingsViewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

// Tablet NavHost
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TabletNavHost(
    navController: NavHostController,
    selectedListingForBooking: HousingListingUiModel?,
    selectedListingForViewing: HousingListingUiModel?,
    selectedListingForAdminView: HousingListingUiModel?,
    selectedJobForViewing: DetailsJobListingUiModel?,
    selectedAdminJobForViewing: AdminJobListingUiModel?,
    isGuestMode: Boolean,
    sharedViewModel: DashboardSharedViewModel,
    accessToken: String?,
    showSheet: Boolean,
    onShowSheetChange: (Boolean) -> Unit,
    sheetState: SheetState,
    onBookingSelected: (HousingListingUiModel?) -> Unit,
    onViewingSelected: (HousingListingUiModel?) -> Unit,
    onAdminViewSelected: (HousingListingUiModel?) -> Unit,
    onJobViewingSelected: (DetailsJobListingUiModel?) -> Unit,
    onAdminJobViewingSelected: (AdminJobListingUiModel?) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Connect,
        modifier = Modifier.fillMaxSize()
    ) {
        // Dashboard
        composable<Dashboard> {
            DashboardScreen(
                onNavigateToListings = {
                    navController.navigate(MyListings)
                },
                isGuestMode = isGuestMode,
                accessToken = accessToken,
                sharedViewModel = sharedViewModel
            )
        }

        // Professionals
        composable<Professionals> {
            ProfessionalsScreen()
        }

        // Connect (Discover)
        composable<Connect> {
            DiscoverScreen(
                onNavigateToHouseListings = {
                    navController.navigate(HouseListings)
                },
                onNavigateToJobListings = {
                    navController.navigate(JobListings)
                },
                onNavigateToAllJobs = {
                    navController.navigate(JobListings)
                },
                onNavigateToAllHousing = {
                    navController.navigate(HouseListings)
                },
                onNavigateToAllProviders = {
                    navController.navigate(Professionals)
                },
                onNavigateToAllServices = {},
                onNavigateToAllSupport = {},
                isGuestMode = isGuestMode,
                sharedViewModel = sharedViewModel
            )
        }

        // Profile
        composable<Profile> {
            ProfileScreen(
                isGuestMode = isGuestMode,
                sharedViewModel = sharedViewModel
            )
        }

        // House Details
        composable<HouseDetails> {
            val listing = selectedListingForViewing
            if (listing != null) {
                HouseDetailsScreen(
                    housingListing = listing,
                    onNavigateBack = {
                        navController.popBackStack()
                        onViewingSelected(null)
                    },
                    onBookClick = { housingListing ->
                        onBookingSelected(housingListing)
                        navController.navigate(BookViewing)
                    }
                )
            } else {
                LaunchedEffect(Unit) {
                    navController.popBackStack()
                }
            }
        }

        // Admin House Details
        composable<AdminHouseDetails> {
            val listing = selectedListingForAdminView
            if (listing != null) {
                AdminHouseDetailsScreen(
                    housingListing = listing,
                    onNavigateBack = {
                        navController.popBackStack()
                        onAdminViewSelected(null)
                    },
                    onEditListing = { id -> println("Edit listing: $id") },
                    onDuplicateListing = { id -> println("Duplicate listing: $id") },
                    onArchiveListing = { id -> println("Archive listing: $id") },
                    onDeleteListing = { id ->
                        println("Delete listing: $id")
                        navController.popBackStack()
                        onAdminViewSelected(null)
                    },
                    onPauseListing = { id -> println("Pause listing: $id") },
                    onResumeListing = { id -> println("Resume listing: $id") },
                    onMarkAvailable = { id -> println("Mark available: $id") },
                    onMarkRented = { id -> println("Mark rented: $id") },
                    onMarkSold = { id -> println("Mark sold: $id") },
                    onViewInquiries = { id -> println("View inquiries: $id") },
                    onShareListing = { id -> println("Share listing: $id") },
                    onViewLogs = { id -> println("View logs: $id") },
                    onCopyListingLink = { id -> println("Copy link: $id") }
                )
            } else {
                LaunchedEffect(Unit) {
                    navController.popBackStack()
                }
            }
        }

        // Book Viewing
        composable<BookViewing> {
            selectedListingForBooking?.let { listing ->
                BookViewingScreen(
                    housingListing = listing,
                    onNavigateBack = {
                        navController.popBackStack()
                        onBookingSelected(null)
                    }
                )
            } ?: run {
                LaunchedEffect(Unit) {
                    navController.popBackStack()
                }
            }
        }

        // House Listings
        composable<HouseListings> {
            val viewModel: HouseListingsViewModel = hiltViewModel()
            HouseListingsScreen(
                viewModel = viewModel,
                onListingClick = { housingListing ->
                    onViewingSelected(housingListing)
                    navController.navigate(HouseDetails)
                },
                onBookClick = { housingListing ->
                    onBookingSelected(housingListing)
                    navController.navigate(BookViewing)
                },
                onPostListingClick = {
                    if (!isGuestMode) onShowSheetChange(true)
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Job Listings
        composable<JobListings> {
            JobListingsScreen(
                onListingClick = { dashboardJob ->
                    val detailsJob = quickConvertToDetailsJob(dashboardJob)
                    onJobViewingSelected(detailsJob)
                    navController.navigate(JobDetails)
                },
                onPostListingClick = {
                    if (!isGuestMode) onShowSheetChange(true)
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Job Details
        composable<JobDetails> {
            val jobListing = selectedJobForViewing
            if (jobListing != null) {
                JobDetailsScreen(
                    jobListing = jobListing,
                    onNavigateBack = {
                        navController.popBackStack()
                        onJobViewingSelected(null)
                    },
                    onApplyClick = { job -> println("Apply for job: ${job.title}") },
                    onMessageEmployerClick = { job -> println("Message employer for: ${job.title}") },
                    onSaveToggle = { jobId, isSaved -> println("Job $jobId saved: $isSaved") },
                    onBookmarkClick = { job -> println("Bookmark clicked for: ${job.title}") }
                )
            } else {
                LaunchedEffect(Unit) {
                    navController.popBackStack()
                }
            }
        }

        // Admin Job Details
        composable<AdminJobDetails> {
            val jobListing = selectedAdminJobForViewing
            if (jobListing != null) {
                AdminJobDetailsScreen(
                    jobListing = jobListing,
                    onNavigateBack = {
                        navController.popBackStack()
                        onAdminJobViewingSelected(null)
                    },
                    onEditJob = { jobId -> println("Edit job: $jobId") },
                    onDuplicateJob = { jobId -> println("Duplicate job: $jobId") },
                    onArchiveJob = { jobId -> println("Archive job: $jobId") },
                    onDeleteJob = { jobId -> println("Delete job: $jobId") },
                    onPauseJob = { jobId -> println("Pause job: $jobId") },
                    onResumeJob = { jobId -> println("Resume job: $jobId") },
                    onCloseJob = { jobId -> println("Close job: $jobId") },
                    onViewApplicants = { jobId -> println("View applicants for: $jobId") },
                    onShareJob = { jobId -> println("Share job: $jobId") },
                    onViewLogs = { jobId -> println("View logs for: $jobId") },
                    onCopyJobLink = { jobId -> println("Copy link for: $jobId") }
                )
            } else {
                LaunchedEffect(Unit) {
                    navController.popBackStack()
                }
            }
        }

        // Post Job
        composable<PostJob> {
            JobPostScreen.Content(onBack = { navController.popBackStack() })
        }

        // Post Service
        composable<PostService> {
            PostServiceScreen(onBack = { navController.popBackStack() })
        }

        // Post Housing
        composable<PostHousing> {
            HousingPostScreen.Content(onBack = { navController.popBackStack() })
        }

        // My Listings
        composable<MyListings> {
            val myListingsViewModel: MyListingsViewModel = hiltViewModel()
            MyListingsScreen(
                onListingClick = { listingUiModel ->
                    println("Generic listing clicked: ${listingUiModel.title}")
                },
                onJobClick = { adminJobListing ->
                    onAdminJobViewingSelected(adminJobListing)
                    navController.navigate(AdminJobDetails)
                },
                onHousingViewClick = { housingListing ->
                    onAdminViewSelected(housingListing)
                    navController.navigate(AdminHouseDetails)
                },
                onPostListingClick = {
                    if (!isGuestMode) onShowSheetChange(true)
                },
                viewModel = myListingsViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

// Main Screen Scaffold with Bottom Navigation
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreenScaffold(
    navController: NavHostController,
    currentDestination: NavDestination?,
    visibleRoutes: List<TopLevelRoute>,
    isGuestMode: Boolean,
    showSheet: Boolean,
    onShowSheetChange: (Boolean) -> Unit,
    sheetState: SheetState,
    showWelcomeSnackbar: Boolean,
    welcomeMessage: String,
    snackbarType: SnackbarType,
    onSnackbarDismiss: () -> Unit,
    sharedViewModel: DashboardSharedViewModel,
    content: @Composable () -> Unit,

) {
    Scaffold(
        bottomBar = {
            NavigationBar(
                modifier = Modifier.fillMaxWidth()
            ) {
                visibleRoutes.forEach { route ->
                    val isSelected = currentDestination?.hierarchy?.any {
                        it.route == route.route::class.qualifiedName
                    } == true

                    NavigationBarItem(
                        icon = {
                            Icon(
                                route.icon,
                                contentDescription = route.contentDescription,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = {
                            Text(
                                route.label,
                                fontSize = 12.sp
                            )
                        },
                        selected = isSelected,
                        onClick = {
                            if (currentDestination?.route != route.route) {
                                navController.navigate(route.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.tertiary,
                            selectedTextColor = MaterialTheme.colorScheme.tertiary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        },
        floatingActionButton = {
            if (!isGuestMode) {
                PulsingPostFab(
                    onClick = { onShowSheetChange(true) }
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Main content
            content()

            // Snackbar positioned at bottom with small padding - using normal Box with Z-index
            if (showWelcomeSnackbar && welcomeMessage.isNotBlank()) {
                androidx.compose.animation.AnimatedVisibility(
                    visible = true,
                    enter = androidx.compose.animation.slideInVertically(
                        initialOffsetY = { it }
                    ) + androidx.compose.animation.fadeIn(),
                    exit = androidx.compose.animation.slideOutVertically(
                        targetOffsetY = { it }
                    ) + androidx.compose.animation.fadeOut(),
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(bottom = 16.dp)
                        .zIndex(100f)  // Add zIndex to ensure it floats above content
                ) {
                    PivotaSnackbar(
                        message = welcomeMessage,
                        type = snackbarType,
                        duration = 3000L,
                        onDismiss = onSnackbarDismiss
                    )
                }
            }
        }
    }

    // Bottom Sheet (kept outside Scaffold as it's a modal)
    if (showSheet && !isGuestMode) {
        PostOptionsBottomSheet(
            sheetState = sheetState,
            onDismiss = { onShowSheetChange(false) },
            onOptionSelected = { category ->
                onShowSheetChange(false)
                when (category) {
                    "jobs" -> navController.navigate(PostJob)
                    "housing" -> navController.navigate(PostHousing)
                    "support" -> navController.navigate(PostSupport)
                    "service" -> navController.navigate(PostService)
                }
            }
        )
    }
}

// No Bottom Navigation Scaffold for detail screens
@Composable
fun NoBottomNavScaffold(
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        content()
    }
}

@Composable
fun OfflineWarningBanner(
    message: String,
    onDismiss: () -> Unit
) {
    var visible by remember { mutableStateOf(true) }

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(initialOffsetY = { -it }),
        exit = slideOutVertically(targetOffsetY = { -it })
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.WifiOff,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = message,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = { visible = false; onDismiss() }) {
                    Icon(Icons.Default.Close, contentDescription = "Dismiss", modifier = Modifier.size(16.dp))
                }
            }
        },
        floatingActionButton = {
            if (!isGuestMode) {
                PulsingPostFab(
                    onClick = { onShowSheetChange(true) }
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            // Main content
            content()

            // ✅ Snackbar INSIDE the Box - positioned at bottom
            if (showWelcomeSnackbar && welcomeMessage.isNotBlank()) {
                PivotaSnackbar(
                    message = welcomeMessage,
                    type = snackbarType,
                    duration = 3000L,  // Reduced to 3 seconds
                    onDismiss = onSnackbarDismiss
                )
            }
        }
    }

    // Bottom Sheet (kept outside Scaffold as it's a modal)
    if (showSheet && !isGuestMode) {
        PostOptionsBottomSheet(
            sheetState = sheetState,
            onDismiss = { onShowSheetChange(false) },
            onOptionSelected = { category ->
                onShowSheetChange(false)
                when (category) {
                    "jobs" -> navController.navigate(PostJob)
                    "housing" -> navController.navigate(PostHousing)
                    "support" -> navController.navigate(PostSupport)
                    "service" -> navController.navigate(PostService)
                }
            }
        )
    }
}

// No Bottom Navigation Scaffold for detail screens
@Composable
fun NoBottomNavScaffold(
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        content()
    }
}