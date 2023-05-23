package com.hansung.capstone.home

import android.content.Context
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.hansung.capstone.R

class CustomMarkerView(context: Context, layoutResource: Int) : MarkerView(context, layoutResource) {
    //    private val valueTextView: TextView = findViewById(R.id.valueTextView)
//
//    // Marker를 업데이트하는 메서드
//    fun updateContent(xValue: Float, yValue: Float) {
//        valueTextView.text = "날짜: $xValue\n거리: $yValue"
//    }
    private val tvXValue: TextView = findViewById(R.id.tvXValue)
    private val tvYValue: TextView = findViewById(R.id.tvYValue)

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        if (e != null) {
            tvXValue.text = "시간: ${e.x}"
            tvYValue.text = "거리: ${e.y}"
        }
        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        return MPPointF((-(width / 2)).toFloat(), (-height).toFloat())
    }
}