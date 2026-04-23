package com.example.pivota.dashboard.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pivota.R
import com.example.pivota.dashboard.domain.model.EmployerType
import com.example.pivota.dashboard.domain.model.ListingStatus
import com.example.pivota.dashboard.presentation.state.JobListingUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JobListingsViewModel @Inject constructor() : ViewModel() {

    private val _allListings = MutableStateFlow<List<JobListingUiModel>>(emptyList())
    val allListings: StateFlow<List<JobListingUiModel>> = _allListings

    private val _filteredListings = MutableStateFlow<List<JobListingUiModel>>(emptyList())
    val filteredListings: StateFlow<List<JobListingUiModel>> = _filteredListings

    init {
        loadSampleListings()
    }

    private fun loadSampleListings() {
        viewModelScope.launch {
            _allListings.value = getSampleJobListings()
            _filteredListings.value = _allListings.value
        }
    }

    private fun getSampleJobListings(): List<JobListingUiModel> {
        return listOf(
            JobListingUiModel(
                id = "1",
                title = "Construction Foreman",
                company = "BuildWell Ltd",
                location = "Upper Hill, Nairobi",
                salary = "KSh 3,500/day",
                jobType = "Contract",
                description = "Experienced foreman needed for high-rise construction project. Must have 5+ years experience.",
                employerType = EmployerType.ORGANIZATION,
                isVerified = true,
                rating = 4.8,
                postedDate = "2 days ago",
                applicationDeadline = "2024-04-15",
                imageRes = R.drawable.job_placeholder1,
                status = ListingStatus.ACTIVE,
                views = 234,
                applications = 12
            ),
            JobListingUiModel(
                id = "2",
                title = "Junior Accountant",
                company = "FinCorp",
                location = "Westlands, Nairobi",
                salary = "KSh 55,000/month",
                jobType = "Full-time",
                description = "CPA qualified accountant with 2+ years experience in financial reporting.",
                employerType = EmployerType.ORGANIZATION,
                isVerified = true,
                rating = 4.7,
                postedDate = "3 days ago",
                applicationDeadline = "2024-04-20",
                imageRes = R.drawable.job_placeholder2,
                status = ListingStatus.ACTIVE,
                views = 189,
                applications = 8
            ),
            JobListingUiModel(
                id = "3",
                title = "Store Keeper",
                company = "Retail Solutions",
                location = "Mombasa Rd, Nairobi",
                salary = "KSh 25,000/month",
                jobType = "Full-time",
                description = "Inventory management, stock taking, and supply chain coordination.",
                employerType = EmployerType.ORGANIZATION,
                isVerified = false,
                rating = 4.2,
                postedDate = "1 week ago",
                imageRes = R.drawable.job_placeholder3,
                status = ListingStatus.ACTIVE,
                views = 98,
                applications = 4
            ),
            JobListingUiModel(
                id = "4",
                title = "Solar Installer",
                company = "Green Energy",
                location = "Karen, Nairobi",
                salary = "KSh 40,000/month",
                jobType = "Contract",
                description = "Solar panel installation and maintenance. Technical certification required.",
                employerType = EmployerType.ORGANIZATION,
                isVerified = true,
                rating = 4.9,
                postedDate = "5 days ago",
                applicationDeadline = "2024-04-10",
                imageRes = R.drawable.job_placeholder4,
                status = ListingStatus.ACTIVE,
                views = 156,
                applications = 9
            ),
            JobListingUiModel(
                id = "5",
                title = "Security Guard",
                company = "SecureForce",
                location = "Industrial Area, Nairobi",
                salary = "KSh 22,000/month",
                jobType = "Full-time",
                description = "Night shift security guard for warehouse. Must have valid guarding license.",
                employerType = EmployerType.ORGANIZATION,
                isVerified = true,
                rating = 4.5,
                postedDate = "3 days ago",
                imageRes = R.drawable.job_placeholder5,
                status = ListingStatus.ACTIVE,
                views = 210,
                applications = 15
            )
        )
    }
}