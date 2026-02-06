package com.example.pivota.listings.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.pivota.listings.domain.models.DocumentType
import com.example.pivota.listings.domain.models.EmploymentType
import com.example.pivota.listings.domain.models.JobType
import com.example.pivota.listings.domain.models.PayRate
import com.example.pivota.listings.presentation.state.PostJobUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * ViewModel for the Job Posting process.
 * Manages form state, adaptive logic, and schema alignment for MVP1.
 */
@HiltViewModel
class PostJobViewModel @Inject constructor() : ViewModel() {

    // Internal state
    private val _uiState = MutableStateFlow(PostJobUiState())
    val uiState: StateFlow<PostJobUiState> = _uiState.asStateFlow()

    // Simplified access for the Selector component
    var selectedType by mutableStateOf(JobType.INFORMAL)
        private set

    /* ───────── UI EVENT HANDLERS ───────── */

    fun onTypeChanged(newType: JobType) {
        selectedType = newType
        _uiState.update { it.copy(jobType = newType) }
    }

    fun updateTitle(title: String) {
        _uiState.update { it.copy(title = title) }
    }

    fun updatePay(amount: String) {
        // Handle numeric conversion safely
        val numericAmount = amount.toDoubleOrNull() ?: 0.0
        _uiState.update { it.copy(payAmount = numericAmount) }
    }

    fun toggleNegotiable(isNegotiable: Boolean) {
        _uiState.update { it.copy(isNegotiable = isNegotiable) }
    }

    fun toggleBenefit(benefit: String) {
        _uiState.update { state ->
            val current = state.benefits
            val updated = if (current.contains(benefit)) {
                current - benefit
            } else {
                current + benefit
            }
            state.copy(benefits = updated)
        }
    }

    fun toggleDocument(doc: DocumentType) {
        _uiState.update { state ->
            val current = state.documentsNeeded
            val updated = if (current.contains(doc)) {
                current - doc
            } else {
                current + doc
            }
            state.copy(documentsNeeded = updated)
        }
    }

    /* ───────── SUBMISSION LOGIC ───────── */

    fun submitJob() {
        val currentState = _uiState.value

        // MVP1 Logic: Join benefits into additionalNotes for schema compatibility
        val mappedNotes = if (currentState.benefits.isNotEmpty()) {
            "Benefits: ${currentState.benefits.joinToString(", ")}. ${currentState.additionalNotes ?: ""}"
        } else {
            currentState.additionalNotes
        }

        // TODO: Call Repository to save currentState.copy(additionalNotes = mappedNotes)
    }
}

