package org.sopt.daangnmarket_android.ui.view.write

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import org.sopt.daangnmarket_android.R
import org.sopt.daangnmarket_android.databinding.FragmentWriteBinding
import org.sopt.daangnmarket_android.domain.model.GalleryImage
import org.sopt.daangnmarket_android.ui.adapter.WriteAdapter
import org.sopt.daangnmarket_android.ui.viewmodel.WriteViewModel
import org.sopt.daangnmarket_android.util.WriteDecoration
import org.sopt.daangnmarket_android.util.WriteItemAnimator

class WriteFragment : Fragment() {
    private var _binding: FragmentWriteBinding? = null
    private val binding get() = _binding ?: error("binding not initialized")
    private var _writeAdapter: WriteAdapter? = null
    private val writeAdapter get() = _writeAdapter ?: error("adapter not initialized")
    private val writeViewModel by activityViewModels<WriteViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            DataBindingUtil.inflate(layoutInflater, R.layout.fragment_write, container, false)
        binding.viewmodel = writeViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    // 세미나와 다르게 onCreateView 가 아닌 onViewCreated 에 각종 코드를 쓴 이유가 뭘까요?
    // 생각해보며 한 단계 성장해봅시다.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        clickEvent()
        hideKeyBoard()
        initRecyclerView()
        observeLiveData()
    }

    private fun clickEvent() {
        binding.tvFinish.setOnClickListener {
            if (writeViewModel.writeTitle.value?.length == 0 || writeViewModel.writePrice.value?.length == 0 || writeViewModel.writeContent.value?.length == 0 || writeViewModel.selectedImageList.value?.size == 0) {
                Toast.makeText(requireContext(), "채워지지 않은 부분이 있습니다", Toast.LENGTH_SHORT).show()
            } else {
                writeViewModel.multipart(
                    requireNotNull(writeViewModel.writeTitle.value),
                    requireNotNull(writeViewModel.writePrice.value),
                    requireNotNull(writeViewModel.writeContent.value),
                    requireNotNull(writeViewModel.selectedImageList.value).map { requireNotNull(it.first.image) }
                )
            }
        }
        binding.btnBack.setOnClickListener {
            requireActivity().finish()
        }
    }

    private fun hideKeyBoard() {
        binding.layoutWrite.setOnClickListener {
            val inputMethodManager =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(
                requireActivity().currentFocus?.windowToken,
                0
            )
        }
    }

    private fun initRecyclerView() {
        Log.i("mlog", "initRecyclerView")
        _writeAdapter = WriteAdapter({
            parentFragmentManager.commit {
                replace<GalleryFragment>(R.id.fcv_write, GALLERY_FRAGMENT)
                addToBackStack(GALLERY_FRAGMENT)
            }
        }, {
            writeViewModel.unSelectImage(it)
            // notifyDataSetChanged 를 쓰지 않고도 대표사진을 변경할 수 있는 방법이 있을까 ...
            writeAdapter.notifyDataSetChanged()
        })
        with(binding.rvWriteImage) {
            addItemDecoration(WriteDecoration(10, 20, 16))
            itemAnimator = WriteItemAnimator()
            adapter = writeAdapter
        }
        writeAdapter.replaceItem(listOf<GalleryImage?>(GalleryImage(null, false, 0)))
    }

    private fun observeLiveData() {
        writeViewModel.selectedImageList.observe(viewLifecycleOwner) {
            val selectedImageList: MutableList<GalleryImage?> =
                mutableListOf<GalleryImage?>(GalleryImage(null, false, 0)).apply {
                    addAll(it.map { pair -> pair.first })
                }
            selectedImageList[0]?.selectOrder = selectedImageList.size - 1
            Log.i("selectedImageList", selectedImageList.toString())
            writeAdapter.replaceItem(selectedImageList)
        }

        writeViewModel.isMultiPartSuccess.observe(viewLifecycleOwner) {
            requireActivity().finish()
        }
    }

    companion object {
        const val GALLERY_FRAGMENT = "GalleryFragment"
    }
}