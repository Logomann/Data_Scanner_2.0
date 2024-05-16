package com.logomann.datascanner20.ui.settings.view_model


import androidx.lifecycle.ViewModel
import com.logomann.datascanner20.domain.settings.SettingsInteractor

class SettingsViewModel(private val interactor: SettingsInteractor) : ViewModel() {

    fun setScannerID(id: Int) {
        interactor.setScannerID(id)
    }

    fun getScannerID(): Int {
        return interactor.getScannerID()
    }

    fun getTheme(): Boolean {
        return interactor.getThemeSettings().isDark
    }

    fun switchTheme() {
        interactor.updateThemeSettings(interactor.getThemeSettings())
    }

}