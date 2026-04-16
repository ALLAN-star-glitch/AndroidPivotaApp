// PivotaApp.kt
package com.example.pivota

import android.app.Application
import com.example.pivota.core.data.ThemeManager
import com.example.pivota.core.utils.TabletDetector
import com.example.pivota.core.network.KtorClientFactory
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class PivotaApp : Application() {

    @Inject
    lateinit var themeManager: ThemeManager

    override fun onCreate() {
        super.onCreate()

        println("========================================")
        println("🚀 PivotaApp Initializing...")
        println("========================================")

        // Initialize tablet detection
        TabletDetector.init(this)

        // Get the detected value and log it
        val isTablet = TabletDetector.isTabletDevice(this)
        println("📱 TabletDetector.init result: isTablet = $isTablet")

        // Log current theme preference
        val isDarkTheme = themeManager.getCurrentThemeSync()
        println("🎨 Current theme preference: ${if (isDarkTheme) "Dark" else "Light"}")

        println("========================================")

        // Initialize Ktor client factory with context
        KtorClientFactory.init(this)
    }
}