package com.hansung.capstone.board

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hansung.capstone.MainActivity
import com.hansung.capstone.MyApplication
import com.hansung.capstone.databinding.ItemPostListBinding
import com.hansung.capstone.databinding.ItemPostListNoImageBinding
import java.time.format.DateTimeFormatter

// 게시판에 들어갈 item type 설정
const val post_type1 = 1
//const val post_type2 = 2
//const val post_type3 = 3

class BoardAdapter(private val resultGetPosts: ResultGetPosts, private val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
            for (i in items.commentList) {
                count += i.reCommentList.size
            }
            count += items.commentList.size
            binding.CommentCount.text = count.toString()

            Glide.with(context)
                .load("${MyApplication.getUrl()}image/${items.imageId[0]}") // 불러올 이미지 url
                .override(100, 100)
                .centerCrop()
                .into(binding.BoardImageView) // 이미지를 넣을 뷰

            Glide.with(context)
                .load("${MyApplication.getUrl()}profile-image/${items.authorProfileImageId}") // 불러올 이미지 url
                .override(100, 100)
//                    .placeholder() // 이미지 로딩 시작하기 전 표시할 이미지
//                    .error(defaultImage) // 로딩 에러 발생 시 표시할 이미지
//                    .fallback(defaultImage) // 로드할 url 이 비어있을(null 등) 경우 표시할 이미지
                .circleCrop() // 동그랗게 자르기
                .into(binding.BoardProfileImage) // 이미지를 넣을 뷰

            itemView.setOnClickListener {
                MainActivity.getInstance()?.goPostDetail(items)
            }
        }
    }

    inner class BoardHolderType2(private val binding: ItemPostListNoImageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(items: Posts) {
            var count = 0
            val convertedDate = MyApplication.convertDate(items.createdDate)
            val createdDate = convertedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            binding.PostUserName.text = items.nickname
            binding.BoardTitle.text = items.title
            binding.BoardContent.text = items.content
            binding.BoardDate.text = createdDate.toString()
            binding.HeartCount.text = items.postVoterId.size.toString()
            for (i in items.commentList) {
                count += i.reCommentList.size
            }
            count += items.commentList.size
            binding.CommentCount.text = count.toString()

            Glide.with(context)
                .load("${MyApplication.getUrl()}profile-image/${items.authorProfileImageId}") // 불러올 이미지 url
                .override(100, 100)
                .circleCrop() // 동그랗게 자르기
                .into(binding.BoardProfileImage) // 이미지를 넣을 뷰

            itemView.setOnClickListener {
                MainActivity.getInstance()?.goPostDetail(items)
            }
        }
    }

//    inner class BoardHolderType3(private val binding: ItemPostListLoadingBinding) :
//        RecyclerView.ViewHolder(binding.root) {
//
//    }
}