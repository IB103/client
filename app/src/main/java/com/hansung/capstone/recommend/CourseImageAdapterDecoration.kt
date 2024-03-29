package com.hansung.capstone.recommend

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class CourseImageAdapterDecoration : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        val position = parent.getChildAdapterPosition(view)
        val count = state.itemCount
        val offset = 20

        when (position) {
            count - 1 -> {
            }
            else -> {
                outRect.right = offset
            }
        }
    }
}