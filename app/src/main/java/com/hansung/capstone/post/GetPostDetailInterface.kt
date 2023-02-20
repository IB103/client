package com.hansung.capstone.post


import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface GetPostDetailInterface {
    @GET("api/community/post/detail")
    fun getPostDetail(
        @Query("id") query: Long,
    ): Call<ResultGetPostDetail>

    companion object {
                private const val server_info = "223.194.133.220:8080"
//        private const val server_info = "121.138.93.178:9999"
        private const val url = "http://$server_info/"

        fun create(): GetPostDetailInterface {
            val httpLoggingInterceptor = HttpLoggingInterceptor()
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

            val headerInterceptor = Interceptor {
                val request = it.request()
                    .newBuilder()
                    .build()
                return@Interceptor it.proceed(request)
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(headerInterceptor)
                .addInterceptor(httpLoggingInterceptor)
                .build()

            return Retrofit.Builder()
                .baseUrl(url)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(GetPostDetailInterface::class.java)
        }
    }
}