package com.everymomentholy.api.response

class GetUserSubscriptionPlanResVo {
    var status: Boolean = true

    var statusCode: Int = 1

//    var response: UserSubscriptionDataVo = UserSubscriptionDataVo()
var response: ResponseData = ResponseData()
   // var response: userSubscriptionData = userSubscriptionData()
}

class ResponseData {
    var userSubscriptionData: UserSubscriptionDataVo = UserSubscriptionDataVo()
}
