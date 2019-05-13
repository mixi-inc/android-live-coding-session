package dev.mixi.raichou

import android.app.Application
import timber.log.Timber

class DebugApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}