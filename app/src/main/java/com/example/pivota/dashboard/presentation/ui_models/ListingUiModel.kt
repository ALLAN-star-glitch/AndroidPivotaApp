package com.example.pivota.dashboard.presentation.ui_models

import androidx.compose.runtime.Immutable
import com.example.pivota.dashboard.domain.ListingStatus
import com.example.pivota.dashboard.domain.model.listings_models.general.ListingCategory


@Immutable
data class ListingUiModel(
    val id: String,
    val title: String,
    val category: ListingCategory,
    val status: ListingStatus,
    val descriptionPreview: String,
    val views: Int,
    val messages: Int,
    val requests: Int,
    val performanceHint: PerformanceHint? = null,
    val isHighlighted: Boolean = false
)