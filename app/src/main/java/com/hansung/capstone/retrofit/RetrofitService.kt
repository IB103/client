package com.hansung.capstone.retrofit

import com.google.gson.JsonObject
import com.hansung.capstone.CommunityService
import com.hansung.capstone.MyApplication
import com.hansung.capstone.RequestParams
import com.hansung.capstone.board.ModifyPost
import com.hansung.capstone.board.Posts
import com.hansung.capstone.board.RePModifyProfileImage
import com.hansung.capstone.board.ResultGetPosts
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*


interface RetrofitService {
    @GET("/api/users/riding/record-history")
    fun getRecord(
        @Query("userId") userId: Long,
        @Query("period") period: Long
    ): Call<RepFindId>
    @POST("/api/email/send")
    fun send(
        @Query("email") email: String
    ):Call<RepSend>
    @POST("/api/email/confirm")
    fun confirm(
        @Query("email") email: String,
        @Query("code") code: String
    ):Call<RepConfirm>
    @GET("/api/users/findID")
    fun findID(
        @Query("username") username: String,
        @Query("birthday") birthday: String
    ): Call<RepFindId>
    @POST("/api/auth/reissue")
    fun reissue(
        @Header("Authorization") accessToken:String,
        @Header("Refresh-Token") refreshToken:String
    ):Call<RespondToken>//수정해야함

    @Multipart
    @PUT("api/users/set-profile-image")
    fun modifyProfileImage(
        @Part("requestDTO") requestDTO: ReqModifyProfileImage,
        @Part imageList: MultipartBody.Part
    ):Call<RePModifyProfileImage>

    @GET("weather?")
    fun getWeather(
        @Query("lat") latitude: String,
        @Query("lon") longitude: String,
        @Query("appid") API_KEY: String
    ): Call<Weather>
    @Multipart
    @PUT("/api/community/post/modify")
    fun modifyPost(
        //@Header("Authorization")accessToken:String,
        @Part("requestDTO") requestDTO: ReqModifyPost,
        @Part imageList: List<MultipartBody.Part>
    ):Call<ModifyPost>
    // Login
    @Headers("accept: application/json", "content-type: application/json")
    @POST("/api/users/signin")
    fun login(
        @Body reqLogin: ReqLogin
    ): Call<RepLogin>

    @Headers("accept: application/json", "content-type: application/json")
    @POST("/api/users/signup")
    fun register(
        @Body reqRegister: ReqRegister
    ): Call<RepRegister>

    @Headers("accept: application/json","content-type: application/json")
    @GET("/api/users/email/duplicate-check")
    fun doublecheckId(
        @Query("email") email: String?
    ): Call<RepDoubleCheckID>

    @Headers("accept: application/json","content-type: application/json")
    @GET("/api/users/nickname/duplicate-check")
    fun doublecheckNickName(
        @Query("nickname") nickname: String?
    ): Call<RepDoubleCheckNickName>
    @Headers("accept: application/json", "content-type: application/json")
    @GET("/api/users/test")
    fun test(@Header("Authorization")accessToken:String): Call<String>

    @Headers("accept: application/json", "content-type: application/json")
    @POST("/api/users/modifyPW")
    fun modifyPW(
        @Body reqModifyPW: ReqModifyPW
    ): Call<RepModifyPW>
    @Headers("accept: application/json", "content-type: application/json")
    @PUT("/api/users/modifyNick")
    fun modifyNick(
        @Body reqModifyNick: ReqModifyNick
    ): Call<RepModifyNick>

    @GET("profile-image/{id}")
    fun getProfileImage(
        @Path("id") id: Long,
    ): Call<ResponseBody>

    @Multipart
    @POST("/api/community/post/create")
    fun postCreate(
        @Part("requestDTO") requestDTO:ReqPost,
        @Part imageList: List<MultipartBody.Part>
    ): Call<RepPost>

    @Headers("accept: application/json", "content-type: application/json")
    @POST("/api/community/comment/create")
    fun postComment(
        @Body reqComment: ReqComment
    ): Call<RepComment>

    @PUT("api/community/comment/modify")
    fun modifyComment(
        @Header("Authorization")accessToken:String,
        @Body reqModifyComment: ReqModifyComment
    ): Call<Posts>
    @PUT("api/community/recomment/modify")
    fun modifyRecomment(
        @Header("Authorization")accessToken:String,
        @Body reqModifyReComment: ReqModifyReComment
    ): Call<Posts>
    @Headers("accept: application/json", "content-type: application/json")
    @POST("/api/community/recomment/create")
    fun postReComment(
        @Body reqReComment: ReqReComment
    ): Call<RepComment>

    @Multipart
    @POST("/api/user-course/create")
    fun coursePostCreate(
        @Part("requestDTO") requestDTO:ReqCoursePost,
        @Part imageList: List<MultipartBody.Part>
    ): Call<ReqCoursePost>

    companion object{
        fun create() : RetrofitService {
            return Retrofit.Builder()
                .baseUrl(MyApplication.getUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(RetrofitService::class.java)
        }
    }
}