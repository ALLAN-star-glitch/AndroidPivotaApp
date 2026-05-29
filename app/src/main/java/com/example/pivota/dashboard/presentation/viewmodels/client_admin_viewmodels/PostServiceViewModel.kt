package com.example.pivota.dashboard.presentation.viewmodels.client_admin_viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pivota.core.network.ApiResult
import com.example.pivota.dashboard.data.dto.CreateServiceOfferingRequestDto
import com.example.pivota.dashboard.data.dto.DayAvailabilityDto
import com.example.pivota.dashboard.domain.model.listings_models.general.Category
import com.example.pivota.dashboard.domain.model.listings_models.general.DiscoveryCategory
import com.example.pivota.dashboard.domain.model.listings_models.professionals.DayAvailability
import com.example.pivota.dashboard.domain.model.listings_models.professionals.PricingUnitOption
import com.example.pivota.dashboard.domain.model.listings_models.professionals.PricingUnitsByCategory
import com.example.pivota.dashboard.domain.model.listings_models.professionals.ServiceOffering
import com.example.pivota.dashboard.domain.useCase.CreateServiceOfferingUseCase
import com.example.pivota.dashboard.domain.useCase.GetComplimentaryCategoriesUseCase
import com.example.pivota.dashboard.domain.useCase.GetFullComplimentaryCategoriesUseCase
import com.example.pivota.dashboard.domain.useCase.GetPricingUnitsByCategoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostServiceViewModel @Inject constructor(
    private val createServiceOfferingUseCase: CreateServiceOfferingUseCase,
    private val getPricingUnitsByCategoryUseCase: GetPricingUnitsByCategoryUseCase,
    private val getFullComplimentaryCategoriesUseCase: GetFullComplimentaryCategoriesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PostServiceUiState())
    val uiState: StateFlow<PostServiceUiState> = _uiState.asStateFlow()

    // Categories state
    private val _categoriesState = MutableStateFlow<CategoriesState>(CategoriesState.Loading)
    val categoriesState: StateFlow<CategoriesState> = _categoriesState.asStateFlow()

    // Pricing units state
    private val _pricingUnitsState = MutableStateFlow<PricingUnitsState>(PricingUnitsState.Idle)
    val pricingUnitsState: StateFlow<PricingUnitsState> = _pricingUnitsState.asStateFlow()

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            _categoriesState.value = CategoriesState.Loading

            getFullComplimentaryCategoriesUseCase().collect { categories ->
                println("📋 [PostServiceViewModel] Loaded ${categories.size} categories")

                if (categories.isNotEmpty()) {
                    _categoriesState.value = CategoriesState.Success(categories)

                    // Filter top-level categories (parentId == null)
                    val topLevelCategories = categories.filter { it.parentId == null }
                    val categoryNames = topLevelCategories.map { it.name }
                    val categoryMap = topLevelCategories.associate { it.name to it.id }

                    // Build subcategory maps for each parent category
                    val subcategoryMap = mutableMapOf<String, List<Category>>()
                    val subcategoryIdMap = mutableMapOf<String, String>()

                    categories.filter { it.parentId != null }.forEach { subcategory ->
                        val parentId = subcategory.parentId
                        if (parentId != null) {
                            val parentCategory = categories.find { it.id == parentId }
                            if (parentCategory != null) {
                                val currentList = subcategoryMap[parentCategory.name] ?: emptyList()
                                subcategoryMap[parentCategory.name] = currentList + subcategory
                                subcategoryIdMap[subcategory.name] = subcategory.id
                            }
                        }
                    }

                    _uiState.update {
                        it.copy(
                            availableCategories = categoryNames,
                            categoryIdMap = categoryMap,
                            allCategories = categories,
                            subcategoryIdMap = subcategoryIdMap
                        )
                    }
                } else {
                    _categoriesState.value = CategoriesState.Error("No categories available")
                }
            }
        }
    }

    fun updateTitle(title: String) {
        _uiState.update { it.copy(title = title) }
    }

    fun updateCategory(categoryName: String, categoryId: String) {
        val categories = (_categoriesState.value as? CategoriesState.Success)?.categories ?: emptyList()
        val subcategories = categories.filter { it.parentId == categoryId }.map { it.name }

        _uiState.update {
            it.copy(
                category = categoryName,
                categoryId = categoryId,
                subcategory = "",  // Clear subcategory when category changes
                subcategoryId = "",  // Clear subcategory ID
                availableSubcategories = subcategories
            )
        }

        // Fetch pricing units when category changes
        if (categoryId.isNotBlank()) {
            fetchPricingUnitsForCategory(categoryId)
        }
    }

    fun updateSubcategory(subcategoryName: String, subcategoryId: String) {
        _uiState.update {
            it.copy(
                subcategory = subcategoryName,
                subcategoryId = subcategoryId
            )
        }
    }

    fun updateDescription(description: String) {
        _uiState.update { it.copy(description = description) }
    }

    fun updateBasePrice(price: String) {
        _uiState.update { it.copy(basePrice = price) }
        // Validate price when it changes
        validatePriceAgainstSelectedUnit(_uiState.value.priceUnit)
    }

    fun updateCurrency(currency: String) {
        _uiState.update { it.copy(currency = currency) }
    }

    fun updatePriceUnit(unit: String) {
        _uiState.update { it.copy(priceUnit = unit) }
        validatePriceAgainstSelectedUnit(unit)
    }

    fun updateYearsExperience(experience: String) {
        _uiState.update { it.copy(yearsExperience = experience) }
    }

    fun updateLocationCity(city: String) {
        _uiState.update { it.copy(locationCity = city) }
    }

    fun updateLocationNeighborhood(neighborhood: String) {
        _uiState.update { it.copy(locationNeighborhood = neighborhood) }
    }

    fun updateAdditionalNotes(notes: String) {
        _uiState.update { it.copy(additionalNotes = notes) }
    }

    fun updateAvailability(availability: List<DayAvailability>) {
        _uiState.update { it.copy(availability = availability) }
    }

    private fun fetchPricingUnitsForCategory(categoryId: String) {
        viewModelScope.launch {
            _pricingUnitsState.value = PricingUnitsState.Loading

            val result = getPricingUnitsByCategoryUseCase(categoryId)

            when (result) {
                is ApiResult.Success -> {
                    val pricingData = result.data
                    println("✅ [PostServiceViewModel] Pricing units loaded for: ${pricingData.categoryName}")
                    println("   Allowed units: ${pricingData.allowedUnits.map { it.unit }}")

                    _pricingUnitsState.value = PricingUnitsState.Success(pricingData)

                    // Update available price units in UI state
                    val allowedUnitsList = pricingData.allowedUnits.map { it.unit }
                    _uiState.update {
                        it.copy(
                            allowedPriceUnits = allowedUnitsList,
                            pricingRules = pricingData.allowedUnits
                        )
                    }

                    // If current price unit is not allowed, reset to first allowed unit
                    val currentUnit = _uiState.value.priceUnit
                    if (allowedUnitsList.isNotEmpty() && currentUnit !in allowedUnitsList) {
                        updatePriceUnit(allowedUnitsList.first())
                    }
                }
                is ApiResult.Error -> {
                    val errorMessage = result.networkError.userFriendlyMessage
                    println("❌ [PostServiceViewModel] Failed to load pricing units: $errorMessage")
                    _pricingUnitsState.value = PricingUnitsState.Error(errorMessage)
                }
                ApiResult.Loading -> {
                    // Already handled
                }
            }
        }
    }

    private fun validatePriceAgainstSelectedUnit(unit: String) {
        val currentPrice = _uiState.value.basePrice.toDoubleOrNull()
        if (currentPrice == null) return

        val pricingRules = _uiState.value.pricingRules
        val ruleForUnit = pricingRules.find { it.unit == unit }

        if (ruleForUnit != null) {
            val priceError = when {
                currentPrice < ruleForUnit.minPrice ->
                    "Price must be at least ${ruleForUnit.currency} ${ruleForUnit.minPrice} for ${ruleForUnit.label}"
                ruleForUnit.maxPrice != null && currentPrice > ruleForUnit.maxPrice ->
                    "Price cannot exceed ${ruleForUnit.currency} ${ruleForUnit.maxPrice} for ${ruleForUnit.label}"
                else -> null
            }

            _uiState.update { it.copy(priceValidationError = priceError) }
        }
    }

    fun submitService(onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, isSuccess = false) }

            // Validate required fields
            val validationError = validateForm()
            if (validationError != null) {
                _uiState.update {
                    it.copy(isLoading = false, error = validationError)
                }
                onError(validationError)
                return@launch
            }

            // Validate price against rules
            val priceError = _uiState.value.priceValidationError
            if (priceError != null) {
                _uiState.update {
                    it.copy(isLoading = false, error = priceError)
                }
                onError(priceError)
                return@launch
            }

            // Build the request
            val request = buildRequest()

            println("🔵 [PostServiceViewModel] Submitting service offering...")
            println("🔵 Title: ${request.title}")
            println("🔵 CategoryId: ${request.categoryId}")
            println("🔵 BasePrice: ${request.basePrice}")
            println("🔵 PriceUnit: ${request.priceUnit}")

            val result = createServiceOfferingUseCase(request)

            when (result) {
                is ApiResult.Success -> {
                    println("✅ [PostServiceViewModel] Service created successfully!")
                    val createdOffering = result.data.data?.firstOrNull()
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isSuccess = true,
                            error = null,
                            createdOffering = createdOffering
                        )
                    }
                    onSuccess()
                }
                is ApiResult.Error -> {
                    // Get the original message from the backend
                    val originalMessage = result.networkError.originalMessage
                    val statusCode = result.networkError.statusCode

                    println("❌ [PostServiceViewModel] Status Code: $statusCode")
                    println("❌ [PostServiceViewModel] Original Message: $originalMessage")

                    // Make the error message more user-friendly
                    val errorMessage = when {
                        originalMessage?.contains("professional-services.create.own") == true ->
                            "You don't have permission to post services. Please ensure you have a professional contractor account."
                        originalMessage?.contains("PROFILE_NOT_FOUND") == true ->
                            "Please create a professional profile first. Go to Profile → Become a Professional"
                        originalMessage?.contains("Insufficient permissions") == true ->
                            "You need professional status to post services. Please complete your professional profile."
                        !originalMessage.isNullOrBlank() -> originalMessage
                        statusCode == 403 -> "Access denied. You don't have permission to perform this action."
                        else -> "An unexpected error occurred. Please try again."
                    }

                    println("❌ [PostServiceViewModel] Final Error: $errorMessage")

                    _uiState.update {
                        it.copy(isLoading = false, error = errorMessage, isSuccess = false)
                    }
                    onError(errorMessage)
                }
                ApiResult.Loading -> {
                    // Already handled
                }
            }
        }
    }

    private fun validateForm(): String? {
        val state = _uiState.value
        return when {
            state.title.isBlank() -> "Please enter a service title"
            state.category.isBlank() -> "Please select a category"
            state.description.isBlank() -> "Please enter a service description"
            state.basePrice.isBlank() -> "Please enter a base price"
            state.basePrice.toDoubleOrNull() == null -> "Please enter a valid price"
            state.locationCity.isBlank() -> "Please enter a city/town"
            else -> null
        }
    }

    private fun buildRequest(): CreateServiceOfferingRequestDto {
        val state = _uiState.value

        // Use subcategoryId if selected, otherwise use categoryId
        val finalCategoryId = if (state.subcategoryId.isNotBlank()) {
            state.subcategoryId
        } else {
            state.categoryId
        }

        val availability = state.availability
            .filter { !it.isClosed }
            .map { day ->
                DayAvailabilityDto(
                    day = day.day,
                    open = day.open,
                    close = day.close,
                    isClosed = day.isClosed
                )
            }

        return CreateServiceOfferingRequestDto(
            title = state.title,
            description = state.description,
            categoryId = finalCategoryId,  // Use subcategory ID if available
            basePrice = state.basePrice.toDouble(),
            priceUnit = state.priceUnit,
            currency = state.currency,
            locationCity = state.locationCity,
            locationNeighborhood = state.locationNeighborhood.takeIf { it.isNotBlank() },
            yearsExperience = state.yearsExperience.toIntOrNull(),
            additionalNotes = state.additionalNotes.takeIf { it.isNotBlank() },
            availability = availability.takeIf { it.isNotEmpty() }
        )
    }

    fun resetState() {
        _uiState.value = PostServiceUiState()
        _pricingUnitsState.value = PricingUnitsState.Idle
        loadCategories() // Reload categories when resetting
    }

    fun resetError() {
        _uiState.update { it.copy(error = null) }
    }

    fun resetSuccess() {
        _uiState.update { it.copy(isSuccess = false) }
    }
}

