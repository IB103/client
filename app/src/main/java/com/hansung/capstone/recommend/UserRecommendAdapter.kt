package com.hansung.capstone.recommend

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hansung.capstone.databinding.ItemUserRecommendBinding

class UserRecommendAdapter(val body: UserRecommendDTO) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            ItemUserRecommendBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return UserRecommendHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as UserRecommendAdapter.UserRecommendHolder
        viewHolder.bind(body.data[position])
    }

    override fun getItemCount(): Int {

        Log.d("카운트","${body.data.count()}")
        return body.data.count()
    }

    inner class UserRecommendHolder(private val binding: ItemUserRecommendBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(items: UserRecommend) {
            Log.d("위치",items.originToDestination)
        }
    }
}
