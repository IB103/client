package com.hansung.capstone.map

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PointF
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
//import android.util.Log
import android.view.*
import android.view.animation.AlphaAnimation
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.UiThread
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.location.*
import com.hansung.capstone.*
import com.hansung.capstone.BuildConfig
import com.hansung.capstone.R
import com.hansung.capstone.databinding.FragmentMapBinding
import com.hansung.capstone.recommend.ItemSpacingPageTransformer
import com.hansung.capstone.retrofit.PermissionUtils
import com.hansung.capstone.retrofit.Permissions
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.MapFragment
import com.naver.maps.map.overlay.*
import com.naver.maps.map.util.MarkerIcons
import kotlinx.android.synthetic.main.fragment_map.*
import kotlinx.android.synthetic.main.item_user_recommend.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapFragment : Fragment(), OnMapReadyCallback, NaverMap.OnMapClickListener {
    lateinit var nMap: NaverMap // 네이버 맵 객체
    lateinit var binding: FragmentMapBinding // 맵 프래그먼트 바인딩
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var waypoints: MutableList<Waypoint>
    private lateinit var encodedPath: String
    private lateinit var coordinates: List<LatLng>
    private lateinit var adapter: MapViewPagerAdapter
    var pathOverlaysCheck: Boolean = false //
    private val pathOverlay = PathOverlay() // 길찾기(경유지) 그리기용
    val pathOverlay2 = PathOverlay() // 길찾기 그리기용
    var markers: MutableList<Marker> = mutableListOf()
    private var infoWindows: MutableList<InfoWindow> = mutableListOf()
    private lateinit var infoWindowOnly: InfoWindow
    private lateinit var imm: InputMethodManager
    val imageViewCheck = MutableLiveData<Boolean>() // 뷰에 표시될 시간
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

    private val listener2 = Overlay.OnClickListener { overlay ->
        val marker = overlay as Marker
        if (marker.infoWindow == null) {
            infoWindowOnly.open(marker)
        } else {
            infoWindowOnly.close()
        }
        true
    }

    private var mapState = 0
    private var bikeState = 0

    @SuppressLint("ResourceType")
    private val directionsLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                // 결과 처리
                @Suppress("DEPRECATION")
                waypoints = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                    result.data?.getParcelableArrayListExtra("waypoints", Waypoint::class.java)!!
                        .toMutableList()
                else
                    result.data?.getParcelableArrayListExtra<Waypoint>("waypoints")!!
                        .toMutableList()
                encodedPath = result.data?.getStringExtra("coordinates").toString()
                coordinates = DataConverter.decode(encodedPath)

                resultApply()

            }
        }

    private var requestLocationPermissionLauncher: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permission ->
            if (permission.all { it.value }) {
                checkLocationPermission()
            } else {
                Toast.makeText(activity, "지도 사용을 위해 권한을 허용해주세요.", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageViewCheck.postValue(false)
        imageViewCheck.observe(viewLifecycleOwner) {
            updateUI(it)
        }

        infoWindowOnly = InfoWindow()
        infoWindowOnly.adapter = object : InfoWindow.DefaultTextAdapter(requireContext()) {
            override fun getText(infoWindow: InfoWindow): CharSequence {
                return infoWindow.marker?.tag as CharSequence? ?: ""
            }
        }

        imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        binding.locationSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                binding.autoCompleteRecyclerView.visibility = View.VISIBLE
                val searchText = s.toString()
                autoCompleteSearch(searchText)
            }
        })

        // 초기 옵션대로 생성
        val fm = childFragmentManager
        val mapFragment = fm.findFragmentById(R.id.mapView) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.mapView, it).commit()
            }
        mapFragment.getMapAsync(this)

        // 검색창에 입력 후 엔터 시 동작
        binding.locationSearch.setOnEditorActionListener { _, actionId, _ ->
            if ((actionId == EditorInfo.IME_ACTION_SEARCH) && (binding.locationSearch.text.toString()
                    .isNotBlank())
            ) {
                imm.hideSoftInputFromWindow(binding.locationSearch.windowToken, 0)
                binding.autoCompleteRecyclerView.visibility = View.GONE
                locationSearch(binding.locationSearch.text.toString())
                binding.locationSearch.clearFocus()
                imageViewCheck.postValue(true)
            } else {
                binding.locationSearch.requestFocus()
                imm.showSoftInput(binding.locationSearch, InputMethodManager.SHOW_IMPLICIT)
                Toast.makeText(activity, "검색어를 입력해주세요", Toast.LENGTH_SHORT).show()
            }
            true
        }

        binding.removeString.setOnClickListener {
            binding.locationSearch.setText("")
            binding.locationSearch.requestFocus()
            binding.autoCompleteRecyclerView.visibility = View.GONE
            imm.showSoftInput(binding.locationSearch, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    override fun onMapClick(p0: PointF, p1: LatLng) {
        val searchBoxLayout = binding.searchBoxLayout
        if (mapState == 0) {
            searchBoxLayout.animate()?.translationY(-searchBoxLayout.height.toFloat())?.duration =
                300
            mapState = 1
            binding.locationSearch.clearFocus()
            imm.hideSoftInputFromWindow(binding.locationSearch.windowToken, 0)
        } else if (mapState == 1) {
            searchBoxLayout.animate()?.translationY(0F)?.duration = 300
            mapState = 0
        }
    }

    fun locationSearch(query: String) {
        removeAll()
        val api = KakaoSearchAPI.create()
        api.getSearchKeyword(
            BuildConfig.KAKAO_REST_API_KEY,
            query
        )
            .enqueue(object : Callback<ResultSearchKeyword> {
                @SuppressLint("ResourceType")
                override fun onResponse(
                    call: Call<ResultSearchKeyword>,
                    response: Response<ResultSearchKeyword>
                ) {
                    val body = response.body()
                    if (body != null) {
                        if (body.documents.isNotEmpty()) {
                            binding.mapViewPager.clipToPadding = false // 패딩 영역을 보여주도록 설정
                            binding.mapViewPager.offscreenPageLimit =
                                3  // 이전 아이템과 다음 아이템 함께 보이도록 설정
                            binding.mapViewPager.setPageTransformer(ItemSpacingPageTransformer())
                            adapter = MapViewPagerAdapter(this@MapFragment, body.documents)
                            activity?.runOnUiThread {
                                binding.mapViewPager.adapter = adapter
                            }
                            binding.mapViewPager.registerOnPageChangeCallback(object :
                                ViewPager2.OnPageChangeCallback() {
                                override fun onPageSelected(position: Int) {
                                    if (position >= 0 && position < body.documents.size) {
//                                        Log.d("onPageSelected", "$position ${body.documents.size}")
                                        Utility.moveToMarker(
                                            LatLng(
                                                body.documents[position].y.toDouble(),
                                                body.documents[position].x.toDouble()
                                            ), nMap
                                        )
                                    }
                                    infoWindowOnly.open(markers[position])
                                    for (i in markers.indices) {
                                        markers[i].map = nMap
                                    }
                                    pathOverlay2.map = null
                                }
                            })

                            // 카메라 이동
                            Utility.moveToMarker(
                                LatLng(
                                    body.documents[0].y.toDouble(),
                                    body.documents[0].x.toDouble()
                                ), nMap
                            )

                            // 검색 결과 장소에 마커 찍기
                            for (i in body.documents.indices) {
                                val marker = Marker()
                                marker.position = LatLng(
                                    body.documents[i].y.toDouble(),
                                    body.documents[i].x.toDouble()
                                )
                                marker.icon = MarkerIcons.BLACK
                                marker.iconTintColor =
                                    Color.parseColor(resources.getString(R.color.waypointMarker))
                                markers.add(marker)
                                marker.onClickListener = listener2
                                marker.map = nMap
                                marker.tag = body.documents[i].place_name
                            }
                            infoWindowOnly.open(markers[0])
                        }
                    }
                }

                override fun onFailure(call: Call<ResultSearchKeyword>, t: Throwable) {
//                    Log.d("getSearchKeyword:", "onFailure : $t")
                }
            })

    }

    private fun autoCompleteSearch(query: String) {
        val api = KakaoSearchAPI.create()
        api.getSearchKeyword(
            BuildConfig.KAKAO_REST_API_KEY,
            query
        )
            .enqueue(object : Callback<ResultSearchKeyword> {
                @SuppressLint("ResourceType")
                override fun onResponse(
                    call: Call<ResultSearchKeyword>,
                    response: Response<ResultSearchKeyword>
                ) {
                    val body = response.body()
                    if (body != null) {
                        if (body.documents.isNotEmpty()) {
                            activity?.runOnUiThread {
                                binding.autoCompleteRecyclerView.adapter =
                                    AutoCompleteAdapter(this@MapFragment, body.documents)
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ResultSearchKeyword>, t: Throwable) {
//                    Log.d("getSearchKeyword:", "onFailure : $t")
                }
            })

    }

    @SuppressLint("MissingPermission")
    @UiThread
    override fun onMapReady(naverMap: NaverMap) {
        // 메인의 객체와 연결
        this.nMap = naverMap
        naverMap.lightness = 0f

        val uiSettings = naverMap.uiSettings
        uiSettings.isScaleBarEnabled = true
        uiSettings.isCompassEnabled = false
        uiSettings.isZoomControlEnabled = false
        uiSettings.isLocationButtonEnabled = false

        naverMap.mapType = NaverMap.MapType.Basic // 맵 타입 Basic


        // 지도에 표시할 정보 -> 자전거 도로
        naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_BUILDING, false)
        naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_TRANSIT, false)
        naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_MOUNTAIN, false)
        naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_CADASTRAL, false)
//        naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_BICYCLE, true)

        naverMap.onMapClickListener = this

        binding.gpsButton2.setOnClickListener {
            if (!nMap.locationOverlay.isVisible) {
                checkLocationPermission()
                if (::fusedLocationProviderClient.isInitialized) {
                    checkLastLocation()
                    updateLocationChecking()
                }
                binding.gpsButton2.setImageResource(R.drawable.yes_gps)
            } else {
                stopLocationChecking()
                binding.gpsButton2.setImageResource(R.drawable.no_gps)
                nMap.locationOverlay.isVisible = false
            }
        }

        binding.findDirectionsButton.setOnClickListener {
            val intent = Intent(activity, DirectionsActivity::class.java)
            if (nMap.locationOverlay.isVisible) { // 위치 버튼 활성화
                fusedLocationProviderClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        if (location != null) {
                            intent.putExtra(
                                "currentPos",
                                "${location.latitude},${location.longitude}"
                            )
                            directionsLauncher.launch(intent)
                        }
                    }
            } else { // 비활성화
                intent.putExtra("currentPos", "")
                directionsLauncher.launch(intent)
            }
        }

        binding.erasePathButton.setOnClickListener {
            eraseDialog(requireContext())
        }

        binding.hidePagerButton.setOnClickListener {
            if (!imageViewCheck.value!!) {
                val animateShow = AlphaAnimation(0f, 1f)
                animateShow.duration = 500
                binding.mapViewPager.startAnimation(animateShow)
                imageViewCheck.postValue(true)
            } else {
                val animateHide = AlphaAnimation(1f, 0f)
                animateHide.duration = 500
                binding.mapViewPager.startAnimation(animateHide)
                imageViewCheck.postValue(false)
            }
        }

        binding.changeBike.setOnClickListener {
            if (bikeState == 0) {
                naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_BICYCLE, true)
                bikeState = 1
                binding.changeBike.setImageResource(R.drawable.bike_on)
            } else {
                naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_BICYCLE, false)
                bikeState = 0
                binding.changeBike.setImageResource(R.drawable.bike_off)
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun checkLastLocation() {
        fusedLocationProviderClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val lastLoc = LatLng(location.latitude, location.longitude)
                    Utility.moveToMarker(lastLoc, nMap)
                    nMap.locationOverlay.position = lastLoc
                    nMap.locationOverlay.isVisible = true
                }
            }
    }

    @SuppressLint("MissingPermission")
    fun checkLocationPermission() {
        val permissionCheckResult =
            PermissionUtils.checkPermissions(requireActivity(), Permissions.permissionsLocation)
        if (permissionCheckResult.isEmpty()) { // 권한이 모두 승인된 상태면
            if (!::fusedLocationProviderClient.isInitialized) {
                initLocationProvider()
            }
        } else {
            Toast.makeText(
                MyApplication.applicationContext(),
                "지도 사용을 위해 권한을 허용해주세요.",
                Toast.LENGTH_SHORT
            ).show()
            requestLocationPermissionLauncher.launch(permissionCheckResult.toTypedArray())
        }
    }

    // location 객체로부터 사용자 위도, 경도 얻어오기
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            locationResult.locations.let {
                for (location in it) {
                    val locToPos = LatLng(location.latitude, location.longitude)
                    nMap.locationOverlay.position = locToPos
                }
            }
        }
    }

    private fun initLocationProvider() {
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(MyApplication.applicationContext())
    }

    @SuppressLint("MissingPermission")
    private fun updateLocationChecking() {
        // 위치 요청 값 설정
        val locationRequest = LocationRequest.create().apply {
            interval = 1000L
            fastestInterval = 500L
            priority = Priority.PRIORITY_HIGH_ACCURACY
        }
        // 갱신되는 위치 확인
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun stopLocationChecking() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    @SuppressLint("ResourceType")
    private fun resultApply() {
        removeString()
        if (::waypoints.isInitialized) {
            removeAll()
            for (i in waypoints.indices) {
                val marker = Marker()
                marker.position =
                    LatLng(waypoints[i].place_lat!!.toDouble(), waypoints[i].place_lng!!.toDouble())
                marker.isHideCollidedSymbols = true
                when (i) {
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
                marker.onClickListener = listener
                markers.add(marker)
                marker.map = nMap
                val infoWindow = InfoWindow()
                infoWindow.adapter = object : InfoWindow.DefaultTextAdapter(requireContext()) {
                    override fun getText(infoWindow: InfoWindow): CharSequence {
                        return infoWindow.marker?.tag as CharSequence? ?: ""
                    }
                }
                marker.tag = waypoints[i].place_name.toString()
                infoWindow.open(marker)
                infoWindows.add(infoWindow)
            }
            pathOverlay.coords = coordinates
            pathOverlay.outlineWidth = 0
            pathOverlay.width = 12
            pathOverlay.color =
                Color.parseColor(resources.getString(R.color.pathOverlayColor)) // 연두색
            pathOverlay.isHideCollidedSymbols = true
            pathOverlay.map = nMap
            Utility.zoomToSeeWholeTrack(coordinates, nMap)
        }
    }

    private fun removeAll() {
        binding.autoCompleteRecyclerView.visibility = View.GONE
        pathOverlay.map = null
        pathOverlay2.map = null
        if (markers.size > 0) {
            for (i in markers) {
                i.map = null
            }
        }
        markers.clear()
        if (infoWindows.size > 0) {
            for (i in infoWindows.indices) {
                infoWindows[i].close()
            }
        }
        infoWindows.clear()
        infoWindowOnly.close()
        adapter = MapViewPagerAdapter(this@MapFragment, listOf())
        activity?.runOnUiThread {
            binding.mapViewPager.adapter = adapter
        }
        imm.hideSoftInputFromWindow(binding.locationSearch.windowToken, 0)
    }

    @SuppressLint("MissingPermission")
    fun toPlace(des: LatLng, pos: Int) {
        for (i in markers.indices) {
            if (i != pos) {
                markers[i].map = null
            }
        }
        fusedLocationProviderClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val api = MapboxDirectionAPI.create()
                    api.getSearchDirections(
                        location.longitude,
                        location.latitude,
                        des.longitude,
                        des.latitude,
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
                                    pathOverlay2.coords = deco
                                    pathOverlay2.outlineWidth = 0
                                    pathOverlay2.width = 12
                                    pathOverlay2.color =
                                        Color.parseColor(resources.getString(R.color.pathOverlayColor2)) // 연두색
                                    pathOverlay2.isHideCollidedSymbols = true
                                    pathOverlay2.map = nMap
                                    Utility.zoomToSeeWholeTrack(deco, nMap)
                                }
                            }

                            override fun onFailure(
                                call: Call<ResultSearchDirections>,
                                t: Throwable
                            ) {
//                                Log.d("결과:", "실패 : $t")
                            }
                        })
                }
            }
        pathOverlaysCheck = true
    }

    private fun eraseDialog(context: Context) {
        val alertDialog = AlertDialog.Builder(context)
        alertDialog.setMessage("지도를 비우시겠습니까?")
        alertDialog.setPositiveButton("비우기") { dialog, _ ->
            dialog.dismiss()
            removeString()
            removeAll()
            imageViewCheck.postValue(false)
            Toast.makeText(requireContext(), "검색 정보가 삭제되었습니다.", Toast.LENGTH_SHORT).show()
        }
        alertDialog.setNegativeButton("취소") { dialog, _ ->
            dialog.dismiss()
        }
        alertDialog.show()
    }

    private fun removeString() {
        binding.locationSearch.setText("")
        binding.locationSearch.clearFocus()
        binding.autoCompleteRecyclerView.visibility = View.GONE
    }

    @UiThread
    private fun updateUI(imageViewCheck: Boolean) { // 라이딩 상태에 따른 UI 변경
        if (imageViewCheck) {
            binding.mapViewPager.visibility = View.VISIBLE
            binding.hidePagerButton.setImageResource(R.drawable.show_path)
        } else {
            binding.mapViewPager.visibility = View.GONE
            binding.hidePagerButton.setImageResource(R.drawable.hide_path)

        }

    }
}



