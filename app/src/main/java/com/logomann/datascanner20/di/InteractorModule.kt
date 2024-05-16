package com.logomann.datascanner20.di

import com.logomann.datascanner20.domain.impl.network.ConnectionInteractorImpl
import com.logomann.datascanner20.domain.network.ConnectionInteractor
import com.logomann.datascanner20.domain.settings.SettingsInteractor
import com.logomann.datascanner20.domain.settings.impl.SettingsInteractorImpl
import org.koin.dsl.module

val interactorModule = module {
    factory<ConnectionInteractor> {
         ConnectionInteractorImpl(get())
    }
    single<SettingsInteractor> {
        SettingsInteractorImpl(get())
    }
}