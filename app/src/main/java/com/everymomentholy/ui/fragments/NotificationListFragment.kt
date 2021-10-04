package com.everymomentholy.ui.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.everymomentholy.R
import com.everymomentholy.api.APIInterface
import com.everymomentholy.api.APIService
import com.everymomentholy.api.response.DataVo
import com.everymomentholy.api.response.NotificationResponseVo
import com.everymomentholy.interfaces.NotificationListClickListner
import com.everymomentholy.ui.activity.NotificationDetailActivity
import com.everymomentholy.ui.adapter.NotificationListAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class NotificationListFragment : Fragment(), NotificationListClickListner {

    private lateinit var rcvNotificationList: RecyclerView
    private lateinit var notificationAdapter: NotificationListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notificationlist, container, false)
        rcvNotificationList = view.findViewById(R.id.rcvNotificationList)
        getNotificationList()
        return view
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
                        setAdapter(context!!, response.body()!!)
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
                            context,
                            response.body()!!.response.msg.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<NotificationResponseVo>, t: Throwable) {
                    Toast.makeText(context, "${t.message}", Toast.LENGTH_SHORT).show()
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
            this@NotificationListFragment
        )
        val layoutManager: RecyclerView.LayoutManager =
            LinearLayoutManager(context)
        rcvNotificationList.layoutManager = layoutManager
        // attach adapter to the recycler view
        rcvNotificationList.adapter = notificationAdapter
    }

    override fun onNotificationListClick(pos: Int, dataVo: DataVo) {
        val intent = Intent(context, NotificationDetailActivity::class.java)
        intent.putExtra("date", dataVo.createdAt)
        intent.putExtra("message", dataVo.message)
        startActivity(intent)
    }
}