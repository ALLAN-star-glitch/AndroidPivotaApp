package com.example.pivota.core.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object KtorClientFactory {
    fun build(): HttpClient {
        return HttpClient(Android) {

            install(HttpTimeout) {
                requestTimeoutMillis = NetworkConstants.TIMEOUT_MILLIS
                connectTimeoutMillis = NetworkConstants.TIMEOUT_MILLIS
                socketTimeoutMillis = NetworkConstants.TIMEOUT_MILLIS
            }

            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                    isLenient = true
                })
            }

            install(Logging) {
                level = LogLevel.BODY
            }

            defaultRequest {
                // Tip: Move this URL to a BuildConfig or Constant file later
                url(NetworkConstants.BASE_URL)
                header(HttpHeaders.ContentType, ContentType.Application.Json)
            }
        }
    }
}