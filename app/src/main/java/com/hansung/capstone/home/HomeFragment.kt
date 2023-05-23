package com.hansung.capstone.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.DefaultValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.hansung.capstone.*
import com.hansung.capstone.databinding.FragmentHomeBinding
import com.hansung.capstone.linechart.GetRecordData
import com.hansung.capstone.mypage.MyPageFragment
import com.hansung.capstone.retrofit.RetrofitService
import com.hansung.capstone.retrofit.RidingData
import com.hansung.capstone.retrofit.Weather
import kotlinx.android.synthetic.main.view_profile.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.roundToInt
import kotlin.math.roundToLong


class HomeFragment : Fragment() {
    companion object {
        lateinit var  binding: FragmentHomeBinding
        const val API_KEY: String = BuildConfig.OPEN_WEATHER_KEY
        const val MIN_TIME: Long = 5000
        const val WEATHER_REQUEST: Int = 102
    }

    private var ridingDataList: List<RidingData> = emptyList()
    private lateinit var weatherState: TextView
    private lateinit var temperature: TextView
    private lateinit var weatherIcon: ImageView
    private lateinit var profileImage: ImageView
    private lateinit var mLocationManager: LocationManager
    private lateinit var mLocationListener: LocationListener
    private lateinit var dataList: List<RidingData>
    private val minDistance: Float = 100f
    var service = RetrofitService.create()
    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("http://api.openweathermap.org/data/2.5/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       // lineChart=binding.chart
        val noImage:Long=-1
        if(MyApplication.prefs.getLong("userId", 0)!=null){
         GetRecordData().getRidingData(7){result ->
             if (result.isNotEmpty()) {
                 this.ridingDataList = result
                 ranking(result)
               // draw(result)
             } else {
                 binding.noDataComment.visibility=View.VISIBLE
                 binding.noDataImage.visibility=View.VISIBLE

                // binding.chart.visibility=View.GONE
             }
         }
        }

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
            this@HomeFragment.profileImage =profileImage
        }
        getWeatherInCurrentLocation()
        binding.goRiding.setOnClickListener {
            val intent = Intent(activity, RidingActivity::class.java)
            startActivity(intent)
        }
        //        binding.imageView4.setOnClickListener {
//            val intent = Intent(activity, RidingActivity::class.java)
        if(MyApplication.prefs.getString("nickname", "")!=""){
            binding.tvNick.text = MyApplication.prefs.getString("nickname", "")
        binding.tvEmail.text =
            MyApplication.prefs.getString("email", "")
            if (MyApplication.prefs.getLong("profileImageId", 0) == noImage) {
                binding.profileImage.setImageResource(R.drawable.user)
            } else {
                getProfileImage()
            }
        }
        binding.profileBox.setOnClickListener {

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentLayout, MyPageFragment())
                .commit()
            MainActivity.getInstance()?.transfer()

        }

        binding.makeCourseButton.setOnClickListener {
            val intent = Intent(activity, MakeCourseActivity::class.java)
            startActivity(intent)
        }
    }


