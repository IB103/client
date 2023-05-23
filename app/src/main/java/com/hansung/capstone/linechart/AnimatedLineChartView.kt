package com.hansung.capstone.linechart

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

class AnimatedLineChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LineChartView(context, attrs, defStyleAttr) {

    private var animProgress = 0f

    fun startAnimation() {
        val animator = ObjectAnimator.ofFloat(this, "animProgress", 0f, 1f)
        animator.duration = 2000 // 애니메이션 지속시간 (2초)
        animator.start()
    }

    fun setAnimProgress(progress: Float) {
        animProgress = progress
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val path = Path()
        if (dataPoints.isNotEmpty()) {
            val lastIndex = (dataPoints.size * animProgress).toInt()
            val lastPoint = dataPoints[lastIndex.coerceAtMost(dataPoints.size - 1)]

            val firstPoint = dataPoints.first()
            path.moveTo(firstPoint.date.toFloat(), firstPoint.distance.toFloat())

            for (i in 1..lastIndex) {
                val point = dataPoints[i]
                path.lineTo(point.date.toFloat(), point.distance.toFloat())
            }

            path.lineTo(lastPoint.date.toFloat(), lastPoint.distance.toFloat())
        }

        canvas.drawPath(path, linePaint)
    }
}
