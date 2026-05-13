package com.example.pivota.welcome.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pivota.core.preferences.PivotaDataStore
import com.example.pivota.welcome.presentation.state.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PurposeSelectionViewModel @Inject constructor(
    private val datastore: PivotaDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(PurposeSelectionUiState())
    val uiState: StateFlow<PurposeSelectionUiState> = _uiState.asStateFlow()

    fun selectPurpose(purpose: String) {
        _uiState.update {
            it.copy(
                selectedPurpose = purpose,
                showBottomSheet = false
            )
        }
    }

    fun updateJobSeekerData(data: JobSeekerFormData) {
        _uiState.update { it.copy(jobSeekerData = data) }
    }

    fun updateSkilledProfessionalData(data: SkilledProfessionalFormData) {
        _uiState.update { it.copy(skilledProfessionalData = data) }
    }

    fun updateAgentData(data: AgentFormData) {
        _uiState.update { it.copy(agentData = data) }
    }

    fun updateHousingSeekerData(data: HousingSeekerFormData) {
        _uiState.update { it.copy(housingSeekerData = data) }
    }

    fun updateSupportBeneficiaryData(data: SupportBeneficiaryFormData) {
        _uiState.update { it.copy(supportBeneficiaryData = data) }
    }

    fun updateEmployerData(data: EmployerFormData) {
        _uiState.update { it.copy(employerData = data) }
    }

    fun updatePropertyOwnerData(data: PropertyOwnerFormData) {
        _uiState.update { it.copy(propertyOwnerData = data) }
    }

    fun showBottomSheet() {
        _uiState.update { it.copy(showBottomSheet = true) }
    }

    fun hideBottomSheet() {
        _uiState.update { it.copy(showBottomSheet = false) }
    }

    /**
     * Convert display name to API enum value
     */
    private fun mapToApiPurpose(displayPurpose: String): String {
        return when (displayPurpose) {
            "Find a Job" -> "FIND_JOB"
            "Offer Skilled Services" -> "OFFER_SKILLED_SERVICES"
            "Work as Agent" -> "WORK_AS_AGENT"
            "Find Housing" -> "FIND_HOUSING"
            "Get Social Support" -> "GET_SOCIAL_SUPPORT"
            "Hire Employees" -> "HIRE_EMPLOYEES"
            "List Properties" -> "LIST_PROPERTIES"
            "Just Exploring" -> "JUST_EXPLORING"
            else -> displayPurpose.uppercase() // Fallback
        }
    }

    fun confirmSelection() {
        val displayPurpose = _uiState.value.selectedPurpose
        println("🔍 DEBUG: confirmSelection called with displayPurpose = '$displayPurpose'")

        if (displayPurpose != null && canProceed()) {
            viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true) }

                try {
                    // Convert display purpose to API enum value
                    val apiPurpose = mapToApiPurpose(displayPurpose)
                    println("🔍 DEBUG: About to save purpose = '$apiPurpose'")

                    // ALWAYS save the purpose, even for "Just Exploring"
                    datastore.setPrimaryPurpose(apiPurpose)
                    println("🔍 DEBUG: Purpose saved successfully")

                    // Verify the save
                    val savedPurpose = datastore.getPrimaryPurpose()
                    println("🔍 DEBUG: Verified saved purpose = '$savedPurpose'")

                    // Cache purpose-specific data based on the purpose
                    when (apiPurpose) {
                        "FIND_JOB" -> {
                            val data = _uiState.value.jobSeekerData
                            datastore.setJobSeekerData(
                                com.example.pivota.core.preferences.JobSeekerData(
                                    headline = data.headline,
                                    isActivelySeeking = data.isActivelySeeking,
                                    skills = parseCommaSeparated(data.skills),
                                    industries = parseCommaSeparated(data.industries),
                                    seniorityLevel = data.seniorityLevel.takeIf { it.isNotBlank() },
                                    expectedSalary = data.expectedSalary.toIntOrNull()
                                )
                            )
                            println("🔍 DEBUG: Saved JobSeekerData")
                        }
                        "OFFER_SKILLED_SERVICES" -> {
                            val data = _uiState.value.skilledProfessionalData
                            datastore.setSkilledProfessionalData(
                                com.example.pivota.core.preferences.SkilledProfessionalData(
                                    profession = if (data.profession == "Other") data.otherProfession else data.profession,
                                    specialties = parseCommaSeparated(data.specialties),
                                    serviceAreas = parseCommaSeparated(data.serviceAreas),
                                    yearsExperience = data.yearsExperience.toIntOrNull(),
                                    licenseNumber = data.licenseNumber.takeIf { it.isNotBlank() },
                                    hourlyRate = data.hourlyRate.toIntOrNull()
                                )
                            )
                            println("🔍 DEBUG: Saved SkilledProfessionalData")
                        }
                        "WORK_AS_AGENT" -> {
                            val data = _uiState.value.agentData
                            datastore.setIntermediaryAgentData(
                                com.example.pivota.core.preferences.IntermediaryAgentData(
                                    agentType = data.agentType,
                                    specializations = parseCommaSeparated(data.specializations),
                                    serviceAreas = parseCommaSeparated(data.serviceAreas),
                                    licenseNumber = data.licenseNumber.takeIf { it.isNotBlank() },
                                    commissionRate = data.commissionRate.toDoubleOrNull()
                                )
                            )
                            println("🔍 DEBUG: Saved IntermediaryAgentData")
                        }
                        "FIND_HOUSING" -> {
                            val data = _uiState.value.housingSeekerData
                            datastore.setHousingSeekerData(
                                com.example.pivota.core.preferences.HousingSeekerData(
                                    searchType = data.searchType.takeIf { it.isNotBlank() },
                                    isLookingForRental = data.isLookingForRental,
                                    isLookingToBuy = data.isLookingToBuy,
                                    propertyTypes = data.propertyTypes,
                                )
                            )
                            println("🔍 DEBUG: Saved HousingSeekerData with searchType=${data.searchType}, propertyTypes=${data.propertyTypes}")
                        }
                        "GET_SOCIAL_SUPPORT" -> {
                            val data = _uiState.value.supportBeneficiaryData
                            datastore.setSupportBeneficiaryData(
                                com.example.pivota.core.preferences.SupportBeneficiaryData(
                                    needs = data.supportTypes,
                                    urgentNeeds = parseCommaSeparated(data.urgentNeeds),
                                    city = data.location.takeIf { it.isNotBlank() },
                                    familySize = data.familySize.toIntOrNull()
                                )
                            )
                            println("🔍 DEBUG: Saved SupportBeneficiaryData")
                        }
                        "HIRE_EMPLOYEES" -> {
                            val data = _uiState.value.employerData
                            datastore.setEmployerData(
                                com.example.pivota.core.preferences.EmployerData(
                                    businessName = data.businessName,
                                    industry = if (data.industrySector == "Other") data.otherIndustry else data.industrySector,
                                    companySize = data.companySize,
                                    description = data.preferredSkills
                                )
                            )
                            println("🔍 DEBUG: Saved EmployerData")
                        }
                        "LIST_PROPERTIES" -> {
                            val data = _uiState.value.propertyOwnerData
                            datastore.setPropertyOwnerData(
                                com.example.pivota.core.preferences.PropertyOwnerData(
                                    listingType = data.listingType.takeIf { it.isNotBlank() },
                                    isListingForRent = data.isListingForRent,
                                    isListingForSale = data.isListingForSale,
                                    propertyCount = data.propertyCount.toIntOrNull(),
                                    propertyTypes = parseCommaSeparated(data.propertyTypes),
                                    serviceAreas = parseCommaSeparated(data.serviceAreas)
                                )
                            )
                            println("🔍 DEBUG: Saved PropertyOwnerData with listingType=${data.listingType}")
                        }
                        "JUST_EXPLORING" -> {
                            // No additional data to cache, but purpose is already saved
                            println("🔍 DEBUG: Just Exploring - no additional data to cache")
                        }
                    }

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isConfirmed = true
                        )
                    }
                    println("🔍 DEBUG: confirmSelection completed successfully")
                } catch (e: Exception) {
                    println("🔍 DEBUG: Error in confirmSelection: ${e.message}")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = e.message ?: "Failed to save selection"
                        )
                    }
                }
            }
        } else {
            println("🔍 DEBUG: confirmSelection skipped - displayPurpose='$displayPurpose', canProceed=${canProceed()}")
        }
    }

    private fun parseCommaSeparated(input: String): List<String> {
        return input.split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
    }

    fun canProceed(): Boolean {
        val purpose = _uiState.value.selectedPurpose
        return if (purpose == "Just Exploring") {
            true
        } else if (purpose != null) {
            when (purpose) {
                "Find a Job" -> _uiState.value.jobSeekerData.headline.isNotBlank()
                "Offer Skilled Services" -> _uiState.value.skilledProfessionalData.profession.isNotBlank()
                "Work as Agent" -> _uiState.value.agentData.agentType.isNotBlank()
                "Find Housing" -> {
                    val data = _uiState.value.housingSeekerData
                    data.searchType.isNotBlank() && data.propertyTypes.isNotEmpty()
                }
                "Get Social Support" -> _uiState.value.supportBeneficiaryData.supportTypes.isNotEmpty()
                "Hire Employees" -> _uiState.value.employerData.businessName.isNotBlank()
                "List Properties" -> {
                    val data = _uiState.value.propertyOwnerData
                    data.professionalStatus.isNotBlank()
                }
                else -> true
            }
        } else {
            false
        }
    }

    fun reset() {
        viewModelScope.launch {
            try {
                datastore.clear()
            } catch (e: Exception) {
                // Ignore clearing error
            }
            _uiState.value = PurposeSelectionUiState()
        }
    }
}