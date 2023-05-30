package com.hansung.capstone.recommend

import android.annotation.SuppressLint
import android.graphics.Color
import android.location.Location
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
import com.hansung.capstone.retrofit.RepCourseDetailData
import com.hansung.capstone.retrofit.RetrofitService


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
    lateinit var decodeCoordinates: List<LatLng>
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
    private var courseId by Delegates.notNull<Long>()
    private var moveCheck by Delegates.notNull<Int>()
    private var bikeState = 0

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        courseId = intent.getLongExtra("courseId", 0)
        moveCheck = intent.getIntExtra("moveCheck", 0)

        // 맵 설정
        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.course_view) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.course_view, it).commit()
            }
        mapFragment.getMapAsync(this)

        binding.courseViewPager.clipToPadding = false // 패딩 영역을 보여주도록 설정
        binding.courseViewPager.offscreenPageLimit = 3  // 이전 아이템과 다음 아이템 함께 보이도록 설정
        binding.courseViewPager.setPageTransformer(ItemSpacingPageTransformer())
        binding.hideButton.setOnClickListener {
            if (pathOverlayCheck2) {
                binding.hideButton.setImageResource(R.drawable.no_line)
                pathOverlay.map = null
                pathOverlayCheck2 = false
            } else {
                binding.hideButton.setImageResource(R.drawable.yes_line)
                pathOverlay.map = nMap
                pathOverlayCheck2 = true
            }
        }
        binding.movePostButton.setOnClickListener {
            if (moveCheck == 0) {
                val intentToPost = Intent(this, PostDetailActivity::class.java)
                intentToPost.putExtra("postid", postId)
                intentToPost.putExtra("moveCheck", 1)
                startActivity(intentToPost)
            } else {
                finish()
            }
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

        val api = RetrofitService.create()
        api.getCourseDetail(courseId.toInt()).enqueue(object : Callback<RepCourseDetailData> {
            override fun onResponse(
                call: Call<RepCourseDetailData>,
                response: Response<RepCourseDetailData>,
            ) {
                if (response.isSuccessful) {
                    val data = response.body()!!.data
//                    Log.d("getCourseDetail", "onResponse : ${response.body().toString()}")
                    imageInfoList = data.imageInfoList
                    originToDestination = data.originToDestination
                    coordinates = data.coordinates
                    postId = data.postId
                    imageId = data.imageId
                    numOfFavorite = data.numOfFavorite

                    pathOverlays = ArrayList<PathOverlay>(imageInfoList.size).apply {
                        repeat(imageInfoList.size) {
                            add(PathOverlay())
                        }
                    }
                    pathOverlaysCheck = ArrayList<Boolean>(imageInfoList.size).apply {
                        repeat(imageInfoList.size) {
                            add(false)
                        }
                    }

                    binding.heartCount2.text = numOfFavorite.toString()
                    binding.locationCount2.text = imageInfoList.size.toString()
                    binding.oToD.text = originToDestination
                    adapter =
                        CourseViewPagerAdapter(this@CheckCourseActivity, imageId, imageInfoList)
                    binding.courseViewPager.adapter = adapter

                    decodeCoordinates = DataConverter.decode(coordinates)
                    pathOverlay = PathOverlay()
                    pathOverlay.coords = decodeCoordinates
                    pathOverlay.outlineWidth = 0
                    pathOverlay.width = 12
                    pathOverlay.color =
                        Color.parseColor(resources.getString(R.color.pathOverlayColor)) // 연두색
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
                        infoWindow.adapter =
                            object : InfoWindow.DefaultTextAdapter(this@CheckCourseActivity) {
                                override fun getText(infoWindow: InfoWindow): CharSequence {
                                    return infoWindow.marker?.tag as CharSequence? ?: ""
                                }
                            }
                        marker.tag = imageInfoList[i].placeName
                        infoWindow.open(marker)
                        infoWindows.add(infoWindow)
                    }
                    binding.courseViewPager.registerOnPageChangeCallback(CustomPageChangeCallback())

                    Utility.zoomToSeeWholeTrack(decodeCoordinates, nMap)
                    binding.fullCourseButton.setOnClickListener {
                        Utility.zoomToSeeWholeTrack(decodeCoordinates, nMap)
                    }
                }
            }

            override fun onFailure(call: Call<RepCourseDetailData>, t: Throwable) {
//                Log.d("getCourseDetail:", "onFailure : $t")
            }
        })

        updateLocationChecking()
        naverMap.onMapClickListener = this

        // 지도에 표시할 정보
        naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_BUILDING, false)
        naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_TRANSIT, false)
        naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_MOUNTAIN, false)
        naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_CADASTRAL, false)

        binding.bikeButton.setOnClickListener {
            if (bikeState == 0) {
                naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_BICYCLE, true)
                bikeState = 1
                binding.bikeButton.setImageResource(R.drawable.bike_on)
            } else {
                naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_BICYCLE, false)
                bikeState = 0
                binding.bikeButton.setImageResource(R.drawable.bike_off)
            }
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
            courseButtonsLayout.animate()
                ?.translationY(-courseButtonsLayout.height.toFloat())?.duration =
                300
            mapState = 1
        } else if (mapState == 1) {
            courseButtonsLayout.animate()?.translationY(0F)?.duration = 300
            mapState = 0
        }
    }

    inner class CustomPageChangeCallback : ViewPager2.OnPageChangeCallback() {
        private var isFirstPageScroll = true

        override fun onPageSelected(position: Int) {
            if (isFirstPageScroll) {
                isFirstPageScroll = false
                return
            }
            Utility.moveToMarker(stringToLatLng(imageInfoList[position].coordinate), nMap)
        }
    }
}
