package com.example.pivota.auth.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequestOtpRequest(
    @SerialName("email") val email: String,
    @SerialName("purpose") val purpose: String // e.g., "SIGNUP", "FORGOT_PASSWORD", "LOGIN"
)

@Serializable
data class LoginRequestDto(
    @SerialName("email") val email: String,
    @SerialName("password") val password: String
)

@Serializable
data class VerifyLoginOtpDto(
    @SerialName("email") val email: String,
    @SerialName("code") val code: String, // 2FA code
    @SerialName("purpose") val purpose: String // e.g., "SIGNUP", "FORGOT_PASSWORD", "2FA"
)

@Serializable
data class UserSignupRequestDto(
    @SerialName("email") val email: String,
    @SerialName("password") val password: String,
    @SerialName("firstName") val firstName: String,
    @SerialName("lastName") val lastName: String,
    @SerialName("phone") val phone: String?,
    @SerialName("code") val code: String // The 6-digit OTP from the signup flow
)

@Serializable
data class OrganisationSignupRequestDto(
    @SerialName("name") val name: String,
    @SerialName("orgType") val orgType: String,
    @SerialName("officialEmail") val officialEmail: String,
    @SerialName("officialPhone") val officialPhone: String? = null,
    @SerialName("physicalAddress") val physicalAddress: String,
    @SerialName("email") val email: String,                  // Admin's email
    @SerialName("phone") val phone: String,                  // Admin's phone
    @SerialName("adminFirstName") val adminFirstName: String,
    @SerialName("adminLastName") val adminLastName: String,
    @SerialName("password") val password: String,
    @SerialName("code") val code: String                     // The 6-digit OTP
)