package com.example.pivota

import android.app.Application
import com.example.pivota.core.ApplicationProvider
import com.example.pivota.core.data.ThemeManager
import com.example.pivota.core.utils.TabletDetector
import com.example.pivota.core.network.KtorClientFactory
import com.example.pivota.core.network.NetworkExceptionHandler
import com.example.pivota.dashboard.data.sync.CategoriesSyncManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class PivotaApp : Application() {

    @Inject
    lateinit var themeManager: ThemeManager

    override fun attachBaseContext(base: android.content.Context) {
        // Initialize ApplicationProvider FIRST before anything else
        ApplicationProvider.init(base)

        KtorClientFactory.init(base)
        super.attachBaseContext(base)

        // Initialize NetworkExceptionHandler early
        NetworkExceptionHandler.getInstance(this)

        println("✅ [PivotaApp] ApplicationProvider initialized in attachBaseContext")
    }

    @Inject lateinit var categoriesSyncManager: CategoriesSyncManager

    override fun onCreate() {
        super.onCreate()

        // Verify ApplicationProvider is initialized
        if (!ApplicationProvider.isInitialized()) {
            ApplicationProvider.init(this)
        }

        categoriesSyncManager.startAutoSync()

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