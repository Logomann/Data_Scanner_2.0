package com.logomann.datascanner20.di

import com.logomann.datascanner20.data.network.impl.ConnectionRepositoryImpl
import com.logomann.datascanner20.data.settings.impl.SettingsRepositoryImpl
import com.logomann.datascanner20.domain.network.ConnectionRepository
import com.logomann.datascanner20.domain.settings.SettingsRepository
import org.koin.dsl.module

val repositoryModule = module {

    factory<ConnectionRepository> {
        ConnectionRepositoryImpl(get())
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(get(),get())
    }


}