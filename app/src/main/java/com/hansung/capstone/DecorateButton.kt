package com.hansung.capstone

import android.graphics.Color
import androidx.core.content.ContextCompat
import com.hansung.capstone.post.PostDetailActivity
import kotlinx.android.synthetic.main.fragment_board.*

class DecorateButton(private val binding: BoardFragment) {
    fun decoTotalBt(){
        binding.freeCategory.setTextColor(Color.parseColor("#A4A4A4"))
        binding.courseCategory.setTextColor(Color.parseColor("#A4A4A4"))
        binding.totalCategory.setTextColor(Color.parseColor("#87D5AA"))
        binding.courseCategory.background= ContextCompat.getDrawable(binding.requireContext(),R.drawable.normal_border)
        binding.totalCategory.background= ContextCompat.getDrawable(binding.requireContext(),R.drawable.press_border)
        binding.freeCategory.background= ContextCompat.getDrawable(binding.requireContext(),R.drawable.normal_border)
    }
    fun decoFreeBt(){
        binding.courseCategory.setTextColor(Color.parseColor("#A4A4A4"))
        binding.totalCategory.setTextColor(Color.parseColor("#A4A4A4"))
        binding.freeCategory.setTextColor(Color.parseColor("#87D5AA"))
        binding.totalCategory.background=ContextCompat.getDrawable(binding.requireContext(),R.drawable.normal_border)
        binding.freeCategory.background=ContextCompat.getDrawable(binding.requireContext(),R.drawable.press_border)
        binding.courseCategory.background=ContextCompat.getDrawable(binding.requireContext(),R.drawable.normal_border)
    }
    fun decoCourseBt(){
        binding.freeCategory.setTextColor(Color.parseColor("#A4A4A4"))
        binding.totalCategory.setTextColor(Color.parseColor("#A4A4A4"))
        binding.courseCategory.setTextColor(Color.parseColor("#87D5AA"))
        binding.totalCategory.background=ContextCompat.getDrawable(binding.requireContext(),R.drawable.normal_border)
        binding.courseCategory.background=ContextCompat.getDrawable(binding.requireContext(),R.drawable.press_border)
        binding.freeCategory.background=ContextCompat.getDrawable(binding.requireContext(),R.drawable.normal_border)
    }
}