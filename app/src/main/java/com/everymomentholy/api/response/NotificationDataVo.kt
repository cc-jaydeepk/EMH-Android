package com.everymomentholy.api.response

import java.io.Serializable

class NotificationDataVo : Serializable {

    var notificationId: Int = 1

    var message: String = ""

    var title: String = ""

    var createdAt: String = ""

    var mode: String = ""

}