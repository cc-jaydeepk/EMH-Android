package com.everymomentholy.api.response

import java.io.Serializable

class LiturgiesResponseVo : Serializable {
    var message: String = ""

    // var data: ArrayList<LiturgiesDataVo> = ArrayList()
    var data: ArrayList<GetLiturgiesDataVo> = ArrayList()
}