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
    }
}