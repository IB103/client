package com.hansung.capstone.course

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hansung.capstone.R
import com.hansung.capstone.databinding.ItemDirectionsRecyclerviewBinding
import com.hansung.capstone.Waypoint
import com.hansung.capstone.home.MakeCourseActivity
import com.hansung.capstone.map.WaypointsSearchActivity

class CourseAdapter(
    private val makeCourseActivity: MakeCourseActivity, var waypoints: MutableList<Waypoint>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            ItemDirectionsRecyclerviewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return CourseHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as CourseAdapter.CourseHolder
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
            val intent = Intent(makeCourseActivity, WaypointsSearchActivity::class.java)
            intent.putExtra("position", viewHolder.adapterPosition)
            makeCourseActivity.waypointSearchLauncher.launch(intent)
            makeCourseActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.none)
        }
        viewHolder.addWaypoint.setOnClickListener {
            addItem(itemCount - 1)
        }
        viewHolder.removeWaypoint.setOnClickListener {
            removeItem(position)
            makeCourseActivity.moveAndDrawMap()
        }
    }

    override fun getItemCount(): Int {
        return waypoints.count()
    }

    inner class CourseHolder(val binding: ItemDirectionsRecyclerviewBinding) :
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
}