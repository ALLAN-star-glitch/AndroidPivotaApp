package com.example.pivota

import android.app.Application
import com.example.pivota.core.utils.TabletDetector
import com.example.pivota.core.network.KtorClientFactory
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PivotaApp : Application() {

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
        println("========================================")

        // Initialize Ktor client factory with context
        KtorClientFactory.init(this)
    }
}