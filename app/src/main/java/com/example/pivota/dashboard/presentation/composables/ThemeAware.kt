// ThemeAware.kt
package com.example.pivota.dashboard.presentation.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pivota.core.presentations.viewmodel.ThemeViewModel
import com.example.pivota.ui.theme.PivotaConnectTheme

@Composable
fun ThemeAware(
    themeViewModel: ThemeViewModel = hiltViewModel(),
    content: @Composable () -> Unit
) {
    val isDarkTheme by themeViewModel.isDarkTheme

    PivotaConnectTheme(
        darkTheme = isDarkTheme
    ) {
        content()
    }
}