import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.pivota.auth.presentation.composables.google_button.AuthGoogleButton
import com.example.pivota.auth.presentation.composables.checkbox.PivotaCheckBox
import com.example.pivota.core.presentations.composables.text_field.PivotaPasswordField
import com.example.pivota.core.presentations.composables.buttons.PivotaPrimaryButton
import com.example.pivota.core.presentations.composables.buttons.PivotaSecondaryButton
import com.example.pivota.core.presentations.composables.text_field.PivotaTextField

@Composable
fun RegistrationFormContent(topPadding: Dp, showHeader: Boolean = false, isWideScreen: Boolean = false, onRegisterSuccess: () -> Unit={}, onNavigateToLoginScreen: ()-> Unit) {

    // State for input fields
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .padding(top = topPadding) // Push it down slightly, but still inside scroll
            .fillMaxSize()
            .clip(RoundedCornerShape(topEnd = 58.dp))
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

// Only show header if in wide screen (i.e., two-pane layout)
            if (showHeader && isWideScreen) {
                HorizontalDivider(
                    modifier = Modifier
                        .width(100.dp)
                        .padding(bottom = 8.dp)
                        .align(Alignment.CenterHorizontally),
                    thickness = 2.dp,
                    color = Color(0xFFE9C16C)
                )

                Text(
                    text = "REGISTER",
                    style = MaterialTheme.typography.headlineMedium.copy(color = Color(0xFF008080)),
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }

            PivotaTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = "First Name",
                modifier = Modifier.fillMaxWidth(),
            )

            PivotaTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = "Last Name",
                modifier = Modifier.fillMaxWidth(),
            )

            PivotaTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email",
                modifier = Modifier.fillMaxWidth(),
            )

            PivotaTextField(
                value = phone,
                onValueChange = { phone = it },
                label = "Phone Number",
                modifier = Modifier.fillMaxWidth(),
                keyboardType = KeyboardType.Phone
            )

            // Password Input Field
            PivotaPasswordField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                modifier = Modifier.fillMaxWidth(),
            )

            // Confirm Password Input Field
            PivotaPasswordField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = "Confirm Password",
                modifier = Modifier.fillMaxWidth(),
            )

            //Terms and Conditions Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PivotaCheckBox()

            }

            // Register & Login Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                PivotaPrimaryButton(
                    text = "Register",
                    onClick = onRegisterSuccess,
                )
                Text("OR", color = Color.Gray)

                PivotaSecondaryButton(
                    text = "Login",
                    onclick = onNavigateToLoginScreen,
                )
            }

            AuthGoogleButton()
        }
    }
}
