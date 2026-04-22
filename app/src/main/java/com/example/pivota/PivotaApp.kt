package com.example.pivota

import android.app.Application
import com.example.pivota.core.auth.TokenManager
import com.example.pivota.core.data.ThemeManager
import com.example.pivota.core.utils.TabletDetector
import com.example.pivota.core.network.KtorClientFactory
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltAndroidApp
class PivotaApp : Application() {

    @Inject
    lateinit var themeManager: ThemeManager

    @Inject
    lateinit var tokenManager: TokenManager

    // Flag to indicate if token is valid (public for access in NavHost)
    var isTokenValid = false
        private set

    override fun attachBaseContext(base: android.content.Context) {
        KtorClientFactory.init(base)
        super.attachBaseContext(base)
        println("✅ [PivotaApp] KtorClientFactory initialized in attachBaseContext")
    }

    override fun onCreate() {
        KtorClientFactory.init(this)
        super.onCreate()

        println("========================================")
        println("🚀 PivotaApp Initializing...")
        println("========================================")

        TabletDetector.init(this)
        val isTablet = TabletDetector.isTabletDevice(this)
        println("📱 TabletDetector.init result: isTablet = $isTablet")

        val isDarkTheme = themeManager.getCurrentThemeSync()
        println("🎨 Current theme preference: ${if (isDarkTheme) "Dark" else "Light"}")

        println("========================================")

        // Check and refresh token synchronously before app continues
        checkAndRefreshTokenOnLaunch()
    }

    private fun checkAndRefreshTokenOnLaunch() {
        runBlocking(Dispatchers.IO) {
            try {
                val hasValidSession = tokenManager.hasValidSession()
                println("🔄 [App] Checking token validity on launch: hasValidSession = $hasValidSession")

                if (hasValidSession) {
                    // Check if token needs refresh
                    val tokenAge = tokenManager.getTokenAge()
                    val shouldRefresh = tokenAge > 12 * 60 * 1000L

                    if (shouldRefresh) {
                        println("🔄 [App] Token is ${tokenAge / 1000}s old, refreshing on launch...")
                        val refreshSuccess = tokenManager.refreshToken()

                        if (refreshSuccess) {
                            println("✅ [App] Token refreshed successfully on launch")
                            isTokenValid = true
                        } else {
                            println("❌ [App] Token refresh failed on launch")
                            isTokenValid = false
                        }
                    } else {
                        println("✅ [App] Token is valid (${tokenAge / 1000}s old)")
                        isTokenValid = true
                    }

                    // Start auto-refresh if token is valid
                    if (isTokenValid) {
                        tokenManager.startAutoRefresh()
                    }
                } else {
                    println("ℹ️ [App] No valid session found")
                    isTokenValid = false
                }
            } catch (e: Exception) {
                println("❌ [App] Failed to check token: ${e.message}")
                isTokenValid = false
            }
        }
    }
}