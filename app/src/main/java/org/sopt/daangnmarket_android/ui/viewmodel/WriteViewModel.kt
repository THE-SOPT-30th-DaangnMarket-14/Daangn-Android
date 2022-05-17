package org.sopt.daangnmarket_android.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.sopt.daangnmarket_android.domain.model.GalleryImage
import org.sopt.daangnmarket_android.domain.repository.GalleryRepository
import javax.inject.Inject

@HiltViewModel
class WriteViewModel @Inject constructor(
    private val galleryRepository: GalleryRepository
) : ViewModel() {
    private var _imageList = MutableLiveData<List<GalleryImage?>>()
    val imageList: LiveData<List<GalleryImage?>> get() = _imageList

    private var _selectedImageList = MutableLiveData<List<Pair<GalleryImage, Int>>>()
    val selectedImageList: LiveData<List<Pair<GalleryImage, Int>>> get() = _selectedImageList

    fun fetchGallery() {
        kotlin.runCatching {
            galleryRepository.fetchGallery()
        }.onSuccess {
            _imageList.value = it
        }.onFailure {
            Log.e("fetchGallery() in ViewModel", "failed")
        }
    }
}