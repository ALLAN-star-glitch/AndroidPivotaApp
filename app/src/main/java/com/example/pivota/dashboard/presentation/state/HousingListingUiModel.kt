package com.example.pivota.dashboard.presentation.state

import com.example.pivota.R
import com.example.pivota.dashboard.domain.ListingStatus

data class HousingListingUiModel(
    val id: String,
    val title: String,
    val price: String,
    val location: String,
    val propertyType: String,
    val description: String,
    val isVerified: Boolean,
    val isForSale: Boolean,
    val rating: Double,
    val bedrooms: Int,
    val bathrooms: Int,
    val squareMeters: Int,

    // 🔹 BACKWARD COMPATIBILITY (single image)
    val imageRes: Int? = null,

    // 🔹 LOCAL IMAGES (for previews / offline / dev)
    val imageList: List<Int> = emptyList(),

    // 🔹 REMOTE IMAGES (production - from API)
    val imageUrls: List<String> = emptyList(),

    val status: ListingStatus,
    val views: Int,
    val messages: Int,
    val requests: Int
) {

    /**
     * ✅ Get main image (used in cards / previews)
     * Priority:
     * 1. Remote image (API)
     * 2. Local images (dev/preview)
     * 3. Single fallback image
     * 4. Placeholder
     */
    fun getMainImage(): Any {
        return when {
            imageUrls.isNotEmpty() -> imageUrls.first()
            imageList.isNotEmpty() -> imageList.first()
            imageRes != null -> imageRes
            else -> R.drawable.property_placeholder1
        }
    }

    /**
     * ✅ Get all images (used in gallery)
     * Always returns a safe non-empty list
     */
    fun getAllImages(): List<Any> {
        return when {
            imageUrls.isNotEmpty() -> imageUrls
            imageList.isNotEmpty() -> imageList
            imageRes != null -> listOf(imageRes)
            else -> listOf(R.drawable.property_placeholder1)
        }
    }

    /**
     * ✅ Helper: check if listing has multiple images
     */
    fun hasMultipleImages(): Boolean {
        return when {
            imageUrls.isNotEmpty() -> imageUrls.size > 1
            imageList.isNotEmpty() -> imageList.size > 1
            else -> false
        }
    }

    /**
     * ✅ Helper: total image count (useful for UI badges)
     */
    fun getImageCount(): Int {
        return when {
            imageUrls.isNotEmpty() -> imageUrls.size
            imageList.isNotEmpty() -> imageList.size
            imageRes != null -> 1
            else -> 1
        }
    }
}