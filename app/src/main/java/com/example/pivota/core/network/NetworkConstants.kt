package com.example.pivota.core.network

import kotlinx.serialization.json.Json

object NetworkConstants {
    //const val BASE_URL = "https://revisionary-leanne-diffusely.ngrok-free.dev/"
    const val BASE_URL = "http://10.0.2.2:10001"

    // Increased timeout values
    const val TIMEOUT_MILLIS = 60000L           // 60 seconds (was 30000)
    const val CONNECT_TIMEOUT_MILLIS = 30000L   // 30 seconds (was 15000)
    const val SOCKET_TIMEOUT_MILLIS = 30000L    // 30 seconds (was 15000)

    object JsonProvider {
        val json = Json {
            prettyPrint = true
            encodeDefaults = true
            ignoreUnknownKeys = true
            isLenient = true
        }
    }
}