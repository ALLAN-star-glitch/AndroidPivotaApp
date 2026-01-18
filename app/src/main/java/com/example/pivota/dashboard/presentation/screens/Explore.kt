package com.example.pivota.dashboard.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Explore(
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
            Text("Explore Screen")
            Spacer(modifier = Modifier.height(16.dp))

            if (isGuest) {
                  Button(onClick = onLockedAction) {
                        Text("Login to explore full features")
                  }
            } else {
                  // Show full Explore content for logged-in users
                  Text("Here is the full Explore content...")
            }
      }
}
