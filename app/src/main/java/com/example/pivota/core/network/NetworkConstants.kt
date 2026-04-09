package com.example.pivota.core.network

import kotlinx.serialization.json.Json

object NetworkConstants {
    const val BASE_URL = "https://revisionary-leanne-diffusely.ngrok-free.dev/"

    const val TIMEOUT_MILLIS = 30000L
    const val CONNECT_TIMEOUT_MILLIS = 15000L
    const val SOCKET_TIMEOUT_MILLIS = 15000L

    object JsonProvider {
        val json = Json {
            prettyPrint = true
            encodeDefaults = true
            ignoreUnknownKeys = true
            isLenient = true
        }
    }
}


