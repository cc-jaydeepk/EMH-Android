package com.everymomentholy.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import com.android.billingclient.api.*
import com.everymomentholy.R
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.collect.ImmutableList


class InAppSubscriptionActivity : AppCompatActivity() {
    //https://droidrocks.com/how-to-integrate-google-play-in-app-purchase-billing-library/

    lateinit var btnBuy: Button

    private lateinit var billingClient: BillingClient
    var response: String? = null
    var des: String? = null
    var sku: String? = null
    var isSuccess = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_in_app_subscription)

        btnBuy = findViewById(R.id.btnBuy)

        billingClient = BillingClient.newBuilder(this)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases()
            .build()
        //start the connection after initializing the billing client
        //start the connection after initializing the billing client
        establishConnection()

        btnBuy.setOnClickListener {
            establishConnection()
        }

        /*billingClient = BillingClient.newBuilder(this)
            .enablePendingPurchases()
            .setListener(
                object : PurchasesUpdatedListener {
                    override fun onPurchasesUpdated(billingResult: BillingResult, list: MutableList<Purchase>?) {
                        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && list != null) {
                            for (purchase in list) {
                                verifySubPurchase(purchase!!)
                            }
                        }
                    }
                }
            ).build()*/
        //start the connection after initializing the billing client
        //start the connection after initializing the billing client

    }



    fun establishConnection() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(@NonNull billingResult: BillingResult) {
                /*if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    showProducts()
                }*/
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
                billingClient.launchBillingFlow(this, billingFlowParams)
            }
        }
    }

    val purchasesUpdatedListener =
        PurchasesUpdatedListener { billingResult, purchases ->
            // To be implemented in a later section.
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                for (purchase in purchases) {
                    //handlePurchase(purchase)
                    verifySubPurchase(purchase)
                }
            } else {
                Toast.makeText(
                    this,
                    "" + billingResult.debugMessage,
                    Toast.LENGTH_LONG
                ).show()
            }
        }


    /*fun showProducts() {
        val queryProductDetailsParams =
            QueryProductDetailsParams.newBuilder()
                .setProductList(
                    ImmutableList.of(
                        QueryProductDetailsParams.Product.newBuilder()
                            .setProductId("product_id_example") // set product id
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
                billingClient.launchBillingFlow(this, billingFlowParams)
            }
        }
    }*/

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
                Toast.makeText(this, "You are a premium user now", Toast.LENGTH_SHORT)
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


}