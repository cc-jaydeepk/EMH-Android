package com.everymomentholy.ui.activity

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.everymomentholy.R

class NotificationDetailActivity : AppCompatActivity() {

    private lateinit var txtDateandTime: TextView
    private lateinit var txtDescription: TextView
    private lateinit var iv_toolbar_notification: ImageView
    private lateinit var txt_toolbar_name: TextView
    private lateinit var iv_toolbar_drawer: ImageView
    private lateinit var iv_toolbar_backImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notificationdetail)

        iv_toolbar_notification = findViewById(R.id.iv_toolbar_notification)
        txtDateandTime = findViewById(R.id.txtDateandTime)
        txtDescription = findViewById(R.id.txtDescription)
        txt_toolbar_name = findViewById(R.id.txt_toolbar_name)
        iv_toolbar_drawer = findViewById(R.id.iv_toolbar_drawer)
        iv_toolbar_backImage = findViewById(R.id.iv_toolbar_backImage)

        txt_toolbar_name.text = "Notifications Details"
        iv_toolbar_notification.visibility = View.GONE
        iv_toolbar_drawer.visibility = View.GONE
        iv_toolbar_backImage.visibility = View.VISIBLE

        iv_toolbar_backImage.setOnClickListener {
            onBackPressed()
        }

        val notificationDate = intent.getStringExtra("date")
        val notificationMessage = intent.getStringExtra("message")

        txtDateandTime.text = notificationDate.toString()
        txtDescription.text = notificationMessage.toString()
    }

}