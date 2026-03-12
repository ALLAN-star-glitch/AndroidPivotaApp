import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.pivota.core.presentations.composables.buttons.PivotaPrimaryButton
import com.example.pivota.core.presentations.composables.buttons.PivotaSecondaryButton
import androidx.compose.ui.unit.sp

@Composable
fun WelcomeContent(
    topPadding: Dp,
    header: String = "Welcome to Pivota",
    welcomeText: String = "Jobs, Housing & Support Across Africa",
    onNavigateToRegistrationScreen: () -> Unit,
    onNavigateToLoginScreen: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = topPadding)
            .clip(RoundedCornerShape(topEnd = 150.dp))
            .background(Color.White)
            .zIndex(2f),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp), // Reduced from 12dp to 8dp
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 16.dp, bottom = 24.dp) // Reduced top padding from 24dp to 16dp
        ) {

            /* ───── LOGO & BRAND NAME ───── */
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp) // Reduced from 4dp to 2dp
            ) {
                coil3.compose.AsyncImage(
                    model = com.example.pivota.R.drawable.pivotaconnect_logo_transparent,
                    contentDescription = "Pivota Logo",
                    modifier = Modifier.size(200.dp)
                )
                Text(
                    text = "Pivotaconnect",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                )
            }

            // Removed the Spacer here - letting the 8dp from Arrangement handle it

            /* ───── Header & Welcome ───── */
            Text(
                text = header,
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                ),
                textAlign = TextAlign.Center
            )

            Text(
                text = welcomeText,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )

            // Removed the Spacer here - letting the 8dp from Arrangement handle it

            /* ───── Buttons & Divider ───── */
            PivotaPrimaryButton(
                text = "Get Started",
                onClick = onNavigateToRegistrationScreen,
                modifier = Modifier.fillMaxWidth(0.85f)
            )

            /* ───────── SOCIAL DIVIDER ───────── */
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(0.7f)
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), thickness = 1.dp, color = Color.LightGray)
                Text(
                    text = " OR ",
                    modifier = Modifier.padding(horizontal = 8.dp), // Reduced from 12dp to 8dp
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                HorizontalDivider(modifier = Modifier.weight(1f), thickness = 1.dp, color = Color.LightGray)
            }

            PivotaSecondaryButton(
                text = "Login",
                onclick = onNavigateToLoginScreen,
                modifier = Modifier.fillMaxWidth(0.85f)
            )
        }
    }
}