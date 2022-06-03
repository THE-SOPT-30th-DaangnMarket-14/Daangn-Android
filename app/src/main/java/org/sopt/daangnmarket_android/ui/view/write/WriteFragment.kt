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
import org.sopt.daangnmarket_android.response.ResponseWrite
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
            if (writeViewModel.writeTitle.value?.length != 0 || writeViewModel.writePrice.value?.length != 0 || writeViewModel.writeContent.value?.length != 0 || writeViewModel.selectedImageList.value?.size != 0) {
                Toast.makeText(requireContext(), "채워지지 않은 부분이 있습니다", Toast.LENGTH_SHORT).show()
            } else {
                // 사실 이게 맞는지도 모르겠..긴한데요..ㅠㅠ 이미지 init 값이 뭔지 모르겠어서.. 주석처리해두었습니다ㅜㅜ
                // writeViewModel.multipart(title = String(), price = String(), content = String(), image = List<Bitmap>(0,0))
                // 승현 - 이 위치에서 ViewModel 의 multipart() 메서드를 호출하면 됩니다.
                // 인자로는 서버에 보낼 값, 그러니까 사용자가 직접 입력해 채워넣은 값을 넣으면 되는 것입니다.
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
    }

    companion object {
        const val GALLERY_FRAGMENT = "GalleryFragment"
    }
}