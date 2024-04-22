package com.everymomentholy.interfaces

import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchaseHistoryRecord

interface QueryPurchasesListner {

    //    fun onQueryPurchase(orderId: String, purchaseToken: String, purchaseTime: Long)
    fun onQueryPurchase(purchasesList: MutableList<Purchase>)

    fun onQuryPurchaseHistoryasync(purchaseHistoryRecords: MutableList<PurchaseHistoryRecord>)
    /*fun onQueryPurchase(
        purchasesList: MutableList<Purchase>,
        purchaseHistoryRecords: MutableList<PurchaseHistoryRecord>
    )*/
}