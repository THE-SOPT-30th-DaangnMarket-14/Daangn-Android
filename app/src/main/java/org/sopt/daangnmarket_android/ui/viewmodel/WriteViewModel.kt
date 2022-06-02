package org.sopt.daangnmarket_android.ui.viewmodel

import android.content.res.Resources
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.BufferedSink
import org.sopt.daangnmarket_android.domain.repository.GalleryRepository
import org.sopt.daangnmarket_android.ui.usecase.DaangnMultiPartUseCase
import org.sopt.daangnmarket_android.util.SingleLiveEvent
import javax.inject.Inject
import org.sopt.daangnmarket_android.domain.model.GalleryImage as GalleryImage

@HiltViewModel
class WriteViewModel @Inject constructor(
    private val galleryRepository: GalleryRepository,
    private val multiPartUseCase: DaangnMultiPartUseCase
) : ViewModel() {
    private val _imageList = MutableLiveData<List<GalleryImage?>>()
    val imageList: LiveData<List<GalleryImage?>> get() = _imageList

    private val _selectedImageList = MutableLiveData<List<Pair<GalleryImage, Int>>>()
    val selectedImageList: LiveData<List<Pair<GalleryImage, Int>>> get() = _selectedImageList

    private var lastImageList: List<GalleryImage?> = listOf()

    private var lastSelectedImageList: List<Pair<GalleryImage, Int>> = listOf()

    private val _pickOutOfBound = SingleLiveEvent<Unit>()
    val pickOutOfBound: LiveData<Unit> get() = _pickOutOfBound

    private val _cameraImageAdded = SingleLiveEvent<Unit>()
    val cameraImageAdded: LiveData<Unit> get() = _cameraImageAdded

    private val _isConfirmPossible = MutableLiveData<Boolean>()
    val isConfirmPossible: LiveData<Boolean> get() = _isConfirmPossible

    private val _isPostSuccess = SingleLiveEvent<Unit>()
    val isPostSuccess: LiveData<Unit> get() = _isPostSuccess

    private val _data = MutableLiveData<String>()
    val data: LiveData<String> get() = _data

    val writeTitle = MutableLiveData<String>()
    val writePrice = MutableLiveData<String>()
    val writeContent = MutableLiveData<String>()

    fun fetchGallery() {
        if (imageList.value == null) {
            runCatching {
                galleryRepository.fetchGallery()
            }.onSuccess {
                _imageList.value = it
                beginTransaction()
            }.onFailure {
                Log.e("fetchGallery() in ViewModel", "failed")
            }
        }
    }

    fun addCameraImage(imageBitmap: Bitmap) {
        val imgList = imageList.value?.toMutableList() ?: mutableListOf()
        val selectedImgList =
            selectedImageList.value?.toMutableList() ?: mutableListOf()
        if (selectedImgList.size < 10) {
            val img = GalleryImage(
                image = imageBitmap,
                isSelected = true,
                selectOrder = selectedImgList.size + 1
            )
            selectedImgList.add(Pair(img, -1))
        }
        selectedImgList.forEachIndexed { index, pair ->
            pair.first.selectOrder = index + 1
            if (pair.second != -1) {
                imgList[pair.second] = pair.first
            }
        }
        Log.i("selectedImageList size", selectedImgList.size.toString())
        _imageList.value = imgList
        _selectedImageList.value = selectedImgList
        _cameraImageAdded.call()
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
            if (pair.second != -1) {
                imgList[pair.second] = pair.first
            }
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
            if (pair.second != -1) {
                imgList[pair.second] = pair.first
            }
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

    fun postItem() {
        viewModelScope.launch {
            runCatching {
                multiPartUseCase(
                    requireNotNull(writeTitle.value),
                    requireNotNull(writePrice.value),
                    requireNotNull(writeContent.value),
                    requireNotNull(selectedImageList.value).map { requireNotNull(it.first.image) }
                )
            }.onSuccess {
                _isPostSuccess.call()
                Log.i("postItem", "success")
            }.onFailure {
                Log.e("postItem", it.toString())
            }
        }
    }
}