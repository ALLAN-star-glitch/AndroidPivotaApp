package com.example.pivota.dashboard.domain.repository

import com.example.pivota.core.network.ApiResult
import com.example.pivota.dashboard.domain.model.listings_models.jobs.*

interface JobPostRepository {

    // GET /jobs-module/jobs
    suspend fun getAllJobs(
        params: GetAllJobsParams
    ): ApiResult<JobPostsResponse>

    // GET /jobs-module/jobs/category
    suspend fun getJobsByCategory(
        params: GetJobsByCategoryParams
    ): ApiResult<JobPostsResponse>

    // GET /jobs-module/details/:id
    suspend fun getJobById(
        params: GetJobByIdParams
    ): ApiResult<JobPostResponse>

    // GET /jobs-module/jobs/owner/:accountId
    suspend fun getJobListingsByOwner(
        params: GetJobListingsByOwnerParams
    ): ApiResult<JobPostsResponse>

    // GET /jobs-module/my-listings
    suspend fun getOwnJobs(
        params: GetOwnJobsParams
    ): ApiResult<JobPostsResponse>

    // POST /jobs-module/jobs
    suspend fun createJobPost(
        params: CreateJobPostParams
    ): ApiResult<CreateJobPostResponse>

    // PATCH /jobs-module/jobs/:id
    suspend fun updateJobPost(
        jobId: String,
        params: UpdateJobPostParams
    ): ApiResult<JobPostResponse>

    // PATCH /jobs-module/jobs/:id/close
    suspend fun closeJobPost(
        jobId: String
    ): ApiResult<CloseJobPostResponse>
}