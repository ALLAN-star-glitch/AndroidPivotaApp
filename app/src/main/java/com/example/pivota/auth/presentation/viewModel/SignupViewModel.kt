package com.example.pivota.auth.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pivota.auth.domain.model.User
import com.example.pivota.auth.domain.useCase.AuthUseCases
import com.example.pivota.auth.presentation.state.SignupUiState
import com.example.pivota.core.database.dao.UserDao
import com.example.pivota.core.network.ApiResult
import com.example.pivota.core.network.NetworkError
import com.example.pivota.core.network.getUserFriendlyMessage
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
    private val datastore: PivotaDataStore,
    private val userDao: UserDao
) : ViewModel() {

    private val _uiState = MutableStateFlow<SignupUiState>(SignupUiState.Idle)
    val uiState: StateFlow<SignupUiState> = _uiState.asStateFlow()

    /* ---------------- SNACKBAR STATE ---------------- */

    private val _mainSnackbarMessage = MutableStateFlow<String?>(null)
    val mainSnackbarMessage: StateFlow<String?> = _mainSnackbarMessage.asStateFlow()

    private val _mainSnackbarType = MutableStateFlow(SnackbarType.INFO)
    val mainSnackbarType: StateFlow<SnackbarType> = _mainSnackbarType.asStateFlow()

    private val _dialogSnackbarMessage = MutableStateFlow<String?>(null)
    val dialogSnackbarMessage: StateFlow<String?> = _dialogSnackbarMessage.asStateFlow()

    private val _dialogSnackbarType = MutableStateFlow(SnackbarType.INFO)
    val dialogSnackbarType: StateFlow<SnackbarType> = _dialogSnackbarType.asStateFlow()

    private val _formState = MutableStateFlow(RegistrationFormState())
    val formState: StateFlow<RegistrationFormState> = _formState.asStateFlow()

    private val _otpValues = MutableStateFlow(List(6) { "" })
    val otpValues: StateFlow<List<String>> = _otpValues.asStateFlow()

    private val _resendCount = MutableStateFlow(0)
    val resendCount: StateFlow<Int> = _resendCount.asStateFlow()

    // Signal to close the OTP dialog when OTP sending fails
    private val _shouldCloseDialog = MutableStateFlow(false)
    val shouldCloseDialog: StateFlow<Boolean> = _shouldCloseDialog.asStateFlow()

    private var pendingUser: User? = null
    private var pendingPassword = ""
    var pendingEmail: String = ""
        private set

    /* ---------------- SNACKBAR ACTIONS ---------------- */

    private fun showMainSnackbar(message: String, type: SnackbarType = SnackbarType.ERROR) {
        _mainSnackbarMessage.value = message
        _mainSnackbarType.value = type
        viewModelScope.launch {
            delay(4000)
            clearMainSnackbar()
        }
    }

    fun showDialogSnackbar(message: String, type: SnackbarType = SnackbarType.INFO) {
        _dialogSnackbarMessage.value = message
        _dialogSnackbarType.value = type
    }

    fun clearMainSnackbar() {
        _mainSnackbarMessage.value = null
    }

    fun clearDialogSnackbar() {
        _dialogSnackbarMessage.value = null
    }

    fun showErrorMessage(message: String) {
        _mainSnackbarMessage.value = message
        _mainSnackbarType.value = SnackbarType.ERROR
        viewModelScope.launch {
            delay(4000)
            clearMainSnackbar()
        }
    }

    fun resetDialogCloseFlag() {
        _shouldCloseDialog.value = false
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
            else -> displayPurpose
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

        // REMOVED phone validation - phone is optional
        val errorMessage = when {
            !form.agreeTerms -> "You must agree to the terms and conditions."
            !isPasswordValid(form.password) -> "Password must be at least 6 characters."
            form.firstName.isBlank() -> "First name is required."
            form.lastName.isBlank() -> "Last name is required."
            !isEmailValid(form.email) -> "Please enter a valid email address."
            else -> null
        }

        if (errorMessage != null) {
            showMainSnackbar(errorMessage, SnackbarType.WARNING)
            return
        }

        val email = form.email.trim()
        val password = form.password.trim()
        val phone = form.phone.trim().ifEmpty { null }

        pendingPassword = password
        pendingEmail = email

        viewModelScope.launch {
            datastore.debugPrintAll()
            datastore.saveBasicUserInfo(
                firstName = form.firstName.trim(),
                lastName = form.lastName.trim(),
                email = email,
                phone = phone ?: "",
                password = password
            )

            val rawPurpose = datastore.getPrimaryPurpose()
            println("DEBUG: rawPurpose from DataStore = $rawPurpose")

            val apiPurpose = mapToApiPurpose(rawPurpose)
            println("DEBUG: apiPurpose after mapping = $apiPurpose")

            // Get purpose-specific data from DataStore with updated types
            val jobSeekerPreferences = datastore.getJobSeekerData()?.let { data ->
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
                    searchType = data.searchType,
                    isLookingForRental = data.isLookingForRental,
                    isLookingToBuy = data.isLookingToBuy,
                    propertyTypes = data.propertyTypes
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
                    listingType = data.listingType,
                    isListingForRent = data.isListingForRent,
                    isListingForSale = data.isListingForSale,
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
                personalPhone = phone,
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

            requestSignupOtp(email, phone)
        }
    }

    // Updated: phone parameter is optional (can be null)
    fun requestSignupOtp(email: String, phone: String?) {
        println("🔍 STEP 1: requestSignupOtp called with email: $email, phone: $phone, purpose: EMAIL_VERIFICATION")
        _uiState.value = SignupUiState.Loading
        // Reset dialog close flag before new request
        _shouldCloseDialog.value = false

        viewModelScope.launch {
            val result = authUseCases.requestOtp(email, "EMAIL_VERIFICATION", phone)

            when (result) {
                is ApiResult.Success -> {
                    println("🔍 STEP 2: OTP request SUCCESS")
                    datastore.saveBasicUserInfo(
                        firstName = _formState.value.firstName,
                        lastName = _formState.value.lastName,
                        email = email,
                        phone = _formState.value.phone,
                        password = _formState.value.password
                    )
                    _uiState.value = SignupUiState.OtpSent
                }
                is ApiResult.Error -> {
                    println("🔍 STEP 2: OTP request FAILED: ${result.technicalMessage}")
                    // Show error in main snackbar (not dialog)
                    val errorMessage = result.getUserFriendlyMessage()
                    showMainSnackbar(errorMessage, SnackbarType.ERROR)
                    // Reset to Idle to stop loading indicator
                    _uiState.value = SignupUiState.Idle
                    // Signal that dialog should close (in case it was trying to open)
                    _shouldCloseDialog.value = true
                }
                ApiResult.Loading -> {
                    // Already in loading state
                }
            }
        }
    }

    // Updated: Resend OTP with optional phone
    fun resendOtp() {
        val email = _formState.value.email
        val phone = _formState.value.phone.ifEmpty { null }

        if (email.isBlank()) {
            showDialogSnackbar("Email is required", SnackbarType.ERROR)
            return
        }

        viewModelScope.launch {
            val result = authUseCases.requestOtp(email, "EMAIL_VERIFICATION", phone)

            when (result) {
                is ApiResult.Success -> {
                    showDialogSnackbar("New verification code sent!", SnackbarType.SUCCESS)
                    // Reset OTP values when resending
                    _otpValues.value = List(6) { "" }
                    // Reset dialog close flag
                    _shouldCloseDialog.value = false
                }
                is ApiResult.Error -> {
                    val errorMessage = result.getUserFriendlyMessage()
                    showDialogSnackbar(errorMessage, SnackbarType.ERROR)
                    // Signal that dialog should close on resend error
                    _shouldCloseDialog.value = true
                }
                ApiResult.Loading -> {
                    // Loading state - do nothing
                }
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
            showDialogSnackbar("Session expired. Please register again.", SnackbarType.ERROR)
            return
        }

        println("🔍 ==================== USER BEFORE SIGNUP ====================")
        println("🔍 firstName: ${user.firstName}")
        println("🔍 lastName: ${user.lastName}")
        println("🔍 email: ${user.email}")
        println("🔍 personalPhone: ${user.personalPhone}")
        println("🔍 primaryPurpose: ${user.primaryPurpose}")
        if (user.housingSeekerPreferences != null) {
            println("🔍 housingSeekerPreferences - searchType: ${user.housingSeekerPreferences.searchType}")
            println("🔍 housingSeekerPreferences - propertyTypes: ${user.housingSeekerPreferences.propertyTypes}")
        }
        if (user.propertyOwnerPortfolio != null) {
            println("🔍 propertyOwnerPortfolio - listingType: ${user.propertyOwnerPortfolio.listingType}")
            println("🔍 propertyOwnerPortfolio - isListingForRent: ${user.propertyOwnerPortfolio.isListingForRent}")
            println("🔍 propertyOwnerPortfolio - isListingForSale: ${user.propertyOwnerPortfolio.isListingForSale}")
        }
        println("🔍 =============================================================")

        _uiState.value = SignupUiState.Loading
        viewModelScope.launch {
            val trimmedCode = code.trim()
            datastore.setOtpCode(trimmedCode)

            val result = authUseCases.registerUser.invoke(user, trimmedCode, pendingPassword)

            when (result) {
                is ApiResult.Success -> {
                    val signupData = result.data

                    when {
                        !signupData.accessToken.isNullOrEmpty() -> {
                            println("🔍 Signup successful with tokens - auto-login")

                            datastore.saveTokens(signupData.accessToken, signupData.refreshToken ?: "")
                            datastore.saveUserEmail(user.email)

                            val userEntity = userDao.getUserByEmail(user.email)

                            val authenticatedUser = if (userEntity != null) {
                                User(
                                    uuid = userEntity.uuid,
                                    email = userEntity.email,
                                    firstName = userEntity.firstName,
                                    lastName = userEntity.lastName,
                                    userName = userEntity.userName,
                                    personalPhone = userEntity.phone,
                                    profileImage = userEntity.profileImage,
                                    accessToken = signupData.accessToken,
                                    refreshToken = signupData.refreshToken,
                                    isAuthenticated = true,
                                    primaryPurpose = userEntity.primaryPurpose,
                                    role = userEntity.role,
                                    accountType = userEntity.accountType,
                                    accountId = userEntity.accountId,
                                    accountName = userEntity.accountName,
                                    organizationUuid = userEntity.organizationUuid,
                                    planSlug = userEntity.planSlug,
                                    tokenId = userEntity.tokenId
                                )
                            } else {
                                User(
                                    email = user.email,
                                    isAuthenticated = true,
                                    accessToken = signupData.accessToken,
                                    refreshToken = signupData.refreshToken
                                )
                            }

                            datastore.clear()
                            clearCache()
                            _uiState.value = SignupUiState.Success(
                                message = signupData.message,
                                redirectTo = signupData.redirectTo ?: "/dashboard",
                                accessToken = signupData.accessToken,
                                refreshToken = signupData.refreshToken ?: "",
                                user = authenticatedUser
                            )
                        }
                        !signupData.redirectUrl.isNullOrEmpty() -> {
                            println("🔍 Payment required - redirect to payment")
                            _uiState.value = SignupUiState.PaymentRequired(
                                message = signupData.message,
                                redirectUrl = signupData.redirectUrl,
                                merchantReference = signupData.merchantReference
                            )
                        }
                        else -> {
                            println("🔍 Signup successful without tokens")
                            datastore.clear()
                            clearCache()
                            _uiState.value = SignupUiState.Success(
                                message = signupData.message,
                                redirectTo = "/login"
                            )
                        }
                    }
                }
                is ApiResult.Error -> {
                    println("🔍 SIGNUP ERROR: ${result.technicalMessage}")
                    val errorMessage = result.getUserFriendlyMessage()
                    _uiState.value = SignupUiState.Error(errorMessage)
                    showDialogSnackbar(errorMessage, SnackbarType.ERROR)
                }
                ApiResult.Loading -> {
                    // Already in loading state
                }
            }
        }
    }

    fun updateOtpFull(value: String) {
        _otpValues.value = List(6) { index -> value.getOrNull(index)?.toString() ?: "" }
    }

    private fun clearCache() {
        pendingUser = null
        pendingPassword = ""
        _otpValues.value = List(6) { "" }
        _resendCount.value = 0
    }

    fun resetState() {
        _uiState.value = SignupUiState.Idle
        clearMainSnackbar()
        clearDialogSnackbar()
        _shouldCloseDialog.value = false
    }

    fun clearError() {
        if (_uiState.value is SignupUiState.Error) {
            _uiState.value = SignupUiState.Idle
        }
    }
}