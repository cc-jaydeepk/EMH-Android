package com.everymomentholy.api.response

class UserSubscriptionDataResVO {

    var started_at: String = ""

    var expire_at: String = ""

    var subscription_status: String = ""

    var subscription_id: String = ""

    var subscription_type: String = ""
    var plan_type: String = ""
    var type: String = ""
    var upcomingPlan: String = ""
    var subscription: String = ""


    //var upcomingPlanData: UserSubscriptionDataResVO = UserSubscriptionDataResVO()
    var upcomingPlanData: UpcomingPlanDataResVO = UpcomingPlanDataResVO()
}