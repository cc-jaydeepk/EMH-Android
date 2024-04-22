package com.everymomentholy.api.response

class OrderAndSubResponseVo {
    //var response: ArrayList<OrderHistoryVo> = ArrayList()
    var status: Boolean = false
    var statusCode: Int = 0
    var response: ArrayList<OrderAndSubHistoryVo> = ArrayList()
    var message: String = ""
}