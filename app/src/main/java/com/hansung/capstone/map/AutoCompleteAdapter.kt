package com.hansung.capstone.map

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hansung.capstone.databinding.ItemWaypointSearchResultRecyclerviewBinding

class AutoCompleteAdapter(
    val mapFragment: MapFragment,
    private val placeList: List<Place>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            ItemWaypointSearchResultRecyclerviewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return AutoCompleteHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as AutoCompleteAdapter.AutoCompleteHolder
        viewHolder.bind(placeList[position])
    }

    override fun getItemCount(): Int {
        return placeList.count()
    }

    inner class AutoCompleteHolder(private val binding: ItemWaypointSearchResultRecyclerviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(items: Place) {
            binding.placeName.text = items.place_name
            binding.addressName.text = items.address_name
            itemView.setOnClickListener {
                val placeName = items.place_name
                mapFragment.locationSearch(placeName)
                mapFragment.binding.locationSearch.setText(placeName)
                mapFragment.binding.locationSearch.clearFocus()
                mapFragment.binding.autoCompleteRecyclerView.visibility = View.GONE
                mapFragment.imageViewCheck.postValue(true)
            }
        }
    }
}