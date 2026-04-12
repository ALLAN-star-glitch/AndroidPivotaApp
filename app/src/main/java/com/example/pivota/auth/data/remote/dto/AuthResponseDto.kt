package com.example.pivota.auth.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor

/* ======================================================
   BASE RESPONSE DTO (Matches backend BaseResponseDto)
====================================================== */

@Serializable
data class BaseResponseDto<T>(
    @SerialName("success") val success: Boolean,
    @SerialName("message") val message: String,
    @SerialName("code") val code: String,  // String in backend, but can be number at gateway
    @SerialName("data") val data: T? = null,
    @SerialName("error") val error: ErrorPayloadDto? = null
) {
    companion object {
        fun <T> ok(data: T, message: String = "Success", code: String = "OK"): BaseResponseDto<T> {
            return BaseResponseDto(
                success = true,
                message = message,
                code = code,
                data = data,
                error = null
            )
        }

        fun fail(message: String, code: String = "INTERNAL_ERROR", details: Any? = null): BaseResponseDto<Nothing> {
            return BaseResponseDto(
                success = false,
                message = message,
                code = code,
                data = null,
                error = ErrorPayloadDto(
                    message = message,
                    code = code,
                    details = details
                )
            )
        }
    }
}

@Serializable(with = ErrorPayloadDtoSerializer::class)
data class ErrorPayloadDto(
    val message: String,
    val code: String? = null,
    val details: Any? = null
)

object ErrorPayloadDtoSerializer : kotlinx.serialization.KSerializer<ErrorPayloadDto> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ErrorPayloadDto")

    override fun deserialize(decoder: Decoder): ErrorPayloadDto {
        val jsonDecoder = decoder as? kotlinx.serialization.json.JsonDecoder
            ?: return ErrorPayloadDto(message = "Unknown error")

        val element = jsonDecoder.decodeJsonElement()
        val jsonObject = element.jsonObject

        val message = when (val msgElement = jsonObject["message"]) {
            null -> "Unknown error"
            is JsonPrimitive -> msgElement.content
            else -> {
                msgElement.jsonArray.joinToString(", ") { it.jsonPrimitive.content }
            }
        }

        return ErrorPayloadDto(
            message = message,
            code = jsonObject["code"]?.jsonPrimitive?.contentOrNull,
            details = jsonObject["details"]
        )
    }

    override fun serialize(encoder: Encoder, value: ErrorPayloadDto) {
        // Not needed for deserialization
    }
}

/* ======================================================
   BASE OTP RESPONSE (Matches backend BaseOtpResponse)
====================================================== */
typealias BaseOtpResponseDto = BaseResponseDto<Nothing>

/* ======================================================
   VERIFY OTP RESPONSE (Matches backend VerifyOtpResponse)
====================================================== */

@Serializable
data class VerifyOtpDataDto(
    @SerialName("verified") val verified: Boolean
)

typealias VerifyOtpResponseDto = BaseResponseDto<VerifyOtpDataDto>

/* ======================================================
   SIGNUP RESPONSE (Matches backend SignupResponse)
   Updated to support auto-login with tokens
====================================================== */

@Serializable
data class SignupSuccessDataDto(
    @SerialName("message") val message: String,  // "Signup successful"

    // For auto-login (free plan)
    @SerialName("accessToken") val accessToken: String? = null,
    @SerialName("refreshToken") val refreshToken: String? = null,
    @SerialName("redirectTo") val redirectTo: String? = null,  // "/dashboard"

    // For premium payment required
    @SerialName("redirectUrl") val redirectUrl: String? = null,
    @SerialName("merchantReference") val merchantReference: String? = null
)

typealias SignupResponseDto = BaseResponseDto<SignupSuccessDataDto>
/* ======================================================
   LOGIN RESPONSE (Matches backend LoginResponse)
   Used for both Login and VerifyMfaLogin
====================================================== */

