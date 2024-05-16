package com.logomann.datascanner20.util

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.logomann.datascanner20.di.dataModule
import com.logomann.datascanner20.di.interactorModule
import com.logomann.datascanner20.di.repositoryModule
import com.logomann.datascanner20.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

const val PREFERENCES = "preferences"
const val THEME_KEY = "key_for_theme"
const val SCANNER_ID = "scanner_id"
const val CAMERA_RESULT = "camera_result"
const val RELOCATION_CODE = 2
const val SEARCH_BY_VIN_CODE = 4
const val CLEAR_CELL_CODE = 11
const val SEARCH_BY_PLACE_CODE = 5
const val LOT_IN_LADUNG_CODE = 7
const val CONTAINER_IN_CARRIAGE_CODE = 15
const val CONTAINER_ARRIVAL = 2
const val CAR_LOTTING_CODE = 10
const val CAMERA_REQUEST_KEY = "CAMERA_REQUEST"

class App : Application() {
    private var darkTheme = false
    private lateinit var application: Application
    private lateinit var sharedPrefs: SharedPreferences
    override fun onCreate() {
        super.onCreate()
        application = this
        sharedPrefs = getSharedPreferences(PREFERENCES, MODE_PRIVATE)
        darkTheme = sharedPrefs.getBoolean(THEME_KEY, darkTheme)
        switchTheme(darkTheme)
        startKoin {
            androidContext(this@App)
            modules(dataModule, interactorModule, repositoryModule, viewModelModule)
        }
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        sharedPrefs.edit().putBoolean(THEME_KEY, darkThemeEnabled).apply()
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}