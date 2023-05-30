package com.hansung.capstone.map

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hansung.capstone.R
import com.hansung.capstone.Waypoint
import com.hansung.capstone.databinding.ItemDirectionsRecyclerviewBinding

class WaypointsAdapter(
    private val directionsActivity: DirectionsActivity,
    var waypoints: MutableList<Waypoint>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            ItemDirectionsRecyclerviewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return WaypointsHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as WaypointsAdapter.WaypointsHolder
        viewHolder.bind(waypoints[position])
        when (position) {
            0 -> {
                viewHolder.waypointText.hint = "출발지 입력"
                viewHolder.addWaypoint.visibility = View.INVISIBLE
                viewHolder.removeWaypoint.visibility = View.INVISIBLE
            }
            itemCount - 1 -> {
                viewHolder.waypointText.hint = "도착지 입력"
                viewHolder.addWaypoint.visibility = View.VISIBLE
                viewHolder.removeWaypoint.visibility = View.INVISIBLE
            }
            else -> {
                viewHolder.waypointText.hint = "경유지 입력"
                viewHolder.addWaypoint.visibility = View.INVISIBLE
                viewHolder.removeWaypoint.visibility = View.VISIBLE
            }
        }
        viewHolder.itemView.setOnClickListener {
            val intent = Intent(directionsActivity, WaypointsSearchActivity::class.java)
            intent.putExtra("position", viewHolder.adapterPosition)
            directionsActivity.directionsSearchLauncher.launch(intent)
            //
            directionsActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.none)
        }
        viewHolder.addWaypoint.setOnClickListener {
            addItem(itemCount - 1)
        }
        viewHolder.removeWaypoint.setOnClickListener {
            removeItem(position)
        }
    }

    override fun getItemCount(): Int {
        return waypoints.count()
    }

    inner class WaypointsHolder(val binding: ItemDirectionsRecyclerviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var waypointText = binding.waypointTextView
        val addWaypoint = binding.addWaypoint
        val removeWaypoint = binding.removeWaypoint
        fun bind(items: Waypoint) {
            if (items.place_name != null) {
                waypointText.text = items.place_name
            }
            else
                waypointText.text = null
        }
    }

    fun updateItem(position: Int, placeName: String, placeLat: String, placeLng: String, placeUrl: String) {
        waypoints[position].place_name = placeName
        waypoints[position].place_lat = placeLat.toDouble()
        waypoints[position].place_lng = placeLng.toDouble()
        waypoints[position].place_url = placeUrl
        notifyItemChanged(position)
    }

    private fun addItem(position: Int) {
        waypoints.add(position, Waypoint())
        notifyItemInserted(position)
        notifyItemRangeChanged(position, waypoints.size)
    }

    private fun removeItem(position: Int) {
        waypoints.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, waypoints.size)
    }

    fun makeWaypointsDirectionQuery(waypoints: MutableList<Waypoint>): String {
        var waypointsQuery = ""
        for (i in waypoints) {
            waypointsQuery += "${i.place_lng},${i.place_lat};"
        }
        return waypointsQuery.dropLast(1)
    }
}


