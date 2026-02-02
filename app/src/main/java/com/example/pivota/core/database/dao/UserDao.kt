/**
 * Data Access Object for local User session management.
 * * This DAO handles persistence for the authenticated user, supporting both:
 * - **Individual Accounts**: Standard user profiles.
 * - **Organization Accounts**: Profiles created with automatic Admin status.
 * * Note: While roles are managed via the backend, [isUserOrganization] allows
 * the local UI to adapt functionality based on the account type.
 */

package com.example.pivota.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.pivota.core.database.entity.UserEntity
import kotlinx.coroutines.flow.Flow
import com.example.pivota.core.database.DatabaseConstants.Tables

@Dao
interface UserDao {

    // 1. Updated 'id' to 'uuid' to match your backend model
    @Query("SELECT * FROM ${Tables.USERS} WHERE uuid = :userUuid")
    suspend fun getUserByUuid(userUuid: String): UserEntity?

    // 2. Useful for observing the current session
    @Query("SELECT * FROM ${Tables.USERS} LIMIT 1")
    fun getLoggedInUser(): Flow<UserEntity?>

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertUser(user: UserEntity)

    // 3. Clean up logic for logout
    @Query("DELETE FROM ${Tables.USERS}")
    suspend fun clearUser()

    /**
     * Used for the MVP 1 logic:
     * Note: While the backend now assigns roles, this remains useful
     * for local UI state logic or checking if a session exists.
     */
    @Query("SELECT COUNT(*) FROM ${Tables.USERS}")
    suspend fun getUserCount(): Int

    // 4. Added: Check specifically for Organization status
    @Query("SELECT accountType = 'ORGANIZATION' FROM ${Tables.USERS} WHERE uuid = :userUuid")
    suspend fun isUserOrganization(userUuid: String): Boolean
}