
package com.example.pivota.dashboard.data.remote

import com.example.pivota.core.di.AuthHttpClient
import com.example.pivota.core.network.NetworkConstants
import com.example.pivota.dashboard.data.dto.CategoriesResponseDto
import com.example.pivota.dashboard.data.dto.DiscoveryMetadataResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import javax.inject.Inject

class CategoriesApiService @Inject constructor(
    @param:AuthHttpClient private val client: HttpClient
) {

    /**
     * Get discovery metadata (lightweight categories)
     * @param vertical Optional: HOUSING, JOBS, SOCIAL_SUPPORT
     * @param type Optional: MAIN, COMPLIMENTARY
     */
    suspend fun getDiscoveryMetadata(
        vertical: String? = null,
        type: String? = null
    ): DiscoveryMetadataResponseDto {
        println("🔍 ========== FETCH DISCOVERY METADATA REQUEST ==========")
        println("🔍 URL: ${NetworkConstants.BASE_URL}/categories-module/categories/discovery")
        println("🔍 PARAMS: vertical=$vertical, type=$type")
        println("🔍 ======================================================")

        return try {
            val response: DiscoveryMetadataResponseDto = client.get("categories-module/categories/discovery") {
                contentType(ContentType.Application.Json)

                // Add query parameters
                vertical?.let { parameter("vertical", it) }
                type?.let { parameter("type", it) }
            }.body()

            println("🔍 ========== FETCH DISCOVERY METADATA RESPONSE ==========")
            println("🔍 SUCCESS: ${response.success}")
            println("🔍 DATA COUNT: ${response.data?.size ?: 0}")
            response.data?.take(5)?.forEach { category ->
                println("🔍 CATEGORY: ${category.name} (${category.vertical}) - ${category.type}")
            }
            println("🔍 ======================================================")

            response
        } catch (e: ClientRequestException) {
            println("❌ Discovery Metadata Client Error (${e.response.status.value}): ${e.message}")
            val errorBody = try { e.response.bodyAsText() } catch (ex: Exception) { "Unable to read error body" }
            println("❌ Error Body: $errorBody")
            throw e
        } catch (e: ServerResponseException) {
            println("❌ Discovery Metadata Server Error (${e.response.status.value}): ${e.message}")
            throw e
        } catch (e: Exception) {
            println("❌ Discovery Metadata Failed: ${e.message}")
            throw e
        }
    }

    /**
     * Get full categories with filtering
     * @param vertical Optional: HOUSING, JOBS, SOCIAL_SUPPORT
     * @param type Optional: MAIN, COMPLIMENTARY
     * @param parentId Optional: Filter by parent category (use "null" for top-level)
     * @param hasSubcategories Optional: Filter categories that have subcategories
     * @param hasParent Optional: Filter categories that have a parent
     * @param search Optional: Search by name
     * @param includeNested Optional: Include nested subcategories
     */
    suspend fun getCategories(
        vertical: String? = null,
        type: String? = null,
        parentId: String? = null,
        hasSubcategories: Boolean? = null,
        hasParent: Boolean? = null,
        search: String? = null,
        includeNested: Boolean? = null
    ): CategoriesResponseDto {
        println("🔍 ========== FETCH CATEGORIES REQUEST ==========")
        println("🔍 URL: ${NetworkConstants.BASE_URL}/categories-module/categories")
        println("🔍 PARAMS: vertical=$vertical, type=$type, parentId=$parentId")
        println("🔍 ===============================================")

        return try {
            val response: CategoriesResponseDto = client.get("categories-module/categories") {
                contentType(ContentType.Application.Json)

                // Add query parameters
                vertical?.let { parameter("vertical", it) }
                type?.let { parameter("type", it) }
                parentId?.let { parameter("parentId", it) }
                hasSubcategories?.let { parameter("hasSubcategories", it.toString()) }
                hasParent?.let { parameter("hasParent", it.toString()) }
                search?.let { parameter("search", it) }
                includeNested?.let { parameter("includeNested", it.toString()) }
            }.body()

            println("🔍 ========== FETCH CATEGORIES RESPONSE ==========")
            println("🔍 SUCCESS: ${response.success}")
            println("🔍 DATA COUNT: ${response.data?.size ?: 0}")
            println("🔍 ===============================================")

            response
        } catch (e: ClientRequestException) {
            println("❌ Categories Client Error (${e.response.status.value}): ${e.message}")
            val errorBody = try { e.response.bodyAsText() } catch (ex: Exception) { "Unable to read error body" }
            println("❌ Error Body: $errorBody")
            throw e
        } catch (e: ServerResponseException) {
            println("❌ Categories Server Error (${e.response.status.value}): ${e.message}")
            throw e
        } catch (e: Exception) {
            println("❌ Categories Failed: ${e.message}")
            throw e
        }
    }
}