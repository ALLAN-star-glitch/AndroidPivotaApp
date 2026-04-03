package com.example.pivota.auth.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pivota.auth.domain.model.User
import com.example.pivota.auth.domain.useCase.AuthUseCases
import com.example.pivota.auth.presentation.state.SignupUiState
import com.example.pivota.core.preferences.PivotaDataStore
import com.example.pivota.core.presentations.composables.SnackbarType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.regex.Pattern
import javax.inject.Inject

data class RegistrationFormState(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phone: String = "",
    val password: String = "",
    val agreeTerms: Boolean = false
)

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val authUseCases: AuthUseCases,
    private val datastore: PivotaDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow<SignupUiState>(SignupUiState.Idle)
    val uiState: StateFlow<SignupUiState> = _uiState.asStateFlow()

    /* ---------------- SNACKBAR STATE ---------------- */

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    private val _snackbarType = MutableStateFlow(SnackbarType.INFO)
    val snackbarType: StateFlow<SnackbarType> = _snackbarType.asStateFlow()

    private val _formState = MutableStateFlow(RegistrationFormState())
    val formState: StateFlow<RegistrationFormState> = _formState.asStateFlow()

    private val _otpValues = MutableStateFlow(List(6) { "" })
    val otpValues: StateFlow<List<String>> = _otpValues.asStateFlow()

    private val _resendCount = MutableStateFlow(0)
    val resendCount: StateFlow<Int> = _resendCount.asStateFlow()

    private var pendingUser: User? = null
    private var pendingPassword = ""
    var pendingEmail: String = ""
        private set

    /* ---------------- SNACKBAR ACTIONS ---------------- */

    private fun showSnackbar(message: String, type: SnackbarType = SnackbarType.ERROR) {
        _snackbarMessage.value = message
        _snackbarType.value = type
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    init {
        loadCachedData()
    }

    private fun loadCachedData() {
        viewModelScope.launch {
            val firstName = datastore.getFirstName()
            val lastName = datastore.getLastName()
            val email = datastore.getEmail()
            val phone = datastore.getPhone()
            val password = datastore.getPassword()

            _formState.update { form ->
                form.copy(
                    firstName = firstName ?: "",
                    lastName = lastName ?: "",
                    email = email ?: "",
                    phone = phone ?: "",
                    password = password ?: ""
                )
            }
            pendingEmail = email ?: ""
        }
    }

    // Map display name to API enum value
    private fun mapToApiPurpose(displayPurpose: String?): String? {
        return when (displayPurpose) {
            "Find a Job" -> "FIND_JOB"
            "Offer Skilled Services" -> "OFFER_SKILLED_SERVICES"
            "Work as Agent" -> "WORK_AS_AGENT"
            "Find Housing" -> "FIND_HOUSING"
            "Get Social Support" -> "GET_SOCIAL_SUPPORT"
            "Hire Employees" -> "HIRE_EMPLOYEES"
            "List Properties" -> "LIST_PROPERTIES"
            "Just Exploring" -> "JUST_EXPLORING"
            else -> displayPurpose // Already in API format
        }
    }

    fun updateFirstName(value: String) = updateForm { copy(firstName = value) }
    fun updateLastName(value: String) = updateForm { copy(lastName = value) }
    fun updateEmail(value: String) = updateForm { copy(email = value) }
    fun updatePhone(value: String) = updateForm { copy(phone = value) }
    fun updatePassword(value: String) = updateForm { copy(password = value) }
    fun updateAgreeTerms(value: Boolean) = updateForm { copy(agreeTerms = value) }

    private inline fun updateForm(block: RegistrationFormState.() -> RegistrationFormState) {
        _formState.value = _formState.value.block()
    }

    fun isEmailValid(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-z]{2,}$"
        return Pattern.matches(emailPattern, email.trim())
    }

    fun isPasswordValid(password: String): Boolean {
        return password.trim().length >= 6
    }

    fun startSignup() {
        val form = _formState.value

        val errorMessage = when {
            !form.agreeTerms -> "You must agree to the terms and conditions."
            !isPasswordValid(form.password) -> "Password must be at least 6 characters."
            form.firstName.isBlank() -> "First name is required."
            form.lastName.isBlank() -> "Last name is required."
            !isEmailValid(form.email) -> "Please enter a valid email address."
            else -> null
        }

        if (errorMessage != null) {
            showSnackbar(errorMessage, SnackbarType.WARNING)
            return
        }

        val email = form.email.trim()
        val password = form.password.trim()
        val phone = form.phone.trim()

        pendingPassword = password
        pendingEmail = email

        viewModelScope.launch {
            datastore.debugPrintAll()
            datastore.saveBasicUserInfo(
                firstName = form.firstName.trim(),
                lastName = form.lastName.trim(),
                email = email,
                phone = phone,
                password = password
            )

            // Get the API-compatible purpose from DataStore
            val rawPurpose = datastore.getPrimaryPurpose()
            println("DEBUG: rawPurpose from DataStore = $rawPurpose")

            val apiPurpose = mapToApiPurpose(rawPurpose)
            println("DEBUG: apiPurpose after mapping = $apiPurpose")

            // Get purpose-specific data from DataStore with proper type conversions
            val jobSeekerPreferences = datastore.getJobSeekerData()?.let { data ->
                println("🔍 DEBUG: ========== RETRIEVED JOB SEEKER DATA ==========")
                println("🔍 DEBUG: headline: '${data.headline}'")
                println("🔍 DEBUG: isActivelySeeking: ${data.isActivelySeeking}")
                println("🔍 DEBUG: skills: ${data.skills}")
                println("🔍 DEBUG: industries: ${data.industries}")
                println("🔍 DEBUG: jobTypes: ${data.jobTypes}")
                println("🔍 DEBUG: seniorityLevel: '${data.seniorityLevel}'")
                println("🔍 DEBUG: expectedSalary: ${data.expectedSalary}")
                println("🔍 DEBUG: ===============================================")

                com.example.pivota.auth.domain.model.JobSeekerPreferences(
                    headline = data.headline ?: "",
                    isActivelySeeking = data.isActivelySeeking,
                    skills = data.skills,
                    industries = data.industries,
                    jobTypes = data.jobTypes,
                    seniorityLevel = data.seniorityLevel,
                    expectedSalary = data.expectedSalary
                )
            }

            val skilledProfessionalProfile = datastore.getSkilledProfessionalData()?.let { data ->
                com.example.pivota.auth.domain.model.SkilledProfessionalProfile(
                    profession = data.profession ?: "",
                    specialties = data.specialties,
                    serviceAreas = data.serviceAreas,
                    yearsExperience = data.yearsExperience,
                    licenseNumber = data.licenseNumber,
                    hourlyRate = data.hourlyRate?.toDouble()
                )
            }

            val intermediaryAgentProfile = datastore.getIntermediaryAgentData()?.let { data ->
                com.example.pivota.auth.domain.model.IntermediaryAgentProfile(
                    agentType = data.agentType ?: "",
                    specializations = data.specializations,
                    serviceAreas = data.serviceAreas,
                    licenseNumber = data.licenseNumber,
                    commissionRate = data.commissionRate
                )
            }

            val housingSeekerPreferences = datastore.getHousingSeekerData()?.let { data ->
                com.example.pivota.auth.domain.model.HousingSeekerPreferences(
                    minBedrooms = data.minBedrooms,
                    maxBedrooms = data.maxBedrooms,
                    minBudget = data.minBudget?.toDouble(),
                    maxBudget = data.maxBudget?.toDouble(),
                    preferredCities = data.preferredCities
                )
            }

            val supportBeneficiaryNeeds = datastore.getSupportBeneficiaryData()?.let { data ->
                com.example.pivota.auth.domain.model.SupportBeneficiaryNeeds(
                    needs = data.needs,
                    urgentNeeds = data.urgentNeeds,
                    city = data.city,
                    familySize = data.familySize
                )
            }

            val employerRequirements = datastore.getEmployerData()?.let { data ->
                com.example.pivota.auth.domain.model.EmployerRequirements(
                    businessName = data.businessName,
                    industry = data.industry,
                    companySize = data.companySize,
                    description = data.description
                )
            }

            val propertyOwnerPortfolio = datastore.getPropertyOwnerData()?.let { data ->
                com.example.pivota.auth.domain.model.PropertyOwnerPortfolio(
                    isProfessional = data.isProfessional,
                    propertyCount = data.propertyCount,
                    propertyTypes = data.propertyTypes,
                    serviceAreas = data.serviceAreas
                )
            }

            pendingUser = User(
                uuid = "",
                email = email,
                firstName = form.firstName.trim(),
                lastName = form.lastName.trim(),
                personalPhone = phone.ifEmpty { null },
                isAuthenticated = false,
                primaryPurpose = apiPurpose,
                jobSeekerPreferences = jobSeekerPreferences,
                skilledProfessionalProfile = skilledProfessionalProfile,
                intermediaryAgentProfile = intermediaryAgentProfile,
                housingSeekerPreferences = housingSeekerPreferences,
                supportBeneficiaryNeeds = supportBeneficiaryNeeds,
                employerRequirements = employerRequirements,
                propertyOwnerPortfolio = propertyOwnerPortfolio
            )

            requestSignupOtp(email)
        }
    }

    fun requestSignupOtp(email: String) {
        println("🔍 STEP 1: requestSignupOtp called with email: $email, purpose: SIGNUP")
        _uiState.value = SignupUiState.Loading
        viewModelScope.launch {
            authUseCases.requestOtp(email, "SIGNUP")
                .onSuccess {
                    println("🔍 STEP 2: OTP request SUCCESS")
                    datastore.saveBasicUserInfo(
                        firstName = _formState.value.firstName,
                        lastName = _formState.value.lastName,
                        email = email,
                        phone = _formState.value.phone,
                        password = _formState.value.password
                    )
                    showSnackbar("Verification code sent to your email!", SnackbarType.SUCCESS)
                    // Delay before showing OTP dialog to let snackbar be visible
                    delay(1500)
                    _uiState.value = SignupUiState.OtpSent
                }
                .onFailure { error ->
                    println("🔍 STEP 2: OTP request FAILED: ${error.message}")
                    showSnackbar(error.message ?: "Failed to send verification code", SnackbarType.ERROR)
                    _uiState.value = SignupUiState.Idle
                }
        }
    }

    fun updateOtpDigit(index: Int, value: String) {
        val current = _otpValues.value.toMutableList()
        current[index] = value
        _otpValues.value = current
    }

    fun incrementResendCount() {
        _resendCount.value += 1
    }

    fun verifyAndRegister(code: String) {
        val user = pendingUser ?: run {
            showSnackbar("Session expired. Please register again.", SnackbarType.ERROR)
            return
        }

        // Log the user object before signup
        println("🔍 ==================== USER BEFORE SIGNUP ====================")
        println("🔍 firstName: ${user.firstName}")
        println("🔍 lastName: ${user.lastName}")
        println("🔍 email: ${user.email}")
        println("🔍 personalPhone: ${user.personalPhone}")
        println("🔍 primaryPurpose: ${user.primaryPurpose}")
        println("🔍 jobSeekerPreferences: ${user.jobSeekerPreferences}")
        println("🔍 skilledProfessionalProfile: ${user.skilledProfessionalProfile}")
        println("🔍 intermediaryAgentProfile: ${user.intermediaryAgentProfile}")
        println("🔍 housingSeekerPreferences: ${user.housingSeekerPreferences}")
        println("🔍 supportBeneficiaryNeeds: ${user.supportBeneficiaryNeeds}")
        println("🔍 employerRequirements: ${user.employerRequirements}")
        println("🔍 propertyOwnerPortfolio: ${user.propertyOwnerPortfolio}")
        println("🔍 =============================================================")

        _uiState.value = SignupUiState.Loading
        viewModelScope.launch {
            val trimmedCode = code.trim()
            datastore.setOtpCode(trimmedCode)

            val result = authUseCases.registerUser.signupIndividual(user, trimmedCode, pendingPassword)

            result.onSuccess {
                datastore.clear()
                clearCache()
                showSnackbar("Account created successfully!", SnackbarType.SUCCESS)
                _uiState.value = SignupUiState.Success
            }.onFailure { error ->
                println("🔍 SIGNUP ERROR: ${error.message}")
                showSnackbar(error.message ?: "Signup failed", SnackbarType.ERROR)
                _uiState.value = SignupUiState.Idle
            }
        }
    }

    private fun clearCache() {
        pendingUser = null
        pendingPassword = ""
        _otpValues.value = List(6) { "" }
        _resendCount.value = 0
    }

    fun resetState() {
        _uiState.value = SignupUiState.Idle
        clearSnackbar()
    }

    fun clearError() {
        if (_uiState.value is SignupUiState.Error) {
            _uiState.value = SignupUiState.Idle
        }
    }
}