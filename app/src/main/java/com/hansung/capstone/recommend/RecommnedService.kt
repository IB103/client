package com.hansung.capstone.recommend

import com.hansung.capstone.MyApplication
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface RecommendService {
    @GET("api/user-course/list")
    fun getUserRecommend(
        @Query("region") region: String,
        @Query("page") page: Int,
    ): Call<UserRecommendDTO>

    companion object {
        fun create(): RecommendService {
            return Retrofit.Builder()
                .baseUrl(MyApplication.getUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(RecommendService::class.java)
        }
    }
}