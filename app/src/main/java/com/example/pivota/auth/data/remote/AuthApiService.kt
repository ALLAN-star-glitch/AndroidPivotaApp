package com.example.pivota.auth.data.remote

import com.example.pivota.auth.data.remote.dto.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import javax.inject.Inject

class AuthApiService @Inject constructor(
    private val client: HttpClient
) {

    /**
     * Stage 1: Request OTP
     * Accepts the RequestOtpRequest DTO as called by the Repository.
     */
    suspend fun requestOtp(request: RequestOtpRequest): BaseResponseDto<Unit?> {
        return client.post("v1/auth-module/otp/request") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    /**
     * Stage 2: Individual Registration
     * Maps to the individual signup endpoint.
     */
    suspend fun signupIndividual(request: UserSignupRequestDto): BaseResponseDto<UserResponseDto> {
        return client.post("v1/auth-module/signup") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    /**
     * Stage 2: Organization Registration
     * Maps to the organization signup endpoint.
     */
    suspend fun signupOrganization(request: OrganisationSignupRequestDto): BaseResponseDto<UserResponseDto> {
        return client.post("v1/auth-module/auth/organisation-signup") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    /**
     * Stage 2: Login (MFA/2FA)
     * Final step of the login flow.
     */
    suspend fun verifyLoginOtp(request: VerifyLoginOtpDto): BaseResponseDto<UserResponseDto> {
        return client.post("v1/auth-module/login/verify-mfa") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    /**
     * User login
     */
    suspend fun login(request: LoginRequestDto): BaseResponseDto<Unit> {
        return client.post("v1/auth-module/login") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
}