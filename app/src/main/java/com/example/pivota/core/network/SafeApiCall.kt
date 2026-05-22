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
        ApiResult.Error(NetworkError.Timeout(originalMessage = e.message), e.message)
    } catch (e: ResponseException) {
        println("❌ HTTP Response Error: ${e.response.status.value} - ${e.response.status.description}")

        val networkError = NetworkExceptionHandler.handleHttpResponse(e.response)

        println("❌ Mapped to error: ${networkError.message}")
        ApiResult.Error(networkError, e.message)
    } catch (e: NoTransformationFoundException) {
        println("❌ Response parsing failed: ${e.message}")
        ApiResult.Error(NetworkError.ParsingError(originalMessage = e.message), e.message)
    } catch (e: Exception) {
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

// ✅ FIXED: Use 'is' for data class type checking
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