package com.hansung.capstone.post

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hansung.capstone.MyApplication
import com.hansung.capstone.databinding.ItemImageDetailBinding

class ImageDetailAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var imageList = listOf<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            ItemImageDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageDetailHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val image = imageList[position]
        val viewHolder = holder as ImageDetailHolder
        viewHolder.setImage(image)
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    inner class ImageDetailHolder(val binding: ItemImageDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun setImage(image: Int) {
            // 이미지 불러오기
            Glide.with(context)
                .load("${MyApplication.getUrl()}image/${image}") // 불러올 이미지 url
                .into(binding.photoView) // 이미지를 넣을 뷰
        }
    }
}