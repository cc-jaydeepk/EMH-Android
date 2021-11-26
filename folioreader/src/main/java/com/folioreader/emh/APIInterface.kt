package com.folioreader.emh

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface APIInterface {

    @POST("privateSharing")
    fun privateSharing(
        @Body privateSharingRequestVo: PrivateSharingRequestVo,
        @Header("Authorization") token: String
    ): Call<PrivateShareResponseVo>

    @POST("setFavorite")
    fun setFavorite(
        @Body setFavouriteRequestVo: SetFavouriteRequestVo,
        @Header("Authorization") token: String
    ): Call<BaseResponseVo>
}