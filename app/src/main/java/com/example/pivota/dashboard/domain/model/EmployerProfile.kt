package com.example.pivota.dashboard.domain.model

data class EmployerProfile(
    val id: String,
    val companyName: String?,
    val industry: String?,
    val companySize: String?,
    val foundedYear: Int?,
    val description: String?,
    val logo: String?,
    val preferredSkills: List<String>,
    val remotePolicy: String?,
    val worksWithAgents: Boolean,
    val preferredAgents: List<String>,
    val businessName: String?,
    val isRegistered: Boolean,
    val yearsExperience: Int?,
    val isVerified: Boolean
) {
    val displayName: String get() = companyName ?: businessName ?: "Employer"
    val hasDescription: Boolean get() = !description.isNullOrBlank()
    val formattedFoundedYear: String get() = foundedYear?.let { "Est. $it" } ?: "Year not specified"
}