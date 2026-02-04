package com.example.pivota.auth.presentation.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
        focusedContainerColor = MaterialTheme.colorScheme.surface,
        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer
    )

    // Handle navigation to OTP screen
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

        Spacer(Modifier.height(24.dp))

        /* ───────── FORM TYPE TOGGLE ───────── */
        CustomSegmentedToggle(
            options = listOf("Individual", "Organisation"),
            selected = formState.accountType,
            onSelect = { viewModel.updateAccountType(it) }
        )

        Spacer(Modifier.height(16.dp))

        /* ───────── DYNAMIC FORM FIELDS ───────── */
        if (formState.accountType == "Individual") {
            OutlinedTextField(
                value = formState.firstName,
                onValueChange = viewModel::updateFirstName,
                label = { Text("First Name") },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                singleLine = true,
                shape = RoundedCornerShape(8.dp)
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = formState.lastName,
                onValueChange = viewModel::updateLastName,
                label = { Text("Last Name") },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                singleLine = true,
                shape = RoundedCornerShape(8.dp)
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = formState.phone,
                onValueChange = viewModel::updatePhone,
                label = { Text("Phone (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                shape = RoundedCornerShape(8.dp)
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = formState.email,
                onValueChange = viewModel::updateEmail,
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = RoundedCornerShape(8.dp)
            )
        } else {
            OutlinedTextField(
                value = formState.orgName,
                onValueChange = viewModel::updateOrgName,
                label = { Text("Organisation Name") },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                shape = RoundedCornerShape(8.dp)
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = formState.orgType,
                onValueChange = viewModel::updateOrgType,
                label = { Text("Organisation Type") },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                shape = RoundedCornerShape(8.dp)
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = formState.orgEmail,
                onValueChange = viewModel::updateOrgEmail,
                label = { Text("Official Email") },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = RoundedCornerShape(8.dp)
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = formState.orgAddress,
                onValueChange = viewModel::updateOrgAddress,
                label = { Text("Physical Address") },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                shape = RoundedCornerShape(8.dp)
            )
            Spacer(Modifier.height(16.dp))
            Text("Administrator Details", fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = formState.adminFirstName,
                onValueChange = viewModel::updateAdminFirstName,
                label = { Text("Admin First Name") },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                shape = RoundedCornerShape(8.dp)
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = formState.adminLastName,
                onValueChange = viewModel::updateAdminLastName,
                label = { Text("Admin Last Name") },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                shape = RoundedCornerShape(8.dp)
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
                        contentDescription = null
                    )
                }
            },
            colors = textFieldColors,
            shape = RoundedCornerShape(8.dp)
        )

        Spacer(Modifier.height(8.dp))

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
                CircularProgressIndicator(modifier = Modifier.size(22.dp), strokeWidth = 2.dp, color = Color.White)
            } else {
                Text("Register", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }

        Spacer(Modifier.height(12.dp))

        /* ───────── SOCIAL DIVIDER ───────── */
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
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

        Spacer(Modifier.height(12.dp))

        /* ───────── GOOGLE SIGNUP ───────── */
        OutlinedButton(
            onClick = { /* TODO: Trigger Google Auth Flow */ },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.ic_google),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(12.dp))
                Text("Continue with Google", fontWeight = FontWeight.Medium)
            }
        }

        /* ───────── ERROR MESSAGE ───────── */
        if (uiState is SignupUiState.Error) {
            Text(
                text = (uiState as SignupUiState.Error).message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            )
        }

        Spacer(Modifier.height(16.dp))

        /* ───────── FOOTER ───────── */
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)
        ) {
            Text("Already have an account? ")
            Text(
                text = "Login",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { onLoginLinkClick() }
            )
        }
    }
}