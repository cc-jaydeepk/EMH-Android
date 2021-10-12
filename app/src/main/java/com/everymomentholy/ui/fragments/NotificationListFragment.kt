package com.everymomentholy.ui.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.everymomentholy.R
import com.everymomentholy.api.APIInterface
import com.everymomentholy.api.APIService
import com.everymomentholy.api.response.LiturgiesDataVo
import com.everymomentholy.api.response.NotificationResponseVo
import com.everymomentholy.interfaces.NotificationListClickListner
import com.everymomentholy.ui.adapter.NotificationListAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class NotificationListFragment : Fragment(), NotificationListClickListner {

    private lateinit var rcvNotificationList: RecyclerView
    private lateinit var notificationAdapter: NotificationListAdapter
    private lateinit var notificationLinear: LinearLayout



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notificationlist, container, false)
        rcvNotificationList = view.findViewById(R.id.rcvNotificationList)
        notificationLinear = view.findViewById(R.id.notificationLinear)
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

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onNotificationListClick(pos: Int, dataVo: LiturgiesDataVo) {
        /*val intent = Intent(context, NotificationDetailActivity::class.java)
        intent.putExtra("date", dataVo.createdAt)
        intent.putExtra("message", dataVo.message)
        startActivity(intent)*/

        // txtToolbarTitle.setText(getString(R.string.select_vehicle))


        val bundle = Bundle()
        bundle.putString("date", dataVo.createdAt)
        bundle.putString("message", dataVo.message)

        val notificationDetailFrag = NotificationDetailFragment()
        // val transaction: FragmentTransaction = fragmentManager.beginTransaction()
        val transaction: FragmentTransaction = fragmentManager!!.beginTransaction()
        transaction.replace(R.id.nav_host_fragment, notificationDetailFrag)
            .addToBackStack(null)
        transaction.commit()

        notificationDetailFrag.setArguments(bundle)
    }
}