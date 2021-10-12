package com.everymomentholy.utils

import com.everymomentholy.api.response.LiturgiesDataVo
import com.google.gson.annotations.SerializedName

class ResponseVo {

    //var data: List<DataVo> = DataVo

   // var data: List<DataVo> = List<DataVo>

    var data: ArrayList<LiturgiesDataVo> = ArrayList()

    @SerializedName("message")
    // var message: String = ""
     var msg: String = ""
}