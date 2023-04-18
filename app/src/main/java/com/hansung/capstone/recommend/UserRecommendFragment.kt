package com.hansung.capstone.recommend

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hansung.capstone.databinding.FragmentUserRecommendBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class UserRecommendFragment : Fragment() {
    private var _binding: FragmentUserRecommendBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserRecommendBinding.inflate(inflater,container,false)
//        return inflater.inflate(R.layout.fragment_user_recommend, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val api = RecommnedService.create()
        api.getUserRecommend("서울",0)
            .enqueue(object : Callback<UserRecommendDTO> {
                override fun onResponse(
                    call: Call<UserRecommendDTO>,
                    response: Response<UserRecommendDTO>,
                ) {
                    Log.d("getAllPost:", "성공 : ${response.body().toString()}")
                    val body = response.body()
                    val userRecommendRecyclerview = binding.userRecommendRecyclerview
                    activity?.runOnUiThread {
                        userRecommendRecyclerview.adapter =
                            body?.let { UserRecommendAdapter(it) }
                    }
                }
                override fun onFailure(call: Call<UserRecommendDTO>, t: Throwable) {
                    Log.d("getAllPost:", "실패 : $t")
                }
            })
    }
}