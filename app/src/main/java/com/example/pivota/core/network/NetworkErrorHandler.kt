package com.example.pivota.core.network

import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLHandshakeException



// ✅ Convert from object to sealed class with data classes
sealed class NetworkError(
    open val message: String,
    open val userFriendlyMessage: String,
    open val originalMessage: String? = null,  // Backend's actual message
    open val statusCode: Int? = null            // HTTP status code
) {
    data class NoInternet(
        override val message: String = "No internet connection",
        override val userFriendlyMessage: String = "No internet connection. Please check your network and try again.",
        override val originalMessage: String? = null,
        override val statusCode: Int? = null
    ) : NetworkError(message, userFriendlyMessage, originalMessage, statusCode)

    data class ServerUnreachable(
        override val message: String = "Server unreachable",
        override val userFriendlyMessage: String = "Cannot reach the server. Please check your connection.",
        override val originalMessage: String? = null,
        override val statusCode: Int? = null
    ) : NetworkError(message, userFriendlyMessage, originalMessage, statusCode)

    data class Timeout(
        override val message: String = "Request timeout",
        override val userFriendlyMessage: String = "Connection timed out. Please try again.",
        override val originalMessage: String? = null,
        override val statusCode: Int? = null
    ) : NetworkError(message, userFriendlyMessage, originalMessage, statusCode)

    data class BadRequest(
        override val message: String = "Bad request",
        override val userFriendlyMessage: String = "Invalid request. Please check your input.",
        override val originalMessage: String? = null,
        override val statusCode: Int? = 400
    ) : NetworkError(message, userFriendlyMessage, originalMessage, statusCode)

    data class Unauthorized(
        override val message: String = "Unauthorized",
        override val userFriendlyMessage: String = "Invalid email or password.", // Changed from session expired
        override val originalMessage: String? = null,
        override val statusCode: Int? = 401
    ) : NetworkError(message, userFriendlyMessage, originalMessage, statusCode)

    data class NotFound(
        override val message: String = "Not found",
        override val userFriendlyMessage: String = "Resource not found.",
        override val originalMessage: String? = null,
        override val statusCode: Int? = 404
    ) : NetworkError(message, userFriendlyMessage, originalMessage, statusCode)

    data class ServerError(
        override val message: String = "Server error",
        override val userFriendlyMessage: String = "Server error occurred. Please try again later.",
        override val originalMessage: String? = null,
        override val statusCode: Int? = 500
    ) : NetworkError(message, userFriendlyMessage, originalMessage, statusCode)

    data class ParsingError(
        override val message: String = "Response parsing error",
        override val userFriendlyMessage: String = "Received invalid response from server.",
        override val originalMessage: String? = null,
        override val statusCode: Int? = null
    ) : NetworkError(message, userFriendlyMessage, originalMessage, statusCode)

    data class Unknown(
        override val message: String = "Unknown error",
        override val userFriendlyMessage: String = "An unexpected error occurred. Please try again.",
        override val originalMessage: String? = null,
        override val statusCode: Int? = null
    ) : NetworkError(message, userFriendlyMessage, originalMessage, statusCode)
}

