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
    // Basic property preferences
    var propertyType: String = "",
    var minBedrooms: String = "",
    var maxBedrooms: String = "",
    var minBudget: String = "",
    var maxBudget: String = "",
    var preferredAreas: String = "",

    // ======================================================
    // NEW SEARCH TYPE FIELDS
    // Values: "RENTAL", "SALE", "BOTH"
    // ======================================================
    var searchType: String = "",  // "RENTAL", "SALE", or "BOTH"
    var isLookingForRental: Boolean = false,
    var isLookingToBuy: Boolean = false,

    var propertyTypes: List<String> = emptyList(),

    // ======================================================
    // NEW RENTAL PREFERENCES
    // ======================================================
    var minimumLeaseTerm: String = "",      // Minimum lease term in months
    var maximumLeaseTerm: String = "",      // Maximum lease term in months
    var depositAmount: String = "",          // Security deposit amount
    var isPetFriendly: Boolean = false,      // Whether pets are allowed
    var utilitiesIncluded: Boolean = false,  // Whether utilities are included
    var utilitiesDetails: String = "",       // Details about utilities

    // ======================================================
    // NEW SALE PREFERENCES
    // ======================================================
    var isNegotiable: Boolean = true,        // Whether price is negotiable
    var titleDeedAvailable: Boolean = false, // Whether title deed is available

    // Legacy field (kept for backward compatibility)
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
    var serviceAreas: String = "",

    // ======================================================
    // NEW LISTING TYPE FIELDS
    // Values: "RENT", "SALE", "BOTH"
    // ======================================================
    var listingType: String = "",           // "RENT", "SALE", or "BOTH"
    var isListingForRent: Boolean = false,   // Whether listing for rent
    var isListingForSale: Boolean = false    // Whether listing for sale
)