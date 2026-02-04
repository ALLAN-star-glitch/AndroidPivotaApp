package com.example.pivota.core.navigation

import kotlinx.serialization.Serializable

@Serializable object Splash
@Serializable object Welcome
@Serializable object Discovery
@Serializable object GuestDashboard
@Serializable object Dashboard

// Use an object for the Graph name
@Serializable object AuthFlow

@Serializable object Register
@Serializable object Login

// This route needs to carry the email to the next screen
@Serializable
data class VerifyOtp(
    val email: String,
    val isLogin: Boolean
)