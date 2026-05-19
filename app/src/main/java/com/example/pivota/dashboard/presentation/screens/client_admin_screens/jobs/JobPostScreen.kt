package com.example.pivota.dashboard.presentation.screens.client_admin_screens.jobs

import androidx.compose.runtime.Composable
import com.example.pivota.dashboard.presentation.composables.client_admin_composables.listings_composables.jobs.AdaptiveJobPostLayout

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