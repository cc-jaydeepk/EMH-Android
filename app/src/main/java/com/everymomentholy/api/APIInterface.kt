package com.everymomentholy.api

import com.everymomentholy.api.request.*
import com.everymomentholy.api.response.*
import com.everymomentholy.utils.Constants
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*


interface APIInterface {

    @POST(Constants.API_REGISTRATION)
    fun userRegistration(@Body registrationRequestVo: RegisterRequestVo): Call<RegisterResponseVo>

    @POST(Constants.API_REGISTRATION)
    fun userRegistrationWithPhone(@Body registerWithoutPhoneRequestVo: RegisterWithoutPhoneRequestVo): Call<RegisterResponseVo>

    @POST(Constants.API_LOGIN)
    fun userLogin(@Body loginRequestVo: LoginRequestVo): Call<LoginResponseVo>

    @POST(Constants.API_CREATESUBSCRIPTION)
    fun createSubscription(@Body createSubscrptionReqVo: CreateSubscrptionReqVo): Call<CreateSubscrptionResVo>

    @GET(Constants.API_GETUSERSUBSCRIPTIONPLANS)
    fun getUserSubscriptionPlans(
        @Path("userid") id: Int,
        @Query("deviceId") deviceId: String?,
        @Header("Authorization") token: String
    ): Call<GetUserSubscriptionPlanResVo>

    /* @GET(Constants.API_GET_USER_PROFILE)
     fun getUserProfile(
         @Path("userid") id: Int,
         @Query("deviceId") deviceId: String?,
         @Header("Authorization") token: String
     ): Call<GetUserProfileVo>*/


    @POST(Constants.API_CANCELSUBSCRIPTION)
    fun cancelSubscription(@Body cancelSubscriptionReqVo: CancelSubscriptionReqVo): Call<CancelSubscriptionResVo>

    @POST(Constants.API_CREATESUBSCRIPTION)
    fun subscribedUser(@Body createSubscrptionReqVo: CreateSubscrptionReqVo): Call<SubscribedUserRes>

    @POST(Constants.API_GETSUBSCRIPTIONSTATUS)
    fun getSubscriptionStatus(@Body getSubscriptionStatusReqVo: GetSubscriptionStatusReqVo): Call<GetSubscriptionStatusResVo>

    @POST(Constants.API_SUBSCRIPTIONPLANLIST)
    fun subscriptionPlanList(): Call<SubscriptionPlanListResponseVo>

    @GET(Constants.API_FORGOT_PASSWORD)
    fun forgotPassword(@Query("email") forgotemail: String?): Call<ForgotPasswordResponseVo>

    @POST(Constants.API_RESET_PASSWORD)
    fun resetPassword(@Body resetPasswordRequestVo: ResetPasswordRequestVo): Call<ResetPasswordResponseVo>

    @GET(Constants.API_HOME_DAILY_LITURGY_QUOTE)
    fun dailyLiturgyQuote(@Header("Authorization") token: String): Call<HomeDailyLiturgyResponseVo>

    @GET(Constants.API_HOME_DAILY_LITURGY_QUOTE)
    fun quoteLiturgies(): Call<QuoteResponseVo>

    @GET(Constants.API_HOME_GETSETTINGS)
    fun getSettings(): Call<HomegetSettingResponseVo>

    @GET(Constants.API_HOME_NOTIFICATIONLIST_WITHOUT_LOGIN)
    fun notificationListWithoutLogin(): Call<NotificationResponseVo>

    @GET(Constants.API_MY_LITURGIES_LIST)
    fun getLiturgies(
        @Query("appUserId") appUserId: Int?,
        @Query("deviceId") deviceId: String?
    ): Call<MyLiturgiesResponseVo>

    @GET(Constants.API_GET_BOOKS)
    fun getBooks(
        @Query("appUserId") appUserId: Int?,
        @Query("deviceId") deviceId: String?,
        @Header("Authorization") token: String
    ): Call<GetLiturgiesResponseVo>

    @GET(Constants.API_GET_USER_PROFILE)
    fun getUserProfile(
        @Path("userid") id: Int,
        @Query("deviceId") deviceId: String?,
        @Header("Authorization") token: String
    ): Call<GetUserProfileVo>

