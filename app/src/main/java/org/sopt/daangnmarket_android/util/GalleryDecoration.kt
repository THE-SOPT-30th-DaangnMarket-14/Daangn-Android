package org.sopt.daangnmarket_android.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class GalleryDecoration(private val offset: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val offset = (offset / 3).dpToPx()
        val index = (view.layoutParams as GridLayoutManager.LayoutParams).spanIndex
        val position = parent.getChildLayoutPosition(view)

        // left, right spacing
        when (index) {
            0 -> {
                outRect.right = offset * 2
            }
            1 -> {
                outRect.left = offset
                outRect.right = offset
            }
            2 -> {
                outRect.left = offset * 2
            }
        }

        // top, bottom spacing
        if(position >= 3) {
            outRect.top = offset * 3
        }
    }
}