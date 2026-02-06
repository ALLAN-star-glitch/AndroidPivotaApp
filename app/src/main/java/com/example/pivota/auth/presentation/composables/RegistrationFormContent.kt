package com.example.pivota.auth.presentation.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pivota.R
import com.example.pivota.auth.presentation.state.SignupUiState
import com.example.pivota.auth.presentation.viewModel.SignupViewModel
import com.example.pivota.core.presentations.composables.UniversalSegmentedToggle

@Composable
fun RegistrationFormContent(
    viewModel: SignupViewModel,
    onRegisterSuccess: (String) -> Unit,
    onLoginLinkClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    val uiState by viewModel.uiState.collectAsState()
    val formState by viewModel.formState.collectAsState()

    var passwordVisible by remember { mutableStateOf(false) }

    // Design-consistent field colors and shapes
    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
        focusedContainerColor = MaterialTheme.colorScheme.surface,
        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer
    )
    val fieldShape = RoundedCornerShape(12.dp)

    // Navigation logic for OTP
    LaunchedEffect(uiState) {
        if (uiState is SignupUiState.OtpSent) {
            onRegisterSuccess(viewModel.pendingEmail)
            viewModel.resetState()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp)
    ) {
        /* ───────── BRANDING ───────── */
        Spacer(Modifier.height(48.dp))
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Pivota Connect Logo",
                modifier = Modifier.size(90.dp)
            )
            Spacer(Modifier.height(12.dp))
            Text("PivotaConnect", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("Connect, Discover, Grow", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        }

        Spacer(Modifier.height(32.dp))

        /* ───────── REUSABLE CORE TOGGLE ───────── */
        UniversalSegmentedToggle(
            options = listOf("Individual", "Organisation"),
            selected = formState.accountType,
            onSelect = { viewModel.updateAccountType(it) },
            iconProvider = { type, tint ->
                Icon(
                    imageVector = if (type == "Individual") Icons.Default.Person else Icons.Default.Business,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = tint
                )
            }
        )

        Spacer(Modifier.height(24.dp))

        /* ───────── DYNAMIC FORM FIELDS ───────── */
        if (formState.accountType == "Individual") {
            // --- Individual Fields ---
            OutlinedTextField(
                value = formState.firstName,
                onValueChange = viewModel::updateFirstName,
                label = { Text("First Name") },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                singleLine = true,
                shape = fieldShape
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = formState.lastName,
                onValueChange = viewModel::updateLastName,
                label = { Text("Last Name") },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                singleLine = true,
                shape = fieldShape
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = formState.phone,
                onValueChange = viewModel::updatePhone,
                label = { Text("Phone (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                shape = fieldShape
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = formState.email,
                onValueChange = viewModel::updateEmail,
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = fieldShape
            )
        } else {
            // --- Organisation Fields ---
            OutlinedTextField(
                value = formState.orgName,
                onValueChange = viewModel::updateOrgName,
                label = { Text("Organisation Name") },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                shape = fieldShape
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = formState.orgType,
                onValueChange = viewModel::updateOrgType,
                label = { Text("Organisation Type") },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                shape = fieldShape
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = formState.orgEmail,
                onValueChange = viewModel::updateOrgEmail,
                label = { Text("Official Email") },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = fieldShape
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = formState.orgAddress,
                onValueChange = viewModel::updateOrgAddress,
                label = { Text("Physical Address") },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                shape = fieldShape
            )
            Spacer(Modifier.height(20.dp))
            Text("Administrator Details", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = formState.adminFirstName,
                onValueChange = viewModel::updateAdminFirstName,
                label = { Text("Admin First Name") },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                shape = fieldShape
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = formState.adminLastName,
                onValueChange = viewModel::updateAdminLastName,
                label = { Text("Admin Last Name") },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                shape = fieldShape
            )
        }

        Spacer(Modifier.height(12.dp))

        /* ───────── PASSWORD ───────── */
        OutlinedTextField(
            value = formState.password,
            onValueChange = viewModel::updatePassword,
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            },
            colors = textFieldColors,
            shape = fieldShape
        )

        Spacer(Modifier.height(12.dp))

        PivotaCheckBox(
            checked = formState.agreeTerms,
            onCheckedChange = viewModel::updateAgreeTerms,
            text = "I agree to the terms and conditions"
        )

        Spacer(Modifier.height(24.dp))

        /* ───────── REGISTER BUTTON ───────── */
        Button(
            onClick = { viewModel.startSignup() },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            enabled = uiState !is SignupUiState.Loading && formState.agreeTerms,
            shape = RoundedCornerShape(28.dp)
        ) {
            if (uiState is SignupUiState.Loading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp, color = Color.White)
            } else {
                Text("Create Account", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }

        Spacer(Modifier.height(20.dp))

        /* ───────── SOCIAL DIVIDER ───────── */
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f), thickness = 1.dp, color = Color.LightGray.copy(alpha = 0.5f))
            Text(
                text = " OR ",
                modifier = Modifier.padding(horizontal = 12.dp),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            HorizontalDivider(modifier = Modifier.weight(1f), thickness = 1.dp, color = Color.LightGray.copy(alpha = 0.5f))
        }

        Spacer(Modifier.height(20.dp))

        /* ───────── GOOGLE SIGNUP ───────── */
        OutlinedButton(
            onClick = { /* Handle Google Auth */ },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(28.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.ic_google),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(12.dp))
                Text("Continue with Google", fontWeight = FontWeight.Medium, color = Color.Black)
            }
        }

        /* ───────── ERROR MESSAGE ───────── */
        if (uiState is SignupUiState.Error) {
            Text(
                text = (uiState as SignupUiState.Error).message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            )
        }

        Spacer(Modifier.height(32.dp))

        /* ───────── FOOTER ───────── */
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth().padding(bottom = 40.dp)
        ) {
            Text("Already have an account? ", color = Color.Gray)
            Text(
                text = "Login",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { onLoginLinkClick() }
            )
        }
    }
}