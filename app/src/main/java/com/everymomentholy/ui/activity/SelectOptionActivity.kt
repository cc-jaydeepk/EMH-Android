package com.everymomentholy.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.everymomentholy.R

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
            val intent = Intent(this@SelectOptionActivity, LoginActivity::class.java)
            startActivity(intent)
        }

        txtSkip = findViewById(R.id.txtSkip)
        txtSkip.setOnClickListener {

            AlertDialog.Builder(this)
                .setMessage("This part is under Development.")
                .setPositiveButton(android.R.string.yes) { dialog, which ->
                }.show()
            // val intent = Intent(this@FirstActivity, HomeActivity::class.java)
       /*     val intent = Intent(this@SelectOptionActivity, MainActivity::class.java)
            startActivity(intent)*/
        }
    }
}