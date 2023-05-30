package com.hansung.capstone.doublecheck

import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.hansung.capstone.retrofit.RepDoubleCheckNickName
import com.hansung.capstone.retrofit.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CheckNick {
  val service=RetrofitService.create()
    fun doubleCheckNick(nick:String, commentNick: TextView, changeBt: Button?){
        service.doublecheckNickName(nick)
            .enqueue(object : Callback<RepDoubleCheckNickName> {
                @SuppressLint("Range", "ResourceAsColor")
                override fun onResponse(
                    call: Call<RepDoubleCheckNickName>,
                    response: Response<RepDoubleCheckNickName>
                ) {
                    if (response.isSuccessful) {
                        val result: RepDoubleCheckNickName? = response.body()
                        if (response.code() == 200) {
                            if (result?.code==100) {
                                Log.d("INFO", "$result")
                                commentNick.text = "닉네임 사용 가능합니다."
                                commentNick.setTextColor(Color.parseColor("#04B431"))
                                if (changeBt != null) {
                                    changeBt.isEnabled = true
                                }
                            } else {
                                Log.d("ERR", "닉네임 중복: " + result?.toString())
                                commentNick.text = "닉네임 사용 불가능합니다."
                                commentNick.setTextColor(Color.parseColor("#FF0000"))
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
                    Log.d("ERR", "onFailure 에러: " + t.message.toString())
                }
            })


    }


}