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
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.hansung.capstone.course.CourseActivity
import com.hansung.capstone.databinding.FragmentHomeBinding
import com.hansung.capstone.retrofit.RetrofitService
import com.hansung.capstone.retrofit.Weather
import kotlinx.android.synthetic.main.view_profile.view.*
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Math.round


class HomeFragment : Fragment() {
    companion object {
        const val API_KEY: String = "${BuildConfig.OPEN_WEATHER_KEY}"
        const val WEATHER_URL: String = "https://api.openweathermap.org/data/2.5/weather"
        const val MIN_TIME: Long = 5000
        const val MIN_DISTANCE: Float = 1000F
        const val WEATHER_REQUEST: Int = 102
    }
    private lateinit var weatherState: TextView
    private lateinit var temperature: TextView
    private lateinit var weatherTip: TextView
    private lateinit var weatherIcon: ImageView
    private lateinit var profileimage: ImageView
    private lateinit var mLocationManager: LocationManager
    private lateinit var mLocationListener: LocationListener
    var latitude:String=MyApplication.prefs.getString("latitude","")
    var longitude:String=MyApplication.prefs.getString("longitude","")

    private val MIN_DISTANCE: Float = 100f

    val gson: Gson = GsonBuilder()
        .setLenient()
        .create()
    var server_info = MyApplication.getUrl()//username password1 password2 email
    var clientBuilder = OkHttpClient.Builder()
    var retrofit2 = Retrofit.Builder().baseUrl("$server_info")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .client(clientBuilder.build())
        .build()
    var service = retrofit2.create(RetrofitService::class.java)
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

//        val navController: NavController = Navigation.findNavController(view)

//        val navHostFragment =
//            requireActivity().supportFragmentManager.findFragmentById(R.id.fragmentLayout) as NavHostFragment
//        val navController = navHostFragment.navController
      //  val navController = findNavController()
      //  navController.navigate(R.id.myPageFragment)

        binding?.apply {
            temperature = temperatureTv
            weatherState = weatherTv
            weatherIcon = weatherIc
            profileimage=profileImage
        }
       // doweather()
        getWeatherInCurrentLocation()
        binding.goRiding.setOnClickListener {
            val intent = Intent(activity, CourseActivity::class.java)
            startActivity(intent)
        }
        //        binding.imageView4.setOnClickListener {
//            val intent = Intent(activity, RidingActivity::class.java)
        if(MyApplication.prefs.getString("nickname", "")!=""){
        binding.tvNick.setText( "${MyApplication.prefs.getString("nickname", "")}")
        binding.tvEmail.text =
            "${MyApplication.prefs.getString("email", "")}"
            if (MyApplication.prefs.getInt("profileImageId", 0) == -1) {
                binding.profileImage.setImageResource(R.drawable.user)
            } else {
                getprofileImage()
            }
        }
        binding.profileBox.setOnClickListener {

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentLayout, MyPageFragment())
                .commit()
            MainActivity.getInstance()?.transfer()

        }
    }

    private fun getprofileImage() {
        var profileImageId = MyApplication.prefs.getInt("profileImageId", 0)

        service.getProfileImage(profileImageId).enqueue(object :
            Callback<ResponseBody> {
            @SuppressLint("SuspiciousIndentation")
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>,
            ) {
                Log.d("결과", "성공 : ${response.body().toString()}")
                val imageB = response.body()?.byteStream()
                val bitmap = BitmapFactory.decodeStream(imageB)
                  profileimage.setImageBitmap(bitmap)
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("결과:", "실패 : $t")
            }
        })
    }
    private fun getWeatherInCurrentLocation(){
        mLocationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        mLocationListener = LocationListener { location ->
            MyApplication.prefs.setString("latitude","${location.latitude}")
            MyApplication.prefs.setString("longitude","${location.longitude}")
            doweather()
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
            ActivityCompat.requestPermissions(requireActivity(), arrayOf<String>(android.Manifest.permission.ACCESS_FINE_LOCATION), WEATHER_REQUEST)
            return
        }

        // 위치 정보 업데이트 요청
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, mLocationListener)
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, mLocationListener)
    }

     fun doweather() {
        var client = retrofit.create(RetrofitService::class.java)
          client.getWeather(latitude = latitude, longitude = longitude, API_KEY =  API_KEY).enqueue(object : Callback<Weather> {
              //  @SuppressLint("Range")
              override fun onResponse(call: Call<Weather>, response: Response<Weather>) {
                  val weather = response.body()
                  val value = weather!!.main.temperature - 273.15 // 온도 단위를 섭씨로 변환
                  val temper = round(value * 10.0) / 10.0
                  val weatherDescription = weather.weather.firstOrNull()?.description ?: "Unknown"
                  Log.d("weather: ${temper} ℃", "${weatherDescription}")

                  temperature.setText("${temper} ℃")
                  weatherState.setText(weatherDescription)
                  if (weatherState.text.contains("rain")||weatherState.text.contains("Rain")||weatherState.text.contains("Drizzle"))
                      weatherIcon.setImageResource(R.drawable.rain)
                  else if (weatherState.text.contains("clouds")||weatherState.text.contains("mist")||weatherState.text.contains("Smoke"))
                      weatherIcon.setImageResource(R.drawable.cloud)
                  else if (weatherState.text.contains("clear"))
                      weatherIcon.setImageResource(R.drawable.clear)
                  else if (weatherState.text.contains("Tornado")||weatherState.text.contains("Squall"))
                      weatherIcon.setImageResource(R.drawable.windy)
                  else if (weatherState.text.contains("thunderstorm"))
                      weatherIcon.setImageResource(R.drawable.thunderstorm)
                  else if (weatherState.text.contains("sand"))
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
            doweather()
        getWeatherInCurrentLocation()

    }
    private fun updateWeather(weather: WeatherData) {
        temperature.setText(weather.tempString+" ℃")
        weatherState.setText(weather.weatherType)
        val resourceID = resources.getIdentifier(weather.icon, "drawable", activity?.packageName)
        weatherIcon.setImageResource(resourceID)
    }
}