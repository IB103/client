package com.hansung.capstone.map

import android.Manifest
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.PermissionChecker
import androidx.recyclerview.widget.RecyclerView
import com.hansung.capstone.BuildConfig
import com.hansung.capstone.MainActivity
import com.hansung.capstone.MyApplication
import com.hansung.capstone.databinding.ItemLocationSearchResultsBinding
import com.hansung.capstone.map.MapFragment.Companion.path
import com.hansung.capstone.map.MapFragment.Companion.staticMarker
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.PathOverlay
import com.naver.maps.map.util.MarkerIcons
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.round
import kotlin.properties.Delegates


class SearchLocationAdapter(
    private val result: ResultSearchKeyword,
    private val naverMap: NaverMap
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

    private fun hasPermission(): Boolean {
        return PermissionChecker.checkSelfPermission(
            MyApplication.ApplicationContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) ==
                PermissionChecker.PERMISSION_GRANTED &&
                PermissionChecker.checkSelfPermission(
                    MyApplication.ApplicationContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) ==
                PermissionChecker.PERMISSION_GRANTED
    }

    inner class LocationSearchHolder(private val binding: ItemLocationSearchResultsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(items: Place) {
            binding.resultTitle.text = items.place_name
            binding.resultRoadAddress.text = items.road_address_name
            binding.resultAddress.text = items.address_name
            // 클릭 이벤트
            itemView.setOnClickListener {
                val cameraUpdate =
                    CameraUpdate.scrollTo(LatLng(items.y.toDouble(), items.x.toDouble()))
                        .animate(CameraAnimation.Easing)
                naverMap.moveCamera(cameraUpdate)
            }
            binding.openPage.setOnClickListener {
                val address = items.place_url
                MainActivity.getInstance()?.goWebPage(address)
            }
            binding.direction.setOnClickListener {

                naverMap.locationTrackingMode = LocationTrackingMode.NoFollow
                var userLocationLat = 0.0
                var userLocationLng = 0.0
                naverMap.locationSource?.activate { location ->
                    userLocationLat = location!!.latitude
                    userLocationLng = location.longitude
                    Log.d("좌표", "${location.latitude}, ${location.longitude}")
                }

//                var userLocationLat = 0.0
//                    var userLocationLat by Delegates.notNull<Double>()
//                var userLocationLng = 0.0
//                    var userLocationLng by Delegates.notNull<Double>()
//                naverMap.addOnLocationChangeListener { location ->
//                    userLocationLat = location.latitude
//                    userLocationLng = location.longitude
////                    Toast.makeText(, "${location.latitude}, ${location.longitude}", Toast.LENGTH_SHORT).show()
//                    Log.d("좌표", "${location.latitude}, ${location.longitude}")
////                    val userMarker = Marker()
////                    userMarker.position = LatLng(location.latitude, location.longitude)
////                    userMarker.icon = MarkerIcons.BLUE
////                    userMarker.map = naverMap
//                }


                val api = MapboxDirectionAPI.create()
                Log.d("현재 좌표","$userLocationLng, $userLocationLat")
                api.getSearchDirection(
//                    127.0103184, 37.5825674, items.x.toDouble(), items.y.toDouble(),
                    userLocationLng, userLocationLat, items.x.toDouble(), items.y.toDouble(),
//                    "geojson","full", BuildConfig.MAPBOX_DOWNLOADS_TOKEN
                    "polyline6", "full", BuildConfig.MAPBOX_DOWNLOADS_TOKEN
                )
                    .enqueue(object : Callback<ResultSearchDirections> {
                        override fun onResponse(
                            call: Call<ResultSearchDirections>,
                            response: Response<ResultSearchDirections>
                        ) {
                            val body = response.body()
                            Log.d("경로 결과", "Body: ${response.body()}")
                            val deco: List<com.google.android.gms.maps.model.LatLng> =
                                decode(body!!.routes[0].geometry)
                            Log.d("경로 결과", deco.toString())
//                            val routeLngLat: List<List<Number>> =
//                                body!!.routes[0].geometry.coordinates
                            val routeLatLng: MutableList<LatLng> =
                                emptyList<LatLng>().toMutableList()

//                            Log.d("값 조회", routeLngLat.toString())
//                            for (y in routeLngLat) {
//                                routeLatLng += LatLng((y[1].toDouble()), (y[0].toDouble()))
//                            }
                            for (y in deco) {
                                routeLatLng += LatLng(y.latitude, y.longitude)
                            }
//                            MapFragment.marker.map = null // 마커 지우기
                            for (i in MapFragment.markers) {
                                i.map = null
                            }
//                            val selectMarker = Marker()
                            staticMarker.position =
                                LatLng(items.y.toDouble(), items.x.toDouble())
                            staticMarker.icon = MarkerIcons.BLUE
//                            selectMarker.iconTintColor = Color.BLUE
                            staticMarker.map = naverMap
                            path.coords = routeLatLng
                            path.outlineWidth = 0
//                            MapFragment.path.width = 20
                            path.color = Color.GREEN
                            path.map = naverMap
                        }

                        override fun onFailure(
                            call: Call<ResultSearchDirections>,
                            t: Throwable
                        ) {
                            Log.d("결과:", "실패 : $t")
                        }
                    })
//                }
//                else {
//                    naverMap.locationTrackingMode = LocationTrackingMode.NoFollow
//                }
            }
        }
    }

    fun decode(encodedPath: String): List<com.google.android.gms.maps.model.LatLng> {
        val len = encodedPath.length

        // For speed we preallocate to an upper bound on the final length, then
        // truncate the array before returning.
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

    fun encode(path: List<com.google.android.gms.maps.model.LatLng>): String? {
        var lastLat: Long = 0
        var lastLng: Long = 0
        val result = StringBuffer()
        for (point in path) {
            val lat = Math.round(point.latitude * 1e6)
            val lng = Math.round(point.longitude * 1e6)
            val dLat = lat - lastLat
            val dLng = lng - lastLng
            encode(dLat, result)
            encode(dLng, result)
            lastLat = lat
            lastLng = lng
        }
        return result.toString()
    }

    private fun encode(v: Long, result: StringBuffer) {
        var v = v
        v = if (v < 0) (v shl 1).inv() else v shl 1
        while (v >= 0x20) {
            result.append(Character.toChars((0x20 or (v and 0x1f).toInt()) + 63))
            v = v shr 5
        }
        result.append(Character.toChars((v + 63).toInt()))
    }

}
