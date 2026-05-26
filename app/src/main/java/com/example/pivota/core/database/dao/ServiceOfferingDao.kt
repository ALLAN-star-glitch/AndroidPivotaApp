package com.example.pivota.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.pivota.core.database.entity.ServiceOfferingEntity
import com.example.pivota.core.database.entity.ServiceOfferingsCacheMetadataEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ServiceOfferingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOfferings(offerings: List<ServiceOfferingEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCacheMetadata(metadata: ServiceOfferingsCacheMetadataEntity)

    @Query("SELECT * FROM service_offerings WHERE categoryId = :categoryId ORDER BY basePrice ASC")
    fun getOfferingsByCategory(categoryId: String): Flow<List<ServiceOfferingEntity>>

    @Query("SELECT * FROM service_offerings WHERE categoryId = :categoryId ORDER BY basePrice ASC")
    suspend fun getOfferingsByCategoryList(categoryId: String): List<ServiceOfferingEntity>

    @Query("DELETE FROM service_offerings WHERE categoryId = :categoryId")
    suspend fun deleteOfferingsByCategory(categoryId: String)

    @Query("SELECT * FROM service_offerings_cache_metadata WHERE categoryId = :categoryId")
    suspend fun getCacheMetadata(categoryId: String): ServiceOfferingsCacheMetadataEntity?

    @Query("DELETE FROM service_offerings WHERE lastUpdated < :timestamp")
    suspend fun deleteStaleOfferings(timestamp: Long)
}