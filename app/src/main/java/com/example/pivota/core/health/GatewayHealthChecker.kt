package com.example.pivota.core.health

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.InetAddress
import java.net.URL
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GatewayHealthChecker @Inject constructor() {

    companion object {
        // ✅ Primary gateway URL
        private const val GATEWAY_URL = "https://revisionary-leanne-diffusely.ngrok-free.dev"

        // ✅ Cache TTL - don't check too frequently
        private const val CACHE_TTL_MS = 30000L // 30 seconds
    }

    // ✅ Cache gateway status
    private var cachedIsHealthy: Boolean? = null
    private var lastCheckTime: Long = 0L
    private var lastResolvedIp: String? = null

    /**
     * Check if gateway is healthy (DNS resolution only)
     * Network availability is already checked by TokenManager
     */
    suspend fun isGatewayHealthy(): Boolean {
        val now = System.currentTimeMillis()

        // ✅ Return cached value if still fresh
        if (cachedIsHealthy != null && now - lastCheckTime < CACHE_TTL_MS) {
            println("🔍 [GatewayHealth] Using cached status: healthy=$cachedIsHealthy (${(now - lastCheckTime) / 1000}s old)")
            return cachedIsHealthy!!
        }

        // ✅ Perform DNS check
        val isHealthy = withContext(Dispatchers.IO) {
            resolveGatewayDns()
        }

        // ✅ Cache the result
        cachedIsHealthy = isHealthy
        lastCheckTime = now
        println("🔍 [GatewayHealth] Health check: healthy=$isHealthy, ip=${lastResolvedIp}")

        return isHealthy
    }

    /**
     * Resolve gateway DNS
     */
    private fun resolveGatewayDns(): Boolean {
        return try {
            // ✅ Extract host from URL
            val url = URL(GATEWAY_URL)
            val host = url.host
            println("🔍 [GatewayHealth] Resolving DNS for: $host")

            // ✅ Resolve hostname to IP
            val inetAddresses = InetAddress.getAllByName(host)
            if (inetAddresses.isNotEmpty()) {
                lastResolvedIp = inetAddresses[0].hostAddress
                println("✅ [GatewayHealth] DNS resolved: ${inetAddresses[0].hostAddress}")
                true
            } else {
                println("❌ [GatewayHealth] No IP addresses found for: $host")
                false
            }
        } catch (e: UnknownHostException) {
            println("❌ [GatewayHealth] DNS resolution failed: ${e.message}")
            false
        } catch (e: Exception) {
            println("❌ [GatewayHealth] Unexpected error: ${e.message}")
            false
        }
    }

    /**
     * Get the last resolved IP (for debugging)
     */
    fun getLastResolvedIp(): String? = lastResolvedIp

    /**
     * Force refresh the cache
     */
    fun invalidateCache() {
        cachedIsHealthy = null
        lastCheckTime = 0L
        println("🔄 [GatewayHealth] Cache invalidated")
    }
}