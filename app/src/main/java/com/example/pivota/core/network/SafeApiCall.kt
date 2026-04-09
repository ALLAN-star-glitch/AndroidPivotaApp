package com.example.pivota.core.network

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
    } catch (e: Exception) {
        val networkError = NetworkExceptionHandler.handleException(e)
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