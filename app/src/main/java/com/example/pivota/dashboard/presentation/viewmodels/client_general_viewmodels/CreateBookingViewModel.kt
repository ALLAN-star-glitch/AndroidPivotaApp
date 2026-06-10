package com.example.pivota.dashboard.presentation.viewmodels.client_general_viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pivota.core.network.ApiResult
import com.example.pivota.core.network.getUserFriendlyMessage
import com.example.pivota.dashboard.domain.model.listings_models.professionals.Booking
import com.example.pivota.dashboard.domain.model.listings_models.professionals.CreateBookingRequest
import com.example.pivota.dashboard.domain.model.listings_models.professionals.DurationType
import com.example.pivota.dashboard.domain.model.listings_models.professionals.ServiceOffering
import com.example.pivota.dashboard.domain.useCase.CreateBookingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CreateBookingViewModel @Inject constructor(
    private val createBookingUseCase: CreateBookingUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateBookingUiState())
    val uiState: StateFlow<CreateBookingUiState> = _uiState.asStateFlow()

    // Navigation events
    private val _navigationEvent = MutableStateFlow<BookingNavigationEvent?>(null)
    val navigationEvent: StateFlow<BookingNavigationEvent?> = _navigationEvent.asStateFlow()

    /**
     * Create a booking from the screen inputs
     */
    fun createBooking(
        serviceOffering: ServiceOffering,
        contractorId: String,
        clientId: String,
        selectedDate: Date,
        durationHours: Int?,
        durationDays: Int?,
        durationWeeks: Int?,
        durationMonths: Int?,
        selectedLocation: String,
        customerNotes: String,
        proposedPrice: Double?
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            // Format date to ISO string for API
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
            val scheduledDateString = dateFormat.format(selectedDate)

            val request = CreateBookingRequest(
                serviceId = serviceOffering.externalId,
                contractorId = contractorId,
                scheduledDate = scheduledDateString,
                locationCity = selectedLocation,
                durationHours = durationHours,
                durationDays = durationDays,
                durationWeeks = durationWeeks,
                durationMonths = durationMonths,
                customerNotes = customerNotes.takeIf { it.isNotBlank() },
                proposedPrice = proposedPrice
            )

            // Log request for debugging
            logBookingRequest(request)

            val result = createBookingUseCase(request)

            when (result) {
                is ApiResult.Success -> {
                    val booking = result.data
                    logBookingSuccess(booking)

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isSuccess = true,
                            createdBooking = booking,
                            error = null
                        )
                    }
                    _navigationEvent.value = BookingNavigationEvent.BookingCreated(booking)
                }
                is ApiResult.Error -> {
                    val errorMessage = result.getUserFriendlyMessage()
                    logBookingError(errorMessage, result.technicalMessage)

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isSuccess = false,
                            error = errorMessage,
                            createdBooking = null
                        )
                    }
                    _navigationEvent.value = BookingNavigationEvent.Error(errorMessage)
                }
                ApiResult.Loading -> {
                    // Already handled
                }
            }
        }
    }

    /**
     * Create booking using DurationType enum (convenience method)
     * Fixed: Using simple if statements instead of destructuring
     */
    fun createBooking(
        serviceOffering: ServiceOffering,
        contractorId: String,
        clientId: String,
        selectedDate: Date,
        durationValue: Int,
        durationType: DurationType,
        selectedLocation: String,
        customerNotes: String,
        proposedPrice: Double?
    ) {
        // Set the appropriate duration field based on type - using simple if statements
        val durationHours: Int? = if (durationType == DurationType.HOURS) durationValue else null
        val durationDays: Int? = if (durationType == DurationType.DAYS) durationValue else null
        val durationWeeks: Int? = if (durationType == DurationType.WEEKS) durationValue else null
        val durationMonths: Int? = if (durationType == DurationType.MONTHS) durationValue else null

        createBooking(
            serviceOffering = serviceOffering,
            contractorId = contractorId,
            clientId = clientId,
            selectedDate = selectedDate,
            durationHours = durationHours,
            durationDays = durationDays,
            durationWeeks = durationWeeks,
            durationMonths = durationMonths,
            selectedLocation = selectedLocation,
            customerNotes = customerNotes,
            proposedPrice = proposedPrice
        )
    }

    fun clearNavigationEvent() {
        _navigationEvent.value = null
    }

    fun resetState() {
        _uiState.value = CreateBookingUiState()
    }

    fun resetSuccess() {
        _uiState.update { it.copy(isSuccess = false, createdBooking = null) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    // ===========================================================
    // PRIVATE LOGGING METHODS
    // ===========================================================

    private fun logBookingRequest(request: CreateBookingRequest) {
        println("🔍 ========== CREATE BOOKING REQUEST ==========")
        println("🔍 Service ID: ${request.serviceId}")
        println("🔍 Contractor ID: ${request.contractorId}")
        println("🔍 Scheduled Date: ${request.scheduledDate}")
        println("🔍 Location: ${request.locationCity}")
        println("🔍 Duration Hours: ${request.durationHours}")
        println("🔍 Duration Days: ${request.durationDays}")
        println("🔍 Duration Weeks: ${request.durationWeeks}")
        println("🔍 Duration Months: ${request.durationMonths}")
        println("🔍 Customer Notes: ${request.customerNotes}")
        println("🔍 Proposed Price: ${request.proposedPrice}")
        println("🔍 ============================================")
    }

    private fun logBookingSuccess(booking: Booking) {
        println("✅ ========== CREATE BOOKING SUCCESS ==========")
        println("✅ Booking ID: ${booking.id}")
        println("✅ External ID: ${booking.externalId}")
        println("✅ Status: ${booking.status}")
        println("✅ Service: ${booking.serviceTitle}")
        println("✅ Contractor: ${booking.contractorName}")
        println("✅ Scheduled Date: ${booking.scheduledDate}")
        println("✅ Location: ${booking.locationCity}")
        println("✅ Total Amount: ${booking.totalAmount} ${booking.currency}")
        println("✅ ============================================")
    }

    private fun logBookingError(errorMessage: String, technicalMessage: String?) {
        println("❌ ========== CREATE BOOKING ERROR ==========")
        println("❌ Error: $errorMessage")
        println("❌ Technical: $technicalMessage")
        println("❌ ==========================================")
    }
}

// ===========================================================
// UI STATE
// ===========================================================

data class CreateBookingUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val createdBooking: Booking? = null
)

// ===========================================================
// NAVIGATION EVENTS
// ===========================================================

sealed class BookingNavigationEvent {
    data class BookingCreated(val booking: Booking) : BookingNavigationEvent()
    data class Error(val message: String) : BookingNavigationEvent()
}