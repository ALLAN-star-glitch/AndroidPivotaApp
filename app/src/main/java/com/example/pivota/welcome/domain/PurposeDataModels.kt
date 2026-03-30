package com.example.pivota.welcome.domain

data class JobSeekerData(
    var skills: String = "",
    var experienceLevel: String = "",
    var expectedSalary: String = "",
    var cvUrl: String? = null
)

data class SkilledProfessionalData(
    var profession: String = "",
    var otherProfession: String = "",
    var specialties: String = "",
    var yearsExperience: String = "",
    var serviceAreas: String = "",
    var hourlyRate: String = "",
    var licenseNumber: String = ""
)

data class AgentData(
    var agentType: String = "",
    var specializations: String = "",
    var serviceAreas: String = "",
    var commissionRate: String = "",
    var licenseNumber: String = ""
)

data class HousingSeekerData(
    var propertyType: String = "",
    var minBedrooms: String = "",
    var maxBedrooms: String = "",
    var minBudget: String = "",
    var maxBudget: String = "",
    var preferredAreas: String = "",
    var moveInDate: String = ""
)

data class SupportBeneficiaryData(
    var supportTypes: List<String> = emptyList(),
    var urgentNeeds: String = "",
    var location: String = "",
    var familySize: String = ""
)

data class EmployerData(
    var businessName: String = "",
    var industrySector: String = "",
    var companySize: String = "",
    var preferredSkills: String = ""
)

data class PropertyOwnerData(
    var professionalStatus: String = "",
    var propertyCount: String = "",
    var propertyTypes: String = "",
    var serviceAreas: String = ""
)

// Extension functions for converting to Map
fun JobSeekerData.toMap() = mapOf(
    "skills" to skills,
    "experienceLevel" to experienceLevel,
    "expectedSalary" to expectedSalary,
    "cvUrl" to (cvUrl ?: "")
)

fun SkilledProfessionalData.toMap() = mapOf(
    "profession" to if (profession == "Other") otherProfession else profession,
    "specialties" to specialties,
    "yearsExperience" to yearsExperience,
    "serviceAreas" to serviceAreas,
    "hourlyRate" to hourlyRate,
    "licenseNumber" to licenseNumber
)

fun AgentData.toMap() = mapOf(
    "agentType" to agentType,
    "specializations" to specializations,
    "serviceAreas" to serviceAreas,
    "commissionRate" to commissionRate,
    "licenseNumber" to licenseNumber
)

fun HousingSeekerData.toMap() = mapOf(
    "propertyType" to propertyType,
    "minBedrooms" to minBedrooms,
    "maxBedrooms" to maxBedrooms,
    "minBudget" to minBudget,
    "maxBudget" to maxBudget,
    "preferredAreas" to preferredAreas,
    "moveInDate" to moveInDate
)

fun SupportBeneficiaryData.toMap() = mapOf(
    "supportTypes" to supportTypes,
    "urgentNeeds" to urgentNeeds,
    "location" to location,
    "familySize" to familySize
)

fun EmployerData.toMap() = mapOf(
    "businessName" to businessName,
    "industrySector" to industrySector,
    "companySize" to companySize,
    "preferredSkills" to preferredSkills
)

fun PropertyOwnerData.toMap() = mapOf(
    "professionalStatus" to professionalStatus,
    "propertyCount" to propertyCount,
    "propertyTypes" to propertyTypes,
    "serviceAreas" to serviceAreas
)