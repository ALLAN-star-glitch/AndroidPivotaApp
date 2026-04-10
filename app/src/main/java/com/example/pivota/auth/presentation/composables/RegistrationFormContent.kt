package com.example.pivota.auth.presentation.composables

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.pivota.core.presentations.composables.buttons.AuthGoogleButton
import com.example.pivota.ui.theme.SuccessGreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun RegistrationFormContent(
    viewModel: SignupViewModel,
    onRegisterSuccess: (String, String, String, User?) -> Unit,
    onLoginLinkClick: () -> Unit,
    onGoogleSignUpClick: () -> Unit
) {
    // Add logging to debug tablet visibility
    LaunchedEffect(Unit) {
        println("🔍 [RegistrationFormContent] Composed - should be visible")
    }

    val scrollState = rememberScrollState()
    val uiState by viewModel.uiState.collectAsState()
    val formState by viewModel.formState.collectAsState()
    val otpValues by viewModel.otpValues.collectAsState()
    val resendCount by viewModel.resendCount.collectAsState()

    // Collect dialog close signal from ViewModel
    val shouldCloseDialog by viewModel.shouldCloseDialog.collectAsState()

    // Main screen snackbar state
    val mainSnackbarMessage by viewModel.mainSnackbarMessage.collectAsState()
    val mainSnackbarType by viewModel.mainSnackbarType.collectAsState()

    // Dialog snackbar state
    val dialogSnackbarMessage by viewModel.dialogSnackbarMessage.collectAsState()
    val dialogSnackbarType by viewModel.dialogSnackbarType.collectAsState()

    val coroutineScope = rememberCoroutineScope()

    var passwordVisible by remember { mutableStateOf(false) }
    var showOtpDialog by remember { mutableStateOf(false) }
    var isVerifying by remember { mutableStateOf(false) }
    var otpError by remember { mutableStateOf<String?>(null) }
    var countdown by remember { mutableStateOf(0) }
    var verificationFailed by remember { mutableStateOf(false) }
    var showContent by remember { mutableStateOf(false) }

    // Animate content entrance
    LaunchedEffect(Unit) {
        delay(300)
        showContent = true
    }

    // Local password validation state
    var localPasswordError by remember { mutableStateOf<String?>(null) }

    // Track if we're in OTP request or verification
    val isRequestingOtp = uiState is SignupUiState.Loading && !showOtpDialog
    val isVerifyingOtp = uiState is SignupUiState.Loading && showOtpDialog

    // Lottie animation
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.signup_animation)
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        isPlaying = true
    )

    // Password validation
    fun validatePassword(password: String): String? {
        return when {
            password.isBlank() -> null
            password.length < 8 -> "Password must be at least 8 characters"
            !password.any { it.isUpperCase() } -> "Password must contain an uppercase letter (A-Z)"
            !password.any { it.isLowerCase() } -> "Password must contain a lowercase letter (a-z)"
            !password.any { it.isDigit() } -> "Password must contain a number (0-9)"
            !password.any { it in "!@#$%^&*()_+-=[]{}|;':\",.<>/?`~" } ->
                "Password must contain a special character"
            else -> null
        }
    }

    fun handlePasswordChange(newValue: String) {
        viewModel.updatePassword(newValue)
        localPasswordError = validatePassword(newValue)
    }

    // Countdown timer
    LaunchedEffect(uiState) {
        if (uiState is SignupUiState.OtpSent) {
            countdown = 60
            while (countdown > 0) {
                delay(1000)
                countdown--
            }
        }
    }

    // Handle dialog close signal
    LaunchedEffect(shouldCloseDialog) {
        if (shouldCloseDialog && showOtpDialog) {
            showOtpDialog = false
            isVerifying = false
            verificationFailed = false
            otpError = null
            viewModel.resetDialogCloseFlag()
        }
    }

    // Handle UI state changes
    LaunchedEffect(uiState) {
        when (uiState) {
            is SignupUiState.OtpSent -> {
                showOtpDialog = true
                isVerifying = false
                verificationFailed = false
                otpError = null
                countdown = 60
                viewModel.resetDialogCloseFlag()
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
                showOtpDialog = false
                isVerifying = false
                val paymentData = uiState as SignupUiState.PaymentRequired
                println("🔍 Payment required: ${paymentData.redirectUrl}")
                viewModel.resetState()
            }
            is SignupUiState.Error -> {
                if (!showOtpDialog) {
                    isVerifying = false
                    verificationFailed = false
                } else {
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

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
        focusedContainerColor = MaterialTheme.colorScheme.surface,
        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer
    )
    val fieldShape = RoundedCornerShape(12.dp)

    val displayPasswordError = localPasswordError

    // Use a Box with fillMaxSize to ensure it takes full space in the right pane
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp)
                .padding(top = 32.dp, bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Reduced spacing for tablet
            Spacer(Modifier.height(16.dp))

            // Animated Lottie Animation - smaller on tablet
            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn(animationSpec = tween(600, easing = FastOutSlowInEasing)) +
                        scaleIn(initialScale = 0.8f, animationSpec = tween(600, easing = FastOutSlowInEasing))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    LottieAnimation(
                        composition = composition,
                        progress = { progress },
                        modifier = Modifier.size(100.dp)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Animated Header
            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn(animationSpec = tween(600, delayMillis = 100, easing = FastOutSlowInEasing)) +
                        slideInVertically(
                            initialOffsetY = { -30 },
                            animationSpec = tween(600, delayMillis = 100, easing = FastOutSlowInEasing)
                        )
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Create your account",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            color = MaterialTheme.colorScheme.primary
                        ),
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = "Join Pivota to access opportunities",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // Google Button at the TOP (after header)
            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn(animationSpec = tween(500, delayMillis = 200, easing = FastOutSlowInEasing)) +
                        slideInVertically(
                            initialOffsetY = { 30 },
                            animationSpec = tween(500, delayMillis = 200, easing = FastOutSlowInEasing)
                        )
            ) {
                Column {
                    AuthGoogleButton(
                        onClick = onGoogleSignUpClick,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(12.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.surfaceVariant
                        )
                        Text(
                            text = " OR sign up with email ",
                            modifier = Modifier.padding(horizontal = 12.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // Animated First Name Field
            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn(animationSpec = tween(500, delayMillis = 300, easing = FastOutSlowInEasing)) +
                        slideInHorizontally(
                            initialOffsetX = { -50 },
                            animationSpec = tween(500, delayMillis = 300, easing = FastOutSlowInEasing)
                        )
            ) {
                OutlinedTextField(
                    value = formState.firstName,
                    onValueChange = viewModel::updateFirstName,
                    label = { Text("First Name") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors,
                    singleLine = true,
                    shape = fieldShape
                )
            }

            Spacer(Modifier.height(10.dp))

            // Animated Last Name Field
            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn(animationSpec = tween(500, delayMillis = 350, easing = FastOutSlowInEasing)) +
                        slideInHorizontally(
                            initialOffsetX = { -50 },
                            animationSpec = tween(500, delayMillis = 350, easing = FastOutSlowInEasing)
                        )
            ) {
                OutlinedTextField(
                    value = formState.lastName,
                    onValueChange = viewModel::updateLastName,
                    label = { Text("Last Name") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors,
                    singleLine = true,
                    shape = fieldShape
                )
            }

            Spacer(Modifier.height(10.dp))

            // Animated Phone Field
            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn(animationSpec = tween(500, delayMillis = 400, easing = FastOutSlowInEasing)) +
                        slideInHorizontally(
                            initialOffsetX = { -50 },
                            animationSpec = tween(500, delayMillis = 400, easing = FastOutSlowInEasing)
                        )
            ) {
                OutlinedTextField(
                    value = formState.phone,
                    onValueChange = viewModel::updatePhone,
                    label = { Text("Phone (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    shape = fieldShape
                )
            }

            Spacer(Modifier.height(10.dp))

            // Animated Email Field
            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn(animationSpec = tween(500, delayMillis = 450, easing = FastOutSlowInEasing)) +
                        slideInHorizontally(
                            initialOffsetX = { -50 },
                            animationSpec = tween(500, delayMillis = 450, easing = FastOutSlowInEasing)
                        )
            ) {
                OutlinedTextField(
                    value = formState.email,
                    onValueChange = viewModel::updateEmail,
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    shape = fieldShape
                )
            }

            Spacer(Modifier.height(10.dp))

            // Animated Password Field
            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn(animationSpec = tween(500, delayMillis = 500, easing = FastOutSlowInEasing)) +
                        slideInHorizontally(
                            initialOffsetX = { -50 },
                            animationSpec = tween(500, delayMillis = 500, easing = FastOutSlowInEasing)
                        )
            ) {
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
                                fontSize = 11.sp
                            )
                        } else if (formState.password.isNotEmpty()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    modifier = Modifier.size(10.dp),
                                    tint = SuccessGreen
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Password meets requirements",
                                    fontSize = 10.sp,
                                    color = SuccessGreen
                                )
                            }
                        } else {
                            Text(
                                text = "Min 8 chars: uppercase, lowercase, number, special",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                )
            }

            Spacer(Modifier.height(12.dp))

            // Animated Terms Checkbox
            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn(animationSpec = tween(500, delayMillis = 600, easing = FastOutSlowInEasing))
            ) {
                PivotaCheckBox(
                    checked = formState.agreeTerms,
                    onCheckedChange = viewModel::updateAgreeTerms,
                    text = "I agree to the terms and conditions"
                )
            }

            Spacer(Modifier.height(20.dp))

            // Animated Create Account Button
            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn(animationSpec = tween(500, delayMillis = 700, easing = FastOutSlowInEasing)) +
                        slideInVertically(
                            initialOffsetY = { 50 },
                            animationSpec = tween(500, delayMillis = 700, easing = FastOutSlowInEasing)
                        )
            ) {
                Button(
                    onClick = {
                        if (formState.agreeTerms && formState.email.isNotEmpty() && displayPasswordError == null && formState.password.isNotEmpty()) {
                            viewModel.startSignup()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    enabled = formState.agreeTerms && formState.email.isNotEmpty() && displayPasswordError == null && formState.password.isNotEmpty() && !isRequestingOtp,
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        disabledContainerColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                    )
                ) {
                    if (isRequestingOtp) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Sending code...", color = Color.White, fontSize = 14.sp)
                    } else {
                        Text("Create Account", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // Animated Login Link
            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn(animationSpec = tween(500, delayMillis = 800, easing = FastOutSlowInEasing)) +
                        slideInVertically(
                            initialOffsetY = { 30 },
                            animationSpec = tween(500, delayMillis = 800, easing = FastOutSlowInEasing)
                        )
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)
                ) {
                    Text(
                        text = "Already have an account? ",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    Text(
                        text = "Login",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable { onLoginLinkClick() }
                    )
                }
            }
        }

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

        if (mainSnackbarMessage != null && !showOtpDialog) {
            PivotaSnackbar(
                message = mainSnackbarMessage!!,
                type = mainSnackbarType,
                duration = 4000L,
                onDismiss = { viewModel.clearMainSnackbar() }
            )
        }
    }

    if (showOtpDialog) {
        OtpVerificationDialog(
            email = formState.email,
            otpValue = otpValues.joinToString(""),
            onOtpChange = { value ->
                viewModel.updateOtpFull(value)
            },
            isVerifying = isVerifying,
            otpError = if (verificationFailed) otpError else null,
            countdown = countdown,
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
                viewModel.resendOtp()
                otpError = null
                isVerifying = false
                verificationFailed = false
                viewModel.clearDialogSnackbar()
            },
            onCancel = {
                if (!isVerifying) {
                    showOtpDialog = false
                    viewModel.resetState()
                    otpError = null
                    verificationFailed = false
                    viewModel.clearDialogSnackbar()
                }
            },
            snackbarMessage = dialogSnackbarMessage,
            snackbarType = dialogSnackbarType,
            onSnackbarDismiss = {
                viewModel.clearDialogSnackbar()
            },
            shouldClose = shouldCloseDialog,
            onDialogClosed = {
                showOtpDialog = false
                isVerifying = false
                verificationFailed = false
                otpError = null
                viewModel.resetDialogCloseFlag()
            }
        )
    }
}