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

    // UPDATED: Include new simplified fields for housing seeker
    fun toHousingSeekerDataDto(domain: HousingSeekerPreferences): HousingSeekerProfileDataDto {
        return HousingSeekerProfileDataDto(
            // New simplified fields
            searchType = domain.searchType,
            isLookingForRental = domain.isLookingForRental,
            isLookingToBuy = domain.isLookingToBuy,
            preferredTypes = domain.propertyTypes,  // Map propertyTypes to preferredTypes
            // Legacy fields (kept for backward compatibility)
            minBedrooms = domain.minBedrooms,
            maxBedrooms = domain.maxBedrooms,
            minBudget = domain.minBudget,
            maxBudget = domain.maxBudget,
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

    // UPDATED: Include new simplified fields for property owner
    fun toPropertyOwnerDataDto(domain: PropertyOwnerPortfolio): PropertyOwnerProfileDataDto {
        return PropertyOwnerProfileDataDto(
            // New simplified fields
            listingType = domain.listingType,
            isListingForRent = domain.isListingForRent,
            isListingForSale = domain.isListingForSale,
            // Core fields
            propertyCount = domain.propertyCount,
            propertyTypes = domain.propertyTypes,
            serviceAreas = domain.serviceAreas,
            // Legacy fields (kept for backward compatibility)
            isProfessional = domain.isProfessional,
            licenseNumber = domain.licenseNumber,
            companyName = domain.companyName,
            yearsInBusiness = domain.yearsInBusiness,
            preferredPropertyTypes = domain.preferredPropertyTypes,
            usesAgent = domain.usesAgent,
            managingAgentUuid = domain.managingAgentUuid,
            propertyPurpose = domain.propertyPurpose
        )
    }

    // ======================================================
    // RESPONSE DTO TO DOMAIN MAPPERS (For handling API responses)
    // ======================================================

    fun toLoginResponse(response: LoginResponseDto): LoginResponse {
        return if (response.success && response.data != null) {
            if (response.data.accessToken != null) {
                val user = toUserFromLoginData(response.data, response.data.email ?: "")
                LoginResponse.Authenticated(
                    user = user,
                    accessToken = response.data.accessToken,
                    refreshToken = response.data.refreshToken ?: "",
                    message = response.data.message
                )
            } else {
                LoginResponse.MfaRequired(
                    email = response.data.email ?: "",
                    uuid = response.data.userUuid ?: ""
                )
            }
        } else {
            throw Exception(response.message)
        }
    }

    fun toUserFromLoginData(data: LoginDataDto, email: String): User {
        return User(
            uuid = data.userUuid ?: "",
            email = data.email ?: email,
            firstName = data.firstName ?: "",
            lastName = data.lastName ?: "",
            userName = data.userName ?: "",
            personalPhone = data.phone,
            profileImage = data.profileImage,
            accessToken = data.accessToken,
            refreshToken = data.refreshToken,
            isAuthenticated = true,
            primaryPurpose = data.primaryPurpose,
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

    fun toVerificationResult(response: VerifyOtpResponseDto): Boolean {
        return response.success && response.data?.verified == true
    }

    fun getErrorMessage(response: BaseResponseDto<*>): String {
        return response.error?.message ?: response.message
    }

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

    // UPDATED: Map from DTO to domain with new fields
    fun toHousingSeekerPreferences(dto: HousingSeekerProfileDataDto): HousingSeekerPreferences {
        return HousingSeekerPreferences(
            // New simplified fields
            searchType = dto.searchType,
            isLookingForRental = dto.isLookingForRental,
            isLookingToBuy = dto.isLookingToBuy,
            propertyTypes = dto.preferredTypes,  // Map preferredTypes to propertyTypes
            // Legacy fields
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

    // UPDATED: Map from DTO to domain with new fields
    fun toPropertyOwnerPortfolio(dto: PropertyOwnerProfileDataDto): PropertyOwnerPortfolio {
        return PropertyOwnerPortfolio(
            // New simplified fields
            listingType = dto.listingType,
            isListingForRent = dto.isListingForRent,
            isListingForSale = dto.isListingForSale,
            // Core fields
            propertyCount = dto.propertyCount,
            propertyTypes = dto.propertyTypes,
            serviceAreas = dto.serviceAreas,
            // Legacy fields
            isProfessional = dto.isProfessional,
            licenseNumber = dto.licenseNumber,
            companyName = dto.companyName,
            yearsInBusiness = dto.yearsInBusiness,
            preferredPropertyTypes = dto.preferredPropertyTypes,
            usesAgent = dto.usesAgent,
            managingAgentUuid = dto.managingAgentUuid,
            propertyPurpose = dto.propertyPurpose
        )
    }

    // ======================================================
    // HELPER METHODS FOR OTHER RESPONSES
    // ======================================================

    fun toSignupResult(response: SignupResponseDto): Boolean {
        return response.success
    }

    fun toResetPasswordResult(response: BaseResponseDto<Nothing>): Pair<Boolean, String> {
        return Pair(response.success, response.message)
    }

    fun toTokenPair(response: RefreshTokenResponseDto): Pair<String, String>? {
        return if (response.success && response.data != null) {
            Pair(response.data.accessToken, response.data.refreshToken)
        } else {
            null
        }
    }

    fun toOtpRequestResult(response: BaseOtpResponseDto): Boolean {
        return response.success
    }


// ======================================================
// NEW PROFILE RESPONSE TO DOMAIN MAPPERS
// ======================================================

    /**
     * Convert complete profile response to domain models
     * This method maps the entire ProfileResponseDto to individual domain objects
     */
    fun toCompleteProfile(response: ProfileResponseDto): CompleteProfileResult {
        return CompleteProfileResult(
            account = response.account.toAccount(),
            user = response.user?.toUser(),
            individualProfile = response.profile?.toIndividualProfile(),
            organizationProfile = response.organizationProfile?.toOrganizationProfile(),
            jobSeekerProfile = response.jobSeekerProfile?.let { toJobSeekerPreferences(it.toJobSeekerDataDto()) },
            skilledProfessionalProfile = response.skilledProfessionalProfile?.let {
                toSkilledProfessionalProfile(it.toSkilledProfessionalDataDto())
            },
            intermediaryAgentProfile = response.intermediaryAgentProfile?.let {
                toIntermediaryAgentProfile(it.toIntermediaryAgentDataDto())
            },
            housingSeekerProfile = response.housingSeekerProfile?.let {
                toHousingSeekerPreferences(it.toHousingSeekerDataDto())
            },
            supportBeneficiaryProfile = response.supportBeneficiaryProfile?.let {
                toSupportBeneficiaryNeeds(it.toSupportBeneficiaryDataDto())
            },
            employerProfile = response.employerProfile?.let {
                toEmployerRequirements(it.toEmployerDataDto())
            },
            propertyOwnerProfile = response.propertyOwnerProfile?.let {
                toPropertyOwnerPortfolio(it.toPropertyOwnerDataDto())
            }
        )
    }

    /**
     * Convert AccountDataDto to Account domain model
     */
    fun AccountDataDto.toAccount(): Account {
        return Account(
            uuid = uuid,
            accountCode = accountCode,
            type = type,
            status = status ?: "ACTIVE",  // Default to ACTIVE if not provided
            isVerified = isVerified ?: false,  // Default to false
            verifiedFeatures = verifiedFeatures ?: emptyList(),  // Default to empty list
            createdAt = createdAt ?: "",  // Default to empty string
            updatedAt = updatedAt ?: "",  // Default to empty string
            name = name
        )
    }

    /**
     * Convert UserDataDto to existing User domain model
     */
    fun UserDataDto.toUser(): User {
        return User(
            uuid = uuid,
            email = email,
            firstName = firstName ?: "",
            lastName = lastName ?: "",
            userName = "${firstName ?: ""} ${lastName ?: ""}".trim(),
            personalPhone = phone,
            profileImage = profileImage,
            isAuthenticated = true,
            userUuid = uuid,
            role = roleName,
            // These will be populated from account data separately
            accountId = null,
            accountName = null,
            accountType = null,
            tokenId = null,
            organizationUuid = null,
            planSlug = null,
            primaryPurpose = null,
            profileImageUrl = profileImage,
            accessToken = null,
            refreshToken = null,
            // Profile data is set separately in CompleteProfileResult
            jobSeekerPreferences = null,
            skilledProfessionalProfile = null,
            intermediaryAgentProfile = null,
            housingSeekerPreferences = null,
            supportBeneficiaryNeeds = null,
            employerRequirements = null,
            propertyOwnerPortfolio = null
        )
    }

    /**
     * Convert IndividualProfileDto to IndividualProfile domain model
     */
    fun IndividualProfileDto.toIndividualProfile(): IndividualProfile {
        return IndividualProfile(
            bio = bio,
            gender = gender,
            dateOfBirth = dateOfBirth,
            nationalId = nationalId,
            profileImage = profileImage
        )
    }

    /**
     * Convert OrganizationProfileDto to OrganizationProfile domain model
     */
    fun OrganizationProfileDto.toOrganizationProfile(): OrganizationProfile {
        return OrganizationProfile(
            name = name,
            type = type,
            registrationNo = registrationNo,
            kraPin = kraPin,
            officialEmail = officialEmail,
            officialPhone = officialPhone,
            website = website,
            about = about,
            logo = logo,
            physicalAddress = physicalAddress,
            members = members.map { it.toTeamMember() },
            pendingInvitations = pendingInvitations.map { it.toPendingInvitation() }
        )
    }

    /**
     * Convert TeamMemberDto to TeamMember domain model
     */
    fun TeamMemberDto.toTeamMember(): TeamMember {
        return TeamMember(
            userUuid = userUuid,
            userName = userName,
            userEmail = userEmail,
            userImage = userImage,
            roleName = roleName
        )
    }

    /**
     * Convert PendingInvitationDto to PendingInvitation domain model
     */
    fun PendingInvitationDto.toPendingInvitation(): PendingInvitation {
        return PendingInvitation(
            id = id,
            email = email,
            status = status,
            expiresAt = expiresAt
        )
    }

    /**
     * Convert VerificationItemDto to VerificationItem domain model
     */
    fun VerificationItemDto.toVerificationItem(): VerificationItem {
        return VerificationItem(
            type = VerificationType.fromString(type),
            status = VerificationStatus.fromString(status),
            documentUrl = documentUrl,
            rejectionReason = rejectionReason,
            verifiedAt = verifiedAt,
            expiresAt = expiresAt
        )
    }

    /**
     * Convert ProfileCompletionDto to ProfileCompletion domain model
     */
    fun ProfileCompletionDto.toProfileCompletion(): ProfileCompletion {
        return ProfileCompletion(
            accountCompleted = false,  // Backend doesn't send this, default to false
            profileCompleted = percentage,  // Use percentage
            documentsCompleted = 0  // Backend doesn't send this
        )
    }

// ======================================================
// DTO TO JOBSEEKER DATA DTO CONVERTERS (for profile response)
// ======================================================

    /**
     * Convert JobSeekerProfileDto to JobSeekerProfileDataDto
     * This bridges the profile response DTO to the existing mapper
     */
    fun JobSeekerProfileDto.toJobSeekerDataDto(): JobSeekerProfileDataDto {
        return JobSeekerProfileDataDto(
            headline = headline,
            isActivelySeeking = isActivelySeeking,
            skills = skills,
            industries = industries,
            jobTypes = jobTypes,
            seniorityLevel = seniorityLevel,
            noticePeriod = noticePeriod,
            expectedSalary = expectedSalary,
            workAuthorization = workAuthorization,
            cvUrl = cvUrl,
            portfolioImages = portfolioImages,
            linkedInUrl = linkedInUrl,
            githubUrl = githubUrl,
            portfolioUrl = portfolioUrl,
            hasAgent = false,
            agentUuid = null
        )
    }

    /**
     * Convert SkilledProfessionalProfileDto to SkilledProfessionalProfileDataDto
     */
    fun SkilledProfessionalProfileDto.toSkilledProfessionalDataDto(): SkilledProfessionalProfileDataDto {
        return SkilledProfessionalProfileDataDto(
            title = title,
            profession = profession,
            specialties = specialties,
            serviceAreas = serviceAreas,
            yearsExperience = yearsExperience,
            licenseNumber = licenseNumber,
            insuranceInfo = insuranceInfo,
            hourlyRate = hourlyRate,
            dailyRate = dailyRate,
            paymentTerms = paymentTerms,
            availableToday = availableToday,
            availableWeekends = availableWeekends,
            emergencyService = emergencyService,
            portfolioImages = portfolioImages,
            certifications = certifications
        )
    }

    /**
     * Convert IntermediaryAgentProfileDto to IntermediaryAgentProfileDataDto
     */
    fun IntermediaryAgentProfileDto.toIntermediaryAgentDataDto(): IntermediaryAgentProfileDataDto {
        return IntermediaryAgentProfileDataDto(
            agentType = agentType,
            specializations = specializations,
            serviceAreas = serviceAreas,
            licenseNumber = licenseNumber,
            licenseBody = licenseBody,
            yearsExperience = yearsExperience,
            agencyName = agencyName,
            agencyUuid = agencyUuid,
            commissionRate = commissionRate,
            feeStructure = feeStructure,
            minimumFee = minimumFee,
            typicalFee = typicalFee,
            about = about,
            profileImage = profileImage,
            contactEmail = contactEmail,
            contactPhone = contactPhone,
            website = website,
            socialLinks = socialLinks,
            clientTypes = clientTypes
        )
    }

    /**
     * Convert HousingSeekerProfileDto to HousingSeekerProfileDataDto
     */
    fun HousingSeekerProfileDto.toHousingSeekerDataDto(): HousingSeekerProfileDataDto {
        return HousingSeekerProfileDataDto(
            // New simplified fields
            searchType = searchType,
            isLookingForRental = isLookingForRental,
            isLookingToBuy = isLookingToBuy,
            // Rental preferences
            minBedrooms = minBedrooms,
            maxBedrooms = maxBedrooms,
            minBudget = minBudget,
            maxBudget = maxBudget,
            preferredTypes = preferredTypes,
            preferredCities = preferredCities,
            preferredNeighborhoods = preferredNeighborhoods,
            moveInDate = moveInDate,
            leaseDuration = leaseDuration,
            householdSize = householdSize,
            hasPets = hasPets,
            petDetails = petDetails,
            minimumLeaseTerm = minimumLeaseTerm,
            maximumLeaseTerm = maximumLeaseTerm,
            depositAmount = depositAmount,
            isPetFriendly = isPetFriendly,
            utilitiesIncluded = utilitiesIncluded,
            utilitiesDetails = utilitiesDetails,
            // Sale preferences
            isNegotiable = isNegotiable,
            titleDeedAvailable = titleDeedAvailable,
            // Location
            latitude = latitude,
            longitude = longitude,
            searchRadiusKm = searchRadiusKm,
            hasAgent = hasAgent,
            agentUuid = agentUuid
        )
    }

    /**
     * Convert SupportBeneficiaryProfileDto to SupportBeneficiaryProfileDataDto
     */
    fun SupportBeneficiaryProfileDto.toSupportBeneficiaryDataDto(): SupportBeneficiaryProfileDataDto {
        return SupportBeneficiaryProfileDataDto(
            needs = needs,
            urgentNeeds = urgentNeeds,
            familySize = familySize,
            dependents = dependents,
            householdComposition = householdComposition,
            vulnerabilityFactors = vulnerabilityFactors,
            city = city,
            neighborhood = neighborhood,
            latitude = latitude,
            longitude = longitude,
            landmark = landmark,
            prefersAnonymity = prefersAnonymity,
            languagePreference = languagePreference,
            consentToShare = consentToShare,
            referredBy = referredBy,
            referredByUuid = referredByUuid,
            caseWorkerUuid = caseWorkerUuid
        )
    }

    /**
     * Convert EmployerProfileDto to EmployerProfileDataDto
     */
    fun EmployerProfileDto.toEmployerDataDto(): EmployerProfileDataDto {
        return EmployerProfileDataDto(
            companyName = companyName,
            industry = industry,
            companySize = companySize,
            foundedYear = foundedYear,
            description = description,
            logo = logo,
            preferredSkills = preferredSkills,
            remotePolicy = remotePolicy,
            worksWithAgents = worksWithAgents,
            preferredAgents = preferredAgents,
            businessName = companyName,
            isRegistered = isVerifiedEmployer,
            yearsExperience = null
        )
    }

    /**
     * Convert PropertyOwnerProfileDto to PropertyOwnerProfileDataDto
     */
    fun PropertyOwnerProfileDto.toPropertyOwnerDataDto(): PropertyOwnerProfileDataDto {
        return PropertyOwnerProfileDataDto(
            // New simplified fields
            listingType = listingType,
            isListingForRent = isListingForRent,
            isListingForSale = isListingForSale,
            // Professional details
            isProfessional = isProfessional,
            licenseNumber = licenseNumber,
            companyName = companyName,
            yearsInBusiness = yearsInBusiness,
            // Property details
            propertyCount = propertyCount,
            propertyTypes = propertyTypes,
            preferredPropertyTypes = preferredPropertyTypes,
            serviceAreas = serviceAreas,
            propertyPurpose = propertyPurpose,
            usesAgent = usesAgent,
            managingAgentUuid = managingAgentUuid
        )
    }
}

