package com.example.pivota.auth.presentation.composables

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.*
import com.example.pivota.R
import com.example.pivota.auth.presentation.state.SignupUiState
import com.example.pivota.auth.presentation.viewModel.SignupViewModel
import com.example.pivota.core.presentations.composables.OtpVerificationDialog
import com.example.pivota.core.presentations.composables.PivotaFullScreenLoading
import com.example.pivota.core.presentations.composables.PivotaSnackbar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun RegistrationFormContent(
    viewModel: SignupViewModel,
    onRegisterSuccess: (String) -> Unit,
    onLoginLinkClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    val uiState by viewModel.uiState.collectAsState()
    val formState by viewModel.formState.collectAsState()
    val otpValues by viewModel.otpValues.collectAsState()
    val resendCount by viewModel.resendCount.collectAsState()

    // Snackbar state
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()
    val snackbarType by viewModel.snackbarType.collectAsState()

    val coroutineScope = rememberCoroutineScope()

    var passwordVisible by remember { mutableStateOf(false) }
    var showOtpDialog by remember { mutableStateOf(false) }
    var isVerifying by remember { mutableStateOf(false) }
    var otpError by remember { mutableStateOf<String?>(null) }
    var countdown by remember { mutableStateOf(0) }

    // Track if we're in OTP request or verification
    val isRequestingOtp = uiState is SignupUiState.Loading && !showOtpDialog
    val isVerifyingOtp = uiState is SignupUiState.Loading && showOtpDialog

    // Lottie animation for the form header
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.signup_animation)
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        isPlaying = true
    )

    // Countdown timer for resend button
    LaunchedEffect(uiState) {
        if (uiState is SignupUiState.OtpSent) {
            countdown = 60
            while (countdown > 0) {
                delay(1000)
                countdown--
            }
        }
    }

    // Handle OTP dialog visibility based on UI state
    LaunchedEffect(uiState) {
        when (uiState) {
            is SignupUiState.OtpSent -> {
                showOtpDialog = true
                isVerifying = false
                otpError = null
            }
            is SignupUiState.Loading -> {
                isVerifying = true
            }
            is SignupUiState.Success -> {
                showOtpDialog = false
                onRegisterSuccess(formState.email)
            }
            is SignupUiState.Error -> {
                isVerifying = false
                if (showOtpDialog) {
                    otpError = (uiState as SignupUiState.Error).message
                }
            }
            else -> {}
        }
    }

    // Design-consistent field colors and shapes
    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
        focusedContainerColor = MaterialTheme.colorScheme.surface,
        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer
    )
    val fieldShape = RoundedCornerShape(12.dp)

    Box(modifier = Modifier.fillMaxSize()) {
        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp)
        ) {
            /* ───────── HEADER WITH LOTTIE ANIMATION ───────── */
            Spacer(Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentAlignment = Alignment.Center
            ) {
                LottieAnimation(
                    composition = composition,
                    progress = { progress },
                    modifier = Modifier.size(150.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Last Step! Personal Details",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Let's get you started on your journey",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "Create your account in just a few steps",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    ),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(32.dp))

            // First Name
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

            // Last Name
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

            // Phone (Optional)
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

            // Email
            OutlinedTextField(
                value = formState.email,
                onValueChange = viewModel::updateEmail,
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = fieldShape
            )
            Spacer(Modifier.height(12.dp))

            // Password
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

            Spacer(Modifier.height(16.dp))

            // Terms and Conditions Checkbox
            PivotaCheckBox(
                checked = formState.agreeTerms,
                onCheckedChange = viewModel::updateAgreeTerms,
                text = "I agree to the terms and conditions"
            )

            Spacer(Modifier.height(24.dp))

            // Register Button
            Button(
                onClick = {
                    if (formState.agreeTerms && formState.email.isNotEmpty() && formState.password.isNotEmpty()) {
                        viewModel.startSignup()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = formState.agreeTerms && formState.email.isNotEmpty() && formState.password.isNotEmpty() && !isRequestingOtp,
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                if (isRequestingOtp) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sending code...", color = Color.White)
                } else {
                    Text("Create Account", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                }
            }

            Spacer(Modifier.height(24.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth().padding(bottom = 40.dp)
            ) {
                Text(
                    text = "Already have an account? ",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                Text(
                    text = "Login",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { onLoginLinkClick() }
                )
            }
        }

        // Full screen loading for OTP request and verification
        if (isRequestingOtp) {
            PivotaFullScreenLoading(
                message = "Sending verification code to\n${formState.email}"
            )
        }

        if (isVerifyingOtp) {
            PivotaFullScreenLoading(
                message = "Verifying your code\nCreating your account..."
            )
        }

        // Snackbar for all messages (errors, successes, warnings)
        if (snackbarMessage != null) {
            PivotaSnackbar(
                message = snackbarMessage!!,
                type = snackbarType,
                onDismiss = { viewModel.clearSnackbar() }
            )
        }
    }

    // Use shared OtpVerificationDialog
    if (showOtpDialog) {
        OtpVerificationDialog(
            email = formState.email,
            otpValues = otpValues,
            onOtpDigitChange = { index, value -> viewModel.updateOtpDigit(index, value) },
            isVerifying = isVerifying,
            otpError = otpError,
            countdown = countdown,
            resendCount = resendCount,
            title = "Verify Your Email",
            description = "We've sent a verification code to",
            verifyButtonText = "Verify & Create Account",
            onVerify = {
                val code = otpValues.joinToString("")
                if (code.length == 6) {
                    viewModel.verifyAndRegister(code)
                } else {
                    otpError = "Please enter a valid 6-digit code"
                }
            },
            onResend = {
                viewModel.incrementResendCount()
                viewModel.requestSignupOtp(formState.email)
                otpError = null
            },
            onCancel = {
                if (!isVerifying) {
                    showOtpDialog = false
                    viewModel.resetState()
                    otpError = null
                }
            }
        )
    }
}