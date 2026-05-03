package com.example.pivota.dashboard.data.mapper

import com.example.pivota.dashboard.data.dto.AccountBaseDto
import com.example.pivota.dashboard.data.dto.CompletionResponseDto
import com.example.pivota.dashboard.data.dto.EmployerProfileDataDto
import com.example.pivota.dashboard.data.dto.HousingSeekerProfileDataDto
import com.example.pivota.dashboard.data.dto.IntermediaryAgentDataDto
import com.example.pivota.dashboard.data.dto.IntermediaryAgentProfileDataDto
import com.example.pivota.dashboard.data.dto.JobSeekerProfileDataDto
import com.example.pivota.dashboard.data.dto.OrganizationProfileDataDto
import com.example.pivota.dashboard.data.dto.PropertyOwnerProfileDataDto
import com.example.pivota.dashboard.data.dto.SkilledProfessionalDataDto
import com.example.pivota.dashboard.data.dto.SkilledProfessionalProfileDataDto
import com.example.pivota.dashboard.data.dto.SupportBeneficiaryProfileDataDto
import com.example.pivota.dashboard.data.dto.UserBaseDto
import com.example.pivota.dashboard.data.dto.UserProfDto
import com.example.pivota.dashboard.data.dto.UserProfileMetadataDto
import com.example.pivota.dashboard.domain.model.AccountStatus
import com.example.pivota.dashboard.domain.model.AccountType
import com.example.pivota.dashboard.domain.model.AgentProfile
import com.example.pivota.dashboard.domain.model.AgentType
import com.example.pivota.dashboard.domain.model.BeneficiaryProfile
import com.example.pivota.dashboard.domain.model.ClientType
import com.example.pivota.dashboard.domain.model.CompleteProfile
import com.example.pivota.dashboard.domain.model.EmployerProfile
import com.example.pivota.dashboard.domain.model.HousingSeekerProfile
import com.example.pivota.dashboard.domain.model.IndividualProfile
import com.example.pivota.dashboard.domain.model.JobSeekerProfile
import com.example.pivota.dashboard.domain.model.ListingType
import com.example.pivota.dashboard.domain.model.OrganizationProfile
import com.example.pivota.dashboard.domain.model.ProfessionalProfile
import com.example.pivota.dashboard.domain.model.ProfileAccount
import com.example.pivota.dashboard.domain.model.ProfileCompletion
import com.example.pivota.dashboard.domain.model.ProfileUser
import com.example.pivota.dashboard.domain.model.PropertyOwnerProfile
import com.example.pivota.dashboard.domain.model.PropertyPurpose
import com.example.pivota.dashboard.domain.model.PropertyType
import com.example.pivota.dashboard.domain.model.SearchType
import com.example.pivota.dashboard.domain.model.UserStatus
import jakarta.inject.Inject
import jakarta.inject.Singleton

/**
 * Mapper for converting DTOs to Domain Models
 * Matches exactly the DTOs defined in the profile data layer
 */
@Singleton
class ProfileDtoMapper @Inject constructor() {


    /**
     * Main mapping function - converts UserProfDto to CompleteProfile domain model
     * Note: This expects the inner data (UserProfDto), not the wrapper (ProfileResponseDto)
     */
    fun toDomain(userProfDto: UserProfDto): CompleteProfile? {
        return CompleteProfile(
            user = toProfileUser(userProfDto.user, userProfDto.profile),
            account = toProfileAccount(userProfDto.account),
            individualProfile = toIndividualProfile(userProfDto.profile),
            organizationProfile = null,
            professionalProfile = userProfDto.skilledProfessionalProfile?.let {
                toProfessionalProfile(
                    it
                )
            },
            jobSeekerProfile = userProfDto.jobSeekerProfile?.let { toJobSeekerProfile(it) },
            agentProfile = userProfDto.intermediaryAgentProfile?.let { toAgentProfile(it) },
            housingSeekerProfile = userProfDto.housingSeekerProfile?.let { toHousingSeekerProfile(it) },
            propertyOwnerProfile = userProfDto.propertyOwnerProfile?.let { toPropertyOwnerProfile(it) },
            beneficiaryProfile = userProfDto.supportBeneficiaryProfile?.let {
                toBeneficiaryProfile(
                    it
                )
            },
            employerProfile = null,
            verifications = emptyList(),
            completion = toProfileCompletion(userProfDto.completion),
            createdAt = userProfDto.createdAt,
            updatedAt = userProfDto.updatedAt
        )
    }


