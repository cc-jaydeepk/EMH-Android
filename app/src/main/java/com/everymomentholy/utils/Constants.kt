package com.everymomentholy.utils

class Constants {

    companion object {
        const val BASE_URL = "http://203.109.113.162:8686/every_moment_holy/public/api/"

        //API
        const val API_REGISTRATION = "register"
        const val API_LOGIN = "login"
        const val API_FORGOT_PASSWORD = "forgotPassword"
        const val API_RESET_PASSWORD = "updatePassword"
        const val API_HOME_DAILY_LITURGY_QUOTE = "dailyLiturgyQuote"
        const val API_HOME_GETSETTINGS = "getSettings"
        const val API_HOME_NOTIFICATIONLIST = "notificationList"


        //Shared preferences keys
        const val SHARED_PREF_NAME = "EveryMomentHoly"
        const val PrefUserID = "userId"
        const val SHARED_PREF_TOKEN = "token"
    }

}