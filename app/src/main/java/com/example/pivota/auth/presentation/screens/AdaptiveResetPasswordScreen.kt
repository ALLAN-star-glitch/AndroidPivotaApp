package com.example.pivota.auth.presentation.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.window.core.layout.WindowSizeClass
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.viewModelScope
import com.airbnb.lottie.compose.*
import com.example.pivota.R
import com.example.pivota.auth.presentation.state.LoginUiState
import com.example.pivota.auth.presentation.viewModel.LoginViewModel
import com.example.pivota.core.presentations.composables.OtpVerificationDialog
import com.example.pivota.core.presentations.composables.PivotaFullScreenLoading
import com.example.pivota.core.presentations.composables.PivotaSnackbar
import com.example.pivota.core.presentations.composables.SnackbarType
import com.example.pivota.ui.theme.SuccessGreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("ConfigurationScreenWidthHeight")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdaptiveResetPasswordScreen(
    onBackClick: () -> Unit,
    onPasswordReset: (String) -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val isMediumScreen = windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)
    val isExpandedScreen = windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)

    // Shared state
    val uiState by viewModel.uiState.collectAsState()
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()
    val snackbarType by viewModel.snackbarType.collectAsState()
    val otpValues by viewModel.otpValues.collectAsState()
    val resendCount by viewModel.resendCount.collectAsState()

    var email by rememberSaveable { mutableStateOf("") }
    var emailError by rememberSaveable { mutableStateOf<String?>(null) }
    var newPassword by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var passwordError by rememberSaveable { mutableStateOf<String?>(null) }

    // OTP Dialog state
    var showOtpDialog by rememberSaveable { mutableStateOf(false) }
    var isVerifying by rememberSaveable { mutableStateOf(false) }
    var otpError by rememberSaveable { mutableStateOf<String?>(null) }
    var countdown by rememberSaveable { mutableIntStateOf(0) }

    // Dialog-specific snackbar state (for success and OTP errors)
    var dialogSnackbarMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var dialogSnackbarType by rememberSaveable { mutableStateOf(SnackbarType.INFO) }

    val isLoading = uiState is LoginUiState.Loading && !showOtpDialog
    val isVerifyingOtp = uiState is LoginUiState.Loading && showOtpDialog

    // Load cached email if available
    LaunchedEffect(Unit) {
        val cachedEmail = viewModel.getCachedResetEmail()
        if (cachedEmail.isNotBlank()) {
            email = cachedEmail
        }
    }

    // Handle OTP dialog visibility and errors
    LaunchedEffect(uiState) {
        when (uiState) {
            is LoginUiState.PasswordResetOtpSent -> {
                showOtpDialog = true
                isVerifying = false
                otpError = null
                countdown = 60
                // ✅ Show success snackbar INSIDE the dialog
                dialogSnackbarMessage = "Verification code sent to your email!"
                dialogSnackbarType = SnackbarType.SUCCESS
                launch {
                    delay(3000)
                    dialogSnackbarMessage = null
                }
                while (countdown > 0) {
                    delay(1000)
                    countdown--
                }
            }
            is LoginUiState.Loading -> {
                if (showOtpDialog) {
                    isVerifying = true
                }
            }
            is LoginUiState.PasswordResetSuccess -> {
                showOtpDialog = false
                isVerifying = false
                val message = (uiState as LoginUiState.PasswordResetSuccess).message
                onPasswordReset(message)
            }
            is LoginUiState.Error -> {
                isVerifying = false
                val errorMessage = (uiState as LoginUiState.Error).message

                if (showOtpDialog) {
                    // ✅ OTP error - show inside dialog
                    otpError = errorMessage
                    dialogSnackbarMessage = errorMessage
                    dialogSnackbarType = SnackbarType.ERROR
                    launch {
                        delay(4000)
                        dialogSnackbarMessage = null
                    }
                } else {
                    // ✅ Main screen error (rate limiting, etc.) - show as snackbar on main screen
                    // Don't use emailError or passwordError, use snackbar instead
                    viewModel.showErrorMessage(errorMessage ?: "An error occurred")
                }
            }
            else -> {}
        }
    }

    fun validateAndRequestReset() {
        emailError = null
        passwordError = null

        emailError = when {
            email.isBlank() -> "Email is required"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Enter a valid email address"
            else -> null
        }

        // Use the same validation logic that matches backend
        passwordError = when {
            newPassword.isBlank() -> "New password is required"
            newPassword.length < 8 -> "Password must be at least 8 characters"
            !newPassword.any { it.isUpperCase() } -> "Password must contain an uppercase letter (A-Z)"
            !newPassword.any { it.isLowerCase() } -> "Password must contain a lowercase letter (a-z)"
            !newPassword.any { it.isDigit() } -> "Password must contain a number (0-9)"
            !newPassword.any { it in "!@#$%^&*()_+-=[]{}|;':\",.<>/?`~" } ->
                "Password must contain a special character"
            else -> null
        }

        if (emailError == null && passwordError == null) {
            viewModel.updateResetNewPassword(newPassword)
            viewModel.requestPasswordReset(email)
        }
    }

    when {
        isMediumScreen || isExpandedScreen -> {
            Box(modifier = Modifier.fillMaxSize()) {
                Row(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f)
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
                    ) {
                        TwoPaneResetPasswordLeftContent()
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f)
                            .background(MaterialTheme.colorScheme.surface)
                    ) {
                        TwoPaneResetPasswordRightContent(
                            email = email,
                            onEmailChange = { email = it },
                            emailError = emailError,
                            newPassword = newPassword,
                            onNewPasswordChange = { newPassword = it },
                            passwordVisible = passwordVisible,
                            onPasswordVisibleChange = { passwordVisible = it },
                            passwordError = passwordError,
                            isLoading = isLoading,
                            onResetClick = { validateAndRequestReset() },
                            onBackClick = onBackClick
                        )
                    }
                }

                if (isLoading) {
                    PivotaFullScreenLoading(message = "Sending verification code...")
                }
                if (isVerifyingOtp) {
                    PivotaFullScreenLoading(message = "Resetting your password...")
                }

                // ✅ Main screen snackbar for errors (rate limiting, etc.)
                if (snackbarMessage != null && !showOtpDialog) {
                    PivotaSnackbar(
                        message = snackbarMessage!!,
                        type = snackbarType,
                        duration = 4000L,
                        onDismiss = { viewModel.clearSnackbar() }
                    )
                }
            }
        }

        else -> {
            Box(modifier = Modifier.fillMaxSize()) {
                ResetPasswordScreenContent(
                    email = email,
                    onEmailChange = { email = it },
                    emailError = emailError,
                    newPassword = newPassword,
                    onNewPasswordChange = { newPassword = it },
                    passwordVisible = passwordVisible,
                    onPasswordVisibleChange = { passwordVisible = it },
                    passwordError = passwordError,
                    isLoading = isLoading,
                    onResetClick = { validateAndRequestReset() },
                    onBackClick = onBackClick
                )

                if (isLoading) {
                    PivotaFullScreenLoading(message = "Sending verification code...")
                }
                if (isVerifyingOtp) {
                    PivotaFullScreenLoading(message = "Resetting your password...")
                }

                // ✅ Main screen snackbar for errors (rate limiting, etc.)
                if (snackbarMessage != null && !showOtpDialog) {
                    PivotaSnackbar(
                        message = snackbarMessage!!,
                        type = snackbarType,
                        duration = 4000L,
                        onDismiss = { viewModel.clearSnackbar() }
                    )
                }
            }
        }
    }

    // OTP Dialog with its own snackbar state
    if (showOtpDialog) {
        OtpVerificationDialog(
            email = email,
            otpValues = otpValues,
            onOtpDigitChange = { index, value -> viewModel.updateOtpDigit(index, value) },
            isVerifying = isVerifying,
            otpError = otpError,
            countdown = countdown,
            resendCount = resendCount,
            title = "Verify Your Identity",
            description = "We've sent a verification code to",
            verifyButtonText = "Reset Password",
            onVerify = {
                val code = otpValues.joinToString("")
                if (code.length == 6) {
                    otpError = null
                    viewModel.verifyAndResetPassword(code)
                } else {
                    otpError = "Please enter a valid 6-digit code"
                    dialogSnackbarMessage = otpError
                    dialogSnackbarType = SnackbarType.ERROR
                }
            },
            onResend = {
                viewModel.resendPasswordResetOtp()
                otpError = null
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
                    viewModel.clearResetPasswordCache()
                    otpError = null
                    dialogSnackbarMessage = null
                }
            },
            snackbarMessage = dialogSnackbarMessage,
            snackbarType = dialogSnackbarType,
            onSnackbarDismiss = { dialogSnackbarMessage = null }
        )
    }
}

