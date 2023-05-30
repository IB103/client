package com.hansung.capstone.recommend

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hansung.capstone.databinding.ItemRecommendRecyclerviewBinding

class RecommendAdapter(private val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var recommendList = mutableListOf<UserRecommend>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            ItemRecommendRecyclerviewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return RecommendHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as RecommendAdapter.RecommendHolder
        viewHolder.bind(recommendList[position])
        viewHolder.itemView.setOnClickListener {
            val intent = Intent(context, CheckCourseActivity::class.java)
            intent.putExtra("courseId", recommendList[position].courseId)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return recommendList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setInitItems(recommendList: List<UserRecommend>) {//초기 화면 세팅
        this.recommendList.clear()
        this.recommendList.addAll(recommendList)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(recommendList: List<UserRecommend>) {//초기 화면 세팅
        this.recommendList.addAll(recommendList)
        notifyDataSetChanged()
    }

    inner class RecommendHolder(val binding: ItemRecommendRecyclerviewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.RecommendWaypointsRecyclerView.addItemDecoration(CourseImageAdapterDecoration())
        }


        fun bind(items: UserRecommend) { // viewPager 이미지 불러와서 저장
            binding.courseName.text = items.originToDestination // 코스 이름
            binding.heartCount.text = items.numOfFavorite.toString() // 좋아요 카운트
            binding.locationCount.text = items.imageInfoList.size.toString()
            // 어댑터 등록
            binding.RecommendWaypointsRecyclerView.adapter =
                RecommendWaypointsAdapter(context, items)
        }
    }
}