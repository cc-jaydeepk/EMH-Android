package com.everymomentholy.ui.fragments

import android.R.id.text2
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.billingclient.api.*
import com.everymomentholy.EMHApplication
import com.everymomentholy.R
import com.everymomentholy.api.APIInterface
import com.everymomentholy.api.APIService
import com.everymomentholy.api.request.*
import com.everymomentholy.api.response.*
import com.everymomentholy.interfaces.QueryPurchasesListner
import com.everymomentholy.interfaces.SubscriptionPlanListCLick
import com.everymomentholy.ui.activity.MainActivity
import com.everymomentholy.ui.activity.SelectOptionActivity
import com.everymomentholy.ui.adapter.*
import com.everymomentholy.utils.Constants
import com.everymomentholy.utils.InAppSubscription
import com.everymomentholy.utils.Utils
import kotlinx.coroutines.GlobalScope
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SubscriptionPlanListFragment : Fragment(), SubscriptionPlanListCLick, QueryPurchasesListner {

    //InApp subscription link
    //https://dev.to/theplebdev/adding-subscriptions-to-your-android-app-part-3-checking-if-user-is-subscribed-3793
    //https://codelabs.developers.google.com/play-billing-codelab#1
    //https://developer.android.com/google/play/billing/integrate

    lateinit var txtSkip: TextView

    var liturgies: GetLiturgiesDataVo = GetLiturgiesDataVo()
    private var planListAdapter: RecyclerView.Adapter<SubscriptionListPlanAdapter.MyViewHolder>? =
        null

    var prefeUserId: Int = 0
    var android_id: String = ""
    lateinit var ivBack: ImageView
    lateinit var txt_toolbar_name: TextView
    lateinit var progressCardView: CardView
    var isPlanSelect = false
    lateinit var selectedPlanType: String

    lateinit var rcvSubscriptionPlan: RecyclerView


    lateinit var statusMessage: String

    lateinit var relativeLayout: RelativeLayout

    var isFrom: Boolean = false
    var isFromHome: Boolean = false

    var message: String = ""
    lateinit var upcomingPlanstatus: String
    lateinit var token: String

    private lateinit var txtYCurrency: TextView
    private lateinit var txtCurrency: TextView
    private lateinit var txtYPrice: TextView
    private lateinit var txtMontlyPrice: TextView
    private lateinit var txtYType: TextView
    private lateinit var txtYMessag: TextView
    private lateinit var txtYsave: TextView
    private lateinit var txtYearly: TextView
    private lateinit var txtMonthly: TextView
    private lateinit var txtYUnlimitedAccess: TextView
    private lateinit var txtUnlimitedAccess: TextView
    private lateinit var txtSubscriptionType: TextView
    private lateinit var txtInAppInstructions: TextView
    lateinit var linearMonthly: LinearLayout
    lateinit var linearYearly: LinearLayout
    lateinit var linearStatic: LinearLayout
    lateinit var relativeToolbar: RelativeLayout
    lateinit var cardViewData: LinearLayout
    lateinit var btnSubscribeNow: Button
    var planType: String? = "monthly"
    lateinit var activeEMail: String
    var subscriptionStatusfromProfile: String = ""
    lateinit var subscriptionType: String
    lateinit var subType: String
    lateinit var planTypeIsStripe: String
    lateinit var monthlyPlanPrice: String
    lateinit var yearlyPlanPrice: String

    // lateinit var inAppSubscriptionStatus: String
    lateinit var statusHistory: String
    private var selectedItemPosition: Int = RecyclerView.NO_POSITION

    private lateinit var billingClientLifecycle: InAppSubscription


    @SuppressLint("HardwareIds", "SuspiciousIndentation")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        // val view = inflater.inflate(R.layout.activity_about_book_liturgies, container, false)
        val view = inflater.inflate(R.layout.subscriptionplan_frag, container, false)

        billingClientLifecycle =
            (getActivity()?.getApplicationContext() as EMHApplication).billingClientLifecycle
        lifecycle.addObserver(billingClientLifecycle)

        isFrom = requireArguments().getBoolean("onPress")
        // isFromHome = requireArguments().getBoolean("onPressHome")
        Log.e("VALUE", "onCreateView: " + isFrom)

        (activity as MainActivity).toolbar.visibility = View.VISIBLE
        (activity as MainActivity).iv_toolbar_backImage.visibility = View.GONE
        (activity as MainActivity).iv_toolbar_search.visibility = View.GONE
        (activity as MainActivity).iv_toolbar_drawer.visibility = View.VISIBLE
        (activity as MainActivity).iv_toolbar_notification.visibility = View.GONE
        (activity as MainActivity).txt_toolbar_name.text = "Subscription Plan"

        txtSkip = view.findViewById(R.id.txtSkip)
        ivBack = view.findViewById(R.id.iv_back)
        txt_toolbar_name = view.findViewById(R.id.txt_toolbar_name)
        btnSubscribeNow = view.findViewById(R.id.btnSubscribeNow)
        rcvSubscriptionPlan = view.findViewById(R.id.rcvSubscriptionPlan)
        progressCardView = view.findViewById(R.id.progressCardView)
        relativeLayout = view.findViewById(R.id.relativeLayout)
        linearMonthly = view.findViewById(R.id.linearMonthly)
        linearYearly = view.findViewById(R.id.linearYearly)
        txtYCurrency = view.findViewById(R.id.txtYCurrency)
        txtCurrency = view.findViewById(R.id.txtCurrency)
        txtYPrice = view.findViewById(R.id.txtYPrice)
        txtMontlyPrice = view.findViewById(R.id.txtMontlyPrice)
        txtYearly = view.findViewById(R.id.txtYearly)
        txtMonthly = view.findViewById(R.id.txtMonthly)
        txtYUnlimitedAccess = view.findViewById(R.id.txtYUnlimitedAccess)
        txtUnlimitedAccess = view.findViewById(R.id.txtUnlimitedAccess)
        txtYsave = view.findViewById(R.id.txtYsave)
        relativeToolbar = view.findViewById(R.id.relativeToolbar)
        linearStatic = view.findViewById(R.id.linearStatic)
        txtSubscriptionType = view.findViewById(R.id.txtSubscriptionType)
        cardViewData = view.findViewById(R.id.cardView)
        txtInAppInstructions = view.findViewById(R.id.txtInAppInstructions)

        linearStatic.visibility = View.VISIBLE
        //DENISHA

        // val firstItem = subscriptionPlanList[0]
        /* for (i in 0 until subscriptionPlanList.size) {
             Log.e("LISTDATA", "onCreateView: " + subscriptionPlanList.get(i) )
             //text2.setText(text2.getText());
         }*/


        if (isFrom) {
            txtSkip.visibility = View.INVISIBLE
            (activity as MainActivity).iv_toolbar_backImage.visibility = View.VISIBLE
            (activity as MainActivity).iv_toolbar_drawer.visibility = View.GONE
            // (activity as MainActivity).toolbar.visibility = View.GONE
        } else {
            /*txtSkip.visibility = View.VISIBLE
            (activity as MainActivity).toolbar.visibility = View.GONE
            (activity as MainActivity).iv_toolbar_backImage.visibility = View.GONE
            relativeToolbar.visibility = View.VISIBLE*/
            txtSkip.visibility = View.INVISIBLE
            (activity as MainActivity).toolbar.visibility = View.VISIBLE
            (activity as MainActivity).iv_toolbar_drawer.visibility = View.VISIBLE
            (activity as MainActivity).txt_toolbar_name.text = "Subscription Plan"
        }

        txtInAppInstructions.setOnClickListener {
            AlertDialog.Builder(activity as MainActivity)
                .setTitle("Free Trial Information")
                .setMessage(R.string.free_trial_info) // Display the string from resources
                .setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss() // Dismiss the dialog when "OK" is pressed
                }
                .setCancelable(true)
                .show() // Show the dialog
        }

        /* if (isFromHome){
             (activity as MainActivity).toolbar.visibility = View.GONE
             (activity as MainActivity).iv_toolbar_backImage.visibility = View.GONE
             relativeToolbar.visibility = View.VISIBLE
         }else{
             (activity as MainActivity).toolbar.visibility = View.VISIBLE
         }*/

        ivBack.setOnClickListener() {
            /*if (isFrom == true) {
                (activity as MainActivity).toolbar.visibility = View.VISIBLE
                (activity as MainActivity).replaceFragment(GetLiturgiesFragment(), "Get Liturgies")
            } else {
                (activity as MainActivity).toolbar.visibility = View.GONE
                (activity as MainActivity).replaceFragment(HomeFragment(), "Every Moment Holy")
            }*/
            //(activity as MainActivity).toolbar.visibility = View.VISIBLE
            (activity as MainActivity).replaceFragment(HomeFragment(), "Every Moment Holy")
        }


        android_id = Settings.Secure.getString(
            requireContext().contentResolver,
            Settings.Secure.ANDROID_ID
        )

        var itemPosition = Utils.readIntData(
            requireActivity(),
            Constants.ITEM_POSITION,
            0
        )!!

        Log.e("itemPosition", "TOKEN: " + itemPosition + "selected")
        // fbgvbgngcdvbnh

        if (itemPosition == 0) {

        } else {

        }

        token = Utils.readStringFromSharedPref(
            requireActivity(),
            Constants.SHARED_PREF_TOKEN,
            ""
        ).toString()

        prefeUserId = Utils.readIntData(
            requireContext(),
            Constants.PrefUserID,
            0
        )!!

        subscriptionType = Utils.readStringFromSharedPref(
            requireActivity(), Constants.SUBSCRIPTION_TYPE,
            ""
        ).toString()


        upcomingPlanstatus = Utils.readStringFromSharedPref(
            requireActivity(), Constants.UPCOMINGPLAN,
            ""
        ).toString()

        /*var subscriptionStatus = Utils.readStringFromSharedPref(
            requireActivity(), Constants.IN_APP_SUBSCRIPTION_STATUS,
            ""
        ).toString()
        Log.e("ASDF", "INAPPSUB: " + subscriptionStatus)*/

        subscriptionStatusfromProfile = Utils.readStringFromSharedPref(
            requireActivity(), Constants.PROFILE_STATUS,
            ""
        ).toString()

        subType = Utils.readStringFromSharedPref(
            requireActivity(), Constants.TYPE,
            ""
        ).toString()

        planTypeIsStripe = Utils.readStringFromSharedPref(
            requireActivity(), Constants.PLAN_TYPE,
            ""
        ).toString()

        if (Constants.USER_LOGIN_STATUS == Constants.SKIP_LOGIN) {
            cardViewData.visibility = View.GONE
        }


        if (subscriptionStatusfromProfile.equals("Yes", true)) {

            if (subType == "Monthly") {
                //  subscriptionType = "Monthly"
                linearYearly.isClickable = false
                linearMonthly.isClickable = false
                linearYearly.isEnabled = false
                linearMonthly.isEnabled = false
                txtSubscriptionType.text =
                    "You have a current subscription -" + " " + subType

                linearMonthly.setBackgroundColor(
                    ContextCompat.getColor(
                        requireActivity(),
                        R.color.loginbg
                    )
                );
                linearYearly.setBackgroundResource(R.drawable.subcripiton_bg);

                txtCurrency.setTextColor(ContextCompat.getColor(requireActivity(), R.color.white));
                txtMontlyPrice.setTextColor(
                    ContextCompat.getColor(
                        requireActivity(),
                        R.color.white
                    )
                );
                txtMonthly.setTextColor(ContextCompat.getColor(requireActivity(), R.color.white));
                txtUnlimitedAccess.setTextColor(
                    ContextCompat.getColor(
                        requireActivity(),
                        R.color.white
                    )
                );

                txtYCurrency.setTextColor(
                    ContextCompat.getColor(
                        requireActivity(),
                        R.color.loginbg
                    )
                );
                txtYPrice.setTextColor(ContextCompat.getColor(requireActivity(), R.color.loginbg));
                txtYearly.setTextColor(ContextCompat.getColor(requireActivity(), R.color.loginbg));
                txtYUnlimitedAccess.setTextColor(
                    ContextCompat.getColor(
                        requireActivity(),
                        R.color.loginbg
                    )
                );
                txtYsave.setTextColor(ContextCompat.getColor(requireActivity(), R.color.loginbg));



            } else {
                linearYearly.isClickable = false
                linearMonthly.isClickable = false
                linearYearly.isEnabled = false
                linearMonthly.isEnabled = false
                txtSubscriptionType.text =
                    "You have a current subscription -" + subType

                linearYearly.setBackgroundColor(
                    ContextCompat.getColor(
                        requireActivity(),
                        R.color.loginbg
                    )
                )
                linearMonthly.setBackgroundResource(R.drawable.subcripiton_bg);
                txtYCurrency.setTextColor(ContextCompat.getColor(requireActivity(), R.color.white));
                txtYPrice.setTextColor(ContextCompat.getColor(requireActivity(), R.color.white));
                txtYearly.setTextColor(ContextCompat.getColor(requireActivity(), R.color.white));
                txtYUnlimitedAccess.setTextColor(
                    ContextCompat.getColor(
                        requireActivity(),
                        R.color.white
                    )
                )
                txtYsave.setTextColor(ContextCompat.getColor(requireActivity(), R.color.white))

                txtCurrency.setTextColor(
                    ContextCompat.getColor(
                        requireActivity(),
                        R.color.loginbg
                    )
                )
                txtMontlyPrice.setTextColor(
                    ContextCompat.getColor(
                        requireActivity(),
                        R.color.loginbg
                    )
                )
                txtMonthly.setTextColor(ContextCompat.getColor(requireActivity(), R.color.loginbg))
                txtUnlimitedAccess.setTextColor(
                    ContextCompat.getColor(
                        requireActivity(),
                        R.color.loginbg
                    )
                )
            }
        } else {
            //txtSubscriptionType.text = "You are not subscribed"
            txtSubscriptionType.visibility = View.GONE
        }

        if (subscriptionStatusfromProfile.equals("Yes", true)) {
            btnSubscribeNow.text = "Unsubscribe"
        } else {
            btnSubscribeNow.text = "Subscribe Now"
        }

        activeEMail = Utils.readStringFromSharedPref(
            requireActivity(), Constants.ACTIVE_PLAYSTORE_EMAIL,
            ""
        ).toString()
        Log.e("ActiveAccount", "onCreateView: " + activeEMail)


        val utilityCallback =
            InAppSubscription.getInstance((context as Activity).application, GlobalScope)
        utilityCallback.setCallbackListener(this)

