package com.everymomentholy.api

import com.everymomentholy.api.request.RegisterRequestVo
import com.everymomentholy.api.response.RegisterResponseVo
import com.everymomentholy.utils.Constants
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface APIInterface {

    @POST(Constants.API_REGISTRATION)
    fun userRegistration(@Body registrationRequestVo: RegisterRequestVo): Call<RegisterResponseVo>
}