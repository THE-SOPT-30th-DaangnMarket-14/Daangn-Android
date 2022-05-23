package org.sopt.daangnmarket_android.ui.view.write

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import org.sopt.daangnmarket_android.R
import org.sopt.daangnmarket_android.databinding.FragmentGalleryBinding
import org.sopt.daangnmarket_android.ui.adapter.GalleryAdapter
import org.sopt.daangnmarket_android.ui.viewmodel.WriteViewModel
import org.sopt.daangnmarket_android.util.GalleryDecoration
import org.sopt.daangnmarket_android.util.GalleryItemAnimator
import org.sopt.daangnmarket_android.util.shortToast

@AndroidEntryPoint
class GalleryFragment : Fragment() {
    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding ?: error("binding not initialized")
    private var _galleryAdapter: GalleryAdapter? = null
    private val galleryAdapter get() = _galleryAdapter ?: error("adapter not initialized")
    private val writeViewModel by activityViewModels<WriteViewModel>()

    private val galleryPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                fetchGallery()
            } else {
                Toast.makeText(
                    requireContext(),
                    "갤러리 접근 권한이 없습니다.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    private val cameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                openCamera()
            } else {
                Toast.makeText(
                    requireContext(),
                    "카메라 권한이 없습니다.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if(result.resultCode == RESULT_OK) {
            result.data?.extras?.get("data")?.let { imageBitmap ->
                Log.i("in intent", imageBitmap.toString())
                Log.i("Is imageBitmap type Bitmap?", (imageBitmap is Bitmap).toString())
                writeViewModel.addCameraImage(imageBitmap as Bitmap)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            DataBindingUtil.inflate(layoutInflater, R.layout.fragment_gallery, container, false)
        binding.viewModel = writeViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        writeViewModel.beginTransaction()
        initRecyclerView()
        checkPermissionForGallery()
        observeLiveData()
        clickEvent()
    }

    override fun onDestroyView() {
        _binding = null
        _galleryAdapter = null
        super.onDestroyView()
    }

    private fun checkPermissionForGallery() {
        when {
            checkSelfPermissionGrantedForGallery() -> {
                fetchGallery()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                showInContextUIForGallery()
            }
            else -> {
                galleryPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    private fun checkSelfPermissionGrantedForGallery(): Boolean {
        return (ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED)
    }

    private fun showInContextUIForGallery() {
        AlertDialog.Builder(requireContext())
            .setTitle("권한 동의 필요")
            .setMessage("사진 등록을 위해 갤러리 접근 권한이 필요합니다.")
            .setPositiveButton("동의") { _, _ ->
                galleryPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            .setNegativeButton("거부") { _, _ ->
                Toast.makeText(requireContext(), "갤러리 접근 권한이 없습니다.", Toast.LENGTH_SHORT).show()
            }
            .create()
            .show()
    }

    private fun checkPermissionForCamera() {
        when {
            checkSelfPermissionGrantedForCamera() -> {
                Log.i("camera permission", "있음")
                openCamera()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE) -> {
                Log.i("camera permission", "거절했음")
                showInContextUIForCamera()
            }
            else -> {
                Log.i("camera permission", "동의필요")
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun checkSelfPermissionGrantedForCamera(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun showInContextUIForCamera() {
        AlertDialog.Builder(requireContext())
            .setTitle("권한 동의 필요")
            .setMessage("사진 촬영을 위해 카메라 접근 권한이 필요합니다.")
            .setPositiveButton("동의") { _, _ ->
                galleryPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
            .setNegativeButton("거부") { _, _ ->
                Toast.makeText(requireContext(), "카메라 접근 권한이 없습니다.", Toast.LENGTH_SHORT).show()
            }
            .create()
            .show()
    }

    private fun initRecyclerView() {
        _galleryAdapter = GalleryAdapter({ layoutPosition ->
            writeViewModel.selectImage(layoutPosition)
        }, {
            checkPermissionForCamera()
        })
        with(binding.rvGallery) {
            addItemDecoration(GalleryDecoration(3))
            itemAnimator = GalleryItemAnimator()
            adapter = galleryAdapter
        }
    }

    private fun fetchGallery() {
        writeViewModel.fetchGallery()
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraLauncher.launch(intent)
    }

    private fun observeLiveData() {
        writeViewModel.imageList.observe(viewLifecycleOwner) {
            galleryAdapter.replaceItem(it)
        }
        writeViewModel.pickOutOfBound.observe(viewLifecycleOwner) {
            requireContext().shortToast("10장까지만 선택해주세요")
        }
        writeViewModel.cameraImageAdded.observe(viewLifecycleOwner) {
            parentFragmentManager.popBackStack()
        }
    }

    private fun clickEvent() {
        binding.tvFinish.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.ivBack.setOnClickListener {
            writeViewModel.rollback()
            requireActivity().onBackPressed()
        }
    }
}