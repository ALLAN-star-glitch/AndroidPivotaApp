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
fun Profile(
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
            "Profile",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            "View and manage your account settings, preferences, and personal details.",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (isGuest) onLockedAction() else {
                    // Navigate to edit profile screen
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = if (isGuest) "Login to Access Profile" else "Edit Profile")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Add more sections like Account Details, Settings shortcuts, or Logout button
    }
}
