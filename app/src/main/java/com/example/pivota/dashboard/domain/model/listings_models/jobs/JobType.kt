package com.example.pivota.dashboard.domain.model.listings_models.jobs

/**
 * Represents the type of job post in PivotaConnect.
 * Aligned with Backend Prisma Schema.
 */
enum class JobType {
    INFORMAL, // Maps to "Casual / Informal"
    FORMAL    // Maps to "Formal Job"
}