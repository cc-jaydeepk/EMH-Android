package com.everymomentholy.api.response

import com.google.gson.annotations.SerializedName

open class ResponseVo {
    //LogIn API Response
    var email: String = ""
    var firstName: String = ""
    var lastName: String = ""
    var token: String = ""
    var userId: Int = 0
    var userProfilePic: String = ""

    //dailyLiturgyQuote API Response
    var quote: String = ""
    var parentLiturgy: String = ""
    var date: String = ""
    //var message: String = ""


    //getSetting API Response
    var featured_liturgy_message: String = ""
    var home_page_liturgy_image: String = ""
    var liturgy_expire_hours: String = ""
    var max_volume_price: String = ""

    //getLiturgies API Response
    var message: String = ""
    var data: ArrayList<DataVo> = ArrayList()

    //aboutus API Response
    var description: String = ""

    //termsCondition API Response
   // @SerializedName("description")
    //var termsDescription: String = ""

    //getBooks API Response
   // var message: String = ""
   // var data: ArrayList<DataVo> = ArrayList()

    //notificationList API Response
     //var data: List<DataVo> = DataVo

    //@SerializedName("message")
   // var notificationmessage: String = ""

    //var data: ArrayList<DataVo> = ArrayList()

}