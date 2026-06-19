package com.example.pivota.dashboard.data.repository

import com.example.pivota.core.network.ApiResult
import com.example.pivota.core.network.NetworkError
import com.example.pivota.core.network.safeApiCall
import com.example.pivota.dashboard.data.dto.*
import com.example.pivota.dashboard.data.mapper.JobPostMapper
import com.example.pivota.dashboard.data.remote.JobPostsApiService
import com.example.pivota.dashboard.domain.model.listings_models.jobs.*
import com.example.pivota.dashboard.domain.repository.JobPostRepository
import javax.inject.Inject

class JobPostRepositoryImpl @Inject constructor(
    private val jobPostsApiService: JobPostsApiService,
    private val mapper: JobPostMapper
) : JobPostRepository {

    override suspend fun getAllJobs(
        params: GetAllJobsParams
    ): ApiResult<JobPostsResponse> {
        return safeApiCall {
            val requestDto = mapper.toGetAllJobsRequestDto(params)
            jobPostsApiService.getAllJobs(requestDto)
        }.let { apiResult ->
            when (apiResult) {
                is ApiResult.Success -> {
                    val response = apiResult.data
                    if (response.success) {
                        ApiResult.Success(mapper.toJobPostsResponse(response))
                    } else {
                        ApiResult.Error(
                            networkError = NetworkError.Unknown(
                                originalMessage = response.message
                            ),
                            technicalMessage = response.message
                        )
                    }
                }
                is ApiResult.Error -> {
                    ApiResult.Error(
                        networkError = apiResult.networkError,
                        technicalMessage = apiResult.technicalMessage
                    )
                }
                ApiResult.Loading -> ApiResult.Loading
            }
        }
    }

    override suspend fun getJobsByCategory(
        params: GetJobsByCategoryParams
    ): ApiResult<JobPostsResponse> {
        return safeApiCall {
            val requestDto = mapper.toGetJobsByCategoryRequestDto(params)
            jobPostsApiService.getJobsByCategory(requestDto)
        }.let { apiResult ->
            when (apiResult) {
                is ApiResult.Success -> {
                    val response = apiResult.data
                    if (response.success) {
                        ApiResult.Success(mapper.toJobPostsResponse(response))
                    } else {
                        ApiResult.Error(
                            networkError = NetworkError.Unknown(
                                originalMessage = response.message
                            ),
                            technicalMessage = response.message
                        )
                    }
                }
                is ApiResult.Error -> {
                    ApiResult.Error(
                        networkError = apiResult.networkError,
                        technicalMessage = apiResult.technicalMessage
                    )
                }
                ApiResult.Loading -> ApiResult.Loading
            }
        }
    }

    override suspend fun getJobById(
        params: GetJobByIdParams
    ): ApiResult<JobPostResponse> {
        return safeApiCall {
            val requestDto = mapper.toGetJobByIdRequestDto(params)
            jobPostsApiService.getJobById(requestDto)
        }.let { apiResult ->
            when (apiResult) {
                is ApiResult.Success -> {
                    val response = apiResult.data
                    if (response.success) {
                        ApiResult.Success(mapper.toJobPostResponse(response))
                    } else {
                        ApiResult.Error(
                            networkError = NetworkError.Unknown(
                                originalMessage = response.message
                            ),
                            technicalMessage = response.message
                        )
                    }
                }
                is ApiResult.Error -> {
                    ApiResult.Error(
                        networkError = apiResult.networkError,
                        technicalMessage = apiResult.technicalMessage
                    )
                }
                ApiResult.Loading -> ApiResult.Loading
            }
        }
    }

    override suspend fun getJobListingsByOwner(
        params: GetJobListingsByOwnerParams
    ): ApiResult<JobPostsResponse> {
        return safeApiCall {
            val requestDto = mapper.toGetJobListingsByOwnerRequestDto(params)
            jobPostsApiService.getJobListingsByOwner(requestDto)
        }.let { apiResult ->
            when (apiResult) {
                is ApiResult.Success -> {
                    val response = apiResult.data
                    if (response.success) {
                        ApiResult.Success(mapper.toJobPostsResponse(response))
                    } else {
                        ApiResult.Error(
                            networkError = NetworkError.Unknown(
                                originalMessage = response.message
                            ),
                            technicalMessage = response.message
                        )
                    }
                }
                is ApiResult.Error -> {
                    ApiResult.Error(
                        networkError = apiResult.networkError,
                        technicalMessage = apiResult.technicalMessage
                    )
                }
                ApiResult.Loading -> ApiResult.Loading
            }
        }
    }

    override suspend fun getOwnJobs(
        params: GetOwnJobsParams
    ): ApiResult<JobPostsResponse> {
        return safeApiCall {
            jobPostsApiService.getMyListings(
                status = params.status,
                limit = params.limit,
                offset = params.offset,
                sortBy = params.sortBy,
                employmentType = params.employmentType,
                paymentType = params.paymentType,
                workArrangement = params.workArrangement,
                commitment = params.commitment,
                experienceLevel = params.experienceLevel,
                educationLevel = params.educationLevel,
                isAnonymous = params.isAnonymous,
                hoursPerWeekMin = params.hoursPerWeekMin,
                hoursPerWeekMax = params.hoursPerWeekMax
            )
        }.let { apiResult ->
            when (apiResult) {
                is ApiResult.Success -> {
                    val response = apiResult.data
                    if (response.success) {
                        ApiResult.Success(mapper.toJobPostsResponse(response))
                    } else {
                        ApiResult.Error(
                            networkError = NetworkError.Unknown(
                                originalMessage = response.message
                            ),
                            technicalMessage = response.message
                        )
                    }
                }
                is ApiResult.Error -> {
                    ApiResult.Error(
                        networkError = apiResult.networkError,
                        technicalMessage = apiResult.technicalMessage
                    )
                }
                ApiResult.Loading -> ApiResult.Loading
            }
        }
    }

    override suspend fun createJobPost(
        params: CreateJobPostParams
    ): ApiResult<CreateJobPostResponse> {
        return safeApiCall {
            val requestDto = mapper.toCreateJobPostRequestDto(params)
            jobPostsApiService.createJobPost(requestDto)
        }.let { apiResult ->
            when (apiResult) {
                is ApiResult.Success -> {
                    val response = apiResult.data
                    if (response.success) {
                        ApiResult.Success(mapper.toCreateJobPostResponse(response))
                    } else {
                        ApiResult.Error(
                            networkError = NetworkError.Unknown(
                                originalMessage = response.message
                            ),
                            technicalMessage = response.message
                        )
                    }
                }
                is ApiResult.Error -> {
                    ApiResult.Error(
                        networkError = apiResult.networkError,
                        technicalMessage = apiResult.technicalMessage
                    )
                }
                ApiResult.Loading -> ApiResult.Loading
            }
        }
    }

    override suspend fun updateJobPost(
        jobId: String,
        params: UpdateJobPostParams
    ): ApiResult<JobPostResponse> {
        return safeApiCall {
            val requestDto = mapper.toUpdateJobPostRequestDto(params)
            jobPostsApiService.updateJobPost(jobId, requestDto)
        }.let { apiResult ->
            when (apiResult) {
                is ApiResult.Success -> {
                    val response = apiResult.data
                    if (response.success) {
                        ApiResult.Success(mapper.toJobPostResponse(response))
                    } else {
                        ApiResult.Error(
                            networkError = NetworkError.Unknown(
                                originalMessage = response.message
                            ),
                            technicalMessage = response.message
                        )
                    }
                }
                is ApiResult.Error -> {
                    ApiResult.Error(
                        networkError = apiResult.networkError,
                        technicalMessage = apiResult.technicalMessage
                    )
                }
                ApiResult.Loading -> ApiResult.Loading
            }
        }
    }

    override suspend fun closeJobPost(
        jobId: String
    ): ApiResult<CloseJobPostResponse> {
        return safeApiCall {
            jobPostsApiService.closeJobPost(jobId)
        }.let { apiResult ->
            when (apiResult) {
                is ApiResult.Success -> {
                    val response = apiResult.data
                    if (response.success) {
                        ApiResult.Success(mapper.toCloseJobPostResponse(response))
                    } else {
                        ApiResult.Error(
                            networkError = NetworkError.Unknown(
                                originalMessage = response.message
                            ),
                            technicalMessage = response.message
                        )
                    }
                }
                is ApiResult.Error -> {
                    ApiResult.Error(
                        networkError = apiResult.networkError,
                        technicalMessage = apiResult.technicalMessage
                    )
                }
                ApiResult.Loading -> ApiResult.Loading
            }
        }
    }
}