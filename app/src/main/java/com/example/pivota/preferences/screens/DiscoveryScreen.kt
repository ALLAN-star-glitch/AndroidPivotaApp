import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun DiscoveryScreen(
    onContinue: () -> Unit
) {
    DiscoveryAdaptiveLayout(
        header = "Discover Life Opportunities",
        welcomeText = "What are you looking for today?",
        onNavigateToDashboard = onContinue
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewDiscoveryScreen() {
    DiscoveryScreen(onContinue = {})
}
