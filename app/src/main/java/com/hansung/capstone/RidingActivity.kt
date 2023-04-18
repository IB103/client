package com.hansung.capstone

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.location.Location
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.UiThread
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.hansung.capstone.Constants.ACTION_START_OR_RESUME_SERVICE
import com.hansung.capstone.Constants.ACTION_STOP_SERVICE
import com.hansung.capstone.databinding.ActivityRidingBinding
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.PathOverlay
import com.naver.maps.map.util.FusedLocationSource
import kotlinx.android.synthetic.main.activity_riding.view.*
import kotlinx.android.synthetic.main.layout_bottom_sheet.*
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class RidingActivity : AppCompatActivity(), OnMapReadyCallback, NaverMap.SnapshotReadyCallback {

    val binding by lazy { ActivityRidingBinding.inflate(layoutInflater) } // 뷰바인딩
    private lateinit var naverMap: NaverMap // 네이버 지도 객체
    private lateinit var locationSource: FusedLocationSource // 네이버 맵 객체에서 사용할 위치 소스
    private lateinit var fusedLocationClient: FusedLocationProviderClient // 사용자 위치를 따오기 위해
    private var paths = mutableListOf<coordinates>() // 경로 표시용 좌표 리스트
    val path = PathOverlay() // 경로 그리기용
    private var isRiding = false
    private val routeLatLng: MutableList<LatLng> = emptyList<LatLng>().toMutableList()

    private var requestPermissionLauncher: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permission ->
            if (permission.all { it.value }) {
                checkLocationPermission()
                Toast.makeText(this, "길 찾기 성공", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "길 찾기를 하려면 권한을 허용해주세요.", Toast.LENGTH_SHORT).show()
            }
        }

    private val permissionList = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // 위치소스 권한 설정
        locationSource = FusedLocationSource(this, 1000)

        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.riding_view) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.riding_view, it).commit()
            }
        mapFragment.getMapAsync(this)

        updateUI(isRiding)
        Log.d("updateUI in onCreate",isRiding.toString())

        // 사용자 좌표 기록 서비스 시작 / 끝 버튼
        binding.ridingStartButton.setOnClickListener {
            // 처음 시작을 누를 때 isRiding = false
            if (!isRiding) {
                sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
            }
        }
        binding.ridingPauseButton.setOnClickListener {
            if (isRiding) {
                sendCommandToService(ACTION_STOP_SERVICE)
                naverMap.takeSnapshot {
                    Log.d("snapShot",it.toString())
                    onSnapshotReady(it)
                }
            }
        }
        subscribeToObservers()
    }

    @UiThread
    private fun updateUI(isRiding: Boolean) {
        this.isRiding = isRiding
        Log.d("updateUI",isRiding.toString())
        if (!isRiding) {
            binding.ridingStartButton.visibility = View.VISIBLE
            binding.ridingPauseButton.visibility = View.GONE

        } else {
            binding.ridingStartButton.visibility = View.GONE
            binding.ridingPauseButton.visibility = View.VISIBLE
        }
    }

    // 서비스에 명령 보내는 함수
    private fun sendCommandToService(action: String) {
        Intent(this, RidingService::class.java).also { Intent ->
            Intent.action = action
            this.startService(Intent)
        }
    }

    private fun subscribeToObservers() {

        RidingService.isRiding.observe(this) {
            this.isRiding = it
            updateUI(it)
        }
        RidingService.pathOverlay.observe(this) {
            paths = it
            Log.d("리스트 갱신 됐냐?", paths.toString())
            addLatestPolyline()
        }
        // LiveData 라이딩 시간을 observe 해서 실시간으로 값을 바꿔서 출력한다
        RidingService.ridingTimer.observe(this) {
            binding.ridingTimer.text = RidingService.Riding.convertStopwatch(it) // 액션바 타이틀을 변경
        }

//        RidingService.ridingLocation.observe(viewLifecycleOwner, { trackingLocation ->
//            map?.animateCamera(CameraUpdateFactory.newLatLngZoom(trackingLocation, MAP_ZOOM))
//        })

//        RidingService.pathOverlay.observe(viewLifecycleOwner, {
//            pathPoints = it
//            addLatestPolyline()
//            moveCameraToUser()
//        })
    }

