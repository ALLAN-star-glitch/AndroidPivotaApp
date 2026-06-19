package com.example.pivota.dashboard.domain.useCase

import com.example.pivota.core.network.ApiResult
import com.example.pivota.dashboard.domain.model.listings_models.jobs.GetAllJobsParams
import com.example.pivota.dashboard.domain.model.listings_models.jobs.JobPostsResponse
import com.example.pivota.dashboard.domain.repository.JobPostRepository
import javax.inject.Inject

class GetAllJobsUseCase @Inject constructor(
    private val repository: JobPostRepository
) {
    suspend operator fun invoke(
        params: GetAllJobsParams = GetAllJobsParams()
    ): ApiResult<JobPostsResponse> {
        return repository.getAllJobs(params)
    }
}