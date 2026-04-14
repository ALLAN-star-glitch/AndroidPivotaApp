package com.example.pivota.core.utils

import android.content.Context
import android.content.res.Configuration
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object TabletDetector {

    private const val TAG = "TabletDetector"
    private val _isTablet = MutableStateFlow<Boolean?>(null)
    val isTablet: StateFlow<Boolean?> = _isTablet

    /**
     * Initialize tablet detection (call once in Application class)
     */
    fun init(context: Context) {
        _isTablet.value = isTabletDevice(context)
    }

    /**
     * Android-recommended way to detect tablets
     * Uses smallest width qualifier (sw600dp = 7" tablets, sw720dp = 10" tablets)
     */
    fun isTabletDevice(context: Context): Boolean {
        val configuration = context.resources.configuration
        val screenLayoutSize = configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK

        // Method 1: Check screen layout size (most reliable)
        val isLargeOrXLarge = screenLayoutSize == Configuration.SCREENLAYOUT_SIZE_LARGE ||
                screenLayoutSize == Configuration.SCREENLAYOUT_SIZE_XLARGE

        // Method 2: Check smallest width in dp (fallback)
        val smallestWidthDp = configuration.smallestScreenWidthDp
        val isWideEnough = smallestWidthDp >= 600

        // Also check for foldables in expanded state
        val isFoldableExpanded = try {
            // For foldables (Android 10+)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                configuration.screenWidthDp >= 600 || configuration.screenHeightDp >= 600
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }

        val isTablet = isLargeOrXLarge || isWideEnough || isFoldableExpanded

        // Debug logging
        Log.d(TAG, "=== Tablet Detection Debug ===")
        Log.d(TAG, "Device Model: ${android.os.Build.MODEL}")
        Log.d(TAG, "Device Manufacturer: ${android.os.Build.MANUFACTURER}")
        Log.d(TAG, "screenLayoutSize: $screenLayoutSize")
        Log.d(TAG, "SCREENLAYOUT_SIZE_LARGE: ${Configuration.SCREENLAYOUT_SIZE_LARGE}")
        Log.d(TAG, "SCREENLAYOUT_SIZE_XLARGE: ${Configuration.SCREENLAYOUT_SIZE_XLARGE}")
        Log.d(TAG, "isLargeOrXLarge: $isLargeOrXLarge")
        Log.d(TAG, "smallestWidthDp: $smallestWidthDp")
        Log.d(TAG, "isWideEnough (>=600): $isWideEnough")
        Log.d(TAG, "screenWidthDp: ${configuration.screenWidthDp}")
        Log.d(TAG, "screenHeightDp: ${configuration.screenHeightDp}")
        Log.d(TAG, "isFoldableExpanded: $isFoldableExpanded")
        Log.d(TAG, "FINAL isTablet: $isTablet")
        Log.d(TAG, "============================")

        return isTablet
    }

    /**
     * Get device type string for headers
     */
    fun getDeviceType(context: Context): String {
        val type = if (isTabletDevice(context)) "TABLET" else "MOBILE"
        Log.d(TAG, "getDeviceType: $type")
        return type
    }

    /**
     * Check if device is a tablet (for header values)
     */
    fun isTabletForHeader(context: Context): Boolean {
        return isTabletDevice(context)
    }
}