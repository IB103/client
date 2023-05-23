package com.hansung.capstone.map

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface KakaoSearchAPI {
    companion object {
        private const val BASE_URL_KAKAO_API = "https://dapi.kakao.com/"

        fun create(): KakaoSearchAPI {
            return Retrofit.Builder()
                .baseUrl(BASE_URL_KAKAO_API)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(KakaoSearchAPI::class.java)
        }
    }

    @GET("v2/local/search/keyword.json")
    fun getSearchKeyword(
        @Header("Authorization") key: String,
        @Query("query") query: String,
        @Query("size") size: Int = 5
        // 매개변수 추가 가능
        // @Query("category_group_code") category: String
    ): Call<ResultSearchKeyword>

    @GET("v2/search/image")
    fun getSearchImage(
        @Header("Authorization") key: String,
        @Query("query") query: String,
        @Query("size") size: Int
    ): Call<LocationImageDTO>

    @GET("v2/local/geo/coord2address.json")
    fun getAddress(
        @Header("Authorization") key: String,
        @Query("x") x: String,
        @Query("y") y: String,
    ): Call<ResultGetAddress>

}