//    private fun moveCameraToUser() {
//        if(pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty()) {
//            map?.animateCamera(
//                CameraUpdateFactory.newLatLngZoom(
//                    pathPoints.last().last(),
//                    MAP_ZOOM
//                )
//            )
//        }
//    }

    private fun addLatestPolyline() {
        if (paths.isNotEmpty() && paths.last().size > 1) {
//            val preLastLatLng = paths.last()[paths.last().size - 2]
            val lastLatLng = paths.last().last()

//            path.coords = paths

//            val polylineOptions = PolylineOptions()
//                .color(POLYLINE_COLOR)
//                .width(POLYLINE_WIDTH)
//                .add(preLastLatLng)
//                .add(lastLatLng)
//            map?.addPolyline(polylineOptions)
//            routeLatLng+=lastLatLng
            routeLatLng += paths.last()
            Log.d("paths.last", paths.last().toString())
            path.coords = routeLatLng
            path.outlineWidth = 0
            path.color = Color.BLUE
            path.map = naverMap
        }
    }

    //
    @UiThread
    override fun onMapReady(naverMap: NaverMap) {
        // 메인의 객체와 연결
        this.naverMap = naverMap
        naverMap.lightness = 0f
        naverMap.locationSource = locationSource

        val uiSettings = naverMap.uiSettings
        uiSettings.isScaleBarEnabled = true
        uiSettings.isCompassEnabled = false
        uiSettings.isZoomControlEnabled = false
//        uiSettings.isLocationButtonEnabled = true


        // 가리기
        com.hansung.capstone.map.MapFragment.path.isHideCollidedSymbols = true
        com.hansung.capstone.map.MapFragment.path.isHideCollidedCaptions = true

        // 맵 타입 Basic
//        checkLastLocation(naverMap)
        naverMap.mapType = NaverMap.MapType.Basic
        checkLocationPermission() // 위치 권한 검사
//        naverMap.locationTrackingMode = LocationTrackingMode.Follow


        // 지도에 표시할 정보 -> 자전거 도로
        naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_BUILDING, false)
        naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_TRANSIT, false)
        naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_MOUNTAIN, false)
        naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_CADASTRAL, false)
//        naverMap.onMapClickListener = this
    }

    // 지도 스냅샷 찍는 함수
    override fun onSnapshotReady(bitmap: Bitmap) {
        // 여기서 저장
        // Generating a file name
        val filename = "${System.currentTimeMillis()}.jpg"

        // Output stream
        var fos: OutputStream? = null

        // For devices running android >= Q
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // getting the contentResolver
            contentResolver?.also { resolver ->

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

    //
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    @SuppressLint("MissingPermission")
    fun checkLocationPermission() {
//        this.lat = lat
//        this.lng = lng
        //등록
        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(MyApplication.applicationContext())
        if (ActivityCompat.checkSelfPermission(
                MyApplication.applicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                MyApplication.applicationContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            naverMap.locationTrackingMode = LocationTrackingMode.Follow
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
        } else {
            Toast.makeText(
                MyApplication.applicationContext(),
                "위치권한을 허용해주세요..",
                Toast.LENGTH_SHORT
            ).show()
            requestPermissionLauncher.launch(permissionList)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(Intent(this, RidingService::class.java))
    }
//    fun onRidingButtonClick(view: View) {
//        if (started) {
////            binding.ridingButton.setImageResource(R.drawable.ic_baseline_play_arrow_24)
//            view.riding_button.setImageResource(R.drawable.ic_baseline_play_arrow_24)
//            started = false
//            RidingService.total = 0
//            binding.ridingTimer.text = "00:00"
//        } else {
////            binding.ridingButton.setImageResource(R.drawable.ic_baseline_pause_24)
//            view.riding_button.setImageResource(R.drawable.ic_baseline_pause_24)
//            started = true
//            thread(start = true) {
//                while (started) {
//                    Thread.sleep(1000)
//                    if (started) {
//                        RidingService.total += 1
//                        handler.sendEmptyMessage(0)
//                    }
//                }
//            }
//        }
//    }


    // 뒤로 눌렀을 때 메인으로 복귀시키는 함수
//    override fun onBackPressed() {
//        val intent = Intent(this, MainActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//        startActivity(intent)
//        finish()
//    }
}