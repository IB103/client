package com.hansung.capstone.delete

import android.annotation.SuppressLint
import android.util.Log
import com.hansung.capstone.CommunityService
import com.hansung.capstone.MainActivity
import com.hansung.capstone.MyApplication
import com.hansung.capstone.board.ResDelete
import com.hansung.capstone.databinding.ActivityPostDetailBinding
import com.hansung.capstone.post.PostCommentsAdapter
import com.hansung.capstone.post.PostDetailActivity
import com.hansung.capstone.post.ResultGetPostDetail
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DeleteComment()  {
    val api = CommunityService.create()
    var DeleteCount=MyApplication.prefs.getInt("deleteCount",0)
    fun delete(accesstoken:String,userId:Int,commentId:Long){
        Log.d("myapplicationUserId","${MyApplication.prefs.getInt("userId",0)}")
        Log.d("userid","$userId")
        Log.d("commentId","$commentId")
        api.deleteComment(accessToken = "Bearer ${accesstoken}",userId.toLong(), commentId)
            .enqueue(object : Callback<ResDelete> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(
                    call: Call<ResDelete>,
                    response: Response<ResDelete>,
                ) { val body = response.body()
                    if(response.isSuccessful){
                        if(body?.code==100) {
                            Log.d("INFO deletecomment", "댓글삭제 성공" + body.toString())
                            DeleteCount++
                            MyApplication.prefs.setInt("deleteCount",DeleteCount)
                           // MainActivity.getInstance()?.setChangedPostCheck(true)
                            Log.d("deleteCount","${MyApplication.prefs.getInt("deleteCount",0)}")
                            PostDetailActivity.getInstance()?.postcomment()
                        }
                    }else {
                        // 통신이 실패한 경우
                        Log.d("ERR deletecomment", "onResponse 실패" + body?.toString())
                    }

                }
                override fun onFailure(call: Call<ResDelete>, t: Throwable) {
                    Log.d("deltecomment:", "실패 : $t")
                }
            })


    }
}