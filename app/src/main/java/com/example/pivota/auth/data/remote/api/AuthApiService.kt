package com.example.pivota.auth.data.remote.api

import com.example.pivota.auth.data.remote.dto.BaseOtpResponseDto
import com.example.pivota.auth.data.remote.dto.BaseResponseDto
import com.example.pivota.auth.data.remote.dto.GoogleSignInRequestDto
import com.example.pivota.auth.data.remote.dto.LoginRequestDto
import com.example.pivota.auth.data.remote.dto.LoginResponseDto
import com.example.pivota.auth.data.remote.dto.ProfileResponseDto
import com.example.pivota.auth.data.remote.dto.RefreshTokenResponseDto
import com.example.pivota.auth.data.remote.dto.RequestOtpRequestDto
import com.example.pivota.auth.data.remote.dto.SignupRequestDto
import com.example.pivota.auth.data.remote.dto.SignupResponseDto
import com.example.pivota.auth.data.remote.dto.VerifyMfaLoginRequestDto
import com.example.pivota.auth.data.remote.dto.VerifyOtpRequestDto
import com.example.pivota.auth.data.remote.dto.VerifyOtpResponseDto
import com.example.pivota.core.di.UnauthHttpClient
import com.example.pivota.core.network.NetworkConstants
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import javax.inject.Inject

