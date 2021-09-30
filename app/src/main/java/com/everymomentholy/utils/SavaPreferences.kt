package com.everymomentholy.utils

import android.content.Context

class SavaPreferences(context: Context) {
    private val sharedPreference = context.getSharedPreferences("myPreference", 0)

    fun putInt(key: String, value: Int){
        sharedPreference.edit().putInt(key, value).apply()
    }

    fun getInt(key: String): Int {
        return sharedPreference.getInt(key, 0)
    }
}