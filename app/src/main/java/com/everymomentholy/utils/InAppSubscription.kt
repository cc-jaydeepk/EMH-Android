package com.everymomentholy.utils

//import com.android.billingclient.api.*

import android.app.Activity
import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.lifecycle.MutableLiveData
import com.android.billingclient.api.*
import com.google.common.collect.ImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class InAppSubscription private constructor(application: Application) : BillingClientStateListener {

    // private val billingClient: BillingClient? = null

    lateinit var activity: Activity

    private val purchasesUpdatedListener =
        PurchasesUpdatedListener { billingResult, purchases ->
            // To be implemented in a later section.
        }

    //Initialize a BillingClient
    private var billingClient = BillingClient.newBuilder(application)
        .setListener(purchasesUpdatedListener)
        .enablePendingPurchases()
        .build()


    //start the connection after initializing the billing client
    // establishConnection()


    override fun onBillingServiceDisconnected() {
        // Try to restart the connection on the next request to
        // Google Play by calling the startConnection() method.
    }

    override fun onBillingSetupFinished(billingResult: BillingResult) {
        // TODO("Not yet implemented")
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            // The BillingClient is ready. You can query purchases here.
        }
    }

    fun establishConnection() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
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

    /* val queryProductDetailsParams =
         QueryProductDetailsParams.newBuilder()
             .setProductList(
                 ImmutableList.of(
                     QueryProductDetailsParams.Product.newBuilder()
                         .setProductId("product_id_example")
                         .setProductType(BillingClient.ProductType.SUBS)
                         .build()
                 )
             )
             .build()

     billingClient.queryProductDetailsAsync(queryProductDetailsParams)
     {
         billingResult,
         productDetailsList ->
         // check billingResult
         // process returned productDetailsList
     }*/

    //Show the products and make them available to Purchase
    fun showProducts() {
        val queryProductDetailsParams =
            QueryProductDetailsParams.newBuilder()
                .setProductList(
                    ImmutableList.of(
                        QueryProductDetailsParams.Product.newBuilder()
                            .setProductId("product_id_example")
                            .setProductType(BillingClient.ProductType.SUBS)
                            .build()
                    )
                )
                .build()

        billingClient.queryProductDetailsAsync(queryProductDetailsParams)
        { billingResult,
          productDetailsList ->
            // check billingResult
            // process returned productDetailsList

        }
    }

    fun launchPurchaseFlow(productDetails: ProductDetails) {
        /*assert(productDetails.subscriptionOfferDetails != null)
        val productDetailsParamsList = ImmutableList.of(
            ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .setOfferToken(productDetails.subscriptionOfferDetails!![0].offerToken)
                .build()
        )
        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()
        billingClient.launchBillingFlow(activity, billingFlowParams)*/

        val productDetailsParamsList = listOf(
            productDetails.getSubscriptionOfferDetails()?.get(0)?.let {
                BillingFlowParams.ProductDetailsParams.newBuilder()
                    // retrieve a value for "productDetails" by calling queryProductDetailsAsync()
                    .setProductDetails(productDetails)
                    // For One-time product, "setOfferToken" method shouldn't be called.
                    // For subscriptions, to get an offer token, call ProductDetails.subscriptionOfferDetails()
                    // for a list of offers that are available to the user
                    .setOfferToken(it.getOfferToken())
                    .build()
            }
        )

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()

// Launch the billing flow
        val billingResult = billingClient.launchBillingFlow(activity, billingFlowParams)

    }

    //Process the purchases and Verify Payment
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
                //prefs.setPremium(1)
            }
        }
        /*Log.d(TAG, "Purchase Token: " + purchases.purchaseToken)
        Log.d(TAG, "Purchase Time: " + purchases.purchaseTime)
        Log.d(TAG, "Purchase OrderID: " + purchases.orderId)*/
    }

    //Handling pending transactions
   /* protected fun onResume() {
        super.onResume()
        billingClient.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.SUBS).build()
        ) { billingResult: BillingResult, list: List<Purchase> ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                for (purchase in list) {
                    if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged) {
                        verifySubPurchase(purchase)
                    }
                }
            }
        }
    }*/

    //use this below code inside your splash screen Activity
    fun checkSubscription() {
        billingClient = BillingClient.newBuilder(activity).enablePendingPurchases()
            .setListener { billingResult: BillingResult?, list: List<Purchase?>? -> }
            .build()
        val finalBillingClient = billingClient
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {}
            override fun onBillingSetupFinished(@NonNull billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    finalBillingClient.queryPurchasesAsync(
                        QueryPurchasesParams.newBuilder()
                            .setProductType(BillingClient.ProductType.SUBS).build()
                    ) { billingResult1: BillingResult, list: List<Purchase> ->
                        if (billingResult1.responseCode == BillingClient.BillingResponseCode.OK) {
                            Log.d("testOffer", list.size.toString() + " size")
                            if (list.size > 0) {
                               // prefs.setPremium(1) // set 1 to activate premium feature
                                var i = 0
                                for (purchase in list) {
                                    //Here you can manage each product, if you have multiple subscription
                                    Log.d(
                                        "testOffer",
                                        purchase.originalJson
                                    ) // Get to see the order information
                                    Log.d("testOffer", " index$i")
                                    i++
                                }
                            } else {
                               // prefs.setPremium(0) // set 0 to de-activate premium feature
                            }
                        }
                    }
                }
            }
        })
    }

    /*suspend fun processPurchases() {
        val productList = ArrayList<String>()
        productList.add(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId("product_id_example")
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        )
        val params = QueryProductDetailsParams.newBuilder()
        params.setProductList(productList)

        // leverage queryProductDetails Kotlin extension function
        val productDetailsResult = withContext(Dispatchers.IO) {
            billingClient.queryProductDetails(params.build())
        }

        // Process the result.
    }*/

    /*billingClient.startConnection(object : BillingClientStateListener {
        override fun onBillingSetupFinished(billingResult: BillingResult) {
            if (billingResult.responseCode ==  BillingClient.BillingResponseCode.OK) {
                // The BillingClient is ready. You can query purchases here.
            }
        }
        override fun onBillingServiceDisconnected() {
            // Try to restart the connection on the next request to
            // Google Play by calling the startConnection() method.
        }
    })*/

    suspend fun onPurchasesUpdated(billingResult: BillingResult, purchases: List<Purchase>?) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                handlePurchase(purchase)
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.
        } else {
            // Handle any other error codes.
        }
    }

    // Establish a connection to Google Play.
    fun startBillingConnection(billingConnectionState: MutableLiveData<Boolean>) {

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    // Timber.tag("BILLINGR").d("Billing response OK")
                    // The BillingClient is ready. You can query purchases and product details here
                    queryPurchases()// we talk about this in the next paragraph
                } else {


                    // Timber.tag("BILLINGR").e(billingResult.debugMessage)
                }
            }

            override fun onBillingServiceDisconnected() {

                // Timber.tag("BILLINGR").d("Billing connection disconnected")
                startBillingConnection(billingConnectionState)
            }
        })
    }

    fun queryPurchases() {
        if (!billingClient.isReady) {

            // Timber.tag("BILLINGR").e("queryPurchases: BillingClient is not ready")
        }

        // QUERY FOR EXISTING SUBSCRIPTION PRODUCTS THAT HAVE BEEN PURCHASED
        billingClient.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.SUBS).build()
        ) { billingResult, purchaseList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                if (!purchaseList.isNullOrEmpty()) {
                   // _purchases.value = purchaseList
                } else {
                    //_purchases.value = emptyList()
                }

            } else {
                //Timber.tag("BILLINGR").e(billingResult.debugMessage)
            }
        }
    }

    //val purchaseHistoryResult = billingClient.queryPurchaseHistory(params.build())


    suspend fun handlePurchase(purchase: Purchase) {
        // Purchase retrieved from BillingClient#queryPurchasesAsync or your PurchasesUpdatedListener.
        // val purchase: Purchase = ...;

        // Verify the purchase.
        // Ensure entitlement was not already granted for this purchaseToken.
        // Grant entitlement to the user.

        val consumeParams =
            ConsumeParams.newBuilder()
                .setPurchaseToken(purchase.getPurchaseToken())
                .build()
        val consumeResult = withContext(Dispatchers.IO) {
           // billingClient.consumeAsync(consumeParams)
        }
    }

    /*val client: BillingClient = ...
    val acknowledgePurchaseResponseListener: AcknowledgePurchaseResponseListener = ...

    suspend fun handlePurchase() {
        if (purchase.purchaseState === Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                val ackPurchaseResult = withContext(Dispatchers.IO) {
                    client.acknowledgePurchase(acknowledgePurchaseParams.build())
                }
            }
        }
    }*/

}