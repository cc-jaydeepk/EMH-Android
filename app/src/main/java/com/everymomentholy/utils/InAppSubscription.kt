package com.everymomentholy.utils

import android.app.Application
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PurchasesUpdatedListener

class InAppSubscription private constructor(application: Application): BillingClientStateListener{

    private val purchasesUpdatedListener =
        PurchasesUpdatedListener { billingResult, purchases ->
            // To be implemented in a later section.
        }

    //Initialize a BillingClient
    private var billingClient = BillingClient.newBuilder(application)
        .setListener(purchasesUpdatedListener)
        .enablePendingPurchases()
        .build()

    override fun onBillingServiceDisconnected() {
        TODO("Not yet implemented")
    }

    override fun onBillingSetupFinished(billingResult: BillingResult) {
       // TODO("Not yet implemented")
        if (billingResult.responseCode ==  BillingClient.BillingResponseCode.OK) {
            // The BillingClient is ready. You can query purchases here.
        }
    }

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
}