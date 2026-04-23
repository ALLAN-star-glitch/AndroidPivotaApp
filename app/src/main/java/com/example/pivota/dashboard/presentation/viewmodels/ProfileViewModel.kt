package com.example.pivota.dashboard.presentation.viewmodels

import com.example.pivota.dashboard.presentation.state.ProfileUiState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pivota.core.network.ApiResult
import com.example.pivota.dashboard.domain.useCase.GetProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase  // Changed from AuthUseCases to GetProfileUseCase
) : ViewModel() {

    private val _profileState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val profileState: StateFlow<ProfileUiState> = _profileState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _profileState.value = ProfileUiState.Loading

            when (val result = getProfileUseCase()) {  // Changed from authUseCases.getProfile()
                is ApiResult.Success -> {
                    _profileState.value = ProfileUiState.Success(result.data)
                }
                is ApiResult.Error -> {
                    _profileState.value = ProfileUiState.Error(
                        result.networkError.userFriendlyMessage
                    )
                }
                ApiResult.Loading -> {
                    // Already handled
                }
            }
        }
    }

    fun refreshProfile() {
        loadProfile()
    }
}