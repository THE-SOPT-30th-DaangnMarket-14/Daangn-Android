package org.sopt.daangnmarket_android.request

import java.io.File

data class RequestWrite(
    val title : String,
    val price : Int,
    val content : String,
    val image : File
)
