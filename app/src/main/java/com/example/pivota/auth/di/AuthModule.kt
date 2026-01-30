import com.example.pivota.auth.data.repository.AuthRepositoryImpl
import com.example.pivota.auth.domain.repository.AuthRepository
import com.example.pivota.core.database.PivotaDatabase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface AuthModule {

    @Binds
    @Singleton
    fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    companion object {
        @Provides
        @Singleton
        fun provideUserDao(database: PivotaDatabase): UserDao {
            return database.userDao()
        }
    }
}