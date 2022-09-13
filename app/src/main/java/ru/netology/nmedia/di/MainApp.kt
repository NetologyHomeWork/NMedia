package ru.netology.nmedia.di

import android.app.Application
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class MainApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            modules(componentModule)
            androidLogger(level = Level.ERROR)
        }
    }
}