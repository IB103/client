package com.hansung.capstone.recommend

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.UiThread
import com.hansung.capstone.BuildConfig
import com.hansung.capstone.R
import com.hansung.capstone.databinding.ActivityLocationSetBinding
import com.hansung.capstone.map.KakaoSearchAPI
import com.hansung.capstone.map.ResultGetAddress
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LocationSetActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var prePosition: String
    private val binding by lazy { ActivityLocationSetBinding.inflate(layoutInflater) } // 뷰 바인딩
    private lateinit var nMap: NaverMap // 네이버 지도 객체

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // 받아온 좌표
        prePosition = intent.getStringExtra("prePosition").toString()

        // 맵 설정
        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.locationSetView) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.locationSetView, it).commit()
            }
        mapFragment.getMapAsync(this)


        binding.selectLocationButton.setOnClickListener {
            nMap.cameraPosition.target.run {
                searchAddress(this.latitude, this.longitude) { address ->
                    val resultIntent = Intent()
                    // 지역 문자열 리턴해주기
                    resultIntent.putExtra("setLocation", address)
                    // setResult()를 사용하여 결과 데이터 설정
                    setResult(Activity.RESULT_OK, resultIntent)
                    // 액티비티 종료
                    finish()
                }
            }
        }
    }

    @UiThread
    override fun onMapReady(naverMap: NaverMap) {
        this.nMap = naverMap // 메인의 객체와 연결
        naverMap.lightness = 0f // 밝기 조절

        // 지도 UI 설정
        val uiSettings = naverMap.uiSettings
        uiSettings.isScaleBarEnabled = true
        uiSettings.isCompassEnabled = false
        uiSettings.isZoomControlEnabled = false

        naverMap.mapType = NaverMap.MapType.Basic // 맵 타입 Basic

        // 지도에 표시할 정보
        naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_BUILDING, false)
        naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_TRANSIT, false)
        naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_MOUNTAIN, false)
        naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_CADASTRAL, false)

        val preLatLng = prePosition.split(",")
        val cameraUpdate =
            CameraUpdate.scrollTo(LatLng(preLatLng[0].toDouble(), preLatLng[1].toDouble()))
                .animate(CameraAnimation.Fly)
        nMap.moveCamera(cameraUpdate)
    }

    private fun searchAddress(lat: Double, lng: Double, callback: (String?) -> Unit) {
        val api = KakaoSearchAPI.create()
        api.getAddress(
            BuildConfig.KAKAO_REST_API_KEY,
            lng.toString(),
            lat.toString()
        ).enqueue(object : Callback<ResultGetAddress> {
            override fun onResponse(
                call: Call<ResultGetAddress>,
                response: Response<ResultGetAddress>
            ) {
                val body = response.body()
                if (body != null) {
                    Log.d("getAddress", "onResponse: $body")
                    val resultAddress =
                        "${body.documents[0].address.region_1depth_name} ${body.documents[0].address.region_2depth_name}"
                    callback(resultAddress)
                } else {
                    callback(null)
                }
            }

            override fun onFailure(
                call: Call<ResultGetAddress>,
                t: Throwable
            ) {
                Log.d("getAddress", "onFailure: $t")
                callback(null)
            }
        })
    }
}