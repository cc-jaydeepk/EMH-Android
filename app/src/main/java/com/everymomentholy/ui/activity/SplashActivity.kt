package com.everymomentholy.ui.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.util.Util
import com.everymomentholy.R
import com.everymomentholy.utils.Constants
import com.everymomentholy.utils.Utils

class SplashActivity : AppCompatActivity() {

    lateinit var handler: Handler
    // var hasLoggedIn: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        supportActionBar?.hide()

        /*handler = Handler()
        handler.postDelayed({
            val intent = Intent(this@SplashActivity, SelectOptionActivity::class.java)
            startActivity(intent)
            finish()
        }, 2000)*/

        /*Handler().postDelayed({
            val sharedPref: SharedPreferences =
                getSharedPreferences(Constants.SHARED_PREF_NAME, 0)
            // boolean hasLoggedIn = sharedpreference.getBoolean("hasLoggedIn, false)
          val  hasLoggedIn = sharedPref.getBoolean("hasLoggedIn", false)
            if (hasLoggedIn) {
                val intent = Intent(this@SplashActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                val mainIntent = Intent(this@SplashActivity, SelectOptionActivity::class.java)
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(mainIntent)
                finish()
            }
        }, 2000)*/

        Handler().postDelayed({
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
        }, 2000)

    }
}