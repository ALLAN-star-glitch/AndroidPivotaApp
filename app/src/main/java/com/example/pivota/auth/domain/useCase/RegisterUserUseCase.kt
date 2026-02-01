package com.example.pivota.auth.domain.useCase

import com.example.pivota.auth.domain.model.User
import com.example.pivota.auth.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUserUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend fun signupIndividual(user: User, code: String, password: String): Result<User> {
        return repository.signupIndividual(user, code, password)
    }

    suspend fun signupOrganization(user: User, code: String, password: String): Result<User> {
        return repository.signupOrganization(user, code, password)
    }
}