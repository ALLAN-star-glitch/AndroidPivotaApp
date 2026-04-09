package com.example.pivota.core.network

import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.TimeoutCancellationException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLHandshakeException

sealed class NetworkError(val message: String, val userFriendlyMessage: String) {
    object NoInternet : NetworkError(
        message = "No internet connection",
        userFriendlyMessage = "No internet connection. Please check your network and try again."
    )

    object ServerUnreachable : NetworkError(
        message = "Server unreachable",
        userFriendlyMessage = "Cannot reach the server. Please check if the server is running and try again."
    )

    object Timeout : NetworkError(
        message = "Request timeout",
        userFriendlyMessage = "Connection timed out. Please check your internet connection and try again."
    )

    object BadRequest : NetworkError(
        message = "Bad request",
        userFriendlyMessage = "Invalid request. Please check your input and try again."
    )

    object Unauthorized : NetworkError(
        message = "Unauthorized",
        userFriendlyMessage = "Session expired. Please login again."
    )

    object NotFound : NetworkError(
        message = "Not found",
        userFriendlyMessage = "Service unavailable. Please try again later."
    )

    object ServerError : NetworkError(
        message = "Server error",
        userFriendlyMessage = "Server error occurred. Please try again later."
    )

    object ParsingError : NetworkError(
        message = "Response parsing error",
        userFriendlyMessage = "Received invalid response from server. Please try again."
    )

    object Unknown : NetworkError(
        message = "Unknown error",
        userFriendlyMessage = "An unexpected error occurred. Please try again."
    )
}

class NetworkExceptionHandler {
    companion object {
        fun handleException(throwable: Throwable): NetworkError {
            // 🔍 Log the FULL exception for debugging
            println("❌ ========== NETWORK EXCEPTION ==========")
            println("❌ Exception Type: ${throwable::class.simpleName}")
            println("❌ Exception Message: ${throwable.message}")
            println("❌ Exception Cause: ${throwable.cause}")
            throwable.printStackTrace()
            println("❌ =======================================")

            return when (throwable) {
                is UnknownHostException -> {
                    println("❌ Network error: Unknown host - ${throwable.message}")
                    NetworkError.ServerUnreachable
                }
                is ConnectException -> {
                    println("❌ Network error: Connection refused - ${throwable.message}")
                    NetworkError.ServerUnreachable
                }
                is SocketTimeoutException -> {
                    println("❌ Network error: Socket timeout - ${throwable.message}")
                    NetworkError.Timeout
                }
                is TimeoutCancellationException -> {
                    println("❌ Network error: Request timeout - ${throwable.message}")
                    NetworkError.Timeout
                }
                is SSLHandshakeException -> {
                    println("❌ Network error: SSL handshake failed - ${throwable.message}")
                    NetworkError.ServerUnreachable
                }
                is IOException -> {
                    when {
                        throwable.message?.contains("unreachable") == true -> {
                            println("❌ Network error: Server unreachable - ${throwable.message}")
                            NetworkError.ServerUnreachable
                        }
                        throwable.message?.contains("timeout") == true -> {
                            println("❌ Network error: Timeout - ${throwable.message}")
                            NetworkError.Timeout
                        }
                        else -> {
                            println("❌ Network error: IO exception - ${throwable.message}")
                            NetworkError.NoInternet
                        }
                    }
                }
                is NoTransformationFoundException -> {
                    println("❌ Network error: Response parsing failed - ${throwable.message}")
                    println("❌ This usually means the response format doesn't match the expected DTO")
                    NetworkError.ParsingError
                }
                else -> {
                    println("❌ Network error: Unknown exception type - ${throwable::class.simpleName}")
                    println("❌ Message: ${throwable.message}")
                    NetworkError.Unknown
                }
            }
        }

        fun handleHttpResponse(response: HttpResponse): NetworkError? {
            println("🔍 HTTP Response Status: ${response.status.value} - ${response.status.description}")

            return when (response.status.value) {
                in 400..499 -> {
                    when (response.status) {
                        HttpStatusCode.BadRequest -> NetworkError.BadRequest
                        HttpStatusCode.Unauthorized -> NetworkError.Unauthorized
                        HttpStatusCode.NotFound -> NetworkError.NotFound
                        else -> NetworkError.BadRequest
                    }
                }
                in 500..599 -> {
                    println("❌ Server error: ${response.status.value}")
                    NetworkError.ServerError
                }
                else -> null
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