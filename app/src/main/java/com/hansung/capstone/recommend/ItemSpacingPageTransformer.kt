package com.hansung.capstone.recommend

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs

class ItemSpacingPageTransformer : ViewPager2.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        val scaleFactor = 1 - 0.2f * abs(position) // 크기 조정을 위한 비율

        page.apply {
            scaleX = scaleFactor
            scaleY = scaleFactor
        }
    }
}