package com.example.pivota.dashboard.domain.model

data class ProfileCompletion(
    val accountCompleted: Boolean,
    val profileCompleted: Int,
    val documentsCompleted: Int
) {
    val overallCompletion: Int get() = (profileCompleted + documentsCompleted) / 2
    val isComplete: Boolean get() = profileCompleted >= 80 && documentsCompleted >= 80
    val isProfileSectionComplete: Boolean get() = profileCompleted >= 80
    val isDocumentsSectionComplete: Boolean get() = documentsCompleted >= 80

    val completionLevel: CompletionLevel get() = when {
        overallCompletion >= 80 -> CompletionLevel.COMPLETE
        overallCompletion >= 50 -> CompletionLevel.PARTIAL
        overallCompletion >= 20 -> CompletionLevel.BASIC
        else -> CompletionLevel.INCOMPLETE
    }
}

enum class CompletionLevel {
    INCOMPLETE, BASIC, PARTIAL, COMPLETE
}