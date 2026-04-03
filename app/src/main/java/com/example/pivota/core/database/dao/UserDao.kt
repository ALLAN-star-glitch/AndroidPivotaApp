package com.example.pivota.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("UPDATE users SET accessToken = :accessToken, refreshToken = :refreshToken, isAuthenticated = :isAuthenticated, updatedAt = :updatedAt WHERE email = :email")
    suspend fun updateUserTokens(email: String, accessToken: String?, refreshToken: String?, isAuthenticated: Boolean, updatedAt: Long)

    @Query("UPDATE users SET isOnboardingComplete = :isComplete WHERE email = :email")
    suspend fun updateOnboardingStatus(email: String, isComplete: Boolean)

    @Query("UPDATE users SET hasSeenWelcomeScreen = :hasSeen WHERE email = :email")
    suspend fun updateWelcomeScreenSeen(email: String, hasSeen: Boolean)

    @Query("DELETE FROM users WHERE email = :email")
    suspend fun deleteUser(email: String)

    @Query("DELETE FROM users")
    suspend fun deleteAll()
}