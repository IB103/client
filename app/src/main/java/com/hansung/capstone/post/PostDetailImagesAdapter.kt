package com.hansung.capstone.post

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hansung.capstone.MyApplication
import com.hansung.capstone.R
import com.hansung.capstone.databinding.ItemPostDetailImagesBinding

class PostDetailImagesAdapter(private val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var imageList = listOf<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            ItemPostDetailImagesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val image = imageList[position]
        val viewHolder = holder as ImageHolder
        viewHolder.setImage(image, position)
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    inner class ImageHolder(val binding: ItemPostDetailImagesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun setImage(image: Int, position: Int) {
            Glide.with(context)
                .load("${MyApplication.getUrl()}image/${image}") // 불러올 이미지 url
                .placeholder(R.drawable.no_image)
                .error(R.drawable.no_image)
                .centerCrop()
                .into(binding.postImage) // 이미지를 넣을 뷰

            itemView.setOnClickListener {
                PostDetailActivity.getInstance()?.goImageDetail(imageList, position)
            }
        }
    }
}