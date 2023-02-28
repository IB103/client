package com.hansung.capstone.modify

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.hansung.capstone.ModifyNickActivity
import com.hansung.capstone.MyApplication
import com.hansung.capstone.MyPageFragment
import com.hansung.capstone.retrofit.RepModifyNick
import com.hansung.capstone.retrofit.ReqModifyNick
import com.hansung.capstone.retrofit.RetrofitService
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

 class modifyNick() {
    //val modifyAvtivity: Activity? =(ModifyNickActivity().ModifyActivity)

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
    fun modifyNick(nick: String){
        val email=MyApplication.prefs.getString("email","")
        var putReqModifyNick = ReqModifyNick(email, nick)
        service.modifyNick(putReqModifyNick)
            .enqueue(object : Callback<RepModifyNick> {
                @SuppressLint("Range", "ResourceAsColor")
                override fun onResponse(
                    call: Call<RepModifyNick>,
                    response: Response<RepModifyNick>,
                ) {
                    if (response.isSuccessful) {
                        var result: RepModifyNick? = response.body()
                        if (response.code() == 200) {//수정해야함
                            if (result?.code == 100) {
                                Log.d("INFO", "닉네임 변경됨" + result?.toString())

                            } else {
                                Log.d("ERR", "닉네임 변경불가: " + result?.toString())

                            }
                        }
                    } else {
                        // 통신이 실패한 경우
                        Log.d("ERR modiftNick", "onResponse 실패")

                    }
                }
                override fun onFailure(call: Call<RepModifyNick>, t: Throwable) {
                    // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
                    Log.d("ERR modifyNick", "onFailure 에러: " + t.message.toString())

                }
            })

    }
}