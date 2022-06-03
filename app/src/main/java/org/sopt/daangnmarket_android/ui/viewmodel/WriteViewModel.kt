package org.sopt.daangnmarket_android.ui.viewmodel

import android.content.res.Resources
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.BufferedSink
import org.sopt.daangnmarket_android.data.ServiceCreator
import org.sopt.daangnmarket_android.domain.model.GalleryImage
import org.sopt.daangnmarket_android.domain.repository.GalleryRepository
import org.sopt.daangnmarket_android.response.ResponseWrite
import org.sopt.daangnmarket_android.util.SingleLiveEvent
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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

    private var _cameraImageAdded = SingleLiveEvent<Unit>()
    val cameraImageAdded: LiveData<Unit> get() = _cameraImageAdded

    private var _isConfirmPossible = MutableLiveData<Boolean>()
    val isConfirmPossible: LiveData<Boolean> get() = _isConfirmPossible

    private var _data = MutableLiveData<String>()
    val data: LiveData<String> get() = _data

    val writeTitle = MutableLiveData<String>()
    val writePrice = MutableLiveData<String>()
    val writeContent = MutableLiveData<String>()

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

    fun multipart(
        title: String,
        price: String,
        content: String,
        image: List<Bitmap>
    ) {
        val titleRequestBody =
            title.toRequestBody("text/plain".toMediaTypeOrNull())
        val priceRequestBody =
            price.toRequestBody("text/plain".toMediaTypeOrNull())
        val contentRequestBody =
            content.toRequestBody("text/plain".toMediaTypeOrNull())

        val requestBodyHashMap = HashMap<String, RequestBody>()
        requestBodyHashMap["title"] = titleRequestBody
        requestBodyHashMap["price"] = priceRequestBody
        requestBodyHashMap["contents"] = contentRequestBody

        val imageListMultipartBody = mutableListOf<MultipartBody.Part>()

        for (i in 0 until image.size) {
            val imageRequestBody = BitmapRequestBody(image[i])
            val imageMultipartBody: MultipartBody.Part =
                MultipartBody.Part.createFormData(
                    "image",
                    "and" + System.currentTimeMillis()
                        .toString() + i.toString(), // 승현 - index 를 활용해 이미지 이름을 구분합니다.
                    imageRequestBody
                )
            imageListMultipartBody.add(imageMultipartBody)
        }

        val call: Call<ResponseWrite> =
            ServiceCreator.writeService.postItem(imageListMultipartBody, requestBodyHashMap)

        call.enqueue(object : Callback<ResponseWrite> {
            override fun onResponse(
                call: Call<ResponseWrite>,
                response: Response<ResponseWrite>
            ) {
                if (response.isSuccessful) {
                    Log.d("NetworkTest", "success")
                }
                // 승현 - 이 부분에 응답이 왔는데 성공응답이 아닌 실패응답이 온 케이스에 대해 else 문을 작성해보면 좋을 것 같습니다.
            }

            override fun onFailure(call: Call<ResponseWrite>, t: Throwable) {
                Log.e("NetworkTest", "error:$t")
            }
        })
    }

    companion object {
        class BitmapRequestBody(private val bitmap: Bitmap) : RequestBody() {
            override fun contentType(): MediaType? {
                return "image/png".toMediaTypeOrNull()
            }

            override fun writeTo(sink: BufferedSink) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 99, sink.outputStream()) //99프로 압축
            }
        }
    }
}
