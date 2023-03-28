package com.hansung.capstone.map

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MapboxDirectionAPI {
    @GET("directions/v5/mapbox/cycling/{longitude},{latitude};{longitude2},{latitude2}")
    fun getSearchDirection(
        @Path("longitude") longitude: Double,
        @Path("latitude") latitude: Double,
        @Path("longitude2") longitude2: Double,
        @Path("latitude2") latitude2: Double,
        @Query("geometries") geometries: String,
        @Query("overview") overview: String,
        @Query("access_token") access_token: String,
    ): Call<ResultSearchDirections>

    companion object {
        private const val BASE_URL_MAPBOX_API = "https://api.mapbox.com/"

        fun create(): MapboxDirectionAPI {
            return Retrofit.Builder()
                .baseUrl(BASE_URL_MAPBOX_API)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(MapboxDirectionAPI::class.java)
        }
    }
}