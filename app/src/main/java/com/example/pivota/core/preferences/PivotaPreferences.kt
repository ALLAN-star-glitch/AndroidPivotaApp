package com.example.pivota.core.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PivotaDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    companion object {
        // ======================================================
        // SESSION & AUTH TOKENS
        // ======================================================
        private val AUTH_TOKEN = stringPreferencesKey("auth_token")
        private val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        private val USER_EMAIL = stringPreferencesKey("user_email")

        private val RESET_PASSWORD_EMAIL = stringPreferencesKey("reset_password_email")

        // ======================================================
        // ONBOARDING & WELCOME
        // ======================================================
        private val ONBOARDING_COMPLETE = booleanPreferencesKey("onboarding_complete")
        private val WELCOME_SCREEN_SEEN = booleanPreferencesKey("welcome_screen_seen")

        // ======================================================
        // ONBOARDING CACHE (Temporary - cleared after signup)
        // ======================================================
        private val OB_ACCOUNT_TYPE = stringPreferencesKey("ob_account_type")
        private val OB_PRIMARY_PURPOSE = stringPreferencesKey("ob_primary_purpose")
        private val OB_FIRST_NAME = stringPreferencesKey("ob_first_name")
        private val OB_LAST_NAME = stringPreferencesKey("ob_last_name")
        private val OB_EMAIL = stringPreferencesKey("ob_email")
        private val OB_PHONE = stringPreferencesKey("ob_phone")
        private val OB_PASSWORD = stringPreferencesKey("ob_password")
        private val OB_OTP_CODE = stringPreferencesKey("ob_otp_code")
        private val OB_PLAN_SLUG = stringPreferencesKey("ob_plan_slug")
        private val OB_PROFILE_IMAGE = stringPreferencesKey("ob_profile_image")

        // Purpose-specific data as JSON strings
        private val OB_JOB_SEEKER_DATA = stringPreferencesKey("ob_job_seeker_data")
        private val OB_SKILLED_PROFESSIONAL_DATA = stringPreferencesKey("ob_skilled_professional_data")
        private val OB_INTERMEDIARY_AGENT_DATA = stringPreferencesKey("ob_intermediary_agent_data")
        private val OB_HOUSING_SEEKER_DATA = stringPreferencesKey("ob_housing_seeker_data")
        private val OB_SUPPORT_BENEFICIARY_DATA = stringPreferencesKey("ob_support_beneficiary_data")
        private val OB_EMPLOYER_DATA = stringPreferencesKey("ob_employer_data")
        private val OB_PROPERTY_OWNER_DATA = stringPreferencesKey("ob_property_owner_data")

        // ======================================================
        // USER PREFERENCES
        // ======================================================
        private val SELECTED_LANGUAGE = stringPreferencesKey("selected_language")
        private val SELECTED_THEME = stringPreferencesKey("selected_theme")

        private val json = Json { ignoreUnknownKeys = true }



        private val DARK_THEME = booleanPreferencesKey("dark_theme")

        private val GUEST_MODE_ENABLED = booleanPreferencesKey("guest_mode_enabled")

        private val TOKEN_SAVED_AT = longPreferencesKey("token_saved_at")

    }

    // ======================================================
    // SESSION & AUTH TOKENS
    // ======================================================

    val authToken: Flow<String?> = dataStore.data.map { it[AUTH_TOKEN] }
    val refreshToken: Flow<String?> = dataStore.data.map { it[REFRESH_TOKEN] }
    val userEmail: Flow<String?> = dataStore.data.map { it[USER_EMAIL] }

    suspend fun getAccessToken(): String? {
        return dataStore.data.map { it[AUTH_TOKEN] }.first()
    }

    suspend fun getRefreshToken(): String? {
        return dataStore.data.map { it[REFRESH_TOKEN] }.first()
    }

    suspend fun saveAccessToken(token: String) {
        dataStore.edit { it[AUTH_TOKEN] = token }
    }

    suspend fun saveRefreshToken(token: String) {
        dataStore.edit { it[REFRESH_TOKEN] = token }
    }

    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        dataStore.edit {
            it[AUTH_TOKEN] = accessToken
            it[REFRESH_TOKEN] = refreshToken
        }
    }

    suspend fun saveUserEmail(email: String) {
        dataStore.edit { it[USER_EMAIL] = email }
    }

    suspend fun getUserEmail(): String? {
        return dataStore.data.map { it[USER_EMAIL] }.first()
    }

    suspend fun clearSession() {
        dataStore.edit {
            it.remove(AUTH_TOKEN)
            it.remove(REFRESH_TOKEN)
            it.remove(USER_EMAIL)
        }
    }
    suspend fun saveTokenTimestamp(timestamp: Long) {
        dataStore.edit { it[TOKEN_SAVED_AT] = timestamp }
    }

    suspend fun getTokenAge(): Long {
        val savedAt = dataStore.data.map { it[TOKEN_SAVED_AT] }.first() ?: 0L
        return if (savedAt > 0) System.currentTimeMillis() - savedAt else 0L
    }

    suspend fun shouldRefreshToken(): Boolean {
        val tokenAge = getTokenAge()
        // Refresh if token is older than 12 minutes (before 15-minute expiry)
        return tokenAge > 12 * 60 * 1000L
    }

    suspend fun saveTokensWithTimestamp(accessToken: String, refreshToken: String) {
        dataStore.edit {
            it[AUTH_TOKEN] = accessToken
            it[REFRESH_TOKEN] = refreshToken
            it[TOKEN_SAVED_AT] = System.currentTimeMillis()
        }
    }

    // Reset password email methods
    suspend fun saveResetPasswordEmail(email: String) {
        dataStore.edit { it[RESET_PASSWORD_EMAIL] = email }
    }

    suspend fun getResetPasswordEmail(): String? {
        return dataStore.data.map { it[RESET_PASSWORD_EMAIL] }.first()
    }

    suspend fun clearResetPasswordEmail() {
        dataStore.edit { it.remove(RESET_PASSWORD_EMAIL) }
    }

    // ======================================================
    // ONBOARDING & WELCOME SCREEN
    // ======================================================

    val isOnboardingComplete: Flow<Boolean> = dataStore.data.map {
        it[ONBOARDING_COMPLETE] ?: false
    }

    suspend fun isOnboardingComplete(): Boolean {
        return dataStore.data.map { it[ONBOARDING_COMPLETE] ?: false }.first()
    }

    suspend fun setOnboardingComplete(complete: Boolean) {
        dataStore.edit { it[ONBOARDING_COMPLETE] = complete }
    }

    suspend fun markOnboardingComplete(complete: Boolean) {
        dataStore.edit { it[ONBOARDING_COMPLETE] = complete }
    }

    val isWelcomeScreenSeen: Flow<Boolean> = dataStore.data.map {
        it[WELCOME_SCREEN_SEEN] ?: false
    }

    suspend fun isWelcomeScreenSeen(): Boolean {
        return dataStore.data.map { it[WELCOME_SCREEN_SEEN] ?: false }.first()
    }

    suspend fun markWelcomeScreenSeen(seen: Boolean) {
        dataStore.edit { it[WELCOME_SCREEN_SEEN] = seen }
    }

    // ======================================================
    // ONBOARDING CACHE (Temporary data)
    // ======================================================

    suspend fun setAccountType(type: String) {
        dataStore.edit { it[OB_ACCOUNT_TYPE] = type }
    }

    suspend fun getAccountType(): String? {
        return dataStore.data.map { it[OB_ACCOUNT_TYPE] }.first()
    }

    suspend fun debugPrintAll() {
        val prefs = dataStore.data.first()
        println("========== DATASTORE DEBUG ==========")
        println("OB_PRIMARY_PURPOSE = ${prefs[OB_PRIMARY_PURPOSE]}")
        println("OB_FIRST_NAME = ${prefs[OB_FIRST_NAME]}")
        println("OB_LAST_NAME = ${prefs[OB_LAST_NAME]}")
        println("OB_EMAIL = ${prefs[OB_EMAIL]}")
        println("=====================================")
    }

    suspend fun setPrimaryPurpose(purpose: String) {
        println("🔍 DEBUG: setPrimaryPurpose called with = '$purpose'")
        println("🔍 DEBUG: Calling stacktrace:")
        Thread.currentThread().stackTrace.take(10).forEach { println("    at ${it.className}.${it.methodName}") }
        dataStore.edit { it[OB_PRIMARY_PURPOSE] = purpose }
    }

    suspend fun getPrimaryPurpose(): String? {
        val result = dataStore.data.map { it[OB_PRIMARY_PURPOSE] }.first()
        println("🔍 DEBUG: getPrimaryPurpose returning = '$result'")
        return result
    }

    suspend fun saveBasicUserInfo(
        firstName: String,
        lastName: String,
        email: String,
        phone: String,
        password: String
    ) {
        dataStore.edit {
            it[OB_FIRST_NAME] = firstName
            it[OB_LAST_NAME] = lastName
            it[OB_EMAIL] = email
            it[OB_PHONE] = phone
            it[OB_PASSWORD] = password
        }
    }

    suspend fun getFirstName(): String? = dataStore.data.map { it[OB_FIRST_NAME] }.first()
    suspend fun getLastName(): String? = dataStore.data.map { it[OB_LAST_NAME] }.first()
    suspend fun getEmail(): String? = dataStore.data.map { it[OB_EMAIL] }.first()
    suspend fun getPhone(): String? = dataStore.data.map { it[OB_PHONE] }.first()
    suspend fun getPassword(): String? = dataStore.data.map { it[OB_PASSWORD] }.first()

    suspend fun setOtpCode(code: String) {
        dataStore.edit { it[OB_OTP_CODE] = code }
    }

    suspend fun getOtpCode(): String? {
        return dataStore.data.map { it[OB_OTP_CODE] }.first()
    }

    // Purpose-specific data
    suspend fun setJobSeekerData(data: JobSeekerData) {
        val jsonString = json.encodeToString(data)
        dataStore.edit { it[OB_JOB_SEEKER_DATA] = jsonString }
    }

    suspend fun getJobSeekerData(): JobSeekerData? {
        val jsonString = dataStore.data.map { it[OB_JOB_SEEKER_DATA] }.first()
        return jsonString?.let { json.decodeFromString(it) }
    }

    suspend fun setSkilledProfessionalData(data: SkilledProfessionalData) {
        val jsonString = json.encodeToString(data)
        dataStore.edit { it[OB_SKILLED_PROFESSIONAL_DATA] = jsonString }
    }

    suspend fun getSkilledProfessionalData(): SkilledProfessionalData? {
        val jsonString = dataStore.data.map { it[OB_SKILLED_PROFESSIONAL_DATA] }.first()
        return jsonString?.let { json.decodeFromString(it) }
    }

    suspend fun setIntermediaryAgentData(data: IntermediaryAgentData) {
        val jsonString = json.encodeToString(data)
        dataStore.edit { it[OB_INTERMEDIARY_AGENT_DATA] = jsonString }
    }

    suspend fun getIntermediaryAgentData(): IntermediaryAgentData? {
        val jsonString = dataStore.data.map { it[OB_INTERMEDIARY_AGENT_DATA] }.first()
        return jsonString?.let { json.decodeFromString(it) }
    }

    suspend fun setHousingSeekerData(data: HousingSeekerData) {
        val jsonString = json.encodeToString(data)
        dataStore.edit { it[OB_HOUSING_SEEKER_DATA] = jsonString }
    }

    suspend fun getHousingSeekerData(): HousingSeekerData? {
        val jsonString = dataStore.data.map { it[OB_HOUSING_SEEKER_DATA] }.first()
        return jsonString?.let { json.decodeFromString(it) }
    }

    suspend fun setSupportBeneficiaryData(data: SupportBeneficiaryData) {
        val jsonString = json.encodeToString(data)
        dataStore.edit { it[OB_SUPPORT_BENEFICIARY_DATA] = jsonString }
    }

    suspend fun getSupportBeneficiaryData(): SupportBeneficiaryData? {
        val jsonString = dataStore.data.map { it[OB_SUPPORT_BENEFICIARY_DATA] }.first()
        return jsonString?.let { json.decodeFromString(it) }
    }

    suspend fun setEmployerData(data: EmployerData) {
        val jsonString = json.encodeToString(data)
        dataStore.edit { it[OB_EMPLOYER_DATA] = jsonString }
    }

    suspend fun getEmployerData(): EmployerData? {
        val jsonString = dataStore.data.map { it[OB_EMPLOYER_DATA] }.first()
        return jsonString?.let { json.decodeFromString(it) }
    }

    suspend fun setPropertyOwnerData(data: PropertyOwnerData) {
        val jsonString = json.encodeToString(data)
        dataStore.edit { it[OB_PROPERTY_OWNER_DATA] = jsonString }
    }

    suspend fun getPropertyOwnerData(): PropertyOwnerData? {
        val jsonString = dataStore.data.map { it[OB_PROPERTY_OWNER_DATA] }.first()
        return jsonString?.let { json.decodeFromString(it) }
    }

    suspend fun getCurrentData(): OnboardingData {
        val prefs = dataStore.data.first()
        return OnboardingData(
            accountType = prefs[OB_ACCOUNT_TYPE],
            primaryPurpose = prefs[OB_PRIMARY_PURPOSE],
            registrationData = RegistrationData(
                firstName = prefs[OB_FIRST_NAME] ?: "",
                lastName = prefs[OB_LAST_NAME] ?: "",
                email = prefs[OB_EMAIL] ?: "",
                phone = prefs[OB_PHONE] ?: "",
                password = prefs[OB_PASSWORD] ?: "",
                planSlug = prefs[OB_PLAN_SLUG] ?: "free-forever",
                otpCode = prefs[OB_OTP_CODE]
            ),
            isOtpVerified = prefs[OB_OTP_CODE] != null
        )
    }

    suspend fun clear() {
        dataStore.edit {
            it.remove(OB_ACCOUNT_TYPE)
            it.remove(OB_PRIMARY_PURPOSE)
            it.remove(OB_FIRST_NAME)
            it.remove(OB_LAST_NAME)
            it.remove(OB_EMAIL)
            it.remove(OB_PHONE)
            it.remove(OB_PASSWORD)
            it.remove(OB_OTP_CODE)
            it.remove(OB_PLAN_SLUG)
            it.remove(OB_PROFILE_IMAGE)
            it.remove(OB_JOB_SEEKER_DATA)
            it.remove(OB_SKILLED_PROFESSIONAL_DATA)
            it.remove(OB_INTERMEDIARY_AGENT_DATA)
            it.remove(OB_HOUSING_SEEKER_DATA)
            it.remove(OB_SUPPORT_BENEFICIARY_DATA)
            it.remove(OB_EMPLOYER_DATA)
            it.remove(OB_PROPERTY_OWNER_DATA)
        }
    }

    suspend fun buildSignupRequest(): UserSignupRequest {
        return UserSignupRequest(
            firstName = getFirstName() ?: "",
            lastName = getLastName() ?: "",
            email = getEmail() ?: "",
            phone = getPhone(),
            password = getPassword() ?: "",
            code = getOtpCode() ?: "",
            planSlug = "free-forever",
            profileImage = null,
            primaryPurpose = getPrimaryPurpose(),
            jobSeekerData = getJobSeekerData(),
            skilledProfessionalData = getSkilledProfessionalData(),
            intermediaryAgentData = getIntermediaryAgentData(),
            housingSeekerData = getHousingSeekerData(),
            supportBeneficiaryData = getSupportBeneficiaryData(),
            employerData = getEmployerData(),
            propertyOwnerData = getPropertyOwnerData()
        )
    }

    suspend fun clearOnboardingCache() {
        dataStore.edit {
            it.remove(OB_ACCOUNT_TYPE)
            it.remove(OB_PRIMARY_PURPOSE)
            it.remove(OB_FIRST_NAME)
            it.remove(OB_LAST_NAME)
            it.remove(OB_EMAIL)
            it.remove(OB_PHONE)
            it.remove(OB_PASSWORD)
            it.remove(OB_OTP_CODE)
            it.remove(OB_PLAN_SLUG)
            it.remove(OB_PROFILE_IMAGE)
            it.remove(OB_JOB_SEEKER_DATA)
            it.remove(OB_SKILLED_PROFESSIONAL_DATA)
            it.remove(OB_INTERMEDIARY_AGENT_DATA)
            it.remove(OB_HOUSING_SEEKER_DATA)
            it.remove(OB_SUPPORT_BENEFICIARY_DATA)
            it.remove(OB_EMPLOYER_DATA)
            it.remove(OB_PROPERTY_OWNER_DATA)
        }
    }

    // ======================================================
    // USER PREFERENCES
    // ======================================================

    val selectedLanguage: Flow<String?> = dataStore.data.map { it[SELECTED_LANGUAGE] }

    suspend fun getSelectedLanguage(): String? {
        return dataStore.data.map { it[SELECTED_LANGUAGE] }.first()
    }

    suspend fun saveSelectedLanguage(language: String) {
        dataStore.edit { it[SELECTED_LANGUAGE] = language }
    }

    val selectedTheme: Flow<String?> = dataStore.data.map { it[SELECTED_THEME] }

    suspend fun getSelectedTheme(): String? {
        return dataStore.data.map { it[SELECTED_THEME] }.first()
    }

    suspend fun saveSelectedTheme(theme: String) {
        dataStore.edit { it[SELECTED_THEME] = theme }
    }

    // ======================================================
    // CLEAR METHODS
    // ======================================================

    suspend fun clearUserData() {
        dataStore.edit {
            it.remove(AUTH_TOKEN)
            it.remove(REFRESH_TOKEN)
            it.remove(USER_EMAIL)
        }
    }

    suspend fun clearAll() {
        dataStore.edit { it.clear() }
    }

    suspend fun resetOnboarding() {
        dataStore.edit {
            it.remove(ONBOARDING_COMPLETE)
            it.remove(WELCOME_SCREEN_SEEN)
        }
    }




    suspend fun clearPrimaryPurpose() {
        dataStore.edit { it.remove(OB_PRIMARY_PURPOSE) }
    }

    suspend fun clearJobSeekerData() {
        dataStore.edit { it.remove(OB_JOB_SEEKER_DATA) }
    }

    suspend fun clearSkilledProfessionalData() {
        dataStore.edit { it.remove(OB_SKILLED_PROFESSIONAL_DATA) }
    }

    suspend fun clearIntermediaryAgentData() {
        dataStore.edit { it.remove(OB_INTERMEDIARY_AGENT_DATA) }
    }

    suspend fun clearHousingSeekerData() {
        dataStore.edit { it.remove(OB_HOUSING_SEEKER_DATA) }
    }

    suspend fun clearSupportBeneficiaryData() {
        dataStore.edit { it.remove(OB_SUPPORT_BENEFICIARY_DATA) }
    }

    suspend fun clearEmployerData() {
        dataStore.edit { it.remove(OB_EMPLOYER_DATA) }
    }

    suspend fun clearPropertyOwnerData() {
        dataStore.edit { it.remove(OB_PROPERTY_OWNER_DATA) }
    }

    suspend fun clearAllOnboardingData() {
        dataStore.edit {
            it.remove(OB_ACCOUNT_TYPE)
            it.remove(OB_PRIMARY_PURPOSE)
            it.remove(OB_FIRST_NAME)
            it.remove(OB_LAST_NAME)
            it.remove(OB_EMAIL)
            it.remove(OB_PHONE)
            it.remove(OB_PASSWORD)
            it.remove(OB_OTP_CODE)
            it.remove(OB_PLAN_SLUG)
            it.remove(OB_PROFILE_IMAGE)
            it.remove(OB_JOB_SEEKER_DATA)
            it.remove(OB_SKILLED_PROFESSIONAL_DATA)
            it.remove(OB_INTERMEDIARY_AGENT_DATA)
            it.remove(OB_HOUSING_SEEKER_DATA)
            it.remove(OB_SUPPORT_BENEFICIARY_DATA)
            it.remove(OB_EMPLOYER_DATA)
            it.remove(OB_PROPERTY_OWNER_DATA)
        }
    }

    // ======================================================
    // THEME PREFERENCES
    // ======================================================

    val isDarkThemeFlow: Flow<Boolean> = dataStore.data.map {
        it[DARK_THEME] ?: false
    }

    suspend fun setDarkTheme(isDark: Boolean) {
        dataStore.edit { it[DARK_THEME] = isDark }
    }

    suspend fun getDarkTheme(): Boolean {
        return dataStore.data.map { it[DARK_THEME] ?: false }.first()
    }

    suspend fun toggleTheme() {
        val current = getDarkTheme()
        setDarkTheme(!current)
    }




    suspend fun saveGuestModeEnabled(enabled: Boolean) {
        dataStore.edit { it[GUEST_MODE_ENABLED] = enabled }
    }

    suspend fun isGuestModeEnabled(): Boolean {
        return dataStore.data.map { it[GUEST_MODE_ENABLED] ?: false }.first()
    }

    suspend fun clearGuestMode() {
        dataStore.edit { it.remove(GUEST_MODE_ENABLED) }
    }

}

