package org.sopt.daangnmarket_android.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import org.sopt.daangnmarket_android.data.service.DaangnService
import org.sopt.daangnmarket_android.ui.usecase.DaangnMultiPartUseCase

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {
    @ViewModelScoped
    @Provides
    fun provideMultiPartUseCase(
        service: DaangnService
    ): DaangnMultiPartUseCase = DaangnMultiPartUseCase(service)
}