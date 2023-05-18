package com.hansung.capstone.recommend

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hansung.capstone.MyApplication
import com.hansung.capstone.databinding.ItemRecommendWaypointsRecyclerviewBinding
import com.hansung.capstone.retrofit.ImageInfo

class RecommendWaypointsAdapter(private val context: Context, private var userRecommend: UserRecommend) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
//    private var recommendWaypointsList: List<UserRecommend> = emptyList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            ItemRecommendWaypointsRecyclerviewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return RecommendWaypointsHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as RecommendWaypointsAdapter.RecommendWaypointsHolder
        viewHolder.setImage(userRecommend.imageId[position],userRecommend.imageInfoList[position])
        viewHolder.waypointNum.text = (position+1).toString()
    }

    override fun getItemCount(): Int {
        return userRecommend.imageId.size
    }

//    @SuppressLint("NotifyDataSetChanged")
//    fun submitList(newList: List<UserRecommend>) {
//        recommendWaypointsList = newList
//        notifyDataSetChanged()
//    }

    inner class RecommendWaypointsHolder(val binding: ItemRecommendWaypointsRecyclerviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var waypointNum = binding.num
        fun setImage(image: Long, imageInfo:ImageInfo) {
            Glide.with(context)
                .load("${MyApplication.getUrl()}image/${image}") // 불러올 이미지 url
//                .override(200,200)
                .centerCrop()
                .into(binding.recommendImage) // 이미지를 넣을 뷰
            binding.waypointName.text = imageInfo.placeName
        }
    }
}