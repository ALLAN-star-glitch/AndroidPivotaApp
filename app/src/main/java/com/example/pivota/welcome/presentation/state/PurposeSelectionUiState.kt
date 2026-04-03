

package com.example.pivota.welcome.presentation.state

data class PurposeSelectionUiState(
    val selectedPurpose: String? = null,
    val showBottomSheet: Boolean = false,
    val isLoading: Boolean = false,
    val isConfirmed: Boolean = false,
    val error: String? = null,

    // Form data
    val jobSeekerData: JobSeekerFormData = JobSeekerFormData(),
    val skilledProfessionalData: SkilledProfessionalFormData = SkilledProfessionalFormData(),
    val agentData: AgentFormData = AgentFormData(),
    val housingSeekerData: HousingSeekerFormData = HousingSeekerFormData(),
    val supportBeneficiaryData: SupportBeneficiaryFormData = SupportBeneficiaryFormData(),
    val employerData: EmployerFormData = EmployerFormData(),
    val propertyOwnerData: PropertyOwnerFormData = PropertyOwnerFormData()
)

// UI Form Data Classes (only used in this screen)
data class JobSeekerFormData(
    var headline: String = "",
    var isActivelySeeking: Boolean = true,
    var skills: String = "",
    var industries: String = "",
    var jobTypes: String = "",
    var seniorityLevel: String = "",
    var expectedSalary: String = "",
    var noticePeriod: String = "",
    var workAuthorization: String = ""
)

data class SkilledProfessionalFormData(
    var profession: String = "",
    var otherProfession: String = "",
    var specialties: String = "",
    var yearsExperience: String = "",
    var serviceAreas: String = "",
    var hourlyRate: String = "",
    var licenseNumber: String = ""
)

data class AgentFormData(
    var agentType: String = "",
    var specializations: String = "",
    var serviceAreas: String = "",
    var commissionRate: String = "",
    var licenseNumber: String = ""
)

data class HousingSeekerFormData(
    var propertyType: String = "",
    var minBedrooms: String = "",
    var maxBedrooms: String = "",
    var minBudget: String = "",
    var maxBudget: String = "",
    var preferredAreas: String = "",
    var listingTypes: List<String> = emptyList()
)

data class SupportBeneficiaryFormData(
    var supportTypes: List<String> = emptyList(),
    var urgentNeeds: String = "",
    var location: String = "",
    var familySize: String = ""
)

data class EmployerFormData(
    var businessName: String = "",
    var industrySector: String = "",
    var companySize: String = "",
    var preferredSkills: String = "",
    var otherIndustry: String = ""
)

data class PropertyOwnerFormData(
    var professionalStatus: String = "",
    var propertyCount: String = "",
    var propertyTypes: String = "",
    var serviceAreas: String = ""
)