package com.example.pivota.dashboard.data.sync

import com.example.pivota.core.network.ApiResult
import com.example.pivota.dashboard.data.repository.CategoriesRepositoryImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoriesSyncManager @Inject constructor(
    private val categoriesRepository: CategoriesRepositoryImpl
) {

    private val mutex = Mutex()
    private var syncJob: Job? = null
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var isSyncing = false

    companion object {


        private const val SYNC_INTERVAL_MS = 3 * 60 * 60 * 1000L // 3 hours
    }

    fun startAutoSync() {
        if (syncJob?.isActive == true) {
            println("🔄 [CategoriesSync] Auto-sync already running")
            return
        }

        syncJob = scope.launch {
            println("🔄 [CategoriesSync] Auto-sync loop started")

            // Perform initial sync immediately
            performSync()

            while (isActive) {
                delay(SYNC_INTERVAL_MS)
                if (isActive) {
                    performSync()
                }
            }
            println("🔄 [CategoriesSync] Auto-sync loop ended")
        }
        println("🔄 [CategoriesSync] Auto-sync started")
    }

    suspend fun stopAutoSync() {
        syncJob?.cancel()
        syncJob = null
        isSyncing = false
        println("🔄 [CategoriesSync] Auto-sync stopped")
    }

    suspend fun performSync(): Boolean {
        if (isSyncing) {
            println("⚠️ [CategoriesSync] Sync already in progress")
            return false
        }

        return mutex.withLock {
            isSyncing = true
            try {
                println("🔄 [CategoriesSync] Starting categories sync...")

                // Use the existing getDiscoveryMetadata which caches to Room
                val result = categoriesRepository.getDiscoveryMetadata(type = "COMPLIMENTARY")

                when (result) {
                    is ApiResult.Success -> {
                        println("✅ [CategoriesSync] Sync completed - ${result.data.size} categories")
                        true
                    }
                    is ApiResult.Error -> {
                        println("❌ [CategoriesSync] Sync failed: ${result.technicalMessage}")
                        false
                    }
                    ApiResult.Loading -> {
                        println("⚠️ [CategoriesSync] Sync still loading...")
                        false
                    }
                }
            } finally {
                isSyncing = false
            }
        }
    }
}