package com.example.pivota.dashboard.presentation.model

sealed class PerformanceHint {
    data object HighInterest : PerformanceHint()
    data object NewResponses : PerformanceHint()
    data class Custom(val message: String) : PerformanceHint()
}