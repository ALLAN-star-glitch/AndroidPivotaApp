import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.pivota.core.presentations.composables.buttons.PivotaPrimaryButton
import com.example.pivota.core.presentations.composables.buttons.PivotaSecondaryButton

@Composable
fun WelcomeContent(topPadding: Dp, onNavigateToRegistrationScreen: () -> Unit, onNavigateToLoginScreen: ()-> Unit) {

    Box(
        modifier = Modifier
            .padding(top = topPadding) // Push it down slightly, but still inside scroll
            .fillMaxSize()
            .clip(RoundedCornerShape(topEnd = 150.dp))
            .background(Color.White)
            .padding(24.dp)
            .zIndex(2f)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp), // Ensures padding doesn't shift form out of view
        ) {



            Text(
                text = "Welcome to Pivota",
                style = MaterialTheme.typography.headlineMedium.copy(color = MaterialTheme.colorScheme.primary)
            )

            Text(
                text = "We connect people across Africa with trusted job opportunities, affordable housing, and reliable services, empowering communities and fostering growth.",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.Black)
            )

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                PivotaPrimaryButton(
                    text = "Get Started",
                    onClick = onNavigateToRegistrationScreen,
                    modifier = Modifier
                        .fillMaxWidth(0.85f) // 85% of the parent width
                )
            }

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center){
                Text(
                    text = "Already a Member?",
                    style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.primary)
                )

            }

            
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                PivotaSecondaryButton(
                    text = "Login",
                    onclick = onNavigateToLoginScreen,
                    modifier = Modifier
                        .fillMaxWidth(0.85f) // 85% of the parent width
                )
            }
        }
    }
}