//        billingClientLifecycle.establishConnection()
//        billingClientLifecycle.querySubscriptionPurchases()


        txtSkip.setOnClickListener {
            val intent = Intent(requireActivity(), MainActivity::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }
        progressCardView.visibility = View.VISIBLE
        requireActivity().getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
     relativeLayout.visibility = View.GONE
        subscriptionPlanList()


        monthlyPlanPrice = Utils.readStringFromSharedPref(requireActivity(),Constants.MONTHLY_SUB_PRICE,"$2.99")
            .toString()
        Log.e("monthlyPlanPrice","monthlyPlanPrice-->"+monthlyPlanPrice)
        txtMonthly.setText(monthlyPlanPrice)

        linearMonthly.setOnClickListener {
            linearMonthly.setBackgroundColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.loginbg
                )
            );
            linearYearly.setBackgroundResource(R.drawable.subcripiton_bg);

            txtCurrency.setTextColor(ContextCompat.getColor(requireActivity(), R.color.white));
            txtMontlyPrice.setTextColor(ContextCompat.getColor(requireActivity(), R.color.white));
            txtMonthly.setTextColor(ContextCompat.getColor(requireActivity(), R.color.white));

            txtUnlimitedAccess.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.white
                )
            );

            txtYCurrency.setTextColor(ContextCompat.getColor(requireActivity(), R.color.loginbg));
            txtYPrice.setTextColor(ContextCompat.getColor(requireActivity(), R.color.loginbg));
            txtYearly.setTextColor(ContextCompat.getColor(requireActivity(), R.color.loginbg));
            txtYsave.setTextColor(ContextCompat.getColor(requireActivity(), R.color.loginbg));
            txtYUnlimitedAccess.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.loginbg
                )
            );


            planType = "monthly"
        }

        linearYearly.setOnClickListener {
            linearYearly.setBackgroundColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.loginbg
                )
            );
            linearMonthly.setBackgroundResource(R.drawable.subcripiton_bg);
            txtYCurrency.setTextColor(ContextCompat.getColor(requireActivity(), R.color.white));
            txtYPrice.setTextColor(ContextCompat.getColor(requireActivity(), R.color.white));
            txtYearly.setTextColor(ContextCompat.getColor(requireActivity(), R.color.white));
            txtYsave.setTextColor(ContextCompat.getColor(requireActivity(), R.color.white));
            txtYUnlimitedAccess.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.white
                )
            );


            txtCurrency.setTextColor(ContextCompat.getColor(requireActivity(), R.color.loginbg));
            txtMontlyPrice.setTextColor(ContextCompat.getColor(requireActivity(), R.color.loginbg));
            txtMonthly.setTextColor(ContextCompat.getColor(requireActivity(), R.color.loginbg));
            txtUnlimitedAccess.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.loginbg
                )
            );
            planType = "yearly"
        }

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
                    billingClientLifecycle.showProducts(requireActivity(), planType!!)
                    /*if (subscriptionStatusfromProfile.equals("Yes", true)) {
                        alreadySubscribeDialog()
                    } else {
                        Log.e("PLANTYPE", "onCreate: " + planType)
                        billingClientLifecycle.showProducts(requireActivity(), planType!!)
                    }*/
                }
            }

        }
        return view
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
                       rcvSubscriptionPlan.visibility = View.GONE
                        //DENISHA
                        /*setAdapter(requireActivity(), response.body()!!)

                        if(!TextUtils.isEmpty(subType))
                        {
                            (planListAdapter as SubscriptionListPlanAdapter).setItemSelected(subType)
                        }*/

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

                            /*if (response.body()!!.response[i].plan_savings == "") {
                                //  holder.txtSaving.text = "(Savings of $" + "" + planList.plan_savings + "/month)"
                                txtYsave.visibility = View.GONE
                            } else {
                                txtYsave.text = "(Save $" + "" + response.body()!!.response[0].plan_savings + "/year)"
                            }*/

