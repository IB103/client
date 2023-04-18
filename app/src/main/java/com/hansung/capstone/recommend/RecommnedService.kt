package com.hansung.capstone.recommend

import com.hansung.capstone.CommunityService
import com.hansung.capstone.MyApplication
import com.hansung.capstone.board.ResultGetPosts
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface RecommnedService {
    @GET("api/community/course/list")
    fun getUserRecommend(
        @Query("region") region: String,
        @Query("page") page: Int,
    ): Call<UserRecommendDTO>

    companion object{
        fun create() : RecommnedService {
            return Retrofit.Builder()
                .baseUrl(MyApplication.getUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(RecommnedService::class.java)
        }
    }
}