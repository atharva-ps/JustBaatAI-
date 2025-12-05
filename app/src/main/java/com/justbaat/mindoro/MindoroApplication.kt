package com.justbaat.mindoro

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.justbaat.mindoro.utils.ThemeManager
import com.justbaat.mindoro.workers.WorkManagerInitializer
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MindoroApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()

        // Apply saved theme on app startup
        val savedTheme = ThemeManager.getSavedThemeMode(this)
        ThemeManager.applyTheme(savedTheme)

        // Initialize WorkManager for automatic quiz data sync every 12 hours
        WorkManagerInitializer.initialize(this)
    }

    // ✅ Use property syntax (val), not function syntax (fun)
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()
}
