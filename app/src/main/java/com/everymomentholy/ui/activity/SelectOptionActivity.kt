package com.everymomentholy.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.work.*
import com.everymomentholy.R
import com.everymomentholy.services.MyLiturgiesDataWorker
import com.everymomentholy.utils.Constants
import java.util.concurrent.TimeUnit

class SelectOptionActivity : AppCompatActivity() {

    lateinit var btnRegister: Button
    lateinit var btnLogin: Button
    lateinit var txtSkip: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activty_select_option)

        supportActionBar?.hide()

        btnRegister = findViewById(R.id.btnRegister)
        btnRegister.setOnClickListener {
            val intent = Intent(this@SelectOptionActivity, RegisterActivity::class.java)
            startActivity(intent)
        }

        btnLogin = findViewById(R.id.btnLogin)
        btnLogin.setOnClickListener {
            Constants.USER_LOGIN_STATUS = Constants.LOGIN
            val intent = Intent(this@SelectOptionActivity, LoginActivity::class.java)
            startActivity(intent)
        }

        txtSkip = findViewById(R.id.txtSkip)
        txtSkip.setOnClickListener {

            Constants.USER_LOGIN_STATUS = Constants.SKIP_LOGIN
            val intent = Intent(this@SelectOptionActivity, MainActivity::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

}