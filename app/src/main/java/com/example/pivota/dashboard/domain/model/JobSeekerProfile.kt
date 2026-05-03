package com.example.pivota.dashboard.domain.model

data class JobSeekerProfile(
    val id: String,
    val headline: String?,
    val isActivelySeeking: Boolean,
    val skills: List<String>,
    val industries: List<String>,
    val jobTypes: List<String>,
    val seniorityLevel: String?,
    val noticePeriod: String?,
    val expectedSalary: Int?,
    val workAuthorization: List<String>,
    val cvUrl: String?,
    val cvLastUpdated: String?,
    val portfolioImages: List<String>,
    val linkedInUrl: String?,
    val githubUrl: String?,
    val portfolioUrl: String?,
    val hasAgent: Boolean,
    val agentId: String?
) {
    val statusText: String get() = if (isActivelySeeking) "Actively Seeking" else "Open to Opportunities"
    val statusColor: String get() = if (isActivelySeeking) "success" else "warning"
    val hasCV: Boolean get() = !cvUrl.isNullOrBlank()
    val formattedExpectedSalary: String get() = expectedSalary?.let { "KES ${String.format("%,d", it)}" } ?: "Negotiable"
    val topSkills: List<String> get() = skills.take(5)
}