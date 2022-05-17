package org.sopt.daangnmarket_android.ui.view.gallery

import android.Manifest
import android.content.ContentUris
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import dagger.hilt.android.AndroidEntryPoint
import org.sopt.daangnmarket_android.R
import org.sopt.daangnmarket_android.domain.model.GalleryImage
import org.sopt.daangnmarket_android.databinding.ActivityGalleryBinding
import org.sopt.daangnmarket_android.ui.adapter.GalleryAdapter
import org.sopt.daangnmarket_android.ui.viewmodel.WriteViewModel
import org.sopt.daangnmarket_android.util.GalleryDecoration

@AndroidEntryPoint
class GalleryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGalleryBinding
    private lateinit var galleryAdapter: GalleryAdapter
    private val writeViewModel by viewModels<WriteViewModel>()

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                fetchGallery()
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
        binding.viewModel = writeViewModel
        binding.lifecycleOwner = this
        initRecyclerView()
        checkPermission()
        observeLiveData()
    }

    private fun checkPermission() {
        when {
            checkSelfPermissionGranted() -> {
                fetchGallery()
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

    private fun initRecyclerView() {
        galleryAdapter = GalleryAdapter { layoutPosition ->
            writeViewModel.selectImage(layoutPosition)
        }
        with(binding.rvGallery) {
            addItemDecoration(GalleryDecoration(3))
            adapter = galleryAdapter
        }
    }

    private fun fetchGallery() {
        writeViewModel.fetchGallery()
    }

    private fun observeLiveData() {
        writeViewModel.imageList.observe(this) {
            galleryAdapter.replaceItem(it)
        }
        writeViewModel.pickOutOfBound.observe(this) {
            Toast.makeText(this, "10장까지만 선택해주세요", Toast.LENGTH_SHORT).show()
        }
    }
}