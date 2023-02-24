package com.hansung.capstone.board

import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hansung.capstone.CommunityService
import com.hansung.capstone.MainActivity
import com.hansung.capstone.MyApplication
import com.hansung.capstone.databinding.ItemPostListBinding
import com.hansung.capstone.databinding.ItemPostListNoImageBinding
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.format.DateTimeFormatter

// 게시판에 들어갈 item type 설정
const val post_type1 = 1
const val post_type2 = 2

class BoardAdapter(private val resultGetPosts: ResultGetPosts) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val api = CommunityService.create()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            post_type1 -> {
                val binding =
                    ItemPostListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                BoardHolderType1(binding)
            }
            else -> {
                val binding = ItemPostListNoImageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                BoardHolderType2(binding)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (resultGetPosts.data[position].imageId.isEmpty())
            resultGetPosts.data[position].postType = 2
        else resultGetPosts.data[position].postType = 1
        return resultGetPosts.data[position].postType
    }

    override fun getItemCount(): Int {
        return resultGetPosts.data.count()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (resultGetPosts.data[position].imageId.size) {
            0 -> {
                (holder as BoardHolderType2).bind(resultGetPosts.data[position])
            }
            else -> {
                (holder as BoardHolderType1).bind(resultGetPosts.data[position])
            }
        }
    }

    inner class BoardHolderType1(private val binding: ItemPostListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(items: Posts) {
            var count = 0
            val convertedDate = MyApplication.convertDate(items.createdDate)
            val createdDate = convertedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            binding.PostUserName.text = items.nickname
            binding.BoardTitle.text = items.title
            binding.BoardContent.text = items.content
            binding.BoardDate.text = createdDate.toString()
            binding.ImageCount.text = items.imageId.size.toString()
            binding.HeartCount.text = items.postVoterId.size.toString()
            for (i in items.commentList){
                count+=i.reCommentList.size
            }
            count+=items.commentList.size
            binding.CommentCount.text = count.toString()

            if (items.imageId.isNotEmpty()) {
                api.getImage(items.imageId[0].toLong()).enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        Log.d("결과", "성공 : ${response.body().toString()}")
                        val imageB = response.body()?.byteStream()
                        val bitmap = BitmapFactory.decodeStream(imageB)
                        binding.BoardImageView.setImageBitmap(bitmap)
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Log.d("결과:", "실패 : $t")
                    }
                })
                api.getProfileImage(items.authorProfileImageId)
                    .enqueue(object : Callback<ResponseBody> {
                        override fun onResponse(
                            call: Call<ResponseBody>,
                            response: Response<ResponseBody>
                        ) {
                            Log.d("결과", "성공 : ${response.body().toString()}")
                            val imageB = response.body()?.byteStream()
                            val bitmap = BitmapFactory.decodeStream(imageB)
                            binding.BoardProfileImage.setImageBitmap(bitmap)
                        }

                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            Log.d("결과:", "실패 : $t")
                        }
                    })
            }

            itemView.setOnClickListener {
                MainActivity.getInstance()?.goPostDetail(items)
            }
        }
    }

    inner class BoardHolderType2(private val binding: ItemPostListNoImageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(items: Posts) {
            val convertedDate = MyApplication.convertDate(items.createdDate)
            val createdDate = convertedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            binding.PostUserName.text = items.nickname
            binding.BoardTitle.text = items.title
            binding.BoardContent.text = items.content
            binding.BoardDate.text = createdDate.toString()
            binding.CommentCount.text = items.commentList.size.toString()
//            binding.HeartCount.text =
            api.getProfileImage(items.authorProfileImageId)
                .enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        Log.d("결과", "성공 : ${response.body().toString()}")
                        val imageB = response.body()?.byteStream()
                        val bitmap = BitmapFactory.decodeStream(imageB)
                        binding.BoardProfileImage.setImageBitmap(bitmap)
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Log.d("결과:", "실패 : $t")
                    }
                })

            itemView.setOnClickListener {
                MainActivity.getInstance()?.goPostDetail(items)
            }
        }
    }
}