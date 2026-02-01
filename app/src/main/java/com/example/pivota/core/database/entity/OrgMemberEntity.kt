/**
 * Represents the association between a User and an Organization.
 * * This entity facilitates the organizational hierarchy where:
 * - **Many-to-Many Relationship**: Maps users to their respective organizations.
 * - **Role-Based Access**: Stores the [roleName] (e.g., Admin, Member) assigned
 * by the backend.
 * - **Admin Logic**: Supports the system rule where the initial creator is
 * automatically granted Admin privileges.
 */

package com.example.pivota.core.database.entity
import androidx.room.Entity
import androidx.room.Index
import com.example.pivota.core.database.DatabaseConstants

@Entity(
    tableName = DatabaseConstants.Tables.ORGANIZATION_MEMBERS,
    primaryKeys = ["userUuid", "organizationUuid"],
    indices = [Index(value = ["organizationUuid"])] // Speeds up member lookups
)
data class OrgMemberEntity(
    val userUuid: String,
    val organizationUuid: String,
    val roleName: String
)