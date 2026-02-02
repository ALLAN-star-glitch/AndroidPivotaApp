package com.example.pivota.auth.data.mapper

import com.example.pivota.auth.data.remote.dto.*
import com.example.pivota.auth.domain.model.AccountType
import com.example.pivota.auth.domain.model.User
import com.example.pivota.core.database.entity.UserEntity

/**
 * Maps User (Domain) to UserEntity (Database/Room)
 */
fun User.toEntity(): UserEntity {
    val orgData = this.accountType as? AccountType.Organization

    return UserEntity(
        uuid = this.uuid,
        accountUuid = this.accountUuid,
        firstName = this.firstName,
        lastName = this.lastName,
        email = this.email,
        personalPhone = this.personalPhone,
        accountType = if (this.accountType is AccountType.Organization) "ORGANIZATION" else "INDIVIDUAL",
        orgUuid = orgData?.orgUuid,
        orgName = orgData?.orgName,
        orgType = orgData?.orgType,
        officialEmail = orgData?.orgEmail,
        officialPhone = orgData?.orgPhone,
        physicalAddress = orgData?.orgAddress,
        adminFirstName = orgData?.adminFirstName,
        adminLastName = orgData?.adminLastName,
        isVerified = this.isVerified,
        selectedPlan = this.selectedPlan?.name,
        // Aligned with the 'isComplete' flag from backend completion DTO
        isOnboardingComplete = this.isOnboardingComplete,
        createdAt = this.createdAt
    )
}

/**
 * Maps UserDto + Account + Completion (Network) to User (Domain)
 * Updated to match the new nested backend structure
 */
fun UserDto.toDomain(
    account: AccountResponseDto,
    completion: CompletionResponseDto
): User {
    val type = when (account.type.uppercase()) {
        "ORGANIZATION" -> {
            // Note: If Organization-specific fields come in 'profile' or 'user',
            // map them here. Currently setting defaults for MVP1.
            AccountType.Organization(
                orgUuid = account.uuid,
                orgName = "", // Map from profile if available
                orgType = "",
                orgEmail = this.email,
                orgPhone = this.phone ?: "",
                orgAddress = "",
                adminFirstName = this.firstName ?: "",
                adminLastName = this.lastName ?: ""
            )
        }
        else -> AccountType.Individual
    }

    return User(
        uuid = this.uuid,
        accountUuid = account.uuid,
        firstName = this.firstName ?: "",
        lastName = this.lastName ?: "",
        email = this.email,
        personalPhone = this.phone ?: "",
        accountType = type,
        isVerified = this.status.uppercase() == "ACTIVE",
        // Logic: Account is fully onboarded if backend says isComplete is true
        isOnboardingComplete = completion.isComplete,
        // You can also add completion.percentage to the User model if needed
        createdAt = System.currentTimeMillis()
    )
}

/**
 * Extension: Maps User to Individual Signup Request DTO
 */
fun User.toIndividualRequest(code: String, password: String): UserSignupRequestDto {
    return UserSignupRequestDto(
        email = this.email,
        password = password,
        firstName = this.firstName,
        lastName = this.lastName,
        phone = this.personalPhone.ifEmpty { null },
        code = code
    )
}

/**
 * Extension: Maps User to Organization Signup Request DTO
 */
fun User.toOrganisationRequest(code: String, password: String): OrganisationSignupRequestDto {
    val orgData = this.accountType as? AccountType.Organization
        ?: throw IllegalArgumentException("User account type is not Organization")

    return OrganisationSignupRequestDto(
        name = orgData.orgName,
        orgType = orgData.orgType,
        officialEmail = orgData.orgEmail,
        officialPhone = orgData.orgPhone,
        physicalAddress = orgData.orgAddress,
        email = this.email,           // Admin email
        phone = this.personalPhone,    // Admin phone
        adminFirstName = orgData.adminFirstName,
        adminLastName = orgData.adminLastName,
        password = password,
        code = code
    )
}