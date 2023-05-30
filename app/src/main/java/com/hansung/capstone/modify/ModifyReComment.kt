package com.hansung.capstone.modify

import android.annotation.SuppressLint
import android.util.Log
import com.hansung.capstone.MyApplication
import com.hansung.capstone.board.Posts
import com.hansung.capstone.post.PostDetailActivity
import com.hansung.capstone.retrofit.ReqModifyReComment
import com.hansung.capstone.retrofit.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ModifyReComment {
    var service = RetrofitService.create()
    fun  modify(reCommentId:Long, content:String){
        val userId= MyApplication.prefs.getLong("userId",0)
        val accessToken= MyApplication.prefs.getString("accessToken","")
        val putReqModifyReComment = ReqModifyReComment(reCommentId,userId, content)
        service.modifyRecomment(accessToken = "Bearer $accessToken",putReqModifyReComment)
            .enqueue(object : Callback<Posts> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(
                    call: Call<Posts>,
                    response: Response<Posts>,
                ) { val body = response.body()
                    if(response.isSuccessful){
                        PostDetailActivity.getInstance()?.postComment()
                        PostDetailActivity.getInstance()?.commentSuccess(2)
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