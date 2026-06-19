package com.example.pivota.dashboard.presentation.viewmodels.client_general_viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pivota.core.network.ApiResult
import com.example.pivota.dashboard.domain.model.listings_models.jobs.GetAllJobsParams
import com.example.pivota.dashboard.domain.model.listings_models.jobs.JobPost
import com.example.pivota.dashboard.domain.useCase.GetAllJobsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JobPostsViewModel @Inject constructor(
    private val getAllJobsUseCase: GetAllJobsUseCase
) : ViewModel() {

    private val _jobsState = MutableStateFlow<JobsUiState>(JobsUiState.Loading)
    val jobsState: StateFlow<JobsUiState> = _jobsState.asStateFlow()

    // Pagination state
    private var currentParams: GetAllJobsParams = GetAllJobsParams()
    private var hasMoreData: Boolean = true
    private var currentOffset: Int = 0

    /**
     * Load all jobs with pagination and filters
     */
    fun loadJobs(
        params: GetAllJobsParams = GetAllJobsParams(),
        isLoadMore: Boolean = false
    ) {
        viewModelScope.launch {
            if (!isLoadMore) {
                _jobsState.update { JobsUiState.Loading }
                currentOffset = 0
                currentParams = params
                hasMoreData = true
            }

            val requestParams = if (isLoadMore) {
                params.copy(offset = currentOffset)
            } else {
                params
            }

            val result = getAllJobsUseCase(requestParams)

            when (result) {
                is ApiResult.Success -> {
                    val jobs = result.data.data
                    val pagination = result.data.pagination

                    hasMoreData = pagination?.hasMore ?: false
                    currentOffset = requestParams.offset + requestParams.limit

                    val currentState = _jobsState.value
                    val existingJobs = if (isLoadMore && currentState is JobsUiState.Success) {
                        currentState.jobs
                    } else {
                        emptyList()
                    }

                    _jobsState.update {
                        JobsUiState.Success(
                            jobs = existingJobs + jobs,
                            hasMore = hasMoreData,
                            totalCount = pagination?.total ?: (existingJobs + jobs).size
                        )
                    }
                }
                is ApiResult.Error -> {
                    _jobsState.update {
                        JobsUiState.Error(
                            message = result.technicalMessage ?: "Failed to load jobs"
                        )
                    }
                }
                ApiResult.Loading -> {
                    // Already handled above
                }
            }
        }
    }

    /**
     * Load more jobs for pagination
     */
    fun loadMore() {
        if (hasMoreData && _jobsState.value is JobsUiState.Success) {
            loadJobs(
                params = currentParams.copy(offset = currentOffset),
                isLoadMore = true
            )
        }
    }

    /**
     * Refresh jobs (pull to refresh)
     */
    fun refreshJobs() {
        currentOffset = 0
        hasMoreData = true
        loadJobs(
            params = currentParams.copy(offset = 0),
            isLoadMore = false
        )
    }

    /**
     * Clear state
     */
    fun clearState() {
        _jobsState.update { JobsUiState.Loading }
        currentOffset = 0
        hasMoreData = true
    }

    override fun onCleared() {
        super.onCleared()
        _jobsState.update { JobsUiState.Loading }
    }
}

// ======================================================
// UI STATE
// ======================================================

sealed class JobsUiState {
    object Loading : JobsUiState()
    data class Success(
        val jobs: List<JobPost>,
        val hasMore: Boolean,
        val totalCount: Int
    ) : JobsUiState()
    data class Error(val message: String) : JobsUiState()
}