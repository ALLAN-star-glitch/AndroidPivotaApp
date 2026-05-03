package com.example.pivota.dashboard.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pivota.core.network.ApiResult
import com.example.pivota.dashboard.domain.useCase.GetProfileUseCase
import com.example.pivota.dashboard.presentation.state.ProfileUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase
) : ViewModel() {

    private val _profileState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val profileState: StateFlow<ProfileUiState> = _profileState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _profileState.update { ProfileUiState.Loading }

            when (val result = getProfileUseCase()) {
                is ApiResult.Success -> {
                    _profileState.update { ProfileUiState.Success(result.data) }
                }
                is ApiResult.Error -> {
                    _profileState.update {
                        ProfileUiState.Error(
                            message = result.networkError.userFriendlyMessage,
                            technicalMessage = result.technicalMessage
                        )
                    }
                }
                ApiResult.Loading -> {
                    // Already handled by update above
                }
            }
        }
    }

    fun refreshProfile() {
        loadProfile()
    }
}