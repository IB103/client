package com.hansung.capstone

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.hansung.capstone.course.CourseActivity
import com.hansung.capstone.course.MakeCourseActivity
import com.hansung.capstone.databinding.FragmentHomeBinding
import com.hansung.capstone.retrofit.RetrofitService
import com.hansung.capstone.retrofit.Weather
import kotlinx.android.synthetic.main.view_profile.view.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.roundToInt


class HomeFragment : Fragment() {
    companion object {
        const val API_KEY: String = BuildConfig.OPEN_WEATHER_KEY
        const val MIN_TIME: Long = 5000
        const val WEATHER_REQUEST: Int = 102
    }
    private lateinit var weatherState: TextView
    private lateinit var temperature: TextView
    private lateinit var weatherIcon: ImageView
    private lateinit var profileImage: ImageView
    private lateinit var mLocationManager: LocationManager
    private lateinit var mLocationListener: LocationListener

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
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentHomeBinding.bind(view)
        val noImage:Long=-1
        val entries = listOf(
            Entry(0f, 3f),
            Entry(1f, 4f),
           Entry(2f, 5f),
            Entry(3f, 6f),
           Entry(4f, 4f)
        )
        val dataSet = LineDataSet(entries, "라이딩 기록")
        val lineData = LineData(dataSet)
        val lineChart = binding.chart
        lineChart.data = lineData
        lineChart.invalidate()
        // 애니메이션 설정
        lineChart.animateX(1000) // X축 방향 애니메이션 설정
        lineChart.animateY(1000, Easing.EasingOption.EaseInQuad) // Y축 방향 애니메이션 설정
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
        if(MyApplication.prefs.getString("latitude","")!="")
            doWeather()
        getWeatherInCurrentLocation()

    }

}