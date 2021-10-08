package com.everymomentholy.api

import com.everymomentholy.api.request.*
import com.everymomentholy.api.response.*
import com.everymomentholy.utils.Constants
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface APIInterface {

    @POST(Constants.API_REGISTRATION)
    fun userRegistration(@Body registrationRequestVo: RegisterRequestVo): Call<RegisterResponseVo>

    @POST(Constants.API_LOGIN)
    fun userLogin(@Body loginRequestVo: LoginRequestVo): Call<LoginResponseVo>

    @GET(Constants.API_FORGOT_PASSWORD)
    fun forgotPassword(@Query("email") forgotemail: String?): Call<ForgotPasswordResponseVo>

    @POST(Constants.API_RESET_PASSWORD)
    fun resetPassword(@Body resetPasswordRequestVo: ResetPasswordRequestVo): Call<ResetPasswordResponseVo>

    @GET(Constants.API_HOME_DAILY_LITURGY_QUOTE)
    fun dailyLiturgyQuote(): Call<HomeDailyLiturgyResponseVo>

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
        @Query("deviceId") deviceId: String?
    ): Call<GetBookResponseVo>

    @GET(Constants.API_ABOUTUS)
    fun aboutUs(): Call<AboutUsResponseVO>

    @GET(Constants.API_TERMSCONDITION)
    fun termsCondition(): Call<TermsConditionResponseVo>

    @POST(Constants.API_CONTACTUS)
    fun contactUs(@Body contactUsRequestVo: ContactUsRequestVo): Call<ContectUsResponseVo>

    /*@GET(Constants.API_HOME_DAILY_LITURGY_QUOTE)
    fun dailyLiturgyQuote(): Call<ResponseVo>*/


}