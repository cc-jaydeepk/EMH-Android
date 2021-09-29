package com.everymomentholy.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.everymomentholy.R

class FirstActivity : AppCompatActivity() {

    lateinit var btnRegister: Button
    lateinit var btnLogin: Button
    lateinit var txtSkip: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first)

        supportActionBar?.hide()

        btnRegister = findViewById(R.id.btnRegister)
        btnRegister.setOnClickListener {
            val intent = Intent(this@FirstActivity, RegisterActivty::class.java)
            startActivity(intent)
        }

        btnLogin = findViewById(R.id.btnLogin)
        btnLogin.setOnClickListener {
            val intent = Intent(this@FirstActivity, LoginActivity::class.java)
            startActivity(intent)
        }

        txtSkip = findViewById(R.id.txtSkip)
        txtSkip.setOnClickListener {
            // val intent = Intent(this@FirstActivity, HomeActivity::class.java)
            val intent = Intent(this@FirstActivity, MainActivity::class.java)
            startActivity(intent)
        }
    }
}