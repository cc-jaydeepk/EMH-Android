package com.everymomentholy.interfaces

import com.everymomentholy.api.response.NotificationDataVo

interface BookListClickListner {
    fun getBookListClick(pos: Int, dataVo: NotificationDataVo)
}