//                            planList.unit_amount
                            Log.e("PPPPPPP", "onResponse: "+ response.body()!!.response[i].plan_type)
                            Log.e("PPPPPPP", "onResponse: "+ response.body()!!.response[i].unit_amount)
                            Log.e("PPPPPPP", "onResponse: "+ response.body()!!.response[i].plan_message)
                        }

                        /*for (i in 0..response.body()!!.response.size - 1){
                            var temp = SubScriptionPlanSubResponseVo()
                            temp.plan_type = response.body()!!.response.
                        }*/
                        billingClientLifecycle.getMonthlyProductDetails()
                        billingClientLifecycle.getYearlyProductDetails()
                        monthlyPlanPrice = Utils.readStringFromSharedPref(requireActivity(),Constants.MONTHLY_SUB_PRICE,"$2.99")
                            .toString()
                        Log.e("monthlyPlanPrice","monthlyPlanPrice-->"+monthlyPlanPrice)
                        txtMontlyPrice.setText(monthlyPlanPrice)

                        yearlyPlanPrice = Utils.readStringFromSharedPref(requireActivity(),Constants.YEARLY_SUB_PRICE,"$29.99")
                            .toString()
                        Log.e("yearlyPlanPrice","yearlyPlanPrice-->"+yearlyPlanPrice)
                        txtYPrice.setText(yearlyPlanPrice)

                    } else {
                        progressCardView.visibility = View.GONE
                        requireActivity().getWindow()
                            .clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        relativeLayout.visibility = View.VISIBLE
                         rcvSubscriptionPlan.visibility = View.GONE
                         linearStatic.visibility = View.VISIBLE
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
        //val position = layoutManager.findFirstVisibleItemPosition()
        // attach adapter to the recycler view
        rcvSubscriptionPlan.adapter = planListAdapter

    }

    private fun alreadySubscribeDialog() {
        val alertDialog = AlertDialog.Builder(
            requireActivity()
        )
        val inflater = layoutInflater
        val alertView: View = inflater.inflate(R.layout.logout_dialog, null)
        alertDialog.setView(alertView)
        val show = alertDialog.show()
        val alertButtonCancel = alertView.findViewById<View>(R.id.txtLougotCancel) as TextView
        val alertButtonYes = alertView.findViewById<View>(R.id.txtLogoutYes) as TextView
        val txtMessage = alertView.findViewById<View>(R.id.txtMessage) as TextView
        val txtSubType = alertView.findViewById<View>(R.id.txtSubType) as TextView

        if (planTypeIsStripe.equals("strip", true)) {
            txtMessage.text =
                "This subscription was done with the Stripe payment system. When it ends, you will need to re-subscribe. Future subscriptions will be handled by Apple/Google."
            txtSubType.visibility = View.VISIBLE
            txtSubType.visibility = View.GONE
            alertButtonCancel.visibility = View.GONE
        } else {
            txtMessage.text = getString(R.string.unsubscribestep)
            txtSubType.visibility = View.VISIBLE
            txtSubType.text = "You have a current subscription" + " " + subType
        }


        /*txtMessage.text = getString(R.string.unsubscribestep)
        txtSubType.visibility = View.VISIBLE
        txtSubType.text = "You have a current subscription" + " " + subType*/

        alertButtonCancel.text = "Cancel existing plan"
        alertButtonYes.text = "Ok"
        alertButtonCancel.setTextSize(15f);
        alertButtonYes.setTextSize(15f);

        alertButtonYes.setOnClickListener {
            show.dismiss()
            /* val intent = Intent(requireActivity(), MainActivity::class.java)
             intent.flags =
                 Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
             startActivity(intent)
             requireActivity().finish()*/
            // getUserProfile()
            val bundle = Bundle()
            var fragment: Fragment = HomeFragment()
            (context as MainActivity).replaceFragment(fragment, "Every Moment Holy", bundle)

        }

        alertButtonCancel.setOnClickListener {
            show.dismiss()
            val packageName = "com.everymomentholy.ui.fragments"

            val subscriptionIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/account/subscriptions?package=$packageName")
            )
            if (subscriptionIntent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(subscriptionIntent)
            } else {
                // Handle the case where the Google Play Store is not installed on the device
                // or there's no activity to handle the intent.
            }
        }
        show.setCanceledOnTouchOutside(false)
    }

    private fun subscriptionPurchased() {
        val alertDialog = AlertDialog.Builder(
            requireActivity()
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
        alertButtonCancel.setTextSize(15f);
        alertButtonYes.setTextSize(15f);

        alertButtonYes.setOnClickListener {
            show.dismiss()
            /* val intent = Intent(requireActivity(), MainActivity::class.java)
             intent.flags =
                 Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
             startActivity(intent)
             requireActivity().finish()*/
            // getUserProfile()
            val bundle = Bundle()
            var fragment: Fragment = HomeFragment()
            (context as MainActivity).replaceFragment(fragment, "Every Moment Holy", bundle)

        }
        show.setCanceledOnTouchOutside(false)
    }

    private fun getUserProfile() {
        var getUserProfileRequestVo: GetUserProfileRequestVo = GetUserProfileRequestVo()
        getUserProfileRequestVo.deviceId = android_id
        getUserProfileRequestVo.userId = prefeUserId

        Log.e(
            "token", Utils.readStringFromSharedPref(
                requireActivity(),
                Constants.SHARED_PREF_TOKEN,
                ""
            ).toString()
        )

        val request = APIService.buildService(APIInterface::class.java)
        val call =
            request.getUserProfile(
                getUserProfileRequestVo.userId, getUserProfileRequestVo.deviceId,
                "bearer " + Utils.readStringFromSharedPref(
                    requireActivity(),
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
                            requireActivity(), Constants.PROFILE_STATUS,
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


    override fun onResume() {
        super.onResume()
        // Toast.makeText(context, "This is on resume", Toast.LENGTH_LONG).show()

    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onSelectPlan(pos: Int, planType: String, isAuto: Boolean) {
        val itemPosition = pos
        selectedPlanType = planType
        isPlanSelect = isAuto
        Log.e("ITEMPOSTION", "onSelectPlan: " + itemPosition)

        //selectedItemPosition = pos

        /*selectedItemPosition = pos
               rcvSubscriptionPlan.adapter?.notifyDataSetChanged()*/
        //rcvSubscriptionPlan.adapter?.notifyDataSetChanged()

        Utils.writeIntToSharedPref(
            requireActivity(), Constants.ITEM_POSITION,
            itemPosition
        )
    }

    override fun onQueryPurchase(
        purchasesList: MutableList<Purchase>
    ) {
        Log.e("Purchase", "onQueryPurchase: In Fragment" + purchasesList.size)

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

                    if (Utils.isNetworkAvailable(requireActivity())) {
                        updateSubscriptionStatus(updateSubscriptionStatusReq)
                    } else {
                        Toast.makeText(
                            requireActivity(),
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
                updateSubscriptionStatusReq.payment_email = activeEMail
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

                if (Utils.isNetworkAvailable(requireActivity())) {
                    updateSubscriptionStatus(updateSubscriptionStatusReq)
                } else {
                    Toast.makeText(
                        requireActivity(),
                        resources.getString(R.string.check_internet),
                        Toast.LENGTH_LONG
                    ).show()
                }

            }
        } else {

        }
    }

    private fun updateSubscriptionStatus(updateSubscriptionStatusReq: UpdateSubscriptionStatusReq) {
        val request = APIService.buildService(APIInterface::class.java)
        val call =
            request.updateSubscriptionStatus(
                updateSubscriptionStatusReq,
                "bearer " + Utils.readStringFromSharedPref(
                    requireActivity(),
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
                            requireActivity(), Constants.IN_APP_SUBSCRIPTION_STATUS,
                            response.body()!!.subscription_status
                        )

                        val message = response.body()!!.subscription_status
                        val message1 = "Store value from subscription fragment"

                        Utils.writeStringToSharedPref(
                            requireActivity(), "RANDOM_KEYWORD",
                            message
                        )

                        Utils.writeStringToSharedPref(
                            requireActivity(), "RANDOM_KEYWORD",
                            message1
                        )

                    } else {
                        if (Constants.USER_LOGIN_STATUS == Constants.SKIP_LOGIN) {

                        } else {
                            Toast.makeText(
                                activity,
                                response.body()!!.message,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }

                override fun onFailure(call: Call<UpdateSubscriptionStatusRes>, t: Throwable) {
                    Toast.makeText(activity, "${t.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            })
        } catch (exception: Exception) {
            exception.printStackTrace()
        }

    }

    override fun onQueryMonthlySubs(monthlyPrice: String) {
        Utils.writeStringToSharedPref(requireActivity(),Constants.MONTHLY_SUB_PRICE,monthlyPrice)
        Log.e("monthlyPlanPrice","monthlyPlanPrice-->"+monthlyPrice)
        txtMontlyPrice.setText(monthlyPrice)
        txtCurrency.visibility = View.INVISIBLE
    }

    override fun onQueryYearlySubs(yearlyPrice: String) {
        Utils.writeStringToSharedPref(requireActivity(),Constants.YEARLY_SUB_PRICE,yearlyPrice)
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