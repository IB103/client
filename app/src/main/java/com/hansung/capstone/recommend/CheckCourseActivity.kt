package com.hansung.capstone.recommend

import android.annotation.SuppressLint
import android.graphics.Color
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.annotation.UiThread
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.location.*
import com.hansung.capstone.*
import com.hansung.capstone.BuildConfig
import com.hansung.capstone.R
import com.hansung.capstone.databinding.ActivityCheckCourseBinding
import com.hansung.capstone.map.ResultSearchDirections
import com.hansung.capstone.retrofit.ImageInfo
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.*
import com.naver.maps.map.util.MarkerIcons
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.properties.Delegates
import android.content.Intent
import android.graphics.PointF
import com.hansung.capstone.map.MapboxDirectionAPI
import com.hansung.capstone.post.PostDetailActivity


class CheckCourseActivity : AppCompatActivity(), OnMapReadyCallback, NaverMap.OnMapClickListener {
    val binding by lazy { ActivityCheckCourseBinding.inflate(layoutInflater) } // 뷰 바인딩
    lateinit var nMap: NaverMap // 네이버 지도 객체
    private lateinit var originToDestination: String
    private lateinit var coordinates: String
    private var postId by Delegates.notNull<Long>()
    private lateinit var imageId: List<Long>
    private lateinit var imageInfoList: List<ImageInfo>
    private var numOfFavorite by Delegates.notNull<Int>()
    private lateinit var adapter: CourseViewPagerAdapter
    private lateinit var decodeCoordinates: List<LatLng>
    private var markers: MutableList<Marker> = mutableListOf()
    lateinit var pathOverlays: ArrayList<PathOverlay> //
    lateinit var pathOverlaysCheck: ArrayList<Boolean> //
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private lateinit var pathOverlay: PathOverlay
    private var pathOverlayCheck2: Boolean = true
    private var infoWindows: MutableList<InfoWindow> = mutableListOf()
    private var mapState = 0
    private val listener = Overlay.OnClickListener { overlay ->
        val marker = overlay as Marker
        val index = markers.indexOf(marker)
        if (marker.infoWindow == null) {
            infoWindows[index].open(marker)
        } else {
            infoWindows[index].close()
        }
        true
    }

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // 인텐트 값 읽기
        val intent = intent
        @Suppress("DEPRECATION")
        imageInfoList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            intent.getParcelableArrayListExtra("imageInfoList", ImageInfo::class.java)!!.toList()
        else
            intent.getParcelableArrayListExtra<ImageInfo>("imageInfoList")!!.toList()
        originToDestination = intent.getStringExtra("originToDestination").toString()
        coordinates = intent.getStringExtra("coordinates").toString()
        postId = intent.getLongExtra("postId", 0)
//        Log.d("getUserRecommend3:", "onResponse : $postId")
        imageId = intent.getLongArrayExtra("imageId")!!.toList()
        numOfFavorite = intent.getIntExtra("numOfFavorite", 0)

        pathOverlays = ArrayList<PathOverlay>(imageInfoList.size).apply {
            repeat(imageInfoList.size) {
                add(PathOverlay())
            }
        } // 크기로 초기화
        pathOverlaysCheck = ArrayList<Boolean>(imageInfoList.size).apply {
            repeat(imageInfoList.size) {
                add(false)
            }
        }