// ======================================================
// DATA CLASSES WITH @Serializable
// ======================================================

@Serializable
data class OnboardingData(
    val accountType: String? = null,
    val primaryPurpose: String? = null,
    val registrationData: RegistrationData? = null,
    val isOtpVerified: Boolean = false
)

@Serializable
data class RegistrationData(
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val password: String,
    val planSlug: String = "free-forever",
    val otpCode: String? = null
)

@Serializable
data class UserSignupRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String? = null,
    val password: String,
    val code: String,
    val planSlug: String? = "free-forever",
    val profileImage: String? = null,
    val primaryPurpose: String? = null,
    val jobSeekerData: JobSeekerData? = null,
    val skilledProfessionalData: SkilledProfessionalData? = null,
    val intermediaryAgentData: IntermediaryAgentData? = null,
    val housingSeekerData: HousingSeekerData? = null,
    val supportBeneficiaryData: SupportBeneficiaryData? = null,
    val employerData: EmployerData? = null,
    val propertyOwnerData: PropertyOwnerData? = null
)

@Serializable
data class JobSeekerData(
    val headline: String? = null,
    val isActivelySeeking: Boolean = true,
    val skills: List<String> = emptyList(),
    val industries: List<String> = emptyList(),
    val jobTypes: List<String> = emptyList(),
    val seniorityLevel: String? = null,
    val expectedSalary: Int? = null
)

