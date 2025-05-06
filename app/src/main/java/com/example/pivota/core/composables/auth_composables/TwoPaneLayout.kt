package com.example.pivota.core.composables.auth_composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.pivota.core.composables.core_composables.BackgroundImageAndOverlay

@Composable
fun TwoPaneLayout(welcomeText: String = "", desc1: String = "", desc2: String = "", formContent: @Composable (topPadding: Dp, showHeader: Boolean, isWideScreen: Boolean)->Unit, showUgradeButton: Boolean) {
    Row(modifier = Modifier.fillMaxSize()) {
        // Left Pane: Image
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .background(Color.White)
        ) {
            BackgroundImageAndOverlay(
                isWideScreen = true,
                welcomeText = welcomeText,
                desc1 = desc1,
                desc2 = desc2,
                offset = 200.dp,
                showUpgradeButton = showUgradeButton
            )
        }

        // Right Pane: Form
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .background(Color.White)
        ) {

            formContent(64.dp, false, false)

        }
    }
}