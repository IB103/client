package com.hansung.capstone.home

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.UiThread
import com.hansung.capstone.*
import com.hansung.capstone.BuildConfig
import com.hansung.capstone.R
import com.hansung.capstone.course.CourseActivity
import com.hansung.capstone.course.CourseAdapter
import com.hansung.capstone.databinding.ActivityMakeCourseBinding
import com.hansung.capstone.map.MapboxDirectionAPI
import com.hansung.capstone.map.ResultSearchDirections
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.PathOverlay
import com.naver.maps.map.util.MarkerIcons
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MakeCourseActivity : AppCompatActivity(), OnMapReadyCallback {
    private val binding by lazy { ActivityMakeCourseBinding.inflate(layoutInflater) } // 뷰 바인딩
    private lateinit var courseAdapter: CourseAdapter // 어댑터
    private lateinit var waypoints: MutableList<Waypoint> // 경유지 저장 리스트
    private lateinit var waypointsPositions: MutableList<LatLng>  //
    private lateinit var nMap: NaverMap // 네이버 맵 객체
    private lateinit var coordinates: String // 인코드 경로
    private var markers = arrayListOf<Marker>() // 마커를 저장하는 배열
    val pathOverlay = PathOverlay() // 경로선 객체

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // 지도 화면에 나타내고 NaverMap 객체 얻어오기
        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.make_course_map) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.make_course_map, it).commit()
            }
        mapFragment.getMapAsync(this)

        // 글 등록 버튼 클릭
        binding.registerButton.setOnClickListener {
            if (checkWaypoints(waypoints)) {
                if (::coordinates.isInitialized) {
                    lateinit var snapshotPath: String // 스냅샷 저장 경로 저장할 변수
                    val intent = Intent(this, CourseActivity::class.java)
//                    Utility.zoomToSeeWholeTrack(waypointsPositions, nMap)
                    nMap.takeSnapshot { bitmap ->
                        snapshotPath = Utility.saveSnapshot(this, bitmap)
                        Log.d("snapshotPath", snapshotPath)
                        intent.putParcelableArrayListExtra("waypoints", ArrayList(waypoints))
                        intent.putExtra("coordinates", coordinates)
                        intent.putExtra("snapshotPath", snapshotPath)
                        startActivity(intent)
                    }
                } else
                    Toast.makeText(this, "업데이트 중입니다.", Toast.LENGTH_SHORT).show()
            } else
                Toast.makeText(this, "경유지를 모두 설정해주세요.", Toast.LENGTH_SHORT).show()
        }
    }

    val waypointSearchLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            // 결과 받아서 처리
            if (result.resultCode == RESULT_OK) {
                val position = result.data?.getIntExtra("position", -1)
                val placeName = result.data?.getStringExtra("place_name")
                val placeLat = result.data?.getStringExtra("place_lat")
                val placeLng = result.data?.getStringExtra("place_lng")
                val placeUrl = result.data?.getStringExtra("place_url")
                runOnUiThread {
                    courseAdapter.updateItem(
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

    @UiThread
    override fun onMapReady(naverMap: NaverMap) {
        this.nMap = naverMap
        nMap.lightness = 0.5f
        naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_BUILDING, false)
        naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_TRANSIT, false)
        naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_MOUNTAIN, false)
        naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_CADASTRAL, false)

        // 경유지를 저장할 MutableList 초기화
        waypoints = MutableList(2) { Waypoint() }

        // CourseRecyclerView 어댑터객체 초기화, 어댑터 연결
        courseAdapter = CourseAdapter(this, waypoints)
        binding.courseRecyclerview.adapter = courseAdapter
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
        val directionsQuery = DataConverter.makeWaypointsDirectionsQuery(waypointsPositions)
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
                    Log.d("getWaypointsDirection", "onFailure: $t")
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

    // waypoints 값 할당 확인
    private fun checkWaypoints(waypoints: MutableList<Waypoint>): Boolean {
        for (w in waypoints) {
            if (w.place_name == null)
                return false
        }
        return true
    }
}