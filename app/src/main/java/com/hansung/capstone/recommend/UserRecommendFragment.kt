package com.hansung.capstone.recommend

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hansung.capstone.databinding.FragmentUserRecommendBinding


class UserRecommendFragment : Fragment() {
    private var _binding: FragmentUserRecommendBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserRecommendBinding.inflate(inflater,container,false)
//        return inflater.inflate(R.layout.fragment_user_recommend, container, false)
        return binding.root
    }

}