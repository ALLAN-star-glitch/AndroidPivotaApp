package com.example.pivota.listings.presentation.screens

import androidx.compose.runtime.Composable
import com.example.pivota.listings.presentation.composables.jobs.AdaptiveJobPostLayout

/**
 * Screen wrapper for the Job Posting flow.
 * This serves as the entry point for the Navigation Graph.
 */
object JobPostScreen {

    @Composable
    fun Content(onBack: () -> Unit) {
        // This coordinates the adaptive layout, form, and live preview
        AdaptiveJobPostLayout(
            onBack = onBack
        )
    }
}