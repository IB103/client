package com.hansung.capstone.map

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hansung.capstone.*
import com.hansung.capstone.databinding.ItemCourseViewpagerBinding
import com.naver.maps.geometry.LatLng
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapViewPagerAdapter(val mapFragment: MapFragment, private val placeList: List<Place> ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            ItemCourseViewpagerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return MapViewPagerHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as MapViewPagerAdapter.MapViewPagerHolder
        viewHolder.bind(placeList[position])
        viewHolder.itemView.setOnClickListener {
            Utility.moveToMarker(
                LatLng(placeList[position].y.toDouble(),placeList[position].x.toDouble()),
                mapFragment.nMap
            )
        }
        viewHolder.binding.imageNum2.text = (position + 1).toString()
        if (placeList[position].place_url == "") {
            viewHolder.binding.infoButton.isClickable = false
            viewHolder.binding.infoButton.alpha = 0.3f
        } else {
            viewHolder.binding.infoButton.isClickable = true
            viewHolder.binding.infoButton.alpha = 1.0f
        }

        if (viewHolder.binding.infoButton.isClickable) {
            viewHolder.binding.infoButton.setOnClickListener {
                val address = placeList[position].place_url
                MainActivity.getInstance()?.goWebPage(address)
            }
        }
        viewHolder.binding.findRoadButton.setOnClickListener {
            if (mapFragment.nMap.locationOverlay.isVisible) {
                if (mapFragment.pathOverlaysCheck) {
                    mapFragment.pathOverlay2.map = null
                    mapFragment.pathOverlaysCheck = false
//                    for(i in MapFragment.markers.indices){
//                            MapFragment.markers[i].map = mapFragment.nMap
//                    }
                    for(i in mapFragment.markers.indices){
                        mapFragment.markers[i].map = mapFragment.nMap
                    }
                } else
                    mapFragment.toPlace(LatLng(placeList[position].y.toDouble(),placeList[position].x.toDouble()), position)
            } else if (mapFragment.pathOverlaysCheck) {
                mapFragment.pathOverlay2.map = null
                mapFragment.pathOverlaysCheck = false
//                for(i in MapFragment.markers.indices){
//                    MapFragment.markers[i].map = mapFragment.nMap
//                }
                for(i in mapFragment.markers.indices){
                    mapFragment.markers[i].map = mapFragment.nMap
                }
            } else {
                Toast.makeText(mapFragment.requireActivity(), "위치 추적 버튼을 활성화 해주세요.", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    override fun getItemCount(): Int {
        return placeList.size
    }

//    @SuppressLint("NotifyDataSetChanged")
//    fun submitList(newList: List<Place>) {
//        placeList = newList
//        notifyDataSetChanged()
//    }

    inner class MapViewPagerHolder(val binding: ItemCourseViewpagerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(items: Place) {
//            Glide.with(mapFragment)
//                .load("${MyApplication.getUrl()}image/${image}") // 불러올 이미지 url
//                .centerCrop()
//                .into(binding.courseImage2) // 이미지를 넣을 뷰
            val api = KakaoSearchAPI.create()
            api.getSearchImage(
                BuildConfig.KAKAO_REST_API_KEY,
                items.place_name,1
            ).enqueue(object : Callback<LocationImageDTO> {
                override fun onResponse(
                    call: Call<LocationImageDTO>,
                    response: Response<LocationImageDTO>
                ) {
                    val url = response.body()
                    if (url != null) {
                        Log.d("검색 결과", "Body: ${response.body()}")
                        if(url.documents.isNotEmpty()) { // 비었는지 확인
                            Glide.with(itemView)
                                .load(url.documents[0].image_url) // 불러올 이미지 url
                                .placeholder(R.drawable.no_image) // 에러 발생 시 대체할 이미지
                                .error(R.drawable.no_image)
                                .centerCrop()
                                .into(binding.courseImage2)
                        }
                    }
                }
                override fun onFailure(call: Call<LocationImageDTO>, t: Throwable) {
                    Log.d("결과:", "실패 : $t")
                }
            })
            binding.placeName2.text = items.place_name
        }

    }
}