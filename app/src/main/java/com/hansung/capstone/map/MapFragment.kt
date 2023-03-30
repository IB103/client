package com.hansung.capstone.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.UiThread
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.hansung.capstone.BuildConfig
import com.hansung.capstone.MyApplication
import com.hansung.capstone.R
import com.hansung.capstone.databinding.FragmentMapBinding
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.MapFragment
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.PathOverlay
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.util.MarkerIcons
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class MapFragment : Fragment(), OnMapReadyCallback, NaverMap.SnapshotReadyCallback {
    private lateinit var naverMap: NaverMap // 네이버 맵 객체
    private lateinit var locationSource: FusedLocationSource // 네이버 맵 객체에서 사용할 위치 소스
    private lateinit var binding: FragmentMapBinding // 맵 프래그먼트 바인딩
    private lateinit var fusedLocationClient: FusedLocationProviderClient // 사용자 위치를 따오기 위해
    private lateinit var lat: String
    private lateinit var lng: String

    private var requestPermissionLauncher: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permission ->
            if (permission.all { it.value }) {
                checkLocationPermission(lat, lng)
                Toast.makeText(activity, "길 찾기 성공", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(activity, "길 찾기를 하려면 권한을 허용해주세요.", Toast.LENGTH_SHORT).show()
            }
        }

    private val permissionList = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    companion object {
        val path = PathOverlay() // 경로 그리기용
        val staticMarker = Marker() // 마커 찍기용
        val staticMarker2 = Marker() // 마커 찍기용
        var markers = arrayListOf<Marker>()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 위치소스 권한 설정
        locationSource =
            activity?.let { FusedLocationSource(it, 1000) }!!

        // 초기 옵션대로 생성
        val fm = childFragmentManager
        val mapFragment = fm.findFragmentById(R.id.mapView) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.mapView, it).commit()
            }
        mapFragment.getMapAsync(this)

        // 검색창에 입력 후 엔터 시 동작
        binding.locationSearch.setOnEditorActionListener { _, id, _ ->
            if ((id == EditorInfo.IME_ACTION_SEARCH) && (binding.locationSearch.text.toString()
                    .isNotBlank())
            ) {
                val imm =
                    activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.locationSearch.windowToken, 0)
                locationSearch()
            } else {
                binding.locationSearch.requestFocus()
                val manager: InputMethodManager =
                    activity?.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
                manager.showSoftInput(binding.locationSearch, InputMethodManager.SHOW_IMPLICIT)
                Toast.makeText(activity, "검색어를 입력해주세요", Toast.LENGTH_SHORT).show()
            }
            true
        }
    }

    private fun locationSearch() {

        // 검색 전에 마커 비우기
        for (i in markers) {
            i.map = null
        }
        markers.clear() // 리스트 비우기
        staticMarker.map = null
        staticMarker2.map = null
        path.map = null

        val api = KakaoSearchAPI.create()
        api.getSearchKeyword(
            BuildConfig.KAKAO_REST_API_KEY,
            binding.locationSearch.text.toString()
        )
            .enqueue(object : Callback<ResultSearchKeyword> {
                override fun onResponse(
                    call: Call<ResultSearchKeyword>,
                    response: Response<ResultSearchKeyword>
                ) {
                    val body = response.body()
//                    Log.d("검색 결과", "Body: ${response.body()}")
                    if (body != null) {
                        if (body.documents.isNotEmpty()) {
                            val resultList = binding.bottomSheet.resultList

                            // 검색 결과 리사이클러뷰에 적용
                            activity?.runOnUiThread {
                                resultList.adapter =
                                    SearchLocationAdapter(this@MapFragment, body, naverMap)
                            }

                            // 카메라 이동
                            val cameraUpdate = CameraUpdate.scrollTo(
                                LatLng(
                                    body.documents[0].y.toDouble(),
                                    body.documents[0].x.toDouble()
                                )
                            ).animate(CameraAnimation.Fly, 1000)
                            naverMap.moveCamera(cameraUpdate)

                            // 검색 결과 장소에 마커 찍기
                            for (item in body.documents) {
                                val marker = Marker()
                                markers.add(marker)
                                marker.position = LatLng(item.y.toDouble(), item.x.toDouble())
                                marker.map = naverMap
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ResultSearchKeyword>, t: Throwable) {
                    Log.d("결과:", "실패 : $t")
                }
            })

    }

    override fun onSnapshotReady(bitmap: Bitmap) {
        // 여기서 저장
        // Generating a file name
        val filename = "${System.currentTimeMillis()}.jpg"

        // Output stream
        var fos: OutputStream? = null

        // For devices running android >= Q
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // getting the contentResolver
            activity?.contentResolver?.also { resolver ->

                val contentValues = ContentValues().apply {

                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }

                val imageUri: Uri? =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
        } else {
            // These for devices running on android < Q
            val imagesDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, filename)
            fos = FileOutputStream(image)
        }

        fos?.use {
            // Finally writing the bitmap to the output stream that we opened
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            Log.d("비트맵 저장하기", "눌렀음")
//            Toast.makeText(this , "Captured View and saved to Gallery" , Toast.LENGTH_SHORT).show()
        }
    }


//    fun viewToBitmap(view: View): Bitmap {
//        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
//        val canvas = Canvas(bitmap)
//        view.draw(canvas)
//        Log.d("비트맵 만들기", "눌렀음")
//        return bitmap
//    }

    @UiThread
    override fun onMapReady(naverMap: NaverMap) {

        // 메인의 객체와 연결
        this.naverMap = naverMap

        naverMap.locationSource = locationSource

        val uiSettings = naverMap.uiSettings
        uiSettings.isScaleBarEnabled = true
        uiSettings.isCompassEnabled = false
        uiSettings.isZoomControlEnabled = false
        uiSettings.isLocationButtonEnabled = true


        // 가리기
        path.isHideCollidedSymbols = true
        path.isHideCollidedCaptions = true

        // 맵 타입 Basic
        checkLastLocation(naverMap)
        naverMap.mapType = NaverMap.MapType.Basic

        // 지도에 표시할 정보 -> 자전거 도로
        naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_BUILDING, false)
        naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_TRANSIT, false)
        naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_MOUNTAIN, false)
        naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_CADASTRAL, false)
