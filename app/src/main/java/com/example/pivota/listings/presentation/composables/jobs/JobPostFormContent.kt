package com.example.pivota.listings.presentation.composables.jobs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pivota.listings.presentation.viewmodel.PostJobViewModel

@Composable
fun JobPostFormContent(
    viewModel: PostJobViewModel
) {
    val scrollState = rememberScrollState()

    // We use a Column with verticalScroll for the form editor
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        // Section 1: Guidance Banner (Sticky-ish)
        SmartMatchGuidanceBanner()

        // Section 2: Job Type Selection (Informal vs Formal)
        // This drives the visibility of other sections
        JobTypeSelector(
            selectedType = viewModel.selectedType,
            onTypeSelected = { viewModel.onTypeChanged(it) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Section 3: Core Details (Title, Category, Location)
        CoreJobDetailsSection(viewModel)

        Spacer(modifier = Modifier.height(24.dp))

        // Section 4: Pay & Negotiation
        CompensationSection(viewModel)

        Spacer(modifier = Modifier.height(24.dp))

        // Section 5: Benefits (Forward-Compatible Pills)
        BenefitsSection(viewModel)

        Spacer(modifier = Modifier.height(24.dp))

        // Section 6: Adaptive Requirements
        // Swaps between Equipment (Informal) or Education/Docs (Formal)
        AdaptiveRequirementsSection(viewModel)

        Spacer(modifier = Modifier.height(48.dp))

        // Section 9: Actions
        PostJobActionButtons(viewModel)

        Spacer(modifier = Modifier.height(32.dp))
    }
}