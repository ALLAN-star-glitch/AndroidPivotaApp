package com.example.pivota.dashboard.presentation.viewmodels.client_admin_viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pivota.core.network.ApiResult
import com.example.pivota.core.network.getUserFriendlyMessage
import com.example.pivota.dashboard.data.dto.CreateServiceOfferingRequestDto
import com.example.pivota.dashboard.data.dto.DayAvailabilityDto
import com.example.pivota.dashboard.domain.model.listings_models.general.Category
import com.example.pivota.dashboard.domain.model.listings_models.professionals.DayAvailability
import com.example.pivota.dashboard.domain.model.listings_models.professionals.PricingUnitOption
import com.example.pivota.dashboard.domain.model.listings_models.professionals.PricingUnitsByCategory
import com.example.pivota.dashboard.domain.model.listings_models.professionals.ServiceOffering
import com.example.pivota.dashboard.domain.useCase.CreateServiceOfferingUseCase
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

    // Flag to track if categories are fully loaded
    private var areCategoriesLoaded = false

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

                    val categoryNames = categories.map { it.name }
                    val categoryMap = categories.associate { it.name to it.id }

                    // Build subcategory maps from the NESTED structure
                    val subcategoryMap = mutableMapOf<String, List<Category>>()
                    val subcategoryIdMap = mutableMapOf<String, String>()

                    categories.forEach { parentCategory ->
                        val subcategories = parentCategory.subcategories ?: emptyList()
                        if (subcategories.isNotEmpty()) {
                            subcategoryMap[parentCategory.name] = subcategories
                            subcategories.forEach { sub ->
                                subcategoryIdMap[sub.name] = sub.id
                            }
                        }
                    }

                    _uiState.update {
                        it.copy(
                            availableCategories = categoryNames,
                            categoryIdMap = categoryMap,
                            allCategories = categories,
                            subcategoryIdMap = subcategoryIdMap,
                            subcategoryMap = subcategoryMap
                        )
                    }

                    areCategoriesLoaded = true

                    println("📋 Subcategory map built. Size: ${subcategoryMap.size}")
                    if (subcategoryMap.isEmpty()) {
                        println("⚠️ No subcategories found! Check if the API is returning subcategories.")
                    }
                    subcategoryMap.forEach { (parent, subs) ->
                        println("   $parent -> ${subs.map { it.name }}")
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
        if (!areCategoriesLoaded) {
            println("📋 [PostServiceViewModel] Categories still loading, please wait...")
            return
        }

        val subcategoryMap = _uiState.value.subcategoryMap
        println("📋 updateCategory - subcategoryMap keys: ${subcategoryMap.keys}")
        println("📋 updateCategory - subcategoryMap size: ${subcategoryMap.size}")

        if (subcategoryMap.isEmpty()) {
            println("📋 [PostServiceViewModel] Subcategory map is empty, categories not ready. Ignoring selection.")
            return
        }

        val subcategories = subcategoryMap[categoryName] ?: emptyList()
        val subcategoryNames = subcategories.map { it.name }
        val subcategoryIdMap = subcategories.associate { it.name to it.id }

        println("📋 [PostServiceViewModel] Category selected: $categoryName")
        println("📋 Subcategories available: ${subcategoryNames.size}")
        if (subcategoryNames.isNotEmpty()) {
            println("📋 Subcategory names: $subcategoryNames")
        }

        _uiState.update {
            it.copy(
                category = categoryName,
                categoryId = categoryId,
                subcategory = "",
                subcategoryId = "",
                availableSubcategories = subcategoryNames,
                subcategoryIdMap = subcategoryIdMap
            )
        }

        if (categoryId.isNotBlank()) {
            fetchPricingUnitsForCategory(categoryId)
        }
    }

    fun updateSubcategory(subcategoryName: String, subcategoryId: String) {
        println("📋 [PostServiceViewModel] Subcategory selected: $subcategoryName, ID: $subcategoryId")
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

    // ✅ REPLACED locationCity and locationNeighborhood with coverageAreas
    fun updateCoverageAreas(areas: List<String>) {
        _uiState.update { it.copy(coverageAreas = areas) }
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

                    val allowedUnitsList = pricingData.allowedUnits.map { it.unit }
                    _uiState.update {
                        it.copy(
                            allowedPriceUnits = allowedUnitsList,
                            pricingRules = pricingData.allowedUnits
                        )
                    }

                    val currentUnit = _uiState.value.priceUnit
                    if (allowedUnitsList.isNotEmpty() && currentUnit !in allowedUnitsList) {
                        updatePriceUnit(allowedUnitsList.first())
                    }
                }
                is ApiResult.Error -> {
                    val errorMessage = result.getUserFriendlyMessage()
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

            val validationError = validateForm()
            if (validationError != null) {
                _uiState.update {
                    it.copy(isLoading = false, error = validationError)
                }
                onError(validationError)
                return@launch
            }

            val priceError = _uiState.value.priceValidationError
            if (priceError != null) {
                _uiState.update {
                    it.copy(isLoading = false, error = priceError)
                }
                onError(priceError)
                return@launch
            }

            val request = buildRequest()

            println("🔵 [PostServiceViewModel] Submitting service offering...")
            println("🔵 Title: ${request.title}")
            println("🔵 CategoryId: ${request.categoryId}")
            println("🔵 BasePrice: ${request.basePrice}")
            println("🔵 PriceUnit: ${request.priceUnit}")
            println("🔵 CoverageAreas: ${request.coverageAreas}")  // ✅ Updated

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
                    val errorMessage = result.getUserFriendlyMessage()

                    println("❌ [PostServiceViewModel] Submission failed: $errorMessage")
                    println("❌ Technical details: ${result.technicalMessage}")
                    println("❌ Status code: ${result.networkError.statusCode}")

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


    private fun validateNegotiablePriceRange() {
        val minPrice = _uiState.value.minNegotiablePrice.toDoubleOrNull()
        val maxPrice = _uiState.value.maxNegotiablePrice.toDoubleOrNull()
        val basePrice = _uiState.value.basePrice.toDoubleOrNull()

        val error = when {
            minPrice != null && maxPrice != null && minPrice > maxPrice ->
                "Minimum price cannot be greater than maximum price"
            minPrice != null && basePrice != null && minPrice > basePrice ->
                "Minimum price cannot be greater than your base price"
            maxPrice != null && basePrice != null && maxPrice < basePrice ->
                "Maximum price cannot be less than your base price"
            else -> null
        }

        _uiState.update { it.copy(priceValidationError = error) }
    }

    private fun validateCustomBookingFee() {
        val amount = _uiState.value.customBookingFeeAmount.toDoubleOrNull()
        val isEnabled = _uiState.value.useCustomBookingFee

        val error = when {
            isEnabled && (amount == null || amount <= 0) ->
                "Please enter a valid booking fee amount"
            isEnabled && amount != null && amount > 10000 ->
                "Booking fee cannot exceed 10,000 KES"
            else -> null
        }

        _uiState.update { it.copy(bookingFeeValidationError = error) }
    }

    private fun validateForm(): String? {
        val state = _uiState.value
        return when {
            state.title.isBlank() -> "Please enter a service title"
            state.category.isBlank() -> "Please select a category"
            state.description.isBlank() -> "Please enter a service description"
            state.basePrice.isBlank() -> "Please enter a base price"
            state.basePrice.toDoubleOrNull() == null -> "Please enter a valid price"
            state.coverageAreas.isEmpty() -> "Please select at least one service area"
            // ========== NEW validations ==========
            state.useCustomBookingFee && state.customBookingFeeAmount.isBlank() -> "Please enter a booking fee amount"
            state.useCustomBookingFee && state.customBookingFeeAmount.toDoubleOrNull() == null -> "Please enter a valid booking fee amount"
            else -> null
        }
    }

    private fun buildRequest(): CreateServiceOfferingRequestDto {
        val state = _uiState.value

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
            categoryId = finalCategoryId,
            basePrice = state.basePrice.toDouble(),
            priceUnit = state.priceUnit,
            currency = state.currency,
            coverageAreas = state.coverageAreas,
            yearsExperience = state.yearsExperience.toIntOrNull(),
            additionalNotes = state.additionalNotes.takeIf { it.isNotBlank() },
            availability = availability.takeIf { it.isNotEmpty() },
            // ========== NEW: Negotiable Pricing Fields ==========
            isNegotiable = state.isNegotiable,
            minNegotiablePrice = state.minNegotiablePrice.toDoubleOrNull(),
            maxNegotiablePrice = state.maxNegotiablePrice.toDoubleOrNull(),
            // ========== NEW: Booking Fee Override Fields ==========
            useCustomBookingFee = state.useCustomBookingFee,
            customBookingFeeEnabled = state.useCustomBookingFee,
            customBookingFeeAmount = state.customBookingFeeAmount.toDoubleOrNull(),
            customBookingFeeCurrency = "KES",
            customBookingFeeDescription = state.customBookingFeeDescription.takeIf { it.isNotBlank() },
            customBookingFeeRefundable = state.customBookingFeeRefundable
        )
    }

    fun resetState() {
        _uiState.value = PostServiceUiState()
        _pricingUnitsState.value = PricingUnitsState.Idle
        areCategoriesLoaded = false
        loadCategories()
    }

    fun resetError() {
        _uiState.update { it.copy(error = null) }
    }

    fun resetSuccess() {
        _uiState.update { it.copy(isSuccess = false) }
    }

    // ========== NEW: Update functions for negotiable pricing ==========
    fun updateIsNegotiable(isNegotiable: Boolean) {
        _uiState.update { it.copy(isNegotiable = isNegotiable) }
    }

    fun updateMinNegotiablePrice(price: String) {
        _uiState.update { it.copy(minNegotiablePrice = price) }
        if (price.isNotBlank()) {
            validateNegotiablePriceRange()
        }
    }

    fun updateMaxNegotiablePrice(price: String) {
        _uiState.update { it.copy(maxNegotiablePrice = price) }
        if (price.isNotBlank()) {
            validateNegotiablePriceRange()
        }
    }

    // ========== NEW: Update functions for booking fee ==========
    fun updateUseCustomBookingFee(useCustomFee: Boolean) {
        _uiState.update { it.copy(useCustomBookingFee = useCustomFee) }
        if (!useCustomFee) {
            _uiState.update {
                it.copy(
                    customBookingFeeAmount = "",
                    customBookingFeeDescription = "",
                    customBookingFeeRefundable = false,
                    bookingFeeValidationError = null
                )
            }
        }
    }

    fun updateCustomBookingFeeAmount(amount: String) {
        _uiState.update { it.copy(customBookingFeeAmount = amount) }
        validateCustomBookingFee()
    }

    fun updateCustomBookingFeeDescription(description: String) {
        _uiState.update { it.copy(customBookingFeeDescription = description) }
    }

    fun updateCustomBookingFeeRefundable(refundable: Boolean) {
        _uiState.update { it.copy(customBookingFeeRefundable = refundable) }
    }
}

