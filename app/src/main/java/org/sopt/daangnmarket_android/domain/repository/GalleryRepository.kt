package org.sopt.daangnmarket_android.domain.repository

import org.sopt.daangnmarket_android.domain.model.GalleryImage

interface GalleryRepository {
    fun fetchGallery(): List<GalleryImage?>
}