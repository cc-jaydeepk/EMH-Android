package com.everymomentholy.api.request

class UpdateSubscriptionStatusReq {

    var payment_email: String = ""
    var subscription_id: String = ""
    var subscription_type: String = ""
    var package_name: String = ""
    var start_date: String = ""
    var end_date: String = ""
    var app_user_id: Int = 1
    var subscription_status: String = ""
    var token: String = ""
}