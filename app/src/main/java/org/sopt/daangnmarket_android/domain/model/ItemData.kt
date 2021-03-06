package org.sopt.daangnmarket_android.domain.model

data class ItemData(
    val title: String,
    val location: String,
    val price: Long,
    val image: String,
    val likeCount: Int,
    val chatCount: Int,
    val timeBefore: String
)