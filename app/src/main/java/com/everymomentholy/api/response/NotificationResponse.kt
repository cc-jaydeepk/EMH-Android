package com.everymomentholy.api.response

import com.google.gson.annotations.SerializedName

class NotificationResponse {

    var data: ArrayList<NotificationDataVo> = ArrayList()

    @SerializedName("message")
    // var message: String = ""
    var msg: String = ""
}