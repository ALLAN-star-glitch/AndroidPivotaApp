import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pivota.dashboard.presentation.screens.ProfileMenuItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileMenuBottomSheet(
    onDismiss: () -> Unit,
    colorScheme: ColorScheme,
    onMyAccountClick: () -> Unit,
    onMyListingsClick: () -> Unit,
    onMyFavoritesClick: () -> Unit,
    onPostClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colorScheme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .background(
                        color = colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(2.dp)
                    )
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Menu Items
            ProfileMenuItem(
                icon = Icons.Outlined.Person,
                title = "My Account",
                subtitle = "Manage your profile and settings",
                onClick = onMyAccountClick,
                colorScheme = colorScheme
            )

            Divider(
                color = colorScheme.outlineVariant.copy(alpha = 0.3f),
                modifier = Modifier.padding(vertical = 4.dp)
            )

            ProfileMenuItem(
                icon = Icons.Outlined.List,
                title = "My Listings",
                subtitle = "View and manage your listings",
                onClick = onMyListingsClick,
                colorScheme = colorScheme
            )

            Divider(
                color = colorScheme.outlineVariant.copy(alpha = 0.3f),
                modifier = Modifier.padding(vertical = 4.dp)
            )

            ProfileMenuItem(
                icon = Icons.Outlined.FavoriteBorder,
                title = "My Favorites",
                subtitle = "Saved opportunities",
                onClick = onMyFavoritesClick,
                colorScheme = colorScheme
            )

            Divider(
                color = colorScheme.outlineVariant.copy(alpha = 0.3f),
                modifier = Modifier.padding(vertical = 4.dp)
            )

            ProfileMenuItem(
                icon = Icons.Outlined.AddCircle,
                title = "Post",
                subtitle = "Create a new listing",
                onClick = onPostClick,
                colorScheme = colorScheme
            )

            Divider(
                color = colorScheme.outlineVariant.copy(alpha = 0.3f),
                modifier = Modifier.padding(vertical = 4.dp)
            )

            ProfileMenuItem(
                icon = Icons.Outlined.Logout,
                title = "Logout",
                subtitle = "Sign out from your account",
                onClick = onLogoutClick,
                colorScheme = colorScheme,
                isDestructive = true
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}