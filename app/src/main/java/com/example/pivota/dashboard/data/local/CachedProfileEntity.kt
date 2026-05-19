
package com.example.pivota.dashboard.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "cached_profiles")
data class CachedProfileEntity(
    @PrimaryKey
    val userId: String,
    val profileJson: String, // Store as JSON
    val timestamp: Long,
    val isDefault: Boolean = false
)