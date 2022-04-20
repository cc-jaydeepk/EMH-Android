package com.everymomentholy.ui.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.work.*
import com.bumptech.glide.util.Util
import com.everymomentholy.R
import com.everymomentholy.services.MyLiturgiesDataWorker
import com.everymomentholy.utils.Constants
import com.everymomentholy.utils.Utils
import java.util.concurrent.TimeUnit

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        supportActionBar?.hide()

        executeLiturgiesDownloadWork()

        Handler(Looper.getMainLooper()).postDelayed({
            val sharedPref: SharedPreferences =
                getSharedPreferences(Constants.SHARED_PREF_NAME, 0)
            // boolean hasLoggedIn = sharedpreference.getBoolean("hasLoggedIn, false)
            val hasLoggedIn = sharedPref.getBoolean("hasLoggedIn", false)
            if (Utils.readIntFromSharedPref(this, Constants.PrefUserID, -1) > 0) {
                val intent = Intent(this@SplashActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                val mainIntent = Intent(this@SplashActivity, SelectOptionActivity::class.java)
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(mainIntent)
                finish()
            }
        }, 2500)

    }

    private fun executeLiturgiesDownloadWork() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val uploadWorkRequest = OneTimeWorkRequestBuilder<MyLiturgiesDataWorker>()
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()

        WorkManager.getInstance().enqueue(uploadWorkRequest)
    }
}