package com.hansung.capstone.recommend

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hansung.capstone.R
import com.hansung.capstone.databinding.FragmentRecommendBinding

class RecommendFragment : Fragment() {
    lateinit var binding: FragmentRecommendBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecommendBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomSheet = childFragmentManager.beginTransaction()
        bottomSheet.replace(R.id.fragment_recommend, RecommendBottomFragment())
        // fragment_recommend에 갈아끼우기
        bottomSheet.commit()
    }
}