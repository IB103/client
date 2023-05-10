package com.hansung.capstone.doublecheck

import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.hansung.capstone.retrofit.RepDoubleCheckID
import com.hansung.capstone.retrofit.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CheckEmail {
    val service=RetrofitService.create()
    fun checkEmail(email:String,commentEmail:TextView,submitBt:Button){
        service.doublecheckId(email).enqueue(object : Callback<RepDoubleCheckID> {
            @SuppressLint("Range", "ResourceAsColor", "SetTextI18n")
            override fun onResponse(
                call: Call<RepDoubleCheckID>,
                response: Response<RepDoubleCheckID>
            ) {
                if (response.isSuccessful) {
                    val result: RepDoubleCheckID? = response.body()
                    if (response.code() == 200) {//수정해야함
                        if (result?.code==100) {
                            Log.d("Success", "$result")
                            commentEmail.text = "ID 사용 가능합니다"
                            commentEmail.setTextColor(Color.parseColor("#04B431"))
                            submitBt.isEnabled = true
                        } else {
                            Log.d("ERR", "$result")
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
                Log.d("ERR checkEmail", "onFailure 에러: " + t.message.toString())
                submitBt.isEnabled = false
            }
        })

    }
}