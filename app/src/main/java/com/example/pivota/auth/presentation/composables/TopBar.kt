package com.example.pivota.auth.presentation.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(callback: () -> Unit, title: String?) {
    TopAppBar(
        title = {
            if (title != null) {
                Text(title)
            }
        },
        navigationIcon = {
            IconButton(onClick = {}) {
                Icon(imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Back")
            }
        },
    )
}