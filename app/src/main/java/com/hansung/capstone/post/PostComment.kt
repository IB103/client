package com.hansung.capstone.post

import android.annotation.SuppressLint
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.hansung.capstone.MyApplication
import com.hansung.capstone.retrofit.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PostComment {
    val gson: Gson = GsonBuilder()
        .setLenient()
        .create()
    private var serverInfo = MyApplication.getUrl() //username password1 password2 email
    var clientBuilder = OkHttpClient.Builder()
    var retrofit = Retrofit.Builder().baseUrl("$serverInfo")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .client(clientBuilder.build())
        .build()!!
    var service = retrofit.create(RetrofitService::class.java)!!
    var result:RepComment? = null
    var resultCode:Int = 0
    val success="success"
    val fail="fail"
    fun postComment(str: String, postId: Int) {
        Log.d("postcomment", "#####################")
        val userId = MyApplication.prefs.getInt("userId", 0)
        val postReqComment = ReqComment(postId, userId, str)
        service.postComment(postReqComment).enqueue(object : Callback<RepComment> {
            @SuppressLint("Range", "ResourceAsColor")
            override fun onResponse(call: Call<RepComment>, response: Response<RepComment>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    if (response.code() == 201) { //수정해야함
                        if (result?.code == 100) {
                            Log.d("INFO comment", "댓글작성 성공" + result.toString())
                            MyApplication.prefs.setInt("resultCode",100)
                        } else {
                            Log.d("ERR", "댓글 작성 실패 " + result.toString())
                            MyApplication.prefs.setInt("resultCode",0)
                        }
                    }
                } else {
                    // 통신이 실패한 경우
                    Log.d("ERR comment", "onResponse 실패" + result?.toString())
                    MyApplication.prefs.setInt("resultCode",0)
                }
            }

            override fun onFailure(call: Call<RepComment>, t: Throwable) {
                // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
                Log.d("ERR comment", "onFailure 에러: " + t.message.toString())
                MyApplication.prefs.setInt("resultCode",0)
            }
        })

    }

//    fun postComment(str:String,postId:Int):String{
//        Log.d("postcomment","#####################")
//        val userId=MyApplication.prefs.getInt("userId",0)
//        var postReqComment = ReqComment(postId, userId,str)
//        service.postComment(postReqComment)
//            .enqueue(object : Callback<RepComment> {
//                @SuppressLint("Range", "ResourceAsColor")
//                override fun onResponse(
//                    call: Call<RepComment>,
//                    response: Response<RepComment>,
//                ) { if (response.isSuccessful) {
//                        result= response.body()
//                        if (response.code() == 201) {//수정해야함
//                            if (result?.code == 100) {
//                                resultCode=100
//                                Log.d("INFO comment", "댓글작성 성공" + result?.toString())
//
//                            } else {
//                                Log.d("ERR", "댓글 작성 실패 " + result?.toString())
//                            }
//                        }
//                    } else {
//                        // 통신이 실패한 경우
//                        Log.d("ERR comment", "onResponse 실패"+result?.toString())
//                    }
//                }
//                override fun onFailure(call: Call<RepComment>, t: Throwable) {
//                    // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
//                    Log.d("ERR comment", "onFailure 에러: " + t.message.toString())
//                }
//            })
//        Log.d("resultCode","${resultCode}")
//        Log.d("resultCode2","${result?.code}")
//
//        return if(resultCode==100)
//            "success"
//        else
//            "fail"
//    }
}