package org.sopt.daangnmarket_android.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import org.sopt.daangnmarket_android.data.repository.GalleryRepositoryImpl
import org.sopt.daangnmarket_android.domain.repository.GalleryRepository
import javax.inject.Singleton

@Module
@InstallIn(ViewModelComponent::class)
object RepositoryModule {
    @ViewModelScoped
    @Provides
    fun provideGalleryRepository(
        @ApplicationContext context: Context
    ): GalleryRepository = GalleryRepositoryImpl(context)
}