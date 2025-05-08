import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
fun PreferenceContent(
    topPadding: Dp,
    onNavigateToDashboardScreen: () -> Unit,
) {
    val preferences = listOf("Jobs", "Rentals", "For Sale", "Services", "Healthcare", "Education")
    val selectedPrefs = remember { mutableStateListOf<String>() }

    Box(
        modifier = Modifier
            .padding(top = topPadding)
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
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Text(
                text = "Choose Your Preferences",
                style = MaterialTheme.typography.headlineSmall
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                preferences.forEach { pref ->
                    PreferenceChip(
                        text = pref,
                        isSelected = selectedPrefs.contains(pref),
                        onClick = {
                            if (selectedPrefs.contains(pref)) {
                                selectedPrefs.remove(pref)
                            } else {
                                selectedPrefs.add(pref)
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                PivotaPrimaryButton(
                    text = "Continue",
                    onClick = onNavigateToDashboardScreen,
                    modifier = Modifier.fillMaxWidth(0.85f)
                )
            }

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Already a Member?",
                    style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.primary)
                )
            }

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                PivotaSecondaryButton(
                    text = "Skip",
                    onclick = onNavigateToDashboardScreen,
                    modifier = Modifier.fillMaxWidth(0.85f)
                )
            }
        }
    }
}

@Composable
fun PreferenceChip(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .clip(CircleShape)
            .clickable { onClick() },
        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 2.dp
    ) {
        Text(
            text = text,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp),
            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
