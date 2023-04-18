//package com.hansung.capstone.recommend
//
//import android.os.Bundle
//import androidx.fragment.app.Fragment
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.viewpager2.widget.ViewPager2
//import com.google.android.material.tabs.TabLayout
//import com.google.android.material.tabs.TabLayoutMediator
//import com.hansung.capstone.databinding.FragmentRecommendTabBinding
//
//class RecommendTabFragment : Fragment() {
//    lateinit var binding: FragmentRecommendTabBinding
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        // Inflate the layout for this fragment
////        return inflater.inflate(R.layout.fragment_recommend_tab, container, false)
//        binding = FragmentRecommendTabBinding.inflate(inflater,container,false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
////        initViewPager()
//    }
//
////    private fun initViewPager() {
////        val viewPager: ViewPager2 = binding.tabViewPager
////        val tabLayout: TabLayout = binding.tabLayoutRecommend
////        //ViewPager2 Adapter 셋팅
////        val tabViewPager = TabViewPagerAdapter(this)
////        viewPager.adapter = tabViewPager
////
////        tabViewPager.addFragment(PublicRecommendFragment())
////        tabViewPager.addFragment(UserRecommendFragment())
////
////        //Adapter 연결
////        binding.tabViewPager.isUserInputEnabled = false
////        binding.tabViewPager.apply {
////            adapter = tabViewPager
////
////            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
////                override fun onPageSelected(position: Int) {
////                    super.onPageSelected(position)
////                }
////            })
////        }
////
////        //ViewPager, TabLayout 연결
////        TabLayoutMediator(binding.tabLayoutRecommend, binding.tabViewPager) { tab, position ->
//////            Log.e("YMC", "ViewPager position: ${position}")
////            when (position) {
////                0 -> tab.text = "공공"
////                1 -> tab.text = "유저"
////            }
////        }.attach()
////    }
//}