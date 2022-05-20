package org.sopt.daangnmarket_android.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class WriteDecoration(private val topOffset: Int, private val bottomOffset: Int, private val leftOffset: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val position = parent.getChildLayoutPosition(view)

        outRect.top = topOffset.dpToPx()
        outRect.bottom = bottomOffset.dpToPx()

        if(position == 0) {
            outRect.left = leftOffset.dpToPx()
        }
    }
}