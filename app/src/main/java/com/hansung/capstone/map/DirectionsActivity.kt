package com.hansung.capstone.map

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
//import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.UiThread
import com.hansung.capstone.*
import com.hansung.capstone.databinding.ActivityDirectionsBinding
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.PathOverlay
import com.naver.maps.map.util.MarkerIcons
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DirectionsActivity : AppCompatActivity(), OnMapReadyCallback {
    private val binding by lazy { ActivityDirectionsBinding.inflate(layoutInflater) }
    private lateinit var waypointsAdapter: WaypointsAdapter
    private lateinit var waypoints: MutableList<Waypoint>
    private lateinit var waypointsPositions: MutableList<LatLng>  //
    private lateinit var coordinates: String // 인코드 경로
    private lateinit var nMap: NaverMap // 네이버 맵 객체
    private lateinit var currentPos: String
    private var markers = arrayListOf<Marker>() // 마커를 저장하는 배열
    val pathOverlay = PathOverlay() // 경로선 객체

    @UiThread
    override fun onMapReady(naverMap: NaverMap) {
        this.nMap = naverMap
        nMap.lightness = 0.5f
        naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_BUILDING, false)
        naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_TRANSIT, false)
        naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_MOUNTAIN, false)
        naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_CADASTRAL, false)

        val intent = intent
        currentPos = intent.getStringExtra("currentPos").toString()

        waypoints = MutableList(2) { Waypoint() }
        if (currentPos != "") {
            waypoints[0].place_name = "현재 위치"
            val convertLatLng = currentPos.split(",")
            waypoints[0].place_lat = convertLatLng[0].toDouble()
            waypoints[0].place_lng = convertLatLng[1].toDouble()
            moveAndDrawMap()
        }

        // 어댑터 연결
        waypointsAdapter = WaypointsAdapter(this, waypoints)
        binding.directionsRecyclerview.adapter = waypointsAdapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.directions_map) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.directions_map, it).commit()
            }
        mapFragment.getMapAsync(this)

        binding.directionsButton.setOnClickListener {
            if (checkWaypoints(waypoints)) {
                if (::coordinates.isInitialized) {
                    val resultIntent = Intent()
                    resultIntent.putParcelableArrayListExtra("waypoints", ArrayList(waypoints))
                    resultIntent.putExtra("coordinates", coordinates)
                    // setResult()를 사용하여 결과 데이터 설정
                    setResult(Activity.RESULT_OK, resultIntent)
                    // 액티비티 종료
                    finish()
                } else
                    Toast.makeText(this, "업데이트 중입니다.", Toast.LENGTH_SHORT).show()
            } else
                Toast.makeText(this, "경유지를 모두 설정해주세요.", Toast.LENGTH_SHORT).show()

        }
    }

    val directionsSearchLauncher =
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
                    waypointsPositions = mutableListOf()
                    for (c in waypoints) {
                        if (c.place_lat != null && c.place_lng != null) {
                            waypointsPositions.add(
                                LatLng(
                                    c.place_lat!!.toDouble(),
                                    c.place_lng!!.toDouble()
                                )
                            )
                        }
                    }
                    moveAndDrawMap()
                }
            }
        }

    // 검색 결과를 가지고 마커찍기, 경로 그리기, 카메라 이동하기
    @SuppressLint("ResourceType")
    fun moveAndDrawMap() {
        // 찍혀있던 마커 비우기
        for (i in markers) {
            i.map = null
        }
        markers.clear() // 마커 리스트 비우기
        waypointsPositions = mutableListOf() // 경로선 표시용 좌표 리스트 초기화
        for (c in waypoints) {
            if (c.place_lat != null && c.place_lng != null) {
                waypointsPositions.add(LatLng(c.place_lat!!.toDouble(), c.place_lng!!.toDouble()))
            }
        }
        // 경유지를 가지고 쿼리문 작성
        val directionsQuery = makeWaypointsDirectionsQuery(waypointsPositions)
        val api = MapboxDirectionAPI.create()
        api.getWaypointsDirections(directionsQuery, "polyline6", "full", BuildConfig.MAPBOX_TOKEN)
            .enqueue(object : Callback<ResultSearchDirections> {
                override fun onResponse(
                    call: Call<ResultSearchDirections>,
                    response: Response<ResultSearchDirections>
                ) {
                    val body = response.body()
                    if (body != null) {
                        coordinates = body.routes[0].geometry
                        val deco: List<LatLng> =
                            DataConverter.decode(coordinates) // 경로 인코드 값 디코드해서 리스트로 저장
                        pathOverlay.coords = deco
                        pathOverlay.outlineWidth = 0
                        pathOverlay.width = 12
                        pathOverlay.color =
                            Color.parseColor(resources.getString(R.color.pathOverlayColor)) // 연두색
                        pathOverlay.isHideCollidedSymbols = true
                        pathOverlay.map = nMap
                    }
                }

                override fun onFailure(
                    call: Call<ResultSearchDirections>,
                    t: Throwable
                ) {
//                    Log.d("getWaypointsDirection", "onFailure: $t")
                }
            })
        Utility.zoomToSeeWholeTrack(waypointsPositions, nMap)

        // 마커 찍기
        for (m in 0 until waypoints.size) {
            if (waypoints[m].place_lat != null && waypoints[m].place_lng != null) {
                val marker = Marker()
                marker.position = LatLng(
                    waypoints[m].place_lat!!.toDouble(),
                    waypoints[m].place_lng!!.toDouble()
                )
                when (m) {
                    0 -> {
                        marker.icon = MarkerIcons.BLACK
                        marker.iconTintColor =
                            Color.parseColor(resources.getString(R.color.startMarker))
                    }
                    waypoints.size - 1 -> {
                        marker.icon = MarkerIcons.BLACK
                        marker.iconTintColor =
                            Color.parseColor(resources.getString(R.color.endMarker))
                    }
                    else -> {
                        marker.icon = MarkerIcons.BLACK
                        marker.iconTintColor =
                            Color.parseColor(resources.getString(R.color.waypointMarker))
                    }
                }
                marker.map = nMap
                val infoWindow = InfoWindow()
                infoWindow.adapter = object : InfoWindow.DefaultTextAdapter(this) {
                    override fun getText(infoWindow: InfoWindow): CharSequence {
                        return infoWindow.marker?.tag as CharSequence? ?: ""
                    }
                }
                marker.tag = waypoints[m].place_name.toString()
                infoWindow.open(marker)
                markers.add(marker)
            }
        }
    }

    // 쿼리문 만드는 함수
    private fun makeWaypointsDirectionsQuery(waypoints: MutableList<LatLng>): String {
        var waypointsQuery = ""
        for (i in waypoints) {
            waypointsQuery += "${i.longitude},${i.latitude};"
        }
        return waypointsQuery.dropLast(1)
    }

    // waypoints 값 할당 확인
    private fun checkWaypoints(waypoints: MutableList<Waypoint>): Boolean {
        for (w in waypoints) {
            if (w.place_name == null)
                return false
        }
        return true
    }

}
