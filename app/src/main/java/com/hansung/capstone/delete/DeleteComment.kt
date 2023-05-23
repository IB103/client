package com.hansung.capstone.delete

import android.annotation.SuppressLint
import android.util.Log
import com.hansung.capstone.CommunityService
import com.hansung.capstone.MainActivity
import com.hansung.capstone.MyApplication
import com.hansung.capstone.board.ResDelete
import com.hansung.capstone.post.PostDetailActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DeleteComment {
    val api = CommunityService.create()
    fun delete(accessToken:String, userId:Long, commentId:Long){
        api.deleteComment(accessToken = "Bearer $accessToken",userId, commentId)
            .enqueue(object : Callback<ResDelete> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(
                    call: Call<ResDelete>,
                    response: Response<ResDelete>,
                ) { val body = response.body()
                    if(response.isSuccessful){
                        if(body?.code==100) {
                            Log.d("INFO deleteComment", "$body")
                            MainActivity.getInstance()?.setDeletedCommentCount(1)
                            PostDetailActivity.getInstance()?.postComment()
                            PostDetailActivity.getInstance()?.commentSuccess(3)
                        }
                    }else {
                        // 통신이 실패한 경우
                        Log.d("ERR deleteComment", "onResponse 실패" + body?.toString())
                    }

                }
                override fun onFailure(call: Call<ResDelete>, t: Throwable) {
                    Log.d("deleteComment:", "실패 : $t")
                }
            })


    }
}