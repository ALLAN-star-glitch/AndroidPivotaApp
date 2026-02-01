package com.example.pivota.auth.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pivota.auth.domain.useCase.AuthUseCases
import com.example.pivota.core.navigation.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authUseCases: AuthUseCases
) : ViewModel() {

    // 1. Change type from String? to Any?
    private val _startDestination = MutableStateFlow<Any?>(null)
    val startDestination = _startDestination.asStateFlow()

    init {
        checkNavigationState()
    }

    private fun checkNavigationState() {
        viewModelScope.launch {
            val hasSeenWelcome = authUseCases.hasSeenWelcome()

            // 2. Assign the OBJECTS, not Strings
            if (hasSeenWelcome) {
                _startDestination.value = GuestDashboard
            } else {
                _startDestination.value = Welcome
            }
        }
    }
}