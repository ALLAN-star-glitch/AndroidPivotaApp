package com.example.pivota.auth.data.mapper

import com.example.pivota.auth.data.remote.dto.*
import com.example.pivota.auth.domain.model.*
import javax.inject.Inject

class AuthDataMapper @Inject constructor() {

    // ======================================================
    // DOMAIN TO DTO MAPPERS (For sending requests)
    // ======================================================

    fun toSignupRequestDto(
        user: User,
        code: String,
        password: String
    ): SignupRequestDto {
        return SignupRequestDto(
            firstName = user.firstName,
            lastName = user.lastName,
            email = user.email,
            password = password,
            phone = user.personalPhone ?: "",
            planSlug = "free-forever",
            code = code,
            profileImage = user.profileImage,
            primaryPurpose = user.primaryPurpose,
            jobSeekerData = user.jobSeekerPreferences?.let { toJobSeekerDataDto(it) },
            skilledProfessionalData = user.skilledProfessionalProfile?.let { toSkilledProfessionalDataDto(it) },
            intermediaryAgentData = user.intermediaryAgentProfile?.let { toIntermediaryAgentDataDto(it) },
            housingSeekerData = user.housingSeekerPreferences?.let { toHousingSeekerDataDto(it) },
            supportBeneficiaryData = user.supportBeneficiaryNeeds?.let { toSupportBeneficiaryDataDto(it) },
            employerData = user.employerRequirements?.let { toEmployerDataDto(it) },
            propertyOwnerData = user.propertyOwnerPortfolio?.let { toPropertyOwnerDataDto(it) }
        )
    }

    fun toJobSeekerDataDto(domain: JobSeekerPreferences): JobSeekerProfileDataDto {
        return JobSeekerProfileDataDto(
            headline = domain.headline,
            isActivelySeeking = domain.isActivelySeeking,
            skills = domain.skills,
            industries = domain.industries,
            jobTypes = domain.jobTypes,
            seniorityLevel = domain.seniorityLevel,
            noticePeriod = domain.noticePeriod,
            expectedSalary = domain.expectedSalary?.toDouble(),
            workAuthorization = domain.workAuthorization,
            cvUrl = domain.cvUrl,
            portfolioImages = domain.portfolioImages,
            linkedInUrl = domain.linkedInUrl,
            githubUrl = domain.githubUrl,
            portfolioUrl = domain.portfolioUrl,
            hasAgent = domain.hasAgent,
            agentUuid = domain.agentUuid
        )
    }

    fun toSkilledProfessionalDataDto(domain: SkilledProfessionalProfile): SkilledProfessionalProfileDataDto {
        return SkilledProfessionalProfileDataDto(
            title = domain.title,
            profession = domain.profession,
            specialties = domain.specialties,
            serviceAreas = domain.serviceAreas,
            yearsExperience = domain.yearsExperience,
            licenseNumber = domain.licenseNumber,
            insuranceInfo = domain.insuranceInfo,
            hourlyRate = domain.hourlyRate,
            dailyRate = domain.dailyRate,
            paymentTerms = domain.paymentTerms,
            availableToday = domain.availableToday,
            availableWeekends = domain.availableWeekends,
            emergencyService = domain.emergencyService,
            portfolioImages = domain.portfolioImages,
            certifications = domain.certifications
        )
    }

    fun toIntermediaryAgentDataDto(domain: IntermediaryAgentProfile): IntermediaryAgentProfileDataDto {
        return IntermediaryAgentProfileDataDto(
            agentType = domain.agentType,
            specializations = domain.specializations,
            serviceAreas = domain.serviceAreas,
            licenseNumber = domain.licenseNumber,
            licenseBody = domain.licenseBody,
            yearsExperience = domain.yearsExperience,
            agencyName = domain.agencyName,
            agencyUuid = domain.agencyUuid,
            commissionRate = domain.commissionRate,
            feeStructure = domain.feeStructure,
            minimumFee = domain.minimumFee,
            typicalFee = domain.typicalFee,
            about = domain.about,
            profileImage = domain.profileImage,
            contactEmail = domain.contactEmail,
            contactPhone = domain.contactPhone,
            website = domain.website,
            socialLinks = domain.socialLinks,
            clientTypes = domain.clientTypes
        )
    }

