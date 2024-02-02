package com.everymomentholy.utils

//import com.android.billingclient.api.*

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.lifecycle.*
import com.android.billingclient.api.*
import com.google.common.collect.ImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.HashMap


class InAppSubscription private constructor(
    application: Application,
    private val defaultScope: CoroutineScope,
) : LifecycleObserver, PurchasesUpdatedListener {


    private lateinit var lifecycle: Lifecycle

    /*lifecycle.addObserver(object: LifecycleEventObserver {
        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            when (event) {
                Lifecycle.Event.ON_RESUME -> TODO()
                Lifecycle.Event.ON_PAUSE -> TODO()
                else -> { }
            }
        }
    })*/


    lateinit var activity: Activity

    val billingClient: BillingClient = BillingClient.newBuilder(activity)
        .setListener(this)
        .enablePendingPurchases()
        .build()


    fun establishConnection() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(@NonNull billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    showProducts()
                }
            }

            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                establishConnection()
            }
        })
    }

    @SuppressLint("SetTextI18n")
    fun showProducts() {
        val queryProductDetailsParams =
            QueryProductDetailsParams.newBuilder()
                .setProductList(
                    ImmutableList.of(
                        QueryProductDetailsParams.Product.newBuilder()
                            .setProductId("emh-monthly-plan") // set product id
                            .setProductType(BillingClient.ProductType.SUBS)
                            .build()
                    )
                )
                .build()

        billingClient.queryProductDetailsAsync(queryProductDetailsParams) { billingResult,
                                                                            productDetailsList ->
            // check billingResult
            // process returned productDetailsList

            for (productDetails in productDetailsList) {
                val productDetailsParamsList = listOf(
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        // retrieve a value for "productDetails" by calling queryProductDetailsAsync()
                        .setProductDetails(productDetails)
                        // For One-time product, "setOfferToken" method shouldn't be called.
                        // For subscriptions, to get an offer token, call ProductDetails.subscriptionOfferDetails()
                        // for a list of offers that are available to the user
                        //.setOfferToken(selectedOfferToken)
                        .build()
                )

                val billingFlowParams = BillingFlowParams.newBuilder()
                    .setProductDetailsParamsList(productDetailsParamsList)
                    .build()

// Launch the billing flow
                billingClient.launchBillingFlow(activity, billingFlowParams)
            }
        }
    }

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
                Toast.makeText(activity, "You are a premium user now", Toast.LENGTH_SHORT)
                    .show()
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
                Toast.makeText(
                    activity,
                    "ITEM_ALREADY_OWNED",
                    Toast.LENGTH_LONG
                ).show()

            BillingClient.BillingResponseCode.DEVELOPER_ERROR -> Log.e(
                "TAG",
                "onPurchasesUpdated: Developer error means that Google Play " +
                        "does not recognize the configuration. If you are just getting started, " +
                        "make sure you have configured the application correctly in the " +
                        "Google Play Console. The SKU product ID must match and the APK you " +
                        "are using must be signed with release keys."
            )
            else -> Log.d(
                "TAG",
                "BillingResult [" + billingResult.responseCode + "]: " + billingResult.debugMessage
            )
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


        lifecycle.addObserver(object: LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                when (event) {
                    Lifecycle.Event.ON_RESUME -> establishConnection()
                    //Lifecycle.Event.ON_PAUSE ->
                    else -> { }
                }
            }
        })
    }

    fun resume(){

    }



}