//        naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_BICYCLE, true)
//        naverMap.symbolScale = 1f
//        naverMap.isIndoorEnabled = true

//        binding.snapshotB.setOnClickListener {
//            val bitmapImage: Bitmap = viewToBitmap(mapView)
//            naverMap.takeSnapshot {
//                onSnapshotReady(it)
//            }
//        }
    }

    @SuppressLint("MissingPermission")
    fun checkLastLocation(naverMap: NaverMap) {
        val fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(MyApplication.applicationContext())
        if (ActivityCompat.checkSelfPermission(
                MyApplication.applicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                MyApplication.applicationContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    Log.d("좌표가 찍히나?", "${location?.latitude} ${location?.longitude}")
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        val cameraUpdate =
                            CameraUpdate.scrollTo(LatLng(location.latitude, location.longitude))
                                .animate(CameraAnimation.Fly)
                        naverMap.moveCamera(cameraUpdate)
                    } else
                        LatLng(37.5666102, 126.9783881)
                }
        }
    }

    @SuppressLint("MissingPermission")
    fun checkLocationPermission(lat: String, lng: String) {
        this.lat = lat
        this.lng = lng
        //등록
        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(MyApplication.applicationContext())
        val api2 = MapboxDirectionAPI.create()
        if (ActivityCompat.checkSelfPermission(
                MyApplication.applicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                MyApplication.applicationContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            naverMap.locationTrackingMode = LocationTrackingMode.NoFollow
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    Log.d("좌표가 찍히나?", "${location?.latitude} ${location?.longitude}")
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        staticMarker2.position =
                            LatLng(location.latitude, location.longitude)
                        staticMarker2.icon = MarkerIcons.BLUE
                        staticMarker2.map = naverMap
                        api2.getSearchDirection(
                            location.longitude, location.latitude, lng.toDouble(), lat.toDouble(),
                            "polyline6", "full", BuildConfig.MAPBOX_TOKEN
                        )
                            .enqueue(object : Callback<ResultSearchDirections> {
                                override fun onResponse(
                                    call: Call<ResultSearchDirections>,
                                    response: Response<ResultSearchDirections>
                                ) {
                                    val body = response.body()
                                    if(body!=null) {
                                        Log.d("경로 결과", "Body: ${response.body()}")
                                        val deco: List<com.google.android.gms.maps.model.LatLng> =
                                            decode(body.routes[0].geometry)
                                        Log.d("경로 결과", deco.toString())
                                        val routeLatLng: MutableList<LatLng> =
                                            emptyList<LatLng>().toMutableList()
                                        routeLatLng += LatLng(location.latitude,location.longitude)
                                        for (z in deco) {
                                            routeLatLng += LatLng(z.latitude, z.longitude)
                                        }
                                        routeLatLng += LatLng(lat.toDouble(),lng.toDouble())
                                        for (i in markers) {
                                            i.map = null
                                        }
                                        staticMarker.position =
                                            LatLng(lat.toDouble(), lng.toDouble())
                                        staticMarker.icon = MarkerIcons.BLUE
                                        staticMarker.map = naverMap
                                        path.coords = routeLatLng
                                        path.outlineWidth = 0
                                        path.color = Color.BLUE
                                        path.map = naverMap
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
        } else {
            Toast.makeText(
                MyApplication.applicationContext(),
                "위치권한을 허용해주세요..",
                Toast.LENGTH_SHORT
            ).show()
            requestPermissionLauncher.launch(permissionList)
        }
    }
}



