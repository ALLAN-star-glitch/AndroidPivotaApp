package com.example.pivota.dashboard.presentation.screens.client_admin_screens.housing

import androidx.compose.runtime.Composable
import com.example.pivota.dashboard.presentation.composables.client_admin_composables.listings_composables.housing.AdaptiveHousingPostLayout

object HousingPostScreen {
    @Composable
    fun Content(onBack: () -> Unit) {
        AdaptiveHousingPostLayout(
            onBack = onBack
        )
    }
}