package com.dicoding.moviecatalog

import android.app.Application
import com.dicoding.moviecatalog.core.di.databaseModule
import com.dicoding.moviecatalog.core.di.networkModule
import com.dicoding.moviecatalog.core.di.repositoryModule
import com.dicoding.moviecatalog.di.useCaseModule
import com.dicoding.moviecatalog.di.viewModelModule
import leakcanary.LeakCanary
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Setup Leak Canary (otomatis hanya jalan di debug build)
        setupLeakCanary()

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

    private fun setupLeakCanary() {
        if (BuildConfig.DEBUG) {
            // LeakCanary configuration
            LeakCanary.config = LeakCanary.config.copy(
                // Detect leaks yang terjadi di semua activities
                dumpHeap = true,
                // Retain objects untuk analisis
                retainedVisibleThreshold = 3
            )
        }
    }
}