package com.example.pivota.auth.presentation.viewModel



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pivota.auth.domain.useCase.AuthUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModel @Inject constructor(
    private val authUseCases: AuthUseCases
) : ViewModel() {

    fun onGetStartedClicked(onNavigate: () -> Unit) {
        viewModelScope.launch {
            authUseCases.setWelcomeSeen()
            onNavigate()
        }
    }
}