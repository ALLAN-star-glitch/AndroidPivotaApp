import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ConnectWithoutContact
import com.example.pivota.dashboard.presentation.composables.Connect
import com.example.pivota.dashboard.presentation.composables.Dashboard


import com.example.pivota.dashboard.presentation.composables.Profile


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
        route = Connect,
        label = "Connect",
        Icons.Outlined.ConnectWithoutContact,
        contentDescription = "Connect with Opportunities",
        requiresAuth = false
    ),
    TopLevelRoute(
        route = Profile,
        label = "My Account",
        icon = Icons.Default.AccountCircle,
        contentDescription = "User Profile and Settings",
        requiresAuth = true
    )
)
