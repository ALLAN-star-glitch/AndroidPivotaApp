package com.example.pivota.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.pivota.core.database.entity.DiscoveryCategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiscoveryCategories(categories: List<DiscoveryCategoryEntity>)

    @Query("SELECT * FROM discovery_categories WHERE type = 'COMPLIMENTARY' ORDER BY name ASC")
    fun getComplimentaryCategories(): Flow<List<DiscoveryCategoryEntity>>

    @Query("SELECT * FROM discovery_categories WHERE type = 'COMPLIMENTARY' ORDER BY name ASC")
    suspend fun getComplimentaryCategoriesList(): List<DiscoveryCategoryEntity>

    @Query("DELETE FROM discovery_categories")
    suspend fun clearDiscoveryCategories()

    @Query("SELECT COUNT(*) FROM discovery_categories")
    suspend fun getCount(): Int
}