package com.example.pivota.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.pivota.core.database.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE isAuthenticated = 1 LIMIT 1")
    suspend fun getAuthenticatedUser(): UserEntity?

    @Query("SELECT * FROM users WHERE isAuthenticated = 1 LIMIT 1")
    fun getAuthenticatedUserFlow(): Flow<UserEntity?>

    @Update
    suspend fun updateUser(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("UPDATE users SET isOnboardingComplete = :isComplete WHERE email = :email")
    suspend fun updateOnboardingStatus(email: String, isComplete: Boolean)

    @Query("UPDATE users SET hasSeenWelcomeScreen = :hasSeen WHERE email = :email")
    suspend fun updateWelcomeScreenSeen(email: String, hasSeen: Boolean)

    @Query("DELETE FROM users WHERE email = :email")
    suspend fun deleteUser(email: String)

    @Query("DELETE FROM users")
    suspend fun deleteAll()

    // NEW: Get authenticated user with complete profile
    @Query("SELECT * FROM users WHERE isAuthenticated = 1 AND completeProfileJson IS NOT NULL LIMIT 1")
    suspend fun getAuthenticatedUserWithProfile(): UserEntity?

    // NEW: Update complete profile cache
    @Query("UPDATE users SET completeProfileJson = :profileJson, completeProfileLastUpdated = :timestamp WHERE uuid = :userId")
    suspend fun updateCompleteProfile(userId: String, profileJson: String, timestamp: Long)

    @Query("UPDATE users SET completeProfileJson = :profileJson, completeProfileLastUpdated = :timestamp WHERE uuid = :userId")
    suspend fun updateProfileCache(userId: String, profileJson: String, timestamp: Long)
}