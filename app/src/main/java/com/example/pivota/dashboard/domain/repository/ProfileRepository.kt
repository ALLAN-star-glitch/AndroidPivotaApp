package com.example.pivota.dashboard.domain.repository

import com.example.pivota.core.network.ApiResult
import com.example.pivota.dashboard.domain.model.CompleteProfile

interface ProfileRepository {
    suspend fun fetchProfile(): ApiResult<CompleteProfile>
}