class NetworkExceptionHandler {
    companion object {

        // ✅ New: Extract error message from response body
        private suspend fun extractErrorMessage(response: HttpResponse): String? {
            return try {
                val body = response.body<String>()
                println("🔍 Raw error response body: $body")

                // Try to parse as JSON
                val jsonElement = Json.parseToJsonElement(body)
                val jsonObject = jsonElement.jsonObject

                // Try common error message fields (order matters)
                val message = jsonObject["message"]?.jsonPrimitive?.content
                    ?: jsonObject["error"]?.jsonPrimitive?.content
                    ?: jsonObject["detail"]?.jsonPrimitive?.content
                    ?: jsonObject["description"]?.jsonPrimitive?.content
                    ?: jsonObject["title"]?.jsonPrimitive?.content

                if (message != null) {
                    println("✅ Extracted error message: $message")
                    return message
                }

                // If no message field, return the first 200 chars of raw body
                body.take(200).takeIf { it.isNotEmpty() }
            } catch (e: Exception) {
                println("⚠️ Failed to parse error body: ${e.message}")
                null
            }
        }

        fun handleException(throwable: Throwable): NetworkError {
            println("❌ ========== NETWORK EXCEPTION ==========")
            println("❌ Exception Type: ${throwable::class.simpleName}")
            println("❌ Exception Message: ${throwable.message}")
            throwable.printStackTrace()
            println("❌ =======================================")

            return when (throwable) {
                is UnknownHostException -> NetworkError.ServerUnreachable(
                    originalMessage = throwable.message
                )
                is ConnectException -> NetworkError.ServerUnreachable(
                    originalMessage = throwable.message
                )
                is SocketTimeoutException -> NetworkError.Timeout(
                    originalMessage = throwable.message
                )
                is TimeoutCancellationException -> NetworkError.Timeout(
                    originalMessage = throwable.message
                )
                is SSLHandshakeException -> NetworkError.ServerUnreachable(
                    originalMessage = throwable.message
                )
                is IOException -> {
                    when {
                        throwable.message?.contains("unreachable") == true -> NetworkError.ServerUnreachable(
                            originalMessage = throwable.message
                        )
                        throwable.message?.contains("timeout") == true -> NetworkError.Timeout(
                            originalMessage = throwable.message
                        )
                        else -> NetworkError.NoInternet(
                            originalMessage = throwable.message
                        )
                    }
                }
                is NoTransformationFoundException -> NetworkError.ParsingError(
                    originalMessage = throwable.message
                )
                else -> NetworkError.Unknown(
                    originalMessage = throwable.message
                )
            }
        }

        // ✅ Updated to include original backend message
        suspend fun handleHttpResponse(response: HttpResponse): NetworkError {
            val statusCode = response.status.value
            val originalMessage = extractErrorMessage(response)

            println("🔍 HTTP Response: $statusCode - ${response.status.description}")
            println("🔍 Original message: $originalMessage")

            // ✅ Use statusCode (Int) for ALL cases
            return when (statusCode) {
                400 -> NetworkError.BadRequest(
                    originalMessage = originalMessage,
                    statusCode = statusCode
                )
                401 -> NetworkError.Unauthorized(
                    originalMessage = originalMessage,
                    userFriendlyMessage = originalMessage ?: "Invalid email or password.",
                    statusCode = statusCode
                )
                403 -> NetworkError.Unauthorized(
                    originalMessage = originalMessage,
                    userFriendlyMessage = originalMessage ?: "Access denied. You don't have permission to perform this action.",
                    statusCode = statusCode
                )
                404 -> NetworkError.NotFound(
                    originalMessage = originalMessage,
                    userFriendlyMessage = originalMessage ?: "Resource not found.",
                    statusCode = statusCode
                )
                409 -> NetworkError.BadRequest(
                    originalMessage = originalMessage,
                    userFriendlyMessage = originalMessage ?: "Resource already exists.",
                    statusCode = statusCode
                )
                in 400..499 -> NetworkError.BadRequest(
                    originalMessage = originalMessage,
                    userFriendlyMessage = originalMessage ?: "Invalid request.",
                    statusCode = statusCode
                )
                in 500..599 -> NetworkError.ServerError(
                    originalMessage = originalMessage,
                    userFriendlyMessage = originalMessage ?: "Server error. Please try again.",
                    statusCode = statusCode
                )
                else -> NetworkError.Unknown(
                    originalMessage = originalMessage,
                    userFriendlyMessage = originalMessage ?: "Unexpected response.",
                    statusCode = statusCode
                )
            }
        }

        fun isNetworkAvailable(throwable: Throwable): Boolean {
            return when (throwable) {
                is UnknownHostException -> false
                is ConnectException -> false
                is SocketTimeoutException -> false
                is TimeoutCancellationException -> false
                is IOException -> true
                else -> true
            }
        }
    }
}
