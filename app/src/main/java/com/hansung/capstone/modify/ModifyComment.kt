package com.hansung.capstone.modify


import android.util.Log
import com.hansung.capstone.MyApplication
import com.hansung.capstone.board.Posts
import com.hansung.capstone.post.PostDetailActivity
import com.hansung.capstone.retrofit.ReqModifyComment
import com.hansung.capstone.retrofit.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ModifyComment {
   val service=RetrofitService.create()
    fun  modify(commentId:Long,content:String){
        val userId=MyApplication.prefs.getLong("userId",0)
        val accessToken:String=MyApplication.prefs.getString("accessToken","")
        val putReqModifyComment = ReqModifyComment(commentId,userId, content)
        this.service.modifyComment(accessToken = "Bearer $accessToken",putReqModifyComment)
            .enqueue(object : Callback<Posts> {
                override fun onResponse(
                    call: Call<Posts>,
                    response: Response<Posts>,
                ) { val body = response.body()
                    if(response.isSuccessful){
                        //if(body?.code==100) {
                            PostDetailActivity.getInstance()?.postComment()
                        PostDetailActivity.getInstance()?.commentSuccess(2)
                            Log.d("INF modifyComment", "댓글 수정 성공" + body.toString())
                        //}
                    }else {
                        //통신이 실패한 경우
                        Log.d("ERR modifyComment", "onResponse 실패" + body?.toString())
                    }
                }
                override fun onFailure(call: Call<Posts>, t: Throwable) {
                    Log.d("modifyComment:", "실패 : $t")
                }
            })
    }
}