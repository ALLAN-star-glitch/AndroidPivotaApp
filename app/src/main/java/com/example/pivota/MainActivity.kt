package com.example.pivota


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.pivota.core.navigation.NavHostSetup
import com.example.pivota.ui.theme.PivotaConnectTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge() // enables drawing behind system bars

        setContent {
            PivotaConnectTheme {
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





