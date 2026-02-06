package com.example.pivota.listings.presentation.state

import com.example.pivota.listings.domain.models.DocumentType
import com.example.pivota.listings.domain.models.EmploymentType
import com.example.pivota.listings.domain.models.JobType
import com.example.pivota.listings.domain.models.PayRate


/**
 * State-holder for the Post Job flow.
 * Designed to be observed by the Adaptive Layout to trigger
 * progressive disclosure of Formal vs. Informal fields.
 */
data class PostJobUiState(
    val title: String = "",
    val description: String = "",
    val jobType: JobType = JobType.INFORMAL,
    val categoryId: String = "",
    val subCategoryId: String? = null,
    val locationCity: String = "",
    val locationNeighborhood: String = "",
    val isRemote: Boolean = false,
    val payAmount: Double = 0.0,
    val payRate: PayRate = PayRate.DAILY,
    val isNegotiable: Boolean = false,
    val benefits: List<String> = emptyList(),
    val additionalNotes: String? = null,
    val employmentType: EmploymentType? = null,
    val experienceLevel: String? = null,
    val educationLevel: String? = null,
    val skills: List<String> = emptyList(),
    val requiresEquipment: Boolean = false,
    val equipmentRequired: List<String> = emptyList(),
    val documentsNeeded: List<DocumentType> = emptyList(),
    val requiresDocuments: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)