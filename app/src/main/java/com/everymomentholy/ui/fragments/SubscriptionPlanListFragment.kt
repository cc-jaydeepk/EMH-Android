package com.everymomentholy.ui.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.everymomentholy.R
import com.everymomentholy.api.APIInterface
import com.everymomentholy.api.APIService
import com.everymomentholy.api.request.*
import com.everymomentholy.api.response.*
import com.everymomentholy.interfaces.SubscriptionPlanListCLick
import com.everymomentholy.ui.activity.MainActivity
import com.everymomentholy.ui.activity.SelectOptionActivity
import com.everymomentholy.ui.adapter.*
import com.everymomentholy.utils.Constants
import com.everymomentholy.utils.Utils
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SubscriptionPlanListFragment : Fragment(), SubscriptionPlanListCLick {

    lateinit var txtSkip: TextView

    var liturgies: GetLiturgiesDataVo = GetLiturgiesDataVo()

    var prefeUserId: Int = 0
    var android_id: String = ""
    lateinit var ivBack: ImageView
    lateinit var txt_toolbar_name: TextView
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
    lateinit var relativeLayout: RelativeLayout

    var isFrom: Boolean = false
    var isFromHome: Boolean = false

    var message: String = ""
    lateinit var subscriptionSatus: String
    lateinit var upcomingPlanstatus: String

    @SuppressLint("HardwareIds")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        // val view = inflater.inflate(R.layout.activity_about_book_liturgies, container, false)
        val view = inflater.inflate(R.layout.subscriptionplan_frag, container, false)

        txtSkip = view.findViewById(R.id.txtSkip)
        ivBack = view.findViewById(R.id.iv_back)
        txt_toolbar_name = view.findViewById(R.id.txt_toolbar_name)
        btnSubscribeNow = view.findViewById(R.id.btnSubscribeNow)
        rcvSubscriptionPlan = view.findViewById(R.id.rcvSubscriptionPlan)
        progressCardView = view.findViewById(R.id.progressCardView)
        relativeLayout = view.findViewById(R.id.relativeLayout)

        // (activity as MainActivity).toolbar.visibility = View.VISIBLE
        btnSubscribeNow.isClickable = true

        isFrom = requireArguments().getBoolean("onPress")
        isFromHome = requireArguments().getBoolean("onPressHome")

        android_id = Settings.Secure.getString(
            requireContext().contentResolver,
            Settings.Secure.ANDROID_ID
        )

        if (isFrom == true) {
            txtSkip.visibility = View.VISIBLE
            ivBack.visibility = View.VISIBLE
            txt_toolbar_name.visibility = View.VISIBLE
            (activity as MainActivity).toolbar.visibility = View.GONE
        } else {
            txtSkip.visibility = View.GONE
            ivBack.visibility = View.GONE
            (activity as MainActivity).toolbar.visibility = View.VISIBLE
        }

        txtSkip.setOnClickListener {
            val intent = Intent(requireActivity(), MainActivity::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }


        paymentSheet = PaymentSheet(this, ::onPaymentSheetResult)

        prefeUserId = Utils.readIntData(
            requireContext(),
            Constants.PrefUserID,
            0
        )!!

        progressCardView.visibility = View.VISIBLE
        requireActivity().getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
        relativeLayout.visibility = View.GONE

        getUserSubscriptionPlans()

        upcomingPlanstatus = Utils.readStringFromSharedPref(
            requireActivity(), Constants.UPCOMINGPLAN,
            ""
        ).toString()


        ivBack.setOnClickListener() {
            if (isFromHome == true) {
                (activity as MainActivity).toolbar.visibility = View.VISIBLE
                (activity as MainActivity).replaceFragment(GetLiturgiesFragment(), "Get Liturgies")
            } else {
                (activity as MainActivity).toolbar.visibility = View.GONE
                (activity as MainActivity).replaceFragment(HomeFragment(), "")
            }
        }

        var subscriptionStatus = Utils.readStringFromSharedPref(
            requireActivity(), Constants.USER_SUBSCRIPTIONSTATUS,
            ""
        )


        subscriptionPlanList()

        btnSubscribeNow.setOnClickListener {
            btnSubscribeNow.isClickable = false

            if (Constants.USER_LOGIN_STATUS == Constants.SKIP_LOGIN) {
                //showLoginDialog()
                showDialogForUnlockWithoutLogin()
            } else {
                if (isPlanSelect) {
                    if (upcomingPlanstatus == "Yes") {
                        showSubscribDialog()
                    } else {
                        progressCardView.visibility = View.VISIBLE
                        requireActivity().getWindow().setFlags(
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                        );
                        var createSubscrptionReqVo: CreateSubscrptionReqVo =
                            CreateSubscrptionReqVo()
                        createSubscrptionReqVo.appUserId = prefeUserId.toString()
                        createSubscrptionReqVo.planType = selectedPlanType
                        createSubscription(createSubscrptionReqVo)
                    }
                } else {
                    Toast.makeText(requireContext(), "Please Select Plan", Toast.LENGTH_SHORT)
                        .show()
                }
            }

        }

        return view
    }


    fun showDialogForUnlockWithoutLogin() {
        val alertDialog = AlertDialog.Builder(
            requireActivity()
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
            val intent = Intent(requireActivity(), SelectOptionActivity::class.java)
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
                //  getSubscriptionStatus("Cancelled")
                // call when user click stripe pamenr sheet close icon
                progressCardView.visibility = View.VISIBLE
                requireActivity().getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )
                getSubscriptionStatus("Payment Canceled")
            }
            is PaymentSheetResult.Failed -> {
                print("Error: ${paymentSheetResult.error}")
                progressCardView.visibility = View.VISIBLE
                requireActivity().getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )
                getSubscriptionStatus("Cancelled")
            }
            is PaymentSheetResult.Completed -> {
                // Display for example, an order confirmation screen
                print("Completed")
                progressCardView.visibility = View.VISIBLE
                requireActivity().getWindow().setFlags(
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

                        subscriptionSatus = Utils.writeStringToSharedPref(
                            requireActivity(), Constants.USER_SUBSCRIPTIONSTATUS,
                            response.body()!!.subscription
                        ).toString()

                        subscriptionSatus = Utils.writeStringToSharedPref(
                            requireActivity(), Constants.USER_SUBSCRIPTIONMESSAGE,
                            response.body()!!.message
                        ).toString()

                        progressCardView.visibility = View.GONE
                        requireActivity().getWindow()
                            .clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

                        if (response.body()!!.subscription == "Yes") {
                            showPaymentStatusDialog()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Payment Cancelled",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }

                    } else {

                    }

                }

                override fun onFailure(call: Call<GetSubscriptionStatusResVo>, t: Throwable) {
                    Toast.makeText(requireContext(), "${t.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            })
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    private fun showPaymentStatusDialog() {
        val alertDialog = AlertDialog.Builder(
            requireContext()
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
            //  requireContext().onBackPressed()
            // (activity as MainActivity).toolbar.visibility = View.VISIBLE
            // (activity as MainActivity).replaceFragment(GetLiturgiesFragment(), "Get Liturgies")
            val intent =
                Intent(requireActivity(), MainActivity::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
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
                        requireActivity().getWindow()
                            .clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        relativeLayout.visibility = View.VISIBLE
                        setAdapter(requireActivity(), response.body()!!)

                    } else {

                    }

                }

                override fun onFailure(call: Call<SubscriptionPlanListResponseVo>, t: Throwable) {
                    Toast.makeText(requireContext(), "${t.message}", Toast.LENGTH_SHORT)
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
            this@SubscriptionPlanListFragment
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
                            requireContext(), Constants.CUSTOMER_ID,
                            response.body()!!.response.customer_id
                        )

                        Utils.writeStringToSharedPref(
                            requireContext(), Constants.SUBSCRIPTION_ID,
                            response.body()!!.response.subscription_id
                        )

                        Utils.writeStringToSharedPref(
                            requireContext(), Constants.PUBLISHABLE_KEY,
                            response.body()!!.response.publishable_key
                        )
                        Utils.writeStringToSharedPref(
                            requireContext(), Constants.EPHEMERALKEY_ID,
                            response.body()!!.response.ephemeralKey_id
                        )
                        Utils.writeStringToSharedPref(
                            requireContext(), Constants.PAYMENT_INTENT_ID,
                            response.body()!!.response.payment_intent_id
                        )

                        paymentIntentClientSecret = response.body()!!.response.payment_intent_id
                        customerConfig = PaymentSheet.CustomerConfiguration(
                            response.body()!!.response.customer_id,
                            response.body()!!.response.ephemeralKey_id
                        )

                        val publishableKey = response.body()!!.response.publishable_key
                        PaymentConfiguration.init(requireContext(), publishableKey)

                        progressCardView.visibility = View.GONE
                        requireActivity().getWindow()
                            .clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        presentPaymentSheet()

                    } else {
                        /* progressCardView.visibility = View.GONE
                         requireActivity().getWindow()
                             .clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                         Toast.makeText(
                             requireContext(),
                             "Call getUserSubscriptionPlans",
                             Toast.LENGTH_LONG
                         ).show()*/
                        //getUserSubscriptionPlans()
                        /*Toast.makeText(
                            requireContext(),
                            response.body()?.message,
                            Toast.LENGTH_LONG
                        ).show()*/
                    }

                }

                override fun onFailure(call: Call<CreateSubscrptionResVo>, t: Throwable) {
                    // Toast.makeText(context, "${t.message}", Toast.LENGTH_SHORT).show()
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

        Log.e(
            "token", Utils.readStringFromSharedPref(
                requireContext(),
                Constants.SHARED_PREF_TOKEN,
                ""
            ).toString()
        )

        val request = APIService.buildService(APIInterface::class.java)
        val call =
            request.getUserSubscriptionPlans(
                getUserSubscription.userId, getUserSubscription.deviceId,
                "bearer " + Utils.readStringFromSharedPref(
                    requireContext(),
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
                            requireActivity(), Constants.UPCOMINGPLAN,
                            response.body()!!.response.userSubscriptionData.upcomingPlan
                        )

                        upcomingPlanstatus = Utils.readStringFromSharedPref(
                            requireActivity(), Constants.UPCOMINGPLAN,
                            ""
                        ).toString()

                        Log.e("plan", "UpcomingPlan:" + upcomingPlanstatus)

                    } else {
                        // Log.e("getUser", "onResponse: getUserSubscriptionPlans ")
                    }
                }

                override fun onFailure(call: Call<GetUserSubscriptionPlanResVo>, t: Throwable) {
                    Toast.makeText(requireActivity(), "${t.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            })
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    private fun showSubscribDialog() {


        val alertDialog = AlertDialog.Builder(
            requireContext()
        )
        val inflater = layoutInflater
        val alertView: View = inflater.inflate(R.layout.subscription_status_dialog, null)
        alertDialog.setView(alertView)
        val show = alertDialog.show()
        val txtMessage = alertView.findViewById<View>(R.id.txtMessage) as TextView
        val txtOk = alertView.findViewById<View>(R.id.txtOk) as TextView

        txtMessage.text = "Currently, you have subscribed to the current plan and upcoming plan."

        txtOk.setOnClickListener {
            val intent = Intent(requireActivity(), MainActivity::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
            show.dismiss()
        }

        show.setCanceledOnTouchOutside(false)
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
        btnSubscribeNow.isClickable = true
        // Toast.makeText(context, "This is on resume", Toast.LENGTH_LONG).show()
        if (context != null) {
            /*     if (Utils.isNetworkAvailable(requireContext())) {
                if (liturgies.isVolume == "Yes")
                    Handler(Looper.getMainLooper()).postDelayed(
                        Runnable { getCollectionList(liturgies.volumeId) },
                        Constants.AFTER_PURCHASE_REFRESH_DELAY
                    )
                else
                    Handler(Looper.getMainLooper()).postDelayed(
                        Runnable { getMyLiturgiesList(liturgies.bookId) },
                        Constants.AFTER_PURCHASE_REFRESH_DELAY
                    )

            } else {
                Toast.makeText(
                    requireContext(),
                    resources.getString(R.string.check_internet),
                    Toast.LENGTH_LONG
                ).show()
            }
        }*/
        }
    }

    override fun onSelectPlan(pos: Int, planType: String, isAuto: Boolean) {
        selectedPlanType = planType
        isPlanSelect = isAuto
    }
}