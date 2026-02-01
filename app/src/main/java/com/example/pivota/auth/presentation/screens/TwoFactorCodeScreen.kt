package com.example.pivota.auth.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pivota.R
import com.example.pivota.auth.presentation.state.SignupUiState
import com.example.pivota.auth.presentation.viewModel.SignupViewModel
import kotlinx.coroutines.delay

@Composable
fun VerifyOtpScreen(
    email: String, // Passed from navigation
    viewModel: SignupViewModel = hiltViewModel(), // Shared instance from NavHost
    onVerificationSuccess: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val otpLength = 6
    val focusRequesters = remember { List(otpLength) { FocusRequester() } }
    val otpValues = remember { mutableStateListOf(*Array(otpLength) { "" }) }
    var timeLeft by remember { mutableIntStateOf(45) }

    // Timer Logic
    LaunchedEffect(Unit) {
        while (timeLeft > 0) {
            delay(1000)
            timeLeft--
        }
    }

    // Navigation logic: Go to dashboard once registration is complete
    LaunchedEffect(uiState) {
        if (uiState is SignupUiState.Success) {
            onVerificationSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(48.dp))

        Icon(
            painter = painterResource(id = R.drawable.verified_user_24px),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(64.dp)
        )

        Spacer(Modifier.height(24.dp))

        Text(
            "Verify Your Email",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(8.dp))

        Text(
            "We've sent a 6-digit code to $email. Enter it below to complete your registration.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                        if (newValue.length <= 1 && newValue.all { it.isDigit() }) {
                            otpValues[index] = newValue
                            if (newValue.isNotEmpty() && index < otpLength - 1) {
                                focusRequesters[index + 1].requestFocus()
                            }
                        }
                    },
                    onBackspace = {
                        if (otpValues[index].isEmpty() && index > 0) {
                            otpValues[index - 1] = ""
                            focusRequesters[index - 1].requestFocus()
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .focusRequester(focusRequesters[index])
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // Timer and Resend
        Text(
            text = if (timeLeft > 0)
                "Resend code in 00:${timeLeft.toString().padStart(2, '0')}"
            else
                "Resend code",
            style = MaterialTheme.typography.labelLarge,
            color = if (timeLeft > 0) Color.Gray else MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable(enabled = timeLeft == 0) {
                timeLeft = 45
                viewModel.requestSignupOtp(email)
            }
        )

        Spacer(Modifier.height(32.dp))

        // Error Message from ViewModel
        if (uiState is SignupUiState.Error) {
            Text(
                text = (uiState as SignupUiState.Error).message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        Button(
            onClick = {
                val code = otpValues.joinToString("")
                viewModel.verifyAndRegister(code)
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            enabled = otpValues.all { it.isNotEmpty() } && uiState !is SignupUiState.Loading,
            shape = RoundedCornerShape(12.dp)
        ) {
            if (uiState is SignupUiState.Loading) {
                // 👇 Removed 'size' (use Modifier instead) and used indeterminate version
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White,
                    strokeWidth = 2.dp // Optional: makes it look cleaner inside a button
                )
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
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .onKeyEvent {
                if (it.type == KeyEventType.KeyDown && it.key == Key.Backspace) {
                    onBackspace()
                    true
                } else false
            },
        textStyle = LocalTextStyle.current.copy(
            textAlign = TextAlign.Center,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        ),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
        )
    )
}