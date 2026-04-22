package com.example.pivota.core.network

import android.content.Context
import android.os.Build
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import java.util.concurrent.atomic.AtomicBoolean

object KtorClientFactory {

    @Volatile
    private var appContext: Context? = null

    private val isInitialized = AtomicBoolean(false)

    @Volatile
    private var cachedClient: HttpClient? = null

    fun init(context: Context) {
        // Don't use applicationContext here if context might be null
        // Just store the context directly
        if (!isInitialized.get()) {
            synchronized(this) {
                if (!isInitialized.get()) {
                    appContext = context
                    isInitialized.set(true)
                    println("✅ [KtorClientFactory] Initialized successfully")
                }
            }
        }
    }

    fun isInitialized(): Boolean = isInitialized.get()

    /**
     * Simple tablet detection using smallest width
     */
    private fun isTablet(context: Context): Boolean {
        val smallestWidthDp = context.resources.configuration.smallestScreenWidthDp
        // Standard tablet threshold is 600dp
        return smallestWidthDp >= 600
    }

    fun build(): HttpClient {
        // Return cached instance if available
        cachedClient?.let { return it }

        synchronized(this) {
            cachedClient?.let { return it }

            val context = appContext
            if (context == null || !isInitialized.get()) {
                throw IllegalStateException(
                    "KtorClientFactory not initialized. Call KtorClientFactory.init(context) " +
                            "in your Application class's attachBaseContext()"
                )
            }

            // Simple tablet detection
            val isTablet = isTablet(context)
            val deviceType = if (isTablet) "TABLET" else "MOBILE"

            // Logging
            println("========================================")
            println("📱 DEVICE DETECTION RESULTS:")
            println("📱 isTablet: $isTablet")
            println("📱 deviceType: $deviceType")
            println("📱 smallestWidthDp: ${context.resources.configuration.smallestScreenWidthDp}")
            println("📱 Device Model: ${Build.MODEL}")
            println("========================================")

            val client = HttpClient(Android) {
                install(HttpTimeout) {
                    requestTimeoutMillis = NetworkConstants.TIMEOUT_MILLIS
                    connectTimeoutMillis = NetworkConstants.CONNECT_TIMEOUT_MILLIS
                    socketTimeoutMillis = NetworkConstants.SOCKET_TIMEOUT_MILLIS
                }

                install(ContentNegotiation) {
                    json(Json {
                        ignoreUnknownKeys = true
                        prettyPrint = true
                        isLenient = true
                        encodeDefaults = true
                    })
                }

                install(Logging) {
                    level = LogLevel.BODY
                    logger = object : Logger {
                        override fun log(message: String) {
                            when {
                                message.contains("REQUEST:") -> println("🔍 HTTP REQUEST: $message")
                                message.contains("RESPONSE:") -> println("🔍 HTTP RESPONSE: $message")
                                message.contains("POST") -> println("🔍 HTTP: $message")
                                message.contains("ERROR") -> println("❌ HTTP ERROR: $message")
                                else -> println("🔍 KTOR: $message")
                            }
                        }
                    }
                }

                defaultRequest {
                    url(NetworkConstants.BASE_URL)
                    header(HttpHeaders.ContentType, ContentType.Application.Json)
                    header(HttpHeaders.Accept, ContentType.Application.Json)
                    header("ngrok-skip-browser-warning", "true")

                    // Device detection headers
                    header("X-Device-Type", deviceType)
                    header("X-Is-Mobile", (!isTablet).toString())
                    header("X-Is-Tablet", isTablet.toString())

                    // Device information
                    header("X-Device", getDeviceName())
                    header("X-Device-Model", Build.MODEL)
                    header("X-Device-Manufacturer", Build.MANUFACTURER)
                    header("X-Device-Brand", Build.BRAND)

                    // OS information
                    header("X-OS", "Android")
                    header("X-OS-Version", Build.VERSION.RELEASE)
                    header("X-OS-SDK", Build.VERSION.SDK_INT.toString())

                    // App information
                    header("X-App-Type", "native-android")
                    header("X-Client-Type", "mobile-app")

                    // Complete device info as JSON
                    val deviceInfo = mapOf(
                        "deviceType" to deviceType,
                        "isTablet" to isTablet,
                        "os" to "Android",
                        "osVersion" to Build.VERSION.RELEASE,
                        "device" to getDeviceName(),
                        "model" to Build.MODEL,
                        "manufacturer" to Build.MANUFACTURER,
                        "sdkVersion" to Build.VERSION.SDK_INT
                    )
                    header("X-Device-Info", deviceInfo.toString())
                }
            }

            cachedClient = client
            return client
        }
    }

    private fun getDeviceName(): String {
        return try {
            val manufacturer = Build.MANUFACTURER
            val model = Build.MODEL
            if (model.startsWith(manufacturer, ignoreCase = true)) {
                model
            } else {
                "$manufacturer $model"
            }
        } catch (e: Exception) {
            "Android Device"
        }
    }

    // Optional: For testing or resetting
    fun reset() {
        synchronized(this) {
            cachedClient?.close()
            cachedClient = null
            appContext = null
            isInitialized.set(false)
        }
    }
}