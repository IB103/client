package com.hansung.capstone.linechart

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import com.hansung.capstone.retrofit.RidingData

open class LineChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    val dataPoints = mutableListOf<RidingData>()

    protected val linePaint = Paint().apply {
        color = 0xFF0000FF.toInt() // 파란색
        strokeWidth = 5f
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    fun setDataPoints(points: List<RidingData>) {
        dataPoints.clear()
        dataPoints.addAll(points)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val path = Path()
        if (dataPoints.isNotEmpty()) {
            val firstPoint = dataPoints.first()
            path.moveTo(firstPoint.date.toFloat(), firstPoint.distance.toFloat())

            for (i in 1 until dataPoints.size) {
                val point = dataPoints[i]
                path.lineTo(point.date.toFloat(), point.distance.toFloat())
            }
        }

        canvas.drawPath(path, linePaint)
    }
}
