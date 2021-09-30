package com.everymomentholy.api

import com.everymomentholy.api.request.ForgotPasswordRequestVo
import com.everymomentholy.api.request.LoginRequestVo
import com.everymomentholy.api.request.RegisterRequestVo
import com.everymomentholy.api.request.ResetPasswordRequestVo
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


}