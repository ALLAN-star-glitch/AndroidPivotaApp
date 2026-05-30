package com.example.pivota.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.pivota.core.database.entity.CategoriesCacheMetadataEntity
import com.example.pivota.core.database.entity.CategoryEntity
import com.example.pivota.core.database.entity.DiscoveryCategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    // ==================== DISCOVERY CATEGORIES ====================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiscoveryCategories(categories: List<DiscoveryCategoryEntity>)

    @Query("SELECT * FROM discovery_categories WHERE cacheKey = :cacheKey ORDER BY name ASC")
    fun getDiscoveryCategories(cacheKey: String): Flow<List<DiscoveryCategoryEntity>>

    @Query("SELECT * FROM discovery_categories WHERE cacheKey = :cacheKey ORDER BY name ASC")
    suspend fun getDiscoveryCategoriesList(cacheKey: String): List<DiscoveryCategoryEntity>

    @Query("SELECT * FROM discovery_categories WHERE type = 'COMPLIMENTARY' ORDER BY name ASC")
    fun getComplimentaryCategories(): Flow<List<DiscoveryCategoryEntity>>

    @Query("SELECT * FROM discovery_categories WHERE type = 'COMPLIMENTARY' ORDER BY name ASC")
    suspend fun getComplimentaryCategoriesList(): List<DiscoveryCategoryEntity>

    @Query("DELETE FROM discovery_categories WHERE cacheKey = :cacheKey")
    suspend fun deleteDiscoveryCategoriesByCacheKey(cacheKey: String)

    @Query("DELETE FROM discovery_categories")
    suspend fun clearAllDiscoveryCategories()

    @Query("SELECT COUNT(*) FROM discovery_categories")
    suspend fun getDiscoveryCategoriesCount(): Int

    // ==================== FULL CATEGORIES ====================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<CategoryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity)

    @Query("SELECT * FROM categories WHERE id = :categoryId")
    suspend fun getCategoryById(categoryId: String): CategoryEntity?

    @Query("SELECT * FROM categories WHERE cacheKey = :cacheKey ORDER BY name ASC")
    suspend fun getCategoriesListByCacheKey(cacheKey: String): List<CategoryEntity>

    @Query("DELETE FROM categories WHERE cacheKey = :cacheKey")
    suspend fun deleteCategoriesByCacheKey(cacheKey: String)

    @Query("DELETE FROM categories")
    suspend fun clearAllCategories()

    @Query("SELECT COUNT(*) FROM categories")
    suspend fun getCategoriesCount(): Int

    // ==================== CACHE METADATA ====================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDiscoveryCacheMetadata(metadata: CategoriesCacheMetadataEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCategoriesCacheMetadata(metadata: CategoriesCacheMetadataEntity)

    @Query("SELECT * FROM categories_cache_metadata WHERE cacheKey = :cacheKey")
    suspend fun getDiscoveryCacheMetadata(cacheKey: String): CategoriesCacheMetadataEntity?

    @Query("SELECT * FROM categories_cache_metadata WHERE cacheKey = :cacheKey")
    suspend fun getCategoriesCacheMetadata(cacheKey: String): CategoriesCacheMetadataEntity?

    @Query("DELETE FROM categories_cache_metadata WHERE cacheKey = :cacheKey")
    suspend fun deleteCacheMetadataByKey(cacheKey: String)

    @Query("DELETE FROM categories_cache_metadata")
    suspend fun clearAllCacheMetadata()

    @Query("SELECT COUNT(*) FROM categories_cache_metadata")
    suspend fun getCacheMetadataCount(): Int
}