package com.example.pivota.auth.data.remote

import com.example.pivota.auth.data.remote.dto.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class AuthApiService(private val client: HttpClient) {

    /**
     * Stage 1: Request OTP
     * Triggers the backend engine to send a 6-digit code.
     * For login, the purpose passed here will be "2FA".
     */
    suspend fun requestOtp(email: String, purpose: String): BaseResponseDto<Unit?> {
        return client.post("v1/auth-module/otp/request") {
            contentType(ContentType.Application.Json)
            setBody(RequestOtpRequest(
                email = email,
                purpose = purpose
            ))
        }.body()
    }

    /**
     * Stage 2: Individual Registration
     * Verified by OTP code.
     */
    suspend fun signupIndividual(request: UserSignupRequestDto): BaseResponseDto<UserDto> {
        return client.post("auth/signup") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    /**
     * Stage 2: Organization Registration
     * Verified by OTP code.
     */
    suspend fun signupOrganization(request: OrganisationSignupRequestDto): BaseResponseDto<UserDto> {
        return client.post("auth/organisation-signup") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    /**
     * Stage 2: Login (MFA/2FA)
     * Matches backend 'verifyMfaLogin'.
     * This is the single call after requesting a "2FA" OTP that authenticates the user.
     */
    suspend fun verifyLoginOtp(request: VerifyLoginOtpDto): BaseResponseDto<UserDto> {
        return client.post("auth/verify-mfa") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
}