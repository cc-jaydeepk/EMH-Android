package com.everymomentholy.interfaces

import com.everymomentholy.api.response.DataVo

interface LiturgyLitstClickListner {
    fun onMyLiturgiesListClick(pos: Int, dataVo: DataVo)
}