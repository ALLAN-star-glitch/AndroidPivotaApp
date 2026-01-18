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
fun Post(isGuest: Boolean, onLockedAction: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Post Screen")
        Spacer(modifier = Modifier.height(16.dp))

        if (isGuest) {
            Button(onClick = onLockedAction) {
                Text("Login to post")
            }
        } else {
            // Show the full posting form for logged-in users
            Text("Here is the full posting form...")
        }
    }
}