// Categories State
sealed class CategoriesState {
    object Loading : CategoriesState()
    data class Success(val categories: List<Category>) : CategoriesState()  // Changed to Category
    data class Error(val message: String) : CategoriesState()
}

// Pricing Units State
sealed class PricingUnitsState {
    object Idle : PricingUnitsState()
    object Loading : PricingUnitsState()
    data class Success(val data: PricingUnitsByCategory) : PricingUnitsState()
    data class Error(val message: String) : PricingUnitsState()
}

// Updated UI State
data class PostServiceUiState(
    val title: String = "",
    val category: String = "",
    val categoryId: String = "",
    val subcategory: String = "",
    val subcategoryId: String = "",
    val description: String = "",
    val basePrice: String = "",
    val currency: String = "KES",
    val priceUnit: String = "PER_HOUR",
    val yearsExperience: String = "",
    val locationCity: String = "",
    val locationNeighborhood: String = "",
    val additionalNotes: String = "",
    val availability: List<DayAvailability> = emptyList(),
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val createdOffering: ServiceOffering? = null,
    // New fields for categories and pricing rules
    val availableCategories: List<String> = emptyList(),
    val categoryIdMap: Map<String, String> = emptyMap(),
    val availableSubcategories: List<String> = emptyList(),
    val subcategoryIdMap: Map<String, String> = emptyMap(),
    val allCategories: List<Category> = emptyList(),  // ADD THIS for reference
    val allowedPriceUnits: List<String> = emptyList(),
    val pricingRules: List<PricingUnitOption> = emptyList(),
    val priceValidationError: String? = null
)