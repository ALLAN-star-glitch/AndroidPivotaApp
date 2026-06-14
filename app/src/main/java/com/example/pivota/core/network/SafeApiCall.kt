package com.example.pivota.core.network

import com.example.pivota.core.ApplicationProvider
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.plugins.ResponseException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import kotlin.time.Duration.Companion.milliseconds
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

sealed class ApiResult<out T> {
    data class Success<out T>(val data: T) : ApiResult<T>()
    data class Error(val networkError: NetworkError, val technicalMessage: String? = null) : ApiResult<Nothing>()
    object Loading : ApiResult<Nothing>()
}

// Helper to safely get NetworkExceptionHandler
fun getNetworkExceptionHandlerSafely(): NetworkExceptionHandler? {
    return try {
        if (ApplicationProvider.isInitialized()) {
            val context = ApplicationProvider.getApplicationContext()
            NetworkExceptionHandler.getInstance(context)
        } else {
            println("⚠️ ApplicationProvider not initialized yet")
            null
        }
    } catch (e: Exception) {
        println("⚠️ Failed to get NetworkExceptionHandler: ${e.message}")
        null
    }
}

suspend inline fun <reified T> safeApiCall(
    timeoutMillis: Long = NetworkConstants.TIMEOUT_MILLIS,
    crossinline apiCall: suspend () -> T
): ApiResult<T> {
    return try {
        withTimeout(timeoutMillis.milliseconds) {
            val result = apiCall()
            ApiResult.Success(result)
        }
    } catch (e: TimeoutCancellationException) {
        println("❌ API call timed out: ${e.message}")
        ApiResult.Error(NetworkError.Timeout(originalMessage = e.message), e.message)
    } catch (e: ResponseException) {
        println("❌ HTTP Response Error: ${e.response.status.value} - ${e.response.status.description}")
        val handler = getNetworkExceptionHandlerSafely()
        val networkError = if (handler != null) {
            handler.handleHttpResponse(e.response)
        } else {
            // Fallback based on status code
            when (e.response.status.value) {
                in 400..499 -> NetworkError.BadRequest(
                    originalMessage = e.message,
                    userFriendlyMessage = "Invalid request. Please try again."
                )
                in 500..599 -> NetworkError.ServerError(
                    originalMessage = e.message,
                    userFriendlyMessage = "Server error. Please try again later."
                )
                else -> NetworkError.Unknown(
                    originalMessage = e.message,
                    userFriendlyMessage = "An unexpected error occurred. Please try again."
                )
            }
        }
        println("❌ Mapped to error: ${networkError.message}")
        ApiResult.Error(networkError, e.message)
    } catch (e: NoTransformationFoundException) {
        println("❌ Response parsing failed: ${e.message}")
        ApiResult.Error(NetworkError.ParsingError(originalMessage = e.message), e.message)
    } catch (e: Exception) {
        val handler = getNetworkExceptionHandlerSafely()

        val networkError = if (handler != null) {
            handler.handleException(e)
        } else {
            // Fallback based on exception type - FIXED to use NoInternet for ConnectException
            when (e) {
                is ConnectException -> {
                    println("❌ Fallback: Assuming no internet connection for ConnectException")
                    NetworkError.NoInternet(
                        originalMessage = e.message,
                        userFriendlyMessage = "No internet connection. Please check your network."
                    )
                }
                is SocketTimeoutException -> {
                    println("❌ Fallback: Connection timeout")
                    NetworkError.Timeout(
                        originalMessage = e.message,
                        userFriendlyMessage = "Connection timed out. Please try again."
                    )
                }
                is UnknownHostException -> {
                    println("❌ Fallback: DNS resolution failed")
                    NetworkError.NoInternet(
                        originalMessage = e.message,
                        userFriendlyMessage = "No internet connection. Please check your network."
                    )
                }
                else -> {
                    println("❌ Fallback: Unknown error")
                    NetworkError.Unknown(
                        originalMessage = e.message,
                        userFriendlyMessage = "An unexpected error occurred. Please try again."
                    )
                }
            }
        }

        val isNetworkAvail = handler?.isNetworkAvailable(e) ?: false

        when (e) {
            is ConnectException -> {
                if (isNetworkAvail) {
                    println("❌ Backend server unreachable - Service may be down: ${e.message}")
                    println("❌ Network available: true (Internet is working, but backend is down)")
                } else {
                    println("❌ No internet connection - Network unavailable: ${e.message}")
                    println("❌ Network available: false")
                }
            }
            is SocketTimeoutException -> {
                println("❌ Connection timeout - Server not responding: ${e.message}")
                println("❌ Network available: true (Internet is working, but server is slow/down)")
            }
            is UnknownHostException -> {
                println("❌ DNS resolution failed - No internet connection: ${e.message}")
                println("❌ Network available: false")
            }
            else -> {
                println("❌ Network available: $isNetworkAvail")
                println("❌ API call failed: ${networkError.message} - ${e.message}")
            }
        }

        ApiResult.Error(networkError, e.message)
    }
}

// Add isInitialized method to ApplicationProvider if not already there
fun ApiResult<*>.getUserFriendlyMessage(): String {
    return when (this) {
        is ApiResult.Error -> {
            networkError.originalMessage?.takeIf { it.isNotBlank() }
                ?: networkError.userFriendlyMessage
        }
        else -> ""
    }
}

fun ApiResult<*>.isRecoverable(): Boolean {
    return when (this) {
        is ApiResult.Error -> {
            when (networkError) {
                is NetworkError.NoInternet -> true
                is NetworkError.Timeout -> true
                is NetworkError.ServerUnreachable -> true
                else -> false
            }
        }
        else -> false
    }
}