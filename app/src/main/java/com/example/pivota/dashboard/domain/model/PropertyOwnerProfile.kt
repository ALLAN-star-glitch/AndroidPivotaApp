package com.example.pivota.dashboard.domain.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class PropertyOwnerProfile(
    val id: String,
    val listingType: ListingType,
    val isListingForRent: Boolean,
    val isListingForSale: Boolean,
    val isProfessional: Boolean,
    val licenseNumber: String?,
    val companyName: String?,
    val yearsInBusiness: Int?,
    val propertyCount: Int?,
    val propertyTypes: List<PropertyType>,
    val preferredPropertyTypes: List<PropertyType>,
    val serviceAreas: List<String>,
    val propertyPurpose: PropertyPurpose?,
    val usesAgent: Boolean,
    val managingAgentId: String?,
    val isVerified: Boolean
) {
    val displayName: String get() = companyName ?: if (isProfessional) "Professional Owner" else "Individual Owner"
    val listingTypeLabel: String get() = when {
        isListingForRent && isListingForSale -> "Rent & Sell"
        isListingForRent -> "For Rent"
        isListingForSale -> "For Sale"
        else -> "Not specified"
    }
}

@Serializable
enum class ListingType {
    @SerialName("RENT")
    RENT,

    @SerialName("SALE")
    SALE,

    @SerialName("BOTH")
    BOTH;

    companion object {
        fun fromString(value: String): ListingType = when (value.uppercase()) {
            "RENT" -> RENT
            "SALE" -> SALE
            "BOTH" -> BOTH
            else -> BOTH
        }
    }
}

@Serializable
enum class PropertyPurpose {
    @SerialName("PRIMARY")
    PRIMARY,

    @SerialName("INVESTMENT")
    INVESTMENT,

    @SerialName("BOTH")
    BOTH;

    companion object {
        fun fromString(value: String): PropertyPurpose = when (value.uppercase()) {
            "PRIMARY" -> PRIMARY
            "INVESTMENT" -> INVESTMENT
            "BOTH" -> BOTH
            else -> PRIMARY
        }
    }
}