package com.justbaat.mindoro.workers

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

object WorkManagerInitializer {

    private const val QUIZ_SYNC_WORK_NAME = "quiz_data_sync_periodic"
    private const val SYNC_INTERVAL_HOURS = 12L

    fun initialize(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        val syncWorkRequest = PeriodicWorkRequestBuilder<QuizDataSyncWorker>(
            SYNC_INTERVAL_HOURS, TimeUnit.HOURS,
            30, TimeUnit.MINUTES // Flex interval
        )
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                15, TimeUnit.MINUTES
            )
            .addTag("quiz_sync")
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            QUIZ_SYNC_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            syncWorkRequest
        )
    }

    fun syncNow(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncWorkRequest = OneTimeWorkRequestBuilder<QuizDataSyncWorker>()
            .setConstraints(constraints)
            .addTag("quiz_sync_manual")
            .build()

        WorkManager.getInstance(context).enqueue(syncWorkRequest)
    }

    fun cancelSync(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(QUIZ_SYNC_WORK_NAME)
    }
}
