package com.everymomentholy

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.downloader.PRDownloader
import com.downloader.PRDownloaderConfig
import kotlinx.coroutines.GlobalScope

class EMHApplication : Application() {

    lateinit var appContainer: AppContainer

    // Container of objects shared across the whole app
    inner class AppContainer {
        private val applicationScope = GlobalScope
        /*private val inAppUtils = InAppUtils.getInstance(
            this@EMHApplication,
            applicationScope
        )*/
    }

    override fun onCreate() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate()
        appContainer = AppContainer()
        val config = PRDownloaderConfig.newBuilder()
            .setDatabaseEnabled(true)
            .setReadTimeout(30000)
            .setConnectTimeout(30000)
            .build()
        PRDownloader.initialize(applicationContext, config)
    }
}