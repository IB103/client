package com.hansung.capstone.board

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class BoardAdapterDecoration : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

//        val position = parent.getChildAdapterPosition(view)
//        val count = state.itemCount
        val offset = 10

        outRect.top = offset
        outRect.bottom = offset

//        when (position) {
//            0 -> {
//                outRect.top = offset
//                outRect.bottom = offset
//
//            }
//            count - 1 -> outRect.bottom = offset
//            else -> {
//                outRect.top = offset
//                outRect.bottom = offset
//            }
//        }
    }
}