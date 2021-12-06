package com.everymomentholy.ui.activity

import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.everymomentholy.R
import com.everymomentholy.api.APIInterface
import com.everymomentholy.api.APIService
import com.everymomentholy.api.request.OrderHistoryRequestVo
import com.everymomentholy.api.response.NotificationResponseVo
import com.everymomentholy.api.response.OrderHistoryResponseVo
import com.everymomentholy.ui.adapter.NotificationListAdapter
import com.everymomentholy.ui.adapter.OrderHistoryAdapter
import com.everymomentholy.utils.Constants
import com.everymomentholy.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderHistoryActivity : AppCompatActivity() {

    private lateinit var rvOrderHistory: RecyclerView
    private lateinit var orderHistoryAdapter: OrderHistoryAdapter
    private lateinit var linearLayout: LinearLayout
    private lateinit var txtToolbarName: TextView
    private lateinit var ivToolbarBackImage: ImageView
    private lateinit var ivToolbarDrawer: ImageView

    @RequiresApi(Build.VERSION_CODES.CUPCAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_history)

        rvOrderHistory = findViewById(R.id.rv_order_history)
        ivToolbarDrawer = findViewById(R.id.iv_toolbar_drawer)
        ivToolbarBackImage = findViewById(R.id.iv_toolbar_backImage)
        txtToolbarName = findViewById(R.id.txt_toolbar_name)

        if (Utils.isNetworkAvailable(this)) {
            getOrderHistoryList()
        } else {
            Toast.makeText(
                this@OrderHistoryActivity,
                resources.getString(R.string.check_internet),
                Toast.LENGTH_LONG
            ).show()
        }

        txtToolbarName.text = "Order History"
        ivToolbarBackImage.visibility = View.VISIBLE
        ivToolbarDrawer.visibility = View.GONE

        ivToolbarBackImage.setOnClickListener() {
            onBackPressed()
        }
    }

    @RequiresApi(Build.VERSION_CODES.CUPCAKE)
    fun getOrderHistoryList() {

        var orderHistoryRequestVo: OrderHistoryRequestVo = OrderHistoryRequestVo()
        orderHistoryRequestVo.deviceId = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ANDROID_ID
        )

        if (Constants.USER_LOGIN_STATUS == Constants.SKIP_LOGIN) {
            orderHistoryRequestVo.userId = Constants.SKIP_LOGIN_USER_ID
        } else {
            orderHistoryRequestVo.userId =
                Utils.readIntFromSharedPref(this, Constants.PrefUserID, -1)
        }

        val request = APIService.buildService(APIInterface::class.java)
        val call = request.getOrderHistory(
            orderHistoryRequestVo.userId,
            orderHistoryRequestVo.deviceId,
            "bearer " + Utils.readStringFromSharedPref(
                this,
                Constants.SHARED_PREF_TOKEN,
                ""
            )
        )

        try {
            call.enqueue(object : Callback<OrderHistoryResponseVo> {
                override fun onResponse(
                    call: Call<OrderHistoryResponseVo>,
                    response: Response<OrderHistoryResponseVo>
                ) {
                    if (response.body()?.statusCode == 1) {
                        setAdapter(this@OrderHistoryActivity, response.body()!!)
                        /*if (context != null) {
                            adapter = GetLiturgiesAdapter(
                                context!!,
                                response.body()!!.response.data
                            )
                            viewPager.setPadding(100, 0, 100, 0)
                            viewPager.adapter = adapter;
                        }*/

                    } else {
                        Toast.makeText(
                            this@OrderHistoryActivity,
                            response.body()!!.message.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<OrderHistoryResponseVo>, t: Throwable) {
                    Toast.makeText(this@OrderHistoryActivity, "${t.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            })
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    fun setAdapter(context: Context, response: OrderHistoryResponseVo) {
        orderHistoryAdapter = OrderHistoryAdapter(
            context,
            // response.body()!!.response.data,
            response.response
        )
        val layoutManager: RecyclerView.LayoutManager =
            LinearLayoutManager(context)
        rvOrderHistory.layoutManager = layoutManager
        // attach adapter to the recycler view
        rvOrderHistory.adapter = orderHistoryAdapter
    }
}