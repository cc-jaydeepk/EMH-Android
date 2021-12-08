package com.everymomentholy.interfaces

import com.everymomentholy.api.request.PurchaseRequestVo

interface OnInAppPurchaseListener {

    fun onPurchaseComplete(purchaseRequestVo: PurchaseRequestVo)
}