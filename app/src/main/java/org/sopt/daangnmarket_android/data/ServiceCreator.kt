package org.sopt.daangnmarket_android.data

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.sopt.daangnmarket_android.data.api.WriteService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceCreator {
    private const val BASE_URL = "http://52.78.114.176:8000"

    private val retrofit: Retrofit = Retrofit.Builder() //생성자 호출
        .baseUrl(BASE_URL) //서버에 메인 URL 전달
        .client(
            OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }).build()
        ) // 승현 - 서버 통신 로그 찍어주는 코드입니다.
        .addConverterFactory(GsonConverterFactory.create()) //gson 컨버터 연동
        .build() //Retrofit 객체 변환

    val writeService: WriteService = retrofit.create(WriteService::class.java)
}