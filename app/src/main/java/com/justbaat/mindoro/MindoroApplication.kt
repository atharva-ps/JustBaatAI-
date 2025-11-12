package com.justbaat.mindoro

import android.app.Application
import com.justbaat.mindoro.utils.ThemeManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MindoroApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Apply saved theme on app startup
        val savedTheme = ThemeManager.getSavedThemeMode(this)
        ThemeManager.applyTheme(savedTheme)
    }
}
