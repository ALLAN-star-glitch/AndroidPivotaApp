package com.example.pivota.listings.presentation.composables.housing

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.window.core.layout.WindowSizeClass
import com.example.pivota.core.presentations.composables.TopBar

@Composable
fun AdaptiveHousingPostLayout(
    onBack: () -> Unit,
    // TODO include viewmodel
) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val isWide =
        windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)

    Scaffold(
        topBar = {
            TopBar(
                title = "Post a House",
                onBack = onBack,
                icon = Icons.Outlined.Info
            )
        },
    ) { innerPadding ->
        Row (
            modifier = Modifier.padding(innerPadding).fillMaxWidth()
        ) {
            Surface(
                modifier = Modifier.weight(1f)
            ) {
                PostHousing()
            }
            if (isWide) {
                Surface(
                    modifier = Modifier.weight(1f)
                ) {
                    HousingPreviewCard(
                        price = "KES 45,000",
                        type = "For Rent",
                        loc = "Westlands"
                    )
                }
            }
        }
    }
}