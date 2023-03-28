package com.hansung.capstone.map

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface KakaoSearchAPI {
    @GET("v2/local/search/keyword.json")
    fun getSearchKeyword(
        @Header("Authorization") key: String,
        @Query("query") query: String
        // 매개변수 추가 가능
        // @Query("category_group_code") category: String
    ): Call<ResultSearchKeyword>

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
}