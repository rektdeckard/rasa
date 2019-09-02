package com.tobiasfried.rasa

import android.app.Application

import com.jakewharton.threetenabp.AndroidThreeTen
import timber.log.Timber
import timber.log.Timber.DebugTree

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize Time Zone
        AndroidThreeTen.init(this)

        // Timber initialization
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
    }

}
