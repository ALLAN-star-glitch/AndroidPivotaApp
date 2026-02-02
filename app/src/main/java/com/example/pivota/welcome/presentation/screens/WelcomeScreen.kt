import androidx.compose.runtime.Composable
import com.example.pivota.welcome.presentation.composables.adaptive_layout.AdaptiveWelcomeLayout

@Composable
fun WelcomeScreen(
    onNavigateToGetStarted: () -> Unit,
    onNavigateToLoginScreen: () -> Unit
) {
    AdaptiveWelcomeLayout(
        header = "PivotaConnect. Life Opportunities",
        welcomeText = "PivotaConnect bridges the gap between talent and opportunity. Access verified jobs, quality housing, and essential services tailored for you.",
        onNavigateToGetStarted = onNavigateToGetStarted,
        onNavigateToLoginScreen = onNavigateToLoginScreen
    )
}


