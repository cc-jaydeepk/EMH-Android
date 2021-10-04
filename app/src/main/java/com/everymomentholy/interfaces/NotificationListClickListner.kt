package com.everymomentholy.interfaces

import com.everymomentholy.api.response.DataVo

interface NotificationListClickListner {
    fun onNotificationListClick(pos: Int, dataVo: DataVo)
}