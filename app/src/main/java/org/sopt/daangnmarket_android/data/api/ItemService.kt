package org.sopt.daangnmarket_android.data.api

import org.sopt.daangnmarket_android.data.response.ResponseItem
import retrofit2.Call
import retrofit2.http.GET

interface ItemService {
    @GET("/item")
    fun getItems(): Call<ResponseItem>
}