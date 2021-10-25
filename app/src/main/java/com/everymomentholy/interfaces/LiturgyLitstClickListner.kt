package com.everymomentholy.interfaces

import com.everymomentholy.api.response.NotificationDataVo

interface LiturgyLitstClickListner {
    fun onMyLiturgiesListClick(pos: Int, bookID: Int, isAuto: Boolean)
}