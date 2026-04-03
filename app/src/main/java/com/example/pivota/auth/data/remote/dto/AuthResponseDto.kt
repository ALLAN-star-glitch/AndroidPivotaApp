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
   BASE RESPONSE (Matches proto SignupResponse/LoginResponse pattern)
====================================================== */

@Serializable
data class BaseResponseDto<T>(
    @SerialName("success") val success: Boolean,
    @SerialName("message") val message: String,
    @SerialName("code") val code: String,
    @SerialName("data") val data: T? = null,
    @SerialName("error") val error: ErrorPayloadDto? = null
)

@Serializable(with = ErrorPayloadDtoSerializer::class)
data class ErrorPayloadDto(
    val message: String? = null,
    val code: String? = null,
    val details: String? = null
) {
    fun getErrorMessage(): String = message ?: "Unknown error"
}

object ErrorPayloadDtoSerializer : kotlinx.serialization.KSerializer<ErrorPayloadDto> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ErrorPayloadDto")

    override fun deserialize(decoder: Decoder): ErrorPayloadDto {
        val jsonDecoder = decoder as? kotlinx.serialization.json.JsonDecoder
            ?: return ErrorPayloadDto()

        val element = jsonDecoder.decodeJsonElement()
        val jsonObject = element.jsonObject

        // Handle message that could be String or Array
        val message = when (val msgElement = jsonObject["message"]) {
            null -> null
            is JsonPrimitive -> msgElement.content
            else -> {
                // It's an array
                msgElement.jsonArray.joinToString(", ") { it.jsonPrimitive.content }
            }
        }

        return ErrorPayloadDto(
            message = message,
            code = jsonObject["code"]?.jsonPrimitive?.contentOrNull,
            details = jsonObject["details"]?.jsonPrimitive?.contentOrNull
        )
    }

    override fun serialize(encoder: Encoder, value: ErrorPayloadDto) {
        // Not needed for deserialization
    }
}

/* ======================================================
   SIGNUP RESPONSE DATA (Matches proto SignupSuccessData)
====================================================== */

@Serializable
data class SignupSuccessDataDto(
    @SerialName("message") val message: String  // "Signup successful"
)

typealias SignupResponseDto = BaseResponseDto<SignupSuccessDataDto>

/* ======================================================
   LOGIN RESPONSE DATA (Matches proto LoginData)
====================================================== */

@Serializable
data class LoginDataDto(
    // Stage 1: MFA required
    @SerialName("email") val email: String? = null,
    @SerialName("uuid") val uuid: String? = null,
    @SerialName("message") val message: String? = null,  // "MFA_REQUIRED"

    // Stage 2: Tokens
    @SerialName("accessToken") val accessToken: String? = null,
    @SerialName("refreshToken") val refreshToken: String? = null
)

typealias LoginResponseDto = BaseResponseDto<LoginDataDto>

/* ======================================================
   VERIFY OTP RESPONSE (Matches proto VerifyOtpResponse)
====================================================== */

@Serializable
data class VerifyOtpDataDto(
    @SerialName("verified") val verified: Boolean
)

typealias VerifyOtpResponseDto = BaseResponseDto<VerifyOtpDataDto>

/* ======================================================
   BASE OTP RESPONSE (Matches proto BaseOtpResponse)
====================================================== */

typealias BaseOtpResponseDto = BaseResponseDto<Nothing>

/* ======================================================
   REFRESH TOKEN RESPONSE (Matches proto RefreshTokenResponse)
====================================================== */

@Serializable
data class RefreshTokenDataDto(
    @SerialName("accessToken") val accessToken: String,
    @SerialName("refreshToken") val refreshToken: String
)

typealias RefreshTokenResponseDto = BaseResponseDto<RefreshTokenDataDto>