    private fun toProfileUser(
        userDto: UserBaseDto,
        profileDto: UserProfileMetadataDto
    ): ProfileUser {
        return ProfileUser(
            id = userDto.uuid,
            userCode = userDto.userCode,
            email = userDto.email,
            firstName = userDto.firstName,
            lastName = userDto.lastName,
            fullName = "${userDto.firstName} ${userDto.lastName}".trim(),
            phoneNumber = userDto.phone.takeIf { it.isNotBlank() },
            profileImageUrl = profileDto.profileImage,
            status = UserStatus.Companion.fromString(userDto.status),
            role = userDto.roleName
        )
    }

    private fun toProfileAccount(accountDto: AccountBaseDto): ProfileAccount {
        return ProfileAccount(
            id = accountDto.uuid,
            code = accountDto.accountCode,
            name = null,
            type = AccountType.Companion.fromString(accountDto.type),
            status = AccountStatus.ACTIVE,
            isVerified = false,
            verifiedFeatures = emptyList(),
            createdAt = "",
            updatedAt = ""
        )
    }

    private fun toIndividualProfile(profileDto: UserProfileMetadataDto): IndividualProfile {
        return IndividualProfile(
            bio = profileDto.bio,
            gender = profileDto.gender,
            dateOfBirth = profileDto.dateOfBirth,
            nationalId = profileDto.nationalId,
            profileImage = profileDto.profileImage
        )
    }

    private fun toProfessionalProfile(dto: SkilledProfessionalProfileDataDto): ProfessionalProfile {
        return ProfessionalProfile(
            id = "",
            title = dto.title,
            profession = dto.profession ?: "",
            specialties = dto.specialties,
            serviceAreas = dto.serviceAreas,
            yearsExperience = dto.yearsExperience,
            licenseNumber = dto.licenseNumber,
            licenseBody = null,
            insuranceInfo = dto.insuranceInfo,
            hourlyRate = dto.hourlyRate,
            dailyRate = dto.dailyRate,
            paymentTerms = dto.paymentTerms,
            availableToday = dto.availableToday,
            availableWeekends = dto.availableWeekends,
            emergencyService = dto.emergencyService,
            portfolioImages = dto.portfolioImages,
            certifications = dto.certifications,
            isVerified = false,
            averageRating = 0f,
            totalReviews = 0,
            completedJobs = 0
        )
    }

    /**
     * Use this when you have SkilledProfessionalDataDto from GetSkilledProfessionalProfile endpoint
     */
    fun toEnhancedProfessionalProfile(dto: SkilledProfessionalDataDto): ProfessionalProfile {
        return ProfessionalProfile(
            id = dto.id,
            title = dto.title,
            profession = dto.profession ?: "",
            specialties = dto.specialties,
            serviceAreas = dto.serviceAreas,
            yearsExperience = dto.yearsExperience,
            licenseNumber = dto.licenseNumber,
            licenseBody = null,
            insuranceInfo = null,
            hourlyRate = dto.hourlyRate,
            dailyRate = dto.dailyRate,
            paymentTerms = null,
            availableToday = false,
            availableWeekends = false,
            emergencyService = false,
            portfolioImages = dto.portfolioImages,
            certifications = emptyList(),
            isVerified = dto.isVerified,
            averageRating = dto.averageRating,
            totalReviews = dto.totalReviews,
            completedJobs = 0
        )
    }

