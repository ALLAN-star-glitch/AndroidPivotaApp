package com.example.pivota.auth.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/* ======================================================
   CORE WRAPPERS
====================================================== */

@Serializable
data class BaseResponseDto<T>(
    @SerialName("success") val success: Boolean,
    @SerialName("message") val message: String,
    @SerialName("code") val code: String,
    @SerialName("data") val data: T? = null,
    @SerialName("error") val error: ApiErrorDto? = null
)

@Serializable
data class ApiErrorDto(
    val message: String? = null,
    val code: String? = null,
    val status: Int? = null
)

/* ======================================================
   INDIVIDUAL USER RESPONSE (T = UserResponseDto)
====================================================== */

@Serializable
data class UserResponseDto(
    @SerialName("user") val user: UserDto,
    @SerialName("account") val account: AccountResponseDto,
    @SerialName("profile") val profile: ProfileResponseDto = ProfileResponseDto(),
    @SerialName("completion") val completion: CompletionResponseDto
)

@Serializable
data class UserDto(
    @SerialName("uuid") val uuid: String,
    @SerialName("userCode") val userCode: String,
    @SerialName("firstName") val firstName: String? = null,
    @SerialName("lastName") val lastName: String? = null,
    @SerialName("email") val email: String,
    @SerialName("phone") val phone: String? = null,
    @SerialName("roleName") val roleName: String? = "",
    @SerialName("status") val status: String,
    @SerialName("accessToken") val accessToken: String? = null,
    @SerialName("refreshToken") val refreshToken: String? = null,
    @SerialName("account") val account: AccountResponseDto, /// TODO ,
    @SerialName("organization") val organization: OrganizationResponseDto? = null
)

/* ======================================================
   ORGANIZATION SIGNUP RESPONSE (T = OrganisationSignupDataDto)
====================================================== */

@Serializable
data class OrganisationSignupDataDto(
    @SerialName("organization") val organization: OrgBaseDto,
    @SerialName("admin") val admin: AdminUserResponseDto,
    @SerialName("account") val account: AccountResponseDto
)

@Serializable
data class OrgBaseDto(
    @SerialName("id") val id: String,
    @SerialName("uuid") val uuid: String,
    @SerialName("name") val name: String,
    @SerialName("orgCode") val orgCode: String,
    @SerialName("verificationStatus") val verificationStatus: String
)

@Serializable
data class AdminUserResponseDto(
    @SerialName("uuid") val uuid: String,
    @SerialName("userCode") val userCode: String,
    @SerialName("email") val email: String,
    @SerialName("firstName") val firstName: String,
    @SerialName("lastName") val lastName: String,
    @SerialName("roleName") val roleName: String,
    @SerialName("phone") val phone: String
)

/* ======================================================
   COMMON SHARED COMPONENTS
====================================================== */

@Serializable
data class AccountResponseDto(
    @SerialName("uuid") val uuid: String,
    @SerialName("accountCode") val accountCode: String,
    @SerialName("type") val type: String // "INDIVIDUAL" | "ORGANIZATION"
)

@Serializable
class ProfileResponseDto // Empty object in JSON

@Serializable
data class CompletionResponseDto(
    @SerialName("percentage") val percentage: Int,
    @SerialName("missingFields") val missingFields: List<String>,
    @SerialName("isComplete") val isComplete: Boolean
)