package org.sopt.daangnmarket_android.data.service

import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.sopt.daangnmarket_android.data.entity.response.ResponsePost
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap

interface DaangnService {
    @Multipart
    @POST("/item")
    suspend fun postItem(
        @Part image: List<MultipartBody.Part>,
        @PartMap data: HashMap<String, RequestBody>
    ): ResponsePost
}