package com.dicoding.moviecatalog

import android.app.Application
import com.dicoding.moviecatalog.core.di.databaseModule
import com.dicoding.moviecatalog.core.di.networkModule
import com.dicoding.moviecatalog.core.di.repositoryModule
import com.dicoding.moviecatalog.di.useCaseModule
import com.dicoding.moviecatalog.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.NONE)
            androidContext(this@MyApplication)
            modules(
                listOf(
                    databaseModule,
                    networkModule,
                    repositoryModule,
                    useCaseModule,
                    viewModelModule
                )
            )
        }
    }
}