package com.example.pivota.auth.presentation.composables

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
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
import androidx.lifecycle.viewModelScope
import com.airbnb.lottie.compose.*
import com.example.pivota.R
import com.example.pivota.auth.domain.model.User
import com.example.pivota.auth.presentation.state.LoginUiState
import com.example.pivota.auth.presentation.viewModel.LoginViewModel
import com.example.pivota.core.presentations.composables.OtpVerificationDialog
import com.example.pivota.core.presentations.composables.PivotaFullScreenLoading
import com.example.pivota.core.presentations.composables.PivotaSnackbar
import com.example.pivota.core.presentations.composables.SnackbarType
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LoginFormContent(
    viewModel: LoginViewModel = androidx.hilt.navigation.compose.hiltViewModel(),
    onLoginSuccess: (User, String, String, String) -> Unit,
    onGoogleLoginClick: () -> Unit,
    onRegisterLinkClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    successMessage: String? = null
) {
    println("🔍 [LoginFormContent] ENTERED WITH successMessage: $successMessage")

    val scrollState = rememberScrollState()
    val uiState by viewModel.uiState.collectAsState()
    val otpValues by viewModel.otpValues.collectAsState()
    val resendCount by viewModel.resendCount.collectAsState()

    // Collect form state from ViewModel (survives rotation)
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val agreeTerms by viewModel.agreeTerms.collectAsState()

    // Main snackbar state (for login errors)
    val mainSnackbarMessage by viewModel.snackbarMessage.collectAsState()
    val mainSnackbarType by viewModel.snackbarType.collectAsState()

    var passwordVisible by remember { mutableStateOf(false) }
    var enableFingerprint by remember { mutableStateOf(false) }
    var showContent by remember { mutableStateOf(false) }

    // Animate content entrance
    LaunchedEffect(Unit) {
        delay(300)
        showContent = true
    }

    // OTP Dialog state
    var showOtpDialog by remember { mutableStateOf(false) }
    var isVerifying by remember { mutableStateOf(false) }
    var otpError by remember { mutableStateOf<String?>(null) }
    var countdown by remember { mutableStateOf(0) }

    // Track verification state
    var verificationFailed by remember { mutableStateOf(false) }
    var dialogSnackbarMessage by remember { mutableStateOf<String?>(null) }
    var dialogSnackbarType by remember { mutableStateOf(SnackbarType.INFO) }

    // Track loading states
    val isLoggingIn = uiState is LoginUiState.Loading && !showOtpDialog

    // Success snackbar state (from password reset)
    var showSimpleSnackbar by remember { mutableStateOf(false) }
    var simpleMessage by remember { mutableStateOf("") }

    LaunchedEffect(successMessage) {
        if (!successMessage.isNullOrBlank()) {
            simpleMessage = successMessage
            showSimpleSnackbar = true
            delay(5000)
            showSimpleSnackbar = false
        }
    }

    // Lottie animation
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.login_animation)
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        isPlaying = true
    )

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
        focusedContainerColor = MaterialTheme.colorScheme.surface,
        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer
    )

    // Handle UI state changes
    LaunchedEffect(uiState) {
        when (uiState) {
            is LoginUiState.OtpSent -> {
                println("🔍 [LoginFormContent] OtpSent state received")
                showOtpDialog = true
                isVerifying = false
                verificationFailed = false
                otpError = null
                countdown = 60
                // Show success snackbar INSIDE the dialog when it opens
                dialogSnackbarMessage = "Verification code sent to your email!"
                dialogSnackbarType = SnackbarType.SUCCESS
                launch {
                    delay(3000)
                    dialogSnackbarMessage = null
                }
            }
            is LoginUiState.Loading -> {
                println("🔍 [LoginFormContent] Loading state - showOtpDialog: $showOtpDialog")
                if (showOtpDialog) {
                    isVerifying = true
                    verificationFailed = false
                }
            }
            is LoginUiState.Success -> {
                println("🔍 [LoginFormContent] Success state")
                showOtpDialog = false
                isVerifying = false
                // Extract all data from Success state and pass to callback
                val successState = uiState as LoginUiState.Success
                onLoginSuccess(
                    successState.user,
                    successState.message,
                    successState.accessToken,
                    successState.refreshToken
                )
                viewModel.resetState()
            }
            is LoginUiState.Error -> {
                println("🔍 [LoginFormContent] Error state - message: ${(uiState as LoginUiState.Error).message}")
                if (showOtpDialog) {
                    // OTP verification error - show inside dialog
                    isVerifying = false
                    verificationFailed = true
                    otpError = (uiState as LoginUiState.Error).message
                    dialogSnackbarMessage = otpError
                    dialogSnackbarType = SnackbarType.ERROR
                    launch {
                        delay(4000)
                        dialogSnackbarMessage = null
                    }
                } else {
                    // ✅ Login error (wrong password, etc.) - show as snackbar on main screen
                    // This uses the same pattern as AdaptiveResetPasswordScreen
                    val errorMessage = (uiState as LoginUiState.Error).message
                    if (!errorMessage.isNullOrBlank()) {
                        viewModel.showErrorMessage(errorMessage)
                    }
                }
            }
            else -> {}
        }
    }

    // Countdown timer for resend button
    LaunchedEffect(countdown) {
        while (countdown > 0) {
            delay(1000)
            countdown--
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(Modifier.height(24.dp))

            // Animated Lottie Animation
            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn(animationSpec = tween(600, easing = FastOutSlowInEasing)) +
                        scaleIn(initialScale = 0.8f, animationSpec = tween(600, easing = FastOutSlowInEasing))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    contentAlignment = Alignment.Center
                ) {
                    LottieAnimation(
                        composition = composition,
                        progress = { progress },
                        modifier = Modifier.size(140.dp)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Animated Welcome Text
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
                        text = "Welcome Back!",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Sign in to continue your journey",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

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
                    OutlinedButton(
                        onClick = onGoogleLoginClick,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(28.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Icon(
                            painterResource(id = R.drawable.ic_google),
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Continue with Google", fontWeight = FontWeight.SemiBold)
                    }

                    Spacer(Modifier.height(16.dp))

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
                            text = " OR sign in with email ",
                            modifier = Modifier.padding(horizontal = 12.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
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

            Spacer(Modifier.height(24.dp))

            // Animated Email Field
            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn(animationSpec = tween(500, delayMillis = 300, easing = FastOutSlowInEasing)) +
                        slideInHorizontally(
                            initialOffsetX = { -50 },
                            animationSpec = tween(500, delayMillis = 300, easing = FastOutSlowInEasing)
                        )
            ) {
                OutlinedTextField(
                    value = email,
                    onValueChange = viewModel::updateEmail,
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            // Animated Password Field
            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn(animationSpec = tween(500, delayMillis = 350, easing = FastOutSlowInEasing)) +
                        slideInHorizontally(
                            initialOffsetX = { -50 },
                            animationSpec = tween(500, delayMillis = 350, easing = FastOutSlowInEasing)
                        )
            ) {
                OutlinedTextField(
                    value = password,
                    onValueChange = viewModel::updatePassword,
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    colors = textFieldColors,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                    trailingIcon = {
                        val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, contentDescription = "Toggle Password Visibility")
                        }
                    }
                )
            }

            // Animated Forgot Password Link
            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn(animationSpec = tween(400, delayMillis = 450, easing = FastOutSlowInEasing))
            ) {
                TextButton(
                    onClick = onForgotPasswordClick,
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Forgot Password?", color = MaterialTheme.colorScheme.primary)
                }
            }

            Spacer(Modifier.height(16.dp))

            // Animated Fingerprint Row
            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn(animationSpec = tween(500, delayMillis = 550, easing = FastOutSlowInEasing)) +
                        slideInHorizontally(
                            initialOffsetX = { -50 },
                            animationSpec = tween(500, delayMillis = 550, easing = FastOutSlowInEasing)
                        )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.fingerprint_24px),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Enable fingerprint login",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Switch(
                        checked = enableFingerprint,
                        onCheckedChange = { enableFingerprint = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        )
                    )
                }
            }

            // Animated Terms Checkbox
            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn(animationSpec = tween(500, delayMillis = 650, easing = FastOutSlowInEasing))
            ) {
                PivotaCheckBox(
                    checked = agreeTerms,
                    onCheckedChange = viewModel::updateAgreeTerms,
                    text = "I agree to the terms and conditions"
                )
            }

            Spacer(Modifier.height(32.dp))

            // Animated Login Button
            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn(animationSpec = tween(500, delayMillis = 750, easing = FastOutSlowInEasing)) +
                        slideInVertically(
                            initialOffsetY = { 50 },
                            animationSpec = tween(500, delayMillis = 750, easing = FastOutSlowInEasing)
                        )
            ) {
                Button(
                    onClick = {
                        if (email.isNotEmpty() && password.isNotEmpty() && agreeTerms) {
                            viewModel.authenticateUser(email, password)
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    enabled = email.isNotEmpty() && password.isNotEmpty() && agreeTerms,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Login", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                }
            }

            Spacer(Modifier.height(24.dp))

            // Animated Register Link
            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn(animationSpec = tween(500, delayMillis = 850, easing = FastOutSlowInEasing)) +
                        slideInVertically(
                            initialOffsetY = { 30 },
                            animationSpec = tween(500, delayMillis = 850, easing = FastOutSlowInEasing)
                        )
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 40.dp)
                ) {
                    Text(
                        text = "Don't have an account? ",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Register",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onRegisterLinkClick() }
                    )
                }
            }
        }

        // Full screen loading for login
        if (isLoggingIn) {
            PivotaFullScreenLoading(message = "Signing you in...")
        }

        // ✅ Main Snackbar for login errors (wrong password, rate limiting, etc.)
        // This uses the same pattern as AdaptiveResetPasswordScreen
        if (mainSnackbarMessage != null && !showOtpDialog) {
            PivotaSnackbar(
                message = mainSnackbarMessage!!,
                type = mainSnackbarType,
                duration = 4000L,
                onDismiss = { viewModel.clearSnackbar() }
            )
        }

        // Success Snackbar for password reset
        if (showSimpleSnackbar && simpleMessage.isNotBlank()) {
            PivotaSnackbar(
                message = simpleMessage,
                type = SnackbarType.SUCCESS,
                duration = 5000L,
                onDismiss = {
                    showSimpleSnackbar = false
                    simpleMessage = ""
                }
            )
        }
    }

    // OTP Verification Dialog
    if (showOtpDialog) {
        OtpVerificationDialog(
            email = email,
            otpValue = otpValues.joinToString(""), // Combine all digits
            onOtpChange = { value -> viewModel.updateOtpFull(value) }, // New ViewModel function
            isVerifying = isVerifying,
            otpError = if (verificationFailed) otpError else null,
            countdown = countdown,
            title = "Verify Your Login",
            description = "We've sent a verification code to",
            verifyButtonText = "Verify & Login",
            onVerify = {
                if (otpValues.joinToString("").length == 6) {
                    verificationFailed = false
                    viewModel.verifyMfaLogin(otpValues.joinToString(""))
                } else {
                    otpError = "Please enter a valid 6-digit code"
                    verificationFailed = true
                    dialogSnackbarMessage = otpError
                    dialogSnackbarType = SnackbarType.ERROR
                }
            },
            onResend = {
                viewModel.resendOtp()
                otpError = null
                verificationFailed = false
                dialogSnackbarMessage = "New verification code sent!"
                dialogSnackbarType = SnackbarType.SUCCESS
                viewModel.viewModelScope.launch {
                    delay(3000)
                    dialogSnackbarMessage = null
                }
            },
            onCancel = {
                if (!isVerifying) {
                    showOtpDialog = false
                    viewModel.resetState()
                    otpError = null
                    verificationFailed = false
                    dialogSnackbarMessage = null
                }
            },
            snackbarMessage = dialogSnackbarMessage,
            snackbarType = dialogSnackbarType,
            onSnackbarDismiss = { dialogSnackbarMessage = null }
        )
    }
}