    fun toHousingSeekerDataDto(domain: HousingSeekerPreferences): HousingSeekerProfileDataDto {
        return HousingSeekerProfileDataDto(
            minBedrooms = domain.minBedrooms,
            maxBedrooms = domain.maxBedrooms,
            minBudget = domain.minBudget,
            maxBudget = domain.maxBudget,
            preferredTypes = domain.preferredTypes,
            preferredCities = domain.preferredCities,
            preferredNeighborhoods = domain.preferredNeighborhoods,
            moveInDate = domain.moveInDate,
            leaseDuration = domain.leaseDuration,
            householdSize = domain.householdSize,
            hasPets = domain.hasPets,
            petDetails = domain.petDetails,
            latitude = domain.latitude,
            longitude = domain.longitude,
            searchRadiusKm = domain.searchRadiusKm,
            hasAgent = domain.hasAgent,
            agentUuid = domain.agentUuid
        )
    }

    fun toSupportBeneficiaryDataDto(domain: SupportBeneficiaryNeeds): SupportBeneficiaryProfileDataDto {
        return SupportBeneficiaryProfileDataDto(
            needs = domain.needs,
            urgentNeeds = domain.urgentNeeds,
            familySize = domain.familySize,
            dependents = domain.dependents,
            householdComposition = domain.householdComposition,
            vulnerabilityFactors = domain.vulnerabilityFactors,
            city = domain.city,
            neighborhood = domain.neighborhood,
            latitude = domain.latitude,
            longitude = domain.longitude,
            landmark = domain.landmark,
            prefersAnonymity = domain.prefersAnonymity,
            languagePreference = domain.languagePreference,
            consentToShare = domain.consentToShare,
            referredBy = domain.referredBy,
            referredByUuid = domain.referredByUuid,
            caseWorkerUuid = domain.caseWorkerUuid
        )
    }

    fun toEmployerDataDto(domain: EmployerRequirements): EmployerProfileDataDto {
        return EmployerProfileDataDto(
            companyName = domain.companyName,
            industry = domain.industry,
            companySize = domain.companySize,
            foundedYear = domain.foundedYear,
            description = domain.description,
            logo = domain.logo,
            preferredSkills = domain.preferredSkills,
            remotePolicy = domain.remotePolicy,
            worksWithAgents = domain.worksWithAgents,
            preferredAgents = domain.preferredAgents,
            businessName = domain.businessName,
            isRegistered = domain.isRegistered,
            yearsExperience = domain.yearsExperience
        )
    }

    fun toPropertyOwnerDataDto(domain: PropertyOwnerPortfolio): PropertyOwnerProfileDataDto {
        return PropertyOwnerProfileDataDto(
            isProfessional = domain.isProfessional,
            licenseNumber = domain.licenseNumber,
            companyName = domain.companyName,
            yearsInBusiness = domain.yearsInBusiness,
            preferredPropertyTypes = domain.preferredPropertyTypes,
            serviceAreas = domain.serviceAreas,
            usesAgent = domain.usesAgent,
            managingAgentUuid = domain.managingAgentUuid,
            propertyCount = domain.propertyCount,
            propertyTypes = domain.propertyTypes,
            propertyPurpose = domain.propertyPurpose
        )
    }

    // ======================================================
    // RESPONSE DTO TO DOMAIN MAPPERS (For handling API responses)
    // ======================================================