@Serializable
data class LoginDataDto(
    // Stage 1: MFA required
    @SerialName("email") val email: String? = null,
    @SerialName("uuid") val uuid: String? = null,
    @SerialName("message") val message: String? = null,

    // Stage 2: Tokens
    @SerialName("accessToken") val accessToken: String? = null,
    @SerialName("refreshToken") val refreshToken: String? = null,

    // JWT Payload fields
    @SerialName("userUuid") val userUuid: String? = null,
    @SerialName("userName") val userName: String? = null,
    @SerialName("firstName") val firstName: String? = null,
    @SerialName("lastName") val lastName: String? = null,
    @SerialName("phone") val phone: String? = null,
    @SerialName("profileImage") val profileImage: String? = null,
    @SerialName("primaryPurpose") val primaryPurpose: String? = null,
    @SerialName("accountId") val accountId: String? = null,
    @SerialName("accountName") val accountName: String? = null,
    @SerialName("accountType") val accountType: String? = null,
    @SerialName("tokenId") val tokenId: String? = null,
    @SerialName("role") val role: String? = null,
    @SerialName("organizationUuid") val organizationUuid: String? = null,
    @SerialName("planSlug") val planSlug: String? = null
)

typealias LoginResponseDto = BaseResponseDto<LoginDataDto>

/* ======================================================
   REFRESH TOKEN RESPONSE (Matches backend BaseRefreshTokenResponse)
====================================================== */

@Serializable
data class RefreshTokenDataDto(
    @SerialName("accessToken") val accessToken: String,
    @SerialName("refreshToken") val refreshToken: String
)

typealias RefreshTokenResponseDto = BaseResponseDto<RefreshTokenDataDto>

/* ======================================================
   DEV TOKEN RESPONSE (Matches backend BaseDevTokenResponse)
====================================================== */

typealias BaseDevTokenResponseDto = BaseResponseDto<RefreshTokenDataDto>

/* ======================================================
   GET ACTIVE SESSIONS RESPONSE (Matches backend GetActiveSessionsResponse)
====================================================== */

@Serializable
data class SessionDto(
    @SerialName("id") val id: Int,
    @SerialName("tokenId") val tokenId: String,
    @SerialName("device") val device: String? = null,
    @SerialName("ipAddress") val ipAddress: String? = null,
    @SerialName("userAgent") val userAgent: String? = null,
    @SerialName("os") val os: String? = null,
    @SerialName("lastActiveAt") val lastActiveAt: String,
    @SerialName("expiresAt") val expiresAt: String,
    @SerialName("createdAt") val createdAt: String,
    @SerialName("revoked") val revoked: Boolean
)

typealias GetActiveSessionsResponseDto = BaseResponseDto<List<SessionDto>>

/* ======================================================
   GOOGLE SIGN-IN REQUEST (Matches proto GoogleLoginRequest)
   Note: clientInfo is handled by the gateway decorator, not passed in body
====================================================== */

@Serializable
data class GoogleSignInRequestDto(
    @SerialName("token") val token: String,
    @SerialName("onboardingData") val onboardingData: GoogleOnboardingDataDto? = null
)

/* ======================================================
   GOOGLE ONBOARDING DATA (Matches proto GoogleOnboardingData)
====================================================== */

@Serializable
data class GoogleOnboardingDataDto(
    @SerialName("primaryPurpose") val primaryPurpose: String? = null,
    @SerialName("jobSeekerData") val jobSeekerData: JobSeekerProfileDataDto? = null,
    @SerialName("housingSeekerData") val housingSeekerData: HousingSeekerProfileDataDto? = null,
    @SerialName("skilledProfessionalData") val skilledProfessionalData: SkilledProfessionalProfileDataDto? = null,
    @SerialName("intermediaryAgentData") val intermediaryAgentData: IntermediaryAgentProfileDataDto? = null,
    @SerialName("supportBeneficiaryData") val supportBeneficiaryData: SupportBeneficiaryProfileDataDto? = null,
    @SerialName("employerData") val employerData: EmployerProfileDataDto? = null,
    @SerialName("propertyOwnerData") val propertyOwnerData: PropertyOwnerProfileDataDto? = null
)