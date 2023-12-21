package com.everymomentholy.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.everymomentholy.R
import com.everymomentholy.api.APIInterface
import com.everymomentholy.api.APIService
import com.everymomentholy.api.request.CancelSubscriptionReqVo
import com.everymomentholy.api.response.CancelSubscriptionResVo
import com.everymomentholy.utils.Constants
import com.everymomentholy.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MySubscriptionStatus : AppCompatActivity() {


    private lateinit var iv_toolbar_backImage: ImageView
    private lateinit var iv_toolbar_drawer: ImageView
    private lateinit var iv_toolbar_notification: ImageView
    private lateinit var txt_toolbar_name: TextView
    private lateinit var txtStartDate: TextView
    private lateinit var txtExpiretDate: TextView
    private lateinit var txtTypeDate: TextView
    private lateinit var txtSttus: TextView
    private lateinit var btnUnsubscribe: Button
    private lateinit var txtUpcominStartDate: TextView
    private lateinit var txtUpcominExpiretDate: TextView
    private lateinit var txtUpcominTypeDate: TextView
    private lateinit var txtUpcominSttus: TextView

    var prefeUserId: Int = 0
    lateinit var subScription_id: String
    lateinit var cancelMessage: String
    lateinit var subscription_status: String
    lateinit var progressCardView: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mysubscription_status)

        prefeUserId = Utils.readIntData(
            this@MySubscriptionStatus,
            Constants.PrefUserID,
            0
        )!!

        var subscriptionStatus = Utils.readStringFromSharedPref(
            this@MySubscriptionStatus, Constants.USER_SUBSCRIPTIONSTATUS,
            ""
        )

        var upcomingPlan = Utils.readStringFromSharedPref(
            this@MySubscriptionStatus, Constants.UpcomingPlan,
            ""
        )


        txtUpcominStartDate = findViewById(R.id.txtUpcominStartDate)
        txtUpcominTypeDate = findViewById(R.id.txtUpcominTypeDate)
        txtUpcominExpiretDate = findViewById(R.id.txtUpcominExpiretDate)
        txtUpcominSttus = findViewById(R.id.txtUpcominSttus)
        progressCardView = findViewById(R.id.progressCardView)
        iv_toolbar_drawer = findViewById(R.id.iv_toolbar_drawer)
        iv_toolbar_backImage = findViewById(R.id.iv_toolbar_backImage)
        txt_toolbar_name = findViewById(R.id.txt_toolbar_name)
        iv_toolbar_notification = findViewById(R.id.iv_toolbar_notification)
        txtStartDate = findViewById(R.id.txtStartDate)
        txtExpiretDate = findViewById(R.id.txtExpiretDate)
        txtTypeDate = findViewById(R.id.txtTypeDate)
        txtSttus = findViewById(R.id.txtSttus)
        btnUnsubscribe = findViewById(R.id.btnUnsubscribe)


        txtUpcominStartDate.text = Utils.readStringFromSharedPref(
            this@MySubscriptionStatus, Constants.UPCOMING_STARTED_AT,
            ""
        ).toString()

        txtUpcominExpiretDate.text = Utils.readStringFromSharedPref(
            this@MySubscriptionStatus, Constants.UPCOMING_EXPIRED_AT,
            ""
        ).toString()

        txtUpcominTypeDate.text = Utils.readStringFromSharedPref(
            this@MySubscriptionStatus, Constants.UPCOMING_SUB_TYPE,
            ""
        ).toString()

        txtUpcominSttus.text = Utils.readStringFromSharedPref(
            this@MySubscriptionStatus, Constants.UPCOMING_SUB_STATUS,
            ""
        ).toString()

        iv_toolbar_backImage.setOnClickListener {
//            val intent = Intent(this, MySubscriptionStatus::class.java)
//
//            startActivity(intent)
            onBackPressed()
        }

        val startDate = intent.getStringExtra("Start")
        val endDate = intent.getStringExtra("End")
        val statusDate = intent.getStringExtra("Status")
        val typeDate = intent.getStringExtra("Type")
        subScription_id = intent.getStringExtra("SubscriptionId").toString()

        txtStartDate.text = startDate
        txtExpiretDate.text = endDate
        txtSttus.text = statusDate
        txtTypeDate.text = typeDate
        subscription_status = ""
        if (statusDate != null) {
            subscription_status = statusDate
        }

        if (subscriptionStatus.equals("Yes",true) && upcomingPlan.equals("Yes",true)) {
            btnUnsubscribe.visibility = View.GONE
        } else if(subscription_status.equals("cancelled",true) || subscription_status.equals("canceled",true))
        {
            btnUnsubscribe.visibility = View.GONE
        }
        else {
            btnUnsubscribe.visibility = View.VISIBLE
        }


        iv_toolbar_backImage.visibility = View.VISIBLE
        iv_toolbar_drawer.visibility = View.GONE
        iv_toolbar_notification.visibility = View.GONE
        txt_toolbar_name.text = "Subscription Plan"
        txt_toolbar_name.setTextColor(ContextCompat.getColor(this, R.color.loginbg));

        btnUnsubscribe.setOnClickListener {
            btnUnsubscribe.isEnabled = false
            progressCardView.visibility = View.VISIBLE
            getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            );
            var cancelSubscriptionReqVo: CancelSubscriptionReqVo =
                CancelSubscriptionReqVo()
            cancelSubscriptionReqVo.appUserId = prefeUserId
            cancelSubscriptionReqVo.subscriptionId = subScription_id

            cancelSubscriptionPlan(cancelSubscriptionReqVo)
        }

        iv_toolbar_backImage.setOnClickListener {
            onBackPressed()
        }
    }

    private fun cancelSubscriptionPlan(cancelSubscriptionReqVo: CancelSubscriptionReqVo) {
        val request = APIService.buildService(APIInterface::class.java)
        val call = request.cancelSubscription(cancelSubscriptionReqVo)

        try {
            call.enqueue(object : Callback<CancelSubscriptionResVo> {
                override fun onResponse(
                    call: Call<CancelSubscriptionResVo>,
                    response: Response<CancelSubscriptionResVo>
                ) {
                    if (response.body()?.statusCode == 1) {

                        Utils.writeStringToSharedPref(
                            this@MySubscriptionStatus, Constants.USER_SUBSCRIPTIONSTATUS,
                            response.body()!!.subscription
                        )

                        cancelMessage = response.body()!!.message

                        progressCardView.visibility = View.GONE
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                        showCancelSubscriptioDialog()

                    } else {
                        progressCardView.visibility = View.GONE
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        Toast.makeText(
                            this@MySubscriptionStatus,
                            response.body()?.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }

                }

                override fun onFailure(call: Call<CancelSubscriptionResVo>, t: Throwable) {
                    // Toast.makeText(context, "${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    private fun showCancelSubscriptioDialog() {

        val alertDialog = AlertDialog.Builder(
            this
        )
        val inflater = layoutInflater
        val alertView: View = inflater.inflate(R.layout.subscription_status_dialog, null)
        alertDialog.setView(alertView)
        val show = alertDialog.show()
        val txtMessage = alertView.findViewById<View>(R.id.txtMessage) as TextView
        val txtOk = alertView.findViewById<View>(R.id.txtOk) as TextView
        val txtHeader = alertView.findViewById<TextView>(R.id.txtHeader) as TextView


        txtMessage.text = cancelMessage

        txtOk.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            this.finish()
            show.dismiss()
        }

        show.setCanceledOnTouchOutside(false)
    }
}