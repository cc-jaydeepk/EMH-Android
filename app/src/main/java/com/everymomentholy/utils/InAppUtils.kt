package com.everymomentholy.utils

import android.app.Activity
import android.app.Application
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LifecycleObserver
import com.android.billingclient.api.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Math.min
import java.util.*

private const val RECONNECT_TIMER_START_MILLISECONDS = 1L * 1000L
private const val RECONNECT_TIMER_MAX_TIME_MILLISECONDS = 1000L * 60L * 15L // 15 minutes
private const val SKU_DETAILS_REQUERY_TIME = 1000L * 60L * 60L * 4L // 4 hours

class InAppUtils private constructor(
    application: Application,
    private val defaultScope: CoroutineScope,
) :
    LifecycleObserver, PurchasesUpdatedListener, BillingClientStateListener {

    private val billingClient: BillingClient = BillingClient.newBuilder(application)
        .setListener(this)
        .enablePendingPurchases()
        .build()

    // how long before the data source tries to reconnect to Google play
    private var reconnectMilliseconds = RECONNECT_TIMER_START_MILLISECONDS

    fun initiatePurchaseFlow(
        activity: Activity?,
        price: String
    ) {

        defaultScope.launch {
            Log.e(TAG, "inside purchase")
            val skuDetails = querySkuDetails(price)
            if (skuDetails != null) {
                val flowParams = BillingFlowParams.newBuilder()
                    .setSkuDetails(skuDetails)
                    .build()
                val br = billingClient.launchBillingFlow(
                    activity!!,
                    flowParams
                )

                if (br.responseCode == BillingClient.BillingResponseCode.OK) {
                    Log.e(TAG, "Billing success: + " + br.debugMessage)
                } else {
                    Log.e(TAG, "Billing failed: + " + br.debugMessage)
                }
            }
        }
    }


    private suspend fun querySkuDetails(price: String): SkuDetails? {
        Log.e(TAG, "inside querySkuDetails")
        val skuList = ArrayList<String>()
        if (productListPriceMap[price] != null) {
            skuList.add(productListPriceMap[price]!!)
            Log.e(TAG, "product id = " + productListPriceMap[price]!!)
            val params = SkuDetailsParams.newBuilder()
            params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)

            // leverage querySkuDetails Kotlin extension function
            val skuDetailsResult = withContext(Dispatchers.IO) {
                billingClient.querySkuDetails(params.build())
            }
            Log.e(TAG, "skuDetails " + skuDetailsResult.skuDetailsList?.size)
            if (skuDetailsResult.skuDetailsList?.size!! > 0)
                return skuDetailsResult.skuDetailsList?.get(0)!!
            else
                return null
        } else
            return null
        // Process the result.
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, list: MutableList<Purchase>?) {
        when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK -> if (null != list) {
                processPurchaseList(list, null)
                return
            } else Log.d(TAG, "Null Purchase List Returned from OK response!")
            BillingClient.BillingResponseCode.USER_CANCELED -> Log.i(
                TAG,
                "onPurchasesUpdated: User canceled the purchase"
            )
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> Log.i(
                TAG,
                "onPurchasesUpdated: The user already owns this item"
            )
            BillingClient.BillingResponseCode.DEVELOPER_ERROR -> Log.e(
                TAG,
                "onPurchasesUpdated: Developer error means that Google Play " +
                        "does not recognize the configuration. If you are just getting started, " +
                        "make sure you have configured the application correctly in the " +
                        "Google Play Console. The SKU product ID must match and the APK you " +
                        "are using must be signed with release keys."
            )
            else -> Log.d(
                TAG,
                "BillingResult [" + billingResult.responseCode + "]: " + billingResult.debugMessage
            )
        }
    }

    override fun onBillingServiceDisconnected() {
        retryBillingServiceConnectionWithExponentialBackoff()
    }

    /**
     * Retries the billing service connection with exponential backoff, maxing out at the time
     * specified by RECONNECT_TIMER_MAX_TIME_MILLISECONDS.
     */
    private fun retryBillingServiceConnectionWithExponentialBackoff() {
        handler.postDelayed(
            { billingClient.startConnection(this@InAppUtils) },
            reconnectMilliseconds
        )
        reconnectMilliseconds = min(
            reconnectMilliseconds * 2,
            RECONNECT_TIMER_MAX_TIME_MILLISECONDS
        )
    }

    override fun onBillingSetupFinished(billingResult: BillingResult) {
        val responseCode = billingResult.responseCode
        val debugMessage = billingResult.debugMessage
        Log.d(TAG, "onBillingSetupFinished: $responseCode $debugMessage")
        when (responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                // The billing client is ready. You can query purchases here.
                // This doesn't mean that your app is set up correctly in the console -- it just
                // means that you have a connection to the Billing service.
                reconnectMilliseconds = RECONNECT_TIMER_START_MILLISECONDS
                /*defaultScope.launch {
                   // querySkuDetails()
                    // refreshPurchases()
                }*/
            }
            else -> retryBillingServiceConnectionWithExponentialBackoff()
        }
    }

    private fun processPurchaseList(purchases: List<Purchase>?, skusToUpdate: List<String>?) {
        if (null != purchases) {
            for (purchase in purchases) {
                defaultScope.launch {
                    if (!purchase.isAcknowledged) {
                        // acknowledge everything --- new purchases are ones not yet acknowledged
                        val billingResult = billingClient.acknowledgePurchase(
                            AcknowledgePurchaseParams.newBuilder()
                                .setPurchaseToken(purchase.purchaseToken)
                                .build()
                        )
                        if (billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
                            Log.e(TAG, "Error acknowledging purchase: ${purchase.skus.toString()}")
                        } else {
                            // purchase acknowledged
                            /*for (sku in purchase.skus) {
                                setSkuState(sku, SkuState.SKU_STATE_PURCHASED_AND_ACKNOWLEDGED)
                            }*/
                        }
                    }
                }
            }
        }
    }

    companion object {

        private val TAG = "EMH " + InAppUtils::class.java.simpleName

        @Volatile
        private var sInstance: InAppUtils? = null
        private val handler = Handler(Looper.getMainLooper())

        // Standard boilerplate double check locking pattern for thread-safe singletons.
        @JvmStatic
        fun getInstance(
            application: Application,
            defaultScope: CoroutineScope,
        ) = sInstance ?: synchronized(this) {
            sInstance ?: InAppUtils(
                application,
                defaultScope,
            )
                .also { sInstance = it }
        }

        val productListPriceMap: HashMap<String, String> = hashMapOf(
            "0.99" to "emh99",
            "1.99" to "emh199",
            "2.99" to "emh299",
            "3.99" to "emh399",
            "4.99" to "emh499",
            "5.99" to "emh599",
            "6.99" to "emh699",
            "7.99" to "emh799",
            "8.99" to "emh899",
            "9.99" to "emh999",
            "10.99" to "emh1099",
            "11.99" to "emh1199",
            "12.99" to "emh1299",
            "13.99" to "emh1399",
            "14.99" to "emh1499",
            "15.99" to "emh1599",
            "16.99" to "emh1699",
            "17.99" to "emh1799",
            "18.99" to "emh1899",
            "19.99" to "emh1999",
            "20.99" to "emh2099",
            "21.99" to "emh2199",
            "22.99" to "emh229",
            "23.99" to "emh239",
            "24.99" to "emh249",
            "25.99" to "emh259",
            "26.99" to "emh269",
            "27.99" to "emh279",
            "28.99" to "emh289",
            "29.99" to "emh29"
        )
    }

    init {
        // initializeFlows()
        billingClient.startConnection(this)
    }
}