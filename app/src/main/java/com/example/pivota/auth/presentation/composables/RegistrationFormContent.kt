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
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.ui.unit.sp
import com.example.pivota.R
import com.example.pivota.auth.domain.model.AccountType
import com.example.pivota.auth.presentation.state.SignupUiState
import com.example.pivota.auth.presentation.viewModel.SignupViewModel

@Composable
fun RegistrationFormContent(
    viewModel: SignupViewModel = androidx.hilt.navigation.compose.hiltViewModel(),
    onRegisterSuccess: (String) -> Unit, // Still hoisted to handle navigation to OTP
    onLoginLinkClick: () -> Unit,

) {
    val scrollState = rememberScrollState()
    val uiState by viewModel.uiState.collectAsState()

    /* ───────── STATE ───────── */
    var accountType by remember { mutableStateOf("Individual") }

    // Individual
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    // Organisation
    var orgName by remember { mutableStateOf("") }
    var orgType by remember { mutableStateOf("") }
    var orgEmail by remember { mutableStateOf("") }
    var orgPhone by remember { mutableStateOf("") }
    var orgAddress by remember { mutableStateOf("") }
    var adminFirstName by remember { mutableStateOf("") }
    var adminLastName by remember { mutableStateOf("") }

    // Shared
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var agreeTerms by remember { mutableStateOf(false) }

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
        focusedContainerColor = MaterialTheme.colorScheme.surface,
        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer
    )

    /* ───────── UI STATE HANDLING ───────── */
    LaunchedEffect(uiState) {
        if (uiState is SignupUiState.OtpSent) {
            // Navigate to OTP screen using the email from VM
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

            Text(
                "PivotaConnect",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Connect, Discover, Grow",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }

        Spacer(Modifier.height(24.dp))

        /* ───────── FORM ───────── */
        CustomSegmentedToggle(
            options = listOf("Individual", "Organisation"),
            selected = accountType,
            onSelect = { accountType = it }
        )

        Spacer(Modifier.height(16.dp))

        /* ───────── INDIVIDUAL ───────── */
        if (accountType == "Individual") {
            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("First Name") },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                singleLine = true,
                shape = RoundedCornerShape(8.dp)
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Last Name") },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                singleLine = true,
                shape = RoundedCornerShape(8.dp)
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                shape = RoundedCornerShape(8.dp)
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = RoundedCornerShape(8.dp)
            )
        }

        /* ───────── ORGANISATION ───────── */
        if (accountType == "Organisation") {
            OutlinedTextField(
                value = orgName,
                onValueChange = { orgName = it },
                label = { Text("Organisation Name") },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                shape = RoundedCornerShape(8.dp)
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = orgType,
                onValueChange = { orgType = it },
                label = { Text("Organisation Type") },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                shape = RoundedCornerShape(8.dp)
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = orgEmail,
                onValueChange = { orgEmail = it },
                label = { Text("Official Email") },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = RoundedCornerShape(8.dp)
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = orgPhone,
                onValueChange = { orgPhone = it },
                label = { Text("Official Phone (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                shape = RoundedCornerShape(8.dp)
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = orgAddress,
                onValueChange = { orgAddress = it },
                label = { Text("Physical Address") },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                shape = RoundedCornerShape(8.dp)
            )
            Spacer(Modifier.height(16.dp))
            Text("Administrator Details", fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = adminFirstName,
                onValueChange = { adminFirstName = it },
                label = { Text("Admin First Name") },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                shape = RoundedCornerShape(8.dp)
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = adminLastName,
                onValueChange = { adminLastName = it },
                label = { Text("Admin Last Name") },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                shape = RoundedCornerShape(8.dp)
            )
        }

        Spacer(Modifier.height(12.dp))

        /* ───────── PASSWORD ───────── */
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
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
            checked = agreeTerms,
            onCheckedChange = { agreeTerms = it },
            text = "I agree to the terms and conditions"
        )

        Spacer(Modifier.height(24.dp))

        /* ───────── REGISTER ───────── */
        Button(
            onClick = {
                if (accountType == "Organisation") {
                    val organization = AccountType.Organization(
                        orgUuid = "",
                        orgName = orgName,
                        orgType = orgType,
                        orgEmail = orgEmail,
                        orgPhone = orgPhone,
                        orgAddress = orgAddress,
                        adminFirstName = adminFirstName,
                        adminLastName = adminLastName
                    )
                    viewModel.startSignup(
                        email = orgEmail,
                        password = password,
                        phone = orgPhone,
                        isOrganization = true,
                        organization = organization
                    )
                } else {
                    viewModel.startSignup(
                        email = email,
                        password = password,
                        phone = phone,
                        isOrganization = false,
                        firstName = firstName,
                        lastName = lastName
                    )
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            enabled = uiState !is SignupUiState.Loading && agreeTerms,
            shape = RoundedCornerShape(28.dp)
        ) {
            if (uiState is SignupUiState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    strokeWidth = 2.dp,
                    color = Color.White
                )
            } else {
                Text("Register", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }

        Spacer(Modifier.height(24.dp))

        /* ───────── ERROR HANDLING ───────── */
        if (uiState is SignupUiState.Error) {
            Text(
                text = (uiState as SignupUiState.Error).message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

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