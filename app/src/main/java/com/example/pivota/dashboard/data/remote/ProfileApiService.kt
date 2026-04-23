
package com.example.pivota.dashboard.data.remote

import com.example.pivota.auth.data.remote.dto.BaseResponseDto
import com.example.pivota.auth.data.remote.dto.ProfileResponseDto
import com.example.pivota.core.di.AuthHttpClient
import com.example.pivota.core.network.NetworkConstants
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import javax.inject.Inject

class ProfileApiService @Inject constructor(
    @param:AuthHttpClient  private val client: HttpClient  // WITH auth header
) {

    suspend fun fetchProfile(): BaseResponseDto<ProfileResponseDto> {
        println("🔍 ========== FETCH PROFILE REQUEST ==========")
        println("🔍 URL: ${NetworkConstants.BASE_URL}/v1/users-profile-module/me")
        println("🔍 ============================================")

        return try {
            val response: BaseResponseDto<ProfileResponseDto> = client.get("v1/users-profile-module/me") {
                contentType(ContentType.Application.Json)
            }.body()

            println("🔍 ========== FETCH PROFILE RESPONSE ==========")
            println("🔍 SUCCESS: ${response.success}")
            println("🔍 HAS DATA: ${response.data != null}")
            response.data?.let { profileData ->
                println("🔍 ACCOUNT TYPE: ${profileData.account.type}")
            }
            println("🔍 ============================================")

            response
        } catch (e: ClientRequestException) {
            println("❌ Fetch Profile Client Error (${e.response.status.value}): ${e.message}")
            val errorBody = try { e.response.bodyAsText() } catch (ex: Exception) { "Unable to read error body" }
            println("❌ Error Body: $errorBody")
            throw e
        } catch (e: ServerResponseException) {
            println("❌ Fetch Profile Server Error (${e.response.status.value}): ${e.message}")
            throw e
        } catch (e: Exception) {
            println("❌ Fetch Profile Failed: ${e.message}")
            throw e
        }
    }
}