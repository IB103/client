package com.hansung.capstone.post

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class PostImageAdapterDecoration:RecyclerView.ItemDecoration() {
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

        when (position){
            0 -> outRect.left = offset
            count-1 -> outRect.right = offset
            else -> {
                outRect.left = offset
                outRect.right = offset
            }
        }
    }
}