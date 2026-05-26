package com.example.pivota.dashboard.presentation.viewmodels.client_general_viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pivota.dashboard.domain.useCase.GetSubcategoriesUseCase
import com.example.pivota.dashboard.presentation.state.SubcategoriesUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject



/**
 * ViewModel for managing subcategories screen state
 */
@HiltViewModel
class SubcategoriesViewModel @Inject constructor(
    private val getSubcategoriesUseCase: GetSubcategoriesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<SubcategoriesUiState>(SubcategoriesUiState.Loading)
    val uiState: StateFlow<SubcategoriesUiState> = _uiState.asStateFlow()

    /**
     * Load subcategories for a given parent category
     * @param parentCategoryId The ID of the parent category
     */
    fun loadSubcategories(parentCategoryId: String) {
        // Reset to loading state
        _uiState.value = SubcategoriesUiState.Loading

        getSubcategoriesUseCase(parentCategoryId)
            .onEach { subcategories ->
                if (subcategories.isEmpty()) {
                    // No subcategories found - this is still a success state (empty list)
                    _uiState.value = SubcategoriesUiState.Success(emptyList())
                } else {
                    _uiState.value = SubcategoriesUiState.Success(subcategories)
                }
            }
            .catch { error ->
                // Handle any errors that occur during the flow
                _uiState.value = SubcategoriesUiState.Error(
                    error.message ?: "Failed to load subcategories"
                )
            }
            .launchIn(viewModelScope)
    }

    /**
     * Refresh subcategories (clear cache and reload)
     */
    fun refresh(parentCategoryId: String) {
        // You can add cache refresh logic here if needed
        loadSubcategories(parentCategoryId)
    }
}