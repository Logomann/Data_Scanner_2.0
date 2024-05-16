package com.logomann.datascanner20.di

import android.app.Application
import android.content.SharedPreferences
import com.logomann.datascanner20.data.NetworkClient
import com.logomann.datascanner20.data.network.impl.PostgreNetworkClient
import com.logomann.datascanner20.util.App
import com.logomann.datascanner20.util.PREFERENCES
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module


val dataModule = module {
    factory<NetworkClient> {
        PostgreNetworkClient(
            androidApplication(),
            get()
        )
    }
    single<SharedPreferences> {
        androidContext().getSharedPreferences(PREFERENCES, Application.MODE_PRIVATE)
    }
    single<App> {
        androidContext() as App
    }
}