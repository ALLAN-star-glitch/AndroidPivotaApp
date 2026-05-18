package com.example.pivota.dashboard.presentation.viewmodels.client_general_viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pivota.dashboard.domain.model.listings_models.general.DiscoveryCategory
import com.example.pivota.dashboard.domain.useCase.GetAllServicesUseCase
import com.example.pivota.dashboard.presentation.state.CommonServicesUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllServicesViewModel @Inject constructor(
    private val getAllServicesUseCase: GetAllServicesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<CommonServicesUiState>(CommonServicesUiState.Loading)
    val uiState: StateFlow<CommonServicesUiState> = _uiState.asStateFlow()

    private var hasReceivedData = false

    init {
        loadAllServices()
    }

    fun loadAllServices() {
        getAllServicesUseCase()
            .onEach { allServices ->
                if (allServices.isNotEmpty()) {
                    hasReceivedData = true
                    _uiState.value = CommonServicesUiState.Success(allServices)
                    println("✅ Loaded ${allServices.size} services for All Services screen")
                } else if (!hasReceivedData) {
                    _uiState.value = CommonServicesUiState.Error("No services available")
                }
            }
            .catch { error ->
                if (!hasReceivedData) {
                    _uiState.value = CommonServicesUiState.Error(error.message ?: "Failed to load services")
                }
                println("❌ Failed to load services: ${error.message}")
            }
            .launchIn(viewModelScope)
    }

    fun refresh() {
        hasReceivedData = false
        _uiState.value = CommonServicesUiState.Loading
        viewModelScope.launch {
            getAllServicesUseCase.refresh()
            loadAllServices()
        }
    }
}