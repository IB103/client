package com.hansung.capstone

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.RecyclerView
import com.example.navermap.NaverAPI
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.Tm128
import com.naver.maps.map.*
import com.naver.maps.map.MapFragment
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.PathOverlay
import com.naver.maps.map.util.FusedLocationSource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapFragment : Fragment(), OnMapReadyCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 위치소스 권한 설정
        locationSource =
            FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)

        // 초기 옵션대로 생성
        val fm = childFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map, it).commit()
            }
        mapFragment.getMapAsync(this)

        val searchB = view?.findViewById<ImageButton>(R.id.locationSearchB)
        searchB?.setOnClickListener {
            // 빈칸일 때 입력 요청 토스트 추가 필요

            // 전에 찍었던 마커 삭제
            for (i in markers) {
                i.map = null
            }
            markers.clear() // 리스트 비우기
            val source = view?.findViewById<EditText>(R.id.locationSearch)
//            if (source.text.toString() == "") {
//                source.requestFocus()
//                val manager: InputMethodManager =
//                    getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
//                manager.showSoftInput(source, InputMethodManager.SHOW_IMPLICIT)
//                Toast.makeText(this@MapFragment, "검색어를 입력해주세요", Toast.LENGTH_SHORT).show()
//            } else {
//                val manager = getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
//                manager.hideSoftInputFromWindow(
//                    currentFocus!!.windowToken,
//                    InputMethodManager.HIDE_NOT_ALWAYS
//                )
            val api = NaverAPI.create()

            api.getSearchLocation(source?.text.toString(), 10, 1)
                .enqueue(object : Callback<ResultGetSearchLocation> {
                    override fun onResponse(
                        call: Call<ResultGetSearchLocation>,
                        response: Response<ResultGetSearchLocation>
                    ) {
                        Log.d("결과", "성공 : ${response.body().toString()}")
//                    // 값을 넣어야한다~
                        val body = response.body()
//
//                    //Gson을 Kotlin에서 사용 가능한 object로 만든다.
//                    val gson = GsonBuilder().create()
//                    val searchResult = Gson().fromJson(body, ResultGetSearchLocation::class.java)
                        val resultList = view?.findViewById<RecyclerView>(R.id.resultList)
                        activity?.runOnUiThread {
                            resultList?.adapter =
                                body?.let { it -> RecyclerViewAdapter(it) }
                        }
                        //

                        val wherex = body?.items?.get(0)?.mapx
                        val wherey = body?.items?.get(0)?.mapy

                        // 첫번째 검색 결과 좌표로 지도 이동
                        var tm = Tm128(wherex!!.toDouble(), wherey!!.toDouble())
                        val cameraUpdate = CameraUpdate.scrollTo(tm.toLatLng())
                            .animate(CameraAnimation.Fly, 1000)
                        naverMap.moveCamera(cameraUpdate)

                        for (item in body.items) {
                            tm = Tm128(item.mapx!!.toDouble(), item.mapy!!.toDouble())
                            val marker = Marker()
                            markers.add(marker)
                            marker.position = tm.toLatLng()
                            marker.map = naverMap
                        }
                    }

                    override fun onFailure(call: Call<ResultGetSearchLocation>, t: Throwable) {
                        Log.d("결과:", "실패 : $t")
                    }
                })
        }
    }

    private lateinit var naverMap: NaverMap
    private lateinit var locationSource: FusedLocationSource

    companion object {
        //        lateinit var naverMap: NaverMap
        var markers = arrayListOf<Marker>()
        var coords = arrayListOf<LatLng>()
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
        val path = PathOverlay()
        var oldLatLng: LatLng? = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        return inflater.inflate(R.layout.fragment_map, container, false)
//        val binding = FragmentMapBinding.inflate(inflater, container, false)
//        binding.locationSearchB.setOnClickListener {
//            Log.d("검색 버튼", "클릭")
//        }
//        return binding.root
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        // 위치소스 권한 설정
//        locationSource =
//            FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
//
//        // 초기 옵션대로 생성
//        val fm = childFragmentManager
//        val mapFragment = fm.findFragmentById(R.id.map) as MapFragment?
//            ?: MapFragment.newInstance().also {
//                fm.beginTransaction().add(R.id.map, it).commit()
//            }
//        mapFragment.getMapAsync(this)
//
//        val searchB = view.findViewById<ImageButton>(R.id.locationSearchB)
//        searchB.setOnClickListener {
//            // 빈칸일 때 입력 요청 토스트 추가 필요
//
//            // 전에 찍었던 마커 삭제
//            for(i in markers){
//                i.map = null
//            }
//            markers.clear() // 리스트 비우기
//            val source = view.findViewById<EditText>(R.id.locationSearch)
////            if (source.text.toString() == "") {
////                source.requestFocus()
////                val manager: InputMethodManager =
////                    getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
////                manager.showSoftInput(source, InputMethodManager.SHOW_IMPLICIT)
////                Toast.makeText(this@MapFragment, "검색어를 입력해주세요", Toast.LENGTH_SHORT).show()
////            } else {
////                val manager = getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
////                manager.hideSoftInputFromWindow(
////                    currentFocus!!.windowToken,
////                    InputMethodManager.HIDE_NOT_ALWAYS
////                )
//                val api = NaverAPI.create()
//
//                api.getSearchLocation(source.text.toString(), 10, 1)
//                    .enqueue(object : Callback<ResultGetSearchLocation> {
//                        override fun onResponse(
//                            call: Call<ResultGetSearchLocation>,
//                            response: Response<ResultGetSearchLocation>
//                        ) {
//                            Log.d("결과", "성공 : ${response.body().toString()}")
////                    // 값을 넣어야한다~
//                            val body = response.body()
////
////                    //Gson을 Kotlin에서 사용 가능한 object로 만든다.
////                    val gson = GsonBuilder().create()
////                    val searchResult = Gson().fromJson(body, ResultGetSearchLocation::class.java)
//                            val resultList = view.findViewById<RecyclerView>(R.id.resultList)
//                            activity?.runOnUiThread {
//                                resultList.adapter =
//                                    body?.let { it -> RecyclerViewAdapter(it) }
//                            }
//                            //
//
//                            val wherex = body?.items?.get(0)?.mapx
//                            val wherey = body?.items?.get(0)?.mapy
//
//                            // 첫번째 검색 결과 좌표로 지도 이동
//                            var tm = Tm128(wherex!!.toDouble(), wherey!!.toDouble())
//                            val cameraUpdate = CameraUpdate.scrollTo(tm.toLatLng())
//                                .animate(CameraAnimation.Fly, 1000)
//                            naverMap.moveCamera(cameraUpdate)
//
//                            for (item in body.items) {
//                                tm = Tm128(item.mapx!!.toDouble(), item.mapy!!.toDouble())
//                                val marker = Marker()
//                                markers.add(marker)
//                                marker.position = tm.toLatLng()
//                                marker.map = naverMap
//                            }
//                        }
//
//                        override fun onFailure(call: Call<ResultGetSearchLocation>, t: Throwable) {
//                            Log.d("결과:", "실패 : $t")
//                        }
//                    })
//            }
    }

    override fun onMapReady(naverMap: NaverMap) {

        // 메인의 객체와 연결
        this.naverMap = naverMap
//        this.naverMap = naverMap

        naverMap.locationSource = locationSource

        val uiSettings = naverMap.uiSettings
        uiSettings.isScaleBarEnabled = true
        uiSettings.isCompassEnabled = false
        uiSettings.isLocationButtonEnabled = false
        uiSettings.isZoomControlEnabled = false


        // 맵 타입 Basic
        CameraPosition(LatLng(37.5666102, 126.9783881), 100.0)
        naverMap.mapType = NaverMap.MapType.Basic

        // 지도에 표시할 정보 -> 자전거 도로
        naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_BUILDING, false)
        naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_TRANSIT, false)
        naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_MOUNTAIN, false)
        naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_CADASTRAL, false)
        naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_BICYCLE, true)

    }

}