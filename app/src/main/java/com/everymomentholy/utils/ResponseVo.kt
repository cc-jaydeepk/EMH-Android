package com.everymomentholy.utils

import com.everymomentholy.api.response.DataVo
import com.everymomentholy.api.response.ResponseVo
import com.google.gson.annotations.SerializedName

class ResponseVo {

    //var data: List<DataVo> = DataVo

   // var data: List<DataVo> = List<DataVo>

    var data: ArrayList<DataVo> = ArrayList()

    @SerializedName("message")
    // var message: String = ""
     var msg: String = ""
}