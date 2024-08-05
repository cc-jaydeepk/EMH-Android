package com.everymomentholy.utils

//import com.android.billingclient.api.*

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import com.android.billingclient.api.*
import com.everymomentholy.R
import com.everymomentholy.api.APIInterface
import com.everymomentholy.api.APIService
import com.everymomentholy.api.request.SaveSubscriptionDataReqVo
import com.everymomentholy.api.request.UpdateSubscriptionStatusReq
import com.everymomentholy.api.response.SaveSubscriptionDataResVo
import com.everymomentholy.api.response.UpdateSubscriptionStatusRes
import com.everymomentholy.interfaces.QueryPurchasesListner
import com.everymomentholy.ui.activity.MainActivity
import com.everymomentholy.ui.fragments.HomeFragment
import kotlinx.coroutines.CoroutineScope
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class InAppSubscription private constructor(
    application: Application,
    private val defaultScope: CoroutineScope,
) : DefaultLifecycleObserver, PurchasesUpdatedListener, BillingClientStateListener,
    ProductDetailsResponseListener, PurchasesResponseListener {

//, PurchaseHistoryResponseListener

    private var mListener: QueryPurchasesListner? = null
    var prefeUserId: Int = 0
    var isSadeDataCall: Boolean = false
    var isPlayStoreInstalled = false

    fun setCallbackListener(listener: QueryPurchasesListner) {
        mListener = listener
    }

    /*fun callActivityMethod() {
        // Check if the listener is not null
        mListener?.onQueryPurchase("Message from UtilityCallback", "", 123)
    }*/


    private lateinit var lifecycle: Lifecycle
    private lateinit var queryProductDetailsParams: QueryProductDetailsParams
    //lateinit var billingClient: BillingClient

    private val LIST_OF_SUBSCRIPTION_PRODUCTS = listOf(
        Constants.MONTHLY_SUB,
        Constants.YEARLY_SUB,
    )

    lateinit var activity: Activity
    var application = application
    lateinit var billingClient: BillingClient
    lateinit var subsciprionPlanType: String


    override fun onCreate(owner: LifecycleOwner) {
        Log.d(TAG, "ON_CREATE")
        // Create a new BillingClient in onCreate().
        // Since the BillingClient can only be used once, we need to create a new instance
        // after ending the previous connection to the Google Play Store in onDestroy().
        billingClient = BillingClient.newBuilder(application)
            .setListener(this)
            .enablePendingPurchases() // Not used for subscriptions.
            .build()
        /*if (!billingClient.isReady) {
            Log.d(TAG, "BillingClient: Start connection...")
            billingClient.startConnection(this)
        }*/

        prefeUserId = Utils.readIntData(
            application,
            Constants.PrefUserID,
            0
        )!!

        establishConnection()

    }

    override fun onBillingSetupFinished(billingResult: BillingResult) {
        val responseCode = billingResult.responseCode
        val debugMessage = billingResult.debugMessage
        Log.d(TAG, "onBillingSetupFinished: $responseCode $debugMessage")
        if (responseCode == BillingClient.BillingResponseCode.OK) {
            // The billing client is ready.
            // You can query product details and purchases here.
            /*Toast.makeText(
                activity,
                "test test test test",
                Toast.LENGTH_LONG
            ).show()*/
            querySubscriptionProductDetails()
            querySubscriptionPurchases()
            // queryPurchaseAsyncHistory()
//            queryOneTimeProductPurchases()
        }
    }

    override fun onBillingServiceDisconnected() {
        if (application != null) {
            Toast.makeText(
                application,
                "Service Disconnected",
                Toast.LENGTH_LONG
            ).show()
        }

    }

    fun establishConnection() {
        if (!billingClient.isReady) {
            Log.d(TAG, "BillingClient: Start connection...")
            billingClient.startConnection(this)
        }
    }

    @SuppressLint("SetTextI18n")
    fun showProducts(activity: Activity, planType: String) {
        this.activity = activity
        subsciprionPlanType = planType
        try {
            var productDetailsM: ProductDetails? = null
            if (planType == "monthly") {
                queryProductDetailsParams =
                    QueryProductDetailsParams.newBuilder()
                        .setProductList(
                            com.google.firebase.crashlytics.buildtools.reloc.com.google.common.collect.ImmutableList.of(
                                QueryProductDetailsParams.Product.newBuilder()
                                    .setProductId("emh_monthly_plan") // set product id
                                    .setProductType(BillingClient.ProductType.SUBS)
                                    .build()
                            )
                        )
                        .build()
            } else {
                queryProductDetailsParams =
                    QueryProductDetailsParams.newBuilder()
                        .setProductList(
                            com.google.firebase.crashlytics.buildtools.reloc.com.google.common.collect.ImmutableList.of(
                                QueryProductDetailsParams.Product.newBuilder()
                                    .setProductId("emh_yearly_plan") // set product id
                                    .setProductType(BillingClient.ProductType.SUBS)
                                    .build()
                            )
                        )
                        .build()
            }

            var productDetailsParamsList = ArrayList<BillingFlowParams.ProductDetailsParams>()
            billingClient.queryProductDetailsAsync(queryProductDetailsParams) { billingResult,
                                                                                productDetailsList ->

                if (productDetailsList.size == 0) {
                    val packageManager = activity.packageManager
                    checkPlayStoreConnection(packageManager, activity)
                }

                for (productDetails in productDetailsList) {
                    productDetailsM = productDetails
                }

                productDetailsM?.let { launchPurchaseFlow(it,activity) }
            }
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }

    fun isPlayStoreInstalled(packageManager: PackageManager): Boolean {
        val playStorePackageName = "com.android.vending"
        return try {
            packageManager.getPackageInfo(playStorePackageName, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    // Check if device can handle Google Play Store intents
    fun isPlayStoreConnected(packageManager: PackageManager): Boolean {
        val intent = packageManager.getLaunchIntentForPackage("com.android.vending")
        return intent != null
    }

    fun checkPlayStoreConnection(packageManager: PackageManager, activity: Activity) {
        if (isPlayStoreInstalled(packageManager)) {
            if (isPlayStoreConnected(packageManager)) {
                println("Google Play Store is installed and connected.")
                if (activity != null) {
                    Toast.makeText(
                        activity,
                        "Google Play Store is installed and connected.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } else {
                println("Google Play Store is installed but not connected.")
                //huthgfvmfidknb;okfr;ok
                isPlayStoreInstalled = true
                playStorenotConnected(isPlayStoreInstalled, activity)

            }
        } else {
            println("Google Play Store is not installed.")
            isPlayStoreInstalled = false
            playStorenotConnected(isPlayStoreInstalled, activity)

        }
    }

    private fun playStorenotConnected(isPlayStoreInstalled: Boolean, activity: Activity) {
        if (activity != null) {
            val alertDialog = AlertDialog.Builder(
                activity
            )
            val inflater = activity.layoutInflater
            val alertView: View = inflater.inflate(R.layout.logout_dialog, null)
            alertDialog.setView(alertView)
            val show = alertDialog.show()
            val alertButtonCancel = alertView.findViewById<View>(R.id.txtLougotCancel) as TextView
            val alertButtonYes = alertView.findViewById<View>(R.id.txtLogoutYes) as TextView
            val txtMessage = alertView.findViewById<View>(R.id.txtMessage) as TextView
            alertButtonCancel.visibility = View.GONE
            alertButtonYes.text = "OK"

            if (isPlayStoreInstalled) {
                txtMessage.text =
                    "Google Play Store is installed but not connected."

                alertButtonYes.setOnClickListener {
                    show.dismiss()
                    openPlayStore(activity)
                }
            } else {
                txtMessage.text =
                    "Google Play Store is not installed on your device. The application does subscription using Google Play Store. Please use a device having Google Play Store and log into the account and then try to subscribe."

                alertButtonYes.setOnClickListener {
                    show.dismiss()
                }
            }



            alertButtonCancel.text = "Cancel existing plan"
            alertButtonYes.text = "Ok"
            alertButtonCancel.setTextSize(15f)
            alertButtonYes.setTextSize(15f)


            show.setCanceledOnTouchOutside(false)
        }

    }

    private fun openPlayStore(activity: Activity) {
        val playStorePackageName = "com.android.vending"
        val intent =
            Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$playStorePackageName"))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            activity.startActivity(intent)
        } catch (e: android.content.ActivityNotFoundException) {
            // If Google Play Store app is not available, open Play Store website
            intent.data =
                Uri.parse("https://play.google.com/store/apps/details?id=$playStorePackageName")
            activity.startActivity(intent)
        }
    }


    fun launchPurchaseFlow(productDetails: ProductDetails, activity: Activity) {
        try {
            assert(productDetails.subscriptionOfferDetails != null)
            val productDetailsParamsList = com.google.common.collect.ImmutableList.of(
                BillingFlowParams.ProductDetailsParams.newBuilder()
                    .setProductDetails(productDetails)
                    .setOfferToken(productDetails.subscriptionOfferDetails!![0].offerToken)
                    .build()
            )
            val billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)
                .build()
            billingClient.launchBillingFlow(activity, billingFlowParams)
        } catch (t: Throwable) {
            t.printStackTrace()
        }

    }


    @SuppressLint("SuspiciousIndentation")
    fun verifySubPurchase(purchases: Purchase) {
        val acknowledgePurchaseParams = AcknowledgePurchaseParams
            .newBuilder()
            .setPurchaseToken(purchases.purchaseToken)
            .build()
        billingClient.acknowledgePurchase(
            acknowledgePurchaseParams
        ) { billingResult: BillingResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                //user prefs to set premium
                /*Toast.makeText(application, "You are a premium user now", Toast.LENGTH_SHORT)
                    .show()*/

                // val productId = purchases.productId
                val purchaseList = mutableListOf<Purchase>()
                purchaseList.add(purchases)

                var activeEMail:String = ""
                if(application != null) {
                    activeEMail = Utils.readStringFromSharedPref(
                        application, Constants.ACTIVE_PLAYSTORE_EMAIL,
                        ""
                    ).toString()
                }


                var jsonobject: JSONObject = JSONObject(purchases.originalJson)
                var jsonOrderId: JSONObject = JSONObject(purchases.originalJson)
                /* val startdate = Utils.convertLongToDate(purchases.purchaseTime)
                 val monthlyPlanEndDate = Utils.convertLongToDatePlusOneMonth(purchases.purchaseTime)
                 val yearlyPlanEndDate = Utils.convertLongToDatePlusOneYear(purchases.purchaseTime)*/
                val startDate = Utils.convertLongToDateUTC(purchases.purchaseTime)
                val monthlyPlanEndDate =
                    Utils.convertLongToDatePlusOneMonthUTC(purchases.purchaseTime)
                val yearlyPlanEndDate =
                    Utils.convertLongToDatePlusOneYearUTC(purchases.purchaseTime)


                var saveSubscriptionDataReqVo: SaveSubscriptionDataReqVo =
                    SaveSubscriptionDataReqVo()
                saveSubscriptionDataReqVo.payment_email = activeEMail
                saveSubscriptionDataReqVo.customer_id = ""
                saveSubscriptionDataReqVo.subscription_id = jsonOrderId.optString("orderId")
                saveSubscriptionDataReqVo.subscription_type = "google_play"
                saveSubscriptionDataReqVo.package_name = jsonobject.optString("productId")
                saveSubscriptionDataReqVo.start_date = startDate
                saveSubscriptionDataReqVo.app_user_id = prefeUserId
                saveSubscriptionDataReqVo.token = jsonOrderId.optString("purchaseToken")
                if (subsciprionPlanType == "monthly") {
                    saveSubscriptionDataReqVo.end_date = monthlyPlanEndDate
                } else {
                    saveSubscriptionDataReqVo.end_date = yearlyPlanEndDate
                }
                saveSubscriptionData(saveSubscriptionDataReqVo, purchaseList)

                //Setting premium to 1
                // 1 - premium
                // 0 - no premium
                // prefs.setPremium(1)
            }
        }
//        Log.d(TAG, "Purchase Token: " + purchases.purchaseToken)
//        Log.d(TAG, "Purchase Time: " + purchases.purchaseTime)
//        Log.d(TAG, "Purchase OrderID: " + purchases.orderId)
    }

    private fun saveSubscriptionData(
        saveSubscriptionDataReqVo: SaveSubscriptionDataReqVo,
        purchasesList: MutableList<Purchase>
    ) {

        if(application != null) {
            val request = APIService.buildService(APIInterface::class.java)
            val call =
                request.savSubscriptionData(
                    saveSubscriptionDataReqVo,
                    "bearer " + Utils.readStringFromSharedPref(
                        application,
                        Constants.SHARED_PREF_TOKEN,
                        ""
                    )
                )

            try {
                call.enqueue(object : Callback<SaveSubscriptionDataResVo> {
                    override fun onResponse(
                        call: Call<SaveSubscriptionDataResVo>,
                        response: Response<SaveSubscriptionDataResVo>
                    ) {
                        if (response.body()?.statusCode == 1) {
                            Log.e("API", "API RESPONSE onResponse: " + response.body()?.message)
                            isSadeDataCall = true
                            Utils.writeStringToSharedPref(
                                application, Constants.IN_STATUS_FROM_SAVE_DATA,
                                response.body()?.subscription_status
                            )
                            Utils.writeStringToSharedPref(
                                application, Constants.TYPE,
                                response.body()?.type
                            )
                            if (response.body()?.subscription_status.equals("yes", true)) {
                                mListener?.onQueryPurchase(purchasesList)
                            }
                        } else {
                            Toast.makeText(
                                application,
                                response.body()!!.message,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<SaveSubscriptionDataResVo>, t: Throwable) {
                        Toast.makeText(application, "${t.message}", Toast.LENGTH_SHORT)
                            .show()
                    }
                })

            } catch (exception: Exception) {
                exception.printStackTrace()
            }
        }
    }


    override fun onPurchasesUpdated(billingResult: BillingResult, list: MutableList<Purchase>?) {
        when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK -> if (null != list) {
                for (purchase in list) {
                    //handlePurchase(purchase)
                    verifySubPurchase(purchase)
                }
                return
            } else Log.d("TAG", "Null Purchase List Returned from OK response!")
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED ->
                // querySubscriptionProductDetails()
                Log.e("log", "ITEM_ALREADY_OWNED")

            BillingClient.BillingResponseCode.DEVELOPER_ERROR ->
                Log.e("log", "DEVELOPER_ERROR")

            BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED ->
                Log.e("log", "FEATURE_NOT_SUPPORTED")

            else -> Log.e("log", "billingResult.debugMessage")

        }
    }

    companion object {

        private val TAG = "EMH " + InAppSubscription::class.java.simpleName

        @Volatile
        private var sInstance: InAppSubscription? = null
        private val handler = Handler(Looper.getMainLooper())

        // Standard boilerplate double check locking pattern for thread-safe singletons.
        @JvmStatic
        fun getInstance(
            application: Application,
            defaultScope: CoroutineScope,
        ) = sInstance ?: synchronized(this) {
            sInstance ?: InAppSubscription(
                application,
                defaultScope,
            )
                .also { sInstance = it }
        }

    }

    init {
        // initializeFlows()

//        establishConnection()
        //showProducts()

        /*lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                when (event) {
                    Lifecycle.Event.ON_RESUME -> establishConnection()
                    //Lifecycle.Event.ON_PAUSE ->
                    else -> {}
                }
            }
        })*/
    }

    private fun querySubscriptionProductDetails() {
        Log.d(TAG, "querySubscriptionProductDetails")
        val params = QueryProductDetailsParams.newBuilder()

        val productList: MutableList<QueryProductDetailsParams.Product> = arrayListOf()
        for (product in LIST_OF_SUBSCRIPTION_PRODUCTS) {
            productList.add(
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(product)
                    .setProductType(BillingClient.ProductType.SUBS)
                    .build()
            )
        }

        //LIST_OF_SUBSCRIPTION_PRODUCTS

        params.setProductList(productList).let { productDetailsParams ->
            billingClient.queryProductDetailsAsync(productDetailsParams.build(), this)
        }

    }

    /**
     * Query Google Play Billing for existing subscription purchases.
     *
     * New purchases will be provided to the PurchasesUpdatedListener.
     * You still need to check the Google Play Billing API to know when purchase tokens are removed.
     */
    fun querySubscriptionPurchases() {
        if (!billingClient.isReady) {
            Log.e(TAG, "querySubscriptionPurchases: BillingClient is not ready")
            billingClient.startConnection(this)
        }
        billingClient.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.SUBS)
                .build(), this
        )
    }

    //Returns the most recent purchase made by the user for each product, even if that purchase is expired, canceled, or consumed.
    fun queryPurchaseAsyncHistory() {
        val params = QueryPurchaseHistoryParams.newBuilder()
            .setProductType(BillingClient.ProductType.SUBS) // Specify the purchase type (e.g., subscriptions)
            .build()

        // Query purchase history
        billingClient.queryPurchaseHistoryAsync(params) { billingResult,
                                                          purchaseHistoryRecords ->
            if (purchaseHistoryRecords != null) {
                // Process the purchase history records
                var latestSubscription: PurchaseHistoryRecord? = null
                if (purchaseHistoryRecords.isNotEmpty()) {
                    // Find the most recent subscription
                    // val purchasesList: MutableList<Purchase> = mutableListOf()
                    mListener?.onQuryPurchaseHistoryasync(purchaseHistoryRecords)
                    latestSubscription = purchaseHistoryRecords.maxByOrNull { it.purchaseTime }

                }

                // Now you can use the latestSubscription to restore the user's subscription
                // ...
            } else {
                // Handle error cases
                // ...
            }
        }
    }

    override fun onProductDetailsResponse(
        billingResult: BillingResult,
        productDetailsList: MutableList<ProductDetails>
    ) {
        val response = BillingResponse(billingResult.responseCode)
        val debugMessage = billingResult.debugMessage
        when {
            response.isOk -> {
                processProductDetails(productDetailsList)
            }

            response.isTerribleFailure -> {
                // These response codes are not expected.
                Log.w(
                    TAG,
                    "onProductDetailsResponse - Unexpected error: ${response.code} $debugMessage"
                )
            }

            else -> {
                Log.e(TAG, "onProductDetailsResponse: ${response.code} $debugMessage")
            }

        }
    }

    private fun processProductDetails(productDetailsList: MutableList<ProductDetails>) {
        val expectedProductDetailsCount = LIST_OF_SUBSCRIPTION_PRODUCTS.size
        if (productDetailsList.isEmpty()) {
            Log.e(
                TAG, "processProductDetails: " +
                        "Expected ${expectedProductDetailsCount}, " +
                        "Found null ProductDetails. " +
                        "Check to see if the products you requested are correctly published " +
                        "in the Google Play Console."
            )
            Log.e("processProductDetails", "List is empty")
        } else {
//            postProductDetails(productDetailsList)
            Log.e("processProductDetails", "productDetailsList size:" + productDetailsList.size)
        }
    }

    override fun onQueryPurchasesResponse(
        billingResult: BillingResult,
        purchasesList: MutableList<Purchase>
    ) {
        val purchaseHistoryRecords: MutableList<PurchaseHistoryRecord> = mutableListOf()
        processPurchases(purchasesList)

        if (purchasesList.size == 0) {
            queryPurchaseAsyncHistory()
        } else {
            mListener?.onQueryPurchase(purchasesList)
            for (purchase in purchasesList) {
                Log.e("PURCHASE_DATA", "orderId: " + purchase.orderId)
            }
        }


    }

    private fun updateSubscriptionStatus(updateSubscriptionStatusReq: UpdateSubscriptionStatusReq) {
        val request = APIService.buildService(APIInterface::class.java)
        val call =
            request.updateSubscriptionStatus(
                updateSubscriptionStatusReq,
                "bearer " + Utils.readStringFromSharedPref(
                    activity,
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
                        Log.e(TAG, "onResponse: " + response.body()?.message)

                    } else {
                        Toast.makeText(
                            activity,
                            response.body()!!.message,
                            Toast.LENGTH_LONG
                        ).show()
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

    private fun processPurchases(purchasesList: List<Purchase>?) {
        Log.d(TAG, "processPurchases: ${purchasesList?.size} purchase(s)")
        //Send the details of purchases on our server
    }

    /*override fun onPurchaseHistoryResponse(
        p0: BillingResult,
        p1: MutableList<PurchaseHistoryRecord>?
    ) {
        Log.e("DDDDD", "onPurchaseHistoryResponse: ", )
    }*/
}
