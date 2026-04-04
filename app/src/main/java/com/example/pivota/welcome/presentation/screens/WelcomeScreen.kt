import androidx.compose.runtime.Composable
import com.example.pivota.welcome.presentation.composables.adaptive_layout.AdaptiveWelcomeLayout

@Composable
fun WelcomeScreen(
    onNavigateToContinueSetup: () -> Unit,
    onNavigateToContinueWithGoogle: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    AdaptiveWelcomeLayout(
        header = "Let's Connect You!",
        welcomeText = "Your all-in-one platform for verified jobs, quality housing, and essential support services across Kenya.",
        onNavigateToContinueSetup = onNavigateToContinueSetup,
        onNavigateToContinueWithGoogle = onNavigateToContinueWithGoogle,
        onNavigateToLogin = onNavigateToLogin
    )
}