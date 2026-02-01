package com.example.pivota.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "account_verifications")
data class AccountVerificationEntity(
    @PrimaryKey val id: String,
    val type: String,   // "NATIONAL_ID", "KRA_PIN", etc.
    val status: String, // "PENDING", "APPROVED", "REJECTED"
    val rejectionReason: String?,
    val verifiedAt: Long?
)