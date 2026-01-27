package com.example.pivota.auth.models

data class SelectedDocument(
    val id: String,
    val name: String,
    val size: String,
    val status: String = "Uploaded"
)