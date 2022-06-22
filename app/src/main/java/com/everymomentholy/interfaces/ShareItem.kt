package com.everymomentholy.interfaces

import com.everymomentholy.api.response.NotificationDataVo
import com.everymomentholy.api.response.QuotePreviousquoteVo

interface ShareItem {

    fun shareQuote(pos: Int, quote: QuotePreviousquoteVo)
}