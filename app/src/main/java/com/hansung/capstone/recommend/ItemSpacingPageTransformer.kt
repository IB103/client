package com.hansung.capstone.recommend

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs

//class ItemSpacingPageTransformer(private val spacingPx: Int) : ViewPager2.PageTransformer {
class ItemSpacingPageTransformer : ViewPager2.PageTransformer {
    override fun transformPage(page: View, position: Float) {
//        val offsetPx = spacingPx / 2
        val scaleFactor = 1 - 0.2f * abs(position) // 크기 조정을 위한 비율
//        var translationX = -position * offsetPx // X축 이동 값

        page.apply {
//            translationX = translationX
            scaleX = scaleFactor
            scaleY = scaleFactor
        }
    }
}