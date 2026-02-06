import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import com.example.pivota.dashboard.presentation.composables.Dashboard
import com.example.pivota.dashboard.presentation.composables.Discover
import com.example.pivota.dashboard.presentation.composables.Profile
import com.example.pivota.dashboard.presentation.composables.Providers
import com.example.pivota.dashboard.presentation.composables.SmartMatch

import com.example.pivota.dashboard.presentation.composables.TopLevelRoute

// Top-level dashboard routes for bottom navigation (MVP1)
val topLevelRoutes = listOf(
    TopLevelRoute(
        route = Dashboard,
        label = "Dashboard",
        icon = Icons.Default.Dashboard,
        contentDescription = "Dashboard Overview",
        requiresAuth = true
    ),
    TopLevelRoute(
        route = Providers,
        label = "Providers",
        icon = Icons.Default.Groups,
        contentDescription = "Browse Service Providers",
        requiresAuth = false
    ),
    TopLevelRoute(
        route = Discover,
        label = "Discover",
        icon = Icons.Default.Explore,
        contentDescription = "Discover Opportunities",
        requiresAuth = false
    ),
    TopLevelRoute(
        route = SmartMatch,
        label = "SmartMatch",
        icon = Icons.Default.AutoAwesome,
        contentDescription = "Personalized Matches",
        requiresAuth = true
    ),
    TopLevelRoute(
        route = Profile,
        label = "Profile",
        icon = Icons.Default.AccountCircle,
        contentDescription = "User Profile and Settings",
        requiresAuth = true
    )
)
