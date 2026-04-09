package com.example.pivota.welcome.presentation.viewmodel

import JoiningAsUiState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pivota.core.preferences.PivotaDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JoiningAsViewModel @Inject constructor(
    private val datastore: PivotaDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(JoiningAsUiState())
    val uiState: StateFlow<JoiningAsUiState> = _uiState.asStateFlow()

    fun selectAccountType(accountType: String) {
        _uiState.update { it.copy(selectedAccountType = accountType) }
    }

    fun confirmAccountType() {
        val accountType = _uiState.value.selectedAccountType
        if (accountType != null) {
            viewModelScope.launch {
                try {
                    datastore.setAccountType(accountType)
                    _uiState.update { it.copy(isConfirmed = true) }
                } catch (e: Exception) {
                    _uiState.update { it.copy(error = e.message ?: "Failed to save account type") }
                }
            }
        }
    }

    fun resetSelection() {
        viewModelScope.launch {
            try {
                // Clear from DataStore as well
                datastore.clear()
            } catch (e: Exception) {
                // Ignore clearing error
            }
            _uiState.update {
                it.copy(
                    selectedAccountType = null,
                    isConfirmed = false,
                    error = null,
                    isLoading = false
                )
            }
        }
    }

    fun setLoading(isLoading: Boolean) {
        _uiState.update { it.copy(isLoading = isLoading) }
    }

    fun setError(error: String?) {
        _uiState.update { it.copy(error = error) }
    }
}