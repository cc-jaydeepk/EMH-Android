package com.everymomentholy.interfaces

import com.everymomentholy.api.response.LiturgiesDataVo

interface LiturgyLitstClickListner {
    fun onMyLiturgiesListClick(pos: Int, dataVo: LiturgiesDataVo)
}