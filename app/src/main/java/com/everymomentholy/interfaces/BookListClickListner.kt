package com.everymomentholy.interfaces

import com.everymomentholy.api.response.DataVo

interface BookListClickListner {
    fun getBookListClick(pos: Int, dataVo: DataVo)
}