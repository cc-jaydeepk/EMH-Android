package com.everymomentholy.utils

class Constants {

    companion object {
        //test server
        // const val BASE_URL = "http://203.109.113.162:8686/every_moment_holy/public/api/"
        const val BASE_URL = "http://ec2-13-234-132-104.ap-south-1.compute.amazonaws.com/api/"

        //Live server
        //const val BASE_URL = "https://app.everymomentholy.com/api/"

        //API
        const val API_REGISTRATION = "register"
        const val API_LOGIN = "login"
        const val API_FORGOT_PASSWORD = "forgotPassword"
        const val API_RESET_PASSWORD = "updatePassword"
        const val API_HOME_DAILY_LITURGY_QUOTE = "dailyLiturgyQuote"
        const val API_HOME_GETSETTINGS = "getSettings"
        const val API_HOME_NOTIFICATIONLIST_WITHOUT_LOGIN = "notificationList"
        const val API_MY_LITURGIES_LIST = "getLiturgies"
        const val API_GET_BOOKS = "getBooks"
        const val API_GET_USER_PROFILE = "getUserProfile/{userid}"
        const val API_GET_USER_PROFILE_UPDATE = "updateUserProfile/{userid}"
        const val API_LOGOUT = "logout"
        const val API_CHANGEPASSWORD = "changePassword"
        const val API_ABOUTUS = "getStaticPages?slug=about-us"
        const val API_FAQ = "getStaticPages?slug=faq"
        const val API_FEATUREDLITURGYMESSAGE = "getStaticPages?slug=featured-liturgy-message"
        const val API_TERMS_CONDITION = "getStaticPages?slug=terms-conditions"
        const val API_SHARELITURGIES = "getStaticPages?slug=share-liturgy"
        const val API_ABOUT_BOOK = "aboutBook"
        const val API_CONTACTUS = "contactUs"
        const val API_ABOUT_VOLUME = "aboutVolume"
        const val API_GET_BOOKS_BY_VOLUME = "getBooksByVolume"
        const val API_PRIVACY_POLICY = "getStaticPages?slug=privacy-policy"
        const val API_SET_FAVORITE = "setFavorite"
        const val API_GET_FAVORITES_LIST = "getFavoritesList"
        const val API_PRIVATE_SHARING = "privateSharing"
        const val API_GET_ORDER_HISTORY = "getOrderHistory"
        const val API_GET_BOOK_STORE = "getBookStore"
        const val API_USER_NOTIFICATIONS = "userNotifications"
        const val API_READ_USER_NOTIFICATIONS = "readUserNotifications"
        const val API_PURCHASE_LITURGY = "purchaseLiturgy"
        const val API_PURCHASE_BOOK = "purchaseBook"
        const val API_PURCHASE_VOLUME = "purchaseVolume"
        const val API_DELETE_ACCOUNT = "deleteUser/{userId}"

        const val DEVICE_TYPE = "1"
        const val SHARED_PREF_APP_SETTINGS_RESPONSE = "APP_SETTINGS_RESPONSE"

        //Shared preferences keys
        const val SHARED_PREF_NAME = "EveryMomentHoly"
        const val PrefUserID = "userId"
        const val SHARED_PREF_TOKEN = "token"
        const val LOGGED_IN_PREF = "logged_in_status"
        const val USER_NAME = "firstName"
        const val USER_LASTNAME = "lastName"
        const val NAME = "userName"
        const val LASTNAME = "userLastName"
        const val USER_EMAIL = "email"
        const val PROFILE_PIC = "userProfilePic"
        const val PROFILE = "userProfilePic"
        const val DEFAULT_PROFILE_PIC = "userProfilePic"
        const val SHARED_PREF_DEVICE_ID = "deviceID"
        const val SHARED_PREF_FIREBASE_INSTANCE_ID = "firebase_instance_id"

        const val SEARCH_FROM_MY_LITURGY = 1
        const val SEARCH_FROM_FAVORITES = 2
        const val SEARCH_FROM_GET_LITURGY = 3
        const val SEARCH_FROM_FEATURED_LITURGY = 4
        var CURRENT_FRAGMENT = 0
        var USER_LOGIN_STATUS = 0
        const val SKIP_LOGIN = 1
        const val LOGIN = 2
        const val SKIP_LOGIN_USER_ID = 5

        // push notification constants
        const val CHANNEL_ID = "EMH"
        const val CHANNEL_NAME = "EMH Notification"
        const val CHANNEL_DESCRIPTION = "EMH"

        var GET_LITURGIES_VIEW_PAGER_POSITION = 0

        const val AFTER_PURCHASE_REFRESH_DELAY = 1500L // in millis

        const val LITURGIES_FILE_NAME = "liturgies.json"
        const val BOOKS_FILE_NAME = "books.json"
        const val FAVORITES_FILE_NAME = "favorites.json"
        const val DAILY_QUOTE_FILE_NAME = "daily.json"
        const val HOME_IMAGE_FILE_NAME = "home_image.json"
    }

}