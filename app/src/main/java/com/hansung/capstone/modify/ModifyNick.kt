package com.hansung.capstone.modify

import android.annotation.SuppressLint
import android.util.Log
import com.hansung.capstone.MyApplication
import com.hansung.capstone.retrofit.RepModifyNick
import com.hansung.capstone.retrofit.ReqModifyNick
import com.hansung.capstone.retrofit.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

 class ModifyNick {

   val service=RetrofitService.create()
    fun modifyNick(nick: String){
        val email=MyApplication.prefs.getString("email","")
        val putReqModifyNick = ReqModifyNick(email, nick)
        service.modifyNick(putReqModifyNick)
            .enqueue(object : Callback<RepModifyNick> {
                @SuppressLint("Range", "ResourceAsColor")
                override fun onResponse(
                    call: Call<RepModifyNick>,
                    response: Response<RepModifyNick>,
                ) {
                    if (response.isSuccessful) {
                        val result: RepModifyNick? = response.body()
                        if (response.code() == 200) {
                            if (result?.code == 100) {
                                Log.d("INFO", "닉네임 변경됨$result")

                            } else {
                                Log.d("ERR", "$result")

                            }
                        }
                    } else {
                        // 통신이 실패한 경우
                        Log.d("ERR modifyNick", "onResponse 실패")

                    }
                }
                override fun onFailure(call: Call<RepModifyNick>, t: Throwable) {
                    Log.d("ERR modifyNick", "onFailure 에러: " + t.message.toString())

                }
            })

    }
}