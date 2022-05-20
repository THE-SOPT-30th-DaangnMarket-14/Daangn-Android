package org.sopt.daangnmarket_android.data.repository

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Size
import dagger.hilt.android.qualifiers.ApplicationContext
import org.sopt.daangnmarket_android.domain.model.GalleryImage
import org.sopt.daangnmarket_android.domain.repository.GalleryRepository
import javax.inject.Inject

class GalleryRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
): GalleryRepository {
    override fun fetchGallery(): List<GalleryImage?> {
        val collection =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Images.Media.getContentUri(
                    MediaStore.VOLUME_EXTERNAL
                )
            } else {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }
        val projection = arrayOf(MediaStore.Images.Media._ID)
        val sortOrder = MediaStore.Images.ImageColumns.DATE_ADDED + " DESC"

        val query = context.contentResolver.query(
            collection,
            projection,
            null,
            null,
            sortOrder
        )

        val imageBitmapList = mutableListOf<GalleryImage?>(null)
        query?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val contentUri: Uri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val image = context.contentResolver.loadThumbnail(
                        contentUri,
                        Size(640, 640),
                        null
                    )
                    imageBitmapList += GalleryImage(image = image, isSelected = false, selectOrder = -1)
                } else {
                    // TODO: SDK 28 이하에서 imageList 만드는 법
                }
            }
        }
        return imageBitmapList
    }
}