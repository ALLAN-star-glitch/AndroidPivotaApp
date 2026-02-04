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
            .padding(top = topPadding) // This creates the "gap" where Teal shows
            .clip(RoundedCornerShape(topEnd = 150.dp))
            .background(Color.White)
            .padding(24.dp)
            .zIndex(2f),
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp), // Reduced slightly for better grouping
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {

            /* ───── 0. LOGO & BRAND NAME ───── */
            /* ───── 0. LOGO & BRAND NAME ───── */
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp) // Tight spacing between logo and text
            ) {
                coil3.compose.AsyncImage(
                    model = com.example.pivota.R.drawable.logo,
                    contentDescription = "Pivota Logo",
                    modifier = Modifier.size(80.dp)
                )
                Text(
                    text = "Pivotaconnect",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.ExtraBold,
                        // Now 'sp' will work with the import above
                        letterSpacing = 1.sp
                    )
                )
            }

            /* ───── 2. Header & Welcome ───── */
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

            /* ───── 3. Buttons & Divider ───── */
            PivotaPrimaryButton(
                text = "Get Started",
                onClick = onNavigateToRegistrationScreen,
                modifier = Modifier.fillMaxWidth(0.85f)
            )

            /* ───────── ADJUSTED SOCIAL DIVIDER ───────── */
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(0.7f) // Reduced width to make it look professional
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), thickness = 1.dp, color = Color.LightGray)
                Text(
                    text = " OR ",
                    modifier = Modifier.padding(horizontal = 12.dp),
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