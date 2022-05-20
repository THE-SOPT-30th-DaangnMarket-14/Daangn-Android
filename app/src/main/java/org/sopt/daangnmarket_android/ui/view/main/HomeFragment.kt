package org.sopt.daangnmarket_android.ui.view.main

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.sopt.daangnmarket_android.databinding.FragmentHomeBinding
import org.sopt.daangnmarket_android.ui.view.data.ItemData
import org.sopt.daangnmarket_android.ui.view.main.adapter.ItemAdapter
import org.sopt.daangnmarket_android.ui.view.write.WriteActivity

class HomeFragment : Fragment() {
    private lateinit var itemAdapter: ItemAdapter
    private var _binding : FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()
        initTransactionEvent()
    }

    private fun initAdapter() {
        itemAdapter = ItemAdapter()
        binding.rvItem.adapter = itemAdapter

        itemAdapter.itemList.addAll(
            listOf(
                ItemData("빙빙이님! 5월 나눔의 날, 특별한 여행을\n선물 받으신 사연을 전해요","당근마켓","","3.332"),
                ItemData("벤츠 모형 자동차","종로구 효자동·끌올 2분 전","10,000원","15"),
                ItemData("애플 펜슬 팔아요","용산구 한남동·끌올 2분 전","11,000원","122"),
                ItemData("1번 쓴 토스터기 팔아요","종로구 효자동·끌올 40분 전","60,000원","23"),
                ItemData("애플 펜슬 2세대 팔아요","용산구 한남동·끌올1시간 전","20,000원","41"),
                ItemData("다육 팔아요","종로구 효자동·끌올2시간 전","14,000원","33")
            )
        )
        itemAdapter.notifyDataSetChanged()
    }

    private fun initTransactionEvent(){
        binding.ibFab.setOnClickListener {
            val intent = Intent(context,WriteActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}