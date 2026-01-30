

import androidx.room.*
import com.example.pivota.auth.data.local.UserEntity
import com.example.pivota.core.database.DatabaseConstants
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Query("SELECT * FROM ${DatabaseConstants.Tables.USERS} WHERE id = :userId")
    suspend fun getUserById(userId: String): UserEntity?

    @Query("SELECT * FROM ${DatabaseConstants.Tables.USERS} LIMIT 1")
    fun getLoggedInUser(): Flow<UserEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("DELETE FROM ${DatabaseConstants.Tables.USERS}")
    suspend fun clearUser()

    /**
     * Used for the MVP 1 logic:
     * If count is 0, the first person registering is the Organization Admin.
     */
    @Query("SELECT COUNT(*) FROM ${DatabaseConstants.Tables.USERS}")
    suspend fun getUserCount(): Int
}