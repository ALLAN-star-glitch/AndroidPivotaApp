package com.example.pivota.dashboard.domain.model.profile_models

import kotlinx.serialization.Serializable

@Serializable
data class ProfessionalProfile(
    val id: String,
    val title: String?,
    val profession: String,
    val specialties: List<String>,
    val serviceAreas: List<String>,
    val yearsExperience: Int?,
    val licenseNumber: String?,
    val licenseBody: String?,
    val insuranceInfo: String?,
    val hourlyRate: Double?,
    val dailyRate: Double?,
    val paymentTerms: String?,
    val availableToday: Boolean,
    val availableWeekends: Boolean,
    val emergencyService: Boolean,
    val portfolioImages: List<String>,
    val certifications: List<String>,
    val isVerified: Boolean,
    val averageRating: Float,
    val totalReviews: Int,
    val completedJobs: Int
) {
    val displayTitle: String get() = title ?: profession
    val hasLicense: Boolean get() = !licenseNumber.isNullOrBlank()
    val hasInsurance: Boolean get() = !insuranceInfo.isNullOrBlank()
    val formattedHourlyRate: String get() = hourlyRate?.let { "KES ${String.format("%,.0f", it)}" } ?: "Not specified"
    val ratingDescription: String get() = when {
        averageRating >= 4.5 -> "Excellent"
        averageRating >= 4.0 -> "Very Good"
        averageRating >= 3.0 -> "Good"
        averageRating > 0 -> "Average"
        else -> "No ratings yet"
    }
    val starRating: Float get() = averageRating
}