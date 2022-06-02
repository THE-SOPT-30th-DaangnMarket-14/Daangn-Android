package org.sopt.daangnmarket_android.ui.usecase

import android.graphics.Bitmap
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.BufferedSink
import org.sopt.daangnmarket_android.data.service.DaangnService
import javax.inject.Inject

class DaangnMultiPartUseCase @Inject constructor(
    private val service: DaangnService
) {
    suspend operator fun invoke(
        title: String,
        price: String,
        contents: String,
        image: List<Bitmap>
    ) {
        val titleRequestBody =
            title.toRequestBody("text/plain".toMediaTypeOrNull())
        val priceRequestBody =
            price.toRequestBody("text/plain".toMediaTypeOrNull())
        val contentsRequestBody =
            contents.toRequestBody("text/plain".toMediaTypeOrNull())
        val requestBodyHashMap = HashMap<String, RequestBody>().apply {
            this["title"] = titleRequestBody
            this["price"] = priceRequestBody
            this["contents"] = contentsRequestBody
        }

        val imageListMultiPartBody = mutableListOf<MultipartBody.Part>()
        image.forEachIndexed { index, bitmap ->
            val imageRequestBody = BitmapRequestBody(bitmap)
            val imageMultipartBody: MultipartBody.Part =
                MultipartBody.Part.createFormData(
                    "image",
                    "and" + System.currentTimeMillis().toString() + index.toString(),
                    imageRequestBody
                )
            imageListMultiPartBody.add(imageMultipartBody)
        }

        service.postItem(imageListMultiPartBody, requestBodyHashMap)
    }

    companion object {
        class BitmapRequestBody(private val bitmap: Bitmap) : RequestBody() {
            override fun contentType(): MediaType? {
                return "image/png".toMediaTypeOrNull()
            }

            override fun writeTo(sink: BufferedSink) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 99, sink.outputStream())
            }
        }
    }
}