package com.logomann.datascanner20.data.settings.impl

import android.content.SharedPreferences
import androidx.core.content.edit
import com.logomann.datascanner20.domain.settings.SettingsRepository
import com.logomann.datascanner20.domain.settings.models.ThemeSettings
import com.logomann.datascanner20.util.App
import com.logomann.datascanner20.util.SCANNER_ID
import com.logomann.datascanner20.util.THEME_KEY

class SettingsRepositoryImpl(
    private val sharedPreferences: SharedPreferences,
    private val app: App
) :
    SettingsRepository {
    override fun setScannerID(id: Int) {
        sharedPreferences.edit().putInt(SCANNER_ID, id).apply()
    }

    override fun getScannerID(): Int {
        return sharedPreferences.getInt(SCANNER_ID, 1)
    }

    override fun getThemeSettings(): ThemeSettings {
        val isDark = sharedPreferences.getBoolean(THEME_KEY, false)
        if (isDark) return ThemeSettings.DARK
        return ThemeSettings.LIGHT
    }

    override fun updateThemeSettings(settings: ThemeSettings) {
        sharedPreferences.edit {
            putBoolean(THEME_KEY, settings.isDark)
        }
        app.switchTheme(settings.isDark)
    }


}