package com.hansung.capstone.recommend

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.hansung.capstone.*
import com.hansung.capstone.databinding.FragmentRecommendBinding
import com.hansung.capstone.map.KakaoSearchAPI
import com.hansung.capstone.map.ResultGetAddress
import com.hansung.capstone.retrofit.PermissionUtils
import com.hansung.capstone.retrofit.Permissions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecommendFragment : Fragment() {
    lateinit var binding: FragmentRecommendBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecommendAdapter
    private var fusedLocationClient: FusedLocationProviderClient? = null // 사용자 위치 얻기
    private lateinit var placeName: String
    private lateinit var intent: Intent

    // 권한 요청 후 처리용 Launcher
    private var requestLocationPermissionLauncher: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permission ->
            if (permission.all { it.value }) {
                checkLocationPermission()
            } else {
                Toast.makeText(activity, "추천 코스를 위해 권한을 허용해 주세요.", Toast.LENGTH_SHORT).show()
                checkLocationPermission()
            }
        }

    private val locationSetLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            // 결과 받아서 처리
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                placeName = result.data?.getStringExtra("setLocation").toString()
                activity?.runOnUiThread {
                    binding.setLocationText.text = placeName
                    readRecommend(placeName)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkLocationPermission()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecommendBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = binding.RecommendRecyclerView

        adapter = RecommendAdapter(requireContext())
        recyclerView.adapter = adapter

        val itemList = listOf<UserRecommend>() // 데이터 리스트 생성
        adapter.submitList(itemList) // 어댑터에 데이터 리스트 전달 및 업데이트 요청

        binding.setLocationButton.setOnClickListener {
            locationSetLauncher.launch(intent)
        }
    }

    @SuppressLint("MissingPermission")
    fun checkLocationPermission() {
        val permissionCheckResult =
            PermissionUtils.checkPermissions(requireActivity(), Permissions.permissionsLocation)
        if (permissionCheckResult.isEmpty()) { // 권한이 모두 승인된 상태면
            fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(MyApplication.applicationContext()) // fusedLocationClient 초기화
            fusedLocationClient!!.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        intent = Intent(requireContext(), LocationSetActivity::class.java)
                        intent.putExtra("prePosition", "${location.latitude},${location.longitude}")
                        searchAddress(location.latitude, location.longitude) { address ->
//                                binding.setLocationText.text = address
                            if (address != null) {
                                binding.setLocationText.text = address
                                readRecommend(address)
                            } else {
                                binding.setLocationText.text = ""
                            }
                        }
                    }
                }
            fusedLocationClient = null

        } else {
            Toast.makeText(
                MyApplication.applicationContext(),
                "위치 정보 권한을 허용해주세요.",
                Toast.LENGTH_SHORT
            ).show()
            requestLocationPermissionLauncher.launch(permissionCheckResult.toTypedArray())
        }
    }

    private fun searchAddress(lat: Double, lng: Double, callback: (String?) -> Unit) {
        val api = KakaoSearchAPI.create()
        api.getAddress(
            BuildConfig.KAKAO_REST_API_KEY,
            lng.toString(),
            lat.toString()
        ).enqueue(object : Callback<ResultGetAddress> {
            override fun onResponse(
                call: Call<ResultGetAddress>,
                response: Response<ResultGetAddress>
            ) {
                val body = response.body()
                if (body != null) {
                    Log.d("getAddress", "onResponse: $body")
                    if (body.documents.isNotEmpty()) {
                        val resultAddress =
                            "${body.documents[0].address.region_1depth_name} ${body.documents[0].address.region_2depth_name}"
                        callback(resultAddress)
                    } else {
                        val resultAddress = "위치 정보가 없습니다."
                        callback(resultAddress)
                    }
                } else {
                    callback(null)
                }
            }

            override fun onFailure(
                call: Call<ResultGetAddress>,
                t: Throwable
            ) {
                Log.d("getAddress", "onFailure: $t")
                callback(null)
            }
        })
    }

    private fun readRecommend(address: String) {
        Log.d("getUserRecommend", "기준 지역 $address")
        val api = RecommendService.create()
        api.getUserRecommend(address, 0)
            .enqueue(object : Callback<UserRecommendDTO> {
                override fun onResponse(
                    call: Call<UserRecommendDTO>,
                    response: Response<UserRecommendDTO>,
                ) {
                    Log.d("getUserRecommend:", "onResponse : ${response.body().toString()}")
                    val body = response.body()
                    if (body!!.data.isNotEmpty()) {
                        binding.RecommendRecyclerView.visibility = View.VISIBLE
                        binding.noResult.visibility = View.GONE
                        activity?.runOnUiThread {
                            body.let { adapter.submitList(body.data) }
                        }
                    } else {
                        binding.RecommendRecyclerView.visibility = View.GONE
                        binding.noResult.visibility = View.VISIBLE
                    }
                }

                override fun onFailure(call: Call<UserRecommendDTO>, t: Throwable) {
                    Log.d("getUserRecommend:", "onFailure : $t")
                }
            })
    }
}