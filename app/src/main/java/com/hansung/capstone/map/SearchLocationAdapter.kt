package com.hansung.capstone.map

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.UiThread
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hansung.capstone.BuildConfig
import com.hansung.capstone.MainActivity
import com.hansung.capstone.databinding.ItemLocationSearchResultsBinding
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.NaverMap
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.round

class SearchLocationAdapter(
    val mapFragment: MapFragment,private val result: ResultSearchKeyword, private val naverMap: NaverMap
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            ItemLocationSearchResultsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return LocationSearchHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as LocationSearchHolder
        viewHolder.bind(result.documents[position])
    }

    override fun getItemCount(): Int {
        Log.d("페이지 수", "${result.documents.count()}")
        return result.documents.count()
    }

    inner class LocationSearchHolder(private val binding: ItemLocationSearchResultsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @UiThread
        fun bind(items: Place) {
            binding.resultTitle.text = items.place_name
            binding.resultRoadAddress.text = items.road_address_name
            binding.resultAddress.text = items.address_name
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
                                .error("https://upload.wikimedia.org/wikipedia/commons/1/14/No_Image_Available.jpg")
                                .centerCrop()
                                .into(binding.resultImage)
                        }
                    }
                }
                override fun onFailure(call: Call<LocationImageDTO>, t: Throwable) {
                    Log.d("결과:", "실패 : $t")
                }
            })

            // 리사이클러뷰 아이템 클릭시 이벤트
            itemView.setOnClickListener {
                val cameraUpdate =
                    CameraUpdate.scrollTo(LatLng(items.y.toDouble(), items.x.toDouble())) // y위도, x경도
                        .animate(CameraAnimation.Easing)
                naverMap.moveCamera(cameraUpdate)
            }

            // 웹 페이지 연결 버튼 클릭시 이벤트
            binding.openPage.setOnClickListener {
                val address = items.place_url
                MainActivity.getInstance()?.goWebPage(address)
            }

            // 길찾기 버튼 클릭시 이벤트
            binding.searchDirection.setOnClickListener {
//                mapFragment.checkLocationPermission(items.y, items.x) // x경도, y위도
            }
        }
    }
}

