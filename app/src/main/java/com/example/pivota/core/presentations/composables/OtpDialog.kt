package com.example.pivota.core.presentations.composables

import android.annotation.SuppressLint
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.repeatable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
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
    otpValues: List<String>,
    onOtpDigitChange: (Int, String) -> Unit,
    isVerifying: Boolean,
    otpError: String?,
    countdown: Int,
    resendCount: Int,
    title: String = "Verify Your Email",
    description: String = "We've sent a verification code to",
    verifyButtonText: String = "Verify",
    onVerify: () -> Unit,
    onResend: () -> Unit,
    onCancel: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val isLandscape = screenWidth > screenHeight
    val isTablet = screenWidth > 600.dp
    val scrollState = rememberScrollState()

    var shakeError by remember { mutableStateOf(false) }

    LaunchedEffect(otpError) {
        if (otpError != null) {
            shakeError = true
            delay(500)
            shakeError = false
        }
    }

    val dialogWidth = when {
        isTablet -> screenWidth * 0.45f
        isLandscape -> screenWidth * 0.7f
        else -> screenWidth * 0.92f
    }

    val dialogMaxHeight = if (isLandscape) screenHeight * 0.9f else screenHeight * 0.8f

    val focusRequesters = List(6) { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    // Handle focus and keyboard
    LaunchedEffect(otpValues) {
        val filledCount = otpValues.count { it.isNotEmpty() }
        if (filledCount < 6) {
            focusRequesters[filledCount].requestFocus()
            keyboardController?.show()
        } else {
            focusManager.clearFocus()
            keyboardController?.hide()
        }
    }

    // FIXED: Natural deletion - deletes current digit or moves to previous
    fun handleDelete(index: Int) {
        when {
            // Current cell has a digit - clear it and stay on this cell
            otpValues[index].isNotEmpty() -> {
                onOtpDigitChange(index, "")
                focusRequesters[index].requestFocus()
            }
            // Current cell is empty and not the first cell - move to previous cell
            index > 0 -> {
                // Move focus to previous cell
                focusRequesters[index - 1].requestFocus()
                // If previous cell has a digit, clear it
                if (otpValues[index - 1].isNotEmpty()) {
                    onOtpDigitChange(index - 1, "")
                }
            }
        }
    }

    val otpComposition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.otp_verification_animation)
    )
    val otpProgress by animateLottieCompositionAsState(
        composition = otpComposition,
        iterations = LottieConstants.IterateForever,
        isPlaying = true
    )

    Dialog(
        onDismissRequest = { if (!isVerifying) onCancel() },
        properties = DialogProperties(
            dismissOnBackPress = !isVerifying,
            dismissOnClickOutside = !isVerifying,
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = Modifier
                .width(dialogWidth)
                .heightIn(max = dialogMaxHeight)
                .padding(16.dp),
            shape = RoundedCornerShape(32.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            shadowElevation = 24.dp
        ) {
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

                Row(
                    horizontalArrangement = Arrangement.spacedBy(if (isLandscape) 8.dp else if (isTablet) 14.dp else 10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    for (index in 0..5) {
                        val cellSize = when {
                            isLandscape -> 48.dp
                            isTablet -> 64.dp
                            else -> 52.dp
                        }

                        OtpSquareDigitCell(
                            digit = otpValues.getOrElse(index) { "" },
                            isActive = otpValues[index].isEmpty() && otpValues.take(index).all { it.isNotEmpty() },
                            isError = otpError != null,
                            hasValue = otpValues[index].isNotEmpty(),
                            fontSize = if (isLandscape) 20.sp else if (isTablet) 28.sp else 22.sp,
                            cellSize = cellSize,
                            focusRequester = focusRequesters[index],
                            shakeError = shakeError,
                            modifier = Modifier.weight(1f),
                            onDigitChange = { digit ->
                                if (digit.length <= 1 && (digit.isEmpty() || digit.all { it.isDigit() })) {
                                    onOtpDigitChange(index, digit)
                                    if (digit.isNotEmpty() && index < 5) {
                                        focusRequesters[index + 1].requestFocus()
                                    }
                                }
                            },
                            onDelete = { handleDelete(index) }
                        )
                    }
                }

                AnimatedVisibility(
                    visible = otpError != null,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { 20 }),
                    exit = fadeOut() + slideOutVertically(targetOffsetY = { -20 })
                ) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.15f),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        Text(
                            text = otpError ?: "",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = if (isLandscape) 11.sp else if (isTablet) 13.sp else 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.error
                            ),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                        )
                    }
                }

                Button(
                    onClick = onVerify,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (isLandscape) 48.dp else if (isTablet) 56.dp else 52.dp),
                    shape = RoundedCornerShape(28.dp),
                    enabled = !isVerifying && otpValues.joinToString("").length == 6,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    if (isVerifying) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Verifying...",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = if (isLandscape) 13.sp else 14.sp,
                                color = Color.White
                            )
                        }
                    } else {
                        Text(
                            verifyButtonText,
                            fontWeight = FontWeight.Bold,
                            fontSize = if (isLandscape) 13.sp else 14.sp,
                            color = Color.White
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(
                        onClick = onResend,
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
                            if (countdown > 0) "Resend (${countdown}s)" else "Resend",
                            fontSize = if (isLandscape) 12.sp else 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    TextButton(
                        onClick = onCancel,
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
        }
    }
}

@Composable
fun OtpSquareDigitCell(
    digit: String,
    isActive: Boolean,
    isError: Boolean,
    hasValue: Boolean,
    fontSize: TextUnit,
    cellSize: Dp,
    focusRequester: FocusRequester,
    shakeError: Boolean,
    modifier: Modifier = Modifier,
    onDigitChange: (String) -> Unit,
    onDelete: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    var isFocused by remember { mutableStateOf(false) }

    // Shake animation for error
    val shakeAnim by animateFloatAsState(
        targetValue = if (shakeError && isActive) 10f else 0f,
        animationSpec = repeatable(
            iterations = 3,
            animation = keyframes {
                durationMillis = 200
                0f at 0
                10f at 50
                -10f at 100
                10f at 150
                0f at 200
            }
        ),
        label = "shake"
    )

    val borderColor = when {
        isError -> MaterialTheme.colorScheme.error
        isFocused -> MaterialTheme.colorScheme.primary
        hasValue -> MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
        else -> MaterialTheme.colorScheme.outlineVariant
    }

    val borderWidth = when {
        isFocused -> 2.dp
        hasValue -> 1.8.dp
        else -> 1.2.dp
    }

    val backgroundColor = when {
        isFocused -> MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        hasValue -> MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
        else -> MaterialTheme.colorScheme.surface
    }

    val scale = if (isFocused) 1.02f else 1f

    Box(
        modifier = modifier
            .size(cellSize)
            .scale(scale)
            .then(
                if (shakeError && isActive) {
                    Modifier.offset(x = shakeAnim.dp, y = 0.dp)
                } else Modifier
            )
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .border(
                width = borderWidth,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = isActive
            ) {
                if (isActive) {
                    focusRequester.requestFocus()
                }
            },
        contentAlignment = Alignment.Center
    ) {
        BasicTextField(
            value = digit,
            onValueChange = { newValue ->
                if (newValue.length <= 1 && (newValue.isEmpty() || newValue.all { it.isDigit() })) {
                    if (newValue.isEmpty()) {
                        onDelete()
                    } else {
                        onDigitChange(newValue)
                    }
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
                .focusRequester(focusRequester)
                .onFocusChanged { focusState ->
                    isFocused = focusState.isFocused
                },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            textStyle = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = fontSize,
                color = when {
                    hasValue -> MaterialTheme.colorScheme.primary
                    isFocused -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                },
                textAlign = TextAlign.Center
            ),
            singleLine = true,
            enabled = true,
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (digit.isEmpty() && !isFocused) {
                        Text(
                            text = "—",
                            fontSize = fontSize,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                        )
                    } else {
                        innerTextField()
                    }
                }
            }
        )
    }
}