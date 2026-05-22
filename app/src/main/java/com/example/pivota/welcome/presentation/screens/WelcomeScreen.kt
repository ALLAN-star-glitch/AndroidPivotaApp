package com.example.pivota.welcome.presentation.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.example.pivota.core.preferences.PivotaDataStore
import com.example.pivota.welcome.presentation.composables.adaptive_layout.AdaptiveWelcomeLayout

@Composable
fun WelcomeScreen(
    onNavigateToContinueSetup: () -> Unit,
    onNavigateToContinueWithGoogle: () -> Unit,
    onNavigateToLogin: () -> Unit,
    datastore: PivotaDataStore
) {

    LaunchedEffect(Unit) {
        datastore.markWelcomeScreenSeen(true)
        println("🔍 [WelcomeScreen] Setting hasSeenWelcome = true")
    }

    AdaptiveWelcomeLayout(
        header = "Let's Connect You!",
        welcomeText = "Your all-in-one platform for verified jobs, quality housing, and essential support services across Kenya.",
        onNavigateToContinueSetup = onNavigateToContinueSetup,
        onNavigateToContinueWithGoogle = onNavigateToContinueWithGoogle,
        onNavigateToLogin = onNavigateToLogin
    )
}