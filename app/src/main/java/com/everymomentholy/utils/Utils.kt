package com.everymomentholy.utils

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.everymomentholy.R
import com.everymomentholy.api.response.MyLiturgiesDataVo
import com.everymomentholy.ui.activity.SelectOptionActivity
import com.everymomentholy.utils.SharedPreference.Companion.getPreferences
import com.folioreader.FolioReader
import java.io.*

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
                /*Toast.makeText(
                    context,
                    "Please check your internet connection.",
                    Toast.LENGTH_SHORT
                )
                    .show()*/
                return false
            } else if (networkInfo.isConnected) {
                return true
            }
            return false
        }

        fun readBoolean(context: Context?, status: Boolean) {
            val editor = getPreferences(context).edit()
            editor.putBoolean(Constants.IS_NOTIFICATION_ON, status)
            editor.apply()
        }

        fun writeBoolean(context: Context?): Boolean {
            return getPreferences(context).getBoolean(Constants.IS_NOTIFICATION_ON, false)
        }

        fun writeBoolean(context: Context, key: String?, value: Boolean) {
            val sharedPref: SharedPreferences =
                context.getSharedPreferences(Constants.IS_NOTIFICATION_ON, Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.putBoolean(key, value)
            editor.apply()
        }

        fun readBoolFromSharedPref(
            context: Context,
            key: String,
            defaultValue: Boolean
        ): Boolean {
            val sharedPref =
                context.getSharedPreferences(Constants.IS_NOTIFICATION_ON, Context.MODE_PRIVATE)
            return sharedPref.getBoolean(key, defaultValue)
        }


        fun writeUserIdBooleanFromSharedPref(context: Context?, loggedIn: Boolean) {
            val editor = getPreferences(context).edit()
            editor.putBoolean(Constants.LOGGED_IN_PREF, loggedIn)
            editor.apply()
        }

        fun readUserIdBooleanFromSharedPref(context: Context?): Boolean {
            return getPreferences(context).getBoolean(Constants.LOGGED_IN_PREF, false)
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

        fun readStringData(activity: Context, key: String?, defaultValue: String?) {
            val sharedPreference =
                activity.getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE)
            var editor = sharedPreference.edit()
            //editor.putString(key, defaultValue)
            editor.putString(key, defaultValue)
            editor.apply()
        }

        fun writeStringData(activity: Context, key: String?, defaultValue: String?) {
            val sharedPreference =
                activity.getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE)
            sharedPreference.getString(key, defaultValue)
        }

        fun writeIntToSharedPref(context: Context, key: String?, value: Int) {
            val sharedPref: SharedPreferences =
                context.getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.putInt(key, value)
            editor.apply()
        }


        fun readIntData(context: Context, key: String?, defaultValue: Int): Int {
            val sharedPref: SharedPreferences =
                context.getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE)
            return sharedPref.getInt(key, defaultValue)
        }

        fun clearAllPreference(context: Context) {
            val sharedPref: SharedPreferences =
                context.getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.clear()

            editor.apply()
        }


        fun readBoolFromSharedPrefe(context: Context?, loggedIn: Boolean) {
            val editor: SharedPreferences.Editor = getPreferences(context).edit()
            editor.putBoolean(Constants.LOGGED_IN_PREF, loggedIn)
            editor.apply()
        }

        /*fun getLoggedStatus(context: Context?): Boolean {
            return getPreferences(context).getBoolean(LOGGED_IN_PREF, false)
        }*/

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

        /*fun readBoolFromSharedPref(
            activity: Activity,
            key: String?,
            defaultValue: Boolean
        ): Boolean {
            val sharedPref =
                activity.getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE)
            return sharedPref.getBoolean(key, defaultValue)
        }*/

        fun showProgressDialog(
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
        }

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


        fun invokeBookReader(
            context: Context,
            path: String,
            myLiturgiesDataVo: MyLiturgiesDataVo,
        ) {
            val folioReader = FolioReader.get()

            val myLiturgyVo: com.folioreader.emh.MyLiturgiesDataVo =
                com.folioreader.emh.MyLiturgiesDataVo()
            myLiturgyVo.userId =
                readIntData(context, Constants.PrefUserID, -1)
            myLiturgyVo.token = "bearer " + readStringFromSharedPref(
                context,
                Constants.SHARED_PREF_TOKEN,
                ""
            )
            myLiturgyVo.bookId = myLiturgiesDataVo.bookId
            myLiturgyVo.chapterId = myLiturgiesDataVo.chapterId
            myLiturgyVo.isFavorite = myLiturgiesDataVo.isFavorite
            myLiturgyVo.audio_file = myLiturgiesDataVo.audio_file
            myLiturgyVo.volumeTags = myLiturgiesDataVo.volumeTags
            myLiturgyVo.isPurchased = myLiturgiesDataVo.isPurchased

            folioReader.openBook(
                context?.filesDir?.absolutePath + "/" + "test_" + myLiturgiesDataVo.chapterId + ".epub",
                myLiturgyVo
            )
        }

        fun showDialogForUnlockWithoutLogin(context: Context) {
            val alertDialog = AlertDialog.Builder(
                context
            )
            val inflater = (context as Activity).layoutInflater
            val alertView: View = inflater.inflate(R.layout.purchase_without_login_dialog, null)
            alertDialog.setView(alertView)
            val show = alertDialog.show()
            val alertButtonCancel = alertView.findViewById<View>(R.id.txtCancel) as TextView
            val alertButtonLoginRegister =
                alertView.findViewById<View>(R.id.txtPurchaseRegisterLogin) as TextView
            val alertButtonPurchase =
                alertView.findViewById<View>(R.id.txtPurchaseWithoutRegisterLogin) as TextView


            alertButtonLoginRegister.setOnClickListener {
                val intent = Intent(context, SelectOptionActivity::class.java)
                context.startActivity(intent)
            }

            alertButtonCancel.setOnClickListener {
                show.dismiss()
            }

            alertButtonPurchase.setOnClickListener() {

            }
            show.setCanceledOnTouchOutside(false)
        }

        fun showProgressDialog(context: Context): ProgressDialog? {
            val progressDialog = ProgressDialog(context)
            progressDialog.setTitle("Every Moment Holy")
            progressDialog.setMessage("Please wait")
            return progressDialog
        }

        fun readJsonFromFile(context: Context, fileName: String): String? {
            return try {
                val fis: FileInputStream = context.openFileInput(fileName)
                val isr = InputStreamReader(fis)
                val bufferedReader = BufferedReader(isr)
                val sb = StringBuilder()
                var line: String?
                while (bufferedReader.readLine().also { line = it } != null) {
                    sb.append(line)
                }
                sb.toString()
            } catch (fileNotFound: FileNotFoundException) {
                null
            } catch (ioException: IOException) {
                null
            }
        }

        fun storeJsonInFile(context: Context, jsonString: String?, fileName: String): Boolean {
            return try {
                val fos: FileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE)
                if (jsonString != null) {
                    fos.write(jsonString.toByteArray())
                }
                fos.close()
                true
            } catch (fileNotFound: FileNotFoundException) {
                false
            } catch (ioException: IOException) {
                false
            }
        }

        /*fun isFilePresent(context: Context): Boolean {
            val path = context.filesDir.absolutePath + "/" + FILE_NAME
            val file = File(path)
            return file.exists()
        }*/
    }

}