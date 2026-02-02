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
        isOnboardingComplete = this.isOnboardingComplete,
        createdAt = this.createdAt
    )
}

/**
 * Maps UserResponseDto (Network) to User (Domain)
 */
fun UserResponseDto.toDomain(): User {
    val type = when (this.account.type.uppercase()) {
        "ORGANIZATION" -> {
            val org = this.organization ?: throw IllegalStateException("Org data missing")
            AccountType.Organization(
                orgUuid = org.uuid,
                orgName = org.name,
                orgType = org.orgType,
                orgEmail = org.officialEmail,
                orgPhone = org.officialPhone,
                orgAddress = org.physicalAddress,
                adminFirstName = org.adminFirstName,
                adminLastName = org.adminLastName
            )
        }
        else -> AccountType.Individual
    }

    return User(
        uuid = this.uuid,
        accountUuid = this.account.uuid,
        firstName = this.firstName ?: "",
        lastName = this.lastName ?: "",
        email = this.email,
        personalPhone = this.phone ?: "",
        accountType = type,
        isVerified = this.status.uppercase() == "ACTIVE",
        isOnboardingComplete = true,
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
        email = this.email,           // Admin email (from root user)
        phone = this.personalPhone,    // Admin phone
        adminFirstName = orgData.adminFirstName,
        adminLastName = orgData.adminLastName,
        password = password,
        code = code
    )
}