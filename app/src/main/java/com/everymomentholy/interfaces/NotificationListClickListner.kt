package com.everymomentholy.interfaces

import com.everymomentholy.api.response.NotificationDataVo

interface NotificationListClickListner {
    fun onNotificationListClick(pos: Int, dataVo: NotificationDataVo)
}