    @Multipart
    @POST(Constants.API_GET_USER_PROFILE_UPDATE)
    fun getUserProfileUpdate(
        @Path("userid") id: Int,
        @Query("deviceId") deviceId: String?,
        @Query("notification_status") notification_status: String,
        @Query("firstName") firstName: String?,
        @Query("lastName") lastName: String?,
        @Query("email") email: String,
        @Query("countryCode") countryCode: String,
        @Query("mobile") phoneno: String,
        // @Query("userProfilePic") userProfilePic: String,
        @Header("Authorization") token: String,
        @Part image: MultipartBody.Part?

    ): Call<GetUserProfileUpdateResponseVo>

    @POST(Constants.API_GET_USER_PROFILE_UPDATE)
    fun notificationAlert(
        @Path("userid") id: Int,
        @Query("deviceId") deviceId: String?,
        @Query("notification_status") notification_status: String,
        @Query("firstName") firstName: String?,
        @Query("lastName") lastName: String?,
        @Query("email") email: String,
        @Query("countryCode") countryCode: String,
        @Query("mobile") phoneno: String,
        // @Query("userProfilePic") userProfilePic: String,
        @Header("Authorization") token: String,
        // @Part image: MultipartBody.Part?

    ): Call<NotificationAlertResponseVo>

    /*@POST(Constants.API_GET_USER_PROFILE_UPDATE)
    fun updateUserData(
        @Path("userid") id: Int,
        @Query("deviceId") deviceId: String?,
        @Query("firstName") firstName: String?,
        @Query("lastName") lastName: String?,
        @Query("email") email: String,
        @Query("countryCode") countryCode: String,
        @Part MultipartBody.Part image,
        @Header("Authorization") token: String
    ): Call<c>*/

    @Multipart
    @POST(Constants.API_GET_USER_PROFILE_UPDATE)
    fun uploadImage(
        @Path("userid") id: Int,
        @Query("deviceId") deviceId: String?,
        @Query("firstName") firstName: String?,
        @Query("lastName") lastName: String?,
        @Query("email") email: String,
        @Query("countryCode") countryCode: String,
        @Header("Authorization") token: String,
        @Part("userProfilePic") image: MultipartBody.Part?
    ): Call<GetUserProfileUpdateResponseVo?>

    @Multipart
    @POST(Constants.API_GET_USER_PROFILE_UPDATE)
    fun getUserProfileEdit(
        @Path("userid") id: Int,
        @Body getUserProfileUpdateRequestVo: GetUserProfileUpdateRequestVo,
        @Header("Authorization") token: String,
        @Part image: Part?
    ): Call<GetUserProfileUpdateResponseVo>

    @POST(Constants.API_LOGOUT)
    fun logoutUser(
        @Body logoutRequestVo: LogoutRequestVo,
        @Header("Authorization") token: String
    ): Call<LogoutResponseVo>

    @POST(Constants.API_CHANGEPASSWORD)
    fun changePassword(
        @Body changePasswordRequestVo: ChangePasswordRequestVo,
        @Header("Authorization") token: String
    ): Call<ChangePasswordResponseVo>

    // fun userLogin(@Body loginRequestVo: LoginRequestVo): Call<LoginResponseVo>

    @GET(Constants.API_ABOUTUS)
    fun aboutUs(): Call<AboutUsResponseVO>

    @GET(Constants.API_FAQ)
    fun frequentlyAsked(): Call<FaqResponseVo>

    @GET(Constants.API_TERMS_CONDITION)
    fun termsCondition(): Call<TermsConditionResponseVo>

    @GET(Constants.API_SHARELITURGIES)
    fun shareLiturgies(): Call<ShareLiturgiesResponseVo>

    @POST(Constants.API_CONTACTUS)
    fun contactUs(@Body contactUsRequestVo: ContactUsRequestVo): Call<ContectUsResponseVo>

    /*@GET(Constants.API_HOME_DAILY_LITURGY_QUOTE)
    fun dailyLiturgyQuote(): Call<ResponseVo>*/
    @GET(Constants.API_ABOUT_BOOK)
    fun aboutBook(@Query("bookId") bookid: Int): Call<AboutBookResponseVo>

    @GET(Constants.API_ABOUT_VOLUME)
    fun getAboutVolume(@Query("volumeId") volumnId: Int): Call<AboutVolumeResponseVo>

