package com.example.pivota.core.presentations.composables

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.repeatable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.airbnb.lottie.compose.*
import com.example.pivota.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("ConfigurationScreenWidthHeight")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtpVerificationDialog(
    email: String,
    otpLength: Int = 6,
    otpValue: String,
    onOtpChange: (String) -> Unit,
    isVerifying: Boolean,
    otpError: String?,
    countdown: Int,
    title: String = "Verify Your Email",
    description: String = "We've sent a verification code to",
    verifyButtonText: String = "Verify",
    onVerify: () -> Unit,
    onResend: () -> Unit,
    onCancel: () -> Unit,
    snackbarMessage: String? = null,
    snackbarType: SnackbarType = SnackbarType.ERROR,
    onSnackbarDismiss: () -> Unit = {},
    shouldClose: Boolean = false,
    onDialogClosed: () -> Unit = {}
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val isLandscape = screenWidth > screenHeight
    val isTablet = screenWidth > 600.dp
    val scrollState = rememberScrollState()
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    var isFocused by remember { mutableStateOf(false) }
    var shakeError by remember { mutableStateOf(false) }
    var isDialogVisible by remember { mutableStateOf(true) }

    // Use TextFieldValue to control cursor position
    var textFieldValue by remember {
        mutableStateOf(
            TextFieldValue(
                text = otpValue,
                selection = androidx.compose.ui.text.TextRange(0)
            )
        )
    }

    // Close dialog if parent requests
    LaunchedEffect(shouldClose) {
        if (shouldClose) {
            isDialogVisible = false
            onDialogClosed()
        }
    }

    // Shake animation trigger
    LaunchedEffect(otpError) {
        if (otpError != null) {
            shakeError = true
            delay(500)
            shakeError = false
        }
    }

    // Update textFieldValue when otpValue changes externally
    LaunchedEffect(otpValue) {
        if (textFieldValue.text != otpValue) {
            val cursorPos = textFieldValue.selection.start.coerceAtMost(otpValue.length)
            textFieldValue = TextFieldValue(
                text = otpValue,
                selection = androidx.compose.ui.text.TextRange(cursorPos)
            )
        }
    }

    if (!isDialogVisible) return

    // Auto-focus and show keyboard when dialog appears
    LaunchedEffect(Unit) {
        delay(100)
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    val dialogWidth = when {
        isTablet -> screenWidth * 0.45f
        isLandscape -> screenWidth * 0.7f
        else -> screenWidth * 0.92f
    }
    val dialogMaxHeight = if (isLandscape) screenHeight * 0.9f else screenHeight * 0.8f

    val otpComposition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.otp_verification_animation)
    )
    val otpProgress by animateLottieCompositionAsState(
        composition = otpComposition,
        iterations = LottieConstants.IterateForever,
        isPlaying = true
    )

    // Create underscore display string
    val underscoreDisplay = (1..otpLength).joinToString(" ") { "_" }

    Dialog(
        onDismissRequest = {
            if (!isVerifying) {
                isDialogVisible = false
                onCancel()
            }
        },
        properties = DialogProperties(
            dismissOnBackPress = !isVerifying,
            dismissOnClickOutside = !isVerifying,
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = Modifier
                .width(dialogWidth)
                .heightIn(max = dialogMaxHeight),
            shape = RoundedCornerShape(32.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            shadowElevation = 24.dp
        ) {
            Box {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(scrollState)
                        .padding(if (isTablet) 32.dp else 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(if (isLandscape) 12.dp else 16.dp)
                ) {
                    LottieAnimation(
                        composition = otpComposition,
                        progress = { otpProgress },
                        modifier = Modifier
                            .size(if (isLandscape) 80.dp else if (isTablet) 160.dp else 120.dp)
                            .padding(8.dp)
                    )

                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = if (isLandscape) 20.sp else if (isTablet) 26.sp else 22.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = if (isLandscape) 12.sp else if (isTablet) 15.sp else 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        textAlign = TextAlign.Center
                    )

                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        Text(
                            text = email,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Medium,
                                fontSize = if (isLandscape) 12.sp else if (isTablet) 15.sp else 13.sp,
                                color = MaterialTheme.colorScheme.primary
                            ),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }

                    // OTP input with visible cursor
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                focusRequester.requestFocus()
                                keyboardController?.show()
                            }
                    ) {
                        // Hidden text field for input (but with visible cursor)
                        BasicTextField(
                            value = textFieldValue,
                            onValueChange = { newValue ->
                                val filtered = newValue.text.filter { it.isDigit() }.take(otpLength)
                                textFieldValue = newValue.copy(text = filtered)
                                onOtpChange(filtered)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(focusRequester)
                                .onFocusChanged { focusState ->
                                    isFocused = focusState.isFocused
                                },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            textStyle = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 32.sp,
                                letterSpacing = 12.sp,
                                textAlign = TextAlign.Center,
                                color = Color.Transparent // Text is invisible
                            ),
                            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary), // Visible cursor
                            decorationBox = { innerTextField ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    // Show underscores and digits overlay
                                    Text(
                                        text = if (textFieldValue.text.isEmpty()) underscoreDisplay else textFieldValue.text,
                                        style = MaterialTheme.typography.headlineLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 32.sp,
                                            letterSpacing = 12.sp,
                                            textAlign = TextAlign.Center,
                                            color = when {
                                                otpError != null -> MaterialTheme.colorScheme.error
                                                else -> MaterialTheme.colorScheme.onSurface
                                            }
                                        )
                                    )
                                    innerTextField()
                                }
                            }
                        )
                    }

                    // Error message
                    if (otpError != null) {
                        Text(
                            text = otpError,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }

                    // Verify button
                    Button(
                        onClick = onVerify,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(if (isLandscape) 48.dp else if (isTablet) 56.dp else 52.dp),
                        shape = RoundedCornerShape(28.dp),
                        enabled = otpValue.length == otpLength && !isVerifying,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
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
                                text = verifyButtonText,
                                fontWeight = FontWeight.Bold,
                                fontSize = if (isLandscape) 13.sp else 14.sp,
                                color = Color.White
                            )
                        }
                    }

                    // Resend & Cancel
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        OutlinedButton(
                            onClick = {
                                onResend()
                                shakeError = false
                                textFieldValue = TextFieldValue(text = "", selection = androidx.compose.ui.text.TextRange(0))
                            },
                            enabled = !isVerifying && countdown == 0,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = if (!isVerifying && countdown == 0)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                        ) {
                            Text(
                                if (countdown > 0) "Resend ($countdown)" else "Resend",
                                fontSize = if (isLandscape) 12.sp else 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        TextButton(
                            onClick = {
                                if (!isVerifying) {
                                    isDialogVisible = false
                                    onCancel()
                                }
                            },
                            enabled = !isVerifying,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                "Cancel",
                                fontSize = if (isLandscape) 12.sp else 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (!isVerifying)
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                        }
                    }
                }

                // Snackbar
                if (snackbarMessage != null) {
                    PivotaSnackbar(
                        message = snackbarMessage,
                        type = snackbarType,
                        onDismiss = onSnackbarDismiss,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp)
                    )
                }
            }
        }
    }
}