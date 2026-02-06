package com.example.pivota.dashboard.presentation.composables

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import topLevelRoutes

@Composable
fun CustomBottomBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Get navigation bar insets
    val navBarInsets = WindowInsets.navigationBars.asPaddingValues()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = navBarInsets.calculateBottomPadding() + 50.dp) // Space above system nav bar
            .zIndex(1f),
        contentAlignment = Alignment.BottomCenter
    ) {
        NavigationBar(
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .shadow(elevation = 8.dp, shape = RoundedCornerShape(24.dp))
                .height(64.dp)
                .fillMaxWidth(),
            containerColor = Color(0xFF008080),
            tonalElevation = 8.dp,
        ) {
            topLevelRoutes.forEach { topLevelRoute ->
                val selected = currentDestination?.hierarchy?.any {
                    it.route == topLevelRoute.route::class.qualifiedName
                } == true

                NavigationBarItem(
                    selected = selected,
                    onClick = {
                        navController.navigate(topLevelRoute.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = topLevelRoute.icon,
                            contentDescription = topLevelRoute.label,
                            modifier = Modifier.size(24.dp), // Ensures consistent sizing
                            tint = if (selected) Color.White else Color.Gray
                        )
                    },
                    label = {
                        Text(
                            text = topLevelRoute.label,
                            style = MaterialTheme.typography.labelSmall, // Proper vertical alignment
                            color = if (selected) Color.White else Color.Gray
                        )
                    },
                    alwaysShowLabel = true, // <-- Ensures label is always shown
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent
                    )
                )
            }
        }
    }
}
