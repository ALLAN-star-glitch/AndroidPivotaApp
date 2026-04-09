

package com.example.pivota.welcome.domain.model

/**
 * Domain models for Welcome/Onboarding feature
 * These represent business concepts for user onboarding
 */
sealed class UserPurpose {
    object JustExploring : UserPurpose()
    data class FindJob(val preferences: JobSeekerPreferences) : UserPurpose()
    data class OfferSkilledServices(val profile: SkilledProfessionalProfile) : UserPurpose()
    data class WorkAsAgent(val profile: AgentProfile) : UserPurpose()
    data class FindHousing(val preferences: HousingPreferences) : UserPurpose()
    data class GetSocialSupport(val needs: SupportNeeds) : UserPurpose()
    data class HireEmployees(val requirements: EmployerRequirements) : UserPurpose()
    data class ListProperties(val portfolio: PropertyOwnerPortfolio) : UserPurpose()
}

data class JobSeekerPreferences(
    val headline: String,
    val isActivelySeeking: Boolean,
    val skills: List<String>,
    val industries: List<String>,
    val jobTypes: List<String>,
    val seniorityLevel: String?,
    val expectedSalary: Int?
)

data class SkilledProfessionalProfile(
    val profession: String,
    val specialties: List<String>,
    val serviceAreas: List<String>,
    val yearsExperience: Int?,
    val licenseNumber: String?,
    val hourlyRate: Int?
)

data class AgentProfile(
    val agentType: String,
    val specializations: List<String>,
    val serviceAreas: List<String>,
    val commissionRate: Double?,
    val licenseNumber: String?
)

data class HousingPreferences(
    val minBedrooms: Int?,
    val maxBedrooms: Int?,
    val minBudget: Int?,
    val maxBudget: Int?,
    val preferredCities: List<String>,
)

data class SupportNeeds(
    val needs: List<String>,
    val urgentNeeds: List<String>,
    val location: String?,
    val familySize: Int?
)

data class EmployerRequirements(
    val businessName: String,
    val industry: String?,
    val companySize: String?,
    val preferredSkills: String?
)

data class PropertyOwnerPortfolio(
    val isProfessional: Boolean,
    val propertyCount: Int?,
    val propertyTypes: List<String>,
    val serviceAreas: List<String>
)