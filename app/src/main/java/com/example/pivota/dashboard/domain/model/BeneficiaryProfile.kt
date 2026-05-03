package com.example.pivota.dashboard.domain.model

data class BeneficiaryProfile(
    val id: String,
    val needs: List<String>,
    val urgentNeeds: List<String>,
    val familySize: Int?,
    val dependents: Int?,
    val householdComposition: String?,
    val vulnerabilityFactors: List<String>,
    val city: String?,
    val neighborhood: String?,
    val latitude: Double?,
    val longitude: Double?,
    val landmark: String?,
    val prefersAnonymity: Boolean,
    val languagePreferences: List<String>,
    val consentToShare: Boolean,
    val referredBy: String?,
    val referredById: String?,
    val caseWorkerId: String?
) {
    val hasUrgentNeeds: Boolean get() = urgentNeeds.isNotEmpty()
    val displayNeeds: String get() = needs.take(3).joinToString(", ")
    val familyDescription: String get() = when {
        familySize != null && dependents != null -> "$familySize people ($dependents dependents)"
        familySize != null -> "$familySize people"
        dependents != null -> "$dependents dependents"
        else -> "Not specified"
    }
}