package com.example.pivota.dashboard.presentation.viewmodels.client_admin_viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pivota.dashboard.domain.ListingFilter
import com.example.pivota.dashboard.domain.ListingStatus
import com.example.pivota.dashboard.domain.ListingType
import com.example.pivota.dashboard.domain.model.listings_models.general.Listing
import com.example.pivota.dashboard.domain.model.listings_models.general.ListingCategory
import com.example.pivota.dashboard.presentation.ui_models.ListingUiModel
import com.example.pivota.dashboard.presentation.ui_models.PerformanceHint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyListingsViewModel @Inject constructor() : ViewModel() {

    private val _allListings = MutableStateFlow<List<Listing>>(emptyList())
    private val _currentStatusFilter = MutableStateFlow(ListingFilter.ALL)

    val currentStatusFilter: StateFlow<ListingFilter> = _currentStatusFilter.asStateFlow()

    val filteredListings: StateFlow<List<ListingUiModel>> = combine(
        _allListings,
        _currentStatusFilter
    ) { listings, statusFilter ->
        listings
            .filter { listing ->
                if (statusFilter == ListingFilter.ALL) true else listing.status == statusFilter
            }
            .map { it.toUiModel() }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Companion.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init { loadListings() }

    private fun loadListings() {
        viewModelScope.launch {
            _allListings.value = listOf(
                Listing(
                    "1",
                    "Senior Electrician",
                    ListingType.JOBS,
                    ListingFilter.ACTIVE,
                    "Urgent contract for industrial wiring in Nairobi"
                ),
                Listing(
                    "2",
                    "Studio Apartment",
                    ListingType.HOUSING,
                    ListingFilter.PENDING,
                    "Modern studio in Westlands, fully furnished with city views"
                ),
                Listing(
                    "3",
                    "Catering Service",
                    ListingType.SERVICES,
                    ListingFilter.CLOSED,
                    "Event catering for up to 200 guests. Standard & Premium menus available"
                ),
                Listing(
                    "4",
                    "UI/UX Designer",
                    ListingType.JOBS,
                    ListingFilter.ACTIVE,
                    "Looking for a creative designer for a 3-month fintech project"
                ),
                Listing(
                    "5",
                    "Two Bedroom Mansionette",
                    ListingType.HOUSING,
                    ListingFilter.ACTIVE,
                    "Spacious family home in Syokimau, near the SGR station"
                ),
                Listing(
                    "6",
                    "Plumbing Repairs",
                    ListingType.SERVICES,
                    ListingFilter.PENDING,
                    "General plumbing, leak detection, and bathroom fittings"
                ),
                Listing(
                    "7",
                    "Project Manager",
                    ListingType.JOBS,
                    ListingFilter.CLOSED,
                    "Handled construction site oversight for the Riverside development"
                ),
                Listing(
                    "8",
                    "Shared Office Space",
                    ListingType.HOUSING,
                    ListingFilter.ACTIVE,
                    "Desk space available in a vibrant tech hub in Kilimani"
                ),
                Listing(
                    "9",
                    "Social Media Management",
                    ListingType.SERVICES,
                    ListingFilter.ACTIVE,
                    "Grow your brand with professional content strategy and engagement"
                ),
                Listing(
                    "10",
                    "Warehouse Assistant",
                    ListingType.JOBS,
                    ListingFilter.PENDING,
                    "Shift-based work for logistics company in Industrial Area"
                )
            )
        }
    }

    fun updateStatusFilter(status: ListingFilter) { _currentStatusFilter.value = status }

    private fun Listing.toUiModel(): ListingUiModel {
        val mockViews = (10..250).random()
        val mockMsgs = (0..15).random()

        // Explicitly mapping labels to singular versions as requested
        val displayLabel = when(this.type) {
            ListingType.JOBS -> "Job"
            ListingType.HOUSING -> "House"
            ListingType.SERVICES -> "Service"
        }

        return ListingUiModel(
            id = this.id,
            title = this.title,
            category = ListingCategory(
                id = this.type.name.lowercase(),
                label = displayLabel
            ),
            status = when (this.status) {
                ListingFilter.ACTIVE -> ListingStatus.ACTIVE
                ListingFilter.PENDING -> ListingStatus.PENDING
                ListingFilter.CLOSED -> ListingStatus.CLOSED
                else -> ListingStatus.ACTIVE
            },
            descriptionPreview = this.description,
            views = mockViews,
            messages = mockMsgs,
            requests = (0..8).random(),
            performanceHint = when {
                mockMsgs > 10 -> PerformanceHint.HighInterest
                this.status == ListingFilter.PENDING -> PerformanceHint.NewResponses
                mockViews > 200 -> PerformanceHint.Custom("Trending in your area")
                else -> null
            }
        )
    }
}