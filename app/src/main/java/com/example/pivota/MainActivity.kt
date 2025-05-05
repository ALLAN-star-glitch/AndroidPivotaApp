package com.example.pivota

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.pivota.auth.RegistrationScreenFreeMembership
import com.example.pivota.ui.theme.PivotaTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PivotaTheme {

                RegistrationScreenFreeMembership()
            }
        }
    }
}
