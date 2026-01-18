import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.pivota.core.presentations.screens.AdaptiveWelcomeLayout

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


