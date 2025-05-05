package com.example.pivota.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.window.core.layout.WindowSizeClass
import com.example.pivota.R
import com.example.pivota.components.PivotaTextField


@Composable
fun RegistrationScreenFreeMembership() {
    Box {
        val windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
        val isWideScreen = windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.HEIGHT_DP_MEDIUM_LOWER_BOUND)


        if (isWideScreen) {
            TwoPaneLayout()
        } else {
            SinglePaneLayout()
        }
    }
}

@Composable
fun TwoPaneLayout() {
    Row(modifier = Modifier.fillMaxSize()) {
        // Left Pane: Image
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .background(Color.White)
        ) {
            BackgroundImageAndOverlay(isWideScreen = true)
        }

        // Right Pane: Form
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .background(Color.White)
        ) {
            RegistrationFormContent(topPadding = 64.dp, showHeader = true, isWideScreen = true)
        }
    }
}

@Composable
fun SinglePaneLayout() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        BackgroundImageAndOverlay(isWideScreen = false)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(2f)
        ) {
            RegistrationFormContent(
                topPadding = 220.dp,
                showHeader = true,
                isWideScreen = false
            )
        }
    }
}

@Composable
fun BackgroundImageAndOverlay(isWideScreen: Boolean) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Background image
        Image(
            painter = painterResource(id = R.drawable.happy_clients),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = if (isWideScreen) Modifier.fillMaxSize() else Modifier
                .fillMaxWidth()
                .height(300.dp)
        )

        Box(
            modifier = Modifier
                .offset(y = 140.dp)
                .fillMaxWidth()
                .fillMaxHeight()
                .clip(RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp))
                .background(Color(0xAA008080))
                .zIndex(1f)
        ) {
            if (isWideScreen) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 32.dp, vertical = 40.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Welcome to Pivota!",
                        style = MaterialTheme.typography.headlineSmall.copy(color = Color.White)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "After registering, you can upgrade your account to post jobs, rentals, or services.",
                        style = MaterialTheme.typography.bodyLarge.copy(color = Color.White),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "It's free to join. Upgrade when you're ready!",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFFE9C16C)),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 24.dp)
                ) {
                    HorizontalDivider(
                        modifier = Modifier
                            .width(100.dp)
                            .padding(bottom = 8.dp),
                        thickness = 2.dp,
                        color = Color(0xFFE9C16C)
                    )

                    Text(
                        text = "REGISTER",
                        style = MaterialTheme.typography.headlineMedium.copy(color = Color.White),
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                }
            }
        }
    }
}




@Composable
fun RegistrationFormContent(topPadding: Dp, showHeader: Boolean = false, isWideScreen: Boolean = false) {
    val tealColor = Color(0xFF008080)

    // State for input fields
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // Password visibility toggles
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = tealColor,
        unfocusedBorderColor = tealColor,
        focusedLabelColor = tealColor,
        unfocusedLabelColor = tealColor,
        cursorColor = tealColor
    )

    Box(
        modifier = Modifier
            .padding(top = topPadding) // Push it down slightly, but still inside scroll
            .fillMaxSize()
            .clip(RoundedCornerShape(topEnd = 58.dp))
            .background(Color.White)
            .padding(24.dp)
            .zIndex(2f)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp), // Ensures padding doesn't shift form out of view
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

                Text(
                    text = "REGISTER",
                    style = MaterialTheme.typography.headlineMedium.copy(color = Color(0xFF008080)),
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }

            PivotaTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = "First Name",
                modifier = Modifier.fillMaxWidth(),
                )

            PivotaTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = "Last Name",
                modifier = Modifier.fillMaxWidth(),
            )

            PivotaTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email",
                modifier = Modifier.fillMaxWidth(),
            )

            PivotaTextField(
                value = phone,
                onValueChange = { phone = it },
                label = "Phone Number",
                modifier = Modifier.fillMaxWidth(),
                keyboardType = KeyboardType.Phone
            )

            // Password Input Field
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val iconRes = if (passwordVisible) R.drawable.ic_visibility else R.drawable.ic_visibility_off
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            painter = painterResource(id = iconRes),
                            contentDescription = if (passwordVisible) "Hide Password" else "Show Password"
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            // Confirm Password Input Field
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val iconRes = if (confirmPasswordVisible) R.drawable.ic_visibility else R.drawable.ic_visibility_off
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(
                            painter = painterResource(id = iconRes),
                            contentDescription = if (confirmPasswordVisible) "Hide Password" else "Show Password"
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            //Terms and Conditions Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = false,
                    onCheckedChange = {},
                    colors = CheckboxDefaults.colors(checkedColor = tealColor)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "I agree to the Terms and Conditions",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Register & Login Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { /* Register */ },
                    colors = ButtonDefaults.buttonColors(containerColor = tealColor)
                ) {
                    Text("Register")
                }

                Text("OR", color = Color.Gray)

                Button(
                    onClick = { /* Login */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    modifier = Modifier
                        .border(
                            width = 1.dp,
                            color = Color(0xFF3F51B5),
                            shape = RoundedCornerShape(50.dp)
                        ),
                    shape = RoundedCornerShape(50.dp)
                ) {
                    Text("Login", style = MaterialTheme.typography.bodyMedium.copy(Color.Black))
                }
            }

            // "Or Register With"
            Text(
                text = "Or Register With",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Image(
                    painter = painterResource(id = R.drawable.google),
                    contentDescription = "Google",
                    modifier = Modifier.size(40.dp)
                )
                Image(
                    painter = painterResource(id = R.drawable.facebook),
                    contentDescription = "Facebook",
                    modifier = Modifier.size(40.dp)
                )
                Image(
                    painter = painterResource(id = R.drawable.instagram),
                    contentDescription = "Instagram",
                    modifier = Modifier.size(40.dp)
                )
            }
        }
    }
}



@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewRegistrationScreenFreeMembership() {
    MaterialTheme {
        RegistrationScreenFreeMembership()
    }
}


