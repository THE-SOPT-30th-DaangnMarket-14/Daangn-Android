package org.sopt.daangnmarket_android.ui.view.data

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ItemService {
    @GET("/item")
    fun getItems(): Call<ResponseItem>
}