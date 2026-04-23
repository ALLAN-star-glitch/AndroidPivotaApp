package com.example.pivota.dashboard.presentation.state

import com.example.pivota.dashboard.domain.model.EmployerType
import com.example.pivota.dashboard.domain.model.ListingStatus

data class JobListingUiModel(
    val id: String,
    val title: String,
    val company: String,
    val location: String,
    val salary: String,
    val jobType: String, // Full-time, Part-time, Contract, Internship
    val description: String,
    val employerType: EmployerType,
    val isVerified: Boolean,
    val rating: Double,
    val postedDate: String,
    val applicationDeadline: String? = null,
    val imageRes: Int? = null,
    val status: ListingStatus,
    val views: Int,
    val applications: Int
)