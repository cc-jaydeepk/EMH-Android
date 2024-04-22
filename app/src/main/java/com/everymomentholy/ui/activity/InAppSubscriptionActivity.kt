package com.everymomentholy.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingFlowParams.ProductDetailsParams
import com.everymomentholy.R
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.collect.ImmutableList


class InAppSubscriptionActivity : AppCompatActivity() {
    //https://droidrocks.com/how-to-integrate-google-play-in-app-purchase-billing-library/

    private lateinit var queryProductDetailsParams: QueryProductDetailsParams
    lateinit var btnBuy: Button
    lateinit var linearMonthly: LinearLayout
    lateinit var linearYearly: LinearLayout

    //private lateinit var lifecycle: Lifecycle

    private lateinit var billingClient: BillingClient
    lateinit var planType: String
    var response: String? = null
    var des: String? = null
    var sku: String? = null
    var isSuccess = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_in_app_subscription)

        btnBuy = findViewById(R.id.btnBuy)
        linearMonthly = findViewById(R.id.linearMonthly)
        linearYearly = findViewById(R.id.linearYearly)

        billingClient = BillingClient.newBuilder(this)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases()
            .build()
        //start the connection after initializing the billing client
        //start the connection after initializing the billing client
        // establishConnection()

        linearMonthly.setOnClickListener {
            planType = "monthly"
        }

        linearYearly.setOnClickListener {
            planType = "Yearly"
        }

        btnBuy.setOnClickListener {
            // establishConnection()
            showProducts(planType)
            /*val inAppSubscription = InAppSubscription.getInstance(application, GlobalScope)
            inAppSubscription.showProducts()*/
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
                Toast.makeText(
                    this@InAppSubscriptionActivity,
                    "Service Disconnected",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    @SuppressLint("SetTextI18n")
    fun showProducts(planType: String) {
        try {
            var productDetailsM: ProductDetails? = null

            if (planType == "monthly") {
                queryProductDetailsParams =
                    QueryProductDetailsParams.newBuilder()
                        .setProductList(
                            ImmutableList.of(
                                QueryProductDetailsParams.Product.newBuilder()
                                    .setProductId("emh_monthly_plan") // set product id
                                    .setProductType(BillingClient.ProductType.SUBS)
                                    .build(),
                            )
                        )
                        .build()
            } else {
                queryProductDetailsParams =
                    QueryProductDetailsParams.newBuilder()
                        .setProductList(
                            ImmutableList.of(
                                QueryProductDetailsParams.Product.newBuilder()
                                    .setProductId("emh_yearly_plan") // set product id
                                    .setProductType(BillingClient.ProductType.SUBS)
                                    .build(),
                            )
                        )
                        .build()
            }

            /*val queryProductDetailsParams =
                QueryProductDetailsParams.newBuilder()
                    .setProductList(
                        ImmutableList.of(
                            QueryProductDetailsParams.Product.newBuilder()
                                .setProductId("emh_monthly_plan") // set product id
                                .setProductType(BillingClient.ProductType.SUBS)
                                .build(),
                            QueryProductDetailsParams.Product.newBuilder()
                                .setProductId("emh_yearly_plan") // set product id
                                .setProductType(BillingClient.ProductType.SUBS)
                                .build()
                        )
                    )
                    .build()*/
            var productDetailsParamsList = ArrayList<ProductDetailsParams>()
            billingClient.queryProductDetailsAsync(queryProductDetailsParams) { billingResult,
                                                                                productDetailsList ->
                // check billingResult
                // process returned productDetailsList

                for (productDetails in productDetailsList) {
                    productDetailsM = productDetails
                    /*productDetailsParamsList = listOf(
                        BillingFlowParams.ProductDetailsParams.newBuilder()
                            .setProductDetails(productDetails)
                            .setOfferToken(productDetails.subscriptionOfferDetails!![0].offerToken)
                            .build()
                    ) as ArrayList<ProductDetailsParams>*/

                }
                /*val billingFlowParams = BillingFlowParams.newBuilder()
                    .setProductDetailsParamsList(productDetailsParamsList)
                    .build()

// Launch the billing flow
                billingClient.launchBillingFlow(this, billingFlowParams)*/
                productDetailsM?.let { launchPurchaseFlow1(it) }
            }
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }

    fun launchPurchaseFlow1(productDetails: ProductDetails) {
        assert(productDetails.subscriptionOfferDetails != null)
        val productDetailsParamsList = com.google.common.collect.ImmutableList.of(
            ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .setOfferToken(productDetails.subscriptionOfferDetails!![0].offerToken)
                .build()
        )
        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()
        billingClient.launchBillingFlow(this, billingFlowParams)
    }

    val purchasesUpdatedListener =
        PurchasesUpdatedListener { billingResult, purchases ->
            // To be implemented in a later section.
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                for (purchase in purchases) {
                    //handlePurchase(purchase)
                    verifySubPurchase(purchase)
                }
            } else if (billingResult.responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
                Toast.makeText(
                    this,
                    "ITEM_ALREADY_OWNED",
                    Toast.LENGTH_LONG
                ).show()
            } else if (billingResult.responseCode == BillingClient.BillingResponseCode.DEVELOPER_ERROR) {
                Toast.makeText(
                    this,
                    "DEVELOPER_ERROR",
                    Toast.LENGTH_LONG
                ).show()
            } else if (billingResult.responseCode == BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED) {
                Toast.makeText(
                    this,
                    "FEATURE_NOT_SUPPORTED",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                    this,
                    "" + billingResult.debugMessage,
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    private fun handlePurchase(purchase: Purchase) {
        val consumer = ConsumeParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()

        val listner = ConsumeResponseListener { billingResult, s ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {

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


    override fun onResume() {
        super.onResume()
        establishConnection()
    }

    /*companion object {

        private lateinit var billingClient: BillingClient
        lateinit var activity: Activity

        @SuppressLint("SetTextI18n")
        fun showProductsNew() {
            billingClient = BillingClient.newBuilder(activity)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build()
            try {
                var productDetailsM: ProductDetails? = null
                val queryProductDetailsParams =
                    QueryProductDetailsParams.newBuilder()
                        .setProductList(
                            ImmutableList.of(
                                QueryProductDetailsParams.Product.newBuilder()
                                    .setProductId("emh_monthly_plan") // set product id
                                    .setProductType(BillingClient.ProductType.SUBS)
                                    .build()
                            )
                        )
                        .build()
                var productDetailsParamsList = ArrayList<ProductDetailsParams>()
                billingClient.queryProductDetailsAsync(queryProductDetailsParams) { billingResult,
                                                                                    productDetailsList ->
                    // check billingResult
                    // process returned productDetailsList

                    for (productDetails in productDetailsList) {
                        productDetailsM = productDetails
                        *//*productDetailsParamsList = listOf(
                            BillingFlowParams.ProductDetailsParams.newBuilder()
                                .setProductDetails(productDetails)
                                .setOfferToken(productDetails.subscriptionOfferDetails!![0].offerToken)
                                .build()
                        ) as ArrayList<ProductDetailsParams>*//*

                    }
                    *//*val billingFlowParams = BillingFlowParams.newBuilder()
                        .setProductDetailsParamsList(productDetailsParamsList)
                        .build()

    // Launch the billing flow
                    billingClient.launchBillingFlow(this, billingFlowParams)*//*
                    productDetailsM?.let { launchPurchaseFlow1(it) }
                }
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }

        fun launchPurchaseFlow1(productDetails: ProductDetails) {
            assert(productDetails.subscriptionOfferDetails != null)
            val productDetailsParamsList = com.google.common.collect.ImmutableList.of(
                ProductDetailsParams.newBuilder()
                    .setProductDetails(productDetails)
                    .setOfferToken(productDetails.subscriptionOfferDetails!![0].offerToken)
                    .build()
            )
            val billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)
                .build()
            billingClient.launchBillingFlow(activity, billingFlowParams)
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

        val purchasesUpdatedListener =
            PurchasesUpdatedListener { billingResult, purchases ->
                // To be implemented in a later section.
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                    for (purchase in purchases) {
                        //handlePurchase(purchase)
                        verifySubPurchase(purchase)
                    }
                } else if (billingResult.responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
                    Toast.makeText(
                        activity,
                        "ITEM_ALREADY_OWNED",
                        Toast.LENGTH_LONG
                    ).show()
                } else if (billingResult.responseCode == BillingClient.BillingResponseCode.DEVELOPER_ERROR) {
                    Toast.makeText(
                        activity,
                        "DEVELOPER_ERROR",
                        Toast.LENGTH_LONG
                    ).show()
                } else if (billingResult.responseCode == BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED) {
                    Toast.makeText(
                        activity,
                        "FEATURE_NOT_SUPPORTED",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        activity,
                        "" + billingResult.debugMessage,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }*/


}