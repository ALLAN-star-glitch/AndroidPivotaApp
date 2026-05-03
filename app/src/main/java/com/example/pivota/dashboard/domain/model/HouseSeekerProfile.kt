package com.example.pivota.dashboard.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HousingSeekerProfile(
    val id: String,
    val searchType: SearchType,
    val isLookingForRental: Boolean,
    val isLookingToBuy: Boolean,
    val minBedrooms: Int?,
    val maxBedrooms: Int?,
    val minBudget: Double?,
    val maxBudget: Double?,
    val preferredTypes: List<PropertyType>,
    val preferredCities: List<String>,
    val preferredNeighborhoods: List<String>,
    val moveInDate: String?,
    val leaseDuration: String?,
    val householdSize: Int?,
    val hasPets: Boolean,
    val petDetails: String?,
    val latitude: Double?,
    val longitude: Double?,
    val searchRadiusKm: Int?,
    val hasAgent: Boolean,
    val agentId: String?
) {
    val bedroomRange: String get() = when {
        minBedrooms != null && maxBedrooms != null -> "$minBedrooms - $maxBedrooms bedrooms"
        minBedrooms != null -> "$minBedrooms+ bedrooms"
        maxBedrooms != null -> "Up to $maxBedrooms bedrooms"
        else -> "Any bedrooms"
    }

    val budgetRange: String get() = when {
        minBudget != null && maxBudget != null -> "KES ${formatMoney(minBudget)} - ${formatMoney(maxBudget)}"
        minBudget != null -> "KES ${formatMoney(minBudget)}+"
        maxBudget != null -> "Up to KES ${formatMoney(maxBudget)}"
        else -> "Budget not specified"
    }

    private fun formatMoney(amount: Double): String = String.format("%,.0f", amount)
}

@Serializable
enum class SearchType {
    @SerialName("RENT")
    RENT,

    @SerialName("BUY")
    BUY,

    @SerialName("BOTH")
    BOTH;

    companion object {
        fun fromString(value: String): SearchType = when (value.uppercase()) {
            "RENT", "RENTAL" -> RENT
            "BUY", "SALE" -> BUY
            "BOTH" -> BOTH
            else -> BOTH
        }
    }
}

@Serializable
enum class PropertyType {
    @SerialName("APARTMENT")
    APARTMENT,

    @SerialName("HOUSE")
    HOUSE,

    @SerialName("BEDSITTER")
    BEDSITTER,

    @SerialName("STUDIO")
    STUDIO,

    @SerialName("TOWNHOUSE")
    TOWNHOUSE,

    @SerialName("COMMERCIAL")
    COMMERCIAL,

    @SerialName("LAND")
    LAND;

    companion object {
        fun fromString(value: String): PropertyType = when (value.uppercase()) {
            "APARTMENT" -> APARTMENT
            "HOUSE" -> HOUSE
            "BEDSITTER" -> BEDSITTER
            "STUDIO" -> STUDIO
            "TOWNHOUSE" -> TOWNHOUSE
            "COMMERCIAL" -> COMMERCIAL
            "LAND" -> LAND
            else -> APARTMENT
        }
    }
}