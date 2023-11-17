package com.everymomentholy.interfaces

interface SubscriptionPlanListCLick {
    //fun onMyLiturgiesListClick(pos: Int, bookID: Int, isAuto: Boolean)
    fun onSelectPlan(pos: Int, planType: String, isAuto:Boolean)
}