    /**
     * Convert LoginResponseDto to LoginResponse domain model
     * Handles both MFA required and Authenticated states
     */
    fun toLoginResponse(response: LoginResponseDto): LoginResponse {
        return if (response.success && response.data != null) {
            if (response.data.accessToken != null) {
                // Stage 2: Authenticated with tokens - Create full User object
                val user = toUserFromLoginData(response.data, response.data.email ?: "")
                LoginResponse.Authenticated(
                    user = user,
                    accessToken = response.data.accessToken,
                    refreshToken = response.data.refreshToken ?: "",
                    message = response.data.message
                )
            } else {
                // Stage 1: MFA Required
                LoginResponse.MfaRequired(
                    email = response.data.email ?: "",
                    uuid = response.data.userUuid ?: ""
                )
            }
        } else {
            // Return error - will be handled by the caller
            throw Exception(response.message)
        }
    }

    /**
     * Convert successful login data to User domain model with all JWT fields
     * Used after MFA verification when tokens are returned
     */
    fun toUserFromLoginData(data: LoginDataDto, email: String): User {
        return User(
            uuid = data.userUuid ?: "",
            email = data.email ?: email,
            firstName = data.firstName ?: "",
            lastName = data.lastName ?: "",
            userName = data.userName ?: "",  // Full name from JWT
            personalPhone = data.phone,
            profileImage = data.profileImage,
            accessToken = data.accessToken,
            refreshToken = data.refreshToken,
            isAuthenticated = true,
            primaryPurpose = data.primaryPurpose,
            // JWT payload fields
            userUuid = data.userUuid,
            accountId = data.accountId,
            accountName = data.accountName,
            accountType = data.accountType,
            tokenId = data.tokenId,
            role = data.role,
            organizationUuid = data.organizationUuid,
            planSlug = data.planSlug
        )
    }

    /**
     * Convert successful login response to User domain model
     * Simplified version when you only have the response DTO
     */
    fun toUser(response: LoginResponseDto, email: String): User {
        return if (response.success && response.data != null) {
            toUserFromLoginData(response.data, email)
        } else {
            User(
                uuid = "",
                email = email,
                firstName = "",
                lastName = "",
                userName = "",
                isAuthenticated = false
            )
        }
    }

    /**
     * Convert VerifyOtpResponseDto to verification result
     */
    fun toVerificationResult(response: VerifyOtpResponseDto): Boolean {
        return response.success && response.data?.verified == true
    }

    /**
     * Get error message from response
     */
    fun getErrorMessage(response: BaseResponseDto<*>): String {
        return response.error?.message ?: response.message
    }

    /**
     * Check if response is successful
     */
    fun isSuccess(response: BaseResponseDto<*>): Boolean {
        return response.success
    }

    // ======================================================
    // DTO TO DOMAIN MAPPERS (For profile data from API)
    // ======================================================

    fun toJobSeekerPreferences(dto: JobSeekerProfileDataDto): JobSeekerPreferences {
        return JobSeekerPreferences(
            headline = dto.headline ?: "",
            isActivelySeeking = dto.isActivelySeeking,
            skills = dto.skills,
            industries = dto.industries,
            jobTypes = dto.jobTypes,
            seniorityLevel = dto.seniorityLevel,
            noticePeriod = dto.noticePeriod,
            expectedSalary = dto.expectedSalary?.toInt(),
            workAuthorization = dto.workAuthorization,
            cvUrl = dto.cvUrl,
            portfolioImages = dto.portfolioImages,
            linkedInUrl = dto.linkedInUrl,
            githubUrl = dto.githubUrl,
            portfolioUrl = dto.portfolioUrl,
            hasAgent = dto.hasAgent,
            agentUuid = dto.agentUuid
        )
    }

