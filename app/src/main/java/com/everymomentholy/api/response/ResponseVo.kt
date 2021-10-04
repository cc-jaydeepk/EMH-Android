package com.everymomentholy.api.response

import com.google.gson.annotations.SerializedName

open class ResponseVo {

    //dailyLiturgyQuote API Response
    var quote: String = ""

    var parentLiturgy: String = ""

    var date: String = ""

    var message: String = ""


    //getSetting API Response
    var featured_liturgy_message: String = ""

    var home_page_liturgy_image: String = ""

    var liturgy_expire_hours: String = ""

    var max_volume_price: String = ""


    //notificationList API Response
     //var data: List<DataVo> = DataVo

    //@SerializedName("message")
   // var notificationmessage: String = ""

    //var data: ArrayList<DataVo> = ArrayList()

}