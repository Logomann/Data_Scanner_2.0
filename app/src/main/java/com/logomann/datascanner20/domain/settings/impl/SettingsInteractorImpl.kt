package com.logomann.datascanner20.domain.settings.impl

import com.logomann.datascanner20.domain.settings.SettingsInteractor
import com.logomann.datascanner20.domain.settings.SettingsRepository
import com.logomann.datascanner20.domain.settings.models.ThemeSettings

class SettingsInteractorImpl(private val repository: SettingsRepository) : SettingsInteractor {
    override fun setScannerID(id: Int) {
        repository.setScannerID(id)
    }

    override fun getScannerID(): Int {
        return repository.getScannerID()
    }

    override fun getThemeSettings(): ThemeSettings {
        return repository.getThemeSettings()
    }

    override fun updateThemeSettings(settings: ThemeSettings) {
        if (settings == ThemeSettings.DARK) {
            repository.updateThemeSettings(ThemeSettings.LIGHT)
        } else {
            repository.updateThemeSettings(ThemeSettings.DARK)
        }
    }
}