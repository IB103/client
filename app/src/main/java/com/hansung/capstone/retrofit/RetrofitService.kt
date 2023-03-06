package com.hansung.capstone.retrofit

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*


interface RetrofitService {

    abstract val getReqDoubleCheckID: Any

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
        @Path("id") id: Int,
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

    @Headers("accept: application/json", "content-type: application/json")
    @POST("/api/community/recomment/create")
    fun postReComment(
        @Body reqReComment: ReqReComment
    ): Call<RepComment>
}