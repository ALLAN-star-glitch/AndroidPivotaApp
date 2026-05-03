package com.example.pivota.dashboard.presentation.viewmodels


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pivota.R
import com.example.pivota.dashboard.domain.ListingStatus
import com.example.pivota.dashboard.presentation.state.HousingListingUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HouseListingsViewModel @Inject constructor() : ViewModel() {

    private val _allListings = MutableStateFlow<List<HousingListingUiModel>>(emptyList())
    val allListings: StateFlow<List<HousingListingUiModel>> = _allListings

    private val _filteredListings = MutableStateFlow<List<HousingListingUiModel>>(emptyList())
    val filteredListings: StateFlow<List<HousingListingUiModel>> = _filteredListings

    init {
        loadSampleListings()
    }

    private fun loadSampleListings() {
        viewModelScope.launch {
            // This would typically come from a repository
            _allListings.value = getSampleHousingListings()
            _filteredListings.value = _allListings.value
        }
    }

    private fun getSampleHousingListings(): List<HousingListingUiModel> {
        return listOf(
            HousingListingUiModel(
                id = "1",
                price = "KSh 45,000",
                title = "2 Bedroom Apartment",
                location = "Syokimau",
                propertyType = "Apartment",
                rating = 4.8,
                isVerified = true,
                description = "Spacious 2BR with balcony, fitted kitchen, 24/7 security",
                isForSale = false,
                imageRes = R.drawable.property_placeholder2,
                bedrooms = 2,
                bathrooms = 2,
                squareMeters = 85,
                status = ListingStatus.ACTIVE,
                views = 234,
                messages = 12,
                requests = 5
            ),
            HousingListingUiModel(
                id = "2",
                price = "KSh 4.5M",
                title = "Luxury Villa",
                location = "Karen",
                propertyType = "House",
                rating = 4.9,
                isVerified = true,
                description = "4BR villa with garden, pool, and servant quarters",
                isForSale = true,
                imageRes = R.drawable.property_placeholder3,
                bedrooms = 4,
                bathrooms = 4,
                squareMeters = 350,
                status = ListingStatus.ACTIVE,
                views = 567,
                messages = 23,
                requests = 8
            ),
            HousingListingUiModel(
                id = "3",
                price = "KSh 35,000",
                title = "Studio Apartment",
                location = "Kilimani",
                propertyType = "Studio",
                rating = 4.3,
                isVerified = true,
                description = "Modern studio, near Yaya Centre, water included",
                isForSale = false,
                imageRes = R.drawable.property_placeholder4,
                bedrooms = 1,
                bathrooms = 1,
                squareMeters = 45,
                status = ListingStatus.ACTIVE,
                views = 189,
                messages = 8,
                requests = 3
            ),
            HousingListingUiModel(
                id = "4",
                price = "KSh 22,000",
                title = "Modern Bedsitter",
                location = "Ruiru",
                propertyType = "Bedsitter",
                rating = 4.5,
                isVerified = true,
                description = "Self-contained bedsitter with parking, near Tuskys",
                isForSale = false,
                imageRes = R.drawable.property_placeholder1,
                bedrooms = 1,
                bathrooms = 1,
                squareMeters = 70,
                status = ListingStatus.PENDING,
                views = 98,
                messages = 4,
                requests = 2
            )
        )
    }
}