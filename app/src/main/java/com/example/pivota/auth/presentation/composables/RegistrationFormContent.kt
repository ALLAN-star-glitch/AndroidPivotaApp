package com.example.pivota.auth.presentation.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.pivota.auth.domain.model.User
import com.example.pivota.auth.presentation.state.SignupUiState
import com.example.pivota.auth.presentation.viewModel.SignupViewModel
import com.example.pivota.core.presentations.composables.OtpVerificationDialog
import com.example.pivota.core.presentations.composables.PivotaFullScreenLoading
import com.example.pivota.core.presentations.composables.PivotaSnackbar
import com.example.pivota.core.presentations.composables.SnackbarType
import com.example.pivota.ui.theme.SuccessGreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun RegistrationFormContent(
    viewModel: SignupViewModel,
    onRegisterSuccess: (String, String, String, User?) -> Unit,  // (message, accessToken, refreshToken, user)
    onLoginLinkClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    val uiState by viewModel.uiState.collectAsState()
    val formState by viewModel.formState.collectAsState()
    val otpValues by viewModel.otpValues.collectAsState()
    val resendCount by viewModel.resendCount.collectAsState()

    // Main screen snackbar state (for signup errors, validation errors)
    val mainSnackbarMessage by viewModel.mainSnackbarMessage.collectAsState()
    val mainSnackbarType by viewModel.mainSnackbarType.collectAsState()

    // Dialog snackbar state (for OTP-related messages)
    val dialogSnackbarMessage by viewModel.dialogSnackbarMessage.collectAsState()
    val dialogSnackbarType by viewModel.dialogSnackbarType.collectAsState()

    val coroutineScope = rememberCoroutineScope()

    var passwordVisible by remember { mutableStateOf(false) }
    var showOtpDialog by remember { mutableStateOf(false) }
    var isVerifying by remember { mutableStateOf(false) }
    var otpError by remember { mutableStateOf<String?>(null) }
    var countdown by remember { mutableStateOf(0) }
    var verificationFailed by remember { mutableStateOf(false) }

    // ✅ Local password validation state
    var localPasswordError by remember { mutableStateOf<String?>(null) }

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

    // ✅ Real-time password validation (matches backend requirements)
    fun validatePassword(password: String): String? {
        return when {
            password.isBlank() -> null  // Don't show error while empty
            password.length < 8 -> "Password must be at least 8 characters"
            !password.any { it.isUpperCase() } -> "Password must contain an uppercase letter (A-Z)"
            !password.any { it.isLowerCase() } -> "Password must contain a lowercase letter (a-z)"
            !password.any { it.isDigit() } -> "Password must contain a number (0-9)"
            !password.any { it in "!@#$%^&*()_+-=[]{}|;':\",.<>/?`~" } ->
                "Password must contain a special character"
            else -> null
        }
    }

    // ✅ Update password and local validation
    fun handlePasswordChange(newValue: String) {
        viewModel.updatePassword(newValue)
        localPasswordError = validatePassword(newValue)
    }

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
                verificationFailed = false
                otpError = null
                countdown = 60
                // Show success message inside dialog when it opens
                viewModel.showDialogSnackbar("Verification code sent to your email!", SnackbarType.SUCCESS)
                coroutineScope.launch {
                    delay(3000)
                    viewModel.clearDialogSnackbar()
                }
            }
            is SignupUiState.Loading -> {
                if (showOtpDialog) {
                    isVerifying = true
                    verificationFailed = false
                }
            }
            is SignupUiState.Success -> {
                showOtpDialog = false
                isVerifying = false
                // Pass the success message, tokens, and user to the callback
                val successState = uiState as SignupUiState.Success
                onRegisterSuccess(
                    successState.message,
                    successState.accessToken ?: "",
                    successState.refreshToken ?: "",
                    successState.user
                )
                viewModel.resetState()
            }
            is SignupUiState.PaymentRequired -> {
                // Handle payment required (redirect to payment page)
                showOtpDialog = false
                isVerifying = false
                val paymentData = uiState as SignupUiState.PaymentRequired
                // TODO: Navigate to payment screen with redirectUrl and merchantReference
                println("🔍 Payment required: ${paymentData.redirectUrl}")
                viewModel.resetState()
            }
            is SignupUiState.Error -> {
                if (showOtpDialog) {
                    isVerifying = false
                    verificationFailed = true
                    otpError = (uiState as SignupUiState.Error).message
                    viewModel.showDialogSnackbar(otpError ?: "Verification failed", SnackbarType.ERROR)
                    coroutineScope.launch {
                        delay(4000)
                        viewModel.clearDialogSnackbar()
                    }
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

    // ✅ Display password error (prioritize local validation)
    val displayPasswordError = localPasswordError

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

            // ✅ Password field with real-time validation
            OutlinedTextField(
                value = formState.password,
                onValueChange = { handlePasswordChange(it) },
                label = { Text("Password") },
                placeholder = { Text("Enter your password") },
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
                shape = fieldShape,
                isError = displayPasswordError != null,
                supportingText = {
                    if (displayPasswordError != null) {
                        Text(
                            text = displayPasswordError!!,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp
                        )
                    } else if (formState.password.isNotEmpty()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = SuccessGreen
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Password meets requirements",
                                fontSize = 11.sp,
                                color = SuccessGreen
                            )
                        }
                    } else {
                        Text(
                            text = "Min 8 chars: uppercase, lowercase, number, special (@ $ ! % * ? &)",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )

            Spacer(Modifier.height(16.dp))

            // Terms and Conditions Checkbox
            PivotaCheckBox(
                checked = formState.agreeTerms,
                onCheckedChange = viewModel::updateAgreeTerms,
                text = "I agree to the terms and conditions"
            )

            Spacer(Modifier.height(24.dp))

            // ✅ Register Button - Enabled only when password is valid
            Button(
                onClick = {
                    if (formState.agreeTerms && formState.email.isNotEmpty() && displayPasswordError == null && formState.password.isNotEmpty()) {
                        viewModel.startSignup()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = formState.agreeTerms && formState.email.isNotEmpty() && displayPasswordError == null && formState.password.isNotEmpty() && !isRequestingOtp,
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
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

        // Full screen loading for OTP request
        if (isRequestingOtp) {
            PivotaFullScreenLoading(
                message = "Sending verification code to\n${formState.email}"
            )
        }

        // Full screen loading for OTP verification
        if (isVerifyingOtp) {
            PivotaFullScreenLoading(
                message = "Verifying your code\nCreating your account..."
            )
        }

        // ✅ Main Snackbar for signup errors, validation errors, and success messages
        if (mainSnackbarMessage != null && !showOtpDialog) {
            PivotaSnackbar(
                message = mainSnackbarMessage!!,
                type = mainSnackbarType,
                duration = 4000L,
                onDismiss = { viewModel.clearMainSnackbar() }
            )
        }
    }

    // OTP Verification Dialog with its own snackbar
    if (showOtpDialog) {
        OtpVerificationDialog(
            email = formState.email,
            otpValues = otpValues,
            onOtpDigitChange = { index, value ->
                viewModel.updateOtpDigit(index, value)
            },
            isVerifying = isVerifying,
            otpError = if (verificationFailed) otpError else null,
            countdown = countdown,
            resendCount = resendCount,
            title = "Verify Your Email",
            description = "We've sent a verification code to",
            verifyButtonText = "Verify & Create Account",
            onVerify = {
                val code = otpValues.joinToString("")
                if (code.length == 6) {
                    verificationFailed = false
                    viewModel.verifyAndRegister(code)
                } else {
                    otpError = "Please enter a valid 6-digit code"
                    verificationFailed = true
                    viewModel.showDialogSnackbar(otpError!!, SnackbarType.ERROR)
                    coroutineScope.launch {
                        delay(3000)
                        viewModel.clearDialogSnackbar()
                    }
                }
            },
            onResend = {
                viewModel.incrementResendCount()
                viewModel.requestSignupOtp(formState.email, formState.phone)
                otpError = null
                verificationFailed = false
                viewModel.showDialogSnackbar("New verification code sent!", SnackbarType.SUCCESS)
                coroutineScope.launch {
                    delay(3000)
                    viewModel.clearDialogSnackbar()
                }
            },
            onCancel = {
                if (!isVerifying) {
                    showOtpDialog = false
                    viewModel.resetState()
                    otpError = null
                    verificationFailed = false
                }
            },
            snackbarMessage = dialogSnackbarMessage,
            snackbarType = dialogSnackbarType,
            onSnackbarDismiss = {
                viewModel.clearDialogSnackbar()
            }
        )
    }
}