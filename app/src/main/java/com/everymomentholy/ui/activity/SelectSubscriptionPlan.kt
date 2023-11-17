package com.everymomentholy.ui.activity

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.everymomentholy.R
import com.everymomentholy.api.APIInterface
import com.everymomentholy.api.APIService
import com.everymomentholy.api.request.CreateSubscrptionReqVo
import com.everymomentholy.api.request.GetSubscriptionStatusReqVo
import com.everymomentholy.api.request.GetUserSubscriptionPlansRequestVo
import com.everymomentholy.api.request.LoginRequestVo
import com.everymomentholy.api.response.*
import com.everymomentholy.interfaces.SubscriptionPlanListCLick
import com.everymomentholy.ui.adapter.SubscriptionListPlanAdapter
import com.everymomentholy.ui.fragments.GetLiturgiesFragment
import com.everymomentholy.ui.fragments.MyProfileFragment
import com.everymomentholy.utils.Constants
import com.everymomentholy.utils.Utils
import com.folioreader.emh.EMHUtils
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SelectSubscriptionPlan : AppCompatActivity(), SubscriptionPlanListCLick {

    var liturgies: GetLiturgiesDataVo = GetLiturgiesDataVo()

    var prefeUserId: Int = 0
    var android_id: String = ""
    lateinit var ivBack: ImageView
    lateinit var progressCardView: CardView
    var isPlanSelect = false
    lateinit var selectedPlanType: String

    lateinit var rcvSubscriptionPlan: RecyclerView
    lateinit var btnSubscribeNow: Button
    private var planListAdapter: RecyclerView.Adapter<SubscriptionListPlanAdapter.MyViewHolder>? =
        null

    lateinit var customer_ID: String
    lateinit var subscription_ID: String
    lateinit var publishable_Key: String
    lateinit var ephemeralKey_ID: String
    lateinit var payment_intent_ID: String
    lateinit var statusMessage: String

    lateinit var paymentSheet: PaymentSheet
    lateinit var customerConfig: PaymentSheet.CustomerConfiguration
    lateinit var paymentIntentClientSecret: String

    lateinit var txtSkip: TextView

    private lateinit var iv_toolbar_backImage: ImageView
    private lateinit var iv_toolbar_drawer: ImageView
    private lateinit var iv_toolbar_notification: ImageView
    private lateinit var txt_toolbar_name: TextView
    val subscriptionSatus: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_subscription_plan)

        iv_toolbar_drawer = findViewById(R.id.iv_toolbar_drawer)
        iv_toolbar_backImage = findViewById(R.id.iv_toolbar_backImage)
        txt_toolbar_name = findViewById(R.id.txt_toolbar_name)
        iv_toolbar_notification = findViewById(R.id.iv_toolbar_notification)

        iv_toolbar_backImage.visibility = View.GONE
        iv_toolbar_drawer.visibility = View.GONE
        iv_toolbar_notification.visibility = View.GONE
        txt_toolbar_name.text = "SUBSCRIPTION PLANS"


        paymentSheet = PaymentSheet(this, ::onPaymentSheetResult)

        android_id = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ANDROID_ID
        )

        // (activity as MainActivity).toolbar.visibility = View.GONE

        prefeUserId = Utils.readIntData(
            this,
            Constants.PrefUserID,
            0
        )!!

        //ivBack = findViewById(R.id.iv_back)

        txtSkip = findViewById(R.id.txtSkip)
        btnSubscribeNow = findViewById(R.id.btnSubscribeNow)
        rcvSubscriptionPlan = findViewById(R.id.rcvSubscriptionPlan)
        progressCardView = findViewById(R.id.progressCardView)

        btnSubscribeNow.isEnabled = true

        iv_toolbar_backImage.setOnClickListener {
            onBackPressed()
            // val intent = Intent(this, SelectOptionActivity::class.java)
            //  intent.putExtra("boolean", true)
//            intent.flags =
//                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//            startActivity(intent)
//            finish()
        }

        progressCardView.visibility = View.VISIBLE
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )

        /*ivBack.setOnClickListener() {
            (activity as MainActivity).toolbar.visibility = View.VISIBLE
           (activity as MainActivity).replaceFragment(GetLiturgiesFragment(), "Get Liturgies")
        }*/

        getUserSubscriptionPlans()

        val upcomingPlanstatus = Utils.readStringFromSharedPref(
            this, Constants.UPCOMINGPLAN,
            ""
        ).toString()

        Log.e("plan", "UpcomingPlan:" + upcomingPlanstatus)

        subscriptionPlanList()

        txtSkip.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            //  intent.putExtra("boolean", true)
            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        btnSubscribeNow.setOnClickListener {
            btnSubscribeNow.isEnabled = false
            if (Constants.USER_LOGIN_STATUS == Constants.SKIP_LOGIN) {
                //showLoginDialog()
                showDialogForUnlockWithoutLogin()
            } else {
                if (isPlanSelect) {
                    if (upcomingPlanstatus == "Yes") {
                        showSubscribDialog()
                    } else {
                        progressCardView.visibility = View.VISIBLE
                        getWindow().setFlags(
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                        )
                        var createSubscrptionReqVo: CreateSubscrptionReqVo =
                            CreateSubscrptionReqVo()
                        createSubscrptionReqVo.appUserId = prefeUserId.toString()
                        createSubscrptionReqVo.planType = selectedPlanType
                        createSubscription(createSubscrptionReqVo)
                    }

                } else {
                    Toast.makeText(this, "Please Select Plan", Toast.LENGTH_SHORT).show()
                }
            }
            /* if (isPlanSelect) {
                 progressCardView.visibility = View.VISIBLE
                 var createSubscrptionReqVo: CreateSubscrptionReqVo =
                     CreateSubscrptionReqVo()
                 createSubscrptionReqVo.appUserId = prefeUserId.toString()
                 createSubscrptionReqVo.planType = selectedPlanType
                 createSubscription(createSubscrptionReqVo)
                 //Toast.makeText(this, "plan Selected", Toast.LENGTH_SHORT).show()
             } else {
                 Toast.makeText(this, "Please Select Plan", Toast.LENGTH_SHORT).show()
             }*/

            //getSubscriptionStatus()


        }

    }

    private fun showSubscribDialog() {


        val alertDialog = AlertDialog.Builder(
            this
        )
        val inflater = layoutInflater
        val alertView: View = inflater.inflate(R.layout.subscription_status_dialog, null)
        alertDialog.setView(alertView)
        val show = alertDialog.show()
        val txtMessage = alertView.findViewById<View>(R.id.txtMessage) as TextView
        val txtOk = alertView.findViewById<View>(R.id.txtOk) as TextView

        txtMessage.text = "Currently, you have subscribed to the current plan and upcoming plan."

        txtOk.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            show.dismiss()
        }

        show.setCanceledOnTouchOutside(false)
    }

    fun showLoginDialog() {
        val alertDialog = AlertDialog.Builder(
            this
        )
        val inflater = (this as Activity).layoutInflater
        val alertView: View = inflater.inflate(R.layout.login_dialog, null)
        alertDialog.setView(alertView)
        val show = alertDialog.show()
        val alertCancel = alertView.findViewById<View>(R.id.txtLoginCancel) as TextView
        val alertOk = alertView.findViewById<View>(R.id.txtLoginOk) as TextView


        alertOk.setOnClickListener {
            show.dismiss()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        alertCancel.setOnClickListener {
            show.dismiss()
        }
        show.setCanceledOnTouchOutside(false)
    }

    fun showDialogForUnlockWithoutLogin() {
        val alertDialog = AlertDialog.Builder(
            this
        )
        val inflater = layoutInflater
        val alertView: View = inflater.inflate(R.layout.purchase_without_login_dialog, null)
        alertDialog.setView(alertView)
        val show = alertDialog.show()
        val alertButtonCancel = alertView.findViewById<View>(R.id.txtCancel) as TextView
        val alertButtonLoginRegister =
            alertView.findViewById<View>(R.id.txtPurchaseRegisterLogin) as TextView
        val alertButtonPurchase =
            alertView.findViewById<View>(R.id.txtPurchaseWithoutRegisterLogin) as TextView


        alertButtonLoginRegister.setOnClickListener {
            val intent = Intent(this, SelectOptionActivity::class.java)
            startActivity(intent)
        }

        alertButtonCancel.setOnClickListener {
            show.dismiss()
        }

        alertButtonPurchase.setOnClickListener() {
            show.dismiss()
            //startPurchaseFlow(myLiturgyDataVo)
        }
        show.setCanceledOnTouchOutside(false)
    }

    fun onPaymentSheetResult(paymentSheetResult: PaymentSheetResult) {
        when (paymentSheetResult) {
            is PaymentSheetResult.Canceled -> {
                print("Payment Failed")
                progressCardView.visibility = View.VISIBLE
                getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )
                getSubscriptionStatus("Cancelled")
            }
            is PaymentSheetResult.Failed -> {
                print("Error: ${paymentSheetResult.error}")
                progressCardView.visibility = View.VISIBLE
                getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )
                getSubscriptionStatus("Payment Canceled")
            }
            is PaymentSheetResult.Completed -> {
                // Display for example, an order confirmation screen
                print("Completed")
                progressCardView.visibility = View.VISIBLE
                getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )
                getSubscriptionStatus("Payment Success")
            }
        }
    }

    private fun getSubscriptionStatus(PaymentStatus: String) {

        var getSubscriptionStatusReqVo: GetSubscriptionStatusReqVo = GetSubscriptionStatusReqVo()
        getSubscriptionStatusReqVo.appUserId = prefeUserId.toString()
        getSubscriptionStatusReqVo.stripeCustomerId = customer_ID
        getSubscriptionStatusReqVo.subscriptionId = subscription_ID
        getSubscriptionStatusReqVo.subscriptionStatus = PaymentStatus

        val request = APIService.buildService(APIInterface::class.java)
        val call = request.getSubscriptionStatus(getSubscriptionStatusReqVo)

        try {
            call.enqueue(object : Callback<GetSubscriptionStatusResVo> {
                override fun onResponse(
                    call: Call<GetSubscriptionStatusResVo>,
                    response: Response<GetSubscriptionStatusResVo>
                ) {
                    if (response.body()?.statusCode == 1) {
                        var status = response.body()!!.status
                        var statusCode = response.body()!!.statusCode
                        statusMessage = response.body()!!.message

                        progressCardView.visibility = View.GONE
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

                        Utils.writeStringToSharedPref(
                            this@SelectSubscriptionPlan, Constants.USER_SUBSCRIPTIONSTATUS,
                            response.body()!!.subscription
                        )

                        if (response.body()!!.subscription == "Yes") {
                            showPaymentStatusDialog()
                        } else {
                            Toast.makeText(
                                this@SelectSubscriptionPlan,
                                "Payment Cancelled",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }

                    } else {

                    }

                }

                override fun onFailure(call: Call<GetSubscriptionStatusResVo>, t: Throwable) {
                    Toast.makeText(this@SelectSubscriptionPlan, "${t.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            })
        } catch (exception: Exception) {
            exception.printStackTrace()
        }

    }

    private fun showPaymentStatusDialog() {
        val alertDialog = AlertDialog.Builder(
            this
        )
        val inflater = layoutInflater
        val alertView: View = inflater.inflate(R.layout.payment_status_dialog, null)
        alertDialog.setView(alertView)
        val show = alertDialog.show()
        val alertButtonYes = alertView.findViewById<TextView>(R.id.txtOk) as TextView
        var paymentStatusMessage =
            alertView.findViewById<TextView>(R.id.txtPaymentStatusMsg) as TextView


        paymentStatusMessage.text = statusMessage

        alertButtonYes.setOnClickListener {
            alertButtonYes.isEnabled = false
            //  requireContext().onBackPressed()
            // (activity as MainActivity).toolbar.visibility = View.VISIBLE
            // (activity as MainActivity).replaceFragment(GetLiturgiesFragment(), "Get Liturgies")
            val intent =
                Intent(this, MainActivity::class.java)
            // intent.putExtra("boolean", true)
            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            show.dismiss()
        }

        show.setCanceledOnTouchOutside(false)
    }

    private fun subscriptionPlanList() {
        val request = APIService.buildService(APIInterface::class.java)
        val call = request.subscriptionPlanList()

        try {
            call.enqueue(object : Callback<SubscriptionPlanListResponseVo> {
                override fun onResponse(
                    call: Call<SubscriptionPlanListResponseVo>,
                    response: Response<SubscriptionPlanListResponseVo>
                ) {
                    if (response.body()?.statusCode == 1) {
                        progressCardView.visibility = View.GONE
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        setAdapter(this@SelectSubscriptionPlan, response.body()!!)

                    } else {

                    }

                }

                override fun onFailure(call: Call<SubscriptionPlanListResponseVo>, t: Throwable) {
                    Toast.makeText(this@SelectSubscriptionPlan, "${t.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            })
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    private fun getUserSubscriptionPlans() {
        var getUserSubscription: GetUserSubscriptionPlansRequestVo =
            GetUserSubscriptionPlansRequestVo()
        getUserSubscription.deviceId = android_id
        getUserSubscription.userId = prefeUserId

        val request = APIService.buildService(APIInterface::class.java)
        val call =
            request.getUserSubscriptionPlans(
                getUserSubscription.userId, getUserSubscription.deviceId,
                "bearer " + Utils.readStringFromSharedPref(
                    this,
                    Constants.SHARED_PREF_TOKEN,
                    ""
                )
            )

        try {
            call.enqueue(object : Callback<GetUserSubscriptionPlanResVo> {
                override fun onResponse(
                    call: Call<GetUserSubscriptionPlanResVo>,
                    response: Response<GetUserSubscriptionPlanResVo>
                ) {
                    if (response.body()?.statusCode == 1) {

                        var upcomingPlan =
                            response.body()!!.response.userSubscriptionData.upcomingPlan

                        Utils.writeStringToSharedPref(
                            this@SelectSubscriptionPlan, Constants.UPCOMINGPLAN,
                            response.body()!!.response.userSubscriptionData.upcomingPlan
                        )

                    } else {
                    }
                }

                override fun onFailure(call: Call<GetUserSubscriptionPlanResVo>, t: Throwable) {
                    Toast.makeText(this@SelectSubscriptionPlan, "${t.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            })
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    fun setAdapter(context: Context, response: SubscriptionPlanListResponseVo) {
        planListAdapter = SubscriptionListPlanAdapter(
            context,
            // response.body()!!.response.data,
            response.response,
            this
        )
        val layoutManager: RecyclerView.LayoutManager =
            LinearLayoutManager(context)
        rcvSubscriptionPlan.layoutManager = layoutManager
        // attach adapter to the recycler view
        rcvSubscriptionPlan.adapter = planListAdapter
    }

    private fun createSubscription(createSubscrptionReqVo: CreateSubscrptionReqVo) {


        val request = APIService.buildService(APIInterface::class.java)
        val call = request.createSubscription(createSubscrptionReqVo)

        try {
            call.enqueue(object : Callback<CreateSubscrptionResVo> {
                override fun onResponse(
                    call: Call<CreateSubscrptionResVo>,
                    response: Response<CreateSubscrptionResVo>
                ) {
                    if (response.body()?.statusCode == 1) {

                        customer_ID = response.body()!!.response.customer_id
                        subscription_ID = response.body()!!.response.subscription_id
                        publishable_Key = response.body()!!.response.publishable_key
                        ephemeralKey_ID = response.body()!!.response.ephemeralKey_id
                        payment_intent_ID = response.body()!!.response.payment_intent_id


                        Utils.writeStringToSharedPref(
                            this@SelectSubscriptionPlan, Constants.CUSTOMER_ID,
                            response.body()!!.response.customer_id
                        )

                        Utils.writeStringToSharedPref(
                            this@SelectSubscriptionPlan, Constants.SUBSCRIPTION_ID,
                            response.body()!!.response.subscription_id
                        )

                        Utils.writeStringToSharedPref(
                            this@SelectSubscriptionPlan, Constants.PUBLISHABLE_KEY,
                            response.body()!!.response.publishable_key
                        )

                        Utils.writeStringToSharedPref(
                            this@SelectSubscriptionPlan, Constants.EPHEMERALKEY_ID,
                            response.body()!!.response.ephemeralKey_id
                        )

                        Utils.writeStringToSharedPref(
                            this@SelectSubscriptionPlan, Constants.PAYMENT_INTENT_ID,
                            response.body()!!.response.payment_intent_id
                        )

                        paymentIntentClientSecret = response.body()!!.response.payment_intent_id
                        customerConfig = PaymentSheet.CustomerConfiguration(
                            response.body()!!.response.customer_id,
                            response.body()!!.response.ephemeralKey_id
                        )


                        val publishableKey = response.body()!!.response.publishable_key
                        PaymentConfiguration.init(this@SelectSubscriptionPlan, publishableKey)

                        progressCardView.visibility = View.GONE
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        presentPaymentSheet()

                    } else {
                        progressCardView.visibility = View.GONE
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
//                        val message = response.body()?;
                        Toast.makeText(
                            this@SelectSubscriptionPlan,
                            "",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                }

                override fun onFailure(call: Call<CreateSubscrptionResVo>, t: Throwable) {
                    // Toast.makeText(context, "${t.message}", Toast.LENGTH_SHORT).show()
                    progressCardView.visibility = View.GONE
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                }
            })
        } catch (exception: Exception) {
            exception.printStackTrace()
            progressCardView.visibility = View.GONE
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
    }

    private fun presentPaymentSheet() {
        val configuration: PaymentSheet.Configuration =
            PaymentSheet.Configuration.Builder("Example, Inc.")
                .customer(customerConfig) // Set `allowsDelayedPaymentMethods` to true if your business can handle payment methods
                // that complete payment after a delay, like SEPA Debit and Sofort.
                .allowsDelayedPaymentMethods(true)
                .build()
        paymentSheet.presentWithPaymentIntent(
            payment_intent_ID,
            configuration
        )
    }


    override fun onResume() {
        super.onResume()
        // Toast.makeText(context, "This is on resume", Toast.LENGTH_LONG).show()

    }

    override fun onSelectPlan(pos: Int, planType: String, isAuto: Boolean) {
        selectedPlanType = planType
        isPlanSelect = isAuto
    }

    /* override fun onBackPressed() {
         super.onBackPressed()
         onBackPressed()
     }*/


}