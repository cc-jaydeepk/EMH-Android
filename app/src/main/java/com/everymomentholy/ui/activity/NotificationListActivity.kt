package com.everymomentholy.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.everymomentholy.R
import com.everymomentholy.api.APIInterface
import com.everymomentholy.api.APIService
import com.everymomentholy.api.response.NotificationDataVo
import com.everymomentholy.api.response.NotificationResponseVo
import com.everymomentholy.interfaces.NotificationListClickListner
import com.everymomentholy.ui.adapter.NotificationListAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationListActivity : AppCompatActivity(), NotificationListClickListner {

    private lateinit var rcvNotificationList: RecyclerView
    private lateinit var notificationAdapter: NotificationListAdapter
    private lateinit var notificationLinear: LinearLayout
    private lateinit var iv_toolbar_notification: ImageView
    private lateinit var txt_toolbar_name: TextView
    private lateinit var iv_toolbar_drawer: ImageView
    private lateinit var iv_toolbar_backImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notificationlist)
        // setContentView(R.layout.fragment_notificationlist)

        iv_toolbar_notification = findViewById(R.id.iv_toolbar_notification)
        txt_toolbar_name = findViewById(R.id.txt_toolbar_name)
        iv_toolbar_drawer = findViewById(R.id.iv_toolbar_drawer)
        iv_toolbar_backImage = findViewById(R.id.iv_toolbar_backImage)
        rcvNotificationList = findViewById(R.id.rcvNotificationList)
        notificationLinear = findViewById(R.id.notificationLinear)

        txt_toolbar_name.text = "Notifications"
        iv_toolbar_notification.visibility = View.GONE
        iv_toolbar_drawer.visibility = View.GONE
        iv_toolbar_backImage.visibility = View.VISIBLE

        iv_toolbar_backImage.setOnClickListener {
            onBackPressed()
        }


        getNotificationList()

        /*val fragment: Fragment = NotificationListFragment()
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.nav_host_notification, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()*/


    }

    private fun getNotificationList() {
        val request = APIService.buildService(APIInterface::class.java)
        val call = request.notificationList()

        try {
            call.enqueue(object : Callback<NotificationResponseVo> {
                override fun onResponse(
                    call: Call<NotificationResponseVo>,
                    response: Response<NotificationResponseVo>
                ) {
                    if (response.body()?.statusCode == 1) {

                        // setAdapter(this@NotificationListActivity, response.body()!!)
                        setAdapter(this@NotificationListActivity, response.body()!!)
                        /* notificationAdapter = NotificationListAdapter(
                             context,
                             response.body()!!.response.data,
                             this@NotificationListActivity
                         )
                         val layoutManager: RecyclerView.LayoutManager =
                             LinearLayoutManager(this@NotificationListActivity)
                         rcvNotificationList.layoutManager = layoutManager
                         // attach adapter to the recycler view
                         rcvNotificationList.adapter = notificationAdapter*/

                    } else {
                        Toast.makeText(
                            this@NotificationListActivity,
                            response.body()!!.response.msg.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<NotificationResponseVo>, t: Throwable) {
                    Toast.makeText(
                        this@NotificationListActivity,
                        "${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    fun setAdapter(context: Context, response: NotificationResponseVo) {
        notificationAdapter = NotificationListAdapter(
            context,
            // response.body()!!.response.data,
            response.response.data,
            this@NotificationListActivity
        )
        val layoutManager: RecyclerView.LayoutManager =
            LinearLayoutManager(context)
        rcvNotificationList.layoutManager = layoutManager
        // attach adapter to the recycler view
        rcvNotificationList.adapter = notificationAdapter
    }


    override fun onNotificationListClick(pos: Int, dataVo: NotificationDataVo) {
        val intent = Intent(this, NotificationDetailActivity::class.java)
        intent.putExtra("date", dataVo.createdAt)
        intent.putExtra("message", dataVo.message)
        startActivity(intent)
    }
}