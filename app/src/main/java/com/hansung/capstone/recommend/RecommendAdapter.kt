package com.hansung.capstone.recommend

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hansung.capstone.databinding.ItemRecommendRecyclerviewBinding
import com.hansung.capstone.post.PostImageAdapterDecoration

class RecommendAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var recommendList: List<UserRecommend> = emptyList()
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
            intent.putParcelableArrayListExtra("imageInfoList", ArrayList(recommendList[position].imageInfoList))
            intent.putExtra("originToDestination", recommendList[position].originToDestination)
            intent.putExtra("coordinates", recommendList[position].coordinates)
            intent.putExtra("postId", recommendList[position].postId)
            intent.putExtra("imageId", recommendList[position].imageId.toLongArray())
            intent.putExtra("numOfFavorite", recommendList[position].numOfFavorite)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return recommendList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(newList: List<UserRecommend>) {
        recommendList = newList
        notifyDataSetChanged()
    }

    inner class RecommendHolder(val binding: ItemRecommendRecyclerviewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
//            binding.RecommendWaypointsRecyclerView.addItemDecoration(PostImageAdapterDecoration())
            binding.RecommendWaypointsRecyclerView.addItemDecoration(CourseImageAdapterDecoration())
        }


        fun bind(items: UserRecommend) { // viewPager 이미지 불러와서 저장
            binding.courseName.text = items.originToDestination // 코스 이름
            binding.heartCount.text = items.numOfFavorite.toString() // 좋아요 카운트
            // 어댑터 등록
            binding.RecommendWaypointsRecyclerView.adapter = RecommendWaypointsAdapter(context,items)
//            binding.RecommendWaypointsRecyclerView.setOnClickListener {
//                val intent = Intent(context, CheckCourseActivity::class.java)
//                intent.putParcelableArrayListExtra("imageInfoList", ArrayList(items.imageInfoList))
//                intent.putExtra("originToDestination", items.originToDestination)
//                intent.putExtra("coordinates", items.coordinates)
//                intent.putExtra("postId", items.postId)
//                intent.putExtra("imageId", items.imageId.toLongArray())
//                intent.putExtra("numOfFavorite", items.numOfFavorite)
//                context.startActivity(intent)
//            }
        }
    }
}