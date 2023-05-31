package com.hansung.capstone.post

import android.annotation.SuppressLint
import android.util.Log
import com.hansung.capstone.CommunityService
import com.hansung.capstone.MainActivity
import com.hansung.capstone.MyApplication
import com.hansung.capstone.databinding.ActivityPostDetailBinding
import com.hansung.capstone.retrofit.*
import kotlinx.android.synthetic.main.activity_post_detail.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PostComment(private val context: PostDetailActivity) {
   val service=RetrofitService.create()
    var result:RepComment? = null
    val api = CommunityService.create()
    fun postComment(str: String, postId: Long,binding:ActivityPostDetailBinding) {
        val userId = MyApplication.prefs.getLong("userId", 0)
        val accessToken= MyApplication.prefs.getString("accessToken", "")
        val postReqComment = ReqComment(postId, userId, str)
        service.postComment(accessToken = "Bearer $accessToken",postReqComment).enqueue(object : Callback<RepComment> {
            @SuppressLint("Range", "ResourceAsColor")
            override fun onResponse(call: Call<RepComment>, response: Response<RepComment>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    if (response.code() == 201) {
                            Log.d("INFO comment", "success $result")
                            context.commentSuccess(1)
                            MainActivity.getInstance()?.setCommentCount(1)
                            Log.d("commentCount","${ MainActivity.getInstance()?.getCommentCount()}")
                            postComments(postId,binding)
                    }
                } else {
                    // 통신이 실패한 경우
                    Log.d("ERR comment", "onResponse 실패" + result?.toString())
                }
            }
            override fun onFailure(call: Call<RepComment>, t: Throwable) {
                Log.d("ERR comment", "onFailure 에러: " + t.message.toString())
            }
        })
    }
    fun postComments(postId:Long,binding:ActivityPostDetailBinding){
        api.getPostDetail(postId)
            .enqueue(object : Callback<ResultGetPostDetail> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(
                    call: Call<ResultGetPostDetail>,
                    response: Response<ResultGetPostDetail>,
                ) {
                    val body = response.body()
                    var count = 0
                    for (i in body?.data?.commentList!!) {
                        count += i.reCommentList.size
                    }
                    for (i in 0 until body.data.commentList.size) {
                        val j:Long=-1
                        if(body.data.commentList[i].userId!=j)
                            ++count
                    }
                    //count += body.data.commentList.size
                    binding.CommentCount.text = count.toString()
                    // 이미지 등록
                    binding.PostDetailComment.adapter =
                        PostCommentsAdapter(body,context)
                }
                override fun onFailure(call: Call<ResultGetPostDetail>, t: Throwable) {
                    Log.d("getPostDetail:", "실패 : $t")
                }
            })
    }

}