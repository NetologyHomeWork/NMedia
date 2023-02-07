package ru.netology.nmedia.di

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import ru.netology.nmedia.data.auth.AppAuth

class MainApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MainApp)
            modules(componentModule, networkModule)
            androidLogger(level = Level.ERROR)
        }
        AppAuth.init(this)
    }
}