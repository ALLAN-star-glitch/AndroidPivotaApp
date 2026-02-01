package com.example.pivota.auth.presentation.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pivota.auth.domain.model.AccountType
import com.example.pivota.auth.domain.model.User
import com.example.pivota.auth.domain.model.UserRole
import com.example.pivota.auth.presentation.state.SignupUiState
import com.example.pivota.auth.presentation.viewModel.SignupViewModel
import com.example.pivota.core.presentations.composables.text_field.PivotaPasswordField
import com.example.pivota.core.presentations.composables.buttons.PivotaPrimaryButton
import com.example.pivota.core.presentations.composables.buttons.PivotaSecondaryButton
import com.example.pivota.core.presentations.composables.text_field.PivotaTextField
import androidx.compose.ui.tooling.preview.Preview

/**
 * Stateful wrapper for the Registration Form.
 * Scoped to the AuthFlow ViewModel to persist data until OTP verification.
 */
@Composable
fun RegistrationFormContent(
    topPadding: Dp,
    showHeader: Boolean = false,
    isWideScreen: Boolean = false,
    viewModel: SignupViewModel = hiltViewModel(),
    onSuccess: (String) -> Unit, // Replaces onNavigateToOtp
    onLoginClick: () -> Unit      // Replaces onNavigateToLoginScreen
) {
    val uiState by viewModel.uiState.collectAsState()

    RegistrationFormInternal(
        topPadding = topPadding,
        showHeader = showHeader,
        isWideScreen = isWideScreen,
        uiState = uiState,
        onRegisterClick = { user, pass, isOrg ->
            viewModel.startSignup(user, pass, isOrg)
        },
        onSuccess = onSuccess,
        onLoginClick = onLoginClick
    )
}

/**
 * Stateless internal content for easier testing and previews.
 */
@Composable
private fun RegistrationFormInternal(
    topPadding: Dp,
    showHeader: Boolean,
    isWideScreen: Boolean,
    uiState: SignupUiState,
    onRegisterClick: (User, String, Boolean) -> Unit,
    onSuccess: (String) -> Unit,
    onLoginClick: () -> Unit
) {
    // Form States
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isOrganization by remember { mutableStateOf(false) }
    var hasAgreedToTerms by remember { mutableStateOf(false) }

    // Listen for ViewModel state changes to trigger navigation
    LaunchedEffect(uiState) {
        if (uiState is SignupUiState.OtpSent) {
            onSuccess(email)
        }
    }

    Box(
        modifier = Modifier
            .padding(top = topPadding)
            .fillMaxSize()
            .clip(RoundedCornerShape(topEnd = 58.dp))
            .background(Color.White)
            .padding(24.dp)
            .zIndex(2f)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
        ) {
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
                    "PivotaConnect",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text("Connect, Discover, Grow")
            }

            PivotaTextField(value = firstName, onValueChange = { firstName = it }, label = "First Name", modifier = Modifier.fillMaxWidth())
            PivotaTextField(value = lastName, onValueChange = { lastName = it }, label = "Last Name", modifier = Modifier.fillMaxWidth())
            PivotaTextField(value = email, onValueChange = { email = it }, label = "Email", modifier = Modifier.fillMaxWidth())
            PivotaTextField(value = phone, onValueChange = { phone = it }, label = "Phone Number", modifier = Modifier.fillMaxWidth(), keyboardType = KeyboardType.Phone)
            PivotaPasswordField(value = password, onValueChange = { password = it }, label = "Password", modifier = Modifier.fillMaxWidth())

            // Roles and Terms
            PivotaCheckBox(checked = isOrganization, onCheckedChange = { isOrganization = it }, text = "Registering as an Organization?")
            PivotaCheckBox(checked = hasAgreedToTerms, onCheckedChange = { hasAgreedToTerms = it }, text = "I agree to the Terms and Conditions")

            // Error Display
            if (uiState is SignupUiState.Error) {
                Text(
                    text = uiState.message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            // Action Buttons
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                PivotaPrimaryButton(
                    text = if (uiState is SignupUiState.Loading) "Sending..." else "Register",
                    onClick = {
                        if (hasAgreedToTerms && email.isNotBlank() && password.isNotBlank()) {
                            val selectedRole = if (isOrganization) UserRole.BUSINESS_ADMINISTRATOR else UserRole.GeneralUser
                            val selectedAccountType = if (isOrganization) {
                                // Defaulting orgUuid to empty as it's generated by backend
                                AccountType.Organization(orgUuid = "", orgName = "$firstName $lastName")
                            } else AccountType.Individual

                            val user = User(
                                uuid = "",
                                accountUuid = "",
                                firstName = firstName,
                                lastName = lastName,
                                email = email,
                                personalPhone = phone,
                                role = selectedRole,
                                accountType = selectedAccountType
                            )
                            onRegisterClick(user, password, isOrganization)
                        }
                    }
                )

                Text("OR", color = Color.Gray)

                PivotaSecondaryButton(text = "Login", onclick = onLoginClick)
            }
            AuthGoogleButton()
        }
    }
}

// ───────── PREVIEW ─────────
@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun RegistrationFormPreview() {
    MaterialTheme {
        RegistrationFormInternal(
            topPadding = 100.dp,
            showHeader = true,
            isWideScreen = false,
            uiState = SignupUiState.Idle,
            onRegisterClick = { _, _, _ -> },
            onSuccess = {},
            onLoginClick = {}
        )
    }
}