package com.everymomentholy.ui.activity

import android.Manifest
import android.accounts.Account
import android.accounts.AccountManager
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchaseHistoryRecord
import com.everymomentholy.EMHApplication
import com.everymomentholy.R
import com.everymomentholy.api.APIInterface
import com.everymomentholy.api.APIService
import com.everymomentholy.api.request.GetUserProfileRequestVo
import com.everymomentholy.api.request.UpdateSubscriptionStatusReq
import com.everymomentholy.api.response.*
import com.everymomentholy.interfaces.QueryPurchasesListner
import com.everymomentholy.interfaces.SubscriptionPlanListCLick
import com.everymomentholy.ui.adapter.SubscriptionListPlanAdapter
import com.everymomentholy.ui.fragments.HomeFragment
import com.everymomentholy.utils.Constants
import com.everymomentholy.utils.InAppSubscription
import com.everymomentholy.utils.Utils
import com.google.android.gms.auth.GoogleAuthUtil
import kotlinx.coroutines.GlobalScope
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SubscriptionPlanListActivity : AppCompatActivity(), SubscriptionPlanListCLick,
    QueryPurchasesListner {

    //link for In App subscription
    // https://reintech.io/blog/implementing-in-app-purchases-and-subscriptions-in-android-apps
    //https://codelabs.developers.google.com/play-billing-codelab#0
    //https://dev.to/theplebdev/adding-subscriptions-to-your-android-app-part-3-checking-if-user-is-subscribed-3793

    var liturgies: GetLiturgiesDataVo = GetLiturgiesDataVo()

    private lateinit var billingClientLifecycle: InAppSubscription
    private var planListAdapter: RecyclerView.Adapter<SubscriptionListPlanAdapter.MyViewHolder>? =
        null


    var prefeUserId: Int = 0
    var android_id: String = ""
    var subscriptionStatusfromProfile: String = ""
    lateinit var token: String
    lateinit var ivBack: ImageView
    lateinit var progressCardView: CardView
    var isPlanSelect = false
    lateinit var selectedPlanType: String
    var planType: String? = "monthly"
    lateinit var activeEMail: String
    private val CONTACTS_PERMISSION_REQUEST_CODE = 101
    var email: String = ""
    var subscriptionType: String = ""

    lateinit var btnSubscribeNow: Button
    lateinit var linearMonthly: LinearLayout
    lateinit var linearYearly: LinearLayout
    lateinit var txtSkip: TextView
    private lateinit var iv_toolbar_backImage: ImageView
    private lateinit var iv_toolbar_drawer: ImageView
    private lateinit var iv_toolbar_notification: ImageView
    private lateinit var txt_toolbar_name: TextView
    private lateinit var txtYCurrency: TextView
    private lateinit var txtCurrency: TextView
    private lateinit var txtYPrice: TextView
    private lateinit var txtMontlyPrice: TextView
    private lateinit var txtYType: TextView
    private lateinit var txtYMessag: TextView
    private lateinit var txtYsave: TextView
   // private lateinit var txtSaving: TextView
    private lateinit var txtYearly: TextView
    private lateinit var txtMonthly: TextView
    private lateinit var txtYUnlimitedAccess: TextView
    private lateinit var txtUnlimitedAccess: TextView
    private lateinit var txtSubscriptionType: TextView
    private lateinit var txtInAppInstructions: TextView
    lateinit var linearStatic: LinearLayout
    lateinit var relativeLayout: RelativeLayout
    lateinit var planTypeIsStripe: String
    lateinit var monthlyPlanPrice: String
    lateinit var yearlyPlanPrice: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_subscription_plan)

        // Billing APIs are all handled in the this lifecycle observer.
        billingClientLifecycle = (application as EMHApplication).billingClientLifecycle
        lifecycle.addObserver(billingClientLifecycle)

        iv_toolbar_drawer = findViewById(R.id.iv_toolbar_drawer)
        iv_toolbar_backImage = findViewById(R.id.iv_toolbar_backImage)
        txt_toolbar_name = findViewById(R.id.txt_toolbar_name)
        iv_toolbar_notification = findViewById(R.id.iv_toolbar_notification)
        linearMonthly = findViewById(R.id.linearMonthly)
        linearYearly = findViewById(R.id.linearYearly)
        txtYCurrency = findViewById(R.id.txtYCurrency)
        txtCurrency = findViewById(R.id.txtCurrency)
        txtYPrice = findViewById(R.id.txtYPrice)
        txtMontlyPrice = findViewById(R.id.txtMontlyPrice)
        txtYearly = findViewById(R.id.txtYearly)
        txtMonthly = findViewById(R.id.txtMonthly)
        txtYUnlimitedAccess = findViewById(R.id.txtYUnlimitedAccess)
        txtUnlimitedAccess = findViewById(R.id.txtUnlimitedAccess)
        txtYsave = findViewById(R.id.txtYsave)
       // txtSaving = findViewById(R.id.txtSaving)
        linearStatic = findViewById(R.id.linearStatic)
        relativeLayout = findViewById(R.id.relativeLayout)
        txtSkip = findViewById(R.id.txtSkip)
        btnSubscribeNow = findViewById(R.id.btnSubscribeNow)
        progressCardView = findViewById(R.id.progressCardView)
        txtSubscriptionType = findViewById(R.id.txtSubscriptionType)
        txtInAppInstructions = findViewById(R.id.txtInAppInstructions)

        iv_toolbar_backImage.visibility = View.GONE
        iv_toolbar_drawer.visibility = View.GONE
        iv_toolbar_notification.visibility = View.GONE
        txt_toolbar_name.text = "SUBSCRIPTION PLANS"

        activeEMail = Utils.readStringFromSharedPref(
            this, Constants.ACTIVE_PLAYSTORE_EMAIL,
            ""
        ).toString()

        token = Utils.readStringFromSharedPref(
            this,
            Constants.SHARED_PREF_TOKEN,
            ""
        ).toString()

        planTypeIsStripe = Utils.readStringFromSharedPref(
            this, Constants.PLAN_TYPE,
            ""
        ).toString()

        android_id = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ANDROID_ID
        )
        subscriptionStatusfromProfile = Utils.readStringFromSharedPref(
            this, Constants.PROFILE_STATUS,
            ""
        ).toString()

        prefeUserId = Utils.readIntData(
            this,
            Constants.PrefUserID,
            0
        )!!

        //ivBack = findViewById(R.id.iv_back)


        if (subscriptionStatusfromProfile.equals("Yes", true)) {
            if (subscriptionType == "emh_monthly_plan") {
                subscriptionType = "Monthly"
                txtSubscriptionType.text =
                    "You are subscribed for a" + " " + subscriptionType + " " + "plan"
            } else {
                txtSubscriptionType.text =
                    "You are subscribed for a" + " " + subscriptionType + " " + "plan"
            }
        } else {
            txtSubscriptionType.text = "You are not subscribed"
        }

        iv_toolbar_backImage.setOnClickListener {
            onBackPressed()
        }

        var subscriptionStatus = Utils.readStringFromSharedPref(
            this, Constants.IN_APP_SUBSCRIPTION_STATUS,
            ""
        ).toString()
        Log.e("ASDF", "INAPPSUB: " + subscriptionStatus)

        val upcomingPlanstatus = Utils.readStringFromSharedPref(
            this, Constants.UPCOMINGPLAN,
            ""
        ).toString()

        Log.e("plan", "UpcomingPlan:" + upcomingPlanstatus)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted
            // Request the permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_CONTACTS),
                CONTACTS_PERMISSION_REQUEST_CODE
            )
        } else {
            // Permission has already been granted
            // You can now access contacts
            // AccessContacts()
            val account = getActiveGoogleAccount(this)
            if (account != null) {
                email = account.name
                // Do something with the email

                val activeEmail = Utils.writeStringToSharedPref(
                    this, Constants.ACTIVE_PLAYSTORE_EMAIL,
                    email
                ).toString()
                Log.d("ActiveAccount", "Active Google play account: $activeEmail")
            } else {
                Log.d("ActiveAccount", "No active Google account found")
            }
        }

        val utilityCallback =
            InAppSubscription.getInstance(application, GlobalScope)
        utilityCallback.setCallbackListener(this)

        txtSkip.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            //  intent.putExtra("boolean", true)
            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        progressCardView.visibility = View.VISIBLE
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
        relativeLayout.visibility = View.GONE

        subscriptionPlanList()


        /*linearMonthly.setOnClickListener {
            linearMonthly.setBackgroundColor(ContextCompat.getColor(this, R.color.loginbg));
            linearYearly.setBackgroundResource(R.drawable.subcripiton_bg);

            txtCurrency.setTextColor(ContextCompat.getColor(this, R.color.white));
            txtMontlyPrice.setTextColor(ContextCompat.getColor(this, R.color.white));
            txtMonthly.setTextColor(ContextCompat.getColor(this, R.color.white));
            txtUnlimitedAccess.setTextColor(ContextCompat.getColor(this, R.color.white));

            txtYCurrency.setTextColor(ContextCompat.getColor(this, R.color.loginbg));
            txtYPrice.setTextColor(ContextCompat.getColor(this, R.color.loginbg));
            txtYearly.setTextColor(ContextCompat.getColor(this, R.color.loginbg));
            txtYUnlimitedAccess.setTextColor(ContextCompat.getColor(this, R.color.loginbg));
            txtYsave.setTextColor(ContextCompat.getColor(this, R.color.loginbg));
            txtSaving.setTextColor(ContextCompat.getColor(this, R.color.loginbg));

            planType = "monthly"
        }*/

        linearMonthly.setOnClickListener {
            linearMonthly.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.loginbg
                )
            );
            linearYearly.setBackgroundResource(R.drawable.subcripiton_bg);

            txtCurrency.setTextColor(ContextCompat.getColor(this, R.color.white));
            txtMontlyPrice.setTextColor(ContextCompat.getColor(this, R.color.white));
            txtMonthly.setTextColor(ContextCompat.getColor(this, R.color.white));

            txtUnlimitedAccess.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.white
                )
            );

            txtYCurrency.setTextColor(ContextCompat.getColor(this, R.color.loginbg));
            txtYPrice.setTextColor(ContextCompat.getColor(this, R.color.loginbg));
            txtYearly.setTextColor(ContextCompat.getColor(this, R.color.loginbg));
            txtYsave.setTextColor(ContextCompat.getColor(this, R.color.loginbg));
            txtYUnlimitedAccess.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.loginbg
                )
            );


            planType = "monthly"
        }

        linearYearly.setOnClickListener {
            linearYearly.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.loginbg
                )
            );
            linearMonthly.setBackgroundResource(R.drawable.subcripiton_bg);
            txtYCurrency.setTextColor(ContextCompat.getColor(this, R.color.white));
            txtYPrice.setTextColor(ContextCompat.getColor(this, R.color.white));
            txtYearly.setTextColor(ContextCompat.getColor(this, R.color.white));
            txtYsave.setTextColor(ContextCompat.getColor(this, R.color.white));
            txtYUnlimitedAccess.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.white
                )
            );


            txtCurrency.setTextColor(ContextCompat.getColor(this, R.color.loginbg));
            txtMontlyPrice.setTextColor(ContextCompat.getColor(this, R.color.loginbg));
            txtMonthly.setTextColor(ContextCompat.getColor(this, R.color.loginbg));
            txtUnlimitedAccess.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.loginbg
                )
            );
            planType = "yearly"
        }

        /*linearYearly.setOnClickListener {
            linearYearly.setBackgroundColor(ContextCompat.getColor(this, R.color.loginbg));
            linearMonthly.setBackgroundResource(R.drawable.subcripiton_bg);
            txtYCurrency.setTextColor(ContextCompat.getColor(this, R.color.white));
            txtYPrice.setTextColor(ContextCompat.getColor(this, R.color.white));
            txtYearly.setTextColor(ContextCompat.getColor(this, R.color.white));
            txtYUnlimitedAccess.setTextColor(ContextCompat.getColor(this, R.color.white));
            txtYsave.setTextColor(ContextCompat.getColor(this, R.color.white));
            txtSaving.setTextColor(ContextCompat.getColor(this, R.color.white));

            txtCurrency.setTextColor(ContextCompat.getColor(this, R.color.loginbg));
            txtMontlyPrice.setTextColor(ContextCompat.getColor(this, R.color.loginbg));
            txtMonthly.setTextColor(ContextCompat.getColor(this, R.color.loginbg));
            txtUnlimitedAccess.setTextColor(ContextCompat.getColor(this, R.color.loginbg));
            planType = "yearly"
        }*/

        /*btnSubscribeNow.setOnClickListener {
            if (Constants.USER_LOGIN_STATUS == Constants.SKIP_LOGIN) {
                showDialogForUnlockWithoutLogin()
            } else {
                Log.e("subscriptionStatus", "subscriptionStatus: " + subscriptionStatus)
                if (subscriptionStatus.equals("Yes", true)) {
                    showLogoutDialog()
                } else {
                    if (planType != null) {
                        Log.e("PLAN", "onCreate: " + planType)
                        billingClientLifecycle.showProducts(this, planType!!)

                    } else {
                        Toast.makeText(this, "Please select plan", Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
        }*/

        /*btnSubscribeNow.setOnClickListener {
            if (Constants.USER_LOGIN_STATUS == Constants.SKIP_LOGIN) {
                showDialogForUnlockWithoutLogin()
            } else {
                if (subscriptionStatusfromProfile.equals("Yes", true)) {
                    alreadySubscribeDialog()
                } else {
                    Log.e("PLAN", "onCreate: " + planType)
                    billingClientLifecycle.showProducts(this, planType!!)
                    *//* if (planType != null) {
                         Log.e("PLAN", "onCreate: " + planType)
                         billingClientLifecycle.showProducts(this, planType!!)

                     } else {
                         Toast.makeText(this, "Please select plan", Toast.LENGTH_LONG)
                             .show()
                     }*//*
                }
            }
        }*/

        btnSubscribeNow.setOnClickListener {
            if (btnSubscribeNow.text.toString().equals("Unsubscribe", true)) {
                //ghjkukyhrfbhm
                if (planTypeIsStripe.equals("strip", true)) {
                    alreadySubscribeDialog()
                    // stripeDialog()
                } else {
                    alreadySubscribeDialog()
                }

            } else {
                if (Constants.USER_LOGIN_STATUS == Constants.SKIP_LOGIN) {
                    showDialogForUnlockWithoutLogin()
                } else {
                    Log.e("PLANTYPE", "onCreate: " + planType)
                    billingClientLifecycle.showProducts(this, planType!!)
                    /*if (subscriptionStatusfromProfile.equals("Yes", true)) {
                        alreadySubscribeDialog()
                    } else {
                        Log.e("PLANTYPE", "onCreate: " + planType)
                        billingClientLifecycle.showProducts(requireActivity(), planType!!)
                    }*/
                }
            }

        }

        txtInAppInstructions.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Free Trial Information")
                .setMessage(R.string.free_trial_info) // Display the string from resources
                .setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss() // Dismiss the dialog when "OK" is pressed
                }
                .setCancelable(false) // Optionally prevent dismissing by tapping outside
                .show() // Show the dialog
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            CONTACTS_PERMISSION_REQUEST_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission granted
                    // You can now access contacts
                    // AccessContacts()
                    val account = getActiveGoogleAccount(this)
                    if (account != null) {
                        email = account.name
                        // Do something with the email
                        Log.d("ActiveAccount", "Active Google account: $email")
                    } else {
                        Log.d("ActiveAccount", "No active Google account found")
                    }
                } else {
                    Toast.makeText(
                        this,
                        "The app was not allowed to read your contact",
                        Toast.LENGTH_LONG
                    ).show();
                    // Permission denied
                    // You may want to handle this case gracefully
                    // You can display a message to the user indicating why you need the permission and how to enable it
                }
                return
            }
            // Handle other permissions if needed
        }
    }

    private fun getActiveGoogleAccount(context: Context): Account? {
        val accounts =
            AccountManager.get(context).getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE)
        return if (accounts.isNotEmpty()) {
            accounts[0] // Return the first Google account
        } else {
            null
        }
    }

    private fun alreadySubscribeDialog() {
        val alertDialog = AlertDialog.Builder(
            this
        )
        val inflater = layoutInflater
        val alertView: View = inflater.inflate(R.layout.logout_dialog, null)
        alertDialog.setView(alertView)
        val show = alertDialog.show()
        val alertButtonCancel = alertView.findViewById<View>(R.id.txtLougotCancel) as TextView
        val alertButtonYes = alertView.findViewById<View>(R.id.txtLogoutYes) as TextView
        val txtMessage = alertView.findViewById<View>(R.id.txtMessage) as TextView

        txtMessage.text =
            "You have been already subscribed to another plan in the application. Please unsubscribe from that plan and then you can take new plan."

        alertButtonCancel.text = "Cancel existing plan"
        alertButtonYes.text = "Ok"
        alertButtonCancel.setTextSize(15f);
        alertButtonYes.setTextSize(15f);

        alertButtonYes.setOnClickListener {
            show.dismiss()
            /* val bundle = Bundle()
             var fragment: Fragment = HomeFragment()
             replaceFragment(fragment, "Every Moment Holy", bundle)*/
            Intent(this, MainActivity::class.java)
            //intent.putExtra("boolean", true)
            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()

        }

        alertButtonCancel.setOnClickListener {
            show.dismiss()
            val packageName = "com.everymomentholy.ui.fragments"

            val subscriptionIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/account/subscriptions?package=$packageName")
            )
            if (subscriptionIntent.resolveActivity(
                    packageManager
                ) != null
            ) {
                startActivity(subscriptionIntent)
            } else {
                // Handle the case where the Google Play Store is not installed on the device
                // or there's no activity to handle the intent.
            }
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

//                        billingClientLifecycle.getMonthlyProductDetails(this@SubscriptionPlanListActivity)

                        progressCardView.visibility = View.GONE
                        getWindow()
                            .clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        relativeLayout.visibility = View.VISIBLE
                        //setAdapter(this@SubscriptionPlanListActivity, response.body()!!)

                        for(i in 0..response.body()!!.response.size - 1){
                            var temp = SubScriptionPlanSubResponseVo()
                            temp.plan_type = response.body()!!.response[i].plan_type

                            txtMontlyPrice.text = response.body()!!.response[0].unit_amount
                            txtMonthly.text = response.body()!!.response[0].plan_type
                            val monthlyrenewalMessage = response.body()!!.response[0].plan_message + "\n" +
                                    "Billed Every Month"
//                            txtUnlimitedAccess.text = response.body()!!.response[0].plan_message
                            txtUnlimitedAccess.text = monthlyrenewalMessage

                            txtYPrice.text = response.body()!!.response[1].unit_amount
                            txtYearly.text = response.body()!!.response[1].plan_type
                            val yearlyrenewalMessage = response.body()!!.response[1].plan_message + "\n" +
                                    "Billed Every Year"
//                            txtYUnlimitedAccess.text = response.body()!!.response[1].plan_message
                            txtYUnlimitedAccess.text = yearlyrenewalMessage

                            txtYsave.text = "(Save $" + "" + response.body()!!.response[1].plan_savings + "/year)"

                        }
                        billingClientLifecycle.getMonthlyProductDetails()
                        billingClientLifecycle.getYearlyProductDetails()
                        monthlyPlanPrice = Utils.readStringFromSharedPref(this@SubscriptionPlanListActivity,Constants.MONTHLY_SUB_PRICE,"$2.99")
                            .toString()
                        Log.e("monthlyPlanPrice","monthlyPlanPrice-->"+monthlyPlanPrice)
                        txtMontlyPrice.setText(monthlyPlanPrice)

                        yearlyPlanPrice = Utils.readStringFromSharedPref(this@SubscriptionPlanListActivity,Constants.YEARLY_SUB_PRICE,"$29.99")
                            .toString()
                        Log.e("yearlyPlanPrice","yearlyPlanPrice-->"+yearlyPlanPrice)
                        txtYPrice.setText(yearlyPlanPrice)

                    } else {
                        progressCardView.visibility = View.GONE
                        getWindow()
                            .clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        relativeLayout.visibility = View.VISIBLE
                        linearStatic.visibility = View.VISIBLE
                    }
                }

                override fun onFailure(call: Call<SubscriptionPlanListResponseVo>, t: Throwable) {
                    Toast.makeText(
                        this@SubscriptionPlanListActivity,
                        "${t.message}",
                        Toast.LENGTH_SHORT
                    )
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
        // rcvSubscriptionPlan.layoutManager = layoutManager
        // attach adapter to the recycler view
        // rcvSubscriptionPlan.adapter = planListAdapter
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


    override fun onResume() {
        super.onResume()
        // Toast.makeText(context, "This is on resume", Toast.LENGTH_LONG).show()

    }

    override fun onSelectPlan(pos: Int, planType: String, isAuto: Boolean) {
        selectedPlanType = planType
        isPlanSelect = isAuto
    }

    /*override fun onQueryPurchase(purchasesList: MutableList<Purchase>) {
        Log.e("Purchase", "onQueryPurchase: In Fragment" + purchasesList.size)

        if (purchasesList != null && purchasesList.size > 0) {
//            Toast.makeText(activity, "false", Toast.LENGTH_SHORT).show()
            //btnSubscribeNow.isEnabled = false
            // Log.e("Purchase", "onQueryPurchase: " + purchasesList.size + " In If")

            try {
                for (purchase in purchasesList) {

                    Log.d("PURCHASE_DATA", "orderId: " + purchase.orderId)
                    var jsonobject: JSONObject = JSONObject(purchase.originalJson)
                    val startdate = Utils.convertLongToDate(purchase.purchaseTime)
                    val monthlyPlanEndDate =
                        Utils.convertLongToDatePlusOneMonth(purchase.purchaseTime)
                    val yearlyPlanEndDate =
                        Utils.convertLongToDatePlusOneYear(purchase.purchaseTime)

                    var updateSubscriptionStatusReq: UpdateSubscriptionStatusReq =
                        UpdateSubscriptionStatusReq()
                    updateSubscriptionStatusReq.payment_email = activeEMail
                    updateSubscriptionStatusReq.subscription_id = jsonobject.optString("orderId")
                    updateSubscriptionStatusReq.subscription_type = "google_play"
                    updateSubscriptionStatusReq.package_name = jsonobject.optString("productId")
                    updateSubscriptionStatusReq.token = jsonobject.optString("purchaseToken")
                    updateSubscriptionStatusReq.start_date = startdate

                    updateSubscriptionStatusReq.app_user_id = prefeUserId
                    updateSubscriptionStatusReq.subscription_status = "Yes"

                    if (jsonobject.optString("productId") == "emh_monthly_plan") {
                        updateSubscriptionStatusReq.end_date = monthlyPlanEndDate
                    } else {
                        updateSubscriptionStatusReq.end_date = yearlyPlanEndDate
                    }

                    if (Utils.isNetworkAvailable(this)) {
                        updateSubscriptionStatus(updateSubscriptionStatusReq)
                    } else {
                        Toast.makeText(
                            this,
                            resources.getString(R.string.check_internet),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } catch (e: Exception) {
                Log.d("LIST", "onQueryPurchase: " + e.message)
            }


        } else {
            btnSubscribeNow.isEnabled = true
        }
    }*/

    override fun onQueryPurchase(
        purchasesList: MutableList<Purchase>
    ) {

        if (purchasesList != null && purchasesList.size > 0) {
//            Toast.makeText(activity, "false", Toast.LENGTH_SHORT).show()
            // btnSubscribeNow.isEnabled = false
            getUserProfile()
            subscriptionPurchased()
            // Log.e("Purchase", "onQueryPurchase: " + purchasesList.size + " In If")

            try {
                for (purchase in purchasesList) {

                    Log.d("PURCHASE_DATA", "orderId: " + purchase.orderId)
                    var jsonobject: JSONObject = JSONObject(purchase.originalJson)
                    val startdate = Utils.convertLongToDate(purchase.purchaseTime)
                    val monthlyPlanEndDate =
                        Utils.convertLongToDatePlusOneMonth(purchase.purchaseTime)
                    val yearlyPlanEndDate =
                        Utils.convertLongToDatePlusOneYear(purchase.purchaseTime)

                    var updateSubscriptionStatusReq: UpdateSubscriptionStatusReq =
                        UpdateSubscriptionStatusReq()
                    updateSubscriptionStatusReq.payment_email = email
                    updateSubscriptionStatusReq.subscription_id = jsonobject.optString("orderId")
                    updateSubscriptionStatusReq.subscription_type = "google_play"
                    updateSubscriptionStatusReq.package_name = jsonobject.optString("productId")
                    updateSubscriptionStatusReq.token = jsonobject.optString("purchaseToken")
                    updateSubscriptionStatusReq.start_date = startdate

                    updateSubscriptionStatusReq.app_user_id = prefeUserId
                    updateSubscriptionStatusReq.subscription_status = "Yes"

                    if (jsonobject.optString("productId") == "emh_monthly_plan") {
                        updateSubscriptionStatusReq.end_date = monthlyPlanEndDate
                    } else {
                        updateSubscriptionStatusReq.end_date = yearlyPlanEndDate
                    }

                    if (Utils.isNetworkAvailable(this)) {
                        updateSubscriptionStatus(updateSubscriptionStatusReq)
                    } else {
                        Toast.makeText(
                            this,
                            resources.getString(R.string.check_internet),
                            Toast.LENGTH_LONG
                        ).show()
                    }


                }
            } catch (e: Exception) {
                Log.d("LIST", "onQueryPurchase: " + e.message)
            }


        } else {
//            Toast.makeText(activity, "true", Toast.LENGTH_SHORT).show()
            btnSubscribeNow.isEnabled = true
            Log.e("Purchase", "onQueryPurchase: " + purchasesList.size + " In else")
        }
        Log.e("Purchase", "onQueryPurchase: " + purchasesList.size + "Outside If")
    }

    private fun subscriptionPurchased() {
        val alertDialog = AlertDialog.Builder(
            this
        )
        val inflater = layoutInflater
        val alertView: View = inflater.inflate(R.layout.logout_dialog, null)
        alertDialog.setView(alertView)
        val show = alertDialog.show()
        val alertButtonCancel = alertView.findViewById<View>(R.id.txtLougotCancel) as TextView
        val alertButtonYes = alertView.findViewById<View>(R.id.txtLogoutYes) as TextView
        val txtMessage = alertView.findViewById<View>(R.id.txtMessage) as TextView

        alertButtonCancel.visibility = View.GONE

        txtMessage.text =
            "Subscription purchased."

        alertButtonCancel.text = "Cancel existing plan"
        alertButtonYes.text = "Ok"
        alertButtonCancel.setTextSize(15f)
        alertButtonYes.setTextSize(15f)

        alertButtonYes.setOnClickListener {

            /* val intent = Intent(requireActivity(), MainActivity::class.java)
             intent.flags =
                 Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
             startActivity(intent)
             requireActivity().finish()*/
            // getUserProfile()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            show.dismiss()

        }
        show.setCanceledOnTouchOutside(false)
    }

    override fun onQuryPurchaseHistoryasync(purchaseHistoryRecords: MutableList<PurchaseHistoryRecord>) {
        if (purchaseHistoryRecords != null && purchaseHistoryRecords.size > 0) {
            for (purchaseHistory in purchaseHistoryRecords) {
                var jsonobject: JSONObject = JSONObject(purchaseHistory.originalJson)

                val startdate = Utils.convertLongToDate(purchaseHistory.purchaseTime)
                val monthlyPlanEndDate =
                    Utils.convertLongToDatePlusOneMonth(purchaseHistory.purchaseTime)
                val yearlyPlanEndDate =
                    Utils.convertLongToDatePlusOneYear(purchaseHistory.purchaseTime)

                var updateSubscriptionStatusReq: UpdateSubscriptionStatusReq =
                    UpdateSubscriptionStatusReq()
                updateSubscriptionStatusReq.payment_email = email
                updateSubscriptionStatusReq.subscription_id = ""
                updateSubscriptionStatusReq.subscription_type = "google_play"
                updateSubscriptionStatusReq.package_name = jsonobject.optString("productId")
                updateSubscriptionStatusReq.token = jsonobject.optString("purchaseToken")
                updateSubscriptionStatusReq.start_date = startdate

                updateSubscriptionStatusReq.app_user_id = prefeUserId
                updateSubscriptionStatusReq.subscription_status = "No"

                if (jsonobject.optString("productId") == "emh_monthly_plan") {
                    updateSubscriptionStatusReq.end_date = monthlyPlanEndDate
                } else {
                    updateSubscriptionStatusReq.end_date = yearlyPlanEndDate
                }

                if (Utils.isNetworkAvailable(this)) {
                    updateSubscriptionStatus(updateSubscriptionStatusReq)
                } else {
                    Toast.makeText(
                        this,
                        resources.getString(R.string.check_internet),
                        Toast.LENGTH_LONG
                    ).show()
                }

            }
        } else {

        }

    }

    private fun getUserProfile() {
        var getUserProfileRequestVo: GetUserProfileRequestVo = GetUserProfileRequestVo()
        getUserProfileRequestVo.deviceId = android_id
        getUserProfileRequestVo.userId = prefeUserId

        Log.e(
            "token", Utils.readStringFromSharedPref(
                this,
                Constants.SHARED_PREF_TOKEN,
                ""
            ).toString()
        )

        val request = APIService.buildService(APIInterface::class.java)
        val call =
            request.getUserProfile(
                getUserProfileRequestVo.userId, getUserProfileRequestVo.deviceId,
                "bearer " + Utils.readStringFromSharedPref(
                    this,
                    Constants.SHARED_PREF_TOKEN,
                    ""
                )
            )

        try {
            call.enqueue(object : Callback<GetUserProfileVo> {
                override fun onResponse(
                    call: Call<GetUserProfileVo>,
                    response: Response<GetUserProfileVo>
                ) {
                    if (response.body()?.statusCode == 1) {
                        // progressbarHomeFragment.visibility = View.GONE
                        //requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        //rootLayout.visibility = View.VISIBLE
                        Utils.writeStringToSharedPref(
                            this@SubscriptionPlanListActivity, Constants.PROFILE_STATUS,
                            response.body()!!.response.userSubscriptionData.subscription
                        )

                        /*  val subscriptionStatus = Utils.readStringFromSharedPref(
                               requireActivity(), Constants.PROFILE_STATUS,
                               ""
                           ).toString()*/

                        //  userStatus = response.body()!!.response.userSubscriptionData.subscription
                    }
                }

                override fun onFailure(call: Call<GetUserProfileVo>, t: Throwable) {

                }
            })
        } catch (exception: Exception) {
            exception.printStackTrace()
        }

    }

    private fun updateSubscriptionStatus(updateSubscriptionStatusReq: UpdateSubscriptionStatusReq) {
        val request = APIService.buildService(APIInterface::class.java)
        val call =
            request.updateSubscriptionStatus(
                updateSubscriptionStatusReq,
                "bearer " + Utils.readStringFromSharedPref(
                    this,
                    Constants.SHARED_PREF_TOKEN,
                    ""
                )
            )

        try {
            call.enqueue(object : Callback<UpdateSubscriptionStatusRes> {
                override fun onResponse(
                    call: Call<UpdateSubscriptionStatusRes>,
                    response: Response<UpdateSubscriptionStatusRes>
                ) {
                    if (response.body()?.statusCode == 1) {
                        Utils.writeStringToSharedPref(
                            this@SubscriptionPlanListActivity, Constants.IN_APP_SUBSCRIPTION_STATUS,
                            response.body()!!.subscription_status
                        )

                        val message = response.body()!!.subscription_status
                        val message1 = "Store value from subscription fragment"

                        Utils.writeStringToSharedPref(
                            this@SubscriptionPlanListActivity, "RANDOM_KEYWORD",
                            message
                        )

                        Utils.writeStringToSharedPref(
                            this@SubscriptionPlanListActivity, "RANDOM_KEYWORD",
                            message1
                        )

                    } else {
                        if (Constants.USER_LOGIN_STATUS == Constants.SKIP_LOGIN) {

                        } else {
                            Toast.makeText(
                                this@SubscriptionPlanListActivity,
                                response.body()!!.message,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }

                override fun onFailure(call: Call<UpdateSubscriptionStatusRes>, t: Throwable) {
                    Toast.makeText(
                        this@SubscriptionPlanListActivity,
                        "${t.message}",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            })
        } catch (exception: Exception) {
            exception.printStackTrace()
        }

    }

    override fun onQueryMonthlySubs(monthlyPrice: String) {
        Utils.writeStringToSharedPref(this@SubscriptionPlanListActivity,Constants.MONTHLY_SUB_PRICE,monthlyPrice)
        Log.e("monthlyPlanPrice","monthlyPlanPrice-->"+monthlyPrice)
        txtMontlyPrice.setText(monthlyPrice)
        txtCurrency.visibility = View.INVISIBLE
    }

    override fun onQueryYearlySubs(yearlyPrice: String) {
        Utils.writeStringToSharedPref(this@SubscriptionPlanListActivity,Constants.YEARLY_SUB_PRICE,yearlyPrice)
        Log.e("yearlyPlanPrice","yearlyPlanPrice-->"+yearlyPrice)
        txtYPrice.setText(yearlyPrice)
        txtYCurrency.visibility = View.INVISIBLE
        if(!yearlyPrice.contains("$"))
        {
            txtYsave.visibility = View.GONE
        }
        else
        {
            txtYsave.visibility = View.VISIBLE
        }
    }
}