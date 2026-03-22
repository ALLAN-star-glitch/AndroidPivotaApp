package com.example.pivota.dashboard.presentation.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import androidx.window.core.layout.WindowSizeClass
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
import topLevelRoutes
import java.util.Date
import java.util.concurrent.TimeUnit

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
    isGuestMode: Boolean = false
) {
    val navController = rememberNavController()
    val sheetState = rememberModalBottomSheetState()
    var showSheet by remember { mutableStateOf(false) }

    // Separate state for different navigation flows
    var selectedListingForBooking by remember { mutableStateOf<HousingListingUiModel?>(null) }
    var selectedListingForViewing by remember { mutableStateOf<HousingListingUiModel?>(null) }
    var selectedListingForAdminView by remember { mutableStateOf<HousingListingUiModel?>(null) }

    // State for user-facing job details navigation
    var selectedJobForViewing by remember { mutableStateOf<DetailsJobListingUiModel?>(null) }

    // NEW: State for admin job details navigation
    var selectedAdminJobForViewing by remember { mutableStateOf<AdminJobListingUiModel?>(null) }

    val navigationItemColors = NavigationSuiteDefaults.itemColors(
        navigationBarItemColors = NavigationBarItemDefaults.colors(
            indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
            selectedIconColor = Color(0xFFE9C16C),
            selectedTextColor = Color(0xFFE9C16C),
            unselectedIconColor = Color(0xFFF2DCA0),
            unselectedTextColor = Color(0xFFF2DCA0),
        ),
        navigationRailItemColors = NavigationRailItemDefaults.colors(
            indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
            selectedIconColor = Color(0xFFE9C16C),
            selectedTextColor = Color(0xFFE9C16C),
            unselectedIconColor = Color(0xFFF2DCA0),
            unselectedTextColor = Color(0xFFF2DCA0),
        )
    )

    val adaptiveInfo = currentWindowAdaptiveInfo()
    val layoutType = with(adaptiveInfo) {
        if (windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)) {
            NavigationSuiteType.NavigationRail
        } else {
            NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(this)
        }
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationSuiteScaffold(
        layoutType = layoutType,
        navigationSuiteItems = {
            topLevelRoutes.forEach { route ->
                item(
                    icon = { Icon(route.icon, contentDescription = route.contentDescription) },
                    label = { Text(route.label) },
                    selected = currentDestination?.hierarchy?.any { it.route == route.route::class.qualifiedName } == true,
                    onClick = {
                        if (currentDestination?.route != route.route) {
                            navController.navigate(route.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    colors = navigationItemColors
                )
            }
        },
        navigationSuiteColors = NavigationSuiteDefaults.colors(
            navigationBarContainerColor = MaterialTheme.colorScheme.primary,
            navigationRailContainerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            floatingActionButton = {
                // Only show FAB if not in guest mode, or if guest mode allows posting
                if (!isGuestMode) {
                    PulsingPostFab(onClick = { showSheet = true }, modifier = Modifier.padding(bottom = 40.dp))
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())) {
                NavHost(
                    navController = navController,
                    startDestination = Discover
                ) {
                    composable<Dashboard> {
                        DashboardScreen(
                            onNavigateToListings = {
                                navController.navigate(MyListings)
                            },
                            isGuestMode = isGuestMode
                        )
                    }
                    composable<Professionals> { ProfessionalsScreen() }
                    composable<Discover> {
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
                                // Navigate to professionals listing screen when created
                            },
                            onNavigateToAllServices = {
                                // Navigate to services listing screen when created
                            },
                            onNavigateToAllSupport = {
                                // Navigate to support listing screen when created
                            },
                            onBookHousingClick = { housingListing ->
                                selectedListingForBooking = housingListing
                                navController.navigate(BookViewing)
                            },
                            onViewHousingClick = { housingListing ->
                                selectedListingForViewing = housingListing
                                navController.navigate(HouseDetails)
                            }
                        )
                    }

                    // User-facing House Details Route (for regular users)
                    composable<HouseDetails> {
                        val listing = selectedListingForViewing

                        if (listing != null) {
                            HouseDetailsScreen(
                                housingListing = listing,
                                onNavigateBack = {
                                    navController.popBackStack()
                                    selectedListingForViewing = null
                                },
                                onBookClick = { housingListing ->
                                    selectedListingForBooking = housingListing
                                    navController.navigate(BookViewing)
                                }
                            )
                        } else {
                            LaunchedEffect(Unit) {
                                navController.popBackStack()
                            }
                        }
                    }

                    // Admin-facing House Details Route (for admin users)
                    composable<AdminHouseDetails> {
                        val listing = selectedListingForAdminView

                        if (listing != null) {
                            AdminHouseDetailsScreen(
                                housingListing = listing,
                                onNavigateBack = {
                                    navController.popBackStack()
                                    selectedListingForAdminView = null
                                },
                                onEditListing = { id ->
                                    // Handle edit action
                                    println("Edit listing: $id")
                                },
                                onDuplicateListing = { id ->
                                    println("Duplicate listing: $id")
                                },
                                onArchiveListing = { id ->
                                    println("Archive listing: $id")
                                },
                                onDeleteListing = { id ->
                                    // Handle delete action
                                    println("Delete listing: $id")
                                    navController.popBackStack()
                                    selectedListingForAdminView = null
                                },
                                onPauseListing = { id ->
                                    println("Pause listing: $id")
                                },
                                onResumeListing = { id ->
                                    println("Resume listing: $id")
                                },
                                onMarkAvailable = { id ->
                                    println("Mark available: $id")
                                },
                                onMarkRented = { id ->
                                    println("Mark rented: $id")
                                },
                                onMarkSold = { id ->
                                    println("Mark sold: $id")
                                },
                                onViewInquiries = { id ->
                                    println("View inquiries: $id")
                                },
                                onShareListing = { id ->
                                    println("Share listing: $id")
                                },
                                onViewLogs = { id ->
                                    println("View logs: $id")
                                },
                                onCopyListingLink = { id ->
                                    println("Copy link: $id")
                                }
                            )
                        } else {
                            LaunchedEffect(Unit) {
                                navController.popBackStack()
                            }
                        }
                    }

                    // Book Viewing Route
                    composable<BookViewing> {
                        selectedListingForBooking?.let { listing ->
                            BookViewingScreen(
                                housingListing = listing,
                                onNavigateBack = {
                                    navController.popBackStack()
                                    selectedListingForBooking = null
                                }
                            )
                        } ?: run {
                            LaunchedEffect(Unit) {
                                navController.popBackStack()
                            }
                        }
                    }

                    composable<SmartMatch> { SmartMatchScreen() }
                    composable<Profile> { ProfileScreen(isGuestMode = isGuestMode) }

                    // HouseListings route
                    composable<HouseListings> {
                        val viewModel: com.example.pivota.dashboard.presentation.viewmodels.HouseListingsViewModel = hiltViewModel()

                        HouseListingsScreen(
                            viewModel = viewModel,
                            onListingClick = { housingListing ->
                                selectedListingForViewing = housingListing
                                navController.navigate(HouseDetails)
                            },
                            onBookClick = { housingListing ->
                                selectedListingForBooking = housingListing
                                navController.navigate(BookViewing)
                            },
                            onPostListingClick = {
                                if (!isGuestMode) showSheet = true
                            },
                            onNavigateBack = {
                                navController.popBackStack()
                            }
                        )
                    }

                    // JobListings route - user-facing job listings
                    composable<JobListings> {
                        JobListingsScreen(
                            onListingClick = { dashboardJob ->
                                // Quick convert and navigate to user-facing job details
                                val detailsJob = quickConvertToDetailsJob(dashboardJob)
                                selectedJobForViewing = detailsJob
                                navController.navigate(JobDetails)
                            },
                            onPostListingClick = {
                                if (!isGuestMode) showSheet = true
                            },
                            onNavigateBack = {
                                navController.popBackStack()
                            }
                        )
                    }

                    // User-facing Job Details Route
                    composable<JobDetails> {
                        val jobListing = selectedJobForViewing

                        if (jobListing != null) {
                            JobDetailsScreen(
                                jobListing = jobListing,
                                onNavigateBack = {
                                    navController.popBackStack()
                                    selectedJobForViewing = null
                                },
                                onApplyClick = { job ->
                                    println("Apply for job: ${job.title}")
                                    // Navigate to application form later
                                },
                                onMessageEmployerClick = { job ->
                                    println("Message employer for: ${job.title}")
                                    // Navigate to chat later
                                },
                                onSaveToggle = { jobId, isSaved ->
                                    println("Job $jobId saved: $isSaved")
                                },
                                onBookmarkClick = { job ->
                                    println("Bookmark clicked for: ${job.title}")
                                }
                            )
                        } else {
                            LaunchedEffect(Unit) {
                                navController.popBackStack()
                            }
                        }
                    }

                    // NEW: Admin Job Details Route
                    composable<AdminJobDetails> {
                        val jobListing = selectedAdminJobForViewing

                        if (jobListing != null) {
                            AdminJobDetailsScreen(
                                jobListing = jobListing,
                                onNavigateBack = {
                                    navController.popBackStack()
                                    selectedAdminJobForViewing = null
                                },
                                onEditJob = { jobId ->
                                    println("Edit job: $jobId")
                                    // Navigate to edit screen or open edit modal
                                },
                                onDuplicateJob = { jobId ->
                                    println("Duplicate job: $jobId")
                                    // Handle duplicate
                                },
                                onArchiveJob = { jobId ->
                                    println("Archive job: $jobId")
                                    // Handle archive
                                },
                                onDeleteJob = { jobId ->
                                    println("Delete job: $jobId")
                                    // Handle delete
                                },
                                onPauseJob = { jobId ->
                                    println("Pause job: $jobId")
                                    // Handle pause
                                },
                                onResumeJob = { jobId ->
                                    println("Resume job: $jobId")
                                    // Handle resume
                                },
                                onCloseJob = { jobId ->
                                    println("Close job: $jobId")
                                    // Handle close
                                },
                                onViewApplicants = { jobId ->
                                    println("View applicants for: $jobId")
                                    // Navigate to applicants list
                                },
                                onShareJob = { jobId ->
                                    println("Share job: $jobId")
                                    // Handle share
                                },
                                onViewLogs = { jobId ->
                                    println("View logs for: $jobId")
                                    // Navigate to logs screen
                                },
                                onCopyJobLink = { jobId ->
                                    println("Copy link for: $jobId")
                                    // Handle copy link
                                }
                            )
                        } else {
                            LaunchedEffect(Unit) {
                                navController.popBackStack()
                            }
                        }
                    }

                    // Type-Safe Posting Flows
                    composable<PostJob> {
                        JobPostScreen.Content(onBack = { navController.popBackStack() })
                    }
                    composable<PostService> {
                        PostServiceScreen(onBack = { navController.popBackStack() })
                    }
                    composable<PostHousing> {
                        HousingPostScreen.Content(onBack = { navController.popBackStack() })
                    }

                    // UPDATED: My Listings route with job support
                    composable<MyListings> {
                        MyListingsScreen(
                            onListingClick = { listingUiModel ->
                                // Handle generic listing clicks
                                println("Generic listing clicked: ${listingUiModel.title}")
                            },
                            // NEW: Job click handler for admin view
                            onJobClick = { adminJobListing ->
                                selectedAdminJobForViewing = adminJobListing
                                navController.navigate(AdminJobDetails)
                            },
                            onHousingViewClick = { housingListing ->
                                selectedListingForAdminView = housingListing
                                navController.navigate(AdminHouseDetails)
                            },
                            onPostListingClick = {
                                if (!isGuestMode) showSheet = true
                            },
                            viewModel = MyListingsViewModel(),
                            onNavigateBack = {
                                navController.popBackStack()
                            }
                        )
                    }
                }

                // Extracted Bottom Sheet - only show if not in guest mode
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
            }
        }
    }
}