package org.sopt.daangnmarket_android.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.sopt.daangnmarket_android.request.RequestWrite
import org.sopt.daangnmarket_android.response.ResponseWrite
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface WriteService {
    @Multipart
    @POST
    fun write(
        @Part title: MultipartBody.Part,
        @Part price: MultipartBody.Part,
        @Part content: MultipartBody.Part,
        @Part image : MultipartBody.Part
    ): Call<ResponseWrite>
}