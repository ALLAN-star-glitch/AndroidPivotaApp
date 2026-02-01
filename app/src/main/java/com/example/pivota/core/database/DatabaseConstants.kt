package com.example.pivota.core.database


/**
 * Authoritative constants for the Room Database infrastructure.
 * Aligned with PivotaConnect Core Package Documentation Section 6.
 */
object DatabaseConstants {

    // --- Database Configuration ---
    const val DATABASE_NAME = "pivota_connect_db"
    const val DATABASE_VERSION = 1

    // --- Table Names (Feature-Agnostic) ---
    // Note: Feature-specific table names should be defined here to prevent
    // naming conflicts across the modular architecture.

    object Tables {
        const val USERS = "users"
        const val ORGANIZATION_MEMBERS = "organization_members"
        const val LISTINGS = "listings"
        const val CONTRACTORS = "contractors"
        const val SEARCH_HISTORY = "search_history"
        const val NOTIFICATIONS = "notifications"
    }

    // --- View/Virtual Table Names ---
    object Views {
        const val SMART_MATCH_RESULTS = "smart_match_results_view"
    }

    // --- Column Names (Commonly Reused) ---
    object Columns {
        const val ID = "id"
        const val CREATED_AT = "created_at"
        const val UPDATED_AT = "updated_at"
        const val SYNC_STATUS = "sync_status"
        const val METADATA = "metadata"
    }
}