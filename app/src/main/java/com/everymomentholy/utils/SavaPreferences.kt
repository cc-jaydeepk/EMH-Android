package com.everymomentholy.utils

import android.content.Context
import android.content.SharedPreferences




class SavaPreferences(context: Context) {
    private val sharedPreference = context.getSharedPreferences("myPreference", 0)

    fun putInt(key: String, value: Int) {
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

    private var sharedPref: SharedPreferences? = null
    private val PREF_STRING = "pref_value"
    fun login(username_params: String?, email_params: String?) {

       // sharedPref = getSharedPreferences(PREF_STRING, 0)
        val editor: SharedPreferences.Editor = sharedPreference.edit()
        editor.putString("username", username_params)
        editor.putString("email", email_params)
       // transaction
        editor.commit() // only after commit() data is saved in sharedPreferences
    }

    fun getDataFromPrefernces() {
       // val prefefnces: SharedPreferences = this.getApplicationContext<Context>().getSharedPreferences(PREF_STRING, 0)
        val editor = sharedPreference.edit()
        val username = sharedPreference.getString("username", "")
        val email = sharedPreference.getString("email", "")
        editor.commit();
    }




}