@Composable
fun TwoPaneResetPasswordLeftContent() {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.reset_password_animation)
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        isPlaying = true
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.size(250.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Reset Password",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                color = MaterialTheme.colorScheme.primary
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Enter your email and new password. We'll send a verification code to confirm.",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun TwoPaneResetPasswordRightContent(
    email: String,
    onEmailChange: (String) -> Unit,
    emailError: String?,
    newPassword: String,
    onNewPasswordChange: (String) -> Unit,
    passwordVisible: Boolean,
    onPasswordVisibleChange: (Boolean) -> Unit,
    passwordError: String?,
    isLoading: Boolean,
    onResetClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 48.dp)
            .padding(top = 48.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Back button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Reset Password",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                color = MaterialTheme.colorScheme.primary
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Enter your email and new password",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Email field
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Email Address") },
            placeholder = { Text("your@email.com") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            isError = emailError != null,
            supportingText = {
                if (emailError != null) {
                    Text(
                        text = emailError!!,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // New Password field
        OutlinedTextField(
            value = newPassword,
            onValueChange = onNewPasswordChange,
            label = { Text("New Password") },
            placeholder = { Text("Enter new password") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { onPasswordVisibleChange(!passwordVisible) }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = "Toggle visibility",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            },
            isError = passwordError != null,
            supportingText = {
                if (passwordError != null) {
                    Text(
                        text = passwordError!!,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp
                    )
                } else {
                    Text(
                        text = "Min 8 chars: uppercase, lowercase, number, special (@ $ ! % * ? &)",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Reset Password Button
        Button(
            onClick = onResetClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            enabled = !isLoading,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                "Reset Password",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Back to login link
        TextButton(
            onClick = onBackClick,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = "Back to Login",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun ResetPasswordScreenContent(
    email: String,
    onEmailChange: (String) -> Unit,
    emailError: String?,
    newPassword: String,
    onNewPasswordChange: (String) -> Unit,
    passwordVisible: Boolean,
    onPasswordVisibleChange: (Boolean) -> Unit,
    passwordError: String?,
    isLoading: Boolean,
    onResetClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    // Local validation state for real-time feedback
    var localPasswordError by remember { mutableStateOf<String?>(null) }

    // Real-time password validation (matches backend requirements)
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

    // Update local validation on password change
    fun handlePasswordChange(newValue: String) {
        onNewPasswordChange(newValue)
        localPasswordError = validatePassword(newValue)
    }

    // Lottie animation
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.reset_password_animation)
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        isPlaying = true
    )

    // Display error (prioritize local validation over backend error)
    val displayPasswordError = localPasswordError ?: passwordError

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp)
            .padding(top = 48.dp, bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Back button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.size(140.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Reset Password",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.primary
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Enter your email and new password",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Email field
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Email Address") },
            placeholder = { Text("your@email.com") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            isError = emailError != null,
            supportingText = {
                if (emailError != null) {
                    Text(
                        text = emailError!!,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // New Password field with real-time validation
        OutlinedTextField(
            value = newPassword,
            onValueChange = { handlePasswordChange(it) },
            label = { Text("New Password") },
            placeholder = { Text("Enter new password") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { onPasswordVisibleChange(!passwordVisible) }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = "Toggle visibility",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            },
            isError = displayPasswordError != null,
            supportingText = {
                if (displayPasswordError != null) {
                    Text(
                        text = displayPasswordError!!,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp
                    )
                } else if (newPassword.isNotEmpty()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = MaterialTheme.colorScheme.primary
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

        Spacer(modifier = Modifier.height(32.dp))

        // Reset Password Button - Enabled only when password is valid
        Button(
            onClick = onResetClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(28.dp),
            enabled = !isLoading && newPassword.isNotEmpty() && displayPasswordError == null,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                disabledContainerColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sending...", color = Color.White)
            } else {
                Text(
                    "Reset Password",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Back to login link
        TextButton(
            onClick = onBackClick,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = "Back to Login",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )
        }
    }
}