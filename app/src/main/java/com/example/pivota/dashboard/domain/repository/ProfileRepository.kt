
package com.example.pivota.dashboard.domain.repository

import com.example.pivota.auth.domain.model.CompleteProfileResult
import com.example.pivota.core.network.ApiResult

interface ProfileRepository {
    suspend fun fetchProfile(): ApiResult<CompleteProfileResult>
}