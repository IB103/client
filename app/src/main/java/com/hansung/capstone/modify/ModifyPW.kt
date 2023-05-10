package com.hansung.capstone.modify

import android.annotation.SuppressLint
import android.util.Log
import com.hansung.capstone.MyApplication
import com.hansung.capstone.retrofit.RepModifyPW
import com.hansung.capstone.retrofit.ReqModifyPW
import com.hansung.capstone.retrofit.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ModifyPW {

    val service = RetrofitService.create()
    fun modifyPW(pw:String){
        val email=MyApplication.prefs.getString("id","")
        val postReqModifyPW = ReqModifyPW(email, pw)
        service.modifyPW(postReqModifyPW).enqueue(object : Callback<RepModifyPW> {
            @SuppressLint("Range", "ResourceAsColor")
            override fun onResponse(
                call: Call<RepModifyPW>,
                response: Response<RepModifyPW>,
            ) {
                if (response.isSuccessful) {

                    val result: RepModifyPW? = response.body()
                    if (response.code() == 200) {
                        if (result?.code == 100) {

                        } else {
                            Log.d("ERR", "$result")
                        }
                    }
                } else {
                    // 통신이 실패한 경우
                    Log.d("ERR ModifyPW", "onResponse 실패")

                }
            }

            override fun onFailure(call: Call<RepModifyPW>, t: Throwable) {
                Log.d("ERR ModifyPw", "onFailure 에러: " + t.message.toString())
            }
        })


    }
}