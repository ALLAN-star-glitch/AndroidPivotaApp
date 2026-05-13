package com.example.pivota.dashboard.presentation.viewmodels.client_general_viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pivota.dashboard.presentation.screens.client_general_screens.listings_screens.professionals.EnhancedProfessionalData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfessionalsViewModel @Inject constructor() : ViewModel() {

    private val _allProfessionals = MutableStateFlow<List<EnhancedProfessionalData>>(emptyList())
    val allProfessionals: StateFlow<List<EnhancedProfessionalData>> = _allProfessionals.asStateFlow()

    private val _filteredProfessionals = MutableStateFlow<List<EnhancedProfessionalData>>(emptyList())
    val filteredProfessionals: StateFlow<List<EnhancedProfessionalData>> = _filteredProfessionals.asStateFlow()

    // Filter states
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _minRating = MutableStateFlow(0.0)
    val minRating: StateFlow<Double> = _minRating.asStateFlow()

    private val _verifiedOnly = MutableStateFlow(false)
    val verifiedOnly: StateFlow<Boolean> = _verifiedOnly.asStateFlow()

    // Price range filters
    private val _minPrice = MutableStateFlow<Int?>(null)
    val minPrice: StateFlow<Int?> = _minPrice.asStateFlow()

    private val _maxPrice = MutableStateFlow<Int?>(null)
    val maxPrice: StateFlow<Int?> = _maxPrice.asStateFlow()

    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

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

    fun updatePriceRange(min: Int?, max: Int?) {
        _minPrice.value = min
        _maxPrice.value = max
        applyFilters()
    }

    fun clearPriceRange() {
        _minPrice.value = null
        _maxPrice.value = null
        applyFilters()
    }

    fun resetFilters() {
        _selectedCategory.value = "All"
        _minRating.value = 0.0
        _verifiedOnly.value = false
        _searchQuery.value = ""
        _minPrice.value = null
        _maxPrice.value = null
        applyFilters()
    }

    private fun applyFilters() {
        viewModelScope.launch {
            val query = _searchQuery.value.lowercase().trim()
            val category = _selectedCategory.value
            val minRating = _minRating.value
            val verifiedOnly = _verifiedOnly.value
            val minPrice = _minPrice.value
            val maxPrice = _maxPrice.value

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

                // Apply price range filter
                if (minPrice != null && matches) {
                    matches = matches && professional.startingPrice >= minPrice
                }
                if (maxPrice != null && matches) {
                    matches = matches && professional.startingPrice <= maxPrice
                }

                matches
            }
        }
    }

    private fun loadSampleProfessionals() {
        viewModelScope.launch {
            _isLoading.value = true
            // Simulate network delay
            delay(500)
            _allProfessionals.value = getSampleProfessionals()
            _filteredProfessionals.value = _allProfessionals.value
            _isLoading.value = false
        }
    }

    private fun getSampleProfessionals(): List<EnhancedProfessionalData> {
        return listOf(
            EnhancedProfessionalData(
                id = "1",
                name = "Musa Jallow",
                businessName = "Musa Electrical Services",
                profileImageUrl = null,  // Will use placeholder
                coverImageUrl = null,
                category = "Electrician",
                specialties = listOf("Solar Installation", "Wiring", "Emergency Repairs"),
                rating = 4.8,
                reviewCount = 32,
                description = "Licensed electrician with expertise in solar panel installation and emergency electrical repairs. Available 24/7 for emergency services.",
                experienceYears = 8,
                location = "Westlands, Nairobi",
                serviceRadius = "15 km",
                startingPrice = 1500,
                isVerified = true,
                isSmartMatch = true,
                responseTime = "< 10 mins",
                availability = listOf("Today", "Weekends"),
                completedJobs = 156,
                isFavorite = false
            ),
            EnhancedProfessionalData(
                id = "2",
                name = "Sarah Wanjiku",
                businessName = "Sarah Interior Designs",
                profileImageUrl = null,
                coverImageUrl = null,
                category = "Designer",
                specialties = listOf("Space Planning", "Furniture Selection", "Color Consulting"),
                rating = 4.9,
                reviewCount = 47,
                description = "Award-winning interior designer specializing in modern African aesthetics. Transform your space with professional design consultation.",
                experienceYears = 6,
                location = "Kilimani, Nairobi",
                serviceRadius = "10 km",
                startingPrice = 2500,
                isVerified = true,
                isSmartMatch = false,
                responseTime = "< 30 mins",
                availability = listOf("Weekdays", "Weekends"),
                completedJobs = 89,
                isFavorite = false
            ),
            EnhancedProfessionalData(
                id = "3",
                name = "James Omondi",
                businessName = "Pipemasters Plumbing",
                profileImageUrl = null,
                coverImageUrl = null,
                category = "Plumber",
                specialties = listOf("Pipe Repair", "Water Heaters", "Bathroom Installation"),
                rating = 4.7,
                reviewCount = 28,
                description = "Professional plumber with 5+ years experience in residential and commercial plumbing. Fast response and quality work guaranteed.",
                experienceYears = 5,
                location = "Kilimani, Nairobi",
                serviceRadius = "20 km",
                startingPrice = 1200,
                isVerified = true,
                isSmartMatch = true,
                responseTime = "< 15 mins",
                availability = listOf("Today", "24/7"),
                completedJobs = 203,
                isFavorite = false
            ),
            EnhancedProfessionalData(
                id = "4",
                name = "Pivota Housing Ltd",
                businessName = null,
                profileImageUrl = null,
                coverImageUrl = null,
                category = "Property Management",
                specialties = listOf("Maintenance", "Tenant Management", "Inspections"),
                rating = 4.6,
                reviewCount = 89,
                description = "Full-service property management company serving Nairobi and surrounding areas. Professional property maintenance and tenant management.",
                experienceYears = 10,
                location = "Nairobi CBD",
                serviceRadius = "30 km",
                startingPrice = 5000,
                isVerified = true,
                isSmartMatch = true,
                responseTime = "< 1 hour",
                availability = listOf("Weekdays"),
                completedJobs = 412,
                isFavorite = false
            ),
            EnhancedProfessionalData(
                id = "5",
                name = "Legal Aid Kenya",
                businessName = null,
                profileImageUrl = null,
                coverImageUrl = null,
                category = "Legal Services",
                specialties = listOf("Family Law", "Land Disputes", "Documentation"),
                rating = 4.9,
                reviewCount = 112,
                description = "NGO providing free legal services to underserved communities. Expert legal advice and representation.",
                experienceYears = 15,
                location = "Nakuru",
                serviceRadius = "50 km",
                startingPrice = 0,
                isVerified = true,
                isSmartMatch = false,
                responseTime = "< 2 hours",
                availability = listOf("Weekdays", "Weekends"),
                completedJobs = 678,
                isFavorite = false
            ),
            EnhancedProfessionalData(
                id = "6",
                name = "John Mwangi",
                businessName = null,
                profileImageUrl = null,
                coverImageUrl = null,
                category = "Carpenter",
                specialties = listOf("Furniture Making", "Repairs", "Installations"),
                rating = 4.5,
                reviewCount = 18,
                description = "Skilled carpenter with 7+ years experience in custom furniture and home repairs. Quality craftsmanship at affordable prices.",
                experienceYears = 7,
                location = "Thika",
                serviceRadius = "25 km",
                startingPrice = 1800,
                isVerified = false,
                isSmartMatch = false,
                responseTime = "< 1 hour",
                availability = listOf("Weekdays"),
                completedJobs = 97,
                isFavorite = false
            ),
            EnhancedProfessionalData(
                id = "7",
                name = "CleanPro Services",
                businessName = "CleanPro Kenya",
                profileImageUrl = null,
                coverImageUrl = null,
                category = "Cleaning Services",
                specialties = listOf("Office Cleaning", "Home Cleaning", "Deep Cleaning"),
                rating = 4.8,
                reviewCount = 156,
                description = "Professional cleaning services for homes and offices. Eco-friendly products and trained staff.",
                experienceYears = 4,
                location = "Westlands, Nairobi",
                serviceRadius = "20 km",
                startingPrice = 2000,
                isVerified = true,
                isSmartMatch = false,
                responseTime = "< 30 mins",
                availability = listOf("Today", "Tomorrow"),
                completedJobs = 234,
                isFavorite = false
            ),
            EnhancedProfessionalData(
                id = "8",
                name = "SecureGuard",
                businessName = "SecureGuard Security Ltd",
                profileImageUrl = null,
                coverImageUrl = null,
                category = "Security Services",
                specialties = listOf("CCTV Installation", "Security Guards", "Access Control"),
                rating = 4.7,
                reviewCount = 78,
                description = "Professional security services for residential and commercial properties.",
                experienceYears = 12,
                location = "Industrial Area, Nairobi",
                serviceRadius = "40 km",
                startingPrice = 8000,
                isVerified = true,
                isSmartMatch = false,
                responseTime = "< 1 hour",
                availability = listOf("Weekdays"),
                completedJobs = 345,
                isFavorite = false
            )
        )
    }

    // Helper function to toggle favorite status
    fun toggleFavorite(professionalId: String) {
        viewModelScope.launch {
            val updatedProfessionals = _allProfessionals.value.map { professional ->
                if (professional.id == professionalId) {
                    professional.copy(isFavorite = !professional.isFavorite)
                } else {
                    professional
                }
            }
            _allProfessionals.value = updatedProfessionals
            applyFilters()
        }
    }

    // Get single professional by ID
    fun getProfessionalById(id: String): EnhancedProfessionalData? {
        return _allProfessionals.value.find { it.id == id }
    }

    // Get filter counts
    fun getActiveFilterCount(): Int {
        var count = 0
        if (_minRating.value > 0) count++
        if (_verifiedOnly.value) count++
        if (_selectedCategory.value != "All") count++
        if (_minPrice.value != null || _maxPrice.value != null) count++
        if (_searchQuery.value.isNotEmpty()) count++
        return count
    }
}