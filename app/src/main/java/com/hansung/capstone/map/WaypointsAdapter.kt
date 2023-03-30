package com.hansung.capstone.map

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hansung.capstone.databinding.ItemDirectionsRecyclerviewBinding

class WaypointsAdapter(val directionsActivity: DirectionsActivity, private val waypoints: List<Waypoint>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            ItemDirectionsRecyclerviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WaypointsHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as WaypointsAdapter.WaypointsHolder
        viewHolder.bind(waypoints[position])
    }

    override fun getItemCount(): Int {
        return waypoints.count()
    }

    inner class WaypointsHolder(private val binding: ItemDirectionsRecyclerviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
            fun bind(items: Waypoint){
                binding.waypoint.text= items.place_name
                itemView.setOnClickListener {
                    val intent = Intent(directionsActivity, WaypointSearchActivity::class.java)
                    directionsActivity.startActivity(intent)
                }
            }
        }
}
