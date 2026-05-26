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
        println("🔵 [AllServicesViewModel] loadAllServices() called")

        getAllServicesUseCase()
            .onEach { allServices ->
                println("🔵 [AllServicesViewModel] Received ${allServices.size} services")
                println("========== ALL SERVICES DATA ==========")
                allServices.forEachIndexed { index, category ->
                    println("${index + 1}. Category: '${category.name}'")
                    println("   - ID: ${category.id}")
                    println("   - hasSubcategories: ${category.hasSubcategories}")
                    println("   - type: ${category.type}")
                    println("   - vertical: ${category.vertical}")
                    println("   - slug: ${category.slug}")
                    println("---")
                }
                println("========================================")

                // Check specifically for Job Fairs
                val jobFairs = allServices.find { it.name == "Job Fairs" || it.id == "cmnbohmsc003barihgdp7hi6x" }
                if (jobFairs != null) {
                    println("🎯 FOUND JOB FAIRS:")
                    println("   - Name: ${jobFairs.name}")
                    println("   - ID: ${jobFairs.id}")
                    println("   - hasSubcategories: ${jobFairs.hasSubcategories}")
                    println("   - This should be FALSE according to API data!")
                } else {
                    println("⚠️ Job Fairs NOT found in the list!")
                }

                if (allServices.isNotEmpty()) {
                    hasReceivedData = true
                    _uiState.value = CommonServicesUiState.Success(allServices)
                    println("✅ Loaded ${allServices.size} services for All Services screen")
                } else if (!hasReceivedData) {
                    _uiState.value = CommonServicesUiState.Error("No services available")
                    println("⚠️ No services available")
                }
            }
            .catch { error ->
                println("❌ [AllServicesViewModel] Error loading services: ${error.message}")
                error.printStackTrace()
                if (!hasReceivedData) {
                    _uiState.value = CommonServicesUiState.Error(error.message ?: "Failed to load services")
                }
                println("❌ Failed to load services: ${error.message}")
            }
            .launchIn(viewModelScope)
    }

    fun refresh() {
        println("🔄 [AllServicesViewModel] refresh() called")
        hasReceivedData = false
        _uiState.value = CommonServicesUiState.Loading
        viewModelScope.launch {
            println("🔄 [AllServicesViewModel] Refreshing cache...")
            getAllServicesUseCase.refresh()
            println("🔄 [AllServicesViewModel] Cache refreshed, reloading...")
            loadAllServices()
        }
    }
}