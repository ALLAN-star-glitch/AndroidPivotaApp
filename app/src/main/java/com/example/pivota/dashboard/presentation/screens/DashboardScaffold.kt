package com.example.pivota.dashboard.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import androidx.window.core.layout.WindowSizeClass
import com.example.pivota.dashboard.presentation.composables.*
import com.example.pivota.listings.presentation.screens.HousingPostScreen
import com.example.pivota.listings.presentation.screens.JobPostScreen
import topLevelRoutes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScaffold() {
    val navController = rememberNavController()
    val sheetState = rememberModalBottomSheetState()
    var showSheet by remember { mutableStateOf(false) }

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
            // Set insets to 0 to prevent the inner Scaffold from adding another layer of padding
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            floatingActionButton = {
                PulsingPostFab(onClick = { showSheet = true })
            }
        ) { innerPadding ->
            Box(modifier = Modifier.fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())) {
                NavHost(
                    navController = navController,
                    startDestination = Discover
                ) {
                    composable<Dashboard> { DashboardScreen() }
                    composable<Providers> { ProvidersScreen() }
                    composable<Discover> { DiscoverScreen() }
                    composable<SmartMatch> { SmartMatchScreen() }
                    composable<Profile> { ProfileScreen() }
                    // Type-Safe Posting Flows
                    composable<PostJob> {
                        JobPostScreen.Content(onBack = { navController.popBackStack() })
                    }
                    composable<PostHousing> {
                        HousingPostScreen.Content(onBack = { navController.popBackStack() })
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