    private fun toJobSeekerProfile(dto: JobSeekerProfileDataDto): JobSeekerProfile {
        return JobSeekerProfile(
            id = "",
            headline = dto.headline,
            isActivelySeeking = dto.isActivelySeeking,
            skills = dto.skills,
            industries = dto.industries,
            jobTypes = dto.jobTypes,
            seniorityLevel = dto.seniorityLevel,
            noticePeriod = dto.noticePeriod,
            expectedSalary = dto.expectedSalary?.toInt(),
            workAuthorization = dto.workAuthorization,
            cvUrl = dto.cvUrl,
            cvLastUpdated = null,  // Not in JobSeekerProfileDataDto
            portfolioImages = dto.portfolioImages,
            linkedInUrl = dto.linkedInUrl,
            githubUrl = dto.githubUrl,
            portfolioUrl = dto.portfolioUrl,
            hasAgent = dto.hasAgent,
            agentId = dto.agentUuid
        )
    }

    private fun toAgentProfile(dto: IntermediaryAgentProfileDataDto): AgentProfile {
        return AgentProfile(
            id = "",
            agentType = AgentType.Companion.fromString(dto.agentType),
            specializations = dto.specializations,
            serviceAreas = dto.serviceAreas,
            licenseNumber = dto.licenseNumber,
            licenseBody = dto.licenseBody,
            yearsExperience = dto.yearsExperience,
            agencyName = dto.agencyName,
            agencyId = dto.agencyUuid,
            commissionRate = dto.commissionRate,
            feeStructure = dto.feeStructure,
            minimumFee = dto.minimumFee,
            typicalFee = dto.typicalFee,
            about = dto.about,
            profileImage = dto.profileImage,
            contactEmail = dto.contactEmail,
            contactPhone = dto.contactPhone,
            website = dto.website,
            socialLinks = dto.socialLinks,
            clientTypes = dto.clientTypes.mapNotNull { ClientType.Companion.fromString(it) },
            isVerified = false,
            averageRating = 0f,
            totalReviews = 0,
            completedDeals = 0
        )
    }

    /**
     * Use this when you have IntermediaryAgentDataDto from GetIntermediaryAgentProfile endpoint
     */
    fun toEnhancedAgentProfile(dto: IntermediaryAgentDataDto): AgentProfile {
        return AgentProfile(
            id = dto.id,
            agentType = AgentType.Companion.fromString(dto.agentType),
            specializations = dto.specializations,
            serviceAreas = dto.serviceAreas,
            licenseNumber = dto.licenseNumber,
            licenseBody = dto.licenseBody,
            yearsExperience = dto.yearsExperience,
            agencyName = dto.agencyName,
            agencyId = dto.agencyUuid,
            commissionRate = dto.commissionRate,
            feeStructure = dto.feeStructure,
            minimumFee = dto.minimumFee,
            typicalFee = dto.typicalFee,
            about = dto.about,
            profileImage = dto.profileImage,
            contactEmail = dto.contactEmail,
            contactPhone = dto.contactPhone,
            website = dto.website,
            socialLinks = dto.socialLinks,
            clientTypes = dto.clientTypes.mapNotNull { ClientType.Companion.fromString(it) },
            isVerified = dto.isVerified,
            averageRating = dto.averageRating,
            totalReviews = dto.totalReviews,
            completedDeals = dto.completedDeals
        )
    }

    private fun toHousingSeekerProfile(dto: HousingSeekerProfileDataDto): HousingSeekerProfile {
        return HousingSeekerProfile(
            id = "",
            searchType = SearchType.Companion.fromString(dto.searchType ?: "BOTH"),
            isLookingForRental = dto.isLookingForRental,
            isLookingToBuy = dto.isLookingToBuy,
            minBedrooms = dto.minBedrooms,
            maxBedrooms = dto.maxBedrooms,
            minBudget = dto.minBudget,
            maxBudget = dto.maxBudget,
            preferredTypes = dto.preferredTypes.mapNotNull { PropertyType.Companion.fromString(it) },
            preferredCities = dto.preferredCities,
            preferredNeighborhoods = dto.preferredNeighborhoods,
            moveInDate = dto.moveInDate,
            leaseDuration = dto.leaseDuration,
            householdSize = dto.householdSize,
            hasPets = dto.hasPets,
            petDetails = dto.petDetails,
            latitude = dto.latitude,
            longitude = dto.longitude,
            searchRadiusKm = dto.searchRadiusKm,
            hasAgent = dto.hasAgent,
            agentId = dto.agentUuid
        )
    }

