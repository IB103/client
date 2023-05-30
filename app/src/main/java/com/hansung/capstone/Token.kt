package com.hansung.capstone


import android.util.Log
import com.hansung.capstone.retrofit.RespondToken
import com.hansung.capstone.retrofit.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime

class Token {
    private val firstValue=MyApplication.prefs.getString("accessToken","")
    private val firstValue2=MyApplication.prefs.getString("refreshToken","")
    var service = RetrofitService.create()
    private val expiresIn = 30 * 60 * 1000//300//30분으로 수정->30 * 60 * 1000
    fun set(){
        val currentTime = System.currentTimeMillis()
        MyApplication.prefs.setLong("tokenTime",currentTime)
    }
    fun checkToken():Boolean{
        val tokenTime=MyApplication.prefs.getLong("tokenTime",0)
        val expirationTime = tokenTime + expiresIn
        val isExpired = expirationTime < System.currentTimeMillis()
        if(isExpired){
           return true
        }
        return false
    }

    fun issueNewToken(callback: (() -> Unit)?):String {
        val currentTime = System.currentTimeMillis()
        service.reissue(
            accessToken = "Bearer ${MyApplication.prefs.getString("accessToken","")}",
            refreshToken = MyApplication.prefs.getString("refreshToken","")
        ).enqueue(object : Callback<RespondToken> {
            override fun onResponse(call: Call<RespondToken>, response: Response<RespondToken>) {
                if (response.isSuccessful) {
                    val result: RespondToken = response.body()!!
                    if (response.code() == 200) {
                        Log.d("test", "성공:$result")
                        MyApplication.prefs.setLong("tokenTime", currentTime)
                        MyApplication.prefs.setString("accessToken", result.data.accessToken)
                        MyApplication.prefs.setString("refreshToken", result.data.refreshToken)
                        Log.d("diff","${firstValue.equals(result.data.accessToken)}")
                        Log.d("diff2","${firstValue2.equals(result.data.refreshToken)}")
                        if (callback != null) {
                            callback()
                        } // 토큰 발급 후 콜백 함수 실행
                    }
                } else {
                    Log.d("ERR", "onResponse test 실패 ${response.body().toString()}")
                }
            }

            override fun onFailure(call: Call<RespondToken>, t: Throwable) {
                Log.d("ERR", "onFailure 에러: " + t.message.toString())
            }
        })
        return MyApplication.prefs.getString("accessToken","")
    }

}