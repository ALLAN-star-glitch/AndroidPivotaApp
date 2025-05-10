package com.example.pivota.dashboard.presentation.screens

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import androidx.window.core.layout.WindowSizeClass
import com.example.pivota.dashboard.presentation.composables.DashboardDestinations
import com.example.pivota.dashboard.presentation.composables.Explore
import com.example.pivota.dashboard.presentation.composables.Manage
import com.example.pivota.dashboard.presentation.composables.Post
import com.example.pivota.dashboard.presentation.composables.Settings
import com.example.pivota.dashboard.presentation.composables.TopLevelRoute
import com.example.pivota.dashboard.presentation.composables.topLevelRoutes
import kotlinx.serialization.Serializable


@Composable
fun DashboardScaffold() {
    val navController = rememberNavController()

    val myNavigationSuiteItemColors = NavigationSuiteDefaults.itemColors(
        navigationBarItemColors = NavigationBarItemDefaults.colors(
            indicatorColor = MaterialTheme.colorScheme.primary,
            selectedIconColor = Color(0xFFE9C16C),
            selectedTextColor = Color(0xFFE9C16C),
            unselectedIconColor = Color(0xFFF2DCA0),
            unselectedTextColor = Color(0xFFF2DCA0)
        ),
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
            topLevelRoutes.forEach { topLevelRoute ->
                item(
                    icon = {
                        Icon(topLevelRoute.icon, contentDescription = topLevelRoute.contentDescription)
                    },
                    label = {
                        Text(topLevelRoute.label)
                    },
                    selected = currentDestination?.hierarchy?.any {
                        it.route == topLevelRoute.route::class.qualifiedName
                    } == true,

                    onClick = {
                        if (currentDestination?.route != topLevelRoute.route) {
                            navController.navigate(topLevelRoute.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    colors = myNavigationSuiteItemColors
                )
            }
        },
        navigationSuiteColors = NavigationSuiteDefaults.colors(
            navigationBarContainerColor = Color(0xAA008080),
        )
    ) {
        NavHost(
            navController = navController,
            startDestination = Explore
        ) {
            composable <Explore>{
                Explore()
            }
            composable<Post> {
                Post()
            }
            composable<Manage> {
                Manage()
            }
            composable<Settings> {
                Settings()
            }
        }
    }
}
