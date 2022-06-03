package org.sopt.daangnmarket_android.data.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.sopt.daangnmarket_android.response.ResponseWrite
import retrofit2.Call
import retrofit2.http.*

interface WriteService {
    @Multipart
    @POST("/item")
    fun postItem(
        @Part image: List<MultipartBody.Part>,
        @PartMap data: HashMap<String, RequestBody>
    ): Call<ResponseWrite>
}