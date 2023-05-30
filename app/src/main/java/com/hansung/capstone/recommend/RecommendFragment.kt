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
import kotlin.properties.Delegates

class RecommendFragment : Fragment() {
    lateinit var binding: FragmentRecommendBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecommendAdapter
    private var fusedLocationClient: FusedLocationProviderClient? = null // 사용자 위치 얻기
    private lateinit var placeName: String
    private lateinit var intent: Intent
    private lateinit var globalAddress: String
    val api = RecommendService.create()
    private var page by Delegates.notNull<Int>()
    private var totalPage by Delegates.notNull<Int>()

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
                    globalAddress = placeName
                    page = 0
                    api.getUserRecommend(globalAddress, page)
                        .enqueue(object : Callback<UserRecommendDTO> {
                            override fun onResponse(
                                call: Call<UserRecommendDTO>,
                                response: Response<UserRecommendDTO>
                            ) {
                                val body = response.body()
                                if (body!!.data.isNotEmpty()) {
                                    binding.RecommendRecyclerView.visibility = View.VISIBLE
                                    binding.noResult.visibility = View.GONE
                                    totalPage = body.totalPage
                                    adapter.setInitItems(body.data)
                                } else {
                                    binding.RecommendRecyclerView.visibility = View.GONE
                                    binding.noResult.visibility = View.VISIBLE
                                }
                                binding.recommendSwipe.setOnRefreshListener {
                                    page = 0
                                    api.getUserRecommend(globalAddress, page)
                                        .enqueue(object : Callback<UserRecommendDTO> {
                                            override fun onResponse(
                                                call: Call<UserRecommendDTO>,
                                                response: Response<UserRecommendDTO>
                                            ) {
                                                val refBody = response.body()
                                                if (refBody!!.data.isNotEmpty()) {
                                                    binding.RecommendRecyclerView.visibility =
                                                        View.VISIBLE
                                                    binding.noResult.visibility = View.GONE
                                                    totalPage = body.totalPage
                                                    adapter.setInitItems(refBody.data)
                                                } else {
                                                    binding.RecommendRecyclerView.visibility =
                                                        View.GONE
                                                    binding.noResult.visibility = View.VISIBLE
                                                }
                                            }

                                            override fun onFailure(
                                                call: Call<UserRecommendDTO>,
                                                t: Throwable
                                            ) {
                                                Log.d("getUserRecommend:", "onFailure : $t")
                                            }
                                        })
                                    binding.recommendSwipe.isRefreshing = false
                                }
                                recyclerView.addOnScrollListener(object :
                                    RecyclerView.OnScrollListener() {
                                    override fun onScrolled(
                                        recyclerView: RecyclerView,
                                        dx: Int,
                                        dy: Int
                                    ) {
                                        super.onScrolled(recyclerView, dx, dy)
                                        if (!recyclerView.canScrollVertically(1) && page < totalPage - 1) {
                                            readRecommend(globalAddress, ++page)
                                        }
                                    }
                                })


                            }

                            override fun onFailure(call: Call<UserRecommendDTO>, t: Throwable) {
                                Log.d("getUserRecommend:", "onFailure : $t")
                            }
                        })
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
        // 초기화
        recyclerView = binding.RecommendRecyclerView
        adapter = RecommendAdapter(requireContext())
        recyclerView.adapter = adapter

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
                            if (address != null) {
                                binding.setLocationText.text = address
                                globalAddress = address
                                page = 0
                                api.getUserRecommend(globalAddress, page)
                                    .enqueue(object : Callback<UserRecommendDTO> {
                                        override fun onResponse(
                                            call: Call<UserRecommendDTO>,
                                            response: Response<UserRecommendDTO>
                                        ) {
                                            val body = response.body()
                                            if (body!!.data.isNotEmpty()) {
                                                binding.RecommendRecyclerView.visibility =
                                                    View.VISIBLE
                                                binding.noResult.visibility = View.GONE
                                                totalPage = body.totalPage
                                                adapter.setInitItems(body.data)
                                            } else {
                                                binding.RecommendRecyclerView.visibility = View.GONE
                                                binding.noResult.visibility = View.VISIBLE
                                            }
                                            binding.recommendSwipe.setOnRefreshListener {
                                                page = 0
                                                api.getUserRecommend(globalAddress, page)
                                                    .enqueue(object : Callback<UserRecommendDTO> {
                                                        override fun onResponse(
                                                            call: Call<UserRecommendDTO>,
                                                            response: Response<UserRecommendDTO>
                                                        ) {
                                                            val body2 = response.body()
                                                            if (body2!!.data.isNotEmpty()) {
                                                                binding.RecommendRecyclerView.visibility =
                                                                    View.VISIBLE
                                                                binding.noResult.visibility =
                                                                    View.GONE
                                                                totalPage = body2.totalPage
                                                                adapter.setInitItems(body2.data)
                                                            } else {
                                                                binding.RecommendRecyclerView.visibility =
                                                                    View.GONE
                                                                binding.noResult.visibility =
                                                                    View.VISIBLE
                                                            }
                                                        }

                                                        override fun onFailure(
                                                            call: Call<UserRecommendDTO>,
                                                            t: Throwable
                                                        ) {
                                                            Log.d("getUserRecommend:", "실패 : $t")
                                                        }
                                                    })
                                                binding.recommendSwipe.isRefreshing = false
                                            }
                                            recyclerView.addOnScrollListener(object :
                                                RecyclerView.OnScrollListener() {
                                                override fun onScrolled(
                                                    recyclerView: RecyclerView,
                                                    dx: Int,
                                                    dy: Int
                                                ) {
                                                    super.onScrolled(recyclerView, dx, dy)
                                                    if (!recyclerView.canScrollVertically(1) && page < totalPage - 1) {
                                                        readRecommend(globalAddress, ++page)
                                                    }
                                                }
                                            })
                                        }

                                        override fun onFailure(
                                            call: Call<UserRecommendDTO>,
                                            t: Throwable
                                        ) {
                                            Log.d("getUserRecommend:", "실패 : $t")
                                        }
                                    })
                            } else {
                                binding.setLocationText.text = "위치 설정 오류"
                                binding.RecommendRecyclerView.visibility = View.GONE
                                binding.noResult.visibility = View.VISIBLE
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
                    if (body.documents.isNotEmpty()) {
                        val resultAddress =
                            "${body.documents[0].address.region_1depth_name} ${body.documents[0].address.region_2depth_name}"
                        callback(resultAddress)
                    } else {
                        callback(null)
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

    // 스크롤 시 다음 페이지 추가
    private fun readRecommend(address: String, page: Int) {
        api.getUserRecommend(address, page)
            .enqueue(object : Callback<UserRecommendDTO> {
                override fun onResponse(
                    call: Call<UserRecommendDTO>,
                    response: Response<UserRecommendDTO>,
                ) {
                    val body = response.body()
                    if (body!!.data.isNotEmpty()) {
                        adapter.run {
                            submitList((body.data))
                        }
                    }
                }

                override fun onFailure(call: Call<UserRecommendDTO>, t: Throwable) {
                    Log.d("getUserRecommend:", "onFailure : $t")
                }
            })
    }
}