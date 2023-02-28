package com.hansung.capstone.doublecheck

import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.hansung.capstone.MyApplication
import com.hansung.capstone.retrofit.RepDoubleCheckNickName
import com.hansung.capstone.retrofit.RetrofitService
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class checkNick() {
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

    fun doublecheckNick(nick:String, commentNick: TextView, changeBt: Button?){
        service.doublecheckNickName(nick)
            .enqueue(object : Callback<RepDoubleCheckNickName> {
                @SuppressLint("Range", "ResourceAsColor")
                override fun onResponse(
                    call: Call<RepDoubleCheckNickName>,
                    response: Response<RepDoubleCheckNickName>
                ) {
                    if (response.isSuccessful) {
                        val result: RepDoubleCheckNickName? = response.body()
                        if (response.code() == 200) {//수정해야함
                            if (result?.code==100) {
                                Log.d("INFO", "닉네임 사용가능: $result")
                                commentNick.text = "닉네임 사용 가능합니다"
                                commentNick.setTextColor(Color.parseColor("#04B431"))
                                //binding.nicknameConfirm.setTextColor(R.color.green)
                                if (changeBt != null) {
                                    changeBt.isEnabled = true
                                }
                            } else {
                                Log.d("ERR", "닉네임 중복: " + result?.toString())
                                commentNick.setText("닉네임 사용 불가능합니다")
                                commentNick.setTextColor(Color.parseColor("#FF0000"))
                                //binding.nicknameConfirm.setTextColor(R.color.red)
                                if (changeBt != null) {
                                    changeBt.isEnabled = false
                                }
                            }
                        }
                    } else {
                        // 통신이 실패한 경우
                        Log.d("ERR", "onResponse 실패")
                        if (changeBt != null) {
                            changeBt.isEnabled = false
                        }
                    }
                }

                override fun onFailure(call: Call<RepDoubleCheckNickName>, t: Throwable) {
                    // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
                    Log.d("ERR", "onFailure 에러: " + t.message.toString())
                    // binding.changeBt.setEnabled(false)
                }
            })


    }


}