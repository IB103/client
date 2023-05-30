package com.hansung.capstone

import com.hansung.capstone.board.*
import com.hansung.capstone.post.ResultGetPostDetail
import com.hansung.capstone.retrofit.RepGetRecord
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface CommunityService {
    @GET("/api/community/post/list/title-or-content")
    fun searchBoard(
        @Query("titleOrContent") titleOrContent: String,
        @Query("page") page: Int
    ): Call<ResultGetPosts>
    @GET("/api/community/post/list/nickname")
    fun searchNickname(
        @Query("nickname") nickname: String,
        @Query("page") page: Int
    ): Call<ResultGetPosts>
    @GET("api/community/post/list/all")
    fun getAllPost(
        @Query("page") query: Int,
    ): Call<ResultGetPosts>
    @GET("api/community/post/list/free")
    fun getAllFreePost(
        @Query("page") query: Int,
    ): Call<ResultGetPosts>
    @GET("api/community/post/list/course")
    fun getAllCoursePost(
        @Query("page") query: Int,
    ): Call<ResultGetPosts>
    @DELETE("api/community/post/delete")
    fun deletePost(
        @Header("Authorization")accessToken:String,
        @Query("userId") userId: Long,
        @Query("postId") postId: Long

        ): Call<ResDelete>
    @DELETE("api/community/comment/delete")
    fun deleteComment(
        @Header("Authorization")accessToken:String,
        @Query("userId") userId: Long,
        @Query("commentId") commentId: Long

    ): Call<ResDelete>
    @DELETE("api/community/recomment/delete")
    fun deleteReComment(
        @Header("Authorization")accessToken:String,
        @Query("userId") userId: Long,
        @Query("reCommentId") reCommentId: Long
        ): Call<ResDeleteReComment>

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

    @GET("api/community/post/list/scrap")
    fun getPostMyScrap(
        @Query("userId") userId: Long,
        @Query("page") page: Int
    ): Call<ResultGetPosts>

    @GET("api/community/post/favorite")
    fun checkFavorite(
        @Query("userId") userId: Long,
        @Query("postId") postId: Long
    ): Call<ResponseBody>
    @GET("api/community/post/scrap")
    fun checkScrap(
        @Query("userId") userId: Long,
        @Query("postId") postId: Long
    ): Call<ResultRespond>



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