package org.sopt.daangnmarket_android.ui.view.gallery

import android.Manifest
import android.content.ContentUris
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Size
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import org.sopt.daangnmarket_android.R
import org.sopt.daangnmarket_android.databinding.ActivityGalleryBinding
import org.sopt.daangnmarket_android.ui.adapter.GalleryAdapter
import org.sopt.daangnmarket_android.util.GalleryDecoration

class GalleryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGalleryBinding
    private lateinit var galleryAdapter: GalleryAdapter

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                initCustomGallery()
            } else {
                Toast.makeText(
                    this,
                    "갤러리 접근 권한이 없습니다.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_gallery)
        checkPermission()
    }

    private fun checkPermission() {
        when {
            checkSelfPermissionGranted() -> {
                initCustomGallery()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                showInContextUI()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    private fun checkSelfPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun showInContextUI() {
        AlertDialog.Builder(this)
            .setTitle("권한 동의 필요")
            .setMessage("사진 등록을 위해 갤러리 접근 권한이 필요합니다.")
            .setPositiveButton("동의") { _, _ ->
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            .setNegativeButton("거부") { _, _ ->
                Toast.makeText(this, "갤러리 접근 권한이 없습니다.", Toast.LENGTH_SHORT).show()
            }
            .create()
            .show()
    }

    private fun initCustomGallery() {
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

        val query = contentResolver.query(
            collection,
            projection,
            null,
            null,
            sortOrder
        )

        val imageBitmapList = mutableListOf<Bitmap>()
        query?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)

                val contentUri: Uri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    imageBitmapList += contentResolver.loadThumbnail(
                        contentUri,
                        Size(640, 640),
                        null
                    )
                } else {
                    // TODO: SDK 28 이하에서 imageList 만드는 법
                }
            }
        }

        galleryAdapter = GalleryAdapter()
        galleryAdapter.replaceItem(imageBitmapList)
        with(binding.rvGallery) {
            addItemDecoration(GalleryDecoration(3))
        }
        binding.rvGallery.adapter = galleryAdapter
    }
}