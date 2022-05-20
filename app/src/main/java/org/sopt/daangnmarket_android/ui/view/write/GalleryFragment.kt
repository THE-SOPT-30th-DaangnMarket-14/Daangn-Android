package org.sopt.daangnmarket_android.ui.view.write

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
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

    private val requestPermissionLauncher =
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
        initRecyclerView()
        checkPermission()
        observeLiveData()
        clickEvent()
    }

    override fun onDestroyView() {
        _binding = null
        _galleryAdapter = null
        super.onDestroyView()
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
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun showInContextUI() {
        AlertDialog.Builder(requireContext())
            .setTitle("권한 동의 필요")
            .setMessage("사진 등록을 위해 갤러리 접근 권한이 필요합니다.")
            .setPositiveButton("동의") { _, _ ->
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            .setNegativeButton("거부") { _, _ ->
                Toast.makeText(requireContext(), "갤러리 접근 권한이 없습니다.", Toast.LENGTH_SHORT).show()
            }
            .create()
            .show()
    }

    private fun initRecyclerView() {
        _galleryAdapter = GalleryAdapter { layoutPosition ->
            writeViewModel.selectImage(layoutPosition)
        }
        with(binding.rvGallery) {
            addItemDecoration(GalleryDecoration(3))
            itemAnimator = GalleryItemAnimator()
            adapter = galleryAdapter
        }
    }

    private fun fetchGallery() {
        writeViewModel.fetchGallery()
    }

    private fun observeLiveData() {
        writeViewModel.imageList.observe(viewLifecycleOwner) {
            galleryAdapter.replaceItem(it)
        }
        writeViewModel.pickOutOfBound.observe(viewLifecycleOwner) {
            requireContext().shortToast("10장까지만 선택해주세요")
        }
    }

    private fun clickEvent() {
        binding.tvFinish.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }
}