package com.logomann.datascanner20.domain.settings

import com.logomann.datascanner20.domain.settings.models.ThemeSettings

interface SettingsRepository {
    fun setScannerID(id: Int)
    fun getScannerID():Int
    fun getThemeSettings():ThemeSettings
    fun updateThemeSettings(settings:ThemeSettings)
}