@Serializable
data class SkilledProfessionalData(
    val title: String? = null,
    val profession: String? = null,
    val specialties: List<String> = emptyList(),
    val serviceAreas: List<String> = emptyList(),
    val yearsExperience: Int? = null,
    val licenseNumber: String? = null,
    val hourlyRate: Int? = null
)

@Serializable
data class IntermediaryAgentData(
    val agentType: String? = null,
    val specializations: List<String> = emptyList(),
    val serviceAreas: List<String> = emptyList(),
    val licenseNumber: String? = null,
    val yearsExperience: Int? = null,
    val agencyName: String? = null,
    val commissionRate: Double? = null
)

@Serializable
data class HousingSeekerData(
    // Search Type (what are they looking for)
    val searchType: String? = null,  // "RENTAL", "SALE", "BOTH"
    val isLookingForRental: Boolean = false,
    val isLookingToBuy: Boolean = false,

    // Property Types (multi-select)
    val propertyTypes: List<String> = emptyList(),  // "APARTMENT", "HOUSE", etc.

    // Legacy fields (kept for backward compatibility)
    val minBedrooms: Int? = null,
    val maxBedrooms: Int? = null,
    val minBudget: Int? = null,
    val maxBudget: Int? = null,
    val preferredTypes: List<String> = emptyList(),
    val preferredCities: List<String> = emptyList()
)

@Serializable
data class SupportBeneficiaryData(
    val needs: List<String> = emptyList(),
    val urgentNeeds: List<String> = emptyList(),
    val familySize: Int? = null,
    val city: String? = null,
    val neighborhood: String? = null
)

@Serializable
data class EmployerData(
    val businessName: String? = null,
    val isRegistered: Boolean = false,
    val industry: String? = null,
    val companySize: String? = null,
    val description: String? = null
)

@Serializable
data class PropertyOwnerData(
    // Listing Type (what are they listing)

    val listingType: String? = null,  // "RENT", "SALE", "BOTH"
    val isListingForRent: Boolean = false,
    val isListingForSale: Boolean = false,

    // Property details
    val propertyCount: Int? = null,
    val propertyTypes: List<String> = emptyList(),
    val serviceAreas: List<String> = emptyList(),

    // Legacy fields (kept for backward compatibility)
    val isProfessional: Boolean = false,
    val preferredPropertyTypes: List<String> = emptyList()
)


