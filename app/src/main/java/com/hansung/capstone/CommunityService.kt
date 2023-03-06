package com.hansung.capstone

import com.hansung.capstone.board.ResultGetPosts
import com.hansung.capstone.post.ResultGetPostDetail
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface CommunityService {
    @GET("api/community/post/list")
    fun getAllPost(
        @Query("page") query: Int,
    ): Call<ResultGetPosts>

    @GET("api/community/post/detail")
    fun getPostDetail(
        @Query("id") query: Long,
    ): Call<ResultGetPostDetail>
    @Headers("accept: application/json", "content-type: application/json")
    @GET("api/community/post/list/nickname")
    fun getPostMyStory(
        @Query("nickname") nickname: String,
        @Query("page") page: Int
    ): Call<ResultGetPosts>
    @GET("image/{id}")
    fun getImage(
        @Path("id") id: Long,
    ): Call<ResponseBody>

    @GET("profile-image/{id}")
    fun getProfileImage(
        @Path("id") id: Long,
    ): Call<ResponseBody>

    @GET("api/community/post/favorite")
    fun checkFavorite(
        @Query("userId") userId: Long,
        @Query("postId") postId: Long
    ): Call<ResponseBody>

    companion object{
        fun create() : CommunityService{
            return Retrofit.Builder()
                .baseUrl(MyApplication.getUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(CommunityService::class.java)
        }
    }
}