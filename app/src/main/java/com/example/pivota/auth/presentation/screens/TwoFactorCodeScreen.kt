package com.example.pivota.auth.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pivota.R
import kotlinx.coroutines.delay

@Preview(showBackground = true)
@Composable
fun TwoFactorCodeScreen(
    onVerify: (String) -> Unit = {},
    onResend: () -> Unit = {},
    onChangeContact: () -> Unit = {}
) {
    val otpLength = 6
    val focusRequesters = remember { List(otpLength) { FocusRequester() } }
    val otpValues = remember { mutableStateListOf(*Array(otpLength) { "" }) }

    var timeLeft by remember { mutableIntStateOf(45) }

    LaunchedEffect(Unit) {
        while (timeLeft > 0) {
            delay(1000)
            timeLeft--
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(Modifier.height(24.dp))

        Icon(
            painter = painterResource(id = R.drawable.verified_user_24px), // replace
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(48.dp)
        )

        Spacer(Modifier.height(16.dp))

        Text(
            "Confirm your contact",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(Modifier.height(8.dp))

        Text(
            "Enter the 6-digit code sent to your email or phone to secure your account and activate your services.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(24.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
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
                    modifier = Modifier.focusRequester(focusRequesters[index])
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = if (timeLeft > 0)
                "Resend code in 00:${timeLeft.toString().padStart(2, '0')}"
            else
                "Resend code",
            color = if (timeLeft > 0)
                MaterialTheme.colorScheme.onSurfaceVariant
            else
                MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .clickable(enabled = timeLeft == 0) {
                    timeLeft = 45
                    onResend()
                }
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                val code = otpValues.joinToString("")
                onVerify(code)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = otpValues.all { it.isNotEmpty() }
        ) {
            Text("Verify & Continue")
        }

        Spacer(Modifier.height(16.dp))

        TextButton(onClick = onChangeContact) {
            Text("Change email or phone")
        }

        Spacer(Modifier.height(12.dp))

        Text(
            "Didn't receive a code?",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
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
            .width(48.dp)
            .height(56.dp)
            .onKeyEvent() {
                if (it.type == KeyEventType.KeyDown && it.key == Key.Backspace) {
                    onBackspace()
                    true
                } else false
            },
        textStyle = LocalTextStyle.current.copy(
            textAlign = TextAlign.Center,
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium
        ),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.NumberPassword
        ),
        shape = RoundedCornerShape(10.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
        )
    )
}
