package com.example.pivota.core.composables.auth_composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.pivota.core.composables.core_composables.BackgroundImageAndOverlay

@Composable
fun SinglePaneLayout(header: String = "", showUpgradeButton: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        BackgroundImageAndOverlay(
            isWideScreen = false,
            header = header,
            offset = 140.dp,
            showUpgradeButton = showUpgradeButton
        )

    }
}