    @GET(Constants.API_GET_BOOKS_BY_VOLUME)
    fun getCollectionList(
        @Query("deviceId") deviceId: String,
        @Query("volumeId") volumeId: Int,
        @Query("appUserId") appUserId: Int?
    ): Call<CollectionListResponseVo>

    @GET(Constants.API_MY_LITURGIES_LIST)
    fun getLiturgiesFromBookId(
        @Query("appUserId") appUserId: Int?,
        @Query("deviceId") deviceId: String?,
        @Query("bookId") bookId: Int?
    ): Call<MyLiturgiesResponseVo>

    @GET(Constants.API_PRIVACY_POLICY)
    fun privacyPolicy(): Call<PrivacyPolicyResponseVo>

    @POST(Constants.API_SET_FAVORITE)
    fun setFavorite(
        @Body setFavouriteRequestVo: SetFavouriteRequestVo,
        @Header("Authorization") token: String
    ): Call<BaseResponseVo>

    @GET(Constants.API_GET_FAVORITES_LIST)
    fun getFavoriteList(
        @Query("userId") userID: Int?,
        @Header("Authorization") token: String
    ): Call<GetFavoritesResponseVo>

    @GET(Constants.API_GET_FAVORITES_BOOKLIST)
    fun getFavoriteBookList(
        @Query("userId") userID: Int?,
        @Header("Authorization") token: String
    ): Call<GetFavoriteBookList>

    @POST(Constants.API_PRIVATE_SHARING)
    fun privateSharing(
        @Body privateSharingRequestVo: PrivateSharingRequestVo,
        @Header("Authorization") token: String
    ): Call<PrivateShareResponseVo>

    @GET(Constants.API_MY_LITURGIES_LIST)
    fun getFeaturedLiturgies(
        @Query("appUserId") appUserId: Int?,
        @Query("deviceId") deviceId: String?,
        @Query("isFeatured") isFeatured: String?,
    ): Call<MyLiturgiesResponseVo>

    @GET(Constants.API_MY_LITURGIES_LIST)
    fun searchLiturgies(
        @Query("deviceId") deviceId: String?,
        @Query("searchText") searchText: String,
        @Query("appUserId") appUserId: Int?,
    ): Call<MyLiturgiesResponseVo>

    @GET(Constants.API_GET_ORDER_HISTORY)
    fun getOrderHistory(
        @Query("userId") userID: Int,
        @Query("deviceId") deviceId: String,
        @Header("Authorization") token: String
    ): Call<OrderHistoryResponseVo>

    @GET(Constants.API_GET_BOOK_STORE)
    fun getBookStore(): Call<BookStoreResponseVo>

    @GET(Constants.API_GET_BOOKS)
    fun getBooksWithOutLogin(): Call<GetLiturgiesResponseVo>

    @GET(Constants.API_USER_NOTIFICATIONS)
    fun getNotification(
        @Query("userId") userID: Int,
        @Header("Authorization") token: String
    ): Call<NotificationResponseVo>

    @POST(Constants.API_READ_USER_NOTIFICATIONS)
    fun readUserNotification(
        @Body notificationReadRequestVo: NotificationReadRequestVo,
        @Header("Authorization") token: String
    ): Call<PrivateShareResponseVo>

    @POST(Constants.API_PURCHASE_LITURGY)
    fun purchaseLiturgyAcknowledge(
        @Body privateSharingRequestVo: PurchaseRequestVo,
        @Header("Authorization") token: String
    ): Call<PrivateShareResponseVo>

    @POST(Constants.API_PURCHASE_BOOK)
    fun purchaseBookAcknowledge(
        @Body privateSharingRequestVo: PurchaseRequestVo,
        @Header("Authorization") token: String
    ): Call<PrivateShareResponseVo>

    @POST(Constants.API_PURCHASE_VOLUME)
    fun purchaseVolumeAcknowledge(
        @Body privateSharingRequestVo: PurchaseRequestVo,
        @Header("Authorization") token: String
    ): Call<PrivateShareResponseVo>

    @DELETE(Constants.API_DELETE_ACCOUNT)
    fun deleteUserAccount(
        @Path("userId") userID: Int,
        @Header("Authorization") token: String
    ): Call<BaseResponseVo>
}
