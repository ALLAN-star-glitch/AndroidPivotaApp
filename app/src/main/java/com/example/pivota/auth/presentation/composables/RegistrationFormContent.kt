package com.example.pivota.auth.presentation.composables

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.airbnb.lottie.compose.*
import com.example.pivota.R
import com.example.pivota.auth.presentation.state.SignupUiState
import com.example.pivota.auth.presentation.viewModel.SignupViewModel
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
    val coroutineScope = rememberCoroutineScope()

    var passwordVisible by remember { mutableStateOf(false) }
    var showOtpDialog by remember { mutableStateOf(false) }
    var otpCode by remember { mutableStateOf("") }
    var isVerifying by remember { mutableStateOf(false) }
    var otpError by remember { mutableStateOf<String?>(null) }

    // Lottie animation
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.signup_animation)
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        isPlaying = true
    )

    // Design-consistent field colors and shapes
    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
        focusedContainerColor = MaterialTheme.colorScheme.surface,
        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer
    )
    val fieldShape = RoundedCornerShape(12.dp)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp)
    ) {
        /* ───────── HEADER WITH LOTTIE ANIMATION ───────── */
        Spacer(Modifier.height(24.dp))

        // Lottie Animation
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

        // Welcome Message
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Welcome to Pivota! 👋",
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

        /* ───────── REGISTRATION FORM ───────── */
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

        /* ───────── REGISTER BUTTON ───────── */
        Button(
            onClick = {
                if (formState.agreeTerms && formState.email.isNotEmpty() && formState.password.isNotEmpty()) {
                    showOtpDialog = true
                    otpError = null
                    otpCode = ""
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            enabled = formState.agreeTerms && formState.email.isNotEmpty() && formState.password.isNotEmpty(),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                "Create Account",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.White
            )
        }

        /* ───────── ERROR MESSAGE ───────── */
        if (uiState is SignupUiState.Error) {
            Text(
                text = (uiState as SignupUiState.Error).message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            )
        }

        Spacer(Modifier.height(24.dp))

        /* ───────── FOOTER ───────── */
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

    // Elegant OTP Verification Dialog
    if (showOtpDialog) {
        OTPVerificationDialog(
            email = formState.email,
            otpCode = otpCode,
            onOtpCodeChange = { otpCode = it },
            isVerifying = isVerifying,
            otpError = otpError,
            onVerify = {
                if (otpCode.length == 6) {
                    isVerifying = true
                    coroutineScope.launch {
                        delay(1500) // Simulate API call
                        isVerifying = false
                        showOtpDialog = false
                        // Navigate directly to dashboard
                        onRegisterSuccess(formState.email)
                    }
                } else {
                    otpError = "Please enter a valid 6-digit code"
                }
            },
            onResend = {
                otpError = null
                otpCode = ""
            },
            onCancel = {
                if (!isVerifying) {
                    showOtpDialog = false
                    otpCode = ""
                    otpError = null
                }
            },
            onDismiss = {
                if (!isVerifying) {
                    showOtpDialog = false
                    otpCode = ""
                    otpError = null
                }
            }
        )
    }
}

@Composable
fun OTPVerificationDialog(
    email: String,
    otpCode: String,
    onOtpCodeChange: (String) -> Unit,
    isVerifying: Boolean,
    otpError: String?,
    onVerify: () -> Unit,
    onResend: () -> Unit,
    onCancel: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = !isVerifying,
            dismissOnClickOutside = !isVerifying,
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Animated Icon
                Surface(
                    modifier = Modifier.size(80.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.MarkEmailRead,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Title
                Text(
                    text = "Verify Your Email",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Description
                Text(
                    text = "We've sent a verification code to",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = email,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                // OTP Digit Cells
                OTPSixDigitInput(
                    value = otpCode,
                    onValueChange = onOtpCodeChange,
                    isError = otpError != null
                )

                // Error Message
                if (otpError != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = otpError,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Verify Button
                Button(
                    onClick = onVerify,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(28.dp),
                    enabled = !isVerifying && otpCode.length == 6,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    if (isVerifying) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Verifying...", color = Color.White)
                    } else {
                        Text(
                            "Verify & Create Account",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Resend and Cancel Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TextButton(
                        onClick = onResend,
                        enabled = !isVerifying
                    ) {
                        Text(
                            "Resend Code",
                            fontSize = 13.sp,
                            color = if (!isVerifying)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    }

                    TextButton(
                        onClick = onCancel,
                        enabled = !isVerifying
                    ) {
                        Text(
                            "Cancel",
                            fontSize = 13.sp,
                            color = if (!isVerifying)
                                MaterialTheme.colorScheme.onSurfaceVariant
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OTPSixDigitInput(
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Visible OTP Cells
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            for (index in 0..5) {
                OTPSingleDigitCell(
                    digit = if (index < value.length) value[index].toString() else "",
                    isActive = value.length == index,
                    isError = isError,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Hidden TextField that receives input
        BasicTextField(
            value = value,
            onValueChange = {
                if (it.length <= 6 && it.all { char -> char.isDigit() }) {
                    onValueChange(it)
                }
            },
            modifier = Modifier
                .size(1.dp)
                .background(Color.Transparent),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )
    }
}

@Composable
fun OTPSingleDigitCell(
    digit: String,
    isActive: Boolean,
    isError: Boolean,
    modifier: Modifier = Modifier
) {
    val borderColor = when {
        isError -> MaterialTheme.colorScheme.error
        isActive -> MaterialTheme.colorScheme.primary
        digit.isNotEmpty() -> MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        else -> MaterialTheme.colorScheme.outlineVariant
    }

    val backgroundColor = when {
        isActive -> MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
        digit.isNotEmpty() -> MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        else -> MaterialTheme.colorScheme.surface
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .border(
                width = if (isActive) 2.dp else 1.5.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = digit,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = if (digit.isNotEmpty())
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            ),
            textAlign = TextAlign.Center
        )
    }
}