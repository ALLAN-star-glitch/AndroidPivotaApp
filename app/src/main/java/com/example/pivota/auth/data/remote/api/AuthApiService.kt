package com.example.pivota.auth.data.remote.api

import com.example.pivota.auth.data.remote.dto.BaseOtpResponseDto
import com.example.pivota.auth.data.remote.dto.BaseResponseDto
import com.example.pivota.auth.data.remote.dto.LoginRequestDto
import com.example.pivota.auth.data.remote.dto.LoginResponseDto
import com.example.pivota.auth.data.remote.dto.RefreshTokenResponseDto
import com.example.pivota.auth.data.remote.dto.RequestOtpRequestDto
import com.example.pivota.auth.data.remote.dto.SignupRequestDto
import com.example.pivota.auth.data.remote.dto.SignupResponseDto
import com.example.pivota.auth.data.remote.dto.VerifyMfaLoginRequestDto
import com.example.pivota.auth.data.remote.dto.VerifyOtpRequestDto
import com.example.pivota.auth.data.remote.dto.VerifyOtpResponseDto
import com.example.pivota.core.network.NetworkConstants
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import javax.inject.Inject

class AuthApiService @Inject constructor(
    private val client: HttpClient
) {

    /**
     * Request OTP for signup, login, or password reset
     */
    suspend fun requestOtp(request: RequestOtpRequestDto): BaseOtpResponseDto {
        // Build request body - only include phone if it's provided
        val requestBody = mutableMapOf<String, String>("email" to request.email)
        request.phone?.let { requestBody["phone"] = it }

        // Log REQUEST
        println("🔍 ========== OTP REQUEST ==========")
        println("🔍 URL: ${NetworkConstants.BASE_URL}/v1/auth-module/otp/request?purpose=${request.purpose}")
        println("🔍 BODY: email=${request.email}${request.phone?.let { ", phone=$it" } ?: ""}")
        println("🔍 ==================================")

        val response: BaseOtpResponseDto = client.post("v1/auth-module/otp/request?purpose=${request.purpose}") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()

        // Log RESPONSE
        println("🔍 ========== OTP RESPONSE ==========")
        println("🔍 SUCCESS: ${response.success}")
        println("🔍 MESSAGE: ${response.message}")
        println("🔍 CODE: ${response.code}")
        println("🔍 ERROR: ${response.error}")
        println("🔍 ===================================")

        return response
    }

    /**
     * Verify OTP code
     */
    suspend fun verifyOtp(request: VerifyOtpRequestDto): VerifyOtpResponseDto {
        return client.post("v1/auth-module/otp/verify") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    /**
     * Individual Signup
     */
    suspend fun signup(request: SignupRequestDto): SignupResponseDto {
        val jsonString = NetworkConstants.JsonProvider.json.encodeToString(request)
        println("🔍 SIGNUP Request: $jsonString")

        return client.post("v1/auth-module/signup") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    /**
     * User Login (Stage 1 - returns MFA_REQUIRED or tokens)
     */
    suspend fun login(request: LoginRequestDto): LoginResponseDto {
        // Log REQUEST
        println("🔍 ========== LOGIN REQUEST ==========")
        println("🔍 URL: ${NetworkConstants.BASE_URL}/v1/auth-module/login")
        println("🔍 BODY: email=${request.email}, password=${request.password}")
        println("🔍 ====================================")

        return try {
            val response: LoginResponseDto = client.post("v1/auth-module/login") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body()

            // Log RESPONSE
            println("🔍 ========== LOGIN RESPONSE ==========")
            println("🔍 SUCCESS: ${response.success}")
            println("🔍 MESSAGE: ${response.message}")
            println("🔍 CODE: ${response.code}")
            println("🔍 DATA: ${response.data}")
            println("🔍 ERROR: ${response.error}")
            println("🔍 ====================================")

            response
        } catch (e: Exception) {
            println("🔍 ========== LOGIN ERROR ==========")
            println("🔍 ERROR: ${e.message}")
            e.printStackTrace()
            println("🔍 =================================")
            throw e
        }
    }

    /**
     * Verify MFA and complete login (Stage 2)
     */
    suspend fun verifyMfaLogin(request: VerifyMfaLoginRequestDto): LoginResponseDto {
        // Log REQUEST
        println("🔍 ========== VERIFY MFA LOGIN REQUEST ==========")
        println("🔍 URL: ${NetworkConstants.BASE_URL}/v1/auth-module/login/verify-mfa")
        println("🔍 BODY: email=${request.email}, code=${request.code}")
        println("🔍 ===============================================")

        return try {
            val response: LoginResponseDto = client.post("v1/auth-module/login/verify-mfa") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body()

            // Log RESPONSE
            println("🔍 ========== VERIFY MFA LOGIN RESPONSE ==========")
            println("🔍 SUCCESS: ${response.success}")
            println("🔍 MESSAGE: ${response.message}")
            println("🔍 CODE: ${response.code}")
            println("🔍 DATA: ${response.data}")
            println("🔍 ERROR: ${response.error}")
            println("🔍 ===============================================")

            response
        } catch (e: Exception) {
            println("🔍 ========== VERIFY MFA LOGIN ERROR ==========")
            println("🔍 ERROR: ${e.message}")
            e.printStackTrace()
            println("🔍 ============================================")
            throw e
        }
    }

    /**
     * Refresh access token
     */
    suspend fun refreshToken(refreshToken: String): RefreshTokenResponseDto {
        return client.post("v1/auth-module/refresh") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("refreshToken" to refreshToken))
        }.body()
    }

    /**
     * Request password reset
     */
    suspend fun requestPasswordReset(email: String): BaseOtpResponseDto {
        println("🔍 ========== PASSWORD RESET REQUEST ==========")
        println("🔍 URL: ${NetworkConstants.BASE_URL}/v1/auth-module/password/forgot")
        println("🔍 BODY: email=$email")
        println("🔍 ============================================")

        return try {
            val response: BaseOtpResponseDto = client.post("v1/auth-module/password/forgot") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("email" to email))
            }.body()

            println("🔍 ========== PASSWORD RESET RESPONSE ==========")
            println("🔍 SUCCESS: ${response.success}")
            println("🔍 MESSAGE: ${response.message}")
            println("🔍 CODE: ${response.code}")
            println("🔍 ERROR: ${response.error}")
            println("🔍 =============================================")

            response
        } catch (e: Exception) {
            println("🔍 ========== PASSWORD RESET ERROR ==========")
            println("🔍 ERROR: ${e.message}")
            e.printStackTrace()
            println("🔍 ==========================================")
            throw e
        }
    }

    /**
     * Reset password with OTP
     */
    suspend fun resetPassword(email: String, code: String, newPassword: String): BaseResponseDto<Nothing> {
        println("🔍 ========== RESET PASSWORD REQUEST ==========")
        println("🔍 URL: ${NetworkConstants.BASE_URL}/v1/auth-module/password/reset")
        println("🔍 BODY: email=$email, code=$code, newPassword=${"*".repeat(newPassword.length)}")
        println("🔍 ============================================")

        return try {
            val response: BaseResponseDto<Nothing> = client.post("v1/auth-module/password/reset") {
                contentType(ContentType.Application.Json)
                setBody(
                    mapOf(
                        "email" to email,
                        "code" to code,
                        "newPassword" to newPassword
                    )
                )
            }.body()

            println("🔍 ========== RESET PASSWORD RESPONSE ==========")
            println("🔍 SUCCESS: ${response.success}")
            println("🔍 MESSAGE: ${response.message}")
            println("🔍 CODE: ${response.code}")
            println("🔍 ERROR: ${response.error}")
            println("🔍 =============================================")

            response
        } catch (e: Exception) {
            println("🔍 ========== RESET PASSWORD ERROR ==========")
            println("🔍 ERROR: ${e.message}")
            e.printStackTrace()
            println("🔍 ==========================================")
            throw e
        }
    }

    /**
     * Logout user
     */
    suspend fun logout(refreshToken: String): BaseResponseDto<Nothing> {
        return client.post("v1/auth-module/logout") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("refreshToken" to refreshToken))
        }.body()
    }
}