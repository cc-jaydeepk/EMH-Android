package com.everymomentholy.ui.activity

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.everymomentholy.R

class NotificationDetailActivity : AppCompatActivity() {

    private lateinit var txtDateandTime: TextView
    private lateinit var txtDescription: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notificationdetail)

        txtDateandTime = findViewById(R.id.txtDateandTime)
        txtDescription = findViewById(R.id.txtDescription)

        val notificationDate = intent.getStringExtra("date")
        val notificationMessage = intent.getStringExtra("message")

        txtDateandTime.text = notificationDate.toString()
        txtDescription.text = notificationMessage.toString()
    }

}