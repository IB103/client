package com.hansung.capstone.map

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.hansung.capstone.databinding.ActivityDirectionsBinding
import com.hansung.capstone.databinding.ActivityPostDetailBinding
import com.naver.maps.geometry.LatLng

class DirectionsActivity : AppCompatActivity() {
    private val binding by lazy { ActivityDirectionsBinding.inflate(layoutInflater) }
    lateinit var waypoints: MutableList<Waypoint>
//    lateinit var directionsRecyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        waypoints = mutableListOf()
        waypoints += Waypoint("경유지")
        binding.directionsRecyclerview.adapter = WaypointsAdapter(this ,waypoints)
    }
}

data class Waypoint(
    val place_name: String,
    val place_lat: Double? = null,
    val place_lng: Double? = null,
)