    fun toSkilledProfessionalProfile(dto: SkilledProfessionalProfileDataDto): SkilledProfessionalProfile {
        return SkilledProfessionalProfile(
            title = dto.title,
            profession = dto.profession ?: "",
            specialties = dto.specialties,
            serviceAreas = dto.serviceAreas,
            yearsExperience = dto.yearsExperience,
            licenseNumber = dto.licenseNumber,
            insuranceInfo = dto.insuranceInfo,
            hourlyRate = dto.hourlyRate,
            dailyRate = dto.dailyRate,
            paymentTerms = dto.paymentTerms,
            availableToday = dto.availableToday,
            availableWeekends = dto.availableWeekends,
            emergencyService = dto.emergencyService,
            portfolioImages = dto.portfolioImages,
            certifications = dto.certifications
        )
    }

    fun toIntermediaryAgentProfile(dto: IntermediaryAgentProfileDataDto): IntermediaryAgentProfile {
        return IntermediaryAgentProfile(
            agentType = dto.agentType,
            specializations = dto.specializations,
            serviceAreas = dto.serviceAreas,
            licenseNumber = dto.licenseNumber,
            licenseBody = dto.licenseBody,
            yearsExperience = dto.yearsExperience,
            agencyName = dto.agencyName,
            agencyUuid = dto.agencyUuid,
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
            clientTypes = dto.clientTypes
        )
    }

    fun toHousingSeekerPreferences(dto: HousingSeekerProfileDataDto): HousingSeekerPreferences {
        return HousingSeekerPreferences(
            minBedrooms = dto.minBedrooms,
            maxBedrooms = dto.maxBedrooms,
            minBudget = dto.minBudget,
            maxBudget = dto.maxBudget,
            preferredTypes = dto.preferredTypes,
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
            agentUuid = dto.agentUuid
        )
    }

    fun toSupportBeneficiaryNeeds(dto: SupportBeneficiaryProfileDataDto): SupportBeneficiaryNeeds {
        return SupportBeneficiaryNeeds(
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
            languagePreference = dto.languagePreference,
            consentToShare = dto.consentToShare,
            referredBy = dto.referredBy,
            referredByUuid = dto.referredByUuid,
            caseWorkerUuid = dto.caseWorkerUuid
        )
    }

    fun toEmployerRequirements(dto: EmployerProfileDataDto): EmployerRequirements {
        return EmployerRequirements(
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
            yearsExperience = dto.yearsExperience
        )
    }

    fun toPropertyOwnerPortfolio(dto: PropertyOwnerProfileDataDto): PropertyOwnerPortfolio {
        return PropertyOwnerPortfolio(
            isProfessional = dto.isProfessional,
            licenseNumber = dto.licenseNumber,
            companyName = dto.companyName,
            yearsInBusiness = dto.yearsInBusiness,
            preferredPropertyTypes = dto.preferredPropertyTypes,
            serviceAreas = dto.serviceAreas,
            usesAgent = dto.usesAgent,
            managingAgentUuid = dto.managingAgentUuid,
            propertyCount = dto.propertyCount,
            propertyTypes = dto.propertyTypes,
            propertyPurpose = dto.propertyPurpose
        )
    }

    // ======================================================
    // HELPER METHODS FOR OTHER RESPONSES
    // ======================================================

    /**
     * Convert SignupResponseDto to domain result
     */
    fun toSignupResult(response: SignupResponseDto): Boolean {
        return response.success
    }

    /**
     * Convert ResetPassword response to domain result
     */
    fun toResetPasswordResult(response: BaseResponseDto<Nothing>): Pair<Boolean, String> {
        return Pair(response.success, response.message)
    }

    /**
     * Convert RefreshTokenResponseDto to token pair
     */
    fun toTokenPair(response: RefreshTokenResponseDto): Pair<String, String>? {
        return if (response.success && response.data != null) {
            Pair(response.data.accessToken, response.data.refreshToken)
        } else {
            null
        }
    }

    /**
     * Convert BaseOtpResponseDto to domain result
     */
    fun toOtpRequestResult(response: BaseOtpResponseDto): Boolean {
        return response.success
    }
}