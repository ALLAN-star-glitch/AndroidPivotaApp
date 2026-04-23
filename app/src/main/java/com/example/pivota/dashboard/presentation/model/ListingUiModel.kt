package com.example.pivota.dashboard.presentation.model

import androidx.compose.runtime.Immutable
import com.example.pivota.dashboard.domain.model.ListingCategory
import com.example.pivota.dashboard.domain.model.ListingStatus

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