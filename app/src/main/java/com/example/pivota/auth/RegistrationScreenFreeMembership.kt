package com.example.pivota.auth

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.window.core.layout.WindowSizeClass
import com.example.pivota.R


@Composable
fun RegistrationScreenFreeMembership() {
    Box {
        val windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
        val isWideScreen = windowSizeClass.isWidthAtLeastBreakpoint(widthDpBreakpoint = windowSizeClass.minWidthDp)

        if (isWideScreen) {
            TwoPaneLayout()
        } else {
            SinglePaneLayout()
        }
    }
}

@Composable
fun TwoPaneLayout() {
    Row(
        modifier = Modifier.fillMaxSize()
    ) {
        // Left Pane
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .background(Color.White)
        ) {
            BackgroundImageAndOverlay()
        }

        // Right Pane (Registration Form)
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .background(Color.White)
        ) {
            RegistrationFormContent(topPadding = 64.dp)
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
        BackgroundImageAndOverlay()
        RegistrationFormContent(topPadding = 280.dp)
    }
}

@Composable
fun BackgroundImageAndOverlay() {
    Box {
        Image(
            painter = painterResource(id = R.drawable.happy_clients), // Replace with your image
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        )

        Box(
            modifier = Modifier
                .offset(y = 220.dp)
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(Color(0xAA008080)) // Teal with 70% opacity
                .zIndex(1f)
        ) {
            Text(
                text = "Register",
                style = MaterialTheme.typography.headlineMedium.copy(color = Color.White),
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
fun RegistrationFormContent(topPadding: Dp) {
    Box(
        modifier = Modifier
            .offset(y = topPadding)
            .fillMaxSize()
            .clip(RoundedCornerShape(topEnd = 32.dp))
            .background(Color.White)
            .padding(24.dp)
            .zIndex(2f)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = "",
                onValueChange = {},
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = "",
                onValueChange = {},
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = "",
                onValueChange = {},
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = { /* handle register */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Create Account")
            }
        }
    }
}
