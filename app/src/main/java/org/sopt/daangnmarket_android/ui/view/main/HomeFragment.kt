package org.sopt.daangnmarket_android.ui.view.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import org.sopt.daangnmarket_android.R
import org.sopt.daangnmarket_android.data.ServiceCreator
import org.sopt.daangnmarket_android.data.response.ResponseItem
import org.sopt.daangnmarket_android.databinding.FragmentHomeBinding
import org.sopt.daangnmarket_android.domain.model.ItemData
import org.sopt.daangnmarket_android.ui.adapter.ItemAdapter
import org.sopt.daangnmarket_android.ui.view.write.WriteActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {
    private lateinit var itemAdapter: ItemAdapter
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding ?: error("binding not initialized")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSwipeRefreshLayout()
        initAdapter()
        initTransactionEvent()
        itemNetwork()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initSwipeRefreshLayout() {
        with(binding.srlList) {
            setColorSchemeResources(R.color.orange_ee8548)
            setOnRefreshListener {
                itemNetwork()
            }
        }
    }

    private fun initAdapter() {
        itemAdapter = ItemAdapter()
        binding.rvItem.adapter = itemAdapter
    }

    private fun initTransactionEvent() {
        binding.ibFab.setOnClickListener {
            val intent = Intent(context, WriteActivity::class.java)
            startActivity(intent)
        }
    }

    private fun itemNetwork() {
        val call: Call<ResponseItem> = ServiceCreator.itemService.getItems()

        call.enqueue(object : Callback<ResponseItem> {
            override fun onResponse(
                call: Call<ResponseItem>,
                response: Response<ResponseItem>
            ) {
                binding.srlList.isRefreshing = false
                if (response.isSuccessful) {
                    val data = response.body()?.data
                    if (data != null) {
                        itemAdapter.itemList = data.map {
                            ItemData(
                                it.title,
                                "청파동",
                                it.price,
                                it.image,
                                it.likeCount,
                                it.chatCount,
                                it.timeBefore
                            )
                        }.toMutableList()
                        itemAdapter.notifyDataSetChanged()
                    }
                } else {
                    Toast.makeText(context, "판매글을 불러오지 못했습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseItem>, t: Throwable) {
                binding.srlList.isRefreshing = false
                Log.e("NetworkTest", "error:$t")
                Toast.makeText(context, "연결 실패", Toast.LENGTH_SHORT).show()
            }
        })
    }
}