        // 맵 설정
        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.course_view) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.course_view, it).commit()
            }
        mapFragment.getMapAsync(this)

        binding.heartCount2.text = numOfFavorite.toString()
        binding.oToD.text = originToDestination
        binding.courseViewPager.clipToPadding = false // 패딩 영역을 보여주도록 설정
        binding.courseViewPager.offscreenPageLimit = 3  // 이전 아이템과 다음 아이템 함께 보이도록 설정
        binding.courseViewPager.setPageTransformer(ItemSpacingPageTransformer())
        adapter = CourseViewPagerAdapter(this, imageId, imageInfoList)
        binding.courseViewPager.adapter = adapter

        binding.hideButton.setOnClickListener {
            if (pathOverlayCheck2) {
                binding.hideButton.setImageResource(R.drawable.hide_path)
                pathOverlay.map = null
                pathOverlayCheck2 = false
            } else {
                binding.hideButton.setImageResource(R.drawable.show_path)
                pathOverlay.map = nMap
                pathOverlayCheck2 = true
            }
        }
        binding.movePostButton.setOnClickListener {
            val intentToPost = Intent(this, PostDetailActivity::class.java)
            intentToPost.putExtra("postid", postId)
            startActivity(intentToPost)
        }
    }

    @SuppressLint("ResourceType")
    @UiThread
    override fun onMapReady(naverMap: NaverMap) {
        this.nMap = naverMap // 메인의 객체와 연결
        naverMap.lightness = 0f // 밝기 조절

        // 지도 UI 설정
        val uiSettings = naverMap.uiSettings
        uiSettings.isScaleBarEnabled = true
        uiSettings.isLocationButtonEnabled = false
        uiSettings.isCompassEnabled = false
        uiSettings.isZoomControlEnabled = false

        naverMap.mapType = NaverMap.MapType.Basic // 맵 타입 Basic

        updateLocationChecking()
        naverMap.onMapClickListener = this

        // 지도에 표시할 정보
        naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_BUILDING, false)
        naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_TRANSIT, false)
        naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_MOUNTAIN, false)
        naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_CADASTRAL, false)

        decodeCoordinates = DataConverter.decode(coordinates)
        pathOverlay = PathOverlay()
        pathOverlay.coords = decodeCoordinates
        pathOverlay.outlineWidth = 0
        pathOverlay.width = 12
        pathOverlay.color = Color.parseColor(resources.getString(R.color.pathOverlayColor)) // 연두색
        pathOverlay.isHideCollidedSymbols = true
        pathOverlay.map = naverMap

        // 마커 추가
        for (i in imageInfoList.indices) {
            val marker = Marker()
            marker.position = stringToLatLng(imageInfoList[i].coordinate)
            marker.isHideCollidedSymbols = true
            when (i) {
                0 -> {
                    marker.icon = MarkerIcons.BLACK
                    marker.iconTintColor =
                        Color.parseColor(resources.getString(R.color.startMarker))
                }
                imageInfoList.size - 1 -> {
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
            marker.onClickListener = listener
            markers.add(marker)
            marker.map = nMap
            val infoWindow = InfoWindow()
            infoWindow.adapter = object : InfoWindow.DefaultTextAdapter(this) {
                override fun getText(infoWindow: InfoWindow): CharSequence {
                    return infoWindow.marker?.tag as CharSequence? ?: ""
                }
            }
            marker.tag = imageInfoList[i].placeName
            infoWindow.open(marker)
            infoWindows.add(infoWindow)
        }
        binding.courseViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                Utility.moveToMarker(stringToLatLng(imageInfoList[position].coordinate), nMap)
            }
        })

        Utility.zoomToSeeWholeTrack(decodeCoordinates, nMap)
        binding.fullCourseButton.setOnClickListener {
            Utility.zoomToSeeWholeTrack(decodeCoordinates, nMap)
        }
    }

    fun stringToLatLng(str: String): LatLng {
        val latlng = str.split(",")
        return LatLng(latlng[0].toDouble(), latlng[1].toDouble())
    }

    @SuppressLint("MissingPermission")
    fun toWaypoint(des: String, pos: Int) {
        fusedLocationProviderClient!!.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val waypointPos = stringToLatLng(des)
                    val api = MapboxDirectionAPI.create()
                    api.getSearchDirections(
                        location.longitude,
                        location.latitude,
                        waypointPos.longitude,
                        waypointPos.latitude,
                        "polyline6",
                        "full",
                        BuildConfig.MAPBOX_TOKEN
                    )
                        .enqueue(object : Callback<ResultSearchDirections> {
                            @SuppressLint("ResourceType")
                            override fun onResponse(
                                call: Call<ResultSearchDirections>,
                                response: Response<ResultSearchDirections>
                            ) {
                                val body = response.body()
                                if (body != null) {
                                    val deco: List<LatLng> =
                                        DataConverter.decode(body.routes[0].geometry)
                                    pathOverlays[pos].coords = deco
                                    pathOverlays[pos].outlineWidth = 0
                                    pathOverlays[pos].width = 12
                                    pathOverlays[pos].color =
                                        Color.parseColor(resources.getString(R.color.pathOverlayColor2))
                                    pathOverlays[pos].isHideCollidedSymbols = true
                                    pathOverlays[pos].map = nMap
                                    Utility.zoomToSeeWholeTrack(deco, nMap)
                                }
                            }

                            override fun onFailure(
                                call: Call<ResultSearchDirections>,
                                t: Throwable
                            ) {
                                Log.d("결과:", "실패 : $t")
                            }
                        })
                }
            }
        pathOverlaysCheck[pos] = true
    }

    // location 객체로부터 사용자 위도, 경도 얻어오기
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            locationResult.locations.let {
                for (location in it) {
                    val locToPos = LatLng(location.latitude, location.longitude)
                    nMap.locationOverlay.position = locToPos
                    binding.gpsButton.setOnClickListener {
                        if (!nMap.locationOverlay.isVisible) {
                            binding.gpsButton.setImageResource(R.drawable.yes_gps)
                            nMap.locationOverlay.isVisible = true
                            Utility.moveToMarker(locToPos, nMap)
                        } else {
                            binding.gpsButton.setImageResource(R.drawable.no_gps)
                            nMap.locationOverlay.isVisible = false
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun updateLocationChecking() {
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(MyApplication.applicationContext())
        // 위치 요청 값 설정
        val locationRequest = LocationRequest.create().apply {
            interval = 1000L
            fastestInterval = 500L
            priority = Priority.PRIORITY_HIGH_ACCURACY
        }
        // 갱신되는 위치 확인
        fusedLocationProviderClient!!.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun stopLocationChecking() {
        fusedLocationProviderClient!!.removeLocationUpdates(locationCallback)
    }

    override fun onDestroy() {
        stopLocationChecking()
        super.onDestroy()
    }

    override fun onMapClick(p0: PointF, p1: LatLng) {
        val courseButtonsLayout = binding.courserButtons
        if (mapState == 0) {
            courseButtonsLayout.animate()?.translationY(-courseButtonsLayout.height.toFloat())?.duration =
                300
            mapState = 1
        } else if (mapState == 1) {
            courseButtonsLayout.animate()?.translationY(0F)?.duration = 300
            mapState = 0
        }
    }
}