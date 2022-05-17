package org.sopt.daangnmarket_android.domain.model

import android.graphics.Bitmap

data class GalleryImage(
    val image: Bitmap,
    var isSelected: Boolean,
    var selectOrder: Int
)
