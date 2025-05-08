import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun WelcomeScreen(  onNavigateToRegisterScreen: () -> Unit, onNavigateToLoginScreen: ()->Unit){
AdaptiveWelcomeLayout(
    header = "Find jobs across Africa",
    welcomeText = "Find Jobs Across Africa",
    onNavigateToLoginScreen = onNavigateToLoginScreen,
    onNavigateToRegisterScreen = onNavigateToRegisterScreen,
)
}

