
package com.example.pivota.core.presentations.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pivota.core.data.ThemeManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val themeManager: ThemeManager
) : ViewModel() {

    private val _isDarkTheme = mutableStateOf(false)
    val isDarkTheme: State<Boolean> = _isDarkTheme

    init {
        // Observe theme changes from DataStore
        themeManager.isDarkThemeFlow.onEach { isDark ->
            _isDarkTheme.value = isDark
        }.launchIn(viewModelScope)
    }

    fun toggleTheme() {
        viewModelScope.launch {
            themeManager.toggleTheme()
        }
    }

    fun setTheme(isDark: Boolean) {
        viewModelScope.launch {
            themeManager.setDarkTheme(isDark)
        }
    }
}