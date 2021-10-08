package com.everymomentholy.utils

class Constants {

    companion object {
       // const val BASE_URL = "http://203.109.113.162:8686/every_moment_holy/public/api/"
        const val BASE_URL = "http://ec2-13-234-132-104.ap-south-1.compute.amazonaws.com/api/"

        //API
        const val API_REGISTRATION = "register"
        const val API_LOGIN = "login"
        const val API_FORGOT_PASSWORD = "forgotPassword"
        const val API_RESET_PASSWORD = "updatePassword"
        const val API_HOME_DAILY_LITURGY_QUOTE = "dailyLiturgyQuote"
        const val API_HOME_GETSETTINGS = "getSettings"
        const val API_HOME_NOTIFICATIONLIST = "notificationList"
        const val API_MY_LITURGIES_LIST = "getLiturgies"
        const val API_GET_BOOKS = "getBooks"
        const val API_LOGOUT = "logout"
        const val API_ABOUTUS = "getStaticPages?slug=about-us"
        const val API_TERMSCONDITION = "getStaticPages?slug=terms-conditions"
        const val API_ABOUT_BOOK = "aboutBook"
        const val API_CONTACTUS = "contactUs"


        const val DEVICE_TYPE = "1"
        const val SHARED_PREF_APP_SETTINGS_RESPONSE = "APP_SETTINGS_RESPONSE"


        //Shared preferences keys
        const val SHARED_PREF_NAME = "EveryMomentHoly"
        const val PrefUserID = "userId"
        const val SHARED_PREF_TOKEN = "token"
        const val LOGGED_IN_PREF = "logged_in_status"
    }

}