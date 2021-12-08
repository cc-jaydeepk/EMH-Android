package com.everymomentholy.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.everymomentholy.R
import com.everymomentholy.api.APIInterface
import com.everymomentholy.api.APIService
import com.everymomentholy.api.request.NotificationReadRequestVo
import com.everymomentholy.api.response.NotificationDataVo
import com.everymomentholy.api.response.PrivateShareResponseVo
import com.everymomentholy.utils.Constants
import com.everymomentholy.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
        val notificationData = intent.getSerializableExtra("notificationData") as NotificationDataVo

        txtDateandTime.text = notificationDate.toString()
        txtDescription.text = notificationMessage.toString()

        if (notificationData.mode == "Unread") {
            readNotification(
                notificationData.notificationId,
                Utils.readIntFromSharedPref(
                    this@NotificationDetailActivity,
                    Constants.PrefUserID,
                    -1
                ),
                notificationData
            )
        }
    }

    private fun readNotification(notificationId: Int, userID: Int, dataVo: NotificationDataVo) {

        var notificationReadRequestVo: NotificationReadRequestVo = NotificationReadRequestVo()
        notificationReadRequestVo.notificationId = notificationId
        notificationReadRequestVo.userId = userID

        val request = APIService.buildService(APIInterface::class.java)
        val call = request.readUserNotification(
            notificationReadRequestVo, "bearer " + Utils.readStringFromSharedPref(
                this,
                Constants.SHARED_PREF_TOKEN,
                ""
            )
        )

        try {
            call.enqueue(object : Callback<PrivateShareResponseVo> {
                override fun onResponse(
                    call: Call<PrivateShareResponseVo>,
                    response: Response<PrivateShareResponseVo>
                ) {
                    if (response.body()?.statusCode == 1) {

                    } else {
                        Toast.makeText(
                            this@NotificationDetailActivity,
                            response.body()!!.response.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<PrivateShareResponseVo>, t: Throwable) {
                    Toast.makeText(
                        this@NotificationDetailActivity,
                        "${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

}