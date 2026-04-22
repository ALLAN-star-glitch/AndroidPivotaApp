// NetworkConstants.kt
package com.example.pivota.core.network

import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

object NetworkConstants {
    // Change this to your current ngrok URL or local IP for testing
    // For local testing with physical device, use your computer's IP
    //const val BASE_URL = "http://192.168.1.xxx:10001/"  // Replace with your local IP
   // const val BASE_URL = "https://revisionary-leanne-diffusely.ngrok-free.dev/"
    const val BASE_URL = "http://10.0.2.2:10000/"

    // Increased timeout values
    const val TIMEOUT_MILLIS = 60000L           // 60 seconds
    const val CONNECT_TIMEOUT_MILLIS = 30000L   // 30 seconds
    const val SOCKET_TIMEOUT_MILLIS = 30000L    // 30 seconds

    object JsonProvider {
        val json = Json {
            prettyPrint = true
            encodeDefaults = true
            ignoreUnknownKeys = true
            isLenient = true
        }
    }

    // Create OkHttpClient with custom timeouts
    fun createOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
            .readTimeout(TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
            .writeTimeout(TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }
}