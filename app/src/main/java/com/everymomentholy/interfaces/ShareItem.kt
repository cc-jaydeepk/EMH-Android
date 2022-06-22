package com.everymomentholy.interfaces

import com.everymomentholy.api.response.NotificationDataVo
import com.everymomentholy.api.response.QuotePreviousquoteVo

interface ShareItem {

    fun shareKiturgy(pos: Int, quote: QuotePreviousquoteVo)
}