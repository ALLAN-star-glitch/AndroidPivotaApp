package com.example.pivota.auth.domain.repository

import com.example.pivota.auth.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    /**
     * Saves the user to local storage.
     * Implementation should handle role assignment (e.g., first user becomes ADMIN).
     */
    suspend fun saveUser(user: User)

    /**
     * Observes the currently authenticated user.
     * Returns a Flow that emits null if no user is logged in.
     */
    fun getAuthenticatedUser(): Flow<User?>

    /**
     * Logs the user out by clearing local session data.
     */
    suspend fun clearSession()
}