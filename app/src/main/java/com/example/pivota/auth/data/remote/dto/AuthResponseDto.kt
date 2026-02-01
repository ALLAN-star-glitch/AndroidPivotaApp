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
data class UserDto(
    @SerialName("uuid") val uuid: String,
    @SerialName("userCode") val userCode: String,
    @SerialName("email") val email: String,
    @SerialName("firstName") val firstName: String? = null,
    @SerialName("lastName") val lastName: String? = null,
    @SerialName("phone") val phone: String? = null,
    @SerialName("roleName") val roleName: String,
    @SerialName("status") val status: String,

    // Tokens are returned in the LoginResponse or directly in certain flows
    @SerialName("accessToken") val accessToken: String? = null,
    @SerialName("refreshToken") val refreshToken: String? = null,

    // Relation to Account (The Trust Anchor)
    @SerialName("account") val account: AccountDto,

    // Nested Organization (Only present for Org signups)
    @SerialName("organization") val organization: OrganizationDto? = null
)

@Serializable
data class AccountDto(
    @SerialName("uuid") val uuid: String,
    @SerialName("type") val type: String, // "INDIVIDUAL" | "ORGANIZATION"
    @SerialName("accountCode") val accountCode: String
)

@Serializable
data class OrganizationDto(
    @SerialName("uuid") val uuid: String,
    @SerialName("name") val name: String,
    @SerialName("orgCode") val orgCode: String,
    @SerialName("verificationStatus") val verificationStatus: String
)

@Serializable
data class RequestOtpRequest(
    @SerialName("email") val email: String,
    @SerialName("purpose") val purpose: String
)

@Serializable
data class UserSignupRequestDto(
    @SerialName("email") val email: String,
    @SerialName("password") val password: String,
    @SerialName("firstName") val firstName: String,
    @SerialName("lastName") val lastName: String,
    @SerialName("phone") val phone: String?,
    @SerialName("code") val code: String // The 6-digit OTP
)

@Serializable
data class OrganisationSignupRequestDto(
    @SerialName("name") val name: String,
    @SerialName("officialEmail") val officialEmail: String,
    @SerialName("officialPhone") val officialPhone: String,
    @SerialName("physicalAddress") val physicalAddress: String,
    @SerialName("email") val email: String, // Admin email
    @SerialName("phone") val phone: String, // Admin phone
    @SerialName("adminFirstName") val adminFirstName: String,
    @SerialName("adminLastName") val adminLastName: String,
    @SerialName("password") val password: String,
    @SerialName("code") val code: String // The 6-digit OTP
)

@Serializable
data class LoginRequestDto(
    @SerialName("email") val email: String,
    @SerialName("password") val password: String
)

@Serializable
data class VerifyLoginOtpDto(
    @SerialName("email") val email: String,
    @SerialName("code") val code: String // The 6-digit OTP sent for 2FA
)