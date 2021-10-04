package com.everymomentholy.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.everymomentholy.R
import com.everymomentholy.api.APIInterface
import com.everymomentholy.api.APIService
import com.everymomentholy.api.response.DataVo
import com.everymomentholy.api.response.NotificationResponseVo
import com.everymomentholy.interfaces.NotificationListClickListner
import com.everymomentholy.ui.adapter.NotificationListAdapter
import com.everymomentholy.ui.fragments.NotificationListFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class NotificationListActivity : AppCompatActivity(), NotificationListClickListner {

    // private lateinit var rcvNotificationList: RecyclerView
    // private lateinit var notificationAdapter: NotificationListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notificationlist)

        val fragment: Fragment = NotificationListFragment()
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.nav_host_notification, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()


        //rcvNotificationList = findViewById(R.id.rcvNotificationList)

        // getNotificationList()
    }

    /*fun getNotificationList() {
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
                        notificationAdapter = NotificationListAdapter(
                            this@NotificationListActivity,
                            response.body()!!.response.data,
                            this@NotificationListActivity
                        )
                        val layoutManager: RecyclerView.LayoutManager =
                            LinearLayoutManager(this@NotificationListActivity)
                        rcvNotificationList.layoutManager = layoutManager
                        // attach adapter to the recycler view
                        rcvNotificationList.adapter = notificationAdapter

                    } else {
                        Toast.makeText(
                            this@NotificationListActivity,
                            response.body()!!.response.msg.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<NotificationResponseVo>, t: Throwable) {
                    Toast.makeText(this@NotificationListActivity, "${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }*/


    override fun onNotificationListClick(pos: Int, dataVo: DataVo) {
        val intent = Intent(this@NotificationListActivity, NotificationDetailActivity::class.java)
        startActivity(intent)
    }
}