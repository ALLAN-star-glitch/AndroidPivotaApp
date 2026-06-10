package com.example.pivota.dashboard.domain.useCase

import com.example.pivota.core.network.ApiResult
import com.example.pivota.dashboard.domain.model.listings_models.professionals.Booking
import com.example.pivota.dashboard.domain.model.listings_models.professionals.CreateBookingRequest
import com.example.pivota.dashboard.domain.repository.BookingRepository
import javax.inject.Inject

class CreateBookingUseCase @Inject constructor(
    private val repository: BookingRepository
) {

    /**
     * Create a new booking
     * @param request The booking creation request
     * @return ApiResult containing the created booking
     */
    suspend operator fun invoke(
        request: CreateBookingRequest
    ): ApiResult<Booking> {
        return repository.createBooking(request)
    }
}