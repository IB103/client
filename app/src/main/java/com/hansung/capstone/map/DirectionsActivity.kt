package com.hansung.capstone.map

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import com.hansung.capstone.Waypoint
import com.hansung.capstone.databinding.ActivityDirectionsBinding

class DirectionsActivity : AppCompatActivity() {
    private val binding by lazy { ActivityDirectionsBinding.inflate(layoutInflater) }
    private lateinit var waypointsAdapter: WaypointsAdapter
    private lateinit var waypoints: MutableList<Waypoint>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        waypoints = MutableList(2) { Waypoint() }
        waypointsAdapter = WaypointsAdapter(this, waypoints)
//        waypointsAdapter = WaypointsAdapter(waypoints)
        binding.directionsRecyclerview.adapter = waypointsAdapter

        binding.directionsButton.setOnClickListener {
            // 쿼리문 받기
            val directionsQuery =
                waypointsAdapter.makeWaypointsDirectionQuery(waypointsAdapter.waypoints)
            val resultIntent = Intent()
            resultIntent.putExtra("directions_query", directionsQuery)

            // setResult()를 사용하여 결과 데이터 설정
            setResult(Activity.RESULT_OK, resultIntent)

            // 액티비티 종료
            finish()

        }
    }

    val myLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                // 결과 처리
                val position = result.data?.getIntExtra("position", -1)
                val placeName = result.data?.getStringExtra("place_name")
                val placeLat = result.data?.getStringExtra("place_lat")
                val placeLng = result.data?.getStringExtra("place_lng")
                val placeUrl = result.data?.getStringExtra("place_url")
//            Log.d("intent 값 전달","$position $placeName $placeLat $placeLng")
                runOnUiThread {
                    waypointsAdapter.updateItem(
                        position!!.toInt(),
                        placeName!!,
                        placeLat!!,
                        placeLng!!,
                        placeUrl!!
                    )
                }
            }
        }
}
//
//data class Waypoint(
//    var place_name: String? = null,
//    var place_lat: Double? = null,
//    var place_lng: Double? = null,
//    var place_url: String? = null
//):Parcelable{
//    constructor(parcel: Parcel) : this(
//        parcel.readString(),
//        parcel.readValue(Double::class.java.classLoader) as? Double,
//        parcel.readValue(Double::class.java.classLoader) as? Double,
//        parcel.readString()
//    )
//
//    // Parcelable 인터페이스에서 필요한 메서드입니다.
//    override fun describeContents(): Int {
//        return 0
//    }
//
//    // Parcelable 인터페이스를 구현하기 위한 메서드입니다.
//    override fun writeToParcel(parcel: Parcel, flags: Int) {
//        parcel.writeString(place_name)
//        parcel.writeValue(place_lat)
//        parcel.writeValue(place_lng)
//        parcel.writeString(place_url)
//    }
//
//    // CREATOR 상수를 정의하여 Parcelable.Creator 인터페이스를 구현합니다.
//    companion object CREATOR : Parcelable.Creator<Waypoint> {
//        // Parcel에서 Waypoint 객체를 생성합니다.
//        override fun createFromParcel(parcel: Parcel): Waypoint {
//            return Waypoint(parcel)
//        }
//
//        // Waypoint 객체의 배열을 생성합니다.
//        override fun newArray(size: Int): Array<Waypoint?> {
//            return arrayOfNulls(size)
//        }
//    }
//}