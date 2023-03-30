package com.hansung.capstone.map

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hansung.capstone.databinding.ItemWaypointSearchResultRecyclerviewBinding

class WaypointsSearchAdapter(private val resultSearchKeyword: ResultSearchKeyword): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            ItemWaypointSearchResultRecyclerviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WaypointsSearchHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as WaypointsSearchAdapter.WaypointsSearchHolder
        viewHolder.bind(resultSearchKeyword.documents[position])
    }

    override fun getItemCount(): Int {
        return resultSearchKeyword.documents.count()
    }

    inner class WaypointsSearchHolder(private val binding: ItemWaypointSearchResultRecyclerviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(items: Place){
            binding.placeName.text=items.place_name
            binding.addressName.text=items.address_name
        }
    }
}