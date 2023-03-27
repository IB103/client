package com.hansung.capstone.delete

import android.annotation.SuppressLint
import android.util.Log
import com.hansung.capstone.CommunityService
import com.hansung.capstone.MainActivity
import com.hansung.capstone.MyApplication
import com.hansung.capstone.board.ResDelete
import com.hansung.capstone.board.ResDeleteReComment
import com.hansung.capstone.post.PostDetailActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DeleteRecomment {
    val api = CommunityService.create()
    var DeleteCount=MyApplication.prefs.getInt("deleteCount",0)
    fun delete(accesstoken:String,userId:Int,recommentId:Long){
        Log.d("myapplicationUserId","${MyApplication.prefs.getInt("userId",0)}")
        Log.d("userid","$userId")
        Log.d("recommentId","$recommentId")
        api.deleteReComment(accessToken = "Bearer ${accesstoken}",userId.toLong(), recommentId)
            .enqueue(object : Callback<ResDeleteReComment> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(
                    call: Call<ResDeleteReComment>,
                    response: Response<ResDeleteReComment>,
                ) { val body = response.body()
                    if(response.isSuccessful){
                        if(body?.code==100) {
                            MyApplication.prefs.setInt("deleteCount",++DeleteCount)
                           // MainActivity.getInstance()?.setChangedPostCheck(true)
                            PostDetailActivity.getInstance()?.postcomment()
                            Log.d("INFO deletecReomment", "대댓글삭제 성공" + body.toString())
                        }
                    }else {
                        // 통신이 실패한 경우
                        Log.d("ERR deletRecomment", "onResponse 실패" + body?.toString())
                    }
                }
                override fun onFailure(call: Call<ResDeleteReComment>, t: Throwable) {
                    Log.d("delteRecomment:", "실패 : $t")
                }
            })


    }
}