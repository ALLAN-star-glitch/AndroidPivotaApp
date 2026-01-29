package com.example.pivota.subscribe.models

import androidx.compose.ui.graphics.Color

data class Plan(
    val name: String,
    val features: List<String>,
    val isBestValue: Boolean = false,
    val color: Color = Color.Transparent
)