package com.example.pivota.dashboard.presentation.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
import com.example.pivota.auth.domain.model.User
import com.example.pivota.dashboard.presentation.composables.*
import com.example.pivota.dashboard.presentation.state.HousingListingUiModel
import com.example.pivota.dashboard.presentation.state.JobListingUiModel as DashboardJobListingUiModel
import com.example.pivota.dashboard.presentation.viewmodels.MyListingsViewModel
import com.example.pivota.admin.presentation.screens.AdminHouseDetailsScreen
import com.example.pivota.admin.presentation.screens.AdminJobDetailsScreen
import com.example.pivota.admin.presentation.screens.AdminJobListingUiModel
import com.example.pivota.admin.presentation.screens.JobStatus
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
import com.example.pivota.core.presentations.composables.PivotaSnackbar
import com.example.pivota.core.presentations.composables.SnackbarType
import topLevelRoutes
import java.util.Date
import java.util.concurrent.TimeUnit
import androidx.compose.material3.SheetState
import com.example.pivota.dashboard.presentation.viewmodels.DashboardViewModel

// Quick conversion function - for user-facing job details
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
    user: User? = null,
    accessToken: String? = null,
    refreshToken: String? = null,
    onMessageConsumed: (() -> Unit)? = null
) {
    val navController = rememberNavController()
    val sheetState = rememberModalBottomSheetState()
    var showSheet by remember { mutableStateOf(false) }
    val dashboardViewModel: DashboardViewModel = hiltViewModel()

    // State for showing welcome snackbar
    var showWelcomeSnackbar by remember { mutableStateOf(false) }
    var welcomeMessage by remember { mutableStateOf("") }
    var snackbarType by remember { mutableStateOf(SnackbarType.SUCCESS) }

    // Show snackbar when successMessage is received
    LaunchedEffect(successMessage) {
        if (!successMessage.isNullOrBlank()) {
            welcomeMessage = successMessage
            snackbarType = SnackbarType.SUCCESS
            showWelcomeSnackbar = true
            onMessageConsumed?.invoke()
        }
    }

    // Start token refresh when dashboard loads (only for authenticated users)
    LaunchedEffect(Unit) {
        if (!isGuestMode && accessToken != null) {
            dashboardViewModel.startTokenRefresh()
            println("🔄 Token auto-refresh started")
        }
    }

    // Log user info if available
    LaunchedEffect(user, accessToken) {
        if (user != null) {
            println("🔍 [DashboardScaffold] User logged in: ${user.email}")
            println("🔍 [DashboardScaffold] Access token available: ${accessToken != null}")
            println("🔍 [DashboardScaffold] Refresh token available: ${refreshToken != null}")
        }
    }

    // Separate state for different navigation flows
    var selectedListingForBooking by remember { mutableStateOf<HousingListingUiModel?>(null) }
    var selectedListingForViewing by remember { mutableStateOf<HousingListingUiModel?>(null) }
    var selectedListingForAdminView by remember { mutableStateOf<HousingListingUiModel?>(null) }

    // State for user-facing job details navigation
    var selectedJobForViewing by remember { mutableStateOf<DetailsJobListingUiModel?>(null) }

    // State for admin job details navigation
    var selectedAdminJobForViewing by remember { mutableStateOf<AdminJobListingUiModel?>(null) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Get window size class for responsive layout
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val isTablet = windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.EXPANDED ||
            windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.MEDIUM

    val visibleRoutes = topLevelRoutes

    // For tablets, use NavigationRail instead of NavigationBar
    if (isTablet) {
        // Tablet layout with NavigationRail
        Row(modifier = Modifier.fillMaxSize()) {
            // Navigation Rail on the left
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
                                    fontSize = 11.sp
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

            // Main content
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
                    isGuestMode = isGuestMode,
                    user = user,
                    accessToken = accessToken,
                    showSheet = showSheet,
                    onShowSheetChange = { showSheet = it },
                    sheetState = sheetState,
                    onBookingSelected = { selectedListingForBooking = it },
                    onViewingSelected = { selectedListingForViewing = it },
                    onAdminViewSelected = { selectedListingForAdminView = it },
                    onJobViewingSelected = { selectedJobForViewing = it },
                    onAdminJobViewingSelected = { selectedAdminJobForViewing = it }
                )
            }
        }

        // Bottom Sheet
        if (showSheet && !isGuestMode) {
            PostOptionsBottomSheet(
                sheetState = sheetState,
                onDismiss = { showSheet = false },
                onOptionSelected = { category ->
                    showSheet = false
                    when (category) {
                        "jobs" -> navController.navigate(PostJob)
                        "housing" -> navController.navigate(PostHousing)
                        "support" -> navController.navigate(PostSupport)
                        "service" -> navController.navigate(PostService)
                    }
                }
            )
        }

        // Show PivotaSnackbar for welcome message
        if (showWelcomeSnackbar && welcomeMessage.isNotBlank()) {
            PivotaSnackbar(
                message = welcomeMessage,
                type = snackbarType,
                duration = 5000L,
                onDismiss = {
                    showWelcomeSnackbar = false
                    welcomeMessage = ""
                }
            )
        }
    } else {
        // Mobile layout - Bottom navigation only on main screens
        MobileNavHost(
            navController = navController,
            selectedListingForBooking = selectedListingForBooking,
            selectedListingForViewing = selectedListingForViewing,
            selectedListingForAdminView = selectedListingForAdminView,
            selectedJobForViewing = selectedJobForViewing,
            selectedAdminJobForViewing = selectedAdminJobForViewing,
            isGuestMode = isGuestMode,
            user = user,
            accessToken = accessToken,
            visibleRoutes = visibleRoutes,
            currentDestination = currentDestination,
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
    user: User?,
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
                onSnackbarDismiss = onSnackbarDismiss
            ) {
                DashboardScreen(
                    onNavigateToListings = {
                        navController.navigate(MyListings)
                    },
                    isGuestMode = isGuestMode,
                    user = user,
                    accessToken = accessToken
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
                onSnackbarDismiss = onSnackbarDismiss
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
                    user = user,
                    isGuestMode = isGuestMode
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
                onSnackbarDismiss = onSnackbarDismiss
            ) {
                ProfileScreen(
                    isGuestMode = isGuestMode,
                    user = user
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
                val viewModel: com.example.pivota.dashboard.presentation.viewmodels.HouseListingsViewModel = hiltViewModel()
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
    user: User?,
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
                user = user,
                accessToken = accessToken
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
                user = user,
                isGuestMode = isGuestMode
            )
        }

        // Profile
        composable<Profile> {
            ProfileScreen(
                isGuestMode = isGuestMode,
                user = user
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
            val viewModel: com.example.pivota.dashboard.presentation.viewmodels.HouseListingsViewModel = hiltViewModel()
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
    content: @Composable () -> Unit
) {
    Scaffold(
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .fillMaxWidth()
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
    ) {  _ -> // Suppress the warning with @Suppress annotation on the function
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Main content
            content()

            // Snackbar positioned at bottom with small padding
            if (showWelcomeSnackbar && welcomeMessage.isNotBlank()) {
                PivotaSnackbar(
                    message = welcomeMessage,
                    type = snackbarType,
                    duration = 3000L,
                    onDismiss = onSnackbarDismiss,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp)
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