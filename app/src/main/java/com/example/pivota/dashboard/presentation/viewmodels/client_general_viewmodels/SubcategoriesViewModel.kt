package com.example.pivota.dashboard.presentation.viewmodels.client_general_viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pivota.dashboard.domain.model.listings_models.general.Category  // Changed from DiscoveryCategory
import com.example.pivota.dashboard.domain.useCase.GetSubcategoriesUseCase
import com.example.pivota.dashboard.presentation.state.SubcategoriesUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

/**
 * ViewModel for managing subcategories screen state
 */
@HiltViewModel
class SubcategoriesViewModel @Inject constructor(
    private val getSubcategoriesUseCase: GetSubcategoriesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<SubcategoriesUiState>(SubcategoriesUiState.Loading)
    val uiState: StateFlow<SubcategoriesUiState> = _uiState.asStateFlow()

    // Cache for subcategories by parent category ID - using Category type
    private val cachedSubcategories = mutableMapOf<String, List<Category>>()
    private var currentParentCategoryId: String = ""

    /**
     * Load subcategories for a given parent category
     * @param parentCategoryId The ID of the parent category
     * @param forceRefresh Force refresh from network even if cached data exists
     */
    fun loadSubcategories(parentCategoryId: String, forceRefresh: Boolean = false) {
        currentParentCategoryId = parentCategoryId

        // Check cache first (unless force refresh is requested)
        if (!forceRefresh && cachedSubcategories.containsKey(parentCategoryId)) {
            val subcategories = cachedSubcategories[parentCategoryId]!!
            println("📦 [SubcategoriesViewModel] Using cached subcategories for parent: $parentCategoryId (${subcategories.size} items)")

            if (subcategories.isEmpty()) {
                _uiState.value = SubcategoriesUiState.Success(emptyList())
            } else {
                _uiState.value = SubcategoriesUiState.Success(subcategories)
            }
            return
        }

        println("🌐 [SubcategoriesViewModel] Fetching subcategories from network for parent: $parentCategoryId")

        // Reset to loading state only when not using cache
        _uiState.value = SubcategoriesUiState.Loading

        getSubcategoriesUseCase(parentCategoryId)
            .onEach { subcategories ->
                println("🔵 [SubcategoriesViewModel] Received ${subcategories.size} subcategories from network for parent: $parentCategoryId")

                // Cache the results
                cachedSubcategories[parentCategoryId] = subcategories

                if (subcategories.isEmpty()) {
                    // No subcategories found - this is still a success state (empty list)
                    _uiState.value = SubcategoriesUiState.Success(emptyList())
                    println("✅ No subcategories found for parent: $parentCategoryId")
                } else {
                    _uiState.value = SubcategoriesUiState.Success(subcategories)
                    println("✅ Loaded and cached ${subcategories.size} subcategories for parent: $parentCategoryId")

                    // Log subcategories for debugging
                    println("========== SUBCATEGORIES DATA ==========")
                    subcategories.forEachIndexed { index, category ->
                        println("${index + 1}. Subcategory: '${category.name}'")
                        println("   - ID: ${category.id}")
                        println("   - hasSubcategories: ${category.hasSubcategories}")
                        println("   - type: ${category.type}")
                        println("   - vertical: ${category.vertical}")
                        println("   - slug: ${category.slug}")
                        println("---")
                    }
                    println("=========================================")
                }
            }
            .catch { error ->
                println("❌ [SubcategoriesViewModel] Error loading subcategories for parent $parentCategoryId: ${error.message}")
                error.printStackTrace()

                // If we have cached data for this parent, use it as fallback
                if (cachedSubcategories.containsKey(parentCategoryId)) {
                    println("📦 [SubcategoriesViewModel] Using cached data as fallback due to network error")
                    val subcategories = cachedSubcategories[parentCategoryId]!!
                    _uiState.value = SubcategoriesUiState.Success(subcategories)
                } else {
                    // Also check if we have cached data for ANY parent as a last resort
                    if (cachedSubcategories.isNotEmpty()) {
                        println("📦 [SubcategoriesViewModel] No cache for this parent, but using most recent cached data as fallback")
                        val mostRecentCache = cachedSubcategories.values.lastOrNull()
                        if (mostRecentCache != null) {
                            _uiState.value = SubcategoriesUiState.Success(mostRecentCache)
                        } else {
                            _uiState.value = SubcategoriesUiState.Error(
                                error.message ?: "Failed to load subcategories"
                            )
                        }
                    } else {
                        _uiState.value = SubcategoriesUiState.Error(
                            error.message ?: "Failed to load subcategories"
                        )
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    /**
     * Refresh subcategories (clear cache and reload)
     * @param parentCategoryId The ID of the parent category
     */
    fun refresh(parentCategoryId: String) {
        println("🔄 [SubcategoriesViewModel] Refreshing subcategories for parent: $parentCategoryId")
        // Clear cache for this specific parent category
        cachedSubcategories.remove(parentCategoryId)
        // Force refresh from network
        loadSubcategories(parentCategoryId, forceRefresh = true)
    }

    /**
     * Clear cache for a specific parent category
     * @param parentCategoryId Optional specific parent ID to clear, if null clears all cache
     */
    fun clearCache(parentCategoryId: String? = null) {
        if (parentCategoryId != null) {
            cachedSubcategories.remove(parentCategoryId)
            println("🗑️ [SubcategoriesViewModel] Cache cleared for parent: $parentCategoryId")
        } else {
            cachedSubcategories.clear()
            println("🗑️ [SubcategoriesViewModel] All cache cleared")
        }
    }

    /**
     * Check if we have cached data for a specific parent category
     * @param parentCategoryId The parent category ID to check
     * @return True if cached data exists
     */
    fun hasCachedData(parentCategoryId: String): Boolean = cachedSubcategories.containsKey(parentCategoryId)

    /**
     * Get cached subcategories for a specific parent category
     * @param parentCategoryId The parent category ID
     * @return List of subcategories or null if not cached
     */
    fun getCachedSubcategories(parentCategoryId: String): List<Category>? = cachedSubcategories[parentCategoryId]

    /**
     * Preload subcategories for multiple parent categories
     * Useful for scenarios where you know the user might navigate to these categories
     * @param parentCategoryIds List of parent category IDs to preload
     */
    fun preloadSubcategories(parentCategoryIds: List<String>) {
        parentCategoryIds.forEach { parentId ->
            if (!cachedSubcategories.containsKey(parentId)) {
                println("🔄 [SubcategoriesViewModel] Preloading subcategories for: $parentId")
                loadSubcategories(parentId)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        cachedSubcategories.clear()
        println("🧹 [SubcategoriesViewModel] ViewModel cleared, cache destroyed")
    }
}