package com.logomann.datascanner20.ui.settings.view_model


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.logomann.datascanner20.domain.settings.SettingsInteractor
import com.logomann.datascanner20.ui.settings.MAXIMUM_TSD

class SettingsViewModel(private val interactor: SettingsInteractor) : ViewModel() {

    var tsdId by mutableIntStateOf(getScannerID())
    var pickedID by mutableIntStateOf(tsdId)
    var openDialog by mutableStateOf(false)
    var compoundName by mutableStateOf("")
    var compoundPicked by mutableStateOf(compoundName)
    val listOfTsd = mutableListOf<String>()


    private fun fillList() {
        listOfTsd.add("")
        (1..MAXIMUM_TSD).map {
            listOfTsd.add(it.toString())
        }
        (1..2).map {
            listOfTsd.add("")
        }
    }


    private fun getScannerID(): Int {
        return interactor.getScannerID()
    }

    fun setScannerID() {
        interactor.setScannerID(tsdId)
    }

    fun getTheme(): Boolean {
        return interactor.getThemeSettings().isDark
    }

    fun switchTheme() {
        interactor.updateThemeSettings(interactor.getThemeSettings())
    }

    init {
        fillList()
    }

}