package com.everymomentholy.ui.fragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.everymomentholy.R
import com.everymomentholy.api.APIInterface
import com.everymomentholy.api.APIService
import com.everymomentholy.api.request.GetLiturgiesRequestVo
import com.everymomentholy.api.request.PurchaseRequestVo
import com.everymomentholy.api.response.GetLiturgiesDataVo
import com.everymomentholy.api.response.GetLiturgiesResponseVo
import com.everymomentholy.interfaces.GetLiturgiesClickListner
import com.everymomentholy.ui.activity.*
import com.everymomentholy.ui.adapter.GetLiturgiesAdapter
import com.everymomentholy.utils.Constants
import com.everymomentholy.utils.ProductTypes
import com.everymomentholy.utils.Utils
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class GetLiturgiesFragment : Fragment(), GetLiturgiesClickListner {

    lateinit var viewPager: ViewPager
    private lateinit var adapter: GetLiturgiesAdapter
    private lateinit var android_id: String
    var prefeUserId: Int = 0
    lateinit var txtToolbarName: TextView
    lateinit var txtGetLiturgiesAbout: TextView
    private lateinit var txtLiturgyTitle: TextView
    private lateinit var txtLiturgyPrice: TextView
    lateinit var txtSubscribeForFull: TextView
    var liturgiesTitle: String = ""
    private lateinit var btnGetLiturgiesReadNow: Button
    private lateinit var txtUnlock: TextView
    private lateinit var txtDollar: TextView
    private lateinit var llGetLiturgiesMain: LinearLayout

    lateinit var paymentSheet: PaymentSheet
    lateinit var customerConfig: PaymentSheet.CustomerConfiguration
    lateinit var paymentIntentClientSecret: String
    lateinit var progressCardView: CardView


    @RequiresApi(Build.VERSION_CODES.CUPCAKE)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_getliturgies, container, false)

        paymentSheet = PaymentSheet(this, ::onPaymentSheetResult)

        viewPager = view.findViewById(R.id.viewPager)
        txtLiturgyTitle = view.findViewById(R.id.txtLiturgyTitle)
        txtLiturgyPrice = view.findViewById(R.id.txtLiturgyPrice)
        txtGetLiturgiesAbout = view.findViewById(R.id.txtGetLiturgiesAbout)
        txtUnlock = view.findViewById(R.id.txtUnlock)

        txtDollar = view.findViewById(R.id.txtDollar)
        llGetLiturgiesMain = view.findViewById(R.id.ll_getLiturgies_main)
        txtSubscribeForFull = view.findViewById(R.id.txtSubscribeForFull)
        progressCardView = view.findViewById(R.id.progressCardView)

        progressCardView.visibility = View.VISIBLE


        var usersubscriptionStatus = Utils.readStringFromSharedPref(
            requireActivity(), Constants.USER_SUBSCRIPTIONSTATUS,
            ""
        )
        if (usersubscriptionStatus == "Yes") {
            txtSubscribeForFull.visibility = View.GONE
        } else {
            txtSubscribeForFull.visibility = View.GONE

        }

        android_id = Settings.Secure.getString(
            requireContext().contentResolver,
            Settings.Secure.ANDROID_ID
        )
        prefeUserId = Utils.readIntData(
            requireActivity(),
            Constants.PrefUserID,
            0
        )!!

        var subscriptionStatus = Utils.readStringFromSharedPref(
            requireActivity(), Constants.USER_SUBSCRIPTIONSTATUS,
            ""
        )

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                var liturgyData = adapter.getLiturgiesData()?.get(position)

                Constants.GET_LITURGIES_VIEW_PAGER_POSITION = position

                setLiturgiesAndVolumeData(liturgyData)

            }

            override fun onPageSelected(position: Int) {
                txtLiturgyTitle.text = adapter.getLiturgiesData()?.get(position).volumeTitle
            }

            override fun onPageScrollStateChanged(state: Int) {

            }

        })

        return view
    }

    fun onPaymentSheetResult(paymentSheetResult: PaymentSheetResult) {
        when (paymentSheetResult) {
            is PaymentSheetResult.Canceled -> {
                print("Canceled")
            }
            is PaymentSheetResult.Failed -> {
                print("Error: ${paymentSheetResult.error}")
            }
            is PaymentSheetResult.Completed -> {
                // Display for example, an order confirmation screen
                print("Completed")
            }
        }

    }

    private fun getBooks() {
        var getLiturgiesRequestVo: GetLiturgiesRequestVo = GetLiturgiesRequestVo()
        getLiturgiesRequestVo.deviceId = android_id
        var token = ""
        if (Constants.USER_LOGIN_STATUS == Constants.SKIP_LOGIN) {
            getLiturgiesRequestVo.appUserId = Constants.SKIP_LOGIN_USER_ID
        } else {
            token = "bearer " + Utils.readStringFromSharedPref(
                requireContext(),
                Constants.SHARED_PREF_TOKEN,
                ""
            )
            getLiturgiesRequestVo.appUserId = prefeUserId
        }
        val request = APIService.buildService(APIInterface::class.java)
        val call = request.getBooks(
            getLiturgiesRequestVo.appUserId,
            getLiturgiesRequestVo.deviceId,
            token
        )

        try {
            call.enqueue(object : Callback<GetLiturgiesResponseVo> {
                override fun onResponse(
                    call: Call<GetLiturgiesResponseVo>,
                    response: Response<GetLiturgiesResponseVo>
                ) {
                    if (response.body()?.statusCode == 1) {
                        progressCardView.visibility = View.GONE
                        txtUnlock.visibility = View.VISIBLE
                        llGetLiturgiesMain.visibility = View.VISIBLE
                        var isVolume =
                            response.body()!!.response.data.filter { it.isVolume.equals("Yes", true) } as ArrayList<GetLiturgiesDataVo>
                        Log.e("VOLUME", "onResponse: " + isVolume.size.toString())
                        if (context != null) {
                            adapter = GetLiturgiesAdapter(
                                context!!,
                                //response.body()!!.response.data
                                isVolume
                            )
                            viewPager.setPadding(100, 0, 100, 0)
                            viewPager.adapter = adapter;
                        }

                        if (Constants.GET_LITURGIES_VIEW_PAGER_POSITION != 0)
                            viewPager.setCurrentItem(
                                Constants.GET_LITURGIES_VIEW_PAGER_POSITION,
                                false
                            )

                    } else {
                        Toast.makeText(
                            requireActivity(),
                            response.body()!!.response.message.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<GetLiturgiesResponseVo>, t: Throwable) {
                    Toast.makeText(requireActivity(), "${t.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            })
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun setLiturgiesAndVolumeData(liturgyData: GetLiturgiesDataVo) {
        txtGetLiturgiesAbout.setOnClickListener() {

            val bundle = Bundle()
            bundle.putSerializable("liturgies", liturgyData)
            var fragment: Fragment = AboutBookLiturgiesFragment()
            (activity as MainActivity).replaceFragment(fragment, "Get Liturgies", bundle)

        }

        txtUnlock.setOnClickListener() {
            if (liturgyData.isVolume.equals("Yes", true)) {
                val intent = Intent(context, CollectionListActivity::class.java)
                intent.putExtra("liturgies", liturgyData)
                context?.startActivity(intent)

                /*val bundle = Bundle()
                bundle.putSerializable("liturgies", liturgyData)
                var fragment: Fragment = CollectionListFragment()
                (activity as MainActivity).replaceFragment(fragment, "Collection", bundle)*/

            } else if (liturgyData.isFreeLiturgyAvailable.equals("No", true)) {
                showLiturgyDialog()
            } else {
                if (txtUnlock.text.toString().equals("Open", true)) {
                    // if (txtUnlock.text == "Read Now")
                    val intent = Intent(context, LiturgiesListDialogActivity::class.java)
                    intent.putExtra("liturgies", liturgyData)
                    context?.startActivity(intent)
                } else {
                    if (Constants.USER_LOGIN_STATUS == Constants.SKIP_LOGIN) {
                        showDialogForUnlockWithoutLogin(liturgyData)
                    } else {
                        val bundle = Bundle()
                        bundle.putSerializable("liturgies", liturgyData)
                        bundle.putBoolean("onPress", true);
                        bundle.putBoolean("onPressHome", true);
                        var fragment: Fragment = SubscriptionPlanListFragment()
                        (activity as MainActivity).replaceFragment(
                            fragment,
                            "subscription",
                            bundle
                        )

                    }
                }
            }
        }


        if (liturgyData.isVolume.equals("Yes", true
            )) {

            txtLiturgyTitle.text = liturgyData.volumeTitle

            if (liturgyData.volumeAmount == "0.0" || liturgyData.volumeAmount == "0.00" || liturgyData.isPurchased == "Yes") {
                txtUnlock.text = "Open"
                var sdk = android.os.Build.VERSION.SDK_INT;
                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    txtUnlock.setBackground(context?.resources?.getDrawable(R.drawable.bg_read_now));
                    txtUnlock.setTextColor(context?.resources?.getColor(R.color.loginbg)!!)
                } else {
                    txtUnlock.setBackground(context?.resources?.getDrawable(R.drawable.bg_read_now));
                    txtUnlock.setTextColor(context?.resources?.getColor(R.color.loginbg)!!)
                }
                if (liturgyData.isPurchased == "Yes") {
                    txtLiturgyPrice.text = "Purchased"
                } else {
                    txtLiturgyPrice.text = "Free"
                }
                txtDollar.text = ""
            }  else {
                if (!liturgyData.discountAmount.isNullOrEmpty() && liturgyData.discountAmount != "0.00") {
                    txtLiturgyPrice.text = "$" + liturgyData.discountAmount
                } else {
                    txtLiturgyPrice.text = "$ " + liturgyData.volumeAmount
                }
                txtUnlock.setBackground(context?.resources?.getDrawable(R.drawable.bg_unlock));
                txtUnlock.setTextColor(context?.resources?.getColor(R.color.white)!!)
                // txtUnlock.text = "Unlock"
                txtUnlock.text = "Subscribe"
            }

        } else {

            //if (liturgyData.bookAmount == "0.0" || liturgyData.bookAmount == "0.00" || liturgyData.isPurchased == "Yes")
            if (liturgyData.bookAmount == "0.0" || liturgyData.bookAmount == "0.00" || liturgyData.isPurchased.equals("Yes", true)) {
                txtUnlock.text = "Open"
                var sdk = android.os.Build.VERSION.SDK_INT;
                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    txtUnlock.setBackground(context?.resources?.getDrawable(R.drawable.bg_read_now));
                    txtUnlock.setTextColor(context?.resources?.getColor(R.color.loginbg)!!)
                } else {
                    txtUnlock.setBackground(context?.resources?.getDrawable(R.drawable.bg_read_now));
                    txtUnlock.setTextColor(context?.resources?.getColor(R.color.loginbg)!!)
                }
                if (liturgyData.isPurchased.equals("Yes", true)) {
                    txtLiturgyPrice.text = "Purchased"
                } else {
                    txtLiturgyPrice.text = "Free"
                }
                txtDollar.text = ""
            } else {
                txtLiturgyPrice.text = "$ " + liturgyData.bookAmount
                txtUnlock.setBackground(ContextCompat.getDrawable(requireActivity(), R.drawable.bg_unlock));
                txtUnlock.setTextColor(ContextCompat.getColor(requireActivity(), R.color.white)!!)
                // txtUnlock.text = "Unlock"
                //txtUnlock.text = "Subscribe"
                txtUnlock.text = "Subscribe"
            }
            txtLiturgyTitle.text = liturgyData.bookTitle

        }
    }

    fun showLiturgyDialog() {
        val alertDialog = AlertDialog.Builder(
            requireActivity()
        )
        val inflater = layoutInflater
        val alertView: View = inflater.inflate(R.layout.login_dialog, null)
        alertDialog.setView(alertView)
        val show = alertDialog.show()
        val alertCancel = alertView.findViewById<View>(R.id.txtLoginCancel) as TextView
        alertCancel.visibility = View.GONE
        val alertOk = alertView.findViewById<View>(R.id.txtLoginOk) as TextView


        alertOk.setOnClickListener {
            show.dismiss()
//            val intent = Intent(this@MainActivity, LoginActivity::class.java)
//            startActivity(intent)
        }

        alertCancel.setOnClickListener {
            show.dismiss()
        }
        show.setCanceledOnTouchOutside(false)
    }

    private fun startPurchaseFlow(getLiturgiesDataVo: GetLiturgiesDataVo) {
        val deviceId = Settings.Secure.getString(
            context?.contentResolver,
            Settings.Secure.ANDROID_ID
        )
        val userId = Utils.readIntData(
            requireContext(),
            Constants.PrefUserID,
            0
        )!!
        val purchaseRequestVo = PurchaseRequestVo(
            userId,
            bookId = getLiturgiesDataVo.bookId,
            amount = getLiturgiesDataVo.bookAmount,
            deviceId = deviceId,
            liturgyId = 0,
            volumeId = 0,
            productType = ProductTypes.BOOK,
            productId = getLiturgiesDataVo.bookPurchaseCode,
            discountAmount = getLiturgiesDataVo.discountAmount
        )

        /*val inAppUtils =
            InAppUtils.getInstance((context as Activity).application, GlobalScope)
        inAppUtils.initiatePurchaseFlow(context as Activity, purchaseRequestVo)*/
    }

    fun showDialogForUnlockWithoutLogin(myLiturgyDataVo: GetLiturgiesDataVo) {
        val alertDialog = AlertDialog.Builder(
            requireContext()
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
            val intent = Intent(context, SelectOptionActivity::class.java)
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

    fun showSubscriptionDialog() {
        val alertDialog = AlertDialog.Builder(
            requireContext()
        )
        val inflater = layoutInflater
        val alertView: View = inflater.inflate(R.layout.getliturgy_dialog, null)
        alertDialog.setView(alertView)
        val show = alertDialog.show()
        val alertButtonCancel = alertView.findViewById<View>(R.id.txtCancel) as TextView
        val alertButtonLoginRegister =
            alertView.findViewById<View>(R.id.txtPurchaseRegisterLogin) as TextView
        val alertButtonPurchase =
            alertView.findViewById<View>(R.id.txtPurchaseWithoutRegisterLogin) as TextView


        alertButtonLoginRegister.setOnClickListener {
            //val intent = Intent(context, SelectOptionActivity::class.java)
            //startActivity(intent)

            val bundle = Bundle()
            bundle.putBoolean("onPress", true);
            var fragment: Fragment = SubscriptionPlanListFragment()
            (activity as MainActivity).replaceFragment(
                fragment,
                "subscription",
                bundle
            )
            show.dismiss()
        }

        alertButtonCancel.setOnClickListener {
            show.dismiss()
        }

        alertButtonPurchase.setOnClickListener() {
            show.dismiss()
            // startPurchaseFlow(myLiturgyDataVo)
        }
        show.setCanceledOnTouchOutside(false)
    }

    override fun onResume() {
        super.onResume()
        //getBooks()
        if (context != null) {
            if (Utils.isNetworkAvailable(requireContext())) {

                Handler(Looper.getMainLooper()).postDelayed(
                    Runnable {
                        if (context != null) {
                            progressCardView.visibility = View.VISIBLE
                            getBooks()
                        }
                    },
                    Constants.AFTER_PURCHASE_REFRESH_DELAY
                )
            } else {
                Toast.makeText(
                    requireContext(),
                    resources.getString(R.string.check_internet),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

}