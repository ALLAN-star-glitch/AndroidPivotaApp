import androidx.compose.runtime.Composable
import com.example.pivota.welcome.presentation.composables.adaptive_layout.AdaptiveWelcomeLayout

@Composable
fun WelcomeScreen(
    onNavigateToGetStarted: () -> Unit,
    onNavigateToLoginScreen: () -> Unit
) {
    AdaptiveWelcomeLayout(
        header = "Let's Connect You!",
        welcomeText = "Your all-in-one platform for verified jobs, quality housing, and essential support services across Kenya.",
        onNavigateToGetStarted = onNavigateToGetStarted,
        onNavigateToLoginScreen = onNavigateToLoginScreen
    )
}


