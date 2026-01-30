package com.example.pivota.auth.data.repository

import UserDao
import com.example.pivota.auth.data.mapper.toDomain
import com.example.pivota.auth.data.mapper.toEntity
import com.example.pivota.auth.domain.model.User
import com.example.pivota.auth.domain.model.UserRole
import com.example.pivota.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val userDao: UserDao
) : AuthRepository {

    override suspend fun saveUser(user: User) {
        // 1. Check if any users exist in the database
        val userCount = userDao.getUserCount()

        // 2. Apply your business rule: First user = ADMIN
        val userWithRole = if (userCount == 0) {
            user.copy(role = UserRole.ADMIN)
        } else {
            user
        }

        // 3. Map to Entity and save
        userDao.insertUser(userWithRole.toEntity())
    }

    override fun getAuthenticatedUser(): Flow<User?> {
        // Maps the Database Entity Flow back to a Domain Model Flow
        return userDao.getLoggedInUser().map { entity ->
            entity?.toDomain()
        }
    }

    override suspend fun clearSession() {
        userDao.clearUser()
    }
}