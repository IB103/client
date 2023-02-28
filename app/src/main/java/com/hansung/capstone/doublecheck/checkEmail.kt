package com.hansung.capstone.doublecheck

import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.hansung.capstone.MyApplication
import com.hansung.capstone.R
import com.hansung.capstone.retrofit.RepDoubleCheckID
import com.hansung.capstone.retrofit.RetrofitService
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class checkEmail {
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
    fun checkEmail(email:String,commentEmail:TextView,submitBt:Button){
        service.doublecheckId(email).enqueue(object : Callback<RepDoubleCheckID> {
            @SuppressLint("Range", "ResourceAsColor")
            override fun onResponse(
                call: Call<RepDoubleCheckID>,
                response: Response<RepDoubleCheckID>
            ) {
                if (response.isSuccessful) {
                    val result: RepDoubleCheckID? = response.body()
                    if (response.code() == 200) {//수정해야함
                        if (result?.code==100) {
                            Log.d("INFO", "ID사용가능: " + result?.toString())
                            commentEmail.text = "ID 사용 가능합니다"
                            commentEmail.setTextColor(Color.parseColor("#04B431"))
                            submitBt.isEnabled = true
                        } else {
                            Log.d("ERR", "ID중복: " + result?.toString())
                            commentEmail.text = "ID 사용 불가능합니다"
                            commentEmail.setTextColor(Color.parseColor("#FF0000"))
                            submitBt.isEnabled = true
                        }
                    }
                } else {
                    // 통신이 실패한 경우
                    Log.d("ERR checkEmail", "onResponse 실패")
                    submitBt.isEnabled = false
                }
            }
            override fun onFailure(call: Call<RepDoubleCheckID>, t: Throwable) {
                // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
                Log.d("ERR checkEmail", "onFailure 에러: " + t.message.toString())
                submitBt.isEnabled = false
            }
        })

    }
}