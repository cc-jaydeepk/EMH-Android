package com.everymomentholy.utils

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.widget.Toast

class Utils {

    companion object {
        /**
         * This method is used to check Internet connectivity in the application. If
         * the Internet is available, then it returns true, else false.
         *
         * @param context - Context of the activity in which the connection needs to be
         * checked
         * @return
         */
        fun isNetworkAvailable(context: Context): Boolean {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo
            if (networkInfo == null) {
                Toast.makeText(
                    context,
                    "Please check your internet connection.",
                    Toast.LENGTH_SHORT
                )
                    .show()
                return false
            } else if (networkInfo.isConnected) {
                return true
            }
            return false
        }


        fun writeStringToSharedPref(activity: Context, key: String?, value: String?) {
            val sharedPref: SharedPreferences =
                activity.getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.putString(key, value)
            editor.apply()
        }

        fun readStringFromSharedPref(
            activity: Context,
            key: String?,
            defaultValue: String?
        ): String? {
            val sharedPref: SharedPreferences =
                activity.getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE)
            return if (sharedPref.getString(key, defaultValue) != null) sharedPref.getString(
                key,
                defaultValue
            ) else ""
        }

        fun writeIntToSharedPref(context: Context, key: String?, value: Int) {
            val sharedPref: SharedPreferences =
                context.getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.putInt(key, value)
            editor.apply()
        }

        fun readIntFromSharedPref(context: Context, key: String?, defaultValue: Int): Int {
            val sharedPref: SharedPreferences =
                context.getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE)
            return sharedPref.getInt(key, defaultValue)
        }

        fun writeBoolToSharedPref(activity: Context, key: String?, value: Boolean) {
            val sharedPref: SharedPreferences =
                activity.getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.putBoolean(key, value)
            editor.apply()
        }

        fun readBoolFromSharedPref(
            activity: Activity,
            key: String?,
            defaultValue: Boolean
        ): Boolean {
            val sharedPref =
                activity.getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE)
            return sharedPref.getBoolean(key, defaultValue)
        }

        /*fun showProgressDialog(
            context: Context?,
            title: String?,
            message: String?
        ): ProgressDialog? {
            val mProgressDialog = ProgressDialog(context, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT)
            mProgressDialog.setCancelable(false)
            if (title != null) mProgressDialog.setTitle(title)
            if (message != null) mProgressDialog.setMessage(message) else mProgressDialog.setMessage(
                "Please wait..."
            )
            return mProgressDialog
        }*/

        fun readStringFromSharedPrefContext(
            context: Context,
            key: String?,
            defaultValue: String?
        ): String? {
            val sharedPref: SharedPreferences =
                context.getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE)
            return if (sharedPref.getString(key, defaultValue) != null) sharedPref.getString(
                key,
                defaultValue
            ) else ""
        }
    }
}