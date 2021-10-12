package com.everymomentholy.interfaces

import com.everymomentholy.api.response.LiturgiesDataVo

interface NotificationListClickListner {
    fun onNotificationListClick(pos: Int, dataVo: LiturgiesDataVo)
}