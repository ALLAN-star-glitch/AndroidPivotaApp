package com.example.pivota.core.network

import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.plugins.ResponseException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout

sealed class ApiResult<out T> {
    data class Success<out T>(val data: T) : ApiResult<T>()
    data class Error(val networkError: NetworkError, val technicalMessage: String? = null) : ApiResult<Nothing>()
    object Loading : ApiResult<Nothing>()
}

// Simplified: For API calls that return parsed DTOs directly
suspend inline fun <reified T> safeApiCall(
    timeoutMillis: Long = NetworkConstants.TIMEOUT_MILLIS,
    crossinline apiCall: suspend () -> T
): ApiResult<T> {
    return try {
        withTimeout(timeoutMillis) {
            val result = apiCall()
            ApiResult.Success(result)
        }
    } catch (e: TimeoutCancellationException) {
        println("❌ API call timed out: ${e.message}")
        ApiResult.Error(NetworkError.Timeout, e.message)
    } catch (e: ResponseException) {
        // ✅ NOW USING handleHttpResponse for HTTP status codes
        println("❌ HTTP Response Error: ${e.response.status.value} - ${e.response.status.description}")

        val networkError = NetworkExceptionHandler.handleHttpResponse(e.response)
            ?: NetworkExceptionHandler.handleException(e)

        println("❌ Mapped to error: ${networkError.message}")
        ApiResult.Error(networkError, e.message)
    } catch (e: NoTransformationFoundException) {
        // Response parsing failed (JSON doesn't match DTO)
        println("❌ Response parsing failed: ${e.message}")
        ApiResult.Error(NetworkError.ParsingError, e.message)
    } catch (e: Exception) {
        // ✅ NOW USING isNetworkAvailable for debugging
        val networkError = NetworkExceptionHandler.handleException(e)
        val isNetworkAvail = NetworkExceptionHandler.isNetworkAvailable(e)

        println("❌ Network available: $isNetworkAvail")
        println("❌ API call failed: ${networkError.message} - ${e.message}")

        ApiResult.Error(networkError, e.message)
    }
}

// Extension function to get user-friendly error message
fun ApiResult<*>.getUserFriendlyMessage(): String {
    return when (this) {
        is ApiResult.Error -> networkError.userFriendlyMessage
        else -> ""
    }
}

// Extension function to check if error is recoverable
fun ApiResult<*>.isRecoverable(): Boolean {
    return when (this) {
        is ApiResult.Error -> {
            when (networkError) {
                NetworkError.NoInternet -> true
                NetworkError.Timeout -> true
                NetworkError.ServerUnreachable -> true
                else -> false
            }
        }
        else -> false
    }
}