package com.hansung.capstone.post

import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hansung.capstone.CommunityService
import com.hansung.capstone.MyApplication
import com.hansung.capstone.databinding.ItemPostDetailRecommentsBinding
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.format.DateTimeFormatter

class PostReCommentsAdapter(private val comment: Comments) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>()  {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemPostDetailRecommentsBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PostReCommentsHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as PostReCommentsHolder
        viewHolder.bind(comment.reCommentList[position])
    }

    override fun getItemCount(): Int {
        return comment.reCommentList.count()
    }

    inner class PostReCommentsHolder(private val binding: ItemPostDetailRecommentsBinding) :
        RecyclerView.ViewHolder(binding.root){
            fun bind(items: ReComments){
                binding.reCommentContent.text = items.content
                binding.reCommentUserName.text = items.userNickname
                val createdDate = MyApplication.convertDate(items.createdDate).format(
                    DateTimeFormatter.ofPattern("MM/dd HH:mm"))
                binding.reCommentCreatedDate.text = createdDate

                val api = CommunityService.create()
                api.getProfileImage(items.userProfileImageId).enqueue(object :
                    Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        Log.d("결과", "성공 : ${response.body().toString()}")
                        val imageB = response.body()?.byteStream()
                        val bitmap = BitmapFactory.decodeStream(imageB)
                        binding.reCommentProfileImage.setImageBitmap(bitmap)
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Log.d("결과:", "실패 : $t")
                    }
                })
            }
        }
}