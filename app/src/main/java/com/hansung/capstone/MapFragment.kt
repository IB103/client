package com.hansung.capstone

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hansung.capstone.databinding.FragmentMapBinding
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.MapFragment
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.PathOverlay
import com.naver.maps.map.util.FusedLocationSource

class MapFragment : Fragment(), OnMapReadyCallback {

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//    }

    private lateinit var locationSource: FusedLocationSource

    companion object {
        lateinit var naverMap: NaverMap
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
        val binding = FragmentMapBinding.inflate(inflater, container, false)

        binding.locationSearchB.setOnClickListener {
            Log.d("검색 버튼", "클릭")
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
    }

    override fun onMapReady(naverMap: NaverMap) {

        // 메인의 객체와 연결
//        MapFragment.naverMap = naverMap
//        this.naverMap = naverMap

        naverMap.locationSource = locationSource

        val uiSettings = naverMap.uiSettings
        uiSettings.isScaleBarEnabled = true
        uiSettings.isCompassEnabled = false
        uiSettings.isLocationButtonEnabled = true

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