package com.hansung.capstone.modify

import android.annotation.SuppressLint
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.hansung.capstone.CommunityService
import com.hansung.capstone.MyApplication
import com.hansung.capstone.board.Posts
import com.hansung.capstone.board.ResultGetPosts
import com.hansung.capstone.post.PostDetailActivity
import com.hansung.capstone.retrofit.ReqModifyComment
import com.hansung.capstone.retrofit.ReqModifyReComment
import com.hansung.capstone.retrofit.RetrofitService
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ModifyRecomment {
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
    fun  modify(recommentId:Long,content:String){
        var userId= MyApplication.prefs.getInt("userId",0)
        var accesstoken= MyApplication.prefs.getString("accesstoken","")
        val putReqModifyRecomment = ReqModifyReComment(recommentId,userId.toLong(), content)
        service.modifyRecomment(accessToken = "Bearer ${accesstoken}",putReqModifyRecomment)
            .enqueue(object : Callback<Posts> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(
                    call: Call<Posts>,
                    response: Response<Posts>,
                ) { val body = response.body()
                    if(response.isSuccessful){
                        PostDetailActivity.getInstance()?.postcomment()
                        Log.d("INF modifyReComment", "대댓글 수정 성공" + body.toString())
                    }else {
                        // 통신이 실패한 경우
                        Log.d("ERR modifyReComment", "onResponse 실패" + body?.toString())
                    }
                }
                override fun onFailure(call: Call<Posts>, t: Throwable) {
                    Log.d("modifyReComment:", "실패 : $t")
                }
            })
    }
}