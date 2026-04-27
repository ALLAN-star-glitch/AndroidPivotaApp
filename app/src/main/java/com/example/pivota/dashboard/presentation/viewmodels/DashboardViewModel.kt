
package com.example.pivota.dashboard.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pivota.core.auth.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val tokenManager: TokenManager
) : ViewModel() {

    fun startTokenRefresh() {
        viewModelScope.launch {
            tokenManager.startAutoRefresh()
        }
    }

    fun stopTokenRefresh() {
        tokenManager.stopAutoRefresh()
    }

    override fun onCleared() {
        super.onCleared()
        tokenManager.stopAutoRefresh()
    }
}