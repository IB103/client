package com.hansung.capstone.delete

import android.annotation.SuppressLint
import android.util.Log
import com.hansung.capstone.CommunityService
import com.hansung.capstone.MainActivity
import com.hansung.capstone.MyApplication
import com.hansung.capstone.board.ResDeleteReComment
import com.hansung.capstone.post.PostDetailActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DeleteReComment {
    val api = CommunityService.create()

    fun delete(accessToken:String, userId:Long, reCommentId:Long){
        Log.d("myapplicationUserId","${MyApplication.prefs.getLong("userId",0)}")
        Log.d("userid","$userId")
        api.deleteReComment(accessToken = "Bearer $accessToken",userId, reCommentId)
            .enqueue(object : Callback<ResDeleteReComment> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(
                    call: Call<ResDeleteReComment>,
                    response: Response<ResDeleteReComment>,
                ) { val body = response.body()
                    if(response.isSuccessful){
                        if(body?.code==100) {
                            MainActivity.getInstance()?.setDeletedCommentCount(1)
                            PostDetailActivity.getInstance()?.postComment()
                            PostDetailActivity.getInstance()?.commentSuccess(3)
                            Log.d("INFO deleteReComment", "$body")
                        }
                    }else {
                        // 통신이 실패한 경우
                        Log.d("ERR deleteReComment", "onResponse 실패" + body?.toString())
                    }
                }
                override fun onFailure(call: Call<ResDeleteReComment>, t: Throwable) {
                    Log.d("deleteReComment:", "실패 : $t")
                }
            })


    }
}