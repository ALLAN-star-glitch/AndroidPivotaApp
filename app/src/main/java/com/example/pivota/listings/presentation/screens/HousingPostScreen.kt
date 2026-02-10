package com.example.pivota.listings.presentation.screens

import androidx.compose.runtime.Composable
import com.example.pivota.listings.presentation.composables.housing.AdaptiveHousingPostLayout

object HousingPostScreen {
    @Composable
    fun Content(onBack: () -> Unit) {
        AdaptiveHousingPostLayout(
            onBack = onBack
        )
    }
}