package com.example.pivota

import WelcomeScreen
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.pivota.core.presentations.navigation.NavHostSetup
import com.example.pivota.ui.theme.PivotaTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge() // enables drawing behind system bars

        setContent {
            PivotaTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    contentWindowInsets = WindowInsets(0, 0, 0, 0) // removes top padding
                ) { innerPadding ->
                    NavHostSetup(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding) // still applies padding if needed in other screens
                    )
                }
            }
        }

        }
    }





