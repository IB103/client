package com.hansung.capstone.recommend

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.hansung.capstone.databinding.FragmentRecommendBottomBinding


//class RecommendBottomFragment : Fragment() {
class RecommendBottomFragment : BottomSheetDialogFragment() {
    lateinit var binding: FragmentRecommendBottomBinding

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRecommendBottomBinding.inflate(inflater, container, false)

        initViewPager()

        return binding.root

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("나왔다","ㅇㅇ")
//        initViewPager()
    }
//
    private fun initViewPager() {
        val viewPager: ViewPager2 = binding.tabViewPager
        val tabLayout: TabLayout = binding.tabLayoutRecommend
        //ViewPager2 Adapter 셋팅
        val tabViewPager = TabViewPagerAdapter(this)
        viewPager.adapter = tabViewPager

        tabViewPager.addFragment(PublicRecommendFragment())
        tabViewPager.addFragment(UserRecommendFragment())

        //Adapter 연결
        binding.tabViewPager.isUserInputEnabled = false
        binding.tabViewPager.apply {
            adapter = tabViewPager

            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                }
            })
        }

        //ViewPager, TabLayout 연결
        TabLayoutMediator(binding.tabLayoutRecommend, binding.tabViewPager) { tab, position ->
//            Log.e("YMC", "ViewPager position: ${position}")
            when (position) {
                0 -> tab.text = "공공"
                1 -> tab.text = "유저"
            }
        }.attach()
    }


}