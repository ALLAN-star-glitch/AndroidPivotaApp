package com.example.pivota.auth.presentation.composables

import androidx.compose.foundation.BorderStroke
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
import com.airbnb.lottie.compose.*
import com.example.pivota.R
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
    onLoginSuccess: (String) -> Unit,
    onGoogleLoginClick: () -> Unit,
    onRegisterLinkClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
) {
    val scrollState = rememberScrollState()
    val uiState by viewModel.uiState.collectAsState()
    val otpValues by viewModel.otpValues.collectAsState()
    val resendCount by viewModel.resendCount.collectAsState()

    // Snackbar state
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()
    val snackbarType by viewModel.snackbarType.collectAsState()

    // Collect form state from ViewModel (survives rotation)
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val agreeTerms by viewModel.agreeTerms.collectAsState()

    var passwordVisible by remember { mutableStateOf(false) }
    var enableFingerprint by remember { mutableStateOf(false) }

    // OTP Dialog state
    var showOtpDialog by remember { mutableStateOf(false) }
    var isVerifying by remember { mutableStateOf(false) }
    var otpError by remember { mutableStateOf<String?>(null) }
    var countdown by remember { mutableStateOf(0) }

    // Track loading states
    val isLoggingIn = uiState is LoginUiState.Loading && !showOtpDialog
    val isVerifyingMfa = uiState is LoginUiState.Loading && showOtpDialog

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
                showOtpDialog = true
                isVerifying = false
                otpError = null
                countdown = 60
                // Start countdown
                while (countdown > 0) {
                    delay(1000)
                    countdown--
                }
            }
            is LoginUiState.Loading -> {
                isVerifying = true
            }
            is LoginUiState.Success -> {
                showOtpDialog = false
                onLoginSuccess((uiState as LoginUiState.Success).user.email)
                viewModel.resetState()
            }
            else -> {}
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

            /* ───────── LOTTIE ANIMATION SECTION ───────── */
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                LottieAnimation(
                    composition = composition,
                    progress = { progress },
                    modifier = Modifier.size(160.dp)
                )
            }

            Spacer(Modifier.height(8.dp))

            /* ───────── WELCOME TEXT ───────── */
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
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Sign in to continue your journey",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(32.dp))

            /* ───────── INPUT FIELDS ───────── */
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

            Spacer(Modifier.height(16.dp))

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

            TextButton(
                onClick = onForgotPasswordClick,
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Forgot Password?", color = MaterialTheme.colorScheme.primary)
            }

            Spacer(Modifier.height(16.dp))

            /* ───────── FINGERPRINT & TERMS ───────── */
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

            PivotaCheckBox(
                checked = agreeTerms,
                onCheckedChange = viewModel::updateAgreeTerms,
                text = "I agree to the terms and conditions"
            )

            Spacer(Modifier.height(32.dp))

            /* ───────── ACTION BUTTONS ───────── */
            Button(
                onClick = {
                    if (email.isNotEmpty() && password.isNotEmpty() && agreeTerms) {
                        viewModel.authenticateUser()
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
                Text("Login with Google", fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(24.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)
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

        // Full screen loading for login
        if (isLoggingIn) {
            PivotaFullScreenLoading(
                message = "Signing you in..."
            )
        }

        // Full screen loading for MFA verification
        if (isVerifyingMfa) {
            PivotaFullScreenLoading(
                message = "Verifying security code..."
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

    /* ───────── OTP VERIFICATION DIALOG ───────── */
    if (showOtpDialog) {
        OtpVerificationDialog(
            email = email,
            otpValues = otpValues,
            onOtpDigitChange = { index, value -> viewModel.updateOtpDigit(index, value) },
            isVerifying = isVerifying,
            otpError = otpError,
            countdown = countdown,
            resendCount = resendCount,
            title = "Verify Your Login",
            description = "We've sent a verification code to",
            verifyButtonText = "Verify & Login",
            onVerify = {
                val code = otpValues.joinToString("")
                if (code.length == 6) {
                    viewModel.verifyMfaLogin(code)
                } else {
                    otpError = "Please enter a valid 6-digit code"
                }
            },
            onResend = {
                viewModel.resendOtp()
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