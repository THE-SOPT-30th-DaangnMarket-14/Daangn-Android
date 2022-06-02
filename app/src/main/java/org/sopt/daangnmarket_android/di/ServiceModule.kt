package org.sopt.daangnmarket_android.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.sopt.daangnmarket_android.data.service.DaangnService
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {
    @Singleton
    @Provides
    fun provideDaangnService(
        retrofit: Retrofit
    ): DaangnService = retrofit.create(DaangnService::class.java)
}