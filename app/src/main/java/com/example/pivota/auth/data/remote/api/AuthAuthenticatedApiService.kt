
package com.example.pivota.auth.data.remote.api

import com.example.pivota.auth.data.remote.dto.BaseResponseDto
import com.example.pivota.core.di.AuthHttpClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.contentType
import javax.inject.Inject

class AuthAuthenticatedApiService @Inject constructor(
    @param:AuthHttpClient private val client: HttpClient
) {

    suspend fun logout(): BaseResponseDto<Nothing> {
        return try {
            client.post("v1/auth-module/logout") {
                contentType(ContentType.Application.Json)
            }.body()
        } catch (e: Exception) {
            println("❌ Logout Failed: ${e.message}")
            throw e
        }
    }
}