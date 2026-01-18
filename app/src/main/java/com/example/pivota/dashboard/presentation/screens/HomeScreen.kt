package com.example.pivota.dashboard.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pivota.dashboard.presentation.composables.HomeAppBar

@Composable
fun HomeScreen(
    isGuest: Boolean = false,
    onLockedAction: () -> Unit = {}
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(bottom = 16.dp) // Space for bottom navigation
    ) {
        // Top Landing App Bar
        HomeAppBar(
            isGuest = isGuest,
            onLockedAction = onLockedAction
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Main welcome content
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            // Simple greeting or instructions
            // You can expand this with featured sections later
        }
    }
}