// Categories State
sealed class CategoriesState {
    object Loading : CategoriesState()
    data class Success(val categories: List<Category>) : CategoriesState()
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
    val coverageAreas: List<String> = emptyList(),
    val additionalNotes: String = "",
    val availability: List<DayAvailability> = emptyList(),
    // ========== NEW: Negotiable Pricing Fields ==========
    val isNegotiable: Boolean = true,
    val minNegotiablePrice: String = "",
    val maxNegotiablePrice: String = "",
    // ========== NEW: Booking Fee Override Fields ==========
    val useCustomBookingFee: Boolean = false,
    val customBookingFeeAmount: String = "",
    val customBookingFeeDescription: String = "",
    val customBookingFeeRefundable: Boolean = false,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val createdOffering: ServiceOffering? = null,
    val availableCategories: List<String> = emptyList(),
    val categoryIdMap: Map<String, String> = emptyMap(),
    val availableSubcategories: List<String> = emptyList(),
    val subcategoryIdMap: Map<String, String> = emptyMap(),
    val subcategoryMap: Map<String, List<Category>> = emptyMap(),
    val allCategories: List<Category> = emptyList(),
    val allowedPriceUnits: List<String> = emptyList(),
    val pricingRules: List<PricingUnitOption> = emptyList(),
    val priceValidationError: String? = null,
    // ========== NEW: Booking Fee Validation Error ==========
    val bookingFeeValidationError: String? = null
)