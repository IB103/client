package com.hansung.capstone



import android.util.Log
import com.hansung.capstone.retrofit.RespondToken
import com.hansung.capstone.retrofit.RetrofitService
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.util.*

class Token {
    var service = RetrofitService.create()
    fun set(){
            val token=MyApplication.prefs.getString("accessToken","")
        val payload = decodeUnsignedJWT(token)
        if (payload.isNotEmpty()) {
            val expirationTime = extractExpirationFromPayload(payload)
            MyApplication.prefs.setLong("tokenTime",expirationTime)
            println("만료 시간: $expirationTime")
        } else {
            println("JWT 디코딩 실패")
        }

    }
    private fun decodeUnsignedJWT(token: String): String {
        val parts = token.split(".")
        if (parts.size == 3) {
            return String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8)
        }
        return ""
    }

    private fun extractExpirationFromPayload(payload: String): Long {
        val jsonPayload = payload.trim()
        val jsonObject = JSONObject(jsonPayload)
        return jsonObject.optLong("exp", 0)
    }

    fun checkToken():Boolean{
        val expirationTime=MyApplication.prefs.getLong("tokenTime",0)
        println("시간 확인 $expirationTime")
        val currentTimestamp = Instant.now().epochSecond
        println("지금 $currentTimestamp")
        println("result ${currentTimestamp >= expirationTime}")
        return currentTimestamp >= expirationTime// true 만료  //false  유효
    }

    fun issueNewToken(callback: (() -> Unit)?) {
        println("$$$$$$$$$$$$")
        service.reissue(
            accessToken = "Bearer ${MyApplication.prefs.getString("accessToken","")}",
            refreshToken = MyApplication.prefs.getString("refreshToken","")
        ).enqueue(object : Callback<RespondToken> {
            override fun onResponse(call: Call<RespondToken>, response: Response<RespondToken>) {
                if (response.isSuccessful) {
                    val result: RespondToken = response.body()!!
                        Log.d("test", "성공:$result")
                        MyApplication.prefs.setString("accessToken", result.data.accessToken)
                        MyApplication.prefs.setString("refreshToken", result.data.refreshToken)
                        set()
                        if (callback != null) {
                            callback()
                        } // 토큰 발급 후 콜백 함수 실행

                } else {
                    Log.d("ERR", "onResponse test 실패 ${response.body().toString()}")
                }
            }

            override fun onFailure(call: Call<RespondToken>, t: Throwable) {
                Log.d("ERR", "onFailure 에러: " + t.message.toString())
            }
        })

    }

}