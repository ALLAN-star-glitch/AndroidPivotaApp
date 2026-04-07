import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import com.example.pivota.dashboard.presentation.composables.Dashboard
import com.example.pivota.dashboard.presentation.composables.Discover
import com.example.pivota.dashboard.presentation.composables.Favorites
import com.example.pivota.dashboard.presentation.composables.Professionals
import com.example.pivota.dashboard.presentation.composables.Profile


import com.example.pivota.dashboard.presentation.composables.TopLevelRoute

// Top-level dashboard routes for bottom navigation (MVP1)
val topLevelRoutes = listOf(
    TopLevelRoute(
        route = Dashboard,
        label = "Board",
        icon = Icons.Default.Dashboard,
        contentDescription = "Dashboard Overview",
        requiresAuth = true
    ),
    TopLevelRoute(
        route = Professionals,
        label = "Pros", // short form
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
        route = Favorites,
        label = "Fav",
        icon = Icons.Default.Favorite,
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
