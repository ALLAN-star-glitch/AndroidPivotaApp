package com.example.pivota.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.pivota.core.database.entity.OrgMemberEntity
import kotlinx.coroutines.flow.Flow
import com.example.pivota.core.database.DatabaseConstants.Tables

@Dao
interface OrgMemberDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMembership(membership: OrgMemberEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemberships(memberships: List<OrgMemberEntity>)

    /**
     * Get the specific role of a user within a specific organization.
     * Useful for checking if the user is a "BusinessAdministrator".
     */
    @Query("""
        SELECT roleName FROM ${Tables.ORGANIZATION_MEMBERS} 
        WHERE userUuid = :userUuid AND organizationUuid = :orgUuid """)
    suspend fun getRoleInOrganization(userUuid: String, orgUuid: String): String?

    /**
     * Get all organizations a user belongs to.
     * Matches the 'memberships' relation in your Prisma User model.
     */
    @Query("SELECT * FROM ${Tables.ORGANIZATION_MEMBERS} WHERE userUuid = :userUuid")
    fun getMembershipsForUser(userUuid: String): Flow<List<OrgMemberEntity>>

    /**
     * Clear memberships for a specific user (e.g., during a refresh or logout).
     */
    @Query("DELETE FROM ${Tables.ORGANIZATION_MEMBERS} WHERE userUuid = :userUuid")
    suspend fun deleteMembershipsForUser(userUuid: String)

    @Transaction
    suspend fun updateMemberships(userUuid: String, newMemberships: List<OrgMemberEntity>) {
        deleteMembershipsForUser(userUuid)
        insertMemberships(newMemberships)
    }
}