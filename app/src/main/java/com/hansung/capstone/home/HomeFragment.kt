package com.hansung.capstone.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.gms.location.*
import com.hansung.capstone.*
import com.hansung.capstone.R
import com.hansung.capstone.databinding.FragmentHomeBinding
import com.hansung.capstone.barchart.GetRecordData
import com.hansung.capstone.mypage.MyPageFragment
import com.hansung.capstone.retrofit.*
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import kotlinx.android.synthetic.main.view_profile.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.round
import kotlin.math.roundToInt


class HomeFragment : Fragment() {
    companion object {
        lateinit var binding: FragmentHomeBinding
        const val API_KEY: String = BuildConfig.OPEN_WEATHER_KEY
        const val MIN_TIME: Long = 5000
        const val WEATHER_REQUEST: Int = 102
    }

    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var requestLocationPermissionLauncher: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permission ->
            if (permission.all { it.value }) {
                locationUpdate()
            } else {
                Toast.makeText(requireActivity(), "권한이 없으면 이용이 불가능합니다.", Toast.LENGTH_SHORT).show()
                requireActivity().finish()
            }

//            val deniedPermissions = permission.filter { entry ->
//                !entry.value
//            }.map { entry ->
//                entry.key
//            }
//
//            if (deniedPermissions.isNotEmpty()) {
//                // 권한이 거부된 경우
//                Toast.makeText(
//                    requireContext(),
//                    "권한이 거부되었습니다. 앱 설정에서 권한을 변경해주세요.",
//                    Toast.LENGTH_SHORT
//                ).show()
//            } else {
//                // 권한이 허용된 경우
//                // 권한이 필요한 작업을 수행
//                locationUpdate()
//            }
        }

    private var ridingDataList: MutableList<RankData> = mutableListOf()
    private lateinit var weatherState: TextView
    private lateinit var temperature: TextView
    private lateinit var weatherIcon: ImageView
    private lateinit var profileImage: ImageView

    //    private lateinit var mLocationManager: LocationManager
