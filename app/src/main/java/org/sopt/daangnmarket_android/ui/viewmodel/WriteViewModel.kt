package org.sopt.daangnmarket_android.ui.viewmodel

import android.content.res.Resources
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.sopt.daangnmarket_android.domain.model.GalleryImage
import org.sopt.daangnmarket_android.domain.repository.GalleryRepository
import org.sopt.daangnmarket_android.util.SingleLiveEvent
import javax.inject.Inject

@HiltViewModel
class WriteViewModel @Inject constructor(
    private val galleryRepository: GalleryRepository
) : ViewModel() {
    private var _imageList = MutableLiveData<List<GalleryImage?>>()
    val imageList: LiveData<List<GalleryImage?>> get() = _imageList

    private var _selectedImageList = MutableLiveData<List<Pair<GalleryImage, Int>>>()
    val selectedImageList: LiveData<List<Pair<GalleryImage, Int>>> get() = _selectedImageList

    private var lastImageList: List<GalleryImage?> = listOf()

    private var lastSelectedImageList: List<Pair<GalleryImage, Int>> = listOf()

    private var _pickOutOfBound = SingleLiveEvent<Unit>()
    val pickOutOfBound: LiveData<Unit> get() = _pickOutOfBound

    private var _isConfirmPossible = MutableLiveData<Boolean>()
    val isConfirmPossible: LiveData<Boolean> get() = _isConfirmPossible

    fun fetchGallery() {
        if (imageList.value == null) {
            kotlin.runCatching {
                galleryRepository.fetchGallery()
            }.onSuccess {
                _imageList.value = it
                beginTransaction()
            }.onFailure {
                Log.e("fetchGallery() in ViewModel", "failed")
            }
        }
    }

    fun selectImage(layoutPosition: Int) {
        val imgList = imageList.value?.toMutableList() ?: mutableListOf()
        val selectedImgList =
            selectedImageList.value?.toMutableList() ?: mutableListOf()
        val img = imgList[layoutPosition] ?: throw IndexOutOfBoundsException()
        img.isSelected = !img.isSelected
        Log.i("isSelected", img.isSelected.toString())
        when (img.isSelected) {
            true -> {
                if (selectedImgList.size < 10) {
                    selectedImgList.add(Pair(img, layoutPosition))
                } else {
                    img.isSelected = !img.isSelected
                    _pickOutOfBound.call()
                }
            }
            false -> selectedImgList.remove(selectedImgList.find { it.first.image == img.image })
        }
        selectedImgList.forEachIndexed { index, pair ->
            pair.first.selectOrder = index + 1
            imgList[pair.second] = pair.first
        }
        _imageList.value = imgList
        _selectedImageList.value = selectedImgList
        _isConfirmPossible.value = selectedImgList.isNotEmpty()
    }

    fun unSelectImage(galleryImage: GalleryImage) {
        val imgList = imageList.value?.toMutableList() ?: mutableListOf()
        val selectedImgList =
            selectedImageList.value?.toMutableList() ?: mutableListOf()
        val img =
            selectedImgList.find { it.first == galleryImage } ?: throw Resources.NotFoundException()
        img.first.isSelected = !img.first.isSelected
        selectedImgList.remove(img)
        selectedImgList.forEachIndexed { index, pair ->
            pair.first.selectOrder = index + 1
            imgList[pair.second] = pair.first
        }
        _imageList.value = imgList
        _selectedImageList.value = selectedImgList
    }

    fun beginTransaction() {
        imageList.value?.let {
            lastImageList = it.map { image ->
                image?.copy()
            }
            Log.i("lastImageList", lastImageList.toString())
            lastSelectedImageList = selectedImageList.value?.map { pair ->
                Pair(pair.first.copy(), pair.second)
            } ?: listOf()
            Log.i("lastSelectedImageList", lastSelectedImageList.toString())
        }
    }

    fun rollback() {
        _imageList.value = lastImageList
        Log.i("lastImageList", lastImageList.toString())
        _selectedImageList.value = lastSelectedImageList
        Log.i("lastSelectedImageList", lastSelectedImageList.toString())
    }
}