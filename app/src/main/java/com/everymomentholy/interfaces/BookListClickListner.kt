package com.everymomentholy.interfaces

import com.everymomentholy.api.response.LiturgiesDataVo

interface BookListClickListner {
    fun getBookListClick(pos: Int, dataVo: LiturgiesDataVo)
}