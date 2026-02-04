package com.example.pivota.auth.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.window.core.layout.WindowWidthSizeClass
import com.example.pivota.R
import com.example.pivota.auth.presentation.state.SignupUiState
import com.example.pivota.auth.presentation.viewModel.SignupViewModel
import com.example.pivota.core.presentations.composables.background_image_and_overlay.BackgroundImageAndOverlay
import kotlinx.coroutines.delay

@Composable
fun VerifyOtpScreen(
    email: String,
    viewModel: SignupViewModel = hiltViewModel(),
    onVerificationSuccess: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val isWide = windowSizeClass.windowWidthSizeClass != WindowWidthSizeClass.COMPACT
    val uiState by viewModel.uiState.collectAsState()

    // Navigation logic: Go to dashboard once registration is complete
    LaunchedEffect(uiState) {
        if (uiState is SignupUiState.Success) {
            onVerificationSuccess()
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        if (isWide) {
            Row(modifier = Modifier.fillMaxSize()) {
                // Left Pane: Visual context
                Box(modifier = Modifier.weight(1f)) {
                    BackgroundImageAndOverlay(
                        isWideScreen = true,
                        header = "Security First",
                        desc1 = "Protecting your account with two-factor authentication.",
                        showUpgradeButton = false,
                        enableCarousel = false,
                        image = R.drawable.happy_people // Use your security-themed image if preferred
                    )
                }
                // Right Pane: Verification Form
                Box(modifier = Modifier.weight(1f).fillMaxHeight(), contentAlignment = Alignment.Center) {
                    VerifyOtpContent(
                        email = email,
                        uiState = uiState,
                        viewModel = viewModel, // Added this
                        onVerify = { code -> viewModel.verifyAndRegister(code) },
                        onResend = {
                            viewModel.incrementResendCount() // Track the attempt in VM
                            viewModel.requestSignupOtp(email)
                        },
                        onNavigateBack = onNavigateBack
                    )
                }
            }
        } else {
            // Single Pane: Mobile Layout
            VerifyOtpContent(
                email = email,
                uiState = uiState,
                viewModel = viewModel, // Added this
                onVerify = { code -> viewModel.verifyAndRegister(code) },
                onResend = {
                    viewModel.incrementResendCount() // Track the attempt in VM
                    viewModel.requestSignupOtp(email)
                },
                onNavigateBack = onNavigateBack
            )
        }
    }
}

@Composable
private fun VerifyOtpContent(
    email: String,
    viewModel: SignupViewModel,
    uiState: SignupUiState,
    onVerify: (String) -> Unit,
    onResend: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val otpLength = 6
    val focusRequesters = remember { List(otpLength) { FocusRequester() } }
    // Observe state from ViewModel
    val otpValues by viewModel.otpValues.collectAsState()
    val resendCount by viewModel.resendCount.collectAsState()

    // Timer & Resend State
    var timeLeft by remember { mutableIntStateOf(45) }
    val maxResends = 3
    val canResend = resendCount < maxResends

    LaunchedEffect(Unit) { focusRequesters[0].requestFocus() }

    // Timer logic: stops if max resends reached
    LaunchedEffect(timeLeft, resendCount) {
        if (timeLeft > 0 && canResend) {
            delay(1000)
            timeLeft--
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.verified_user_24px),
            contentDescription = null,
            tint = Color(0xFF006565),
            modifier = Modifier.size(64.dp)
        )

        Spacer(Modifier.height(24.dp))

        Text(
            "Verify Your Email",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF006565)
        )

        Spacer(Modifier.height(8.dp))

        Text(
            "Enter the 6-digit code sent to $email",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(32.dp))

        // OTP Input Row
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            otpValues.forEachIndexed { index, value ->
                OtpDigitBox(
                    value = value,
                    onValueChange = { newValue ->
                        val char = newValue.lastOrNull()?.toString() ?: ""
                        if (char.isNotEmpty() && char.all { it.isDigit() }) {
                            viewModel.updateOtpDigit(index, char)
                            if (index < otpLength - 1) focusRequesters[index + 1].requestFocus()
                        } else if (char.isEmpty()) {
                            viewModel.updateOtpDigit(index, "")
                        }
                    },
                    onBackspace = {
                        if (otpValues[index].isEmpty() && index > 0) {
                            viewModel.updateOtpDigit(index - 1, "")
                            focusRequesters[index - 1].requestFocus()
                        } else {
                            viewModel.updateOtpDigit(index, "")
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .focusRequester(focusRequesters[index])
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // Resend Logic with 3-attempt cap
        if (canResend) {
            Text(
                text = if (timeLeft > 0)
                    "Resend code in 00:${timeLeft.toString().padStart(2, '0')}"
                else
                    "Resend code (${maxResends - resendCount} left)",
                style = MaterialTheme.typography.labelLarge,
                color = if (timeLeft > 0) Color.Gray else Color(0xFF006565),
                fontWeight = if (timeLeft == 0) FontWeight.Bold else FontWeight.Normal,
                modifier = Modifier.clickable(enabled = timeLeft == 0) {
                    timeLeft = 45
                    onResend()
                }
            )
        } else {
            Text(
                "Maximum resend attempts reached. Please contact support.",
                style = MaterialTheme.typography.labelLarge,
                color = Color.Red.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }

        Spacer(Modifier.height(32.dp))

        if (uiState is SignupUiState.Error) {
            Text(
                text = uiState.message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        Button(
            onClick = { onVerify(otpValues.joinToString("")) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = otpValues.all { it.isNotEmpty() } && uiState !is SignupUiState.Loading,
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006565))
        ) {
            if (uiState is SignupUiState.Loading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
            } else {
                Text("Verify & Create Account", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(16.dp))

        TextButton(onClick = onNavigateBack) {
            Text("Edit email address", color = Color.Gray)
        }
    }
}

@Composable
private fun OtpDigitBox(
    value: String,
    onValueChange: (String) -> Unit,
    onBackspace: () -> Unit,
    modifier: Modifier = Modifier
) {
    BasicTextField(
        value = value,
        onValueChange = { newValue ->
            // Accept only a single digit
            if (newValue.length <= 1 && newValue.all { it.isDigit() }) {
                onValueChange(newValue)
            }
        },
        modifier = modifier
            .aspectRatio(1f)
            .onKeyEvent { event ->
                if (event.key == Key.Backspace && event.type == KeyEventType.KeyDown) {
                    onBackspace()
                    true
                } else {
                    false
                }
            },
        textStyle = LocalTextStyle.current.copy(
            textAlign = TextAlign.Center,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF006565)
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .background(Color(0xFFF6FAF9), RoundedCornerShape(8.dp))
                    .border(
                        width = 1.5.dp,
                        color = if (value.isNotEmpty()) Color(0xFF006565) else Color(0xFFE0E0E0),
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                innerTextField()
            }
        }
    )
}
