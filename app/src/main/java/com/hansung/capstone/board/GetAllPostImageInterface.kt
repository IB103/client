package com.hansung.capstone.board

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface GetAllPostImageInterface {
    @Headers("accept:image/png ", "content-type: image/png")
    @GET("image/{id}")
//    @Streaming
    fun getImage(
        @Path("id") id: Long,
    ): Call<ResponseBody>
}