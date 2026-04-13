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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
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
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun RegistrationFormContent(
    viewModel: SignupViewModel,
    onRegisterSuccess: (String, String, String, User?) -> Unit,
    onLoginLinkClick: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Credential Manager
    val credentialManager = remember { CredentialManager.create(context) }

    // Google Sign-In State
    val googleSignInState by viewModel.googleSignInState.collectAsState()
    var isGettingGoogleToken by remember { mutableStateOf(false) }

    // Force loading to stay visible during navigation
    var forceShowLoading by remember { mutableStateOf(false) }
    var hasNavigated by remember { mutableStateOf(false) }

    // Get Web Client ID from strings.xml
    val webClientId = stringResource(R.string.default_web_client_id)

    // Updated signInWithGoogle using GetSignInWithGoogleOption (working version)
    suspend fun signInWithGoogle() {
        try {
            println("🔐 [Google Sign-Up] Starting Google Sign-Up flow...")

            // Use GetSignInWithGoogleOption - same working approach as login
            val signInOption = GetSignInWithGoogleOption(serverClientId = webClientId)

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(signInOption)
                .build()

            println("🔐 [Google Sign-Up] Request built with GetSignInWithGoogleOption")

            val response = try {
                credentialManager.getCredential(context, request)
            } catch (e: NoCredentialException) {
                println("⚠️ NoCredentialException - No credentials found")
                viewModel.showMainSnackbar("Please add a Google account in Settings", SnackbarType.INFO)
                isGettingGoogleToken = false
                return
            } catch (e: GetCredentialException) {
                println("❌ GetCredentialException: ${e.message}")
                if (!e.message?.contains("cancel", ignoreCase = true)!!) {
                    viewModel.showMainSnackbar("Google Sign-In failed. Please try again.", SnackbarType.ERROR)
                }
                isGettingGoogleToken = false
                return
            }

            println("✅ [Google Sign-Up] Got credential response")

            val credential = response.credential
            println("🔐 [Google Sign-Up] Credential type: ${credential.type}")

            if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val idToken = googleIdTokenCredential.idToken
                println("🔐 [Google Sign-Up] ID Token received: ${idToken?.take(50)}...")

                if (!idToken.isNullOrEmpty()) {
                    println("✅ [Google Sign-Up] ID Token valid, sending to backend...")
                    viewModel.signUpWithGoogle(idToken)
                } else {
                    isGettingGoogleToken = false
                    viewModel.showMainSnackbar("Failed to get ID token", SnackbarType.ERROR)
                }
            } else {
                isGettingGoogleToken = false
                println("❌ [Google Sign-Up] Invalid credential type: ${credential.type}")
                viewModel.showMainSnackbar("Invalid credential type", SnackbarType.ERROR)
            }
        } catch (e: Exception) {
            isGettingGoogleToken = false
            println("❌ [Google Sign-Up] Exception: ${e.message}")
            e.printStackTrace()

            if (!e.message?.contains("cancel", ignoreCase = true)!!) {
                viewModel.showMainSnackbar("Google Sign-In failed. Please try again.", SnackbarType.ERROR)
            }
        }
    }

    // Handle Google Sign-In success
    LaunchedEffect(googleSignInState) {
        when (val state = googleSignInState) {
            is SignupViewModel.GoogleSignInState.Success -> {
                println("✅ [Google Sign-Up] Success! Preparing to navigate...")
                forceShowLoading = true
                isGettingGoogleToken = false

                delay(800)

                if (!hasNavigated) {
                    hasNavigated = true
                    println("✅ [Google Sign-Up] Navigating to dashboard now...")
                    onRegisterSuccess(
                        "Google sign-in successful",
                        state.accessToken,
                        state.refreshToken,
                        state.user
                    )
                }
            }
            is SignupViewModel.GoogleSignInState.Error -> {
                println("❌ [Google Sign-Up] Error: ${state.message}")
                isGettingGoogleToken = false
                forceShowLoading = false
            }
            is SignupViewModel.GoogleSignInState.Loading -> {
                println("⏳ [Google Sign-Up] Processing...")
                forceShowLoading = true
            }
            else -> {}
        }
    }

    // Show loading overlay for Google Sign-In
    val showGoogleLoading = isGettingGoogleToken ||
            googleSignInState is SignupViewModel.GoogleSignInState.Loading ||
            forceShowLoading

    if (showGoogleLoading) {
        PivotaFullScreenLoading(message = "Please wait...")
        return
    }

    // Rest of your UI code (only rendered when not loading)
    val scrollState = rememberScrollState()
    val uiState by viewModel.uiState.collectAsState()
    val formState by viewModel.formState.collectAsState()
    val otpValues by viewModel.otpValues.collectAsState()
    val resendCount by viewModel.resendCount.collectAsState()
    val shouldCloseDialog by viewModel.shouldCloseDialog.collectAsState()
    val mainSnackbarMessage by viewModel.mainSnackbarMessage.collectAsState()
    val mainSnackbarType by viewModel.mainSnackbarType.collectAsState()
    val dialogSnackbarMessage by viewModel.dialogSnackbarMessage.collectAsState()
    val dialogSnackbarType by viewModel.dialogSnackbarType.collectAsState()

    var passwordVisible by remember { mutableStateOf(false) }
    var showOtpDialog by remember { mutableStateOf(false) }
    var isVerifying by remember { mutableStateOf(false) }
    var otpError by remember { mutableStateOf<String?>(null) }
    var countdown by remember { mutableStateOf(0) }
    var verificationFailed by remember { mutableStateOf(false) }
    var showContent by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(300)
        showContent = true
    }

    var localPasswordError by remember { mutableStateOf<String?>(null) }

    val isRequestingOtp = uiState is SignupUiState.Loading && !showOtpDialog
    val isVerifyingOtp = uiState is SignupUiState.Loading && showOtpDialog

    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.signup_animation)
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        isPlaying = true
    )

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

    LaunchedEffect(uiState) {
        if (uiState is SignupUiState.OtpSent) {
            countdown = 60
            while (countdown > 0) {
                delay(1000)
                countdown--
            }
        }
    }

    LaunchedEffect(shouldCloseDialog) {
        if (shouldCloseDialog && showOtpDialog) {
            showOtpDialog = false
            isVerifying = false
            verificationFailed = false
            otpError = null
            viewModel.resetDialogCloseFlag()
        }
    }

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
            Spacer(Modifier.height(16.dp))

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

            // Google Button with Credential Manager
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
                        onClick = {
                            isGettingGoogleToken = true
                            forceShowLoading = false
                            hasNavigated = false
                            coroutineScope.launch {
                                signInWithGoogle()
                            }
                        },
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