package com.hansung.capstone.retrofit

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

}