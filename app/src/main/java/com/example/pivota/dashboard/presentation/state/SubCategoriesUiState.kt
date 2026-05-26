package com.example.pivota.dashboard.presentation.state

import com.example.pivota.dashboard.domain.model.listings_models.general.Category

/**
 * UI State for the Subcategories screen
 */
sealed class SubcategoriesUiState {
    object Loading : SubcategoriesUiState()
    data class Success(val subcategories: List<Category>) : SubcategoriesUiState()
    data class Error(val message: String) : SubcategoriesUiState()
}