@SuppressLint("ClickableViewAccessibility")
//private fun draw(ridingDataList:List<RidingData>) {
//    binding.noDataComment.visibility = View.GONE
//    binding.noDataImage.visibility = View.GONE
//    binding.chart.visibility = View.VISIBLE
//    //if(ridingDataList.isNotEmpty()){
//    Log.d("ready", "$ridingDataList")
//
//    val entries = ridingDataList.map { Entry(it.ridingTime.toFloat(), it.ridingDistance) }
//    val dataSet = LineDataSet(entries, "라이딩 기록")
//    val markerView = CustomMarkerView(requireContext(), R.layout.marker_layout)
//    val lineData = LineData(dataSet)
//    val lineChart = binding.chart
//    lineChart.marker = markerView
//    val xAxis = lineChart.xAxis
//    lineChart.data = lineData
//    lineChart.invalidate()
//    xAxis.apply {
//        setDrawGridLines(false)
//        setDrawAxisLine(true)
//        setDrawLabels(true)
//        position = XAxis.XAxisPosition.BOTTOM
//        textColor = resources.getColor(R.color.black, null)
//        textSize = 10f
//        labelRotationAngle = 0f
//        setLabelCount(10, true)
//    }
//
//    lineChart.apply {
//
//        axisRight.isEnabled = false   //y축 사용여부
//        axisLeft.isEnabled = true
//        legend.isEnabled = false    //legend 사용여부
//        description.isEnabled = false //주석
//        isScaleYEnabled = false //y축 줌 사용여부
//        isScaleXEnabled = false //x축 줌 사용여부
//    }
//
//    dataSet.apply {
//        color = resources.getColor(R.color.maincolor, null)
//        circleRadius = 3f
//        lineWidth = 1f
//        setCircleColor(resources.getColor(R.color.maincolor, null))
//        setDrawHighlightIndicators(false)
//        setDrawValues(false) // 숫자표시
//        valueTextColor = resources.getColor(R.color.black, null)
//        valueFormatter = DefaultValueFormatter(2)  // 소숫점 자릿수 설정
//        valueTextSize = 10f
//
//    }
//    lineChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
//        override fun onValueSelected(e: Entry?, h: Highlight?) {
//            if (e != null) {
//                lineChart.highlightValue(h) // 하이라이트 표시
//            }
////                markerView.refreshContent(e.x, e.y)
////                lineChart.highlightValue(h)
////                lineChart.invalidate()
////            } else {
////                lineChart.highlightValue(null)
////                lineChart.invalidate()
////            }
//        }
//
//        override fun onNothingSelected() {
//            //  lineChart.marker = null
////            lineChart.highlightValue(null)
////            lineChart.invalidate()
//        }
//    })
//
//    lineChart.setOnTouchListener { v, event ->
//        if (event.action == MotionEvent.ACTION_DOWN) {
//            val highlight = lineChart.getHighlightByTouchPoint(event.x, event.y)
//            if (highlight == null) {
//                // 좌표 지점이 아닌 곳을 터치했을 때 MarkerView를 숨김
//
//            }
//        }
//        false
//    }
//}
   private fun ranking(data:List<RidingData>){
    val size=data.size
    val sortedList = data.sortedBy { it.ridingDistance }
    val reversedList=sortedList.reversed()
    val textView = requireActivity().findViewById<TextView>(R.id.first_one)

   textView.text=String.format("%.2f",reversedList[0].ridingDistance )+"km"
    binding.firstTwo.text=reversedList[0].ridingTime.toString()


    if(size==2){
        binding.secondOne.text=String.format("%.2f",reversedList[1].ridingDistance )+"km"
        binding.secondTwo.text=reversedList[1].ridingTime.toString()
    }else if(size==3){
        binding.secondOne.text=String.format("%.2f",reversedList[1].ridingDistance )+"km"
        binding.secondTwo.text=reversedList[1].ridingTime.toString()
        binding.thirdOne.text=String.format("%.2f",reversedList[2].ridingDistance)+"km"
         binding.thirdTwo.text=reversedList[2].ridingTime.toString()
    }



   }
    private fun getProfileImage() {
        val profileImageId = MyApplication.prefs.getLong("profileImageId", 0)
        Glide.with(requireActivity())
            .load("${MyApplication.getUrl()}profile-image/$profileImageId") // 불러올 이미지 url
            .override(200, 200)
            .centerCrop()
            .into(profileImage)
    }

    @SuppressLint("MissingPermission")
    private fun getWeatherInCurrentLocation(){
        mLocationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        mLocationListener = LocationListener { location ->
            MyApplication.prefs.setString("latitude","${location.latitude}")
            MyApplication.prefs.setString("longitude","${location.longitude}")
            doWeather()
        }

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // 위치 권한이 없을 경우 권한 요청
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), WEATHER_REQUEST)
            return
        }

        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, minDistance, mLocationListener)
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, minDistance, mLocationListener)
    }

     @SuppressLint("SuspiciousIndentation")
     fun doWeather() {
        val client = retrofit.create(RetrofitService::class.java)
          client.getWeather(MyApplication.prefs.getString("latitude",""),  MyApplication.prefs.getString("longitude",""), API_KEY).enqueue(object : Callback<Weather> {
              //  @SuppressLint("Range")
              @SuppressLint("SetTextI18n")
              override fun onResponse(call: Call<Weather>, response: Response<Weather>) {
                  val weather = response.body()
                  val value = weather!!.main.temperature - 273.15 // 온도 단위를 섭씨로 변환
                  val temper = (value * 10.0).roundToInt() / 10.0
                  val weatherDescription = weather.weather.firstOrNull()?.description ?: "Unknown"

                  temperature.text = "$temper ℃"
                  weatherState.text = weatherDescription
                  if (weatherDescription.contains("rain")||weatherDescription.contains("Rain")||weatherDescription.contains("drizzle"))
                      weatherIcon.setImageResource(R.drawable.rain)
                  else if (weatherDescription.contains("clouds")||weatherDescription.contains("mist")||weatherDescription.contains("Smoke"))
                      weatherIcon.setImageResource(R.drawable.cloud)
                  else if (weatherDescription.contains("clear"))
                      weatherIcon.setImageResource(R.drawable.clear)
                  else if (weatherDescription.contains("Tornado")||weatherDescription.contains("Squall"))
                      weatherIcon.setImageResource(R.drawable.windy)
                  else if (weatherDescription.contains("thunderstorm"))
                      weatherIcon.setImageResource(R.drawable.thunderstorm)
                  else if (weatherDescription.contains("sand"))
                      weatherIcon.setImageResource(R.drawable.sand)
              }
              override fun onFailure(call: Call<Weather>, t: Throwable) {
                  Log.d("onFailure", "실패 ")
              }
          })
    }

    override fun onResume() {
        super.onResume()
      //if(ridingDataList.isNotEmpty())
        //ranking(ridingDataList)
        if(MyApplication.prefs.getString("latitude","")!="")
            doWeather()
        getWeatherInCurrentLocation()

    }

}