    private fun toPropertyOwnerProfile(dto: PropertyOwnerProfileDataDto): PropertyOwnerProfile {
        return PropertyOwnerProfile(
            id = "",
            listingType = ListingType.Companion.fromString(dto.listingType ?: "BOTH"),
            isListingForRent = dto.isListingForRent,
            isListingForSale = dto.isListingForSale,
            isProfessional = dto.isProfessional,
            licenseNumber = dto.licenseNumber,
            companyName = dto.companyName,
            yearsInBusiness = dto.yearsInBusiness,
            propertyCount = dto.propertyCount,
            propertyTypes = dto.propertyTypes.mapNotNull { PropertyType.Companion.fromString(it) },
            preferredPropertyTypes = dto.preferredPropertyTypes.mapNotNull {
                PropertyType.Companion.fromString(
                    it
                )
            },
            serviceAreas = dto.serviceAreas,
            propertyPurpose = dto.propertyPurpose?.let { PropertyPurpose.Companion.fromString(it) },
            usesAgent = dto.usesAgent,
            managingAgentId = dto.managingAgentUuid,
            isVerified = false
        )
    }

    private fun toBeneficiaryProfile(dto: SupportBeneficiaryProfileDataDto): BeneficiaryProfile {
        return BeneficiaryProfile(
            id = "",
            needs = dto.needs,
            urgentNeeds = dto.urgentNeeds,
            familySize = dto.familySize,
            dependents = dto.dependents,
            householdComposition = dto.householdComposition,
            vulnerabilityFactors = dto.vulnerabilityFactors,
            city = dto.city,
            neighborhood = dto.neighborhood,
            latitude = dto.latitude,
            longitude = dto.longitude,
            landmark = dto.landmark,
            prefersAnonymity = dto.prefersAnonymity,
            languagePreferences = dto.languagePreference,
            consentToShare = dto.consentToShare,
            referredBy = dto.referredBy,
            referredById = dto.referredByUuid,
            caseWorkerId = dto.caseWorkerUuid
        )
    }

    fun toEmployerProfile(dto: EmployerProfileDataDto): EmployerProfile {
        return EmployerProfile(
            id = "",
            companyName = dto.companyName,
            industry = dto.industry,
            companySize = dto.companySize,
            foundedYear = dto.foundedYear,
            description = dto.description,
            logo = dto.logo,
            preferredSkills = dto.preferredSkills,
            remotePolicy = dto.remotePolicy,
            worksWithAgents = dto.worksWithAgents,
            preferredAgents = dto.preferredAgents,
            businessName = dto.businessName,
            isRegistered = dto.isRegistered,
            yearsExperience = dto.yearsExperience,
            isVerified = false
        )
    }

    fun toOrganizationProfile(dto: OrganizationProfileDataDto): OrganizationProfile {
        return OrganizationProfile(
            id = dto.uuid,
            name = dto.name,
            type = dto.type,
            registrationNumber = dto.registrationNo,
            kraPin = dto.kraPin,
            officialEmail = dto.officialEmail,
            officialPhone = dto.officialPhone,
            website = dto.website,
            about = dto.about,
            logo = dto.logo,
            physicalAddress = dto.physicalAddress,
            members = emptyList(),
            pendingInvitations = emptyList()
        )
    }

    private fun toProfileCompletion(dto: CompletionResponseDto): ProfileCompletion {
        return ProfileCompletion(
            accountCompleted = dto.isComplete,
            profileCompleted = if (dto.isComplete) 100 else dto.percentage,
            documentsCompleted = 0
        )
    }
}