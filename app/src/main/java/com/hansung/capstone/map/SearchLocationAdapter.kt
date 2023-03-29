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

    override fun getItemCount(): Int {
        Log.d("페이지 수", "${result.documents.count()}")
        return result.documents.count()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as LocationSearchHolder
        viewHolder.bind(result.documents[position])
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
                BuildConfig.KAKAO_SEARCH_API_KEY,
                items.place_name,1
            ).enqueue(object : Callback<LocationImageDTO> {
                override fun onResponse(
                    call: Call<LocationImageDTO>,
                    response: Response<LocationImageDTO>
                ) {
                    val url = response.body()
                    if (url != null) {
                        Log.d("검색 결과", "Body: ${response.body()}")
                        Glide.with(itemView)
                            .load(url.documents[0].image_url) // 불러올 이미지 url
                            .error("https://upload.wikimedia.org/wikipedia/commons/1/14/No_Image_Available.jpg")
                            .centerCrop()
                            .into(binding.resultImage)
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
                mapFragment.checkLocationPermission(items.y, items.x) // x경도, y위도
            }
        }
    }
}

// 디코드
fun decode(encodedPath: String): List<com.google.android.gms.maps.model.LatLng> {
    val len = encodedPath.length
    val path: MutableList<com.google.android.gms.maps.model.LatLng> = ArrayList()
    var index = 0
    var lat = 0
    var lng = 0
    while (index < len) {
        var result = 1
        var shift = 0
        var b: Int
        do {
            b = encodedPath[index++].code - 63 - 1
            result += b shl shift
            shift += 5
        } while (b >= 0x1f)
        lat += if (result and 1 != 0) (result shr 1).inv() else result shr 1
        result = 1
        shift = 0
        do {
            b = encodedPath[index++].code - 63 - 1
            result += b shl shift
            shift += 5
        } while (b >= 0x1f)
        lng += if (result and 1 != 0) (result shr 1).inv() else result shr 1
        path.add(
            com.google.android.gms.maps.model.LatLng(
                round(lat * 1e-6 * 10000000) / 10000000,
                round(lng * 1e-6 * 10000000) / 10000000
            )
        )
    }
    return path
}
//// 인코드
//fun encode(path: List<com.google.android.gms.maps.model.LatLng>): String? {
//    var lastLat: Long = 0
//    var lastLng: Long = 0
//    val result = StringBuffer()
//    for (point in path) {
//        val lat = Math.round(point.latitude * 1e6)
//        val lng = Math.round(point.longitude * 1e6)
//        val dLat = lat - lastLat
//        val dLng = lng - lastLng
//        encode(dLat, result)
//        encode(dLng, result)
//        lastLat = lat
//        lastLng = lng
//    }
//    return result.toString()
//}
//private fun encode(v: Long, result: StringBuffer) {
//    var v = v
//    v = if (v < 0) (v shl 1).inv() else v shl 1
//    while (v >= 0x20) {
//        result.append(Character.toChars((0x20 or (v and 0x1f).toInt()) + 63))
//        v = v shr 5
//    }
//    result.append(Character.toChars((v + 63).toInt()))
//}

