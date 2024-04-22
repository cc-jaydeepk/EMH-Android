package com.everymomentholy.ui.activity

import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.everymomentholy.R
import com.everymomentholy.api.APIInterface
import com.everymomentholy.api.APIService
import com.everymomentholy.api.request.OrderHistoryRequestVo
import com.everymomentholy.api.request.OrderAndSubReqVo
import com.everymomentholy.api.response.OrderHistoryResponseVo
import com.everymomentholy.api.response.OrderAndSubResponseVo
import com.everymomentholy.ui.adapter.OrderAndSubscriptionHostory
import com.everymomentholy.utils.Constants
import com.everymomentholy.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderHistoryActivity : AppCompatActivity() {

    // private lateinit var rvOrderHistory: RecyclerView
    // private lateinit var orderHistoryAdapter: OrderHistoryAdapter
    private lateinit var linearLayout: LinearLayout
    private lateinit var txtToolbarName: TextView
    private lateinit var ivToolbarBackImage: ImageView
    private lateinit var ivToolbarDrawer: ImageView

    private lateinit var rvPurchaseHistory: RecyclerView
    private lateinit var pastPurchaseAdapter: OrderAndSubscriptionHostory
    lateinit var progressCardView: CardView

    @RequiresApi(Build.VERSION_CODES.CUPCAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_history)

        progressCardView = findViewById(R.id.progressCardView)
        rvPurchaseHistory = findViewById(R.id.rv_order_history)
        ivToolbarDrawer = findViewById(R.id.iv_toolbar_drawer)
        ivToolbarBackImage = findViewById(R.id.iv_toolbar_backImage)
        txtToolbarName = findViewById(R.id.txt_toolbar_name)

        if (Utils.isNetworkAvailable(this)) {
            // getOrderHistoryList()
            progressCardView.visibility = View.VISIBLE
            getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
            getOrderAndSubscriptionHistory()
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

    fun getOrderAndSubscriptionHistory() {

        var pastPurchaseHistoryVo: OrderAndSubReqVo = OrderAndSubReqVo()
        pastPurchaseHistoryVo.deviceId = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ANDROID_ID
        )

        if (Constants.USER_LOGIN_STATUS == Constants.SKIP_LOGIN) {
            pastPurchaseHistoryVo.userId = Constants.SKIP_LOGIN_USER_ID
        } else {
            pastPurchaseHistoryVo.userId =
                Utils.readIntFromSharedPref(this, Constants.PrefUserID, -1)
        }

        val request = APIService.buildService(APIInterface::class.java)
        val call = request.getOrderandSubscriptionHistory(
            pastPurchaseHistoryVo.userId,
            pastPurchaseHistoryVo.deviceId,
            "bearer " + Utils.readStringFromSharedPref(
                this,
                Constants.SHARED_PREF_TOKEN,
                ""
            )
        )

        try {
            call.enqueue(object : Callback<OrderAndSubResponseVo> {
                override fun onResponse(
                    call: Call<OrderAndSubResponseVo>,
                    response: Response<OrderAndSubResponseVo>
                ) {
                    if (response.body()?.statusCode == 1) {
                        progressCardView.visibility = View.GONE
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        setAdapter(this@OrderHistoryActivity, response.body()!!)

                    } else {
                        progressCardView.visibility = View.GONE
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        Toast.makeText(
                            this@OrderHistoryActivity,
                            response.body()!!.message.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<OrderAndSubResponseVo>, t: Throwable) {
                    Toast.makeText(this@OrderHistoryActivity, "${t.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            })
        } catch (exception: Exception) {
            exception.printStackTrace()
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
                        // setAdapter(this@OrderHistoryActivity, response.body()!!)
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

    fun setAdapter(context: Context, response: OrderAndSubResponseVo) {
        pastPurchaseAdapter = OrderAndSubscriptionHostory(
            context,
            response.response
        )
        val layoutManager: RecyclerView.LayoutManager =
            LinearLayoutManager(context)
        rvPurchaseHistory.layoutManager = layoutManager
        // attach adapter to the recycler view
        rvPurchaseHistory.adapter = pastPurchaseAdapter
    }

    /*fun setAdapter(context: Context, response: OrderHistoryResponseVo) {
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
    }*/
}