//    private lateinit var mLocationListener: LocationListener
    private val minDistance: Float = 100f
    var service = RetrofitService.create()
    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("http://api.openweathermap.org/data/2.5/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkWeatherLocationPermissions()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // lineChart=binding.chart
//        val noImage:Long=-1
//        if(MyApplication.prefs.getLong("userId", 0)!=null){
//         GetRecordData().getRidingData(7){result ->
//             if (result.isNotEmpty()) {
//                 this.ridingDataList = result
////                 ranking(result)
//               // draw(result)
//             } else {
//                 binding.noDataComment.visibility = View.VISIBLE
//                 binding.noDataImage.visibility = View.VISIBLE
//             }

//        if (Token().checkToken()) {
//            Token().issueNewToken {
//                requestData()
//            }
//        } else requestData()
//
//        //}
//
//>>>>>>> Stashed changes
//        val navController: NavController = Navigation.findNavController(view)

//        val navHostFragment =
//            requireActivity().supportFragmentManager.findFragmentById(R.id.fragmentLayout) as NavHostFragment
//        val navController = navHostFragment.navController
        //  val navController = findNavController()
        //  navController.navigate(R.id.myPageFragment)
        binding.apply {
            temperature = temperatureTv
            weatherState = weatherTv
            weatherIcon = weatherIc
            this@HomeFragment.profileImage = profileImage
        }
        requestData()
//        getWeatherInCurrentLocation()
        binding.goRiding.setOnClickListener {
            if (MyApplication.prefs.getString("accessToken", "") != "") {
                val intent = Intent(activity, RidingActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(activity, LoginActivity::class.java)
                intent.putExtra("loginNeeded", true)
                startActivity(intent)
            }
        }
        //        binding.imageView4.setOnClickListener {
//            val intent = Intent(activity, RidingActivity::class.java)
        if (MyApplication.prefs.getString("nickname", "") != "") {
            binding.tvNick.text = MyApplication.prefs.getString("nickname", "")
            binding.tvEmail.text =
                MyApplication.prefs.getString("email", "")
            getProfileImage(MyApplication.prefs.getLong("profileImageId", 0), binding.profileImage)

        }
        binding.profileBox.setOnClickListener {

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentLayout, MyPageFragment())
                .commit()
            MainActivity.getInstance()?.transfer()

        }

        binding.makeCourseButton.setOnClickListener {
            if (MyApplication.prefs.getString("accessToken", "") != "") {
                val intent = Intent(activity, MakeCourseActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(activity, LoginActivity::class.java)
                intent.putExtra("loginNeeded", true)
                startActivity(intent)
            }

        }
    }

    private fun requestData() {
        GetRecordData().getRankData { result ->
            if (result.isNotEmpty()) {
                println("rank data $result")
                binding.noDataComment.visibility = View.GONE
                binding.rankLayout.visibility = View.VISIBLE
                this.ridingDataList = result
                ranking(result)
            } else {
                binding.noDataComment.visibility = View.VISIBLE
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun ranking(data: MutableList<RankData>) {
        val size = data.size
//=======
//
//    @SuppressLint("ClickableViewAccessibility")
////private fun draw(ridingDataList:List<RidingData>) {
////    binding.noDataComment.visibility = View.GONE
////    binding.noDataImage.visibility = View.GONE
////    binding.chart.visibility = View.VISIBLE
////    //if(ridingDataList.isNotEmpty()){
////    Log.d("ready", "$ridingDataList")
////
////    val entries = ridingDataList.map { Entry(it.ridingTime.toFloat(), it.ridingDistance) }
////    val dataSet = LineDataSet(entries, "라이딩 기록")
////    val markerView = CustomMarkerView(requireContext(), R.layout.marker_layout)
////    val lineData = LineData(dataSet)
////    val lineChart = binding.chart
////    lineChart.marker = markerView
////    val xAxis = lineChart.xAxis
////    lineChart.data = lineData
////    lineChart.invalidate()
////    xAxis.apply {
////        setDrawGridLines(false)
////        setDrawAxisLine(true)
////        setDrawLabels(true)
////        position = XAxis.XAxisPosition.BOTTOM
////        textColor = resources.getColor(R.color.black, null)
////        textSize = 10f
////        labelRotationAngle = 0f
////        setLabelCount(10, true)
////    }
////
////    lineChart.apply {
////
////        axisRight.isEnabled = false   //y축 사용여부
////        axisLeft.isEnabled = true
////        legend.isEnabled = false    //legend 사용여부
////        description.isEnabled = false //주석
////        isScaleYEnabled = false //y축 줌 사용여부
////        isScaleXEnabled = false //x축 줌 사용여부
////    }
////
////    dataSet.apply {
////        color = resources.getColor(R.color.maincolor, null)
////        circleRadius = 3f
////        lineWidth = 1f
////        setCircleColor(resources.getColor(R.color.maincolor, null))
////        setDrawHighlightIndicators(false)
////        setDrawValues(false) // 숫자표시
////        valueTextColor = resources.getColor(R.color.black, null)
////        valueFormatter = DefaultValueFormatter(2)  // 소숫점 자릿수 설정
////        valueTextSize = 10f
////
////    }
////    lineChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
////        override fun onValueSelected(e: Entry?, h: Highlight?) {
////            if (e != null) {
////                lineChart.highlightValue(h) // 하이라이트 표시
////            }
//////                markerView.refreshContent(e.x, e.y)
//////                lineChart.highlightValue(h)
//////                lineChart.invalidate()
//////            } else {
//////                lineChart.highlightValue(null)
//////                lineChart.invalidate()
//////            }
////        }
////
////        override fun onNothingSelected() {
////            //  lineChart.marker = null
//////            lineChart.highlightValue(null)
//////            lineChart.invalidate()
////        }
////    })
////
////    lineChart.setOnTouchListener { v, event ->
////        if (event.action == MotionEvent.ACTION_DOWN) {
////            val highlight = lineChart.getHighlightByTouchPoint(event.x, event.y)
////            if (highlight == null) {
////                // 좌표 지점이 아닌 곳을 터치했을 때 MarkerView를 숨김
////
////            }
////        }
////        false
////    }
////}
//    private fun requestData() {
//        GetRecordData().getRankData { result ->
//            if (result.isNotEmpty()) {
//                binding.noDataComment.visibility = View.GONE
//                //binding.noDataImage.visibility=View.GONE
//                binding.rankLayout.visibility = View.VISIBLE
//                this.ridingDataList = result
//                ranking(result)
//                // draw(result)
//            } else {
//                binding.noDataComment.visibility = View.VISIBLE
//                // binding.noDataImage.visibility=View.VISIBLE
//                binding.rankLayout.visibility = View.GONE
//                // binding.chart.visibility=View.GONE
//            }
//        }
//    }
//
//    private fun ranking(data: MutableList<RankData>) {
//        val size = data.size
////        Log.d("size", "$size")
//>>>>>>> Stashed changes
        binding.firstDistance.text = String.format("%.2f", data[0].totalDistance) + "km"
        binding.firstNick.text = data[0].userNickname
        getProfileImage(data[0].profileImageId, binding.rank1Image)
        if (size >= 2) {
            binding.secondDistance.text = String.format("%.2f", data[1].totalDistance) + "km"
            binding.secondNick.text = data[1].userNickname
            getProfileImage(data[1].profileImageId, binding.rank2Image)
        }

        if (size >= 3) {
            binding.thirdDistance.text = String.format("%.2f", data[2].totalDistance) + "km"
            binding.thirdNick.text = data[2].userNickname
            getProfileImage(data[2].profileImageId, binding.rank3Image)
        }

    }

    private fun getProfileImage(profileImageId: Long, profileImage: ImageView) {
        val noImage: Long = -1
        println("user porilfeID $profileImageId")
        if (profileImageId == noImage)
            profileImage.setImageResource(R.drawable.user)
        else {
            Glide.with(requireActivity())
                .load("${MyApplication.getUrl()}profile-image/$profileImageId") // 불러올 이미지 url
                .centerCrop()
                .into(profileImage)
        }
    }

    @SuppressLint("MissingPermission")
    private fun checkWeatherLocationPermissions() {
//        mLocationManager =
//            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
//
//        mLocationListener = LocationListener { location ->
//            MyApplication.prefs.setString("latitude", "${location.latitude}")
//            MyApplication.prefs.setString("longitude", "${location.longitude}")
//            doWeather()
//        }

//        if (ActivityCompat.checkSelfPermission(
//                requireContext(),
//                android.Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                requireContext(),
//                android.Manifest.permission.ACCESS_COARSE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            // 위치 권한이 없을 경우 권한 요청
//            ActivityCompat.requestPermissions(
//                requireActivity(),
//                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
//                WEATHER_REQUEST
//            )
//            return
//        }

//        mLocationManager.requestLocationUpdates(
//            LocationManager.NETWORK_PROVIDER,
//            MIN_TIME,
//            minDistance,
//            mLocationListener
//        )
//        mLocationManager.requestLocationUpdates(
//            LocationManager.GPS_PROVIDER,
//            MIN_TIME,
//            minDistance,
//            mLocationListener
//        )

        val permissionCheckResult =
            PermissionUtils.checkPermissions(requireActivity(), Permissions.permissionsLocation)
        if (permissionCheckResult.isEmpty()) { // 권한이 모두 승인된 상태면
            locationUpdate()
        } else {
            Toast.makeText(
                MyApplication.applicationContext(),
                "앱 이용을 위해 권한을 허용해주세요.",
                Toast.LENGTH_SHORT
            ).show()
            requestLocationPermissionLauncher.launch(permissionCheckResult.toTypedArray())
        }
    }

    @SuppressLint("SuspiciousIndentation")
    fun doWeather() {
        val client = retrofit.create(RetrofitService::class.java)
        client.getWeather(
            MyApplication.prefs.getString("latitude", ""),
            MyApplication.prefs.getString("longitude", ""),
            API_KEY
        ).enqueue(object : Callback<Weather> {
            //  @SuppressLint("Range")
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<Weather>, response: Response<Weather>) {
                val weather = response.body()
                val value = weather!!.main.temperature - 273.15 // 온도 단위를 섭씨로 변환
                val temper = (value * 10.0).roundToInt() / 10.0
                val weatherDescription = weather.weather.firstOrNull()?.description ?: "Unknown"

                temperature.text = "$temper ℃"
                if (weatherDescription.contains("rain") || weatherDescription.contains("Rain") || weatherDescription.contains(
                        "drizzle"
                    )
                ) {
                    weatherState.text = "비"
                    binding.ridingImage.setImageResource(R.drawable.rest)
                    weatherIcon.setImageResource(R.drawable.rain)
                } else if (weatherDescription.contains("clouds") || weatherDescription.contains("mist") || weatherDescription.contains(
                        "Smoke"
                    )
                ) {
                    weatherState.text = "흐림"
                    binding.ridingImage.setImageResource(R.drawable.biking)
                    weatherIcon.setImageResource(R.drawable.cloud)
                } else if (weatherDescription.contains("clear")) {
                    weatherState.text = "맑음"
                    binding.ridingImage.setImageResource(R.drawable.biking)
                    weatherIcon.setImageResource(R.drawable.clear)
                } else if (weatherDescription.contains("Tornado") || weatherDescription.contains("Squall")) {
                    weatherState.text = "바람"
                    binding.ridingImage.setImageResource(R.drawable.rest)
                    weatherIcon.setImageResource(R.drawable.windy)
                } else if (weatherDescription.contains("thunderstorm")) {
                    weatherState.text = "번개"
                    binding.ridingImage.setImageResource(R.drawable.rest)
                    weatherIcon.setImageResource(R.drawable.thunderstorm)
                } else if (weatherDescription.contains("sand")) {
                    weatherState.text = "황사"
                    binding.ridingImage.setImageResource(R.drawable.rest)
                    weatherIcon.setImageResource(R.drawable.sand)
                }
            }

            override fun onFailure(call: Call<Weather>, t: Throwable) {
                Log.d("onFailure", "실패 ")
            }
        })
    }

    @SuppressLint("SuspiciousIndentation")
    fun doWeather(lat: Double, lng: Double) {
        val client = retrofit.create(RetrofitService::class.java)
        client.getWeather(
            lat.toString(),
            lng.toString(),
            API_KEY
        ).enqueue(object : Callback<Weather> {
            //  @SuppressLint("Range")
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<Weather>, response: Response<Weather>) {
                val weather = response.body()
                val value = weather!!.main.temperature - 273.15 // 온도 단위를 섭씨로 변환
                val temper = (value * 10.0).roundToInt() / 10.0
                val weatherDescription = weather.weather.firstOrNull()?.description ?: "Unknown"

                temperature.text = "$temper ℃"
                if (weatherDescription.contains("rain") || weatherDescription.contains("Rain") || weatherDescription.contains(
                        "drizzle"
                    )
                ) {
                    weatherState.text = "비"
                    binding.ridingImage.setImageResource(R.drawable.rest)
                    weatherIcon.setImageResource(R.drawable.rain)
                } else if (weatherDescription.contains("clouds") || weatherDescription.contains("mist") || weatherDescription.contains(
                        "Smoke"
                    )
                ) {
                    weatherState.text = "흐림"
                    binding.ridingImage.setImageResource(R.drawable.biking)
                    weatherIcon.setImageResource(R.drawable.cloud)
                } else if (weatherDescription.contains("clear")) {
                    weatherState.text = "맑음"
                    binding.ridingImage.setImageResource(R.drawable.biking)
                    weatherIcon.setImageResource(R.drawable.clear)
                } else if (weatherDescription.contains("Tornado") || weatherDescription.contains("Squall")) {
                    weatherState.text = "바람"
                    binding.ridingImage.setImageResource(R.drawable.rest)
                    weatherIcon.setImageResource(R.drawable.windy)
                } else if (weatherDescription.contains("thunderstorm")) {
                    weatherState.text = "번개"
                    binding.ridingImage.setImageResource(R.drawable.rest)
                    weatherIcon.setImageResource(R.drawable.thunderstorm)
                } else if (weatherDescription.contains("sand")) {
                    weatherState.text = "황사"
                    binding.ridingImage.setImageResource(R.drawable.rest)
                    weatherIcon.setImageResource(R.drawable.sand)
                }
            }

            override fun onFailure(call: Call<Weather>, t: Throwable) {
                Log.d("onFailure", "실패 ")
            }
        })
    }

    override fun onResume() {
        super.onResume()
        if (MyApplication.prefs.getString("email", "") != "") {
            binding.tvNick.text = MyApplication.prefs.getString("nickname", "")
            binding.tvEmail.text = MyApplication.prefs.getString("email", "")
            getProfileImage(MyApplication.prefs.getLong("profileImageId", 0L), binding.profileImage)
        } else {
            binding.tvNick.text = "비회원"
            binding.tvEmail.text = ""
            binding.profileImage.setImageResource(R.drawable.user)
//=======
//        if (MyApplication.prefs.getString("email", "") != "") {
//            binding.tvNick.text = MyApplication.prefs.getString("nickname", "")
//            binding.tvEmail.text = MyApplication.prefs.getString("email", "")
//            val profileImageId = MyApplication.prefs.getLong("profileImageId", 0)
//            getProfileImage(profileImageId, binding.profileImage)
//        } else {
//            binding.tvNick.text = "비회원"
//            binding.tvEmail.text = ""
//            binding.profileImage.setImageResource(R.drawable.user2)
//>>>>>>> Stashed changes
        }
        //if(ridingDataList.isNotEmpty())
        //ranking(ridingDataList)

//        if (MyApplication.prefs.getString("latitude", "") != "")
//            doWeather()

//        else
//            checkWeatherLocationPermissions()

//        locationUpdate()

    }

    @SuppressLint("MissingPermission")
    fun locationUpdate() {
        val permissionCheckResult =
            PermissionUtils.checkPermissions(requireActivity(), Permissions.permissionsLocation)
        if (permissionCheckResult.isEmpty()) { // 권한이 모두 승인된 상태면
//            mLocationManager =
//                requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
//
//            mLocationListener = LocationListener { location ->
//                MyApplication.prefs.setString("latitude", "${location.latitude}")
//                MyApplication.prefs.setString("longitude", "${location.longitude}")
//                doWeather()
//            }
//            mLocationManager.requestLocationUpdates(
//                LocationManager.NETWORK_PROVIDER,
//                MIN_TIME,
//                minDistance,
//                mLocationListener
//            )
//            mLocationManager.requestLocationUpdates(
//                LocationManager.GPS_PROVIDER,
//                MIN_TIME,
//                minDistance,
//                mLocationListener
//            )

            fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(MyApplication.applicationContext())
            fusedLocationClient!!.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        Log.d("locationResult", "${location.latitude} ${location.longitude}")
                        doWeather(location.latitude, location.longitude)
                    }
                }

            fusedLocationClient = null
        }
    }

}

