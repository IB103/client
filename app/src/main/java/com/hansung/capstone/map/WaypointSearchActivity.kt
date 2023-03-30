package com.hansung.capstone.map

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.hansung.capstone.BuildConfig
import com.hansung.capstone.databinding.ActivityDirectionsBinding
import com.hansung.capstone.databinding.ActivityWaypointSearchBinding
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.overlay.Marker
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WaypointSearchActivity : AppCompatActivity() {
    val binding by lazy { ActivityWaypointSearchBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // editText 검색 처리
        binding.waypointSearchBox.setOnEditorActionListener { _, id, _ ->
            if ((id == EditorInfo.IME_ACTION_SEARCH) && (binding.waypointSearchBox.text.toString()
                    .isNotBlank())
            ) {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.waypointSearchBox.windowToken, 0)
                locationSearch()
            } else {
                binding.waypointSearchBox.requestFocus()
                val manager: InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                manager.showSoftInput(binding.waypointSearchBox, InputMethodManager.SHOW_IMPLICIT)
                Toast.makeText(this, "검색어를 입력해주세요", Toast.LENGTH_SHORT).show()
            }
            true
        }
    }

    private fun locationSearch() {

        // 카카오 검색 api
        val api = KakaoSearchAPI.create()
        api.getSearchKeyword(
            BuildConfig.KAKAO_REST_API_KEY,
            binding.waypointSearchBox.text.toString()
        )
            .enqueue(object : Callback<ResultSearchKeyword> {
                override fun onResponse(
                    call: Call<ResultSearchKeyword>,
                    response: Response<ResultSearchKeyword>
                ) {
                    val body = response.body()
                    if (body != null) {
                        if (body.documents.isNotEmpty()) {
                            val waypointSearchResultRecyclerview = binding.waypointSearchResultRecyclerview

                            // 검색 결과 리사이클러뷰에 적용
                            runOnUiThread {
                                waypointSearchResultRecyclerview.adapter =
                                    WaypointsSearchAdapter(body)
                            }
                        }
                    }
                }
                override fun onFailure(call: Call<ResultSearchKeyword>, t: Throwable) {
                    Log.d("결과:", "실패 : $t")
                }
            })

    }
}