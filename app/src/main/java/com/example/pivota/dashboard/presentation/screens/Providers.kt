package com.example.pivota.dashboard.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Providers(
    isGuest: Boolean = false,
    onLockedAction: () -> Unit = {}
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Text(
            "Service Providers",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            "Browse available service providers across different categories. Guests have limited access.",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (isGuest) onLockedAction() else {
                    // Navigate to provider details or booking
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = if (isGuest) "Login to Access Providers" else "View Providers")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // You can add more sections here: Featured Providers, Top-rated, or Categories
    }
}
