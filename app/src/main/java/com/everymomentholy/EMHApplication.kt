package com.everymomentholy

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.downloader.PRDownloader
import com.downloader.PRDownloaderConfig

class EMHApplication : Application(){

    override fun onCreate() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate()
        val config = PRDownloaderConfig.newBuilder()
            .setDatabaseEnabled(true)
            .setReadTimeout(30000)
            .setConnectTimeout(30000)
            .build()
        PRDownloader.initialize(applicationContext, config)
    }
}