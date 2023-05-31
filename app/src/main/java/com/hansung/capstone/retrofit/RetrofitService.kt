package com.hansung.capstone.retrofit

import com.google.gson.GsonBuilder
import com.hansung.capstone.MyApplication
import com.hansung.capstone.board.ModifyPost
import com.hansung.capstone.board.Posts
import com.hansung.capstone.board.RePModifyProfileImage
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*


interface RetrofitService {
    @POST("/api/users/logout")
    fun logOut(
        @Header("Authorization") accessToken: String
    ): Call<RepLogOut>

    @GET("/api/users/riding/rank")
    fun getRank(
//        <<<<<<< Updated upstream

//    =======
//    @Header("Authorization") accessToken: String
//    >>>>>>> Stashed changes
    ): Call<RepRank>

    @GET("/api/users/riding/record-history")
    fun getRecord(
        @Header("Authorization") accessToken: String,
        @Query("userId") userId: Long,
        @Query("period") period: Long
    ): Call<RepGetRecord>

    @POST("/api/email/send")
    fun send(

        @Query("email") email: String
    ): Call<RepUser>

    @POST("/api/email/confirm")
    fun confirm(
//=======
//        @Header("Authorization") accessToken: String,
//        @Query("email") email: String
//    ): Call<String>
//
//    @POST("/api/email/confirm")
//    fun confirm(
//        @Header("Authorization") accessToken: String,
//>>>>>>> Stashed changes
        @Query("email") email: String,
        @Query("code") code: String
    ): Call<RepConfirm>

    @GET("/api/users/findID")
    fun findID(
        @Query("username") username: String,
        @Query("birthday") birthday: String
    ): Call<RepFindId>

    @POST("/api/auth/reissue")
    fun reissue(
        @Header("Authorization") accessToken: String,
        @Header("Refresh-Token") refreshToken: String
    ): Call<RespondToken>//수정해야함

    @Multipart
    @PUT("api/users/set-profile-image")
    fun modifyProfileImage(
        @Header("Authorization") accessToken: String,
        @Part("requestDTO") requestDTO: ReqModifyProfileImage,
        @Part imageList: MultipartBody.Part
    ): Call<RePModifyProfileImage>

    @GET("weather?")
    fun getWeather(
        @Query("lat") latitude: String,
        @Query("lon") longitude: String,
        @Query("appid") API_KEY: String
    ): Call<Weather>

    @Multipart
    @PUT("/api/community/post/modify")
    fun modifyPost(
        @Header("Authorization") accessToken: String,
        @Part("requestDTO") requestDTO: ReqModifyPost,
        @Part imageList: List<MultipartBody.Part>
    ): Call<ModifyPost>

    // Login
    @Headers("accept: application/json", "content-type: application/json")
    @POST("/api/users/signin")
    fun login(
        @Body reqLogin: ReqLogin
    ): Call<RepUser>

    @Headers("accept: application/json", "content-type: application/json")
    @POST("/api/users/signup")
    fun register(
        @Body reqRegister: ReqRegister
    ): Call<RepRegister>

    @Headers("accept: application/json", "content-type: application/json")
    @GET("/api/users/email/duplicate-check")
    fun doublecheckId(
        @Query("email") email: String?
    ): Call<RepDoubleCheckID>

    @Headers("accept: application/json", "content-type: application/json")
    @GET("/api/users/nickname/duplicate-check")
    fun doublecheckNickName(
        @Query("nickname") nickname: String?
    ): Call<RepDoubleCheckNickName>
//    =======
//
//    @Headers("accept: application/json", "content-type: application/json")
//    @GET("/api/users/test")
//    fun test(@Header("Authorization") accessToken: String): Call<String>
//    >>>>>>> Stashed changes


    @PUT("/api/users/modifyPW")
    fun modifyPW(
        @Header("Authorization") accessToken: String,
        @Body reqModifyPW: ReqModifyPW
    ): Call<RepModifyPW>

    @Headers("accept: application/json", "content-type: application/json")
    @PUT("/api/users/modifyNick")
    fun modifyNick(
        @Header("Authorization") accessToken: String,
        @Body reqModifyNick: ReqModifyNick
    ): Call<RepModifyNick>


    @Multipart
    @POST("/api/community/post/create")
    fun postCreate(
        @Header("Authorization") accessToken: String,
        @Part("requestDTO") requestDTO: ReqPost,
        @Part imageList: List<MultipartBody.Part>
    ): Call<RepPost>

    @Headers("accept: application/json", "content-type: application/json")
    @POST("/api/community/comment/create")
    fun postComment(
        @Header("Authorization") accessToken: String,
        @Body reqComment: ReqComment
    ): Call<RepComment>

    @PUT("api/community/comment/modify")
    fun modifyComment(
        @Header("Authorization") accessToken: String,
        @Body reqModifyComment: ReqModifyComment
    ): Call<Posts>

    @PUT("api/community/recomment/modify")
    fun modifyRecomment(
        @Header("Authorization") accessToken: String,
        @Body reqModifyReComment: ReqModifyReComment
    ): Call<Posts>

    @Headers("accept: application/json", "content-type: application/json")
    @POST("/api/community/recomment/create")
    fun postReComment(
        @Header("Authorization") accessToken: String,
        @Body reqReComment: ReqReComment
    ): Call<RepComment>

    @Multipart
    @POST("/api/user-course/create")
    fun coursePostCreate(
        @Part("requestDTO") requestDTO: ReqCoursePost,
        @Part imageList: List<MultipartBody.Part>,
        @Part thumbnail: MultipartBody.Part
    ): Call<RepCoursePost>

    @POST("/api/users/riding/record")
    fun recordRidingData(
        @Header("Authorization") accessToken: String,
        @Body reqRidingData: ReqRidingData
    ): Call<RepRidingData>

    @GET("/api/user-course/detail")
    fun getCourseDetail(
        @Query("courseId") courseId: Int
    ): Call<RepCourseDetailData>

    companion object {
        fun create(): RetrofitService {
            return Retrofit.Builder()
                .baseUrl(MyApplication.getUrl())
                .addConverterFactory(
                    GsonConverterFactory.create(
                        GsonBuilder().setLenient().create()
                    )
                )
                .build()
                .create(RetrofitService::class.java)
        }
    }
}