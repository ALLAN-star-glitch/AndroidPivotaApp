// KtorClientFactory.kt - Fixed version
package com.example.pivota.core.network

import android.content.Context
import android.os.Build
import com.example.pivota.core.auth.TokenProvider
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
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import java.util.concurrent.atomic.AtomicBoolean

object KtorClientFactory {

    @Volatile
    private var appContext: Context? = null

    private val isInitialized = AtomicBoolean(false)

    @Volatile
    private var cachedClient: HttpClient? = null
    @Volatile
    private var cachedUnauthClient: HttpClient? = null

    @Volatile
    private var tokenProvider: TokenProvider? = null  // Fixed: changed to tokenProvider

    fun init(context: Context) {
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

    fun setTokenProvider(provider: TokenProvider) {  // Added this missing method
        tokenProvider = provider
        synchronized(this) {
            cachedClient?.close()
            cachedClient = null
        }
    }

    fun isInitialized(): Boolean = isInitialized.get()

    private fun isTablet(context: Context): Boolean {
        val smallestWidthDp = context.resources.configuration.smallestScreenWidthDp
        return smallestWidthDp >= 600
    }

    fun buildUnauth(): HttpClient {
        cachedUnauthClient?.let { return it }

        synchronized(this) {
            cachedUnauthClient?.let { return it }

            val context = appContext
            if (context == null || !isInitialized.get()) {
                throw IllegalStateException(
                    "KtorClientFactory not initialized. Call KtorClientFactory.init(context) " +
                            "in your Application class's attachBaseContext()"
                )
            }

            val isTablet = isTablet(context)
            val deviceType = if (isTablet) "TABLET" else "MOBILE"

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

                    // NO AUTH HEADER HERE
                    println("🔑 [Auth] Client WITHOUT auth header")

                    // Device detection headers
                    header("X-Device-Type", deviceType)
                    header("X-Is-Mobile", (!isTablet).toString())
                    header("X-Is-Tablet", isTablet.toString())
                    header("X-Device", getDeviceName())
                    header("X-Device-Model", Build.MODEL)
                    header("X-Device-Manufacturer", Build.MANUFACTURER)
                    header("X-Device-Brand", Build.BRAND)
                    header("X-OS", "Android")
                    header("X-OS-Version", Build.VERSION.RELEASE)
                    header("X-OS-SDK", Build.VERSION.SDK_INT.toString())
                    header("X-App-Type", "native-android")
                    header("X-Client-Type", "mobile-app")
                }
            }
            cachedUnauthClient = client
            return client
        }
    }

    fun build(): HttpClient {
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

            val isTablet = isTablet(context)
            val deviceType = if (isTablet) "TABLET" else "MOBILE"

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
                            val safeMessage = if (message.contains("Bearer")) {
                                message.replace(Regex("Bearer [\\w.-]+"), "Bearer [REDACTED]")
                            } else {
                                message
                            }
                            when {
                                safeMessage.contains("REQUEST:") -> println("🔍 HTTP REQUEST: $safeMessage")
                                safeMessage.contains("RESPONSE:") -> println("🔍 HTTP RESPONSE: $safeMessage")
                                safeMessage.contains("POST") -> println("🔍 HTTP: $safeMessage")
                                safeMessage.contains("ERROR") -> println("❌ HTTP ERROR: $safeMessage")
                                else -> println("🔍 KTOR: $safeMessage")
                            }
                        }
                    }
                }

                defaultRequest {
                    url(NetworkConstants.BASE_URL)
                    header(HttpHeaders.ContentType, ContentType.Application.Json)
                    header(HttpHeaders.Accept, ContentType.Application.Json)
                    header("ngrok-skip-browser-warning", "true")

                    // Add Authorization header if token exists - FIXED variable names
                    try {
                        val provider = tokenProvider
                        if (provider != null) {
                            val token = runBlocking { provider.getAccessToken() }  // FIXED: provider, not manager
                            if (token != null) {
                                println("🔑 [Auth] Adding Bearer token: ${token.take(30)}...")
                                header(HttpHeaders.Authorization, "Bearer $token")
                            } else {
                                println("⚠️ [Auth] No token available from TokenProvider")
                            }
                        } else {
                            println("⚠️ [Auth] TokenProvider is null")
                        }
                    } catch (e: Exception) {
                        println("❌ [Auth] Error adding auth header: ${e.message}")
                        e.printStackTrace()
                    }

                    // Device detection headers
                    header("X-Device-Type", deviceType)
                    header("X-Is-Mobile", (!isTablet).toString())
                    header("X-Is-Tablet", isTablet.toString())
                    header("X-Device", getDeviceName())
                    header("X-Device-Model", Build.MODEL)
                    header("X-Device-Manufacturer", Build.MANUFACTURER)
                    header("X-Device-Brand", Build.BRAND)
                    header("X-OS", "Android")
                    header("X-OS-Version", Build.VERSION.RELEASE)
                    header("X-OS-SDK", Build.VERSION.SDK_INT.toString())
                    header("X-App-Type", "native-android")
                    header("X-Client-Type", "mobile-app")
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

    fun reset() {
        synchronized(this) {
            cachedClient?.close()
            cachedClient = null
            cachedUnauthClient?.close()
            cachedUnauthClient = null
            appContext = null
            isInitialized.set(false)
            tokenProvider = null  // FIXED: was tokenManager
        }
    }
}