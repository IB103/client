package com.hansung.capstone.post

import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hansung.capstone.CommunityService
import com.hansung.capstone.MyApplication
import com.hansung.capstone.databinding.ItemPostDetailCommentsBinding
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.format.DateTimeFormatter

class PostCommentsAdapter(private val resultDetailPost: ResultGetPostDetail) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemCount(): Int {
        return resultDetailPost.data.commentList.count()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            ItemPostDetailCommentsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostDetailHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as PostDetailHolder
        viewHolder.bind(resultDetailPost.data.commentList[position])
    }

    inner class PostDetailHolder(private val binding: ItemPostDetailCommentsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(items: Comments) {
            binding.CommentContent.text = items.content
            binding.CommentUserName.text = items.userNickname
            val createdDate = MyApplication.convertDate(items.createdDate).format(DateTimeFormatter.ofPattern("MM/dd HH:mm"))
            binding.CommentCreatedDate.text = createdDate

            val api = CommunityService.create()
            api.getProfileImage(items.userProfileImageId).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    Log.d("결과", "성공 : ${response.body().toString()}")
                    val imageB = response.body()?.byteStream()
                    val bitmap = BitmapFactory.decodeStream(imageB)
                    binding.CommentProfileImage.setImageBitmap(bitmap)
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.d("결과:", "실패 : $t")
                }
            })

        }
    }
}