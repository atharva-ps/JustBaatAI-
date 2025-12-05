package com.justbaat.mindoro.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.justbaat.mindoro.catfreequizzes.QuizRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class QuizDataSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: QuizRepository
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val TAG = "QuizDataSyncWorker"
    }

    override suspend fun doWork(): Result {
        return try {
            Log.d(TAG, "Starting quiz data sync...")

            val success = repository.syncQuizDataFromServer()

            if (success) {
                Log.d(TAG, "Quiz data synced successfully")
                Result.success()
            } else {
                Log.e(TAG, "Failed to sync quiz data")
                Result.retry()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing quiz data", e)
            Result.retry()
        }
    }
}
