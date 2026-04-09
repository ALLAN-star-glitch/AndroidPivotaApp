package com.example.pivota.core.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import java.io.IOException
import java.net.UnknownHostException
import javax.net.ssl.SSLHandshakeException

object KtorClientFactory {
    fun build(): HttpClient {
        return HttpClient(Android) {
            // Configure timeouts to prevent hanging requests
            install(HttpTimeout) {
                requestTimeoutMillis = NetworkConstants.TIMEOUT_MILLIS
                connectTimeoutMillis = NetworkConstants.CONNECT_TIMEOUT_MILLIS
                socketTimeoutMillis = NetworkConstants.SOCKET_TIMEOUT_MILLIS
            }

            // Set up JSON serialization with lenient parsing
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                    isLenient = true
                    encodeDefaults = true
                })
            }

            // Enable request/response logging for debugging
            install(Logging) {
                level = LogLevel.BODY
                logger = object : Logger {
                    override fun log(message: String) {
                        // Filter and print only relevant logs
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

            // Apply default configuration to all requests
            defaultRequest {
                url(NetworkConstants.BASE_URL)
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                header(HttpHeaders.Accept, ContentType.Application.Json)
                // Add ngrok bypass header
                header("ngrok-skip-browser-warning", "true")
            }
        }
    }
}