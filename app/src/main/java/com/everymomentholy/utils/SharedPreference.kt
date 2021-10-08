package com.everymomentholy.utils

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager


class SharedPreference {
    companion object {

        val USER_ID = "userId"

        fun getPreferences(context: Context?): SharedPreferences {
            return PreferenceManager.getDefaultSharedPreferences(context)
        }

        fun setLoggedIn(context: Context?, loggedIn: Boolean) {
            val editor = getPreferences(context).edit()
            editor.putBoolean(USER_ID, loggedIn)
            editor.apply()
        }

        fun getLoggedStatus(context: Context?): Boolean {
            return getPreferences(context).getBoolean(USER_ID, false)
        }

        fun SavePreferences(activity: Context, key: String, value: Int) {
            val sharedPref: SharedPreferences =
                activity.getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.putInt(key, value)
            editor.commit()
        }

        private fun getPrefs(context: Context): SharedPreferences? {
            return context.getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE)
        }

        fun save(context: Context?, key: String?, value: Int) {
            getPrefs(context!!)!!.edit().putInt(key, value).commit()
        }

        fun getInt(context: Context?, key: String?): Int {
            return getPrefs(context!!)!!.getInt(key, 0)
        }

        /*private fun SavePreferences(key: String, value: Int) {
            val sharedPreferences = getPreferences(MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putInt(key, value)
            editor.commit()
        }*/


    }
}