package com.example.pivota.auth.data.remote.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

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

@Serializable
data class UserResponseDto(
    @SerialName("uuid") val uuid: String,
    @SerialName("userCode") val userCode: String,
    @SerialName("email") val email: String,
    @SerialName("firstName") val firstName: String? = null,
    @SerialName("lastName") val lastName: String? = null,
    @SerialName("phone") val phone: String? = null,
    @SerialName("roleName") val roleName: String,
    @SerialName("status") val status: String,
    @SerialName("accessToken") val accessToken: String? = null,
    @SerialName("refreshToken") val refreshToken: String? = null,
    @SerialName("account") val account: AccountResponseDto,
    @SerialName("organization") val organization: OrganizationResponseDto? = null
)

@Serializable
data class AccountResponseDto(
    @SerialName("uuid") val uuid: String,
    @SerialName("type") val type: String, // "INDIVIDUAL" | "ORGANIZATION"
    @SerialName("accountCode") val accountCode: String
)

@Serializable
data class OrganizationResponseDto(
    @SerialName("uuid") val uuid: String,
    @SerialName("name") val name: String,
    @SerialName("orgType") val orgType: String,           // e.g., NGO, Company, Institution
    @SerialName("officialEmail") val officialEmail: String,
    @SerialName("officialPhone") val officialPhone: String? = null,
    @SerialName("physicalAddress") val physicalAddress: String,
    @SerialName("adminFirstName") val adminFirstName: String,
    @SerialName("adminLastName") val adminLastName: String
)
