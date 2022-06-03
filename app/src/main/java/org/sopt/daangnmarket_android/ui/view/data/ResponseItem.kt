package org.sopt.daangnmarket_android.ui.view.data

data class ResponseItem(
    val status: Int,
    val success: Boolean,
    val message: String,
    val data: List<Data>
){
    data class Data(
        val title: String,
        val location: String,
        val price: Long,
        val image: String,
        val likeCount: Int,
        val chatCount: Int,
        val timeBefore: String
    )
}

