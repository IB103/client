package com.hansung.capstone.course

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import com.hansung.capstone.*
import com.hansung.capstone.BuildConfig
import com.hansung.capstone.R
import com.hansung.capstone.databinding.ActivityMakeCourseBinding
import com.hansung.capstone.map.MapboxDirectionAPI
import com.hansung.capstone.map.ResultSearchDirections
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.PathOverlay
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MakeCourseActivity : AppCompatActivity(), OnMapReadyCallback {
    private val binding by lazy { ActivityMakeCourseBinding.inflate(layoutInflater) }
    private lateinit var waypointsAdapter: CourseAdapter
    private lateinit var waypoints: MutableList<Waypoint>
    private lateinit var line: MutableList<LatLng>
    private lateinit var nMap: NaverMap // 네이버 맵 객체
    private lateinit var coordinates: String
    private var markers = arrayListOf<Marker>()
    val path = PathOverlay()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map, it).commit()
            }

        mapFragment.getMapAsync(this)

        waypoints = MutableList(2) { Waypoint() }
        waypointsAdapter = CourseAdapter(this, waypoints)
        binding.courseRecyclerview.adapter = waypointsAdapter

        binding.registerButton.setOnClickListener {
            lateinit var snapshotPath: String
            val intent = Intent(this, CourseActivity::class.java)
            nMap.takeSnapshot { bitmap ->
                snapshotPath = Utility.saveSnapshot(this,bitmap)
                intent.putParcelableArrayListExtra("waypoints", ArrayList(waypoints))
                intent.putExtra("coordinates", coordinates)
                intent.putExtra("snapshotPath", snapshotPath)
                startActivity(intent)
            }
        }
    }

    val myLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                // 결과 처리
                val position = result.data?.getIntExtra("position", -1)
                val placeName = result.data?.getStringExtra("place_name")
                val placeLat = result.data?.getStringExtra("place_lat")
                val placeLng = result.data?.getStringExtra("place_lng")
                val placeUrl = result.data?.getStringExtra("place_url")
                runOnUiThread {
                    waypointsAdapter.updateItem(
                        position!!.toInt(),
                        placeName!!,
                        placeLat!!,
                        placeLng!!,
                        placeUrl!!
                    )
                    line = mutableListOf()
                    for (c in waypoints) {
                        if (c.place_lat != null && c.place_lng != null) {
                            line.add(LatLng(c.place_lat!!.toDouble(), c.place_lng!!.toDouble()))
                        }
                    }
                    moveMap()
                }
            }
        }

    override fun onMapReady(naverMap: NaverMap) {
        this.nMap = naverMap
    }

    fun moveMap() {
        for (i in markers) {
            i.map = null
        }
        markers.clear()
        line = mutableListOf()
        for (c in waypoints) {
            if (c.place_lat != null && c.place_lng != null) {
                line.add(LatLng(c.place_lat!!.toDouble(), c.place_lng!!.toDouble()))
            }
        }
        val directionsQuery = makeWaypointsDirectionQuery(line)
        val api = MapboxDirectionAPI.create()
        api.getWaypointsDirection(directionsQuery, "polyline6", "full", BuildConfig.MAPBOX_TOKEN)
            .enqueue(object : Callback<ResultSearchDirections> {
                override fun onResponse(
                    call: Call<ResultSearchDirections>,
                    response: Response<ResultSearchDirections>
                ) {
                    val body = response.body()
                    if (body != null) {
                        val deco: List<LatLng> =
                            DataConverter.decode(body.routes[0].geometry)
                        coordinates = DataConverter.encode(deco)
                        path.coords = deco
                        path.outlineWidth = 0
                        path.color = Color.GREEN
                        path.map = nMap
                        nMap.lightness = 0.3f
                    }
                }

                override fun onFailure(
                    call: Call<ResultSearchDirections>,
                    t: Throwable
                ) {
                    Log.d("getWaypointsDirection", "onFailure: $t")
                }
            })
        zoomToSeeWholeTrack(line)
        for (m in line) {
            val marker = Marker()
            markers.add(marker)
            marker.position = m
            marker.map = nMap
        }
    }

    private fun makeWaypointsDirectionQuery(waypoints: MutableList<LatLng>): String {
        var waypointsQuery = ""
        for (i in waypoints) {
            waypointsQuery += "${i.longitude},${i.latitude};"
        }
        return waypointsQuery.dropLast(1)
    }

    private fun zoomToSeeWholeTrack(routeLatLng: List<LatLng>) {
        val bounds = LatLngBounds.Builder()

        if (routeLatLng.isNotEmpty()) {
            for (path in routeLatLng) {
                bounds.include(path)
            }
            nMap.moveCamera(
                CameraUpdate.fitBounds(bounds.build(), 300).animate(CameraAnimation.Fly)
            )
        }
    }
}