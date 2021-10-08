package com.everymomentholy.utils

import android.content.Context
import android.content.SharedPreferences

class SavaPreferences(context: Context) {
    private val sharedPreference = context.getSharedPreferences("myPreference", 0)

    fun putInt(key: String, value: Int){
        sharedPreference.edit().putInt(key, value).apply()
    }

    fun getInt(key: String): Int {
        return sharedPreference.getInt(key, 0)
    }

    /*fun saveUser(responseVo: ResponseVo) {
        val sharedPref: SharedPreferences = getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        editor.putInt("id", responseVo.user)
        editor.putString("username", user.getUsername())
        editor.putString("email", user.getEmail())
        editor.putBoolean("logged", true)
        editor.apply()
    }*/

    private fun saveUserSettings() {
        //val userSettings: SharedPreferences = getSharedPreferences("userSettings", Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()
        editor.putInt("timeOne", 2)
        editor.apply()
    }

    private fun getUserSettings(): Int {
        //val userSettings: SharedPreferences = getSharedPreferences("userSettings", Context.MODE_PRIVATE)
       return sharedPreference.getInt("timeOne", 2)
    }
}