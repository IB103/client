//package com.hansung.capstone.map
//
//import okhttp3.Interceptor
//import okhttp3.OkHttpClient
//import okhttp3.logging.HttpLoggingInterceptor
//import retrofit2.Call
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//import retrofit2.http.GET
//import retrofit2.http.Header
//import retrofit2.http.Query
//
//interface NaverSearchAPI {
//    @GET("v1/search/image")
//    fun getSearchImage(
//        @Header("X-Naver-Client-Id") id: String,
//        @Header("X-Naver-Client-Secret") secret: String,
//        @Query("query") query: String,
//        @Query("display") display: Int,
//        @Query("start") start: Int,
//        @Query("filter") filter: String,
//    ): Call<LocationImageDTO>
//
//    companion object {
//        private const val BASE_URL_NAVER_API = "https://openapi.naver.com/"
//        val CLIENT_ID = "AX3ivoMZXWoT6GhWHHmw"
//        val CLIENT_SECRET = "c5l9l5NZFP"
//
//
//        fun create(): NaverSearchAPI {
//            return Retrofit.Builder()
//                .baseUrl(BASE_URL_NAVER_API)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build()
//                .create(NaverSearchAPI::class.java)
//        }
//    }
//}