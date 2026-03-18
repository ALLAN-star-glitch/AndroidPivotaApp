package com.example.pivota.dashboard.presentation.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

// Quick conversion function - just enough to see the UI
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

@SuppressLint("ViewModelConstructorInComposable")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScaffold() {
    val navController = rememberNavController()
    val sheetState = rememberModalBottomSheetState()
    var showSheet by remember { mutableStateOf(false) }

    // Separate state for different navigation flows
    var selectedListingForBooking by remember { mutableStateOf<HousingListingUiModel?>(null) }
    var selectedListingForViewing by remember { mutableStateOf<HousingListingUiModel?>(null) }
    var selectedListingForAdminView by remember { mutableStateOf<HousingListingUiModel?>(null) }

    // State for job details navigation - using the details screen's model type
    var selectedJobForViewing by remember { mutableStateOf<DetailsJobListingUiModel?>(null) }

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
                PulsingPostFab(onClick = { showSheet = true })
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
                            }
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
                                onBack = {
                                    navController.popBackStack()
                                    selectedListingForAdminView = null
                                },
                                onApprove = { id ->
                                    // Handle approve action
                                    println("Approve listing: $id")
                                    navController.popBackStack()
                                    selectedListingForAdminView = null
                                },
                                onReject = { id ->
                                    // Handle reject action
                                    println("Reject listing: $id")
                                    navController.popBackStack()
                                    selectedListingForAdminView = null
                                },
                                onEdit = { id ->
                                    // Handle edit action
                                    println("Edit listing: $id")
                                },
                                onDelete = { id ->
                                    // Handle delete action
                                    println("Delete listing: $id")
                                    navController.popBackStack()
                                    selectedListingForAdminView = null
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
                    composable<Profile> { ProfileScreen() }

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
                                showSheet = true
                            },
                            onNavigateBack = {
                                navController.popBackStack()
                            }
                        )
                    }

                    // UPDATED: JobListings route with quick conversion
                    composable<JobListings> {
                        JobListingsScreen(
                            onListingClick = { dashboardJob ->
                                // Quick convert and navigate
                                val detailsJob = quickConvertToDetailsJob(dashboardJob)
                                selectedJobForViewing = detailsJob
                                navController.navigate(JobDetails)
                            },
                            onPostListingClick = {
                                showSheet = true
                            },
                            onNavigateBack = {
                                navController.popBackStack()
                            }
                        )
                    }

                    // ADD THIS: Job Details Route
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

                    // My Listings route with housing view support
                    composable<MyListings> {
                        MyListingsScreen(
                            onListingClick = { listingUiModel ->
                                // Handle non-housing listing clicks
                            },
                            onHousingViewClick = { housingListing ->
                                selectedListingForAdminView = housingListing
                                navController.navigate(AdminHouseDetails)
                            },
                            onPostListingClick = {
                                showSheet = true
                            },
                            viewModel = MyListingsViewModel(),
                            onNavigateBack = {
                                navController.popBackStack()
                            }
                        )
                    }
                }

                // Extracted Bottom Sheet
                if (showSheet) {
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