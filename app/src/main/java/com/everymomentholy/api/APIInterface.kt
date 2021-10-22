package com.everymomentholy.api

import com.everymomentholy.api.request.*
import com.everymomentholy.api.response.*
import com.everymomentholy.utils.Constants
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*


interface APIInterface {

    @POST(Constants.API_REGISTRATION)
    fun userRegistration(@Body registrationRequestVo: RegisterRequestVo): Call<RegisterResponseVo>

    @POST(Constants.API_REGISTRATION)
    fun userRegistrationWithPhone(@Body registerWithoutPhoneRequestVo: RegisterWithoutPhoneRequestVo): Call<RegisterResponseVo>

    @POST(Constants.API_LOGIN)
    fun userLogin(@Body loginRequestVo: LoginRequestVo): Call<LoginResponseVo>

    @GET(Constants.API_FORGOT_PASSWORD)
    fun forgotPassword(@Query("email") forgotemail: String?): Call<ForgotPasswordResponseVo>

    @POST(Constants.API_RESET_PASSWORD)
    fun resetPassword(@Body resetPasswordRequestVo: ResetPasswordRequestVo): Call<ResetPasswordResponseVo>

    @GET(Constants.API_HOME_DAILY_LITURGY_QUOTE)
    fun dailyLiturgyQuote(@Header("Authorization") token: String): Call<HomeDailyLiturgyResponseVo>

    @GET(Constants.API_HOME_GETSETTINGS)
    fun getSettings(): Call<HomegetSettingResponseVo>

    @GET(Constants.API_HOME_NOTIFICATIONLIST)
    fun notificationList(): Call<NotificationResponseVo>

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
        @Query("firstName") firstName: String?,
        @Query("lastName") lastName: String?,
        @Query("email") email: String,
        @Query("countryCode") countryCode: String,
        @Query("mobile") phoneno: String,
        // @Query("userProfilePic") userProfilePic: String,
        @Header("Authorization") token: String,
        @Part image: MultipartBody.Part?

    ): Call<GetUserProfileUpdateResponseVo>

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

    @GET(Constants.API_TERMSCONDITION)
    fun termsCondition(): Call<TermsConditionResponseVo>

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
}
