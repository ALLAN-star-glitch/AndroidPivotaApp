import PreferenceAdaptiveLayout
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun PrefrenceScreen(  onNavigateToDashboardScreen: () -> Unit){
    PreferenceAdaptiveLayout(
        header = "Choose Your Preference",
        welcomeText = "What are you looking for?",
        onNavigateToDashboard = onNavigateToDashboardScreen,
    )
}

