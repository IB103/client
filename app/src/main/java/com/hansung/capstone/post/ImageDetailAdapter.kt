package com.hansung.capstone.post

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hansung.capstone.MyApplication
import com.hansung.capstone.R
import com.hansung.capstone.databinding.ActivityImageFullScreenBinding
import com.hansung.capstone.databinding.ItemImageDetailBinding
import kotlinx.coroutines.*
import java.util.*

class ImageDetailAdapter(
    private val context: Context,
    val binding: ActivityImageFullScreenBinding
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var imageList = listOf<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            ItemImageDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageDetailHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as ImageDetailHolder
        viewHolder.setImage(imageList[position])
    }

    override fun getItemCount(): Int {
        return imageList.size
    }


    inner class ImageDetailHolder(val binding: ItemImageDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun setImage(image: Int) {
            Glide.with(context)
                .load("${MyApplication.getUrl()}image/${image}") // 불러올 이미지 url
                .placeholder(R.drawable.no_image)
                .error(R.drawable.no_image)
                .into(binding.photoView) // 이미지를 넣을 뷰
        }
    }
}