package com.example.pivota.auth.presentation.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.pivota.R

@Composable
fun LoginFormContent(
    topPadding: Dp,
    showHeader: Boolean = false,
    isWideScreen: Boolean = false,
    onRegisterClick: () -> Unit, // Renamed from onNavigateToRegisterScreen
    onLoginSuccess: () -> Unit    // Renamed from onNavigateToDashboardScreen
) {
    // State for input fields
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .padding(top = topPadding)
            .fillMaxSize()
            .clip(RoundedCornerShape(topEnd = 58.dp))
            .background(Color.White)
            .padding(24.dp)
            .zIndex(2f)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
        ) {

            // Only show header if in wide screen (i.e., two-pane layout)
            if (showHeader && isWideScreen) {
                HorizontalDivider(
                    modifier = Modifier
                        .width(100.dp)
                        .padding(bottom = 8.dp)
                        .align(Alignment.CenterHorizontally),
                    thickness = 2.dp,
                    color = Color(0xFFE9C16C)
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    "Welcome Back!\nSign in to continue",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold
                )
            }

            PivotaTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email or Phone") },
                placeholder = { Text("example@domain.com") },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,

            PivotaPasswordField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                colors = textFieldColors,

                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                // Toggle Icon
                trailingIcon = {
                    val image = if (passwordVisible)
                        Icons.Filled.Visibility
                    else Icons.Filled.VisibilityOff

                    val description = if (passwordVisible) "Hide password" else "Show password"

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = description)
                    }
                }
            )

            // Remember me now
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PivotaCheckBox(
                    checked = rememberMe,
                    onCheckedChange = { rememberMe = it },
                    text = "Remember me"
                )
            }

            // Action Buttons Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                PivotaPrimaryButton(
                    text = "Login",
                    onClick = onLoginSuccess // Updated Lambda call
                )

                Text("OR", color = Color.Gray)

                PivotaSecondaryButton(
                    text = "Register",
                    onclick = onRegisterClick // Updated Lambda call
                )
            }

            //Terms and Conditions Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PivotaCheckBox()

            }
        }
    }
}