class AuthApiService @Inject constructor(
    @param:UnauthHttpClient private val client: HttpClient
) {

    /**
     * Request OTP for signup, login, or password reset
     */
    suspend fun requestOtp(request: RequestOtpRequestDto): BaseOtpResponseDto {
        val requestBody = mutableMapOf<String, String>("email" to request.email)
        request.phone?.let { requestBody["phone"] = it }

        println("🔍 ========== OTP REQUEST ==========")
        println("🔍 URL: ${NetworkConstants.BASE_URL}/authentication/otp/request?purpose=${request.purpose}")
        println("🔍 BODY: email=${request.email}${request.phone?.let { ", phone=$it" } ?: ""}")
        println("🔍 ==================================")

        return try {
            val response: BaseOtpResponseDto = client.post("authentication/otp/request?purpose=${request.purpose}") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

            println("🔍 ========== OTP RESPONSE ==========")
            println("🔍 SUCCESS: ${response.success}")
            println("🔍 MESSAGE: ${response.message}")
            println("🔍 CODE: ${response.code}")
            println("🔍 ERROR: ${response.error}")
            println("🔍 ===================================")

            response
        } catch (e: ClientRequestException) {
            println("❌ OTP Request Client Error (${e.response.status.value}): ${e.message}")
            val errorBody = try { e.response.bodyAsText() } catch (ex: Exception) { "Unable to read error body" }
            println("❌ Error Body: $errorBody")
            throw e
        } catch (e: ServerResponseException) {
            println("❌ OTP Request Server Error (${e.response.status.value}): ${e.message}")
            throw e
        } catch (e: Exception) {
            println("❌ OTP Request Failed: ${e.message}")
            throw e
        }
    }

    /**
     * Verify OTP code
     */
    suspend fun verifyOtp(request: VerifyOtpRequestDto): VerifyOtpResponseDto {
        return try {
            client.post("authentication/otp/verify") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body()
        } catch (e: Exception) {
            println("❌ Verify OTP Failed: ${e.message}")
            throw e
        }
    }

    /**
     * Individual Signup
     */
    suspend fun signup(request: SignupRequestDto): SignupResponseDto {
        val jsonString = NetworkConstants.JsonProvider.json.encodeToString(request)
        println("🔍 SIGNUP Request: $jsonString")

        return try {
            client.post("authentication-onboarding/signup") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body()
        } catch (e: Exception) {
            println("❌ Signup Failed: ${e.message}")
            throw e
        }
    }

    /**
     * User Login (Stage 1 - returns MFA_REQUIRED or tokens)
     */
    suspend fun login(request: LoginRequestDto): LoginResponseDto {
        println("🔍 ========== LOGIN REQUEST ==========")
        println("🔍 URL: ${NetworkConstants.BASE_URL}/authentication/login")
        println("🔍 BODY: email=${request.email}, password=${request.password}")
        println("🔍 ====================================")

        return try {
            val response: LoginResponseDto = client.post("authentication/login") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body()

            println("🔍 ========== LOGIN RESPONSE ==========")
            println("🔍 SUCCESS: ${response.success}")
            println("🔍 MESSAGE: ${response.message}")
            println("🔍 CODE: ${response.code}")
            println("🔍 DATA: ${response.data}")
            println("🔍 ERROR: ${response.error}")
            println("🔍 ====================================")

            response
        } catch (e: ClientRequestException) {
            println("❌ Login Client Error (${e.response.status.value}): ${e.message}")
            val errorBody = try { e.response.bodyAsText() } catch (ex: Exception) { "Unable to read error body" }
            println("❌ Error Body: $errorBody")
            throw e
        } catch (e: ServerResponseException) {
            println("❌ Login Server Error (${e.response.status.value}): ${e.message}")
            throw e
        } catch (e: Exception) {
            println("❌ Login Failed: ${e.message}")
            throw e
        }
    }

    /**
     * Verify MFA and complete login (Stage 2)
     */
    suspend fun verifyMfaLogin(request: VerifyMfaLoginRequestDto): LoginResponseDto {
        println("🔍 ========== VERIFY MFA LOGIN REQUEST ==========")
        println("🔍 URL: ${NetworkConstants.BASE_URL}/authentication/login/verify-mfa")
        println("🔍 BODY: email=${request.email}, code=${request.code}")
        println("🔍 ===============================================")

        return try {
            val response: LoginResponseDto = client.post("authentication/login/verify-mfa") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body()

            println("🔍 ========== VERIFY MFA LOGIN RESPONSE ==========")
            println("🔍 SUCCESS: ${response.success}")
            println("🔍 MESSAGE: ${response.message}")
            println("🔍 CODE: ${response.code}")
            println("🔍 DATA: ${response.data}")
            println("🔍 ERROR: ${response.error}")
            println("🔍 ===============================================")

            response
        } catch (e: Exception) {
            println("❌ Verify MFA Login Failed: ${e.message}")
            throw e
        }
    }

    // ======================================================
    // GOOGLE SIGN-IN
    // ======================================================

    /**
     * Google Sign-In - Login or Register using Google OAuth token
     */
    suspend fun googleSignIn(request: GoogleSignInRequestDto): LoginResponseDto {
        println("🔍 ========== GOOGLE SIGN-IN REQUEST ==========")
        println("🔍 URL: ${NetworkConstants.BASE_URL}/authentication/google")
        println("🔍 TOKEN: ${request.token.take(20)}...")
        println("🔍 HAS ONBOARDING DATA: ${request.onboardingData != null}")
        request.onboardingData?.primaryPurpose?.let {
            println("🔍 PRIMARY PURPOSE: $it")
        }
        println("🔍 ============================================")

        return try {
            val response: LoginResponseDto = client.post("authentication/google") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body()

            println("🔍 ========== GOOGLE SIGN-IN RESPONSE ==========")
            println("🔍 SUCCESS: ${response.success}")
            println("🔍 MESSAGE: ${response.message}")
            println("🔍 CODE: ${response.code}")
            println("🔍 HAS TOKENS: ${response.data?.accessToken != null}")
            println("🔍 =============================================")

            response
        } catch (e: ClientRequestException) {
            println("❌ Google Sign-In Client Error (${e.response.status.value}): ${e.message}")
            val errorBody = try { e.response.bodyAsText() } catch (ex: Exception) { "Unable to read error body" }
            println("❌ Error Body: $errorBody")
            throw e
        } catch (e: ServerResponseException) {
            println("❌ Google Sign-In Server Error (${e.response.status.value}): ${e.message}")
            throw e
        } catch (e: Exception) {
            println("❌ Google Sign-In Failed: ${e.message}")
            throw e
        }
    }

    /**
     * Refresh access token
     */
    suspend fun refreshToken(refreshToken: String): RefreshTokenResponseDto {
        return try {
            client.post("authentication/refresh-token") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("refreshToken" to refreshToken))
            }.body()
        } catch (e: Exception) {
            println("❌ Refresh Token Failed: ${e.message}")
            throw e
        }
    }

    /**
     * Request password reset
     */
    suspend fun requestPasswordReset(email: String): BaseOtpResponseDto {
        println("🔍 ========== PASSWORD RESET REQUEST ==========")
        println("🔍 URL: ${NetworkConstants.BASE_URL}/authentication/password/forgot")
        println("🔍 BODY: email=$email")
        println("🔍 ============================================")

        return try {
            val response: BaseOtpResponseDto = client.post("authentication/password/forgot") {
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
            println("❌ Password Reset Request Failed: ${e.message}")
            throw e
        }
    }

    /**
     * Reset password with OTP
     */
    suspend fun resetPassword(email: String, code: String, newPassword: String): BaseResponseDto<Nothing> {
        println("🔍 ========== RESET PASSWORD REQUEST ==========")
        println("🔍 URL: ${NetworkConstants.BASE_URL}/authentication/password/reset")
        println("🔍 BODY: email=$email, code=$code, newPassword=${"*".repeat(newPassword.length)}")
        println("🔍 ============================================")

        return try {
            val response: BaseResponseDto<Nothing> = client.post("authentication/password/reset") {
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
            println("❌ Reset Password Failed: ${e.message}")
            throw e
        }
    }
}