
package com.example.pivota.core

import android.content.Context

object ApplicationProvider {
    private var applicationContext: Context? = null

    fun init(context: Context) {
        applicationContext = context.applicationContext
        println("✅ [ApplicationProvider] Initialized with context: ${context.packageName}")
    }

    fun getApplicationContext(): Context {
        return applicationContext ?: throw IllegalStateException("ApplicationProvider not initialized. Call ApplicationProvider.init() in your Application class onCreate()")
    }

    fun isInitialized(): Boolean = applicationContext != null
}