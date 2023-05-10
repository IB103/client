package com.hansung.capstone.course

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import com.hansung.capstone.BuildConfig
import com.hansung.capstone.R
import com.hansung.capstone.databinding.ActivityMakeCourseBinding
import com.hansung.capstone.map.MapboxDirectionAPI
import com.hansung.capstone.map.ResultSearchDirections
import com.hansung.capstone.Waypoint
import com.hansung.capstone.map.decode
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.MarkerIcons
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.round

class MakeCourseActivity : AppCompatActivity(), OnMapReadyCallback {
    private val binding by lazy { ActivityMakeCourseBinding.inflate(layoutInflater) }
    private lateinit var waypointsAdapter: CourseAdapter
    private lateinit var waypoints: MutableList<Waypoint>
    private lateinit var coords: MutableList<LatLng>
    private lateinit var naverMap: NaverMap // 네이버 맵 객체
    private lateinit var path: String
    var markers = arrayListOf<Marker>()
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
            val intent = Intent(this, CourseActivity::class.java)
            intent.putParcelableArrayListExtra("waypoints", ArrayList(waypoints))
            intent.putExtra("path",path)
            startActivity(intent)
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
//            Log.d("intent 값 전달","$position $placeName $placeLat $placeLng")
                runOnUiThread {
                    waypointsAdapter.updateItem(
                        position!!.toInt(),
                        placeName!!,
                        placeLat!!,
                        placeLng!!,
                        placeUrl!!
                    )
                    coords = mutableListOf()
                    for (c in waypoints) {
                        if (c.place_lat != null && c.place_lng != null) {
                            coords.add(LatLng(c.place_lat!!.toDouble(), c.place_lng!!.toDouble()))
                        }
                    }
                    moveMap()
                }
            }
        }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap

    }

    fun moveMap() {
        for (i in markers) {
            i.map = null
        }
        markers.clear()
        coords = mutableListOf()
        for (c in waypoints) {
            if (c.place_lat != null && c.place_lng != null) {
                coords.add(LatLng(c.place_lat!!.toDouble(), c.place_lng!!.toDouble()))
            }
        }
        val directionsQuery = makeWaypointsDirectionQuery(coords)
        val api = MapboxDirectionAPI.create()
        api.getWaypointsDirection(directionsQuery, "polyline6", "full", BuildConfig.MAPBOX_TOKEN)
            .enqueue(object : Callback<ResultSearchDirections> {
                override fun onResponse(
                    call: Call<ResultSearchDirections>,
                    response: Response<ResultSearchDirections>
                ) {
                    val body = response.body()
                    if (body != null) {
                        Log.d("경로 결과", "Body: ${response.body()}")
                        val deco: List<LatLng> =
                            decode(body.routes[0].geometry)
                        path = encode(deco)
                        Log.d("경로 결과", deco.toString())
//                        val routeLatLng: MutableList<LatLng> =
//                            emptyList<LatLng>().toMutableList()
//                        for (z in deco) {
//                            routeLatLng += LatLng(z.latitude, z.longitude)
//                        }
//                                    zoomToSeeWholeTrack(routeLatLng)
                        com.hansung.capstone.map.MapFragment.path.coords = deco
                        com.hansung.capstone.map.MapFragment.path.outlineWidth = 0
                        com.hansung.capstone.map.MapFragment.path.color = Color.GREEN
                        com.hansung.capstone.map.MapFragment.path.map = naverMap
                        naverMap.lightness = 0.3f
                    }
                }

                override fun onFailure(
                    call: Call<ResultSearchDirections>,
                    t: Throwable
                ) {
                    Log.d("결과:", "실패 : $t")
                }
            })
        zoomToSeeWholeTrack(coords)
        for (m in coords) {
            val marker = Marker()
            markers.add(marker)
            marker.position = m
            marker.map = naverMap
        }

        Log.d("값보자", waypoints.toString())
    }

    fun makeWaypointsDirectionQuery(waypoints: MutableList<LatLng>): String {
        var waypointsQuery = ""
        for (i in waypoints) {
            waypointsQuery += "${i.longitude},${i.latitude};"
        }
        Log.d("스트링", waypointsQuery)
        val removeLast = waypointsQuery.dropLast(1)
        Log.d("스트링", removeLast)
        return removeLast
    }

    private fun zoomToSeeWholeTrack(routeLatLng: List<LatLng>) {
        val bounds = LatLngBounds.Builder()

        for (path in routeLatLng) {
            bounds.include(path)
        }

        naverMap.moveCamera(
            CameraUpdate.fitBounds(bounds.build(), 300).animate(CameraAnimation.Fly)
        )
    }

    fun decode(encodedPath: String): List<LatLng> {
        val len = encodedPath.length
        val path: MutableList<LatLng> = ArrayList()
        var index = 0
        var lat = 0
        var lng = 0
        while (index < len) {
            var result = 1
            var shift = 0
            var b: Int
            do {
                b = encodedPath[index++].code - 63 - 1
                result += b shl shift
                shift += 5
            } while (b >= 0x1f)
            lat += if (result and 1 != 0) (result shr 1).inv() else result shr 1
            result = 1
            shift = 0
            do {
                b = encodedPath[index++].code - 63 - 1
                result += b shl shift
                shift += 5
            } while (b >= 0x1f)
            lng += if (result and 1 != 0) (result shr 1).inv() else result shr 1
            path.add(
                LatLng(
                    round(lat * 1e-6 * 10000000) / 10000000,
                    round(lng * 1e-6 * 10000000) / 10000000
                )
            )
        }
        return path
    }

    //// 인코드
    fun encode(path: List<LatLng>): String {
        var lastLat: Long = 0
        var lastLng: Long = 0
        val result = StringBuffer()
        for (point in path) {
            val lat = Math.round(point.latitude * 1e6)
            val lng = Math.round(point.longitude * 1e6)
            val dLat = lat - lastLat
            val dLng = lng - lastLng
            encode(dLat, result)
            encode(dLng, result)
            lastLat = lat
            lastLng = lng
        }
        return result.toString()
    }

    private fun encode(v: Long, result: StringBuffer) {
        var v = v
        v = if (v < 0) (v shl 1).inv() else v shl 1
        while (v >= 0x20) {
            result.append(Character.toChars((0x20 or (v and 0x1f).toInt()) + 63))
            v = v shr 5
        }
        result.append(Character.toChars((v + 63).toInt()))
    }
}