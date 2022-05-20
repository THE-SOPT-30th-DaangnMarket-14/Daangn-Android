package org.sopt.daangnmarket_android.util

import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import org.sopt.daangnmarket_android.domain.model.GalleryImage
import org.sopt.daangnmarket_android.ui.adapter.GalleryAdapter

class GalleryItemAnimator: DefaultItemAnimator() {
    class GalleryItemHolderInfo(val galleryImage: GalleryImage): ItemHolderInfo()

    override fun animateChange(
        oldHolder: RecyclerView.ViewHolder,
        newHolder: RecyclerView.ViewHolder,
        preInfo: ItemHolderInfo,
        postInfo: ItemHolderInfo
    ): Boolean {
        val holder = newHolder as GalleryAdapter.GalleryViewHolder

        if(preInfo is GalleryItemHolderInfo) {
            holder.bind(preInfo.galleryImage)
            return true
        }

        return super.animateChange(oldHolder, newHolder, preInfo, postInfo)
    }

    override fun canReuseUpdatedViewHolder(viewHolder: RecyclerView.ViewHolder): Boolean = true

    override fun canReuseUpdatedViewHolder(
        viewHolder: RecyclerView.ViewHolder,
        payloads: MutableList<Any>
    ): Boolean = true
}