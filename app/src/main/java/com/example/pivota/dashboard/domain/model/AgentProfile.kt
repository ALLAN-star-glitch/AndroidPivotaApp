package com.example.pivota.dashboard.domain.model

data class AgentProfile(
    val id: String,
    val agentType: AgentType,
    val specializations: List<String>,
    val serviceAreas: List<String>,
    val licenseNumber: String?,
    val licenseBody: String?,
    val yearsExperience: Int?,
    val agencyName: String?,
    val agencyId: String?,
    val commissionRate: Double?,
    val feeStructure: String?,
    val minimumFee: Double?,
    val typicalFee: String?,
    val about: String?,
    val profileImage: String?,
    val contactEmail: String?,
    val contactPhone: String?,
    val website: String?,
    val socialLinks: Map<String, String>,
    val clientTypes: List<ClientType>,
    val isVerified: Boolean,
    val averageRating: Float,
    val totalReviews: Int,
    val completedDeals: Int
) {
    val displayName: String get() = agencyName ?: "${agentType.displayName} Agent"
    val hasLicense: Boolean get() = !licenseNumber.isNullOrBlank()
    val formattedCommission: String get() = commissionRate?.let { "$it%" } ?: "Contact for pricing"
    val starRating: Float get() = averageRating
}

enum class AgentType {
    HOUSING_AGENT, RECRUITMENT_AGENT, BUSINESS_BROKER, INSURANCE_AGENT, TRAVEL_AGENT;

    val displayName: String get() = when (this) {
        HOUSING_AGENT -> "Housing"
        RECRUITMENT_AGENT -> "Recruitment"
        BUSINESS_BROKER -> "Business"
        INSURANCE_AGENT -> "Insurance"
        TRAVEL_AGENT -> "Travel"
    }

    companion object {
        fun fromString(value: String): AgentType = when (value.uppercase()) {
            "HOUSING_AGENT" -> HOUSING_AGENT
            "RECRUITMENT_AGENT" -> RECRUITMENT_AGENT
            "BUSINESS_BROKER" -> BUSINESS_BROKER
            "INSURANCE_AGENT" -> INSURANCE_AGENT
            "TRAVEL_AGENT" -> TRAVEL_AGENT
            else -> HOUSING_AGENT
        }
    }
}

enum class ClientType {
    LANDLORDS, TENANTS, EMPLOYERS, JOB_SEEKERS, BUYERS, SELLERS;

    companion object {
        fun fromString(value: String): ClientType = when (value.uppercase()) {
            "LANDLORDS" -> LANDLORDS
            "TENANTS" -> TENANTS
            "EMPLOYERS" -> EMPLOYERS
            "JOB_SEEKERS" -> JOB_SEEKERS
            "BUYERS" -> BUYERS
            "SELLERS" -> SELLERS
            else -> LANDLORDS
        }
    }
}