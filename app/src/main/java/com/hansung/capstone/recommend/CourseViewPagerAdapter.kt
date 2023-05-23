package com.hansung.capstone.recommend

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hansung.capstone.MainActivity
import com.hansung.capstone.MyApplication
import com.hansung.capstone.Utility
import com.hansung.capstone.databinding.ItemCourseViewpagerBinding
import com.hansung.capstone.retrofit.ImageInfo


class CourseViewPagerAdapter(
    val checkCourseActivity: CheckCourseActivity,
    val imageId: List<Long>,
    private val imageInfoList: List<ImageInfo>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            ItemCourseViewpagerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return CourseViewPagerHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as CourseViewPagerAdapter.CourseViewPagerHolder
        viewHolder.bind(imageId[position], imageInfoList[position])
        viewHolder.itemView.setOnClickListener {
            Utility.moveToMarker(
                checkCourseActivity.stringToLatLng(imageInfoList[position].coordinate),
                checkCourseActivity.nMap
            )
//            checkCourseActivity.binding.courseViewPager.currentItem = position
        }
        viewHolder.binding.imageNum2.text = (position + 1).toString()
        if (imageInfoList[position].placeLink == "") {
            viewHolder.binding.infoButton.isClickable = false
            viewHolder.binding.infoButton.alpha = 0.3f
        } else {
            viewHolder.binding.infoButton.isClickable = true
            viewHolder.binding.infoButton.alpha = 1.0f
        }

        if (viewHolder.binding.infoButton.isClickable) {
            viewHolder.binding.infoButton.setOnClickListener {
                val address = imageInfoList[position].placeLink
                MainActivity.getInstance()?.goWebPage(address)
            }
        }
        viewHolder.binding.findRoadButton.setOnClickListener {
            if (checkCourseActivity.nMap.locationOverlay.isVisible) {
                if (checkCourseActivity.pathOverlaysCheck[position]) {
                    checkCourseActivity.pathOverlays[position].map = null
                    checkCourseActivity.pathOverlaysCheck[position] = false
                } else
                    checkCourseActivity.toWaypoint(imageInfoList[position].coordinate, position)
            } else if (checkCourseActivity.pathOverlaysCheck[position]) {
                checkCourseActivity.pathOverlays[position].map = null
                checkCourseActivity.pathOverlaysCheck[position] = false
            } else {
                Toast.makeText(checkCourseActivity, "위치 추적 버튼을 활성화 해주세요.", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    override fun getItemCount(): Int {
        return imageInfoList.size
    }


    inner class CourseViewPagerHolder(val binding: ItemCourseViewpagerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(image: Long, imageInfo: ImageInfo) {
            Glide.with(checkCourseActivity)
                .load("${MyApplication.getUrl()}image/${image}") // 불러올 이미지 url
                .centerCrop()
                .into(binding.courseImage2) // 이미지를 넣을 뷰
            binding.placeName2.text = imageInfo.placeName
        }

    }
}