package com.everymomentholy.api.response

class PastPurchaseHistoryResponseVo {
    var status: Boolean = true

    var statusCode: Int = 1

    //var response: LiturgiesResponseVo = LiturgiesResponseVo()
    var response: PurchaseResponseVo = PurchaseResponseVo()
    var message: String = ""
}