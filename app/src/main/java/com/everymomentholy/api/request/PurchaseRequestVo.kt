package com.everymomentholy.api.request

import com.everymomentholy.utils.ProductTypes

class PurchaseRequestVo(

    var userId: Int,
    var bookId: Int,
    var liturgyId: Int,
    var amount: String,
    val deviceId: String,
    val volumeId: Int,
    val productType: ProductTypes
)