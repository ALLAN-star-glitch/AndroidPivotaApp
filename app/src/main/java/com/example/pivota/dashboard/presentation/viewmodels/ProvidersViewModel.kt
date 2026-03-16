package com.example.pivota.dashboard.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pivota.R
import com.example.pivota.dashboard.presentation.screens.EnhancedProfessionalData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfessionalsViewModel @Inject constructor() : ViewModel() {

    private val _allProfessionals = MutableStateFlow<List<EnhancedProfessionalData>>(emptyList())
    val allProfessionals: StateFlow<List<EnhancedProfessionalData>> = _allProfessionals

    private val _filteredProfessionals = MutableStateFlow<List<EnhancedProfessionalData>>(emptyList())
    val filteredProfessionals: StateFlow<List<EnhancedProfessionalData>> = _filteredProfessionals

    // Filter states
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory

    private val _minRating = MutableStateFlow(0.0)
    val minRating: StateFlow<Double> = _minRating

    private val _verifiedOnly = MutableStateFlow(false)
    val verifiedOnly: StateFlow<Boolean> = _verifiedOnly

    init {
        loadSampleProfessionals()
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        applyFilters()
    }

    fun updateCategory(category: String) {
        _selectedCategory.value = category
        applyFilters()
    }

    fun updateMinRating(rating: Double) {
        _minRating.value = rating
        applyFilters()
    }

    fun updateVerifiedOnly(verified: Boolean) {
        _verifiedOnly.value = verified
        applyFilters()
    }

    fun resetFilters() {
        _selectedCategory.value = "All"
        _minRating.value = 0.0
        _verifiedOnly.value = false
        _searchQuery.value = ""
        applyFilters()
    }

    private fun applyFilters() {
        viewModelScope.launch {
            val query = _searchQuery.value.lowercase()
            val category = _selectedCategory.value
            val minRating = _minRating.value
            val verifiedOnly = _verifiedOnly.value

            _filteredProfessionals.value = _allProfessionals.value.filter { professional ->
                var matches = true

                // Apply category filter
                if (category != "All") {
                    matches = matches && professional.category == category
                }

                // Apply search filter
                if (query.isNotEmpty()) {
                    matches = matches && (
                            professional.name.lowercase().contains(query) ||
                                    professional.businessName?.lowercase()?.contains(query) == true ||
                                    professional.category.lowercase().contains(query) ||
                                    professional.location.lowercase().contains(query) ||
                                    professional.specialties.any { it.lowercase().contains(query) }
                            )
                }

                // Apply rating filter
                if (minRating > 0) {
                    matches = matches && professional.rating >= minRating
                }

                // Apply verified filter
                if (verifiedOnly) {
                    matches = matches && professional.isVerified
                }

                matches
            }
        }
    }

    private fun loadSampleProfessionals() {
        viewModelScope.launch {
            _allProfessionals.value = getSampleProfessionals()
            _filteredProfessionals.value = _allProfessionals.value
        }
    }

    private fun getSampleProfessionals(): List<EnhancedProfessionalData> {
        return listOf(
            EnhancedProfessionalData(
                id = "1",
                name = "Musa Jallow",
                businessName = "Musa Electrical Services",
                profileImageRes = R.drawable.job_placeholder2,
                coverImageRes = R.drawable.happy_clients,
                category = "Electrician",
                specialties = listOf("Solar Installation", "Wiring", "Emergency Repairs"),
                rating = 4.8,
                reviewCount = 32,
                description = "Licensed electrician with expertise in solar panel installation and emergency electrical repairs.",
                experienceYears = 8,
                location = "Westlands, Nairobi",
                serviceRadius = "15 km",
                startingPrice = 1500,
                isVerified = true,
                isSmartMatch = true,
                responseTime = "< 10 mins",
                availability = listOf("Today", "Weekends"),
                completedJobs = 156
            ),
            EnhancedProfessionalData(
                id = "2",
                name = "Sarah Wanjiku",
                businessName = "Sarah Interior Designs",
                profileImageRes = R.drawable.job_placeholder4,
                coverImageRes = R.drawable.happy_people,
                category = "Designer",
                specialties = listOf("Space Planning", "Furniture Selection", "Color Consulting"),
                rating = 4.9,
                reviewCount = 47,
                description = "Award-winning interior designer specializing in modern African aesthetics.",
                experienceYears = 6,
                location = "Kilimani, Nairobi",
                serviceRadius = "10 km",
                startingPrice = 2500,
                isVerified = true,
                isSmartMatch = false,
                responseTime = "< 30 mins",
                availability = listOf("Weekdays", "Weekends"),
                completedJobs = 89
            ),
            EnhancedProfessionalData(
                id = "3",
                name = "James Omondi",
                businessName = "Pipemasters Plumbing",
                profileImageRes = R.drawable.job_placeholder3,
                coverImageRes = R.drawable.organization,
                category = "Plumber",
                specialties = listOf("Pipe Repair", "Water Heaters", "Bathroom Installation"),
                rating = 4.7,
                reviewCount = 28,
                description = "Professional plumber with 5+ years experience in residential and commercial plumbing.",
                experienceYears = 5,
                location = "Kilimani, Nairobi",
                serviceRadius = "20 km",
                startingPrice = 1200,
                isVerified = true,
                isSmartMatch = true,
                responseTime = "< 15 mins",
                availability = listOf("Today", "24/7"),
                completedJobs = 203
            ),
            EnhancedProfessionalData(
                id = "4",
                name = "Pivota Housing Ltd",
                businessName = null,
                profileImageRes = R.drawable.job_placeholder3,
                coverImageRes = R.drawable.services,
                category = "Property Management",
                specialties = listOf("Maintenance", "Tenant Management", "Inspections"),
                rating = 4.6,
                reviewCount = 89,
                description = "Full-service property management company serving Mombasa and surrounding areas.",
                experienceYears = 10,
                location = "Mombasa",
                serviceRadius = "30 km",
                startingPrice = 5000,
                isVerified = true,
                isSmartMatch = true,
                responseTime = "< 1 hour",
                availability = listOf("Weekdays"),
                completedJobs = 412
            ),
            EnhancedProfessionalData(
                id = "5",
                name = "Legal Aid Kenya",
                businessName = null,
                profileImageRes = R.drawable.job_placeholder5,
                coverImageRes = R.drawable.happy_people,
                category = "Legal Services",
                specialties = listOf("Family Law", "Land Disputes", "Documentation"),
                rating = 4.9,
                reviewCount = 112,
                description = "NGO providing free legal services to underserved communities.",
                experienceYears = 15,
                location = "Nakuru",
                serviceRadius = "50 km",
                startingPrice = 0,
                isVerified = true,
                isSmartMatch = false,
                responseTime = "< 2 hours",
                availability = listOf("Weekdays", "Weekends"),
                completedJobs = 678
            ),
            EnhancedProfessionalData(
                id = "6",
                name = "John Mwangi",
                businessName = null,
                profileImageRes = R.drawable.job_placeholder1,
                coverImageRes = R.drawable.happypeople,
                category = "Carpenter",
                specialties = listOf("Furniture Making", "Repairs", "Installations"),
                rating = 4.5,
                reviewCount = 18,
                description = "Skilled carpenter with 7+ years experience in custom furniture and home repairs.",
                experienceYears = 7,
                location = "Thika",
                serviceRadius = "25 km",
                startingPrice = 1800,
                isVerified = false,
                isSmartMatch = false,
                responseTime = "< 1 hour",
                availability = listOf("Weekdays"),
                completedJobs = 97
            )
        )
    }
}