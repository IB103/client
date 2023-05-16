package com.hansung.capstone.course

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import com.hansung.capstone.databinding.ActivityDialogBinding
import com.hansung.capstone.map.WaypointsSearchActivity
import com.naver.maps.geometry.LatLng

class DialogActivity : AppCompatActivity() {
    private val binding by lazy { ActivityDialogBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        val searchLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    // 결과 처리
//                    val position = result.data?.getIntExtra("position", -1)
                    val placeName = result.data?.getStringExtra("place_name")
//                    val placeLat = result.data?.getStringExtra("place_lat")
//                    val placeLng = result.data?.getStringExtra("place_lng")
                    val placeUrl = result.data?.getStringExtra("place_url")
                    runOnUiThread {
                        binding.placeName.setText(placeName)
//                        waypointsAdapter.updateItem(
//                            position!!.toInt(),
//                            placeName!!,
////                            placeLat!!,
////                            placeLng!!,
//                            placeUrl!!
//                        )
                    }
                }
            }

//        val intent = Intent(this, WaypointsSearchActivity::class.java)
//        searchLauncher.launch(intent)

        binding.placeSearch.setOnClickListener {
            val intent = Intent(this, WaypointsSearchActivity::class.java)
            searchLauncher.launch(intent)
        }
    }
}