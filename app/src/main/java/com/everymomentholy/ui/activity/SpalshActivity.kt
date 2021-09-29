package com.everymomentholy.ui.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.everymomentholy.R

class SpalshActivity : AppCompatActivity() {
    lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        supportActionBar?.hide()

        handler = Handler()
        handler.postDelayed({
            val intent = Intent(this@SpalshActivity, FirstActivity::class.java)
            startActivity(intent)
            finish()
        }, 2000)
    }
}