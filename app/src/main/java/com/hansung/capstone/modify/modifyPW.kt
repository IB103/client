package com.hansung.capstone.modify

import android.annotation.SuppressLint
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.hansung.capstone.MyApplication
import com.hansung.capstone.retrofit.RepModifyPW
import com.hansung.capstone.retrofit.ReqModifyPW
import com.hansung.capstone.retrofit.RetrofitService
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class modifyPW {
    val gson: Gson = GsonBuilder()
        .setLenient()
        .create()
    var server_info = MyApplication.getUrl() //username password1 password2 email
    var clientBuilder = OkHttpClient.Builder()
    var retrofit = Retrofit.Builder().baseUrl("$server_info")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .client(clientBuilder.build())
        .build()
    var service = retrofit.create(RetrofitService::class.java)
    fun modifyPW(pw:String){
        val email=MyApplication.prefs.getString("id","")
        var postReqModifyPW = ReqModifyPW(email, pw)
        service.modifyPW(postReqModifyPW).enqueue(object : Callback<RepModifyPW> {
            @SuppressLint("Range", "ResourceAsColor")
            override fun onResponse(
                call: Call<RepModifyPW>,
                response: Response<RepModifyPW>,
            ) {
                if (response.isSuccessful) {
                    var result: RepModifyPW? = response.body()
                    if (response.code() == 200) {//수정해야함
                        if (result?.code == 100) {

                        } else {
                            Log.d("ERR", "PW변경 불가 " + result?.toString())
                        }
                    }
                } else {
                    // 통신이 실패한 경우
                    Log.d("ERR ModifyPW", "onResponse 실패")

                }
            }

            override fun onFailure(call: Call<RepModifyPW>, t: Throwable) {
                // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
                Log.d("ERR ModifyPw", "onFailure 에러: " + t.message.toString())
            }
        })


    }
}