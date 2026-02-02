package com.example.pivota.auth.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
    @SerialName("roleName") val roleName: String,
    @SerialName("status") val status: String
)

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
