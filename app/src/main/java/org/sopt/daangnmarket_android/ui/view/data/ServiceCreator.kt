package org.sopt.daangnmarket_android.ui.view.data

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

object ServiceCreator {
    private const val BASE_URL = "http://52.78.114.176:8000/"

    private val itemRetrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(provideOkHttpClient())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .run {
            addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            build()
        }

    class AppInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain)
                : Response = with(chain) {
            val newRequest = request().newBuilder()
                .addHeader("Content-Type", "application/json")
                .build()

            proceed(newRequest)
        }
    }

    val itemService: ItemService = itemRetrofit.create(ItemService::class.java)
}


