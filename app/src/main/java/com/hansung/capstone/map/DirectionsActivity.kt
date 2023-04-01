package com.hansung.capstone.map

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
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
//            Log.d("intent 값 전달","$position $placeName $placeLat $placeLng")
                runOnUiThread {
                    waypointsAdapter.updateItem(
                        position!!.toInt(),
                        placeName!!,
                        placeLat!!,
                        placeLng!!
                    )
                }
            }
        }
}

data class Waypoint(
    var place_name: String? = null,
    var place_lat: Double? = null,
    var place_lng: Double? = null,
)